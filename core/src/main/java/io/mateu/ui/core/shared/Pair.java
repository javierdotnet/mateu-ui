package io.mateu.ui.core.shared;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/10/16.
 */
public class Pair extends Data {

    public String getValue() {
        return getString("value");
    }

    public void setValue(String value) {
        set("value", value);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String text) {
        set("text", text);
    }

}
