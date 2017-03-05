package io.mateu.ui.vaadin.data;

import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.vaadin.ViewLayout;

/**
 * Created by miguel on 30/12/16.
 */
public class ViewNodeDataStore extends DataStore {

    private final ViewLayout viewNode;

    public ViewNodeDataStore(ViewLayout viewNode) {
        super(viewNode.getView().getForm().getData());
        this.viewNode = viewNode;
    }

    @Override
    public void hasChanged(String k, Object oldValue, Object newValue) {
        if (viewNode != null && viewNode.getView().getForm().getPropertyListeners().containsKey(k)) for (ChangeListener l : viewNode.getView().getForm().getPropertyListeners().get(k)) {
            l.changed(oldValue, newValue);
        }
    }
}
