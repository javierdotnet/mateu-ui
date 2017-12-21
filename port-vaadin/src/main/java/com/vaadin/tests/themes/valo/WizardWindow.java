package com.vaadin.tests.themes.valo;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.vaadin.ViewLayout;
import io.mateu.ui.vaadin.data.DataStore;
import io.mateu.ui.vaadin.data.ViewNodeDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
public class WizardWindow extends Window {

    private final AbstractWizard wizard;
    private DataStore dataStore;

    public WizardWindow(AbstractWizard wizard) {
        super(wizard.getTitle());

        dataStore = new DataStore(wizard.getData());
        wizard.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                //System.out.println("********setted(" + newData + ")");
                dataStore.setData(newData);
                wizard.getData().setAll(newData);
            }

            @Override
            public void setted(String k, Object v) {
                //System.out.println("********setted(" + k + ", " + v + ")");
                dataStore.set(k, v);
                wizard.getData().set(k, v);
            }

            @Override
            public void idsResetted() {
                dataStore.resetIds();
            }
        });
        wizard.addListener(new ViewListener() {
            @Override
            public void onClose() {
                close();
            }
        });

        this.wizard = wizard;

        // Center it in the browser window
        center();

        setModal(true);


        setWidth("800px");
        setHeight("820px");

        try {
            wizard.execute(null, dataStore.getData(), new Callback<AbstractWizardPageView>() {
                @Override
                public void onSuccess(AbstractWizardPageView result) {
                    update(result);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void update(AbstractWizardPageView view) {

        dataStore.setAll(view.initializeData());

        ViewLayout v = new ViewLayout(dataStore, view);
        v.setSizeFull();

        view.getForm().addDataSetterListener(new DataSetterListener() {
            public void setted(Data newData) {
                WizardWindow.this.dataStore.setData(newData);
                wizard.getData().setAll(newData);
            }

            public void setted(String k, Object v) {
                //System.out.println("****xx**setted(" + k + ", " + v + ")");
                WizardWindow.this.dataStore.set(k, v);
                wizard.getData().set(k, v);
            }

            public void idsResetted() {
                WizardWindow.this.dataStore.resetIds();
            }
        });

        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

        List<Component> cs = new ArrayList<>();

        for (AbstractAction a : view.getActions()) {
            cs.add(new Button(a.getName(), e -> {
                a.run();
            }));
        }

        if (!view.isFirstPage()) {
            cs.add(new Button("Previous", e -> {
                try {
                    wizard.execute(AbstractWizard.Actions.GOBACK, dataStore.getData(), new Callback<AbstractWizardPageView>() {
                        @Override
                        public void onSuccess(AbstractWizardPageView result) {
                            update(result);
                        }
                    });
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }));
        }
        Button b = null;
        if (!view.isLastPage()) {
            cs.add(b = new Button("Next", e -> {
                List<String> errors = view.getForm().validate();
                if (errors.size() > 0) {
                    MateuUI.notifyErrors(errors);
                } else {
                    try {
                        wizard.execute(AbstractWizard.Actions.GONEXT, dataStore.getData(), new Callback<AbstractWizardPageView>() {
                            @Override
                            public void onSuccess(AbstractWizardPageView result) {
                                update(result);
                            }
                        });
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }));
        } else {
            cs.add(b = new Button("Done", e -> {
                List<String> errors = view.getForm().validate();
                if (errors.size() > 0) {
                    MateuUI.notifyErrors(errors);
                } else {
                    try {
                        wizard.execute(AbstractWizard.Actions.END, dataStore.getData(), new Callback<AbstractWizardPageView>() {
                            @Override
                            public void onSuccess(AbstractWizardPageView result) {
                                update(result);
                            }
                        });
                        close();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }));
        }
        b.addStyleName(ValoTheme.BUTTON_PRIMARY);
        b.setClickShortcut(ShortcutAction.KeyCode.ENTER);


        if (false) {
            cs.add(b = new Button("Data", e -> {
                System.out.println("************");
                System.out.println("getData() = " + getData());
                System.out.println("************");
                System.out.println("pageview.getData() = " + view.getData());
                System.out.println("************");
                System.out.println("wizard.getData() = " + wizard.getData());
                System.out.println("************");
                System.out.println("datastore.getData() = " + dataStore.getData());
                System.out.println("************");
            }));

        }

        Label footerText = new Label("");
        footerText.setSizeUndefined();
        footer.addComponents(footerText); //, cancel);
        footer.setExpandRatio(footerText, 1);

        for (Component c : cs) footer.addComponents(c); //, cancel);

        v.addComponent(footer);

        Panel panel = new Panel();
        //panel.addStyleName("mypanelexample");
        //panel.setSizeUndefined(); // Shrink to fit content
        panel.setWidth("100%");
        //panel.setSizeFull();

        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

        //panel.addStyleName(ValoTheme.PANEL_WELL);
        v.setWidth("100%");
        panel.setContent(v);
        panel.addStyleName("escrolado");

        VerticalLayout l = new VerticalLayout();
        l.addComponent(panel);
        l.setExpandRatio(panel, 1.0f);
        l.addComponent(footer);
        l.setSizeFull();

        setContent(l);
    }
}
