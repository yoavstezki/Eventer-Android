package com.yoavs.eventer.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * @author yoavs
 */

public class Event implements Serializable {
    private String key;
    private String date;
    private String groupKey;
    private String title;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String date, String groupKey, String title) {
        this.date = date;
        this.groupKey = groupKey;
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (key != null ? !key.equals(event.key) : event.key != null) return false;
        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        if (groupKey != null ? !groupKey.equals(event.groupKey) : event.groupKey != null)
            return false;
        return title != null ? title.equals(event.title) : event.title == null;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (groupKey != null ? groupKey.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
