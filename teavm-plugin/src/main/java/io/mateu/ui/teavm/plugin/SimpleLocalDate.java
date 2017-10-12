package io.mateu.ui.teavm.plugin;

import java.util.Date;

/**
 * Created by miguel on 15/8/17.
 */
public class SimpleLocalDate {

    /**
     * The year.
     */
    private final int year;
    /**
     * The month-of-year.
     */
    private final int month;
    /**
     * The day-of-month.
     */
    private final int day;

    public SimpleLocalDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }


    public static SimpleLocalDate now() {
        Date d = new Date();
        return new SimpleLocalDate(d.getYear(), d.getMonth(), d.getDate());
    }
}
