package io.mateu.ui.vaadin.components;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.DataSetterListener;
import io.mateu.ui.core.client.views.ViewListener;
import io.mateu.ui.core.shared.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by miguel on 2/1/17.
 */
public class ViewsComponent extends TabSheet {

    private Map<String, Tab> tabs = new HashMap<>();
    private Map<Tab, String> keys = new HashMap<>();

    public ViewsComponent() {
        setSizeUndefined();
    }

    public void addView(AbstractView view) {

        if (tabs.containsKey(view.getViewId())) {
            Tab t = tabs.get(view.getViewId());
            removeTab(t);
            Tab x = tabs.remove(view.getViewId());
            keys.remove(x);
        }

        {
            Tab t = addTab(new ViewLayout(view), view.getTitle());
            t.setClosable(true);

            view.addListener(new ViewListener() {
                @Override
                public void onClose() {
                    removeTab(t);
                    Tab x = tabs.remove(view.getViewId());
                    keys.remove(x);
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
                                keys.remove(t);
                                ((AbstractEditorView) view).setInitialId(newData.get("_id"));
                                tabs.put(view.getViewId(), t);
                                keys.put(t, view.getViewId());
                            }
                        }
                    }

                    @Override
                    public void setted(String k, Object v) {

                    }

                    @Override
                    public void idsResetted() {

                    }

                    @Override
                    public void cleared() {

                    }
                });
            }

            setCloseHandler(new CloseHandler() {
                @Override
                public void onTabClose(TabSheet tabSheet, Component tabContent) {
                    Tab tab = tabSheet.getTab(tabContent);
                    Notification.show("Closing " + tab.getCaption());

                    // We need to close it explicitly in the handler
                    tabSheet.removeTab(tab);
                    System.out.println("onTabClose()");
                    tabs.remove(keys.get(tab));
                    keys.remove(tab);

                }
            });
            tabs.put(view.getViewId(), t);
            keys.put(t, view.getViewId());
            setSelectedTab(t);
        }
    }
}
