package io.mateu.ui.javafx;

import io.mateu.ui.core.app.AbstractApplication;
import io.mateu.ui.javafx.app.AppNode;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import io.mateu.ui.core.app.ClientSideHelper;
import io.mateu.ui.core.app.MateuUI;
import io.mateu.ui.core.views.AbstractView;

/**
 * Created by miguel on 9/8/16.
 */
public class JavafxPort extends Application {
    private static AbstractApplication app;

    public JavafxPort(AbstractApplication app) {
        this.app = app;
    }

    public static AbstractApplication getApp() {
        return app;
    }

    public void start(Stage primaryStage) throws Exception {

        Font.loadFont(JavafxPort.class.getResource("/mateu/ui/javafx/fonts/tron/TRON.ttf").toExternalForm(), 10);
        Font.loadFont(JavafxPort.class.getResource("/mateu/ui/javafx/fonts/lato/Lato-Regular.ttf").toExternalForm(), 10);


        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(new Scene(new AppNode()));

        MateuUI.setClientSideHelper(new ClientSideHelper() {
            @Override
            public void openView(AbstractView view) {
                AppNode.get().getViewNode().setView(view);
            }
        });

        primaryStage.show();

    }
}
