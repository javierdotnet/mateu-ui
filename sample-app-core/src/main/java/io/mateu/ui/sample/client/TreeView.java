package io.mateu.ui.sample.client;

import com.google.common.collect.Lists;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.TreeField;
import io.mateu.ui.core.client.components.fields.grids.columns.*;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 28/1/17.
 */
public class TreeView extends AbstractView {

    @Override
    public Data initializeData() {
        Data data = new Data();
        List<Data> l = Lists.newArrayList();
        l.add(new Data("_text", "España", "_children", Arrays.asList(
                new Data("_text", "Baleares", "_children", Arrays.asList(
                        new Data("_text", "Palma de Mallorca")
                        , new Data("_text", "Alcudia")
                        , new Data("_text", "Son Carrió")
                ))
                , new Data("_text", "Andalucía")
                , new Data("_text", "Madrid")
                , new Data("_text", "Coruña")
        )));
        l.add(new Data("_text", "Inglaterra"));
        l.add(new Data("_text", "Japón"));

        Data root = new Data("_text", "Mundo", "_children", l);

        data.set("tree", root);
        return data;
    }

    @Override
    public String getTitle() {
        return "Tree";
    }

    @Override
    public void build() {
        add(new TreeField("tree", "Tree") {
            @Override
            public void open(Data data) {
                MateuUI.alert("_text = " + data.get("_text"));
            }
        });
    }

    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> l = super.createActions();

        l.add(new AbstractAction("Set data") {
            @Override
            public void run() {

                List<Data> l = Lists.newArrayList();
                l.add(new Data("_text", "wedewdwe", "_children", Arrays.asList(
                        new Data("_text", "aCDASSASF", "_children", Arrays.asList(
                                new Data("_text", "Palma de Mallorca")
                                , new Data("_text", "Alcudia")
                                , new Data("_text", "Son Carrió")
                        ))
                        , new Data("_text", "Andalucía")
                )));
                l.add(new Data("_text", "ww"));
                l.add(new Data("_text", "kk"));

                Data root = new Data("_text", "Mundo", "_children", l);


                getForm().set("tree", root);

            }
        });


        l.add(new AbstractAction("Clear data") {
            @Override
            public void run() {

                getForm().set("tree", null);

            }
        });

        return l;
    }
}
