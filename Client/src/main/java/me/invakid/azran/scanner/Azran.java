package me.invakid.azran.scanner;

import me.invakid.azran.ClientMain;
import me.invakid.azran.db.DatabaseManager;
import me.invakid.azran.server.AzranClient;
import me.invakid.azran.server.IPPicker;
import me.invakid.azran.strings.CheatString;
import me.invakid.azran.strings.StringsCollection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Azran {

    private IPPicker ipPicker;

    public Azran(File file, String pin) {
        DatabaseManager.INSTANCE.init();
        String ip = getIP();

        try {
            ipPicker = new IPPicker(ClientMain.IPS_WEBSITE);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        byte[] code = toCode(pin);

        AzranClient.ServerResult res;
        try {
            res = AzranClient.downloadMainProgramFile(ipPicker.getRandomAddress(), code);
        } catch (IOException e1) {
            e1.printStackTrace();
            printResult("E");
            return;
        }

        if (res == null) {
            printResult("N");
            return;
        }

        try {
            LineIterator it = FileUtils.lineIterator(file);
            while(it.hasNext()) {
                String line = it.nextLine();
                CheatString detection = getDetection(line, res.stringsCollection);

                if(detection != null) {
                    DatabaseManager.INSTANCE.addDetection(ip, line);
                    printResult("D"/*"D:::" + detection.cheatName*/);
                    return;
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        printResult("C");
    }

    public static String getIP()
    {
        URL myIP;
        try {
            myIP = new URL("http://api.externalip.net/ip/");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(myIP.openStream())
            );
            return in.readLine();
        } catch (Exception e)
        {
            try
            {
                myIP = new URL("http://myip.dnsomatic.com/");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(myIP.openStream())
                );
                return in.readLine();
            } catch (Exception e1)
            {
                try {
                    myIP = new URL("http://icanhazip.com/");

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(myIP.openStream())
                    );
                    return in.readLine();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }

        return null;
    }

    private CheatString getDetection(String s, StringsCollection collection) {
        return collection.javawStrings.getCheatStrings().stream()
                .filter(string -> string.caseSensitive ? s.contains(string.string) : s.toLowerCase().contains(string.string.toLowerCase()))
                .findFirst().orElse(null);
    }

    private byte[] toCode(String codeStr) {
        if (codeStr.length() != 4)
            throw new IllegalArgumentException("Code length must be " + 4);

        byte[] code = new byte[4];

        for (int i = 0; i < 4; i++) {
            char c = codeStr.charAt(i);
            int num = (int) c - 48;
            code[i] = (byte) num;
        }

        return code;
    }

    public void printResult(String result) {
        try {
            File thisFile =  new File(Azran.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

            File file = new File(thisFile.getParent(), "azran-result-" + System.currentTimeMillis() / 1000L);
            if (!file.exists()) file.createNewFile();

            FileUtils.writeLines(file, Collections.singleton(result));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
