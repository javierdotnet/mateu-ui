package io.mateu.ui.core.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by miguel on 21/10/16.
 */
public interface DataContainer {

    public String getString(String property);

    public double getDouble(String property);

    public boolean getBoolean(String property);

    public Date getDate(String property);

    public int getInt(String property);

    public long getLong(String property);

    public List<DataContainer> getList(String property);

    public DataContainer getFila(String property);

    public void copiar(DataContainer modelo);

    public List<Pair> getListaPares(String property);

    public <X> X set(String name, X value);

    public <X> X get(String property);

    public <X> X get(String property, X valueWhenNull);

    public Collection<String> getPropertyNames();

    public Map<String, Object> getProperties();

    public void putAll(Map<? extends String, ? extends Object> m);

    public void clear();

    public void remover(String property);

    public boolean igual(DataContainer data);

    public boolean estaVacio(String property);

    public void addListener(String propertyName, ChangeListener listener);

    public void removeListener(ChangeListener listener);

    public void removeListeners(String propertyName);

    public void removeAllListeners();

    public Object clone();

}
