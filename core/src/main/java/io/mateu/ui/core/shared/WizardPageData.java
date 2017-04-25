package io.mateu.ui.core.shared;

/**
 * Created by miguel on 23/10/16.
 */
public class WizardPageData extends Data {

    public void setAction(Object action) {
        set("_action", action);
    }

    public Object getAction() {
        return get("_action");
    }



}
