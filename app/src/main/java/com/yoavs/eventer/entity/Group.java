package com.yoavs.eventer.entity;

import java.util.Map;

/**
 * @author yoavs
 */

public class Group {

    private String key;
    //todo: change title to name!!!!
    private String title;
    private Map<String, Boolean> members;
    private Long lastUpdate;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
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
