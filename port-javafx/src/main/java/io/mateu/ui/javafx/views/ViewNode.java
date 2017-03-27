package io.mateu.ui.javafx.views;

import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.*;
import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.views.ListView;
import io.mateu.ui.core.shared.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.javafx.JFXHelper;
import io.mateu.ui.javafx.JavafxPort;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.data.ViewNodeDataStore;
import io.mateu.ui.javafx.views.components.GridNode;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.*;
import javafx.util.Callback;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


/**
 * Created by miguel on 9/8/16.
 */
public class ViewNode extends StackPane {

    private static final Image REQUIRED_IMAGE = new Image(GraphicValidationDecoration.class.getResource("/impl/org/controlsfx/control/validation/required-indicator.png").toExternalForm());
    private MaskerPane maskerPane;


    private AbstractView view;
    private DataStore dataStore;
    private BorderPane bp;
    private ProgressIndicator progressIndicator;
    private Pane componentsCotainer;
    private boolean minsFixed;
    private Stage moreFiltersDialog;
    private Node firstField = null;
    private Dialog dmf;
    private ValidationSupport validationSupport;

    public Node getFirstField() {
        return firstField;
    }

    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }

    public ViewNode() {
        setStyle("-fx-background-color: #eaeaff;");
    }

    public ViewNode(AbstractView view) {
        this();
        this.view = view;
        dataStore = new ViewNodeDataStore(this);
        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                getDataStore().setData(newData);
            }

            @Override
            public void setted(String k, Object v) {
                getDataStore().set(k, v);
            }
        });
        build();
        getChildren().add(maskerPane = new MaskerPane());
        maskerPane.setVisible(false);
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public AbstractView getView() {
        return view;
    }

    public void startWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                maskerPane.setVisible(true);
                //getChildren().add(progressIndicator = new ProgressIndicator());
            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                maskerPane.setVisible(false);
                //getChildren().remove(progressIndicator);
            }
        });
    }

    public void build() {
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        setStyle("-fx-background-color: white;");

        bp = new BorderPane();
        bp.setTop(createToolBar(view.getActions()));
        ScrollPane sp = new ScrollPane(componentsCotainer = new VBox());
        sp.getStyleClass().add("mateu-view-scroll");
        bp.setCenter(sp);
        componentsCotainer.getStyleClass().add("mateu-view");
        componentsCotainer.setPadding(new Insets(10, 20, 10, 20));
        if (false) addEventHandler(Event.ANY, e -> {
            System.out.println("caught " + e.getEventType().getName());
        });

        build(sp, componentsCotainer, view.getForm(), 0);

        addListeners();

        getChildren().add(bp);
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

        if (view instanceof ListView) {
            ListView lv = (ListView) view;
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

    private Node createToolBar(List<AbstractAction> actions) {
        ToolBar toolBar = new ToolBar();

        if (view instanceof ListView) {
            ListView lv = (ListView) view;

            validationSupport = new ValidationSupport();

            Pane contenedor = new HBox();

            int pos = 0;
            int posField = 0;
            List<Component> cs = lv.getForm().getComponentsSequence();
            for (Component c : cs) {
                addComponent(validationSupport,null, null, contenedor, c, false, true);

                if (c instanceof AbstractField) posField++;
                if (posField >= lv.getMaxFieldsInHeader()) break;
            }

            if (contenedor.getChildren().size() > 0) {
                toolBar.getItems().add(contenedor);

                int numFields = 0;
                for (Component c : cs) {
                    if (c instanceof AbstractField) numFields++;
                }

                if (numFields > lv.getMaxFieldsInHeader()) {
                    Button bmf;
                    toolBar.getItems().add(bmf = new Button("+"));
                    bmf.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            dmf.showAndWait();
                        }
                    });

                    addMoreFiltersDialog();
                }

                if (false && numFields > lv.getMaxFieldsInHeader()) {

                    moreFiltersDialog = new Stage();
                    moreFiltersDialog.setWidth(600);
                    moreFiltersDialog.setHeight(600);
                    moreFiltersDialog.setTitle("More filters");
                    moreFiltersDialog.initOwner(JavafxPort.mainStage);

                    //primaryStage.setFullScreen(true);


                    BorderPane bpmf = new BorderPane();

                    Pane moreFiltersComponentsCotainer;
                    ScrollPane sp = new ScrollPane(moreFiltersComponentsCotainer = new Pane());
                    sp.getStyleClass().add("mateu-view-scroll");
                    bpmf.setCenter(sp);
                    moreFiltersComponentsCotainer.getStyleClass().add("mateu-view");

                    StackPane stack = new StackPane(bpmf);

                    stack.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

                    stack.setStyle("-fx-background-color: white;");

                    moreFiltersDialog.setScene(new Scene(new StackPane(stack)));



                    build(validationSupport, sp, moreFiltersComponentsCotainer, lv.getForm(), lv.getMaxFieldsInHeader(), true);
                    Button bmf;
                    toolBar.getItems().add(bmf = new Button("More filters..."));
                    bmf.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            moreFiltersDialog.showAndWait();
                        }
                    });
                }

            }
        }

        /*
        if (view instanceof AbstractListView) {
            List<Component> cs = view.getForm().getComponentsSequence();
            int pos = 0;
            for (Component c : cs) {
                addComponent(null, toolBar, c, false);
            }
        } else {

        }
        */

        for (AbstractAction a : actions) {
            Button b;
            toolBar.getItems().add(b = new Button(a.getName()));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    a.run();
                }
            });
        }
        {
            Button b;
            toolBar.getItems().add(b = new Button("Data"));
            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //System.out.println(getDataStore().toString());
                    MateuUI.openView(new AbstractDialog() {
                        @Override
                        public void onOk(Data data) {

                        }

                        @Override
                        public String getTitle() {
                            return "Data";
                        }

                        @Override
                        public Data initializeData() {
                            Data d = super.initializeData();
                            d.set("data", view.getForm().getData());
                            return d;
                        }

                        @Override
                        public AbstractForm createForm() {
                            return new ViewForm(this).setLastFieldMaximized(true).add(new DataViewerField("data"));
                        }
                    });
                }
            });
        }
        return toolBar;
    }

    private void addMoreFiltersDialog() {
        // Create the custom dialog.
        Dialog<javafx.util.Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("More filters");

// Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

// Set the button types.
        ButtonType loginButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.


// Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        //loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).

        Pane pane;
        ScrollPane sp = new ScrollPane(pane = new Pane());
        sp.getStyleClass().add("mateu-view-scroll");
        pane.getStyleClass().add("mateu-view");
        if (false) addEventHandler(Event.ANY, e -> {
            System.out.println("caught " + e.getEventType().getName());
        });

        //build(sp, pane, view.getForm(), ((AbstractListView) view).getMaxFieldsInHeader());
        ListView lv = (ListView) view;
        build(validationSupport, sp, pane, lv.getForm(), lv.getMaxFieldsInHeader(), true);

        dialog.getDialogPane().setContent(sp);
        dialog.getDialogPane().setMaxWidth(600);
        dialog.getDialogPane().setMinHeight(200);
        dialog.setResizable(true);

// Request focus on the username field by default.
        //Platform.runLater(() -> name.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.


        loginButton.addEventFilter(ActionEvent.ACTION, ae -> {
            dialog.close();
            ((ListView) view).search();
        });

        //Optional<javafx.util.Pair<String, String>> result = dialog.showAndWait();

        dmf = dialog;
    }

    private void build(ScrollPane scrollPane, Pane overallContainer, FieldContainer form, int fromField) {
        build(new ValidationSupport(), scrollPane, overallContainer, form, fromField, false);
    }

    private void build(ValidationSupport validationSupport, ScrollPane scrollPane, Pane overallContainer, FieldContainer form, int fromField, boolean inToolBar) {

        HBox badgesPane;
        overallContainer.getChildren().add(badgesPane = new HBox(2));

        Property<ObservableList<DataStore>> pb = getDataStore().getObservableListProperty("_badges");
        ChangeListener<ObservableList<DataStore>> pl;
        pb.addListener(pl = new ChangeListener<ObservableList<DataStore>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<DataStore>> observable, ObservableList<DataStore> oldValue, ObservableList<DataStore> newValue) {
                badgesPane.getChildren().clear();
                if (newValue != null) for (DataStore x : newValue) {
                    badgesPane.getChildren().add(new Label(x.get("_value")));
                }
            }
        });
        pl.changed(null, null, pb.getValue());


        List<Pane> panels = new ArrayList<>();
        panels.add(new VBox(2));

        Node lastNode = null;
        if (view instanceof ListView && !inToolBar) {
            ListView lv = (ListView) view;
            lastNode = addComponent(validationSupport, scrollPane, overallContainer, panels.get(panels.size() - 1), new GridField("_data", lv.getColumns()).setPaginated(true).setExpandable(false), true, false, false);
        } else {
            int pos = 0;
            int posField = 0;
            List<Component> cs = form.getComponentsSequence();
            for (Component c : cs) {
                Node n = null;
                if (!(c instanceof AbstractField) || posField >= fromField) {
                    /*
                    if (c instanceof ColumnStart) panels.add(new VBox(2));
                    else if (c instanceof RowStart) panels.add(new FlowPane(5, 2));
                    else if (c instanceof ColumnEnd) panels.remove(panels.size() - 1);
                    else if (c instanceof RowEnd) panels.remove(panels.size() - 1);
                    else {
                    */
                        if (posField == 0 || (c instanceof  AbstractField && ((AbstractField)c).isBeginingOfLine())) {
                            FlowPane fp;
                            panels.add(fp = new FlowPane(5, 2));
                            fp.setPrefWidth(5000);
                        }
                        n = addComponent(validationSupport, scrollPane, overallContainer, panels.get(panels.size() - 1), c, form.isLastFieldMaximized() && pos++ == cs.size() - 1, false);
                    //}
                }
                if (n != null) lastNode = n;
                if (c instanceof AbstractField) posField++;
            }
        }

        overallContainer.getChildren().addAll(panels);

        if (!form.isLastFieldMaximized()) {
            Node finalLastNode = lastNode;
            scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {

                Node lastNodex = finalLastNode;

                @Override
                public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                    fixMins(overallContainer, lastNodex);
                    System.out.println("bounds changed to " + newValue.toString());
                    overallContainer.setPrefHeight(newValue.getHeight());
                    overallContainer.setPrefWidth(newValue.getWidth());

                }
            });
        }
    }

    private void fixMins(Pane componentsCotainer, Node lastNode) {

        if ((true || !minsFixed) && lastNode != null && lastNode instanceof Region) {
            Region r = (Region) lastNode;
            componentsCotainer.setMinWidth(r.getBoundsInParent().getWidth() + r.getLocalToSceneTransform().transform(0, 0).getX() - componentsCotainer.getLocalToSceneTransform().transform(0, 0).getX());
            componentsCotainer.setMinHeight(r.getBoundsInParent().getHeight() + r.getLocalToSceneTransform().transform(0, 0).getY() - componentsCotainer.getLocalToSceneTransform().transform(0, 0).getY());
            System.out.println("mins=" + componentsCotainer.getMinWidth() + "," + componentsCotainer.getMinHeight());
        }

        minsFixed = true;
    }

    private Node addComponent(ValidationSupport validationSupport, ScrollPane scrollPane, Pane overallContainer, Pane container, Component c, boolean maximize, boolean inToolBar) {
        return addComponent(validationSupport, scrollPane, overallContainer, container, c, maximize, inToolBar, true);
    }

    private Node addComponent(ValidationSupport validationSupport, ScrollPane scrollPane, Pane overallContainer, Pane container, Component c, boolean maximize, boolean inToolBar, boolean showLabels) {
        //TODO: resolver con inyección de dependencias

        Node n = null;

        /*
                Pane donde = container;
        if (donde instanceof VBox) {
            container = donde;
            ((VBox) donde).getChildren().add(donde = new FlowPane());
            donde.setPrefWidth(5000);
        }
         */
        Pane donde = container;
        if (donde instanceof FlowPane) {
            container = donde;
            ((FlowPane) donde).getChildren().add(donde = new VBox());
            //donde.setPrefWidth(5000);
        }


        if (c instanceof Tabs) {

            Tabs tabs = (Tabs) c;

            TabPane tp;
            n = tp = new TabPane();

            for (Tab t : tabs.getTabs()) {

                javafx.scene.control.Tab tc = new javafx.scene.control.Tab(t.getCaption());
                tc.setClosable(false);
                tp.getTabs().add(tc);


                ScrollPane sp = new ScrollPane(componentsCotainer = new VBox());
                sp.getStyleClass().add("mateu-view-scroll");
                componentsCotainer.getStyleClass().add("mateu-view");
                componentsCotainer.setPadding(new Insets(10, 20, 10, 20));


                tc.setContent(sp);

                build(validationSupport, sp, componentsCotainer, t, 0, false);

            }

            donde.getChildren().add(tp);

        } else if (c instanceof AbstractField) {

            Control control = null;

            if (showLabels) {
                if (((AbstractField) c).getLabel() != null) {

                    /*
                    TextFlow flow = new TextFlow();

                    Text text1=new Text(((AbstractField) c).getLabel().getText());
                    text1.setStyle("-fx-font-weight: regular");

                    flow.getChildren().add(text1);

                    if (((AbstractField)c).isRequired()) {
                        Text text2=new Text("(*)");
                        text2.setStyle("-fx-font-weight: bold");

                        flow.getChildren().add(text2);
                    }

                    donde.getChildren().add(flow);
                    if (!inToolBar) flow.setStyle("-fx-min-width: 200px;-fx-alignment: baseline-right;");
                    else flow.setStyle("-fx-alignment: baseline-right;");
                       */


                    javafx.scene.control.Label l;
                    donde.getChildren().add(l = new javafx.scene.control.Label(((AbstractField) c).getLabel().getText()));
                    if (!inToolBar) l.setStyle("-fx-min-width: 200px;-fx-alignment: baseline-left;");
                    else l.setStyle("-fx-alignment: baseline-left;");

                    /*
                    if (((AbstractField)c).isRequired()) {
                        //l.setText(l.getText() + " (*)");
                        Node requiredDecoration = new ImageView( REQUIRED_IMAGE );
                        Decorator.addDecoration( l, new GraphicDecoration( requiredDecoration, Pos.TOP_LEFT ));
                    }
                    */

                }
            }

            if (c instanceof AutocompleteField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                cmb.getItems().addAll(((AutocompleteField)c).getValues());
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<javafx.scene.control.ListView<Pair>, ListCell<Pair>>() {
                    @Override public ListCell<Pair> call(javafx.scene.control.ListView<Pair> p) {
                        return new ListCell<Pair>() {

                            @Override protected void updateItem(Pair item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item == null || empty) {
                                    setText(null);
                                } else {
                                    setText(item.getText());
                                }
                            }
                        };
                    }
                });

                cmb.valueProperty().bindBidirectional(dataStore.getPairProperty(((AbstractField)c).getId()));

                n = control = cmb;


                ObservableList<Pair> data = cmb.getItems();

                cmb.setEditable(true);
                cmb.getEditor().focusedProperty().addListener(observable -> {
                    if (cmb.getSelectionModel().getSelectedIndex() < 0) {
                        cmb.getEditor().setText(null);
                    }
                });
                cmb.addEventHandler(KeyEvent.KEY_PRESSED, t -> cmb.hide());
                cmb.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

                    private boolean moveCaretToPos = false;
                    private int caretPos;

                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode() == KeyCode.UP) {
                            caretPos = -1;
                            moveCaret((cmb.getEditor().getText() != null)?cmb.getEditor().getText().length():0);
                            return;
                        } else if (event.getCode() == KeyCode.DOWN) {
                            if (!cmb.isShowing()) {
                                cmb.show();
                            }
                            caretPos = -1;
                            moveCaret((cmb.getEditor().getText() != null)?cmb.getEditor().getText().length():0);
                            return;
                        } else if (event.getCode() == KeyCode.BACK_SPACE) {
                            moveCaretToPos = true;
                            caretPos = cmb.getEditor().getCaretPosition();
                        } else if (event.getCode() == KeyCode.DELETE) {
                            moveCaretToPos = true;
                            caretPos = cmb.getEditor().getCaretPosition();
                        } else if (event.getCode() == KeyCode.ENTER) {
                            return;
                        }

                        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode().equals(KeyCode.SHIFT) || event.getCode().equals(KeyCode.CONTROL)
                                || event.isControlDown() || event.getCode() == KeyCode.HOME
                                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
                            return;
                        }

                        ObservableList<Pair> list = FXCollections.observableArrayList();
                        for (Pair aData : data) {
                            if (aData != null && (cmb.getEditor().getText() == null  || (aData.getText() != null
                                    && aData.getText().toLowerCase().contains(cmb.getEditor().getText().toLowerCase())))) {
                                list.add(aData);
                            }
                        }
                        String t = cmb.getEditor().getText();

                        cmb.setItems(list);
                        cmb.getEditor().setText(t);
                        if (!moveCaretToPos) {
                            caretPos = -1;
                        }
                        moveCaret((t != null)?t.length():0);
                        if (!list.isEmpty()) {
                            cmb.show();
                        }
                    }

                    private void moveCaret(int textLength) {
                        if (caretPos == -1) {
                            cmb.getEditor().positionCaret(textLength);
                        } else {
                            cmb.getEditor().positionCaret(caretPos);
                        }
                        moveCaretToPos = false;
                    }
                });

                if (firstField == null) {
                    firstField = cmb.getEditor();
                }


            } else if (c instanceof CalendarField || c instanceof DateField) {
                DatePicker tf;
                n = control = tf = new DatePicker();
                tf.valueProperty().bindBidirectional(dataStore.getLocalDateProperty(((AbstractField) c).getId()));
            } else if (c instanceof DateTimeField) {
                TextField tf;
                n = control = tf = new TextField();

                String pattern = "yyyy-MM-dd HH:mm";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                tf.setTooltip(new Tooltip(pattern));

                tf.textProperty().bindBidirectional(dataStore.getLocalDateTimeProperty(((AbstractField) c).getId()), new LocalDateTimeStringConverter(formatter, null));
            } else if (c instanceof CheckBoxField) {
                CheckBox tf;
                n = control = tf = new CheckBox();
                String text = ((CheckBoxField) c).getText();
                if (text != null) tf.setText(text);
                tf.selectedProperty().bindBidirectional(dataStore.getBooleanProperty(((AbstractField) c).getId()));
            } else if (c instanceof CheckBoxListField) {

                Pane h;
                n = h = new HBox(6);

                final Map<Pair, CheckBox> tfs = new HashMap<>();

                Property<PairList> prop = dataStore.getPairListProperty(((AbstractField) c).getId());

                for (final Pair p : ((CheckBoxListField)c).getValues()) {
                    CheckBox tf;
                    h.getChildren().add(tf = new CheckBox());
                    tf.setText(p.getText());
                    tfs.put(p, tf);

                    tf.setSelected(prop.getValue() != null && prop.getValue().getValues().contains(p));

                    tf.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            PairList l = prop.getValue();
                            if (l == null) l = new PairList();
                            if (newValue) l.getValues().add(p);
                            else l.getValues().remove(p);
                            prop.setValue((l.getValues().size() > 0)?new PairList(l):null);
                        }
                    });

                }

                dataStore.getPairListProperty(((AbstractField)c).getId()).addListener(new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        for (Pair p : tfs.keySet()) tfs.get(p).setSelected(newValue != null && newValue.getValues().contains(p));
                    }
                });

            } else if (c instanceof ComboBoxField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                cmb.getItems().add(new Pair(null, null));
                cmb.getItems().addAll(((ComboBoxField)c).getValues());
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<javafx.scene.control.ListView<Pair>, ListCell<Pair>>() {
                    @Override public ListCell<Pair> call(javafx.scene.control.ListView<Pair> p) {
                        return new ListCell<Pair>() {

                            @Override protected void updateItem(Pair item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item == null || empty) {
                                    setText(null);
                                } else {
                                    setText(item.getText());
                                }
                            }
                        };
                    }
                });

                cmb.valueProperty().bindBidirectional(dataStore.getPairProperty(((AbstractField)c).getId()));

                n = control = cmb;

            } else if (c instanceof DataViewerField) {

                TreeTableView<Pair> t = new TreeTableView<>();
                t.setShowRoot(true);

//                row.getStyleClass().add("highlightedRow");
//                  row.getStyleClass().removeAll(Collections.singleton("highlightedRow"));

                t.setRowFactory(new Callback<TreeTableView<Pair>, TreeTableRow<Pair>>() {
                    @Override
                    public TreeTableRow<Pair> call(TreeTableView<Pair> param) {
                        return new TreeTableRow<Pair>() {
                            @Override
                            protected void updateItem(Pair item, boolean empty) {
                                super.updateItem(item, empty);
                                boolean leaf = !empty;
                                leaf = leaf && item != null && item.get("_leaf") != null;
                                if (!this.getStyleClass().contains("dataviewer")) this.getStyleClass().add("dataviewer");
                                if (leaf) {
                                    this.getStyleClass().add("leaf");
                                } else {
                                    this.getStyleClass().remove("leaf");
                                }
                            }
                        };
                    }
                });


                ObservableList<Pair> m = FXCollections.observableArrayList();

                TreeItem<Pair> root = buildTree(((AbstractField)c).getId(), dataStore.get(((AbstractField)c).getId()));
                t.setRoot(root);

                TreeTableColumn<Pair,String> nameCol = new TreeTableColumn<>("Name");
                nameCol.setPrefWidth(250);
                nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Pair, String> p) ->
                        new ReadOnlyStringWrapper("" + p.getValue().getValue().getText()));
                TreeTableColumn<Pair,String> valueCol = new TreeTableColumn<>("Value");
                valueCol.setPrefWidth(250);
                valueCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<Pair, String> p) ->
                        new ReadOnlyStringWrapper("" + p.getValue().getValue().getValue()));


                t.getColumns().setAll(nameCol, valueCol);

                dataStore.getProperty(((AbstractField)c).getId()).addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                        TreeItem<Pair> root = buildTree(((AbstractField)c).getId(), newValue);
                        t.setRoot(root);
                    }
                });

                //cmb.valueProperty().bindBidirectional(dataStore.getPairProperty(((AbstractField)c).getId()));

                n = control = t;

            } else if (c instanceof DoubleField) {
                TextField tf;
                n = control = tf = new TextField() {
                    public void replaceText(int start, int end, String text) {
                        //System.out.println("replaceText(" + start + "," + end + "," + text + ")");
                        String s = getText();
                        if (s == null) s = "";
                        s = s.substring(0, start) + text + s.substring(end);
                        if (s.matches("[0-9]*\\.*[0-9]*")) {
                            super.replaceText(start, end, text);
                        } else {
                            //Aga2.getHelper().display("Campo numérico. Solo acepta valores numéricos. Acepta decimales");
                        }
                    };
                };
                tf.setAlignment(Pos.BASELINE_RIGHT);

                /*
                    DecimalFormat format = new DecimalFormat( "#.0" );
                    tf.setTextFormatter( new TextFormatter<>(converter ->
                    {
                        if ( converter.getControlNewText().isEmpty() )
                        {
                            return converter;
                        }

                        ParsePosition parsePosition = new ParsePosition( 0 );
                        Object object = format.parse( converter.getControlNewText(), parsePosition );

                        if ( object == null || parsePosition.getIndex() < converter.getControlNewText().length() )
                        {
                            return null;
                        }
                        else
                        {
                            return converter;
                        }
                    }));
                    */
                    tf.textProperty().bindBidirectional(dataStore.getDoubleProperty(((DoubleField) c).getId()), new StringConverter<Double>() {
                        @Override
                        public String toString(Double object) {
                            if (object == null) return null;
                            else return "" + object;
                        }

                        @Override
                        public Double fromString(String string) {
                            Double d = null;
                            try {
                                d = new Double(string);
                            } catch (Exception e) {

                            }
                            return d;
                        }
                    });

                n = tf;

            } else if (c instanceof FileField) {
                //////////////////////

                HBox v;
                n = v = new HBox();

                final HBox h0;
                v.getChildren().add(h0 = new HBox());

                HBox h1;
                v.getChildren().add(h1 = new HBox());

                dataStore.getFileLocatorProperty(((AbstractField)c).getId()).addListener(new ChangeListener<FileLocator>() {

                    @Override
                    public void changed(ObservableValue<? extends FileLocator> arg0, FileLocator oldValue, FileLocator newValue) {
                        h0.getChildren().clear();
                        if (newValue != null && newValue.getUrl() != null) {
                            Hyperlink l = new Hyperlink(newValue.getFileName());
                            l.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    try {
                                        Desktop.getDesktop().open(new File(newValue.getUrl()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        MateuUI.alert("" + e.getClass().getName() + ":" + e.getMessage());
                                    }
                                }
                            });
                            h0.getChildren().add(l);
                            Button b;
                            h0.getChildren().add(b = new Button("X"));
                            b.setOnAction(new EventHandler<ActionEvent>() {

                                @Override
                                public void handle(ActionEvent paramT) {
                                    dataStore.getFileLocatorProperty(((AbstractField)c).getId()).setValue(null);
                                }
                            });

                            v.setSpacing(5);
                        } else {
                            v.setSpacing(0);
                        }
                    }
                });;

                Button b;
                h1.getChildren().add(b = new Button("Choose file..."));
                b.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent paramT) {
                        Stage s = new Stage();
                        s.setWidth(500);
                        s.setHeight(300);
                        s.setTitle("Choose a file to upload");

                        FileChooser fc = new FileChooser();
                        File f = fc.showOpenDialog(s);
                        if (f != null) try {
                            MateuUI.getBaseService().upload(f.getName(), JFXHelper.read(new FileInputStream(f)), ((FileField)c).isTemporary(), new io.mateu.ui.core.client.app.Callback<FileLocator>() {
                                @Override
                                public void onSuccess(FileLocator result) {
                                    dataStore.getFileLocatorProperty(((AbstractField)c).getId()).setValue(result);
                                }
                            });
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            MateuUI.alert("" + e.getClass().getName() + ":" + e.getMessage());
                        }
                    }
                });


            } else if (c instanceof GridField) {
                n = new GridNode(this, (GridField) c);
            } else if (c instanceof HtmlField) {
                WebView tf = new WebView();
                dataStore.getStringProperty(((AbstractField) c).getId()).addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        String u = newValue;
                        if (u != null && !"".equals(u)) {
                            tf.getEngine().loadContent(u);
                        } else {
                            tf.getEngine().loadContent("");
                        }
                    }
                });
                String u = dataStore.getStringProperty(((AbstractField) c).getId()).getValue();
                if (u != null && !"".equals(u)) {
                    tf.getEngine().loadContent(u);
                }
                StackPane sp = new StackPane();
                n = sp;
                sp.getChildren().add(tf);
            } else if (c instanceof IntegerField) {
                TextField tf;
                n = control = tf = new TextField() {
                    public void replaceText(int start, int end, String text) {
                        //System.out.println("replaceText(" + start + "," + end + "," + text + ")");
                        String s = getText();
                        if (s == null) s = "";
                        s = s.substring(0, start) + text + s.substring(end);
                        if (s.matches("[0-9]*")) {
                            super.replaceText(start, end, text);
                        } else {
                            //Aga2.getHelper().display("Campo numérico entero. Solo acepta valores enteros, sin decimales");
                        }
                    };
                };
                tf.setAlignment(Pos.BASELINE_RIGHT);

                    tf.textProperty().bindBidirectional(dataStore.getIntegerProperty(((IntegerField) c).getId()), new StringConverter<Integer>() {
                        @Override
                        public String toString(Integer object) {
                            if (object == null) return null;
                            else return "" + object;
                        }

                        @Override
                        public Integer fromString(String string) {
                            Integer d = null;
                            try {
                                d = new Integer(string);
                            } catch (Exception e) {

                            }
                            return d;
                        }
                    });
                 n = tf;

            } else if (c instanceof LinkField) {
                Hyperlink tf;
                n = tf = new Hyperlink();
                tf.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent arg0) {
                        ((LinkField) c).run();
                    }
                });

                String text = ((LinkField) c).getText();
                if (text != null) {
                    tf.setText(text);
                } else {
                    tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                }
            } else if (c instanceof ListSelectionField) {

                ListSelectionField lsf = (ListSelectionField) c;

                Pane h;
                n = h = new HBox(6);

                Label l;
                h.getChildren().add(l = new Label());
                Button b;
                h.getChildren().add(b = new Button("+"));

                Property<PairList> prop = dataStore.getPairListProperty(((AbstractField) c).getId());

                ChangeListener<PairList> cl;
                prop.addListener(cl = new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        StringBuffer s = new StringBuffer();
                        boolean empty = true;
                        if (newValue != null) {
                            for (Pair p : newValue.getValues()) {
                                if (empty) empty = false;
                                else s.append(", ");
                                s.append(p.getText());
                            }
                        }
                        l.setText((empty)?"empty (no item selected)":s.toString());
                    }
                });
                cl.changed(null, null, null);

                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        ListSelectionView<Pair> v = new ListSelectionView<>();
                        ObservableList<Pair> sourceValues = FXCollections.observableArrayList(lsf.getValues());
                        ObservableList<Pair> targetValues = FXCollections.observableArrayList(prop.getValue().getValues());
                        sourceValues.removeAll(targetValues);
                        v.setSourceItems(sourceValues);
                        v.setTargetItems(targetValues);

                        Dialog d = new Dialog();
                        d.setTitle("Select values from list");
                        d.setResizable(true);
                        d.getDialogPane().setContent(v);

                        ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                        d.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                        Optional<ButtonType> result = d.showAndWait();
                        if (result.get() == loginButtonType) { //ButtonType.OK){
                            // ... user chose OK
                            PairList pl = new PairList();
                            pl.setValues(targetValues);
                            prop.setValue(pl);
                        } else {
                            // ... user chose CANCEL or closed the dialog
                        }

                    }
                });



            } else if (c instanceof RadioButtonField) {

                ToggleGroup g = new ToggleGroup();

                Pane h;
                n = h = new HBox(6);

                final Map<Pair, RadioButton> tfs = new HashMap<>();

                for (final Pair p : ((RadioButtonField)c).getValues()) {
                    RadioButton tf;
                    h.getChildren().add(tf = new RadioButton());
                    tf.setText(p.getText());
                    tfs.put(p, tf);

                    tf.setToggleGroup(g);
                    //tf.selectedProperty().bindBidirectional(x.getStringProperty(c.getId()));

                    Pair v = dataStore.getPairProperty(((AbstractField)c).getId()).getValue();
                    tf.setSelected(v != null && v.equals(p));

                    tf.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) dataStore.getPairProperty(((AbstractField)c).getId()).setValue(p);
                        }
                    });

                }

                dataStore.getPairProperty(((AbstractField)c).getId()).addListener(new ChangeListener<Pair>() {
                    @Override
                    public void changed(ObservableValue<? extends Pair> observable, Pair oldValue, Pair newValue) {
                        if (newValue != null)  if (tfs.containsKey(newValue)) tfs.get(newValue).setSelected(true);
                    }
                });


            } else if (c instanceof RichTextField) {
                HTMLEditor tf = new HTMLEditor();
                dataStore.getStringProperty(((AbstractField) c).getId()).addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (tf.getUserData() == null) {
                            tf.setHtmlText(newValue);
                        } else {
                            tf.setUserData(null);
                        }
                    }
                });
                tf.setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        tf.setUserData(true);
                        dataStore.getStringProperty(((AbstractField) c).getId()).setValue(tf.getHtmlText());
                    }
                });
                n = control = tf;
            } else if (c instanceof SearchField) {
                SearchField sf = (SearchField) c;
                HBox h = new HBox();
                Hyperlink tf = new Hyperlink();
                Button bdel = new Button("X");
                Property<Pair> prop = dataStore.getPairProperty(((AbstractField) c).getId());
                prop.addListener(new ChangeListener<Pair>() {
                    @Override
                    public void changed(ObservableValue<? extends Pair> observable, Pair oldValue, Pair newValue) {
                        tf.setText((newValue != null)?newValue.getText():null);
                        bdel.setVisible(newValue != null);
                    }
                });
                tf.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        MateuUI.openView(new EditorDialog(sf.getCrud().getNewEditorView().setInitialId(prop.getValue().getValue())));
                    }
                });
                h.getChildren().add(tf);
                h.getChildren().add(bdel);
                bdel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tf.setText(null);
                        prop.setValue(null);
                    }
                });
                Button bsearch;
                h.getChildren().add(bsearch = new Button("Search"));
                bsearch.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        MateuUI.openView(new CRUDDialog(sf.getCrud()) {
                            @Override
                            public void onOk(Data data) {
                                if (getSelection().size() == 0) {
                                    MateuUI.alert("You must select one element");
                                } else {
                                    Data d = getSelection().get(0);
                                    prop.setValue(new Pair(d.get("_id"), d.getString("col1")));
                                }
                            }
                        });
                    }
                });

                tf.setText((prop.getValue() != null)?prop.getValue().getText():null);
                bdel.setVisible(prop.getValue() != null);

                n = h;
            } else if (c instanceof SelectByIdField) {
                HBox h = new HBox();
                TextField tf;
                h.getChildren().add(tf = new TextField());
                Label l;
                h.getChildren().add(l = new Label());

                String id = ((AbstractField) c).getId();
                Property<Pair> p = dataStore.getPairProperty(((AbstractField) c).getId());
                tf.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue != null && !"".equals(newValue.trim())) {
                            ((SelectByIdField)c).call(((SelectByIdField)c).getQl().replaceAll("xxxx", newValue), new AsyncCallback<Object[][]>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    p.setValue(null);
                                    l.setText("" + caught.getClass().getName() + ": " + caught.getMessage());
                                }

                                @Override
                                public void onSuccess(Object[][] result) {
                                    Pair v = null;
                                    if (result != null && result.length > 0) {
                                        v = new Pair(result[0][0], "" + result[0][1]);
                                    }
                                    p.setValue(v);
                                }
                            });
                        } else p.setValue(null);
                    }
                });
                p.addListener(new ChangeListener<Pair>() {
                    @Override
                    public void changed(ObservableValue<? extends Pair> observable, Pair oldValue, Pair newValue) {
                        if (newValue != null) {
                            if (!tf.isFocused()) tf.setText("" + newValue.getValue());
                            l.setText("" + newValue.getText());
                        } else {
                            if (!tf.isFocused()) tf.setText(null);
                            l.setText("---");
                        }
                    }
                });


                n = h;
            } else if (c instanceof ShowImageField) {

                // simple displays ImageView the image as is
                ImageView iv = new ImageView();
                Property<String> p = dataStore.getStringProperty(((AbstractField) c).getId());
                p.addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        // load the image
                        if (newValue == null) iv.setImage(null);
                        else {
                            Image image = new Image(newValue, true);
                            iv.setImage(image);
                        }
                    }
                });
                String v = p.getValue();
                if (v != null) {
                    Image image = new Image(v, true);
                    iv.setImage(image);
                }
                n = iv;
            } else if (c instanceof ShowTextField) {
                javafx.scene.control.Label tf = new javafx.scene.control.Label();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = tf;
            } else if (c instanceof SqlAutocompleteField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<javafx.scene.control.ListView<Pair>, ListCell<Pair>>() {
                    @Override public ListCell<Pair> call(javafx.scene.control.ListView<Pair> p) {
                        return new ListCell<Pair>() {

                            @Override protected void updateItem(Pair item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item == null || empty) {
                                    setText(null);
                                } else {
                                    setText(item.getText());
                                }
                            }
                        };
                    }
                });

                cmb.valueProperty().bindBidirectional(dataStore.getPairProperty(((AbstractField)c).getId()));
                n = cmb;

                ((SqlAutocompleteField)c).call(new io.mateu.ui.core.client.app.Callback<Object[][]>() {
                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Pair> l = new ArrayList<>();
                                for (Object[] r : result) {
                                    l.add(new Pair(r[0], (r[1] == null)?null:"" + r[1]));
                                }
                                cmb.getItems().add(new Pair(null, null));
                                cmb.getItems().addAll(l);
                            }
                        });
                    }
                });

                ObservableList<Pair> data = cmb.getItems();

                cmb.setEditable(true);
                cmb.getEditor().focusedProperty().addListener(observable -> {
                    if (cmb.getSelectionModel().getSelectedIndex() < 0) {
                        cmb.getEditor().setText(null);
                    }
                });
                cmb.addEventHandler(KeyEvent.KEY_PRESSED, t -> cmb.hide());
                cmb.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

                    private boolean moveCaretToPos = false;
                    private int caretPos;

                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode() == KeyCode.UP) {
                            caretPos = -1;
                            moveCaret((cmb.getEditor().getText() != null)?cmb.getEditor().getText().length():0);
                            return;
                        } else if (event.getCode() == KeyCode.DOWN) {
                            if (!cmb.isShowing()) {
                                cmb.show();
                            }
                            caretPos = -1;
                            moveCaret((cmb.getEditor().getText() != null)?cmb.getEditor().getText().length():0);
                            return;
                        } else if (event.getCode() == KeyCode.BACK_SPACE) {
                            moveCaretToPos = true;
                            caretPos = cmb.getEditor().getCaretPosition();
                        } else if (event.getCode() == KeyCode.DELETE) {
                            moveCaretToPos = true;
                            caretPos = cmb.getEditor().getCaretPosition();
                        } else if (event.getCode() == KeyCode.ENTER) {
                            return;
                        }

                        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT || event.getCode().equals(KeyCode.SHIFT) || event.getCode().equals(KeyCode.CONTROL)
                                || event.isControlDown() || event.getCode() == KeyCode.HOME
                                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
                            return;
                        }

                        ObservableList<Pair> list = FXCollections.observableArrayList();
                        for (Pair aData : data) {
                            if (aData != null && (cmb.getEditor().getText() == null  || (aData.getText() != null
                                    && aData.getText().toLowerCase().contains(cmb.getEditor().getText().toLowerCase())))) {
                                list.add(aData);
                            }
                        }
                        String t = cmb.getEditor().getText();

                        cmb.setItems(list);
                        cmb.getEditor().setText(t);
                        if (!moveCaretToPos) {
                            caretPos = -1;
                        }
                        moveCaret((t != null)?t.length():0);
                        if (!list.isEmpty()) {
                            cmb.show();
                        }
                    }

                    private void moveCaret(int textLength) {
                        if (caretPos == -1) {
                            cmb.getEditor().positionCaret(textLength);
                        } else {
                            cmb.getEditor().positionCaret(caretPos);
                        }
                        moveCaretToPos = false;
                    }
                });

            } else if (c instanceof SqlCheckBoxList) {


                Pane h;
                n = h = new VBox(6);

                Property<PairList> prop = dataStore.getPairListProperty(((AbstractField) c).getId());

                final Map<Pair, CheckBox> tfs = new HashMap<>();

                ((SqlCheckBoxList)c).call(new io.mateu.ui.core.client.app.Callback<Object[][]>() {
                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Pair> l = new ArrayList<>();
                                for (Object[] r : result) {
                                    l.add(new Pair(r[0], (r[1] == null)?null:"" + r[1]));
                                }

                                for (Pair p : l) {
                                    CheckBox tf;
                                    h.getChildren().add(tf = new CheckBox());
                                    tf.setText(p.getText());
                                    tfs.put(p, tf);

                                    tf.setSelected(prop.getValue() != null && prop.getValue().getValues().contains(p));

                                    tf.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                            PairList l = prop.getValue();
                                            if (l == null) l = new PairList();
                                            if (newValue) l.getValues().add(p);
                                            else l.getValues().remove(p);
                                            prop.setValue((l.getValues().size() > 0)?new PairList(l):null);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                prop.addListener(new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        for (Pair p : tfs.keySet()) tfs.get(p).setSelected(newValue != null && newValue.getValues().contains(p));
                    }
                });


            } else if (c instanceof SqlComboBoxField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<javafx.scene.control.ListView<Pair>, ListCell<Pair>>() {
                    @Override
                    public ListCell<Pair> call(javafx.scene.control.ListView<Pair> p) {
                        return new ListCell<Pair>() {

                            @Override
                            protected void updateItem(Pair item, boolean empty) {
                                super.updateItem(item, empty);

                                if (item == null || empty) {
                                    setText(null);
                                } else {
                                    setText(item.getText());
                                }
                            }
                        };
                    }
                });

                cmb.valueProperty().bindBidirectional(dataStore.getPairProperty(((AbstractField) c).getId()));
                n = control = cmb;

                ((SqlComboBoxField) c).call(new io.mateu.ui.core.client.app.Callback<Object[][]>() {
                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Pair> l = new ArrayList<>();
                                for (Object[] r : result) {
                                    l.add(new Pair(r[0], (r[1] == null) ? null : "" + r[1]));
                                }
                                cmb.getItems().add(new Pair(null, null));
                                cmb.getItems().addAll(l);
                            }
                        });
                    }
                });

            } else if (c instanceof SqlListSelectionField) {

                SqlListSelectionField lsf = (SqlListSelectionField) c;

                Pane h;
                n = h = new HBox(6);

                Label l;
                h.getChildren().add(l = new Label());
                Button b;
                h.getChildren().add(b = new Button("+"));

                Property<PairList> prop = dataStore.getPairListProperty(((AbstractField) c).getId());

                ChangeListener<PairList> cl;
                prop.addListener(cl = new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        StringBuffer s = new StringBuffer();
                        boolean empty = true;
                        if (newValue != null) {
                            for (Pair p : newValue.getValues()) {
                                if (empty) empty = false;
                                else s.append(", ");
                                s.append(p.getText());
                            }
                        }
                        l.setText((empty)?"empty (no item selected)":s.toString());
                    }
                });
                cl.changed(null, null, null);

                b.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        lsf.call(new io.mateu.ui.core.client.app.Callback<Object[][]>() {
                            @Override
                            public void onSuccess(Object[][] result) {
                                MateuUI.runInUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<Pair> l = new ArrayList<>();
                                        for (Object[] r : result) {
                                            l.add(new Pair(r[0], (r[1] == null) ? null : "" + r[1]));
                                        }
                                        ListSelectionView<Pair> v = new ListSelectionView<>();
                                        ObservableList<Pair> sourceValues = FXCollections.observableArrayList(l);
                                        ObservableList<Pair> targetValues = FXCollections.observableArrayList(prop.getValue().getValues());
                                        sourceValues.removeAll(targetValues);
                                        v.setSourceItems(sourceValues);
                                        v.setTargetItems(targetValues);

                                        Dialog d = new Dialog();
                                        d.setTitle("Select values from list");
                                        d.setResizable(true);
                                        d.getDialogPane().setContent(v);

                                        ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                                        d.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                                        Optional<ButtonType> result = d.showAndWait();
                                        if (result.get() == loginButtonType) { //ButtonType.OK){
                                            // ... user chose OK
                                            PairList pl = new PairList();
                                            pl.setValues(targetValues);
                                            prop.setValue(pl);
                                        } else {
                                            // ... user chose CANCEL or closed the dialog
                                        }
                                    }
                                });
                            }
                        });

                    }
                });


            } else if (c instanceof SqlRadioButtonField) {

                ToggleGroup g = new ToggleGroup();

                Pane h;
                n = h = new VBox(6);

                final Map<Pair, RadioButton> tfs = new HashMap<>();

                ((SqlRadioButtonField) c).call(new io.mateu.ui.core.client.app.Callback<Object[][]>() {
                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Pair> l = new ArrayList<>();
                                for (Object[] r : result) {
                                    l.add(new Pair(r[0], (r[1] == null) ? null : "" + r[1]));
                                }
                                for (Pair p : l) {
                                    RadioButton tf;
                                    h.getChildren().add(tf = new RadioButton());
                                    tf.setText(p.getText());
                                    tfs.put(p, tf);

                                    tf.setToggleGroup(g);
                                    //tf.selectedProperty().bindBidirectional(x.getStringProperty(c.getId()));

                                    Pair v = dataStore.getPairProperty(((AbstractField)c).getId()).getValue();
                                    tf.setSelected(v != null && v.equals(p));

                                    tf.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                            if (newValue) dataStore.getPairProperty(((AbstractField)c).getId()).setValue(p);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


                dataStore.getPairProperty(((AbstractField)c).getId()).addListener(new ChangeListener<Pair>() {
                    @Override
                    public void changed(ObservableValue<? extends Pair> observable, Pair oldValue, Pair newValue) {
                        if (newValue != null)  if (tfs.containsKey(newValue)) tfs.get(newValue).setSelected(true);
                    }
                });


            } else if (c instanceof TextAreaField) {
                javafx.scene.control.TextArea tf = new javafx.scene.control.TextArea();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = control = tf;
            } else if (c instanceof io.mateu.ui.core.client.components.fields.TextField) {
                javafx.scene.control.TextField tf = new javafx.scene.control.TextField();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((io.mateu.ui.core.client.components.fields.TextField) c).getId()));
                n = control = tf;

                if (firstField == null) {
                    firstField = tf;
                }


            } else if (c instanceof WebField) {
                WebView tf = new WebView();
                dataStore.getStringProperty(((AbstractField) c).getId()).addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        String u = newValue;
                        if (u != null && !"".equals(u)) {
                            tf.getEngine().load(u);
                        } else {
                            tf.getEngine().loadContent("");
                        }
                    }
                });
                String u = dataStore.getStringProperty(((AbstractField) c).getId()).getValue();
                if (u != null && !"".equals(u)) {
                    tf.getEngine().load(u);
                }
                StackPane sp = new StackPane();
                n = sp;
                sp.getChildren().add(tf);
            }
            if (n != null) {
                donde.getChildren().add(n);

                Node finalN1 = n;
                Pane finalDonde = donde;
                Pane finalContainer = container;
                ((AbstractField)c).addListener(new FieldListener() {
                    @Override
                    public void visibilityChanged(boolean newValue) {
                        if (newValue) {
                            if (finalN1.getUserData() != null) {
                                int i = (int) finalN1.getUserData();
                                finalN1.setUserData(null);
                                finalContainer.getChildren().add(i, finalDonde);
                            }
                        } else {
                            if (finalN1.getUserData() == null) {
                                int i = finalContainer.getChildren().indexOf(finalDonde);
                                finalN1.setUserData(i);
                                //finalDonde.setVisible(newValue);
                                finalContainer.getChildren().remove(finalDonde);
                            }
                        }
                    }

                    @Override
                    public void enablementChanged(boolean newValue) {
                        System.out.println("enablementChanged(" + newValue + ")");
                        setDisable(finalN1, !newValue);
                        //if (finalN1 instanceof javafx.scene.control.TextField) ((javafx.scene.control.TextField)finalN1).setEditable(false);
                    }
                });

            }

            if (((AbstractField) c).isRequired() && control != null) {
                validationSupport.registerValidator(control, Validator.createEmptyValidator("Required field"));
            }

        } else {

            if (c instanceof io.mateu.ui.core.client.components.Label) {
                javafx.scene.control.Label l;
                donde.getChildren().add(n = l = new javafx.scene.control.Label(((io.mateu.ui.core.client.components.Label) c).getText()));
                l.setStyle("-fx-alignment: baseline-" + getAlignmentString(((io.mateu.ui.core.client.components.Label) c).getAlignment()) + ";");
            }
        }

        if (maximize && n != null && n instanceof Region) {
            Region r = (Region) n;

            if (n instanceof GridNode) {
                ((GridNode)n).getTableView().setPrefWidth(5000);
                ((GridNode)n).getTableView().setPrefHeight(5000);
            }

            Node finalN = n;
            scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                    fixMins(componentsCotainer, finalN);
                    //System.out.println("bounds changed to " + newValue.toString());
                    fixHeight(overallContainer, r, newValue.getWidth(), newValue.getHeight());
                }
            });

        }

        return n;
    }

    private void setDisable(Node n, boolean disabled) {
        if (n instanceof Control) {
            ((Control)n).setDisable(disabled);
        } else if (n instanceof Pane) {
            for (Node m : ((Pane)n).getChildren()) {
                setDisable(m, disabled);
            }
        }
    }

    private TreeItem<Pair> buildTree(String n, Object v) {
        TreeItem<Pair> i = new TreeItem<>();
        if (v instanceof DataStore) {
            i.setValue(new Pair("", n));
            DataStore d = (DataStore) v;
            for (String p : d.keySet()) {
                i.getChildren().add(buildTree(p, d.get(p)));
            }
        } else if (v instanceof Data) {
            i.setValue(new Pair("", n));
            Data d = (Data) v;
            for (String p : d.getPropertyNames()) {
                i.getChildren().add(buildTree(p, d.get(p)));
            }
        } else if (v instanceof List) {
            List l = (List) v;
            i.setValue(new Pair("", n + "[] (" + l.size() + ")"));
            int pos = 0;
            for (Object o : l) {
                i.getChildren().add(buildTree(n + "[" + pos++ + "]", o));
            }
        } else if (v instanceof PairList) {
            PairList l = (PairList) v;
            i.setValue(new Pair("", n + "[] (" + l.getValues().size() + ")"));
            int pos = 0;
            for (Object o : l.getValues()) {
                i.getChildren().add(buildTree(n + "[" + pos++ + "]", o));
            }
        } else {
            Pair p;
            i.setValue(p = new Pair(v, n));
            p.set("_leaf", true);
        }
        i.setExpanded(true);
        return i;
    }

    private String getAlignmentString(Alignment alignment) {
        if (alignment == Alignment.RIGHT) return "right";
        else if (alignment == Alignment.CENTER) return "center";
        else return "left";
    }

    private void fixHeight(Pane overallContainer, Region r, double w, double h) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double deltaY =  r.getLocalToSceneTransform().transform(0, 0).getY() - overallContainer.getLocalToSceneTransform().transform(0, 0).getY();
                //System.out.println("deltay = " + deltaY + ", w = " + w + ", h = " + h);
                r.setPrefHeight(h - deltaY);
                r.setPrefWidth(w);
            }
        });
    }


}
