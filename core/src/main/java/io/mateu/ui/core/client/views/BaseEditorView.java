package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 1/1/17.
 */
public abstract class BaseEditorView extends AbstractEditorView {

    public abstract String getServerSideControllerKey();

    @Override
    public void save(Data data, AsyncCallback<Data> callback) {
        MateuUI.getBaseService().set(getServerSideControllerKey(), data, callback);
    }

    @Override
    public void load(Object id, AsyncCallback<Data> callback) {
        MateuUI.getBaseService().get(getServerSideControllerKey(), id, callback);
    }
}
