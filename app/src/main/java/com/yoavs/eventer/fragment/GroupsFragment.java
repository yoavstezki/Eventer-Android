package com.yoavs.eventer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.UserGroupsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.adpter.GroupListAdapter;
import com.yoavs.eventer.activity.NewGroupActivity;
import com.yoavs.eventer.entity.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public class GroupsFragment extends ListFragment {

    private static final String TAG = "GroupsFragment";
    private List<Group> groups;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        FloatingActionButton addNewGroup = rootView.findViewById(R.id.add_new_group);

        setOnClick(addNewGroup);


        groups = new ArrayList<>();
        final GroupListAdapter groupListAdapter = new GroupListAdapter(getActivity(), groups);
        setListAdapter(groupListAdapter);


        UserGroupsDB.getUserGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String groupKey = snapshot.getKey();
                    UserGroupsDB.findGroup(groupKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            groups.add(group);
                            groupListAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    private void setOnClick(FloatingActionButton addNewGroup) {
        addNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewGroupActivity.class);
                startActivity(intent);
            }
        });
    }

}
