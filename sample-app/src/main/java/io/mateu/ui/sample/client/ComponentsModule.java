package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.app.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ComponentsModule extends AbstractModule {
    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractMenu("App") {
            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("AbstractApplication") {
                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction("AbstractArea") {
                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction("AbstractModule") {
                    @Override
                    public void run() {

                    }
                });
            }
        }, (MenuEntry) new AbstractMenu("Views") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("AbstractView") {
                     @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction("AbstractListView") {
                    @Override
                    public void run() {
                        MateuUI.openView(new ListView());
                    }
                }, (MenuEntry) new AbstractAction("AbstractCRUDView") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDView());
                    }
                }, (MenuEntry) new AbstractAction("AbstractCRUDView 2") {
                    @Override
                    public void run() {
                        MateuUI.openView(new CRUDView2());
                    }
                }, (MenuEntry) new AbstractAction("AbstractForm") {
                    @Override
                    public void run() {

                    }
                });
            }
        }, (MenuEntry) new AbstractMenu("Components") {

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("All fields") {
                    @Override
                    public void run() {
                        MateuUI.openView(new AllFieldsFiew());
                    }
                }, (MenuEntry) new AbstractAction("TextField") {
                     @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new TextFieldView());
                    }
                });
            }
        }, (MenuEntry) new AbstractMenu("More components") {
            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction("Complex form") {
                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new ComplexFormView());
                    }
                });
            }
        });
    }
}
