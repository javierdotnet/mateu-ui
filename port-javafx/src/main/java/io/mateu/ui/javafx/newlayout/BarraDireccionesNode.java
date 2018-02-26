package io.mateu.ui.javafx.newlayout;

import io.mateu.ui.core.client.app.MateuUI;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class BarraDireccionesNode extends HBox {

    private static BarraDireccionesNode singleton;
    private final TextField dir;

    public BarraDireccionesNode() {
        singleton = this;

        {
            Button b;
            getChildren().add(b = new Button("Back"));

            b.setOnAction((e) -> {

                System.out.println("back");

                MateuUI.runInUIThread(() -> VistaActualNode.get().back());
            });
        }


        getChildren().add(dir = new TextField());

        {
            Button b;
            getChildren().add(b = new Button("Go"));

            b.setOnAction((e) -> {
                System.out.println("vamos a " + dir.getText());

                cargar(dir.getText());
            });
        }


        dir.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    event.consume();
                    cargar(dir.getText());
                }
            }
        });

        setHgrow(dir, Priority.ALWAYS);
    }

    public void cargar(String url) {
        System.out.println("cargamos " + url + "...");
        dir.setText(url);
        MateuUI.runInUIThread(() -> VistaActualNode.get().cargar(url));
    }

    public static BarraDireccionesNode get() {
        return singleton;
    }
}
