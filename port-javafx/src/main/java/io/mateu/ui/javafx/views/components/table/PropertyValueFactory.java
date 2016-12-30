package io.mateu.ui.javafx.views.components.table;

import io.mateu.ui.javafx.data.DataStore;
import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;

/**
 * Created by miguel on 30/12/16.
 */
public class PropertyValueFactory<T> extends javafx.scene.control.cell.PropertyValueFactory<DataStore,T> {
    /**
     * Creates a default PropertyValueFactory to extract the value from a given
     * TableView row item reflectively, using the given property name.
     *
     * @param property The name of the property with which to attempt to
     *                 reflectively extract a corresponding value for in a given object.
     */
    public PropertyValueFactory(@NamedArg("property") String property) {
        super(property);
    }

    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<DataStore, T> param) {
        return (ObservableValue<T>) param.getValue().getProperty(getProperty());
    }
}
