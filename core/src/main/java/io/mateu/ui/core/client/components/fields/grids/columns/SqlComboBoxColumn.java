package io.mateu.ui.core.client.components.fields.grids.columns;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 3/1/17.
 */
public class SqlComboBoxColumn extends AbstractColumn<SqlComboBoxColumn> {
    private final String sql;

    public SqlComboBoxColumn(String id, String label, int width, String sql) {
        super(id, label, width, true);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(getSql(),callback);
    }

}
