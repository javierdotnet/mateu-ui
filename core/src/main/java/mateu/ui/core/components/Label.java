package mateu.ui.core.components;

/**
 * Created by miguel on 9/8/16.
 */
public class Label extends BaseComponent {

    private final String text;

    public Label(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
