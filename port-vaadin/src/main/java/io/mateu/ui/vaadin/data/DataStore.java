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
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Created by miguel on 29/12/16.
 */
public class DataStore extends ObservableMapWrapper<String, Object> {

    private Data data = new Data();
    Map<String, Property> props = new LinkedHashMap<String, Property>();

    private javafx.beans.value.ChangeListener listenerx = new javafx.beans.value.ChangeListener<Object>() {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            for (String k : props.keySet()) if (props.get(k).equals(observable)) {
                //System.out.println("pasando valor con data.set(" + k + ", " + newValue + ")");
                if (newValue != null) {
                    if (List.class.isAssignableFrom(newValue.getClass())) {
                        List ll = new ArrayList();
                        for (Object o : (List) newValue) {
                            if (o instanceof DataStore) {
                                ll.add(((DataStore)o).getData());
                            } else ll.add(o);
                        }
                        data.set(k, ll);
                    } else if (newValue instanceof DataStore) {
                        data.set(k, ((DataStore)newValue).getData());
                    } else data.set(k, newValue); // set value in form's data
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
        if (!props.containsKey("__id")) set("__id", UUID.randomUUID());
    }


    public void setData(Data data) {
        if (this.data == null && data != null) this.data = data;
        //clear();

        List<String> vistas = new ArrayList<>();

        if (data != null) {
            List<String> ns = new ArrayList<String>(data.getPropertyNames());
            Collections.reverse(ns);
            for (String n : ns)
            {
                Object v = data.get(n);
                //System.out.println("datastore.setdata(" + n + "," + v + ")");
                if (v != null) {
                    if (v instanceof Pair || v instanceof FileLocator) {
                        set(n, v);
                    } else if (v instanceof Data)
                    {
                        DataStore x = new DataStore((Data) v);
                        set(n, x);
                    }
                    else if (List.class.isAssignableFrom(v.getClass()))
                    {

                        ObservableList l = getObservableListProperty(n).getValue();
                        //if (l instanceof FilteredList) l = ((FilteredList)l).getSource();
                        List ll = new ArrayList();
                        for (Object y : (List) v)
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
                        set(n, v);
                } else {
                    set(n, v);
                }
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
                    } else if (v instanceof Optional) {
                        Object z = ((Optional)v).get();
                        if (z != null && z instanceof DataStore) v = ((DataStore) z).getData();
                    }
                }
                data.set(n, v);
            }
        }
    }


    public <X> X set(String name, X value) {
        //System.out.println("datastore.set(" + name + "," + value + ")");
        if (value != null && List.class.isAssignableFrom(value.getClass())) {
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

    @Override
    public boolean equals(Object obj) {
        boolean eq = false;
        Object k = get("__id");
        if (k != null) eq = obj instanceof DataStore && k.equals(((DataStore)obj).get("__id"));
        else eq = super.equals(obj);
        return eq;
    }

    @Override
    public int hashCode() {
        return get("__id").hashCode();
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
            props.put(id, p = new SimpleObjectProperty<Number>());
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
        //System.out.println("datastore.getObservableListProperty(" + id + ")");
        Property p = props.get(id);
        if (p == null) {
            //System.out.println("creamos la propiedad");
            ObservableList<Object> ocl;
            props.put(id, p = new SimpleObjectProperty(ocl = FXCollections.observableArrayList()));
            p.addListener(listenerx);
            Property finalP = p;
            ocl.addListener(new ListChangeListener() {
                @Override
                public void onChanged(Change c) {
                    listenerx.changed(finalP, null, ocl);
                }
            });
        } else {
            //System.out.println("la propiedad " + id + " ya existe");
        }
        if (p.getValue() == null) {
            ObservableList<Object> ocl;
            p.setValue(ocl = FXCollections.observableArrayList());
            Property finalP1 = p;
            ocl.addListener(new ListChangeListener() {
                @Override
                public void onChanged(Change c) {
                    listenerx.changed(finalP1, null, ocl);
                }
            });
        }
        //System.out.println("p.getValue = " + p.getValue());
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

    public void setAll(Data data) {
        if (data != null) {
            for (String k : data.getPropertyNames()) set(k, data.get(k));
        }
    }
}
