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
                AbstractArea area = MateuUI.getApp().getArea(ts[pos]);
                if (area != null) {
                    data.put("area", area);

                    if (!"".equals(coords)) coords += "/";
                    coords += ts[pos];
                    pos++;
                }
            }

            // la posición 1 es el id del menu
            if (ts.length > 1) {
                MenuEntry menu = MateuUI.getApp().getMenu(ts[pos]);
                if (menu != null) {
                    data.put("menu", menu);

                    if (!"".equals(coords)) coords += "/";
                    coords += ts[pos];
                    pos++;
                }
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
                    || selector.equals("menu") || selector.equals("home")
                    || selector.equals("nav") || selector.equals("searchinapp") || selector.equals("favourites") || selector.equals("lastedited"))) {
                if (viewAndParameters.contains("/mui/")) {
                    System.out.println("no reconocemos el selector pero contiene /mui/, así que es nuestro pero no estamos autorizados");
                    return viewAndParameters; // si no existe el menú (no estamos autorizados) devolvemos el churro para quedarnos la petición y seguir el proceso
                } else return null;
            } else return viewAndParameters;
        } else return null;

    }

    @Override
    public AbstractView getView(String viewName) {
        System.out.println("MiViewProvider.getView(" + viewName + ")");

        AbstractView view = null;

        Map<Object, Object> data = parse(viewName);

            try {

                String selector = (String) data.get("selector");

                if (!Strings.isNullOrEmpty(selector)) {

                    if ("mui".equals(selector) || "pmo".equals(selector)) {

                        // si no tenemos area y menú es que no estamos autorizados
                        if (data.get("area") == null || data.get("menu") == null) return new ForbiddenView();

                        Object o = null;

                        String vn = (String) data.get("resto");
                        if (vn.startsWith("/")) vn = vn.substring(1);
                        String parametros = "";
                        if (vn.contains("?")) {
                            parametros = vn.substring(vn.indexOf("?") + 1) + parametros;
                            vn = vn.substring(0, vn.indexOf("?"));
                        }
                        if (vn.contains("/")) {
                            if (!"".equals(parametros)) parametros = "?" + parametros;
                            parametros = vn.substring(vn.indexOf("/") + 1) + parametros;
                            vn = vn.substring(0, vn.indexOf("/"));
                        }

                        String datos = "";
                        if (vn.contains("..")) {
                            datos = vn.split("\\.\\.")[1];
                            vn = vn.split("\\.\\.")[0];
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

                            if (!Strings.isNullOrEmpty(datos)) {
                                view.setInitialData(new Data(BaseEncoding.base64().decode(datos)));
                            }

                            view.setParametros(parametros);

                            if (view instanceof AbstractCRUDView) {
                                ((AbstractCRUDView) view).addListener(new CRUDListener() {
                                    @Override
                                    public void openEditor(AbstractEditorView e, boolean inNewTab) {
                                        e.setListFragment(MateuUI.getCurrentFragment());
                                        MateuUI.openView(e, inNewTab);
                                    }
                                });
                            } else if (view instanceof AbstractEditorView) {

                                String s = parametros;

                                if (s.contains("?")) {
                                    s = s.substring(0, s.indexOf("?"));
                                    parametros = parametros.substring(parametros.indexOf("?") + 1);
                                }

                                Object id = null;
                                if (!Strings.isNullOrEmpty(s)) {
                                    if (s.startsWith("s")) id = s.substring(1);
                                    else if (s.startsWith("l")) id = Long.parseLong(s.substring(1));
                                    else if (s.startsWith("i")) id = Integer.parseInt(s.substring(1));
                                }

                                AbstractEditorView ev = (AbstractEditorView) view;

                                ev.setInitialId(id);

                                if (!Strings.isNullOrEmpty(parametros)) {
                                    String[] t = parametros.split("&");
                                    for (String p : t) if (p.contains("=")) {
                                        String k = p.split("=")[0];
                                        String v = p.split("=")[1];
                                        if ("q".equals(k)) ev.setListQl(new String(BaseEncoding.base64().decode(v)));
                                        else if ("pos".equals(k)) ev.setListPos(Integer.parseInt(v));
                                        else if ("count".equals(k)) ev.setListCount(Integer.parseInt(v));
                                        else if ("rpp".equals(k)) ev.setListRowsPerPage(Integer.parseInt(v));
                                        else if ("page".equals(k)) ev.setListPage(Integer.parseInt(v));
                                        else if ("listfragment".equals(k)) ev.setListFragment(new String(BaseEncoding.base64().decode(v)));
                                    } else {
                                        System.out.println("parámetro " + p + " sin valor");
                                    }
                                }

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

                    } else if ("menu".equals(selector)) {
                        MenuEntry e = (MenuEntry)data.get("menu");
                        view = new MenuView(e.getName(), e.getId());
                        view.setGranted(true);
                    } else if ("areahome".equals(selector)) {
                        AbstractArea a = (AbstractArea) data.get("area");
                        view = new AreaView(a.getName(), a.getId());
                        view.setGranted(true);
                    } else if ("changearea".equals(selector)) {
                        view = new ChangeAreaView("Change app area", (AbstractArea)data.get("area"));
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
                    } else {
                        // si estamos aquí es que no reconocemos el selector (por no haber reconocido el area / menu). Devolvemos forbidden
                        return new ForbiddenView();
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
