package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewForm;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

/**
 * Created by miguel on 10/8/16.
 */
public class ComboBoxFieldView extends AbstractView {

    @Override
    public Data initializeData() {
        Data d = super.initializeData();
        d.set("field1", new Pair(0, "Laura Steel"));
        return d;
    }

    @Override
    public String getTitle() {
        return "ComboBoxField";
    }

    @Override
    public void build() {
        add(new AutocompleteField("field1", "field1", "a", "b", "c", "d", "e", "f", "g", "h"));
        add(new ComboBoxField("field1", "field1", "a", "b", "c", "d"));
        add(new SqlComboBoxField("field2", "field1", "select id, firstname || ' ' || lastname from customer order by 2"));
        add(new SqlAutocompleteField("field3", "field1", "select id, firstname || ' ' || lastname from customer order by 2"));
    }
}
