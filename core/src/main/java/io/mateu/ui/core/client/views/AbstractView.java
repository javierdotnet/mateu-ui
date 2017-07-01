package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractView {

    private AbstractForm form;
    private List<AbstractAction> actions;
    private List<AbstractAction> links;
    private List<ViewListener> listeners = new ArrayList<>();

    public abstract String getTitle();

    public abstract void build();

    public AbstractView onOpen(UserData user) {
        return this;
    }

    public AbstractForm createForm() {
        return new ViewForm(this);
    }

    public AbstractForm getForm() {
        if (form == null) {
            form = createForm();
            build();
        }
        return form;
    }

    public Data getData() {
        return getForm().getData();
    }

    public AbstractView add(Component component) {
        getForm().add(component);
        return this;
    }

    public void close() {
        for (ViewListener l : listeners) l.onClose();
    }

    public void addListener(ViewListener listener) {
        listeners.add(listener);
    }

    public AbstractView addPropertyListener(String propertyName, ChangeListener listener) {
        getForm().addPropertyListener(propertyName, listener);
        return this;
    }

    public List<AbstractAction> createActions() {
        return new ArrayList<>();
    }

    public List<AbstractAction> getActions() {
        if (actions == null) actions = createActions();
        return actions;
    }

    public List<AbstractAction> createLinks() {
        return new ArrayList<>();
    }

    public List<AbstractAction> getLinks() {
        if (links == null) links = createLinks();
        return links;
    }

    public String getViewId() {
        return getClass().getCanonicalName();
    }

    public Data initializeData() {
        return new Data();
    }
}
