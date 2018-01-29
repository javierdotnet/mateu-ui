package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 10/8/16.
 */
public class CalendarFieldView extends AbstractView {
    @Override
    public String getTitle() {
        return "CalendarField";
    }

    @Override
    public void build() {
        add(new TextField("_id", "_id"))
                .add(new CalendarField("calendario", "Calendario", null) {
                    @Override
                    public AbstractForm getDataForm() {
                        AbstractForm f = super.getDataForm();

                        f.add(new TextField("_text", "Nombre de la fare"));
                        f.add(new IntegerField("otro", "Otro campo"));

                        return f;
                    }
                })

        ;
    }


    @Override
    public List<AbstractAction> createActions() {
        List<AbstractAction> l = super.createActions();

        l.add(new AbstractAction("Clear data") {
            @Override
            public void run() {
                set("calendario", null);
            }
        });

        l.add(new AbstractAction("Set data") {
            @Override
            public void run() {

                Data data = new Data();

                data.set("_metadata", new Data());
                data.set("_fromdate", LocalDate.of(2018, 04, 10));
                data.set("_todate", LocalDate.of(2018, 07, 05));

                List<Data> options = new ArrayList<>();
                Data o0;
                options.add(o0 = new Data("_text", "Temporada baja", "__id", UUID.randomUUID().toString()));
                Data o1;
                options.add(o1 = new Data("_text", "Temporada alta", "__id", UUID.randomUUID().toString()));
                data.set("_options", options);

                List<Data> vs = new ArrayList<>();

                vs.add(new Data("_key", LocalDate.of(2018, 05, 12), "_value", o0.get("__id")));
                vs.add(new Data("_key", LocalDate.of(2018, 05, 13), "_value", o0.get("__id")));
                vs.add(new Data("_key", LocalDate.of(2018, 05, 14), "_value", o0.get("__id")));
                vs.add(new Data("_key", LocalDate.of(2018, 05, 15), "_value", o0.get("__id")));
                vs.add(new Data("_key", LocalDate.of(2018, 05, 16), "_value", o0.get("__id")));

                vs.add(new Data("_key", LocalDate.of(2018, 04, 30), "_value", o1.get("__id")));
                vs.add(new Data("_key", LocalDate.of(2018, 05, 01), "_value", o1.get("__id")));
                vs.add(new Data("_key", LocalDate.of(2018, 05, 02), "_value", o1.get("__id")));

                data.set("_values", vs);


                set("calendario", data);

            }
        });

        return l;
    }
}
