package io.mateu.ui.sample.server;

import io.mateu.ui.core.server.ServerSideEditorViewController;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 1/1/17.
 */
public class CrimesCRUDController extends ServerSideEditorViewController {
    @Override
    public Data get(Object id) throws Throwable {
        return ServerSideHelper.getServerSideApp().getFromSql("select * from crimes where id = " + id);
    }

    @Override
    public Object set(Data data) throws Throwable {
        ServerSideHelper.getServerSideApp().setToSql(data, "crimes");

        Object id = data.get("_id");

        if (id == null) {
            id = ServerSideHelper.getServerSideApp().selectSingleValue("select max(id) from crimes");
        }

        return id;
    }

    @Override
    public String getKey() {
        return "crimescrud";
    }
}
