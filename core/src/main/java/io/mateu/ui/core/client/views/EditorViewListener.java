package io.mateu.ui.core.client.views;

/**
 * Created by miguel on 1/1/17.
 */
public interface EditorViewListener {

    public void onLoad();

    public void onSave();

    public void onSuccess();

    public void onFailure(Throwable caught);

}
