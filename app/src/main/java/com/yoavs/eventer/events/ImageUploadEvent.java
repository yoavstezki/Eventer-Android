package com.yoavs.eventer.events;
import android.net.Uri;

/**
 * @author yoavs
 */

public class ImageUploadEvent extends BaseEvent {
    private Uri downloadUri;

    public ImageUploadEvent(Uri downloadUri) {
        this.downloadUri = downloadUri;
    }

    public Uri getDownloadUri() {
        return downloadUri;
    }
}
