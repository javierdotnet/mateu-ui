package io.mateu.ui.javafx.views;

import com.google.common.base.Strings;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.components.*;
import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.components.Separator;
import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.fields.*;
import io.mateu.ui.core.client.components.fields.grids.CalendarField;
import io.mateu.ui.core.client.views.ListView;
import io.mateu.ui.core.server.ServerSideHelper;
import io.mateu.ui.core.shared.*;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.javafx.JFXHelper;
import io.mateu.ui.javafx.data.DataStore;
import io.mateu.ui.javafx.data.ViewNodeDataStore;
import io.mateu.ui.javafx.views.components.GridNode;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.*;
import javafx.util.Callback;
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
import java.net.URISyntaxException;
import java.net.URL;
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
    private Node firstField = null;
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
        view.getForm().setHelper(new FormHelper() {
            @Override
            public Data getData() {
                return getDataStore().getData();
            }
        });
        dataStore = new ViewNodeDataStore(this);

        init();
    }

    public ViewNode(AbstractView view, DataStore dataStore) {
        this();

        this.dataStore = dataStore;

        this.view = view;
        view.getForm().setHelper(new FormHelper() {
            @Override
            public Data getData() {
                return getDataStore().getData();
            }
        });

        init();
    }

    private void init() {


        view.getForm().getData();
        view.getForm().addDataSetterListener(new DataSetterListener() {
            @Override
            public void setted(Data newData) {
                getDataStore().setData(newData);
                if (getFirstField() != null) MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        getFirstField().requestFocus();
                    }
                });
            }

            @Override
            public void setted(String k, Object v) {
                getDataStore().set(k, v);
            }

            @Override
            public void idsResetted() {
                dataStore.resetIds();
            }
        });
        build();
        getChildren().add(maskerPane = new MaskerPane());
        maskerPane.setVisible(false);

        AbstractAction shortcuttable = null;

        if (view instanceof AbstractListView) {
            shortcuttable = new AbstractAction("") {
                @Override
                public void run() {
                    ((AbstractListView) view).search();
                }
            };
        } else for (AbstractAction a : view.getActions()) {
            if (a.isCallOnEnterKeyPressed()) {
                shortcuttable = a;
                break;
            }
        }
        if (shortcuttable != null) {
            AbstractAction finalShortcuttable = shortcuttable;
            setOnKeyPressed(new EventHandler<KeyEvent>()
            {
                @Override
                public void handle(KeyEvent ke)
                {
                    if (ke.getCode().equals(KeyCode.ENTER))
                    {
                        finalShortcuttable.run();
                    }
                }
            });
        }
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
                if (maskerPane != null) maskerPane.setVisible(true);
                //getChildren().add(progressIndicator = new ProgressIndicator());
            }
        });
    }

    public void endWaiting() {
        MateuUI.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (maskerPane != null) maskerPane.setVisible(false);
                //getChildren().remove(progressIndicator);
            }
        });
    }

    public void build() {
        getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        setStyle("-fx-background-color: white;");

        bp = new BorderPane();
        bp.setBorder(Border.EMPTY);
        if (!(view instanceof AbstractWizardPageView) && !(view instanceof AbstractDialog)) bp.setTop(createToolBar(view.getActions()));
        ScrollPane sp = new ScrollPane(componentsCotainer = new VBox(10));
        sp.setBorder(Border.EMPTY);
        componentsCotainer.setBorder(Border.EMPTY);
        //sp.getStyleClass().add("mateu-view-scroll");
        bp.setCenter(sp);
        //bp.setBorder(Border.EMPTY);
        //componentsCotainer.getStyleClass().add("mateu-view");
        componentsCotainer.setPadding(new Insets(10, 20, 10, 20));
        if (false) addEventHandler(Event.ANY, e -> {
            System.out.println("caught " + e.getEventType().getName());
        });

        HBox h = new HBox(0);
        h.setPadding(new Insets(0));

        Label title;
        h.getChildren().add(title = new Label());
        title.getStyleClass().add("title");
        title.textProperty().bind(dataStore.getStringProperty("_title"));

        {
            HBox badgesPane;
            h.getChildren().add(badgesPane = new HBox(2));

            Property<ObservableList<DataStore>> pb = dataStore.getObservableListProperty("_badges");
            ListChangeListener<DataStore> pl;
            pb.getValue().addListener(pl = new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {
                    badgesPane.getChildren().clear();
                    for (DataStore x : pb.getValue()) {
                        Label l = new Label("" + x.get("_value"));
                        if (x.get("_css") != null) l.getStyleClass().add(x.get("_css"));
                        badgesPane.getChildren().add(l);
                    }

                }
            });
            pl.onChanged(new ListChangeListener.Change<DataStore>(pb.getValue()) {
                @Override
                public boolean next() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public int getFrom() {
                    return 0;
                }

                @Override
                public int getTo() {
                    return 0;
                }

                @Override
                public List<DataStore> getRemoved() {
                    return null;
                }

                @Override
                protected int[] getPermutation() {
                    return new int[0];
                }
            });
        }

        componentsCotainer.getChildren().add(h);

        //links
        {
            HBox linksPane;
            componentsCotainer.getChildren().add(linksPane = new HBox(2));

            Property<ObservableList<DataStore>> pb = dataStore.getObservableListProperty("_links");
            ListChangeListener<DataStore> pl;
            pb.getValue().addListener(pl = new ListChangeListener<DataStore>() {
                @Override
                public void onChanged(Change<? extends DataStore> c) {
                    linksPane.getChildren().clear();
                    for (DataStore x : pb.getValue()) {
                        Hyperlink l = new Hyperlink("" + x.get("_caption"));
                        l.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                ((AbstractAction)x.get("_action")).run();
                            }
                        });
                        linksPane.getChildren().add(l);
                    }

                }
            });
            pl.onChanged(new ListChangeListener.Change<DataStore>(pb.getValue()) {
                @Override
                public boolean next() {
                    return false;
                }

                @Override
                public void reset() {

                }

                @Override
                public int getFrom() {
                    return 0;
                }

                @Override
                public int getTo() {
                    return 0;
                }

                @Override
                public List<DataStore> getRemoved() {
                    return null;
                }

                @Override
                protected int[] getPermutation() {
                    return new int[0];
                }
            });


        }
        // end of links






        dataStore.set("_title", view.getTitle());

        if (view instanceof AbstractEditorView) {

            dataStore.getProperty("_id").addListener(new ChangeListener<Object>() {
                @Override
                public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                    MateuUI.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            String t = view.getTitle();
                            if (newValue == null) t = "New " + t;
                            else {
                                String text = dataStore.get("_tostring");
                                if (text == null) text = "" + newValue;
                                t += " " + text;
                            }
                            dataStore.getProperty("_title").setValue(t);
                        }
                    });
                }
            });

        }

        Label subtitle;
        componentsCotainer.getChildren().add(subtitle = new Label());
        subtitle.textProperty().bind(dataStore.getStringProperty("_subtitle"));

        subtitle.setPadding(new Insets(0, 0, 10, 0));

        build(sp, componentsCotainer, view.getForm(), 0);

        componentsCotainer.setMinWidth(200);

        sp.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                componentsCotainer.setPrefWidth(newValue.getWidth());
            }
        });


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
                public void onSuccessLoad(Data result) {
                    endWaiting();
                }

                @Override
                public void onSuccessSave(Data result) {
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
                    lv.rpc();
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
        toolBar.setStyle("-fx-background-color: #f4f4f4;");

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
                        public void build() {
                            add(new DataViewerField("data"));
                        }
                    });
                }
            });
        }

        return toolBar;
    }

    private Dialog<javafx.util.Pair<String, String>> addMoreFiltersDialog() {
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
        ScrollPane sp = new ScrollPane(pane = new VBox(10));
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
            dialog.hide();
            ((ListView) view).search();
        });

        //Optional<javafx.util.Pair<String, String>> result = dialog.showAndWait();

        return dialog;
    }

    private void build(ScrollPane scrollPane, Pane overallContainer, FieldContainer form, int fromField) {
        build(new ValidationSupport(), scrollPane, overallContainer, form, fromField, false);
    }

    private void build(ValidationSupport validationSupport, ScrollPane scrollPane, Pane overallContainer, FieldContainer form, int fromField, boolean inToolBar) {

        List<Pane> panels = new ArrayList<>();
        panels.add(new VBox(10));

        Node lastNode = null;
        int pos = 0;
        int posField = 0;
        List<Component> cs = form.getComponentsSequence();
        for (Component c : cs) {
            Node n = null;
            if (!(c instanceof AbstractField) || posField >= fromField) {

                    if (posField == 0 || c instanceof Separator || (c instanceof  AbstractField && (((AbstractField)c).isBeginingOfLine()))) {
                        FlowPane fp;
                        panels.add(fp = new FlowPane(20, 10));
                    }
                    n = addComponent(validationSupport, scrollPane, overallContainer, panels.get(panels.size() - 1), c, form.isLastFieldMaximized() && pos++ == cs.size() - 1, false);

            }
            if (n != null) lastNode = n;
            if (c instanceof AbstractField) posField++;
        }

        if (view instanceof ListView && !inToolBar) {
            ListView lv = (ListView) view;
            lastNode = addComponent(validationSupport, scrollPane, overallContainer, panels.get(panels.size() - 1), new GridField("_data", lv.getColumns()).setPaginated(true).setExpandable(false).setFullWidth(true), true, false, false);
        }


        overallContainer.getChildren().addAll(panels);

    }

    private Node addComponent(ValidationSupport validationSupport, ScrollPane scrollPane, Pane overallContainer, Pane container, Component c, boolean maximize, boolean inToolBar) {
        return addComponent(validationSupport, scrollPane, overallContainer, container, c, maximize, inToolBar, true);
    }

    private Node addComponent(ValidationSupport validationSupport, ScrollPane scrollPane, Pane overallContainer, Pane container, Component c, boolean maximize, boolean inToolBar, boolean showLabels) {
        //TODO: resolver con inyección de dependencias

        Node n = null;

        Pane donde = container;
        if (donde instanceof FlowPane) {
            container = donde;
            ((FlowPane) donde).getChildren().add(donde = new VBox());
        }


        if (c instanceof Separator) {
            if (!Strings.isNullOrEmpty(((Separator) c).getText())) donde.getChildren().add(new Label(((Separator) c).getText()));
            javafx.scene.control.Separator s;
            donde.getChildren().add(s = new javafx.scene.control.Separator());
            n = s;
        } else if (c instanceof io.mateu.ui.core.client.components.Button) {
            io.mateu.ui.core.client.components.Button b = (io.mateu.ui.core.client.components.Button) c;

            Button x;
            donde.getChildren().add(x = new Button(b.getName()));
            x.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    b.run();
                }
            });

        } else if (c instanceof Tabs) {

            Tabs tabs = (Tabs) c;

            TabPane tp;
            n = tp = new TabPane();

            for (Tab t : tabs.getTabs()) {

                javafx.scene.control.Tab tc = new javafx.scene.control.Tab(t.getCaption());
                tc.setClosable(false);
                tp.getTabs().add(tc);


                ScrollPane sp = new ScrollPane(componentsCotainer = new VBox());
                componentsCotainer.setPadding(new Insets(10, 20, 10, 20));

                //tc.setContent(sp);
                tc.setContent(componentsCotainer);

                build(validationSupport, sp, componentsCotainer, t, 0, false);

            }

            donde.getChildren().add(tp);

        } else if (c instanceof AbstractField) {

            Control control = null;

            if (showLabels) {
                if (((AbstractField) c).getLabel() != null) {

                    String lt = ((AbstractField) c).getLabel().getText();

                    if (!Strings.isNullOrEmpty(((AbstractField) c).getHelp())) lt = ((lt != null)?lt:"") +  " (?)";

                    javafx.scene.control.Label l;
                    donde.getChildren().add(l = new javafx.scene.control.Label(lt));
                    if (!inToolBar) l.setStyle("-fx-min-width: 200px;-fx-alignment: baseline-left;");
                    else l.setStyle("-fx-alignment: baseline-left;");

                }
            }

            if (c instanceof AutocompleteField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                cmb.getStyleClass().add("l");
                cmb.getItems().addAll(((AutocompleteField)c).getValues());
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
                n = empaquetar(tf, 260);
            } else if (c instanceof DateTimeField || c instanceof GMTDateField) {
                TextField tf;
                n = control = tf = new TextField();
                tf.getStyleClass().add("l");

                String pattern = "yyyy-MM-dd HH:mm";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                tf.setTooltip(new Tooltip(pattern));

                tf.textProperty().bindBidirectional(dataStore.getLocalDateTimeProperty(((AbstractField) c).getId()), new LocalDateTimeStringConverter(formatter, null));
            } else if (c instanceof CheckBoxField) {
                CheckBox tf;
                n = control = tf = new CheckBox();
                n = empaquetar(tf, 260);
                String text = ((CheckBoxField) c).getText();
                if (text != null) tf.setText(text);
                tf.selectedProperty().bindBidirectional(dataStore.getBooleanProperty(((AbstractField) c).getId()));
            } else if (c instanceof WeekDaysField) {

                Pane h;
                n = h = new HBox(3);

                n = empaquetar(n, 260);

                String[] labels = {"M", "T", "W", "T", "F", "S", "S"};

                Property<Object> sprop = dataStore.getProperty(((AbstractField) c).getId());

                for (int i = 0 ; i < 7; i++) {
                    Property<Boolean> prop = dataStore.getBooleanProperty(((AbstractField) c).getId() + "_" + i);

                    CheckBox tf;
                        h.getChildren().add(tf = new CheckBox());
                        tf.setText(labels[i]);

                        tf.setSelected(prop.getValue() != null && prop.getValue());

                        tf.selectedProperty().addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                prop.setValue(newValue);
                            }
                        });

                    prop.addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            tf.setSelected(newValue != null && newValue);
                            boolean[] v = new boolean[labels.length];
                            for (int i = 0; i < v.length; i++) {
                                Boolean o = dataStore.getBooleanProperty(((AbstractField) c).getId() + "_" + i).getValue();
                                v[i] = (o != null)? o :false;
                            }
                            sprop.setValue(v);
                        }
                    });

                }
                sprop.addListener(new ChangeListener<Object>() {
                    @Override
                    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
                        boolean[] v = new boolean[labels.length];
                        if (newValue != null) {
                            v = (boolean[]) newValue;
                        }
                        for (int i = 0; i < v.length; i++) {
                            dataStore.getBooleanProperty(((AbstractField) c).getId() + "_" + i).setValue(v[i]);
                        }
                    }
                });

            } else if (c instanceof CheckBoxListField) {

                Pane h;
                n = h = new HBox(6);

                n = empaquetar(n, 260);

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
                cmb.getStyleClass().add("l");
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

                t.setPrefWidth(900);
                t.setPrefHeight(600);

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
                tf.getStyleClass().add("s");

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
                n = empaquetar(n, 260);

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
                                        Desktop.getDesktop().open(new File(new URL(newValue.getUrl()).toURI()));
                                    } catch (Exception e) {
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
                            dataStore.getFileLocatorProperty(((AbstractField)c).getId()).setValue(ServerSideHelper.getServerSideApp().upload(f.getName(), JFXHelper.read(new FileInputStream(f)), ((FileField)c).isTemporary()));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            MateuUI.alert("" + e.getClass().getName() + ":" + e.getMessage());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });

                n = empaquetar(n, 260);

            } else if (c instanceof GridField) {
                GridNode gn;
                n = gn = new GridNode(this, (GridField) c);
                control = gn.getTableView();
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
                n = empaquetar(n, 260);

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
                n = empaquetar(n, 260);

            } else if (c instanceof ListSelectionField) {

                ListSelectionField lsf = (ListSelectionField) c;

                Pane h;
                n = h = new HBox(6);

                n = empaquetar(n, 260);

                javafx.scene.control.ListView l;
                h.getChildren().add(l = new javafx.scene.control.ListView());

                l.setPrefSize(200, 250);
                l.setEditable(false);

                Button b;
                h.getChildren().add(b = new Button("+"));

                Property<PairList> prop = dataStore.getPairListProperty(((AbstractField) c).getId());

                ObservableList<String> xdata;
                l.setItems(xdata = FXCollections.observableArrayList());

                ChangeListener<PairList> cl;
                prop.addListener(cl = new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        xdata.clear();
                        if (newValue != null) {
                            for (Pair p : newValue.getValues()) {
                                if (p != null && p.getText() != null) xdata.add(p.getText());
                            }
                        }

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


            } else if (c instanceof LongField) {
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
                n = empaquetar(n, 260);
                tf.setAlignment(Pos.BASELINE_RIGHT);

                tf.textProperty().bindBidirectional(dataStore.getLongProperty(((LongField) c).getId()), new StringConverter<Long>() {
                    @Override
                    public String toString(Long object) {
                        if (object == null) return null;
                        else return "" + object;
                    }

                    @Override
                    public Long fromString(String string) {
                        Long d = null;
                        try {
                            d = new Long(string);
                        } catch (Exception e) {

                        }
                        return d;
                    }
                });
                n = tf;

            } else if (c instanceof RadioButtonField) {

                ToggleGroup g = new ToggleGroup();

                Pane h;
                n = h = new HBox(6);

                n = empaquetar(n, 260);

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
                        try {
                            MateuUI.openView(new EditorDialog(sf.getCrud().getNewEditorView().setInitialId(prop.getValue().getValue())));
                        } catch (Throwable throwable) {
                            MateuUI.notifyError(throwable.getMessage());
                        }
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

                n = empaquetar(n, 260);

            } else if (c instanceof SelectByIdField) {

                SelectByIdField sf = (SelectByIdField) c;

                HBox h = new HBox(2);
                TextField tf;
                h.getChildren().add(tf = new TextField());
                Button bn;
                h.getChildren().add(bn = new Button("New"));
                Button be;
                h.getChildren().add(be = new Button("Edit"));
                Label l;
                h.getChildren().add(l = new Label());
                l.setPadding(new Insets(6, 0, 0, 0));

                bn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        sf.createNew();
                    }
                });

                String id = ((AbstractField) c).getId();
                Property<Pair> p = dataStore.getPairProperty(((AbstractField) c).getId());

                be.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Pair v = p.getValue();
                        if (v != null) sf.edit(v.getValue());
                    }
                });

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
                            be.setVisible(true);
                        } else {
                            if (!tf.isFocused()) tf.setText(null);
                            be.setVisible(false);
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
                tf.setWrapText(true);
                tf.setMaxWidth(260);
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = tf;
                n = empaquetar(n, 260);
            } else if (c instanceof ShowEntityField) {
                javafx.scene.control.Label tf = new javafx.scene.control.Label();
                tf.setWrapText(true);
                tf.setMaxWidth(260);
                tf.textProperty().bindBidirectional(dataStore.getDataProperty(((AbstractField) c).getId()), new StringConverter<Data>() {
                    @Override
                    public String toString(Data object) {
                        if (object != null) return object.getString("text");
                        return null;
                    }

                    @Override
                    public Data fromString(String string) {
                        return null;
                    }
                });
                n = tf;
                n = empaquetar(n, 260);
            } else if (c instanceof SqlAutocompleteField) {

                ComboBox<Pair> cmb = new ComboBox<Pair>();
                cmb.getStyleClass().add("l");
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

                n = empaquetar(n, 260);

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
                cmb.getStyleClass().add("l");
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

                                Property<Pair> p = dataStore.getPairProperty(((AbstractField) c).getId());

                                cmb.valueProperty().bindBidirectional(p);
                            }
                        });
                    }
                });

            } else if (c instanceof SqlListSelectionField) {

                SqlListSelectionField lsf = (SqlListSelectionField) c;

                Pane h;
                n = h = new HBox(6);

                n = empaquetar(n, 260);

                javafx.scene.control.ListView l;
                h.getChildren().add(l = new javafx.scene.control.ListView());

                l.setPrefSize(200, 250);
                l.setEditable(false);

                Button b;
                h.getChildren().add(b = new Button("+"));

                Property<PairList> prop = dataStore.getPairListProperty(((AbstractField) c).getId());

                ObservableList<String> xdata;
                l.setItems(xdata = FXCollections.observableArrayList());

                ChangeListener<PairList> cl;
                prop.addListener(cl = new ChangeListener<PairList>() {
                    @Override
                    public void changed(ObservableValue<? extends PairList> observable, PairList oldValue, PairList newValue) {
                        xdata.clear();
                        if (newValue != null) {
                            for (Pair p : newValue.getValues()) {
                                if (p != null && p.getText() != null) xdata.add(p.getText());
                            }
                        }

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

                n = empaquetar(n, 260);

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
                tf.getStyleClass().add("l");
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((AbstractField) c).getId()));
                n = control = tf;
            } else if (c instanceof MultilanguageTextField) {

                HBox b;
                n = b = new HBox();

                Property<Data> prop = dataStore.getDataProperty(((AbstractField) c).getId());

                javafx.scene.control.TextField tf = new javafx.scene.control.TextField();

                tf.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        Data data = new Data(prop.getValue());
                        data.set(data.get("_selected"), newValue);
                        prop.setValue(data);
                    }
                });

                Data data = prop.getValue();
                if (!data.containsKey("_selected")) {
                    data.set("_selected", "en");
                }
                tf.setText(data.get(data.get("_selected")));

                control = tf;
                tf.getStyleClass().add("l");
                if (firstField == null) {
                    firstField = tf;
                }


                b.getChildren().add(tf);

                ObservableList<String> idiomas = FXCollections.observableArrayList("en", "es", "de", "fr", "it", "ar", "cz", "ru");
                ComboBox<String> lc;
                b.getChildren().add(lc = new ComboBox<String>(idiomas));


                lc.valueProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue == null) newValue = "en";
                        Data data = new Data(prop.getValue());
                        data.set("_selected", newValue);
                        prop.setValue(data);
                    }
                });

                lc.setValue(data.get("_selected"));


                prop.addListener(new ChangeListener<Data>() {
                    @Override
                    public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {
                        tf.setText((newValue != null)?newValue.get(newValue.get("_selected")):null);
                        lc.setValue((newValue != null)?newValue.get("_selected"):null);
                    }
                });


            } else if (c instanceof MultilanguageTextAreaField) {
                HBox b;
                n = b = new HBox();

                Property<Data> prop = dataStore.getDataProperty(((AbstractField) c).getId());

                javafx.scene.control.TextArea tf = new javafx.scene.control.TextArea();

                tf.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        Data data = new Data(prop.getValue());
                        data.set(data.get("_selected"), newValue);
                        prop.setValue(data);
                    }
                });

                Data data = prop.getValue();
                if (!data.containsKey("_selected")) {
                    data.set("_selected", "en");
                }
                tf.setText(data.get(data.get("_selected")));

                control = tf;
                tf.getStyleClass().add("l");
                if (firstField == null) {
                    firstField = tf;
                }


                b.getChildren().add(tf);

                ObservableList<String> idiomas = FXCollections.observableArrayList("en", "es", "de", "fr", "it", "ar", "cz", "ru");
                ComboBox<String> lc;
                b.getChildren().add(lc = new ComboBox<String>(idiomas));


                lc.valueProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if (newValue == null) newValue = "en";
                        Data data = new Data(prop.getValue());
                        data.set("_selected", newValue);
                        prop.setValue(data);
                    }
                });

                lc.setValue(data.get("_selected"));


                prop.addListener(new ChangeListener<Data>() {
                    @Override
                    public void changed(ObservableValue<? extends Data> observable, Data oldValue, Data newValue) {
                        tf.setText((newValue != null)?newValue.get(newValue.get("_selected")):null);
                        lc.setValue((newValue != null)?newValue.get("_selected"):null);
                    }
                });
            } else if (c instanceof io.mateu.ui.core.client.components.fields.TextField) {
                javafx.scene.control.TextField tf = new javafx.scene.control.TextField();
                tf.textProperty().bindBidirectional(dataStore.getStringProperty(((io.mateu.ui.core.client.components.fields.TextField) c).getId()));
                n = control = tf;
                tf.getStyleClass().add("l");
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


            if (!Strings.isNullOrEmpty(((AbstractField) c).getHelp())) {
                final Tooltip tooltip = new Tooltip();
                tooltip.setText(((AbstractField) c).getHelp());
//                control.setTooltip(tooltip);
                bindTooltip(control, tooltip);
            }

        } else {

            if (c instanceof io.mateu.ui.core.client.components.Label) {
                javafx.scene.control.Label l;
                donde.getChildren().add(n = l = new javafx.scene.control.Label(((io.mateu.ui.core.client.components.Label) c).getText()));
                l.setStyle("-fx-alignment: baseline-" + getAlignmentString(((io.mateu.ui.core.client.components.Label) c).getAlignment()) + ";");
            }
        }

        if (n != null && n instanceof Region) {
            Region r = (Region) n;

            if (n instanceof GridNode) {
                ((GridNode)n).getTableView().setMinWidth(200);

                if ((c instanceof GridField && ((GridField) c).isFullWidth())) {
                    Node finalN = n;
                    scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
                        @Override
                        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                            //fixMins(componentsCotainer, finalN);
                            //System.out.println("bounds changed to " + newValue.toString());
                            //fixHeight(overallContainer, r, newValue.getWidth(), newValue.getHeight());
                            ((GridNode)finalN).getTableView().setPrefWidth(newValue.getWidth() - 40);
                        }
                    });
                }

            } else if (n instanceof TabPane) {
                ((TabPane)n).setMinWidth(200);

                if ((c instanceof Tabs && ((Tabs) c).isFullWidth())) {
                    Node finalN = n;
                    scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
                        @Override
                        public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                            //fixMins(componentsCotainer, finalN);
                            //System.out.println("bounds changed to " + newValue.toString());
                            //fixHeight(overallContainer, r, newValue.getWidth(), newValue.getHeight());
                            ((TabPane)finalN).setPrefWidth(newValue.getWidth() - 40);
                        }
                    });
                }

            } else if (n instanceof javafx.scene.control.Separator) {

                Node finalN2 = n;
                scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
                    @Override
                    public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                        //fixMins(componentsCotainer, finalN);
                        //System.out.println("bounds changed to " + newValue.toString());
                        //fixHeight(overallContainer, r, newValue.getWidth(), newValue.getHeight());
                        ((javafx.scene.control.Separator)finalN2).setPrefWidth(newValue.getWidth() - 40);
                    }
                });
            }


        }

        return n;
    }

    public static void bindTooltip(final Node node, final Tooltip tooltip){
        node.setOnMouseMoved(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                // +15 moves the tooltip 15 pixels below the mouse cursor;
                // if you don't change the y coordinate of the tooltip, you
                // will see constant screen flicker
                tooltip.show(node, event.getScreenX(), event.getScreenY() + 15);
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                tooltip.hide();
            }
        });
    }

    private Node empaquetar(Node n, int width) {
        Pane h = new Pane(n);
        //h.setStyle("-fx-background-color: red;");
        h.setPrefWidth(width);
        h.setMaxWidth(width);
        return h;
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

}
