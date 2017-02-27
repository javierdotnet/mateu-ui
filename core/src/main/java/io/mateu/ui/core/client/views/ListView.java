package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 7/2/17.
 */
public interface ListView {
    List<AbstractColumn> getColumns();

    int getMaxFieldsInHeader();

    void search();

    List<Data> getSelection();

    void addListViewListener(ListViewListener listener);

    AbstractForm getForm();

    List<AbstractAction> getActions();
}
