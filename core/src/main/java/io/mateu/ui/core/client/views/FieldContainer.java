package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.components.*;
import io.mateu.ui.core.client.components.fields.AbstractField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public class FieldContainer implements Component {

    private final AbstractForm form;

    private boolean lastFieldMaximized;

    private LabelDisposition labelDisposition = LabelDisposition.ABOVE;

    private List<Component> componentsSequence = new ArrayList<>();

    public LabelDisposition getLabelDisposition() {
        return labelDisposition;
    }

    public void setLabelDisposition(LabelDisposition labelDisposition) {
        this.labelDisposition = labelDisposition;
    }

    public List<Component> getComponentsSequence() {
        return componentsSequence;
    }

    public FieldContainer(AbstractForm form) {
        this.form = form;
    }

    public void setComponentsSequence(List<Component> componentsSequence) {
        this.componentsSequence = componentsSequence;
        for (Component component : componentsSequence) {
            if (component instanceof AbstractField) ((AbstractField) component).setForm(getForm());
        }
    }

    public FieldContainer add(Component component) {
        componentsSequence.add(component);
        if (component instanceof AbstractField) ((AbstractField) component).setForm(getForm());
        return this;
    }

    public boolean isLastFieldMaximized() {
        return lastFieldMaximized;
    }

    public FieldContainer setLastFieldMaximized(boolean lastFieldMaximized) {
        this.lastFieldMaximized = lastFieldMaximized;
        return this;
    }

    public FieldContainer startRow() {
        componentsSequence.add(new RowStart());
        return this;
    }

    public FieldContainer endRow() {
        componentsSequence.add(new RowEnd());
        return this;
    }

    public FieldContainer startColumn() {
        componentsSequence.add(new ColumnStart());
        return this;
    }

    public FieldContainer endColumn() {
        componentsSequence.add(new ColumnEnd());
        return this;
    }

    public FieldContainer include(FieldContainer container) {
        componentsSequence.addAll(container.getComponentsSequence());
        return this;
    }

    public AbstractForm getForm() {
        return form;
    }
}
