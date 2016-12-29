package io.mateu.ui.javafx.data;

import com.sun.javafx.collections.ObservableMapWrapper;
import io.mateu.ui.core.Mateu;
import io.mateu.ui.core.data.ChangeListener;
import io.mateu.ui.core.data.DataContainer;
import io.mateu.ui.core.data.GridFilter;
import io.mateu.ui.core.data.Pair;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by miguel on 23/10/16.
 */
public class DataContainerImpl extends ObservableMapWrapper<String, Object> implements DataContainer {

    Map<String, Property> props = new LinkedHashMap<String, Property>();
    private int hash;
    private Map<String, List<ChangeListener>> listeners = new LinkedHashMap<String, List<ChangeListener>>();
    private javafx.beans.value.ChangeListener listenerx = new javafx.beans.value.ChangeListener<Object>() {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            for (String k : props.keySet()) if (props.get(k).equals(observable)) {
                if (listeners.containsKey(k)) for (ChangeListener l : listeners.get(k)) {
                    l.changed(oldValue, newValue);
                }
                break;
            }
        }
    };
    private static int hashCount;

    static {
        hashCount = 1;
    }

    public DataContainerImpl() {
        super(new HashMap<String, Object>());
    }

    public DataContainerImpl(Map<String, Object> arg0) {
        super(new HashMap<String, Object>());
        for (String k : arg0.keySet()) {
            Object v = arg0.get(k);
            set(k, v);
        }
    }

    @Override
    public String getString(String propiedad) {
        Object s = get(propiedad);
        if (s == null) return null;
        else if (s instanceof String) return (String) s;
        else return "" + s;
    }

    @Override
    public double getDouble(String propiedad) {
        double i= 0;
        if (get(propiedad)!= null) i = (double) get(propiedad);
        return i;
    }

    @Override
    public boolean getBoolean(String propiedad) {
        boolean b = false;
        if (get(propiedad)!= null) b = (boolean) get(propiedad);
        return b;
    }

    @Override
    public Date getDate(String propiedad) {
        return get(propiedad);
    }

    @Override
    public int getInt(String propiedad) {
        int i= 0;
        if (get(propiedad)!= null) i = (int) get(propiedad);
        return i;
    }

    @Override
    public long getLong(String propiedad) {
        long i= 0;
        Object o;
        if ((o = get(propiedad))!= null) {
            if (o instanceof Long) i = (long) o;
            else if (o instanceof Integer) i = ((Integer)o).longValue();
        }
        return i;
    }

    @Override
    public List<DataContainer> getList(String propiedad) {
        List<DataContainer> l = getObservableListProperty2(propiedad).getValue();
        if (l instanceof FilteredList) l = ((FilteredList)l).getSource();
        return l ;
    }

    @Override
    public DataContainer getFila(String propiedad) {
        DataContainer d = getFilaProperty(propiedad).getValue();
        return d;
    }

    @Override
    public void copiar(DataContainer modelo) {
        clear();

        for (Property p : props.values()) {
            if (p != null) {
                if (p.getValue() != null && !(p.getValue() instanceof ObservableList)) {
                    p.setValue(null);
                }
            }
        }

        if (modelo != null) {
            List<String> ns = new ArrayList<String>(modelo.getPropertyNames());
            Collections.reverse(ns);
            for (String n : ns)
            {
                if (modelo.get(n) instanceof DataContainer)
                {
                    DataContainer x = Mateu.createNewDataContainer();
                    x.copiar((DataContainer) modelo.get(n));
                    set(n, x);
                }
                else if (modelo.get(n) instanceof ArrayList || modelo.get(n) instanceof ObservableList)
                {
                    ObservableList l = getObservableListProperty2(n).getValue();
                    if (l instanceof FilteredList) l = ((FilteredList)l).getSource();
                    l.clear();
                    for (Object y : (List) modelo.get(n))
                    {
                        if (y instanceof DataContainer) {
                            DataContainer z = Mateu.createNewDataContainer();
                            z.copiar((DataContainer) y);
                            l.add(z);
                        } else if (y instanceof Pair) {
                            Pair z = (Pair) y;
                            l.add(new PairImpl(z.getValue(), z.getText()));
                        } else {
                            l.add(y);
                        }
                    }
                }
                else
                    set(n, modelo.get(n));
            }

        }

        if (modelo != null) putAll(modelo.getProperties());
    }

    @Override
    public List<Pair> getListaPares(String propiedad) {
        List<Pair> l = getObservableListParesProperty2(propiedad).getValue();
        return l;
    }

    @Override
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
            Property<ObservableList<DataContainer>> p = getObservableListProperty2(name);
            p.getValue().clear();
            p.getValue().addAll((List<DataContainer>)value);
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

    @Override
    public <X> X get(String property) {
        X value = (X) ((Property)getProperty(property)).getValue();
        if (value != null && value instanceof LocalDate) {
            Instant instant = Instant.from(((LocalDate)value).atStartOfDay(ZoneId.systemDefault()));
            value = (X) Date.from(instant);
        }
        return value;
    }

    @Override
    public <X> X get(String property, X valueWhenNull) {
        X x = get(property);
        return (x == null)?valueWhenNull:x;
    }

    @Override
    public Collection<String> getPropertyNames() {
        return props.keySet();
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> m = new HashMap<String, Object>();
        for (String k : props.keySet()) {
            Object v = props.get(k).getValue();
            if (v != null && v instanceof LocalDateTime) v = Date.from(((LocalDateTime)v).atZone(ZoneId.systemDefault()).toInstant());
            else if (v != null && v instanceof LocalDate) v = Date.from(((LocalDate)v).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            m.put(k, v);
        }
        return m;
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        super.putAll(m);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void remover(String propiedad) {
        remove(propiedad);
    }

    @Override
    public boolean igual(DataContainer data) {
        return getProperties().equals(data.getProperties());
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

    public Property<DataContainer> getFilaProperty(String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty<DataContainer>(Mateu.createNewDataContainer()));
            p.addListener(listenerx);
        }
        return p;
    }

    @Override
    public String toString() {
        String s = "";
        //s = super.toString();
        for (String k : props.keySet()) {
            System.out.println("" + k + ":" + props.get(k).getValue());
        }
        return s;
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
            Object v = super.get(id);
            if (v != null) {
                if (v instanceof Date) p = new SimpleObjectProperty<LocalDateTime>();
                else if (v instanceof Boolean) p = new SimpleBooleanProperty();
                else if (v instanceof Integer) p = new SimpleIntegerProperty();
                else if (v instanceof Double) p = new SimpleDoubleProperty();
            }
            if (p == null) p = new SimpleObjectProperty();
            props.put(id, p);
            p.addListener(listenerx);
        }
        return p;
    }

    @Override
    public boolean estaVacio(String propiedad) {
        boolean vacio = get(propiedad) == null;
        if (!vacio) {
            Object x = get(propiedad);
            vacio = x instanceof String && "".equals(((String)x).trim());
        }
        return vacio;
    }

    public Property<ObservableList<Map<String, SimpleObjectProperty<Object>>>> getObservableListProperty(
            String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty(FXCollections.observableArrayList()));
            p.addListener(listenerx);
        }
        if (p.getValue() == null) p.setValue(FXCollections.observableArrayList());
        return p;
    }

    public Property<ObservableList<DataContainer>> getObservableListProperty2(
            String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty(FXCollections.observableArrayList()));
            p.addListener(listenerx);
        }
        if (p.getValue() == null) p.setValue(FXCollections.observableArrayList());
        return p;
    }

    public Property<ObservableList<DataContainerImpl>> getObservableListProperty3(
            String id) {
        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty(FXCollections.observableArrayList()));
            p.addListener(listenerx);
        }
        if (p.getValue() == null) p.setValue(FXCollections.observableArrayList());
        return p;
    }

    public Property<ObservableList<Pair>> getObservableListParesProperty2(
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

    @Override
    public boolean equals(Object arg0) {
        return this == arg0;
    }

    @Override
    public int hashCode() {
        if (hash == 0) hash = hashCount++;
        return hash;
    }

    public Property<ObservableList<DataContainerImpl>> getFilteredObservableListProperty3(
            String id, List<GridFilter> filtros) {

        Property p = props.get(id);
        if (p == null) {
            props.put(id, p = new SimpleObjectProperty(FXCollections.observableArrayList()));
            p.addListener(listenerx);
        }
        if (p.getValue() == null) {
            p.setValue(FXCollections.observableArrayList());
        }

        Property pf = props.get(id + "_filtered");
        if (pf == null) {
            props.put(id + "_filtered", pf = new SimpleObjectProperty(getNewFilteredList((ObservableList<DataContainerImpl>) p.getValue(), filtros)));
            p.addListener(listenerx);
        }
        if (pf.getValue() == null) {
            pf.setValue(getNewFilteredList((ObservableList<DataContainerImpl>) p.getValue(), filtros));
        }
        return pf;

    }

    private FilteredList<DataContainerImpl> getNewFilteredList(
            ObservableList<DataContainerImpl> l, final List<GridFilter> filtros) {
        final FilteredList<DataContainerImpl> filteredData = new FilteredList(l);

        if (filtros != null && filtros.size() > 0) filteredData.setPredicate(new Predicate<DataContainerImpl>() {

            @Override
            public boolean test(DataContainerImpl t) {
                boolean ok = true;

                for (GridFilter f : filtros) {
                    ok &= f.matches(t);
                    if (!ok) break;
                }

                return ok;
            }
        });
        else filteredData.setPredicate(new Predicate<DataContainerImpl>() {

            @Override
            public boolean test(DataContainerImpl t) {
                return true;
            }
        });

        return filteredData;
    }

    @Override
    public void addListener(String propertyName, ChangeListener listener) {
        List<ChangeListener> l = listeners.get(propertyName);
        if (l == null) listeners.put(propertyName, l = new ArrayList<ChangeListener>());
        if (!l.contains(listener)) l.add(listener);
    }

    @Override
    public void removeListener(ChangeListener listener) {
        for (List<ChangeListener> l : listeners.values()) l.remove(listener);
    }

    @Override
    public void removeAllListeners() {
        listeners.clear();
    }

    @Override
    public void removeListeners(String propertyName) {
        listeners.remove(propertyName);
    }

    @Override
    public Object clone() {
        return new DataContainerImpl(getProperties());
    }
}
