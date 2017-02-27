package io.mateu.ui.core.client.app;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractAction extends AbstractExecutable implements MenuEntry {

    private String name;


    public AbstractAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    };

}
