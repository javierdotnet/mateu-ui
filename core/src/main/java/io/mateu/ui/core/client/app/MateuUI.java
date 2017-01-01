package io.mateu.ui.core.client.app;

import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.AbstractView;

import java.util.List;

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

    public static void runInUIThread(Runnable runnable) {
        getClientSideHelper().runInUIThread(runnable);
    }

    public static BaseServiceAsync getBaseService() {
        return getClientSideHelper().getBaseService();
    }

    public static String extractIds(List<Data> selection) {
        String ids = "";
        for (Data x : selection) {
            if (!"".equals(ids)) ids += ",";
            Object id = x.get("_id");
            if (id == null) id = x.get("id");
            if (id != null && id instanceof String) {
                ids += "'" + ((String)id).replaceAll("'", "''") + "'";
            } else {
                ids += id;
            }
        }
        return ids;
    }
}
