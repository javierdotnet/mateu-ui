package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.app.ClientSideHelper;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.AbstractWizard;
import io.mateu.ui.core.shared.Data;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.net.URL;
import java.util.List;

@WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1000)
//@WebServlet(urlPatterns = {"/admin/*", "/VAADIN/*"}, asyncSupported = true, loadOnStartup = 1000)
@VaadinServletConfiguration(productionMode = false, ui = ValoThemeUI.class)
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
                new ValoThemeSessionInitListener());

        MateuUI.setClientSideHelper(new ClientSideHelper() {

            public BaseServiceAsync baseServiceImpl = new BaseServiceClientSideImpl();

            @Override
            public void openView(AbstractView view) {
                System.out.println("openView(" + view.getClass().getName() + ")");
                ValoThemeUI.addView((ValoThemeUI) UI.getCurrent(), view);
                //views.addView(view);
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
                return ((ValoThemeUI)UI.getCurrent()).getApp();
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
            public void open(URL url) {
                System.out.println("open(" + url + ")");
                Page.getCurrent().open(url.toString(), url.toString());
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
            public void open(AbstractWizard wizard) {

                // Create a sub-window and set the content
                WizardWindow subWindow = new WizardWindow(wizard);

                // Open it in the UI
                UI.getCurrent().addWindow(subWindow);
            }
        });


        if (false && System.getProperty("appname", "Mateu ERP").toLowerCase().contains("quoon")) getService().addSessionInitListener((SessionInitListener) event -> event.getSession().addBootstrapListener(new BootstrapListener() {

            @Override
            public void modifyBootstrapFragment(
                    BootstrapFragmentResponse response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void modifyBootstrapPage(BootstrapPageResponse response) {
                response.getDocument().head().
                        getElementsByAttributeValue("rel", "shortcut icon").attr("href", "/com/vaadin/tests/themes/tests-valo-reindeer/Q-sola-favicon.png");
                response.getDocument().head()
                        .getElementsByAttributeValue("rel", "icon")
                        .attr("href", "/com/vaadin/tests/themes/tests-valo-reindeer/Q-sola-favicon.png");
            }}
        ));
    }
}

