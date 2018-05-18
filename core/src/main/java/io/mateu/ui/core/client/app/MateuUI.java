package io.mateu.ui.core.client.app;

import io.mateu.ui.*;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractWizard;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.AbstractView;

import java.net.URL;
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

    public static AbstractApplication getApp() {
        return getClientSideHelper().getApp();
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

    public static void openView(AbstractView view, boolean inNewTab) {
        getClientSideHelper().openView(view, inNewTab);
    }

    public static void openView(AbstractView view) {
        openView(view, false);
    }

    public static void openView(AbstractView parentView, AbstractView view, boolean inNewTab) {
        getClientSideHelper().openView(parentView, view);
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

    public static void run(Runnable runnable, Runnable onerror) {
        getClientSideHelper().run(runnable, onerror);
    }

    public static void notifyErrors(List<String> msgs) {
        getClientSideHelper().notifyErrors(msgs);
    }

    public static void notifyError(String msg) {
        getClientSideHelper().notifyError(msg);
    }

    public static void notifyInfo(String msg) {
        getClientSideHelper().notifyInfo(msg);
    }

    public static void notifyDone(String msg) {
        getClientSideHelper().notifyDone(msg);
    }

    public static void open(URL url) { getClientSideHelper().open(url, false); }

    public static void open(URL url, boolean inNewTab) { getClientSideHelper().open(url, inNewTab); }

    public static void open(AbstractWizard wizard, boolean inNewTab) { getClientSideHelper().open(wizard, inNewTab); }

    public static boolean hasPermission(int permissionId) {
        boolean ok = false;
        if (getApp().getUserData() != null) for (Integer p : getApp().getUserData().getPermissions()) if (p == permissionId) {
            ok = true;
            break;
        }
        return ok;
    }

    public static void confirm(String text, Runnable onOk) {
        getClientSideHelper().confirm(text, onOk);
    }

    public static void openViewInDialog(AbstractView view) {
        getClientSideHelper().openViewInDialog(view);
    }

    public static String getCurrentFragment() {
        return getClientSideHelper().getCurrentFragment();
    }
}
