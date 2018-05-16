package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.NavField;

public class NavView extends BaseView {

    public NavView() {
        super("Navigate " + MateuUI.getApp().getName());
    }

    @Override
    public String getViewId() {
        return "nav";
    }

    @Override
    public void build() {
        super.build();

        add(new NavField("_nav"));
    }
}
