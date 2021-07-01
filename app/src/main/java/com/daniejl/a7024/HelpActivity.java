package com.daniejl.a7024;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("[7024] Info Page");
        }
    }

    @Override
    protected void onResume() {
        populateHelpPage();
        super.onResume();
    }

    @Override
    public void onPause() {
        saveSettings();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void saveSettings() {
        EditText cap = findViewById(R.id.incentiveCapInput);
        try {
            int incentiveCap = Integer.parseInt(cap.getText().toString());
            if (incentiveCap > 0 && incentiveCap < 1000) {
                DataHandler.INCENTIVE_MAX = incentiveCap;
            }
        } catch (NumberFormatException ignored) {
        }

        EditText pay = findViewById(R.id.basePayInput);
        try {
            double basepay = Double.parseDouble(pay.getText().toString());
            if (basepay > 0 && basepay < 100000) {
                DataHandler.BASE_PAY = basepay;
            }
        } catch (NumberFormatException ignored) {
        }

        SwitchCompat exclude = findViewById(R.id.excludeSwitch);
        DataHandler.EXCLUDE_ERRORS = exclude.isChecked();

        DataHandler.saveAllData();
    }

    private void populateHelpPage() {
        EditText cap = findViewById(R.id.incentiveCapInput);
        cap.setText(String.valueOf(DataHandler.INCENTIVE_MAX));

        EditText pay = findViewById(R.id.basePayInput);
        pay.setText(String.valueOf(DataHandler.BASE_PAY));

        SwitchCompat exclude = findViewById(R.id.excludeSwitch);
        exclude.setChecked(DataHandler.EXCLUDE_ERRORS);

        TextView usageBox = findViewById(R.id.middleBox);
        String usage = "<b>Add new week:</b> Tap the green \"+\" button on the main page then select the first day of your work week.<br>" +
                "<b>Delete a week: </b> Long press on a week from the main page, tap \"Delete\" to delete that week.<br>" +
                "<b>See daily standard time:</b> To see a specific day's standard time, tap on that day's date label within that week.<br>" +
                "<b>See total times:</b> To see total standard/actual time for a week, long press any date label within that week.<br>" +
                "<b>Add/remove an error:</b> Long press on a week from the main page, tap \"Add Error\" to add, repeat to remove.<br>" +
                "<b>Save data/settings:</b> Data and settings are saved automatically."/*<br>" +
                "<b>Why actual time input?</b> This allows you to put in a guesstimated actual time for your final day to test different percentages" +
                " with to see how high/low you need to perform to obtain the average you want."*/;

        usageBox.setText(Html.fromHtml(usage, Html.FROM_HTML_MODE_COMPACT));

        TextView helpBox = findViewById(R.id.bottomBox);
        String help = "<b>Base Pay:</b> Hourly pay before any premiums are added on, typically less than total hourly pay.<br>" +
                "<b>Incentive Pay:</b> Extra hourly pay earned based on weekly performance % and base pay.<br>" +
                "<b>Incentive Cap:</b> The performance percent where incentive pay maxes out.<br>" +
                "<b>Actual Time:</b> Time spent working- does NOT include startups, breaks, downtime, etc.<br>" +
                "<b>Standard Time:</b> The amount of work done, measured by time.<br>" +
                "<b>Performance %:</b> The percentage your standard time is of your actual time.<br>" +
                "<b>Error:</b> A mispick which causes the loss of incentive pay for the week in which it occurs.";
        helpBox.setText(Html.fromHtml(help, Html.FROM_HTML_MODE_COMPACT));

        TextView creditBox = findViewById(R.id.aboutBox);
        String credit = "<b>App made by Daniel Johnson</b><br><br><u>Special thanks to:</u>" +
                "<br>Evan Sipes<br>Stephan Briggs";
        creditBox.setText(Html.fromHtml(credit, Html.FROM_HTML_MODE_COMPACT));
    }
}