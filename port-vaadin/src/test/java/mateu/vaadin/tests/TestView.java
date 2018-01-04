package mateu.vaadin.tests;

import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;

public class TestView extends AbstractView {

    @Override
    public Data initializeData() {
        return new Data("x", "aaaaa");
    }

    public TestView() {

    }

    @Override
    public String getTitle() {
        return getData().get("x");
    }

    @Override
    public void build() {

    }
}
