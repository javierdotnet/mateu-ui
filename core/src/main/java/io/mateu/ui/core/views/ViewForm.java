package io.mateu.ui.core.views;

import io.mateu.ui.core.data.DataContainer;

/**
 * Created by miguel on 8/12/16.
 */
public class ViewForm extends AbstractForm {

    private final AbstractView view;

    public ViewForm(AbstractView view) {
        this.view = view;
    }

    @Override
    public DataContainer getData() {
        return view.getData();
    }
}
