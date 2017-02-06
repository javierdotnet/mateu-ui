package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 5/2/17.
 */
public class CRUDDialog extends AbstractDialog {

    private final AbstractCRUDView crud;

    public CRUDDialog(AbstractCRUDView crud) {
        this.crud = crud;
    }

    @Override
    public void onOk(Data data) {

    }

    @Override
    public String getTitle() {
        return crud.getTitle();
    }

    @Override
    public AbstractForm createForm() {
        return crud.getForm();
    }
}
