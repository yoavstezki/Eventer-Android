package com.yoavs.eventer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.yoavs.eventer.DB.RequestsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.entity.Request;
import com.yoavs.eventer.enums.ScreenModeEnum;

import java.util.Date;

/**
 * @author yoavs
 */

public class CreateOrUpdateRequestActivity extends BaseActivity {

    private TextInputEditText requestItemName;
    private ProgressBar progressBar;
    private ScreenModeEnum screenModeEnum;
    private Request request;
    private String eventKey;


    public CreateOrUpdateRequestActivity() {
        super(R.layout.activity_create_update_request, R.id.new_request_toolbar);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView titleNewRequest = findViewById(R.id.title_request_screen);
        requestItemName = findViewById(R.id.new_request_item_name);

        Button clearBtn = findViewById(R.id.new_request_clear_btn);
        Button createOrUpdateBtn = findViewById(R.id.new_request_create_btn);

        progressBar = findViewById(R.id.new_request_pb);


        clearBtn.setOnClickListener(getClearListener());


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //create mode
            screenModeEnum = (ScreenModeEnum) extras.get("screenMode");
            eventKey = extras.getString("eventKey");

            if (screenModeEnum != null) {

                switch (screenModeEnum) {
                    case CREATE:
                        createOrUpdateBtn.setOnClickListener(getCreateListener(eventKey));
                        break;
                    case UPDATE:
                        request = (Request) extras.get("request");
                        if (request != null) {
                            createOrUpdateBtn.setText(R.string.update);
                            createOrUpdateBtn.setOnClickListener(getUpdateListener(eventKey, request));
                            titleNewRequest.setText(R.string.update_item_requeset);
                            requestItemName.setText(request.getItemName());
                        }
                        break;
                }
            }
        }
    }


    @NonNull
    private View.OnClickListener getClearListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestItemName.setText(null);
            }
        };
    }

    @NonNull
    private View.OnClickListener getCreateListener(final String eventKey) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                String requestItemName = CreateOrUpdateRequestActivity.this.requestItemName.getText().toString();

                RequestsDB.addNewRequest(eventKey, requestItemName, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        progressBar.setVisibility(View.GONE);
                        CreateOrUpdateRequestActivity.this.onBackPressed();
                    }
                });
            }
        };
    }

    @NonNull
    private View.OnClickListener getUpdateListener(final String eventKey, final Request request) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                String updateRequestItemName = requestItemName.getText().toString();
                request.setItemName(updateRequestItemName);
                request.setLastUpdate(new Date().getTime());

                RequestsDB.updateRequestName(eventKey, request.getKey(), request.getItemName(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        progressBar.setVisibility(View.GONE);
                        CreateOrUpdateRequestActivity.this.onBackPressed();
                    }
                });
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_or_update, menu);

        //show menu for update screen only..
        return screenModeEnum.equals(ScreenModeEnum.UPDATE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete: {
                progressBar.setVisibility(View.VISIBLE);
                RequestsDB.removeItemRequest(eventKey, request.getKey())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                CreateOrUpdateRequestActivity.this.onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CreateOrUpdateRequestActivity.this, R.string.failed_to_remove_item_request, Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }

        return super.onOptionsItemSelected(item);
    }


}
