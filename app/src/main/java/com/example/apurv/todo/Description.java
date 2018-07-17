package com.example.apurv.todo;

import android.app.AlarmManager;

import android.app.PendingIntent;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLogTags;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import javax.crypto.spec.DESedeKeySpec;

public class Description extends AppCompatActivity implements View.OnClickListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
    Long id;
    TextView tv;
    TextView dateView;
    TextView timeView;
    EditText descriptionTextview;
    String time;
    String repeat_alarm;
    String setNotification;
    Switch aSwitch;
    Switch bSwitch;

    LinearLayout linearLayout;
    int position;
    int day, month, year, hour, min;

    todo obj;
    android.support.v7.widget.Toolbar toolbar;

    boolean flag = false;

    Button save_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        save_button = findViewById(R.id.button);

        Intent intent = getIntent();
        id = intent.getLongExtra("Id", -1);
        position = intent.getIntExtra("pos", -1);
        todoOpenHelper openHelper = new todoOpenHelper(this);
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Table_name, null, Contract.id + " = " + id, null, null, null, null);


        cursor.moveToNext();
        String title = cursor.getString(cursor.getColumnIndex(Contract.title));
        long date = cursor.getLong(cursor.getColumnIndex(Contract.date));
        String description = cursor.getString((cursor.getColumnIndex(Contract.description)));
        long id = cursor.getLong((cursor.getColumnIndex(Contract.id)));
        setNotification = cursor.getString(cursor.getColumnIndex((Contract.notification_set)));
        repeat_alarm=cursor.getString(cursor.getColumnIndex(Contract.repeating));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv = toolbar.findViewById(R.id.toolbar_title);
        getSupportActionBar().setTitle(" ");
        tv.setText(title);

        descriptionTextview = findViewById(R.id.description);
        descriptionTextview.append(description);


        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateView = findViewById(R.id.date);
        dateView.setText(day + "/" + month + "/" + year);
        timeView = findViewById(R.id.time);

        int hr = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        time = hr + ":" + min;

        timeView.setText(time);

        linearLayout = findViewById(R.id.parent_description);

        obj = new todo(title, (calendar.getTimeInMillis()), description, -1);

        aSwitch = findViewById(R.id.switch1);
        bSwitch = findViewById(R.id.switch2);

        if (setNotification.contentEquals("true"))
            aSwitch.setChecked(true);
        else {
            aSwitch.setChecked(false);
            bSwitch.setChecked(false);
        }


        if (setNotification.contentEquals("true")&&repeat_alarm.contentEquals("true"))
            bSwitch.setChecked(true);
        else {
            bSwitch.setChecked(false);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setNotification = "true";
                } else {
                    repeat_alarm="false";
                    bSwitch.setChecked(false);
                    setNotification = "false";

                }
            }
        });

        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setNotification="true";
                    aSwitch.setChecked(true);
                    repeat_alarm = "true";
                } else {
                    repeat_alarm = "false";

                }
            }
        });

        save_button.setOnClickListener(this);
    }

    public void dateChange(View view) {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(Description.this, year, month, day);
        datePickerDialog.setMinDate(calendar);
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    public void timeChange(View view) {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(Description.this, hour, min, false);
        if (day == calendar.get(Calendar.DAY_OF_MONTH) && month == calendar.get(Calendar.MONTH) && year == calendar.get(Calendar.YEAR))
            timePickerDialog.setMinTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

        timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
    }


    @Override
    public void onClick(View view) {

        // saveArrayList(arr,"record");

        if (checkValidDateTime()||setNotification.contentEquals("false")) {
            todoOpenHelper sopobj = new todoOpenHelper(this);
            SQLiteDatabase database = sopobj.getWritableDatabase();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, min);

            todo obj1 = new todo(obj.getName(), calendar.getTimeInMillis(), descriptionTextview.getText().toString(), id);
            obj1.setTimedate(calendar.getTimeInMillis());
            obj1.setNotification_set(setNotification);
            obj1.setRepeat_alarm(repeat_alarm);
            obj.setId(id);
            ContentValues contentValues = new ContentValues();
            contentValues.put(Contract.title, obj1.getName());
            contentValues.put(Contract.description, obj1.getDescription());
            contentValues.put(Contract.date, obj1.getTimedate());
            contentValues.put(Contract.notification_set, obj1.getNotification_set());
            contentValues.put(Contract.id, obj1.getId());
            contentValues.put(Contract.repeating,obj1.getRepeat_alarm());


            String[] value = {Long.toString(id)};
            database.update(Contract.Table_name, contentValues, Contract.id + " = ?", value);

            Intent intent = new Intent();
            intent.putExtra("Id", id);
            intent.putExtra("pos", position);
            intent.putExtra("title", obj1.getName());
            intent.putExtra("description", obj1.getDescription());
            intent.putExtra("date", obj1.getTimedate());
            intent.putExtra("set", setNotification);
            intent.putExtra("repeat",repeat_alarm);
            setResult(2, intent);
            finish();
        } else {
            Toast.makeText(this, "Set Valid time", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkValidDateTime() {
        Calendar calendar=Calendar.getInstance();
        calendar.set(year,month,day,hour,min);
        if(System.currentTimeMillis()<=calendar.getTimeInMillis())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(3);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int tempMonth = month + 1;
        dateView.setText(dayOfMonth +"/"+month +"/"+year);
        Description.this.day =dayOfMonth;
        Description.this.month =monthOfYear;
        Description.this.year =year;

        Calendar calendar = Calendar.getInstance();

        if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH) && monthOfYear == calendar.get(Calendar.MONTH) && year == calendar.get(Calendar.YEAR)) {
            if (hour < calendar.get(Calendar.HOUR_OF_DAY)) {
                Description.this.hour = calendar.get(Calendar.HOUR_OF_DAY);
                Description.this.min = calendar.get(Calendar.MINUTE);
                timeView.setText(" ");
                Snackbar.make(linearLayout, "Enter Valid Time Again Or System Time Will Be Set", Snackbar.LENGTH_SHORT)
                        .setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                        .show();
                return;
            } else if (min < calendar.get(Calendar.MINUTE)) {
                Description.this.hour = calendar.get(Calendar.HOUR_OF_DAY);
                Description.this.min = calendar.get(Calendar.MINUTE);
                timeView.setText(" ");
                Snackbar.make(linearLayout, "Enter Valid Time Again Or System Time Will Be Set", Snackbar.LENGTH_SHORT)
                        .setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                        .show();
                return;
            }
        }


}

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        timeView.setText(hourOfDay + ":" + minute);
        Description.this.hour = hourOfDay;
        Description.this.min = minute;
    }
}
