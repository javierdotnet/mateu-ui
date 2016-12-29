package io.mateu.ui.core;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.data.DataContainer;
import io.mateu.ui.core.data.Pair;

/**
 * Created by miguel on 23/10/16.
 */
public interface MateuHelper {

    public DataContainer createNewDataContainer();

    public Pair createNewPair();

    /*
    returns the async version of this service
     */
    public <T> T create(java.lang.Class<?> serviceInterface);

    void alert(String msg);
}
