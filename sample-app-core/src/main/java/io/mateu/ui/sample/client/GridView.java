package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.grids.columns.*;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Arrays;

/**
 * Created by miguel on 28/1/17.
 */
public class GridView extends AbstractView {
    @Override
    public String getTitle() {
        return "Grid";
    }

    @Override
    public AbstractForm createForm() {
        return new ViewForm(this).setLastFieldMaximized(true)
                .add(new GridField("g", Arrays.asList(
                        new TextColumn("a", "a", 100, true)
                        , new IntegerColumn("b", "b", 100, true)
                        , new DoubleColumn("c", "c", 100, true)
                        , new CheckBoxColumn("d", "d", 100, true)
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
                        , new TextColumn("z", "a", 100, true)
                )));
    }
}
