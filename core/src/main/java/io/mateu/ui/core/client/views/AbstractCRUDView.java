package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 31/12/16.
 */
public abstract class AbstractCRUDView extends AbstractSqlListView {

    private List<CRUDListener> listeners = new ArrayList<>();

    public abstract AbstractEditorView getNewEditorView() throws Throwable;

    public boolean canCreate() {
        return true;
    }

    public boolean canDelete() {
        return true;
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        if (canCreate()) as.add(new AbstractAction("New") {
            @Override
            public void run() {
               openNew(false);
            }
        });
        if (canDelete()) as.add(new AbstractAction("Delete") {
            @Override
            public void run() {
                if (getSelection().size() == 0) MateuUI.alert("No rows selected");
                else {
                    MateuUI.confirm("Are you sure you want to delete them?", new Runnable() {
                        @Override
                        public void run() {
                            delete(getSelection(), new Callback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    search();
                                }
                            });
                        }
                    });
                }
            }
        });
        return as;
    }

    public void openNew(boolean inNewTab) {
        try {
            openEditor(getNewEditorView(), inNewTab);
        } catch (Throwable e) {
            MateuUI.notifyError(e.getMessage());
        }
    }

    @Override
    public List<AbstractColumn> createColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        if (isIdColumnNeeded()) cols.add(new LinkColumn("_id", "Id", 100) {
            @Override
            public void run(Data data) {
                open(getId(), data, isModifierPressed());
            }
        });
        cols.addAll(createExtraColumns());
        return cols;
    }

    public void open(String propertyId, Data data, boolean inNewTab) {
        try {
            AbstractEditorView ev = getNewEditorView().setInitialId(data.get(propertyId));
            ev.setListFragment((String) get("_fragment"));
            ev.setListQl(getSql());
            ev.setListPage((Integer) get("_data_currentpageindex"));
            ev.setListRowsPerPage(getRowsPerPage());
            ev.setListPos(data.getInt("_pos"));
            ev.setListCount((Integer) get("_data_totalrows"));
            openEditor(ev, inNewTab);
        } catch (Throwable e) {
            MateuUI.notifyError(e.getMessage());
        }
    }

    /*
    public void open(Object id, boolean inNewTab, int posInGrid) {
        try {
            AbstractEditorView ev = getNewEditorView();
            ev.setInitialId(id);
            ev.setListQl(getSql());
            ev.setListPage((Integer) get("_data_currentpageindex"));
            ev.setListPos(((Integer) get("_data_currentpageindex")) * getRowsPerPage() + posInGrid);
            ev.setListCount((Integer) get("_data_totalrows"));
            openEditor(ev, inNewTab);
        } catch (Throwable e) {
            MateuUI.notifyError(e.getMessage());
        }
    }
    */

    public abstract List<AbstractColumn> createExtraColumns();

    public abstract void delete(List<Data> selection, AsyncCallback<Void> callback);

    public void openEditor(AbstractEditorView e, boolean inNewTab) {
        e.addEditorViewListener(new EditorViewListener() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onSave() {
            }

            @Override
            public void onSuccessLoad(Data result) {

            }

            @Override
            public void onSuccessSave(Data result) {
                try {
                    search();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable caught) {

            }
        });
        for (CRUDListener l : listeners) l.openEditor(e, inNewTab);
    }

    public AbstractCRUDView addListener(CRUDListener l) {
        listeners.add(l);
        return this;
    }
}
