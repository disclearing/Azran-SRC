package me.invakid.azran.server.type;

import java.io.*;

public class StringsCollection {
    public final CheatStrings antivirusProcesses, recordingSoftware;

    public StringsCollection(CheatStrings antivirusProcesses, CheatStrings recordingSoftware) {
        this.antivirusProcesses = antivirusProcesses;
        this.recordingSoftware = recordingSoftware;
    }

    public static StringsCollection deserialize(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        CheatStrings antivirusProcesses = CheatStrings.deserialize(dis);
        CheatStrings recordingSoftware = CheatStrings.deserialize(dis);

        return new StringsCollection(antivirusProcesses, recordingSoftware);
    }

    public void serialize(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        antivirusProcesses.serialize(dos);
        recordingSoftware.serialize(dos);
    }
}