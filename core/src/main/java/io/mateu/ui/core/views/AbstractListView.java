package io.mateu.ui.core.views;

import io.mateu.ui.core.app.AbstractAction;
import io.mateu.ui.core.app.AsyncCallback;
import io.mateu.ui.core.app.Callback;
import io.mateu.ui.core.app.MateuUI;
import io.mateu.ui.core.components.fields.grids.AbstractColumn;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.GridData;

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
        return 4;
    }

    public abstract void rpc(Data parameters, AsyncCallback<Data> callback);

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction() {
            @Override
            public String getName() {
                return "Search";
            }

            @Override
            public void run() {
                reset();
                search();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                rpc(getForm().getData(), new Callback<Data>() {

                    public void superOnFailure(Throwable caught) {
                        super.onFailure(caught);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        MateuUI.run(new Runnable() {
                            @Override
                            public void run() {
                                for (ListViewListener l : listViewListeners) l.onFailure(caught);
                                superOnFailure(caught);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(Data result) {
                        MateuUI.run(new Runnable() {
                            @Override
                            public void run() {
                                for (ListViewListener l : listViewListeners) l.onSuccess();
                                getForm().setData(result);
                            }
                        });
                     }
                });
            }
        }).start();
    }


    public void addListViewListener(ListViewListener listener) {
        listViewListeners.add(listener);
    }

}
