package io.mateu.ui.vaadin;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import io.mateu.ui.core.shared.Data;

import java.time.LocalDate;

public class DiaLayout extends CssLayout {
    private final CalendarLayout calendarLayout;
    private final LocalDate fecha;
    private final Data data;
    private final Data value;

    public DiaLayout(CalendarLayout calendarLayout) {
        this(calendarLayout, null, null);
    }

    public DiaLayout(CalendarLayout calendarLayout, LocalDate fecha, Data data) {
        this.calendarLayout = calendarLayout;
        this.fecha = fecha;
        this.data = data;
        this.value = (data != null)?(calendarLayout.options.get(data.get("_value"))):null;

        addStyleName("dia");

        addComponent(new Label("" + ((fecha != null)?fecha.getDayOfMonth():"-")));

        if (value != null && value.containsKey("_css")) addStyleName(value.getString("_css"));
    }
}
