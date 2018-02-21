package io.mateu.ui.javafx.newlayout;

import com.google.common.base.Strings;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.shared.ViewProvider;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class VistaActualNode extends StackPane {

    private static VistaActualNode singleton;

    private List<ViewProvider> navigator = new ArrayList<>();

    public VistaActualNode() {
        singleton = this;

        ServiceLoader<ViewProvider> sl = ServiceLoader.load(ViewProvider.class);

        for (ViewProvider p : sl) navigator.add(p);
    }

    public static VistaActualNode get() {
        return singleton;
    }

    public void cargar(String url) {

        getChildren().clear();


        if (!Strings.isNullOrEmpty(url)) {

            if (url.startsWith("mui..") || url.startsWith("mdd..")) {
                getChildren().add(new ViewNode(navigator.get(0).getView(url)));
            } else {
                WebView w;
                getChildren().add(w = new WebView());
                w.setPrefWidth(3000);
                w.getEngine().load(url);
            }

        }

    }
}
