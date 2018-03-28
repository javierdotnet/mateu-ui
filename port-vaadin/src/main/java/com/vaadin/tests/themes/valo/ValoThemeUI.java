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
import com.google.common.io.BaseEncoding;
import com.google.common.io.Files;
import com.vaadin.annotations.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.*;
import com.vaadin.server.*;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.App;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.*;
import io.mateu.ui.core.shared.ViewProvider;
import io.mateu.ui.vaadin.HomeView;
import io.mateu.ui.vaadin.ViewLayout;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;


//@Theme("tests-valo-reindeer")
//@Theme("quonext")
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
    private VerticalLayout divIndicadorAreaActual;
    private VerticalLayout divRestoAreas;

    public void search(AbstractListView lv, int page) {
        //miguel: buscar
        String u = lv.getViewIdBase() + "/" + BaseEncoding.base64().encode(lv.getForm().getData().strip("_data").toString().getBytes());
        if (page != 0) u += "/" + page;
        navigator.navigateTo(u);
    }

    private TestIcon testIcon = new TestIcon(100);
    ValoMenuLayout root = new ValoMenuLayout();
    ComponentContainer viewDisplay = root.getContentContainer();
    CssLayout menu = new CssLayout();
    VerticalLayout menuItemsLayout = new VerticalLayout();

    {
        menu.addStyleName("testMenu");
        menuItemsLayout.setSpacing(false);
    }
    private Navigator navigator;
    private LinkedHashMap<String, String> menuItems = new LinkedHashMap<String, String>();
    private MenuBar settings;

    @Override
    protected void init(VaadinRequest request) {

        TooltipConfiguration ttc = super.getTooltipConfiguration();
        ttc.setOpenDelay(200);
        ttc.setQuickOpenDelay(300);
        ttc.setQuickOpenTimeout(300);

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
                    public List<AbstractArea> buildAreas() {
                        return Lists.newArrayList(new AbstractArea("Area 1") {
                            @Override
                            public List<AbstractModule> buildModules() {
                                return Lists.newArrayList(new AbstractModule() {
                                    @Override
                                    public String getName() {
                                        return "Módulo 1";
                                    }

                                    @Override
                                    public List<MenuEntry> buildMenu() {
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

        ServiceLoader<ViewProvider> sl = ServiceLoader.load(ViewProvider.class);

        for (ViewProvider p : sl) navigator.addProvider(new com.vaadin.navigator.ViewProvider() {
            @Override
            public String getViewName(String viewNameAndParameters) {
                return p.getViewName(viewNameAndParameters);
            }

            @Override
            public View getView(String viewName) {
                return getVaadinView(p.getView(viewName));
            }
        });


        //navigator.addView("common", CommonParts.class);

        String f = Page.getCurrent().getUriFragment();
        System.out.println("Page.getCurrent().getUriFragment()=" + f);
        if (f == null || f.equals("")) {
            //navigator.navigateTo("common");
            boolean hayPartePublica = false;
            for (AbstractArea a : getApp().getAreas()) {
                hayPartePublica |= a.isPublicAccess();
            }
            if (!hayPartePublica) openLoginDialog();
            else {
                AbstractView h;
                if ((h = getApp().getPublicHome()) != null) {
                    addView((ValoThemeUI) UI.getCurrent(), h);
                }
            }
        }

        navigator.setErrorView(HomeView.class);

        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {

                View v = event.getNewView();
                if (v instanceof ViewLayout) {

                    ((ViewLayout) v).getView().addListener(new ViewListener() {
                        @Override
                        public void onClose() {
                            UI.getCurrent().getPage().getJavaScript().execute("history.back()");
                        }
                    });

                    if (((ViewLayout) v).getView() instanceof AbstractEditorView) {
                        Object id = null;
                        String s = event.getParameters();
                        if (s != null) {
                            if (s.startsWith("s")) id = s.substring(1);
                            else if (s.startsWith("l")) id = Long.parseLong(s.substring(1));
                            else if (s.startsWith("i")) id = Integer.parseInt(s.substring(1));
                        }
                        if (id != null) {
                            ((AbstractEditorView) ((ViewLayout) v).getView()).setInitialId(id);
                            ((AbstractEditorView) ((ViewLayout) v).getView()).load();
                        }
                    } else if (((ViewLayout) v).getView() instanceof AbstractListView) {
                        AbstractListView lv = (AbstractListView) ((ViewLayout) v).getView();
                        Data data = null;

                        int page = 0;
                        String s = event.getParameters();
                        if (!Strings.isNullOrEmpty(s)) {

                            String d = s;
                            if (s.contains("/")) {
                                d = s.split("/")[0];
                                page = Integer.parseInt(s.split("/")[1]);
                            }

                            data = new Data(new String(BaseEncoding.base64().decode(d)));
                        }

                        if (data != null) {
                            lv.setData(data);
                        }

                        if (lv.isSearchOnOpen() || (data != null && data.getPropertyNames().size() > 0)) {
                                lv.set("_data_currentpageindex", page);
                            List<String> errors = lv.getForm().validate();
                            if (errors.size() > 0) {
                                MateuUI.notifyErrors(errors);
                            } else {
                                lv.rpc();
                            }
                        }

                    }

                }


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

    }

    public View getVaadinView(AbstractView view) {
        System.out.println("getVaadinView(" + ((view != null)?view.getClass().getName():"null") + ")");

        ViewLayout v = null;
        try {

            if (view != null) {

                v = new ViewLayout(view);

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

                        @Override
                        public void cleared() {

                        }
                    });
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }


    private void setApp(AbstractApplication app) {
        app.setPort(AbstractApplication.PORT_VAADIN);
        VaadinSession.getCurrent().setAttribute("app", app);
    }

    public AbstractApplication getApp() {
        return (AbstractApplication) VaadinSession.getCurrent().getAttribute("app");
    }


    public static void addView(ValoThemeUI ui, AbstractView view) {

        System.out.println("abriendo vista " + view.getClass().getName() + "::" + view.getViewId());

        if (view instanceof AbstractDialog) {
            ViewLayout v = new ViewLayout(view);

            AbstractDialog d = (AbstractDialog) view;

            // Create a sub-window and set the content
            Window subWindow = new Window(((AbstractDialog)view).getTitle());

            HorizontalLayout footer = new HorizontalLayout();
            footer.setWidth("100%");
            footer.setSpacing(true);
            footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);

            Label footerText = new Label("");
            footerText.setSizeUndefined();


            Button ok;
            if (d instanceof AbstractAddRecordDialog) {
                ok = new Button("Add record", e -> {
                    List<String> errors = v.getView().getForm().validate();
                    if (errors.size() > 0) {
                        MateuUI.notifyErrors(errors);
                    } else {
                        //if (d.isCloseOnOk()) subWindow.close();
                        ((AbstractAddRecordDialog)view).addAndClean(v.getView().getForm().getData());
                    }
                });
            } else {
                ok = new Button(d.getOkText(), e -> {
                    List<String> errors = v.getView().getForm().validate();
                    if (errors.size() > 0) {
                        MateuUI.notifyErrors(errors);
                    } else {
                        if (d.isCloseOnOk()) subWindow.close();
                        ((AbstractDialog)view).onOk(v.getView().getForm().getData());
                    }
                });
            }
            ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
            ok.setClickShortcut(ShortcutAction.KeyCode.ENTER);

            footer.addComponents(footerText);

            for (AbstractAction a : d.getActions()) {
                Button b = new Button(a.getName(), e -> {
                    a.run();
                });
                //b.addStyleName(ValoTheme.BUTTON_);
                //b.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                if ("previous".equalsIgnoreCase(a.getName())) {
                    b.setIcon(VaadinIcons.ANGLE_LEFT);
                } else if ("next".equalsIgnoreCase(a.getName())) {
                    b.setIcon(VaadinIcons.ANGLE_RIGHT);
                }
                footer.addComponent(b);
            }


            if (d instanceof AbstractListEditorDialog) {
                AbstractListEditorDialog lv = (AbstractListEditorDialog) d;

                Property<Integer> pos = new SimpleObjectProperty<>();

                pos.setValue(lv.getInitialPos());


                Button prev = new Button("Previous", e -> {
                    List<String> errors = v.getView().getForm().validate();
                    if (errors.size() > 0) {
                        MateuUI.notifyErrors(errors);
                    } else {

                        if (pos.getValue() > 0) {
                            lv.setData(pos.getValue(), view.getForm().getData());
                            pos.setValue(pos.getValue() - 1);
                            view.getForm().setData(lv.getData(pos.getValue()));
                        }

                    }
                });
                prev.setIcon(VaadinIcons.ANGLE_LEFT);
                footer.addComponent(prev);

                Button next = new Button("Next", e -> {
                    List<String> errors = v.getView().getForm().validate();
                    if (errors.size() > 0) {
                        MateuUI.notifyErrors(errors);
                    } else {

                        if (pos.getValue() < lv.getListSize() - 1) {
                            lv.setData(pos.getValue(), view.getForm().getData());
                            pos.setValue(pos.getValue() + 1);
                            view.getForm().setData(lv.getData(pos.getValue()));
                        }

                    }
                });
                next.setIcon(VaadinIcons.ANGLE_RIGHT);
                footer.addComponent(next);

                pos.addListener(new ChangeListener<Integer>() {
                    @Override
                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                        if (newValue <= 0) {
                            prev.setEnabled(false);
                        } else {
                            prev.setEnabled(true);
                        }
                        if (newValue < lv.getListSize() - 1) {
                            next.setEnabled(true);
                        } else {
                            next.setEnabled(false);
                        }
                    }
                });

            }

            footer.addComponents(ok); //, cancel);
            footer.setExpandRatio(footerText, 1);

            v.addComponent(footer);

            subWindow.setContent(v);

            // Center it in the browser window
            subWindow.center();

            subWindow.setModal(true);

            // Open it in the UI
            ui.addWindow(subWindow);

        } else {

            String viewId = view.getViewId();

            if (MateuUI.getApp().getPosicion() != null) viewId = "pos" + MateuUI.getApp().getPosicion().getId() + ".." + viewId;
            if (MateuUI.getApp().getArea() != null) viewId = "area" + MateuUI.getApp().getArea().getId() + ".." + viewId;

            System.out.println("navigating to " + viewId);

            ui.navigator.navigateTo(viewId);

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


        refreshMenu();

        return menu;
    }

    private void refreshSettings() {
        VaadinSession s = VaadinSession.getCurrent();

        settings.removeItems();

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
                    getApp().setArea(null);
                    getApp().setPosicion(null);
                    refreshSettings();
                    refreshMenu();
                    AbstractView h;
                    if ((h = getApp().getPublicHome()) != null) {
                        addView((ValoThemeUI) UI.getCurrent(), h);
                    } else navigator.navigateTo("");
                }
            });

        }


    }

    private void refreshMenu() {

        VaadinSession s = VaadinSession.getCurrent();
        menuItemsLayout.removeAllComponents();

        if (s.getAttribute("usuario") == null) {

            System.out.println("***** NO AUTENTICADO.");

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


            divIndicadorAreaActual = new VerticalLayout();
            divIndicadorAreaActual.setSpacing(false);
            divIndicadorAreaActual.addStyleName("divIndicadorAreaActual");

            List<Pair> areas = new ArrayList<>();
            for (AbstractArea a : getApp().getAreas()) {
                boolean isPublic = a.isPublicAccess();

                if (!isPublic) {

                    areas.add(new Pair(a, a.getName()));

                    if (getArea() == null) {
                        setArea(a);
                    }

                }
            }

            if (getArea() == null && getApp().getAreas().size() > 0) setArea(getApp().getAreas().get(0));

            divRestoAreas = new VerticalLayout();
            Label lx;
            divRestoAreas.addComponent(lx = new Label("Other available areas:"));
            lx.addStyleName("labelotrasareas");
            divRestoAreas.setSpacing(false);
            divRestoAreas.addStyleName("divRestoAreas");

            if (areas.size() > 1) {

                divIndicadorAreaActual.addComponent(new Label("You are at " + ((getArea() != null)?getArea().getName():"--")));

                for (AbstractArea x : getApp().getAreas()) if (!x.equals(getArea())) {
                    Button b;
                    divRestoAreas.addComponent(b = new Button(x.getName(), new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent clickEvent) {
                            setArea(x);
                            refreshMenu();
                        }
                    }));
                    b.addStyleName(ValoTheme.BUTTON_LINK);
                    b.addStyleName("linkotrasareas");
                }


            }

            if (getArea() != null) refreshMenu(areas, getArea());

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
                        getApp().setArea(null);
                        getApp().setPosicion(null);
                        refreshSettings();
                        refreshMenu();

                        AbstractView h;
                        if ((h = getApp().getPrivateHome()) != null) {
                            addView((ValoThemeUI) UI.getCurrent(), h);
                        }

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

        Image image = new Image();

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
                image.setSource(new FileResource(file));
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

        f.addComponent(image);

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

    private void refreshMenu(List<Pair> areas, AbstractArea a) {

        menuItemsLayout.removeAllComponents();

        if (areas.size() > 1) menuItemsLayout.addComponent(divIndicadorAreaActual);

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

        if (areas.size() > 1) menuItemsLayout.addComponent(divRestoAreas);
    }

    private void setArea(AbstractArea a) {
        //VaadinSession.getCurrent().setAttribute("area", a);
        MateuUI.getApp().setArea(a);
    }

    private AbstractArea getArea() {
        //return (AbstractArea) VaadinSession.getCurrent().getAttribute("area");
        return MateuUI.getApp().getArea();
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
            Button b = new Button(e.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getApp().setPosicion(e);
                    MateuUI.openView(new MenuView(e.getName(), getApp().getMenuLocator(e)), event.isAltKey() || event.isCtrlKey());
                    refreshMenu();
                }
            });
            b.setCaption(b.getCaption()
                    + " <span class=\"valo-menu-badge\">" + ((AbstractMenu) e).getEntries().size() + "</span>"
            );
            b.setCaptionAsHtml(true);
            b.setPrimaryStyleName(ValoTheme.MENU_ITEM);

            if (getApp().getPosicion() != null && getApp().getPosicion().equals(e)) b.addStyleName("posicionactual");

            //b.setIcon(testIcon.get());  // sin iconos en el menú
            menuItemsLayout.addComponent(b);
        }

        if (e instanceof AbstractAction) {

            Button b = new Button(e.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    getApp().setPosicion(e);
                    ((AbstractAction)e).setModifierPressed(event.isAltKey() || event.isCtrlKey()).run();
                    refreshMenu();
                }
            });
            b.setCaption(b.getCaption()
            //        + " <span class=\"valo-menu-badge\">123</span>"
            );
            b.setCaptionAsHtml(true);
            b.setPrimaryStyleName(ValoTheme.MENU_ITEM);

            if (getApp().getPosicion() != null && getApp().getPosicion().equals(e)) b.addStyleName("posicionactual");

            //b.setIcon(testIcon.get());  // sin iconos en el menú
            menuItemsLayout.addComponent(b);
        }


    }



}
