package io.mateu.ui.core.client.components.fields.grids.columns;

import io.mateu.ui.core.client.app.ActionOnRow;

/**
 * Created by miguel on 23/10/16.
 */
public abstract class LinkColumn extends AbstractColumn implements ActionOnRow {

    private boolean modifierPressed;

    public LinkColumn(String id, String label, int width) {
        super(id, label, width, false);
    }

    public String getText() {
        return null;
    }

    public LinkColumn setModifierPressed(boolean modifierPressed) {
        this.modifierPressed = modifierPressed;
        return this;
    }

    public boolean isModifierPressed() {
        return modifierPressed;
    }
}
