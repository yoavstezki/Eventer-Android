package com.yoavs.eventer.DB;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yoavs
 */

public class RequestsDB {

    private static String root = "events";
    private static String requestsNode = "requests";
    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(root);
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static EventBus eventBus = EventBus.getDefault();

    public static DatabaseReference getRequests(String eventKey) {
        return databaseReference.child(eventKey).child(requestsNode);
    }

    public static void addNewRequest(String eventKey, String requestItemName, DatabaseReference.CompletionListener completionListener) {
        String requestItemKey = databaseReference.push().getKey();

        Map<String, Object> values = new HashMap<>();
        values.put("suggestedUserId", firebaseAuth.getCurrentUser().getUid());
        values.put("itemName", requestItemName);
        values.put("lastUpdate", new Date().getTime());
        values.put("purchase", false);
        values.put("approvalUserId", "");

        databaseReference.child(eventKey).child(requestsNode).child(requestItemKey).setValue(values, completionListener);
    }

    public static void updateRequestName(String eventKey, String requestKey, String requestItemName, DatabaseReference.CompletionListener completionListener) {
        Map<String, Object> values = new HashMap<>();
        values.put("itemName", requestItemName);
        values.put("lastUpdate", new Date().getTime());

        updateRequest(eventKey, requestKey, values, completionListener);
    }

    public static void updateApprovalUserRequest(String eventKey, String requestKey, String approvalUser, Boolean purchase) {
        Map<String, Object> values = new HashMap<>();
        values.put("approvalUserId", approvalUser);
        values.put("purchase", purchase);
        values.put("lastUpdate", new Date().getTime());

        updateRequest(eventKey, requestKey, values, null);
    }

    private static void updateRequest(String eventKey, String requestKey, Map<String, Object> values, DatabaseReference.CompletionListener completionListener) {
        DatabaseReference requestNode = databaseReference.child(eventKey).child(requestsNode)
                .child(requestKey);

        if (completionListener != null) {
            requestNode.updateChildren(values, completionListener);
        } else {
            requestNode.updateChildren(values);
        }
    }

    public static Task<Void> removeItemRequest(String eventKey, String requestKey) {
        return databaseReference.child(eventKey).child("requests").child(requestKey).removeValue();
    }
}
