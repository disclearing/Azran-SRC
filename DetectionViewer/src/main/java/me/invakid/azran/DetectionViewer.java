package me.invakid.azran;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetectionViewer extends Application {

    private static final int WIDTH = 1150, HEIGHT = 750;
    private static Background background;

    private TableView table;

    public static void main(String[] args) {
        background = new Background(new BackgroundImage(new Image(DetectionViewer.class.getResourceAsStream("/background.jpg"), WIDTH + 10, HEIGHT + 10, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT));

        launch(args);
    }

    @SuppressWarnings("all")
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/window.fxml"));

        primaryStage.setTitle("Azran DetectionViewer");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));

        AnchorPane pane = (AnchorPane) root.lookup("#anchorPane");
        pane.setBackground(DetectionViewer.background);

        ImageView logo = (ImageView) root.lookup("#logo");
        logo.setSmooth(true);
        logo.setImage(new Image(this.getClass().getResourceAsStream("/logo.png")));

        table = (TableView) root.lookup("#dataTable");

        ContextMenu menu = new ContextMenu();

        MenuItem copy = new MenuItem("Copy to clipboard");
        copy.setOnAction((event) -> {
            Clipboard.getSystemClipboard().setContent(new ClipboardContent() {
                {
                    putString(((Detection) table.getSelectionModel().getSelectedItem()).toString());
                }
            });

            showAlert("Result of copy", "Successfully copied selected detection to clipboard!");
        });

        MenuItem save = new MenuItem("Save to file");
        save.setOnAction((event) -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select file");
                File output = fileChooser.showSaveDialog(primaryStage);

                if(output == null)
                    return;

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
                    for(Object detection : table.getItems()) {
                        writer.write(((Detection) detection).toString());
                        writer.newLine();
                    }
                }

                showAlert("Result of save", String.format("Successfully saved detections to %s!", output.getAbsolutePath()));
            } catch(Exception ignored) {
            }
        });

        menu.getItems().addAll(copy, save);

        table.setOnMousePressed((event) -> {
            if(event.getButton() != MouseButton.SECONDARY)
                return;

            if(table.getSelectionModel().getSelectedItem() == null)
                return;

            menu.show(table, event.getScreenX(), event.getScreenY());
        });

        ObservableList tableColumns       = table.getColumns();
        TableColumn    information        = (TableColumn) tableColumns.get(0);
        TableColumn    authentication     = (TableColumn) tableColumns.get(1);

        ObservableList informationColumns = information.getColumns();
        TableColumn    ignColumn          = (TableColumn) informationColumns.get(0);
        TableColumn    nameColumn         = (TableColumn) informationColumns.get(1);
        TableColumn    lineColumn         = (TableColumn) informationColumns.get(2);
        TableColumn    stringColumn       = (TableColumn) informationColumns.get(3);

        ObservableList authColumns        = authentication.getColumns();
        TableColumn    pinColumn          = (TableColumn) authColumns.get(0);
        TableColumn    guildColumn        = (TableColumn) authColumns.get(1);
        TableColumn    channelColumn      = (TableColumn) authColumns.get(2);
        TableColumn    generatedColumn    = (TableColumn) authColumns.get(3);

        ignColumn      .setCellValueFactory(new PropertyValueFactory<Detection, String>("ign"));
        nameColumn     .setCellValueFactory(new PropertyValueFactory<Detection, String>("detectionName"));
        lineColumn     .setCellValueFactory(new PropertyValueFactory<Detection, String>("detectionLine"));
        stringColumn   .setCellValueFactory(new PropertyValueFactory<Detection, String>("detectedString"));

        pinColumn      .setCellValueFactory(new PropertyValueFactory<Detection, String>("pinUsed"));
        guildColumn    .setCellValueFactory(new PropertyValueFactory<Detection, String>("guild"));
        channelColumn  .setCellValueFactory(new PropertyValueFactory<Detection, String>("channel"));
        generatedColumn.setCellValueFactory(new PropertyValueFactory<Detection, String>("generatedBy"));

        ObservableList<Detection> detections = FXCollections.observableArrayList();

        List<Document> database = loadDatabase();
        for(Document document : database) {
            try {
                detections.add(new Detection(
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(document.getObjectId("_id").getDate()),
                        document.getString("ign"),
                        document.getString("ip"),
                        document.getString("name"),
                        document.getString("line"),
                        document.getString("string"),
                        document.getString("pin"),
                        document.getString("guild"),
                        document.getString("channel"),
                        document.getString("member")
                ));
            } catch(Exception ignored) {
            }
        }

        table.setItems(detections);

        primaryStage.show();
        primaryStage.setResizable(false);
    }

    private List<Document> loadDatabase() {
        List<Document> documents = new ArrayList<>();

        MongoClient client = new MongoClient(new MongoClientURI("mongodb+srv://DetectionViewer:PLEc20J9kwLvFknM@azrancluster-cgmkd.mongodb.net/test"));
        MongoDatabase database = client.getDatabase("results");
        MongoCollection<Document> detections = database.getCollection("detections");

        for(Document doc : detections.find())
            documents.add(doc);

        return documents;
    }

    private static void showAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Azran DetectionViewer");
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

}
