package com.yoavs.eventer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.EventsDB;
import com.yoavs.eventer.DB.UserGroupsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.activity.EventDetailsActivity;
import com.yoavs.eventer.activity.NewEventActivity;
import com.yoavs.eventer.adpter.EventListAdapter;
import com.yoavs.eventer.entity.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public class EventFragment extends ListFragment {

    private List<Event> events;
    private EventListAdapter eventListAdapter;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_event, container, false);


        FloatingActionButton addNewEvent = rootView.findViewById(R.id.add_new_event);
        progressBar = rootView.findViewById(R.id.event_progress_bar);

        setOnClick(addNewEvent);


        events = new ArrayList<>();
        eventListAdapter = new EventListAdapter(getActivity(), events);
        setListAdapter(eventListAdapter);

        initModel();

        return rootView;
    }

    private void initModel() {
        UserGroupsDB.getUserGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> keys = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    keys.add(snapshot.getKey());
                }

                EventsDB.getEventsByGroupKey()
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                events.clear();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    Event event = snapshot.getValue(Event.class);

                                    if (event != null && keys.contains(event.getGroupKey())) {

                                        event.setKey(snapshot.getKey());

                                        events.add(event);
                                        eventListAdapter.notifyDataSetChanged();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //todo: display error.
                            }
                        });

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo: display error.
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void setOnClick(FloatingActionButton addNewEvent) {
        addNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Event event = events.get(position);

        Intent intent = new Intent(this.getActivity(), EventDetailsActivity.class);
        intent.putExtra("event", event);

        startActivity(intent);
    }
}
