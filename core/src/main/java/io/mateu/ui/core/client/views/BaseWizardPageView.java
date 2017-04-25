package io.mateu.ui.core.client.views;

/**
 * Created by miguel on 23/4/17.
 */
public abstract class BaseWizardPageView extends AbstractWizardPageView {

    private BaseWizard wizard;

    @Override
    public boolean isFirstPage() {
        return getWizard().getPages().indexOf(this) == 0;
    }

    @Override
    public boolean isLastPage() {
        return getWizard().getPages().indexOf(this) == getWizard().getPages().size() - 1;
    }

    public BaseWizard getWizard() {
        return wizard;
    }

    public void setWizard(BaseWizard wizard) {
        this.wizard = wizard;
    }
}
