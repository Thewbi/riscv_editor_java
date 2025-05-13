package de.template;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        URL rss = getClass().getResource("App.fxml");
        Parent root = FXMLLoader.load(rss);

        //Scene scene = new Scene(new Label("Hello World!"), 300, 250)
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Hello World!");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
