package com.example.apurv.todo;

import android.app.AlarmManager;

import android.app.PendingIntent;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Layout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Validator;
import javax.xml.validation.ValidatorHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {


    TodoAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<todo> arr;
    FloatingActionButton button;
    android.support.v7.widget.Toolbar tv;
    Boolean List_Empty=true;
    Layout list;
    View dialogView;
    android.support.design.widget.CoordinatorLayout parent_layout;

    public static final String DATE_KEY = "date";
    public static final String TIME_KEY = "time";

    int day, month, year, hour, min;

    Boolean timeSetOnce=false;

    EditText Title;
    EditText et2;
    EditText dateEditText;
    EditText timeEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent_layout=findViewById(R.id.parent_main);

        arr = new ArrayList<>();
        adapter=new TodoAdapter(arr, this, new CustomItemLongClickListener() {
            @Override
            public void onItemLongClick(final View v, final int position) {
                //       todo obj = arr.get(i);
                final int pos = position;


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Do you want to delete?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                                        // Do something after 5s = 5000ms
                                        todoOpenHelper obj = new todoOpenHelper(MainActivity.this);
                                        SQLiteDatabase database = obj.getWritableDatabase();
                                final todo todoobj = new todo(arr.get(position).getName(), arr.get(position).getTimedate(),arr.get(position).getDescription(), arr.get(position).getId());
                                todoobj.setTimedate(arr.get(position).getTimedate());
                                todoobj.setNotification_set(arr.get(position).getNotification_set());

                                        long id = arr.get(pos).getId();
                                        String[] value = {Long.toString(id)};
                                        database.delete(Contract.Table_name, Contract.id + " = ? ", value);
                                        arr.remove(pos);
                                        adapter.notifyDataSetChanged();
                                        setListBack();

                                Snackbar.make(parent_layout, "Want To Undo Change?", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                saveExpenseInDatabase(todoobj);
                                                arr.add(position,todoobj);
                                                adapter.notifyDataSetChanged();
                                                return;
                                            }
                                        })
                                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                        .show();



                            }
                        }
                );
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        }, new CustomItemListner() {
            @Override
            public void onClickListener(View v, int position) {
                Intent intent=new Intent(MainActivity.this,Description.class);
                intent.putExtra("Id",arr.get(position).getId());
                intent.putExtra("pos",position);
                startActivityForResult(intent,1);
            }
        });
                button = findViewById(R.id.fab);

        tv = findViewById(R.id.toolbar);

        tv.setTitle("ToDo LIST");
        tv.setTitleTextColor(getResources().getColor(R.color.ListBack));

        button.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.list);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());

        todoOpenHelper openHelper = new todoOpenHelper(this);
        SQLiteDatabase database = openHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Table_name, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(Contract.title));
            long date = cursor.getLong(cursor.getColumnIndex(Contract.date));
            String description = cursor.getString((cursor.getColumnIndex(Contract.description)));
            long id = cursor.getLong((cursor.getColumnIndex(Contract.id)));
            todo obj = new todo(title, date, description, id);
            String set=cursor.getString(cursor.getColumnIndex(Contract.notification_set));
            obj.setNotification_set(set);
            obj.setTimedate(date);
            obj.setRepeat_alarm(cursor.getColumnName(cursor.getColumnIndex(Contract.repeating)));
            arr.add(0, obj);
        }
       setListBack();


    }

    private void setListBack() {
        if(arr.size()==0)
        {
            recyclerView.setBackground(getDrawable(R.drawable.empty_list_back));
        }
        else
        {
            recyclerView.setBackgroundColor(getResources().getColor(R.color.ListBack));
        }
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;


        builder = new AlertDialog.Builder(this);

        dialogView = getLayoutInflater().inflate(R.layout.add_layout, null);
        Title = dialogView.findViewById(R.id.expenseTitleEditText);
        et2 = dialogView.findViewById(R.id.description);
        dateEditText = dialogView.findViewById(R.id.addExpense_DateEditText);
        timeEditText = dialogView.findViewById(R.id.addExpense_TimeEditText);

        builder.setView(dialogView);


        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog =DatePickerDialog.newInstance(MainActivity.this, year, month, day);
                datePickerDialog.setMinDate(calendar);
                datePickerDialog.show(getFragmentManager(),"DatePickerDialog");
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Calendar calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog =TimePickerDialog.newInstance(MainActivity.this, hour, min, false);
                if(day==calendar.get(Calendar.DAY_OF_MONTH)&&month==calendar.get(Calendar.MONTH)&&year==calendar.get(Calendar.YEAR))
                timePickerDialog.setMinTime(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND));

                timePickerDialog.show(getFragmentManager(),"TimePickerDialog");
            }
        });

        builder.setTitle("Add TODO ITEM");
        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String todoTitle, description;

                todoTitle = Title.getText().toString();
                description = et2.getText().toString();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, min);

                todo obj = new todo(todoTitle, (calendar.getTimeInMillis()), description, -1);
                obj.setNotification_set("true");
                obj.setTimedate(calendar.getTimeInMillis());
                arr.add(obj);
                saveExpenseInDatabase(obj);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==2)
        {
            Long id1 = data.getLongExtra("Id", -1);

            String title=data.getStringExtra("title");
            long date=data.getLongExtra("date",-1);
            String description=data.getStringExtra("description");
            String repeat=data.getStringExtra("repeat");


                todo obj = new todo(title, date, description, id1);
                String set =data.getStringExtra("set");
                obj.setNotification_set(set);
                obj.setTimedate(date);
                obj.setRepeat_alarm(repeat);


            arr.remove(data.getIntExtra("pos", 0));
            arr.add(data.getIntExtra("pos", 0), obj);
            adapter.notifyDataSetChanged();

            if(set.contentEquals("true"))
            {
                setAlarm(id1,obj.getTimedate(),obj.getName(),obj.getDescription(),obj.getRepeat_alarm());

            }
            else
            {
                cancelAlarm(id1);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void saveExpenseInDatabase(todo obj) {
        todoOpenHelper expensesOpenHelper = new todoOpenHelper(this);
        SQLiteDatabase database = expensesOpenHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.repeating,obj.getRepeat_alarm());
        contentValues.put(Contract.title, obj.getName());
        contentValues.put(Contract.description, obj.getDescription());
        contentValues.put(Contract.date, obj.getTimedate());
        contentValues.put(Contract.notification_set,obj.getNotification_set());
        long id = database.insert(Contract.Table_name, null, contentValues);
        if (id > -1L) {
            obj.setId(id);
            adapter.notifyDataSetChanged();
            setAlarm(id,obj.getTimedate(),obj.getName(),obj.getDescription(),obj.getRepeat_alarm());
            setListBack();
        }

    }

   public void cancelAlarm(long ReqCode)
   {
       AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

       Toast.makeText(MainActivity.this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
       Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,(int) ReqCode, myIntent, 0);
       manager.cancel(pendingIntent);
   }
   public void setAlarm(long ReqCode,Long time,String title,String description,String repeat) {
       AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

       Toast.makeText(MainActivity.this, "Alarm Set", Toast.LENGTH_SHORT).show();
       Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
       myIntent.putExtra("title", title);
       myIntent.putExtra("description", description);
       PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, (int) ReqCode, myIntent, 0);
       if (repeat.contentEquals("true")) {
           manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, time,86400000, pendingIntent);
       } else {
           manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

       }
   }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int tempMonth = month + 1;



        Calendar calendar = Calendar.getInstance();
if(timeSetOnce) {
    if (dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH) && monthOfYear == calendar.get(Calendar.MONTH) && year == calendar.get(Calendar.YEAR)) {
        if (hour < calendar.get(Calendar.HOUR_OF_DAY)) {
            MainActivity.this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            MainActivity.this.min=calendar.get(Calendar.MINUTE);
            timeEditText.setText(" ");
            Snackbar.make(dialogView, "Enter Valid Time Again Or System Time Will Be Set", Snackbar.LENGTH_SHORT)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
            return;
        } else if (min < calendar.get(Calendar.MINUTE)) {
            MainActivity.this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            MainActivity.this.min=calendar.get(Calendar.MINUTE);
            timeEditText.setText(" ");
            Snackbar.make(dialogView, "Enter Valid Time Again Or System Time Will Be Set", Snackbar.LENGTH_SHORT)
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
        dateEditText.setText(dayOfMonth + "/" + month + "/" +year);
        MainActivity.this.day = dayOfMonth;
        MainActivity.this.month = monthOfYear;
        MainActivity.this.year = year;



    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        timeSetOnce=true;
        timeEditText.setText(hourOfDay + ":" + minute);
        MainActivity.this.hour = hourOfDay;
        MainActivity.this.min = minute;
    }
}

