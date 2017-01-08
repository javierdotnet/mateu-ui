package io.mateu.ui.core.client.app;

import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractMenu implements MenuEntry {

    private String name;

    public AbstractMenu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract List<MenuEntry> getEntries();

}
