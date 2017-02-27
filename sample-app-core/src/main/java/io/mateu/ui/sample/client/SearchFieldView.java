package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.fields.SearchField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;

/**
 * Created by miguel on 7/2/17.
 */
public class SearchFieldView extends AbstractView {
    @Override
    public String getTitle() {
        return "SearchFieldView";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).add(new SearchField("f", "XXXX", new CRUDView()));
    }
}
