package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.shared.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ComboBoxField extends AbstractField<ComboBoxField> {

    private List<Pair> values = new ArrayList<>();

    public ComboBoxField(String id) {
        super(id);
    }

    public ComboBoxField(String id, String label) {
        super(id, label);
    }

    public ComboBoxField(String id, String label, String... args) {
        super(id, label);
        String v = null;
        int pos = 0;
        for (String a : args) {
            if (pos % 2 == 1) values.add(new Pair(v, a));
            else v = a;
            pos++;
        }
    }

    public ComboBoxField(String id, String label, List<Pair> values) {
        super(id, label);
        this.values.addAll(values);
    }

    public List<Pair> getValues() {
        return values;
    }
}
