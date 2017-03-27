package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/1/17.
 */
public class SqlListSelectionField extends AbstractField<SqlListSelectionField> {

    private String sql;

    public SqlListSelectionField(String id) {
        super(id);
    }

    public SqlListSelectionField(String id, String label) {
        super(id, label);
    }

    public SqlListSelectionField(String id, String label, String sql) {
        super(id, label); this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public SqlListSelectionField setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public void call(AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(getSql(),callback);
    }
}
