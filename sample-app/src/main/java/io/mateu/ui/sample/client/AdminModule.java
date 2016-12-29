package io.mateu.ui.sample.client;

import io.mateu.ui.core.app.AbstractAction;
import io.mateu.ui.core.app.AbstractModule;
import io.mateu.ui.core.app.MenuEntry;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AdminModule extends AbstractModule {
    @Override
    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractAction() {
            @Override
            public String getName() {
                return "My profile";
            }

            @Override
            public void run() {

            }
        }, (MenuEntry) new AbstractAction() {
            @Override
            public String getName() {
                return "Change password";
            }

            @Override
            public void run() {

            }
        }, (MenuEntry) new AbstractAction() {
            @Override
            public String getName() {
                return "Template";
            }

            @Override
            public void run() {

            }
        });
    }
}
