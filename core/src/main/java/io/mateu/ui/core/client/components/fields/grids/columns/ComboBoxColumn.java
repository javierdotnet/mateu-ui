package io.mateu.ui.core.client.components.fields.grids.columns;

import io.mateu.ui.core.shared.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class ComboBoxColumn extends AbstractColumn<ComboBoxColumn> {
    private List<Pair> values = new ArrayList<>();

    public ComboBoxColumn(String id, String label, int width) {
        super(id, label, width, true);
    }

    public ComboBoxColumn(String id, String label, int width, String... args) {
        super(id, label, width, true);
        String v = null;
        int pos = 0;
        for (String a : args) {
            if (pos % 2 == 1) values.add(new Pair(v, a));
            else v = a;
            pos++;
        }
    }

    public ComboBoxColumn(String id, String label, int width, List<Pair> values) {
        super(id, label, width, true);
        this.values.addAll(values);
    }

    public List<Pair> getValues() {
        return values;
    }
}
