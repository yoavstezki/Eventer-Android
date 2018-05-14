package com.yoavs.eventer.entity;

import java.io.Serializable;

/**
 * @author yoavs
 */

public class User implements Serializable {
    private String UId;
    private String name;
    private String facebookId;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String facebookId) {
        this.name = name;
        this.facebookId = facebookId;
    }

    public String getUId() {
        return UId;
    }

    public String getName() {
        return name;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (UId != null ? !UId.equals(user.UId) : user.UId != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        return facebookId != null ? facebookId.equals(user.facebookId) : user.facebookId == null;
    }

    @Override
    public int hashCode() {
        int result = UId != null ? UId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (facebookId != null ? facebookId.hashCode() : 0);
        return result;
    }
}
