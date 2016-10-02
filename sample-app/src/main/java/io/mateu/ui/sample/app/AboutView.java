package io.mateu.ui.sample.app;

import io.mateu.ui.core.components.Label;
import io.mateu.ui.core.views.AbstractForm;
import io.mateu.ui.core.views.AbstractView;

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
        return new AbstractForm().add(new Label("Sobre nosotros"));
    }
}
