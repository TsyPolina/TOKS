package GUI;

import Controller.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {
    public void start(Stage stage) throws Exception{
            Parent chat = FXMLLoader.load(getClass().getResource("ChatForm.fxml"));
            stage.setTitle("Chat");
            stage.setScene(new Scene(chat, 673, 864));
            stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}

