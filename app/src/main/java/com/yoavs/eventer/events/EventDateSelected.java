package com.yoavs.eventer.events;

/**
 * @author yoavs
 */

public class EventDateSelected {
    private String selectedDare;

    public EventDateSelected(String selectedDare) {
        this.selectedDare = selectedDare;
    }

    public String getSelectedDare() {
        return selectedDare;
    }
}
