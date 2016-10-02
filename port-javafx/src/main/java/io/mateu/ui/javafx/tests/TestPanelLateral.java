package io.mateu.ui.javafx.tests;

import javafx.animation.Animation;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Created by miguel on 11/8/16.
 */
public class TestPanelLateral extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {


        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        BorderPane root;
        Scene s;
        primaryStage.setScene(s = new Scene(root = new BorderPane()));

        StackPane c;
        root.setCenter(c = new StackPane());
        c.setStyle("-fx-background-color: brown;");
        c.setOnMouseClicked(e -> System.out.println("click en el centro"));

        Pane r;
        root.setRight(r = new Pane());
        r.setStyle("-fx-background-color: cadetblue;");
        r.setOnMouseClicked(e -> {
            System.out.println(r.getWidth());

            //r.setMaxWidth((r.getWidth() > 40)?5:100);
            Animation a = new Transition() {

                double ancho;

                {
                    ancho = r.getWidth();
                    setCycleCount(new Double(ancho / 10).intValue());
                    setCycleDuration(Duration.millis(5));
                }

                @Override
                protected void interpolate(double frac) {
                    System.out.println("antes: " + r.getWidth());
                    r.setMaxWidth(ancho -= 10);
                    System.out.println("después: " + r.getMaxWidth());
                }
            };

            a.play();
        });
        r.setPickOnBounds(false);

        Label l;
        r.getChildren().add(l = new Label("IDIEWDEDHIWED WUDHEWUDH WDHWEUD UWD WUD WUDE"));
        l.setMinWidth(50);

        Rectangle rect = new Rectangle(45, 200);
        rect.setX(-5);
        rect.setY(0);
        rect.setArcHeight(5);
        rect.setArcWidth(5);
        Circle circ = new Circle(30);
        circ.setCenterY(40);
        circ.setCenterX(-5);


        //union function which combines any two shapes
        Shape path = Path.union(rect, circ);
        path = Path.subtract(path, new Rectangle(500, 500));
        path.setFill(Color.CADETBLUE);
        path.setStroke(Color.BLACK);

        path.setClip(new Rectangle(-100, 0, 99, 800));

        r.getChildren().add(path);
        path.setTranslateX(1);
        //path.setTranslateX(0);
        //path.setTranslateY(0);

        primaryStage.show();


    }

    public static void main(String... args) {
        launch(args);
    }
}
