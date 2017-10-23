package io.mateu.ui.core.client.components;

import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.views.FieldContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 23/3/17.
 */
public class Tabs extends AbstractField implements Component {

    private boolean fullWidth = false;

    private List<Tab> tabs = new ArrayList<>();

    public Tabs(String id) {
        super(id);
    }


    public List<Tab> getTabs() {
        return tabs;
    }

    public Tabs setTabs(List<Tab> tabs) {
        this.tabs = tabs;
        return this;
    }

    public Tabs add(Tab t) {
        getTabs().add(t);
        return this;
    }

    public boolean isFullWidth() {
        return fullWidth;
    }

    public Tabs setFullWidth(boolean fullWidth) {
        this.fullWidth = fullWidth;
        return this;
    }
}
