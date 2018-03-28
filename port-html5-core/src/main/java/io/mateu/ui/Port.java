package io.mateu.ui;

import de.iterable.teavm.jquery.JQuery;
import de.iterable.teavm.jquery.types.JQueryEventHandler;
import de.iterable.teavm.jquery.types.JQueryEventObject;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.AbstractWizard;
import io.mateu.ui.core.shared.Data;

import java.net.URL;
import java.util.*;

public class Port {

    private static App app;

    public static App getApp() {
        return app;
    }

    public static void main(String[] args) {

        Iterator<App> apps = ServiceLoader.load(App.class).iterator();

        while (apps.hasNext()) {
            Object o = apps.next();
            System.out.println("found " + o.getClass().getName());
            if (o instanceof App) {
                app = (App) o;
                System.out.println("app " + app.getName() + " loaded");
            }
            break;
        }

        //app = new SampleApp();


        MateuUI.setClientSideHelper(new ClientSideHelper() {

            @Override
            public void openView(AbstractView v, boolean inNewTab) {
                StringBuffer h = new StringBuffer();
                Map<String, JQueryEventHandler> listeners = new HashMap<>();

                ViewHelper.build(h, listeners, v);

                JQuery.by("#main div.container").html(h.toString());
                for (String k : listeners.keySet()) {
                    String q = k.split("separador")[0];
                    String e = k.split("separador")[1];
                    JQuery.by(q).on(e, listeners.get(k));
                }
                Bridge.bindMain();
            }

            @Override
            public Data getNewDataContainer() {
                return null;
            }

            @Override
            public <T> T create(Class<?> serviceInterface) {
                return (T) (new io.mateu.ui.teavm.AsyncFactory()).create(serviceInterface);
            }

            @Override
            public void alert(String msg) {
                Bridge.alert(msg);
            }

            @Override
            public void run(Runnable runnable) {

            }

            @Override
            public void runInUIThread(Runnable runnable) {

            }

            @Override
            public BaseServiceAsync getBaseService() {
                return null;
            }

            @Override
            public void run(Runnable runnable, Runnable onerror) {

            }

            @Override
            public void openView(AbstractView parentView, AbstractView view) {

            }

            @Override
            public void notifyErrors(List<String> msgs) {

            }

            @Override
            public AbstractApplication getApp() {
                return (AbstractApplication) app;
            }

            @Override
            public void notifyError(String msg) {

            }

            @Override
            public void notifyInfo(String msg) {

            }

            @Override
            public void notifyDone(String msg) {

            }

            @Override
            public void open(URL url, boolean inNewTab) {

            }

            @Override
            public void confirm(String text, Runnable onOk) {

            }

            @Override
            public void open(AbstractWizard wizard, boolean inNewTab) {

            }

            @Override
            public void openViewInDialog(AbstractView view) {

            }
        });




        init();


/*
        HTMLDocument document = HTMLDocument.current();
        HTMLElement div = document.createElement("div");
        div.appendChild(document.createTextNode("TeaVM generated element"));
        document.getBody().appendChild(div);
*/
    }

    private static void init() {

        JQuery.by("#branding").html("<h1>" + getApp().getName()+ "</h1>");

        addAreas();

    }

    private static void addAreas() {

        String h = "<ul>";
        int pos = 0;
        for (AbstractArea a : getApp().getAreas()) {
            h += "<li><a href='#' id='a-area-" + pos + "'>" + a.getName() + "</a></li>";
            pos++;
        }
        h += "</ul>";

        JQuery.by("#top-menu").html(h);

        pos = 0;
        for (AbstractArea a : getApp().getAreas()) {
            JQuery.by("#a-area-" + pos).on("click", new JQueryEventHandler() {
                @Override
                public boolean apply(JQueryEventObject jQueryEventObject) {
                    jQueryEventObject.preventDefault();
                    setMenu(a);
                    return false;
                }
            });
            pos++;
        }

    }

    private static void setMenu(AbstractArea a) {

        StringBuffer h = new StringBuffer();
        h.append("<ul>");
        int pos = 0;
        for (AbstractModule m : a.buildModules()) for (MenuEntry e : m.buildMenu()) {
            pos = appendMenu(pos, h, e);
        }
        h.append("</ul>");

        JQuery.by("#left-menu").html(h.toString());

        pos = 0;
        for (AbstractModule m : a.buildModules()) for (MenuEntry e : m.buildMenu()) {
            pos = activateMenu(pos, e);
        }
    }

    private static int appendMenu(int pos, StringBuffer h, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            h.append("<li>" + ((AbstractMenu)e).getName() + "<ul>");
            for (MenuEntry x : ((AbstractMenu)e).getEntries()) {
                pos = appendMenu(pos, h, x);
            }
            h.append("</ul></li>");
        } else {
            h.append("<li><a href='#' id='a-menu-" + pos + "'>" + e.getName() + "</a></li>");
            pos++;
        }
        return pos;
    }

    private static int activateMenu(int pos, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            for (MenuEntry x : ((AbstractMenu)e).getEntries()) {
                pos = activateMenu(pos, x);
            }
        } else {
            int finalPos = pos;
            JQuery.by("#a-menu-" + pos).on("click", new JQueryEventHandler() {
                @Override
                public boolean apply(JQueryEventObject jQueryEventObject) {
                    jQueryEventObject.preventDefault();
                    if (e instanceof AbstractAction) {
                        ((AbstractAction) e).run();
                    }
                    Bridge.log("menu option " + finalPos + "clicked");
                    return false;
                }
            });
            pos++;
        }
        return pos;
    }
}
