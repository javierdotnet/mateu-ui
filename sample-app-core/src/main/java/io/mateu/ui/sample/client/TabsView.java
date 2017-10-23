package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.Tabs;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

/**
 * Created by miguel on 23/3/17.
 */
public class TabsView extends AbstractView {
    @Override
    public String getTitle() {
        return "Tabs";
    }

    @Override
    public void build() {
        add(new TextField("tf1", "TF1"))
                .add(new Tabs("tabs")
                        .add(new Tab("tab 1").add(new TextField("tf2", "TF2"))
                                .add(new TextField("tf2a", "TF2"))
                                .add(new TextField("tf2b", "TF2"))
                                .add(new TextField("tf2c", "TF2"))
                                .add(new TextField("tf2d", "TF2"))
                                .add(new TextField("tf2e", "TF2")))
                        .add(new Tab("tab 2").add(new TextField("tf3", "TF3")))
                        .add(new Tab("tab 3").add(new TextField("tf2", "TF2")))
                );
    }
}
