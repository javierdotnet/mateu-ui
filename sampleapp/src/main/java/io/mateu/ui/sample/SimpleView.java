package io.mateu.ui.sample;

import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractView;

/**
 * Created by miguel on 2/7/17.
 */
public class SimpleView extends AbstractView {
    @Override
    public String getTitle() {
        return "Simple view";
    }

    @Override
    public void build() {
        add(new TextField("nombre", "Nombre"));
        add(new TextField("appelidos", "Apellidos"));
        add(new TextField("nombre", "Nombre de nuevo"));
    }
}
