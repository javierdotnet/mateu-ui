package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.shared.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class RadioButtonField extends AbstractField<RadioButtonField> {

    private List<Pair> values = new ArrayList<>();

    public RadioButtonField(String id) {
        super(id);
    }

    public RadioButtonField(String id, String label) {
        super(id, label);
    }

    public RadioButtonField(String id, String label, String... args) {
        super(id, label);
        String v = null;
        int pos = 0;
        for (String a : args) {
            if (pos % 2 == 1) values.add(new Pair(v, a));
            else v = a;
            pos++;
        }
    }

    public List<Pair> getValues() {
        return values;
    }}
