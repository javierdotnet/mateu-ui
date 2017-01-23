package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.TextColumn;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.client.views.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 31/12/16.
 */
public class CRUDView extends AbstractCRUDView {
    @Override
    public String getSql() {
        String sql = "select id, firstname, lastname, street from customer where id = id ";
        if (!getForm().getData().isEmpty("f")) {
            sql += " and lower(firstname) like '%" + getForm().getData().getString("f").toLowerCase().replaceAll("'", "''") + "%'";
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
        return new ViewForm(this).add(new TextField("f", "Filter").setRequired(true));
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
                return "Customer";
            }

            @Override
            public AbstractForm createForm() {
                return new ViewForm(this)
                        .add(new TextField("firstname", "First name"))
                        .add(new TextField("lastname", "Last name"))
                        .add(new TextField("street", "Street"))
                        ;
            }
        };
    }

    @Override
    public List<AbstractColumn> createExtraColumns() {
        return Arrays.asList(new TextColumn("col1", "First Name", 100, false)
        , new TextColumn("col2", "Last Name", 100, false)
                , new TextColumn("col3", "Street", 200, false));
    }

    @Override
    public void delete(List<Data> selection, AsyncCallback<Void> callback) {
        MateuUI.getBaseService().execute("delete from customer where id in (" + MateuUI.extractIds(selection) + ")", callback);
    }
}
