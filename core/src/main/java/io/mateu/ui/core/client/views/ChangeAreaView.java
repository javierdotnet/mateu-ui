package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractMenu;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.ChangeAreaField;
import io.mateu.ui.core.client.components.fields.MenuField;

public class ChangeAreaView extends BaseView {

    private final AbstractArea areaActual;

    public ChangeAreaView(String title, AbstractArea areaActual) {
        super(title);
        this.areaActual = areaActual;
    }

    @Override
    public String getViewId() {
        return "changearea";
    }

    @Override
    public void build() {
        super.build();

        add(new ChangeAreaField("_areas"));

    }

    public AbstractArea getAreaActual() {
        return areaActual;
    }
}
