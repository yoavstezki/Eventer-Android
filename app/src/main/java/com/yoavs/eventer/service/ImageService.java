package com.yoavs.eventer.service;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    public UploadTask uploadImageToStorage(final String userId, final Bitmap bitmap) {
        final StorageReference storageReference = storage.getReference().child(userId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] data = outputStream.toByteArray();

        return storageReference.putBytes(data);
    }

    public void loadStorageImage(String userId, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        storage.getReference().child(userId).getDownloadUrl()
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
}
