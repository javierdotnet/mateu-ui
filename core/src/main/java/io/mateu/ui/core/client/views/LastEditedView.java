package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.FavouritesField;
import io.mateu.ui.core.client.components.fields.LastEditedField;

public class LastEditedView extends BaseView {

    public LastEditedView() {
        super("" + ((MateuUI.getApp().getUserData() != null)?MateuUI.getApp().getUserData().getName():"---") + " last edited records");
    }

    @Override
    public String getViewId() {
        return "lastedited";
    }

    @Override
    public void build() {
        super.build();

        add(new LastEditedField("_lastedited"));
    }
}
