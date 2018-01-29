package io.mateu.ui.vaadin;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import io.mateu.ui.core.client.components.RandomStyle;
import io.mateu.ui.core.client.components.fields.CalendarField;
import io.mateu.ui.core.shared.Data;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarLayout extends VerticalLayout {

    private final CalendarField field;
    private final CssLayout layoutCalendario;
    private final HorizontalLayout layoutFormulario;
    private final Property<Data> prop;



    Map<LocalDate, Data> values = new HashMap<>();
    Map<String, Data> options = new HashMap<>();

    public List<DiaLayout> seleccion = new ArrayList<>();
    public DiaLayout ultimoSeleccionado = null;
    public List<DiaLayout> seleccionEnCurso = new ArrayList<>();
    public Map<LocalDate, DiaLayout> nodos = new HashMap<>();
    public boolean anadiendo;
    public boolean quitando;
    public boolean diasSemana;
    private CssLayout nodoMeses;

    public CalendarLayout(CalendarField field, Property<Data> prop) {
        this.field = field;
        this.prop = prop;

        addComponent(layoutCalendario = new CssLayout());

        addComponent(layoutFormulario = new HorizontalLayout());


        prop.addListener(new ChangeListener<Data>() {
            @Override
            public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {
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


        Data data;
        if ((data = prop.getValue()) != null) {
            LocalDate desde = data.getLocalDate("_fromdate");
            LocalDate hasta = data.getLocalDate("_todate");

            if (desde != null && hasta != null) {

                LocalDate d = LocalDate.of(desde.getYear(), desde.getMonth(), 1);
                LocalDate l = LocalDate.of(hasta.getYear(), hasta.getMonth(), 1).plusMonths(1).minusDays(1);


                values = new HashMap<>();
                options = new HashMap<>();

                for (Data x : data.getList("_values")) values.put(x.get("_key"), x);

                int pos = 0;
                for (Data x : data.getList("_options")) {
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

                    nodosemana.addComponent(new DiaLayout(this, d, values.get(d)));

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
        layoutFormulario.addComponent(new Label("Aquí el formulario"));
    }


}
