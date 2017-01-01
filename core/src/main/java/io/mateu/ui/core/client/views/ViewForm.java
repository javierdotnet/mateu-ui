package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 8/12/16.
 */
public class ViewForm extends AbstractForm {

    private final AbstractView view;

    public ViewForm(AbstractView view) {
        this.view = view;
    }


    @Override
    public Data initializeData() {
        return view.initializeData();
    }
}
