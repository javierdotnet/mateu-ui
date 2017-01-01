package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.components.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public class FieldContainer implements Component {

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

    public void setComponentsSequence(List<Component> componentsSequence) {
        this.componentsSequence = componentsSequence;
    }

    public FieldContainer add(Component component) {
        componentsSequence.add(component);
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

}
