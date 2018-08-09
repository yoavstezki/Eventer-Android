package com.yoavs.eventer.DB;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.events.UserLeaveGroupEvent;
import com.yoavs.eventer.loaclDB.GroupMembersTable;
import com.yoavs.eventer.loaclDB.LastUpdateTable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public class GroupMemberDB {

    private static volatile GroupMemberDB instance = new GroupMemberDB();
    private final String groupsNode = "groups";
    private final String membersNode = "members";
    private final String lastUpdate = "lastUpdate";
    private final DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference(groupsNode);
    private EventBus bus = EventBus.getDefault();

    private GroupMemberDB() {
        bus.register(this);
    }

    public static GroupMemberDB getInstance() {
        return instance;
    }

    public void getMembersBy(final String groupKey, final Context context, final OnSuccessListener<List<String>> successListener) {

        getRemoteLastUpdate(groupKey, new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long lastUpdateDate) {

                if (isLocalDatabaseUpToDate(groupKey, lastUpdateDate, context)) {
                    List<String> userKeys = fetchUserKeysFromLocalDB(groupKey, context);
                    successListener.onSuccess(userKeys);

                } else {
                    groupReference.child(groupKey).child(membersNode)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    List<String> userKeys = new ArrayList<>();
                                    for (DataSnapshot userKeySnapshot : dataSnapshot.getChildren()) {
                                        userKeys.add(userKeySnapshot.getKey());
                                    }
                                    successListener.onSuccess(userKeys);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
        });
    }

    private void getRemoteLastUpdate(String groupsKey, final OnSuccessListener<Long> successListener) {

        groupReference.child(groupsKey).child(lastUpdate)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            successListener.onSuccess((Long) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private Boolean isLocalDatabaseUpToDate(String groupsKey, Long lastUpdateDate, Context context) {
        Long localUpdateTime = getLocalUpdateTime(groupsKey, context);

        return localUpdateTime != null && localUpdateTime >= lastUpdateDate;
    }

    private Long getLocalUpdateTime(String groupKey, Context context) {
        return LastUpdateTable.getLastUpdate
                (
                        GroupMembersTable.GroupMembersTableEntry.TABLE_NAME,
                        groupKey,
                        context
                );
    }

    private List<String> fetchUserKeysFromLocalDB(String groupKey, Context context) {
        return GroupMembersTable.getUserKeysByGroupKey(groupKey, context);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void deleteMember(UserLeaveGroupEvent event) {
        groupReference.child(event.getGroupId()).child(membersNode).child(event.getUserId()).removeValue();
    }
}
