package io.mateu.ui.vaadin;

import com.google.common.collect.Lists;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.LayoutEvents;
import com.vaadin.shared.ui.dnd.DropEffect;
import com.vaadin.shared.ui.dnd.EffectAllowed;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;
import io.mateu.ui.vaadin.data.DataStore;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiaLayout extends CssLayout {
    private final CalendarLayout calendarLayout;
    public final LocalDate fecha;
    public DataStore data;
    public DataStore value;
    public boolean seleccionado = false;

    public DiaLayout(CalendarLayout calendarLayout) {
        this(calendarLayout, null, null);
    }

    public DiaLayout(CalendarLayout calendarLayout, LocalDate fecha, DataStore dataIn) {
        this.calendarLayout = calendarLayout;
        this.fecha = fecha;
        this.data = dataIn;
        this.value = (data != null)?(calendarLayout.options.get(data.get("_value"))):null;

        addStyleName("dia");

        addComponent(new Label("" + ((fecha != null)?fecha.getDayOfMonth():"-")));

        if (value != null && value.containsKey("_css")) addStyleName(value.getString("_css"));

        addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent layoutClickEvent) {
                if (layoutClickEvent.isDoubleClick()) {
                    calendarLayout.edit((data != null)?data.get("_value"):null, fecha);
                } else {
                    Object __id = (data != null)?data.get("_value"):null;
                    DataStore found = null;
                    if (__id != null) for (DataStore d : ((ListDataProvider<DataStore>)calendarLayout.comboOpciones.getDataProvider()).getItems()) {
                        if (__id.equals(d.get("__id"))) found = d;
                    }
                    calendarLayout.comboOpciones.setValue(found);

                    if (calendarLayout.seleccion.size() == 1 && calendarLayout.seleccion.contains(DiaLayout.this)) {
                        calendarLayout.limpiarSeleccion();
                    } else {
                        seleccionar(false, layoutClickEvent.isCtrlKey(), layoutClickEvent.isShiftKey());
                    }
                }

            }
        });









        DragSourceExtension<DiaLayout> dragSource = new DragSourceExtension<>(this);

// set the allowed effect
        dragSource.setEffectAllowed(EffectAllowed.MOVE);
// set the text to transfer
        dragSource.setDataTransferText("hello receiver");
// set other data to transfer (in this case HTML)
        dragSource.setDataTransferData("text/html", "<label>hello receiver</label>");



        dragSource.addDragStartListener(event ->
                {
                    if (fecha != null) {

                        if (!calendarLayout.anadiendo) {
                            calendarLayout.limpiarSeleccion();
                            calendarLayout.quitando = false;
                        } else {
                            if (seleccionado) {
                                calendarLayout.quitando = true;
                            } else {
                                calendarLayout.quitando = false;
                            }
                        }

                        for (DiaLayout n : calendarLayout.seleccionEnCurso) {
                            if (!n.seleccionado) {
                                n.removeStyleName("seleccionado");
                            }
                        }
                        calendarLayout.seleccionEnCurso = new ArrayList<>();

                        calendarLayout.seleccionEnCurso.add(DiaLayout.this);
                        if (calendarLayout.quitando) {
                            removeStyleName("seleccionado");
                        } else {
                            if (!getStyleName().contains("seleccionado")) addStyleName("seleccionado");
                        }
                        calendarLayout.ultimoSeleccionado = DiaLayout.this;
                    }
                }
        );

/*
        dragSource.addDragEndListener(event -> {
            if (event.isCanceled()) {
                Notification.show("Drag event was canceled");
            } else {
                Notification.show("Drag event finished");
            }
        });
*/










        // make the layout accept drops
        DropTargetExtension<DiaLayout> dropTarget = new DropTargetExtension<>(this);

// the drop effect must match the allowed effect in the drag source for a successful drop
        dropTarget.setDropEffect(DropEffect.MOVE);


// catch the drops
        dropTarget.addDropListener(event -> {
            // if the drag source is in the same UI as the target
            Optional<AbstractComponent> eventDragSource = event.getDragSourceComponent();
            if (eventDragSource.isPresent() && eventDragSource.get() instanceof DiaLayout) {



                // fijamos selección en curso

                LocalDate i = LocalDate.from(calendarLayout.ultimoSeleccionado.fecha);
                LocalDate d = LocalDate.from(fecha);

                if (i.isAfter(d)) {
                    LocalDate aux = d;
                    d = i;
                    i = aux;
                }

                List<DayOfWeek> wds = Lists.newArrayList(
                        DayOfWeek.MONDAY
                        , DayOfWeek.TUESDAY
                        , DayOfWeek.WEDNESDAY
                        , DayOfWeek.THURSDAY
                        , DayOfWeek.FRIDAY
                        , DayOfWeek.SATURDAY
                        , DayOfWeek.SUNDAY
                );

                int wd0 = 0;
                int wd1 = wds.size();
                if (calendarLayout.diasSemana) {
                    wd0 = wds.indexOf(d.getDayOfWeek());
                    wd1 = wds.indexOf(i.getDayOfWeek());


                    if (wd0 > wd1) {
                        int aux = wd1;
                        wd1 = wd0;
                        wd0 = aux;
                    }
                }


                List<DiaLayout> aux = new ArrayList<>();
                int count = 0;
                while (!i.isAfter(d)) {
                    DiaLayout n = calendarLayout.nodos.get(i);

                    if (!calendarLayout.diasSemana || (wd0 <= wds.indexOf(i.getDayOfWeek()) && wds.indexOf(i.getDayOfWeek()) <= wd1)) aux.add(n);

                    i = i.plusDays(1);

                    if (count++ > 10000) {
                        System.out.println("aaaaahhhhhh");
                        break;
                    }
                }

                for (DiaLayout n : calendarLayout.seleccionEnCurso) {
                    if (!aux.contains(n)) {
                        if (n.seleccionado) {
                            if (!n.getStyleName().contains("seleccionado")) n.addStyleName("seleccionado");
                        } else {
                            n.removeStyleName("seleccionado");
                        }
                    }
                }
                calendarLayout.seleccionEnCurso = aux;
                for (DiaLayout n : calendarLayout.seleccionEnCurso) {
                    if (calendarLayout.quitando) {
                        n.removeStyleName("seleccionado");
                    } else {
                        if (!n.getStyleName().contains("seleccionado")) n.addStyleName("seleccionado");
                    }
                }




                // hacemos efectiva (grabamos) la selección

                for (DiaLayout n : calendarLayout.seleccionEnCurso) {
                    n.seleccionado = !calendarLayout.quitando;
                    if (n.seleccionado) {
                        if (!calendarLayout.seleccion.contains(n)) {
                            calendarLayout.seleccion.add(n);
                            if (!n.getStyleName().contains("seleccionado")) n.addStyleName("seleccionado");
                        }
                    } else {
                        if (calendarLayout.seleccion.contains(n)) {
                            calendarLayout.seleccion.remove(n);
                            n.removeStyleName("seleccionado");
                        }
                    }
                }
                calendarLayout.seleccionEnCurso = new ArrayList<>();
                calendarLayout.ultimoSeleccionado = null;

                /*

                // move the label to the layout
                dropTargetLayout.addComponent(dragSource.get());

                // get possible transfer data
                String message = event.getDataTransferData("text/html");
                if (message != null) {
                    Notification.show("DropEvent with data transfer html: " + message);
                } else {
                    // get transfer text
                    message = event.getDataTransferText();
                    Notification.show("DropEvent with data transfer text: " + message);
                }

                // handle possible server side drag data, if the drag source was in the same UI
                event.getDragData().ifPresent(data -> handleMyDragData((MyObject) data));

                */
            }
        });


    }

    private void seleccionar(boolean forzarValor, boolean agregando, boolean multiple) {
        if (fecha != null) {
            if (!agregando && !multiple) calendarLayout.limpiarSeleccion();
            seleccionado = (forzarValor)?true:!seleccionado;
            if (seleccionado && fecha != null) {
                if (!agregando && !multiple) calendarLayout.limpiarSeleccion();
                if (multiple) {
                    if (calendarLayout.ultimoSeleccionado != null && calendarLayout.ultimoSeleccionado.fecha != null) {
                        LocalDate i = LocalDate.from(calendarLayout.ultimoSeleccionado.fecha);
                        LocalDate d = LocalDate.from(fecha);

                        if (i.isAfter(d)) {
                            LocalDate aux = d;
                            d = i;
                            i = aux;
                        }

                        int count = 0;
                        while (!i.isAfter(d)) {
                            DiaLayout n = calendarLayout.nodos.get(i);

                            n.seleccionado = true;
                            if (!n.getStyleName().contains("seleccionado")) n.addStyleName("seleccionado");
                            if (!calendarLayout.seleccion.contains(n)) calendarLayout.seleccion.add(n);

                            i = i.plusDays(1);

                            if (count++ > 10000) {
                                System.out.println("aaaaahhhhhh");
                                break;
                            }
                        }

                    }
                }
                if (!calendarLayout.seleccion.contains(this)) calendarLayout.seleccion.add(this);
                calendarLayout.ultimoSeleccionado = this;
            } else {
                calendarLayout.seleccion.remove(this);
            }

            if (seleccionado) {
                if (!getStyleName().contains("seleccionado")) addStyleName("seleccionado");
            } else {
                removeStyleName("seleccionado");
            }
        }

    }
}
