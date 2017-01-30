package io.mateu.ui.javafx.app;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.javafx.JavafxPort;
import javafx.scene.layout.BorderPane;
import io.mateu.ui.javafx.views.ViewNode;

/**
 * Created by miguel on 9/8/16.
 */
public class AppNode extends BorderPane {

    public static AppNode me;

    private final TopNode topNode;
    private final ViewsNode viewsNode;
    //private final MenuNode menuNode;

    public static AppNode get() {
        return me;
    }

    public AppNode() {
        me = this;
        setTop(topNode = new TopNode());
        //setLeft(menuNode = new MenuNode());
        setCenter(viewsNode = new ViewsNode());
        setStyle("-fx-background-color: #FFeaea;");

    }

    public void clearTabs() {
        if (viewsNode != null) viewsNode.clearTabs();
    }

    public TopNode getTopNode() {
        return topNode;
    }

    public ViewsNode getViewsNode() {
        return viewsNode;
    }

    /*
    public MenuNode getMenuNode() {
        return menuNode;
    }
    */


}
