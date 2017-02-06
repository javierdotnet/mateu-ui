package io.mateu.ui.vaadin;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.components.fields.IntegerField;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.AsyncCallback;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.vaadin.data.DataStore;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.vaadin.ui.NumberField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 4/1/17.
 */
public class ViewLayout extends VerticalLayout implements View {

    private DataStore dataStore;

    Window win = new Window("Waiting...");

    private final AbstractView view;

    public ViewLayout(AbstractView view) {
        this.view = view;
        this.dataStore = new DataStore(view.getForm().getData());

        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                dataStore.setData(newData);
                String t = view.getTitle();
                if (view instanceof AbstractEditorView) {
                    Object id = dataStore.get("_id");
                    if (id == null) t = "New " + t;
                    else t += " " + id;
                }
                dataStore.set("_title", t);
            }
        });
        dataStore.set("_title", view.getTitle());

        setMargin(true);
        addStyleName("content-common");

        String t = view.getTitle();
        if (view instanceof AbstractEditorView) {
            Object id = dataStore.get("_id");
            if (id == null) t = "New " + t;
            else t += " " + id;
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

        addComponent(h1);


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

                getUI().addWindow(win);
                win.center();
                win.focus();

            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                getUI().removeWindow(win);
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
        FormLayout row = new FormLayout();
        row.setWidth("100%");
        row.setSpacing(true);
        addComponent(row);

        int posField = 0;
        for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
            if (c instanceof AbstractField) {

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
        f.setMargin(false);
        f.setSpacing(false);

        h.setSpacing(true);

        h.addComponent(f);

        if (view instanceof AbstractListView) {

            int posField = 0;
            Component last = null;
            for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
                if (c instanceof AbstractField) {

                    last = add(f, (AbstractField) c, true, true);

                    posField++;
                    if (posField >= ((AbstractListView) view).getMaxFieldsInHeader()) break;
                }
            }
            if (last != null) {
//                f.setMargin(new MarginInfo(false, true, false, false));
            }
        }

        for (AbstractAction a : view.getActions()) {
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

            GridField g = (GridField) field;

            Table table = new Table((g.getLabel() != null) ? g.getLabel().getText() : null);

// Define two columns for the built-in container
            {
                table.addContainerProperty("_selected", CheckBox.class, null, "Sel.", null, Table.Align.CENTER);
            }
            for (AbstractColumn col : g.getColumns()) {
                if (col instanceof LinkColumn) {
                    table.addContainerProperty(col.getId(), Button.class, null, col.getLabel(), null, null);
                } else {
                    table.addContainerProperty(col.getId(), Object.class, null, col.getLabel(), null, null);
                }
                table.setColumnWidth(col.getId(), col.getWidth());
            }

            Property<ObservableList<DataStore>> p = dataStore.getObservableListProperty(field.getId());
            Component finalC = c;
            p.getValue().addListener(new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {

                    System.out.println(field.getId() + " changed!!!");

                    List<DataStore> l = (List<DataStore>) p.getValue(); //c.getList();
                    List ll = new ArrayList();
                    for (Object o : l) {
                        if (o instanceof DataStore) {
                            ll.add(((DataStore) o).getData());
                        } else ll.add(o);
                    }

                    data.set(field.getId(), ll); //c.getList());

                    System.out.println("recovered " + l.size() + " rows");

                    table.removeAllItems();
                    int pos = 0;
                    for (DataStore x : l) {
                        List<Object> cells = new ArrayList();
                        {
                            CheckBox b = new CheckBox();
                            b.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                                @Override
                                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                                    x.set("_selected", valueChangeEvent.getProperty().getValue());
                                }
                            });
                            cells.add(b);
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
                                cells.add(b);
                            } else {
                                cells.add(x.get(col.getId()));
                            }
                        }
                        table.addItem(cells.toArray(), pos++);
                    }
                }
            });

            table.setPageLength(10);

            c = table;
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



        } else if (field instanceof CalendarField || field instanceof io.mateu.ui.core.client.components.fields.DateField) {

            DateField cb;
            c = cb = new DateField((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (v != null) cb.setValue((Date) v);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    cb.setValue((Date) newValue);
                }
            });
            cb.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
                }
            });

        } else if (field instanceof CheckBoxField) {

            CheckBox cb;
            c = cb = new CheckBox((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null, (v != null && v instanceof Boolean && ((Boolean) v)) ? true : false);

            Property p = dataStore.getProperty(field.getId());
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    cb.setValue((newValue != null && newValue instanceof Boolean && ((Boolean) newValue)) ? true : false);
                }
            });
            cb.addValueChangeListener(new com.vaadin.data.Property.ValueChangeListener() {
                @Override
                public void valueChange(com.vaadin.data.Property.ValueChangeEvent valueChangeEvent) {
                    p.setValue(valueChangeEvent.getProperty().getValue());
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

        } else if (field instanceof ShowTextField) {
            c = new Label((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

            if (v != null) ((Label) c).setValue("" + v);

            Property p = dataStore.getProperty(field.getId());
            Component finalC = c;
            p.addListener(new ChangeListener() {
                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    ((Label) finalC).setValue((newValue != null)?"" + newValue:null);
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

        } else if (field instanceof TextAreaField)  {
            c = new TextArea((field.getLabel() != null && field.getLabel().getText() != null)?field.getLabel().getText():null);

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
