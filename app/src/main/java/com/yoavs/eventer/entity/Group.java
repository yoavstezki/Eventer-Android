package com.yoavs.eventer.entity;

import java.util.Map;

/**
 * @author yoavs
 */

public class Group {

    public String key;
    //todo: change title to name!!!!
    public String title;
    public Map<String, Boolean> members;
    public Long lastUpdate;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public Group(String key, String title, Long lastUpdate) {
        this.key = key;
        this.title = title;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Group{" +
                "key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", members=" + members +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
