package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractMenu;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.MenuField;

public class AreaView extends BaseView {
    private final String areaId;

    public AreaView(String title, String areaId) {
        super(title);
        this.areaId = areaId;
    }

    @Override
    public String getViewId() {
        return "area.." + areaId;
    }

    @Override
    public void build() {
        super.build();
    }
}
