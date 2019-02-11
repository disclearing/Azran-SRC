package me.invakid.azran.scanner;

import javafx.application.Platform;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AzranScanner extends ProcessScanner {

    private int checkNum;

    private Map<Calendar, Boolean> result = new ConcurrentHashMap<>();
    private boolean dumpingFinished = false;

    AzranScanner(int pid, int checkNum) {
        super(pid);
        this.checkNum = checkNum;
    }

    @Override
    public void scan() {
        Platform.runLater(() -> ScannerApplication.getInstance().setCurrentStatus("Initializing Check " + checkNum));

        super.scan();
    }

    @Override
    public void cheatsFound(String detection) {
        ScannerApplication scannerApplication = ScannerApplication.getInstance();

        scannerApplication.setDetection(String.format("Cheats found! (%s)", detection));
        scannerApplication.cheatsFound(true);

        result.clear();
        result.put(Calendar.getInstance(), true);
    }

    @Override
    public void nothingFound() {
        result.clear();
        result.put(Calendar.getInstance(), false);
    }

    @Override
    public void dumpingFinished() {
        Platform.runLater(() -> ScannerApplication.getInstance().setCurrentStatus("Performing Check " + checkNum));

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                Set<String> lines = new HashSet<>();

                try {
                    LineIterator it = FileUtils.lineIterator(getOutput());
                    while (it.hasNext()) lines.add(it.nextLine());
                    it.close();
                } catch (Exception ignored) {
                }

                continueScan(lines);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

}
