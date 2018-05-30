package com.yoavs.eventer.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yoavs.eventer.service.ImageService;

/**
 * @author yoavs
 */

public class ImageLoaderUtil {

    private static ImageService imageService = new ImageService();

    public static void loadImage(String userId, final ImageView imageView, final ProgressBar progressBar) {
        imageService.loadStorageImage(userId,
                new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .resize(125, 125)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
