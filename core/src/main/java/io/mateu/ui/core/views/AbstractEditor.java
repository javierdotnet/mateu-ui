package io.mateu.ui.core.views;

import io.mateu.ui.core.app.AbstractAction;
import io.mateu.ui.core.app.AsyncCallback;
import io.mateu.ui.core.data.DataContainer;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractEditor extends AbstractView {

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> actions = new ArrayList<>();
        actions.add(new AbstractAction() {
            @Override
            public String getName() {
                return "Save";
            }

            @Override
            public void run() {
                save();
            }
        });
        return actions;
    }

    public void save() {

    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        return errors;
    }

    public abstract void save(DataContainer data, AsyncCallback<DataContainer> callback);

    public abstract void load(String id, AsyncCallback<DataContainer> callback);

}
