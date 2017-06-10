package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.fields.ListSelectionField;
import io.mateu.ui.core.client.components.fields.SqlListSelectionField;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.core.shared.PairList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 10/8/16.
 */
public class ListSelectionFieldView extends AbstractView {

    @Override
    public Data initializeData() {
        Data d = super.initializeData();
        List<Data> l = new ArrayList<>();
        l.add(new Data("_value", "123.21"));
        l.add(new Data("_value", "Hola!", "_css", "rojo"));
        l.add(new Data("_value", "80", "_css", "verde"));
        d.set("_badges", l);
        PairList pl = new PairList();
        pl.getValues().add(new Pair("2", "22"));
        d.set("field1", pl);
        return d;
    }

    @Override
    public String getTitle() {
        return "TextField";
    }

    @Override
    public List<AbstractAction> getActions() {
        List<AbstractAction> as = super.getActions();
        as.add(new AbstractAction("Ver data") {
            @Override
            public void run() {
                System.out.println(getForm().getData());
            }
        });
        return as;
    }

    @Override
    public void build() {
        add(new ListSelectionField("field1", "Label1", "1", "11", "2", "22", "3", "33"))
                .add(new SqlListSelectionField("field2", "Label2", "select id, firstname || ' ' || lastname from customer order by 2"))
        ;
    }
}
