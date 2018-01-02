package com.vaadin.tests.themes.valo;

import com.google.common.base.Strings;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.vaadin.ViewLayout;

import java.lang.reflect.Constructor;

public class MiViewProvider implements ViewProvider {

    @Override
    public String getViewName(String viewAndParameters) {
        System.out.println("getViewName(" + viewAndParameters + ")");
        String p = viewAndParameters;
        if (p.contains("?")) p = p.substring(0, p.indexOf("?"));
        if (p.contains("/")) p = p.substring(0, p.indexOf("/"));
        return p;
    }

    @Override
    public View getView(String viewName) {
        System.out.println("getView(" + viewName + ")");
        ViewLayout v = null;
        try {

            if (!Strings.isNullOrEmpty(viewName)) {
                AbstractView view = null;

                if (viewName.contains("$")) {
                    Class<?> cl = Class.forName(viewName);
                    Constructor<?> c = cl.getDeclaredConstructors()[0];
                    c.setAccessible(true);
                    view = (AbstractView) c.newInstance(cl.getEnclosingClass().newInstance());
                } else {
                    view = (AbstractView) Class.forName(viewName).newInstance();
                }

                v = new ViewLayout(view);

                if (view instanceof AbstractCRUDView) {
                    ((AbstractCRUDView)view).addListener(new CRUDListener() {
                        @Override
                        public void openEditor(AbstractEditorView e) {
                            MateuUI.openView(e);
                        }
                    });
                }

                if (view instanceof AbstractEditorView) {

                    view.getForm().addDataSetterListener(new DataSetterListener() {
                        @Override
                        public void setted(Data newData) {
                            if (newData.get("_id") != null) {
                                /*
                                if (!newData.get("_id").equals(((AbstractEditorView) view).getInitialId())) {
                                    String oldK = view.getViewId();

                                    Integer oldContView = ui.viewsIdsInNavigator.remove(oldK);
                                    ((AbstractEditorView) view).setInitialId(newData.get("_id"));
                                    ui.viewsIdsInNavigator.put(view.getViewId(), oldContView);
                                }
                                    */
                            }
                        }

                        @Override
                        public void setted(String k, Object v) {

                        }

                        @Override
                        public void idsResetted() {

                        }
                    });
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }
}
