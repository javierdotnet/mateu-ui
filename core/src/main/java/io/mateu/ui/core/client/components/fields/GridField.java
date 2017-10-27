package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.GriRowFormatter;
import io.mateu.ui.core.client.components.fields.grids.GridFieldHelper;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.data.GridFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.mateu.ui.core.client.Mateu.getHelper;

/**
 * Created by miguel on 23/10/16.
 */
public class GridField extends AbstractField implements GriRowFormatter {

    private boolean expandable = true;

    private boolean fullWidth = false;

    private boolean paginated = false;

    private boolean usedToSelect = false;

    private boolean usedToSelectMultipleValues = false;

    private List<AbstractColumn> columns = new ArrayList<AbstractColumn>();

    private List<AbstractAction> actions = new ArrayList<AbstractAction>();

    private List<AbstractAction> contextualMenuActions = new ArrayList<AbstractAction>();

    private Runnable after;

    private int height = -1;

    public GridField(String id)
    {
        super(id);
    }

    public GridField(String id, String label)
    {
        super(id, label);
    }

    public GridField(String id, AbstractColumn[] columnas)
    {
        this(id, Arrays.asList(columnas));
    }

    public GridField(String id, List<AbstractColumn> columnas)
    {
        this(id, null, columnas);
    }

    public GridField(String id, List<AbstractColumn> columnas, boolean paginated)
    {
        this(id, null, columnas, paginated);
    }

    public GridField(String id, String label, AbstractColumn[] columnas)
    {
        this(id, label, columnas, false);
    }

    public GridField(String id, String label, List<AbstractColumn> columnas)
    {
        this(id, label, columnas, false);
    }

    public GridField(String id, String label, AbstractColumn[] columnas, boolean paginated)
    {
        this(id, label, Arrays.asList(columnas), paginated);
    }

    public GridField(String id, String label, List<AbstractColumn> columnas, boolean paginated)
    {
        super(id, label);
        this.columns.addAll(columnas);
        this.paginated = paginated;
    }

    public GridField addAccion(AbstractAction accion)
    {
        getActions().add(accion);
        return this;
    }

    public boolean isPaginated() {
        return paginated;
    }

    public GridField setPaginated(boolean paginated) {
        this.paginated = paginated;
        return this;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public GridField setExpandable(boolean expandable) {
        this.expandable = expandable;
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
    public String getRowColor(Data m)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRowBackgroundColor(Data m)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getRowExtraStyleAttributes(Data m)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public GridField setColumnas(List<AbstractColumn> columns, Runnable despues)
    {
        this.columns = columns;

        ((GridFieldHelper) getHelper()).repaint(despues);

        return this;
    }

    public int getRowHeight()
    {
        return 20;
    }

    public GridField setAfter(Runnable after) {
        this.after = after;
        return this;
    }

    public Runnable getAfter() {
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

    public AbstractForm getDataForm() {
        return getDataForm(new Data());
    }


    public AbstractForm getDataForm(Data initialData) {
        AbstractForm f = new AbstractForm() {
        };
        for (AbstractColumn c : getColumns()) {
            f.add(new TextField(c.getId(), c.getLabel()));
        }
        return f;
    }

    public int getHeight() {
        return height;
    }

    public GridField setHeight(int height) {
        this.height = height;
        return this;
    }

    public boolean isFullWidth() {
        return fullWidth || getColumns().size() > 4;
    }

    public GridField setFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
        return this;
    }

    public boolean isUsedToSelect() {
        return usedToSelect;
    }

    public GridField setUsedToSelect(boolean usedToSelect) {
        this.usedToSelect = usedToSelect;
        return this;
    }

    public boolean isUsedToSelectMultipleValues() {
        return usedToSelectMultipleValues;
    }

    public GridField setUsedToSelectMultipleValues(boolean usedToSelectMultipleValues) {
        this.usedToSelectMultipleValues = usedToSelectMultipleValues;
        return this;
    }
}
