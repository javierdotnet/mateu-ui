package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractEditorView extends AbstractView {

    private Object initialId;
    private List<EditorViewListener> editorViewListeners = new ArrayList<>();

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

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        return errors;
    }

    public abstract void save(Data data, AsyncCallback<Data> callback);

    public abstract void load(Object id, AsyncCallback<Data> callback);

    public Object getInitialId() {
        return initialId;
    }

    public AbstractEditorView setInitialId(Object initialId) {
        this.initialId = initialId;
        return this;
    }

    @Override
    public String getViewId() {
        return super.getViewId() + "-" + getInitialId();
    }

    public void addEditorViewListener(EditorViewListener listener) {
        editorViewListeners.add(listener);
    }

    public List<EditorViewListener> getEditorViewListeners() {
        return editorViewListeners;
    }

    public void load() {
        for (EditorViewListener l : getEditorViewListeners()) l.onLoad();
        load(getId(), new Callback<Data>() {

            @Override
            public void onFailure(Throwable caught) {
                for (EditorViewListener l : getEditorViewListeners()) l.onFailure(caught);
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Data result) {
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess();
                getForm().setData(result);
            }
        });
    }

    private Object getId() {
        if (getForm().getData().get("_id") != null) return getForm().getData().get("_id");
        else return getInitialId();
    }

    public void save() {
        for (EditorViewListener l : getEditorViewListeners()) l.onLoad();
        save(getForm().getData(), new Callback<Data>() {

            @Override
            public void onFailure(Throwable caught) {
                for (EditorViewListener l : getEditorViewListeners()) l.onFailure(caught);
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Data result) {
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess();
                getForm().setData(result);
            }
        });
    }
}
