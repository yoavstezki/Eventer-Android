package com.yoavs.eventer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.UserGroupsDB;
import com.yoavs.eventer.DB.UsersDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.adpter.MemberListAdapter;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.service.FacebookFriendsFinderService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddMembersActivity extends BaseActivity {

    private List<User> members;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;
    private ListView membersList;
    private TextView emptyView;
    private ArrayList<User> groupMembers;
    private String groupKey;
    private MemberListAdapter listAdapter;

    public AddMembersActivity() {
        super(R.layout.activity_add_members, R.id.add_members_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        membersList = findViewById(R.id.user_members_list);
        progressBar = findViewById(R.id.add_members_progress_bar);
        emptyView = findViewById(R.id.add_members_empty_view);

        groupMembers = getGroupMembers();
        groupKey = getGroupKey();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        members = new ArrayList<>();
        listAdapter = new MemberListAdapter(this, members);
        membersList.setAdapter(listAdapter);

        setOnItemClick();

        FacebookFriendsFinderService.find(facebookFriendsFinderCallback());
    }

    @NonNull
    private GraphRequest.GraphJSONArrayCallback facebookFriendsFinderCallback() {
        return new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray array, GraphResponse response) {
                JSONObject object = response.getJSONObject();
                try {
                    JSONArray data = object.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = (JSONObject) data.get(i);

                        if (jsonObject != null) {
                            String id = (String) jsonObject.get("id");

                            findUserByFacebookId(id);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void findUserByFacebookId(String id) {
        UsersDB.getInstance().findUserByFacebookId(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    if (user == null) {
                        return;
                    }

                    user.setUId(snapshot.getKey());

                    if (!groupMembers.contains(user) && !currentUser.getUid().equals(user.getUId())) {
                        members.add(user);
                        listAdapter.notifyDataSetChanged();
                    }
                }

                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                updateUI();
            }
        });
    }

    private String getGroupKey() {
        return getIntent().getExtras().getString("groupKey");
    }

    private ArrayList<User> getGroupMembers() {
        return (ArrayList<User>) getIntent().getSerializableExtra("groupMembers");
    }

    private void setOnItemClick() {
        membersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = members.get(i);
                if (user != null) {
                    UserGroupsDB.addUserToGroup(user.getUId(), groupKey, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            AddMembersActivity.this.onBackPressed();
                        }
                    });
                }
            }
        });
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        if (members.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
}
