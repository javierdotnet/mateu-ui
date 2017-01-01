package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;

/**
 * Created by miguel on 10/8/16.
 */
public class TextFieldView extends AbstractView {
    @Override
    public String getTitle() {
        return "TextField";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).add(new TextField("field1", "Label for textfield"));
    }
}
