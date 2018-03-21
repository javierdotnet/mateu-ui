package io.mateu.ui.javafx.views;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class EditorCenterNode extends VBox {
    private final EditorNode editorNode;

    public EditorCenterNode(EditorNode editorNode) {
        this.editorNode = editorNode;
    }
}
