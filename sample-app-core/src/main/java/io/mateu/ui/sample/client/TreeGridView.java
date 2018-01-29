package io.mateu.ui.sample.client;

import com.google.common.collect.Lists;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.TreeGridField;
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
public class TreeGridView extends AbstractView {

    @Override
    public Data initializeData() {
        Data data = new Data();
        List<Data> l = Lists.newArrayList();
        l.add(new Data("_text", "España", "descripcion", "qudhqewduhqwu qudh wqd", "total", 500.0, "_children", Arrays.asList(
                new Data("_text", "Baleares", "descripcion", "wefewf qudh wqd", "total", 1110.3, "_children", Arrays.asList(
                        new Data("_text", "Palma de Mallorca" , "descripcion", "wefewf qudh wqd", "total", 12.3)
                        , new Data("_text", "Alcudia")
                        , new Data("_text", "Son Carrió")
                ))
                , new Data("_text", "Andalucía")
                , new Data("_text", "Madrid")
                , new Data("_text", "Coruña")
        )));
        l.add(new Data("_text", "USA", "descripcion", "wefewf qudh wqd", "total", 150.3));
        l.add(new Data("_text", "Japón", "descripcion", "sdafasfsf qudh wqd", "total", 14.001));

        Data root = new Data("_text", "Mundo", "_children", l);

        data.set("tree", root);
        return data;

    }

    @Override
    public String getTitle() {
        return "TreeGrid";
    }

    @Override
    public void build() {
        add(new TreeGridField("tree", Arrays.asList(
                new TextColumn("descripcion", "descr", 200, true)
                , new OutputColumn("total", "Total", 100)
                , new IntegerColumn("b", "b", 100, true)
                , new ComboBoxColumn("cb", "cb", 100, Arrays.asList(
                        new Pair(1, "1")
                        , new Pair(2, "2")
                        , new Pair(3, "3")
                        , new Pair(4, "4")
                ))
                , new SqlComboBoxColumn("scb", "scb", 100, "select id, name from currency order by 2")
                , new LinkColumn("e", "e", 100) {
                    @Override
                    public void run(Data data) {
                        MateuUI.alert("Hello!");
                    }
                }
        )) {
            @Override
            public AbstractForm getDataForm(Data initialData) {
                AbstractForm f = new AbstractForm() {
                };
                f.add(new TextField("descripcion", "A"));
                f.add(new IntegerField("b", "B"));
                return f;
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
