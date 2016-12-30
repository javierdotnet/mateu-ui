package io.mateu.ui.core.components.fields.grids;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/10/16.
 */
public interface GriRowFormatter {

    public String getRowColor(Data m);

    public String getRowBackgroundColor(Data m);

    public String getRowExtraStyleAttributes(Data m);
}
