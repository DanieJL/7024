package com.daniejl.a7024;
import java.util.Date;

public class Week {
    private final int ID;
    private final Date startDate;
    private final Date endDate;
    private String[] actualTimes = new String[7];
    private double[] percentages = new double[7];

    Week(Date start){
        this.ID = MainActivity.LAST_ID +1;
        MainActivity.LAST_ID = this.ID;

        this.startDate = start;
        long time1 = startDate.getTime();
        long time2 = time1 + (6 * 86400 * 1000);
        this.endDate = new Date(time2);
    }

    Week(int id, String start, String end, String ats, String pers){
        this.ID = id;
        this.startDate = new Date(Long.parseLong(start));
        this.endDate = new Date(Long.parseLong(end));

        ats = ats.substring(1, ats.length()-1);
        pers = pers.substring(1,pers.length()-1);
        ats = ats.replaceAll("\\s","");
        pers = pers.replaceAll("\\s","");
        String[] atsArr = ats.split(",",-1);
        String[] persArr = pers.split(",",-1);

        for(int i = 0; i<7;i++){
            this.actualTimes[i] = atsArr[i];
            if(atsArr[i].contains("null")){
                this.actualTimes[i] = null;
            }
        }
        for(int i = 0; i<7;i++){
            this.percentages[i] = Double.parseDouble(persArr[i]);
        }
    }

    public double getWeekIncentive(){
        double extraPercent = getWeekPerformance() - 100;
        double incentivePay = 0;
        double totalHrs = 0;
        if(extraPercent>0){
            if(extraPercent>30) {
                extraPercent = 30;
            }
            extraPercent = extraPercent/100;
            incentivePay = extraPercent * MainActivity.BASE_PAY;
            for(int i = 0; i<7; i++){
                if(this.actualTimes[i]!=null) {
                    totalHrs += getTimeDecimal(actualTimes[i]);
                }
            }
            incentivePay = incentivePay * totalHrs;
        }
        return incentivePay;
    }

    public double getWeekPerformance(){
        double totalHrs = 0;
        double weight = 0;
        for(int i = 0; i<7; i++){
            if(this.actualTimes[i]!=null && this.percentages[i]!=0) {
                totalHrs += getTimeDecimal(actualTimes[i]);
                weight += getTimeDecimal(actualTimes[i]) * percentages[i];
            }
        }
        return weight/totalHrs;
    }

    public Double getWeekStandards(){
        double totalAT = 0;
        double performace = getWeekPerformance()/100;
        for(int i = 0; i<7; i++){
            if(this.actualTimes[i]!=null) {
                totalAT += getTimeDecimal(actualTimes[i]);
            }
        }
        return totalAT * performace;
    }

    private double getTimeDecimal(String s){
        double hours = 0;
        if(s.matches("([0-9]+:[0-9]+|[0-9]+)")) {
            if(s.contains(":")) {
                hours = Double.parseDouble(s.substring(0, s.lastIndexOf(":")));
                double minutes = Double.parseDouble(s.substring(s.lastIndexOf(":") + 1));
                hours += minutes / 60;
            }
            else{
                hours = Double.parseDouble(s);
            }
        }
        else{System.out.println(s + ": NON REGEX MATCH");}
        return hours;
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

    public Date getStartDate(){
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
