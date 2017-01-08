package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 3/1/17.
 */
public class SqlComboBoxField extends AbstractField<SqlComboBoxField> {
    private String sql;

    public SqlComboBoxField(String id) {
        super(id);
    }

    public SqlComboBoxField(String id, String label) {
        super(id, label);
    }

    public SqlComboBoxField(String id, String label, String sql) {
        super(id, label); this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public SqlComboBoxField setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(getSql(),callback);
    }

}
