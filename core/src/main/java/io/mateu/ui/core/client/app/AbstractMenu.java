package io.mateu.ui.core.client.app;

import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractMenu implements MenuEntry {

    private String id = UUID.randomUUID().toString();

    private String name;

    public AbstractMenu(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract List<MenuEntry> getEntries();


    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
