package io.mateu.ui.javafx.app;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.javafx.JavafxPort;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Created by miguel on 9/8/16.
 */
public class TopNode extends MenuBar {

    public TopNode() {
        /*
        setStyle("-fx-background-color: #eaffea;");
        setPrefHeight(30);

        setPadding(new Insets(8));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        getChildren().add(new Label("Welcome to " + JavafxPort.getApp().getName() + "!"));
//        Region spacer = new Region();
//        setHgrow(spacer, Priority.ALWAYS);
//        getChildren().add(spacer);
*/
        buildMenuBar();


        HBox areas = new HBox();
        areas.setAlignment(Pos.CENTER);
        areas.setSpacing(10);
        /*
        JavafxPort.getApp().getAreas().forEach(a -> {
            Button b;
            areas.getChildren().add(b = new Button(a.getName()));
            b.setOnAction(e -> AppNode.get().getMenuNode().build(a.getModules()));
        });
        */
        getChildren().add(areas);

        //setHgrow(areas, Priority.ALWAYS);

        getChildren().add(new TextField("Search in " + JavafxPort.getApp().getName()));
        getChildren().add(new Button("Logout"));
    }

    private void buildMenuBar() {

        for (AbstractArea a : JavafxPort.getApp().getAreas()) {
            Menu m;
            getMenus().add(m = new Menu(a.getName()));

            for (AbstractModule mod : a.getModules()) {
                buildMenu(m, mod);
            }
        }
    }

    private void buildMenu(Menu m, AbstractModule mod) {
        for (MenuEntry e : mod.getMenu()) {
            buildMenu(m, e);
        }
    }

    private void buildMenu(Menu m, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            Menu s;
            m.getItems().add(s = new Menu(e.getName()));
            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) {
                buildMenu(s, ee);
            }
        } else {
            MenuItem i;
            m.getItems().add(i = new MenuItem(e.getName()));
            i.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (e instanceof AbstractAction) {
                        ((AbstractAction)e).run();
                    }
                }
            });
        }
    }

}
