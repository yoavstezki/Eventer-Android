package com.yoavs.eventer.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * @author yoavs
 */

public abstract class BaseActivity extends AppCompatActivity {

    private int layoutResID;
    private Integer toolbarId;

    public BaseActivity(@LayoutRes int layoutResID, @IdRes int toolbarId) {
        this.layoutResID = layoutResID;
        this.toolbarId = toolbarId;
    }

    public BaseActivity(@LayoutRes int layoutResID) {
        this.layoutResID = layoutResID;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);

        if (toolbarId != null) {
            Toolbar toolbar = findViewById(toolbarId);

            setOnNavigationClick(toolbar);
        }
    }

    private void setOnNavigationClick(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
