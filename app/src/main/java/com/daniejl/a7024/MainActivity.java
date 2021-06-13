package com.daniejl.a7024;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static int LAST_ID = 0;
    public static int BASE_PAY = 20;
    private static final Locale LOCALE = new Locale("en","US");

    private List<Week> weekList = new ArrayList<>();
    DecimalFormat df = new DecimalFormat("#0.00");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", LOCALE);
    private static final SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM/dd/yyyy", LOCALE);
    private static final SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat(" EEE, M/dd", LOCALE);
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.mainToolbar));

        mDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            String strDate = month + "/" + day + "/" + year;
            Date date = null;
            try {
                date = simpleDateFormat2.parse(strDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date!=null) {
                Week newWeek = new Week(date);
                weekList.add(newWeek);
                saveWeeksList();
                goToWeekPage(newWeek.getID());
            }
        };

      //  saveWeeksList();
        loadWeekList();
        createMainButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        FloatingActionButton check = findViewById(R.id.newweek);
        if(check==null){
            inflater.inflate(R.menu.back_menu, menu);
        }
        else {
            inflater.inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_back) {//    saveWeekPage(ID);
            setContentView(R.layout.activity_main);
            setSupportActionBar(findViewById(R.id.mainToolbar));
            createMainButtons();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //save Week objects to JSON, put into sharedPreferences
    private void saveWeeksList(){
        JSONArray weeksJSON = new JSONArray();
        for(Week W : weekList){
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
        SharedPreferences.Editor editor = getSharedPreferences("7024data", MODE_PRIVATE).edit();
        System.out.println(weeksJSON.toString());
        editor.putString("JSON", weeksJSON.toString());
        editor.putInt("lastID", LAST_ID);
        editor.apply();

    }

    //load save data to create weekList
    private void loadWeekList(){
        weekList.clear();
        SharedPreferences prefs = getSharedPreferences("7024data", MODE_PRIVATE);
        String JSONString = prefs.getString("JSON", "");
        LAST_ID = prefs.getInt("lastID", 0);

        try {
            JSONArray temp = new JSONArray(JSONString);
            for(int i = 0; i < temp.length(); i++) {
                JSONObject week = temp.getJSONObject(i);

                int id = week.getInt("ID");
                String startDate = week.getString("startDate");
                String endDate = week.getString("endDate");
                String actualTimes = week.getString("actualTimes");
                String percentages = week.getString("percentages");

                weekList.add(new Week(id, startDate, endDate, actualTimes, percentages));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //for all Weeks in weekList create a button on the main page.
    @SuppressLint("ResourceAsColor")
    private void createMainButtons(){
        LinearLayout ll = findViewById(R.id.weeklist);
        ll.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        weekList.sort(Comparator.comparing(Week::getStartDate).reversed());

        for(Week w : weekList){
            Button myButton = new Button(this);
            String title = simpleDateFormat.format(w.getStartDate()) + "-" + simpleDateFormat.format(w.getEndDate()) + " (" + df.format(w.getWeekPerformance()) + "%)";
            myButton.setText(title);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(displayMetrics.widthPixels-20, 200);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            myButton.setTextAppearance( R.style.TextAppearance_AppCompat_Large);
            ll.addView(myButton, lp);

            myButton.setOnClickListener(v -> goToWeekPage(w.getID()));

            myButton.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth));
                builder.setCancelable(true);
                builder.setMessage("Delete the week of " + simpleDateFormat.format(w.getStartDate()) + "-" + simpleDateFormat.format(w.getEndDate()) + "?");
                builder.setPositiveButton("Confirm", (dialog, which) -> {
                            weekList.remove(w);
                            saveWeeksList();
                            createMainButtons();
                        });
                builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {});
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            });
        }
        FloatingActionButton addWeekBtn = findViewById(R.id.newweek);
        addWeekBtn.setOnClickListener(view -> createNewWeek());
    }

    //take start date input, create week, save all, go to new blank week page
    private void createNewWeek(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                MainActivity.this,
                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);
        dialog.setMessage("First day of new week:");
        dialog.show();
    }

    private void goToWeekPage(int ID){
        setContentView(R.layout.activity_week);
        for(Week w : weekList) {
            if (ID == w.getID()) {
                populateWeekPage(w);
                break;
            }
        }
        setSupportActionBar(findViewById(R.id.backToolbar));
        Button btn = findViewById(R.id.calculate);
        btn.setOnClickListener(v -> {
            saveWeekPage(ID);
            calculate(ID);
        });
    }

    private void calculate(int ID){
        TextView incentive = findViewById(R.id.incentive);
        TextView standard = findViewById(R.id.standard);
        TextView average = findViewById(R.id.average);
        for(Week w : weekList) {
            if (ID == w.getID()) {
                String avg = "Average: " + df.format(w.getWeekPerformance()) + "%";
                average.setText(avg);
                String inc = "Incentive: $" + df.format(w.getWeekIncentive());
                incentive.setText(inc);
                String std = "Standards: " + df.format(w.getWeekStandards());
                standard.setText(std);
                break;
            }
        }
    }

    private void populateWeekPage(Week w){
        //make array of these and cycle + assign, currently too sloppy
        TextView daterange = findViewById(R.id.daterange);
        TextView incentive = findViewById(R.id.incentive);
        TextView standard = findViewById(R.id.standard);
        TextView average = findViewById(R.id.average);
        TextView date1 = findViewById(R.id.date1);
        TextView date2 = findViewById(R.id.date2);
        TextView date3 = findViewById(R.id.date3);
        TextView date4 = findViewById(R.id.date4);
        TextView date5 = findViewById(R.id.date5);
        TextView date6 = findViewById(R.id.date6);
        TextView date7 = findViewById(R.id.date7);
        EditText at1 = findViewById(R.id.at1);
        EditText at2 = findViewById(R.id.at2);
        EditText at3 = findViewById(R.id.at3);
        EditText at4 = findViewById(R.id.at4);
        EditText at5 = findViewById(R.id.at5);
        EditText at6 = findViewById(R.id.at6);
        EditText at7 = findViewById(R.id.at7);
        EditText per1 = findViewById(R.id.per1);
        EditText per2 = findViewById(R.id.per2);
        EditText per3 = findViewById(R.id.per3);
        EditText per4 = findViewById(R.id.per4);
        EditText per5 = findViewById(R.id.per5);
        EditText per6 = findViewById(R.id.per6);
        EditText per7 = findViewById(R.id.per7);

        String dateR = simpleDateFormat2.format(w.getStartDate()) + "-" + simpleDateFormat2.format(w.getEndDate());
        daterange.setText(dateR);
        date1.setText(simpleDateFormat3.format(w.getStartDate()));
        date2.setText(simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (24 * 60 * 60 * 1000))));
        date3.setText(simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (2 * 24 * 60 * 60 * 1000))));
        date4.setText(simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (3 * 24 * 60 * 60 * 1000))));
        date5.setText(simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (4 * 24 * 60 * 60 * 1000))));
        date6.setText(simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (5 * 24 * 60 * 60 * 1000))));
        date7.setText(simpleDateFormat3.format(new Date(w.getStartDate().getTime() + (6 * 24 * 60 * 60 * 1000))));
        String[] ats = w.getActualTimes();
        if(ats[0]!=null) {at1.setText(ats[0]);}
        if(ats[1]!=null) {at2.setText(ats[1]);}
        if(ats[2]!=null) {at3.setText(ats[2]);}
        if(ats[3]!=null) {at4.setText(ats[3]);}
        if(ats[4]!=null) {at5.setText(ats[4]);}
        if(ats[5]!=null) {at6.setText(ats[5]);}
        if(ats[6]!=null) {at7.setText(ats[6]);}
        double[] pers = w.getPercentages();
        if(pers[0]>0) {per1.setText(Double.toString(pers[0]));}
        if(pers[1]>0) {per2.setText(Double.toString(pers[1]));}
        if(pers[2]>0) {per3.setText(Double.toString(pers[2]));}
        if(pers[3]>0) {per4.setText(Double.toString(pers[3]));}
        if(pers[4]>0) {per5.setText(Double.toString(pers[4]));}
        if(pers[5]>0) {per6.setText(Double.toString(pers[5]));}
        if(pers[6]>0) {per7.setText(Double.toString(pers[6]));}
        String avg = "Average: " + df.format(w.getWeekPerformance()) + "%";
        average.setText(avg);
        String inc = "Incentive: $" + df.format(w.getWeekIncentive());
        incentive.setText(inc);
        String std = "Standards: " + df.format(w.getWeekStandards());
        standard.setText(std);

    }

    private void saveWeekPage(int id){
        EditText at1 = findViewById(R.id.at1);
        EditText at2 = findViewById(R.id.at2);
        EditText at3 = findViewById(R.id.at3);
        EditText at4 = findViewById(R.id.at4);
        EditText at5 = findViewById(R.id.at5);
        EditText at6 = findViewById(R.id.at6);
        EditText at7 = findViewById(R.id.at7);
        EditText per1 = findViewById(R.id.per1);
        EditText per2 = findViewById(R.id.per2);
        EditText per3 = findViewById(R.id.per3);
        EditText per4 = findViewById(R.id.per4);
        EditText per5 = findViewById(R.id.per5);
        EditText per6 = findViewById(R.id.per6);
        EditText per7 = findViewById(R.id.per7);

        String[] ats = new String[7];
        if(at1.getText().length()>0) {ats[0] = at1.getText().toString();}
        if(at2.getText().length()>0) {ats[1] = at2.getText().toString();}
        if(at3.getText().length()>0) {ats[2] = at3.getText().toString();}
        if(at4.getText().length()>0) {ats[3] = at4.getText().toString();}
        if(at5.getText().length()>0) {ats[4] = at5.getText().toString();}
        if(at6.getText().length()>0) {ats[5] = at6.getText().toString();}
        if(at7.getText().length()>0) {ats[6] = at7.getText().toString();}
        double[] pers = new double[7];
        if(per1.getText().length()>0) {pers[0] = Double.parseDouble(per1.getText().toString());}
        if(per2.getText().length()>0) {pers[1] = Double.parseDouble(per2.getText().toString());}
        if(per3.getText().length()>0) {pers[2] = Double.parseDouble(per3.getText().toString());}
        if(per4.getText().length()>0) {pers[3] = Double.parseDouble(per4.getText().toString());}
        if(per5.getText().length()>0) {pers[4] = Double.parseDouble(per5.getText().toString());}
        if(per6.getText().length()>0) {pers[5] = Double.parseDouble(per6.getText().toString());}
        if(per7.getText().length()>0) {pers[6] = Double.parseDouble(per7.getText().toString());}

        for(Week w : weekList){
            if(id == w.getID()){
                w.setActualTimes(ats);
                w.setPercentages(pers);
            }
        }
        saveWeeksList();
    }
}