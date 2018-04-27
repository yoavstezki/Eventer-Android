package com.yoavs.eventer.adpter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Group;

import java.util.List;

/**
 * @author yoavs
 */

public class GroupListAdapter extends ArrayAdapter<Group> {

    public GroupListAdapter(@NonNull Context context, List<Group> groups) {
        super(context, 0, groups);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Group group = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_group, parent, false);
        }

        TextView groupNameTextView = convertView.findViewById(R.id.group_name);

        groupNameTextView.setText(group.title);

        return convertView;
    }
}
