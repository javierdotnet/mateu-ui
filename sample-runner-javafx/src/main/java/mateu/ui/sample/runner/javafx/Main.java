package mateu.ui.sample.runner.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import mateu.ui.javafx.JavafxPort;
import mateu.ui.sample.app.SampleApp;

/**
 * Created by miguel on 9/8/16.
 */
public class Main extends Application {

    public void start(Stage primaryStage) throws Exception {

        new JavafxPort(new SampleApp()).start(primaryStage);

    }

    public static void main(String... args) {
        launch(args);
    }
}
