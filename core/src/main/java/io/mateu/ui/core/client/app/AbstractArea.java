package io.mateu.ui.core.client.app;


import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractArea {

    private String name;

    public AbstractArea(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract List<AbstractModule> getModules();
}
