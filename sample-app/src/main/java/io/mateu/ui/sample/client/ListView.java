package io.mateu.ui.sample.client;

import io.mateu.ui.core.app.AbstractExecutable;
import io.mateu.ui.core.app.AsyncCallback;
import io.mateu.ui.core.app.MateuUI;
import io.mateu.ui.core.components.fields.TextField;
import io.mateu.ui.core.components.fields.grids.AbstractColumn;
import io.mateu.ui.core.components.fields.grids.TextColumn;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.views.AbstractForm;
import io.mateu.ui.core.views.AbstractListView;
import io.mateu.ui.core.views.AbstractView;
import io.mateu.ui.core.views.ViewForm;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 30/12/16.
 */
public class ListView extends AbstractListView {
    @Override
    public String getTitle() {
        return "ListView";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).add(new TextField("q", "Filter"));
    }


    @Override
    public List<AbstractColumn> createColumns() {
        return Arrays.asList(new TextColumn("col1", "Col 1", 100, false));
    }

    @Override
    public void rpc(Data parameters, AsyncCallback<Data> callback) {
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MateuUI.run(new AbstractExecutable() {
            @Override
            public void run() {

                Data d = new Data("q", "AAAAAAAA");
                d.getList("_data").add(new Data("col1", "XXXX", "col2", "YYYYY"));
                d.set("_data_currentpageindex", getForm().getData().getInt("_data_currentpageindex"));
                d.set("_data_pagecount", 3);
                callback.onSuccess(d);
            }
        });
    }
}
