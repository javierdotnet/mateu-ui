package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;

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

        d.getList("_data");

        for (Object[] l : ServerSideHelper.getServerSideApp().selectPage(sql, fromRow, rowsPerPage)) {
            Data r;
            d.getList("_data").add(r = new Data());
            if (l != null) for (int i = 0; i < l.length; i++) {
                r.set((i == 0)?"_id":"col" + i, l[i]);
            }
        }

        int numRows = ServerSideHelper.getServerSideApp().getNumberOfRows(sql);
        //d.set("_data_currentpageindex", from);
        d.set("_data_pagecount", numRows / rowsPerPage + ((numRows % rowsPerPage == 0)?0:1));

        return d;
    }

    @Override
    public Data set(String serverSideControllerKey, Data data) throws Exception {
        System.out.println("set(" + serverSideControllerKey + "," + data + ")");
        Object id = EditorViewControllerRegistry.getController(serverSideControllerKey).set(data);
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public Data get(String serverSideControllerKey, Object id) throws Exception {
        System.out.println("get(" + serverSideControllerKey + "," + id + ")");
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public FileLocator upload(String fileName, byte[] bytes, boolean temporary) throws Exception {
        return ServerSideHelper.getServerSideApp().upload(fileName, bytes, temporary);
    }

    @Override
    public UserData authenticate(String login, String password) throws Exception {
        return ServerSideHelper.getServerSideApp().authenticate(login, password);
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword) throws Exception {
        ServerSideHelper.getServerSideApp().changePassword(login, oldPassword, newPassword);
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto) throws Exception {
        ServerSideHelper.getServerSideApp().updateProfile(login, name, email, foto);
    }

}
