package io.mateu.ui.vaadin;

import com.vaadin.client.widget.grid.datasources.ListDataSource;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.DateToLongConverter;
import com.vaadin.event.SelectionEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.TextRenderer;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.core.shared.PairList;
import io.mateu.ui.vaadin.data.DataStore;
import io.mateu.ui.vaadin.data.ViewNodeDataStore;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.vaadin.grid.cellrenderers.editoraware.CheckboxRenderer;
import org.vaadin.ui.NumberField;

import java.time.*;
import java.util.*;

import static com.vaadin.ui.components.calendar.ContainerEventProvider.CAPTION_PROPERTY;

/**
 * Created by miguel on 4/1/17.
 */
public class ViewLayout extends VerticalLayout implements View {

    private DataStore dataStore;

    Window win = new Window("Waiting...");

    private final AbstractView view;

    public AbstractView getView() {
        return view;
    }

    public ViewLayout(AbstractView view) {
        this.view = view;
        this.dataStore = new ViewNodeDataStore(this);

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
            }

            @Override
            public void setted(String k, Object v) {
                dataStore.set(k, v);
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
        }


//        setContent(new MVerticalLayout(new MHorizontalLayout(c, d).withFullWidth())
//                .expand(new MHorizontalLayout(menu).expand(mainContent)));


        //setMargin(false);
        //setWidth("800px");
        //addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

        buildToolBar();

        if (view instanceof AbstractListView) {
            add(this, new GridField("_data", ((AbstractListView) view).getColumns()), false);
        } else {
            buildBody();
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

    }

    public void startWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {

                if (getUI() != null && win != null) {
                    getUI().addWindow(win);
                    win.center();
                    win.focus();
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
                public void onSuccess() {
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

    private void buildBody() {

        HorizontalLayout row = null;

        /*
        FormLayout row = new FormLayout();
        row.setWidth("100%");
        row.setSpacing(true);
        addComponent(row);
        */

        int posField = 0;
        for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
            if (c instanceof AbstractField) {

                if (row == null || ((AbstractField)c).isBeginingOfLine()) {
                    row = new HorizontalLayout();
                    row.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
                    row.setSpacing(true);
                    addComponent(row);
                }

                add(row, (AbstractField) c);

                posField++;
            }
        }
    }

    private void buildToolBar() {

        HorizontalLayout h = new HorizontalLayout();

        h.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        FormLayout f = new FormLayout();
        //h.setSpacing(true);
        //h.setMargin(true);
        //h.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        h.setMargin(new MarginInfo(false, false, true, false));
        h.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        f.setMargin(false);
        f.setSpacing(false);

        h.setSpacing(true);

        //h.addComponent(f);

        if (view instanceof AbstractListView) {

            int posField = 0;
            Component last = null;
            for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
                if (c instanceof AbstractField) {

                    last = add(h, (AbstractField) c, true, true);

                    posField++;
                    if (posField >= ((AbstractListView) view).getMaxFieldsInHeader()) break;
                }
            }
            if (last != null) {
//                f.setMargin(new MarginInfo(false, true, false, false));
            }
        }

        if (view.getActions().size() > 0) {
            MenuBar menubar = new MenuBar();
            for (AbstractAction a : view.getActions()) {
                menubar.addItem(a.getName(), new MenuBar.Command() {
                    @Override
                    public void menuSelected(MenuBar.MenuItem menuItem) {
                        a.run();
                    }
                });
            }
            h.addComponent(menubar);
        }

        if (false) for (AbstractAction a : view.getActions()) {
            h.addComponent(new Button(a.getName(), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    a.run();
                }
            }));
        }

        if (false) {
            h.addComponent(new Button("DataStore", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    System.out.println(dataStore.toString());
                }
            }));

            h.addComponent(new Button("Data", new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    System.out.println(view.getForm().getData().toString());
                }
            }));
        }

        addComponent(h);
    }

    private void add(Layout where, AbstractField c) {
        add(where, c, true);
    }

    private Component add(Layout where, AbstractField c, boolean paintLabel) {
        return add(where, c, paintLabel, false);
    }
    private Component add(Layout where, AbstractField c, boolean paintLabel, boolean inToolbar) {
        Component x = getVaadinComponent(c);
        where.addComponent(x);
        if (inToolbar) x.addStyleName("inline");
        return x;
    }

    private Component getVaadinComponent(AbstractField field) {
        Component c = null;

        Data data = view.getForm().getData();

        Object v = data.get(field.getId());


        if (field instanceof GridField) {

            Property<ObservableList<DataStore>> p = dataStore.getObservableListProperty(field.getId());

            GridField g = (GridField) field;

            IndexedContainer ds = new IndexedContainer();

            ds.addContainerProperty("_selected", Boolean.class, null);
            for (AbstractColumn col : g.getColumns()) {
                if (col instanceof LinkColumn) {
                    ds.addContainerProperty(col.getId(), AbstractAction.class, null);
                } else {
                    ds.addContainerProperty(col.getId(), Object.class, null);
                }
            }
            ds.addContainerProperty("_dummycol", Boolean.class, null);

            Grid table = new Grid((g.getLabel() != null) ? g.getLabel().getText() : null);

            table.setContainerDataSource(ds);

            table.setSelectionMode(Grid.SelectionMode.MULTI);

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
                if (col instanceof LinkColumn) {

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
            table.getColumn("_dummycol").setWidthUndefined().setHeaderCaption("");


            Component finalC = c;
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
                        if (false) {
                            CheckBox b = new CheckBox();
                            b.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                                @Override
                                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                                    x.set("_selected", valueChangeEvent.getProperty().getValue());
                                }
                            });
                            cells.getItemProperty("_selected").setValue(b);
                        } else {
                            cells.getItemProperty("_selected").setValue(x.get("_selected"));
                        }
                        for (AbstractColumn col : g.getColumns()) {
                            if (col instanceof LinkColumn) {
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

            c = (Component) table;
        } else if (field instanceof AutocompleteField) {

            AutocompleteField rf = (AutocompleteField) field;

            ComboBox og;
            c = og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

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



        } else if (field instanceof CalendarField || field instanceof io.mateu.ui.core.client.components.fields.DateField) {

            DateField cb;
            c = cb = new DateField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (v != null) cb.setValue((Date) v);

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

        } else if (field instanceof DateTimeField) {

            DateField cb;
            c = cb = new DateField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            cb.setResolution(Resolution.MINUTE);

            System.out.println("v=" + v);

            if (v != null) cb.setValue((Date) v);

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

        } else if (field instanceof CheckBoxField) {

            CheckBoxField rf = (CheckBoxField) field;

            OptionGroup og;
            c = og = new OptionGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            og.setMultiSelect(true);

            og.addItem("Yes");


            if (v != null) {
                for (Object o : og.getItemIds()) {
                    if (v instanceof Boolean && ((Boolean)v)) og.select(o); else og.unselect(o);
                }
            }

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    //og.select(newValue);

                    for (Object o : og.getItemIds()) {
                        if (newValue instanceof Boolean && ((Boolean)newValue)) og.select(o); else og.unselect(o);
                    }

                }
            });
            og.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue() != null);
                }
            });

        } else if (field instanceof CheckBoxListField) {

            CheckBoxListField rf = (CheckBoxListField) field;

            OptionGroup og;
            c = og = new OptionGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

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

        } else if (field instanceof ComboBoxField) {

            ComboBoxField rf = (ComboBoxField) field;

            ComboBox og;
            c = og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

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


        } else if (field instanceof DoubleField) {
            NumberField intf;
            c = intf = new NumberField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println((newValue != null)?newValue.getClass().getName():"null");
                    intf.setValue((newValue != null)?Double.parseDouble("" + newValue):null);
                }
            });
            intf.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    System.out.println((valueChangeEvent.getProperty().getValue() != null)?valueChangeEvent.getProperty().getValue().getClass().getName():"null");
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

        } else if (field instanceof IntegerField) {

            org.vaadin.viritin.fields.IntegerField intf;
            c = intf = new org.vaadin.viritin.fields.IntegerField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (v != null) intf.setValue((Integer) v);


            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
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


        } else if (field instanceof LinkField) {

            LinkField lf = (LinkField) field;

            Button cb;
            c = cb = new Button((lf.getText() != null)?lf.getText():(String) v);

            cb.setStyleName("link");

            cb.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    lf.run();
                }
            });


        } else if (field instanceof ListSelectionField) {

            ListSelectionField rf = (ListSelectionField) field;

            TwinColSelect tcs ;
            c = tcs = new TwinColSelect((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
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

        } else if (field instanceof RadioButtonField) {

            RadioButtonField rf = (RadioButtonField) field;

            OptionGroup og;
            c = og = new OptionGroup((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

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

        } else if (field instanceof RichTextField)  {
            c = new RichTextArea((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (v != null) ((RichTextArea) c).setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ((RichTextArea) finalC).setValue((newValue != null)?"" + newValue:null);
                }
            });
            ((RichTextArea)c).addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

        } else if (field instanceof SelectByIdField) {
            SelectByIdField rf = (SelectByIdField) field;


            HorizontalLayout hl = new HorizontalLayout();
            hl.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
            c = hl;

            TextField tf;
            hl.addComponent(tf = new TextField((field.getLabel() != null && field.getLabel().getText() != null) ? field.getLabel().getText() : null));

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

        } else if (field instanceof ShowTextField) {
            c = new TextField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            ((TextField)c).setEnabled(false);
            if (v != null) ((TextField) c).setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ((TextField) finalC).setValue((newValue != null)?"" + newValue:null);
                }
            });
        } else if (field instanceof SqlComboBoxField) {

            SqlComboBoxField rf = (SqlComboBoxField) field;

            ComboBox og;
            c = og = new ComboBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

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

        } else if (field instanceof SqlListSelectionField) {

            SqlListSelectionField rf = (SqlListSelectionField) field;

            TwinColSelect tcs ;
            c = tcs = new TwinColSelect((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
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


        } else if (field instanceof TextAreaField)  {
            c = new TextArea((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            ((TextArea) c).setNullRepresentation("");

            if (v != null) ((TextArea) c).setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ((TextArea) finalC).setValue((newValue != null)?"" + newValue:null);
                }
            });
            ((TextArea)c).addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

        } else if (field instanceof io.mateu.ui.core.client.components.fields.TextField) {
            c = new TextField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            ((TextField) c).setNullRepresentation("");

            if (v != null) ((TextField) c).setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ((TextField) finalC).setValue((newValue != null)?"" + newValue:null);
                }
            });
            ((TextField)c).addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });
        } else if (field instanceof WebField) {
            BrowserFrame b;
            c = b = new BrowserFrame((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);
            b.setWidth("600px");
            b.setHeight("400px");

            if (v != null) {
                System.out.println("setting iframe url to " + v);
                b.setSource(new ExternalResource("" + v));
            }

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println("setting iframe url to " +  newValue);
                    ((BrowserFrame) finalC).setSource((newValue != null)?new ExternalResource("" + newValue):null);
                }
            });
        } else {
            c = new TextField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (v != null) ((TextField) c).setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ((TextField) finalC).setValue((newValue != null)?"" + newValue:null);
                }
            });
            ((TextField)c).addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });
        }

        Component finalC1 = c;
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

        if (field.isRequired() && c instanceof com.vaadin.ui.AbstractField) ((com.vaadin.ui.AbstractField)c).setRequired(true);

        return c;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        System.out.println("entering view " + getClass().getName());
    }
}
