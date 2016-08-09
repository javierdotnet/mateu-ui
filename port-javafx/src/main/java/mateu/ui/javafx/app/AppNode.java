package mateu.ui.javafx.app;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import mateu.ui.javafx.JavafxPort;
import mateu.ui.javafx.views.ViewNode;

/**
 * Created by miguel on 9/8/16.
 */
public class AppNode extends BorderPane {

    public static AppNode me;

    private final TopNode topNode;
    private final ViewNode viewNode;
    private final MenuNode menuNode;

    public static AppNode get() {
        return me;
    }

    public AppNode() {
        me = this;
        setTop(topNode = new TopNode());
        setLeft(menuNode = new MenuNode());
        setCenter(viewNode = new ViewNode());
        setStyle("-fx-background-color: #FFeaea;");

        

    }

    public TopNode getTopNode() {
        return topNode;
    }

    public ViewNode getViewNode() {
        return viewNode;
    }

    public MenuNode getMenuNode() {
        return menuNode;
    }


}
