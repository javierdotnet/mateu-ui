package io.mateu.ui.sample.client;

import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.App;
import io.mateu.ui.core.client.views.AbstractView;

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

    @Override
    public AbstractView getPublicHome() {
        return new DataViewerView();
    }
}
