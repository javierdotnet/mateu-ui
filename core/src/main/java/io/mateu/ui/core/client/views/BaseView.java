package io.mateu.ui.core.client.views;

/**
 * Created by miguel on 10/6/17.
 */
public class BaseView extends AbstractView {

    private String title;

    public BaseView(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void build() {

    }
}
