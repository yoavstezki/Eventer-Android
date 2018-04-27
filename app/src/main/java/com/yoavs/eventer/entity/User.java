package com.yoavs.eventer.entity;

/**
 * @author yoavs
 */

public class User {
    public String key;
    public String name;
    public String facebookId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String key, String name, String facebookId) {
        this.name = name;
        this.facebookId = facebookId;
    }
}
