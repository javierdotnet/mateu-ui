/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package io.mateu.ui.javafx.views.components.table;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * A class containing a {@link TableCell} implementation that draws a
 * {@link TextField} node inside the cell.
 *
 * <p>By default, the TextFieldTableCell is rendered as a {@link Label} when not
 * being edited, and as a TextField when in editing mode. The TextField will, by
 * default, stretch to fill the entire table cell.
 *
 * @param <T> The type of the elements contained within the TableColumn.
 * @since JavaFX 2.2
 */
public class MateuIntegerFieldTableCell<S,T> extends TableCell<S,T> {

    /***************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/

    /**
     * Provides a {@link TextField} that allows editing of the cell content when
     * the cell is double-clicked, or when
     * {@link TableView#edit(int, TableColumn)} is called.
     * This method will only  work on {@link TableColumn} instances which are of
     * type String.
     *
     * @return A {@link Callback} that can be inserted into the
     *      {@link TableColumn#cellFactoryProperty() cell factory property} of a
     *      TableColumn, that enables textual editing of the content.
     */
    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter());
    }

    /**
     * Provides a {@link TextField} that allows editing of the cell content when
     * the cell is double-clicked, or when
     * {@link TableView#edit(int, TableColumn) } is called.
     * This method will work  on any {@link TableColumn} instance, regardless of
     * its generic type. However, to enable this, a {@link StringConverter} must
     * be provided that will convert the given String (from what the user typed
     * in) into an instance of type T. This item will then be passed along to the
     * {@link TableColumn#onEditCommitProperty()} callback.
     *
     * @param converter A {@link StringConverter} that can convert the given String
     *      (from what the user typed in) into an instance of type T.
     * @return A {@link Callback} that can be inserted into the
     *      {@link TableColumn#cellFactoryProperty() cell factory property} of a
     *      TableColumn, that enables textual editing of the content.
     */
    public static <S,T> Callback<TableColumn<S,T>, TableCell<S,T>> forTableColumn(
            final StringConverter<T> converter) {
        return list -> new MateuIntegerFieldTableCell<S,T>(converter);
    }


    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private TextField textField;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a default TextFieldTableCell with a null converter. Without a
     * {@link StringConverter} specified, this cell will not be able to accept
     * input from the TextField (as it will not know how to convert this back
     * to the domain object). It is therefore strongly encouraged to not use
     * this constructor unless you intend to set the converter separately.
     */
    public MateuIntegerFieldTableCell() {
        this(null);
    }

    /**
     * Creates a TextFieldTableCell that provides a {@link TextField} when put
     * into editing mode that allows editing of the cell content. This method
     * will work on any TableColumn instance, regardless of its generic type.
     * However, to enable this, a {@link StringConverter} must be provided that
     * will convert the given String (from what the user typed in) into an
     * instance of type T. This item will then be passed along to the
     * {@link TableColumn#onEditCommitProperty()} callback.
     *
     * @param converter A {@link StringConverter converter} that can convert
     *      the given String (from what the user typed in) into an instance of
     *      type T.
     */
    public MateuIntegerFieldTableCell(StringConverter<T> converter) {
        this.getStyleClass().add("text-field-table-cell");
        setConverter(converter);

        focusedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean arg1, Boolean arg2) {
                if (arg2) {
                    System.out.println("focused!!!");
                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            //startEdit();
                            fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
                        }
                    });
                } else {
                    System.out.println("not focused!!!");
                    //commitEdit(converter.fromString(textField.getText()));
                }
            }
        });
    }



    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    // --- converter
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

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



    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override public void startEdit() {
        if (! isEditable()
                || ! getTableView().isEditable()
                || ! getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = CellUtils.createTextField(this, getConverter());

                //miguel

                textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override public void handle(final KeyEvent t) {
                        if (t.getCode() == KeyCode.ENTER) {
                            if (getConverter() == null) {
                                throw new IllegalStateException(
                                        "Attempting to convert text input into Object, but provided "
                                                + "StringConverter is null. Be sure to set a StringConverter "
                                                + "in your cell factory.");
                            }
                            commitEdit(getConverter().fromString(textField.getText()));
                    /*
                    Platform.runLater(new Runnable() {

						@Override
						public void run() {
		                    cell.getTableView().requestFocus();
						}
					});
                    t.consume();
                    */
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {
                                    getTableView().selectionModelProperty().get().clearSelection();
                                }
                            });
                        } else if (t.getCode() == KeyCode.ESCAPE) {
                            cancelEdit();
                        } else if (t.getCode() == KeyCode.TAB) {
                            System.out.println("-xx->" + getSkin());
                            commitEdit(getConverter().fromString(textField.getText()));
                            Platform.runLater(new Runnable() {

                                @Override
                                public void run() {

                                    getTableView().selectionModelProperty().get().select(getTableRow().getIndex(), getTableColumn());

                                    if (t.isShiftDown()) getTableView().selectionModelProperty().get().selectPrevious();
                                    else getTableView().selectionModelProperty().get().selectNext();
                                    getTableView().requestFocus();
                                }
                            });
                        }
                    }
                });
                textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> arg0,
                                        Boolean arg1, Boolean arg2) {
                        if (!arg2) {
                            System.out.println("focus lost on textfield");
                            cancelEdit();
                        }
                    }
                });


            }

            CellUtils.startEdit(this, getConverter(), null, null, textField);
        }
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();
        CellUtils.cancelEdit(this, getConverter(), null);
        getTableView().requestFocus();
    }

    /** {@inheritDoc} */
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CellUtils.updateItem(this, getConverter(), null, null, textField);
    }
}
