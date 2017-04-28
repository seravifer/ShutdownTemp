package shutdown;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader miCargador = new FXMLLoader(getClass().getResource("view.fxml"));
            Scene scene = new Scene(miCargador.load());
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
            primaryStage.setTitle("Shutdown");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
