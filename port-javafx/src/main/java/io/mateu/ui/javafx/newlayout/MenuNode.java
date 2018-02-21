package io.mateu.ui.javafx.newlayout;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.javafx.JavafxPort;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MenuNode extends VBox {

    public MenuNode(boolean authenticated) {

        AbstractArea area = null;

        List<AbstractArea> areas = new ArrayList<>();
        for (AbstractArea a : JavafxPort.getApp().getAreas()) if ((authenticated && !a.isPublicAccess()) || (!authenticated && a.isPublicAccess())) {
            areas.add(a);
        }

        if (areas.size() > 1) {
            area = areas.get(0);
        } else if (areas.size() == 1) {
            area = areas.get(0);
        }


        if (area != null) for (AbstractModule m : area.getModules()) {
            Label lm;
            getChildren().add(lm = new Label(m.getName()));
            lm.getStyleClass().add("titulomenu");

            for (MenuEntry e : m.getMenu()) {
                Label le;
                getChildren().add(le = new Label(e.getName()));
                le.getStyleClass().add("opcionmenu");
                le.setOnMouseClicked((x) -> {
                    if (e instanceof AbstractAction) MateuUI.runInUIThread(() ->  ((AbstractAction) e).run());
                });
            }
        }

    }

    private void buildMenuBar() {

        io.mateu.ui.javafx.app.AppNode.get().clearTabs();

        /*

        bar.getMenus().clear();

        for (AbstractArea a : JavafxPort.getApp().getAreas()) {

            boolean isPublic = a.isPublicAccess();
            if (!JavafxPort.getApp().isAuthenticationNeeded() || (isPublic && JavafxPort.getApp().getUserData() == null) || (!isPublic && JavafxPort.getApp().getUserData() != null)) {

                if (a.getName() != null) {

                    Menu m = new Menu(a.getName());

                    for (AbstractModule mod : a.getModules()) {
                        buildMenu(m, mod);
                    }

                    if (m.getItems().size() > 0) bar.getMenus().add(m);

                } else {

                    for (AbstractModule mod : a.getModules()) {
                        buildMenu(bar, mod);
                    }

                }


            }

        }
        */

        AbstractView h = (JavafxPort.getApp().getUserData() != null)?JavafxPort.getApp().getPrivateHome():JavafxPort.getApp().getPublicHome();
        if (h != null) MateuUI.openView(JavafxPort.getApp().getPublicHome());

    }

    private void buildMenu(MenuBar bar, AbstractModule mod) {
        Menu s = new Menu(mod.getName());
        for (MenuEntry e : mod.getMenu()) {
            buildMenu(s, e);
        }
        if (s.getItems().size() > 0) bar.getMenus().add(s);
    }

    private void buildMenu(MenuBar bar, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            Menu s = new Menu(e.getName());
            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) {
                buildMenu(s, ee);
            }
            if (s.getItems().size() > 0) bar.getMenus().add(s);
        } else {
            Menu i;
            bar.getMenus().add(i = new Menu(e.getName()));
            i.setOnShown(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    if (e instanceof AbstractAction) {
                        ((AbstractAction)e).run();
                    }
                }
            });
            i.setOnShowing(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    if (e instanceof AbstractAction) {
                        ((AbstractAction)e).run();
                    }
                }
            });
        }
    }

    private void buildMenu(Menu m, AbstractModule mod) {

        Menu s = new Menu(mod.getName());
        for (MenuEntry e : mod.getMenu()) {
            buildMenu(s, e);
        }
        if (s.getItems().size() > 0) m.getItems().add(s);

    }

    private void buildMenu(Menu m, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            Menu s = new Menu(e.getName());
            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) {
                buildMenu(s, ee);
            }
            if (s.getItems().size() > 0) m.getItems().add(s);
        } else {
            MenuItem i;
            m.getItems().add(i = new MenuItem(e.getName()));
            i.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (e instanceof AbstractAction) {
                        ((AbstractAction)e).run();
                    }
                }
            });
        }
    }
}
