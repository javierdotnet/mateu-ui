package io.mateu.ui.javafx.newlayout;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.javafx.JavafxPort;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MenuNode extends VBox {

    private final List<AbstractArea> areas;

    public MenuNode(boolean authenticated) {

        AbstractArea area = null;

        areas = new ArrayList<>();
        for (AbstractArea a : JavafxPort.getApp().getAreas()) if ((authenticated && !a.isPublicAccess()) || (!authenticated && a.isPublicAccess())) {
            areas.add(a);
        }

        if (areas.size() > 1) {
            area = areas.get(0);
        } else if (areas.size() == 1) {
            area = areas.get(0);
        }



        loadArea(area);

    }

    private void loadArea(AbstractArea area) {

        getChildren().clear();

        if (areas.size() > 1) {

            ContextMenu m = new ContextMenu();

            FontAwesomeIconView iv;
            Label la;
            getChildren().add(new HBox(la = new Label(area.getName()), iv = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_LEFT)));
            la.getStyleClass().add("areaactual");
            iv.hoverProperty().addListener((z, o, h) -> {
                if (h) iv.setIcon(FontAwesomeIcon.ANGLE_DOWN);
                else if (!m.isShowing()) iv.setIcon(FontAwesomeIcon.ANGLE_LEFT);
            });

            AbstractArea finalArea = area;
            iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    m.getItems().clear();


                    m.getStyleClass().add("menuopcionesperfil");

                    for (AbstractArea a : areas) if (!finalArea.equals(a)) {
                        MenuItem i;
                        m.getItems().add(i = new MenuItem("Change to " + a.getName()));
                        i.setOnAction((e) -> {
                            loadArea(a);
                        });
                    }

                    Bounds boundsInScreen = iv.localToScreen(iv.getBoundsInLocal());

                    m.show(iv, boundsInScreen.getMinX(), boundsInScreen.getMaxY());

                    m.showingProperty().addListener((z, o, h) -> {
                        if (h) iv.setIcon(FontAwesomeIcon.ANGLE_DOWN);
                        else iv.setIcon(FontAwesomeIcon.ANGLE_LEFT);
                    });

                }
            });

        }

        if (area != null) for (AbstractModule m : area.getModules()) {
            Label lm;
            getChildren().add(lm = new Label(m.getName()));
            lm.getStyleClass().add("titulomenu");

            for (MenuEntry e : m.getMenu()) {
                if (e instanceof AbstractMenu) {
                    ContextMenu mx = new ContextMenu();
                    Label le;
                    FontAwesomeIconView iv;
                    getChildren().add(new HBox(le = new Label(e.getName()), iv = new FontAwesomeIconView(FontAwesomeIcon.ANGLE_LEFT)));
                    le.getStyleClass().add("opcionmenu");
                    iv.hoverProperty().addListener((z, o, h) -> {
                        if (h) iv.setIcon(FontAwesomeIcon.ANGLE_DOWN);
                        else if (!mx.isShowing()) iv.setIcon(FontAwesomeIcon.ANGLE_LEFT);
                    });

                    iv.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            mx.getItems().clear();

                            mx.getStyleClass().add("menuopcionesperfil");

                            buildSubmenu(mx, (AbstractMenu) e);

                            Bounds boundsInScreen = iv.localToScreen(iv.getBoundsInLocal());

                            mx.show(iv, boundsInScreen.getMinX(), boundsInScreen.getMaxY());

                            mx.showingProperty().addListener((z, o, h) -> {
                                if (h) iv.setIcon(FontAwesomeIcon.ANGLE_DOWN);
                                else iv.setIcon(FontAwesomeIcon.ANGLE_LEFT);
                            });

                        }
                    });
                } else {
                    Label le;
                    getChildren().add(le = new Label(e.getName()));
                    le.getStyleClass().add("opcionmenu");
                    le.setOnMouseClicked((x) -> {
                        if (e instanceof AbstractAction) MateuUI.runInUIThread(() ->  ((AbstractAction) e).run());
                    });
                }
            }
        }
    }

    private void buildSubmenu(ContextMenu mx, AbstractMenu s) {
        for (MenuEntry e : s.getEntries()) {
            if (e instanceof AbstractMenu) {

            } else {
                MenuItem i;
                mx.getItems().add(i = new MenuItem(e.getName()));
                i.setOnAction((z) -> {
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
