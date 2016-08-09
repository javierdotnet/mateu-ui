package mateu.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mateu.ui.core.app.AbstractApplication;
import mateu.ui.javafx.app.AppNode;

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

        //primaryStage.setFullScreen(true);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setScene(new Scene(new AppNode()));
        primaryStage.show();

    }
}
