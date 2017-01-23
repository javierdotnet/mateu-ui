package io.mateu.ui.vaadin;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;

/**
 * Created by miguel on 23/1/17.
 */
public class ProgessLayout extends VerticalLayout {

    public ProgessLayout() {
        setSizeFull();
        addStyleName("mascara");
        ProgressBar pb = new ProgressBar();
        pb.setIndeterminate(true);
        addComponent(pb);
        setComponentAlignment(pb, Alignment.MIDDLE_CENTER);
    }
}
