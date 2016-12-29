package io.mateu.ui.core.components.fields;

import io.mateu.ui.core.app.AbstractAction;
import io.mateu.ui.core.app.AbstractExecutable;
import io.mateu.ui.core.components.fields.grids.AbstractColumn;
import io.mateu.ui.core.components.fields.grids.GriRowFormatter;
import io.mateu.ui.core.components.fields.grids.GridFieldHelper;
import io.mateu.ui.core.data.DataContainer;
import io.mateu.ui.core.data.GridFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.mateu.ui.core.Mateu.getHelper;

/**
 * Created by miguel on 23/10/16.
 */
public class GridField extends AbstractField implements GriRowFormatter {

    private List<AbstractColumn> columns = new ArrayList<AbstractColumn>();

    private List<AbstractAction> actions = new ArrayList<AbstractAction>();

    private List<AbstractAction> contextualMenuActions = new ArrayList<AbstractAction>();

    private AbstractExecutable after;

    private int height = 200;

    public GridField(String id)
    {
        super(id);
    }

    public GridField(String id, AbstractColumn[] columnas)
    {
        super(id);
        this.columns.addAll(Arrays.asList(columnas));
    }

    public GridField addAccion(AbstractAction accion)
    {
        getActions().add(accion);
        return this;
    }

    public List<AbstractColumn> getColumns()
    {
        return columns;
    }

    public void keepFocus()
    {
        ((GridFieldHelper) getHelper()).keepFocus();
    }

    public void recoverFocus()
    {
        ((GridFieldHelper) getHelper()).recoverFocus();
    }

    public GridField addColumn(AbstractColumn column)
    {
        columns.add(column);
        return this;
    }

    public List<AbstractAction> getActions()
    {
        return actions;
    }

    public GridField setActions(List<AbstractAction> actions)
    {
        this.actions = actions;
        return this;
    }

    @Override
    public String getRowColor(DataContainer m)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRowBackgroundColor(DataContainer m)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRowExtraStyleAttributes(DataContainer m)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public GridField setColumnas(List<AbstractColumn> columns, AbstractExecutable despues)
    {
        this.columns = columns;

        ((GridFieldHelper) getHelper()).repaint(despues);

        return this;
    }

    public int getRowHeight()
    {
        return 20;
    }

    public GridField setAfter(AbstractExecutable after) {
        this.after = after;
        return this;
    }

    public AbstractExecutable getAfter() {
        return after;
    }

    public List<AbstractAction> getContextualMenuActions() {
        return contextualMenuActions;
    }

    public List<GridFilter> getFilters() {
        return new ArrayList<GridFilter>();
    }

    public void selectionHasChanged() {
        ((GridFieldHelper)getHelper()).selectionHasChanged();
    }

    public String getInfoSeleccion() {
        return "" + ((GridFieldHelper)getHelper()).getSelection().size() + " rows selected.";
    }

    public int getHeight() {
        return height;
    }

    public GridField setHeight(int height) {
        this.height = height;
        return this;
    }
}
