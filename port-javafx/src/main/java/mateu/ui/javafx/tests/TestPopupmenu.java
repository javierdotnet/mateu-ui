package mateu.ui.javafx.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Created by miguel on 11/8/16.
 */
public class TestPopupmenu extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        VBox root;
        Scene s;
        primaryStage.setScene(s = new Scene(root = new VBox()));

        Label l;
        root.getChildren().add(l = new Label("Menú"));

        Circle circle = new Circle(15, 15, 42);
        root.getChildren().add(circle);
        Tooltip.install(
                circle,
                new Tooltip("Circle of light")
        );

        final PasswordField pf = new PasswordField();
        final Tooltip tooltip = new Tooltip();
        tooltip.setText(
                "\nYour password must be\n" +
                        "at least 8 characters in length\n"
        );
        pf.setTooltip(tooltip);

        root.getChildren().add(pf);


        primaryStage.show();



    }
}
