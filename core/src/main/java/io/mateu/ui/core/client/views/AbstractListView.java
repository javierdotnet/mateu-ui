package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.shared.Data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 21/10/16.
 */
public abstract class AbstractListView extends AbstractView implements ListView {

    private List<AbstractColumn> columns;

    public abstract List<AbstractColumn> createColumns();

    public boolean useAutoColumnIds() {
        return true;
    }

    private List<ListViewListener> listViewListeners = new ArrayList<>();

    public Data getMetadata() {
        return null;
    }

    public boolean isSearchOnOpen() {
        return true;
    }

    public boolean isExcelEnabled() {
        return true;
    }

    public boolean isPdfEnabled() {
        return true;
    }

    public boolean isIdColumnNeeded() {
        return true;
    }

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

        Object _this = this;

        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("Search") {

            @Override
            public void run() {

                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {

                        reset();
                        search();

                    }
                });

/*
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
                */
            }
        });

        if (isExcelEnabled()) as.add(new AbstractAction("Excel") {
            @Override
            public void run() {
                Data p = getForm().getData();
                p.set("_format", "excel");
                p.set("_listview", (_this.getClass().isAnonymousClass())?_this.getClass().getSuperclass().getName():_this.getClass().getName());
                p.set("_baseurl", MateuUI.getApp().getBaseUrl());
                if (getMetadata() != null) p.set("_metadata", getMetadata());
                MateuUI.getBaseService().dump(p, new Callback<URL>() {
                    @Override
                    public void onSuccess(URL result) {
                        MateuUI.open(result);
                    }
                });
            }
        });
        if (isPdfEnabled()) as.add(new AbstractAction("Pdf") {
            @Override
            public void run() {
                Data p = getForm().getData();
                p.set("_format", "pdf");
                p.set("_listview", (_this.getClass().isAnonymousClass())?_this.getClass().getSuperclass().getName():_this.getClass().getName());
                p.set("_baseurl", MateuUI.getApp().getBaseUrl());
                if (getMetadata() != null) p.set("_metadata", getMetadata());
                MateuUI.getBaseService().dump(p, new Callback<URL>() {
                    @Override
                    public void onSuccess(URL result) {
                        MateuUI.open(result);
                    }
                });
            }
        });

        as.add(new AbstractAction("Close") {
            @Override
            public void run() {
                close();
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
        List<String> errors = getForm().validate();
        if (errors.size() > 0) {
            MateuUI.notifyErrors(errors);
        } else {
            for (ListViewListener l : listViewListeners) l.onSearch();
        }
    }

    @Override
    public void rpc() {
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
