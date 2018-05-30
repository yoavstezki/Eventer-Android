package com.yoavs.eventer.DB;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yoavs.eventer.entity.Group;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yoavs
 */

public class GroupsDB {

    private static final String root = "groups";
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(root);
    private Map<String, Group> userCache = new HashMap<>();

    public static DatabaseReference findGroup(String groupKey) {
        return databaseReference.child(groupKey);
    }

    public static void addGroup(String title, final String userKey, final DatabaseReference.CompletionListener completionListener) {

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

    public static Query getGroupTitleQuery(String groupKey) {
        return databaseReference.child(groupKey).orderByChild("title");
    }
}
