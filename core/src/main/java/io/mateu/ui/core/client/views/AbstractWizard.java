package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 22/4/17.
 */
public abstract class AbstractWizard extends AbstractView {

    public enum Actions {
        GONEXT, GOBACK, END
    }


    public abstract void execute(Object action, Data data, Callback<AbstractWizardPageView> callback) throws Throwable;

    public abstract String getTitle();

    public Data initializeData() {
        return new Data();
    }

    public abstract void onOk(Data data) throws Throwable;

    public boolean closeOnOk() {
        return true;
    }

}
