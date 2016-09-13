package mateu.ui.core.views;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractView {

    private AbstractForm form;

    public abstract String getTitle();

    public abstract AbstractForm createForm();

    public AbstractForm getForm() {
        if (form == null) form = createForm();
        return form;
    }
}
