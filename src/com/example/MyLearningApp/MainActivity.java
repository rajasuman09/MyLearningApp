package com.example.MyLearningApp;


import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import uk.me.jstott.sun.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import uk.me.jstott.coordconv.LatitudeLongitude;


public class MainActivity extends Activity {

    Button b1, b2, b3;
    TextView positionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        DBHelper myDbHelper;
        myDbHelper = new DBHelper(this);

        positionView = (TextView) findViewById(R.id.textView3);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            myDbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        b1=(Button)findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("http://www.example.com"));
                startActivity(i);
            }
        });

        b2=(Button)findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DisplayFestivals.class);
                startActivity(i);
            }
        });

        b3=(Button)findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
            Notify("Haribol", "You've received new message");
                }

        });


    }
    private void Notify(String notificationTitle, String notificationMessage){
        int mId = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.vaishnavacalendarlogo)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(positionView);
        newFragment.show(getFragmentManager(), "datePicker");
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        TextView mTextView;

        public DatePickerFragment()
        {
            //
        }

        public DatePickerFragment(TextView textview)
        {
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

            double sun_longitude = getSunLongitude(year, month + 1, day);
            double moon_longitude = getMoonLongitude(year, month + 1, day);
            int tithi = getTithi(sun_longitude, moon_longitude);

            int paksa = getPaksa(tithi);

            double ayanamsa = getAyanamsa(year, month + 1, day);

            int naksatra = (int) Math.floor(put_in_360(moon_longitude - ayanamsa) * (3 / 40.0));

            int rasi = getRasi(sun_longitude, ayanamsa);

            double local_longitude = 78.48;

            double local_latitude = 17.37;

            double local_offset = 5.5;

            LatitudeLongitude ll2 = new LatitudeLongitude(local_latitude, local_longitude);
            TimeZone timeZone = TimeZone.getTimeZone("GMT+5:30");
            boolean dst = false;
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            Time sunrise = Sun.sunriseTime(cal, ll2, timeZone, dst);
            String masa = getMasaName(year, month + 1, day);
            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            mTextView.setText("Tithi: " + tithi + "\nPaksa: " + paksa + "\nNaksatra:" + naksatra + "\nSun Rise:" + sunrise + "\nAyanamsa:" + ayanamsa
            + "\nSun Longitude: "+sun_longitude + "\nMoon Longitude:" + moon_longitude);

        }

        public String getMasaName(int year, int month, int day) {

            int[] rasi = new int[6];
            boolean actual_day;
            boolean conjunction_found;
            int tyear = year, tmonth = month, tday = day;
            String masa = "unknown";
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);


            for(int i = 3; i >= 0; i--) {
                conjunction_found = false;
                actual_day = false;
                do {
                    cal.add(Calendar.DATE, -1);
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    double sun_longitude = getSunLongitude(year, month, day);
                    double moon_longitude = getMoonLongitude(year, month, day);
                    int tithi = getTithi(sun_longitude, moon_longitude);
                    int paksa = getPaksa(tithi);
                    double ayanamsa = getAyanamsa(year, month, day);

                    if (paksa == 1) {
                        if (tithi == 16) {
                            actual_day = true;
                            rasi[i] = getRasi(sun_longitude, ayanamsa);
                            conjunction_found = true;
                            cal.add(Calendar.DATE, -1);
                        } else if (tithi == 15) {
                            if (actual_day == false) {
                                rasi[i] = getRasi(sun_longitude, ayanamsa);
                                conjunction_found = true;
                            }
                        }
                    }
                }while(conjunction_found == false);

            }

            year = tyear; month = tmonth; day = tday;
            cal.set(year, month, day);
            for(int i = 4; i < 6; i++) {
                conjunction_found = false;
                actual_day = false;
                do {
                    cal.add(Calendar.DATE, 1);
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);
                    double sun_longitude = getSunLongitude(year, month, day);
                    double moon_longitude = getMoonLongitude(year, month, day);
                    int tithi = getTithi(sun_longitude, moon_longitude);
                    int paksa = getPaksa(tithi);
                    double ayanamsa = getAyanamsa(year, month, day);

                    if (paksa == 1) {
                        if (tithi == 15) {
                            rasi[i] = getRasi(sun_longitude, ayanamsa);
                        } else if (tithi == 16) {
                            actual_day = true;
                            rasi[i] = getRasi(sun_longitude, ayanamsa);
                            cal.add(Calendar.DATE,1);
                            conjunction_found = true;
                        }
                        else if (tithi == 17){
                            if (actual_day == false){
                                conjunction_found = true;
                            }
                        }
                    }
                }while(conjunction_found == false);

            }

        return masa;
        }

        public int getRasi(double sun_longitude, double ayanamsa){
            return (int) Math.floor(put_in_360(sun_longitude - ayanamsa) / 30.0);
        }

        public int getPaksa(int tithi){
            return (int) Math.floor(tithi / 15);
        }

        public double getAyanamsa(int year, int month, int day){
            double a = 16.90709 * year/1000 - 0.757371 * year/1000 * year/1000 - 6.92416100010001000;
            double b = (month-1 + day/30) * 1.1574074/1000;
            return a+b;

        }

        public int getTithi(double sun_longitude, double moon_longitude){
            double tithi = Math.floor(put_in_360(moon_longitude - sun_longitude - 180.0)/12.0);
            return (int) tithi;
        }

        public double getSunLongitude(int year, int month, int day){

            final double PI = 3.14159265358979323846;
            int d = 367*year - (7*(year + ((month+9)/12)))/4 + (275*month)/9 + day - 730530;
            double w = put_in_360(282.9404 + 4.70935E-5*d);
            double a = 1.000000;
            double e = 0.016709 - 1.151E-9*d;
            double M = put_in_360(356.0470 + 0.9856002585*d);

            //double oblecl = 23.4393 - 3.563E-7 * d;

            //double L = w+M;
            double E = M + (180.0/PI) * e * Math.sin(Math.toRadians(M)) * (1 + e * Math.cos(Math.toRadians(M)));

            double x = Math.cos(Math.toRadians(E)) - e;
            double y = Math.sin(Math.toRadians(E)) * Math.sqrt(1 - e * e);

            double r = Math.sqrt(x*x + y*y);
            double v = Math.toDegrees(Math.atan2(y,x));

            return put_in_360(v+w);

        }

        public double getMoonLongitude(int year, int month, int day){

            final double PI = 3.14159265358979323846;
            int d = 367*year - (7*(year + ((month+9)/12)))/4 + (275*month)/9 + day - 730530;
            double N = put_in_360(125.1228 - 0.0529538083  * d);
            double i =   5.1454;
            double w = put_in_360( 318.0634 + 0.1643573223  * d);
            double a =  60.2666;        //Mean distance
            double e = 0.054900; //(Eccentricity)
            double M = put_in_360( 115.3654 + 13.0649929509 * d);

            double E0 = M + (180.0/PI) * e * Math.sin(Math.toRadians(M)) * (1 + e * Math.cos(Math.toRadians(M)));
            double E1;
            E1=E0;

            do {
                E0 = E1;
                E1 = E0 - (E0 - (180.0 / PI) * e * Math.sin(Math.toRadians(E0)) - M) / (1 - e * Math.cos(Math.toRadians(E0)));
            }while (Math.abs(E1-E0) > 0.005);



            double x = a*(Math.cos(Math.toRadians(E1)) - e);
            double y = a* Math.sqrt(1 - e * e) * Math.sin(Math.toRadians(E1));

            double r = Math.sqrt(x*x + y*y);
            double v = put_in_360(Math.toDegrees(Math.atan2(y, x)));

            double xeclip = r * ( Math.cos(Math.toRadians(N)) * Math.cos(Math.toRadians(v + w)) - Math.sin(Math.toRadians(N)) * Math.sin(Math.toRadians(v + w)) * Math.cos(Math.toRadians(i)) );
            double yeclip = r * ( Math.sin(Math.toRadians(N)) * Math.cos(Math.toRadians(v+ w)) + Math.cos(Math.toRadians(N)) * Math.sin(Math.toRadians(v+w)) * Math.cos(Math.toRadians(i)) );
            double zeclip = r * Math.sin(Math.toRadians(v+w)) * Math.sin(Math.toRadians(i));

            double longitude = put_in_360(Math.toDegrees(Math.atan2(Math.toRadians(yeclip), Math.toRadians(xeclip))));
            double latitude = put_in_360(Math.toDegrees(Math.asin(Math.toRadians(zeclip / r))));

            double wSun = 282.9404 + 4.70935E-5*d;
            double MSun = 356.0470 + 0.9856002585*d;

            double Ls = put_in_360(wSun + MSun);
            double Ms = put_in_360(MSun);
            double Lm = put_in_360(N + w + M);
            double Mm = put_in_360(M);
            double D = put_in_360(Lm - Ls);
            double F = put_in_360(Lm - N);

            double pert1 = -1.274 * Math.sin(Math.toRadians(Mm - 2*D));
            double pert2 = +0.658 * Math.sin(Math.toRadians(2*D));
            double pert3 = -0.186 * Math.sin(Math.toRadians(Ms));
            double pert4 = -0.059 * Math.sin(Math.toRadians(2*Mm - 2*D));
            double pert5 = -0.057 * Math.sin(Math.toRadians(Mm - 2*D + Ms));
            double pert6 = +0.053 * Math.sin(Math.toRadians(Mm + 2*D));
            double pert7 = +0.046 * Math.sin(Math.toRadians(2*D - Ms));
            double pert8 = +0.041 * Math.sin(Math.toRadians(Mm - Ms));
            double pert9 = -0.035 * Math.sin(Math.toRadians(D));
            double pert10 = -0.031 * Math.sin(Math.toRadians(Mm + Ms));
            double pert11 = -0.015 * Math.sin(Math.toRadians(2 * F - 2 * D));
            double pert12 = +0.011 * Math.sin(Math.toRadians(Mm - 4 * D));

            double pert13 = -0.173 * Math.sin(Math.toRadians(F - 2*D));
            double pert14 = -0.055 * Math.sin(Math.toRadians(Mm - F - 2*D));
            double pert15 = -0.046 * Math.sin(Math.toRadians(Mm + F - 2*D));
            double pert16 = +0.033 * Math.sin(Math.toRadians(F + 2 * D));
            double pert17 = +0.017 * Math.sin(Math.toRadians(2*Mm + F));

            double pert18 = -0.58 * Math.cos(Math.toRadians(Mm - 2 * D));
            double pert19 = -0.46 * Math.cos(Math.toRadians(2 * D));

            double moon_longitude = longitude + pert1 + pert2 + pert3 + pert4 + pert5 + pert6 + pert7 + pert8 + pert9 + pert10 + pert11 + pert12;
            double moon_latitude = latitude + pert13 + pert14 + pert15 + pert16 + pert17;

            return moon_longitude;

        }

        double put_in_360(double x){
            double result = x - Math.round(x/360)*360;
            while(result < 0)
                result = result + 360;

            return result;
        }
    }

}