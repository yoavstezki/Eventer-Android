package com.yoavs.eventer.DB;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * @author yoavs
 */

public class UserEventsDB {


    public static DatabaseReference getUserEvents() {
        UserGroupsDB.getUserGroups()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot dataSnapshot1 = dataSnapshot;

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return null;
    }


}
