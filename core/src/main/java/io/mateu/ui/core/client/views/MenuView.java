package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractMenu;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.MenuField;

public class MenuView extends BaseView {
    private final String menuId;

    public MenuView(String title, String menuId) {
        super(title);
        this.menuId = menuId;
    }

    @Override
    public String getViewId() {
        return "menu.." + menuId;
    }

    @Override
    public void build() {
        super.build();

        AbstractMenu m = (AbstractMenu) MateuUI.getApp().getMenu(menuId);

        add(new MenuField("_menu", m));

    }
}
