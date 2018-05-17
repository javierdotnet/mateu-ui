package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutModule extends AbstractModule {
    @Override
    public String getName() {
        return "About";
    }

    public List<MenuEntry> buildMenu() {
        List<MenuEntry> l = new ArrayList<>();
        l.add(new AbstractAction("About us") {

            @Override
            public void run() {
                MateuUI.getClientSideHelper().openView(new AboutView(), isModifierPressed());
            }
        });

        l.add(new AbstractMenu("Submenus") {

            @Override
            public List<MenuEntry> buildEntries() {
                List<MenuEntry> l = new ArrayList<>();

                l.add(new AbstractMenu("Submenu 1") {

                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> l = new ArrayList<>();

                        l.add(new AbstractMenu("Submenu 3") {

                            @Override
                            public List<MenuEntry> buildEntries() {
                                List<MenuEntry> l = new ArrayList<>();

                                l.add(new AbstractAction("Action") {

                                    @Override
                                    public void run() {
                                        MateuUI.alert("Hello!");
                                    }
                                });

                                l.add(new AbstractAction("Action") {

                                    @Override
                                    public void run() {
                                        MateuUI.alert("Hello!");
                                    }
                                });

                                l.add(new AbstractAction("Action") {

                                    @Override
                                    public void run() {
                                        MateuUI.alert("Hello!");
                                    }
                                });


                                return l;
                            }
                        });

                        l.add(new AbstractMenu("Submenu 4") {

                            @Override
                            public List<MenuEntry> buildEntries() {
                                List<MenuEntry> l = new ArrayList<>();

                                l.add(new AbstractAction("Action") {

                                    @Override
                                    public void run() {
                                        MateuUI.alert("Hello!");
                                    }
                                });

                                return l;
                            }
                        });



                        return l;
                    }
                });


                l.add(new AbstractMenu("Submenu 2") {

                    @Override
                    public List<MenuEntry> buildEntries() {
                        List<MenuEntry> l = new ArrayList<>();

                        l.add(new AbstractAction("Action") {

                            @Override
                            public void run() {
                                MateuUI.alert("Hello!");
                            }
                        });

                        return l;
                    }
                });


                return l;
            }
        });

        return l;
    }
}
