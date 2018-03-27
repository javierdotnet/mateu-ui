package io.mateu.ui.javafx.views.components;

import com.google.common.base.Strings;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.ActionOnRow;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.fields.GridField;
import io.mateu.ui.core.client.components.fields.grids.columns.AbstractColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.DataColumn;
import io.mateu.ui.core.client.components.fields.grids.columns.LinkColumn;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.client.views.ListView;
import io.mateu.ui.core.shared.CellStyleGenerator;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.views.ViewNode;
import io.mateu.ui.javafx.views.components.tree.PropertyValueFactory;
import io.mateu.ui.javafx.views.components.tree.MateuDataColumnTreeTableCell;
import io.mateu.ui.javafx.views.components.tree.MateuLinkTreeTableCell;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by miguel on 30/12/16.
 */
public class TreeGridNode extends VBox {

    private final GridField field;
    private final ViewNode viewNode;
    private TreeTableView<DataStore> tableView;
    FlowPane paginationContainer;
    private Pagination pagination;
    private TreeTableView<DataStore> t;
    private boolean listenerSelectionActivo = true;

    public TreeGridNode(ViewNode viewNode, GridField field) {
        this.viewNode = viewNode;
        this.field = field;

        getChildren().add(tableView = buildTable(field));

        createToolBar(field.getActions());

    }



    private void createToolBar(List<AbstractAction> actions) {
        ToolBar toolBar = new ToolBar();

        toolBar.setStyle("-fx-background-color: #f4f4f4;");
        toolBar.setBorder(Border.EMPTY);

        if (field.isPaginated()) {
            if (paginationContainer == null) {
                paginationContainer = new FlowPane();
                toolBar.getItems().add(paginationContainer);
                //paginationContainer.getChildren().clear();
                paginationContainer.getChildren().add(pagination = new Pagination());
                pagination.setDisable(true);

                //todo: añadir nº de registros por página!!!
                Label rr;
                paginationContainer.getChildren().add(rr = new Label());
                rr.textProperty().bind(viewNode.getDataStore().getStringProperty(field.getId() + "_totalrows"));


                ((ListView)viewNode.getView()).addListViewListener(new ListViewListener() {
                    @Override
                    public void onReset() {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("paginación = 0");
                                pagination.setDisable(true);
                                pagination.setCurrentPageIndex(0);
                                System.out.println("paginación == 0 ok");
                            }
                        });
                    }

                    @Override
                    public void onSearch() {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                pagination.setDisable(true);
                                System.out.println("paginación deshabilitada");
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                pagination.setDisable(false);
                                System.out.println("paginación habilitada");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                pagination.setDisable(false);
                                System.out.println("paginación habilitada");
                            }
                        });
                    }
                });

                pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {

                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                        if (!pagination.isDisabled() && !oldValue.equals(newValue)) {
                            System.out.println("LANZAMOS BÚSQUEDA POR CAMBIO DE PÁGINA");
                            MateuUI.runInUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((ListView) viewNode.getView()).search();
                                }
                            });
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

                    AbstractForm f = field.getDataForm();

                    if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
                    else {
                        MateuUI.openView(new AbstractAddRecordDialog() {

                            @Override
                            public void addAndClean(Data data) {
                                DataStore ds = new DataStore(data);
                                Property p = viewNode.getDataStore().getProperty(field.getId());
                                DataStore donde = (DataStore) p.getValue();
                                List<DataStore> ll = aplanarDataStore((DataStore) p.getValue());
                                for (TreeItem<DataStore> i : t.getSelectionModel().getSelectedItems()) if (i.getValue() != null) {
                                    for (DataStore z : ll) if (z.get("__id").equals(i.getValue().get("__id"))) {
                                        donde = z;
                                    }
                                }
                                System.out.println("donde=" + donde);
                                if (donde != null) {
                                    donde.getObservableListProperty("_children").getValue().add(ds);
                                    if (t.getSelectionModel().getSelectedItem() != null) t.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<DataStore>(ds));
                                    else if (t.getRoot() != null) t.getRoot().getChildren().add(new TreeItem<DataStore>(ds));
                                    else t.setRoot(new TreeItem<DataStore>(ds));
                                } else {
                                    p.setValue(ds);
                                    t.setRoot(new TreeItem<DataStore>(ds));
                                }
                                setData(new Data());
                                set("__id", "" + UUID.randomUUID());
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
                    }

                }
            });
            toolBar.getItems().add(b = new Button("Remove"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    List<DataStore> borrar = new ArrayList<>();

                    Property p = viewNode.getDataStore().getProperty(field.getId());
                    List<DataStore> ll = aplanarDataStore((DataStore) p.getValue());
                    for (TreeItem<DataStore> i : new ArrayList<>(t.getSelectionModel().getSelectedItems())) if (i != null) {
                        if (i.getParent() != null) {
                            i.getParent().getValue().getObservableListProperty("_children").getValue().remove(i.getValue());
                            i.getParent().getChildren().remove(i);
                        }
                        else if (i.equals(t.getRoot())) {
                            p.setValue(null);
                            t.setRoot(null);
                        }
                    }
                }
            });
            toolBar.getItems().add(b = new Button("Duplicate"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    /*
                    List<DataStore> l = new ArrayList<>();
                    for (DataStore d : t.getSelectionModel().getSelectedItems()) {
                        l.add(new DataStore((Data) d.getData().clone()));
                    }
                    viewNode.getDataStore().getObservableListProperty(field.getId()).getValue().addAll(l);
                    */
                    System.out.println("****PENDIENTE****");
                }
            });

            toolBar.getItems().add(b = new Button("Up"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    /*
                    ObservableList<DataStore> l = viewNode.getDataStore().getObservableListProperty(field.getId()).getValue();
                    List<Integer> poses = new ArrayList<>();
                    int firstSelected = -1;
                    for (Integer pos : t.getSelectionModel().getSelectedIndices()) {
                        if (firstSelected == -1) firstSelected = pos;
                        poses.add(pos);
                    }
                    if (firstSelected > 0) {
                        t.getSelectionModel().clearSelection();
                        for (Integer pos : poses) {
                            DataStore x = l.get(pos);
                            l.remove(x);
                            l.add(pos - 1, x);
                        }
                        listenerSelectionActivo = false;
                        for (Integer pos : poses) t.getSelectionModel().select(pos - 1);
                        for (DataStore d : t.getItems()) d.set("_selected", false);
                        for (DataStore d : t.getSelectionModel().getSelectedItems()) if (d != null) d.set("_selected", true);
                        listenerSelectionActivo = true;
                    }
                    t.requestFocus();
                    */
                    System.out.println("****PENDIENTE****");
                }
            });

            toolBar.getItems().add(b = new Button("Down"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    /*
                    ObservableList<DataStore> l = viewNode.getDataStore().getObservableListProperty(field.getId()).getValue();
                    List<Integer> poses = new ArrayList<>();
                    int lastSelected = -1;
                    for (Integer pos : t.getSelectionModel().getSelectedIndices()) {
                        lastSelected = pos;
                        poses.add(pos);
                    }
                    if (lastSelected >= 0 && lastSelected < l.size() - 1) {
                        t.getSelectionModel().clearSelection();
                        Collections.reverse(poses);
                        for (Integer pos : poses) if (pos < l.size() - 1) {
                            DataStore x = l.get(pos);
                            l.remove(x);
                            l.add(pos + 1, x);
                        }
                        listenerSelectionActivo = false;
                        for (Integer pos : poses) t.getSelectionModel().select(pos + 1);
                        for (DataStore d : t.getItems()) d.set("_selected", false);
                        for (DataStore d : t.getSelectionModel().getSelectedItems()) if (d != null) d.set("_selected", true);
                        listenerSelectionActivo = true;
                    }
                    t.requestFocus();
                    */
                    System.out.println("****PENDIENTE****");
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

    private DataStore aDataStore(TreeItem<DataStore> root) {
        if (root == null) return null;
        else {
            DataStore data = root.getValue();
            for (TreeItem<DataStore> h : root.getChildren()) {
                data.getObservableListProperty("_children").getValue().add(aDataStore(h));
            }
            return data;
        }
    }

    private Data aData(TreeItem<Data> root) {
        if (root == null) return null;
        else {
            Data data = root.getValue();
            for (TreeItem<Data> h : root.getChildren()) {
                data.getList("_children").add(aData(h));
            }
            return data;
        }
    }


    private TreeTableView<DataStore> buildTable(GridField c) {
        t = new TreeTableView<>();

        //t.setBlendMode(BlendMode.GREEN);

        t.getColumns().addAll(buildColumns(t, c));

        String pname = c.getId();

        if (c.isUsedToSelect()) {
            pname += "_data";
        }

        Property p = viewNode.getDataStore().getProperty(pname);

        p.addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                DataStore d = null;
                System.out.println("property has changed to " + ((newValue != null)?newValue.getClass().getName():"null"));
                if (newValue instanceof DataStore) d = (DataStore) newValue;
                else if (newValue instanceof Data) d = new DataStore((Data) newValue);
                fill(t, d);
            }
        });

        fill(t, (DataStore)p.getValue());


        if (!c.isUsedToSelect() || c.isUsedToSelectMultipleValues()) t.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        else t.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        t.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<DataStore>>() {
            @Override
            public void onChanged(Change<? extends TreeItem<DataStore>> item) {
                if (listenerSelectionActivo) {
                    if (c.isUsedToSelect()) {
                        if (c.isUsedToSelectMultipleValues()) {
                            viewNode.getDataStore().getProperty(c.getId()).setValue(t.getSelectionModel().getSelectedItems());
                        } else {
                            viewNode.getDataStore().getProperty(c.getId()).setValue(t.getSelectionModel().getSelectedItem());
                        }
                    } else {
                        for (DataStore d : aplanarDataStore(((DataStore)p.getValue()))) if (d != null) d.set("_selected", false);
                        List<DataStore> ll = aplanarDataStore(((DataStore)p.getValue()));
                        for (TreeItem<DataStore> i : t.getSelectionModel().getSelectedItems()) if (i != null && i.getValue() != null) {
                            i.getValue().set("_selected", true);
                            for (DataStore z : ll) if (z.get("__id").equals(i.getValue().get("__id"))) {
                                z.set("_selected", true);
                            }
                        }
                    }
                }

            }
        });
        t.setEditable(false);

        if (field.isFullWidth() || field.getColumns().size() > 4) {
            t.setPrefHeight(400);
            t.setPrefWidth(900);
        } else {
            t.setPrefHeight(150);
            t.setPrefWidth(getWidth(field.getColumns()));
        }

        return t;
    }

    private List<DataStore> aplanarDataStore(DataStore data) {
        List<DataStore> l = new ArrayList<>();
        aplanarDataStore(l, data);
        return l;
    }

    private void aplanarDataStore(List<DataStore> l, DataStore data) {
        if (data != null) {
            l.add(data);
            for (DataStore d : data.getObservableListProperty("_children").getValue()) {
                aplanarDataStore(l, d);
            }
        }
    }

    private List<Data> aplanarData(Data data) {
        List<Data> l = new ArrayList<>();
        aplanarData(l, data);
        return l;
    }

    private void aplanarData(List<Data> l, Data data) {
        if (data != null) {
            l.add(data);
            for (Data d : data.getList("_children")) {
                aplanarData(l, d);
            }
        }
    }

    private Data cloneDataTree(Data data) {
        if (data == null) return null;
        else {
            return new Data(data);
        }
    }

    private void logTree(Data data) {
        System.out.println("tree=");
        log("", data);
        System.out.println("end of tree.");
    }

    private void log(String prefijo, Data data) {
        System.out.println(prefijo + data + " (_selected=" + ((data != null)?data.get("_selected"):"---") + ")");
        for (Data d : data.getList("_children")) {
            log(prefijo + "  ", d);
        }
    }

    private List<Data> aplanarDatastores(TreeItem<Data> root) {
        List<Data> l = new ArrayList<>();
        aplanarDatastores(l, root);
        return l;
    }

    private void aplanarDatastores(List<Data> l, TreeItem<Data> root) {
        if (root != null) {
            l.add(root.getValue());
            for (TreeItem<Data> i : root.getChildren()) {
                aplanarDatastores(l, i);
            }
        }
    }

    private void fill(TreeTableView treeView, DataStore data) {

        TreeItem root = createTreeItems(data);

        treeView.setShowRoot(true);

        treeView.setRoot(root);

    }

    private TreeItem createTreeItems(DataStore data) {

        TreeItem<DataStore> root = new TreeItem<DataStore>(data);

        root.setExpanded(true);

        if (data != null) {
            for (DataStore d : data.getObservableListProperty("_children").getValue()) {
                root.getChildren().add(createTreeItems(d));
            }
        }

        return root;

    }

    private double getWidth(List<AbstractColumn> columns) {
        double w = 0;
        for (AbstractColumn c : columns) w += c.getWidth();
        if (field.isExpandable()) w += 62;
        return w;
    }

    private List<TreeTableColumn<DataStore, ?>> buildColumns(TreeTableView<DataStore> t, GridField g) {
        List<TreeTableColumn<DataStore, ?>> l = new ArrayList<>();



        //Creating a column
        TreeTableColumn<DataStore,String> treecolumn = new TreeTableColumn<>("Column");
        treecolumn.setPrefWidth(150);

        //Defining cell content
        treecolumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<DataStore, String> p) ->
                new ReadOnlyStringWrapper((p.getValue() != null && p.getValue().getValue() != null)?p.getValue().getValue().toString():null));

        l.add(treecolumn);


        for (AbstractColumn c : g.getColumns()) {

            TreeTableColumn col = new TreeTableColumn(c.getLabel());

            if (c instanceof DataColumn) {
                col.setCellValueFactory(new PropertyValueFactory<Object>(c.getId()));
                col.setCellFactory(MateuDataColumnTreeTableCell.<DataStore, Object>forTreeTableColumn(new StringConverter<Object>() {
                    @Override
                    public String toString(Object object) {
                        return (object == null) ? null : ((object instanceof DataStore)?"" + ((DataStore)object).get("_text"):"" + object);
                    }

                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                }, c.getId(), (ActionOnRow) c, new CellStyleGenerator() {
                    @Override
                    public String getStyle(Object value) {
                        return (value != null && value instanceof DataStore)?((DataStore)value).get("_css"):null;
                    }

                    @Override
                    public boolean isContentShown() {
                        return true;
                    }
                }));
            } else if (c instanceof LinkColumn) {
                col.setCellValueFactory(new PropertyValueFactory<Object>(c.getId()));
                col.setCellFactory(MateuLinkTreeTableCell.<DataStore, Object>forTreeTableColumn(new StringConverter<Object>() {
                    @Override
                    public String toString(Object object) {
                        if (((LinkColumn) c).getText() != null) return ((LinkColumn) c).getText();
                        return (object == null)?null:"" + object;
                    }

                    @Override
                    public Object fromString(String string) {
                        return string;
                    }
                }, (ActionOnRow) c, new CellStyleGenerator() {
                    @Override
                    public String getStyle(Object value) {
                        return (value != null && value instanceof DataStore)?((DataStore)value).get("_css"):null;
                    }

                    @Override
                    public boolean isContentShown() {
                        return true;
                    }
                }));
            } else {
                col.setCellValueFactory(new PropertyValueFactory<Object>(c.getId()));
                col.setCellFactory(column -> {
                    return new TreeTableCell<Data, Object>() {

                        @Override
                        protected void updateItem(Object item, boolean empty) {
                            super.updateItem(item, empty);

                            getStyleClass().clear();
                            getStyleClass().addAll("cell", "indexed-cell", "table-cell", "table-column");

                            if (item == null || empty) {
                                setText(null);
                                setStyle("");
                                setGraphic(null);
                            } else {
                                // Format date.
                                setText("" + item);
                                setGraphic(null);
                                setStyle("");
                                if (c.getStyleGenerator() != null) {
                                    String s = c.getStyleGenerator().getStyle(item);
                                    if (s != null) for (String x : s.split(" ")) if (!Strings.isNullOrEmpty(x)) getStyleClass().add(x);
                                    if (s == null) {
                                    } else if (s.contains("pending")) {
                                        setText(null);
                                        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CIRCLE_THIN));
                                        setStyle("-fx-alignment: center;");
                                    } else if (s.contains("done")) {
                                        setText(null);
                                        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
                                        setStyle("-fx-alignment: center;");
                                    } else if (s.contains("cancelled")) {
                                        setText(null);
                                        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
                                        setStyle("-fx-alignment: center;");
                                    } else if (s.contains("expired")) {
                                        setText(null);
                                        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT));
                                        setStyle("-fx-alignment: center;");
                                    }
                                    if (!c.getStyleGenerator().isContentShown()) setText("");
                                }
                            }
                        }
                    };
                });
            }

            col.setPrefWidth(c.getWidth());

            l.add(col);
        }

        if (field.isExpandable()) {
            TreeTableColumn col = new TreeTableColumn("Edit");

            col.setCellFactory(MateuLinkTreeTableCell.<DataStore, Object>forTreeTableColumn(new StringConverter<Object>() {
                @Override
                public String toString(Object object) {
                    return "Edit";
                }

                @Override
                public Object fromString(String string) {
                    return string;
                }
            }, new ActionOnRow() {
                @Override
                public boolean isModifierPressed() {
                    return false;
                }

                @Override
                public void run(Data data) {
                    AbstractForm f = g.getDataForm();

                    if (f == null) MateuUI.alert("getDataForm() methd must return some value in GridField");
                    else {


                        TreeItem item = findItem(t.getRoot(), data);
                        MateuUI.openView(new AbstractListEditorDialog() {

                            @Override
                            public Data getData(int pos) {
                                return ((DataStore)item.getValue()).getData();
                            }

                            @Override
                            public void setData(int pos, Data data) {
                                ((DataStore)item.getValue()).setData(data);
                            }

                            @Override
                            public int getListSize() {
                                return (item.getParent() != null)?item.getParent().getChildren().size():0;
                            }

                            @Override
                            public int getInitialPos() {
                                return (item.getParent() != null)?item.getParent().getChildren().indexOf(item):0;
                            }

                            @Override
                            public Data initializeData() {
                                return data;
                            }

                            @Override
                            public void onOk(Data data) {
                                item.setValue(data);
                            }

                            @Override
                            public String getTitle() {
                                return "Edit record";
                            }

                            @Override
                            public AbstractForm createForm() {
                                return g.getDataForm();
                            }

                            @Override
                            public void build() {

                            }
                        });
                    }
                }
            }, new CellStyleGenerator() {
                @Override
                public String getStyle(Object value) {
                    return null;
                }

                @Override
                public boolean isContentShown() {
                    return true;
                }
            }));

            col.setPrefWidth(60);

            l.add(col);
        }

        return l;
    }

    private TreeItem findItem(TreeItem<DataStore> root, Data data) {
        if (root == null || root.getValue() == null || root.getValue().get("__id") == null || data == null) return null;
        if (root.getValue().get("__id").equals(data.get("__id"))) return root;
        else {
            TreeItem<DataStore> item = null;
            for (TreeItem<DataStore> i : root.getChildren()) {
                if (i.getValue().get("__id").equals(data.get("__id"))) {
                    item = i;
                    break;
                } else {
                    item = findItem(i, data);
                    if (item != null) break;
                }
            }
            return item;
        }
    }

    public TreeTableView<DataStore> getTableView() {
        return tableView;
    }
}
