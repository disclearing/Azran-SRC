package me.invakid.azran.scanner;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import me.invakid.azran.Main;
import me.invakid.azran.server.type.StringsCollection;
import me.invakid.azran.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class ScannerApplication {

    @Getter
    private static ScannerApplication instance;

    private final Stage stage;
    private final StringsCollection stringsCollection;
    private static final String[] mcCMD = {
            "net.minecraft.launchwrapper.Launch",
            "net.minecraft.client.main.Main",
            "--version "
    };

    private Label status;
    private String currentStatus = "";
    private ProgressIndicator indicator;
    private Timeline timeline;
    private ImageView detection;
    boolean stopped = false;

    public ScannerApplication(Stage stage, StringsCollection stringsCollection) {
        instance = this;

        this.stage = stage;
        this.stringsCollection = stringsCollection;

        miscChecks();
    }

    public static int getMinecraftPID() {
        return Utils.findPIDByCMD("javaw.exe", mcCMD, false);
    }

    private static int getExplorerPID() {
        int res = Utils.findPIDByCMD("explorer.exe", "explorer.exe", true);
        return res == -1 ? Utils.findPIDByCMD("explorer.exe", "/LOADSAVEDWINDOWS", true) : res;
    }

    private static int getLssasPID() {
        return Utils.findPIDByName("lsass.exe");
    }

    public void open() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scan.fxml"));

        stage.setTitle("Azran");

        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);

        AnchorPane pane = (AnchorPane) root.lookup("#anchorPane");
        pane.setBackground(Main.getBackground());

        stage.show();

        stage.setResizable(false);

        indicator = (ProgressIndicator) root.lookup("#progressTest");
        indicator.setOpacity(0.0);

        ImageView logoView = (ImageView) root.lookup("#logoView");
        logoView.setSmooth(true);
        logoView.setImage(new Image(getClass().getResourceAsStream("/logo.png")));

        detection = (ImageView) root.lookup("#detectionImg");
        detection.setSmooth(true);

        status = (Label) root.lookup("#lblStatus");
        status.setMaxWidth(Double.MAX_VALUE);
        AnchorPane.setLeftAnchor(status, 0.0);
        AnchorPane.setRightAnchor(status, 0.0);
        status.setAlignment(Pos.CENTER);
        status.setText("");
        status.setOpacity(0.0);

        Button scan = (Button) root.lookup("#btnScan");
        scan.setOnMouseClicked(event -> {
            FadeTransition buttonOut = new FadeTransition(Duration.millis(500), scan);

            buttonOut.setFromValue(1.0);
            buttonOut.setToValue(0.0);
            buttonOut.play();

            scan.setDisable(true);

            FadeTransition indicatorIn = new FadeTransition(Duration.millis(500), indicator);

            indicatorIn.setFromValue(0.0);
            indicatorIn.setToValue(1.0);
            indicatorIn.play();

            FadeTransition statusIn = new FadeTransition(Duration.millis(500), status);

            statusIn.setFromValue(0.0);
            statusIn.setToValue(1.0);

            statusIn.setOnFinished((e) -> {
                ScanManager manager = new ScanManager();

                manager.addScanner(new AzranScanner(getMinecraftPID(), 1));
                manager.addScanner(new ExplorerScanner(getExplorerPID(), 2));
                manager.addScanner(new AzranScanner(getLssasPID(), 3));
                manager.addScanner(new AzranScanner(getAntivirusPID(), 4));

                manager.executeScan();
            });

            statusIn.play();

            indicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, event1 -> {
                        String statusText = status.getText();
                        status.setText(
                                ((currentStatus + " . . .").equals(statusText))
                                        ? currentStatus + " ."
                                        : statusText + " ."
                        );
                    }),
                    new KeyFrame(Duration.millis(1000))
            );
            timeline.setCycleCount(Timeline.INDEFINITE);

            timeline.play();

            setCurrentStatus("Initializing");
        });
    }

    void setCurrentStatus(String str) {
        this.currentStatus = str;
        status.setText(str);
    }

    void setDetection(String str) {
        timeline.stop();
        status.setText(str);
    }

    private void miscChecks() {
        List<String> detections = new ArrayList<>();

        if (getMinecraftPID() == -1) detections.add("Minecraft is not running!");

        this.stringsCollection.recordingSoftware.getCheatStrings().stream().filter(cheatString -> Utils.findPIDByName(cheatString.string) != -1).findFirst().ifPresent(cheatString -> detections.add(String.format("Recording software found! (%s)", cheatString.cheatName)));

        Calendar explorerRun = Utils.getStartTime(getExplorerPID());
        Calendar javawRun = Utils.getStartTime(getMinecraftPID());

        if (explorerRun != null && javawRun != null && explorerRun.after(javawRun))
            detections.add(String.format("User has restarted Explorer %s ago!", Utils.formatTime(Utils.getDifference(explorerRun.getTime(), Calendar.getInstance().getTime()))));

        Calendar recycleBinUpdated = Utils.getRecycleBinModified();

        if (javawRun != null && recycleBinUpdated.after(javawRun))
            detections.add(String.format("User has modified Recycle Bin %s ago!", Utils.formatTime(Utils.getDifference(recycleBinUpdated.getTime(), Calendar.getInstance().getTime()))));

        if (FileUtils.listFiles(FileUtils.getTempDirectory(), new String[]{"dll"}, false).stream().anyMatch(file -> StringUtils.containsIgnoreCase(file.getName(), "jnativehook-")))
            detections.add("JNativeHook files found in %temp%, possible .jar Autoclicker!");

        // Showing the alert
        if (detections.isEmpty()) return;

        Utils.showWarningAlert(detections.stream().map(str -> str + "\n").collect(Collectors.joining()).trim());
    }

    void cheatsFound(boolean b) {
        stopped = true;

        detection.setOpacity(0.0);
        detection.setImage(new Image(getClass().getResourceAsStream(b ? "/detected.png" : "/clean.png")));

        FadeTransition progressOut = new FadeTransition(Duration.millis(500), indicator);

        progressOut.setFromValue(1.0);
        progressOut.setToValue(0.0);

        progressOut.play();

        FadeTransition detectionIn = new FadeTransition(Duration.millis(500), detection);

        detectionIn.setFromValue(0.0);
        detectionIn.setToValue(1.0);

        detectionIn.play();
    }

    private int getAntivirusPID() {
        return stringsCollection.antivirusProcesses.getCheatStrings().stream().mapToInt(avName -> Utils.findPIDByName(avName.string)).filter(current -> current != -1).findFirst().orElse(-1);
    }


}
