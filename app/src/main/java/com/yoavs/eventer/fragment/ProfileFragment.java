package com.yoavs.eventer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yoavs.eventer.R;
import com.yoavs.eventer.activity.LoginActivity;
import com.yoavs.eventer.events.ImageUploadEvent;
import com.yoavs.eventer.service.ImageService;
import com.yoavs.eventer.utils.FileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseAuth firebaseAuth;
    private ImageView profilePicture;
    private Button logout;
    private TextView userName;
    private ProgressBar progressBar;
    private EventBus bus = EventBus.getDefault();
    private ImageService imageService = new ImageService();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePicture = rootView.findViewById(R.id.profilePicture);
        logout = rootView.findViewById(R.id.fb_logout_bt);
        userName = rootView.findViewById(R.id.userName);
        progressBar = rootView.findViewById(R.id.profile_progress_bar);
        FloatingActionButton takePicFB = rootView.findViewById(R.id.profile_take_pic_fb);

        takePicFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        bus.register(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String text = getString(R.string.welcome) + " " + currentUser.getDisplayName();
            userName.setText(text);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadImage(ImageUploadEvent imageUploadEvent) {
        try {
            File file = FileUtil.searchFileBy(firebaseAuth.getUid(), getContext());
            loadImageFrom(file);

            progressBar.setVisibility(View.GONE);
        } catch (FileNotFoundException e) {
            downloadFile(imageUploadEvent);
        }
    }

    private void loadImageFrom(File file) {
        Picasso.get()
                .load(file)
                .resize(500, 500)
                .into(profilePicture, imageLoaded());
    }

    private void downloadFile(ImageUploadEvent imageUploadEvent) {

        Picasso.get()
                .load(imageUploadEvent.getDownloadUri())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        profilePicture.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                        FileUtil.saveImageFile(firebaseAuth.getUid(), bitmap, getContext());
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_load_image), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    @NonNull
    private Callback imageLoaded() {
        return new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_load_image), Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            File photoFile = FileUtil.createImageFile(firebaseAuth.getUid(), getContext());

            Uri photoURI = getUriForFile(photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private Uri getUriForFile(File photoFile) {
        return FileProvider.getUriForFile(getContext(), "com.yoavs.eventer.fileprovider", photoFile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                String uid = firebaseAuth.getUid();
                File file = FileUtil.searchFileBy(uid, getContext());
                Uri photoUri = getUriForFile(file);
                loadImageFrom(photoUri);
                imageService.saveImageToStorage(uid, photoUri);

            } catch (FileNotFoundException e) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void loadImageFrom(Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(500, 500)
                .rotate(90)
                .into(profilePicture, imageLoaded());
    }
}

