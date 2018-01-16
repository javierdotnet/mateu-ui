package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.MultilanguageTextAreaField;
import io.mateu.ui.core.client.components.fields.MultilanguageTextField;
import io.mateu.ui.core.client.components.fields.SelectByIdField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 10/8/16.
 */
public class MultilanguageView extends AbstractView {
    @Override
    public String getTitle() {
        return "Multilanguage";
    }

    @Override
    public void build() {
        add(new TextField("_id", "_id"))
                .add(new MultilanguageTextField("field1", "field1"))
                .add(new MultilanguageTextAreaField("field2", "field2"))
                .add(new MultilanguageTextField("field1", "field1"))
                .add(new MultilanguageTextAreaField("field2", "field2").setUnmodifiable(true))
                .add(new MultilanguageTextField("field3", "field3").setUnmodifiable(true))
        ;
    }

}
