package com.yoavs.eventer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.RequestsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.adpter.RequestListAdapter;
import com.yoavs.eventer.entity.Event;
import com.yoavs.eventer.entity.Request;
import com.yoavs.eventer.enums.ScreenModeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public class EventDetailsActivity extends BaseActivity {

    private List<Request> requests = new ArrayList<>();
    private TextView eventName;
    private TextView eventDate;
    private ProgressBar eventDetailsProgressBar;

    private RequestListAdapter requestListAdapter;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    public EventDetailsActivity() {
        super(R.layout.activity_event_details, R.id.event_toolbar);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Event event = (Event) extras.get("event");

            if (event != null) {
                eventName = findViewById(R.id.event_name);
                eventDate = findViewById(R.id.event_date);

                setTitle(event);
                String eventKey = event.getKey();
                initModel(eventKey);

                final ListView requestList = findViewById(R.id.request_list);
                requestListAdapter = new RequestListAdapter(this, requests);
                requestList.setAdapter(requestListAdapter);

                requestList.setOnItemClickListener(getItemRequestListener(eventKey));
                requestList.setOnItemLongClickListener(getUpdateRequestListener(eventKey));

                FloatingActionButton addRequestFab = findViewById(R.id.add_request_fab);

                addRequestFab.setOnClickListener(getAddRequestListener(eventKey));

                eventDetailsProgressBar = findViewById(R.id.event_details_progress_bar);
            }
        }
    }

    @NonNull
    private AdapterView.OnItemClickListener getItemRequestListener(final String eventKey) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {

                Request request = requests.get(i);

                Boolean purchase = request.getPurchase();
                String approvalUserId = purchase ? "" : firebaseAuth.getUid();

                RequestsDB.updateApprovalUserRequest(eventKey, request.getKey(), approvalUserId, !purchase);
            }
        };
    }

    private AdapterView.OnItemLongClickListener getUpdateRequestListener(final String eventKey) {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Request request = requests.get(position);

                if (request != null && request.getSuggestedUserId().equals(firebaseAuth.getUid())) {
                    Intent intent = new Intent(EventDetailsActivity.this, CreateOrUpdateRequestActivity.class);
                    intent.putExtra("screenMode", ScreenModeEnum.UPDATE);
                    intent.putExtra("eventKey", eventKey);
                    intent.putExtra("request", request);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(EventDetailsActivity.this, R.string.edit_your_requeset, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        };
    }

    private View.OnClickListener getAddRequestListener(final String eventKey) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailsActivity.this, CreateOrUpdateRequestActivity.class);
                intent.putExtra("screenMode", ScreenModeEnum.CREATE);
                intent.putExtra("eventKey", eventKey);

                startActivity(intent);
            }
        };
    }

    private void initModel(String eventKey) {
        DatabaseReference requests = RequestsDB.getRequests(eventKey);
        requests.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EventDetailsActivity.this.requests.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Request request = snapshot.getValue(Request.class);

                    if (request != null && !EventDetailsActivity.this.requests.contains(request)) {
                        request.setKey(snapshot.getKey());
                        EventDetailsActivity.this.requests.add(request);
                        requestListAdapter.notifyDataSetChanged();
                    }
                }

                eventDetailsProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                eventDetailsProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setTitle(Event event) {
        eventName.setText(event.getTitle());
        eventDate.setText(event.getDate());
    }
}
