package io.mateu.ui.javafx;

import io.mateu.ui.core.client.app.AbstractApplication;
import io.mateu.ui.core.client.BaseServiceClientSideImpl;
import io.mateu.ui.core.client.BaseServiceAsync;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.app.AppNode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import io.mateu.ui.core.client.app.ClientSideHelper;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.AbstractView;
import javafx.stage.Window;

/**
 * Created by miguel on 9/8/16.
 */
public class JavafxPort extends Application {
    private static AbstractApplication app;
    public static Stage mainStage;

    public JavafxPort(AbstractApplication app) {
        this.app = app;
    }

    public static AbstractApplication getApp() {
        return app;
    }

    public void start(Stage primaryStage) throws Exception {

        System.out.println("JavafxPort.start()");

        mainStage = primaryStage;

        try {
            System.out.println("Loading font " + JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm());

            Font.loadFont(JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm(), 10);
            Font.loadFont(JavafxPort.class.getResource("/io/mateu/ui/javafx/fonts/lato/Lato-Regular.ttf").toExternalForm(), 10);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(new Scene(new AppNode()));

        MateuUI.setClientSideHelper(new ClientSideHelper() {

            public BaseServiceAsync baseServiceImpl = new BaseServiceClientSideImpl();

            @Override
            public void openView(AbstractView view) {
                AppNode.get().getViewsNode().addView(view);
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

                Task task = new Task<Void>() {
                    @Override public Void call() {

                        runnable.run();

                        return null;
                    }
                };

                new Thread(task).start();

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
        });

        primaryStage.show();

    }
}
