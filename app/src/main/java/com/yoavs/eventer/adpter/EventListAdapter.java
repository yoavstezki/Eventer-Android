package com.yoavs.eventer.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.GroupsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Event;
import com.yoavs.eventer.entity.Group;

import java.util.List;

/**
 * @author yoavs
 */

public class EventListAdapter extends ArrayAdapter<Event> {
    public EventListAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event, parent, false);
        }

        if (event != null) {

            TextView eventTitleTextView = convertView.findViewById(R.id.event_name_and_date);
            final TextView eventGroupName = convertView.findViewById(R.id.event_group_name);

            eventTitleTextView.setText(String.format("%s - %s", event.getTitle(), event.getDate()));


            GroupsDB.getGroupTitleQuery(event.getGroupKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Group group = dataSnapshot.getValue(Group.class);
                    eventGroupName.setText(group != null ? group.getTitle() : "");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return convertView;
    }
}
