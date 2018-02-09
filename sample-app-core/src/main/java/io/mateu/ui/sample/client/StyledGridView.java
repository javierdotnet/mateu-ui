package io.mateu.ui.sample.client;

import com.google.common.collect.Lists;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.TextField;
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
public class StyledGridView extends AbstractView {

    @Override
    public Data initializeData() {
        Data data = new Data();
        List<Data> l = Lists.newArrayList();
        l.add(new Data("a", "hdwhdehw", "b", new Data("_css", "warning", "_text", "aaaa")));
        l.add(new Data("a", "uhwihduehd", "b", new Data("_css", "warning", "_text", "bbbb")));
        l.add(new Data("a", "qwt6qwtw", "b", new Data("_css", "warning", "_text", "cccc")));
        data.set("g", l);
        return data;
    }

    @Override
    public String getTitle() {
        return "Grid";
    }

    @Override
    public void build() {
        add(new GridField("g", Arrays.asList(
                new TextColumn("a", "a", 100, true)
                , new DataColumn("b", "b", 100) {
                    @Override
                    public void run(Data data) {

                    }
                })));
    }
}
