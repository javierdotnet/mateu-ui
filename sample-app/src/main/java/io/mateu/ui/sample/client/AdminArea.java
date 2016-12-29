package io.mateu.ui.sample.client;

import io.mateu.ui.core.app.AbstractModule;
import io.mateu.ui.core.app.AbstractArea;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AdminArea extends AbstractArea {
    public String getName() {
        return "Admin";
    }

    public List<AbstractModule> getModules() {
        return Arrays.asList((AbstractModule) new AdminModule());
    }
}
