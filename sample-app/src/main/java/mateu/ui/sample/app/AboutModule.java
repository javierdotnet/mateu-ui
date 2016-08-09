package mateu.ui.sample.app;

import mateu.ui.core.app.AbstractAction;
import mateu.ui.core.app.AbstractModule;
import mateu.ui.core.app.ClientSideHelper;
import mateu.ui.core.app.MenuEntry;
import mateu.ui.core.views.AbstractView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutModule extends AbstractModule {
    public List<MenuEntry> getMenu() {
        List<MenuEntry> l = new ArrayList<>();
        l.add(new AbstractAction() {
            @Override
            public String getName() {
                return "About us";
            }

            @Override
            public void run() {
                ClientSideHelper.openView(new AboutView());
            }
        });
        return l;
    }
}
