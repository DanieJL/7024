package com.daniejl.a7024;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class StatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("[7024 App] Statistics");
        }
        populateStats();

    }

    private void populateStats() {
        int totalDays = 0;
        double totalST = 0;
        double totalAT = 0;
        double avgST;
        double avgAT;
        double avgPer;
        double bestAT = 0;
        double bestST = 0;
        double bestPer = 0;
        double totalInc = 0;

        for (Week w : DataHandler.WEEK_LIST) {
            if (!w.isError() || !DataHandler.EXCLUDE_ERRORS) {
                String[] actualTimes = w.getActualTimes();
                double[] percents = w.getPercentages();
                for (int i = 0; i < 7; i++) {
                    if (Week.isValidInput(actualTimes[i], percents[i])) {
                        double actualTime = Week.getTimeAsDecimal(actualTimes[i]);
                        double standardTime = actualTime * (percents[i] / 100);

                        totalDays += 1;
                        totalAT += actualTime;
                        totalST += standardTime;
                        if (actualTime > bestAT) {
                            bestAT = actualTime;
                        }
                        if (standardTime > bestST) {
                            bestST = standardTime;
                        }
                        if (percents[i] > bestPer) {
                            bestPer = percents[i];
                        }
                    }
                }
                totalInc += w.getWeekIncentive();
            }
        }
        TextView dataBox = findViewById(R.id.statsBox);

        if (totalDays > 0) {
            avgST = totalST / totalDays;
            avgAT = totalAT / totalDays;
            avgPer = (100 / avgAT) * avgST;

            String text = "<br>Total Days: " + totalDays + "<br>" +
                    "<br>Average Percent: " + DataHandler.df.format(avgPer) + "%" +
                    "<br>Average Actual Time: " + Week.getDecimalAsTime(avgAT) +
                    "<br>Average Standard Time: " + Week.getDecimalAsTime(avgST) +
                    "<br>" +
                    "<br>Best Percent: " + DataHandler.df.format(bestPer) + "%" +
                    "<br>Best Actual Time: " + Week.getDecimalAsTime(bestAT) +
                    "<br>Best Standard Time: " + Week.getDecimalAsTime(bestST) +
                    "<br>" +
                    "<br>Total Incentive Pay: $" + DataHandler.df.format(totalInc) +
                    "<br>Total Actual Time: " + Week.getDecimalAsTime(totalAT) +
                    "<br>Total Standard Time: " + Week.getDecimalAsTime(totalST);

            dataBox.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            String text = "You have no entries yet!";
            dataBox.setText(text);
        }
    }
}