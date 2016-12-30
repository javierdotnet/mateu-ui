package io.mateu.ui.sample.client;

import io.mateu.ui.core.components.Alignment;
import io.mateu.ui.core.components.Label;
import io.mateu.ui.core.views.AbstractForm;
import io.mateu.ui.core.views.AbstractView;
import io.mateu.ui.core.views.ViewForm;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutView extends AbstractView {
    @Override
    public String getTitle() {
        return "About";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).setLastFieldMaximized(true).add(new Label("Sobre nosotros").setAlignment(Alignment.CENTER));
    }
}
