package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 23/4/17.
 */
public abstract class BaseWizard extends AbstractWizard {

    private Data data = new Data();
    private List<BaseWizardPageView> pages = new ArrayList<>();
    private BaseWizardPageView currentPage;
    private final String title;

    public BaseWizard(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void build() {
        for (AbstractWizardPageView page : getPages()) {
            page.build();
        }
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public AbstractWizardPageView execute(Object action, Data data) throws Throwable {
        if (getPages().size() == 0) throw new Throwable("This wizard has no pages");
        this.data.setAll(data);
        if (currentPage == null) currentPage = getPages().get(0);
        else {
            int pos = getPages().indexOf(currentPage);
            if (action instanceof  Actions) {
                Actions a = (Actions) action;
                switch (a) {
                    case GONEXT:
                        if (pos < getPages().size() - 1) {
                            currentPage = getPages().get(pos + 1);
                        } else {
                            throw new Throwable("No more pages");
                        }
                        break;
                    case GOBACK:
                        if (pos > 0) {
                            currentPage = getPages().get(pos - 1);
                        } else {
                            throw new Throwable("Already first page");
                        }
                        break;
                    case END:
                        close();
                        break;
                    default:
                        throw new Throwable("Unknown action");
                }
            } else throw new Throwable("Unknown action");
        }
        return currentPage;
    }

    public List<BaseWizardPageView> getPages() {
        return pages;
    }

    public BaseWizard setPages(List<BaseWizardPageView> pages) {
        for (BaseWizardPageView p : pages) {
            p.setWizard(this);
        }
        this.pages = pages;
        return this;
    }

    public BaseWizardPageView getCurrentPage() {
        return currentPage;
    }

    public BaseWizard addPage(BaseWizardPageView page) {
        page.setWizard(this);
        getPages().add(page);
        return this;
    }

    public abstract void onOk() throws Exception;

}
