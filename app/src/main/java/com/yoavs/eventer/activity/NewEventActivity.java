package com.yoavs.eventer.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.yoavs.eventer.DB.EventsDB;
import com.yoavs.eventer.DB.GroupsDB;
import com.yoavs.eventer.DB.UserGroupsDB;
import com.yoavs.eventer.R;
import com.yoavs.eventer.components.DatePiker;
import com.yoavs.eventer.entity.Group;
import com.yoavs.eventer.events.EventDateSelected;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yoavs
 */

public class NewEventActivity extends BaseActivity {

    private TextInputEditText newEventTitle;
    private TextInputEditText newEventDate;
    private Spinner groupListSpinner;
    private Button clearBtn;
    private Button createBtn;
    private ProgressBar progressBar;
    private EventBus eventBus = EventBus.getDefault();

    private ArrayAdapter<Group> arrayAdapter;


    public NewEventActivity() {
        super(R.layout.activity_new_event, R.id.new_event_toolbar);
        eventBus.register(NewEventActivity.this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newEventTitle = findViewById(R.id.new_event_title);
        newEventDate = findViewById(R.id.new_event_date);
        groupListSpinner = findViewById(R.id.group_list_spinner);
        createBtn = findViewById(R.id.new_event_create_btn);
        clearBtn = findViewById(R.id.new_event_clear_btn);
        progressBar = findViewById(R.id.new_event_pb);

        setGroupListAdapter();
        setOnClearClick();
        setOnCreateClick();
        setOnNewEventDateClick();
    }

    private void setOnNewEventDateClick() {

        newEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = getDatePickerDialog();
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });
    }

    @NonNull
    private DatePickerDialog getDatePickerDialog() {
        DatePiker datePiker = new DatePiker();

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(datePiker, datePiker.getYear(), datePiker.getMonth(), datePiker.getDay());

        datePickerDialog.setThemeDark(false);

        datePickerDialog.showYearPickerFirst(false);

        datePickerDialog.setAccentColor(Color.parseColor("#009688"));

        datePickerDialog.setTitle(getString(R.string.select_event_date));
        return datePickerDialog;
    }

    private void setGroupListAdapter() {
        List<Group> userGroups = getUserGroups();

        arrayAdapter = new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_spinner_item, userGroups);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        groupListSpinner.setAdapter(arrayAdapter);
    }

    private void setOnClearClick() {
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEventTitle.setText(null);
                newEventDate.setText(null);
                groupListSpinner.setSelection(0);
            }
        });
    }

    private void setOnCreateClick() {

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                progressBar.setVisibility(View.VISIBLE);

                Group selectedItem = (Group) groupListSpinner.getSelectedItem();

                EventsDB.addNewEvent
                        (
                                newEventTitle.getText().toString(),
                                newEventDate.getText().toString(),
                                selectedItem.getKey(),
                                new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        progressBar.setVisibility(View.GONE);
                                        NewEventActivity.this.onBackPressed();
                                    }
                                }
                        );
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(newEventTitle.getWindowToken(), 0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventDateSelected(EventDateSelected eventDateSelected) {
        newEventDate.setText(eventDateSelected.getSelectedDare());
    }


    private List<Group> getUserGroups() {

        final List<Group> groups = new ArrayList<>();

        UserGroupsDB.getUserGroups()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String groupKey = snapshot.getKey();

                            GroupsDB.findGroup(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    Group group = dataSnapshot.getValue(Group.class);
                                    group.setKey(dataSnapshot.getKey());
                                    groups.add(group);
//                                    groupMaterialSpinnerAdapter.notifyDataSetChanged();
                                    arrayAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return groups;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(NewEventActivity.this);
    }
}
