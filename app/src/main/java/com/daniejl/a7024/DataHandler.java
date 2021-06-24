package com.daniejl.a7024;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DataHandler extends MainActivity {
    public static int LAST_ID = 0;       //increment IDs for created weeks
    public static int ACTIVE_ID;         //the ID of the week currently opened
    public static double BASE_PAY = 20;  //the user's base pay
    public static boolean EXCLUDE_ERRORS = true;

    public static int INCENTIVE_MIN = 100;
    public static int INCENTIVE_MAX = 130;

    public static List<Week> WEEK_LIST = new ArrayList<>();
    public static SharedPreferences SAVE_FILE;
    public static DatePickerDialog.OnDateSetListener mDateSetListener;

    public static final DecimalFormat df = new DecimalFormat("#0.00");
    public static final Locale LOCALE = new Locale("en", "US");
    public static final SimpleDateFormat MDFormat = new SimpleDateFormat("M/d", LOCALE);
    public static final SimpleDateFormat MDYYYYFormat = new SimpleDateFormat("M/d/yyyy", LOCALE);
    public static final SimpleDateFormat EEEMDFormat = new SimpleDateFormat("EEE, M/d", LOCALE);

    public static void saveAllData() {
        JSONArray weeksJSON = new JSONArray();
        for (Week W : DataHandler.WEEK_LIST) {
            JSONObject data = new JSONObject();
            try {
                data.put("ID", W.getID());
                data.put("startDate", W.getStartDate().getTime());
                data.put("endDate", W.getEndDate().getTime());
                data.put("error", W.isError());
                data.put("actualTimes", Arrays.toString(W.getActualTimes()));
                data.put("percentages", Arrays.toString(W.getPercentages()));
                weeksJSON.put(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences.Editor editor = SAVE_FILE.edit();
        editor.putString("JSON", weeksJSON.toString());
        editor.putInt("lastID", DataHandler.LAST_ID);
        editor.putInt("incentiveCap", DataHandler.INCENTIVE_MAX);
        editor.putString("basePay", String.valueOf(DataHandler.BASE_PAY));
        editor.putBoolean("excludeErrors", DataHandler.EXCLUDE_ERRORS);
        editor.apply();
    }

    public static void loadAllData() {
        DataHandler.WEEK_LIST.clear();
        SharedPreferences prefs = SAVE_FILE;
        String JSONString = prefs.getString("JSON", "");
        DataHandler.LAST_ID = prefs.getInt("lastID", 0);
        DataHandler.INCENTIVE_MAX = prefs.getInt("incentiveCap", 130);
        DataHandler.BASE_PAY = Double.parseDouble(prefs.getString("basePay", "18.5"));
        DataHandler.EXCLUDE_ERRORS = prefs.getBoolean("excludeErrors", true);

        try {
            JSONArray temp = new JSONArray(JSONString);
            for (int i = 0; i < temp.length(); i++) {
                JSONObject week = temp.getJSONObject(i);

                int id = week.getInt("ID");
                String startDate = week.getString("startDate");
                String endDate = week.getString("endDate");
                String error = week.getString("error");
                String actualTimes = week.getString("actualTimes");
                String percentages = week.getString("percentages");

                DataHandler.WEEK_LIST.add(new Week(id, startDate, endDate, error, actualTimes, percentages));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
