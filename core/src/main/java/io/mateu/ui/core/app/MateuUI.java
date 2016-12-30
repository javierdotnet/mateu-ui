package io.mateu.ui.core.app;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.views.AbstractView;

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

    public static Data getNewDataContainer() {
        return getClientSideHelper().getNewDataContainer();
    }

    public static <T> T create(java.lang.Class<?> serviceInterface) {
        return getClientSideHelper().create(serviceInterface);
    }
    public static void alert(String msg) {
        getClientSideHelper().alert(msg);
    }


    public static void run(Runnable runnable) {
        getClientSideHelper().run(runnable);
    }

    public static void openView(AbstractView view) {
        getClientSideHelper().openView(view);
    }
}
