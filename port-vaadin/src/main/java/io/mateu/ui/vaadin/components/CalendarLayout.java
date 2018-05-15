package io.mateu.ui.vaadin.components;

import com.google.common.base.Strings;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.*;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.RandomStyle;
import io.mateu.ui.core.client.components.fields.CalendarField;
import io.mateu.ui.core.client.views.AbstractAddRecordDialog;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractListEditorDialog;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.vaadin.data.DataStore;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarLayout extends VerticalLayout {

    private final CalendarField field;
    private final CssLayout layoutCalendario;
    private final HorizontalLayout layoutFormulario;
    public ComboBox<DataStore> comboOpciones;
    private final Property<DataStore> prop;



    Map<LocalDate, DataStore> values = new HashMap<>();
    Map<String, DataStore> options = new HashMap<>();

    public List<DiaLayout> seleccion = new ArrayList<>();
    public DiaLayout ultimoSeleccionado = null;
    public List<DiaLayout> seleccionEnCurso = new ArrayList<>();
    public Map<LocalDate, DiaLayout> nodos = new HashMap<>();
    public boolean anadiendo;
    public boolean quitando;
    public boolean diasSemana;
    private CssLayout nodoMeses;

    public CalendarLayout(CalendarField field, Property<DataStore> prop) {
        this.field = field;
        this.prop = prop;

        addComponent(layoutCalendario = new CssLayout());

        addComponent(layoutFormulario = new HorizontalLayout());


        prop.addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observable, DataStore oldValue, DataStore newValue) {
                refrescar();
            }
        });

        refrescar();
    }

    private void refrescar() {

        refrescarCalendario();

        refrescarFormulario();
    }

    private void refrescarCalendario() {
        layoutCalendario.removeAllComponents();
        layoutCalendario.addComponent(nodoMeses = new CssLayout());


        DataStore data;
        if ((data = prop.getValue()) != null) {
            LocalDate desde = data.getLocalDate("_fromdate");
            LocalDate hasta = data.getLocalDate("_todate");

            if (desde == null) desde = LocalDate.now();
            if (hasta == null) hasta = desde.plusYears(1);

            if (desde != null && hasta != null) {

                LocalDate d = LocalDate.of(desde.getYear(), desde.getMonth(), 1);
                LocalDate l = LocalDate.of(hasta.getYear(), hasta.getMonth(), 1).plusMonths(1).minusDays(1);


                values = new HashMap<>();
                options = new HashMap<>();

                ObservableList<DataStore> lvs = data.getObservableListProperty("_values").getValue();

                for (DataStore x : lvs) values.put(x.get("_key"), x);

                int pos = 0;
                for (DataStore x : data.getObservableListProperty("_options").getValue()) {
                    options.put(x.get("__id"), x);
                    if (!x.containsKey("_css")) x.set("_css", RandomStyle.getCsss().get(pos++ % RandomStyle.getCsss().size()));
                }

                int posmes = 0;
                HorizontalLayout fila = null;
                VerticalLayout nodomes = null;
                HorizontalLayout nodosemana = null;
                int mesactual = -1;

                while (!d.isAfter(l)) {

                    if (nodomes == null || mesactual != d.getMonthValue()) {

                        if (nodosemana != null) {
                            d = d.minusDays(1);
                            boolean visto = false;
                            if (!visto && !DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) {
                                nodosemana.addComponent(new DiaLayout(this));
                            } else visto = true;
                            if (!visto && !DayOfWeek.SATURDAY.equals(d.getDayOfWeek())) {
                                nodosemana.addComponent(new DiaLayout(this));
                            } else visto = true;
                            if (!visto && !DayOfWeek.FRIDAY.equals(d.getDayOfWeek())) {
                                nodosemana.addComponent(new DiaLayout(this));
                            } else visto = true;
                            if (!visto && !DayOfWeek.THURSDAY.equals(d.getDayOfWeek())) {
                                nodosemana.addComponent(new DiaLayout(this));
                            } else visto = true;
                            if (!visto && !DayOfWeek.WEDNESDAY.equals(d.getDayOfWeek())) {
                                nodosemana.addComponent(new DiaLayout(this));
                            } else visto = true;
                            if (!visto && !DayOfWeek.TUESDAY.equals(d.getDayOfWeek())) {
                                nodosemana.addComponent(new DiaLayout(this));
                            } else visto = true;
                            d = d.plusDays(1);
                        }


                        if (posmes++ % 2 == 0) {
                            fila = new HorizontalLayout();
                            nodoMeses.addComponent(fila);
                        }

                        mesactual = d.getMonthValue();
                        nodomes = new VerticalLayout();
                        nodomes.addStyleName("mes");
                        //nodomes.setAlignment(Pos.TOP_CENTER);
                        nodomes.addComponent(new Label(d.format(DateTimeFormatter.ofPattern("MMMM yyyy"))));
                        CssLayout s;
                        nodomes.addComponent(s = new CssLayout());
                        //s.setPrefHeight(5);
                        fila.addComponent(nodomes);

                        nodosemana = null;
                    }

                    if (nodosemana == null || DayOfWeek.MONDAY.equals(d.getDayOfWeek())) {

                        nodosemana = new HorizontalLayout();
                        nodosemana.addStyleName("semana");
                        nodomes.addComponent(nodosemana);

                        boolean visto = false;
                        if (!visto && !DayOfWeek.MONDAY.equals(d.getDayOfWeek())) {
                            nodosemana.addComponent(new DiaLayout(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.TUESDAY.equals(d.getDayOfWeek())) {
                            nodosemana.addComponent(new DiaLayout(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.WEDNESDAY.equals(d.getDayOfWeek())) {
                            nodosemana.addComponent(new DiaLayout(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.THURSDAY.equals(d.getDayOfWeek())) {
                            nodosemana.addComponent(new DiaLayout(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.FRIDAY.equals(d.getDayOfWeek())) {
                            nodosemana.addComponent(new DiaLayout(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.SATURDAY.equals(d.getDayOfWeek())) {
                            nodosemana.addComponent(new DiaLayout(this));
                        } else visto = true;

                    }

                    DiaLayout dl;
                    DataStore v = values.get(d);
                    if (v == null) {
                        lvs.add(v = new DataStore(new Data("__id", UUID.randomUUID().toString(), "_key", LocalDate.from(d), "_value", null)));
                    }
                    nodosemana.addComponent(dl = new DiaLayout(this, d, v));
                    nodos.put(d, dl);

                    d = d.plusDays(1);
                }

                if (nodosemana != null) {
                    d = d.minusDays(1);
                    boolean visto = false;
                    if (!visto && !DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) {
                        nodosemana.addComponent(new DiaLayout(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.SATURDAY.equals(d.getDayOfWeek())) {
                        nodosemana.addComponent(new DiaLayout(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.FRIDAY.equals(d.getDayOfWeek())) {
                        nodosemana.addComponent(new DiaLayout(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.THURSDAY.equals(d.getDayOfWeek())) {
                        nodosemana.addComponent(new DiaLayout(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.WEDNESDAY.equals(d.getDayOfWeek())) {
                        nodosemana.addComponent(new DiaLayout(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.TUESDAY.equals(d.getDayOfWeek())) {
                        nodosemana.addComponent(new DiaLayout(this));
                    } else visto = true;
                }

            }

        }

    }

    private void refrescarFormulario() {
        layoutFormulario.removeAllComponents();

        layoutFormulario.addComponent(new Label("Options:"));

        layoutFormulario.addComponent(comboOpciones = new ComboBox<>());
        ListDataProvider<DataStore> ldp;
        comboOpciones.setDataProvider(ldp = new ListDataProvider<>(prop.getValue().getObservableListProperty("_options").getValue()));
        comboOpciones.setTextInputAllowed(false);
/*        comboOpciones.setItemCaptionGenerator(new ItemCaptionGenerator<Data>() {
            @Override
            public String apply(Data data) {
                if (data == null) return null;
                return "<div class='" + data.get("_css") + "'>" + data.get("_text") + "</div>";
            }
        });*/
/*
        comboOpciones.setStyleGenerator(new StyleGenerator<Data>() {
            @Override
            public String apply(Data data) {
                if (data == null) return null;
                return "background-color: red;";
            }
        });
*/
        //comboOpciones.setCaptionAsHtml(true);


        layoutFormulario.addComponent(new Button("Edit", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Object __id = null;
                if (comboOpciones.getValue() != null) __id = comboOpciones.getValue().get("__id");
                edit(__id, null);
            }
        }));
        layoutFormulario.addComponent(new Button("Add", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                edit(null, null);
            }
        }));
        layoutFormulario.addComponent(new Button("Set", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                set(comboOpciones.getValue());
            }
        }));
        layoutFormulario.addComponent(new Button("Unset", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                set(null);
            }
        }));

        CheckBox cb;
        layoutFormulario.addComponent(cb = new CheckBox("Add selections"));
        cb.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Boolean> valueChangeEvent) {
               anadiendo = valueChangeEvent.getValue();
            }
        });

        layoutFormulario.addComponent(cb = new CheckBox("Week days selection"));
        cb.addValueChangeListener(new HasValue.ValueChangeListener<Boolean>() {
            @Override
            public void valueChange(HasValue.ValueChangeEvent<Boolean> valueChangeEvent) {
                diasSemana = valueChangeEvent.getValue();
            }
        });
    }

    private void set(DataStore value) {
        Object __id = null;
        if (value != null) __id = value.get("__id");

        for (DiaLayout d : seleccion) {
            if (__id != null) {
                if (d.data == null) {
                    d.data = new DataStore(new Data("_key", d.fecha, "_value", __id));
                    d.data.set("_value", __id);
                }
            } else {
                if (d.data != null) {
                    d.data.set("_value", __id);
                }
            }
            if (d.value != null) d.removeStyleName(d.value.get("_css"));
            d.value = value;
            if (d.value != null) d.addStyleName(d.value.get("_css"));
        }
    }


    public void edit(Object __id, LocalDate fecha) {
        AbstractForm f = field.getDataForm();

        if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
        else {

            if (__id == null) {
                MateuUI.openView(new AbstractAddRecordDialog() {

                    @Override
                    public void addAndClean(Data data) {
                        DataStore ds = new DataStore(data);
                        System.out.println("***********************field.getNameProperty()=" + field.getNameProperty());
                        if (!Strings.isNullOrEmpty(field.getNameProperty())) ds.set("_nameproperty", field.getNameProperty());
                        {
                            ObservableList<DataStore> l = prop.getValue().getObservableListProperty("_options").getValue();
                            l.add(ds);
                            //((ListDataProvider<Data>)comboOpciones.getDataProvider()).getItems().add(data);
                            comboOpciones.getDataProvider().refreshAll();
                            ds.set("_css", RandomStyle.getCsss().get(l.size() % RandomStyle.getCsss().size()));
                        }
                        clear();
                        set("__id", "" + UUID.randomUUID());

                        if (fecha != null) {
                            DataStore dx = null;
                            if (!values.containsKey(fecha)) {
                                values.put(fecha, dx = new DataStore(new Data("_key", fecha)));

                                ObservableList<DataStore> l = prop.getValue().getObservableListProperty("_values").getValue();
                                l.add(dx);

                            } else dx = values.get(fecha);
                            dx.set("_value", ds.get("__id"));
                        }
                    }

                    @Override
                    public AbstractForm createForm() {
                        return f;
                    }

                    @Override
                    public String getTitle() {
                        return "Add new record";
                    }

                    @Override
                    public void build() {
                    }
                });

            } else {

                int posx = 0;
                for (DataStore d : prop.getValue().getObservableListProperty("_options").getValue()) {
                    if (d.get("__id").equals(__id)) {
                        break;
                    }
                    posx++;
                }

                int finalPosx = posx;
                MateuUI.openView(new AbstractListEditorDialog() {

                    @Override
                    public Data getData(int pos) {
                        return prop.getValue().getObservableListProperty("_options").getValue().get(pos).getData();
                    }

                    @Override
                    public void setData(int pos, Data data) {
                        prop.getValue().getObservableListProperty("_options").getValue().set(pos, new DataStore(data));
                    }

                    @Override
                    public int getListSize() {
                        return prop.getValue().getObservableListProperty("_options").getValue().size();
                    }

                    @Override
                    public int getInitialPos() {
                        return finalPosx;
                    }

                    @Override
                    public Data initializeData() {
                        return prop.getValue().getObservableListProperty("_options").getValue().get(getInitialPos()).getData();
                    }

                    @Override
                    public void onOk(Data data) {
                        for (DataStore d : prop.getValue().getObservableListProperty("_options").getValue()) {
                            if (d.get("__id").equals(data.get("__id"))) {
                                d.setData(data);
                                break;
                            }
                        }
                    }

                    @Override
                    public String getTitle() {
                        return "Edit record";
                    }

                    @Override
                    public AbstractForm createForm() {
                        return f;
                    }

                    @Override
                    public void build() {

                    }
                });

            }

        }
    }

    public void limpiarSeleccion() {
        for (DiaLayout l : seleccion) {
            l.removeStyleName("seleccionado");
            l.seleccionado = false;
        }
        seleccion.clear();
    }
}
