package io.mateu.ui.sample.client;

import io.mateu.ui.core.app.AbstractApplication;
import io.mateu.ui.core.app.AbstractArea;
import io.mateu.ui.core.app.App;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
@App
public class SampleApp extends AbstractApplication
{

    public String getName() {
        return "Sample Application";
    }

    public List<AbstractArea> getAreas() {
        return Arrays.asList(new AboutArea(), new ComponentsArea(), new AdminArea(), new CommunicationArea());
    }
}
