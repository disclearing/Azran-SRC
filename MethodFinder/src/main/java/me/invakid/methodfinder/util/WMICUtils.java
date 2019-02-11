package me.invakid.methodfinder.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WMICUtils {

    public static boolean is64Bit() {
        try {
            List<String> wmicResult = executeCommand("wmic os get osarchitecture");

            return Integer.valueOf("0" + Objects.requireNonNull(wmicResult).get(1).replaceAll("(\\d*).*", "$1")) == 64;
        } catch(Exception ignored) {
        }

        return false;
    }

    public static List<String> executeCommand(String command) {
        try {
            Process query = Runtime.getRuntime().exec(command);

            BufferedReader in = new BufferedReader(new InputStreamReader(query.getInputStream()));
            List<String> wmicResult = new ArrayList<>();

            String line;
            while ((line = in.readLine()) != null) if (!line.equals(""))
                wmicResult.add(line.trim());

            return wmicResult;
        } catch (Exception ignored) {
        }

        return null;
    }

}
