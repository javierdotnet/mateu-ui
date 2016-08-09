package mateu.ui.sample.app;

import mateu.ui.core.app.AbstractArea;
import mateu.ui.core.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ComponentsArea extends AbstractArea {
    public String getName() {
        return "Components";
    }

    public List<AbstractModule> getModules() {
        return Arrays.asList((AbstractModule) new ComponentsModule());
    }
}
