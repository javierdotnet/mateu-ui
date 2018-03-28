package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class ComponentsArea extends AbstractArea {

    public ComponentsArea() {
        super("Components");
    }

    public List<AbstractModule> buildModules() {
        return Arrays.asList((AbstractModule) new ComponentsModule());
    }

    @Override
    public boolean isPublicAccess() {
        return true;
    }
}
