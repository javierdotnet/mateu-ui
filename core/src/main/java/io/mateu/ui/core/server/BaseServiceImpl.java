package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.FileLocator;

/**
 * Created by miguel on 27/12/16.
 */
public class BaseServiceImpl implements BaseService {
    @Override
    public Object[][] select(String sql) throws Exception {
        return ServerSideHelper.getServerSideApp().select(sql);
    }

    @Override
    public Object selectSingleValue(String sql) throws Exception {
        return ServerSideHelper.getServerSideApp().selectSingleValue(sql);
    }

    @Override
    public void execute(String sql) throws Exception {
        ServerSideHelper.getServerSideApp().execute(sql);
    }

    @Override
    public Data selectPaginated(Data parameters) throws Exception {



        Data d = new Data();

        int rowsPerPage = parameters.getInt("_rowsperpage");
        int fromRow = rowsPerPage * parameters.getInt("_currentpageindex");
        String sql = parameters.getString("_sql");

        for (Object[] l : ServerSideHelper.getServerSideApp().selectPage(sql, fromRow, rowsPerPage)) {
            Data r;
            d.getList("_data").add(r = new Data());
            if (l != null) for (int i = 0; i < l.length; i++) {
                r.set((i == 0)?"id":"col" + i, l[i]);
            }
        }

        int numRows = ServerSideHelper.getServerSideApp().getNumberOfRows(sql);
        //d.set("_data_currentpageindex", from);
        d.set("_data_pagecount", numRows / rowsPerPage + ((numRows % rowsPerPage == 0)?0:1));

        return d;
    }

    @Override
    public Data set(String serverSideControllerKey, Data data) throws Exception {
        Object id = EditorViewControllerRegistry.getController(serverSideControllerKey).set(data);
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public Data get(String serverSideControllerKey, Object id) throws Exception {
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public FileLocator upload(byte[] bytes) throws Exception {
        return ServerSideHelper.getServerSideApp().upload(bytes);
    }
}
