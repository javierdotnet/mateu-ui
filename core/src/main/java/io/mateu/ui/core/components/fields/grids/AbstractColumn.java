package io.mateu.ui.core.components.fields.grids;

/**
 * Created by miguel on 23/10/16.
 */
public class AbstractColumn<T extends AbstractColumn<T>> {

    private String id;
    private String label;
    private int width;
    private boolean editable;

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
}
