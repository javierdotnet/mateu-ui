package io.mateu.ui.core.client.views;

/**
 * Created by miguel on 30/12/16.
 */
public interface ListViewListener {

    public void onReset();

    public void onSearch();

    public void onSuccess();

    public void onFailure(Throwable caught);

}
