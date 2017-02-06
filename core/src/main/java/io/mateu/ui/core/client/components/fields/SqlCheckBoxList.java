package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 3/1/17.
 */
public class SqlCheckBoxList extends AbstractField<SqlCheckBoxList> {
    private String sql;

    public SqlCheckBoxList(String id) {
        super(id);
    }

    public SqlCheckBoxList(String id, String label) {
        super(id, label);
    }

    public SqlCheckBoxList(String id, String label, String sql) {
        super(id, label); this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public SqlCheckBoxList setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(getSql(),callback);
    }

}
