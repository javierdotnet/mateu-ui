package io.mateu.ui.core.shared;

import io.mateu.ui.core.client.data.DataContainer;

import java.io.Serializable;
import java.util.*;

/**
 * Created by miguel on 21/10/16.
 */
public class Data implements Serializable, DataContainer {

    Map<String, Object> data = new HashMap<>();

    public Data() {
    }

    public Data(Object... args) {
        super();
        int pos = 0;
        String n = null;
        if (args != null) for (Object x : args) {
            if (pos % 2 == 0) {
                n = (String) x;
            } else {
                data.put(n, x);
            }
            pos++;
        }
    }

    public boolean containsKey(Object k) {
        return data.containsKey(k);
    }

    public Data(Map<String, Object> properties) {
        data = properties;
    }

    public List<Data> getSelection(String property) {
        List<Data> s = new ArrayList<>();
        for (Data x : getList(property)) if (x.getBoolean("_selected")) s.add(x);
        return s;
    }

    @Override
    public String getString(String property) {
        Object v = data.get(property);
        return (v != null)?"" + v : null;
    }

    @Override
    public double getDouble(String property) {
        return (data.get(property) !=null)?(Double) get(property):0;
    }

    @Override
    public boolean getBoolean(String property) {
        return (get(property) != null)?(Boolean) get(property):false;
    }

    @Override
    public Date getDate(String property) {
        Object d = get(property);
        if (d instanceof Long) return new Date((Long) d);
        return (Date) d;
    }

    @Override
    public int getInt(String property) {
        return (get(property) != null)?(Integer) get(property):0;
    }

    @Override
    public long getLong(String property) {
        long l = 0;
        Object o;
        if ((o = get(property)) !=null) {
            if (o instanceof Long) {
                l = (Long) o;
            } else {
                l = new Long("" + o);
            }

        }
        return l;
    }

    @Override
    public List<Data> getList(String property) {
        List<Data> l = get(property);
        if (l == null) {
            l = new ArrayList<Data>();
            set(property, l);
        }
        return l;
    }

    @Override
    public Data getData(String property) {
        return get(property);
    }

    public void copy(Data original) {
        clear();
        putAll(original.getProperties());
    }

    @Override
    public List<Pair> getPairList(String property) {
        return get(property);
    }

    @Override
    public <X> X set(String name, X value) {
        if (value != null && value instanceof Date) value = (X) new Date(((Date)value).getTime());
        return (X) data.put(name, value);
    }

    @Override
    public <X> X get(String property) {
        return (X) data.get(property);
    }

    @Override
    public <X> X get(String property, X valueWhenNull) {
        Object x = get(property);
        return (X) ((x != null)?x:valueWhenNull);
    }

    public Collection<String> getPropertyNames() {
        return data.keySet();
    }

    public Map<String, Object> getProperties() {
        return data;
    }

    public void putAll(Map<? extends String, ? extends Object> m) {
        data.putAll(m);
    }

    public void clear() {
        data.clear();
    }

    public void remover(String property) {
        data.remove(property);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Data && getProperties().equals(((Data)obj).getProperties());
    }


    @Override
    public boolean isEmpty(String property) {
        boolean empty = get(property) == null;
        if (!empty) {
            Object x = get(property);
            empty = x instanceof String && "".equals(((String)x).trim());
        }
        return empty;
    }



    public Object clone() {
        return new Data(data);
    }

    @Override
    public String toString() {
        String s = "";
        //s = super.toString();
        for (String k : data.keySet()) {
            if (data.get(k) instanceof Data) {
                s += "" + k + ":[\n" + ((Data) data.get(k)).toString() + "]";
            } else if (data.get(k) instanceof Data) {
                Data d = (Data) data.get(k);
                s += "" + k + ":(";
                String ss = "";
                for (String n : d.getPropertyNames()) {
                    if (!"".equals(ss)) ss += ",";
                    ss += "" + n + ":" + d.get(n);
                }
                s += ss + ")";
            } else s += "" + k + ":" + data.get(k);
            s += "\n";
        }
        return s;

    }
}
