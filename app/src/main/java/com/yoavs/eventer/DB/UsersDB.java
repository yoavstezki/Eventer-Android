package com.yoavs.eventer.DB;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.entity.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yoavs
 */

public class UsersDB {

    private static volatile UsersDB usersDB = new UsersDB();
    private final String root = "users";
    private DatabaseReference databaseReference;
    private Map<String, User> userCache = new HashMap<>();

    private UsersDB() {
        databaseReference = FirebaseDatabase.getInstance().getReference(root);
    }

    public static UsersDB getInstance() {
        return usersDB;
    }

    public User findUserByKey(String key) {
        User cacheUser = userCache.get(key);

        if (cacheUser == null) {
            databaseReference.child(root).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return cacheUser;
    }

    public void addUser(FirebaseUser firebaseUser, String facebookId) {

        String uid = firebaseUser.getUid();

        User user = new User(uid, firebaseUser.getDisplayName(), facebookId);

        userCache.put(uid, user);

        databaseReference.child(uid).setValue(user);
    }
}
