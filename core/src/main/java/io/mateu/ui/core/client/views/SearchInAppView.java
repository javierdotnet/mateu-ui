package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.NavField;
import io.mateu.ui.core.client.components.fields.SearchInAppField;

public class SearchInAppView extends BaseView {

    public SearchInAppView() {
        super("Search in " + MateuUI.getApp().getName());
    }

    @Override
    public String getViewId() {
        return "searchinapp";
    }

    @Override
    public void build() {
        super.build();

        add(new SearchInAppField("_searchinapp"));
    }
}
