package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

/**
 * Created by miguel on 10/8/16.
 */
public class PropertyListenerView extends AbstractView {
    @Override
    public String getTitle() {
        return "Property listener";
    }

    @Override
    public AbstractForm createForm() {
        TextField tf2;
        return new ViewForm(this)
                .add(new TextField("field1", "Label for field1"))
                .add(tf2 = new TextField("field2", "Label for field2 (hidden when field1 == x)"))
                .add(new TextField("field1", "Label for field1(repeated)"))
                .addPropertyListener("field1", new ChangeListener() {
                    @Override
                    public void changed(Object oldValue, Object newValue) {
                        tf2.setVisible(!"x".equals(newValue));
                    }
                });
    }

}
