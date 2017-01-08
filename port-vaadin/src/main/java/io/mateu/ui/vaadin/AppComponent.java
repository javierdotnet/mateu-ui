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

        MateuUI.setClientSideHelper(new ClientSideHelper() {

            public BaseServiceAsync baseServiceImpl = new BaseServiceClientSideImpl();

            @Override
            public void openView(AbstractView view) {
                views.addView(view);
            }

            @Override
            public Data getNewDataContainer() {
                return new Data();
            }

            @Override
            public <T> T create(Class<?> serviceInterface) {
                try {
                    return (T) Class.forName(serviceInterface.getName().replaceAll("\\.shared\\.", ".client.") + "ClientSideImpl").newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void alert(String msg) {
                Notification.show("Alert", msg, Notification.Type.WARNING_MESSAGE);
            }

            @Override
            public void run(Runnable runnable) {
                new Thread(runnable).start();
            }

            @Override
            public void runInUIThread(Runnable runnable) {

                getUI().access(runnable);

            }

            @Override
            public BaseServiceAsync getBaseService() {
                return baseServiceImpl;
            }
        });


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
