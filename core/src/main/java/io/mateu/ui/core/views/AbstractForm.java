package io.mateu.ui.core.views;

import io.mateu.ui.core.app.MateuUI;
import io.mateu.ui.core.components.Component;
import io.mateu.ui.core.components.fields.AbstractField;
import io.mateu.ui.core.data.DataContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractForm extends FieldContainer {

    public AbstractForm add(Component component) {
        super.add(component);
        return this;
    }

    public abstract DataContainer getData();

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        getComponentsSequence().forEach((c) -> {
            if (c instanceof AbstractField) {
                AbstractField f = (AbstractField) c;
                if (f.isRequired() && getData().estaVacio(f.getId())) errors.add("Field " + f.getLabel() + " is required.");
            }
        });

        return errors;
    }

}
