package mateu.ui.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Created by miguel on 11/8/16.
 */
public class TestAutocompletar extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {


        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        VBox root;
        Scene s;
        primaryStage.setScene(s = new Scene(root = new VBox()));

        Label l;
        root.getChildren().add(l = new Label("xxx"));


        TextField t;
        root.getChildren().add(t = new Autocompletar());


        primaryStage.show();


    }
}
