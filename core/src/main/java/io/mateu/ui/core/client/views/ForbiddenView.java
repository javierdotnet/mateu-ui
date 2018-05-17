package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.NavField;

public class ForbiddenView extends BaseView {

    public ForbiddenView() {
        super("Forbidden");
        setGranted(false);
    }

    @Override
    public String getViewId() {
        return "forbidden";
    }

    @Override
    public void build() {
        super.build();
    }
}
