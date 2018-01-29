package io.mateu.ui.javafx.data;

import javafx.beans.property.SimpleObjectProperty;

public class DataStoreProperty extends SimpleObjectProperty<DataStore> {

    public DataStoreProperty(DataStore initialValue) {
        super(initialValue);
    }

    public void fireChangeEvent() {
        fireValueChangedEvent();
    }

}
