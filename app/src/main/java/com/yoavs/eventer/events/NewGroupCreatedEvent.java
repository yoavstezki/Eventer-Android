package com.yoavs.eventer.events;

/**
 * @author yoavs
 */

public  class NewGroupCreatedEvent {

    public final boolean isSave;

    public NewGroupCreatedEvent(boolean isSave, String error) {
        this.isSave = isSave;
    }
}
