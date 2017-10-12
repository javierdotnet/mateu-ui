package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 1/1/17.
 */
public interface EditorViewListener {

    public void onLoad();

    public void onSave();

    public void onSuccessLoad(Data result);

    public void onSuccessSave(Data result);

    public void onFailure(Throwable caught);

}
