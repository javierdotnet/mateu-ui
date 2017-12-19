package io.mateu.ui.javafx.data;

import com.sun.javafx.collections.ObservableMapWrapper;
import io.mateu.ui.core.client.Mateu;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.core.shared.PairList;
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
public class DataStore {

    //private Data data;
    Map<String, Property> props = new LinkedHashMap<String, Property>();

    private String dataClassName;


    private javafx.beans.value.ChangeListener listenerx = new javafx.beans.value.ChangeListener<Object>() {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            for (String k : props.keySet()) if (props.get(k).equals(observable)) {
                //data.set(k, newValue); // set value in form's data
                hasChanged(k, oldValue, newValue);
                break;
            }
        }

    };

    public void hasChanged(String k, Object oldValue, Object newValue) {
    }


    public DataStore(Data data) {
        if (data != null) {
            dataClassName = data.getClass().getName();
            setData(data);
        }
        if (!props.containsKey("_selected")) set("_selected", false);
        if (!props.containsKey("__id")) set("__id", UUID.randomUUID());
    }


    public void setData(Data data) {
        //this.data = data;
        //clear();

        List<String> vistas = new ArrayList<>();

        if (data != null) {
            List<String> ns = new ArrayList<String>(data.getPropertyNames());
            Collections.reverse(ns);
            for (String n : ns)
            {
                if (data.get(n) instanceof Pair || data.get(n) instanceof FileLocator) {
                    set(n, data.get(n));
                } else if (data.get(n) instanceof Data)
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
                        if (y instanceof Pair) {
                            set(n, y);
                        } else if (y instanceof Data) {
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

        for (String n : props.keySet()) if (!n.startsWith("_")) {
            Property p = props.get(n);
            if (p != null && !vistas.contains(n)) {
                if (p.getValue() != null && !(p.getValue() instanceof ObservableList)) {
                    p.setValue(null);
                }
            }
        }
    }

    public Data getData() {
        Data data = new Data();
        fill(data);
        return data;
    }

    private void fill(Data data) {
        for (String n : props.keySet()) {
            Property p = props.get(n);
            if (p != null) {
                Object v = p.getValue();
                if (v != null) {
                    if (v instanceof ObservableList) {
                        List z = (List) p.getValue();
                        List<Object> nl = new ArrayList<>();
                        for (Object vv : z) {
                            if (vv instanceof DataStore) {
                                nl.add(((DataStore) vv).getData());
                            } else {
                                nl.add(vv);
                            }
                        }
                        v = nl;
                    } else if (v instanceof DataStore) {
                        v = ((DataStore) v).getData();
                    }
                }
                data.set(n, v);
            }
        }
    }


    public <X> X set(String name, X value) {
        if (value instanceof Date) {
            Property p = props.get(name);
            if (p != null && p instanceof StringProperty) {
                getProperty(name).setValue((value != null) ? "" + value : null);
            } else {
                Instant instant = Instant.ofEpochMilli(((Date) value).getTime());
                LocalDate res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
                ((Property) getLocalDateTimeProperty(name)).setValue(res);
            }
        } else if (value instanceof FileLocator) {
            getFileLocatorProperty(name).setValue((FileLocator) value);
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
                s += "" + k + ":[\n" + ((DataStore) props.get(k).getValue()).toString() + "]";
            } else if (props.get(k).getValue() instanceof Data) {
                Data d = (Data) props.get(k).getValue();
                s += "" + k + ":(";
                String ss = "";
                for (String n : d.getPropertyNames()) {
                    if (!"".equals(ss)) ss += ",";
                    ss += "" + n + ":" + d.get(n);
                }
                s += ss + ")";
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
            props.put(id, p = new SimpleObjectProperty<Double>());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Integer> getIntegerProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<Integer>());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Long> getLongProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<Long>());
            p.addListener(listenerx);
        }
        return p;
    }
    public Property<Number> getNumberProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<Integer>());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<Boolean> getBooleanProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<Boolean>());
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


    public Property<Pair> getPairProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<Pair>());
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<PairList> getPairListProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<PairList>(new PairList()));
            p.addListener(listenerx);
        }
        return p;
    }

    public Property<FileLocator>  getFileLocatorProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<FileLocator>());
            p.addListener(listenerx);
        }
        return p;
    }

    @Override
    public boolean equals(Object obj) {
        boolean eq = false;
        Object k = get("__id");
        if (k != null) eq = obj instanceof DataStore && k.equals(((DataStore)obj).get("__id"));
        else eq = super.equals(obj);
        return eq;
    }

    public Set<String> keySet() {
        return props.keySet();
    }

    public int size() {
        return props.size();
    }

    public boolean isEmpty() {
        return props.isEmpty();
    }


    public void clear() {
        props.clear();
    }

    public void resetIds() {
        for (String n : props.keySet()) {
            if ("_id".equals(n)) props.get(n).setValue(null);
            else if (props.get(n).getValue() != null) {
                Class c = props.get(n).getValue().getClass();
                if (List.class.isAssignableFrom(c)) {
                    for (Object o : ((List)props.get(n).getValue())) {
                        if (o instanceof DataStore) {
                            DataStore x = (DataStore) o;
                            x.resetIds();
                        }
                    }
                }
            }
        }
    }

}
