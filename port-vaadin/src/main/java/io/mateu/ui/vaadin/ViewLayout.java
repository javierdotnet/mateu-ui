package io.mateu.ui.vaadin;

import com.google.common.base.Strings;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.*;
import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.DateTimeField;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.DataColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.*;
import io.mateu.ui.vaadin.data.DataStore;
import io.mateu.ui.vaadin.data.ViewNodeDataStore;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.util.*;

/**
 * Created by miguel on 4/1/17.
 */
public class ViewLayout extends VerticalLayout implements View {

    private DataStore dataStore;

    Window win = new Window("Waiting...");

    private final AbstractView view;
    private int fileId = 0;

    private AbstractComponent firstField;

    public AbstractView getView() {
        return view;
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public ViewLayout(AbstractView view) {
        this.view = view;
        view.getForm().setHelper(new FormHelper() {
            @Override
            public Data getData() {
                return getDataStore().getData();
            }
        });
        this.dataStore = new ViewNodeDataStore(this);
        init();
    }

    public ViewLayout(DataStore dataStore, AbstractView view) {
        this.view = view;
        view.getForm().setHelper(new FormHelper() {
            @Override
            public Data getData() {
                return getDataStore().getData();
            }
        });
        this.dataStore = dataStore;
        init();
    }

    public void init() {


        view.getData();
        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                ViewLayout.this.dataStore.setData(newData);
                String t = view.getTitle();
                if (view instanceof AbstractEditorView) {
                    Object id = ViewLayout.this.dataStore.get("_id");
                    if (id == null) t = "New " + t;
                    else {
                        String text = ViewLayout.this.dataStore.get("_tostring");
                        if (text == null) text = "" + id;
                        t += " " + text;
                    }
                }
                ViewLayout.this.dataStore.set("_title", t);

                if (firstField != null && firstField instanceof com.vaadin.ui.AbstractField) {
                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            ((com.vaadin.ui.AbstractField)firstField).focus();
                        }
                    });
                }
            }

            @Override
            public void setted(String k, Object v) {
                ViewLayout.this.dataStore.set(k, v);
            }

            @Override
            public void idsResetted() {
                ViewLayout.this.dataStore.resetIds();
            }
        });
        this.dataStore.set("_title", view.getTitle());

        setMargin(true);
        addStyleName("content-common");


        if (!(view instanceof AbstractDialog)) {

            String t = view.getTitle();
            if (view instanceof AbstractEditorView) {
                Object id = this.dataStore.get("_id");
                if (id == null) t = "New " + t;
                else {
                    String text = this.dataStore.get("_tostring");
                    if (text == null) text = "" + id;
                    t += " " + text;
                }

                this.dataStore.getProperty("_id").addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                String t = view.getTitle();
                                if (newValue == null) t = "New " + t;
                                else {
                                    String text = ViewLayout.this.dataStore.get("_tostring");
                                    if (text == null) text = "" + newValue;
                                    t += " " + text;
                                }
                                ViewLayout.this.dataStore.getProperty("_title").setValue(t);
                            }
                        });
                    }
                });
            }

            Label h1 = new Label(t);
            h1.addStyleName(ValoTheme.LABEL_H1);

            Property p = this.dataStore.getProperty("_title");
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    h1.setValue((newValue != null)?"" + newValue:null);
                }
            });
            p.setValue(t);

            {
                HorizontalLayout hl = new HorizontalLayout();
                hl.addComponent(h1);
                HorizontalLayout badges = new HorizontalLayout();
                hl.addComponent(badges);

                ChangeListener<ObservableList<DataStore>> bl;
                Property<ObservableList<DataStore>> pb = this.dataStore.getObservableListProperty("_badges");
                pb.addListener(bl = new ChangeListener<ObservableList<DataStore>>() {
                    @Override
                    public void changed(ObservableValue<? extends ObservableList<DataStore>> observable, ObservableList<DataStore> oldValue, ObservableList<DataStore> newValue) {
                        badges.removeAllComponents();
                        if (newValue != null) for (DataStore x : newValue) {
                            Label l = new Label("" + x.get("_value"));
                            l.addStyleName("valo-badge-style");
                            l.addStyleName("superbadge");
                            if (x.get("_css") != null) {
                                l.addStyleName("" + x.get("_css"));
                            }
                            badges.addComponent(l);
                        }
                    }
                });
                bl.changed(null, null, pb.getValue());

                pb.getValue().addListener(new ListChangeListener<DataStore>() {
                    @Override
                    public void onChanged(Change<? extends DataStore> c) {
                        badges.removeAllComponents();
                        if (pb.getValue() != null) for (DataStore x : pb.getValue()) {
                            Label l = new Label("" + x.get("_value"));
                            l.addStyleName("valo-badge-style");
                            l.addStyleName("superbadge");
                            if (x.get("_css") != null) {
                                l.addStyleName("" + x.get("_css"));
                            }
                            badges.addComponent(l);
                        }
                    }
                });


                addComponent(hl);
            }

            {
                HorizontalLayout links = new HorizontalLayout();

                ChangeListener<ObservableList<DataStore>> bl;
                Property<ObservableList<DataStore>> pb = this.dataStore.getObservableListProperty("_links");
                pb.addListener(bl = new ChangeListener<ObservableList<DataStore>>() {
                    @Override
                    public void changed(ObservableValue<? extends ObservableList<DataStore>> observable, ObservableList<DataStore> oldValue, ObservableList<DataStore> newValue) {
                        links.removeAllComponents();
                        if (newValue != null) for (DataStore x : newValue) {
                            Button l = new Button("" + x.get("_caption"));
                            l.addStyleName(ValoTheme.BUTTON_LINK);
                            l.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {
                                    ((AbstractAction) x.get("_action")).run();
                                }
                            });
                            links.addComponent(l);
                        }
                    }
                });
                bl.changed(null, null, pb.getValue());

                pb.getValue().addListener(new ListChangeListener<DataStore>() {
                    @Override
                    public void onChanged(Change<? extends DataStore> c) {
                        links.removeAllComponents();
                        if (pb.getValue() != null) for (DataStore x : pb.getValue()) {
                            Button l = new Button("" + x.get("_caption"));
                            l.addStyleName(ValoTheme.BUTTON_LINK);
                            l.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent clickEvent) {
                                    ((AbstractAction) x.get("_action")).run();
                                }
                            });
                            links.addComponent(l);
                        }
                    }
                });


                addComponent(links);
            }

            Label subtitleLabel;
            addComponent(subtitleLabel = new Label());
            this.dataStore.getProperty("_subtitle").addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    subtitleLabel.setValue((newValue != null)?"" + newValue:null);
                }
            });

        }


//        setContent(new MVerticalLayout(new MHorizontalLayout(c, d).withFullWidth())
//                .expand(new MHorizontalLayout(menu).expand(mainContent)));


        //setMargin(false);
        //setWidth("800px");
        //addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        buildToolBar();

        if (view instanceof AbstractListView) {
            add(this, new GridField("_data", ((AbstractListView) view).getColumns(), true).setExpandable(false).setFullWidth(true), false);
        } else {
            buildBody(this, view.getForm());
        }

        addListeners();


        ProgressBar pb = new ProgressBar();
        pb.setIndeterminate(true);
        HorizontalLayout h = new HorizontalLayout();
        h.addComponent(pb);
        h.addComponent(new Label("Waiting..."));
        h.setMargin(true);
        h.setSpacing(true);
        win.setContent(h);
        win.setHeight("80px");
        win.setWidth("200px");
        win.setCaption(null);
        win.setResizable(false);
        win.setDraggable(false);
        win.setClosable(false);
        win.setModal(true);


        if (firstField != null && firstField instanceof com.vaadin.ui.AbstractField) {
            MateuUI.runInUIThread(new Runnable() {
                @Override
                public void run() {
                    ((com.vaadin.ui.AbstractField)firstField).focus();
                }
            });
        }

    }

    public void startWaiting() {
    }

    public void endWaiting() {
    }


    private void addListeners() {

        if (view instanceof AbstractEditorView) {
            AbstractEditorView ev = (AbstractEditorView) view;
            ev.addEditorViewListener(new EditorViewListener() {
                @Override
                public void onLoad() {
                    startWaiting();
                }

                @Override
                public void onSave() {
                    startWaiting();
                }

                @Override
                public void onSuccessLoad(Data result) {
                    endWaiting();
                }

                @Override
                public void onSuccessSave(Data result) {

                }

                @Override
                public void onFailure(Throwable caught) {
                    endWaiting();
                }
            });
            if (ev.getInitialId() != null) ev.load();
        }

        if (view instanceof AbstractListView) {
            AbstractListView lv = (AbstractListView) view;
            lv.addListViewListener(new ListViewListener() {
                @Override
                public void onReset() {

                }

                @Override
                public void onSearch() {
                    startWaiting();
                }

                @Override
                public void onSuccess() {
                    endWaiting();
                }

                @Override
                public void onFailure(Throwable caught) {
                    endWaiting();
                }
            });
        }

    }

    private void buildBody(Layout layout, FieldContainer fields) {

        HorizontalLayout row = null;

        /*
        FormLayout row = new FormLayout();
        row.setWidth("100%");
        row.setSpacing(true);
        addComponent(row);
        */

        for (io.mateu.ui.core.client.components.Component c : fields.getComponentsSequence()) {
            if (c instanceof GridField && (((GridField) c).isFullWidth() || ((GridField) c).getColumns().size() > 4)) {
                if (layout instanceof HorizontalLayout) layout = (Layout) layout.getParent();
                add(layout, (AbstractField) c);
            } else if (c instanceof Tabs && ((Tabs) c).isFullWidth()) {
                if (layout instanceof HorizontalLayout) layout = (Layout) layout.getParent();
                add(layout, (AbstractField) c);
            } else {
                if (row == null || (c instanceof AbstractField && ((AbstractField)c).isBeginingOfLine())) {
                    row = new HorizontalLayout();
                    row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
                    row.setSpacing(true);
                    layout.addComponent(row);
                }
                if (c instanceof io.mateu.ui.core.client.components.Button) {

                    io.mateu.ui.core.client.components.Button b = (io.mateu.ui.core.client.components.Button) c;

                    HorizontalLayout h = new HorizontalLayout();
                    Button x;
                    h.addComponent(x = new Button(b.getName()));
                    h.setCaption(" ");
                    row.addComponent(h);
                    x.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            b.run();
                        }
                    });

                } else if (c instanceof AbstractField) {

                    add(row, (AbstractField) c);

                }
            }
        }
    }

    private void buildToolBar() {

        if (view.getActions().size() > 0) {
            MenuBar menubar = new MenuBar();
            menubar.setWidth("100%");
            for (AbstractAction a : view.getActions()) {
                MenuBar.MenuItem item = menubar.addItem(a.getName(), new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem menuItem) {
                        a.run();
                    }
                });

                if ("save and close".equalsIgnoreCase(a.getName())) {
                    item.setStyleName("saveandclose");
                }

            }
            if (true) {
                MenuBar.MenuItem item = menubar.addItem("Data", new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem menuItem) {
                        System.out.println(view.getData());
                    }
                });
            }
            addComponent(menubar);
        }


        AbstractAction shortcuttable = null;

        if (view instanceof AbstractListView) {
            shortcuttable = new AbstractAction("") {
                @Override
                public void run() {
                    ((AbstractListView) view).search();
                }
            };
        } else for (AbstractAction a : view.getActions()) {
            if (a.isCallOnEnterKeyPressed()) {
                shortcuttable = a;
                break;
            }
        }
        if (shortcuttable != null) {
            AbstractAction finalShortcuttable = shortcuttable;
            Button bx = new Button("", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    finalShortcuttable.run();
                }
            });

            bx.setWidth("0px");
            bx.setHeight("0px");
            bx.setStyleName("transparent");
            bx.setClickShortcut(ShortcutAction.KeyCode.ENTER);
            addComponent(bx);
        } else {
            addComponent(new Label(" "));
        }

        if (view instanceof AbstractListView) {

            HorizontalLayout h = new HorizontalLayout();
            h.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
            //h.setSpacing(true);
            //h.setMargin(true);
            //h.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            h.setMargin(new MarginInfo(false, false, true, false));
            h.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);

            h.setSpacing(true);

            //h.addComponent(f);


            int mfih = ((AbstractListView) view).getMaxFieldsInHeader();
            if (mfih < 100) mfih = 100;
            int posField = 0;
            for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
                if (c instanceof AbstractField) {

                    add(h, (AbstractField) c, true, true);

                    posField++;
                    if (posField >= mfih) break;
                }
            }

            addComponent(h);
        }
    }

    private void add(Layout where, AbstractField c) {
        add(where, c, true);
    }

    private void add(Layout where, AbstractField c, boolean paintLabel) {
        add(where, c, paintLabel, false);
    }
    private void add(Layout where, AbstractField c, boolean paintLabel, boolean inToolbar) {
        for (Component x : getVaadinComponent(c, inToolbar)) {
            where.addComponent(x);
            if (inToolbar) x.addStyleName("inline");
        }
    }

    private double getWidth(GridField field, List<AbstractColumn> columns) {
        double w = 0;
        for (AbstractColumn c : columns) w += c.getWidth();
        if (field.isExpandable()) w += 62;
        return w;
    }

    private List<Component> getVaadinComponent(AbstractField field, boolean inToolbar) {

        List<Component> cs = new ArrayList<>();

        Data data = dataStore.getData();

        Object v = data.get(field.getId());


        if (field instanceof GridField) {

            GridField g = (GridField) field;

            Grid<DataStore> table = new Grid<>((g.getLabel() != null) ? g.getLabel().getText() : null);

            String pname = g.getId();

            if (g.isUsedToSelect()) {
                pname += "_data";
            }

            Property<ObservableList<DataStore>> p = dataStore.getObservableListProperty(pname);


            Component loqueanadimos = table;

            //todo: utilizar para los listados!!!!
            //table.setDataProvider( fetchItems, sizeCallBack);

            ListDataProvider<DataStore> ldp;
            table.setDataProvider(ldp = new ListDataProvider<DataStore>(new ArrayList<>()));
            ldp.getItems().addAll(p.getValue());

            //System.out.println("*********************** g.getColumns().size()=" + g.getColumns().size());


            table.addColumn((d) -> d.getBooleanProperty("_selected").getValue()).setId("_selected");
            int pos = 0;
            for (AbstractColumn col : g.getColumns()) {
                //System.out.println("*********************** col.geLabel()=" + col.getLabel());
                if (col instanceof DataColumn) {
                    table.addColumn((d) -> ((DataStore) d.getProperty(col.getId()).getValue()).get("_text")).setId("__col_" + pos++).setCaption(col.getLabel());
                } else if (col instanceof LinkColumn) {
                    table.addColumn((d) -> d.getProperty(col.getId()).getValue()).setId("__col_" + pos++).setCaption(col.getLabel());
                } else {
                    table.addColumn((d) -> d.getProperty(col.getId()).getValue()).setId("__col_" + pos++).setCaption(col.getLabel());
                }
            }
            if (g.isExpandable())
                table.addColumn((d) -> "Edit").setId("_edit"); //todo: esto deber presentarse como un enlace / botón
            table.addColumn((d) -> d.getProperty("_dummycol").getValue()).setId("_dummycol"); // esta columna solo es para que quede bien y ocupe toda la pantalla


            if (!g.isUsedToSelect() || g.isUsedToSelectMultipleValues()) table.setSelectionMode(Grid.SelectionMode.MULTI);
            else table.setSelectionMode(Grid.SelectionMode.SINGLE);

            //todo: arreglar lo de los style generators
            CellStyleGenerator csg = new CellStyleGenerator(g.getColumns());

            if (csg.hasGenerators()) table.setStyleGenerator((item) -> {
                return "";
            });

            if (g.isFullWidth() || g.getColumns().size() > 4) {
                table.setSizeFull();
            } else {
                table.setHeightMode(HeightMode.UNDEFINED);
                //table.setWidth("" + getWidth(g, g.getColumns()) + "px");
                table.setHeightByRows(5);
            }

            System.out.println("*********************** table.getWidth()=" + table.getWidth());

            table.addSelectionListener(new SelectionListener<DataStore>() {
                @Override
                public void selectionChange(SelectionEvent<DataStore> selectionEvent) {

                    if (g.isUsedToSelect()) {
                        if (g.isUsedToSelectMultipleValues()) {
                            dataStore.getProperty(g.getId()).setValue(selectionEvent.getAllSelectedItems());
                        } else {
                            dataStore.getProperty(g.getId()).setValue(selectionEvent.getFirstSelectedItem());
                        }
                    } else {
                        for (DataStore d : p.getValue()) d.set("_selected", selectionEvent.getAllSelectedItems().contains(d));
                    }

                    /*
                    for (DataStore o : selectionEvent.getAllSelectedItems()) {
                        o.getBooleanProperty("_selected").setValue(true);
                        //todo: comprobar esto!!!
                        if (p.getValue() != null) p.getValue().get(p.getValue().indexOf(o)).set("_selected", true);
                    }
                    */

                }
            });

// Define two columns for the built-in container
            if (true) {
                table.getColumn("_selected").setHidden(true);
            }
            pos = 0;
            for (AbstractColumn col : g.getColumns()) {
                Grid.Column colx;
                if (col instanceof DataColumn) {

                    colx = table.getColumn("__col_" + pos).setCaption(col.getLabel()).setRenderer(new ButtonRenderer(new ClickableRenderer.RendererClickListener() {
                        @Override
                        public void click(ClickableRenderer.RendererClickEvent e) {
                            Data v = ((DataStore) e.getItem()).get(col.getId());
                            List<DataStore> l = (List<DataStore>) p.getValue();
                            ((DataColumn) col).run(v);
                        }


                    }));
                } else if (col instanceof LinkColumn) {

                    colx = table.getColumn("__col_" + pos).setRenderer(new ButtonRenderer(new ClickableRenderer.RendererClickListener() {
                        @Override
                        public void click(ClickableRenderer.RendererClickEvent e) {
                            ((LinkColumn) col).run(((DataStore) e.getItem()).getData());
                        }
                    }));
                } else {
                    colx = table.getColumn("__col_" + pos);
                }
                colx.setWidth(col.getWidth());
                //todo: comprobar si hay que alinear!!!
                /*
                Table.Align align = null;
                switch (col.getAlignment()) {
                    case CENTER:
                        align = Table.Align.CENTER;
                        break;
                    case RIGHT:
                        align = Table.Align.RIGHT;
                        break;
                    case LEFT:
                        align = Table.Align.LEFT;
                        break;
                }
                //colx.set(col.getId(), align);
                */
                pos++;
            }
            if (g.isExpandable()) {
                Grid.Column colx;

                colx = table.getColumn("_edit").setCaption("Edit").setRenderer(new ButtonRenderer(new ClickableRenderer.RendererClickListener() {
                    @Override
                    public void click(ClickableRenderer.RendererClickEvent e) {
                        AbstractForm f = g.getDataForm();

                        if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
                        else {
                            MateuUI.openView(new AbstractDialog() {

                                @Override
                                public Data initializeData() {
                                    return ((DataStore) e.getItem()).getData();
                                }

                                @Override
                                public void onOk(Data data) {
                                    ((DataStore) e.getItem()).setData(data);
                                    ldp.refreshAll();
                                }

                                @Override
                                public String getTitle() {
                                    return "Edit record";
                                }

                                @Override
                                public AbstractForm createForm() {
                                    return g.getDataForm();
                                }

                                @Override
                                public void build() {

                                }
                            });
                        }
                    }
                }));
                colx.setWidth(60);
            }
            table.getColumn("_dummycol").setWidthUndefined().setCaption("");

            p.getValue().addListener(new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {

                    table.getSelectionModel().deselectAll();

                    List<DataStore> l = (List<DataStore>) p.getValue(); //c.getList();
                    List ll = new ArrayList();
                    for (Object o : l) {
                        if (o instanceof DataStore) {
                            ll.add(((DataStore) o).getData());
                        } else ll.add(o);
                    }

                    data.set(field.getId(), ll);

                    ldp.getItems().clear();
                    ldp.getItems().addAll(l);
                    ldp.refreshAll();
                }
            });

            //table.setPageLength(10);

            if (g.isExpandable()) {

                VerticalLayout vl;
                loqueanadimos = vl = new VerticalLayout();
                if (g.isFullWidth()) vl.setWidth("100%");
                vl.addComponent(table);

                HorizontalLayout h = new HorizontalLayout();
                Button badd;
                h.addComponent(badd = new Button("Add"));
                badd.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        AbstractForm f = g.getDataForm();

                        if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
                        else {
                            MateuUI.openView(new AbstractDialog() {
                                @Override
                                public void onOk(Data data) {
                                    DataStore ds = new DataStore(data);
                                    p.getValue().add(ds); // añadimos solo al modelo, que luego añadirá al grid al dispararse el evento onchange
                                }

                                @Override
                                public String getTitle() {
                                    return "Add new record";
                                }

                                @Override
                                public AbstractForm createForm() {
                                    return g.getDataForm();
                                }

                                @Override
                                public void build() {

                                }
                            });
                        }
                    }
                });


                Button bdel;
                h.addComponent(bdel = new Button("Remove"));
                bdel.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        p.getValue().removeAll(table.getSelectionModel().getSelectedItems());
                    }
                });

                vl.addComponent(h);
            }

            if (g.isPaginated()) {
                //todo: añadir paginación!!!
                /*
                ComboBox og;
                og = new ComboBox("Page");
                og.setTextInputAllowed(false);
                og.setEmptySelectionAllowed(false);
                og.(0);
                og.setValue(0);
                og.setEnabled(false);

                Property pc = dataStore.getProperty(field.getId() + "_pagecount");
                pc.addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

                        og.setEnabled(false);
                        og.removeAllItems();

                        try {
                            int c = (Integer) newValue;
                            for (int i = 0; i < c; i++) {
                                og.addItem(new Integer(i));
                            }
                        } catch (Exception e) {

                        }
                        og.setValue(0);
                        og.setEnabled(true);
                    }
                });

                Property pi = dataStore.getProperty(field.getId() + "_currentpageindex");
                pi.addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        if (oldValue != null) og.unselect(oldValue);

                        for (Object o : og.getItemIds()) {
                            if (o.equals(newValue)) og.select(o);
                        }

                    }
                });
                og.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                    @Override
                    public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                        if (og.isEnabled()) {
                            System.out.println("LANZAMOS BÚSQUEDA POR CAMBIO DE PÁGINA");
                            MateuUI.runInUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ListView) view).search();
                                }
                            });
                        }
                    }
                });



                ((ListView)view).addListViewListener(new ListViewListener() {
                    @Override
                    public void onReset() {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("paginación = 0");
                                og.setEnabled(false);
                                og.setValue(0);
                                System.out.println("paginación == 0 ok");
                            }
                        });
                    }

                    @Override
                    public void onSearch() {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                og.setEnabled(false);
                                System.out.println("paginación deshabilitada");
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                og.setEnabled(true);
                                System.out.println("paginación habilitada");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                og.setEnabled(true);
                                System.out.println("paginación habilitada");
                            }
                        });
                    }
                });

                cs.add(og);
                */
            }

            cs.add(loqueanadimos);

        } else if (field instanceof Tabs) {
            Tabs tabs = (Tabs) field;

            TabSheet tabsheet = new TabSheet();

            for (Tab t : tabs.getTabs()) {

                VerticalLayout vl = new VerticalLayout();

                TabSheet.Tab tx = tabsheet.addTab(vl, t.getCaption());

                buildBody(vl, t);

            }

            cs.add(tabsheet);

        } else if (field instanceof AutocompleteField) {



            AutocompleteField rf = (AutocompleteField) field;

            List<Pair> valores = new ArrayList<>();

            ComboBox<Pair> og;
            og = new ComboBox<>((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            ListDataProvider<Pair> ldp;
            og.setDataProvider(ldp = new ListDataProvider<>(valores));


            //og.setFilteringMode(FilteringMode.CONTAINS);

            for (Pair p : rf.getValues()) {
                ldp.getItems().add(p);
            }

            if (v != null) {
                og.setSelectedItem((Pair) v);
            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    og.setSelectedItem((Pair) newValue);
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof CalendarField || field instanceof io.mateu.ui.core.client.components.fields.DateField) {

            DateField cb;
            cb = new DateField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            cb.setDateFormat("dd/MM/yyyy");
            if (firstField == null) firstField = cb;

            if (v != null && v instanceof Date) cb.setValue(((Date)v).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            if (v != null && v instanceof LocalDate) cb.setValue((LocalDate) v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    cb.setValue((LocalDate) newValue);
                }
            });
            cb.addValueChangeListener(new HasValue.ValueChangeListener<LocalDate>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<LocalDate> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(cb);

        } else if (field instanceof DateTimeField || field instanceof GMTDateField) {

            com.vaadin.ui.DateTimeField cb;
            cb = new com.vaadin.ui.DateTimeField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            cb.setResolution(DateTimeResolution.MINUTE);
            cb.setDateFormat("dd/MM/yyyy HH:mm");
            if (firstField == null) firstField = cb;

            if (v != null && v instanceof Date) cb.setValue(((Date)v).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            if (v != null && v instanceof LocalDateTime) cb.setValue((LocalDateTime) v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    cb.setValue((LocalDateTime) newValue);
                }
            });
            cb.addValueChangeListener(new HasValue.ValueChangeListener<LocalDateTime>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<LocalDateTime> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(cb);
        } else if (field instanceof CheckBoxField) {

            CheckBoxField rf = (CheckBoxField) field;

            CheckBox cb = new CheckBox("Yes");
            if (firstField == null) firstField = cb;

            cb.setDescription("Yes");


            if (v != null) {
                cb.setValue((Boolean)v);
            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    cb.setValue((newValue != null)?(Boolean) newValue:false);
                }
            });
            cb.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Boolean> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue() != null && valueChangeEvent.getValue());
                }
            });

            HorizontalLayout h = new HorizontalLayout();
            h.setCaption((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            h.addComponent(cb);
            cs.add(h);

        } else if (field instanceof CheckBoxListField) {

            CheckBoxListField rf = (CheckBoxListField) field;

            CheckBoxGroup og;
            og = new CheckBoxGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = og;

            List<Pair> valores = new ArrayList<>();

            ListDataProvider<Pair> ldp;
            og.setDataProvider(ldp = new ListDataProvider<>(valores));


            //og.setFilteringMode(FilteringMode.CONTAINS);

            for (Pair p : rf.getValues()) {
                ldp.getItems().add(p);
            }

            if (v != null) {
                og.select(v);
            }

            Property<PairList> p = dataStore.getPairListProperty(field.getId());
            p.addListener(new ChangeListener<PairList>() {
                @Override
                public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                    og.deselectAll();
                    if (newValue != null) og.select(newValue.getValues());
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Set>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Set> valueChangeEvent) {
                    p.getValue().getValues().clear();
                    p.getValue().getValues().addAll(valueChangeEvent.getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof WeekDaysField) {

            WeekDaysField rf = (WeekDaysField) field;

            String[] labels = {"M", "T", "W", "T", "F", "S", "S"};

            CheckBoxGroup og;
            og = new CheckBoxGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = og;

            //todo: acabar!!!!

            List<Pair> valores = new ArrayList<>();

            for (int i = 0; i < labels.length; i++) {
                valores.add(new Pair("" + i, labels[i]));
            }

            ListDataProvider<Pair> ldp;
            og.setDataProvider(ldp = new ListDataProvider<>(valores));

            if (v != null) {
                boolean[] b = (boolean[]) v;
                for (int i = 0; i < b.length; i++) if (b[i]) og.select(valores.get(i));
            }

            Property<Object> sp = dataStore.getProperty(field.getId());
            Property<PairList> p = dataStore.getPairListProperty(field.getId() + "_lista");
            p.addListener(new ChangeListener<PairList>() {
                @Override
                public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                    og.deselectAll();
                    if (newValue != null) og.select(newValue.getValues());
                    boolean b[] = new boolean[labels.length];
                    for (int i = 0; i < valores.size(); i++) {
                        b[i] = newValue.getValues().contains(valores.get(i));
                    }
                    sp.setValue(b);
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Set>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Set> valueChangeEvent) {
                    p.getValue().getValues().clear();
                    p.getValue().getValues().addAll(valueChangeEvent.getValue());
                    boolean b[] = new boolean[labels.length];
                    for (int i = 0; i < valores.size(); i++) {
                        b[i] = valueChangeEvent.getValue().contains(valores.get(i));
                    }
                    sp.setValue(b);
                }
            });
            sp.addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    boolean b[] = new boolean[labels.length];
                    if (newValue != null) b = (boolean[]) newValue;
                    p.getValue().getValues().clear();
                    for (int i = 0; i < b.length; i++) p.getValue().getValues().add(valores.get(i));
                }
            });

            cs.add(og);



        } else if (field instanceof ComboBoxField) {

            ComboBoxField rf = (ComboBoxField) field;

            List<Pair> valores = new ArrayList<>();

            ComboBox<Pair> og;
            og = new ComboBox<>((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            og.setTextInputAllowed(false);

            ListDataProvider<Pair> ldp;
            og.setDataProvider(ldp = new ListDataProvider<>(valores));


            //og.setFilteringMode(FilteringMode.CONTAINS);

            for (Pair p : rf.getValues()) {
                ldp.getItems().add(p);
            }

            if (v != null) {
                og.setSelectedItem((Pair) v);
            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    og.setSelectedItem((Pair) newValue);
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof DoubleField) {

            TextField intf;
            intf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            if (firstField == null) firstField = (AbstractComponent) intf;
            intf.setValueChangeMode(ValueChangeMode.BLUR);
            intf.addStyleName("camponumerico");

            if (v != null) intf.setValue("" + v);

            Property<Double> p = dataStore.getDoubleProperty(field.getId());
            p.addListener(new ChangeListener<Double>() {
                @Override
                public void changed(ObservableValue observable, Double oldValue, Double newValue) {
                    intf.setValue((newValue != null)?"" + newValue:"");
                }
            });
            intf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {

                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    try {
                        p.setValue((!Strings.isNullOrEmpty(valueChangeEvent.getValue()))?new Double(valueChangeEvent.getValue().replaceAll(",", ".")):null);
                        intf.setComponentError(null);
                    } catch (Exception e) {
                        intf.setComponentError(new UserError("Must be a valid number"));
                    }

                }
            });

            cs.add(intf);

        } else if (field instanceof FileField) {

            Property<FileLocator> p = dataStore.getFileLocatorProperty(field.getId());

            class MyUploader implements Upload.Receiver, Upload.SucceededListener {
                public File file;

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

                    String baseUrl = System.getProperty("tmpurl");
                    URL url = null;
                    try {
                        if (baseUrl == null) {
                            url = file.toURI().toURL();
                        } else url = new URL(baseUrl + "/" + file.getName());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }


                    System.out.println("uploadSucceeded(" + file.getAbsolutePath() + ")");

                    p.setValue(new FileLocator(0, file.getName(), file.toURI().toString(), file.getAbsolutePath()));
                }
            };
            MyUploader receiver = new MyUploader();


            Upload upload = new Upload(null, receiver);
            //upload.setImmediateMode(false);
            upload.addSucceededListener(receiver);

            HorizontalLayout h = new HorizontalLayout();
            h.setSpacing(true);
            Link l;
            h.addComponent(l = new Link());
            h.addComponent(new Button("X", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    p.setValue(null);
                }
            }));
            h.addComponent(upload);
            cs.add(h);

            h.setCaption((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText():null);

            // Open the URL in a new window/tab
            l.setTargetName("_blank");

            p.addListener(new ChangeListener<FileLocator>() {
                @Override
                public void changed(ObservableValue<? extends FileLocator> observable, FileLocator oldValue, FileLocator newValue) {
                    l.setCaption((newValue != null) ? newValue.getFileName() : null);
                    l.setResource((newValue != null) ? new ExternalResource(newValue.getUrl()):null);
                    upload.setComponentError(null);
                }
            });

        } else if (field instanceof IntegerField) {

            TextField intf;
            intf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            if (firstField == null) firstField = (AbstractComponent) intf;
            intf.setValueChangeMode(ValueChangeMode.BLUR);
            intf.addStyleName("camponumerico");

            if (v != null) intf.setValue("" + v);

            Property<Integer> p = dataStore.getIntegerProperty(field.getId());
            p.addListener(new ChangeListener<Integer>() {
                @Override
                public void changed(ObservableValue observable, Integer oldValue, Integer newValue) {
                    intf.setValue((newValue != null)?"" + newValue:"");
                }
            });
            intf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {

                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    try {
                        p.setValue((!Strings.isNullOrEmpty(valueChangeEvent.getValue()))?new Integer(valueChangeEvent.getValue()):null);
                        intf.setComponentError(null);
                    } catch (Exception e) {
                        intf.setComponentError(new UserError("Must be a valid number without decimals"));
                    }

                }
            });

            cs.add(intf);

        } else if (field instanceof LinkField) {

            LinkField lf = (LinkField) field;

            Button cb;
            cb = new Button((lf.getText() != null)?lf.getText():(String) v);

            cb.setStyleName("link");

            cb.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    lf.run();
                }
            });

            cs.add(cb);

        } else if (field instanceof ListSelectionField) {

            ListSelectionField rf = (ListSelectionField) field;

            TwinColSelect tcs ;
            tcs = new TwinColSelect((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            tcs.setLeftColumnCaption("All");
            tcs.setRightColumnCaption("Selected");
            //tcs.setNewItemsAllowed(true);

            ListDataProvider<Pair> ldp;

            tcs.setDataProvider(ldp = new ListDataProvider<>(rf.getValues()));

            if (v != null) {
                for (Pair p : ((PairList)v).getValues()) tcs.select(p);
            }

            Property<PairList> p = dataStore.getPairListProperty(field.getId());
            p.addListener(new ChangeListener<PairList>() {
                @Override
                public void changed(ObservableValue observable, PairList oldValue, PairList newValue) {
                    if (newValue == null || newValue.getValues().size() == 0) tcs.deselectAll();
                    else {
                        List<Pair> nuevos = new ArrayList<>();
                        List<Pair> deseleccionar = new ArrayList<>(tcs.getSelectedItems());
                        for (Pair p : newValue.getValues()) {
                            if (!ldp.getItems().contains(p)) nuevos.add(p);
                            deseleccionar.remove(p);
                        }
                        tcs.deselect(deseleccionar);
                        ldp.getItems().addAll(nuevos);
                        for (Pair p : newValue.getValues()) tcs.select(p);
                    }
                }
            });
            tcs.addValueChangeListener(new HasValue.ValueChangeListener<Set>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Set> valueChangeEvent) {
                    PairList l = new PairList();
                    l.getValues().addAll((Collection<? extends Pair>) tcs.getValue());
                    p.setValue(l);
                }
            });

            cs.add(tcs);

        } else if (field instanceof LongField) {

            TextField intf;
            intf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            if (firstField == null) firstField = (AbstractComponent) intf;
            intf.setValueChangeMode(ValueChangeMode.BLUR);
            intf.addStyleName("camponumerico");

            if (v != null) intf.setValue("" + v);

            Property<Long> p = dataStore.getLongProperty(field.getId());
            p.addListener(new ChangeListener<Long>() {
                @Override
                public void changed(ObservableValue observable, Long oldValue, Long newValue) {
                    intf.setValue((newValue != null)?"" + newValue:"");
                }
            });
            intf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {

                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    try {
                        p.setValue((!Strings.isNullOrEmpty(valueChangeEvent.getValue()))?new Long(valueChangeEvent.getValue()):null);
                        intf.setComponentError(null);
                    } catch (Exception e) {
                        intf.setComponentError(new UserError("Must be a valid number without decimals"));
                    }

                }
            });

            cs.add(intf);

        } else if (field instanceof RadioButtonField) {


            RadioButtonField rf = (RadioButtonField) field;

            RadioButtonGroup<Pair> og =
                    new RadioButtonGroup<>((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (firstField == null) firstField = og;


            og.setItems(rf.getValues());

            if (v != null) {
                og.setSelectedItem((Pair) v);
            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    og.setSelectedItem((Pair) newValue);
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof RichTextField)  {

            RichTextArea rta = new RichTextArea((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            if (firstField == null) firstField = rta;
            rta.addStyleName("l");
            if (v != null) rta.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    rta.setValue((newValue != null)?"" + newValue:null);
                }
            });
            rta.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(rta);

        } else if (field instanceof SelectByIdField) {


            SelectByIdField rf = (SelectByIdField) field;

            if (inToolbar) {

                TextField tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
                tf.addStyleName("l");
                if (firstField == null) firstField = tf;

                tf.setPlaceholder("");

                if (v != null) tf.setValue("" + v);

                Property p = dataStore.getProperty(field.getId());
                p.addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        tf.setValue((newValue != null)?"" + newValue:null);
                    }
                });
                tf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                    @Override
                    public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                        p.setValue(valueChangeEvent.getValue());
                    }
                });
                cs.add(tf);

            } else {

                HorizontalLayout hl = new HorizontalLayout();
                hl.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
                cs.add(hl);

                TextField tf;
                hl.addComponent(tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null));
                if (firstField == null) firstField = tf;

                Property p = dataStore.getProperty(field.getId());

                Button bedit = null;

                hl.addComponent(new Button("New", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        rf.createNew();
                    }
                }));
                hl.addComponent(bedit = new Button("Edit", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        Pair v = (Pair) p.getValue();
                        if (v != null) rf.edit(((Pair) p.getValue()).getValue());
                    }
                }));

                Label l;
                hl.addComponent(l = new Label());

                if (v != null && v instanceof Pair) {
                    tf.setValue("" + ((Pair) v).getValue());
                    l.setValue("" + ((Pair) v).getText());
                }

                Button finalBedit = bedit;
                p.addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        tf.setValue((newValue != null)?"" + ((Pair) newValue).getValue():null);
                        l.setValue((newValue != null)?"" + ((Pair) newValue).getText():null);
                        if (finalBedit != null) finalBedit.setEnabled(newValue != null);
                    }
                });

                tf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                    @Override
                    public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                        rf.call(rf.getQl().replaceAll("xxxx", "" + valueChangeEvent.getValue()), new AsyncCallback<Object[][]>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                p.setValue(null);
                                l.setValue("" + caught.getClass().getName() + ": " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Object[][] result) {
                                if (result.length > 0) {
                                    p.setValue(new Pair(result[0][0], "" + result[0][1]));
                                } else {
                                    p.setValue(null);
                                }
                            }
                        });
                    }
                });

            }

        } else if (field instanceof ShowTextField) {
            Label tf = new Label();
            tf.setCaption((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            tf.addStyleName("l");
            tf.setContentMode(ContentMode.HTML);
            if (v != null) tf.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    String s = (newValue != null)?"" + newValue:null;
                    if (s != null) s = s.replaceAll("\n", "<br/>");
                    tf.setValue(s);
                }
            });

            cs.add(tf);
        } else if (field instanceof HtmlField) {
            Label tf = new Label();
            tf.setCaption((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            tf.addStyleName("l");
            tf.setContentMode(ContentMode.HTML);
            if (v != null) tf.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    String s = (newValue != null)?"" + newValue:null;
                    tf.setValue(s);
                }
            });

            cs.add(tf);
        } else if (field instanceof SqlAutocompleteField) {

            SqlAutocompleteField rf = (SqlAutocompleteField) field;
            List<Pair> valores = new ArrayList<>();

            ComboBox<Pair> og;
            og = new ComboBox<>((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            ListDataProvider<Pair> ldp;
            og.setDataProvider(ldp = new ListDataProvider<>(valores));

            //og.setFilteringMode(FilteringMode.CONTAINS);

            try {
                rf.call(new AsyncCallback<Object[][]>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Object[][] result) {
                        for (Object[] l : result) {
                            ldp.getItems().add(new Pair(l[0], "" + l[1]));
                        }

                        Property p = dataStore.getProperty(field.getId());
                        Object v = p.getValue();
                        for (Pair o : ldp.getItems()) {
                            if (o.equals(v)) og.setSelectedItem(o);
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (v != null) {
                //og.select(v);

                for (Pair o : ldp.getItems()) {
                    if (o.equals(v)) og.setSelectedItem(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    og.setSelectedItem((Pair) newValue);
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof SqlComboBoxField) {



            SqlComboBoxField rf = (SqlComboBoxField) field;

            List<Pair> valores = new ArrayList<>();

            ComboBox<Pair> og;
            og = new ComboBox<>((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            og.setTextInputAllowed(false);

            ListDataProvider<Pair> ldp;
            og.setDataProvider(ldp = new ListDataProvider<>(valores));

            //og.setFilteringMode(FilteringMode.CONTAINS);

            try {
                rf.call(new AsyncCallback<Object[][]>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Object[][] result) {
                        for (Object[] l : result) {
                            ldp.getItems().add(new Pair(l[0], "" + l[1]));
                        }

                        Property p = dataStore.getProperty(field.getId());
                        Object v = p.getValue();
                        for (Pair o : ldp.getItems()) {
                            if (o.equals(v)) og.setSelectedItem(o);
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (v != null) {
                //og.select(v);

                for (Pair o : ldp.getItems()) {
                    if (o.equals(v)) og.setSelectedItem(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    og.setSelectedItem((Pair) newValue);
                }
            });
            og.addValueChangeListener(new HasValue.ValueChangeListener<Pair>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Pair> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof SqlListSelectionField) {


            SqlListSelectionField rf = (SqlListSelectionField) field;

            TwinColSelect tcs ;
            tcs = new TwinColSelect((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            tcs.setLeftColumnCaption("All");
            tcs.setRightColumnCaption("Selected");
            //tcs.setNewItemsAllowed(true);


            ListDataProvider<Pair> ldp = new ListDataProvider<>(new ArrayList<>());

            tcs.setDataProvider(ldp);

            rf.call(new AsyncCallback<Object[][]>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(Object[][] result) {
                    List<Pair> l = new ArrayList<>();
                    for (Object[] r : result) {
                        l.add(new Pair(r[0], (r[1] == null) ? null : "" + r[1]));
                    }

                    ldp.getItems().addAll(l);
                }
            });

            if (v != null) {
                for (Pair p : ((PairList)v).getValues()) tcs.select(p);
            }

            Property<PairList> p = dataStore.getPairListProperty(field.getId());
            p.addListener(new ChangeListener<PairList>() {
                @Override
                public void changed(ObservableValue observable, PairList oldValue, PairList newValue) {
                    if (newValue == null || newValue.getValues().size() == 0) tcs.deselectAll();
                    else {
                        List<Pair> nuevos = new ArrayList<>();
                        List<Pair> deseleccionar = new ArrayList<>(tcs.getSelectedItems());
                        for (Pair p : newValue.getValues()) {
                            if (!ldp.getItems().contains(p)) nuevos.add(p);
                            deseleccionar.remove(p);
                        }
                        tcs.deselect(deseleccionar);
                        ldp.getItems().addAll(nuevos);
                        for (Pair p : newValue.getValues()) tcs.select(p);
                    }
                }
            });
            tcs.addValueChangeListener(new HasValue.ValueChangeListener<Set>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<Set> valueChangeEvent) {
                    PairList l = new PairList();
                    l.getValues().addAll((Collection<? extends Pair>) tcs.getValue());
                    p.setValue(l);
                }
            });

            cs.add(tcs);

        } else if (field instanceof TextAreaField)  {


            TextArea ta = new TextArea((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            ta.addStyleName("l");
            if (firstField == null) firstField = ta;

            ta.setPlaceholder("");

            if (v != null) ta.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ta.setValue((newValue != null)?"" + newValue:null);
                }
            });
            ta.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });

            cs.add(ta);


        } else if (field instanceof io.mateu.ui.core.client.components.fields.TextField) {


            TextField tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            tf.addStyleName("l");
            if (firstField == null) firstField = tf;

            tf.setPlaceholder("");

            if (field.isUnmodifiable()) tf.setEnabled(false);

            if (v != null) tf.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (newValue != null) tf.setValue("" + newValue);
                    else tf.clear();
                }
            });
            tf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });
            cs.add(tf);

        } else if (field instanceof WebField) {
            BrowserFrame b;
            b = new BrowserFrame((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            b.setWidth("600px");
            b.setHeight("400px");

            if (v != null) {
                System.out.println("setting iframe url to " + v);
                b.setSource(new ExternalResource("" + v));
            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println("setting iframe url to " +  newValue);
                    b.setSource((newValue != null)?new ExternalResource("" + newValue):null);
                }
            });
            cs.add(b);
        } else {


            TextField tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            if (firstField == null) firstField = tf;

            if (v != null) tf.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (newValue != null) tf.setValue("" + newValue);
                    else tf.clear();
                }
            });
            tf.addValueChangeListener(new HasValue.ValueChangeListener<String>() {
                @Override
                public void valueChange(HasValue.ValueChangeEvent<String> valueChangeEvent) {
                    p.setValue(valueChangeEvent.getValue());
                }
            });
            cs.add(tf);

        }

        for (Component finalC1: cs) {
            field.addListener(new FieldListener() {
                @Override
                public void visibilityChanged(boolean newValue) {
                    finalC1.setVisible(newValue);
                }

                @Override
                public void enablementChanged(boolean newValue) {
                    finalC1.setEnabled(newValue);
                }
            });

            if (field.isRequired() && finalC1 instanceof com.vaadin.ui.AbstractField) ((com.vaadin.ui.AbstractField)finalC1).setRequiredIndicatorVisible(true);
            if (field.isRequired() && finalC1 instanceof ComboBox) ((com.vaadin.ui.ComboBox)finalC1).setRequiredIndicatorVisible(true);

            if (!Strings.isNullOrEmpty(field.getHelp())) {
                if (finalC1 instanceof com.vaadin.ui.AbstractField) {
                    ((com.vaadin.ui.AbstractField) finalC1).setDescription(field.getHelp());
                }
            }
        }


        return cs;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        System.out.println("entering view " + getClass().getName());
    }

}
