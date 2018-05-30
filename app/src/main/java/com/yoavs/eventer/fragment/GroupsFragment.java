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
import com.yoavs.eventer.DB.GroupsDB;
import com.yoavs.eventer.DB.UserGroupsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.activity.GroupDetailsActivity;
import com.yoavs.eventer.activity.NewGroupActivity;
import com.yoavs.eventer.adpter.GroupListAdapter;
import com.yoavs.eventer.entity.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public class GroupsFragment extends ListFragment {

    private static final String TAG = "GroupsFragment";
    private List<Group> groups;
    private ProgressBar progressBar;
    private GroupListAdapter groupListAdapter;
    private ValueEventListener groupEventListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        FloatingActionButton addNewGroup = rootView.findViewById(R.id.add_new_group);
        progressBar = rootView.findViewById(R.id.groups_progress_bar);

        setOnClick(addNewGroup);

        groups = new ArrayList<>();
        groupListAdapter = new GroupListAdapter(getActivity(), groups);
        setListAdapter(groupListAdapter);

        initModel();


        return rootView;
    }

    private void initModel() {
        groupEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groups.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String groupKey = snapshot.getKey();
                    GroupsDB.findGroup(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            String key = dataSnapshot.getKey();
                            group.setKey(key);

                            if (!groups.contains(group)) {
                                groups.add(group);
                                groupListAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        };
        UserGroupsDB.getUserGroups().addValueEventListener(groupEventListener);
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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Group group = groups.get(position);

        Intent intent = new Intent(this.getActivity(), GroupDetailsActivity.class);
        intent.putExtra("groupName", group.getTitle());
        intent.putExtra("groupKey", group.getKey());

        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        UserGroupsDB.getUserGroups().removeEventListener(groupEventListener);
    }
}
