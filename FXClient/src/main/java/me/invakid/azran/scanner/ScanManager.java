package me.invakid.azran.scanner;

import javafx.application.Platform;
import me.invakid.azran.server.AzranSocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

class ScanManager {

    private List<AzranScanner> scannerList;

    ScanManager() {
        scannerList = new ArrayList<>();
    }

    void addScanner(AzranScanner scanner) {
        scannerList.add(scanner);
    }

    void executeScan() {
        new Thread(() -> {
            try {
                Calendar startTime = Calendar.getInstance();

                for (AzranScanner scanner : scannerList) {
                    scanner.scan();

                    boolean scanResult = false;
                    while (true) {
                        try {
                            Thread.sleep(1000);

                            Map<Calendar, Boolean> result = scanner.getResult();
                            if (!result.isEmpty()) {
                                Calendar finishTime = result.keySet().iterator().next();
                                if (finishTime.compareTo(startTime) > 0)
                                    scanResult = result.get(finishTime);

                                break;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (scanResult) return;
                }

                Platform.runLater(() -> {
                    ScannerApplication scannerApplication = ScannerApplication.getInstance();

                    scannerApplication.cheatsFound(false);
                    scannerApplication.setDetection("Nothing found!");
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                AzranSocket.INSTANCE.disconnect();
            }
        }).start();
    }

}
