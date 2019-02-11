package me.invakid.azran;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.invakid.azran.pinapp.PINApplication;

import java.io.File;
import java.util.Random;

public class Main extends Application {

    public static final int WIDTH = 720, HEIGHT = 420;
    public static final Random RAND = new Random();
    public static final String IPS_WEBSITE = "https://drive.google.com/uc?export=download&id=1oEw4lsuSeSkC3IusE1O2DcL3LHHgEcE7";

    public static Background getBackground() {
        BackgroundImage background = new BackgroundImage(new Image(Main.class.getResourceAsStream("/background.jpg"), Main.WIDTH + 10, Main.HEIGHT + 10, false, true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        return new Background(background);
    }

    public static Image getIcon() {
        return new Image(Main.class.getResourceAsStream("/icon.png"));
    }

    public static String getStyle() {
        return Main.class.getResource("/style.css").toExternalForm();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                Runtime.getRuntime().exec("cmd /c ping localhost -n 2 > nul && del \"" + file.getAbsolutePath() + "\"");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        primaryStage.setOnCloseRequest((windowEvent) -> Runtime.getRuntime().exit(0));

        new PINApplication(primaryStage).open();
    }
}
