package com.yoavs.eventer.DB;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yoavs.eventer.events.UserLeaveGroupEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yoavs
 */

public class GroupMemberDB {

    private static volatile GroupMemberDB instance = new GroupMemberDB();
    private final String groupsNode = "groups";
    private final String membersNode = "members";
    private final DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference(groupsNode);
    private EventBus bus = EventBus.getDefault();

    private GroupMemberDB() {
        bus.register(this);
    }

    public static GroupMemberDB getInstance() {
        return instance;
    }

    public DatabaseReference getMembersBy(String groupKey) {
        return groupReference.child(groupKey).child(membersNode);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void deleteMember(UserLeaveGroupEvent event) {
        groupReference.child(event.getGroupId()).child(membersNode).child(event.getUserId()).removeValue();
    }
}
