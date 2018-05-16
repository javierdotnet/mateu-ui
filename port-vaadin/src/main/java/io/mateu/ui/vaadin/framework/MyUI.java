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
package io.mateu.ui.vaadin.framework;

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
import io.mateu.ui.vaadin.components.HomeView;
import io.mateu.ui.vaadin.components.ViewLayout;
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


//@Theme("tests-valo-reindeer")
//@Theme("quonext")
//@Title("Mateu.io")
//@StyleSheet("valo-theme-ui.css")
@StyleSheet({"css/font-awesome.min.css"})
//@Push
@PreserveOnRefresh
public class MyUI extends UI {

    private static int fileId = 0;
    private Resource foto;
    private static Resource sinfoto;
    private VerticalLayout divIndicadorAreaActual;

    static {
         sinfoto = new ClassResource("profile-pic-300px.jpg");
    }

    private Button linkFavoritos;
    private Button linkUltimosRegistros;
    private Button linkNuevoFavorito;

    public void search(AbstractListView lv, int page) {
        //miguel: buscar
        String u = lv.getViewIdBase() + "/" + BaseEncoding.base64().encode(lv.getForm().getData().strip("_data").toString().getBytes());
        if (page != 0) u += "/" + page;
        navigator.navigateTo(u);
    }

    private TestIcon testIcon = new TestIcon(100);
    MenuLayout root = new MenuLayout();
    ComponentContainer viewDisplay = root.getContentContainer();
    CssLayout menu = new CssLayout();
    VerticalLayout menuItemsLayout = new VerticalLayout();
    Map<MenuEntry, Button> botonesMenu = new HashMap<>();

    {
        menu.addStyleName("testMenu");
        menuItemsLayout.setSpacing(false);
    }
    public Navigator navigator;
    private MenuBar settings;

    private AbstractArea areaActual;
    private MenuEntry menuActual;

    public AbstractArea getAreaActual() {
        return areaActual;
    }

    public void setAreaActual(AbstractArea areaActual) {
        this.areaActual = areaActual;
    }

    public MenuEntry getMenuActual() {
        return menuActual;
    }

    public void setMenuActual(MenuEntry menuActual) {
        this.menuActual = menuActual;
        for (MenuEntry e : botonesMenu.keySet()) {
            Button b = botonesMenu.get(e);
            if (menuActual == null || !(menuActual.equals(e) || getApp().getPath(menuActual).contains(e))) b.removeStyleName("posicionactual");
            if (menuActual != null && (menuActual.equals(e) || getApp().getPath(menuActual).contains(e))) b.addStyleName("posicionactual");
        }
    }

    public ComponentContainer getViewDisplay() {
        return viewDisplay;
    }

    /**
     * por aquí entramos. se llama al inicio, cuando refrescamos la página (si no está anotado @preserveonrefresh) o la abrimos en otra pestaña
     * @param request
     */
    @Override
    protected void init(VaadinRequest request) {

        /*
        inicializamos la configuración del tooltip para las ayudas
         */
        TooltipConfiguration ttc = super.getTooltipConfiguration();
        ttc.setOpenDelay(200);
        ttc.setQuickOpenDelay(300);
        ttc.setQuickOpenTimeout(300);

        /*
        si no tenemos la app en la sesión, entonces la buscamos utilizando SPI y la metemos en la sesión
         */
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
                                                        io.mateu.ui.core.client.app.MateuUI.alert("hola!");
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



        /*
        corregimos el acho de la barra izda para IE9
         */
        if (getPage().getWebBrowser().isIE()
                && getPage().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }


        /*
        ponemos el título de la página
         */
        getPage().setTitle(getApp().getName());

        /*
        fijamos el componente root como contenido de la página y lo ajustamos al 100% del ancho de la página
         */
        setContent(root);
        root.setWidth("100%");


        /*
        creamos un navigator
         */
        navigator = new Navigator(this, viewDisplay);


        /*
        añadimos el componente menú
         */
        root.addMenu(buildMenu(request));
        addStyleName(ValoTheme.UI_WITH_MENU);


        /*
        buscamos view providers utilizando SPI. Los view providers convierten de url a vista vaadin
         */
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


        /*
        actualizar los settings (datos del usuario en la parte izda)
         */
        refreshSettings();

        /*
        ponemos una vista por defecto
         */
        navigator.setErrorView(HomeView.class);

        /*
        añadimos un listener para cuando cambia la url. Aquí es donde se gestiona la navegación
         */
        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                /*
                aquí controlamos si el usuario tiene acceso a esta vista
                 */
                boolean ok = true;
                return ok;
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
                                io.mateu.ui.core.client.app.MateuUI.notifyErrors(errors);
                            } else {
                                lv.rpc();
                            }
                        }

                    }

                    getViewDisplay().removeAllComponents();
                    getViewDisplay().addComponent(v.getViewComponent());

                    refreshMenu(((ViewLayout) v).getView().getArea(), ((ViewLayout) v).getView().getMenu());

                    getPage().setTitle(((ViewLayout) v).getView().getTitle());

                }


                for (Iterator<Component> it = menuItemsLayout.iterator(); it
                        .hasNext();) {
                    it.next().removeStyleName("selected");
                }
                menu.removeStyleName("valo-menu-visible");

            }
        });


        /*
        si hemos entrado por la raíz y no hay contenidos públicos, entonces pedir el login inmediatamente
         */
        String f = Page.getCurrent().getUriFragment();
        System.out.println("Page.getCurrent().getUriFragment()=" + f);
        if (f == null || f.equals("")) {
            boolean hayPartePublica = false;
            for (AbstractArea a : getApp().getAreas()) {
                hayPartePublica |= a.isPublicAccess();
            }
            if (!hayPartePublica) openLoginDialog();
        }


    }



    /**
     * convierte nuestro view de mateu-ui a una view de vaadin
     *
     * @param view
     * @return
     */
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


    /**
     * inicialización del port, para saber si estamos con javafx o vaadin.
     *
     * también guarda nuestra app en la sesión de vaadin
     *
     * todo: comprobar que la sesión de vaadin y no la sesión http es donde queremos guardar la app
     *
     * @param app
     */
    private void setApp(AbstractApplication app) {
        app.setPort(AbstractApplication.PORT_VAADIN);
        VaadinSession.getCurrent().setAttribute("app", app);
    }

    /**
     * recupera la app de la sesión
     *
     * @return
     */
    public AbstractApplication getApp() {
        return (AbstractApplication) VaadinSession.getCurrent().getAttribute("app");
    }

    /**
     * abre una vista. La colocará en el lado derecho
     *
     * @param ui
     * @param view
     */
    public static void addView(MyUI ui, AbstractView view) {

        if (view == null) {
            System.out.println("abriendo vista null");
            ui.getViewDisplay().removeAllComponents();
        } else {

            System.out.println("abriendo vista " + view.getClass().getName() + "::" + view.getViewId());

            ViewLayout v = new ViewLayout(view);
            if (view instanceof AbstractDialog) {

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
                            io.mateu.ui.core.client.app.MateuUI.notifyErrors(errors);
                        } else {
                            //if (d.isCloseOnOk()) subWindow.close();
                            ((AbstractAddRecordDialog)view).addAndClean(v.getView().getForm().getData());
                        }
                    });
                } else {
                    ok = new Button(d.getOkText(), e -> {
                        List<String> errors = v.getView().getForm().validate();
                        if (errors.size() > 0) {
                            io.mateu.ui.core.client.app.MateuUI.notifyErrors(errors);
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
                            io.mateu.ui.core.client.app.MateuUI.notifyErrors(errors);
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
                            io.mateu.ui.core.client.app.MateuUI.notifyErrors(errors);
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

                System.out.println("añadiendo vista al contenedor de vistas");

                ui.getViewDisplay().removeAllComponents();
                ui.getViewDisplay().addComponent(v);

                ui.refreshMenu(v.getArea(), v.getMenu());

            }

        }

    }

    /**
     * ni idea de para que sirve esto. Viene de vadin
     *
     * @return
     */
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


    /**
     * construye el menu
     *
     * @param request
     * @return
     */
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

        //Label title = new Label("<h3><strong>" + getApp().getName() + "</strong></h3>", ContentMode.HTML);

        Button title = new Button(getApp().getName(), new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Page.getCurrent().open("#!", (event.isAltKey() || event.isCtrlKey()) ? "_blank" : Page.getCurrent().getWindowName());
            }
        });
        title.addStyleName(ValoTheme.BUTTON_LINK);
        title.addStyleName("tituloapp");

        title.setSizeUndefined();
        top.addComponent(title);
        top.setExpandRatio(title, 1);

        settings = new MenuBar();
        settings.addStyleName("user-menu");
        settings.addStyleName("mi-user-menu");
        menu.addComponent(settings);


        HorizontalLayout navlinks = new HorizontalLayout();
        navlinks.setSpacing(true);

        {
            Button nav = new Button(VaadinIcons.ARROWS_CROSS, new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    Page.getCurrent().open("#!nav", (event.isAltKey() || event.isCtrlKey()) ? "_blank" : Page.getCurrent().getWindowName());
                }
            });
            nav.addStyleName(ValoTheme.BUTTON_LINK);
            nav.setDescription("Search inside menu");
            nav.addStyleName("navlink");

            navlinks.addComponent(nav);
        }

        if (MateuUI.getApp().isFavouritesAvailable()) {
            Button nav = new Button(VaadinIcons.USER_STAR, new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    Page.getCurrent().open("#!favourites", (event.isAltKey() || event.isCtrlKey()) ? "_blank" : Page.getCurrent().getWindowName());
                }
            });
            nav.addStyleName(ValoTheme.BUTTON_LINK);
            nav.addStyleName("navlink");
            nav.setDescription("My favourites");
            nav.setVisible(MateuUI.getApp().getUserData() != null);

            linkFavoritos = nav;

            navlinks.addComponent(nav);
        }

        if (MateuUI.getApp().isLastEditedAvailable()) {
            Button nav = new Button(VaadinIcons.RECORDS, new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    Page.getCurrent().open("#!lastedited", (event.isAltKey() || event.isCtrlKey()) ? "_blank" : Page.getCurrent().getWindowName());
                }
            });
            nav.addStyleName(ValoTheme.BUTTON_LINK);
            nav.addStyleName("navlink");
            nav.setDescription("Last edited records");
            nav.setVisible(MateuUI.getApp().getUserData() != null);

            linkUltimosRegistros = nav;

            navlinks.addComponent(nav);
        }

        HorizontalLayout aux = new HorizontalLayout(navlinks);
        aux.setSpacing(false);
        aux.addStyleName("contenedoriconosnav");
        menu.addComponent(aux);


        if (MateuUI.getApp().isFavouritesAvailable()) {
            aux = new HorizontalLayout();
            {
                Button nav = new Button(VaadinIcons.STAR, new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        System.out.println(Page.getCurrent().getUriFragment());
                        System.out.println(Page.getCurrent().getLocation());
                    }
                });
                nav.addStyleName(ValoTheme.BUTTON_LINK);
                nav.addStyleName("navlink");
                nav.setDescription("Add current page to my favourites");
                nav.setVisible(MateuUI.getApp().getUserData() != null);

                linkNuevoFavorito = nav;

                aux.addComponent(nav);
            }
            aux.setSpacing(false);
            aux.addStyleName("contenedoriconosnav");
            menu.addComponent(aux);
        }

        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);


        refreshMenu(null, null);

        return menu;
    }

    /**
     * actualiza los settings (la parte que muestra la sesión del usuario)
     *
     *
     */
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
                    foto = (getApp().getUserData().getPhoto() != null)?new ExternalResource(getApp().getUserData().getPhoto()):sinfoto,
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
                    getApp().setUserData(null);
                    getViewDisplay().removeAllComponents();
                    refreshSettings();

                    if (MateuUI.getApp().isFavouritesAvailable()) linkFavoritos.setVisible(false);
                    if (MateuUI.getApp().isLastEditedAvailable()) linkUltimosRegistros.setVisible(false);
                    if (MateuUI.getApp().isFavouritesAvailable()) linkNuevoFavorito.setVisible(false);

                    refreshMenu(null, null);
                    addView(MyUI.this, getApp().getPublicHome());
                    if (!"".equals(navigator.getState())) navigator.navigateTo("");
                }
            });

        }


    }

    /**
     * construye el menú
     */
    public void refreshMenu(AbstractArea area, MenuEntry menu) {

        if (area != null && area.equals(getAreaActual())) {

            // no cambia el area. Solo puede cambiar el menu

            if ((menu == null && getMenuActual() == null) || (menu != null && menu.equals(getMenuActual()))) {

                // no cambia nada. no hacemos nada

            } else {
                setMenuActual(menu);
            }

        } else {

            // cambia el area. debemos reconstruir el menu

            setAreaActual(area);

            VaadinSession s = VaadinSession.getCurrent();

            boolean autentico = s.getAttribute("usuario") != null;

            List<AbstractArea> areas = new ArrayList<>();

            for (AbstractArea a : getApp().getAreas()) {
                if (autentico) {
                    if (!a.isPublicAccess()) {
                        if (area == null) area = a;
                        areas.add(a);
                    }
                } else {
                    if (!getApp().isAuthenticationNeeded() || a.isPublicAccess()) {
                        if (area == null) area = a;
                        areas.add(a);
                    }
                }
            }

            /**
             * area no puede ser null
             */
            if (area != null) {
                menuItemsLayout.removeAllComponents();

                divIndicadorAreaActual = new VerticalLayout();
                divIndicadorAreaActual.setSpacing(false);
                divIndicadorAreaActual.addStyleName("divIndicadorAreaActual");

                if (areas.size() > 1) {

                    Button b;
                    AbstractArea finalArea = area;
                    divIndicadorAreaActual.addComponent(b = new Button("You are at " + ((area != null)?area.getName():"--"), new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent clickEvent) {
                            navigator.navigateTo(getApp().getAreaId(finalArea) + "/changearea");
                        }
                    }));
                    b.addStyleName(ValoTheme.BUTTON_LINK);
                    b.addStyleName("linkotrasareas");

                    menuItemsLayout.addComponent(divIndicadorAreaActual);
                }

                buildMenuOptions(area);
            }


            setMenuActual(menu);
        }


    }

    /**
     * are el diálogo para autenticarse
     */
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
                io.mateu.ui.core.client.app.MateuUI.getBaseService().forgotPassword(l.getValue(), new AsyncCallback<Void>() {

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
                io.mateu.ui.core.client.app.MateuUI.getBaseService().authenticate(l.getValue(), p.getValue(), new AsyncCallback<UserData>() {

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

                        if (MateuUI.getApp().isFavouritesAvailable()) linkFavoritos.setVisible(true);
                        if (MateuUI.getApp().isLastEditedAvailable()) linkUltimosRegistros.setVisible(true);
                        if (MateuUI.getApp().isFavouritesAvailable()) linkNuevoFavorito.setVisible(true);

                        refreshSettings();
                        refreshMenu(null, null);
                        addView(MyUI.this, getApp().getPrivateHome());
                        if (!"".equals(navigator.getState())) navigator.navigateTo("");
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

    /**
     * abre el diálogo para cambiar el password
     *
     */
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

                    io.mateu.ui.core.client.app.MateuUI.getBaseService().changePassword(getApp().getUserData().getLogin(), l.getValue(), p.getValue(), new AsyncCallback<Void>() {

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

    /**
     * abre el diálogo para editar el perfil
     *
     */
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
                io.mateu.ui.core.client.app.MateuUI.getBaseService().updateProfile(getApp().getUserData().getLogin(), l.getValue(), p.getValue(), null, new AsyncCallback<Void>() {

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
                        refreshSettings();
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

    /**
     * abre el diálogo para cambiar la foto
     *
     *
     */
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

                    io.mateu.ui.core.client.app.MateuUI.getBaseService().updateFoto(getApp().getUserData().getLogin(), loc, new AsyncCallback<Void>() {

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

                            refreshSettings();

                        }
                    });

                } catch (IOException e1) {
                    e1.printStackTrace();
                    io.mateu.ui.core.client.app.MateuUI.alert("" + e1.getClass().getName() + ":" + e1.getMessage());
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

    /**
     * crea el menú pa un área concreta
     *
     * @param a
     */
    private void buildMenuOptions(AbstractArea a) {

        Label label = null;


        // recger la opción de menú selecionada de la url
        String s = navigator.getState();
        System.out.println("state=" + s);

        for (AbstractModule m : a.getModules()) {

            label = new Label(m.getName(), ContentMode.HTML);
            label.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
            label.addStyleName(ValoTheme.LABEL_H4);
            label.setSizeUndefined();
            menuItemsLayout.addComponent(label);

            for (MenuEntry e : m.getMenu()) {

                addMenu(a, e);

            }
        }
    }

    public void changeToArea(AbstractArea a) {
        //todo: calcular la url y cargarla
    }


    /**
     * construye una opción del menú
     *
     */
    private void addMenu(AbstractArea area, MenuEntry e) {

        Button b = null;

        if (e instanceof AbstractMenu) {
            b = new Button(e.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    setAreaActual(area);
                    setMenuActual(e);
                    Page.getCurrent().open("#!" + getApp().getAreaId(area) + "/" + getApp().getMenuId(e) + "/menuhome", (event.isAltKey() || event.isCtrlKey())?"_blank":Page.getCurrent().getWindowName());
                }
            });
            b.setCaption(b.getCaption()
                    + " <span class=\"valo-menu-badge\">" + ((AbstractMenu) e).getEntries().size() + "</span>"
            );
        }

        if (e instanceof AbstractAction) {

            b = new Button(e.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    setAreaActual(area);
                    setMenuActual(e);
                    ((AbstractAction)e).setModifierPressed(event.isAltKey() || event.isCtrlKey()).run();
                }
            });

        }

        if (b != null) {
            b.setCaptionAsHtml(true);
            b.setPrimaryStyleName(ValoTheme.MENU_ITEM);

            //b.setIcon(testIcon.get());  // sin iconos en el menú
            menuItemsLayout.addComponent(b);

            botonesMenu.put(e, b);

        }


    }



}
