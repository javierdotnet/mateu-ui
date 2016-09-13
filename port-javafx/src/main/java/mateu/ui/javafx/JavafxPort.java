package mateu.ui.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mateu.ui.core.app.AbstractApplication;
import mateu.ui.core.app.ClientSideHelper;
import mateu.ui.core.app.MateuUI;
import mateu.ui.core.views.AbstractView;
import mateu.ui.javafx.app.AppNode;
import mateu.ui.javafx.views.ViewNode;

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
