package io.mateu.ui.core.shared;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.core.client.views.*;

import java.lang.reflect.Constructor;


@AutoService(io.mateu.ui.core.shared.ViewProvider.class)
public class MiViewProvider implements io.mateu.ui.core.shared.ViewProvider {

    @Override
    public String getViewName(String viewAndParameters) {
        System.out.println("MiViewProvider.getViewName(" + viewAndParameters + ")");
        if (Strings.isNullOrEmpty(viewAndParameters)) return null;
        String vn = viewAndParameters;
        if (vn.startsWith("area")) vn = vn.substring(vn.indexOf("..") + "..".length());
        if (vn.startsWith("pos")) vn = vn.substring(vn.indexOf("..") + "..".length());
        if (!(vn.startsWith("mui..") || vn.startsWith("menu.."))) return null;
        String p = viewAndParameters;
        if (p.contains("?")) p = p.substring(0, p.indexOf("?"));
        if (p.contains("/")) p = p.substring(0, p.indexOf("/"));
        return p;
    }

    @Override
    public AbstractView getView(String viewName) {
        System.out.println("MiViewProvider.getView(" + viewName + ")");

        String vn = viewName;
        if (vn.startsWith("area")) {
            int pos = Integer.parseInt(vn.substring("area".length(), vn.indexOf("..")));
            MateuUI.getApp().setArea(MateuUI.getApp().getAreas().get(pos));
            vn = vn.substring(vn.indexOf("..") + "..".length());
        }
        if (vn.startsWith("pos")) {
            String pos = vn.substring("area".length(), vn.indexOf(".."));
            MateuUI.getApp().setPosicion(MateuUI.getApp().getMenu(pos));

            vn = vn.substring(vn.indexOf("..") + "..".length());
        }
        if (!(vn.startsWith("mui..") || vn.startsWith("menu.."))) return null;

        AbstractView view = null;
        try {

            if (!Strings.isNullOrEmpty(vn)) {

                if (vn.startsWith("mui..")) {
                    vn = vn.replaceFirst("mui\\.\\.", "");

                    if (vn.contains("$")) {
                        Class<?> cl = Class.forName(vn);
                        Constructor<?> c = cl.getDeclaredConstructors()[0];
                        c.setAccessible(true);
                        view = (AbstractView) c.newInstance(cl.getEnclosingClass().newInstance());
                    } else {
                        view = (AbstractView) Class.forName(vn).newInstance();
                    }

                    if (view instanceof AbstractCRUDView) {
                        ((AbstractCRUDView) view).addListener(new CRUDListener() {
                            @Override
                            public void openEditor(AbstractEditorView e, boolean inNewTab) {
                                MateuUI.openView(e, inNewTab);
                            }
                        });
                    }

                } else if (vn.startsWith("menu..")) {
                    String menuId = vn.substring("menu..".length());
                    MenuEntry e = MateuUI.getApp().getMenu(menuId);
                    view = new MenuView(e.getName(), menuId);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
