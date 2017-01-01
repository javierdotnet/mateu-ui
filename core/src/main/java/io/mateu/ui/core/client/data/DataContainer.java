package io.mateu.ui.core.client.data;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.core.shared.Pair;

import java.util.Date;
import java.util.List;

/**
 * Created by miguel on 29/12/16.
 */
public interface DataContainer {
    String getString(String property);

    double getDouble(String property);

    boolean getBoolean(String property);

    Date getDate(String property);

    int getInt(String property);

    long getLong(String property);

    List<Data> getList(String property);

    Data getData(String property);

    List<Pair> getPairList(String property);

    <X> X set(String name, X value);

    <X> X get(String property);

    <X> X get(String property, X valueWhenNull);

    boolean isEmpty(String property);


}
