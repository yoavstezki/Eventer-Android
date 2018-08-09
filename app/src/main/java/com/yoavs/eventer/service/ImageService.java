package com.yoavs.eventer.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yoavs.eventer.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * @author yoavs
 */

public class ImageService {

    private final static String TAG = "ImageService";
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public void saveFacebookImageToStorage(final String userId) {

        //todo: change Picasso..
        Uri fbUserProfilePictureUri = Profile.getCurrentProfile().getProfilePictureUri(400, 400);

        Picasso.get()
                .load(fbUserProfilePictureUri)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        saveImageToStorage(userId, bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.e(TAG, "Failed to load profile picture file " + e);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    public UploadTask saveImageToStorage(final String userId, final Bitmap bitmap) {
        final StorageReference storageReference = storage.getReference().child(userId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        return storageReference.putBytes(data);
    }

    private void loadFromStorage(final String userId, final Context context, final OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        storage.getReference().child(userId).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                            onSuccessListener.onSuccess(uri);
                            saveImageToLocal(userId, uri, context);
                        } catch (Exception e) {
                            Log.e(TAG, "Task is too long", e);
                        }

                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    public void loadImage(String uid, Context context, OnSuccessListener<Uri> onSuccess, OnFailureListener onFailure) {
        try {
            File file = FileUtil.searchFileBy(uid, context);
            onSuccess.onSuccess(Uri.fromFile(file));
        } catch (FileNotFoundException e) {
            loadFromStorage(uid, context, onSuccess, onFailure);
//            Log.e(TAG, "Imgae file not found locally, trying to get from remote", e);
        }
    }

    private void saveImageToLocal(final String uid, final Uri imageUri, final Context context) throws Exception {
        Executors.callable(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    FileUtil.saveImageFile(uid, bitmap, context);
                } catch (IOException e) {
//                    Log.e(TAG, "throw some error when trying to get image from uri ", e);
                }
            }
        }).call();
    }
}
