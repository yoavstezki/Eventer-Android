package com.yoavs.eventer.adpter;

import android.content.Context;
import android.graphics.Paint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yoavs.eventer.DB.UsersDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Request;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.service.ImageService;

import java.util.List;

/**
 * @author yoavs
 */

public class RequestListAdapter extends ArrayAdapter<Request> {

    private static final String TAG = "RequestListAdapter";
    private final ImageService imageService = new ImageService();


    public RequestListAdapter(@NonNull Context context, @NonNull List<Request> requests) {
        super(context, 0, requests);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_request, parent, false);
        }

        Request request = getItem(position);

        if (request != null) {

            ImageView suggestedUserPic = convertView.findViewById(R.id.suggested_user_pic);
            ProgressBar picProgressBar = convertView.findViewById(R.id.pic_progress_bar);

            imageService.loadImage(request.getSuggestedUserId(), getContext(), getOnSuccess(suggestedUserPic, picProgressBar), getOnFailure());

            TextView requestItemName = convertView.findViewById(R.id.request_item_name);
            final TextView approvalUserName = convertView.findViewById(R.id.approval_user_name);

            requestItemName.setText(request.getItemName());

            if (request.getPurchase() && request.getApprovalUserId() != null) {
                requestItemName.setPaintFlags(requestItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                UsersDB.getInstance().getUserReference(request.getApprovalUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            approvalUserName.setText(user != null ? user.getName() : null);
                            approvalUserName.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                requestItemName.setPaintFlags(0);
                approvalUserName.setText(null);
                approvalUserName.setVisibility(View.GONE);
            }
        }
        return convertView;
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
