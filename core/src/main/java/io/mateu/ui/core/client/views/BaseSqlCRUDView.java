package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 1/1/17.
 */
public abstract class BaseSqlCRUDView extends AbstractCRUDView {

    public abstract String getTableName();

    @Override
    public List<AbstractColumn> createExtraColumns() {
        return null;
    }

    @Override
    public void delete(List<Data> selection, AsyncCallback<Void> callback) {
        MateuUI.getBaseService().execute("delete from " + getTableName() + " where id in (" + MateuUI.extractIds(selection) + ")", callback);
    }
}
