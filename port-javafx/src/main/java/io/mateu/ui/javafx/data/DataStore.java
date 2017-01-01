package io.mateu.ui.javafx.data;

import com.sun.javafx.collections.ObservableMapWrapper;
import io.mateu.ui.core.client.Mateu;
import io.mateu.ui.core.shared.Data;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by miguel on 29/12/16.
 */
public class DataStore extends ObservableMapWrapper<String, Object> {

    private Data data;
    Map<String, Property> props = new LinkedHashMap<String, Property>();


    private javafx.beans.value.ChangeListener listenerx = new javafx.beans.value.ChangeListener<Object>() {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            for (String k : props.keySet()) if (props.get(k).equals(observable)) {
                data.set(k, newValue); // set value in form's data
                hasChanged(k, oldValue, newValue);
                break;
            }
        }

    };

    public void hasChanged(String k, Object oldValue, Object newValue) {
    }


    public DataStore(Data data) {
        super(new HashMap<String, Object>());
        set("_selected", false);
        setData(data);
    }


    public void setData(Data data) {
        this.data = data;
        //clear();

        List<String> vistas = new ArrayList<>();

        if (data != null) {
            List<String> ns = new ArrayList<String>(data.getPropertyNames());
            Collections.reverse(ns);
            for (String n : ns)
            {
                if (data.get(n) instanceof Data)
                {
                    DataStore x = new DataStore(data.get(n));
                    set(n, x);
                }
                else if (data.get(n) instanceof ArrayList || data.get(n) instanceof ObservableList)
                {
                    ObservableList l = getObservableListProperty(n).getValue();
                    //if (l instanceof FilteredList) l = ((FilteredList)l).getSource();
                    l.clear();
                    for (Object y : (List) data.get(n))
                    {
                        if (y instanceof Data) {
                            DataStore x = new DataStore((Data) y);
                            l.add(x);
                        } else {
                            l.add(y);
                        }
                    }
                }
                else
                    set(n, data.get(n));
                vistas.add(n);
            }

        }

        for (String n : props.keySet()) {
            Property p = props.get(n);
            if (p != null && !vistas.contains(n)) {
                if (p.getValue() != null && !(p.getValue() instanceof ObservableList)) {
                    p.setValue(null);
                }
            }
        }

        if (data != null) putAll(data.getProperties());
    }

    public Data getData() {
        return data;
    }


    public <X> X set(String name, X value) {
        if (value instanceof Date) {
            Property p = props.get(name);
            if (p != null && p instanceof StringProperty) {
                getProperty(name).setValue((value != null)? "" + value:null);
            } else {
                Instant instant = Instant.ofEpochMilli(((Date)value).getTime());
                LocalDate res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
                ((Property)getLocalDateTimeProperty(name)).setValue(res);
            }
        } else if (value instanceof List<?>) {
            Property<ObservableList<DataStore>> p = getObservableListProperty(name);
            p.getValue().clear();
            for (Data x : (List<Data>) value) p.getValue().add(new DataStore(x));
        } else {
            try {
                if (value != null && value instanceof Boolean) {
                    ((Property)getBooleanProperty(name)).setValue(value);
                } else {
                    ((Property)getProperty(name)).setValue(value);
                }

            } catch (ClassCastException e) {
                ((Property)getProperty(name)).setValue("" + value);
            }
        }
        return value;
    }

    public <X> X get(String property) {
        X value = (X) ((Property)getProperty(property)).getValue();
        if (value != null && value instanceof LocalDate) {
            Instant instant = Instant.from(((LocalDate)value).atStartOfDay(ZoneId.systemDefault()));
            value = (X) Date.from(instant);
        }
        return value;
    }

    public <X> X get(String property, X valueWhenNull) {
        X x = get(property);
        return (x == null)?valueWhenNull:x;
    }

    @Override
    public String toString() {
        String s = "";
        //s = super.toString();
        for (String k : props.keySet()) {
            if (props.get(k).getValue() instanceof DataStore) {
                s += "" + k + ":[\n" + ((DataStore)props.get(k).getValue()).toString() + "]";
            } else s += "" + k + ":" + props.get(k).getValue();
            s += "\n";
        }
        return s;
    }

    public Property<String> getStringProperty(String id) {
        Property p = props.get(id);
        if (p != null && !(p instanceof SimpleStringProperty)) {
            Object v = p.getValue();
            p.unbind();
            props.put(id, p = new SimpleStringProperty());
            if (v != null) p.setValue("" + v);
            p.addListener(listenerx);
        }
        if (p == null) {
            props.put(id, p = new SimpleStringProperty());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Data> getDataProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<Data>(Mateu.createNewDataContainer()));
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<LocalDate> getLocalDateProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<LocalDate>());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<LocalDateTime> getLocalDateTimeProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<LocalDateTime>());
            p.addListener(listenerx);
        }
        return p;
    }


    public Property<Double> getDoubleProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleDoubleProperty());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Integer> getIntegerProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleIntegerProperty());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Number> getNumberProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleIntegerProperty());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Boolean> getBooleanProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleBooleanProperty());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Object> getProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            p = new SimpleObjectProperty();
            props.put(id, p);
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<ObservableList<DataStore>> getObservableListProperty(
            String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty(FXCollections.observableArrayList()));
            p.addListener(listenerx);
        }
        if (p.getValue() == null) p.setValue(FXCollections.observableArrayList());
        return p;
    }

    public Property<LocalDateTime> getDateProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<LocalDateTime>());
            p.addListener(listenerx);
        }
        return p;
    }





}
