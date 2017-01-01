package io.mateu.ui.core.server;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by miguel on 1/1/17.
 */
public class EditorViewControllerRegistry {

    private static boolean built;

    private static Map<String, ServerSideEditorViewController> controllersMap = new HashMap<>();


    public static void build() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Reflections reflections = new Reflections(Class.forName(ServerSideHelper.read(EditorViewControllerRegistry.class.getResourceAsStream("/META-INF/services/io.mateu.ui.serversideapp"))).getPackage().getName());

        Set<Class<? extends ServerSideEditorViewController>> controllers =
                reflections.getSubTypesOf(ServerSideEditorViewController.class);

        for (Class c : controllers) {
            ServerSideEditorViewController i = (ServerSideEditorViewController) c.newInstance();
            controllersMap.put(i.getKey(), i);
        }

    }

    public static ServerSideEditorViewController getController(String key) throws Exception {
        if (!built) {
            build();
            built = true;
        }
        return controllersMap.get(key);
    }

}
