package io.mateu.ui.core.client.components;

import io.mateu.ui.core.client.views.FieldContainer;

/**
 * Created by miguel on 23/3/17.
 */
public class Tab extends FieldContainer {

    private String caption;

    public Tab(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public Tab add(Component component) {
        return (Tab) super.add(component);
    }
}
