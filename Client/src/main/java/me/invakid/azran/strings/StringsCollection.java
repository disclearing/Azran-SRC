package me.invakid.azran.strings;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Random;

public class StringsCollection {
	public final CheatStrings javawStrings;

	public StringsCollection(CheatStrings javawStrings) {
		this.javawStrings = javawStrings;
	}

	public static StringsCollection fromFile(File file) {
		try {
			CheatStrings strings = CheatStrings.fromStream(FileUtils.openInputStream(file));
			return new StringsCollection(strings);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void serialize(OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		javawStrings.serialize(dos);
	}

	public StringsCollection getSome(int maxStrings) {
		Random rnd = new Random();

		CheatStrings javawStrings = this.javawStrings.getSome(maxStrings, rnd);

		return new StringsCollection(javawStrings);
	}

	public static StringsCollection deserialize(InputStream in) throws IOException {
		DataInputStream dis = new DataInputStream(in);

		CheatStrings javawStrings = CheatStrings.deserialize(dis);

		return new StringsCollection(javawStrings);
	}
}