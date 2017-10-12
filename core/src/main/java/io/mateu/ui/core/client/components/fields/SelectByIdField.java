package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.EditorViewListener;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

/**
 * Created by miguel on 20/1/17.
 */
public abstract class SelectByIdField extends TextField {

    private String ql;

    public SelectByIdField(String id, String ql) {
        super(id);
        this.ql = ql;
    }

    public SelectByIdField(String id, String label, String ql) {
        super(id, label);
        this.ql = ql;
    }


    public String getQl() {
        return ql;
    }

    public SelectByIdField setQl(String ql) {
        this.ql = ql;
        return this;
    }

    public void call(String ql, AsyncCallback<Object[][]> callback) {
        MateuUI.getBaseService().select(ql,callback);
    }

    public abstract AbstractEditorView getEditor();

    public void createNew() {
        AbstractEditorView editor = getEditor();
        MateuUI.openView(editor.addEditorViewListener(new EditorViewListener() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onSave() {

            }

            @Override
            public void onSuccessLoad(Data result) {

            }

            @Override
            public void onSuccessSave(Data result) {
                System.out.println("******ONSUCESS*****");
                getForm().set(getId(), getPair(result));
                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        editor.close();
                    }
                });
            }

            @Override
            public void onFailure(Throwable caught) {

            }
        }));

    }

    public void edit(Object id) {
        AbstractEditorView editor = getEditor().setInitialId(id);
        MateuUI.openView(editor.addEditorViewListener(new EditorViewListener() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onSave() {

            }

            @Override
            public void onSuccessLoad(Data result) {

            }

            @Override
            public void onSuccessSave(Data result) {
                System.out.println("******ONSUCESS*****");
                getForm().set(getId(), getPair(result));
                editor.close();
            }

            @Override
            public void onFailure(Throwable caught) {

            }
        }));
    }

    public abstract Pair getPair(Data editorData);

}
