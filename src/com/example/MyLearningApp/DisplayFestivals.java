package com.example.MyLearningApp;

/**
 * Created by Raja.Chirala on 09/02/2016.
 */
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.*;

import java.util.ArrayList;

public class DisplayFestivals extends Activity {

    DBHelper mydb;

       @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.display_festivals);

            mydb = new DBHelper(this);


            ArrayList<FestivalDetails> festival_list = mydb.getAllFestivals();

           //FestivalListAdapter adapter = new FestivalListAdapter(this, R.layout.festival_each_item, festival_list);

            ListView listView = (ListView) findViewById(R.id.festivals_list);
            listView.setAdapter(new FestivalListAdapter(this, festival_list));

        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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


}
