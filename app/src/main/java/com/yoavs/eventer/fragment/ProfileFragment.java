package com.yoavs.eventer.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yoavs.eventer.R;
import com.yoavs.eventer.activity.LoginActivity;
import com.yoavs.eventer.service.ImageService;
import com.yoavs.eventer.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseAuth firebaseAuth;
    private ImageView profilePicture;
    private Button logout;
    private TextView userName;
    private ProgressBar progressBar;
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

        loadImage();
    }

    public void loadImage() {
        String uid = firebaseAuth.getUid();
        try {
            File file = FileUtil.searchFileBy(uid, getContext());
            loadImageFrom(file);

            progressBar.setVisibility(View.GONE);
        } catch (FileNotFoundException e) {
            imageService.loadStorageImage(uid,
                    new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadImageFile(uri);
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_load_image), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void loadImageFrom(File file) {
        Picasso.get()
                .load(file)
                .resize(500, 500)
                .into(profilePicture, imageLoaded());
    }

    private void downloadImageFile(Uri imageURI) {
        FileUtil.downloadImageFile(imageURI,
                new OnSuccessListener<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        profilePicture.setImageBitmap(bitmap);
                        progressBar.setVisibility(View.GONE);
                        FileUtil.saveImageFile(firebaseAuth.getUid(), bitmap, getContext());
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileFragment.this.getContext(), getString(R.string.failed_to_load_image), Toast.LENGTH_LONG).show();
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
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

            File photoFile = FileUtil.createImageFile(firebaseAuth.getUid(), getContext());

            Uri photoURI = FileUtil.getUriForFile(photoFile, getContext());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                String uid = firebaseAuth.getUid();
                File file = FileUtil.searchFileBy(uid, getContext());
                Uri photoUri = FileUtil.getUriForFile(file, getContext());
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), photoUri);

                loadImageFrom(photoUri);

                imageService.uploadImageToStorage(uid, bitmap);

            } catch (FileNotFoundException e) {
                progressBar.setVisibility(View.GONE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadImageFrom(Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(500, 500)
                .into(profilePicture, imageLoaded());
    }
}

