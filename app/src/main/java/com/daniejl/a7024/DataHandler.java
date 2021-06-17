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

    public static double INCENTIVE_MIN = 100;
    public static double INCENTIVE_MAX = 130;

    public static List<Week> WEEKLIST = new ArrayList<>();
    public static SharedPreferences SAVE_FILE;
    public static DatePickerDialog.OnDateSetListener mDateSetListener;

    public static final DecimalFormat df = new DecimalFormat("#0.00");
    public static final Locale LOCALE = new Locale("en", "US");
    public static final SimpleDateFormat MDFormat = new SimpleDateFormat("M/d", LOCALE);
    public static final SimpleDateFormat MDYYYFormat = new SimpleDateFormat("M/d/yyyy", LOCALE);
    public static final SimpleDateFormat EEEMDFormat = new SimpleDateFormat("EEE, M/d", LOCALE);

    public static void saveAllData() {
        JSONArray weeksJSON = new JSONArray();
        for (Week W : DataHandler.WEEKLIST) {
            JSONObject data = new JSONObject();
            try {
                data.put("ID", W.getID());
                data.put("startDate", W.getStartDate().getTime());
                data.put("endDate", W.getEndDate().getTime());
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
        editor.apply();
    }

    public static void loadAllData() {
        DataHandler.WEEKLIST.clear();
        SharedPreferences prefs = SAVE_FILE;
        String JSONString = prefs.getString("JSON", "");
        DataHandler.LAST_ID = prefs.getInt("lastID", 0);

        try {
            JSONArray temp = new JSONArray(JSONString);
            for (int i = 0; i < temp.length(); i++) {
                JSONObject week = temp.getJSONObject(i);

                int id = week.getInt("ID");
                String startDate = week.getString("startDate");
                String endDate = week.getString("endDate");
                String actualTimes = week.getString("actualTimes");
                String percentages = week.getString("percentages");

                DataHandler.WEEKLIST.add(new Week(id, startDate, endDate, actualTimes, percentages));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
