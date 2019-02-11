package me.invakid.azran.server.type;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CheatStrings implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clientName;
    private List<CheatString> cheatStrings;

    private CheatStrings(String clientName, List<CheatString> cheatStrings) {
        this.clientName = clientName;
        this.cheatStrings = cheatStrings;
    }

    static CheatStrings deserialize(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        String clientName = dis.readUTF();

        List<CheatString> strings = new ArrayList<>();
        int cheatStringAmount = dis.readInt();
        for (int i = 0; i < cheatStringAmount; i++) {
            String str = dis.readUTF();
            String name = dis.readUTF();
            boolean bool = dis.readBoolean();
            strings.add(new CheatString(str, name, bool));
        }

        return new CheatStrings(clientName, strings);
    }

    void serialize(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        dos.writeUTF(clientName);

        dos.writeInt(cheatStrings.size());
        for (CheatString string : cheatStrings) {
            dos.writeUTF(string.string);
            dos.writeUTF(string.cheatName);
            dos.writeBoolean(string.caseSensitive);
        }
    }
}