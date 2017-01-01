package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 9/8/16.
 */
public class AboutArea extends AbstractArea {
    public String getName() {
        return "About";
    }

    public List<AbstractModule> getModules() {
        return Arrays.asList((AbstractModule) new AboutModule());
    }
}
