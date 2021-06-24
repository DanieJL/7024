package com.daniejl.a7024;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {

    String[] greetings = {"Welcome!", "What's up?", "Yo yo yo!", "Hi friend!", "Howdy partner!", "What's new?", "G'day mate!", "Hi there!", "Hello!", "Sup!?", "Hiya pal!", "Ahoy matey!", "Goodmorrow!",
    "What's crackin'?", "‘Ello, gov’nor!", "Aloha!", "Greetings!", "Whazzzzup!?", "Bonjour!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataHandler.SAVE_FILE = getSharedPreferences("7024data", Context.MODE_PRIVATE);
        DataHandler.loadAllData();
        setDateListener();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int index = new Random().nextInt(greetings.length);
            actionBar.setTitle("[7024] " + greetings[index]);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
        }
    }

    @Override
    protected void onResume() {
        createMainButtons();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);
        return true;
    }

    //create the toolbar buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.statsButton) {
            startActivity(new Intent(MainActivity.this, StatActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.helpButton) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.daniejl.a7024");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //for all Weeks in weekList create a button on the main page + their actions
    private void createMainButtons() {
        LinearLayout ll = findViewById(R.id.weeklist);
        ll.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        DataHandler.WEEK_LIST.sort(Comparator.comparing(Week::getStartDate).thenComparing(Week::getID).reversed());

        for (Week w : DataHandler.WEEK_LIST) {
            String title = DataHandler.MDFormat.format(w.getStartDate()) + "-" + DataHandler.MDYYYYFormat.format(w.getEndDate());
            if (w.getWeekPerformance() > 0) {
                title += " [" + DataHandler.df.format(w.getWeekPerformance()) + "%]";
            } else {
                title += " [empty]";
            }
            Button myButton = new Button(this);
            myButton.setText(title);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((displayMetrics.widthPixels - 20), 200);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            myButton.setHapticFeedbackEnabled(true);
            myButton.setTextAppearance(R.style.TextAppearance_AppCompat_Large);
            if (w.isError()) {
                myButton.setTextColor(rgb(180, 180, 180));
            }
            ll.addView(myButton, lp);

            myButton.setOnClickListener(v -> {
                myButton.performHapticFeedback(16);
                DataHandler.ACTIVE_ID = w.getID();
                startActivity(new Intent(MainActivity.this, WeekActivity.class));
            });

            myButton.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth));
                builder.setCancelable(true);
                builder.setMessage("Delete the week of " + DataHandler.MDFormat.format(w.getStartDate()) + "-" + DataHandler.MDYYYYFormat.format(w.getEndDate()) + "?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    DataHandler.WEEK_LIST.remove(w);
                    DataHandler.saveAllData();
                    createMainButtons();
                });

                if (w.isError()) {
                    builder.setNeutralButton("Remove Error", (dialog, which) -> {
                        w.setError(false);
                        DataHandler.saveAllData();
                        createMainButtons();
                    });
                } else {
                    builder.setNeutralButton("Add Error", (dialog, which) -> {
                        w.setError(true);
                        DataHandler.saveAllData();
                        createMainButtons();
                    });
                }

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
                date = DataHandler.MDYYYYFormat.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                Week newWeek = new Week(date);
                DataHandler.WEEK_LIST.add(newWeek);
                DataHandler.ACTIVE_ID = newWeek.getID();
                DataHandler.saveAllData();
                startActivity(new Intent(MainActivity.this, WeekActivity.class));
            }
        };
    }
}