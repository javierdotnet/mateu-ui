package io.mateu.ui.javafx.app;

import io.mateu.ui.core.views.AbstractView;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.scene.control.Tab;

/**
 * Created by miguel on 29/12/16.
 */
public class ViewTab extends Tab {
    private final AbstractView view;

    public ViewTab(AbstractView view) {
        this.view = view;

        setText(view.getTitle());
        setContent(new ViewNode(view));

    }

    public String getKey() {
        return view.getId();
    }
}
