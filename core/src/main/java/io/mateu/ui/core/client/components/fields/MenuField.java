package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.AbstractMenu;

/**
 * Created by miguel on 9/8/16.
 */
public class MenuField extends AbstractField<MenuField> {

    private final AbstractMenu menu;

    public MenuField(String id, AbstractMenu menu) {
        super(id);
        this.menu = menu;
    }

    public AbstractMenu getMenu() {
        return menu;
    }
}
