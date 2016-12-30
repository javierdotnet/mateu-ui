package io.mateu.ui.core.app;

/**
 * Created by miguel on 30/12/16.
 */
public class Callback<T> implements AsyncCallback<T> {
    @Override
    public void onFailure(Throwable caught) {
        MateuUI.alert("ERROR: " + caught.getClass().getName() + ((caught.getMessage() != null)?":" + caught.getMessage():""));
    }

    @Override
    public void onSuccess(T result) {
        MateuUI.alert("Done!");
    }
}
