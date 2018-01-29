package io.mateu.ui.javafx.views.components;

import com.google.common.collect.Lists;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.data.DataStore;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NodoDia extends StackPane {

    private final CalendarNode calendarNode;
    public final LocalDate fecha;
    private final StackPane base;
    private final StackPane contenido;
    private final StackPane mascara;
    private DataStore data;
    private DataStore value;
    private boolean seleccionado = false;

    public NodoDia(CalendarNode calendarNode) {
        this(calendarNode, null, null);
    }

    public NodoDia(CalendarNode calendarNode, LocalDate d, DataStore data) {

        this.data = data;
        this.value = (data != null)?(calendarNode.options.get(data.get("_value"))):null;
        this.calendarNode = calendarNode;
        this.fecha = d;
        calendarNode.nodos.put(d, this);

        getChildren().add(base = new StackPane());
        base.getChildren().add(contenido = new StackPane());
        contenido.getChildren().add(new Text((d != null)?d.format(DateTimeFormatter.ofPattern("dd")):"-"));
        base.getChildren().add(mascara = new StackPane());

        mascara.getStyleClass().add("mascara");

        setPrefWidth(30);
        setPrefHeight(30);


        if (value != null) {
            if (value.containsKey("_css")) {
                contenido.getStyleClass().add(value.get("_css"));
            }
        }

        base.setStyle("-fx-background-color: #" + ((d == null)?"e1e1ff":"dadaff") + ";");



        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(event.getClickCount() == 2){
                        calendarNode.edit((data != null)?data.get("_value"):null, fecha);
                    } else {
                        if (calendarNode.seleccion.size() == 1 && calendarNode.seleccion.contains(NodoDia.this)) {
                            calendarNode.limpiarSeleccion();
                        } else {
                            seleccionar(false, event.isControlDown(), event.isShiftDown());
                        }
                    }
                }
            }
        });

        setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (fecha != null) {

                    Dragboard db = startDragAndDrop(TransferMode.ANY);

                    /* Put a string on a dragboard */
                    ClipboardContent content = new ClipboardContent();
                    content.putString("");
                    db.setContent(content);

                    calendarNode.anadiendo = event.isControlDown();
                    calendarNode.diasSemana = event.isAltDown();

                    if (!calendarNode.anadiendo) {
                        calendarNode.limpiarSeleccion();
                        calendarNode.quitando = false;
                    } else {
                        if (seleccionado) {
                            calendarNode.quitando = true;
                        } else {
                            calendarNode.quitando = false;
                        }
                    }

                    for (NodoDia n : calendarNode.seleccionEnCurso) {
                        if (!n.seleccionado) {
                            n.mascara.getStyleClass().remove("seleccionado");
                        }
                    }
                    calendarNode.seleccionEnCurso = new ArrayList<>();

                    calendarNode.seleccionEnCurso.add(NodoDia.this);
                    if (calendarNode.quitando) {
                        if (mascara.getStyleClass().contains("seleccionado")) mascara.getStyleClass().remove("seleccionado");
                    } else {
                        if (!mascara.getStyleClass().contains("seleccionado")) mascara.getStyleClass().add("seleccionado");
                    }
                    calendarNode.ultimoSeleccionado = NodoDia.this;

                    event.consume();

                }

            }
        });

        setOnDragDone(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getSource() instanceof NodoDia && ((NodoDia) event.getSource()).fecha != null) {

                    for (NodoDia n : calendarNode.seleccionEnCurso) {
                        n.seleccionado = !calendarNode.quitando;
                        if (n.seleccionado) {
                            if (!calendarNode.seleccion.contains(n)) calendarNode.seleccion.add(n);
                        } else {
                            if (calendarNode.seleccion.contains(n)) calendarNode.seleccion.remove(n);
                        }
                    }
                    calendarNode.seleccionEnCurso = new ArrayList<>();
                    calendarNode.ultimoSeleccionado = null;
                }
            }
        });

        setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getSource() instanceof NodoDia && ((NodoDia) event.getSource()).fecha != null && fecha != null) {

                    LocalDate i = LocalDate.from(calendarNode.ultimoSeleccionado.fecha);
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
                    if (calendarNode.diasSemana) {
                        wd0 = wds.indexOf(d.getDayOfWeek());
                        wd1 = wds.indexOf(i.getDayOfWeek());


                        if (wd0 > wd1) {
                            int aux = wd1;
                            wd1 = wd0;
                            wd0 = aux;
                        }
                    }


                    List<NodoDia> aux = new ArrayList<>();
                    int count = 0;
                    while (!i.isAfter(d)) {
                        NodoDia n = calendarNode.nodos.get(i);

                        if (!calendarNode.diasSemana || (wd0 <= wds.indexOf(i.getDayOfWeek()) && wds.indexOf(i.getDayOfWeek()) <= wd1)) aux.add(n);

                        i = i.plusDays(1);

                        if (count++ > 10000) {
                            System.out.println("aaaaahhhhhh");
                            break;
                        }
                    }

                    for (NodoDia n : calendarNode.seleccionEnCurso) {
                        if (!aux.contains(n)) {
                            if (n.seleccionado) {
                                if (!n.mascara.getStyleClass().contains("seleccionado")) n.mascara.getStyleClass().add("seleccionado");
                            } else {
                                if (n.mascara.getStyleClass().contains("seleccionado")) n.mascara.getStyleClass().remove("seleccionado");
                            }
                        }
                    }
                    calendarNode.seleccionEnCurso = aux;
                    for (NodoDia n : calendarNode.seleccionEnCurso) {
                        if (calendarNode.quitando) {
                            if (n.mascara.getStyleClass().contains("seleccionado")) n.mascara.getStyleClass().remove("seleccionado");
                        } else {
                            if (!n.mascara.getStyleClass().contains("seleccionado")) n.mascara.getStyleClass().add("seleccionado");
                        }
                    }


                }
            }
        });

    }

    public void seleccionar() {
        seleccionar(false, false, false);
    }

    public void seleccionar(boolean forzarValor, boolean agregando, boolean multiple) {
        if (fecha != null) {
            if (!agregando && !multiple) calendarNode.limpiarSeleccion();
            seleccionado = (forzarValor)?true:!seleccionado;
            if (seleccionado && fecha != null) {
                if (!agregando && !multiple) calendarNode.limpiarSeleccion();
                if (multiple) {
                    if (calendarNode.ultimoSeleccionado != null && calendarNode.ultimoSeleccionado.fecha != null) {
                        LocalDate i = LocalDate.from(calendarNode.ultimoSeleccionado.fecha);
                        LocalDate d = LocalDate.from(fecha);

                        if (i.isAfter(d)) {
                            LocalDate aux = d;
                            d = i;
                            i = aux;
                        }

                        int count = 0;
                        while (!i.isAfter(d)) {
                            NodoDia n = calendarNode.nodos.get(i);

                            n.seleccionado = true;
                            if (!n.mascara.getStyleClass().contains("seleccionado")) n.mascara.getStyleClass().add("seleccionado");
                            if (!calendarNode.seleccion.contains(n)) calendarNode.seleccion.add(n);

                            i = i.plusDays(1);

                            if (count++ > 10000) {
                                System.out.println("aaaaahhhhhh");
                                break;
                            }
                        }

                    }
                }
                if (!calendarNode.seleccion.contains(this)) calendarNode.seleccion.add(this);
                calendarNode.ultimoSeleccionado = this;
            } else {
                calendarNode.seleccion.remove(this);
            }

            if (seleccionado) {
                if (!mascara.getStyleClass().contains("seleccionado")) mascara.getStyleClass().add("seleccionado");
            } else {
                mascara.getStyleClass().remove("seleccionado");
            }
        }
    }

    public void set(DataStore value) {
        unset();
        if (data == null) {
            data = new DataStore(new Data("__id", UUID.randomUUID().toString()));
            calendarNode.values.put(fecha, data);
            calendarNode.dataProperty.getValue().getObservableListProperty("_values").getValue().add(data);
        }
        data.set("_value", value.get("__id"));
        if (value != null && value.get("_css") != null) {
            if (!contenido.getStyleClass().contains(value.get("_css"))) contenido.getStyleClass().add(value.get("_css"));
        }
        this.value = value;
    }

    public void unset() {
        if (value != null && value.get("_css") != null) {
            if (contenido.getStyleClass().contains(value.get("_css"))) contenido.getStyleClass().remove(value.get("_css"));
        }
        if (data != null) data.set("_value", null);
        value = null;
    }

    public void deseleccionar() {
        seleccionado = false;
        if (mascara.getStyleClass().contains("seleccionado")) mascara.getStyleClass().remove("seleccionado");
    }
}
