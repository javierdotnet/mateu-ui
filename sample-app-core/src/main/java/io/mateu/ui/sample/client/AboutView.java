package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.Alignment;
import io.mateu.ui.core.client.components.Label;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutView extends AbstractView {
    @Override
    public String getTitle() {
        return "About";
    }

    @Override
    public void build() {
        add(new Label("Sobre nosotros").setAlignment(Alignment.CENTER));
    }
}
