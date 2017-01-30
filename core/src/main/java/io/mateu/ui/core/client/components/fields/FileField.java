package io.mateu.ui.core.client.components.fields;

/**
 * Created by miguel on 3/1/17.
 */
public class FileField extends AbstractField<FileField> {

    private boolean temporary;

    public FileField(String id) {
        super(id);
    }

    public FileField(String id, String label) {
        super(id, label);
    }

    public boolean isTemporary() {
        return temporary;
    }

    public FileField setTemporary(boolean temporary) {
        this.temporary = temporary;
        return this;
    }
}
