package io.mateu.ui.core.app;

import io.mateu.ui.core.data.DataContainer;

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

    public static DataContainer getNewDataContainer() {
        return getClientSideHelper().getNewDataContainer();
    }

    public static <T> T create(java.lang.Class<?> serviceInterface) {
        return getClientSideHelper().create(serviceInterface);
    }
    public static void alert(String msg) {
        getClientSideHelper().alert(msg);
    }


}
