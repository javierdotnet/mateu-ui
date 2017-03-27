package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractView {

    private AbstractForm form;
    private List<AbstractAction> actions;
    private List<ViewListener> listeners = new ArrayList<>();

    public abstract String getTitle();

    public abstract AbstractForm createForm();

    public AbstractForm getForm() {
        if (form == null) form = createForm();
        return form;
    }

    public void close() {
        for (ViewListener l : listeners) l.onClose();
    }

    public void addListener(ViewListener listener) {
        listeners.add(listener);
    }

    public List<AbstractAction> createActions() {
        return new ArrayList<>();
    }

    public List<AbstractAction> getActions() {
        if (actions == null) actions = createActions();
        return actions;
    }


    public String getViewId() {
        return getClass().getCanonicalName();
    }

    public Data initializeData() {
        return new Data();
    }
}
