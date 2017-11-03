package io.mateu.ui.core.client.components;

import io.mateu.ui.core.client.components.fields.AbstractField;

/**
 * Created by miguel on 9/8/16.
 */
public class Separator extends AbstractField {

    private String text;

    public Separator(String text) {
        super("____separator");
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
