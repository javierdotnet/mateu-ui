package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 10/8/16.
 */
public class DateFieldView extends AbstractView {
    @Override
    public String getTitle() {
        return "DateField";
    }

    @Override
    public void build() {
        add(new TextField("_id", "_id"))
                .add(new DateField("fecha", "Fecha"))
                .add(new DateTimeField("fechayhora", "Fecha y hora").setUnmodifiable(true))
                .add(new IntegerField("i", "Entero"))
                .add(new LongField("l", "Long"))
                .add(new DoubleField("d", "Doble"))

        ;
    }

}
