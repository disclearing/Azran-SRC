package me.invakid.azran.server;

import me.invakid.azran.strings.StringsCollection;
import me.invakid.azran.util.Utils;

import java.io.*;
import java.net.Socket;

public class AzranClient {
	private static final int CODE_LENGTH = 4;

	private AzranClient() {
	}

	public static ServerResult downloadMainProgramFile(Address server, byte[] code) throws IOException {
		if (code.length != CODE_LENGTH)
			throw new IllegalArgumentException("code.length must be " + CODE_LENGTH + "!");

		Socket socket = new Socket(server.ip, server.port);

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		// Writing code to the server
		out.write(code, 0, CODE_LENGTH);

		// Writing bit to the server
		// out.write(Main.BIT);

		// Receiving (cut down) strings from server
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf;
		buf = new byte[1024];
		int read;
		while ((read = in.read(buf)) > 0) {
			baos.write(buf, 0, read);
		}

		// baos contains encrypted data
		byte[] decrypted = Utils.decrypt(baos.toByteArray(), "{FILE}" + new String(code));

		ByteArrayInputStream bais = new ByteArrayInputStream(decrypted);

		StringsCollection strings;
		try {
			strings = StringsCollection.deserialize(bais);
		} catch (EOFException e) {
			// No more data exists, server didn't send it. Invalid code.
			socket.close();
			return null;
		}

		socket.close();

		return new ServerResult(strings);
	}

	public static class ServerResult {
		public final StringsCollection stringsCollection;

		public ServerResult(StringsCollection stringsCollection) {
			this.stringsCollection = stringsCollection;
		}
	}
}