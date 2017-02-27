package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.AsyncCallback;

/**
 * Created by miguel on 20/1/17.
 */
public class SelectByIdField extends TextField {

    private String ql;

    public SelectByIdField(String id, String ql) {
        super(id);
        this.ql = ql;
    }

    public SelectByIdField(String id, String label, String ql) {
        super(id, label);
        this.ql = ql;
    }


    public String getQl() {
        return ql;
    }

    public SelectByIdField setQl(String ql) {
        this.ql = ql;
        return this;
    }

    public void call(String ql, AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(ql,callback);
    }

}
