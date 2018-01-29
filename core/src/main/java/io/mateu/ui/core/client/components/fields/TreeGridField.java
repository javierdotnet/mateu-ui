package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 27/1/17.
 */
public class TreeGridField extends GridField {
    public TreeGridField(String id) {
        super(id);
    }

    public TreeGridField(String id, String label) {
        super(id, label);
    }

    public TreeGridField(String id, AbstractColumn[] columnas) {
        super(id, columnas);
    }

    public TreeGridField(String id, List<AbstractColumn> columnas) {
        super(id, columnas);
    }

    public TreeGridField(String id, List<AbstractColumn> columnas, boolean paginated) {
        super(id, columnas, paginated);
    }

    public TreeGridField(String id, String label, AbstractColumn[] columnas) {
        super(id, label, columnas);
    }

    public TreeGridField(String id, String label, List<AbstractColumn> columnas) {
        super(id, label, columnas);
    }

    public TreeGridField(String id, String label, AbstractColumn[] columnas, boolean paginated) {
        super(id, label, columnas, paginated);
    }

    public TreeGridField(String id, String label, List<AbstractColumn> columnas, boolean paginated) {
        super(id, label, columnas, paginated);
    }

    public void open(Data value) {
        // override to execute action on double click
    }
}
