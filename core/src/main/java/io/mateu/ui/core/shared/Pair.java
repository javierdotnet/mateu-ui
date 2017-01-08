package io.mateu.ui.core.shared;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/10/16.
 */
public class Pair extends Data {

    public Pair(Object value, String text) {
        setValue(value);
        setText(text);
    }

    public Object getValue() {
        return get("value");
    }

    public void setValue(Object value) {
        set("value", value);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String text) {
        set("text", text);
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public boolean equals(Data data) {
        return get("value").equals(data.get("value"));
    }
}
