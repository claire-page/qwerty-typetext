package ui;

import controllers.Control;
import io.database.DBControl;

import javafx.application.Application;

import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;


public class Qwerty extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        Control controller = new Control(); //instantiating controller.

        //the return value of basicLayoutBuilder.build() returns a BorderPane,
        // but it's also being declared here as a Parent.
        //this is so I can set it as the root node of the scene.
        //woooo polymorphism!

        Parent homeScreen = new BasicLayoutBuilder(controller).build();
        stage.setScene(new Scene(homeScreen, Style.DEFAULT_SCENEWIDTH, Style.DEFAULT_SCENEHEIGHT));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        Application.launch(Qwerty.class);
    }

}


