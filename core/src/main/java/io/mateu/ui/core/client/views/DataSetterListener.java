package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 30/12/16.
 */
public interface DataSetterListener {

    public void setted(Data newData);

    public void setted(String k, Object v);
}
