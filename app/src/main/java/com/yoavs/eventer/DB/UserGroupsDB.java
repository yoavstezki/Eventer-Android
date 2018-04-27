package com.yoavs.eventer.DB;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yoavs
 */

public class UserGroupsDB {

    private static final String usersNode = "users";
    private static final String groupsNode = "groups";

    private static DatabaseReference userReference = FirebaseDatabase.getInstance().getReference(usersNode);
    private static DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference(groupsNode);
    private static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    public static DatabaseReference getUserGroups() {
        return userReference
                .child(currentUser.getUid())
                .child(groupsNode);

    }

    static void addGroupToUser(String groupKey, DatabaseReference.CompletionListener completionListener) {

        Map<String, Object> values = new HashMap<>();
        values.put(groupKey, true);

        userReference.child(currentUser.getUid()).child(groupsNode).updateChildren(values, completionListener);
    }

    public static DatabaseReference findGroup(String groupKey) {
        return groupReference.child(groupKey);
    }
}
