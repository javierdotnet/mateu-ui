package io.mateu.ui.javafx.newlayout;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AppNode extends VBox {

    public AppNode() {

        getChildren().add(new BarraDireccionesNode());

        HBox h;
        getChildren().add(h = new HBox());
        h.getChildren().add(new IzquierdaNode());
        h.getChildren().add(new VistaActualNode());

        setVgrow(h, Priority.ALWAYS);
    }


}
