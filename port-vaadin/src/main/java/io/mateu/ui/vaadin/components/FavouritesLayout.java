package io.mateu.ui.vaadin.components;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

public class FavouritesLayout extends CssLayout {

    public FavouritesLayout() {

        MateuUI.getApp().getFavourites(MateuUI.getApp().getUserData(), new Callback<Data>() {
            @Override
            public void onSuccess(Data result) {

                List<Data> links = result.getList("links");

                List<Data> grupos = new ArrayList<>();
                grupos.add(new Data("id", 0, "name", "Ungrouped", "links", links));
                grupos.addAll(result.getList("groups"));


                for (Data g : grupos) {

                    VerticalLayout vl = new VerticalLayout();
                    vl.setSizeUndefined();

                    Label l;
                    vl.addComponent(l = new Label(g.getString("name")));
                    l.addStyleName("favgrupo");

                    for (Data f : g.getList("links")) {

                        Button b = new Button(f.getString("name"), new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                Page.getCurrent().open(f.getString("url"), (event.isAltKey() || event.isCtrlKey())?"_blank":Page.getCurrent().getWindowName());
                            }
                        });
                        b.setCaption(b.getCaption()
                                //        + " <span class=\"valo-menu-badge\">123</span>"
                        );
                        b.setCaptionAsHtml(true);
                        b.setPrimaryStyleName(ValoTheme.BUTTON_LINK);
                        b.addStyleName("accionsubmenu");

                        vl.addComponent(b);
                    }

                    addComponent(vl);

                }

            }
        });

    }
}
