package mateu.ui.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mateu.ui.core.app.ClientSideHelper;
import mateu.ui.core.app.MateuUI;
import mateu.ui.core.views.AbstractView;
import mateu.ui.javafx.JavafxPort;
import mateu.ui.javafx.app.AppNode;

/**
 * Created by miguel on 11/8/16.
 */
public class TestFont extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Font.loadFont(JavafxPort.class.getResource("/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm(), 10);
        Font.loadFont(JavafxPort.class.getResource("/mateu/ui/javafx/fonts/lato/Lato-Regular.ttf").toExternalForm(), 10);
//        Font.loadFont(JavafxPort.class.getResource("/mateu/ui/javafx/fonts/lato/Lato-Bold.ttf").toExternalForm(), 10);
//        Font.loadFont(JavafxPort.class.getResource("/mateu/ui/javafx/fonts/lato/Lato-Italic.ttf").toExternalForm(), 10);


        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        VBox root;
        Scene s;
        primaryStage.setScene(s = new Scene(root = new VBox()));


        Label l;
        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: Lato-Regular;-fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: Lato-Bold;-fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: Lato-Italic;-fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: Lato;-fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: Lato;-fx-font-weight: bold; -fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: Lato;-fx-font-style: italic; -fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: XX;-fx-font-size: 40px;");

        root.getChildren().add(l = new Label("Esto es una prueba"));
        l.setStyle("-fx-font-family: TRON;-fx-font-size: 40px;");

        primaryStage.show();

    }
}
