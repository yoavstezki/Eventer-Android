package com.yoavs.eventer.components;


import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yoavs.eventer.events.EventDateSelected;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author yoavs
 */

public class DatePiker implements DatePickerDialog.OnDateSetListener {

    private final int Year;
    private final int Month;
    private final int Day;
    private Calendar calendar;

    private EventBus eventBus = EventBus.getDefault();

    public DatePiker() {
        calendar = Calendar.getInstance();

        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);


    }


    public int getYear() {
        return Year;
    }

    public int getMonth() {
        return Month;
    }

    public int getDay() {
        return Day;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.MONTH, monthOfYear);

        String selectedDare = SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(cal.getTime());

        eventBus.post(new EventDateSelected(selectedDare));
    }
}
