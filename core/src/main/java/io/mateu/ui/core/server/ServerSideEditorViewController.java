package io.mateu.ui.core.server;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 1/1/17.
 */
public abstract class ServerSideEditorViewController {

    public abstract Data get(Object id) throws Exception;

    public abstract Object set(Data data) throws Exception;

    public abstract String getKey();
}
