package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.DataViewerField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractDialog;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 28/1/17.
 */
public class DataViewerView extends AbstractView {

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> as = super.createActions();
        as.add(new AbstractAction("Open dialog") {
            @Override
            public void run() {
                MateuUI.openView(new AbstractDialog() {
                    @Override
                    public void onOk(Data data) {

                    }

                    @Override
                    public String getTitle() {
                        return "data";
                    }

                    @Override
                    public AbstractForm createForm() {
                        return new ViewForm(this).add(new TextField("f1", "Name"));
                    }
                });
            }
        });

        as.add(new AbstractAction("Open dialog with data") {
            @Override
            public void run() {
                MateuUI.openView(new AbstractDialog() {
                    @Override
                    public void onOk(Data data) {

                    }

                    @Override
                    public String getTitle() {
                        return "Metadata";
                    }

                    @Override
                    public Data initializeData() {

                        return new Data("_metadata", new Data(DataViewerView.this.getForm().getData()));
                    }

                    @Override
                    public AbstractForm createForm() {
                        return new ViewForm(this).setLastFieldMaximized(true).add(new DataViewerField("_metadata"));
                    }
                });
            }
        });

        return as;
    }

    @Override
    public String getTitle() {
        return "Data viewer";
    }

    @Override
    public Data initializeData() {
        Data data = super.initializeData();
        Data d = new Data();
        d.set("aa", "aaa");
        d.set("bb", "bbb");
        d.set("cc", true);
        d.set("dd", null);
        d.set("ee", Arrays.asList(
           new Data("a", "aa")
                , new Data("b", "bb")
                , new Data("c", "cc")
                , new Data("x", Arrays.asList(
                        new Data("a", "aa")
                        , new Data("b", "bb")
                        , new Data("c", "cc")
                ))
        ));
        d.set("w", "www");

        data.set("data", d);

        return data;
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).setLastFieldMaximized(true).add(new DataViewerField("data"));
    }
}
