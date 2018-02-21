package io.mateu.ui.javafx;

import io.mateu.ui.App;
import io.mateu.ui.core.client.app.*;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.components.fields.DataViewerField;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.app.AppNode;
import io.mateu.ui.javafx.app.ViewTab;
import io.mateu.ui.javafx.newlayout.BarraDireccionesNode;
import io.mateu.ui.javafx.newlayout.VistaActualNode;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 * Created by miguel on 9/8/16.
 */
public class JavafxPort extends Application {
    private static App app;
    public static Stage mainStage;
    private static JavafxPort _this;

    private static Dialog dialogoActual;
    private static List<Node> pilaContenidos = new ArrayList<>();


    public static JavafxPort get() {
        return _this;
    }

    public JavafxPort(App app) {
        app.setPort(AbstractApplication.PORT_JAVAFX);
        this.app = app;
    }

    public static App getApp() {
        return app;
    }

    public void start(Stage primaryStage) throws Exception {

        System.out.println("JavafxPort.start()");

        _this = this;

        //Thread.setDefaultUncaughtExceptionHandler(Main::showError);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                String msg = "" + e.getClass().getName() + ":" + e.getMessage();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        MateuUI.alert(msg);
                    }
                });
            }
        });

        mainStage = primaryStage;

        try {
            //System.out.println("Loading font " + JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm());

            //Font.loadFont(JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm(), 10);
            //Font.loadFont(JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/lato/Lato-Regular.ttf").toExternalForm(), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }


        MateuUI.setClientSideHelper(new ClientSideHelper() {

            public BaseServiceAsync baseServiceImpl = new BaseServiceClientSideImpl();

            @Override
            public void openView(AbstractView view) {
                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {

                        if (view instanceof AbstractDialog) {

                            Dialog d = new Dialog();
                            d.setTitle(view.getTitle());
                            d.setResizable(true);

                            //alert.setHeaderText("Look, an Error Dialog");

                            if (view instanceof CRUDDialog) {
                                Map<String, ViewTab> tabs = new HashMap<>();
                                TabPane tabPane;
                                ViewTab tcrud;
                                d.getDialogPane().setContent(tabPane = new TabPane(tcrud = new ViewTab(view)));
                                tcrud.setClosable(false);
                                ((CRUDDialog)view).getCrud().addListener(new CRUDListener() {
                                    @Override
                                    public void openEditor(AbstractEditorView view) {

                                        ViewTab t;

                                        if (tabs.containsKey(view.getViewId())) {
                                            t = tabs.get(view.getViewId());
                                            tabPane.getSelectionModel().select(t);
                                        } else {
                                            tabPane.getTabs().add(t = new ViewTab(view));
                                            view.addListener(new ViewListener() {
                                                @Override
                                                public void onClose() {
                                                    tabPane.getTabs().remove(t);
                                                    tabs.remove(view.getViewId());
                                                }
                                            });
                                            if (view instanceof AbstractEditorView) {
                                                view.getForm().addDataSetterListener(new DataSetterListener() {
                                                    @Override
                                                    public void setted(Data newData) {
                                                        if (newData.get("_id") != null) {
                                                            if (!newData.get("_id").equals(((AbstractEditorView) view).getInitialId())) {
                                                                String oldK = view.getViewId();
                                                                tabs.remove(oldK);
                                                                ((AbstractEditorView) view).setInitialId(newData.get("_id"));
                                                                tabs.put(view.getViewId(), t);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void setted(String k, Object v) {
                                                        if ("_id".equals(k)) {
                                                            String oldK = view.getViewId();
                                                            tabs.remove(oldK);
                                                            ((AbstractEditorView) view).setInitialId(v);
                                                            tabs.put(view.getViewId(), t);
                                                        }
                                                    }

                                                    @Override
                                                    public void idsResetted() {
                                                        ((AbstractEditorView) view).setInitialId(null);
                                                    }

                                                    @Override
                                                    public void cleared() {
                                                        ((AbstractEditorView) view).setInitialId(null);
                                                    }
                                                });
                                            }
                                            t.setOnClosed(new EventHandler<Event>() {
                                                @Override
                                                public void handle(Event event) {
                                                    tabs.remove(view.getViewId());
                                                }
                                            });
                                            tabs.put(view.getViewId(), t);
                                            tabPane.getSelectionModel().select(t);
                                            if (t.getViewNode().getFirstField() != null) {
                                                MateuUI.runInUIThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        System.out.println("FOCUS REQUESTED!");
                                                        t.getViewNode().getFirstField().requestFocus();
                                                    }
                                                });
                                            }
                                        }

                                    }
                                });
                            } else {
                                StackPane p;
                                d.getDialogPane().setContent(p = new StackPane(new ViewNode(view)));
                                p.setPrefWidth(700);
                                p.setPrefHeight(600);
                            }



                            for (AbstractAction a : view.getActions()) {
                                ButtonType bt = new ButtonType(a.getName(), ButtonBar.ButtonData.OTHER);
                                d.getDialogPane().getButtonTypes().add(bt);
                                final Button b = (Button) d.getDialogPane().lookupButton(bt);
                                b.addEventFilter(
                                        ActionEvent.ACTION,
                                        event -> {
                                            a.run();
                                            event.consume();
                                        }
                                );
                            }

                            if (view instanceof AbstractListEditorDialog) {

                                Property<Integer> pos = new SimpleObjectProperty<>();

                                AbstractListEditorDialog lv = (AbstractListEditorDialog) view;

                                pos.setValue(lv.getInitialPos());

                                    ButtonType bt = new ButtonType("Previous", ButtonBar.ButtonData.BACK_PREVIOUS);
                                    d.getDialogPane().getButtonTypes().add(bt);
                                    Button prev = (Button) d.getDialogPane().lookupButton(bt);
                                    prev.addEventFilter(
                                            ActionEvent.ACTION,
                                            event -> {
                                                // Check whether some conditions are fulfilled
                                                List<String> errors = view.getForm().validate();
                                                if (errors.size() > 0) {
                                                    // The conditions are not fulfilled so we consume the event
                                                    // to prevent the dialog to close
                                                    event.consume();
                                                    MateuUI.notifyErrors(errors);
                                                } else {
                                                    if (pos.getValue() > 0) {
                                                        lv.setData(pos.getValue(), view.getForm().getData());
                                                        pos.setValue(pos.getValue() - 1);
                                                        view.getForm().setData(lv.getData(pos.getValue()));
                                                    }
                                                    event.consume();
                                                }
                                            }
                                    );

                                    bt = new ButtonType("Next", ButtonBar.ButtonData.NEXT_FORWARD);
                                    d.getDialogPane().getButtonTypes().add(bt);
                                    Button next = (Button) d.getDialogPane().lookupButton(bt);
                                    next.addEventFilter(
                                            ActionEvent.ACTION,
                                            event -> {
                                                // Check whether some conditions are fulfilled
                                                List<String> errors = view.getForm().validate();
                                                if (errors.size() > 0) {
                                                    // The conditions are not fulfilled so we consume the event
                                                    // to prevent the dialog to close
                                                    event.consume();
                                                    MateuUI.notifyErrors(errors);
                                                } else {
                                                    if (pos.getValue() < lv.getListSize() - 1) {
                                                        lv.setData(pos.getValue(), view.getForm().getData());
                                                        pos.setValue(pos.getValue() + 1);
                                                        view.getForm().setData(lv.getData(pos.getValue()));
                                                    }
                                                    event.consume();
                                                }
                                            }
                                    );

                                pos.addListener(new ChangeListener<Integer>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                                        if (newValue <= 0) {
                                            prev.setDisable(true);
                                        } else {
                                            prev.setDisable(false);
                                        }
                                        if (newValue < lv.getListSize() - 1) {
                                            next.setDisable(false);
                                        } else {
                                            next.setDisable(true);
                                        }
                                    }
                                });
                            }

                            if (view instanceof AbstractAddRecordDialog) {

                                ButtonType dataButtonType = new ButtonType("Data", ButtonBar.ButtonData.OTHER);
                                ButtonType loginButtonType = new ButtonType("Add and reset", ButtonBar.ButtonData.OK_DONE);
                                d.getDialogPane().getButtonTypes().addAll(dataButtonType, loginButtonType, CANCEL);

                                final Button btOk = (Button) d.getDialogPane().lookupButton(loginButtonType);
                                btOk.addEventFilter(
                                        ActionEvent.ACTION,
                                        event -> {
                                            // Check whether some conditions are fulfilled
                                            List<String> errors = view.getForm().validate();
                                            if (errors.size() > 0) {
                                                // The conditions are not fulfilled so we consume the event
                                                // to prevent the dialog to close
                                                event.consume();
                                                MateuUI.notifyErrors(errors);
                                            } else {
                                                ((AbstractAddRecordDialog) view).addAndClean(view.getForm().getData().strip("_title"));
                                                event.consume();
                                            }
                                        }
                                );

                                final Button btData = (Button) d.getDialogPane().lookupButton(dataButtonType);
                                btData.addEventFilter(
                                        ActionEvent.ACTION,
                                        event -> {
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
                                            event.consume();
                                        }
                                );


                                Optional<ButtonType> result = d.showAndWait();
                                if (result.get() == loginButtonType) { //ButtonType.OK){
                                    // ... user chose OK
                                } else {
                                    // ... user chose CANCEL or closed the dialog
                                }

                            } else {

                                ButtonType dataButtonType = new ButtonType("Data", ButtonBar.ButtonData.OTHER);

                                ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                                d.getDialogPane().getButtonTypes().addAll(dataButtonType, loginButtonType, CANCEL);

                                final Button btOk = (Button) d.getDialogPane().lookupButton(loginButtonType);
                                btOk.addEventFilter(
                                        ActionEvent.ACTION,
                                        event -> {
                                            // Check whether some conditions are fulfilled
                                            List<String> errors = view.getForm().validate();
                                            if (errors.size() > 0) {
                                                // The conditions are not fulfilled so we consume the event
                                                // to prevent the dialog to close
                                                event.consume();
                                                MateuUI.notifyErrors(errors);
                                            }
                                        }
                                );

                                final Button btData = (Button) d.getDialogPane().lookupButton(dataButtonType);
                                btData.addEventFilter(
                                        ActionEvent.ACTION,
                                        event -> {
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
                                            event.consume();
                                        }
                                );


                                Optional<ButtonType> result = d.showAndWait();
                                if (result.get() == loginButtonType) { //ButtonType.OK){
                                    // ... user chose OK
                                    ((AbstractDialog)view).onOk(view.getForm().getData());
                                } else {
                                    // ... user chose CANCEL or closed the dialog
                                }

                            }

                        } else {
                            if (view instanceof AbstractCRUDView) {
                                ((AbstractCRUDView)view).addListener(new CRUDListener() {
                                    @Override
                                    public void openEditor(AbstractEditorView e) {
                                        MateuUI.openView(e);
                                    }
                                });
                            }

                            //AppNode.get().getViewsNode().addView(view);
                            BarraDireccionesNode.get().cargar(view.getViewId());

                        }

                    }
                });
            }

            @Override
            public Data getNewDataContainer() {
                return new Data();
            }

            @Override
            public <T> T create(Class<?> serviceInterface) {
                try {
                    return (T) Class.forName(serviceInterface.getName().replaceAll("\\.shared\\.", ".client.") + "ClientSideImpl").newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void alert(String msg) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Look, an Error Dialog");
                alert.setContentText(msg);

                alert.showAndWait();
            }

            @Override
            public void run(Runnable runnable) {

                run(runnable, null);

                //Platform.runLater(runnable);

            }

            @Override
            public void runInUIThread(Runnable runnable) {
                Platform.runLater(runnable);
            }

            @Override
            public BaseServiceAsync getBaseService() {
                return baseServiceImpl;
            }

            @Override
            public void run(Runnable runnable, Runnable onerror) {

                try {

                    Task task = new Task<Void>() {
                        @Override public Void call() {

                            try {

                                runnable.run();

                            } catch (Exception e) {
                                e.printStackTrace();
                                runInUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        alert(e.getClass().getName() + ": " + e.getMessage());
                                    }
                                });
                                if (onerror != null) onerror.run();
                            }


                            return null;
                        }
                    };

                    new Thread(task).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    alert(e.getClass().getName() + ": " + e.getMessage());
                }

            }

            @Override
            public void openView(AbstractView parentView, AbstractView view) {
                openView(view);
            }

            @Override
            public void notifyErrors(List<String> msgs) {

                StringBuffer sb = new StringBuffer();
                boolean primero = true;
                for (String m : msgs) {
                    if (primero) primero = false; else sb.append("\n");
                    sb.append(m);
                }

                Notifications.create().title("Errors").text(sb.toString()).position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(5)).darkStyle().showError();
            }

            @Override
            public AbstractApplication getApp() {
                return (AbstractApplication) app;
            }

            @Override
            public void notifyError(String msg) {
                Notifications.create().title("Error").text(msg).position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(5)).darkStyle().showError();
            }

            @Override
            public void notifyInfo(String msg) {
                Notifications.create().title("Info").text(msg).position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(5)).darkStyle().showInformation();
            }

            @Override
            public void notifyDone(String msg) {
                Notifications.create().title("Done").text(msg).position(Pos.BOTTOM_RIGHT).hideAfter(Duration.seconds(5)).darkStyle().showConfirm();
            }

            @Override
            public void open(URL url) {
                try {
                    if( Desktop.isDesktopSupported() )
                    {
                        new Thread(() -> {
                            try {
                                Desktop.getDesktop().browse( url.toURI() );
                            } catch (IOException | URISyntaxException e1) {
                                e1.printStackTrace();
                            }
                        }).start();
                    } else
                    try {
                        File f = new File(url.toURI());
                        Desktop.getDesktop().open(f);
                    } catch (IllegalArgumentException e) {
                        //java.lang.IllegalArgumentException: URI scheme is not "file"
                        Stage s = new Stage();
                        s.setWidth(800);
                        s.setHeight(600);
                        s.setTitle(url.toString());
                        s.initOwner(mainStage);

                        System.out.println(url);

                        WebView v = new WebView();
                        v.getEngine().loadContent("<html><body><h1>Un momento por favor...</h1></body></html>");
                        StackPane root = new StackPane();
                        root.getChildren().add(v);

                        Scene scene = new Scene(root);
                        //scene.getStylesheets().add(getClass().getResource("autenticado.css").toExternalForm());
                        s.setScene(scene);
                        s.show();

                        System.out.println("abriendo " + url);

                        v.getEngine().load(url.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if (false) MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {

                        Stage s = new Stage();
                        s.setWidth(800);
                        s.setHeight(600);
                        s.setTitle(url.toString());
                        s.initOwner(mainStage);

                        System.out.println(url);

                        WebView v = new WebView();
                        v.getEngine().loadContent("<html><body><h1>Un momento por favor...</h1></body></html>");
                        StackPane root = new StackPane();
                        root.getChildren().add(v);

                        Scene scene = new Scene(root);
                        //scene.getStylesheets().add(getClass().getResource("autenticado.css").toExternalForm());
                        s.setScene(scene);
                        s.show();

                        System.out.println("abriendo " + url);

                        v.getEngine().load(url.toString());

                    }
                });
            }

            @Override
            public void confirm(String text, Runnable onOk) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initStyle(StageStyle.UTILITY);
                alert.setTitle("Choose an option");
                alert.setHeaderText("Please confirm");
                alert.setContentText(text);

                //To make enter key press the actual focused button, not the first one. Just like pressing "space".
                alert.getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        event.consume();
                        try {
                            Robot r = new Robot();
                            r.keyPress(java.awt.event.KeyEvent.VK_SPACE);
                            r.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                List<ButtonType> buttons = new ArrayList<>();
                for (ButtonType bt : new ButtonType[]{OK, CANCEL}) {
                    buttons.add(bt);
                }

                alert.getButtonTypes().setAll(buttons);

                Optional<ButtonType> result = alert.showAndWait();
                if (!result.isPresent()) {
                    //return CANCEL;
                } else {
                    if (OK.equals(result.get())) onOk.run();
                }
            }

            @Override
            public void open(AbstractWizard wizard) {
                MateuUI.runInUIThread(new Runnable() {
                    @Override
                    public void run() {

                        WizardDialog d = new WizardDialog(wizard);

                        Optional<ButtonType> result = d.showAndWait();

                    }

                });
            }
        });

        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(900);

        /*
        AppNode appnode;
        primaryStage.setScene(new Scene(appnode = new AppNode()));
         */

        Scene s;
        primaryStage.setScene(s = new Scene(new io.mateu.ui.javafx.newlayout.AppNode()));
        s.getStylesheets().add(getClass().getResource("/io/mateu/ui/javafx/views/style.css").toExternalForm());

        primaryStage.show();

        boolean hayPartePublica = false;
        for (AbstractArea a : app.getAreas()) {
            hayPartePublica |= a.isPublicAccess();
        }
        if (!hayPartePublica) {
            //appnode.getTopNode().askForLogin();
        }

    }


    public static void showDialog(Dialog<?> dlg) {
        Window owner = mainStage;
        dlg.initOwner(owner);
        dlg.showAndWait();
    }

}
