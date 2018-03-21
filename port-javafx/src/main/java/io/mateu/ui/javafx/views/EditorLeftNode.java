package io.mateu.ui.javafx.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.javafx.data.DataStore;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorLeftNode extends VBox {
    private final EditorNode editorNode;
    private Map<Tab, Button> botones;

    public EditorLeftNode(EditorNode editorNode) {
        this.editorNode = editorNode;

        getStyleClass().add("izdaeditor");

        botones = new HashMap<>();

        setPrefWidth(225);


        for (Tab t : editorNode.getTabPane().getTabs()) {
            Button b;
            getChildren().add(b = new Button(t.getText()));
            b.getStyleClass().add("editortab");
            botones.put(t, b);
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    editorNode.select(t);
                }
            });
        }


        if (true) {
            VBox badgesPane;
            getChildren().add(badgesPane = new VBox(2));

            badgesPane.getStyleClass().add("izdaeditorresumen");

            Property<ObservableList<DataStore>> pb = editorNode.getViewNode().getDataStore().getObservableListProperty("_badges");
            ListChangeListener<DataStore> pl;
            pb.getValue().addListener(pl = new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {
                    badgesPane.getChildren().clear();
                    for (DataStore x : pb.getValue()) {
                        Label l = new Label("" + x.get("_value"));
                        if (x.get("_css") != null) l.getStyleClass().add(x.get("_css"));
                        badgesPane.getChildren().add(l);
                    }

                }
            });
            pl.onChanged(new ListChangeListener.Change<DataStore>(pb.getValue()) {
                @Override
                public boolean next() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public int getFrom() {
                    return 0;
                }

                @Override
                public int getTo() {
                    return 0;
                }

                @Override
                public List<DataStore> getRemoved() {
                    return null;
                }

                @Override
                protected int[] getPermutation() {
                    return new int[0];
                }
            });
        }

        //links
        if (true) {
            VBox linksPane;
            getChildren().add(linksPane = new VBox(2));

            linksPane.getStyleClass().add("izdaeditorlinks");

            Property<ObservableList<DataStore>> pb = editorNode.getViewNode().getDataStore().getObservableListProperty("_links");
            ListChangeListener<DataStore> pl;
            pb.getValue().addListener(pl = new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {
                    linksPane.getChildren().clear();
                    for (DataStore x : pb.getValue()) {
                        Hyperlink l = new Hyperlink("" + x.get("_caption"));
                        l.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                ((AbstractAction)x.get("_action")).run();
                            }
                        });
                        linksPane.getChildren().add(l);
                    }

                }
            });
            pl.onChanged(new ListChangeListener.Change<DataStore>(pb.getValue()) {
                @Override
                public boolean next() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public int getFrom() {
                    return 0;
                }

                @Override
                public int getTo() {
                    return 0;
                }

                @Override
                public List<DataStore> getRemoved() {
                    return null;
                }

                @Override
                protected int[] getPermutation() {
                    return new int[0];
                }
            });


        }
        // end of links


    }

    public void setSelected(Tab t) {
        for (Button b : botones.values()) b.getStyleClass().remove("seleccionado");
        botones.get(t).getStyleClass().add("seleccionado");
    }
}
