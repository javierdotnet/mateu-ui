package io.mateu.ui.core.client.components;

/**
 * Created by miguel on 9/8/16.
 */
public class Label extends BaseComponent {

    private String text;
    private Alignment alignment = Alignment.LEFT;

    public Label(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }


    public Alignment getAlignment() {
        return alignment;
    }

    public Label setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }
}
