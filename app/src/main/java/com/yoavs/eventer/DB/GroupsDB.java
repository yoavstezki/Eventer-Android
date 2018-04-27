package com.yoavs.eventer.DB;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yoavs.eventer.entity.Group;
import com.yoavs.eventer.factory.GroupDBFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yoavs
 */

public class GroupsDB {

    public static volatile GroupsDB instance = new GroupsDB();
    private final String root = "groups";
    private final GroupDBFactory groupDBFactory;
    private DatabaseReference databaseReference;
    private Map<String, Group> userCache = new HashMap<>();

    private GroupsDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference(root);
        groupDBFactory = new GroupDBFactory();
    }

    public void addGroup(String title, final String userKey, final DatabaseReference.CompletionListener completionListener) {

        final String groupKey = databaseReference.push().getKey();

        HashMap<String, Boolean> members = new HashMap<>();
        members.put(userKey, true);

        Map<String, Object> values = new HashMap<>();
        values.put("title", title);
        values.put("lastUpdate", new Date().getTime());
        values.put("members", members);

        databaseReference.child(groupKey).setValue(values, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                UserGroupsDB.addGroupToUser(groupKey, completionListener);
            }
        });
    }
}
