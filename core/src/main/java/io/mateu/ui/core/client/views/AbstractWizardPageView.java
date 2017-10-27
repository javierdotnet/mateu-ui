package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/4/17.
 */
public abstract class AbstractWizardPageView extends AbstractView {

    private AbstractWizard wizard;
    private Data initialData;

    public AbstractWizardPageView(AbstractWizard wizard) {
        this.wizard = wizard;
    }


    @Override
    public Data initializeData() {
        return initialData;
    }

    public void setInitialData(Data initialData) {
        this.initialData = initialData;
    }

    public abstract boolean isFirstPage();

    public abstract boolean isLastPage();

    public void set(String k, Object v) {
        getWizard().getForm().set(k, v);
    }

    public Data getData() {
        return getWizard().getForm().getData();
    }

    public void setData(Data data, boolean only_) {
        getWizard().getForm().setData(data, only_);
    }

    public void setData(Data data) {
        getWizard().getForm().setData(data);
    }

    public void setAll(Data data) {
        getWizard().getForm().setAll(data);
    }


    public AbstractWizard getWizard() {
        return wizard;
    }

    public void setWizard(AbstractWizard wizard) {
        this.wizard = wizard;
    }
}
