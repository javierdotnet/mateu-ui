package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 3/1/17.
 */
public class SqlRadioButtonField extends AbstractField<SqlRadioButtonField> {
    private String sql;

    public SqlRadioButtonField(String id) {
        super(id);
    }

    public SqlRadioButtonField(String id, String label) {
        super(id, label);
    }

    public SqlRadioButtonField(String id, String label, String sql) {
        super(id, label); this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public SqlRadioButtonField setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(getSql(),callback);
    }

}
