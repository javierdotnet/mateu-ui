package io.mateu.ui.vaadin;

import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 2/1/17.
 */
public class AppProvider {

    public static AbstractApplication getApp() {
        return new AbstractApplication() {
            @Override
            public String getName() {
                return "xxxx";
            }

            @Override
            public List<AbstractArea> getAreas() {
                return new ArrayList<AbstractArea>();
            }
        };
    }

}
