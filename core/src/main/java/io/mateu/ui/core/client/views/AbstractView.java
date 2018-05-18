package io.mateu.ui.core.client.views;

import com.google.common.io.BaseEncoding;
import io.mateu.ui.core.client.app.AbstractAction;
import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.MenuEntry;
import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.UserData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractView {

    private AbstractArea area;
    private MenuEntry menu;
    private AbstractForm form;
    private List<AbstractAction> actions;
    private List<AbstractAction> links;
    private List<ViewListener> listeners = new ArrayList<>();
    private Data initialData = new Data();
    private String parametros;
    private boolean granted = true;

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public Data getInitialData() {
        return initialData;
    }

    public void setInitialData(Data initialData) {
        this.initialData = initialData;
    }

    public AbstractArea getArea() {
        return area;
    }

    public void setArea(AbstractArea area) {
        this.area = area;
    }

    public MenuEntry getMenu() {
        return menu;
    }

    public void setMenu(MenuEntry menu) {
        this.menu = menu;
    }

    public abstract String getTitle();

    public abstract void build();

    public AbstractView onOpen(UserData user) {
        return this;
    }

    public AbstractForm createForm() {
        return new ViewForm(this);
    }

    public AbstractForm getForm() {
        if (form == null) {
            form = createForm();
            build();
        }
        return form;
    }


    public AbstractView add(Component component) {
        if (form == null) {
            form = createForm();
        }
        form.add(component);
        return this;
    }

    public void close() {
        for (ViewListener l : listeners) l.onClose();
    }

    public void addListener(ViewListener listener) {
        listeners.add(listener);
    }

    public AbstractView addPropertyListener(String propertyName, ChangeListener listener) {
        getForm().addPropertyListener(propertyName, listener);
        return this;
    }

    public List<AbstractAction> createActions() {
        return new ArrayList<>();
    }

    public List<AbstractAction> getActions() {
        if (actions == null) actions = createActions();
        return actions;
    }

    public List<AbstractAction> createLinks() {
        return new ArrayList<>();
    }

    public List<AbstractAction> getLinks() {
        if (links == null) links = createLinks();
        return links;
    }

    public String getViewId() {
        String i = getViewIdBase();
        Data d = getData();
        if (d != null && !d.getPropertyNames().isEmpty()) i += ".." + BaseEncoding.base64().encode(getData().toString().getBytes());
        return i;
    }

    public String getViewIdBase() {
        return "mui/" + getClass().getName();
    }

    public Data initializeData() {
        return getInitialData();
    }


    public Object get(String k) {
        return getForm().getData().get(k);
    }

    public void set(String k, Object v) {
        getForm().set(k, v);
    }

    public void clear() {
        getForm().clear();
    }

    public Data getData() {
        return getForm().getData();
    }

    public void setData(Data data, boolean only_) {
        getForm().setData(data, only_);
    }

    public void setData(Data data) {
        getForm().setData(data);
    }

    public void setAll(Data data) {
        getForm().setAll(data);
    }

    public void setParametros(String parametros) {
        this.parametros = parametros;
    }

    public String getParametros() {
        return parametros;
    }
}
