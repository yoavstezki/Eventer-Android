package com.yoavs.eventer.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Group;

/**
 * @author yoavs
 */

public class GroupViewHolder extends RecyclerView.ViewHolder {


    public TextView groupNameView;


    public GroupViewHolder(View itemView) {
        super(itemView);

        groupNameView = itemView.findViewById(R.id.group_name);

    }

    public void bindToGroup(Group group) {
        groupNameView.setText(group.getTitle());
    }


}
