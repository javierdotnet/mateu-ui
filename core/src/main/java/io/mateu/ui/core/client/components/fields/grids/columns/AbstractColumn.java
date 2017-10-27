package io.mateu.ui.core.client.components.fields.grids.columns;

import io.mateu.ui.core.shared.CellStyleGenerator;

/**
 * Created by miguel on 23/10/16.
 */
public class AbstractColumn<T extends AbstractColumn<T>> {

    private String id;
    private String label;
    private int width = 200;
    private boolean editable;
    private ColumnAlignment alignment = ColumnAlignment.LEFT;
    private CellStyleGenerator styleGenerator;

    public AbstractColumn(String id, String label, int width, boolean editable) {
        this.id = id;
        this.label = label;
        this.width = width;
        this.editable = editable;
    }

    public String getId() {
        return id;
    }

    public T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getLabel() {
        return label;
    }

    public T setLabel(String label) {
        this.label = label;
        return (T) this;
    }

    public int getWidth() {
        return width;
    }

    public T setWidth(int width) {
        this.width = width;
        return (T) this;
    }

    public boolean isEditable() {
        return editable;
    }

    public T setEditable(boolean editable) {
        this.editable = editable;
        return (T) this;
    }

    public ColumnAlignment getAlignment() {
        return alignment;
    }

    public T setAlignment(ColumnAlignment alignment) {
        this.alignment = alignment;
        return (T) this;
    }

    public CellStyleGenerator getStyleGenerator() {
        return styleGenerator;
    }

    public T setStyleGenerator(CellStyleGenerator styleGenerator) {
        this.styleGenerator = styleGenerator;
        return (T) this;
    }
}
