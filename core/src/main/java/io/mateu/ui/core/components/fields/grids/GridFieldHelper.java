package io.mateu.ui.core.components.fields.grids;

import io.mateu.ui.core.app.AbstractExecutable;
import io.mateu.ui.core.data.DataContainer;

import java.util.List;

/**
 * Created by miguel on 23/10/16.
 */
public interface GridFieldHelper {

    public void repaint(AbstractExecutable after);

    public List<DataContainer> getSelection();

    public void selectionHasChanged();

    public void keepFocus();

    public void recoverFocus();
}
