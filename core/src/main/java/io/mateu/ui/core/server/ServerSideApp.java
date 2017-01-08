package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.BaseService;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by miguel on 1/1/17.
 */
public interface ServerSideApp {

    public BaseService getBaseServiceImpl();

    public Object[][] select(String sql) throws Exception;

    public void execute(String sql) throws Exception;

    public Object selectSingleValue(String sql) throws Exception;

    public void update(String sql) throws Exception;

    public int getNumberOfRows(String sql);

    public Object[][] selectPage(String sql, int desdeFila, int numeroFilas) throws Exception;

    public void runSqlBatch(String b) throws Exception;

    public void transact(SQLTransaction t) throws Exception;

    public FileLocator upload(byte[] bytes);

    public void notransact(SQLTransaction t) throws Exception;

    public Data getFromSql(String sql) throws Exception;

    public void setToSql(Data data, String tableName) throws Exception;
}


