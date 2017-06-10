package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.SelectByIdField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 10/8/16.
 */
public class TextFieldView extends AbstractView {
    @Override
    public String getTitle() {
        return "TextField";
    }

    @Override
    public void build() {
        add(new TextField("_id", "_id"))
                .add(new TextField("field1", "Label for textfield"))
                .add(new TextField("field2", "Label for textfield").setUnmodifiable(true))
                .add(new SelectByIdField("filed3", "SearchById", "select x.id, x.firstname from customer x where x.id = xxxx ") {

                    @Override
                    public AbstractEditorView getEditor() {
                        return new AbstractEditorView() {
                            @Override
                            public void save(Data data, AsyncCallback<Data> callback) {
                                callback.onSuccess(data);
                            }

                            @Override
                            public void load(Object id, AsyncCallback<Data> callback) {
                                callback.onSuccess(new Data("_id", 1002, "name", "uwdhed wediwed"));
                            }

                            @Override
                            public String getTitle() {
                                return "XXXX";
                            }

                            @Override
                            public void build() {
                                add(new TextField("name", "Name"));
                            }
                        };
                    }

                    @Override
                    public Pair getPair(Data editorData) {
                        return new Pair(editorData.get("_id"), editorData.get("name"));
                    }
                })
        ;
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("errors") {
            @Override
            public void run() {
                MateuUI.notifyErrors(Arrays.asList("dededwe", "wdedew", "wde ddeewd"));
            }
        });

        as.add(new AbstractAction("error") {
            @Override
            public void run() {
                MateuUI.notifyError("wiodew wedo wedw edewd m");
            }
        });
        as.add(new AbstractAction("info") {
            @Override
            public void run() {
                MateuUI.notifyInfo("wiodew wedo wedw edewd m");
            }
        });
        as.add(new AbstractAction("done") {
            @Override
            public void run() {
                MateuUI.notifyDone("wiodew wedo wedw edewd m");
            }
        });
        as.add(new AbstractAction("Reset") {
            @Override
            public void run() {
                getForm().set("field1", null);
            }
        });
        return as;
    }
}
