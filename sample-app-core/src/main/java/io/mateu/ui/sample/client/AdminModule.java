package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractModule;
import io.mateu.ui.core.client.app.MenuEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AdminModule extends AbstractModule {

    @Override
    public String getName() {
        return "Admin";
    }

    @Override
    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractAction("My profile") {
            @Override
            public void run() {

            }
        }, (MenuEntry) new AbstractAction("Change password") {
            @Override
            public void run() {

            }
        }, (MenuEntry) new AbstractAction("Template") {
            @Override
            public void run() {

            }
        });
    }
}
