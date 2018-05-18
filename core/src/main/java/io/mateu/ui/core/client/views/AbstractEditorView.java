package io.mateu.ui.core.client.views;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
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
    private boolean usedForRefField;

    private String listFragment;
    private String listQl;
    private int listPos;
    private int listCount;
    private int listPage;
    private int listRowsPerPage;

    public String getListFragment() {
        return listFragment;
    }

    public void setListFragment(String listFragment) {
        this.listFragment = listFragment;
    }

    public int getListRowsPerPage() {
        return listRowsPerPage;
    }

    public void setListRowsPerPage(int listRowsPerPage) {
        this.listRowsPerPage = listRowsPerPage;
    }

    public int getListPage() {
        return listPage;
    }

    public void setListPage(int listPage) {
        this.listPage = listPage;
    }

    public String getListQl() {
        return listQl;
    }

    public void setListQl(String listQl) {
        this.listQl = listQl;
    }

    public int getListPos() {
        return listPos;
    }

    public void setListPos(int listPos) {
        this.listPos = listPos;
    }

    public int getListCount() {
        return listCount;
    }

    public void setListCount(int listCount) {
        this.listCount = listCount;
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> actions = new ArrayList<>();

        if (!usedForRefField) actions.add(new AbstractAction("Refresh") {
            @Override
            public void run() {
                load();
            }
        });
        if (!usedForRefField) actions.add(new AbstractAction("Reset") {
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
        if (!usedForRefField) actions.add(new AbstractAction("Save and close") {
            @Override
            public void run() {
                saveAndClose();
            }
        });
        if (!usedForRefField) actions.add(new AbstractAction("Save and duplicate") {
            @Override
            public void run() {
                saveAndDuplicate();
            }
        });
        if (!usedForRefField) actions.add(new AbstractAction("Save and reset") {
            @Override
            public void run() {
                saveAndReset();
            }
        });
        if (!usedForRefField) actions.add(new AbstractAction("Duplicate") {
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
        String id = super.getViewIdBase();

        if (getInitialId() != null) {
            Object iid = getInitialId();
            String s = "" + iid;
            if (iid instanceof String) s = "s" + s;
            else if (iid instanceof Long) s = "l" + s;
            else if (iid instanceof Integer) s = "i" + s;
            id += "/" + s;
        }

        if (!Strings.isNullOrEmpty(getListQl())) {

            id += "?";

            id += "q=" + BaseEncoding.base64().encode(getListQl().getBytes());
            id += "&pos=" + getListPos();
            id += "&count=" + getListCount();
            id += "&rpp=" + getListRowsPerPage();
            id += "&page=" + getListPage();
            if (!Strings.isNullOrEmpty(getListFragment())) id += "&listfragment=" + BaseEncoding.base64().encode(getListFragment().getBytes());

        }

        return id;
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
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccessLoad(result);
            }
        });
    }

    public Object getId() {
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
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccessSave(result);
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
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccessSave(result);
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
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccessSave(result);
                getForm().setData(initializeData());
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
                for (EditorViewListener l : getEditorViewListeners()) l.onSuccessSave(result);
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

    public boolean isUsedForRefField() {
        return usedForRefField;
    }

    public void setUsedForRefField(boolean usedForRefField) {
        this.usedForRefField = usedForRefField;
    }
}
