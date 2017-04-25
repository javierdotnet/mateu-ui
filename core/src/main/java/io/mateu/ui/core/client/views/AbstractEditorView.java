package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
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
        actions.add(new AbstractAction("Refresh") {
            @Override
            public void run() {
                load();
            }
        });
        actions.add(new AbstractAction("Reset") {
            @Override
            public void run() {
                getForm().setData(initializeData());
            }
        });
        actions.add(new AbstractAction("Save") {
            @Override
            public void run() {
                save();
            }
        });
        actions.add(new AbstractAction("Save and close") {
            @Override
            public void run() {
                saveAndClose();
            }
        });
        actions.add(new AbstractAction("Save and duplicate") {
            @Override
            public void run() {
                saveAndDuplicate();
            }
        });
        actions.add(new AbstractAction("Save and reset") {
            @Override
            public void run() {
                saveAndReset();
            }
        });
        actions.add(new AbstractAction("Duplicate") {
            @Override
            public void run() {
                getForm().resetIds();
                MateuUI.notifyDone("Note that on saving you will create a new " + getTitle());
            }
        });
        actions.add(new AbstractAction("Close") {
            @Override
            public void run() {
                close();
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

    public AbstractEditorView addEditorViewListener(EditorViewListener listener) {
        editorViewListeners.add(listener);
        return this;
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
                getForm().setData(result);
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess(result);
            }
        });
    }

    private Object getId() {
        if (getForm().getData().get("_id") != null) return getForm().getData().get("_id");
        else return getInitialId();
    }

    public void saveAndClose() {
        save(new Callback<Data>() {

            @Override
            public void onFailure(Throwable caught) {
                for (EditorViewListener l : getEditorViewListeners()) l.onFailure(caught);
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Data result) {
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess(result);
                getForm().setData(result);
                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
                MateuUI.notifyDone("Saved!");
            }
        });
    }

    public void saveAndDuplicate() {
        save(new Callback<Data>() {

            @Override
            public void onFailure(Throwable caught) {
                for (EditorViewListener l : getEditorViewListeners()) l.onFailure(caught);
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Data result) {
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess(result);
                getForm().setData(result);
                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        getForm().set("_id", null);
                        MateuUI.notifyDone("Note that on saving you will create a new " + getTitle());
                    }
                });
            }
        });
    }

    public void saveAndReset() {
        save(new Callback<Data>() {

            @Override
            public void onFailure(Throwable caught) {
                for (EditorViewListener l : getEditorViewListeners()) l.onFailure(caught);
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Data result) {
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess(result);
                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        getForm().setData(initializeData());
                    }
                });
                MateuUI.notifyDone("Saved!");
            }
        });
    }

    public void save() {
        save(new Callback<Data>() {

            @Override
            public void onFailure(Throwable caught) {
                for (EditorViewListener l : getEditorViewListeners()) l.onFailure(caught);
                super.onFailure(caught);
            }

            @Override
            public void onSuccess(Data result) {
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccess(result);
                getForm().setData(result);
                MateuUI.notifyDone("Saved!");
            }
        });
    }

    public void save(Callback<Data> callback) {
        List<String> errors = getForm().validate();
        if (errors.size() > 0) {
            MateuUI.notifyErrors(errors);
        } else {
            for (EditorViewListener l : getEditorViewListeners()) l.onSave();
            Data d = getForm().getData();
            if (MateuUI.getApp().getUserData() != null) d.set("_user", MateuUI.getApp().getUserData().getLogin());
            save(d, callback);
        }
    }
}
