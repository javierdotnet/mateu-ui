package mateu.ui.javafx.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import mateu.ui.core.components.*;
import mateu.ui.core.components.TextField;
import mateu.ui.core.views.AbstractView;
import mateu.ui.core.views.FieldContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ViewNode extends BorderPane {

    private AbstractView view;

    public ViewNode() {
        setStyle("-fx-background-color: #eaeaff;");
    }

    public AbstractView getView() {
        return view;
    }

    public void setView(AbstractView view) {
        this.view = view;
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        getChildren().clear();
        setTop(new Label(view.getTitle()));
        setCenter(build(view.getForm()));
        if (false) addEventHandler(Event.ANY, e -> {
            System.out.println("caught " + e.getEventType().getName());
        });
        getCenter().boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                System.out.println("bounds changed to " + newValue.toString());
            }
        });
    }

    private Node build(FieldContainer form) {

        List<Pane> panels = new ArrayList<>();
        panels.add(new VBox());

        form.getComponentsSequence().forEach(c -> {

            if (c instanceof ColumnStart) panels.add(new VBox());
            else if (c instanceof RowStart) panels.add(new FlowPane());
            else if (c instanceof ColumnEnd) panels.remove(panels.size() - 1);
            else if (c instanceof RowEnd) panels.remove(panels.size() - 1);
            else addComponent(panels.get(panels.size() - 1), c);

        });

        return panels.get(0);
    }

    private void addComponent(Pane container, Component c) {
        //TODO: resolver con inyección de dependencias

        if (c instanceof TextField) {
            Pane donde = container;
            if (donde instanceof VBox) {
                ((VBox) donde).getChildren().add(donde = new FlowPane());
            }
            if (((TextField) c).getLabel() != null) {
                Label l;
                donde.getChildren().add(l = new Label(((TextField) c).getLabel()));
                l.getStyleClass().add("label");
            }
            donde.getChildren().add(new javafx.scene.control.TextField());
        } else if (c instanceof mateu.ui.core.components.Label) {
            Pane donde = container;
            if (donde instanceof VBox) {
                ((VBox) donde).getChildren().add(donde = new FlowPane());
            }
            donde.getChildren().add(new Label(((mateu.ui.core.components.Label) c).getText()));
        }

    }
}
