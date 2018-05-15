package io.mateu.ui.vaadin.components;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.AbsoluteLayout;

/**
 * Created by miguel on 23/1/17.
 */
public class MaskedView extends AbsoluteLayout implements View {

    private final ViewLayout viewLayout;

    public MaskedView(ViewLayout viewLayout) {
        this.viewLayout = viewLayout;

        addComponent(viewLayout, "top:0px;left:0px;");

        addComponent(new ProgessLayout(), "top:0px;left:0px;");

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
