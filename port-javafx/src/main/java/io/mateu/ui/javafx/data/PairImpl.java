package io.mateu.ui.javafx.data;

import io.mateu.ui.core.data.DataContainer;
import io.mateu.ui.core.data.Pair;

/**
 * Created by miguel on 23/10/16.
 */
public class PairImpl extends DataContainerImpl implements Pair {

    public PairImpl() {
        super();
        set("value", (String) null);
        set("text", (String) null);
    }


    public PairImpl(String value, String text) {
        setValue(value);
        setText(text);
    }

    @Override
    public String getValue() {
        return get("value");
    }

    @Override
    public void setValue(String value) {
        set("value", value);
    }

    @Override
    public String getText() {
        return get("text");
    }

    @Override
    public void setText(String text) {
        set("text", text);
    }
}
