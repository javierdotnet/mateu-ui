package io.mateu.ui.sample.app;

import io.mateu.ui.core.app.AbstractArea;
import io.mateu.ui.core.app.AbstractModule;

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
