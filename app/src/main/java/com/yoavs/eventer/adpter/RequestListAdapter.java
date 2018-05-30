package com.yoavs.eventer.adpter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.yoavs.eventer.DB.UsersDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Request;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.utils.ImageLoaderUtil;

import java.util.List;

/**
 * @author yoavs
 */

public class RequestListAdapter extends ArrayAdapter<Request> {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


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

            ImageLoaderUtil.loadImage(request.getSuggestedUserId(), suggestedUserPic, picProgressBar);

            TextView requestItemName = convertView.findViewById(R.id.request_item_name);
            final TextView approvalUserName = convertView.findViewById(R.id.approval_user_name);

            requestItemName.setText(request.getItemName());

            if (request.getPurchase() && request.getApprovalUserId() != null) {
                requestItemName.setPaintFlags(requestItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


                UsersDB.getInstance().findUserByKey(request.getApprovalUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
}
