package io.mateu.ui.vaadin;

import com.vaadin.ui.*;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 2/1/17.
 */
public class AppComponent extends VerticalLayout {

    private final AbstractApplication app;
    private final ViewsComponent views;

    public AppComponent(AbstractApplication app) {
        this.app = app;

        //setSizeFull();




        addComponent(buildMenu(app));


        addComponent(views = new ViewsComponent());

        /*
        final TextField name = new TextField();
        name.setCaption("Type your name here:");

        Button button = new Button("Click Me");
        button.addClickListener( e -> {
            addComponent(new Label("Thanks " + name.getValue()
                    + ", it works!"));
        });

        addComponents(name, button);
        */

        setMargin(true);
        setSpacing(false);

    }

    private Component buildMenu(AbstractApplication app) {
        MenuBar b = new MenuBar();
        for (AbstractArea a : app.getAreas()) {
            MenuBar.MenuItem i = b.addItem(a.getName(), null, null);

            for (AbstractModule m : a.getModules()) {
                buildMenu(m, i);
            }
        }
        return b;
    }

    private void buildMenu(AbstractModule m, MenuBar.MenuItem i) {
        for (MenuEntry e : m.getMenu()) {
            buildMenu(e, i);
        }
    }

    private void buildMenu(MenuEntry e, MenuBar.MenuItem i) {
        if (e instanceof AbstractMenu) {
            MenuBar.MenuItem ii = i.addItem(e.getName(), null);
            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) {
                buildMenu(ee, ii);
            }
        } else {
            i.addItem(e.getName(), new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem menuItem) {
                    ((AbstractAction)e).run();
                }
            });
        }
    }

}
