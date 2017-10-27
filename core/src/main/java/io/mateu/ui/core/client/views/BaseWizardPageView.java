package io.mateu.ui.core.client.views;

/**
 * Created by miguel on 23/4/17.
 */
public abstract class BaseWizardPageView extends AbstractWizardPageView {

    private String title;

    public BaseWizardPageView(String title) {
        super(null);
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isFirstPage() {
        return ((BaseWizard)getWizard()).getPages().indexOf(this) == 0;
    }

    @Override
    public boolean isLastPage() {
        return ((BaseWizard)getWizard()).getPages().indexOf(this) == ((BaseWizard)getWizard()).getPages().size() - 1;
    }

}
