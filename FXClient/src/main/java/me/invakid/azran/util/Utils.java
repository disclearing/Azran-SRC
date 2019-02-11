package me.invakid.azran.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import me.invakid.azran.Main;
import me.invakid.azran.scanner.ScannerApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static void showErrorAlert(String message) {
        showAlert(Alert.AlertType.ERROR, message);
    }

    public static void showSuccessAlert(String message) {
        showSuccessAlert(message, false);
    }

    public static void showSuccessAlert(String message, boolean copyable) {
        showAlert(Alert.AlertType.INFORMATION, message, copyable);
    }

    public static void showWarningAlert(String message) {
        showAlert(Alert.AlertType.WARNING, message);
    }

    private static void showAlert(Alert.AlertType alertType, String content) {
        showAlert(alertType, content, false);
    }

    private static void showAlert(Alert.AlertType alertType, String content, boolean copyable) {
        Alert alert = new Alert(alertType);

        alert.setTitle("Azran SS Tool");

        if (alertType == Alert.AlertType.INFORMATION) alert.setHeaderText("Information");
        else if (alertType == Alert.AlertType.ERROR) alert.setHeaderText("Error");
        else alert.setHeaderText("Warning");

        if (copyable) {
            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            GridPane gridPane = new GridPane();
            gridPane.setMaxWidth(Double.MAX_VALUE);
            gridPane.add(textArea, 0, 0);

            alert.getDialogPane().setContent(gridPane);
        } else
            alert.setContentText(content);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(Main.getIcon());

        alert.showAndWait();
    }

    private static ZonedDateTime toZonedDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static int getDifference(ZonedDateTime before, ZonedDateTime after) {
        return Math.toIntExact(TimeUnit.SECONDS.convert(Duration.between(before, after).toMinutes(), TimeUnit.MINUTES));
    }

    public static int getDifference(Date before, Date after) {
        return getDifference(toZonedDateTime(before), toZonedDateTime(after));
    }

    public static String formatTime(int secs) {
        if (secs < 60)
            return secs + " second(s)";

        double mins = (double) secs / 60d;
        if (mins < 60)
            return (int) mins + " minute(s)";

        double hrs = mins / 60d;
        if (hrs < 24)
            return (int) hrs + " hour(s)";

        double days = hrs / 24d;
        return (int) days + " day(s)";
    }

    private static long getLastModified(File directory) {
        File[] files = directory.listFiles();

        if (files == null)
            return -1;

        if (files.length == 0) {
            return directory.lastModified();
        }

        Arrays.sort(files, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
        return files[0].lastModified();
    }

    public static String getIGN() {
        String result = "Unspecified";

        try {
            int mcPID = ScannerApplication.getMinecraftPID();
            if (mcPID != -1)
                result = Objects.requireNonNull(getCommandLineByPID(mcPID)).split("--username")[1].split("--")[0].trim();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    private static String getCommandLineByPID(int pid) {
        List<String> wmicResult = callWMIC(String.format("wmic process where(processid like %s) get commandline", pid));
        return wmicResult == null || wmicResult.isEmpty() ? null : wmicResult.get(1);
    }

    public static int findPIDByCMD(String name, String cmd, boolean endsWith) {
        try {
            String cmdLower = cmd.toLowerCase().trim();

            List<String> wmicResult = callWMIC(String.format("wmic process where(name like '%s') get commandline, processid", name));
            if (wmicResult == null) return -1;

            int pidStart = wmicResult.get(0).indexOf("P");

            for (String response : wmicResult) {
                String responseLine = response.substring(0, pidStart).trim().toLowerCase().replaceAll("\"", "");

                boolean found = false;

                if (endsWith) {
                    if (responseLine.endsWith(cmdLower)) found = true;
                } else if (responseLine.contains(cmdLower)) found = true;

                if (found) return Integer.parseInt(response.substring(pidStart).trim());
            }
        } catch (Exception ignored) {
        }

        return -1;
    }

    public static int findPIDByCMD(String name, String[] cmd, boolean endsWith) {
        return Arrays.stream(cmd).mapToInt(string -> findPIDByCMD(name, string, endsWith)).filter(res -> res != -1).findFirst().orElse(-1);
    }

    public static int findPIDByName(String name) {
        List<String> wmicResult = callWMIC(String.format("wmic process where(name like '%s') get processid", name));
        return wmicResult != null && !wmicResult.isEmpty() ? Integer.parseInt(wmicResult.get(1).trim()) : -1;
    }

    public static boolean is64Bit() {
        try {
            List<String> wmicResult = callWMIC("wmic os get osarchitecture");
            return Integer.valueOf("0" + Objects.requireNonNull(wmicResult).get(1).replaceAll("(\\d*).*", "$1")) == 64;
        } catch (Exception ignored) {
        }

        return false;
    }

    private static List<String> callWMIC(String command) {
        try {
            Process query = Runtime.getRuntime().exec(command);

            BufferedReader in = new BufferedReader(new InputStreamReader(query.getInputStream()));
            List<String> wmicResult = new ArrayList<>();

            String line;
            while ((line = in.readLine()) != null) if (!line.equals(""))
                wmicResult.add(line);

            return wmicResult;
        } catch (Exception ignored) {
        }

        return null;
    }

    public static Calendar getStartTime(int pid) {
        Calendar date = null;

        try {
            Process query = Runtime.getRuntime().exec(String.format("wmic process where(processid like %s) get creationdate", pid));

            BufferedReader in = new BufferedReader(new InputStreamReader(query.getInputStream()));
            List<String> wmicResult = new ArrayList<>();

            String line;
            while ((line = in.readLine()) != null) if (!line.equals(""))
                wmicResult.add(line);

            String s = wmicResult.get(1);

            int year = Integer.parseInt(s.substring(0, 4));
            int month = Integer.parseInt(s.substring(4, 6));
            int day = Integer.parseInt(s.substring(6, 8));
            int hour = Integer.parseInt(s.substring(8, 10));
            int minute = Integer.parseInt(s.substring(10, 12));
            int second = Integer.parseInt(s.substring(12, 14));

            date = Calendar.getInstance();
            date.set(year, month - 1, day, hour, minute, second);
        } catch (Exception ignored) {
        }

        return date;
    }

    public static Calendar getRecycleBinModified() {
        File dir = new File("C:\\$Recycle.Bin");

        Calendar time = Calendar.getInstance();
        time.setTime(new Date(getLastModified(dir)));

        return time;
    }

}
