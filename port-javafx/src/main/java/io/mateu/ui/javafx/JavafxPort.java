package io.mateu.ui.javafx;

import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.client.views.*;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.app.AppNode;
import io.mateu.ui.javafx.app.ViewTab;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import io.mateu.ui.core.client.app.ClientSideHelper;
import io.mateu.ui.core.client.app.MateuUI;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by miguel on 9/8/16.
 */
public class JavafxPort extends Application {
    private static AbstractApplication app;
    public static Stage mainStage;
    private static JavafxPort _this;

    public static JavafxPort get() {
        return _this;
    }

    public JavafxPort(AbstractApplication app) {
        this.app = app;
    }

    public static AbstractApplication getApp() {
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
            System.out.println("Loading font " + JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm());

            Font.loadFont(JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm(), 10);
            Font.loadFont(JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/lato/Lato-Regular.ttf").toExternalForm(), 10);
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
                                tabPane.setPrefWidth(600);
                                tabPane.setPrefHeight(400);
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
                                                        // do nothing
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
                                p.setPrefWidth(600);
                                p.setPrefHeight(400);
                            }


                            ButtonType loginButtonType = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                            d.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                            Optional<ButtonType> result = d.showAndWait();
                            if (result.get() == loginButtonType) { //ButtonType.OK){
                                // ... user chose OK
                                ((AbstractDialog)view).onOk(view.getForm().getData());
                            } else {
                                // ... user chose CANCEL or closed the dialog
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
                            AppNode.get().getViewsNode().addView(view);
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
                return app;
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
                    Desktop.getDesktop().open(new File(url.toURI()));
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
        });

        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(new Scene(new AppNode()));


        primaryStage.show();

    }

    public static void showDialog(Dialog<?> dlg) {
        Window owner = mainStage;
        dlg.initOwner(owner);
        dlg.showAndWait();
    }

}
