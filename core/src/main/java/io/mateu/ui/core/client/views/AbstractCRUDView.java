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

    public abstract AbstractEditorView getNewEditorView();

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("New") {
            @Override
            public void run() {
               openEditor(getNewEditorView());
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

    @Override
    public List<AbstractColumn> createColumns() {
        List<AbstractColumn> cols = new ArrayList<>();
        cols.add(new LinkColumn("_id", "Id", 100) {
            @Override
            public void run(Data data) {
                openEditor(getNewEditorView().setInitialId(data.get(getId())));
            }
        });
        cols.addAll(createExtraColumns());
        return cols;
    }

    public abstract List<AbstractColumn> createExtraColumns();

    public abstract void delete(List<Data> selection, AsyncCallback<Void> callback);

    public void openEditor(AbstractEditorView e) {
        for (CRUDListener l : listeners) l.openEditor(e);
    }

    public AbstractCRUDView addListener(CRUDListener l) {
        listeners.add(l);
        return this;
    }
}
