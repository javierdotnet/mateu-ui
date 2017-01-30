package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;

import java.util.List;

/**
 * Created by miguel on 27/1/17.
 */
public class TreeTableGridField extends GridField {
    public TreeTableGridField(String id) {
        super(id);
    }

    public TreeTableGridField(String id, String label) {
        super(id, label);
    }

    public TreeTableGridField(String id, AbstractColumn[] columnas) {
        super(id, columnas);
    }

    public TreeTableGridField(String id, List<AbstractColumn> columnas) {
        super(id, columnas);
    }

    public TreeTableGridField(String id, List<AbstractColumn> columnas, boolean paginated) {
        super(id, columnas, paginated);
    }

    public TreeTableGridField(String id, String label, AbstractColumn[] columnas) {
        super(id, label, columnas);
    }

    public TreeTableGridField(String id, String label, List<AbstractColumn> columnas) {
        super(id, label, columnas);
    }

    public TreeTableGridField(String id, String label, AbstractColumn[] columnas, boolean paginated) {
        super(id, label, columnas, paginated);
    }

    public TreeTableGridField(String id, String label, List<AbstractColumn> columnas, boolean paginated) {
        super(id, label, columnas, paginated);
    }
}
