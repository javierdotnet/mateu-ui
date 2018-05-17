package io.mateu.ui.core.shared;

import com.google.auto.service.AutoService;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;


@AutoService(io.mateu.ui.core.shared.ViewProvider.class)
public class MiViewProvider implements io.mateu.ui.core.shared.ViewProvider {

    public static Map<Object, Object> parse(String viewAndParameters) {
        Map<Object, Object> data = new HashMap<>();
        if (viewAndParameters == null) return data;

        if ("".equals(viewAndParameters)) {
            data.put("selector", "home");
        } else if ("nav".equals(viewAndParameters)) {
            data.put("selector", "nav");
        } else if ("searchinapp".equals(viewAndParameters)) {
            data.put("selector", "searchinapp");
        } else if ("favourites".equals(viewAndParameters)) {
            data.put("selector", "favourites");
        } else if ("lastedited".equals(viewAndParameters)) {
            data.put("selector", "lastedited");
        } else {

            String[] ts = viewAndParameters.split("/");

            String coords = "";

            int pos = 0;
            // la posición 0 es el id del área
            if (ts.length > 0) {
                AbstractArea area = MateuUI.getApp().getArea(ts[0]);
                if (area != null) {
                    data.put("area", area);

                    if (!"".equals(coords)) coords += "/";
                    coords += ts[pos];
                    pos++;
                }
            }
            // buscamos hasta completar una opción de menú que no sea un submenú
            boolean found = false;
            int posx = pos;
            String mid = "";
            MenuEntry  m = null;
            while (posx < ts.length && (!found || m != null)) {
                if (!"".equals(mid)) mid += "/";
                mid += ts[posx];
                m = MateuUI.getApp().getMenu(mid);
                if (m != null) {

                    data.put("menu", m);

                    found = true;
                    pos = posx++ + 1;

                    if (!"".equals(coords)) coords += "/";
                    coords += mid;

                } else posx++;
            }

            // el siguiente elemento lo utilizamos para saber si es nuestro
            if (pos < ts.length) {
                String d = ts[pos];
                data.put("selector", d);
                if (!"".equals(coords)) coords += "/";
                coords += d;
            }

            data.put("resto", viewAndParameters.substring(coords.length()));
        }

        return data;
    }

    @Override
    public String getViewName(String viewAndParameters) {
        System.out.println("MiViewProvider.getViewName(" + viewAndParameters + ")");
        if (viewAndParameters == null) return null;

        Map<Object, Object> data = parse(viewAndParameters);

        // el siguiente elemento lo utilizamos para saber si es nuestro
        String selector = (String) data.get("selector");
        if (selector != null) {
            if (!(selector.equals("mui") || selector.equals("changearea") || selector.equals("areahome")
                    || selector.equals("menuhome") || selector.equals("home")
                    || selector.equals("nav") || selector.equals("searchinapp") || selector.equals("favourites") || selector.equals("lastedited"))) {
                if (data.get("area") == null && data.get("menu") == null) {
                    return "prohibido";
                } else return null;
            } else return viewAndParameters;
        } else return null;

    }

    @Override
    public AbstractView getView(String viewName) {
        System.out.println("MiViewProvider.getView(" + viewName + ")");

        AbstractView view = null;

        Map<Object, Object> data = parse(viewName);


        if ("prohibido".equals(viewName)) return new ForbiddenView();


            try {

                String selector = (String) data.get("selector");

                if (!Strings.isNullOrEmpty(selector)) {

                    if ("mui".equals(selector)) {

                        Object o = null;

                        String vn = (String) data.get("resto");
                        if (vn.startsWith("/")) vn = vn.substring(1);
                        String parametros = "";
                        if (vn.contains("?")) {
                            parametros = vn.substring(vn.indexOf("?") + 1);
                            vn = vn.substring(0, vn.indexOf("?"));
                        }
                        if (vn.contains("/")) {
                            parametros = vn.substring(vn.indexOf("/") + 1);
                            vn = vn.substring(0, vn.indexOf("/"));
                        }


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

                            view.setGranted(data.get("area") != null && data.get("menu") != null);

                            view.setParametros(parametros);

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
                    } else if ("home".equals(selector)) {
                        AbstractApplication app = MateuUI.getApp();

                        boolean autentico = app.getUserData() != null;

                        if (autentico) {
                            // buscamos la home privada
                            view = app.getPrivateHome();
                        } else {
                            // buscamos la home pública
                            view = app.getPublicHome();
                        }

                        if (view != null) view.setGranted(true);

                    } else if ("menuhome".equals(selector)) {
                        MenuEntry e = (MenuEntry)data.get("menu");
                        view = new MenuView(e.getName(), e.getId());
                        view.setGranted(true);
                    } else if ("changearea".equals(selector)) {
                        view = new ChangeAreaView("Change app area", (AbstractArea)data.get("area"));
                        view.setGranted(true);
                    } else if ("areahome".equals(selector)) {
                        view = new AreaHomeView(((AbstractArea)data.get("area")).getName());
                        view.setGranted(true);
                    } else if ("nav".equals(selector)) {
                        view = new NavView();
                        view.setGranted(true);
                    } else if ("searchinapp".equals(selector)) {
                        view = new SearchInAppView();
                        view.setGranted(true);
                    } else if ("favourites".equals(selector)) {
                        view = new FavouritesView();
                        view.setGranted(true);
                    } else if ("lastedited".equals(selector)) {
                        view = new LastEditedView();
                        view.setGranted(true);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (view != null) {

                view.setArea((AbstractArea) data.get("area"));
                view.setMenu((MenuEntry) data.get("menu"));

            }


        return view;
    }
}
