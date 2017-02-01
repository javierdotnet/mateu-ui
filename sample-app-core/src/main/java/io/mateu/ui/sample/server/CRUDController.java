package io.mateu.ui.sample.server;

import io.mateu.ui.core.server.ServerSideEditorViewController;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 1/1/17.
 */
public class CRUDController extends ServerSideEditorViewController {
    @Override
    public Data get(Object id) throws Exception {
        return ServerSideHelper.getServerSideApp().getFromSql("select * from customer where id = " + id);
    }

    @Override
    public Object set(Data data) throws Exception {
        ServerSideHelper.getServerSideApp().setToSql(data, "customer");

        Object id = data.get("_id");

        if (id == null) {
            id = ServerSideHelper.getServerSideApp().selectSingleValue("select max(id) from customer");
        }

        return id;
    }

    @Override
    public String getKey() {
        return "crud";
    }
}
