package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.Alignment;
import io.mateu.ui.core.client.components.Label;
import io.mateu.ui.core.client.components.fields.HtmlField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutView extends AbstractView {
    @Override
    public String getTitle() {
        return "About";
    }

    @Override
    public Data initializeData() {
        return new Data("h", "Hola mundo!");
    }

    @Override
    public void build() {
        add(new HtmlField("h"));
    }
}
