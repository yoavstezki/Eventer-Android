package com.yoavs.eventer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author yoavs
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static File createImageFile(String uniqueName, @NonNull Context context) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, uniqueName + ".jpg");
    }

    public static File searchFileBy(String uniqueName, @NonNull Context context) throws FileNotFoundException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir != null) {
            for (File file : storageDir.listFiles()) {
                if ((uniqueName + ".jpg").equals(file.getName())) {
                    return file;
                }
            }
        }
        throw new FileNotFoundException("Can't find file with name: " + uniqueName);
    }

    public static void saveImageFile(String uniqueName, Bitmap bitmap, Context context) {
        File imageFile = createImageFile(uniqueName, context);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error while trying save image " + e);
        } catch (IOException e) {
            Log.e(TAG, "Error while trying save image " + e);
        }
    }

    public static void downloadImageFile(Uri imageURI, final OnSuccessListener<Bitmap> onSuccessListener, final OnFailureListener onFailureListener) {
        Picasso.get()
                .load(imageURI)
                .resize(500, 500)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        onSuccessListener.onSuccess(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        onFailureListener.onFailure(e);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }

    public static Uri getUriForFile(File photoFile, Context context) {
        return FileProvider.getUriForFile(context, "com.yoavs.eventer.fileprovider", photoFile);
    }
}
