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

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("New") {
            @Override
            public void run() {
               openNew();
            }
        });
        as.add(new AbstractAction("Delete") {
            @Override
            public void run() {
                if (getSelection().size() == 0) MateuUI.alert("No rows selected");
                else {
                    delete(getSelection(), new Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            search();
                        }
                    });
                }
            }
        });
        return as;
    }

    public void openNew() {
        try {
            openEditor(getNewEditorView());
        } catch (Throwable e) {
            MateuUI.notifyError(e.getMessage());
        }
    }

    @Override
    public List<AbstractColumn> createColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        cols.add(new LinkColumn("_id", "Id", 100) {
            @Override
            public void run(Data data) {
                open(getId(), data);
            }
        });
        cols.addAll(createExtraColumns());
        return cols;
    }

    public void open(String propertyId, Data data) {
        try {
            openEditor(getNewEditorView().setInitialId(data.get(propertyId)));
        } catch (Throwable e) {
            MateuUI.notifyError(e.getMessage());
        }
    }

    public abstract List<AbstractColumn> createExtraColumns();

    public abstract void delete(List<Data> selection, AsyncCallback<Void> callback);

    public void openEditor(AbstractEditorView e) {
        e.addEditorViewListener(new EditorViewListener() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onSave() {
            }

            @Override
            public void onSuccess(Data result) {
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
        for (CRUDListener l : listeners) l.openEditor(e);
    }

    public AbstractCRUDView addListener(CRUDListener l) {
        listeners.add(l);
        return this;
    }
}
