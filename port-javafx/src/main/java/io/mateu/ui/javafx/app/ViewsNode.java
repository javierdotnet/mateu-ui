package io.mateu.ui.javafx.app;

import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.DataSetterListener;
import io.mateu.ui.core.client.views.ViewListener;
import io.mateu.ui.core.shared.Data;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by miguel on 29/12/16.
 */
public class ViewsNode extends BorderPane {

    private Map<String, ViewTab> tabs = new HashMap<>();

    private final TabPane tabPane;

    public ViewsNode() {
        setCenter(tabPane = new TabPane());
    }


    public void addView(AbstractView view) {

        ViewTab t;

        if (tabs.containsKey(view.getViewId())) {
            t = tabs.get(view.getViewId());
        } else {
            tabPane.getTabs().add(t = new ViewTab(view));
            view.addListener(new ViewListener() {
                @Override
                public void onClose() {
                    tabPane.getTabs().remove(t);
                    tabs.remove(view.getViewId());
                }
            });
            if (view instanceof AbstractEditorView) {
                view.getForm().addDataSetterListener(new DataSetterListener() {
                    @Override
                    public void setted(Data newData) {
                        if (newData.get("_id") != null) {
                            if (!newData.get("_id").equals(((AbstractEditorView) view).getInitialId())) {
                                String oldK = view.getViewId();
                                tabs.remove(oldK);
                                ((AbstractEditorView) view).setInitialId(newData.get("_id"));
                                tabs.put(view.getViewId(), t);
                            }
                        }
                    }
                });
            }
            t.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    tabs.remove(view.getViewId());
                }
            });
            tabs.put(view.getViewId(), t);
        }
        tabPane.getSelectionModel().select(t);

    }
}
