package me.invakid.azran.server;

import me.invakid.azran.Main;
import me.invakid.azran.server.type.CheatStrings;
import me.invakid.azran.util.Utils;
import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum AzranSocket {
    INSTANCE;

    public Socket socket;
    public CheatStrings recordingSoftware, antivirusProcesses;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    AzranSocket() {
    }

    private static <T> T decompress(String data) {
        try {
            ByteArrayInputStream bas = new ByteArrayInputStream(Base64.decodeBase64(data));
            GZIPInputStream gzip = new GZIPInputStream(bas);
            ObjectInputStream objectIn = new ObjectInputStream(gzip);

            @SuppressWarnings("unchecked") T object = (T) objectIn.readObject();

            bas.close();
            objectIn.close();
            gzip.close();
            return object;
        } catch (Exception e) {
            return null;
        }
    }

    private static String compress(Object data) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            ObjectOutputStream objectOut = new ObjectOutputStream(gzip);

            objectOut.writeObject(data);
            objectOut.close();

            byte[] compressed = bos.toByteArray();
            bos.close();

            gzip.close();

            return Base64.encodeBase64String(compressed);
        } catch (Exception e) {
            return null;
        }
    }

    public void connect() {
        try {
            Address server = new IPPicker(Main.IPS_WEBSITE).getRandomAddress();
            socket = new Socket(server.ip, server.port);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean sendPIN(String pin) {
        try {
            writeObject(String.format("%s!!!%s", pin, Utils.getIGN()));

            List<String> response = readObject();
            if (response.size() != 2)
                return false;

            recordingSoftware = deserializeObject(decompress(response.get(0)));
            antivirusProcesses = deserializeObject(decompress(response.get(1)));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public String sendStrings(Set<String> stringSet) {
        try {
            writeObject(stringSet);

            return readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public void disconnect() {
        try {
            writeObject("done");
        } catch (Exception ignored) {
        }

        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeObject(Object o) {
        writeString(compress(o));
    }

    private void writeString(String s) {
        try {
            outputStream.writeObject(s);
            outputStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private <T> T readObject() throws Exception {
        return decompress(readString());
    }

    private String readString() throws Exception {
        return String.valueOf(inputStream.readObject());
    }

    @SuppressWarnings("all")
    private <T> T deserializeObject(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decodeBase64(data));
            ObjectInputStream dataInput = new ObjectInputStream(inputStream);
            try {
                return (T) dataInput.readObject();
            } finally {
                dataInput.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
