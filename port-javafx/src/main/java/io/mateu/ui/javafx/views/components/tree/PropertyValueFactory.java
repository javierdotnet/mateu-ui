package io.mateu.ui.javafx.views.components.tree;

import io.mateu.ui.core.shared.Data;
import io.mateu.ui.javafx.data.DataStore;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;

/**
 * Created by miguel on 30/12/16.
 */
public class PropertyValueFactory<T> implements Callback<TreeTableColumn.CellDataFeatures<DataStore,T>, ObservableValue<T>> {
    private final String property;

    public PropertyValueFactory(@NamedArg("property") String property) {
        this.property = property;
    }



    @Override
    public ObservableValue<T> call(TreeTableColumn.CellDataFeatures<DataStore, T> param) {
        ObservableValue<T> v = new ObjectBinding<T>() {
            @Override
            protected T computeValue() {
                if (param.getValue() == null || param.getValue().getValue() == null || param.getValue().getValue().getProperty(property) == null) return null;
                else return (T) param.getValue().getValue().getProperty(property).getValue();
            }
        };
        return v;
    }
}
