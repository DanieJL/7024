package com.daniejl.a7024;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataHandler.SAVE_FILE = getSharedPreferences("7024data", Context.MODE_PRIVATE);
        DataHandler.loadAllData();
        setDateListener();
    }

    @Override
    protected void onResume() {
        createMainButtons();
        super.onResume();
    }

    //for all Weeks in weekList create a button on the main page
    private void createMainButtons() {
        LinearLayout ll = findViewById(R.id.weeklist);
        ll.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        DataHandler.WEEKLIST.sort(Comparator.comparing(Week::getStartDate).thenComparing(Week::getID).reversed());

        for (Week w : DataHandler.WEEKLIST) {
            String title = DataHandler.MDFormat.format(w.getStartDate()) + "-" + DataHandler.MDYYYFormat.format(w.getEndDate());
            if (w.getWeekPerformance() > 0) {
                title += " [" + DataHandler.df.format(w.getWeekPerformance()) + "%]";
            } else {
                title += " (empty)";
            }
            Button myButton = new Button(this);
            myButton.setText(title);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(displayMetrics.widthPixels - 20, 200);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            myButton.setHapticFeedbackEnabled(true);
            myButton.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
            ll.addView(myButton, lp);

            myButton.setOnClickListener(v -> {
                myButton.performHapticFeedback(16);
                DataHandler.ACTIVE_ID = w.getID();
                startActivity(new Intent(MainActivity.this, WeekActivity.class));
            });

            myButton.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth));
                builder.setCancelable(true);
                builder.setMessage("Delete the week of " + DataHandler.MDFormat.format(w.getStartDate()) + "-" + DataHandler.MDFormat.format(w.getEndDate()) + "?");
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                    DataHandler.WEEKLIST.remove(w);
                    DataHandler.saveAllData();
                    createMainButtons();
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            });
        }
        FloatingActionButton addWeekBtn = findViewById(R.id.newweek);
        addWeekBtn.setOnClickListener(view -> {
            createNewWeek();
            addWeekBtn.performHapticFeedback(12);
        });
    }

    //bring up the date selector to create a new week
    private void createNewWeek() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                MainActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                DataHandler.mDateSetListener,
                year, month, day);
        dialog.setMessage("First day of new week:");
        dialog.show();
    }

    //create new week using selected date
    private void setDateListener() {
        DataHandler.mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String strDate = month + "/" + day + "/" + year;
            Date date = null;
            try {
                date = DataHandler.MDYYYFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                Week newWeek = new Week(date);
                DataHandler.WEEKLIST.add(newWeek);
                DataHandler.ACTIVE_ID = newWeek.getID();
                DataHandler.saveAllData();
                startActivity(new Intent(MainActivity.this, WeekActivity.class));
            }
        };
    }
}