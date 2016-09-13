package mateu.ui.sample.app;

import mateu.ui.core.app.*;
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
                MateuUI.getClientSideHelper().openView(new AboutView());
            }
        });
        return l;
    }
}
