package io.mateu.ui.vaadin;

import com.vaadin.data.Item;
import com.vaadin.ui.*;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractExecutable;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.vaadin.data.DataStore;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 4/1/17.
 */
public class ViewLayout extends VerticalLayout {

    private DataStore dataStore;

    private final AbstractView view;

    public ViewLayout(AbstractView view) {
        this.view = view;
        this.dataStore = new DataStore(view.getForm().getData());

        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                dataStore.setData(newData);
            }
        });

        buildToolBar();
        if (view instanceof AbstractListView) {
            add(this, new GridField("_data", ((AbstractListView) view).getColumns()), false);
        } else {
            buildBody();
        }

        addListeners();
    }

    public void startWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Notification.show("waiting...");
            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                Notification.show("...end.");
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
        int posField = 0;
        for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
            if (c instanceof AbstractField) {

                add(this, (AbstractField) c);

                posField++;
            }
        }
    }

    private void buildToolBar() {
        HorizontalLayout h = new HorizontalLayout();

        if (view instanceof AbstractListView) {

            int posField = 0;
            for (io.mateu.ui.core.client.components.Component c : view.getForm().getComponentsSequence()) {
                if (c instanceof AbstractField) {

                    add(h, (AbstractField) c, true, true);

                    posField++;
                    if (posField > ((AbstractListView) view).getMaxFieldsInHeader()) break;
                }
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

        {
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

    private void add(Layout where, AbstractField c, boolean paintLabel) {
        add(where, c, paintLabel, false);
    }
    private void add(Layout where, AbstractField c, boolean paintLabel, boolean inToolbar) {
        HorizontalLayout h = new HorizontalLayout();
        if (paintLabel && c.getLabel() != null) {
            Label l;
            h.addComponent(l = new Label((c.getLabel().getText() != null)?c.getLabel().getText():""));
            if (!inToolbar) {
                l.setWidth("200px");
                l.addStyleName("align-right");
            }
        }
        h.addComponent(getVaadinComponent(c));
        where.addComponent(h);
    }

    private Component getVaadinComponent(AbstractField field) {
        Component c = null;

        if (field instanceof GridField) {

            GridField g = (GridField) field;

            Table table = new Table((g.getLabel() != null)?g.getLabel().getText():null);

// Define two columns for the built-in container
            {
                table.addContainerProperty("_selected", CheckBox.class,  null, "Sel.", null, Table.Align.CENTER);
            }
            for (AbstractColumn col : g.getColumns()) {
                if (col instanceof LinkColumn) {
                    table.addContainerProperty(col.getId(), Button.class, null, col.getLabel(), null, null);
                } else {
                    table.addContainerProperty(col.getId(), Object.class, null, col.getLabel(), null, null);
                }
                table.setColumnWidth(col.getId(), col.getWidth());
            }

            Data data = view.getForm().getData();

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
                            ll.add(((DataStore)o).getData());
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
                                        ((LinkColumn)col).run(x.getData());
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
        } else {
            c = new TextField();

            Data data = view.getForm().getData();

            Object v = data.get(field.getId());
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

        return c;
    }

}
