package org.example.studytracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class MainApp extends Application {

    private static final String BASE = "/org/example/studytracker/";

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        scene = new Scene(loadFxml("auth.fxml").load(), 1000, 640);

        URL css = MainApp.class.getResource(BASE + "styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        primaryStage.setTitle("StudyTrack - Time Tracker");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(580);
        primaryStage.show();
    }

    /** Shows the sign-in / create-account screen. */
    public static void showAuth() {
        try {
            scene.setRoot(loadFxml("auth.fxml").load());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Shows the dashboard for the signed-in user. */
    public static void showDashboard(User user) {
        try {
            FXMLLoader loader = loadFxml("dashboard.fxml");
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setUser(user);
            scene.setRoot(root);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static FXMLLoader loadFxml(String name) {
        URL url = MainApp.class.getResource(BASE + name);
        if (url == null) {
            throw new IllegalStateException(
                "Cannot find " + name + ". It must be at src/main/resources/org/example/studytracker/" + name);
        }
        return new FXMLLoader(url);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
