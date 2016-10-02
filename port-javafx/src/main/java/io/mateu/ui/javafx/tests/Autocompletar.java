package io.mateu.ui.javafx.tests;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Created by miguel on 11/8/16.
 */
public class Autocompletar extends TextField {

    private final ContextMenu popup;

    public Autocompletar() {
        super();

        popup = new ContextMenu();


        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                popup.getItems().clear();
                popup.getItems().add(new CustomMenuItem(new Label("aaaaa: " + oldValue + "->" + newValue)));
                if (!popup.isShowing()) {
                    popup.show(Autocompletar.this, Side.BOTTOM, 0, 0);
                }
            }
        });


        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                popup.hide();
            }
        });

    }

}
