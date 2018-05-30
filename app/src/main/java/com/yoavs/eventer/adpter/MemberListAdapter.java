package com.yoavs.eventer.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.User;
import com.yoavs.eventer.service.ImageService;
import com.yoavs.eventer.utils.ImageLoaderUtil;

import java.util.List;

/**
 * @author yoavs
 */

public class MemberListAdapter extends ArrayAdapter<User> {

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


        ImageLoaderUtil.loadImage(user.getUId(), imageView, progressBar);

        groupNameTextView.setText(user != null ? user.getName() : null);


        return convertView;
    }
}
