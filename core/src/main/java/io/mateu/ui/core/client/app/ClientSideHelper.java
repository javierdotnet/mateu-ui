package io.mateu.ui.core.client.app;

import io.mateu.ui.*;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.views.AbstractWizard;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.AbstractView;

import java.net.URL;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public interface ClientSideHelper {
    public void openView(AbstractView abstractView, boolean inNewTab);
    public Data getNewDataContainer();

    public <T> T create(java.lang.Class<?> serviceInterface);
    public void alert(String msg);

    void run(Runnable runnable);

    void runInUIThread(Runnable runnable);

    BaseServiceAsync getBaseService();

    void run(Runnable runnable, Runnable onerror);

    void openView(AbstractView parentView, AbstractView view);

    void notifyErrors(List<String> msgs);

    AbstractApplication getApp();

    void notifyError(String msg);

    void notifyInfo(String msg);

    void notifyDone(String msg);

    void open(URL url, boolean inNewTab);

    void confirm(String text, Runnable onOk);

    void open(AbstractWizard wizard, boolean inNewTab);

    void openViewInDialog(AbstractView view);
}
