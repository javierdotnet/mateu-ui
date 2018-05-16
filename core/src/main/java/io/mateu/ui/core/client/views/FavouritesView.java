package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.FavouritesField;
import io.mateu.ui.core.client.components.fields.SearchInAppField;

public class FavouritesView extends BaseView {

    public FavouritesView() {
        super("" + ((MateuUI.getApp().getUserData() != null)?MateuUI.getApp().getUserData().getName():"---") + " favourites");
    }

    @Override
    public String getViewId() {
        return "fav";
    }

    @Override
    public void build() {
        super.build();

        add(new FavouritesField("_fav"));
    }
}
