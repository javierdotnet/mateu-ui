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

    public Pair(Pair value) {
        clear();
        if (value != null) {
            setValue(value.getValue());
            setText(value.getText());
        }
    }

    public Pair(String json) {
        super(json);
    }

    public Pair() {

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
    public int hashCode() {
        return (getValue() != null)?getValue().hashCode():0;
    }

    @Override
    public boolean equals(Object obj) {
        boolean ok = obj == null && get("value") == null;
        ok |= obj != null && obj instanceof Pair && ((get("value") == null && ((Pair)obj).get("value") == null) || (get("value") != null && get("value").equals(((Pair)obj).get("value"))));
        return ok;
    }
}
