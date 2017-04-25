package io.mateu.ui.vaadin;

import com.google.common.base.Strings;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
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
import org.vaadin.ui.NumberField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.util.*;

import static org.apache.fop.fonts.type1.AdobeStandardEncoding.c;

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
        view.getForm().getData();
        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                dataStore.setData(newData);
                String t = view.getTitle();
                if (view instanceof AbstractEditorView) {
                    Object id = dataStore.get("_id");
                    if (id == null) t = "New " + t;
                    else {
                        String text = dataStore.get("_tostring");
                        if (text == null) text = "" + id;
                        t += " " + text;
                    }
                }
                dataStore.set("_title", t);

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
                dataStore.set(k, v);
            }

            @Override
            public void idsResetted() {
                dataStore.resetIds();
            }
        });
        dataStore.set("_title", view.getTitle());

        setMargin(true);
        addStyleName("content-common");


        if (!(view instanceof AbstractDialog)) {

            String t = view.getTitle();
            if (view instanceof AbstractEditorView) {
                Object id = dataStore.get("_id");
                if (id == null) t = "New " + t;
                else {
                    String text = dataStore.get("_tostring");
                    if (text == null) text = "" + id;
                    t += " " + text;
                }

                dataStore.getProperty("_id").addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                String t = view.getTitle();
                                if (newValue == null) t = "New " + t;
                                else {
                                    String text = dataStore.get("_tostring");
                                    if (text == null) text = "" + newValue;
                                    t += " " + text;
                                }
                                dataStore.getProperty("_title").setValue(t);
                            }
                        });
                    }
                });
            }

            Label h1 = new Label(t);
            h1.addStyleName(ValoTheme.LABEL_H1);

            Property p = dataStore.getProperty("_title");
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    h1.setValue((newValue != null)?"" + newValue:null);
                }
            });
            p.setValue(t);

            HorizontalLayout hl = new HorizontalLayout();
            hl.addComponent(h1);
            HorizontalLayout badges = new HorizontalLayout();
            hl.addComponent(badges);

            ChangeListener<ObservableList<DataStore>> bl;
            Property<ObservableList<DataStore>> pb = dataStore.getObservableListProperty("_badges");
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

            addComponent(hl);

            Label subtitleLabel;
            addComponent(subtitleLabel = new Label());
            dataStore.getProperty("_subtitle").addListener(new ChangeListener() {
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
            add(this, new GridField("_data", ((AbstractListView) view).getColumns(), true).setExpandable(false), false);
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
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {

                if (getUI() != null && win != null) {
                    getUI().addWindow(win);
                    win.center();
                    //win.focus();
                }

            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (getUI() != null && win != null) getUI().removeWindow(win);
            }
        });
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
                public void onSuccess(Data result) {
                    endWaiting();
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
            if (c instanceof GridField) {
                add(layout, (AbstractField) c);
            } else {
                if (row == null || (c instanceof AbstractField && ((AbstractField)c).isBeginingOfLine()) || c instanceof Tabs) {
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

                } else if (c instanceof Tabs) {

                    Tabs tabs = (Tabs) c;

                    TabSheet tabsheet = new TabSheet();
                    row.addComponent(tabsheet);

                    for (Tab t : tabs.getTabs()) {

                        VerticalLayout vl = new VerticalLayout();

                        tabsheet.addTab(vl, t.getCaption());

                        buildBody(vl, t);

                    }


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
            }
            {
                MenuBar.MenuItem item = menubar.addItem("Data", new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem menuItem) {
                        System.out.println(view.getForm().getData());
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

    private List<Component> getVaadinComponent(AbstractField field, boolean inToolbar) {

        List<Component> cs = new ArrayList<>();

        Data data = view.getForm().getData();

        Object v = data.get(field.getId());


        if (field instanceof GridField) {

            Property<ObservableList<DataStore>> p = dataStore.getObservableListProperty(field.getId());

            GridField g = (GridField) field;

            IndexedContainer ds = new IndexedContainer();

            ds.addContainerProperty("_selected", Boolean.class, null);
            for (AbstractColumn col : g.getColumns()) {
                if (col instanceof DataColumn) {
                    ds.addContainerProperty(col.getId(), DataStore.class, null);
                } else if (col instanceof LinkColumn) {
                        ds.addContainerProperty(col.getId(), AbstractAction.class, null);
                } else {
                    ds.addContainerProperty(col.getId(), Object.class, null);
                }
            }
            if (g.isExpandable()) ds.addContainerProperty("_edit", String.class, "Edit");
            ds.addContainerProperty("_dummycol", Boolean.class, null);

            Grid table = new Grid((g.getLabel() != null) ? g.getLabel().getText() : null);

            table.setContainerDataSource(ds);

            table.setSelectionMode(Grid.SelectionMode.MULTI);

            CellStyleGenerator csg = new CellStyleGenerator(g.getColumns());

            if (csg.hasGenerators()) table.setCellStyleGenerator(csg);

            table.setWidth("100%");

            table.addSelectionListener(new SelectionEvent.SelectionListener() {
                @Override
                public void select(SelectionEvent selectionEvent) {
                    for (Object o : selectionEvent.getSelected()) {
                        System.out.println("selected:" + o);
                        ds.getItem(o).getItemProperty("_selected").setValue(true);
                        if (p.getValue() != null) p.getValue().get(ds.indexOfId(o)).set("_selected", true);
                    }
                    for (Object o : selectionEvent.getAdded()) {
                        System.out.println("added:" + o);
                        ds.getItem(o).getItemProperty("_selected").setValue(true);
                        if (p.getValue() != null) p.getValue().get(ds.indexOfId(o)).set("_selected", true);
                    }
                    for (Object o : selectionEvent.getRemoved()) {
                        System.out.println("removed:" + o);
                        ds.getItem(o).getItemProperty("_selected").setValue(false);
                        if (p.getValue() != null && p.getValue().size() > ds.indexOfId(o)) p.getValue().get(ds.indexOfId(o)).set("_selected", false);
                    }

                }
            });

// Define two columns for the built-in container
            if (true) {
                table.getColumn("_selected").setHidden(true);
            }
            for (AbstractColumn col : g.getColumns()) {
                Grid.Column colx;
                if (col instanceof DataColumn) {

                    colx = table.getColumn(col.getId()).setHeaderCaption(col.getLabel()).setConverter(new Converter<String, DataStore>() {

                        @Override
                        public DataStore convertToModel(String s, Class<? extends DataStore> aClass, Locale locale) throws ConversionException {
                            return null;
                        }

                        @Override
                        public String convertToPresentation(DataStore o, Class<? extends String> aClass, Locale locale) throws ConversionException {
                            return (o != null)?o.get("_text"):null;
                        }

                        @Override
                        public Class<DataStore> getModelType() {
                            return DataStore.class;
                        }

                        @Override
                        public Class<String> getPresentationType() {
                            return String.class;
                        }
                    }).setRenderer(new ButtonRenderer(new ClickableRenderer.RendererClickListener() {
                        @Override
                        public void click(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                            System.out.println("" + rendererClickEvent.getItemId());
                            Object v = ds.getItem(rendererClickEvent.getItemId()).getItemProperty(rendererClickEvent.getPropertyId()).getValue();
                            System.out.println("v=" + v);
                            if (v != null) System.out.println("v.class=" + v.getClass().getName());
                            List<DataStore> l = (List<DataStore>) p.getValue();
                            ((DataColumn)col).run(l.get(((Integer)rendererClickEvent.getItemId()).intValue()).getData());
                            //((DataColumn)col).run((Data) v);
                            //((LinkColumn) col).run(rendererClickEvent.);
                        }


                    }));
                } else if (col instanceof LinkColumn) {

                    colx = table.getColumn(col.getId()).setHeaderCaption(col.getLabel()).setConverter(new Converter<String, AbstractAction>() {

                        @Override
                        public AbstractAction convertToModel(String s, Class<? extends AbstractAction> aClass, Locale locale) throws ConversionException {
                            return null;
                        }

                        @Override
                        public String convertToPresentation(AbstractAction o, Class<? extends String> aClass, Locale locale) throws ConversionException {
                            return o.getName();
                        }

                        @Override
                        public Class<AbstractAction> getModelType() {
                            return AbstractAction.class;
                        }

                        @Override
                        public Class<String> getPresentationType() {
                            return String.class;
                        }
                    }).setRenderer(new ButtonRenderer(new ClickableRenderer.RendererClickListener() {
                        @Override
                        public void click(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                            System.out.println("" + rendererClickEvent.getItemId());
                            ((AbstractAction)ds.getItem(rendererClickEvent.getItemId()).getItemProperty(rendererClickEvent.getPropertyId()).getValue()).run();
                            //((LinkColumn) col).run(rendererClickEvent.);
                        }
                    }));
                } else {
                    colx = table.getColumn(col.getId()).setHeaderCaption(col.getLabel());
                }
                colx.setWidth(col.getWidth());
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
            }
            if (g.isExpandable()) {
                Grid.Column colx;

                    colx = table.getColumn("_edit").setHeaderCaption("Edit").setRenderer(new ButtonRenderer(new ClickableRenderer.RendererClickListener() {
                        @Override
                        public void click(ClickableRenderer.RendererClickEvent rendererClickEvent) {
                            System.out.println("" + rendererClickEvent.getItemId());
                            AbstractForm f = g.getDataForm();

                            if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
                            else {
                                MateuUI.openView(new AbstractDialog() {

                                    @Override
                                    public Data initializeData() {
                                        return p.getValue().get((Integer) rendererClickEvent.getItemId()).getData();
                                    }

                                    @Override
                                    public void onOk(Data data) {
                                        DataStore ds = new DataStore(data);
                                        p.getValue().set((Integer) rendererClickEvent.getItemId(), ds);
                                    }

                                    @Override
                                    public String getTitle() {
                                        return "Add new record";
                                    }

                                    @Override
                                    public AbstractForm createForm() {
                                        return g.getDataForm();
                                    }
                                });
                            }
                        }
                    }));
                colx.setWidth(60);
            }
            table.getColumn("_dummycol").setWidthUndefined().setHeaderCaption("");

            p.getValue().addListener(new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {

                    System.out.println(field.getId() + " changed!!!");

                    table.getSelectionModel().reset();

                    List<DataStore> l = (List<DataStore>) p.getValue(); //c.getList();
                    List ll = new ArrayList();
                    for (Object o : l) {
                        if (o instanceof DataStore) {
                            ll.add(((DataStore) o).getData());
                        } else ll.add(o);
                    }

                    data.set(field.getId(), ll); //c.getList());

                    System.out.println("recovered " + l.size() + " rows");

                    ds.removeAllItems();
                    int pos = 0;
                    for (DataStore x : l) {
                        Item cells = ds.addItem(pos++);
                        cells.getItemProperty("_selected").setValue(x.get("_selected"));
                        for (AbstractColumn col : g.getColumns()) {
                            if (col instanceof DataColumn) {
                                Button b = new Button("" + ((DataStore)x.get(col.getId())).get("_text"));
                                b.setStyleName("link");
                                b.addClickListener(new Button.ClickListener() {
                                    public void buttonClick(Button.ClickEvent event) {
                                        System.out.println(x.getData().toString());
                                        ((LinkColumn) col).run(x.getData());
                                    }
                                });
                                cells.getItemProperty(col.getId()).setValue(x.get(col.getId()));
                            } else if (col instanceof LinkColumn) {
                                Button b = new Button("" + x.get(col.getId()));
                                b.setStyleName("link");
                                b.addClickListener(new Button.ClickListener() {
                                    public void buttonClick(Button.ClickEvent event) {
                                        System.out.println(x.getData().toString());
                                        ((LinkColumn) col).run(x.getData());
                                    }
                                });
                                cells.getItemProperty(col.getId()).setValue(new AbstractAction("" + x.get(col.getId())) {
                                    @Override
                                    public void run() {
                                        ((LinkColumn) col).run(x.getData());
                                    }
                                });
                            } else {
                                cells.getItemProperty(col.getId()).setValue(x.get(col.getId()));
                            }
                        }
                    }
                }
            });

            //table.setPageLength(10);

            cs.add(table);


            if (g.isExpandable()) {
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
                                    p.getValue().add(ds);
                                }

                                @Override
                                public String getTitle() {
                                    return "Add new record";
                                }

                                @Override
                                public AbstractForm createForm() {
                                    return g.getDataForm();
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
                        List<DataStore> sel = new ArrayList<>();
                        for (DataStore d : p.getValue()) {
                            if (d.getBooleanProperty("_selected").getValue()) sel.add(d);
                        }
                        p.getValue().removeAll(sel);
                    }
                });

                cs.add(h);
            }

            if (g.isPaginated()) {
                ComboBox og;
                og = new ComboBox("Page");
                og.setTextInputAllowed(false);
                og.setNullSelectionAllowed(false);
                og.addItem(0);
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
            }

        } else if (field instanceof AutocompleteField) {

            AutocompleteField rf = (AutocompleteField) field;

            ComboBox og;
            og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = (AbstractComponent) og;

            og.setFilteringMode(FilteringMode.CONTAINS);

            for (Pair p : rf.getValues()) {
                og.addItem(p);
            }

            if (v != null) {
                //og.select(v);

                for (Object o : og.getItemIds()) {
                    //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                    if (o.equals(v)) og.select(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
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
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof CalendarField || field instanceof io.mateu.ui.core.client.components.fields.DateField) {

            DateField cb;
            cb = new DateField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = cb;

            if (v != null && v instanceof Date) cb.setValue((Date)v);
            if (v != null && v instanceof LocalDate) cb.setValue(Date.from(((LocalDate)v).atStartOfDay(ZoneId.systemDefault()).toInstant()));

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    cb.setValue((newValue == null)?null:Date.from(((LocalDate)newValue).atStartOfDay(ZoneId.systemDefault()).toInstant()));
                }
            });
            cb.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue((valueChangeEvent.getProperty().getValue() == null)?null:Instant.ofEpochMilli(((Date)valueChangeEvent.getProperty().getValue()).getTime()).atZone(ZoneId.systemDefault()).toLocalDate());
                }
            });

            cs.add(cb);

        } else if (field instanceof DateTimeField) {

            DateField cb;
            cb = new DateField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            cb.setResolution(Resolution.MINUTE);
            if (firstField == null) firstField = cb;

            System.out.println("v=" + v);

            if (v != null && v instanceof Date) cb.setValue((Date)v);
            if (v != null && v instanceof LocalDateTime) cb.setValue(Date.from(((LocalDateTime)v).atZone(ZoneId.systemDefault()).toInstant()));

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println("changed to " + newValue);
                    cb.setValue((newValue == null)?null:Date.from(((LocalDateTime)newValue).atZone(ZoneId.systemDefault()).toInstant()));
                }
            });
            cb.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue((valueChangeEvent.getProperty().getValue() == null)?null:Instant.ofEpochMilli(((Date)valueChangeEvent.getProperty().getValue()).getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
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
                    cb.setValue((Boolean) newValue);
                }
            });
            cb.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue() != null && (Boolean) valueChangeEvent.getProperty().getValue());
                }
            });

            HorizontalLayout h = new HorizontalLayout();
            h.setCaption((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            h.addComponent(cb);
            cs.add(h);

        } else if (field instanceof CheckBoxListField) {

            CheckBoxListField rf = (CheckBoxListField) field;

            OptionGroup og;
            og = new OptionGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = og;

            og.setMultiSelect(true);

            for (Pair p : rf.getValues()) {
                og.addItem(p);
            }

            if (v != null) {
                //og.select(v);

                for (Object o : og.getItemIds()) {
                    //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                    if (o.equals(v)) og.select(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    //og.select(newValue);

                    for (Object o : og.getItemIds()) {
                        if (o.equals(newValue)) og.select(o);
                    }

                }
            });
            og.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof ComboBoxField) {

            ComboBoxField rf = (ComboBoxField) field;

            ComboBox og;
            og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            og.setFilteringMode(FilteringMode.CONTAINS);

            for (Pair p : rf.getValues()) {
                og.addItem(p);
            }

            if (v != null) {
                //og.select(v);

                for (Object o : og.getItemIds()) {
                    System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                    if (o.equals(v)) og.select(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
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
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof DoubleField) {
            NumberField intf;
            intf = new NumberField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            if (firstField == null) firstField = (AbstractComponent) intf;

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println((newValue != null) ? newValue.getClass().getName() : "null");
                    intf.setValue((newValue != null) ? Double.parseDouble("" + newValue) : null);
                }
            });
            intf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    System.out.println((valueChangeEvent.getProperty().getValue() != null) ? valueChangeEvent.getProperty().getValue().getClass().getName() : "null");
                    p.setValue(valueChangeEvent.getProperty().getValue());
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

            org.vaadin.viritin.fields.IntegerField intf;
            intf = new org.vaadin.viritin.fields.IntegerField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = intf;

            if (v != null) intf.setValue((Integer) v);


            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    intf.setValue((newValue != null)?(Integer) newValue:null);
                }
            });
            intf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    System.out.println((valueChangeEvent.getProperty().getValue() != null)?valueChangeEvent.getProperty().getValue().getClass().getName():"null");
                    p.setValue(valueChangeEvent.getProperty().getValue());
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

            for (Pair p : rf.getValues()) {
                tcs.addItem(p);
            }

            if (v != null) {
                //og.select(v);
                for (Pair p : ((PairList)v).getValues()) {
                    tcs.select(p);
                }
            }

            Property<PairList> p = dataStore.getPairListProperty(field.getId());
            p.addListener(new ChangeListener<PairList>() {
                @Override
                public void changed(ObservableValue observable, PairList oldValue, PairList newValue) {
                    if (newValue == null) for (Object x : tcs.getItemIds()) tcs.unselect(x);
                    else {
                        for (Object o : newValue.getValues()) tcs.select(o);
                        for (Object x : tcs.getItemIds()) if (!newValue.getValues().contains(x)) tcs.unselect(x);
                    }
                }
            });
            tcs.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    PairList l = new PairList();
                    l.getValues().addAll((Collection<? extends Pair>) tcs.getValue());
                    p.setValue(l);
                }
            });

            cs.add(tcs);

        } else if (field instanceof LongField) {

            org.vaadin.viritin.fields.IntegerField intf;
            intf = new org.vaadin.viritin.fields.IntegerField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = intf;

            if (v != null) intf.setValue(((Long) v).intValue());


            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    intf.setValue((newValue != null)?((Long) newValue).intValue():null);
                }
            });
            intf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    System.out.println((valueChangeEvent.getProperty().getValue() != null)?valueChangeEvent.getProperty().getValue().getClass().getName():"null");
                    if (valueChangeEvent.getProperty().getValue() != null) p.setValue(new Long((Integer) valueChangeEvent.getProperty().getValue()));
                    else p.setValue(null);
                }
            });

            cs.add(intf);

        } else if (field instanceof RadioButtonField) {

            RadioButtonField rf = (RadioButtonField) field;

            OptionGroup og;
            og = new OptionGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            if (firstField == null) firstField = og;

            for (Pair p : rf.getValues()) {
                og.addItem(p);
            }

            if (v != null) {
                //og.select(v);

                for (Object o : og.getItemIds()) {
                    //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                    if (o.equals(v)) og.select(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    //og.select(newValue);

                    for (Object o : og.getItemIds()) {
                        if (o.equals(newValue)) og.select(o);
                    }

                }
            });
            og.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
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
            rta.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

            cs.add(rta);

        } else if (field instanceof SelectByIdField) {
            SelectByIdField rf = (SelectByIdField) field;

            if (inToolbar) {

                TextField tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
                tf.addStyleName("l");
                if (firstField == null) firstField = tf;

                tf.setNullRepresentation("");

                if (v != null) tf.setValue("" + v);

                Property p = dataStore.getProperty(field.getId());
                p.addListener(new ChangeListener() {
                    @Override
                    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                        tf.setValue((newValue != null)?"" + newValue:null);
                    }
                });
                tf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                    @Override
                    public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                        p.setValue(valueChangeEvent.getProperty().getValue());
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

                tf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                    @Override
                    public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                        rf.call(rf.getQl().replaceAll("xxxx", "" + valueChangeEvent.getProperty().getValue()), new AsyncCallback<Object[][]>() {
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
            TextField tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            tf.addStyleName("l");
            tf.setEnabled(false);
            if (v != null) tf.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    tf.setValue((newValue != null)?"" + newValue:null);
                }
            });

            cs.add(tf);
        } else if (field instanceof SqlAutocompleteField) {

            SqlAutocompleteField rf = (SqlAutocompleteField) field;

            ComboBox og;
            og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            og.setFilteringMode(FilteringMode.CONTAINS);

            try {
                rf.call(new AsyncCallback<Object[][]>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Object[][] result) {
                        for (Object[] l : result) {
                            og.addItem(new Pair(l[0], "" + l[1]));
                        }

                        Property p = dataStore.getProperty(field.getId());
                        Object v = p.getValue();
                        for (Object o : og.getItemIds()) {
                            //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                            if (o.equals(v)) og.select(o);
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (v != null) {
                //og.select(v);

                for (Object o : og.getItemIds()) {
                    //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                    if (o.equals(v)) og.select(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (oldValue != null) og.unselect(oldValue);

                    for (Object o : og.getItemIds()) {
                        if (o.equals(newValue)) {
                            og.select(o);
                            break;
                        }
                    }

                }
            });
            og.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

            cs.add(og);

        } else if (field instanceof SqlComboBoxField) {

            SqlComboBoxField rf = (SqlComboBoxField) field;

            ComboBox og;
            og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            og.addStyleName("l");
            if (firstField == null) firstField = og;

            og.setFilteringMode(FilteringMode.CONTAINS);

            try {
                rf.call(new AsyncCallback<Object[][]>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Object[][] result) {
                        for (Object[] l : result) {
                            og.addItem(new Pair(l[0], "" + l[1]));
                        }

                        Property p = dataStore.getProperty(field.getId());
                        Object v = p.getValue();
                        for (Object o : og.getItemIds()) {
                            //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                            if (o.equals(v)) og.select(o);
                        }

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (v != null) {
                //og.select(v);

                for (Object o : og.getItemIds()) {
                    //System.out.println("o=" + o.getClass().getName() + ":" + o + ", v=" + v.getClass().getName() + ":" + v + ", equals()=" + o.equals(v));
                    if (o.equals(v)) og.select(o);
                }

            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    if (oldValue != null) og.unselect(oldValue);

                    for (Object o : og.getItemIds()) {
                        if (o.equals(newValue)) {
                            og.select(o);
                            break;
                        }
                    }

                }
            });
            og.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
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

                    for (Pair p : l) {
                        tcs.addItem(p);
                    }
                }
            });

            if (v != null) {
                //og.select(v);
                for (Pair p : ((List<Pair>)v)) {
                    tcs.select(p);
                }
            }

            Property<PairList> p = dataStore.getPairListProperty(field.getId());
            p.addListener(new ChangeListener<PairList>() {
                @Override
                public void changed(ObservableValue observable, PairList oldValue, PairList newValue) {
                    if (newValue == null) for (Object x : tcs.getItemIds()) tcs.unselect(x);
                    else {
                        for (Object o : newValue.getValues()) tcs.select(o);
                        for (Object x : tcs.getItemIds()) if (!newValue.getValues().contains(x)) tcs.unselect(x);
                    }
                }
            });
            tcs.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
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

            ta.setNullRepresentation("");

            if (v != null) ta.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ta.setValue((newValue != null)?"" + newValue:null);
                }
            });
            ta.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

            cs.add(ta);

        } else if (field instanceof io.mateu.ui.core.client.components.fields.TextField) {
            TextField tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null);
            tf.addStyleName("l");
            if (firstField == null) firstField = tf;

            tf.setNullRepresentation("");

            if (v != null) tf.setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    tf.setValue((newValue != null)?"" + newValue:null);
                }
            });
            tf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
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
                    tf.setValue((newValue != null)?"" + newValue:null);
                }
            });
            tf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
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

            if (field.isRequired() && finalC1 instanceof com.vaadin.ui.AbstractField) ((com.vaadin.ui.AbstractField)finalC1).setRequired(true);

            if (!Strings.isNullOrEmpty(field.getHelp())) {
                if (finalC1 instanceof com.vaadin.ui.AbstractField) {
                    ((com.vaadin.ui.AbstractField) finalC1).setDescription(field.getHelp());
                }
            }
        }


        return cs;
    }

    private Data getData(Item item) {
        Data d = new Data();
        for (Object id : item.getItemPropertyIds()) {
            Object v = item.getItemProperty(id);
            d.set("" + id, v);
        }
        return d;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        System.out.println("entering view " + getClass().getName());
    }

}
