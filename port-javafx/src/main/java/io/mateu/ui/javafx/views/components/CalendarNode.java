package io.mateu.ui.javafx.views.components;

import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.RandomStyle;
import io.mateu.ui.core.client.components.fields.CalendarField;
import io.mateu.ui.core.client.views.AbstractAddRecordDialog;
import io.mateu.ui.core.client.views.AbstractForm;
import io.mateu.ui.core.client.views.AbstractListEditorDialog;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.JFXHelper;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarNode extends VBox {

    private final CalendarField field;
    public final Property<DataStore> dataProperty;
    private VBox nodoMeses;
    private HBox nodoFormulario;


    Map<LocalDate, DataStore> values = new HashMap<>();
    Map<String, DataStore> options = new HashMap<>();

    public List<NodoDia> seleccion = new ArrayList<>();
    public NodoDia ultimoSeleccionado = null;
    public List<NodoDia> seleccionEnCurso = new ArrayList<>();
    public Map<LocalDate, NodoDia> nodos = new HashMap<>();
    public boolean anadiendo;
    public boolean quitando;
    public boolean diasSemana;
    private ViewNode viewNode;

    private ComboBox<DataStore> cb;

    public CalendarNode(ViewNode viewNode, CalendarField field, Property<DataStore> dataProperty) {

        super(10);

        this.viewNode = viewNode;
        this.field = field;
        this.dataProperty = dataProperty;

        dataProperty.addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observable, DataStore oldValue, DataStore newValue) {
                refresh();
            }
        });

        build();

    }

    private void build() {

        ScrollPane sp;
        getChildren().add(sp = new ScrollPane(nodoMeses = new VBox(10, new Text("Aquí los meses"))));
        sp.setPrefHeight(450);
        sp.setPrefWidth(490);
        sp.setPadding(new Insets(10));

        getChildren().add(nodoFormulario = new HBox(new Text("Aquí el formulario")));

    }

    private void refresh() {

        seleccion = new ArrayList<>();

        buildMeses();

        buildFormulario();
    }

    private void buildMeses() {
        nodoMeses.getChildren().clear();
        nodos.clear();

        DataStore data;
        if ((data = dataProperty.getValue()) != null) {
            LocalDate desde = data.getLocalDateProperty("_fromdate").getValue();
            LocalDate hasta = data.getLocalDateProperty("_todate").getValue();

            if (desde == null) desde = LocalDate.now();
            if (hasta == null) hasta = desde.plusYears(1);

            LocalDate d = LocalDate.of(desde.getYear(), desde.getMonth(), 1);
            LocalDate l = LocalDate.of(hasta.getYear(), hasta.getMonth(), 1).plusMonths(1).minusDays(1);


            values = new HashMap<>();
            options = new HashMap<>();

            ObservableList<DataStore> lvs = data.getObservableListProperty("_values").getValue();

            for (DataStore x : lvs) values.put(x.getPure("_key"), x);

            int pos = 0;
            for (DataStore x : data.getObservableListProperty("_options").getValue()) {
                options.put(x.get("__id"), x);
                if (!x.containsKey("_css")) x.set("_css", RandomStyle.getCsss().get(pos++ % RandomStyle.getCsss().size()));
            }

            int posmes = 0;
            HBox fila = null;
            VBox nodomes = null;
            HBox nodosemana = null;
            int mesactual = -1;

            while (!d.isAfter(l)) {

                if (nodomes == null || mesactual != d.getMonthValue()) {

                    if (nodosemana != null) {
                        d = d.minusDays(1);
                        boolean visto = false;
                        if (!visto && !DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) {
                            nodosemana.getChildren().add(new NodoDia(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.SATURDAY.equals(d.getDayOfWeek())) {
                            nodosemana.getChildren().add(new NodoDia(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.FRIDAY.equals(d.getDayOfWeek())) {
                            nodosemana.getChildren().add(new NodoDia(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.THURSDAY.equals(d.getDayOfWeek())) {
                            nodosemana.getChildren().add(new NodoDia(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.WEDNESDAY.equals(d.getDayOfWeek())) {
                            nodosemana.getChildren().add(new NodoDia(this));
                        } else visto = true;
                        if (!visto && !DayOfWeek.TUESDAY.equals(d.getDayOfWeek())) {
                            nodosemana.getChildren().add(new NodoDia(this));
                        } else visto = true;
                        d = d.plusDays(1);
                    }


                    if (posmes++ % 2 == 0) {
                        fila = new HBox(10);
                        nodoMeses.getChildren().add(fila);
                    }

                    mesactual = d.getMonthValue();
                    nodomes = new VBox(1);
                    nodomes.setAlignment(Pos.TOP_CENTER);
                    nodomes.getChildren().add(new Text(d.format(DateTimeFormatter.ofPattern("MMMM yyyy"))));
                    StackPane s;
                    nodomes.getChildren().add(s = new StackPane());
                    s.setPrefHeight(5);
                    fila.getChildren().add(nodomes);

                    nodosemana = null;
                }

                if (nodosemana == null || DayOfWeek.MONDAY.equals(d.getDayOfWeek())) {

                    nodosemana = new HBox(1);
                    nodomes.getChildren().add(nodosemana);

                    boolean visto = false;
                    if (!visto && !DayOfWeek.MONDAY.equals(d.getDayOfWeek())) {
                        nodosemana.getChildren().add(new NodoDia(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.TUESDAY.equals(d.getDayOfWeek())) {
                        nodosemana.getChildren().add(new NodoDia(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.WEDNESDAY.equals(d.getDayOfWeek())) {
                        nodosemana.getChildren().add(new NodoDia(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.THURSDAY.equals(d.getDayOfWeek())) {
                        nodosemana.getChildren().add(new NodoDia(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.FRIDAY.equals(d.getDayOfWeek())) {
                        nodosemana.getChildren().add(new NodoDia(this));
                    } else visto = true;
                    if (!visto && !DayOfWeek.SATURDAY.equals(d.getDayOfWeek())) {
                        nodosemana.getChildren().add(new NodoDia(this));
                    } else visto = true;

                }

                DataStore v = values.get(d);
                if (v == null) {
                    lvs.add(v = new DataStore(new Data("__id", UUID.randomUUID().toString(), "_key", LocalDate.from(d), "_value", null)));
                }
                nodosemana.getChildren().add(new NodoDia(this, d, v));

                d = d.plusDays(1);
            }

            if (nodosemana != null) {
                d = d.minusDays(1);
                boolean visto = false;
                if (!visto && !DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) {
                    nodosemana.getChildren().add(new NodoDia(this));
                } else visto = true;
                if (!visto && !DayOfWeek.SATURDAY.equals(d.getDayOfWeek())) {
                    nodosemana.getChildren().add(new NodoDia(this));
                } else visto = true;
                if (!visto && !DayOfWeek.FRIDAY.equals(d.getDayOfWeek())) {
                    nodosemana.getChildren().add(new NodoDia(this));
                } else visto = true;
                if (!visto && !DayOfWeek.THURSDAY.equals(d.getDayOfWeek())) {
                    nodosemana.getChildren().add(new NodoDia(this));
                } else visto = true;
                if (!visto && !DayOfWeek.WEDNESDAY.equals(d.getDayOfWeek())) {
                    nodosemana.getChildren().add(new NodoDia(this));
                } else visto = true;
                if (!visto && !DayOfWeek.TUESDAY.equals(d.getDayOfWeek())) {
                    nodosemana.getChildren().add(new NodoDia(this));
                } else visto = true;
            }


        }

        //nodoMeses.getChildren().add(new Text("Hola!!!"));
        //requestLayout();
    }

    private void buildFormulario() {
        nodoFormulario.getChildren().clear();

        HBox h;
        nodoFormulario.getChildren().add(h = new HBox(10));
        h.getChildren().add(new Text("Options:"));

        h.getChildren().add(cb = new ComboBox<DataStore>());
        if (dataProperty.getValue() != null) cb.setItems(dataProperty.getValue().getObservableListProperty("_options").getValue());
        cb.setCellFactory((c) -> new ListCell<DataStore>() {
            @Override
            protected void updateItem(DataStore item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
                setText(null);
                if(item!=null){
                    StackPane p = new StackPane();
                    p.setPrefHeight(12);
                    p.setPrefWidth(20);
                    p.setStyle("-fx-border-color: grey;");
                    if (item.get("_css") != null) p.getStyleClass().add(item.get("_css"));
                    setGraphic(p);
                    setText(item.toString());
                }
            }
        });

        StackPane muestra;
        h.getChildren().add(muestra = new StackPane());
        muestra.setPrefWidth(20);
        muestra.setPrefHeight(12);
        muestra.setStyle("-fx-border-color: grey;");

        cb.valueProperty().addListener(new ChangeListener<DataStore>() {
            @Override
            public void changed(ObservableValue<? extends DataStore> observable, DataStore oldValue, DataStore newValue) {
                String css = null;
                if (newValue != null && newValue.get("_css") != null) css = newValue.get("_css");
                muestra.getStyleClass().clear();
                muestra.getStyleClass().add(css);
            }
        });


        Button b;

        h.getChildren().add(b = new Button("Edit"));
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb.getValue() == null) {
                    MateuUI.alert("You must select an option first.");
                } else {
                    edit(cb.getValue().get("__id"), null);
                }
            }
        });

        h.getChildren().add(b = new Button("Add"));
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                edit(null, null);
            }
        });

        h.getChildren().add(b = new Button("Set"));
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (cb.getValue() == null) {
                    MateuUI.alert("You must select an option first.");
                } else {
                    for (NodoDia n : seleccion) {
                        n.set(cb.getValue());
                    }
                }
            }
        });

        h.getChildren().add(b = new Button("Unset"));
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (NodoDia n : seleccion) {
                    n.unset();
                }
            }
        });

    }

    public void edit(String __id, LocalDate date) {
        AbstractForm f = field.getDataForm();

        if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
        else {

            if (__id == null) {
                MateuUI.openView(new AbstractAddRecordDialog() {

                    @Override
                    public void addAndClean(Data data) {
                        DataStore ds = new DataStore(data);
                        System.out.println("***********************field.getNameProperty()=" + field.getNameProperty());
                        ds.set("_nameproperty", field.getNameProperty());
                        {
                            ObservableList<DataStore> l = viewNode.getDataStore().getDataProperty(field.getId()).getValue()
                                    .getObservableListProperty("_options").getValue();
                            l.add(ds);
                            ds.set("_css", RandomStyle.getCsss().get(l.size() % RandomStyle.getCsss().size()));
                        }
                        clear();
                        set("__id", "" + UUID.randomUUID());

                        if (date != null) {
                            DataStore dx = null;
                            if (!values.containsKey(date)) {
                                values.put(date, dx = new DataStore(new Data("_key", date)));

                                ObservableList<DataStore> l = viewNode.getDataStore().getDataProperty(field.getId())
                                        .getValue().getObservableListProperty("_values").getValue();
                                l.add(dx);

                            } else dx = values.get(date);
                            dx.set("_value", ds.get("__id"));

                            refresh();
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
                for (DataStore d : cb.getItems()) {
                    if (d.get("__id").equals(__id)) {
                        break;
                    }
                    posx++;
                }

                int finalPosx = posx;
                MateuUI.openView(new AbstractListEditorDialog() {

                    @Override
                    public Data getData(int pos) {
                        return cb.getItems().get(pos).getData();
                    }

                    @Override
                    public void setData(int pos, Data data) {
                        cb.getItems().get(pos).setData(data);
                    }

                    @Override
                    public int getListSize() {
                        return cb.getItems().size();
                    }

                    @Override
                    public int getInitialPos() {
                        return finalPosx;
                    }

                    @Override
                    public Data initializeData() {
                        return cb.getItems().get(getInitialPos()).getData();
                    }

                    @Override
                    public void onOk(Data data) {
                        for (DataStore d : cb.getItems()) {
                            if (d.get("__id").equals(data.get("__id"))) {
                                d.setData(data);

                                cb.setValue(null);
                                List<DataStore> l = new ArrayList<>(cb.getItems());
                                cb.getItems().clear();
                                cb.getItems().addAll(l);
                                cb.setValue(d);
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
        for (NodoDia n : new ArrayList<>(seleccion)) n.deseleccionar();
        seleccion = new ArrayList<>();
    }
}
