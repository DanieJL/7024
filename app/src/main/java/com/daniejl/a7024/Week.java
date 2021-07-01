package com.daniejl.a7024;

import java.util.Date;

public class Week {
    private final int ID;
    private final Date startDate;
    private final Date endDate;
    private boolean error;
    private String[] actualTimes = new String[7];
    private double[] percentages = new double[7];


    //constructor for user created weeks
    Week(Date start) {
        this.ID = DataHandler.LAST_ID + 1;
        DataHandler.LAST_ID = this.ID;

        this.error = false;
        this.startDate = start;
        long time1 = startDate.getTime();
        long time2 = time1 + (6 * 86400 * 1000);
        this.endDate = new Date(time2);
    }

    //constructor for re-creating weeks from save data
    Week(int id, String start, String end, String err, String ats, String pers) {
        this.ID = id;

        this.startDate = new Date(Long.parseLong(start));
        this.endDate = new Date(Long.parseLong(end));

        this.error = err.equals("true");

        ats = ats.substring(1, ats.length() - 1);
        pers = pers.substring(1, pers.length() - 1);
        ats = ats.replaceAll("\\s", "");
        pers = pers.replaceAll("\\s", "");
        String[] atsArr = ats.split(",", -1);
        String[] persArr = pers.split(",", -1);

        for (int i = 0; i < 7; i++) {
            this.actualTimes[i] = atsArr[i];
            if (atsArr[i].contains("null")) {
                this.actualTimes[i] = null;
            }
        }
        for (int i = 0; i < 7; i++) {
            this.percentages[i] = Double.parseDouble(persArr[i]);
        }
    }

    public void setPercentages(double[] percentages) {
        this.percentages = percentages;
    }

    public void setActualTimes(String[] actualTimes) {
        this.actualTimes = actualTimes;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String[] getActualTimes() {
        return actualTimes;
    }

    public double[] getPercentages() {
        return percentages;
    }

    public int getID() {
        return ID;
    }

    //calculate the incentive for the week
    public double getWeekIncentive() {
        double extraPercent = getWeekPerformance() - DataHandler.INCENTIVE_MIN;
        double incentivePay = 0;
        double totalHrs = 0;
        if (extraPercent > 0) {
            if (extraPercent > (DataHandler.INCENTIVE_MAX - DataHandler.INCENTIVE_MIN)) {
                extraPercent = DataHandler.INCENTIVE_MAX - DataHandler.INCENTIVE_MIN;
            }
            extraPercent = extraPercent / 100;
            incentivePay = extraPercent * DataHandler.BASE_PAY;
            for (int i = 0; i < 7; i++) {
                if (isValidInput(this.actualTimes[i], this.percentages[i])) {
                    totalHrs += getTimeAsDecimal(this.actualTimes[i]);
                }
            }
            incentivePay = incentivePay * totalHrs;
        }
        return incentivePay;
    }

    //calculate the performance for the week
    public double getWeekPerformance() {
        double totalHrs = 0;
        double weight = 0;
        for (int i = 0; i < 7; i++) {
            if (isValidInput(this.actualTimes[i], this.percentages[i])) {
                double time = getTimeAsDecimal(this.actualTimes[i]);
                totalHrs += time;
                weight += time * this.percentages[i];
            }
        }
        double average = weight / totalHrs;
        if (average > 0) {
            return average;
        }
        return 0;
    }

    public String getWeekActualTime(){
        double actHrs = 0;
        for (int i = 0; i < 7; i++) {
            if (isValidInput(this.actualTimes[i], this.percentages[i])) {
                actHrs += getTimeAsDecimal(this.actualTimes[i]);
            }
        }
        return getDecimalAsTime(actHrs);
    }

    public String getWeekStandardTime(){
        double stdHrs = 0;
        for (int i = 0; i < 7; i++) {
            if (isValidInput(this.actualTimes[i], this.percentages[i])) {
                stdHrs += getTimeAsDecimal(this.actualTimes[i]) * (this.percentages[i]/100);
            }
        }
        return getDecimalAsTime(stdHrs);
    }

    //used to verify input is valid
    public static boolean isValidInput(String actualTime, String percent) {
        boolean result = false;
        double at = getTimeAsDecimal(actualTime);
        if (percent.matches("([0-9]+.[0-9]+|[0-9]+|.[0-9]+|[0-9]+.)")) {
            double per = Double.parseDouble(percent);
            if (per > 0 && per < 10000000 && at > 0 && at < 100000) {
                result = true;
            }
        }
        return result;
    }

    //used to verify input is valid
    public static boolean isValidInput(String actualTime, double per) {
        boolean result = false;
        double at = getTimeAsDecimal(actualTime);
        if (per > 0 && per < 10000000 && at > 0 && at < 100000) {
            result = true;
        }
        return result;
    }

    //creates a double from a time string, "10:30" = 10.5, "9:45" = 9.75, etc.
    public static double getTimeAsDecimal(String s) {
        double hours = 0;
        if (s == null) {
            return hours;
        }
        if (s.matches("([0-9]+:[0-5][0-9]|[0-9]+)")) {
            if (s.contains(":")) {
                hours = Double.parseDouble(s.substring(0, s.lastIndexOf(":")));
                double minutes = Double.parseDouble(s.substring(s.lastIndexOf(":") + 1));
                hours += (minutes / 60);
            } else {
                hours = Double.parseDouble(s);
            }
        }
        return hours;
    }

    //creates a time string from double, 10.5 = "10:30" , 9.75 = "9:45", etc.
    public static String getDecimalAsTime(double d) {
        String time = Double.toString(d);
        String hrsString = time.substring(0, time.indexOf("."));
        String minsString = time.substring(time.indexOf("."));

        int hrs = Integer.parseInt(hrsString);
        double mins = Double.parseDouble(minsString) * 60;

        hrsString = String.valueOf(hrs);
        minsString = String.valueOf(mins);
        minsString = minsString.substring(0, minsString.lastIndexOf("."));
        if (minsString.length() < 2) {
            minsString = "0" + minsString;
        }
        return (hrsString + ":" + minsString);
    }
}
