package io.mateu.ui.core.shared;

import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import io.mateu.ui.core.client.app.AbstractAction;
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


                    Object o = null;

                    if (vn.contains("$")) {
                        if (vn.split("\\$").length == 2) {
                            Class<?> cl = Class.forName(vn);
                            Constructor<?> c = cl.getDeclaredConstructors()[0];
                            c.setAccessible(true);
                            o = c.newInstance(cl.getEnclosingClass().newInstance());
                        } else if (vn.split("\\$").length == 3) {
                            Class<?> cl = Class.forName(vn);
                            Constructor<?> c = cl.getDeclaredConstructors()[0];
                            c.setAccessible(true);
                            Constructor<?> c2 = cl.getEnclosingClass().getDeclaredConstructors()[0];
                            c2.setAccessible(true);
                            Object[] params = new Object[c2.getParameterCount()];
                            params[0] = cl.getEnclosingClass().getEnclosingClass().newInstance();
                            o = c.newInstance(c2.newInstance(params));
                        } else {
                            return null;
                        }
                    } else {
                        o = Class.forName(vn).newInstance();
                    }

                    if (o instanceof AbstractAction) {

                        ((AbstractAction) o).run();

                    } else if (o instanceof AbstractView) {

                        view = (AbstractView) o;

                        if (view instanceof AbstractCRUDView) {
                            ((AbstractCRUDView) view).addListener(new CRUDListener() {
                                @Override
                                public void openEditor(AbstractEditorView e, boolean inNewTab) {
                                    MateuUI.openView(e, inNewTab);
                                }
                            });
                        }

                    } else {
                        return null;
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
