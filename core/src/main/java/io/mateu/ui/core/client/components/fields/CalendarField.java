package io.mateu.ui.core.client.components.fields;

import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.shared.Data;

/**
 * Created by miguel on 9/8/16.
 */
public class CalendarField extends AbstractField<CalendarField> {

    private String nameProperty;

    public CalendarField(String id) {
        super(id);
    }

    public CalendarField(String id, String label, String nameProperty) {
        super(id, label);
        this.nameProperty = nameProperty;
    }

    public AbstractForm getDataForm() {
        AbstractForm f = new AbstractForm() {

        };
        return f;
    }

    public String getNameProperty() {
        return nameProperty;
    }
}
