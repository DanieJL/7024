package com.daniejl.a7024;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("[7024 App] Information");
        }


    }

    @Override
    protected void onResume() {
        populateHelpPage();
        super.onResume();
    }

    @Override
    public void onPause() {

        EditText cap = findViewById(R.id.incentiveCapInput);
        try{
            int incentiveCap = Integer.parseInt(cap.getText().toString());
            if(incentiveCap>0 && incentiveCap < 999){
                DataHandler.INCENTIVE_MAX = incentiveCap;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        EditText pay = findViewById(R.id.basePayInput);
        try{
            double basepay = Double.parseDouble(pay.getText().toString());
            if(basepay>0 && basepay <999){
                DataHandler.BASE_PAY = basepay;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        DataHandler.saveAllData();
        super.onPause();
    }

    private void populateHelpPage(){
        EditText cap = findViewById(R.id.incentiveCapInput);
        cap.setText(String.valueOf(DataHandler.INCENTIVE_MAX));

        EditText pay = findViewById(R.id.basePayInput);
        pay.setText(String.valueOf(DataHandler.BASE_PAY));

        TextView helpBox = findViewById(R.id.helpBox);
        String help = "<b>Base Pay:</b> Your pay before any premiums are added on, lower than your total hourly pay.<br>" +
                "<b>Incentive Pay:</b> The extra pay earned based on your weekly performance % and base pay.<br>" +
                "<b>Incentive Cap:</b> The performance percent where incentive pay maxes out.<br>" +
                "<b>Actual Time:</b> The time spent working- does NOT include startups, breaks, downtime, etc.<br>" +
                "<b>Standard Time:</b> The amount of work done, measured by time.<br>" +
                "<b>Performance %:</b> The percentage your standard time is of your actual time.<br>" +
                "<b>Error:</b> A mispick which causes the loss of incentive pay for the week in which it occurs. (Weeks with errors are excluded from the statistics page)";
        helpBox.setText(Html.fromHtml(help));

        TextView creditBox = findViewById(R.id.creditBox);
        String credit = "App made by Daniel Johnson<br><br>Special thanks to:" +
                "<br>-Evan \"Evan\" Sipes<br>-Stephan \"Briggs\" Briggs";
        creditBox.setText(Html.fromHtml(credit));

    }
}