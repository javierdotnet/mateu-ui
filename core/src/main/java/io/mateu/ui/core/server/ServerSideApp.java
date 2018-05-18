package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.UserData;

import java.io.IOException;
import java.net.URL;

/**
 * Created by miguel on 1/1/17.
 */
public interface ServerSideApp {

    public BaseService getBaseServiceImpl();

    public Object[][] select(String sql)throws Throwable;

    public void execute(String sql)throws Throwable;

    public Object selectSingleValue(String sql)throws Throwable;

    public void update(String sql)throws Throwable;

    public int getNumberOfRows(String sql);

    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas)throws Throwable;

    public void runSqlBatch(String b)throws Throwable;

    public void transact(SQLTransaction t)throws Throwable;

    public FileLocator upload(String fileName, byte[] bytes, boolean temporary) throws Throwable;

    public void notransact(SQLTransaction t)throws Throwable;

    public Data getFromSql(String sql)throws Throwable;

    public void setToSql(Data data, String tableName)throws Throwable;

    UserData authenticate(String login, String password)throws Throwable;

    void forgotPassword(String login)throws Throwable;

    void changePassword(String login, String oldPassword, String newPassword)throws Throwable;

    void updateProfile(String login, String name, String email, FileLocator foto)throws Throwable;

    UserData signUp(String login, String name, String email, String password) throws Throwable;

    String recoverPassword(String loginOrPassword) throws Throwable;

    URL dump(Data parameters) throws Throwable;

    String getXslfoForListing()throws Throwable;

    void updateFoto(String login, FileLocator foto)throws Throwable;

    Object selectIdAtPos(String listQl, int listPos) throws Throwable;
}


