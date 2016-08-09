package mateu.ui.sample.app;

import mateu.ui.core.app.AbstractApplication;
import mateu.ui.core.app.AbstractArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class SampleApp extends AbstractApplication
{

    public String getName() {
        return "Sample Application";
    }

    public List<AbstractArea> getAreas() {
        return Arrays.asList(new AboutArea(), new ComponentsArea(), new AdminArea());
    }
}
