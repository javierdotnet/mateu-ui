package io.mateu.ui.core.client.components.fields;

/**
 * Created by miguel on 3/1/17.
 */
public abstract class LinkField extends AbstractField<LinkField> {
    public LinkField(String id) {
        super(id);
    }

    public LinkField(String id, String label) {
        super(id, label);
    }

    public abstract void run();

    public String getText() {
        return null;
    }
}
