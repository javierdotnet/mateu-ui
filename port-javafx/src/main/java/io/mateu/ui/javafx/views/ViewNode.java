package io.mateu.ui.javafx.views;

import io.mateu.ui.core.client.Mateu;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.components.*;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.TextField;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.shared.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.javafx.JFXHelper;
import io.mateu.ui.javafx.JavafxPort;
import io.mateu.ui.javafx.app.AppNode;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.data.ViewNodeDataStore;
import io.mateu.ui.javafx.views.components.GridNode;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.*;

/**
 * Created by miguel on 9/8/16.
 */
public class ViewNode extends StackPane {

    private AbstractView view;
    private DataStore dataStore;
    private BorderPane bp;
    private ProgressIndicator progressIndicator;
    private Pane componentsCotainer;
    private boolean minsFixed;
    private Stage moreFiltersDialog;

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
        build();
        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                getDataStore().setData(newData);
            }
        });
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
                getChildren().add(progressIndicator = new ProgressIndicator());
            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                getChildren().remove(progressIndicator);
            }
        });
    }

    public void build() {
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        setStyle("-fx-background-color: white;");

        bp = new BorderPane();
        bp.setTop(createToolBar(view.getActions()));
        ScrollPane sp = new ScrollPane(componentsCotainer = new Pane());
        sp.getStyleClass().add("mateu-view-scroll");
        bp.setCenter(sp);
        componentsCotainer.getStyleClass().add("mateu-view");
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

    private Node createToolBar(List<AbstractAction> actions) {
        ToolBar toolBar = new ToolBar();

        if (view instanceof AbstractListView) {
            AbstractListView lv = (AbstractListView) view;

            Pane contenedor = new HBox();

            int pos = 0;
            int posField = 0;
            List<Component> cs = lv.getForm().getComponentsSequence();
            for (Component c : cs) {
                addComponent(null, null, contenedor, c, false, true);

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



                    build(sp, moreFiltersComponentsCotainer, lv.getForm(), lv.getMaxFieldsInHeader(), true);
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
                    System.out.println(getDataStore().toString());
                }
            });
        }
        return toolBar;
    }

    private void build(ScrollPane scrollPane, Pane overallContainer, FieldContainer form, int fromField) {
        build(scrollPane, overallContainer, form, fromField, false);
    }

    private void build(ScrollPane scrollPane, Pane overallContainer, FieldContainer form, int fromField, boolean inToolBar) {

        List<Pane> panels = new ArrayList<>();
        panels.add(new VBox(2));

        Node lastNode = null;
        if (view instanceof AbstractListView && !inToolBar) {
            AbstractListView lv = (AbstractListView) view;
            lastNode = addComponent(scrollPane, overallContainer, panels.get(panels.size() - 1), new GridField("_data", lv.getColumns()).setPaginated(true).setExpandable(false), true, false, false);
        } else {
            int pos = 0;
            int posField = 0;
            List<Component> cs = form.getComponentsSequence();
            for (Component c : cs) {
                Node n = null;
                if (!(c instanceof AbstractField) || posField >= fromField) {
                    if (c instanceof ColumnStart) panels.add(new VBox(2));
                    else if (c instanceof RowStart) panels.add(new FlowPane(5, 2));
                    else if (c instanceof ColumnEnd) panels.remove(panels.size() - 1);
                    else if (c instanceof RowEnd) panels.remove(panels.size() - 1);
                    else n = addComponent(scrollPane, overallContainer, panels.get(panels.size() - 1), c, form.isLastFieldMaximized() && pos++ == cs.size() - 1, false);
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

    private Node addComponent(ScrollPane scrollPane, Pane overallContainer, Pane container, Component c, boolean maximize, boolean inToolBar) {
        return addComponent(scrollPane, overallContainer, container, c, maximize, inToolBar, true);
    }

    private Node addComponent(ScrollPane scrollPane, Pane overallContainer, Pane container, Component c, boolean maximize, boolean inToolBar, boolean showLabels) {
        //TODO: resolver con inyección de dependencias

        Node n = null;

        Pane donde = container;
        if (donde instanceof VBox) {
            ((VBox) donde).getChildren().add(donde = new FlowPane());
            donde.setPrefWidth(5000);
        }

        if (c instanceof AbstractField) {

            if (showLabels) {
                if (((AbstractField) c).getLabel() != null) {
                    Label l;
                    donde.getChildren().add(l = new Label(((AbstractField) c).getLabel().getText()));
                    if (!inToolBar) l.setStyle("-fx-min-width: 200px;-fx-alignment: baseline-right;");
                    else l.setStyle("-fx-alignment: baseline-right;");
                }
            }

            if (c instanceof AutocompleteField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                cmb.getItems().addAll(((AutocompleteField)c).getValues());
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<ListView<Pair>, ListCell<Pair>>() {
                    @Override public ListCell<Pair> call(ListView<Pair> p) {
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


            } else if (c instanceof CalendarField || c instanceof DateField) {
                DatePicker tf;
                n = tf = new DatePicker();
                tf.valueProperty().bindBidirectional(dataStore.getLocalDateProperty(((AbstractField) c).getId()));
                tf.getEditor().textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        System.out.println("date field changed to " + newValue);
                    }
                });
            } else if (c instanceof CheckBoxField) {
                CheckBox tf;
                n = tf = new CheckBox();
                String text = ((CheckBoxField) c).getText();
                if (text != null) tf.setText(text);
                tf.selectedProperty().bindBidirectional(dataStore.getBooleanProperty(((AbstractField) c).getId()));
            } else if (c instanceof CheckBoxListField) {
                n = new CheckBox();

                Pane h;
                n = h = new HBox(6);

                final Map<Pair, CheckBox> tfs = new HashMap<>();

                for (final Pair p : ((CheckBoxListField)c).getValues()) {
                    CheckBox tf;
                    h.getChildren().add(tf = new CheckBox());
                    tf.setText(p.getText());
                    tfs.put(p, tf);

                    tf.setSelected(dataStore.getPairListProperty(((AbstractField)c).getId()).getValue().getValues().contains(p));

                    tf.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            if (newValue) dataStore.getPairListProperty(((AbstractField)c).getId()).getValue().getValues().add(p);
                        }
                    });

                }

                dataStore.getPairListProperty(((AbstractField)c).getId()).addListener(new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        if (newValue != null)  for (Pair p : newValue.getValues()) if (tfs.containsKey(p)) tfs.get(p).setSelected(true);
                    }
                });

            } else if (c instanceof ComboBoxField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                cmb.getItems().addAll(((ComboBoxField)c).getValues());
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<ListView<Pair>, ListCell<Pair>>() {
                    @Override public ListCell<Pair> call(ListView<Pair> p) {
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

            } else if (c instanceof DoubleField) {
                javafx.scene.control.TextField tf = new javafx.scene.control.TextField();

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
                javafx.scene.control.TextArea tf = new javafx.scene.control.TextArea();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = tf;

                //////////////////////

                VBox v;
                n = v = new VBox();

                final HBox h0;
                v.getChildren().add(h0 = new HBox());

                HBox h1;
                v.getChildren().add(h1 = new HBox());

                dataStore.getFileLocatorProperty(((AbstractField)c).getId()).addListener(new ChangeListener<FileLocator>() {

                    @Override
                    public void changed(ObservableValue<? extends FileLocator> arg0, FileLocator oldValue, FileLocator newValue) {
                        h0.getChildren().clear();
                        if (newValue != null && newValue.getUrl() != null) {
                            h0.getChildren().add(new Label(newValue.getUrl()));
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
                            MateuUI.getBaseService().upload(JFXHelper.read(new FileInputStream(f)), new io.mateu.ui.core.client.app.Callback<FileLocator>() {
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
                javafx.scene.control.TextField tf = new javafx.scene.control.TextField();
                    DecimalFormat format = new DecimalFormat( "#" );
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
                javafx.scene.control.TextArea tf = new javafx.scene.control.TextArea();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = tf;
            } else if (c instanceof ShowTextField) {
                javafx.scene.control.Label tf = new javafx.scene.control.Label();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = tf;
            } else if (c instanceof SqlComboBoxField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                //cmb.getSelectionModel().selectFirst(); //select the first element
                cmb.setCellFactory(new Callback<ListView<Pair>, ListCell<Pair>>() {
                    @Override public ListCell<Pair> call(ListView<Pair> p) {
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



                ((SqlComboBoxField)c).call(new io.mateu.ui.core.client.app.Callback<Object[][]>() {
                    @Override
                    public void onSuccess(Object[][] result) {
                        MateuUI.runInUIThread(new Runnable() {
                            @Override
                            public void run() {
                                List<Pair> l = new ArrayList<>();
                                for (Object[] r : result) {
                                    l.add(new Pair(r[0], (r[1] == null)?null:"" + r[1]));
                                }
                                cmb.getItems().addAll(l);
                            }
                        });
                    }
                });


            } else if (c instanceof TextAreaField) {
                javafx.scene.control.TextArea tf = new javafx.scene.control.TextArea();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = tf;
            } else if (c instanceof TextField) {
                javafx.scene.control.TextField tf = new javafx.scene.control.TextField();
                if (c.getClass().isAssignableFrom(Double.class)) {
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
                    tf.textProperty().bindBidirectional(dataStore.getDoubleProperty(((TextField) c).getId()), new StringConverter<Double>() {
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
                } else if (c.getClass().isAssignableFrom(Integer.class)) {
                    DecimalFormat format = new DecimalFormat( "#" );
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
                    tf.textProperty().bindBidirectional(dataStore.getIntegerProperty(((TextField) c).getId()), new StringConverter<Integer>() {
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
                } else {
                    tf.textProperty().bindBidirectional(dataStore.getStringProperty(((TextField) c).getId()));
                }
                n = tf;

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
            if (n != null) donde.getChildren().add(n);

        } else {

            if (c instanceof io.mateu.ui.core.client.components.Label) {
                Label l;
                donde.getChildren().add(n = l = new Label(((io.mateu.ui.core.client.components.Label) c).getText()));
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
