package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 27/12/16.
 */
public class CommunicationArea extends AbstractArea {
    @Override
    public String getName() {
        return "Comunicación";
    }

    @Override
    public List<AbstractModule> getModules() {
        return Arrays.asList((AbstractModule) new CommunicationModule());
    }
}
