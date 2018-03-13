package io.mateu.ui.javafx.newlayout;

import com.google.common.base.Strings;
import com.google.common.io.BaseEncoding;
import io.mateu.ui.core.client.app.MateuUI;
import io.mateu.ui.core.client.views.AbstractEditorView;
import io.mateu.ui.core.client.views.AbstractListView;
import io.mateu.ui.core.client.views.AbstractView;
import io.mateu.ui.core.client.views.ViewListener;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.ViewProvider;
import io.mateu.ui.javafx.views.ViewNode;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class VistaActualNode extends StackPane {

    private static VistaActualNode singleton;

    private List<String> historial = new ArrayList<>();

    private List<ViewProvider> navigator = new ArrayList<>();

    public VistaActualNode() {
        singleton = this;

        ServiceLoader<ViewProvider> sl = ServiceLoader.load(ViewProvider.class);

        setPrefWidth(3000);

        for (ViewProvider p : sl) navigator.add(p);
    }

    public static VistaActualNode get() {
        return singleton;
    }

    public void cargar(String url) {

        getChildren().clear();


        if (!Strings.isNullOrEmpty(url)) {

            if (historial.size() == 0 || !historial.get(historial.size() - 1).equals(url)) {
                historial.add(url);
            }

            AbstractView v = null;

            for (ViewProvider p : navigator) {
                v = p.getView(p.getViewName(url));
                if (v != null) break;
            }

            if (v != null) {

                getChildren().add(new ViewNode(v));

                v.addListener(new ViewListener() {
                    @Override
                    public void onClose() {
                        back();
                    }
                });

                if (v instanceof AbstractEditorView) {
                    Object id = null;
                    String s = getParameters(url);
                    if (s != null) {
                        if (s.startsWith("s")) id = s.substring(1);
                        else if (s.startsWith("l")) id = Long.parseLong(s.substring(1));
                        else if (s.startsWith("i")) id = Integer.parseInt(s.substring(1));
                    }
                    if (id != null) {
                        ((AbstractEditorView) v).setInitialId(id);
                        ((AbstractEditorView) v).load();
                    }
                } else if (v instanceof AbstractListView) {
                    AbstractListView lv = (AbstractListView) v;
                    Data data = null;

                    int page = 0;
                    String s = getParameters(url);
                    if (!Strings.isNullOrEmpty(s)) {

                        String d = s;
                        if (s.contains("/")) {
                            d = s.split("/")[0];
                            page = Integer.parseInt(s.split("/")[1]);
                        }

                        data = new Data(new String(BaseEncoding.base64().decode(d)));
                    }

                    if (data != null) {
                        lv.setData(data);
                    }

                    if (lv.isSearchOnOpen() || (data != null && data.getPropertyNames().size() > 0)) {
                        lv.set("_data_currentpageindex", page);
                        List<String> errors = lv.getForm().validate();
                        if (errors.size() > 0) {
                            MateuUI.notifyErrors(errors);
                        } else {
                            lv.rpc();
                        }
                    }

                }

            } else {
                WebView w;
                getChildren().add(w = new WebView());
                w.setPrefWidth(3000);
                w.getEngine().load(url);
            }

        }

    }

    private String getParameters(String url) {
        String p = "";
        if (!url.endsWith("/") && url.contains("/")) p = url.substring(url.indexOf("/") + 1);
        return p;
    }

    public void back() {
        if (historial.size() > 0) {
            historial.remove(historial.get(historial.size() - 1));
            BarraDireccionesNode.get().cargar((historial.size() > 0)?historial.get(historial.size() - 1):"");
        } else {
            BarraDireccionesNode.get().cargar("");
        }
    }

}
