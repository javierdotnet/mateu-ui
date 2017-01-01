package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutModule extends AbstractModule {
    public List<MenuEntry> getMenu() {
        List<MenuEntry> l = new ArrayList<>();
        l.add(new AbstractAction() {
            @Override
            public String getName() {
                return "About us";
            }

            @Override
            public void run() {
                MateuUI.getClientSideHelper().openView(new AboutView());
            }
        });
        return l;
    }
}
