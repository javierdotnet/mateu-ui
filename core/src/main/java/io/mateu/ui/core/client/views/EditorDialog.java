package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 5/2/17.
 */
public class EditorDialog extends AbstractDialog {

    private final AbstractEditorView editor;

    public EditorDialog(AbstractEditorView editor) {
        this.editor = editor;
    }

    @Override
    public void onOk(Data data) {

    }

    @Override
    public String getTitle() {
        return editor.getTitle();
    }

    @Override
    public AbstractForm createForm() {
        return editor.getForm();
    }
}
