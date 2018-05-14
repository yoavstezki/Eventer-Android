package com.yoavs.eventer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.yoavs.eventer.DB.GroupsDB;
import com.yoavs.eventer.R;

public class NewGroupActivity extends BaseActivity {

    private TextInputEditText titleEditText;
    private ProgressBar progressBar;
    private Button clearBtn;
    private Button createBtn;
    private FirebaseAuth firebaseAuth;

    public NewGroupActivity() {
        super(R.layout.activity_new_group, R.id.new_group_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        titleEditText = findViewById(R.id.new_group_title);
        clearBtn = findViewById(R.id.new_group_clear_btn);
        createBtn = findViewById(R.id.new_group_create_btn);
        progressBar = findViewById(R.id.new_group_pb);

        setOnClearClick();
        setOnCreateClick();
    }

    private void setOnClearClick() {
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleEditText.setText(null);
            }
        });
    }

    private void setOnCreateClick() {
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
    }
}
