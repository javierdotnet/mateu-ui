package io.mateu.ui.core.app;

import io.mateu.ui.core.data.DataContainer;
import io.mateu.ui.core.views.AbstractView;

/**
 * Created by miguel on 9/8/16.
 */
public interface ClientSideHelper {
    public void openView(AbstractView abstractView);
    public DataContainer getNewDataContainer();

    public <T> T create(java.lang.Class<?> serviceInterface);
    public void alert(String msg);
}
