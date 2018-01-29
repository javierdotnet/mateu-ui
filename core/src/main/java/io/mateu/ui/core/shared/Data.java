package io.mateu.ui.core.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.mateu.ui.core.client.data.DataContainer;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by miguel on 21/10/16.
 */
public class Data implements Serializable, DataContainer {

    Map<String, Object> data = new HashMap<>();

    public Data() {
    }

    public Data(String json) {

        Object o = new Jsonizer().parseJson(json);

        if (o instanceof Data) data = ((Data)o).getProperties();
    }

    public Data(Data other) {
        copy(other);
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

    public Data strip(String... ids) {
        Data d = new Data(this);
        for (String id : ids) {
            d.remover(id);
        }
        return d;
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
    public LocalDate getLocalDate(String property) {
        Object d = get(property);
        return (LocalDate) d;
    }

    @Override
    public LocalDateTime getLocalDateTime(String property) {
        Object d = get(property);
        return (LocalDateTime) d;
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
        if (original != null) for (Map.Entry<String, Object> e : original.getProperties().entrySet()) {
            if (e.getValue() instanceof List) {
                List l = getList(e.getKey());
                l.clear();
                for (Object o : ((List) e.getValue())) {
                    l.add(auxCopy(o));
                }
            } else {
                set(e.getKey(), auxCopy(e.getValue()));
            }
        }
    }

    public Object auxCopy(Object o) {
        Object c = null;
        if (o instanceof FileLocator) {
            c = new FileLocator((FileLocator) o);
        } else if (o instanceof Pair) {
            c = new Pair((Pair) o);
        } else if (o instanceof Data) {
            c = new Data((Data)o);
        } else {
            c = o;
        }
        return c;
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
        Data d = new Data(data);
        if (d.containsKey("__id")) d.set("__id", "" + UUID.randomUUID());
        return d;
    }

    @Override
    public String toString() {
        if (containsKey("_text")) return get("_text");
        if (containsKey("_nameproperty")) return get(get("_nameproperty"));
        return new Jsonizer().toJson(this);
    }

    public void setAll(Data data) {
        if (data != null) {
            for (String k : data.getPropertyNames()) {
                set(k, data.get(k));
            }
        }
    }


    public static void main(String... args) {

        String s = "ewoiX3NlbGVjdGVkIiA6ZmFsc2UKLCAiX3N1YnRpdGxlIiA6IjMgcmVjb3JkcyBmb3VuZCBpbiA0bXMuIgosICJfbGlua3MiIDpbXQosICJmIiA6ImV1ciIKLCAiX2RhdGFfdG90YWxyb3dzIiA6MwosICJfX2lkIiA6bnVsbAosICJfdGl0bGUiIDoiQ1JVRCIKLCAiX2RhdGFfcGFnZWNvdW50IiA6MQosICJfYmFkZ2VzIiA6W10KLCAiX2RhdGFfY3VycmVudHBhZ2VpbmRleCIgOjAKLCAiX19fZGF0YWNsYXNzbmFtZSIgOiJpby5tYXRldS51aS5jb3JlLnNoYXJlZC5EYXRhIgp9";

        String s2 = "ewoiX3NlbGVjdGVkIiA6ZmFsc2UKLCAiX3N1YnRpdGxlIiA6IjMgcmVjb3JkcyBmb3VuZCBpbiAxNzY3bXMuIgosICJfbGlua3MiIDpbXQosICJmIiA6ImV1ciIKLCAiX2RhdGFfdG90YWxyb3dzIiA6MwosICJfX2lkIiA6bnVsbAosICJfdGl0bGUiIDoiQ1JVRCIKLCAiX2RhdGFfcGFnZWNvdW50IiA6MQosICJfYmFkZ2VzIiA6W10KLCAiX2RhdGFfY3VycmVudHBhZ2VpbmRleCIgOjAKLCAiX19fZGF0YWNsYXNzbmFtZSIgOiJpby5tYXRldS51aS5jb3JlLnNoYXJlZC5EYXRhIgp9";

        Data dx = new Data(new String(Base64.getDecoder().decode(s)));

        System.out.println("dx=" + dx);

        Data d = new Data(

                "entero", 5, "doble", 3.2, "null", null, "cadena", "qhdoiq diqw \"dwq ede", "array", new int[] {3, 4, 10}
        , "lista", Lists.newArrayList(3.2, 5.1), "data", new Data("nombre", "Mateu", "apellido", "Pérez")
                , "booleano", true

                , "listadedata", Lists.newArrayList(
                new Data("nombre", "Mateu", "apellido", "Pérez", "edad", 9)
                , new Data("nombre", "Miguel", "apellido", "Pérez", "edad", 48)
        )
        );

        System.out.println(d.toString());


        System.out.println("============================");

        Data d2 = new Data(d.toString());

        System.out.println(d2.toString());
    }
}
