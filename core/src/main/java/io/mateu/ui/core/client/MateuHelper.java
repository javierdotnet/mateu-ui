package io.mateu.ui.core.client;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

/**
 * Created by miguel on 23/10/16.
 */
public interface MateuHelper {

    public Data createNewDataContainer();

    public Pair createNewPair();

    /*
    returns the async version of this service
     */
    public <T> T create(java.lang.Class<?> serviceInterface);

    void alert(String msg);
}
