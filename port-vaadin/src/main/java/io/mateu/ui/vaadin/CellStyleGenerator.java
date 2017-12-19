package io.mateu.ui.vaadin;

import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.DataColumn;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.vaadin.data.DataStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 11/4/17.
 */
public class CellStyleGenerator implements StyleGenerator<Object> {

    private final Map<Integer, io.mateu.ui.core.shared.CellStyleGenerator> generators = new HashMap<>();

    public CellStyleGenerator(List<AbstractColumn> columns) {
        int pos = 0;
        for (AbstractColumn c : columns) {
            if (c.getStyleGenerator() != null) generators.put(pos, c.getStyleGenerator());
            else if (c instanceof DataColumn) {
                generators.put(pos, new io.mateu.ui.core.shared.CellStyleGenerator() {
                    @Override
                    public String getStyle(Object value) {
                        if (value instanceof DataStore) {
                            return ((DataStore) value).get("_css");
                        } else if (value instanceof Data) {
                                return ((Data)value).get("_css");
                        }
                        return null;
                    }

                    @Override
                    public boolean isContentShown() {
                        return true;
                    }
                });
            }
            pos++;
        }
    }

    public boolean hasGenerators() { return generators.size() > 0; }

    @Override
    public String apply(Object o) {

        /*
        if (generators.containsKey(cellReference.getColumnIndex())) {
            return generators.get(cellReference.getColumnIndex()).getStyle(cellReference.getValue());
        }
        */
        return null;

    }
}
