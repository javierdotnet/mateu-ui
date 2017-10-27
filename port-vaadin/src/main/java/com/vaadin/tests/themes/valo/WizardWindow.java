package com.vaadin.tests.themes.valo;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractWizard;
import io.mateu.ui.core.client.views.AbstractWizardPageView;
import io.mateu.ui.vaadin.ViewLayout;
import io.mateu.ui.vaadin.data.DataStore;

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

        dataStore = new DataStore(wizard.initializeData());

        this.wizard = wizard;

        // Center it in the browser window
        center();

        setModal(true);


        setWidth("800px");
        setHeight("600px");

        try {
            wizard.execute(null, null, new Callback<AbstractWizardPageView>() {
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

        view.setInitialData(dataStore.getData());

        ViewLayout v = new ViewLayout(view);

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
                dataStore.setData(view.getForm().getData());
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
                dataStore.setData(view.getForm().getData());
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
            }));
        } else {
            cs.add(b = new Button("Done", e -> {
                dataStore.setData(view.getForm().getData());
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
            }));
        }
        b.addStyleName(ValoTheme.BUTTON_PRIMARY);
        b.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        Label footerText = new Label("");
        footerText.setSizeUndefined();
        footer.addComponents(footerText); //, cancel);
        footer.setExpandRatio(footerText, 1);

        for (Component c : cs) footer.addComponents(c); //, cancel);

        v.addComponent(footer);

        Panel panel = new Panel();
        //panel.addStyleName("mypanelexample");
        //panel.setSizeUndefined(); // Shrink to fit content
        v.setSizeUndefined();
        panel.setSizeFull();

        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
        panel.setContent(v);

        VerticalLayout l = new VerticalLayout();
        l.addComponent(panel);
        l.setExpandRatio(panel, 1.0f);
        l.addComponent(footer);
        l.setSizeFull();


        setContent(l);
    }
}
