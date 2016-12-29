package io.mateu.ui.core.components.fields.grids;

import io.mateu.ui.core.data.DataContainer;

/**
 * Created by miguel on 23/10/16.
 */
public interface GriRowFormatter {

    public String getRowColor(DataContainer m);

    public String getRowBackgroundColor(DataContainer m);

    public String getRowExtraStyleAttributes(DataContainer m);
}
