package mateu.vaadin.tests;

import static org.junit.Assert.*;

import io.mateu.ui.core.shared.MiViewProvider;
import io.mateu.ui.core.client.views.AbstractView;
import org.junit.Test;

public class MiViewProviderTest {

    @Test
    public void test() {

        MiViewProvider p = new MiViewProvider();

/*        AbstractView v = p.getView(p.getViewName(new TestView() {

        }.getViewId()));*/

        //assertNotNull(v);

        assertNotNull(p);

    }

}
