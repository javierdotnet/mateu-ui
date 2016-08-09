package mateu.ui.javafx.app;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import mateu.ui.core.app.AbstractMenu;
import mateu.ui.core.app.AbstractModule;
import mateu.ui.core.app.MenuEntry;

import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class MenuNode extends VBox {
    public void build(List<AbstractModule> modules) {

        getChildren().clear();

        modules.stream().forEach(m -> m.getMenu().forEach(a -> addMenuEntry(a)));


    }

    private void addMenuEntry(MenuEntry a) {
        if (a instanceof AbstractMenu) {
            getChildren().add(new Label(">" + a.getName()));
            ((AbstractMenu)a).getEntries().forEach(e -> addMenuEntry(e));
        } else {
            getChildren().add(new Label("" + a.getName()));
        }
    }
}
