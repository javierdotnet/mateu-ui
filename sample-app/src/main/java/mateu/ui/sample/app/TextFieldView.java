package mateu.ui.sample.app;

import mateu.ui.core.components.TextField;
import mateu.ui.core.views.AbstractForm;
import mateu.ui.core.views.AbstractView;

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
