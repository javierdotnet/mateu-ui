package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 27/1/17.
 */
public class TreeField extends AbstractField<TreeField> {
    public TreeField(String id) {
        super(id);
    }

    public TreeField(String id, String label) {
        super(id, label);
    }

    public void open(Data data) {
        // override to execute action on double click
    }
}
