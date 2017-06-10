package io.mateu.ui.javafx;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import io.mateu.ui.App;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by miguel on 5/6/17.
 */
public class Main extends Application {

    private App app = null;
    private ClassLoader loader = null;

    public Main(ClassLoader loader) {
        this.loader = loader;
    }

    public Main(App app) {
        this.app = app;
    }

    public Main() {

    }


    public void start(Stage primaryStage) throws Exception {

        if (app == null) {

            String cn = CharStreams.toString(new InputStreamReader(getClass().getResourceAsStream("/META-INF/services/" + App.class.getName())));
            app = (App) Class.forName(cn).newInstance();
            System.out.println("app " + app.getName() + " loaded");

            /*
            Iterator<App> apps = ServiceLoader.load(App.class).iterator();

            while (apps.hasNext()) {
                Object o = apps.next();
                System.out.println("found " + o.getClass().getName());
                if (o instanceof App) {
                    app = (App) o;
                    app = apps.next();
                    System.out.println("app " + app.getName() + " loaded");
                }
                break;
            }
            */
        }


        if (app != null) new JavafxPort(app).start(primaryStage);
        else throw new Exception("App not found. Check META-INF/services/io.mateu.ui.App file");

    }


    public static void main(String... args) {
        launch(args);
    }
}