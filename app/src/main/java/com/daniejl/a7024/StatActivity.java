package com.daniejl.a7024;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class StatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("7024 Statistics");
        }

        populateStats();
    }

    private void populateStats(){
        int totalDays = 0;
        double totalST = 0;
        double totalAT = 0;
        double avgST = 0;
        double avgAT = 0;
        double avgPer = 0;
        double avgInc = 0;
        double bestAT = 0;
        double bestST = 0;
        double bestPer = 0;
        double totalInc = 0;

        for(Week w : DataHandler.WEEK_LIST){
            String[] actualTimes = w.getActualTimes();
            double[] percents = w.getPercentages();
            for(int i = 0; i < 7; i++){
                if(Week.isValidInput(actualTimes[i],percents[i])){
                    double actualTime = Week.getTimeAsDecimal(actualTimes[i]);
                    double standardTime = actualTime * (percents[i]/100);

                    totalDays+=1;
                    totalAT += actualTime;
                    totalST += standardTime;
                    if(actualTime > bestAT){
                        bestAT = actualTime;
                    }
                    if(standardTime > bestST){
                        bestST = standardTime;
                    }
                    if(percents[i] > bestPer){
                        bestPer = percents[i];
                    }
                }
            }
            totalInc += w.getWeekIncentive();
        }
        avgST = totalST / totalDays;
        avgAT = totalAT / totalDays;
        avgInc = totalInc / totalDays;
        avgPer = (100/avgAT) * avgST;

        TextView dataBox = findViewById(R.id.statsBox);

        String text = "Total Days: " + totalDays +
                "\nTotal Incentive Pay: " + DataHandler.df.format(totalInc) + "$" +
                "\nTotal Actual Time: " + Week.getDecimalAsTime(totalAT) +
                "\nTotal Standard Time: " + Week.getDecimalAsTime(totalST) +
                "\n" +
                "\nAverage Percent: " + DataHandler.df.format(avgPer) + "%" +
                "\nAverage Incentive: " + DataHandler.df.format(avgInc) + "$" +
                "\nAverage Actual Time: " + Week.getDecimalAsTime(avgAT) +
                "\nAverage Standard Time: " + Week.getDecimalAsTime(avgST) +
                "\n" +
                "\nBest Percent: " + DataHandler.df.format(bestPer) + "%" +
                "\nBest Actual Time: " + Week.getDecimalAsTime(bestAT) +
                "\nBest Standard Time: " + Week.getDecimalAsTime(bestST);

        dataBox.setText(text);
    }
}