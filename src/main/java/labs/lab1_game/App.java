package labs.lab1_game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Platform;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    public static double win_w = 960;
    public static double win_h = 640;
    
    public static double player_radius = 30;
    public static double target_radius = 30;
    
    public static double target_speed = 10;
    
    public static double arrow_speed = 100;
    
    public static long sleep_time = 50;
    
    public static double arrow_hitbox_radius = 10;
    
    public static double arrow_length = 30;
    
    public static double arrow_width = 4;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), win_w, win_h);
        stage.setScene(scene);
        stage.setTitle("Fire in a hole!");
        stage.setResizable(false);
        // Without this the process will just live forever
        stage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}
