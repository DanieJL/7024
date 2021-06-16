package com.daniejl.a7024;

import java.util.Date;

public class Week {
    private final int ID;
    private final Date startDate;
    private final Date endDate;
    private String[] actualTimes = new String[7];
    private double[] percentages = new double[7];


    Week(Date start) {
        this.ID = global.LAST_ID + 1;
        global.LAST_ID = this.ID;

        this.startDate = start;
        long time1 = startDate.getTime();
        long time2 = time1 + (6 * 86400 * 1000);
        this.endDate = new Date(time2);
    }

    Week(int id, String start, String end, String ats, String pers) {
        this.ID = id;
        this.startDate = new Date(Long.parseLong(start));
        this.endDate = new Date(Long.parseLong(end));

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

    public double getWeekIncentive() {
        double extraPercent = getWeekPerformance() - global.INCENTIVE_MIN;
        double incentivePay = 0;
        double totalHrs = 0;
        if (extraPercent > 0) {
            if (extraPercent > global.INCENTIVE_MAX - global.INCENTIVE_MIN) {
                extraPercent = global.INCENTIVE_MAX - global.INCENTIVE_MIN;
            }
            extraPercent = extraPercent / 100;
            incentivePay = extraPercent * global.BASE_PAY;
            for (int i = 0; i < 7; i++) {
                if (this.actualTimes[i] != null && this.percentages[i] > 0) {
                    totalHrs += getTimeAsDecimal(actualTimes[i]);
                }
            }
            incentivePay = incentivePay * totalHrs;
        }
        return incentivePay;
    }

    public double getWeekPerformance() {
        double totalHrs = 0;
        double weight = 0;
        for (int i = 0; i < 7; i++) {
            if (this.actualTimes[i] != null && this.percentages[i] > 0) {
                totalHrs += getTimeAsDecimal(actualTimes[i]);
                weight += getTimeAsDecimal(actualTimes[i]) * percentages[i];
            }
        }
        double average = weight / totalHrs;
        if (average > 0) {
            return average;
        }
        return 0;
    }


    //creates a double from a time string, "10:30" = 10.5, "9:45" = 9.75, etc.
    public static double getTimeAsDecimal(String s) {
        double hours = 0;
        if (s.matches("([0-9]+:[0-9]+|[0-9]+)")) {
            if (s.contains(":")) {
                hours = Double.parseDouble(s.substring(0, s.lastIndexOf(":")));
                double minutes = Double.parseDouble(s.substring(s.lastIndexOf(":") + 1));
                hours += (minutes / 60);
            } else {
                hours = Double.parseDouble(s);
            }
        } else {
            System.out.println(s + ": NON REGEX MATCH");
        }
        return hours;
    }

    //creates a time string from double, 10.5 = "10:30" , 9.75 = "9:45", etc.
    public static String getDecimalAsTime(double d) {
        String time = Double.toString(d);
        String hrsString = time.substring(0,time.indexOf("."));
        String minsString = time.substring(time.indexOf("."));

        int hrs = Integer.parseInt(hrsString);
        double mins = Double.parseDouble(minsString) * 60;

        hrsString = String.valueOf(hrs);
        minsString = String.valueOf(mins);
        minsString = minsString.substring(0, minsString.lastIndexOf("."));
        if(minsString.length()<2){
            minsString = "0" + minsString;
        }
        return (hrsString + ":" + minsString);
    }

    public void setPercentages(double[] percentages) {
        this.percentages = percentages;
    }

    public void setActualTimes(String[] actualTimes) {
        this.actualTimes = actualTimes;
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
}
