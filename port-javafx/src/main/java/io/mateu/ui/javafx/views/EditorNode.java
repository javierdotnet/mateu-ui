package io.mateu.ui.javafx.views;

import io.mateu.ui.core.client.components.Tabs;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;

public class EditorNode extends BorderPane {
    private final ViewNode viewNode;
    private final List<Pane> panels;
    private TabPane tabPane = null;
    private Map<Tab, Node> contenidoTabs = new HashMap<>();

    public EditorNode(ViewNode viewNode, List<Pane> panels) {
        this.viewNode = viewNode;
        this.panels = panels;
        contenidoTabs = new HashMap<>();

        if (panels.size() > 0 && panels.get(0).getChildren().size() > 0 && panels.get(0).getChildren().get(0) instanceof VBox && ((VBox)panels.get(0).getChildren().get(0)).getChildren().size() > 0 && ((VBox)panels.get(0).getChildren().get(0)).getChildren().get(0) instanceof TabPane) {
            tabPane = (TabPane) ((VBox)panels.get(0).getChildren().get(0)).getChildren().get(0);
        } else {
            Tab t;
            tabPane = new TabPane(t = new Tab("Data"));
            VBox vb;
            ScrollPane sp = new ScrollPane(vb = new VBox(20));
            vb.setPadding(new Insets(10, 20, 10, 20));
            vb.getChildren().addAll(panels);
            t.setContent(vb);
        }

        for (Tab t : tabPane.getTabs()) {
            contenidoTabs.put(t, t.getContent());
            t.getContent().getStyleClass().add("contenidoeditor");
        }

        setLeft(new EditorLeftNode(this));
        setCenter(new EditorCenterNode(this));

        if (tabPane.getTabs().size() > 0) select(tabPane.getTabs().get(0));
    }

    public List<Pane> getPanels() {
        return panels;
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public Map<Tab, Node> getContenidoTabs() {
        return contenidoTabs;
    }

    public void select(Tab t) {
        setCenter(getContenidoTabs().get(t));
        ((EditorLeftNode)getLeft()).setSelected(t);
    }

    public ViewNode getViewNode() {
        return viewNode;
    }
}
