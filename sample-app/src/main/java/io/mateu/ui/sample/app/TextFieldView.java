package io.mateu.ui.sample.app;

import io.mateu.ui.core.views.AbstractView;
import io.mateu.ui.core.components.TextField;
import io.mateu.ui.core.views.AbstractForm;

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
        return new AbstractForm().add(new TextField("field1", "Label for textfield"));
    }
}
