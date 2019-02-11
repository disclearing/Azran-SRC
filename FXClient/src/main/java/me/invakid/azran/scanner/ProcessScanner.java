package me.invakid.azran.scanner;

import com.google.common.io.Files;
import javafx.application.Platform;
import lombok.Getter;
import me.invakid.azran.server.AzranSocket;
import me.invakid.azran.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class ProcessScanner {

    private static final String STRINGS2_NAME = Utils.is64Bit() ? "A.exe" : "B.exe";
    private final int pid;
    private File output;
    private Set<String> scannedStrings = new HashSet<>();

    ProcessScanner(int pid) {
        this.pid = pid;
    }

    public void scan() {
        new Thread(() -> {
            try {
                output = dumpStrings(pid);

                Platform.runLater(() -> {
                    try {
                        dumpingFinished();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    public abstract void cheatsFound(String detection);

    public abstract void nothingFound();

    public abstract void dumpingFinished();

    public void continueScan(Set<String> stringSet) {
        new Thread(() -> {
            try {
                String result = AzranSocket.INSTANCE.sendStrings(stringSet);
                Platform.runLater(() -> {
                    try {
                        if (result.isEmpty()) {
                            nothingFound();
                            return;
                        }

                        cheatsFound(result);
                    } finally {
                        try {
                            FileUtils.deleteDirectory(getOutput().getParentFile());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private File dumpStrings(int pid) {
        File output = null;

        try {
            File tempDir = Files.createTempDir();

            File strings2;
            FileUtils.copyInputStreamToFile(ScannerApplication.class.getResourceAsStream("/" + STRINGS2_NAME), strings2 = new File(tempDir, RandomStringUtils.randomAlphanumeric(6) + ".exe"));

            output = new File(tempDir, RandomStringUtils.randomAlphanumeric(6) + ".txt");

            String cmd = String.format("cmd.exe /C %s -pid %d > %s", strings2.getName(), pid, output.getName());
            ProcessBuilder sProcessBuilder = new ProcessBuilder(cmd.split(" ")).directory(tempDir);

            Process sProcess = sProcessBuilder.start();
            sProcess.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return output;
    }

}
