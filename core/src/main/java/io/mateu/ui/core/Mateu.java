package io.mateu.ui.core;

import io.mateu.ui.core.communication.Service;
import io.mateu.ui.core.data.DataContainer;
import io.mateu.ui.core.data.Pair;

/**
 * Created by miguel on 23/10/16.
 */
public class Mateu {

    static MateuHelper h;

    public static MateuHelper getHelper() {
        return h;
    }

    public static void setHelper(MateuHelper helper) {
        h = helper;
    }

    public static DataContainer createNewDataContainer() {
        return h.createNewDataContainer();
    }

    public static Pair createNewPair() {
        return h.createNewPair();
    }


}
