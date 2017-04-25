/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package io.mateu.ui.javafx.views.components.table;

import com.google.common.base.Strings;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import io.mateu.ui.core.client.app.ActionOnRow;
import io.mateu.ui.core.shared.CellStyleGenerator;
import io.mateu.ui.javafx.data.DataStore;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * A class containing a {@link TableCell} implementation that draws a
 * {@link CheckBox} node inside the cell, optionally with a label to indicate
 * what the checkbox represents.
 *
 * <p>By default, the CheckBoxTableCell is rendered with a CheckBox centred in
 * the TableColumn. If a label is required, it is necessary to provide a
 * non-null StringConverter instance to the

 *
 * <p>To construct an instance of this class, it is necessary to provide a
 * {@link Callback} that, given an object of type T, will return an
 * {@code ObservableProperty<Boolean>} that represents whether the given item is
 * selected or not. This ObservableValue will be bound bidirectionally (meaning
 * that the CheckBox in the cell will set/unset this property based on user
 * interactions, and the CheckBox will reflect the state of the ObservableValue,
 * if it changes externally).
 *
 * @param <T> The type of the elements contained within the TableColumn.
 * @since JavaFX 2.2
 */
public class MateuLinkTableCell<S,T> extends TableCell<S,T> {

    private final ActionOnRow action;
    private final CellStyleGenerator cellStyleGenerator;

    /***************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/


    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(final StringConverter<T> converter, ActionOnRow action, CellStyleGenerator cellStyleGenerator) {
        return new Callback<TableColumn<S,T>, TableCell<S,T>>() {
            @Override public TableCell<S,T> call(TableColumn<S,T> list) {
                return new MateuLinkTableCell<S,T>(converter, action, cellStyleGenerator);
            }
        };
    }



    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/


    private boolean showLabel;

    private Property<Object> objectProperty;

    private Hyperlink hyperlink;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/



    /**
     * Creates a CheckBoxTableCell with a custom string converter.
     *
     * @param converter A StringConverter that, given an object of type T, will return a
     *      String that can be used to represent the object visually.
     * @param cellStyleGenerator
     */
    public MateuLinkTableCell(final StringConverter<T> converter, ActionOnRow action, CellStyleGenerator cellStyleGenerator) {

        this.action = action;
        this.cellStyleGenerator = cellStyleGenerator;
        
        // we let getSelectedProperty be null here, as we can always defer to the
        // TableColumn

        this.hyperlink = new Hyperlink();

        hyperlink.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                action.run(((DataStore)getTableRow().getItem()).getData());
            }

        });

        setGraphic(hyperlink);

        //setSelectedStateCallback(getSelectedProperty);
        setConverter(converter);

//        // alignment is styleable through css. Calling setAlignment
//        // makes it look to css like the user set the value and css will not
//        // override. Initializing alignment by calling set on the
//        // CssMetaData ensures that css will be able to override the value.
//        final CssMetaData prop = CssMetaData.getCssMetaData(alignmentProperty());
//        prop.set(this, Pos.CENTER);


    }


    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    // --- converter
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter") {
                protected void invalidated() {
                    updateShowLabel();
                }
            };

    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }



    // --- selected state callback property
    private ObjectProperty<Callback<Integer, ObservableValue<Object>>>
            selectedStateCallback =
            new SimpleObjectProperty<Callback<Integer, ObservableValue<Object>>>(
                    this, "selectedStateCallback");

    /**
     * Property representing the {@link Callback} that is bound to by the
     * CheckBox shown on screen.
     */
    public final ObjectProperty<Callback<Integer, ObservableValue<Object>>> selectedStateCallbackProperty() {
        return selectedStateCallback;
    }

    /**
     * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Object>> value) {
        selectedStateCallbackProperty().set(value);
    }

    /**
     * Returns the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final Callback<Integer, ObservableValue<Object>> getSelectedStateCallback() {
        return selectedStateCallbackProperty().get();
    }



    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        getStyleClass().clear();
        getStyleClass().addAll("cell", "indexed-cell", "table-cell", "table-column");

        if (empty) {
            setText(null);
            setStyle("");
            setGraphic(null);
        } else {
            StringConverter c = getConverter();

            if (showLabel) {
                setText(c.toString(item));
            }
            setGraphic(hyperlink);

            hyperlink.setText(c.toString(item));
            // Format date.
            setStyle("");
            if (getCellStyleGenerator() != null) {
                String s = getCellStyleGenerator().getStyle(item);
                if (s != null) {
                    System.out.println("s=" + s);
                }
                if (s != null) {
                    for (String x : s.split(" ")) {
                        if (!Strings.isNullOrEmpty(x)) {
                            getStyleClass().add(x);
                        }
                    }
                }
                if (s == null) {
                } else if (s.contains("pending")) {
                    setText(null);
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CIRCLE_THIN));
                    setStyle("-fx-alignment: center;");
                } else if (s.contains("done")) {
                    setText(null);
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK));
                    setStyle("-fx-alignment: center;");
                } else if (s.contains("cancelled")) {
                    setText(null);
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
                    setStyle("-fx-alignment: center;");
                } else if (s.contains("expired")) {
                    setText(null);
                    setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOCK_ALT));
                    setStyle("-fx-alignment: center;");
                }
            }
        }

    }



    /***************************************************************************
     *                                                                         *
     * Private implementation                                                  *
     *                                                                         *
     **************************************************************************/

    private void updateShowLabel() {
        //this.showLabel = converter != null;
        //this.checkBox.setAlignment(showLabel ? Pos.CENTER_LEFT : Pos.CENTER);
    }

    private ObservableValue getSelectedProperty() {
        return getSelectedStateCallback() != null ?
                getSelectedStateCallback().call(getIndex()) :
                getTableColumn().getCellObservableValue(getIndex());
    }

    public CellStyleGenerator getCellStyleGenerator() {
        return cellStyleGenerator;
    }
}