package io.mateu.ui.core.client.views;

import io.mateu.ui.core.client.components.Component;
import io.mateu.ui.core.client.components.Tab;
import io.mateu.ui.core.client.components.Tabs;
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

    private FormHelper helper;

    private List<DataSetterListener> dataSetterListeners = new ArrayList<>();

    public AbstractForm() {
        super(null);
    }

    @Override
    public AbstractForm getForm() {
        return this;
    }

    public void set(String k, Object v) {
        for (DataSetterListener l : dataSetterListeners) {
            l.setted(k, v);
        }
    }

    public void setAll(Data data) {
        if (data != null) for (DataSetterListener l : dataSetterListeners) {
            for (String k : data.getPropertyNames()) l.setted(k, data.get(k));
        }
    }

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
        System.out.println("adding listener to property " + propertyName);
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
        if (data == null) data = initializeData();
        return (helper != null)?helper.getData():data;
    }

    public void setData(Data data, boolean only_) {
        Data aux = this.data;
        if (aux == null) aux = new Data();
        if (only_) {
            aux = getData();
            for (String n : data.getPropertyNames()) {
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
        if (component instanceof AbstractField) {
            ((AbstractField)component).setForm(this);
        }
        super.add(component);
        if (component instanceof PKField || (component instanceof AbstractField && ((AbstractField)component).isUnmodifiable())) {
            addPropertyListener("_id", new ChangeListener() {
                @Override
                public void changed(Object oldValue, Object newValue) {
                    System.out.println("_id has changed!");
                    AbstractField f = (AbstractField) component;
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

        validate(getComponentsSequence(), errors);

        return errors;
    }

    private void validate(List<Component> componentsSequence, List<String> errors) {
        componentsSequence.forEach((c) -> {
            if (c instanceof Tabs) {
                for (Tab t : ((Tabs) c).getTabs()) {
                    validate(t.getComponentsSequence(), errors);
                }
            } else if (c instanceof FieldContainer) {
                validate(((FieldContainer) c).getComponentsSequence(), errors);
            } else if (c instanceof AbstractField) {
                AbstractField f = (AbstractField) c;
                if (f.isRequired() && getData().isEmpty(f.getId())) errors.add("Field " + f.getLabel().getText() + " is required.");
            }
        });
    }

    public String getColumnWidths() {
        return columnWidths;
    }

    public AbstractForm setColumnWidths(String columnWidths) {
        this.columnWidths = columnWidths;
        return this;
    }

    public void setHelper(FormHelper helper) {
        this.helper = helper;
    }

    public void resetIds() {
        for (DataSetterListener l : dataSetterListeners) l.idsResetted();
    }
}
