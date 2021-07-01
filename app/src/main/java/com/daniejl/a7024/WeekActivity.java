package com.daniejl.a7024;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeekActivity extends AppCompatActivity {
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

        for (TextView e : dateList) {
            String number = e.getTag().toString().replaceAll("[^0-9]", "");
            e.setOnClickListener(v -> showDayStandard((Integer.parseInt(number) - 1), e.getText().toString()));
            e.setOnLongClickListener(v -> {
                showTotalTimes();
                return true;
            });
        }

        Button btn = findViewById(R.id.calculate);
        btn.setOnClickListener(v -> {
            calculate();
            btn.performHapticFeedback(16);
        });

    }

    @Override
    protected void onResume() {
        populateWeekPage(DataHandler.ACTIVE_ID);
        super.onResume();
    }

    @Override
    public void onPause() {
        saveWeekPage(DataHandler.ACTIVE_ID);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void calculate() {
        saveWeekPage(DataHandler.ACTIVE_ID);
        colorForBadInput();
        TextView incentive = findViewById(R.id.incentive);
        TextView average = findViewById(R.id.average);
        for (Week w : DataHandler.WEEK_LIST) {
            if (DataHandler.ACTIVE_ID == w.getID()) {
                String avg = "<b>Performance: " + DataHandler.df.format(w.getWeekPerformance()) + "%</b>";
                average.setText(Html.fromHtml(avg, Html.FROM_HTML_MODE_COMPACT));
                String inc = "<b>Incentive Pay: $" + DataHandler.df.format(w.getWeekIncentive()) + "</b>";
                if(w.isError()){
                    inc = "<b><s>Incentive Pay:</s> $" + DataHandler.df.format(w.getWeekIncentive()) + "</b>";
                }
                incentive.setText(Html.fromHtml(inc, Html.FROM_HTML_MODE_COMPACT));
                break;
            }
        }
    }

    private void showTotalTimes(){
        calculate();
        for (Week w : DataHandler.WEEK_LIST) {
            if (DataHandler.ACTIVE_ID == w.getID()) {
                TextView line1 = findViewById(R.id.average);
                TextView line2 = findViewById(R.id.incentive);
                String msg1 = "<b>Actual time: " + w.getWeekActualTime() + "</b>";
                String msg2 = "<b>Standard time: " + w.getWeekStandardTime() + "</b>";
                line1.setText(Html.fromHtml(msg1, Html.FROM_HTML_MODE_COMPACT));
                line2.setText(Html.fromHtml(msg2, Html.FROM_HTML_MODE_COMPACT));
                break;
            }
        }
    }

    //show snackbar to display standard time for specific day
    private void showDayStandard(int day, String dayText) {
        String atTemp = actualTimeEntries.get(day).getText().toString();
        String perTemp = percentEntries.get(day).getText().toString();
        calculate();

        if (Week.isValidInput(atTemp, perTemp)) {
            double per = Double.parseDouble(perTemp) / 100;
            double at = Week.getTimeAsDecimal(atTemp);
            atTemp = Week.getDecimalAsTime((per * at));

            String dayText2 = dayText;
            for(Week w : DataHandler.WEEK_LIST){
                if(w.getID() == DataHandler.ACTIVE_ID){
                    dayText2 = "<u>" + DataHandler.EEEEEMDYYYYFormat.format(new Date(w.getStartDate().getTime() + (day * 24 * 60 * 60 * 1000))) + "</u>";
                    break;
                }
            }

            TextView line1 = findViewById(R.id.average);
            TextView line2 = findViewById(R.id.incentive);
            String msg2 = "Standard Time: " + atTemp;
            line1.setText(Html.fromHtml(dayText2, Html.FROM_HTML_MODE_COMPACT));
            line2.setText(msg2);
        }
    }

    //make valid input text color white, invalid input text color gray
    private void colorForBadInput() {
        for (int i = 0; i < 7; i++) {
            String at = actualTimeEntries.get(i).getText().toString();
            String per = percentEntries.get(i).getText().toString();

            if (Week.isValidInput(at, per)) {
                actualTimeEntries.get(i).setTextColor(Color.WHITE);
                percentEntries.get(i).setTextColor(Color.WHITE);
            } else {
                actualTimeEntries.get(i).setTextColor(Color.GRAY);
                percentEntries.get(i).setTextColor(Color.GRAY);
            }
        }
    }

    //put current AT/Per inputs into current week object, then save all data
    private void saveWeekPage(int ID) {
        String[] ats = new String[7];
        double[] pers = new double[7];
        for (int i = 0; i < 7; i++) {
            String atEntry = actualTimeEntries.get(i).getText().toString();
            String perEntry = percentEntries.get(i).getText().toString();
            if (atEntry.length() > 0) {
                ats[i] = atEntry;
            } else {
                ats[i] = null;
            }
            if (perEntry.length() > 0) {
                try {
                    pers[i] = Double.parseDouble(perEntry);
                } catch (NumberFormatException e) {
                    pers[i] = 0;
                }
            } else {
                pers[i] = 0;
            }
        }
        for (Week w : DataHandler.WEEK_LIST) {
            if (ID == w.getID()) {
                w.setActualTimes(ats);
                w.setPercentages(pers);
                break;
            }
        }
        DataHandler.saveAllData();
    }

    //build the week page from stored data for week being looked at
    private void populateWeekPage(int ID) {
        Week w = new Week(new Date());
        for (Week m : DataHandler.WEEK_LIST) {
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
                String perString = DataHandler.df2.format(pers[i]);
                perEntry.setText(perString);
            }
            String dateTitle = DataHandler.EEEMDFormat.format(new Date(w.getStartDate().getTime() + (i * 24 * 60 * 60 * 1000)));
            dateBox.setText(dateTitle);
        }
        String dateR = DataHandler.MDYYYYFormat.format(w.getStartDate()) + "-" + DataHandler.MDYYYYFormat.format(w.getEndDate());
        String title = "[7024] " + dateR;
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        calculate();
    }
}
