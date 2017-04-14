package io.mateu.ui.vaadin;

import com.vaadin.ui.Grid;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 11/4/17.
 */
public class CellStyleGenerator implements Grid.CellStyleGenerator {

    private final Map<String, io.mateu.ui.core.shared.CellStyleGenerator> generators = new HashMap<>();

    public CellStyleGenerator(List<AbstractColumn> columns) {
        for (AbstractColumn c : columns) if (c.getStyleGenerator() != null) generators.put(c.getId(), c.getStyleGenerator());
    }

    @Override
    public String getStyle(Grid.CellReference cellReference) {
        if (generators.containsKey(cellReference.getPropertyId())) {
            return generators.get(cellReference.getPropertyId()).getStyle(cellReference.getValue());
        }
        return null;
    }

    public boolean hasGenerators() { return generators.size() > 0; }
}
