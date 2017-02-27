package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 21/10/16.
 */
public abstract class AbstractListView extends AbstractView implements ListView {

    private List<AbstractColumn> columns;

    public abstract List<AbstractColumn> createColumns();

    private List<ListViewListener> listViewListeners = new ArrayList<>();


    @Override
    public List<AbstractColumn> getColumns() {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

    @Override
    public int getMaxFieldsInHeader() {
        return 1;
    }

    public abstract void rpc(Data parameters, AsyncCallback<Data> callback);

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("Search") {

            @Override
            public void run() {
                MateuUI.run(new Runnable() {
                    @Override
                    public void run() {
                        reset();
                        search();
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        for (ListViewListener l : listViewListeners) l.onSuccess();
                    }
                });
            }
        });
        return as;
    }

    private void reset() {
        for (ListViewListener l : listViewListeners) l.onReset();
    }

    public void onPageChange() {
        search();
    }

    @Override
    public void search() {
        for (ListViewListener l : listViewListeners) l.onSearch();

                rpc(getForm().getData(), new Callback<Data>() {

                    @Override
                    public void onFailure(Throwable caught) {

                                for (ListViewListener l : listViewListeners) l.onFailure(caught);
                                super.onFailure(caught);

                    }

                    @Override
                    public void onSuccess(Data result) {
                                for (ListViewListener l : listViewListeners) l.onSuccess();
                                getForm().setData(result, true);

                     }
                });
            }

    @Override
    public List<Data> getSelection() {
        return getForm().getData().getSelection("_data");
    }

    @Override
    public void addListViewListener(ListViewListener listener) {
        listViewListeners.add(listener);
    }

}
