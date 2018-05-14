package com.yoavs.eventer.events;

/**
 * @author yoavs
 */

public class UserLeaveGroupEvent {
    private String groupId;
    private String userId;

    public UserLeaveGroupEvent(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }
}
