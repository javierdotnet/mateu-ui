package io.mateu.ui.javafx.views.components;

import io.mateu.ui.core.Mateu;
import io.mateu.ui.core.app.AbstractAction;
import io.mateu.ui.core.app.ActionOnRow;
import io.mateu.ui.core.app.MateuUI;
import io.mateu.ui.core.components.fields.GridField;
import io.mateu.ui.core.components.fields.grids.AbstractColumn;
import io.mateu.ui.core.components.fields.grids.LinkColumn;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.views.AbstractListView;
import io.mateu.ui.core.views.ListViewListener;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.views.ViewNode;
import io.mateu.ui.javafx.views.components.table.MateuCheckBoxTableCell;
import io.mateu.ui.javafx.views.components.table.MateuLinkTableCell;
import io.mateu.ui.javafx.views.components.table.PropertyValueFactory;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import io.mateu.ui.javafx.views.components.table.MateuTextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 30/12/16.
 */
public class GridNode extends VBox {

    private final GridField field;
    private final ViewNode viewNode;
    FlowPane paginationContainer;
    private Pagination pagination;

    public GridNode(ViewNode viewNode, GridField field) {
        this.viewNode = viewNode;
        this.field = field;

        getChildren().add(buildTable(field));

        createToolBar(field.getActions());

    }



    private void createToolBar(List<AbstractAction> actions) {
        ToolBar toolBar = new ToolBar();

        if (field.isPaginated()) {
            if (paginationContainer == null) {
                paginationContainer = new FlowPane();
                toolBar.getItems().add(paginationContainer);
                //paginationContainer.getChildren().clear();
                paginationContainer.getChildren().add(pagination = new Pagination());
                pagination.setDisable(true);

                //todo: añadir nº de registros por página!!!

                ((AbstractListView)viewNode.getView()).addListViewListener(new ListViewListener() {
                    @Override
                    public void onReset() {
                        System.out.println("paginación = 0");
                        pagination.setCurrentPageIndex(0);
                        System.out.println("paginación == 0");
                    }

                    @Override
                    public void onSearch() {
                        pagination.setDisable(true);
                        System.out.println("paginación deshabilitada");
                    }

                    @Override
                    public void onSuccess() {
                        pagination.setDisable(false);
                        System.out.println("paginación habilitada");
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        pagination.setDisable(false);
                        System.out.println("paginación habilitada");
                    }
                });

                pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                        if (!pagination.isDisabled() && !oldValue.equals(newValue)) {
                            System.out.println("LANZAMOS BÚSQUEDA POR CAMBIO DE PÁGINA");
                            ((AbstractListView)viewNode.getView()).search();
                        }
                    }
                });
            }
            pagination.currentPageIndexProperty().bindBidirectional(viewNode.getDataStore().getNumberProperty(field.getId() + "_currentpageindex"));
            pagination.pageCountProperty().bindBidirectional(viewNode.getDataStore().getNumberProperty(field.getId() + "_pagecount"));
        }

        if (field.isExpandable()) {
            Button b;
            toolBar.getItems().add(b = new Button("Add"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Data d;
                    viewNode.getDataStore().getData().getList(field.getId()).add(d = new Data());
                    DataStore ds = new DataStore(d);
                    ds.getBooleanProperty("selected");
                    for (AbstractColumn c : field.getColumns()) {
                        ds.getStringProperty(c.getId());
                    }
                    viewNode.getDataStore().getObservableListProperty(field.getId()).getValue().add(ds);
                }
            });
        }

        for (AbstractAction a : actions) {
            Button b;
            toolBar.getItems().add(b = new Button(a.getName()));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    MateuUI.run(a);
                }
            });
        }

        if (toolBar.getItems().size() > 0) getChildren().add(toolBar);
    }


    private Node buildTable(GridField c) {
        TableView<DataStore> t = new TableView<>();

        t.getColumns().addAll(buildColumns(c));

        Property<ObservableList<DataStore>> i = viewNode.getDataStore().getObservableListProperty(c.getId());//dataStore.getFilteredObservableListProperty3(id, campo.getFiltros());
        t.itemsProperty().bindBidirectional(i);

        t.getSelectionModel().setCellSelectionEnabled(true);
        t.setEditable(true);

        t.addEventHandler(KeyEvent.KEY_PRESSED, new javafx.event.EventHandler<KeyEvent>() {

            @Override
            public void handle(final KeyEvent event) {

                System.out.println("===TABLEVIEW.PRESSED===>" + event.getText() + "/" + event.getCode());


                if (event.getCode() == KeyCode.TAB) {
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            if (event.isShiftDown()) t.selectionModelProperty().get().selectPrevious();
                            else t.selectionModelProperty().get().selectNext();
                        }
                    });

                    event.consume();
                }

            }
        });


        t.setPrefWidth(5000);
        t.setPrefHeight(5000);

        return t;
    }

    private List<TableColumn<DataStore, ?>> buildColumns(GridField g) {
        List<TableColumn<DataStore, ?>> l = new ArrayList<>();

        {
            TableColumn<DataStore, Boolean> c1;
            l.add(c1 = new TableColumn<DataStore, Boolean>("Sel."));
            c1.setCellValueFactory(new PropertyValueFactory<Boolean>("selected"));
            c1.setCellFactory(MateuCheckBoxTableCell.forTableColumn(c1));
            c1.setEditable(true);
            c1.setPrefWidth(30);
        }

        for (AbstractColumn c : g.getColumns()) {

            TableColumn<DataStore, String> c1;
            l.add(c1 = new TableColumn<DataStore, String>(c.getLabel()));

            c1.setPrefWidth(c.getWidth());

            if (c instanceof LinkColumn) {
                c1.setCellValueFactory(new PropertyValueFactory<String>(c.getId()));
                c1.setCellFactory(MateuLinkTableCell.<DataStore, String>forTableColumn(new DefaultStringConverter(), (ActionOnRow) c));
            } else {
                c1.setCellValueFactory(new PropertyValueFactory<String>(c.getId()));
                c1.setCellFactory(MateuTextFieldTableCell.<DataStore, String>forTableColumn(new DefaultStringConverter()));
                c1.setEditable(true);
            }

        }

        return l;
    }
}
