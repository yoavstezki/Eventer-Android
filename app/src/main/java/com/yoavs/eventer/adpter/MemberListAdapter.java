package com.yoavs.eventer.adpter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.service.ImageService;

import java.util.List;

/**
 * @author yoavs
 */

public class MemberListAdapter extends ArrayAdapter<User> {

    private static final String TAG = "MemberListAdapter";
    private ImageService imageService = new ImageService();

    public MemberListAdapter(@NonNull Context context, List<User> users) {
        super(context, 0, users);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_member, parent, false);
        }

        TextView groupNameTextView = convertView.findViewById(R.id.member_name);
        final ProgressBar progressBar = convertView.findViewById(R.id.member_item_progress_bar);
        final ImageView imageView = convertView.findViewById(R.id.member_pic);

        if (user != null) {
            imageService.loadImage(user.getUId(), getContext(), getOnSuccess(imageView, progressBar), getOnFailure());

            groupNameTextView.setText(user.getName());
        }


        return convertView;
    }

    @NonNull
    private OnSuccessListener<Uri> getOnSuccess(final ImageView imageView, final ProgressBar progressBar) {
        return new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .resize(125, 125)
                        .into(imageView, whenDone(progressBar));
            }
        };
    }

    @NonNull
    private OnFailureListener getOnFailure() {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "error to load image file ", e);
            }
        };
    }

    private Callback whenDone(final ProgressBar progressBar) {
        return new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Error when trying to display image", e);
            }
        };
    }
}
