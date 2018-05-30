package com.yoavs.eventer.DB;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yoavs
 */

public class EventsDB {

    private static final String root = "events";
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(root);

    public static void addNewEvent(String title, String date, String groupKey, final DatabaseReference.CompletionListener completionListener) {

        String eventKey = databaseReference.push().getKey();
        Map<String, Object> values = new HashMap<>();

        values.put("title", title);
        values.put("date", date);
        values.put("groupKey", groupKey);

        databaseReference.child(eventKey).setValue(values, completionListener);
    }


    public static Query getEventsByGroupKey() {
        return databaseReference.orderByChild("groupKey");
    }

}
