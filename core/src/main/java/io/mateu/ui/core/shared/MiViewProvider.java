package io.mateu.ui.core.shared;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.*;

import java.lang.reflect.Constructor;


@AutoService(io.mateu.ui.core.shared.ViewProvider.class)
public class MiViewProvider implements io.mateu.ui.core.shared.ViewProvider {

    @Override
    public String getViewName(String viewAndParameters) {
        System.out.println("MiViewProvider.getViewName(" + viewAndParameters + ")");
        if (viewAndParameters != null && !viewAndParameters.startsWith("mui..")) return null;
        String p = viewAndParameters;
        if (p.contains("?")) p = p.substring(0, p.indexOf("?"));
        if (p.contains("/")) p = p.substring(0, p.indexOf("/"));
        return p;
    }

    @Override
    public AbstractView getView(String viewName) {
        System.out.println("MiViewProvider.getView(" + viewName + ")");

        AbstractView view = null;
        try {

            if (!Strings.isNullOrEmpty(viewName)) {

                if (viewName.startsWith("mui..")) viewName = viewName.replaceFirst("mui\\.\\.", "");

                if (viewName.contains("$")) {
                    Class<?> cl = Class.forName(viewName);
                    Constructor<?> c = cl.getDeclaredConstructors()[0];
                    c.setAccessible(true);
                    view = (AbstractView) c.newInstance(cl.getEnclosingClass().newInstance());
                } else {
                    view = (AbstractView) Class.forName(viewName).newInstance();
                }

                if (view instanceof AbstractCRUDView) {
                    ((AbstractCRUDView) view).addListener(new CRUDListener() {
                        @Override
                        public void openEditor(AbstractEditorView e) {
                            MateuUI.openView(e);
                        }
                    });
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
