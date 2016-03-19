package com.example.MyLearningApp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import uk.me.jstott.coordconv.LatitudeLongitude;
import uk.me.jstott.sun.Sun;
import uk.me.jstott.sun.Time;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Raja.Chirala on 06/03/2016.
 */
public class DateCheck extends Activity{

    TextView tv_date_picker;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_check);
        tv_date_picker = (TextView) findViewById(R.id.textView3);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(tv_date_picker);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        TextView mTextView;

        public DatePickerFragment() {
            //
        }

        public DatePickerFragment(TextView textview) {
            mTextView = textview;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            MainActivity inst = new MainActivity();
            double sun_longitude = inst.getSunLongitude(year, month + 1, day);
            double moon_longitude = inst.getMoonLongitude(year, month + 1, day);
            int tithi = inst.getTithi(sun_longitude, moon_longitude);

            int paksa = inst.getPaksa(tithi);

            double ayanamsa = inst.getAyanamsa(year, month + 1, day);

            int naksatra = (int) Math.floor(inst.put_in_360(moon_longitude - ayanamsa) * (3 / 40.0));

            int rasi = inst.getRasi(sun_longitude, ayanamsa);

            double local_longitude = 78.48;

            double local_latitude = 17.37;

            LatitudeLongitude ll2 = new LatitudeLongitude(local_latitude, local_longitude);
            Calendar cal = Calendar.getInstance();
            TimeZone timeZone = cal.getTimeZone();
            String timeZonename = cal.getTimeZone().getDisplayName();
            int dst_savings = cal.getTimeZone().getDSTSavings();
            boolean dst = false;
            if (dst_savings > 0){
                dst = true;
            }

            cal.set(year, month, day);
            Time sunrise = Sun.sunriseTime(cal, ll2, timeZone, dst);
            String masa = inst.getMasa(year, month, day, paksa);
            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            mTextView.setText("Tithi: " + tithi + "\nPaksa: " + paksa + " Masa: " + masa + "\nNaksatra:" + naksatra + "\nSun Rise:" + sunrise + "\nAyanamsa:" + ayanamsa
                    + "\nSun Longitude: " + sun_longitude + "\nMoon Longitude:" + moon_longitude + "\nTime Zone: " + timeZone + "\nDST Savings: " + dst_savings
                    + "\nTimezone Name: " + timeZonename);

        }
    }
}
