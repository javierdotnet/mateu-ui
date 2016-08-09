package mateu.ui.core.app;

import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public abstract class AbstractMenu implements MenuEntry {

    public abstract String getName();

    public abstract List<MenuEntry> getEntries();

}
