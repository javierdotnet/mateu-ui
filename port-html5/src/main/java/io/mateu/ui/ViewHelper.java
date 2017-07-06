package io.mateu.ui;

import de.iterable.teavm.jquery.types.JQueryEventHandler;
import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.views.AbstractView;

import java.util.Map;

/**
 * Created by miguel on 2/7/17.
 */
public class ViewHelper {

    public static void build(StringBuffer h, Map<String, JQueryEventHandler> listeners, AbstractView v) {

        //v.build();

        for (Component c : v.getForm().getComponentsSequence()) {
            if (c instanceof TextField) {
                Bridge.log(">>>> text field");
                append(h, listeners, (TextField) c);
            } else {
                Bridge.log(">>>> other");
                append(h, listeners, c);
            }
        }

    }

    private static void append(StringBuffer h, Map<String, JQueryEventHandler> listeners, TextField c) {
        h.append("<label for='" + c.getId() + "'>" + c.getLabel().getText() + "</label>");
        h.append("<input id='" + c.getId() + "' type='text' rv-value='" + c.getId() + "'>");

        Bridge.log("added text field " + c);
    }


    private static void append(StringBuffer h, Map<String, JQueryEventHandler> listeners, Component c) {
        Bridge.log("added component " + c);
    }
}
