package io.mateu.ui.core.client.views;

import io.mateu.ui.core.shared.Data;

public abstract class AbstractListEditorDialog extends AbstractDialog {

    public abstract Data getData(int pos);

    public abstract void setData(int pos, Data data);

    public abstract int getListSize();

    public abstract int getInitialPos();

    public boolean isOrdered() {
        return true;
    }

}
