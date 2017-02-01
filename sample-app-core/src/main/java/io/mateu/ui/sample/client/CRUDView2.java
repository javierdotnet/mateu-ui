package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractForm;

/**
 * Created by miguel on 2/1/17.
 */
public class CRUDView2 extends CRUDView {

    @Override
    public int getMaxFieldsInHeader() {
        return 1;
    }

    @Override
    public AbstractForm createForm() {
        AbstractForm f = super.createForm()
                .add(new TextField("f2", "F2"))
                .add(new TextField("f2", "F2"))
                .add(new TextField("f2", "F2"))
                .add(new TextField("f2", "F2"))
                .add(new TextField("f2", "F2"));
        return f;
    }
}
