package io.mateu.ui.vaadin.components;

import com.vaadin.data.HasValue;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.vaadin.framework.MyUI;

import java.util.ArrayList;
import java.util.List;

public class SearchInMenuLayout extends VerticalLayout {

    private final CssLayout menu = new CssLayout();

    public SearchInMenuLayout() {
        TextField t;
        addComponent(t = new TextField("Search in menu"));
        t.setIcon(VaadinIcons.SEARCH);
        t.focus();

        t.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<String> e) {
                search(e.getValue());
            }
        });

        addComponent(menu);

        setSizeFull();

        search(null);
    }

    private void search(String text) {

        if (text == null) text = "";
        text = text.toLowerCase();

        boolean autentico = MateuUI.getApp().getUserData() != null;

        menu.removeAllComponents();

        CssLayout contenedor;
        menu.addComponent(contenedor = new CssLayout());
        contenedor.setWidth("100%");

        List<AbstractArea> areas = new ArrayList<>();

        for (AbstractArea a : io.mateu.ui.core.client.app.MateuUI.getApp().getAreas()) {
            if (autentico) {
                if (!a.isPublicAccess()) {
                    areas.add(a);
                }
            } else {
                if (!io.mateu.ui.core.client.app.MateuUI.getApp().isAuthenticationNeeded() || a.isPublicAccess()) {
                    areas.add(a);
                }
            }
        }
        for (AbstractArea a : areas) {

            CssLayout la = new CssLayout();
            for (AbstractModule m : a.getModules()) {
                for (MenuEntry e : m.getMenu()) {
                    addMenuEntry(la, e, text);
                }
            }

            if (la.getComponentCount() > 0) {
                VerticalLayout vl = new VerticalLayout();
                Label l;
                vl.addComponent(l = new Label("In " + a.getName() + ":"));
                l.addStyleName("tituloarea");
                vl.addComponent(la);
                vl.setSizeUndefined();
                contenedor.addComponent(vl);
            }

        }
    }

    private void addMenuEntry(Layout contenedor, MenuEntry e, String text) {
        VerticalLayout l = new VerticalLayout();
        l.addStyleName("listaopcionesmenu");
        l.setWidthUndefined();

        Component c = null;

        if (e instanceof AbstractMenu) {
            VerticalLayout lx = new VerticalLayout();
            lx.addStyleName("submenu");
            lx.setWidthUndefined();

            VerticalLayout lz = new VerticalLayout();

            lz.addStyleName("contenedorsubmenu");
            lz.setWidthUndefined();

            for (MenuEntry ez : ((AbstractMenu) e).getEntries()) {
                addMenuEntry(lz, ez, ("".equals(text) || e.getName().toLowerCase().contains(text))?"":text);
            }

            if (lz.getComponentCount() > 0) lx.addComponent(lz);

            if (lx.getComponentCount() > 0) {
                Label lab;
                lx.addComponent(lab = new Label(e.getName()));
                lab.addStyleName((contenedor instanceof CssLayout)?"titulosubmenuprincipal":"titulosubmenu");

                lx.addComponent(lz);

                c = lx;
            }

        } else if (e instanceof AbstractAction) {

            if ("".equals(text) || e.getName().toLowerCase().contains(text)) {

                Button b = new Button(e.getName(), new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        //navigator.navigateTo(item.getKey());
                        ((AbstractAction)e).setModifierPressed(event.isAltKey() || event.isCtrlKey()).run();
                    }
                });
                b.setCaption(b.getCaption()
                        //        + " <span class=\"valo-menu-badge\">123</span>"
                );
                b.setCaptionAsHtml(true);
                b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
                b.addStyleName("accionsubmenu");
                //b.setIcon(testIcon.get());  // sin iconos en el menú
                c = b;

            }

        }

        if (c != null) l.addComponent(c);

        if (l.getComponentCount() > 0) contenedor.addComponent(l);
    }


}
