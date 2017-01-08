package io.mateu.ui.core.server;

import java.sql.Connection;

/**
 * Created by miguel on 13/9/16.
 */
public interface SQLTransaction {

    public void run(Connection conn) throws Exception;

}
