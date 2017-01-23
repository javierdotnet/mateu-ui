package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.components.fields.AbstractField;
import io.mateu.ui.core.client.components.fields.PKField;
import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.core.shared.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 8/8/16.
 */
public abstract class AbstractForm extends FieldContainer {

    private String columnWidths = "200,200";

    private Data data;

    private List<DataSetterListener> dataSetterListeners = new ArrayList<>();



    public void addDataSetterListener(DataSetterListener listener) {
        dataSetterListeners.add(listener);
    }

    public void removeDataSetterListener(DataSetterListener listener) {
        dataSetterListeners.remove(listener);
    }

    public void removeAllDataSetterListeners(List<DataSetterListener> listeners) {
        dataSetterListeners.removeAll(listeners);
    }

    private Map<String, List<ChangeListener>> propertyListeners = new HashMap<>();

    public AbstractForm addPropertyListener(String propertyName, ChangeListener listener) {
        if (!propertyListeners.containsKey(propertyName)) propertyListeners.put(propertyName, new ArrayList<>());
        propertyListeners.get(propertyName).add(listener);
        return this;
    }

    public void removePropertyListener(ChangeListener listener) {
        for (List<ChangeListener> l : propertyListeners.values()) if (l.contains(listener)) l.remove(listener);
    }

    public void removePropertyListeners(String propertyName) {
        propertyListeners.remove(propertyName);
    }


    public void removeAllPropertyListeners() {
        propertyListeners.clear();
    }






    public Data initializeData() {
        return new Data();
    }

    public Data getData() {
        if (data == null) setData(initializeData());
        return data;
    }

    public void setData(Data data, boolean only_) {
        Data aux = this.data;
        if (aux == null) aux = new Data();
        if (only_) {
            for (String n : data.getPropertyNames()) if (n.startsWith("_")) {
                aux.set(n, data.get(n));
            }
        } else {
            aux.copy(data);
        }
        this.data = aux;
        for (DataSetterListener l : dataSetterListeners) l.setted(aux);
    }

    public void setData(Data data) {
        setData(data, false);
    }




    public Map<String, List<ChangeListener>> getPropertyListeners() {
        return propertyListeners;
    }

    public AbstractForm add(Component component) {
        super.add(component);
        if (component instanceof PKField) {
            addPropertyListener("_id", new ChangeListener() {
                @Override
                public void changed(Object oldValue, Object newValue) {
                    PKField f = (PKField) component;
                    if (newValue == null) {
                        f.setEnabled(true);
                    } else {
                        f.setEnabled(false);
                    }
                }
            });
        }
        return this;
    }

    public AbstractForm setLastFieldMaximized(boolean value) {
        super.setLastFieldMaximized(value);
        return this;
    }
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        getComponentsSequence().forEach((c) -> {
            if (c instanceof AbstractField) {
                AbstractField f = (AbstractField) c;
                if (f.isRequired() && getData().isEmpty(f.getId())) errors.add("Field " + f.getLabel() + " is required.");
            }
        });

        return errors;
    }

    public String getColumnWidths() {
        return columnWidths;
    }

    public AbstractForm setColumnWidths(String columnWidths) {
        this.columnWidths = columnWidths;
        return this;
    }
}
