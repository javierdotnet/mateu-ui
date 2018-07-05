package io.mateu.ui.vaadin.framework;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.ClientSideHelper;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.MiViewProvider;
import io.mateu.ui.vaadin.components.ViewLayout;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.net.URL;
import java.util.List;
import java.util.Map;

@WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1000)
//@WebServlet(urlPatterns = {"/admin/*", "/VAADIN/*"}, asyncSupported = true, loadOnStartup = 1000)
@VaadinServletConfiguration(productionMode = false, ui = MyUI.class)
public class Servlet extends VaadinServlet {



    @Override
    protected void servletInitialized() throws ServletException {

        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");
        System.out.println("***********************************************");

        super.servletInitialized();

        getService().addSessionInitListener(
                new ThemeSessionInitListener());

        io.mateu.ui.core.client.app.MateuUI.setClientSideHelper(new ClientSideHelper() {

            public BaseServiceAsync baseServiceImpl = new BaseServiceClientSideImpl();

            @Override
            public void openView(AbstractView view, boolean inNewTab) {
                System.out.println("openView(" + view.getClass().getName() + ")");

                if (view instanceof AbstractDialog) {

                    MyUI.addView((MyUI) UI.getCurrent(), view);

                } else {

                    MyUI ui = (MyUI) UI.getCurrent();

                    view.setArea(ui.getAreaActual());
                    view.setMenu(ui.getMenuActual());

                    String viewId = view.getViewId();

                    String u = "";
                    if (view.getArea() != null) {
                        if (!"".equals(u)) u += "/";
                        u += ui.getApp().getAreaId(view.getArea());
                    }
                    if (view.getMenu() != null) {
                        if (!"".equals(u)) u += "/";
                        u += ui.getApp().getMenuId(view.getMenu());
                    }
                    if (!"".equals(u)) u += "/";
                    u += view.getViewId();

                    Page.getCurrent().open("#!" + u, (inNewTab)?"_blank":Page.getCurrent().getWindowName());
                }

            }

            @Override
            public Data getNewDataContainer() {
                return new Data();
            }

            @Override
            public <T> T create(Class<?> serviceInterface) {
                try {
                    return (T) Class.forName(serviceInterface.getName().replaceAll("\\.shared\\.", ".client.") + "ClientSideImpl").newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void alert(String msg) {
                Notification.show("Alert", msg, Notification.Type.WARNING_MESSAGE);
            }

            @Override
            public void run(Runnable runnable) {
                runnable.run();
                //new Thread(runnable).start();
            }

            @Override
            public void runInUIThread(Runnable runnable) {

                UI.getCurrent().access(runnable);

            }

            @Override
            public BaseServiceAsync getBaseService() {
                return baseServiceImpl;
            }

            @Override
            public void run(Runnable runnable, Runnable onerror) {
                try {
                    //new Thread(runnable).start();
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    onerror.run();
                }
            }

            @Override
            public void openView(AbstractView parentView, AbstractView view) {
            }

            @Override
            public void notifyErrors(List<String> msgs) {
                StringBuffer sb = new StringBuffer();
                boolean primero = true;
                for (String s : msgs) {
                    if (primero) primero = false;
                    else sb.append("\n");
                    sb.append(s);
                }
                Notification n = new Notification("Error", sb.toString(), Notification.Type.ERROR_MESSAGE);
                n.setPosition(Position.MIDDLE_CENTER);
                n.setDelayMsec(1000);
                n.show(Page.getCurrent());
            }

            @Override
            public AbstractApplication getApp() {
                return ((MyUI)UI.getCurrent()).getApp();
            }

            @Override
            public void notifyError(String msg) {
                Notification n = new Notification("Info", msg, Notification.Type.ERROR_MESSAGE);
                n.setPosition(Position.MIDDLE_CENTER);
                n.setDelayMsec(1000);
                n.show(Page.getCurrent());
            }

            @Override
            public void notifyInfo(String msg) {
                Notification n = new Notification("Info", msg, Notification.Type.WARNING_MESSAGE);
                n.setPosition(Position.MIDDLE_CENTER);
                n.setDelayMsec(1000);
                n.show(Page.getCurrent());
            }

            @Override
            public void notifyDone(String msg) {
                Notification n = new Notification("Done", msg, Notification.Type.WARNING_MESSAGE);
                n.setPosition(Position.MIDDLE_CENTER);
                n.setStyleName("warning success");
                n.setDelayMsec(1000);
                n.show(Page.getCurrent());
            }

            @Override
            public void open(URL url, boolean inNewTab) {
                System.out.println("open(" + url + ")");
                Page.getCurrent().open(url.toString(), (inNewTab)?"_blank":url.toString());
            }

            @Override
            public void confirm(String text, Runnable onOk) {

                // Create a sub-window and set the content
                Window subWindow = new Window("Please confirm");

                VerticalLayout v = new VerticalLayout();
                v.addComponent(new Label(text));

                HorizontalLayout footer = new HorizontalLayout();
                footer.setWidth("100%");
                footer.setSpacing(true);
                footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

                Label footerText = new Label("");
                footerText.setSizeUndefined();

                Button ok = new Button("Ok", e -> {
                    subWindow.close();
                    onOk.run();
                });
                ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
                ok.setClickShortcut(ShortcutAction.KeyCode.ENTER);

                Button cancel = new Button("Cancel");

                footer.addComponents(footerText, ok); //, cancel);
                footer.setExpandRatio(footerText, 1);

                v.addComponent(footer);

                subWindow.setContent(v);

                // Center it in the browser window
                subWindow.center();

                subWindow.setModal(true);

                // Open it in the UI
                UI.getCurrent().addWindow(subWindow);

            }

            @Override
            public void open(AbstractWizard wizard, boolean inNewTab) {

                // Create a sub-window and set the content
                WizardWindow subWindow = new WizardWindow(wizard);

                // Open it in the UI
                UI.getCurrent().addWindow(subWindow);
            }

            @Override
            public void openViewInDialog(AbstractView view) {
                ViewLayout v = new ViewLayout(view);

                // Create a sub-window and set the content
                Window subWindow = new Window(view.getTitle());

                HorizontalLayout footer = new HorizontalLayout();
                footer.setWidth("100%");
                footer.setSpacing(true);
                footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

                Label footerText = new Label("");
                footerText.setSizeUndefined();


                footer.addComponents(footerText);

                footer.setExpandRatio(footerText, 1);

                v.addComponent(footer);

                subWindow.setContent(v);

                // Center it in the browser window
                subWindow.center();

                subWindow.setModal(true);


                view.addListener(new ViewListener() {
                    @Override
                    public void onClose() {
                        subWindow.close();
                    }
                });

                // Open it in the UI
                UI.getCurrent().addWindow(subWindow);
            }

            @Override
            public String getCurrentFragment() {
                return ((MyUI)UI.getCurrent()).navigator.getState();
            }

            @Override
            public void showAtCenter(Component vaadinComponent) {
                MyUI.get().showAtCenter(vaadinComponent);
            }

            @Override
            public void showInWindow(String caption, Component vaadinComponent, Window.CloseListener closeListener) {
                MyUI.get().showInWindow(caption, vaadinComponent, closeListener);
            }
        });

    }
}

