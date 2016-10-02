package io.mateu.ui.javafx.app;

import io.mateu.ui.javafx.JavafxPort;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Created by miguel on 9/8/16.
 */
public class TopNode extends HBox {

    public TopNode() {
        setStyle("-fx-background-color: #eaffea;");
        setPrefHeight(30);

        setPadding(new Insets(8));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        getChildren().add(new Label("Welcome to " + JavafxPort.getApp().getName() + "!"));
//        Region spacer = new Region();
//        setHgrow(spacer, Priority.ALWAYS);
//        getChildren().add(spacer);

        HBox areas = new HBox();
        areas.setAlignment(Pos.CENTER);
        areas.setSpacing(10);
        JavafxPort.getApp().getAreas().forEach(a -> {
            Button b;
            areas.getChildren().add(b = new Button(a.getName()));
            b.setOnAction(e -> AppNode.get().getMenuNode().build(a.getModules()));
        });
        getChildren().add(areas);
        setHgrow(areas, Priority.ALWAYS);

        getChildren().add(new TextField("Search in " + JavafxPort.getApp().getName()));
        getChildren().add(new Button("Logout"));
    }

}
