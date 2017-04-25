package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/4/17.
 */
public abstract class AbstractWizardPageView extends AbstractView {

    private Data initialData;

    @Override
    public Data initializeData() {
        return initialData;
    }

    public void setInitialData(Data initialData) {
        this.initialData = initialData;
    }

    public abstract boolean isFirstPage();

    public abstract boolean isLastPage();

}
