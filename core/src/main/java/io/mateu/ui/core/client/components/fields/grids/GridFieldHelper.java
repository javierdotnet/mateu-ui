package io.mateu.ui.core.client.components.fields.grids;

import io.mateu.ui.core.client.app.AbstractExecutable;
import io.mateu.ui.core.shared.Data;

import java.util.List;

/**
 * Created by miguel on 23/10/16.
 */
public interface GridFieldHelper {

    public void repaint(AbstractExecutable after);

    public List<Data> getSelection();

    public void selectionHasChanged();

    public void keepFocus();

    public void recoverFocus();
}
