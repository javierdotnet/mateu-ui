package io.mateu.ui.core.client.app;


import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractArea {

    private boolean publicAccess = false;
    private String name;

    public AbstractArea(String name) {
        this.name = name;
    }

    public AbstractArea(String name, boolean publicAccess) {
        this.name = name; this.publicAccess = publicAccess;
    }

    public String getName() {
        return name;
    }

    public abstract List<AbstractModule> getModules();

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }
}
