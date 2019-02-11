package me.invakid.azran.pinapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import me.invakid.azran.Main;
import me.invakid.azran.scanner.ScannerApplication;
import me.invakid.azran.server.AzranSocket;
import me.invakid.azran.server.type.StringsCollection;
import me.invakid.azran.util.Utils;

import java.io.EOFException;

public class PINApplication {

    private final Stage stage;

    public PINApplication(Stage stage) {
        this.stage = stage;
    }

    public void open() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/pin.fxml"));

        stage.setTitle("Azran");
        stage.getIcons().add(Main.getIcon());

        Scene scene = new Scene(root, Main.WIDTH, Main.HEIGHT);
        scene.getStylesheets().add(Main.getStyle());

        stage.setScene(scene);

        AnchorPane pane = (AnchorPane) root.lookup("#anchorPane");
        pane.setBackground(Main.getBackground());

        stage.show();
        stage.setResizable(false);

        ImageView logoView = (ImageView) root.lookup("#logoView");
        logoView.setSmooth(true);
        logoView.setImage(new Image(getClass().getResourceAsStream("/logo.png")));

        TextField pinField = (TextField) root.lookup("#pinField");
        pinField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) pinField.setText(newValue.replaceAll("[^\\d]", ""));
            if (newValue.length() > 4) pinField.setText(oldValue);
        });

        Button loginButton = (Button) root.lookup("#btnLogin");
        loginButton.setOnMouseClicked(event -> {
            String pinInput = pinField.getText();

            if (pinInput.length() != 4) {
                Utils.showErrorAlert("PIN must be four characters long!");
                return;
            }

            AzranSocket.INSTANCE.connect();
            try {
                if (!AzranSocket.INSTANCE.sendPIN(pinInput))
                    throw new EOFException();
            } catch (EOFException ex) {
                Utils.showErrorAlert("Incorrect PIN!");
                pinField.setText("");
            } catch (Exception ex) {
                Utils.showErrorAlert("Can't connect to the server!");
                pinField.setText("");
            }

            if (AzranSocket.INSTANCE.antivirusProcesses != null) {
                Utils.showSuccessAlert("Successfully authenticated!");
                ((Node) event.getSource()).getScene().getWindow().hide();
                try {
                    new ScannerApplication(stage, new StringsCollection(AzranSocket.INSTANCE.antivirusProcesses, AzranSocket.INSTANCE.recordingSoftware)).open();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
