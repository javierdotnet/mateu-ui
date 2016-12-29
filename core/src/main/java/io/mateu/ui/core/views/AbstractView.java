package io.mateu.ui.core.views;

import io.mateu.ui.core.MateuHelper;
import io.mateu.ui.core.app.AbstractAction;
import io.mateu.ui.core.app.MateuUI;
import io.mateu.ui.core.data.DataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractView {

    private DataContainer data;

    private AbstractForm form;
    private List<AbstractAction> actions;

    public abstract String getTitle();

    public abstract AbstractForm createForm();

    public AbstractForm getForm() {
        if (form == null) form = createForm();
        return form;
    }

    public DataContainer initializeData() {
        return MateuUI.getNewDataContainer();
    }

    public DataContainer getData() {
        if (data == null) data = initializeData();
        return data;
    }

    public List<AbstractAction> createActions() {
        return new ArrayList<>();
    }

    public List<AbstractAction> getActions() {
        if (actions == null) actions = createActions();
        return actions;
    }
}
