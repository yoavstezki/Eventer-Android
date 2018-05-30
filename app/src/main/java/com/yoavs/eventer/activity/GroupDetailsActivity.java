package com.yoavs.eventer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.GroupMemberDB;
import com.yoavs.eventer.DB.UsersDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.adpter.MemberListAdapter;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.events.UserLeaveGroupEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class GroupDetailsActivity extends BaseActivity {

    private ArrayList<User> members;
    private FloatingActionButton fab;
    private String groupKey;
    private EventBus eventBus = EventBus.getDefault();
    private FirebaseAuth firebaseAuth;
    private MemberListAdapter memberListAdapter;

    public GroupDetailsActivity() {
        super(R.layout.activity_group_details, R.id.group_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        fab = findViewById(R.id.add_member_fab);
        setOnAddMemberClick();

        members = new ArrayList<>();
        TextView groupNameText = findViewById(R.id.title_group_name);
        ListView memberList = findViewById(R.id.member_list);

        ProgressBar progressBar = findViewById(R.id.group_details_progress_bar);
        memberList.setEmptyView(progressBar);

        memberListAdapter = new MemberListAdapter(this, members);
        memberList.setAdapter(memberListAdapter);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String groupName = extras.getString("groupName");
            groupNameText.setText(groupName);

            groupKey = extras.getString("groupKey");

            getMembersGroup();
        }
    }

    private void getMembersGroup() {
        GroupMemberDB.getInstance().getMembersBy(groupKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        members.clear();
                        for (DataSnapshot userKeySnapshot : dataSnapshot.getChildren()) {
                            String userKey = userKeySnapshot.getKey();
                            findMemberBy(userKey, memberListAdapter);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void findMemberBy(String userKey, final MemberListAdapter memberListAdapter) {
        UsersDB.getInstance().findUserByKey(userKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            return;
                        }
                        user.setUId(dataSnapshot.getKey());

                        if (!members.contains(user)) {
                            members.add(user);
                            memberListAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setOnAddMemberClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupDetailsActivity.this, AddMembersActivity.class);
                intent.putExtra("groupMembers", members);
                intent.putExtra("groupKey", groupKey);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete: {
                eventBus.post(new UserLeaveGroupEvent(groupKey, firebaseAuth.getUid()));
                GroupDetailsActivity.this.onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
