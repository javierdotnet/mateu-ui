package io.mateu.ui.vaadin.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sun.javafx.collections.ObservableMapWrapper;
import io.mateu.ui.core.client.Mateu;
import io.mateu.ui.core.client.data.ChangeListener;
import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.FileLocator;
import io.mateu.ui.core.shared.Pair;
import io.mateu.ui.core.shared.PairList;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
                System.out.println("data.set(" + k + ", " + newValue + ")");
                if (newValue instanceof ArrayList) {
                    List ll = new ArrayList();
                    for (Object o : (List) newValue) {
                        if (o instanceof DataStore) {
                            data.set(k, ((DataStore)o).getData());
                        } else data.set(k, o);
                    }
                } else if (newValue instanceof DataStore) {
                    data.set(k, ((DataStore)newValue).getData());
                } else data.set(k, newValue); // set value in form's data
                hasChanged(k, oldValue, newValue);
                break;
            }
        }

    };

    public void hasChanged(String k, Object oldValue, Object newValue) {
    }


    public DataStore(Data data) {
        super(new HashMap<String, Object>());
        setData(data);
        set("_selected", false);
    }


    public void setData(Data data) {
        if (this.data == null) this.data = data;
        //clear();

        List<String> vistas = new ArrayList<>();

        if (data != null) {
            List<String> ns = new ArrayList<String>(data.getPropertyNames());
            Collections.reverse(ns);
            for (String n : ns)
            {
                if (data.get(n) instanceof Pair) {
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
                    List ll = new ArrayList();
                    for (Object y : (List) data.get(n))
                    {
                        if (y instanceof Pair) {
                            ll.add(new DataStore((Pair)y));
                        } else if (y instanceof Data) {
                            DataStore x = new DataStore((Data) y);
                            ll.add(x);
                        } else {
                            ll.add(y);
                        }
                    }
                    l.setAll(ll);
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
        if (value instanceof List<?>) {
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

    public Property<Long> getLongProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleLongProperty());
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
