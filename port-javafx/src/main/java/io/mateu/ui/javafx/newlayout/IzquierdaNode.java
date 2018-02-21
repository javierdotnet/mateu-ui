package io.mateu.ui.javafx.newlayout;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.javafx.JavafxPort;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

public class IzquierdaNode extends VBox {

    public IzquierdaNode() {
        setPrefWidth(230);
        setMinWidth(230);
        setMaxWidth(230);
        setStyle("-fx-background-color: #273238;");


        getChildren().add(new SesionNode());



    }

}
