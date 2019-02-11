package me.invakid.methodfinder.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

    private static InputStream strings2Resource = Utils.class.getResourceAsStream("/" + (WMICUtils.is64Bit() ? "64.exe" : "86.exe"));

    public static String getTimeNow() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    public static boolean extractStrings2(File output) {
        try {
            FileUtils.copyInputStreamToFile(strings2Resource, output);
            return true;
        } catch (Exception ignored) {
        }

        return false;
    }

}
