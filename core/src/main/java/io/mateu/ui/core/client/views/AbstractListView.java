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
public abstract class AbstractListView extends AbstractView {

    private List<AbstractColumn> columns;

    public abstract List<AbstractColumn> createColumns();

    private List<ListViewListener> listViewListeners = new ArrayList<>();


    public List<AbstractColumn> getColumns() {
        if (columns == null) {
            columns = createColumns();
        }
        return columns;
    }

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

    public void search() {
        for (ListViewListener l : listViewListeners) l.onSearch();

                rpc(getForm().getData(), new Callback<Data>() {

                    public void superOnFailure(Throwable caught) {
                        super.onFailure(caught);
                    }

                    @Override
                    public void onFailure(Throwable caught) {

                                for (ListViewListener l : listViewListeners) l.onFailure(caught);
                                superOnFailure(caught);

                    }

                    @Override
                    public void onSuccess(Data result) {
                                for (ListViewListener l : listViewListeners) l.onSuccess();
                                getForm().setData(result, true);

                     }
                });
            }



    public void addListViewListener(ListViewListener listener) {
        listViewListeners.add(listener);
    }

}
