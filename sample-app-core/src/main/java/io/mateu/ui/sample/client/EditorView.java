package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 20/4/17.
 */
public class EditorView extends AbstractEditorView {

    @Override
    public Data initializeData() {
        Data d = super.initializeData();

        d.getList("_badges").add(new Data("_value", "3", "_css", "red"));
        d.set("_subtitle", "Subtítulo");

        return d;
    }

    @Override
    public String getTitle() {
        return "Editor";
    }

    @Override
    public void build() {
        add(new TextField("f1", "Field1"))
                .add(new TextField("_title", "_title"))
                .add(new TextField("_subtitle", "_subtitle"))
                ;
    }

    @Override
    public void save(Data data, AsyncCallback<Data> callback) {

    }

    @Override
    public void load(Object id, AsyncCallback<Data> callback) {

    }
}
