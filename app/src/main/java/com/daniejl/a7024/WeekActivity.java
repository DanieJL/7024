package com.daniejl.a7024;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeekActivity extends AppCompatActivity {
    private String title = "Edit Week";
    private static List<EditText> percentEntries;
    private static List<EditText> actualTimeEntries;
    private static List<TextView> dateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        actualTimeEntries = new ArrayList<EditText>() {{
            add(findViewById(R.id.at1));
            add(findViewById(R.id.at2));
            add(findViewById(R.id.at3));
            add(findViewById(R.id.at4));
            add(findViewById(R.id.at5));
            add(findViewById(R.id.at6));
            add(findViewById(R.id.at7));
        }};
        percentEntries = new ArrayList<EditText>() {{
            add(findViewById(R.id.per1));
            add(findViewById(R.id.per2));
            add(findViewById(R.id.per3));
            add(findViewById(R.id.per4));
            add(findViewById(R.id.per5));
            add(findViewById(R.id.per6));
            add(findViewById(R.id.per7));
        }};
        dateList = new ArrayList<TextView>() {{
            add(findViewById(R.id.date1));
            add(findViewById(R.id.date2));
            add(findViewById(R.id.date3));
            add(findViewById(R.id.date4));
            add(findViewById(R.id.date5));
            add(findViewById(R.id.date6));
            add(findViewById(R.id.date7));
        }};
    }

    @Override
    protected void onResume() {
        populateWeekPage(global.ACTIVE_ID);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }

        Button btn = findViewById(R.id.calculate);
        btn.setOnClickListener(v -> calculate(global.ACTIVE_ID));
        super.onResume();
    }

    @Override
    public void onPause() {
        saveWeekPage(global.ACTIVE_ID);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //getContentView().performHapticFeedback(5);
            onBackPressed();
        }
        return true;
    }

    private void calculate(int ID) {
        saveWeekPage(global.ACTIVE_ID);
        colorForBadInput();
        TextView incentive = findViewById(R.id.incentive);
        TextView average = findViewById(R.id.average);
        for (Week w : global.WEEKLIST) {
            if (ID == w.getID()) {
                String avg = "Average: " + global.df.format(w.getWeekPerformance()) + "%";
                average.setText(avg);
                String inc = "Incentive: $" + global.df.format(w.getWeekIncentive());
                incentive.setText(inc);
                break;
            }
        }
    }

    private void showDayStandard(int day, String dayText) { //this needs to pull from textboxes, not from the object!
        String atTemp = actualTimeEntries.get(day).getText().toString();
        String perTemp = percentEntries.get(day).getText().toString();

        if(perTemp.length()>0 && atTemp.length()>0) {
            double per = Double.parseDouble(perTemp)/100;
            double at = Week.getTimeAsDecimal(atTemp);
            atTemp = Week.getDecimalAsTime(per*at);

            String msg = dayText + " standard time: " + atTemp;
            Snackbar mySnackbar = Snackbar.make(this, findViewById(R.id.entry), msg, 3000);
            mySnackbar.show();
        }
    }

    private void colorForBadInput(){
        for(int i = 0; i<7; i++){
            double per = 0;
            String at = actualTimeEntries.get(i).getText().toString();
            String perString = percentEntries.get(i).getText().toString();

            if(perString.length()>0) {
                per = Double.parseDouble(perString);
            }
            if(at.matches("([0-9]+:[0-9]+|[0-9]+)") && per > 0){
                actualTimeEntries.get(i).setTextColor(Color.WHITE);
                percentEntries.get(i).setTextColor(Color.WHITE);
            }
            else {
                actualTimeEntries.get(i).setTextColor(Color.GRAY);
                percentEntries.get(i).setTextColor(Color.GRAY);
            }
        }
    }

    //save Week objects to JSON, put into sharedPreferences
    private void saveWeekPage(int ID) {
        String[] ats = new String[7];
        double[] pers = new double[7];
        for (int i = 0; i < 7; i++) {
            EditText atEntry = actualTimeEntries.get(i);
            EditText perEntry = percentEntries.get(i);
            if (atEntry.getText().length() > 0) {
                ats[i] = atEntry.getText().toString();
            }
            if (perEntry.getText().length() > 0) {
                pers[i] = Double.parseDouble(perEntry.getText().toString());
            }
        }
        for (Week w : global.WEEKLIST) {
            if (ID == w.getID()) {
                w.setActualTimes(ats);
                w.setPercentages(pers);
                break;
            }
        }
        global.saveAllData();
    }

    private void populateWeekPage(int ID) {
        for (TextView e : dateList) {
            String number = e.getTag().toString().replaceAll("[^0-9]", "");
            e.setOnClickListener(v -> showDayStandard((Integer.parseInt(number)-1), e.getText().toString()));
        }
        Week w = new Week(new Date());
        for (Week m : global.WEEKLIST) {
            if (m.getID() == ID) {
                w = m;
                break;
            }
        }
        String[] ats = w.getActualTimes();
        double[] pers = w.getPercentages();

        for (int i = 0; i < 7; i++) {
            EditText atEntry = actualTimeEntries.get(i);
            EditText perEntry = percentEntries.get(i);
            TextView dateBox = dateList.get(i);
            if (ats[i] != null) {
                atEntry.setText(ats[i]);
            }
            if (pers[i] > 0) {
                String perString = Double.toString(pers[i]);
                perEntry.setText(perString);
            }
            String dateTitle = "  " + global.simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (i * 24 * 60 * 60 * 1000)));
            dateBox.setText(dateTitle);
        }
        String dateR = global.simpleDateFormat2.format(w.getStartDate()) + "-" + global.simpleDateFormat2.format(w.getEndDate());
        title = "Week " + dateR;
        calculate(w.getID());
    }
}
