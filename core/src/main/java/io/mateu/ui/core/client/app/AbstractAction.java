package io.mateu.ui.core.client.app;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractAction implements MenuEntry, Runnable {

    private String name;
    private boolean callOnEnterKeyPressed = false;


    public AbstractAction(String name) {
        this.name = name;
    }

    public AbstractAction(String name, boolean callOnEnterKeyPressed) {
        this.name = name;
        this.callOnEnterKeyPressed = callOnEnterKeyPressed;
    }


    public String getName() {
        return name;
    };

    public boolean isCallOnEnterKeyPressed() {
        return callOnEnterKeyPressed;
    }

    public AbstractAction setCallOnEnterKeyPressed(boolean callOnEnterKeyPressed) {
        this.callOnEnterKeyPressed = callOnEnterKeyPressed;
        return this;
    }
}
