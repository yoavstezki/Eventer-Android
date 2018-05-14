package com.yoavs.eventer.DB;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.events.UserLeaveGroupEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yoavs
 */

public class UsersDB {

    private static volatile UsersDB usersDB = new UsersDB();
    private final String usersNode = "users";
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference(usersNode);
    private EventBus eventBus = EventBus.getDefault();

    public UsersDB() {
        eventBus.register(this);
    }

    public static UsersDB getInstance() {
        return usersDB;
    }

    public DatabaseReference findUserByKey(String key) {
        return usersReference.child(key);
    }

    public Query findUserByFacebookId(String facebookId) {
        return usersReference.orderByChild("facebookId").equalTo(facebookId);
    }

    public void addUser(FirebaseUser firebaseUser, String facebookId) {

        String uid = firebaseUser.getUid();

        User user = new User(firebaseUser.getDisplayName(), facebookId);

        usersReference.child(uid).setValue(user);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void userLeaveGroup(UserLeaveGroupEvent event) {
        usersReference.child(event.getUserId()).child("groups").child(event.getGroupId()).removeValue();
    }
}
