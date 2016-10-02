package io.mateu.ui.core.app;

/**
 * Created by miguel on 9/8/16.
 */
public class MateuUI {

    private static MateuUI me;
    private static ClientSideHelper clientSideHelper;


    public static MateuUI get() {
        return me;
    }

    public static ClientSideHelper getClientSideHelper() {
        return clientSideHelper;
    }

    public static void setClientSideHelper(ClientSideHelper clientSideHelper) {
        MateuUI.clientSideHelper = clientSideHelper;
    }
}
