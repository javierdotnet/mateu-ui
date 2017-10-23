package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;

import java.net.URL;
import java.util.Date;

/**
 * Created by miguel on 27/12/16.
 */
public class BaseServiceImpl implements BaseService {
    @Override
    public Object[][] select(String sql)throws Throwable {
        return ServerSideHelper.getServerSideApp().select(sql);
    }

    @Override
    public Object selectSingleValue(String sql)throws Throwable {
        return ServerSideHelper.getServerSideApp().selectSingleValue(sql);
    }

    @Override
    public void execute(String sql)throws Throwable {
        ServerSideHelper.getServerSideApp().execute(sql);
    }

    @Override
    public Data selectPaginated(Data parameters)throws Throwable {



        Data d = new Data();

        long t0 = new Date().getTime();

        int rowsPerPage = parameters.getInt("_rowsperpage");
        int fromRow = rowsPerPage * parameters.getInt("_data_currentpageindex");
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
        long t = new Date().getTime() - t0;
        d.set("_subtitle", "" + numRows + " records found in " + t + "ms.");
        d.set("_data_currentpageindex", fromRow / rowsPerPage);
        d.set("_data_totalrows", numRows);
        d.set("_data_pagecount", numRows / rowsPerPage + ((numRows % rowsPerPage == 0)?0:1));

        return d;
    }

    @Override
    public Data set(String serverSideControllerKey, Data data) throws Throwable {
        System.out.println("set(" + serverSideControllerKey + "," + data + ")");
        Object id = EditorViewControllerRegistry.getController(serverSideControllerKey).set(data);
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public Data get(String serverSideControllerKey, long id) throws Throwable {
        System.out.println("get(" + serverSideControllerKey + "," + id + ")");
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public Data get(String serverSideControllerKey, int id) throws Throwable {
        System.out.println("get(" + serverSideControllerKey + "," + id + ")");
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public Data get(String serverSideControllerKey, String id) throws Throwable {
        System.out.println("get(" + serverSideControllerKey + "," + id + ")");
        return EditorViewControllerRegistry.getController(serverSideControllerKey).get(id);
    }

    @Override
    public void forgotPassword(String login) throws Throwable {
        ServerSideHelper.getServerSideApp().forgotPassword(login);
    }

    @Override
    public UserData authenticate(String login, String password)throws Throwable {
        return ServerSideHelper.getServerSideApp().authenticate(login, password);
    }

    @Override
    public void changePassword(String login, String oldPassword, String newPassword)throws Throwable {
        ServerSideHelper.getServerSideApp().changePassword(login, oldPassword, newPassword);
    }

    @Override
    public void updateProfile(String login, String name, String email, FileLocator foto)throws Throwable {
        ServerSideHelper.getServerSideApp().updateProfile(login, name, email, foto);
    }

    @Override
    public void updateFoto(String login, FileLocator foto)throws Throwable {
        ServerSideHelper.getServerSideApp().updateFoto(login, foto);
    }

    @Override
    public URL dump(Data parameters)throws Throwable {
        return ServerSideHelper.getServerSideApp().dump(parameters);
    }

}
