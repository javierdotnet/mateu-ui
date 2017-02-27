package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 5/2/17.
 */
public abstract class CRUDDialog extends AbstractDialog implements ListView {

    private final AbstractCRUDView crud;

    public CRUDDialog(AbstractCRUDView crud) {
        this.crud = crud;
    }

    @Override
    public String getTitle() {
        return crud.getTitle();
    }

    @Override
    public AbstractForm createForm() {
        return crud.getForm();
    }

    @Override
    public List<AbstractColumn> getColumns() {
        return crud.getColumns();
    }

    @Override
    public int getMaxFieldsInHeader() {
        return crud.getMaxFieldsInHeader();
    }

    @Override
    public void search() {
        crud.search();
    }

    @Override
    public List<Data> getSelection() {
        return crud.getSelection();
    }

    @Override
    public void addListViewListener(ListViewListener listener) {
        crud.addListViewListener(listener);
    }

    @Override
    public List<AbstractAction> getActions() {
        return crud.getActions();
    }

    public AbstractCRUDView getCrud() {
        return crud;
    }
}
