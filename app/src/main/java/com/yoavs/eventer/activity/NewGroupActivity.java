package com.yoavs.eventer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yoavs.eventer.DB.GroupsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Group;

public class NewGroupActivity extends AppCompatActivity {

    private TextInputEditText titleEditText;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        firebaseAuth = FirebaseAuth.getInstance();

        RelativeLayout container = findViewById(R.id.container);


        titleEditText = findViewById(R.id.new_group_title);
        Button clearBtn = findViewById(R.id.new_group_clear_btn);
        Button createBtn = findViewById(R.id.new_group_create_btn);
        progressBar = findViewById(R.id.new_group_pb);


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditText.setText(null);
            }
        });

        setOnClickTo(createBtn);

    }

    private void setOnClickTo(Button createBtn) {
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                hideKeyboard();

                String title = titleEditText.getText().toString();

                GroupsDB.instance.addGroup(title, firebaseAuth.getCurrentUser().getUid(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        progressBar.setVisibility(View.GONE);
                        NewGroupActivity.this.onBackPressed();
                    }
                });
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
    }
}
