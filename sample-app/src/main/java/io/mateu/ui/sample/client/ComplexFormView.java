package io.mateu.ui.sample.client;

import io.mateu.ui.core.components.fields.TextField;
import io.mateu.ui.core.views.AbstractForm;
import io.mateu.ui.core.views.AbstractView;
import io.mateu.ui.core.views.ViewForm;

/**
 * Created by miguel on 10/8/16.
 */
public class ComplexFormView extends AbstractView {
    @Override
    public String getTitle() {
        return "Complex form";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this)
                .add(new TextField("f1", "Name"))
                .add(new TextField("f2", "Adress"))
                .add(new TextField("f3", "Country"))
                .add(new TextField("f1", "Name repeated"))
                ;
    }
}
