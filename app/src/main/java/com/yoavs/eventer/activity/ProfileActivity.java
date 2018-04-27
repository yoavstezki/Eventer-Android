package com.yoavs.eventer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.yoavs.eventer.R;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private ImageView profilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilePicture = findViewById(R.id.profilePicture);

        firebaseAuth = FirebaseAuth.getInstance();

        TextView userName = findViewById(R.id.userName);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String text = getString(R.string.welcome) + " " + currentUser.getDisplayName();
            userName.setText(text);

            Picasso.get()
                    .load(currentUser.getPhotoUrl())
                    .into(profilePicture);
        }
    }
}

