package io.mateu.ui.vaadin.components;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.Callback;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.List;

public class LastEditedLayout extends CssLayout {

    public LastEditedLayout() {


        MateuUI.getApp().getLastEdited(MateuUI.getApp().getUserData(), new Callback<Data>() {
            @Override
            public void onSuccess(Data result) {

                setSizeFull();

                List<Data> records = result.getList("records");

                VerticalLayout vl = new VerticalLayout();
                vl.setSizeUndefined();
                vl.setSpacing(false);

                for (Data g : records) {

                    HorizontalLayout hl = new HorizontalLayout();
                    hl.setSpacing(false);


                    Label l;

                    Image i;
                    hl.addComponent(i = new Image());
                    i.setIcon(("edit".equals(g.getString("icon")))?VaadinIcons.EDIT:VaadinIcons.PLUS);
                    i.addStyleName("lasteditedicon");
                    i.setSizeUndefined();

                    hl.addComponent(l = new Label(g.getString("name")));
                    l.addStyleName("lasteditedname");

                    hl.addComponent(l = new Label(g.getString("when")));
                    l.addStyleName("lasteditedwhen");


                    Button b = new Button("Open", new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent event) {
                            Page.getCurrent().open(g.getString("url"), (event.isAltKey() || event.isCtrlKey())?"_blank":Page.getCurrent().getWindowName());
                        }
                    });
                    b.setCaption(b.getCaption()
                            //        + " <span class=\"valo-menu-badge\">123</span>"
                    );
                    b.setCaptionAsHtml(true);
                    b.setPrimaryStyleName(ValoTheme.BUTTON_LINK);
                    b.addStyleName("lasteditedlink");

                    hl.addComponent(b);


                    vl.addComponent(hl);

                }

                addComponent(vl);

            }
        });

    }


}
