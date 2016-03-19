package com.example.MyLearningApp;

/**
 * Created by Raja.Chirala on 09/02/2016.
 */

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.*;
import android.app.Activity;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.*;

import java.util.*;

public class DisplayFestivals extends Activity {

    DBHelper mydb;
    ListView listView;
    EditText editsearch;
    FestivalListAdapter adapter;
    ArrayAdapter<Integer> dataAdapter;
    private boolean isSpinnerInitial = true;
    private LinearLayout mProgress;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_festivals);
        mProgress = (LinearLayout) findViewById(R.id.loader);

        List<Integer> years = Arrays.asList(getYearList());
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);

        // Check if the dates present in the calendar db are for present year or not. If not, calculate for present year
        mydb = new DBHelper(this);
        if (!mydb.is_current_year_data()){
            mProgress.setVisibility(LinearLayout.VISIBLE);
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        Looper.prepare();
                        Calendar c = Calendar.getInstance();
                        mydb.calculate_festival_days(c.get(Calendar.YEAR));

                        mHandler.post(new Runnable() {
                            public void run() {
                                listView = (ListView) findViewById(R.id.festivals_list);
                                ArrayList<FestivalDetails> festival_list = mydb.getAllFestivals();
                                adapter = new FestivalListAdapter(getApplicationContext(), festival_list);
                                listView.setAdapter(adapter);
                                mProgress.setVisibility(LinearLayout.GONE);
                            }

                        });

                        Looper.loop();
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }
        else{
            listView = (ListView) findViewById(R.id.festivals_list);
            ArrayList<FestivalDetails> festival_list = mydb.getAllFestivals();
            adapter = new FestivalListAdapter(getApplicationContext(), festival_list);
            listView.setAdapter(adapter);
        }


        // Locate the EditText in listview_main.xml
        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.spinner1);
        item.setTitle("Year");
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
        spinner.setSelection(Calendar.getInstance().get(Calendar.YEAR)-2000);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {


                if (isSpinnerInitial) {
                    isSpinnerInitial = false;
                } else {

                    mProgress.setVisibility(LinearLayout.VISIBLE);
                    final int year = (int) arg0.getItemAtPosition(arg2);

                    new Thread(new Runnable() {
                        public void run() {
                            Looper.prepare();
                            mydb.calculate_festival_days(year);

                            // Update the progress bar
                            mHandler.post(new Runnable() {
                                public void run() {
                                    final ArrayList<FestivalDetails> festival_list = mydb.getAllFestivals();
                                    adapter = new FestivalListAdapter(getApplicationContext(), festival_list);
                                    listView.setAdapter(adapter);
                                    mProgress.setVisibility(LinearLayout.GONE);

                                }

                            });
                            Looper.loop();
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Integer[] getYearList() {
        Integer[] years = new Integer[100];
        years[0] = 2000;
        for (int i = 1; i < 100; i++) {
            years[i] = years[i - 1] + 1;
        }
        return years;
    }

}
