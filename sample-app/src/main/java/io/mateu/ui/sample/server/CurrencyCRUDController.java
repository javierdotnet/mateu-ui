package io.mateu.ui.sample.server;

import io.mateu.ui.core.server.ServerSideEditorViewController;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 1/1/17.
 */
public class CurrencyCRUDController extends ServerSideEditorViewController {
    @Override
    public Data get(Object id) throws Exception {
        return ServerSideHelper.getServerSideApp().getFromSql("select * from currency where id = '" + id + "'");
    }

    @Override
    public Object set(Data data) throws Exception {
        ServerSideHelper.getServerSideApp().setToSql(data, "currency");

        Object id = data.get("_id");

        if (id == null) {
            id = ServerSideHelper.getServerSideApp().selectSingleValue("select max(id) from currency");
        }

        return id;
    }

    @Override
    public String getKey() {
        return "currencycrud";
    }
}
