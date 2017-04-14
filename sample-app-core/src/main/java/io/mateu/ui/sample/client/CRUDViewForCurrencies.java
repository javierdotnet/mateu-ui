package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.PKField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.OutputColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 31/12/16.
 */
public class CRUDViewForCurrencies extends AbstractCRUDView {
    @Override
    public String getSql() {
        return "select id, name, '' || decimals from currency order by id";
    }

    @Override
    public String getTitle() {
        return "CRUD";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).add(new TextField("f", "Filter"));
    }

    @Override
    public AbstractEditorView getNewEditorView() {
        return new BaseEditorView() {

            @Override
            public String getServerSideControllerKey() {
                return "currencycrud";
            }

            @Override
            public String getTitle() {
                return "Currency";
            }

            @Override
            public AbstractForm createForm() {
                return new ViewForm(this)
                        .add(new PKField("id", "Code"))
                        .add(new TextField("name", "Name"))
                        .add(new IntegerField("decimals", "Decimals"))
                        ;
            }
        };
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        return Arrays.asList(new OutputColumn("col1", "Name", 100)
        , new OutputColumn("col2", "Decimals", 100)
        );
    }

    @Override
    public void delete(List<Data> selection, AsyncCallback<Void> callback) {
        MateuUI.getBaseService().execute("delete from currency where id in (" + MateuUI.extractIds(selection) + ")", callback);
    }
}
