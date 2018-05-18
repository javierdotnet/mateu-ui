package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 21/10/16.
 */
public abstract class AbstractSqlListView extends AbstractListView {

    public abstract String getSql();

    @Override
    public void rpc(Data parameters, AsyncCallback<Data> callback) {
        parameters.set("_sql", getSql());
        parameters.set("_rowsperpage", getRowsPerPage());
        parameters.set("_sums", getSums());
        MateuUI.getBaseService().selectPaginated(parameters, callback);
    }

    protected int getRowsPerPage() {
        return 100;
    }

}
