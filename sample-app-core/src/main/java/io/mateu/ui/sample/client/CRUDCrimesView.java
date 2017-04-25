package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.OutputColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.CellStyleGenerator;
import io.mateu.ui.core.shared.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 31/12/16.
 */
public class CRUDCrimesView extends AbstractCRUDView {
    @Override
    public String getSql() {
        String sql = "select id,codetime,address, beat , crimedescr from crimes where id = id ";
        if (!getForm().getData().isEmpty("fn")) {
            sql += " and lower(address) like '%" + getForm().getData().getString("fn").toLowerCase().replaceAll("'", "''") + "%'";
        }
        if (!getForm().getData().isEmpty("ln")) {
            sql += " and lower(crimedescr) like '%" + getForm().getData().getString("ln").toLowerCase().replaceAll("'", "''") + "%'";
        }
        sql += " order by id";
        return sql;
    }

    @Override
    public String getTitle() {
        return "CRUD";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this)
                .add(new TextField("fn", "Address").setRequired(true))
                .add(new TextField("ln", "Crime description"))
                ;
    }

    @Override
    public AbstractEditorView getNewEditorView() {
        return new BaseEditorView() {

            @Override
            public String getServerSideControllerKey() {
                return "crud";
            }

            @Override
            public String getTitle() {
                return "Crime";
            }

            @Override
            public AbstractForm createForm() {
                return new ViewForm(this)
                        .add(new TextField("col2", "Address").setRequired(true))
                        .add(new TextField("col3", "Crime description"))
                        ;
            }
        };
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        return Arrays.asList(
                new OutputColumn("col1", "Code time", 100)
                , new OutputColumn("col2", "Address", 200)
                , new OutputColumn("col3", "Beat", 50)
                , new OutputColumn("col4", "Crime descr.", 200)
        );
    }

    @Override
    public void delete(List<Data> selection, AsyncCallback<Void> callback) {
        MateuUI.getBaseService().execute("delete from crime where id in (" + MateuUI.extractIds(selection) + ")", callback);
    }
}
