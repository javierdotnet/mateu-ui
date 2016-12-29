package io.mateu.ui.core.data;

import io.mateu.ui.core.components.fields.AbstractField;

/**
 * Created by miguel on 23/10/16.
 */
public interface GridFilter {

    public abstract String getLabel();

    public abstract AbstractField getCampo();

    public abstract boolean matches(DataContainer d);
}
