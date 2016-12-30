package io.mateu.ui.javafx.app;

import io.mateu.ui.core.views.AbstractView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;


/**
 * Created by miguel on 29/12/16.
 */
public class ViewsNode extends BorderPane {

    private final TabPane tabPane;

    public ViewsNode() {
        setCenter(tabPane = new TabPane());
    }


    public void addView(AbstractView view) {
        tabPane.getTabs().add(new ViewTab(view));
    }
}
