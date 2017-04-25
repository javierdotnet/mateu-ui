package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 22/4/17.
 */
public abstract class AbstractWizard {

    public enum Actions {
        GONEXT, GOBACK, END
    }

    public abstract Data getData();

    public abstract AbstractWizardPageView execute(Object action, Data data) throws Throwable;

    public abstract String getTitle();

    public Data initializeData() {
        return new Data();
    }
}
