package io.mateu.ui.core.client.app;

import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.AbstractView;

/**
 * Created by miguel on 9/8/16.
 */
public interface ClientSideHelper {
    public void openView(AbstractView abstractView);
    public Data getNewDataContainer();

    public <T> T create(java.lang.Class<?> serviceInterface);
    public void alert(String msg);

    void run(Runnable runnable);

    void runInUIThread(Runnable runnable);

    BaseServiceAsync getBaseService();
}
