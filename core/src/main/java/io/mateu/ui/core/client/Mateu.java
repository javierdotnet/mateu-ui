package io.mateu.ui.core.client;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

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

    public static Data createNewDataContainer() {
        return h.createNewDataContainer();
    }

    public static Pair createNewPair() {
        return h.createNewPair();
    }


}
