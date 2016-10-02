package io.mateu.ui.sample.app;

import io.mateu.ui.core.app.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ComponentsModule extends AbstractModule {
    public List<MenuEntry> getMenu() {
        return Arrays.asList((MenuEntry) new AbstractMenu() {
            @Override
            public String getName() {
                return "App";
            }

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractApplication";
                    }

                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractArea";
                    }

                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractModule";
                    }

                    @Override
                    public void run() {

                    }
                });
            }
        }, (MenuEntry) new AbstractMenu() {
            @Override
            public String getName() {
                return "Views";
            }

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractView";
                    }

                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractSearcher";
                    }

                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractForm";
                    }

                    @Override
                    public void run() {

                    }
                });
            }
        }, (MenuEntry) new AbstractMenu() {
            @Override
            public String getName() {
                return "Components";
            }

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "AbstractField";
                    }

                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "Label";
                    }

                    @Override
                    public void run() {

                    }
                }, (MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "TextField";
                    }

                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new TextFieldView());
                    }
                });
            }
        }, (MenuEntry) new AbstractMenu() {
            @Override
            public String getName() {
                return "Components";
            }

            @Override
            public List<MenuEntry> getEntries() {
                return Arrays.asList((MenuEntry) new AbstractAction() {
                    @Override
                    public String getName() {
                        return "Complex form";
                    }

                    @Override
                    public void run() {
                        MateuUI.getClientSideHelper().openView(new ComplexFormView());
                    }
                });
            }
        });
    }
}
