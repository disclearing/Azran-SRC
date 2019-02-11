package me.invakid.azran.scanner;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import me.invakid.azran.Main;
import me.invakid.azran.util.Utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ExplorerScanner extends AzranScanner {

    ExplorerScanner(int pid, int checkNum) {
        super(pid, checkNum);
    }

    @Override
    public void continueScan(Set<String> lines) {
        super.continueScan(lines);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }

            Platform.runLater(() -> {
                if (ScannerApplication.getInstance().stopped)
                    return;

                Set<String> pcaclient = new HashSet<>();
                try {
                    pcaclient = lines.stream().filter(line -> line.contains("PcaClient")).map(line -> line.split(",")[5]).collect(Collectors.toSet());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (!pcaclient.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Would you like to view the executables ran on this session?", ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Azran SS Tool");
                    alert.setHeaderText("Executables");

                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alertStage.getIcons().add(Main.getIcon());

                    alert.showAndWait();

                    if (alert.getResult() != ButtonType.YES)
                        return;

                    Utils.showSuccessAlert("Executables: " + System.getProperty("line.separator") + pcaclient.stream().map(s -> s + System.getProperty("line.separator")).collect(Collectors.joining()).trim(), true);
                }
            });
        }).start();
    }
}
