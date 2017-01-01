package io.mateu.ui.core.client.data;

import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 23/10/16.
 */
public interface GridFilter {

    public abstract String getLabel();

    public abstract AbstractField getCampo();

    public abstract boolean matches(Data d);
}
