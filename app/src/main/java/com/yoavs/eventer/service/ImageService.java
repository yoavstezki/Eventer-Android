package com.yoavs.eventer.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yoavs.eventer.utils.FileUtil;

import java.io.ByteArrayOutputStream;

/**
 * @author yoavs
 */

public class ImageService {

    private final static String TAG = "ImageService";
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public void saveFacebookImageToStorage(final String userId) {

        Uri fbUserProfilePictureUri = Profile.getCurrentProfile().getProfilePictureUri(400, 400);

        Picasso.get()
                .load(fbUserProfilePictureUri)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        uploadImageToStorage(userId, bitmap);
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

    private void uploadImageToStorage(final String userId, final Bitmap bitmap) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final StorageReference storageReference = storage.getReference().child(userId);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                byte[] data = outputStream.toByteArray();

                storageReference.putBytes(data);
            }
        });
    }

    public void loadStorageImage(String userId, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        storage.getReference().child(userId).getDownloadUrl()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void saveImage(final String userId, final Uri photoUri, final Context context) {

        Picasso.get()
                .load(photoUri)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        uploadImageToStorage(userId, bitmap);
                        FileUtil.saveImageFile(userId, bitmap, context);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }
}
