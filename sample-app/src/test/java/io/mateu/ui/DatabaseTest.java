package io.mateu.ui;

import io.mateu.ui.core.server.SQLTransaction;
import io.mateu.ui.core.server.ServerSideHelper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;

/**
 * Created by miguel on 1/1/17.
 */
public class DatabaseTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DatabaseTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DatabaseTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testDatabasePopulation() throws Exception {

        ServerSideHelper.getServerSideApp().transact(new SQLTransaction() {
            @Override
            public void run(Connection conn) throws Exception {

            }
        });

        assertTrue( true );
    }
}
