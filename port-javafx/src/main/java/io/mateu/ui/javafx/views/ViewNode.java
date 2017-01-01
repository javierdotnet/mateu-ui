package io.mateu.ui.javafx.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.*;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.data.ViewNodeDataStore;
import io.mateu.ui.javafx.views.components.GridNode;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ViewNode extends StackPane {

    private AbstractView view;
    private DataStore dataStore;
    private BorderPane bp;
    private ProgressIndicator progressIndicator;
    private Pane componentsCotainer;
    private boolean minsFixed;
    private Node lastNode;

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public ViewNode() {
        setStyle("-fx-background-color: #eaeaff;");
    }

    public ViewNode(AbstractView view) {
        this();
        this.view = view;
        dataStore = new ViewNodeDataStore(this);
        build();
        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                getDataStore().setData(newData);
            }
        });
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public AbstractView getView() {
        return view;
    }

    public void startWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                getChildren().add(progressIndicator = new ProgressIndicator());
            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                getChildren().remove(progressIndicator);
            }
        });
    }

    public void build() {
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        setStyle("-fx-background-color: white;");

        bp = new BorderPane();
        bp.setTop(createToolBar(view.getActions()));
        ScrollPane sp = new ScrollPane(componentsCotainer = new Pane());
        sp.getStyleClass().add("mateu-view-scroll");
        bp.setCenter(sp);
        componentsCotainer.getStyleClass().add("mateu-view");
        if (false) addEventHandler(Event.ANY, e -> {
            System.out.println("caught " + e.getEventType().getName());
        });
        build(componentsCotainer, view.getForm());

        getChildren().add(bp);
    }

    private Node createToolBar(List<AbstractAction> actions) {
        ToolBar toolBar = new ToolBar();

        /*
        if (view instanceof AbstractListView) {
            List<Component> cs = view.getForm().getComponentsSequence();
            int pos = 0;
            for (Component c : cs) {
                addComponent(null, toolBar, c, false);
            }
        } else {

        }
        */

        for (AbstractAction a : actions) {
            Button b;
            toolBar.getItems().add(b = new Button(a.getName()));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    a.run();
                }
            });
        }
        {
            Button b;
            toolBar.getItems().add(b = new Button("Data"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println(getDataStore().toString());
                }
            });
        }
        return toolBar;
    }

    private void build(Pane overallContainer, FieldContainer form) {

        List<Pane> panels = new ArrayList<>();
        panels.add(new VBox(2));

        if (view instanceof AbstractEditorView) {
            AbstractEditorView ev = (AbstractEditorView) view;
            ev.addEditorViewListener(new EditorViewListener() {
                @Override
                public void onLoad() {
                    startWaiting();
                }

                @Override
                public void onSave() {
                    startWaiting();
                }

                @Override
                public void onSuccess() {
                    endWaiting();
                }

                @Override
                public void onFailure(Throwable caught) {
                    endWaiting();
                }
            });
            if (ev.getInitialId() != null) ev.load();
        }

        if (view instanceof AbstractListView) {
            AbstractListView lv = (AbstractListView) view;
            lv.addListViewListener(new ListViewListener() {
                @Override
                public void onReset() {

                }

                @Override
                public void onSearch() {
                    startWaiting();
                }

                @Override
                public void onSuccess() {
                    endWaiting();
                }

                @Override
                public void onFailure(Throwable caught) {
                    endWaiting();
                }
            });
            addComponent(overallContainer, panels.get(panels.size() - 1), new GridField("_data", lv.getColumns()).setPaginated(true).setExpandable(false), true);
        } else {
            int pos = 0;
            List<Component> cs = form.getComponentsSequence();
            for (Component c : cs) {
                if (c instanceof ColumnStart) panels.add(new VBox(2));
                else if (c instanceof RowStart) panels.add(new FlowPane(5, 2));
                else if (c instanceof ColumnEnd) panels.remove(panels.size() - 1);
                else if (c instanceof RowEnd) panels.remove(panels.size() - 1);
                else addComponent(overallContainer, panels.get(panels.size() - 1), c, form.isLastFieldMaximized() && pos++ == cs.size() - 1);
            }
        }

        overallContainer.getChildren().addAll(panels);

        if (!form.isLastFieldMaximized()) ((ScrollPane)bp.getCenter()).viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                fixMins();
                //System.out.println("bounds changed to " + newValue.toString());
                overallContainer.setPrefHeight(newValue.getHeight());
                overallContainer.setPrefWidth(newValue.getWidth());

            }
        });
    }

    private void fixMins() {

        if ((true || !minsFixed) && lastNode != null && lastNode instanceof Region) {
            Region r = (Region) lastNode;
            componentsCotainer.setMinWidth(r.getBoundsInParent().getWidth() + r.getLocalToSceneTransform().transform(0, 0).getX() - componentsCotainer.getLocalToSceneTransform().transform(0, 0).getX());
            componentsCotainer.setMinHeight(r.getBoundsInParent().getHeight() + r.getLocalToSceneTransform().transform(0, 0).getY() - componentsCotainer.getLocalToSceneTransform().transform(0, 0).getY());
            //System.out.println("mins=" + componentsCotainer.getMinWidth() + "," + componentsCotainer.getMinHeight());
        }

        minsFixed = true;
    }

    private void addComponent(Pane overallContainer, Pane container, Component c, boolean maximize) {
        //TODO: resolver con inyección de dependencias

        Node n = null;

        if (c instanceof TextField) {
            Pane donde = container;
            if (donde instanceof VBox) {
                ((VBox) donde).getChildren().add(donde = new FlowPane(5, 2));
            }
            if (((TextField) c).getLabel() != null) {
                Label l;
                donde.getChildren().add(l = new Label(((TextField) c).getLabel().getText()));
                l.getStyleClass().add("label");
            }
            javafx.scene.control.TextField tf;
            donde.getChildren().add(tf = new javafx.scene.control.TextField());

            tf.textProperty().bindBidirectional(dataStore.getStringProperty(((TextField) c).getId()));
            n = tf;
        } else if (c instanceof io.mateu.ui.core.client.components.Label) {
            Pane donde = container;
            if (donde instanceof VBox) {
                ((VBox) donde).getChildren().add(donde = new FlowPane());
            }
            Label l;
            donde.getChildren().add(n = l = new Label(((io.mateu.ui.core.client.components.Label) c).getText()));
            l.setStyle("-fx-alignment: baseline-" + getAlignmentString(((io.mateu.ui.core.client.components.Label) c).getAlignment()) + ";");
        } else if (c instanceof GridField) {
            Pane donde = container;
            if (donde instanceof VBox) {
                ((VBox) donde).getChildren().add(donde = new FlowPane());
            }
            if (((GridField) c).getLabel() != null && ((GridField) c).getLabel().getText() != null) {
                Label l;
                donde.getChildren().add(l = new Label(((GridField) c).getLabel().getText()));
                l.getStyleClass().add("label");
            }
            donde.getChildren().add(n = new GridNode(this, (GridField) c));
        }

        lastNode = n;

        if (maximize && n != null && n instanceof Region) {
            Region r = (Region) n;

            ((ScrollPane)bp.getCenter()).viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                    fixMins();
                    //System.out.println("bounds changed to " + newValue.toString());
                    fixHeight(overallContainer, r, newValue.getWidth(), newValue.getHeight());
                }
            });

        }

    }

    private String getAlignmentString(Alignment alignment) {
        if (alignment == Alignment.RIGHT) return "right";
        else if (alignment == Alignment.CENTER) return "center";
        else return "left";
    }

    private void fixHeight(Pane overallContainer, Region r, double w, double h) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double deltaY =  r.getLocalToSceneTransform().transform(0, 0).getY() - overallContainer.getLocalToSceneTransform().transform(0, 0).getY();
                //System.out.println("deltay = " + deltaY + ", w = " + w + ", h = " + h);
                r.setPrefHeight(h - deltaY);
                r.setPrefWidth(w);
            }
        });
    }


}
