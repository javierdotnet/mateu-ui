/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.themes.valo;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.vaadin.annotations.*;
import com.vaadin.data.HasValue;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.*;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.App;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.*;
import io.mateu.ui.vaadin.HomeView;
import io.mateu.ui.vaadin.ViewLayout;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;


//@Theme("tests-valo-reindeer")
@Theme("quonext")
//@Title("Mateu.io")
//@StyleSheet("valo-theme-ui.css")
@StyleSheet({"css/font-awesome.min.css"})
//@Push
@PreserveOnRefresh
public class ValoThemeUI extends UI {

    private boolean testMode = false;
    public Map<String, Integer> viewsIdsInNavigator;
    public int contViews;
    private ViewDisplay trueViewDisplay;
    private static int fileId = 0;
    private Resource foto;
    private VerticalLayout divSelectorArea;

    @WebServlet(value = "/*", asyncSupported = true, loadOnStartup = 1000)
    //@WebServlet(urlPatterns = {"/admin/*", "/VAADIN/*"}, asyncSupported = true, loadOnStartup = 1000)
    @VaadinServletConfiguration(productionMode = false, ui = ValoThemeUI.class)
    public static class Servlet extends VaadinServlet {

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
                    addView((ValoThemeUI) UI.getCurrent(), view);
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

    private TestIcon testIcon = new TestIcon(100);
    ValoMenuLayout root = new ValoMenuLayout();
    ComponentContainer viewDisplay = root.getContentContainer();
    CssLayout menu = new CssLayout();
    VerticalLayout menuItemsLayout = new VerticalLayout();

    {
        menu.setId("testMenu");
        menuItemsLayout.setSpacing(false);
    }
    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
    private MenuBar settings;

    @Override
    protected void init(VaadinRequest request) {

        if (getApp() == null) {
            Iterator<App> apps = ServiceLoader.load(App.class).iterator();

            while (apps.hasNext()) {
                setApp((AbstractApplication) apps.next());
                System.out.println("app " + getApp().getName() + " loaded");
                break;
            }

            String u = "" + Page.getCurrent().getLocation();
            if (u.contains("#")) u = u.substring(0, u.indexOf("#"));


            if (getApp() == null) {

                // creamos una app al vuelo para probar la interfaz


                setApp(new AbstractApplication() {
                    @Override
                    public String getName() {
                        return "Test app";
                    }

                    @Override
                    public List<AbstractArea> getAreas() {
                        return Lists.newArrayList(new AbstractArea("Area 1") {
                            @Override
                            public List<AbstractModule> getModules() {
                                return Lists.newArrayList(new AbstractModule() {
                                    @Override
                                    public String getName() {
                                        return "Módulo 1";
                                    }

                                    @Override
                                    public List<MenuEntry> getMenu() {
                                        return Lists.newArrayList(
                                                new AbstractAction("Opción 1") {
                                                    @Override
                                                    public void run() {
                                                        MateuUI.alert("hola!");
                                                    }
                                                }
                                        );
                                    }
                                });
                            }
                        });
                    }
                });

            }


            getApp().setBaseUrl(u);
        }

        viewsIdsInNavigator = new HashMap<>();
        menuItems = new LinkedHashMap<String, String>();
        contViews = 0;



        if (request.getParameter("test") != null) {
            testMode = true;

            if (browserCantRenderFontsConsistently()) {
                getPage().getStyles().add(
                        ".v-app.v-app.v-app {font-family: Sans-Serif;}");
            }
        }

        if (getPage().getWebBrowser().isIE()
                && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }

        if (!testMode) {
            Responsive.makeResponsive(this);
        }

        getPage().setTitle(getApp().getName());
        setContent(root);
        root.setWidth("100%");

        root.addMenu(buildMenu(request));
        addStyleName(ValoTheme.UI_WITH_MENU);

        navigator = new Navigator(this, viewDisplay);

        //navigator.addView("common", CommonParts.class);

        String f = Page.getCurrent().getUriFragment();
        if (f == null || f.equals("")) {
            //navigator.navigateTo("common");
        }

        navigator.setErrorView(HomeView.class);

        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                for (Iterator<Component> it = menuItemsLayout.iterator(); it
                        .hasNext();) {
                    it.next().removeStyleName("selected");
                }
                for (Entry<String, String> item : menuItems.entrySet()) {
                    if (event.getViewName().equals(item.getKey())) {
                        for (Iterator<Component> it = menuItemsLayout
                                .iterator(); it.hasNext();) {
                            Component c = it.next();
                            if (c.getCaption() != null
                                    && c.getCaption().startsWith(
                                            item.getValue())) {
                                c.addStyleName("selected");
                                break;
                            }
                        }
                        break;
                    }
                }
                menu.removeStyleName("valo-menu-visible");
            }
        });

        boolean hayPartePublica = false;
        for (AbstractArea a : getApp().getAreas()) {
            hayPartePublica |= a.isPublicAccess();
        }
        if (!hayPartePublica) openLoginDialog();
    }

    private void setApp(AbstractApplication app) {
        app.setPort(AbstractApplication.PORT_VAADIN);
        VaadinSession.getCurrent().setAttribute("app", app);
    }

    public AbstractApplication getApp() {
        return (AbstractApplication) VaadinSession.getCurrent().getAttribute("app");
    }


    private static void addView(ValoThemeUI ui, AbstractView view) {

        System.out.println("abriendo vista " + view.getClass().getName() + "/" + view.getViewId());

        if (view instanceof AbstractCRUDView) {
            ((AbstractCRUDView)view).addListener(new CRUDListener() {
                @Override
                public void openEditor(AbstractEditorView e) {
                    MateuUI.openView(e);
                }
            });
        }


        if (view instanceof AbstractDialog) {
            ViewLayout v = new ViewLayout(view);

            // Create a sub-window and set the content
            Window subWindow = new Window(((AbstractDialog)view).getTitle());

            HorizontalLayout footer = new HorizontalLayout();
            footer.setWidth("100%");
            footer.setSpacing(true);
            footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

            Label footerText = new Label("");
            footerText.setSizeUndefined();

            Button ok = new Button("Ok", e -> {
                List<String> errors = v.getView().getForm().validate();
                if (errors.size() > 0) {
                    MateuUI.notifyErrors(errors);
                } else {
                    subWindow.close();
                    ((AbstractDialog)view).onOk(v.getView().getForm().getData());
                }
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
            ui.addWindow(subWindow);

        } else {

            String idv;
            Integer viewNumber = ui.viewsIdsInNavigator.get(view.getViewId());

            if (viewNumber != null && viewNumber >= ui.contViews - 20) {

                ui.viewsIdsInNavigator.remove(view.getViewId());
                ui.navigator.removeView("view_" + viewNumber);

            }


            {

                //setText(view.getTitle());
                ViewLayout v = new ViewLayout(view);
                //trueViewDisplay.showView(v);

                if (ui.contViews > 20) {
                    ui.navigator.removeView("view_" + (ui.contViews - 20));
                }


                if (view instanceof AbstractEditorView) {
                    view.getForm().addDataSetterListener(new DataSetterListener() {
                        @Override
                        public void setted(Data newData) {
                            if (newData.get("_id") != null) {
                                if (!newData.get("_id").equals(((AbstractEditorView) view).getInitialId())) {
                                    String oldK = view.getViewId();
                                    Integer oldContView = ui.viewsIdsInNavigator.remove(oldK);
                                    ((AbstractEditorView) view).setInitialId(newData.get("_id"));
                                    ui.viewsIdsInNavigator.put(view.getViewId(), oldContView);
                                }
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

                if (view instanceof AbstractListView && ((AbstractListView) view).isSearchOnOpen()) {
                    ((AbstractListView)view).search();
                }


                ui.viewsIdsInNavigator.put(view.getViewId(), ui.contViews);
                ui.navigator.addView(idv = "view_" + ui.contViews++, v);

                View oldv = ui.navigator.getCurrentView();
                System.out.println("current view = " + ui.navigator.getCurrentView());
                String oldvid = null;
                if (oldv != null && oldv instanceof ViewLayout) {
                    oldvid = ((ViewLayout)oldv).getView().getViewId();
                }
                System.out.println("oldvid = " + oldvid);

                String finalOldvid = oldvid;
                view.addListener(new ViewListener() {
                    @Override
                    public void onClose() {
                        ui.viewsIdsInNavigator.remove(view.getViewId());
                        ui.navigator.removeView(idv);
                        if (finalOldvid != null) ui.navigator.navigateTo("view_" + ui.viewsIdsInNavigator.get(finalOldvid));
                        else ui.navigator.navigateTo("");
                    }
                });

            }

            System.out.println("navigating to " + idv);

            ui.navigator.navigateTo(idv);

        }



    }


    private boolean browserCantRenderFontsConsistently() {
        // PhantomJS renders font correctly about 50% of the time, so
        // disable it to have consistent screenshots
        // https://github.com/ariya/phantomjs/issues/10592

        // IE8 also has randomness in its font rendering...

        return getPage().getWebBrowser().getBrowserApplication()
                .contains("PhantomJS")
                || (getPage().getWebBrowser().isIE() && getPage()
                        .getWebBrowser().getBrowserMajorVersion() <= 9);
    }

    static boolean isTestMode() {
        return ((ValoThemeUI) getCurrent()).testMode;
    }

    Component buildTestMenu() {
        CssLayout menu = new CssLayout();
        menu.addStyleName(ValoTheme.MENU_PART_LARGE_ICONS);

        Label logo = new Label("Va");
        logo.setSizeUndefined();
        logo.setPrimaryStyleName(ValoTheme.MENU_LOGO);
        menu.addComponent(logo);

        Button b = new Button(
                "Reference <span class=\"valo-menu-badge\">3</span>");
        b.setIcon(FontAwesome.TH_LIST);
        b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        b.addStyleName("selected");
        b.setHtmlContentAllowed(true);
        menu.addComponent(b);

        b = new Button("API");
        b.setIcon(FontAwesome.BOOK);
        b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        menu.addComponent(b);

        b = new Button("Examples <span class=\"valo-menu-badge\">12</span>");
        b.setIcon(FontAwesome.TABLE);
        b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        b.setHtmlContentAllowed(true);
        menu.addComponent(b);

        return menu;
    }

    CssLayout buildMenu(VaadinRequest request) {

        HorizontalLayout top = new HorizontalLayout();
        top.setWidth("100%");
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        menu.addComponent(top);
        //menu.addComponent(createThemeSelect());

        Button showMenu = new Button("Menu", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);

        Label title = new Label("<h3><strong>" + getApp().getName() + "</strong></h3>",
                ContentMode.HTML);
        title.setSizeUndefined();
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        settings = new MenuBar();
        settings.addStyleName("user-menu");
        settings.addStyleName("mi-user-menu");
        menu.addComponent(settings);

        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);


        // cambio temas

        List<Pair> temas = Lists.newArrayList(new Pair("quonext", "Quonext"), new Pair("tests-valo-reindeer", "Valo"));

        ComboBox<Pair> combo = new ComboBox<>();
        combo.setItems(temas);
        combo.setEmptySelectionAllowed(false);
        combo.setTextInputAllowed(false);
        combo.setScrollToSelectedItem(true);
        combo.addStyleName("selectortema");
// Use the name property for item captions
        combo.setItemCaptionGenerator(Pair::getText);



        if (!Strings.isNullOrEmpty(getTheme())) {
            for (Pair p : temas) if (getTheme().equals(p.getValue())) combo.setSelectedItem(p);
        }

        combo.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                if (valueChangeEvent.getValue() != null) {

                    setTheme((String) valueChangeEvent.getValue().getValue());
                }
            }
        });

        menu.addComponent(combo);

        // fin cambio temas


        refreshMenu();

        //if (label != null) label.setValue(label.getValue() + " <span class=\"valo-menu-badge\">" + count + "</span>");

        return menu;
    }

    private void refreshMenu() {

        VaadinSession s = VaadinSession.getCurrent();

        settings.removeItems();
        menuItemsLayout.removeAllComponents();

        if (s.getAttribute("usuario") == null) {

            System.out.println("***** NO AUTENTICADO.");

            if (getApp().isAuthenticationNeeded()){
                MenuItem settingsItem = settings.addItem("Login", new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuItem menuItem) {


                        openLoginDialog();

                    }
                });
            }


            Label label = null;
            int count = -1;
            for (AbstractArea a : getApp().getAreas()) {

                boolean isPublic = a.isPublicAccess();

                if (!getApp().isAuthenticationNeeded() || isPublic) {
                    label = new Label(a.getName(), ContentMode.HTML);
                    label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
                    label.addStyleName(ValoTheme.LABEL_H4);
                    label.setSizeUndefined();
                    menuItemsLayout.addComponent(label);
                    for (AbstractModule m : a.getModules()) {
                        for (MenuEntry e : m.getMenu()) {

                            addMenu(e);

                        }
                    }

                }

            }
        } else {

            System.out.println("***** AUTENTICADO. USUARIO=" + s.getAttribute("usuario"));


            MenuItem settingsItem = settings.addItem("",
                    foto = (getApp().getUserData().getPhoto() != null)?new ExternalResource(getApp().getUserData().getPhoto()):new ClassResource("profile-pic-300px.jpg"),
                    null);

            settingsItem.addItem("Edit Profile", new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem menuItem) {
                    editProfile();
                }
            });
            settingsItem.addItem("Change password", new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem menuItem) {
                    changePassword();
                }
            });
            settingsItem.addItem("Change photo", new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem menuItem) {
                    uploadFoto();
                }
            });
            settingsItem.addSeparator();
            settingsItem.addItem("Sign Out", new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem menuItem) {
                    VaadinSession.getCurrent().setAttribute("usuario", null);
                    clearViews();
                    refreshMenu();
                }
            });


            divSelectorArea = new VerticalLayout();
            divSelectorArea.setSpacing(false);
            divSelectorArea.addComponent(new Label("Area:"));
            divSelectorArea.addStyleName("divSelectorArea");

            List<Pair> areas = new ArrayList<>();
            if (getApp().getAreas().size() > 1) {
                for (AbstractArea a : getApp().getAreas()) {

                    boolean isPublic = a.isPublicAccess();

                    if (!isPublic) {

                        areas.add(new Pair(a, a.getName()));

                    }
                }
            }

            ComboBox<Pair> combo = new ComboBox<>();
            combo.setItems(areas);
            combo.setEmptySelectionAllowed(false);
            combo.setTextInputAllowed(false);
            combo.setScrollToSelectedItem(true);
// Use the name property for item captions
            combo.setItemCaptionGenerator(Pair::getText);
            if (areas.size() > 0) combo.setSelectedItem(areas.get(0));

            combo.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                    if (valueChangeEvent.getValue() != null) {

                        AbstractArea a = (AbstractArea) valueChangeEvent.getValue().getValue();

                        setArea(a);

                        refreshMenu(a);
                    }
                }
            });

            divSelectorArea.addComponent(combo);

            for (AbstractArea a : getApp().getAreas()) {

                boolean isPublic = a.isPublicAccess();

                if (!isPublic) {

                    refreshMenu(a);

                    break;
                }
            }
        }

    }

    private void openLoginDialog() {


        // Create a sub-window and set the content
        Window subWindow = new Window("Login");

        subWindow.setWidth("375px");

        FormLayout f = new FormLayout();
        f.setMargin(true);

        TextField l;
        f.addComponent(l = new TextField("Username"));
        PasswordField p;
        f.addComponent(p = new PasswordField("Password"));
        Label e;
        f.addComponent(e = new Label());

        VerticalLayout v = new VerticalLayout();
        v.addComponent(f);





        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

        Label footerText = new Label("");
        footerText.setSizeUndefined();

        Button forgot = new Button("Forgot password", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                e.setValue("Asking for email...");
                MateuUI.getBaseService().forgotPassword(l.getValue(), new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        e.setValue("" + caught.getClass().getName() + ": " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result) {
                        e.setValue("Email sent. Please check your inbox");
                    }
                });
            }
        });
        //forgot.addStyleName(ValoTheme.BUTTON_);


        Button ok = new Button("Login", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                e.setValue("Authenticating...");
                MateuUI.getBaseService().authenticate(l.getValue(), p.getValue(), new AsyncCallback<UserData>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        e.setValue("" + caught.getClass().getName() + ": " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(UserData result) {
                        e.setValue("OK!");
                        getApp().setUserData(result);
                        VaadinSession.getCurrent().setAttribute("usuario", "admin");
                        subWindow.close();
                        refreshMenu();
                    }
                });
            }
        });
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        Button cancel = new Button("Cancel");

        footer.addComponents(footerText, forgot, ok); //, cancel);
        footer.setExpandRatio(footerText, 1);

        v.addComponent(footer);


        subWindow.setContent(v);

        // Center it in the browser window
        subWindow.center();

        subWindow.setModal(true);

        // Open it in the UI
        UI.getCurrent().addWindow(subWindow);

        l.focus();
    }

    private void clearViews() {
        ValoThemeUI ui = (ValoThemeUI) UI.getCurrent();
        for (int x : ui.viewsIdsInNavigator.values()) {
            ui.navigator.removeView("view_" + x);
        }
        ui.viewsIdsInNavigator.clear();
        viewDisplay.removeAllComponents();
    }

    private void changePassword() {
        // Create a sub-window and set the content
        Window subWindow = new Window("Change password");

        subWindow.setWidth("375px");

        FormLayout f = new FormLayout();
        f.setMargin(true);

        PasswordField l;
        f.addComponent(l = new PasswordField("Current password"));
        PasswordField p;
        f.addComponent(p = new PasswordField("New password"));
        PasswordField p2;
        f.addComponent(p2 = new PasswordField("Repeat password"));
        Label e;
        f.addComponent(e = new Label());

        VerticalLayout v = new VerticalLayout();
        v.addComponent(f);





        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

        Label footerText = new Label("");
        footerText.setSizeUndefined();

        Button ok = new Button("Change it!", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                if (l.getValue() == null || "".equals(l.getValue().trim())) e.setValue("Old password is required");
                else if (p.getValue() == null || "".equals(p.getValue().trim())) e.setValue("New password is required");
                else if (p2.getValue() == null || "".equals(p2.getValue().trim())) e.setValue("New password repeated is required");
                else if (!p.getValue().equals(p2.getValue())) e.setValue("New password and new password repeated must be equal");
                else {
                    e.setValue("Changing password...");

                    MateuUI.getBaseService().changePassword(getApp().getUserData().getLogin(), l.getValue(), p.getValue(), new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            e.setValue("" + caught.getClass().getName() + ": " + caught.getMessage());
                        }

                        @Override
                        public void onSuccess(Void result) {
                            e.setValue("OK!");
                            subWindow.close();
                        }
                    });

                }
            }
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

        l.focus();
    }

    private void editProfile() {
        // Create a sub-window and set the content
        Window subWindow = new Window("My profile");

        subWindow.setWidth("375px");

        FormLayout f = new FormLayout();
        f.setMargin(true);

        TextField l;
        f.addComponent(l = new TextField("Name"));
        l.setValue(getApp().getUserData().getName());
        TextField p;
        f.addComponent(p = new TextField("Email"));
        p.setValue(getApp().getUserData().getEmail());
        Label e;
        f.addComponent(e = new Label());

        VerticalLayout v = new VerticalLayout();
        v.addComponent(f);





        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

        Label footerText = new Label("");
        footerText.setSizeUndefined();

        Button ok = new Button("Update it!", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                e.setValue("Authenticating...");
                MateuUI.getBaseService().updateProfile(getApp().getUserData().getLogin(), l.getValue(), p.getValue(), null, new AsyncCallback<Void>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        e.setValue("" + caught.getClass().getName() + ": " + caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Void result) {
                        e.setValue("OK!");
                        getApp().getUserData().setName(l.getValue());
                        getApp().getUserData().setEmail(p.getValue());
                        subWindow.close();
                        refreshMenu();
                    }
                });
            }
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

        l.focus();
    }

    private void uploadFoto() {
        // Create a sub-window and set the content
        Window subWindow = new Window("My photo");

        subWindow.setWidth("475px");

        FormLayout f = new FormLayout();
        f.setMargin(true);

        Label e = new Label();

        class MyUploader implements Upload.Receiver, Upload.SucceededListener {

            File file;

            public File getFile() {
                return file;
            }

            public OutputStream receiveUpload(String fileName,
                                              String mimeType) {
                // Create and return a file output stream

                System.out.println("receiveUpload(" + fileName + "," + mimeType + ")");

                FileOutputStream os = null;
                if (fileName != null && !"".equals(fileName)) {

                    long id = fileId++;
                    String extension = ".tmp";
                    if (fileName == null || "".equals(fileName.trim())) fileName = "" + id + extension;
                    if (fileName.lastIndexOf(".") < fileName.length() - 1) {
                        extension = fileName.substring(fileName.lastIndexOf("."));
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    }
                    File temp = null;
                    try {
                        temp = File.createTempFile(fileName, extension);
                        os = new FileOutputStream(file = temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return os;
            }

            public void uploadSucceeded(Upload.SucceededEvent event) {
                // Show the uploaded file in the image viewer
                //image.setSource(new FileResource(file));
                System.out.println("uploadSucceeded(" + file.getAbsolutePath() + ")");

            }

            public FileLocator getFileLocator() throws IOException {

                String extension = ".tmp";
                String fileName = file.getName();

                if (file.getName() == null || "".equals(file.getName().trim())) fileName = "" + getId();
                if (fileName.lastIndexOf(".") < fileName.length() - 1) {
                    extension = fileName.substring(fileName.lastIndexOf("."));
                    fileName = fileName.substring(0, fileName.lastIndexOf(".")).replaceAll(" ", "_");
                }

                java.io.File temp = (System.getProperty("tmpdir") == null)? java.io.File.createTempFile(fileName, extension):new java.io.File(new java.io.File(System.getProperty("tmpdir")), fileName + extension);

                System.out.println("java.io.tmpdir=" + System.getProperty("java.io.tmpdir"));
                System.out.println("Temp file : " + temp.getAbsolutePath());

                if (System.getProperty("tmpdir") == null || !temp.exists()) {
                    System.out.println("writing temp file to " + temp.getAbsolutePath());
                    Files.copy(file, temp);
                } else {
                    System.out.println("temp file already exists");
                }


                String baseUrl = System.getProperty("tmpurl");
                URL url = null;
                try {
                    if (baseUrl == null) {
                        url = file.toURI().toURL();
                    } else url = new URL(baseUrl + "/" + file.getName());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return new FileLocator(0, file.getName(), url.toString(), file.getAbsolutePath());
            }
        };
        MyUploader receiver = new MyUploader();


        Upload upload = new Upload(null, receiver);
        //upload.setImmediateMode(false);
        upload.addSucceededListener(receiver);

        f.addComponent(upload);

        f.addComponent(e);

        VerticalLayout v = new VerticalLayout();
        v.addComponent(f);


        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

        Label footerText = new Label("");
        footerText.setSizeUndefined();

        Button ok = new Button("Change it!", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent clickEvent) {
                e.setValue("Changing photo...");
                try {
                    FileLocator loc = receiver.getFileLocator();

                    MateuUI.getBaseService().updateFoto(getApp().getUserData().getLogin(), loc, new AsyncCallback<Void>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            e.setValue("" + caught.getClass().getName() + ": " + caught.getMessage());
                        }

                        @Override
                        public void onSuccess(Void result) {
                            e.setValue("OK!");
                            getApp().getUserData().setPhoto(loc.getUrl());
                            foto = (getApp().getUserData().getPhoto() != null)?new ExternalResource(getApp().getUserData().getPhoto()):new ClassResource("profile-pic-300px.jpg");
                            subWindow.close();
                            refreshMenu();
                        }
                    });

                } catch (IOException e1) {
                    e1.printStackTrace();
                    MateuUI.alert("" + e1.getClass().getName() + ":" + e1.getMessage());
                }

            }
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

        upload.focus();
    }

    private void refreshMenu(AbstractArea a) {

        menuItemsLayout.removeAllComponents();

        menuItemsLayout.addComponent(divSelectorArea);

        Label label = null;


        for (AbstractModule m : a.getModules()) {

            label = new Label(m.getName(), ContentMode.HTML);
            label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
            label.addStyleName(ValoTheme.LABEL_H4);
            label.setSizeUndefined();
            menuItemsLayout.addComponent(label);


            for (MenuEntry e : m.getMenu()) {

                addMenu(e);

            }
        }
    }

    private void setArea(AbstractArea a) {
        VaadinSession.getCurrent().setAttribute("area", a);
    }

    private AbstractArea getArea() {
        return (AbstractArea) VaadinSession.getCurrent().getAttribute("area");
    }



    private void addItem(MenuItem parent, MenuEntry e) {
        if (e instanceof AbstractMenu) {
            //for (MenuEntry ee : ((AbstractMenu)e).getEntries()) addMenu(ee); // antes, menú plano sin submenús

            MenuItem dropdown = parent.addItem(e.getName(), null);

            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) addItem(dropdown, ee);

        }

        if (e instanceof AbstractAction) {

            parent.addItem(e.getName(), new MenuBar.Command() {
                @Override
                public void menuSelected(MenuItem menuItem) {
                    ((AbstractAction)e).run();
                }
            });
        }
    }
    private void addMenu(MenuEntry e) {

        if (e instanceof AbstractMenu) {
            //for (MenuEntry ee : ((AbstractMenu)e).getEntries()) addMenu(ee); // antes, menú plano sin submenús


            MenuBar b = new MenuBar();
            MenuBar.MenuItem dropdown = b.addItem(e.getName(), null);
            //dropdown = b.addItem("", null);
            /*
            dropdown.addItem("Another Action", null);
            dropdown.addItem("Secondary Action", null);
            dropdown.addSeparator();
            dropdown.addItem("Last Action", null);
*/

            //b.addStyleName(ValoTheme.MENUBAR_BORDERLESS);

            //b.addStyleName("user-menu");

            for (MenuEntry ee : ((AbstractMenu)e).getEntries()) addItem(dropdown, ee);


            //b.setIcon(testIcon.get());
            //b.setPrimaryStyleName("submenu");
            b.addStyleName("submenu");
            b.setCaptionAsHtml(true);

            HorizontalLayout div = new HorizontalLayout();
            div.setSpacing(false);
            //div.addStyleName("divsubmenu");
            Label l = new Label();
            //l.setIcon(testIcon.get());  // sin iconos en el menú
            //l.addStyleName("iconosubmenu");
            div.addComponent(l);
            div.addComponent(b);

            menuItemsLayout.addComponent(div);

        }

        if (e instanceof AbstractAction) {

            Button b = new Button(e.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    //navigator.navigateTo(item.getKey());
                    ((AbstractAction)e).run();
                }
            });
            b.setCaption(b.getCaption()
            //        + " <span class=\"valo-menu-badge\">123</span>"
            );
            b.setCaptionAsHtml(true);
            b.setPrimaryStyleName(ValoTheme.MENU_ITEM);
            //b.setIcon(testIcon.get());  // sin iconos en el menú
            menuItemsLayout.addComponent(b);
        }


    }



}
