package com.yoavs.eventer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

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
}
