package mateu.ui.core.views;

import mateu.ui.core.components.Component;

/**
 * Created by miguel on 8/8/16.
 */
public class AbstractForm extends FieldContainer {

    public AbstractForm add(Component component) {
        super.add(component);
        return this;
    }


}
