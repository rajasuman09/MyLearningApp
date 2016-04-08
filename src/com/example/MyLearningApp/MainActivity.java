package com.example.MyLearningApp;


import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.w3c.dom.Text;
import uk.me.jstott.sun.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import uk.me.jstott.coordconv.LatitudeLongitude;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      //  PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Acquire a reference to the system Location Manager


        DBHelper myDbHelper;
        myDbHelper = new DBHelper(this);
        myDbHelper.initialize();

        AlarmReceiver ar = new AlarmReceiver(this);
        Calendar alarm_time = Calendar.getInstance();
        alarm_time.setTimeInMillis(System.currentTimeMillis());

        alarm_time.set(Calendar.HOUR_OF_DAY, 20);
        alarm_time.set(Calendar.MINUTE, 00);
        alarm_time.set(Calendar.SECOND, 00);

        Intent intentAlarm = new Intent(ar.mContext, AlarmReceiver.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,alarm_time.getTimeInMillis(),24*60*60*1000, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        ArrayList array_list;
        int i = 0, gaurabda;
        String paksa_name, tithi_name, masa_name, naksatra_name;

        double local_longitude = myDbHelper.getLocationLongitude(); //  78.48;
        double local_latitude = myDbHelper.getLocationLatitude(); // 17.37;
        double local_offset = 5.5;

        LatitudeLongitude ll2 = new LatitudeLongitude(local_latitude, local_longitude);
        TimeZone timeZone = TimeZone.getTimeZone("GMT+5:30");
        final Calendar c = Calendar.getInstance(timeZone);
        boolean dst = false;
        Time sunrise = Sun.sunriseTime(c, ll2, timeZone, dst);

        int bm_minutes = sunrise.getHours()*60 + sunrise.getMinutes() - 96;
        int bm_hours = (int) (bm_minutes / 60.0);
        bm_minutes = (int) (bm_minutes - (bm_hours * 60));
        Time brahma_muhurta_start = new Time(bm_hours, bm_minutes, sunrise.getSeconds());

        bm_minutes = sunrise.getHours()*60 + sunrise.getMinutes() - 48;
        bm_hours = (int) (bm_minutes / 60.0);
        bm_minutes = (int) (bm_minutes - (bm_hours * 60));
        Time brahma_muhurta_end = new Time(bm_hours, bm_minutes, sunrise.getSeconds());


        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        double sun_longitude = getSunLongitude(year, month + 1, day);
        double moon_longitude = getMoonLongitude(year, month + 1, day);
        int tithi = getTithi(sun_longitude, moon_longitude);

        int paksa = getPaksa(tithi);
        paksa_name = getPaksaName(paksa);

        masa_name = getMasa(year, month, day, paksa);
        tithi_name = getTithiName(tithi);
        double ayanamsa = getAyanamsa(year, month + 1, day);
        int naksatra = (int) Math.floor(put_in_360(moon_longitude - ayanamsa) * (3 / 40.0));
        naksatra_name = getNaksatraName(naksatra);
        gaurabda = getGaurabdaYear(year, masa_name, month);

        TextView event_heading, event_date, tv_tithi, tv_paksa, tv_masa, tv_sunrise, tv_naksatra, tv_gaurabda, tv_bm_start, tv_bm_end;

        event_heading = (TextView)findViewById(R.id.textView1);
        event_date = (TextView)findViewById(R.id.textView3);
        Button view_all_festivals = (Button)findViewById(R.id.ViewAllEvents);
        Button date_check = (Button)findViewById(R.id.CheckDate);
        tv_tithi = (TextView)findViewById(R.id.tithi);
        tv_paksa = (TextView)findViewById(R.id.paksa);
        tv_masa = (TextView)findViewById(R.id.masa);
        tv_sunrise = (TextView)findViewById(R.id.sunrise);
        tv_naksatra = (TextView)findViewById(R.id.naksatra);
        tv_gaurabda = (TextView)findViewById(R.id.gaurabda);
        tv_bm_start = (TextView)findViewById(R.id.brahma_muhurta_start);
        tv_bm_end = (TextView)findViewById(R.id.brahma_muhurta_end);

        tv_tithi.setText("Tithi: "+ tithi_name);
        tv_paksa.setText("Paksa: "+ paksa_name);
        tv_masa.setText("Masa: "+ masa_name);
        tv_sunrise.setText("Sunrise: " + sunrise);
        tv_naksatra.setText("Naksatra: " + naksatra_name);
        tv_bm_start.setText("Brahma Muhurta: " + brahma_muhurta_start);
        tv_bm_end.setText("  to  " + brahma_muhurta_end);
        tv_gaurabda.setText("Gaurabda: " + gaurabda);

        array_list = myDbHelper.getTodaysEvents(tithi_name, paksa_name, masa_name);

        while(array_list.size()==0) {
            c.add(Calendar.DATE, 1);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            sun_longitude = getSunLongitude(year, month + 1, day);
            moon_longitude = getMoonLongitude(year, month + 1, day);
            tithi = getTithi(sun_longitude, moon_longitude);

            paksa = getPaksa(tithi);
            paksa_name = getPaksaName(paksa);

            masa_name = getMasa(year, month, day, paksa);
            tithi_name = getTithiName(tithi);

            array_list = myDbHelper.getTodaysEvents(tithi_name, paksa_name, masa_name);

            i++;
        };

        if(i>0){
            event_heading.setText("Upcoming Events");
        }
        Date date = c.getTime();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E, MMMM d, yyyy");
        event_date.setText(dateFormatter.format(date));

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, array_list);

        ListView listView = (ListView) findViewById(R.id.today_or_upcoming_events_list);
        listView.setAdapter(adapter);

        //Set listener on button in main activity
        view_all_festivals.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DisplayFestivals.class);
                startActivity(i);
            }
        });

        //Set listener on button in main activity
        date_check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DateCheck.class);
                startActivity(i);
            }
        });

    }

    private int getGaurabdaYear(int year, String masa_name, int month){
        int GYear = year - 1486;

        if(masa_name == "Kesava" || masa_name == "Narayana" || masa_name == "Madhava" || masa_name == "Govinda"){
            if (month >= 0 && month <= 5){
                GYear--;
            }
        }
        return GYear;
    }

    private String getNaksatraName(int naksatra){
        String naksatra_name = "";
        switch (naksatra){
            case 0:
                naksatra_name = "Asvini";
                break;
            case 1:
                naksatra_name = "Bharani";
                break;
            case 2:
                naksatra_name = "Krittika";
                break;
            case 3:
                naksatra_name = "Rohini";
                break;
            case 4:
                naksatra_name = "Mrigashirsha";
                break;
            case 5:
                naksatra_name = "Ardra";
                break;
            case 6:
                naksatra_name = "Punarvasu";
                break;
            case 7:
                naksatra_name = "Pushya";
                break;
            case 8:
                naksatra_name = "Ashlesha";
                break;
            case 9:
                naksatra_name = "Magha";
                break;
            case 10:
                naksatra_name = "Purva Phalguni";
                break;
            case 11:
                naksatra_name = "Uttara Phalguni";
                break;
            case 12:
                naksatra_name = "Hasta";
                break;
            case 13:
                naksatra_name = "Chitra";
                break;
            case 14:
                naksatra_name = "Swati";
                break;
            case 15:
                naksatra_name = "Vishakha";
                break;
            case 16:
                naksatra_name = "Anuradha";
                break;
            case 17:
                naksatra_name = "Jyeshta";
                break;
            case 18:
                naksatra_name = "Mula";
                break;
            case 19:
                naksatra_name = "Purva Ashada";
                break;
            case 20:
                naksatra_name = "Uttara Ashada";
                break;
            case 21:
                naksatra_name = "Shravana";
                break;
            case 22:
                naksatra_name = "Dhanishta";
                break;
            case 23:
                naksatra_name = "Shatabhisha";
                break;
            case 24:
                naksatra_name = "Purva Bhadrapada";
                break;
            case 25:
                naksatra_name = "Uttara Bhadrapada";
                break;
            case 26:
                naksatra_name = "Revati";
                break;
        }
        return naksatra_name;
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
            //startActivity(new Intent(this, SettingsActivity.class));


          //  getSupportFragmentManager().beginTransaction()
          //          .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
          //          .commit();
           getFragmentManager().beginTransaction()
                   .replace(android.R.id.content, new SettingsActivity(this))
                   .addToBackStack(null)
                   .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

  /*  public static class MainSettingsFragment extends PreferenceFragment
                                            implements SharedPreferences.OnSharedPreferenceChangeListener,
                                            GoogleApiClient.ConnectionCallbacks,
                                            GoogleApiClient.OnConnectionFailedListener,
            com.google.android.gms.location.LocationListener{

       // protected Context context;
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;
        private Location mLastLocation;
        LocationRequest mLocationRequest;

        // Google client to interact with Google API
        private GoogleApiClient mGoogleApiClient;
        String KEY_PREF_LOCATION_SWITCH = "pref_getlocation";
        String KEY_PREF_PRESENT_LOCATION = "pref_location";

        public MainSettingsFragment(){
            //
        }

        public MainSettingsFragment(Context context){
            this.context = context.getApplicationContext();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            sharedpreferences = getActivity().getSharedPreferences("preferences", MODE_PRIVATE);
            //PreferenceManager.setDefaultValues();

            editor = sharedpreferences.edit();

            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getView().setBackgroundColor(Color.WHITE);
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStart(){
            super.onStart();
            Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
            mGoogleApiClient.connect();
            Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onStop(){
            mGoogleApiClient.disconnect();
            super.onStop();
            Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
        }



        @Override
        public void onConnected(Bundle connectionHint) {

            Log.d("MYTAG", "GOOGLE API CONNECTED!");
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation == null) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }

          //  mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                editor.putString(KEY_PREF_PRESENT_LOCATION, mLastLocation.toString());
                editor.commit();
                //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            }
        }

        @Override
        public void onLocationChanged(Location location) {

            mLastLocation = location;
            if (mLastLocation != null) {
                editor.putString(KEY_PREF_PRESENT_LOCATION, mLastLocation.toString());
                editor.commit();

            }
        }

        @Override
        public void onConnectionFailed(ConnectionResult result) {
          //  Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
          //          + result.getErrorCode());
        }

        @Override
        public void onConnectionSuspended(int arg0) {
            mGoogleApiClient.connect();
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {
            if (key.equals(KEY_PREF_LOCATION_SWITCH)) {
                if (sharedPreferences.getBoolean(key,false) == true) {
                    Preference connectionPref = findPreference(KEY_PREF_PRESENT_LOCATION);
                   mGoogleApiClient.connect();
               //     mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
               //             mGoogleApiClient);
              //      if (mLastLocation != null) {
              //          editor.putString(KEY_PREF_PRESENT_LOCATION, mLastLocation.toString());
              //          editor.commit();
                //    }
                    connectionPref.setSummary(sharedPreferences.getString(KEY_PREF_PRESENT_LOCATION, "Not Available"));
                    mGoogleApiClient.disconnect();
                }
                Preference pref_getlocation = findPreference(KEY_PREF_LOCATION_SWITCH);
                pref_getlocation.setEnabled(sharedPreferences.getBoolean(key, false));
            }

        }

    }*/

    public String getMasa(int year, int month, int day, int cpaksa) {

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
                    double sun_longitude = getSunLongitude(year, month+1, day);
                    double moon_longitude = getMoonLongitude(year, month+1, day);
                    int tithi = getTithi(sun_longitude, moon_longitude);
                    int paksa = getPaksa(tithi);
                    double ayanamsa = getAyanamsa(year, month+1, day);

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
                    double sun_longitude = getSunLongitude(year, month+1, day);
                    double moon_longitude = getMoonLongitude(year, month+1, day);
                    int tithi = getTithi(sun_longitude, moon_longitude);
                    int paksa = getPaksa(tithi);
                    double ayanamsa = getAyanamsa(year, month+1, day);

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

            boolean is_rasi_adjusted = false;

            for(int i = 1; i<=5; i++){
                if ((rasi[i-1]+2)%12 == rasi[i]){
                    for(int j=i;j<5;j++){
                        if(rasi[j+1] == rasi[j]){
                            for(int k=i;k<=j; k++){
                                rasi[k] = rasi[k] - 1;
                            }
                            is_rasi_adjusted = true;
                        }
                    }
                    if (is_rasi_adjusted == false){
                        for(int k=i;k<=5;k++){
                            rasi[k] = rasi[k] - 1;
                        }
                    }
                }
            }

            if(rasi[3] == rasi[4]){
                masa = getMasaName(rasi[3]);
            }
            else{
                if(cpaksa==0){
                    masa = getMasaName(rasi[4]);
                }
                else{
                    masa = getMasaName(rasi[3]);
                }
            }
        return masa;
        }

        public String getMasaName(int rasi){
            String masa = "";
            switch (rasi){
                case 0:
                    masa = "Madhusudana";
                    break;
                case 1:
                    masa = "Trivikrama";
                    break;
                case 2:
                    masa = "Vamana";
                    break;
                case 3:
                    masa = "Sridhara";
                    break;
                case 4:
                    masa = "Hrsikesa";
                    break;
                case 5:
                    masa = "Padmanabha";
                    break;
                case 6:
                    masa = "Damodara";
                    break;
                case 7:
                    masa = "Kesava";
                    break;
                case 8:
                    masa = "Narayana";
                    break;
                case 9:
                    masa = "Madhava";
                    break;
                case 10:
                    masa = "Govinda";
                    break;
                case 11:
                    masa = "Visnu";
                    break;
            }
            return masa;
        }

    public String getTithiName(int tithi){
        String tithi_name = "";
        switch (tithi){
            case 0:
            case 15:
                tithi_name = "Pratipat";
                break;
            case 1:
            case 16:
                tithi_name = "Dvitiya";
                break;
            case 2:
            case 17:
                tithi_name = "Tritiya";
                break;
            case 3:
            case 18:
                tithi_name = "Caturthi";
                break;
            case 4:
            case 19:
                tithi_name = "Pancami";
                break;
            case 5:
            case 20:
                tithi_name = "Sasti";
                break;
            case 6:
            case 21:
                tithi_name = "Saptami";
                break;
            case 7:
            case 22:
                tithi_name = "Astami";
                break;
            case 8:
            case 23:
                tithi_name = "Navami";
                break;
            case 9:
            case 24:
                tithi_name = "Dasami";
                break;
            case 10:
            case 25:
                tithi_name = "Ekadasi";
                break;
            case 11:
            case 26:
                tithi_name = "Dvadasi";
                break;
            case 12:
            case 27:
                tithi_name = "Trayodasi";
                break;
            case 13:
            case 28:
                tithi_name = "Caturdasi";
                break;
            case 14:
                tithi_name = "Amavasya";
                break;
            case 29:
                tithi_name = "Purnima";
                break;
        }
        return tithi_name;
    }

    public int getRasi(double sun_longitude, double ayanamsa){
            return (int) Math.floor(put_in_360(sun_longitude - ayanamsa) / 30.0);
    }

    public int getPaksa(int tithi) {
        return (int) Math.floor(tithi / 15);
    }

    public String getPaksaName(int paksa) {

        String paksa_name;

        if (paksa == 0) {
            paksa_name = "Krishna";
        } else {
            paksa_name = "Gaura";
        }

        return paksa_name;
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

            double r = Math.sqrt(x * x + y * y);
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
            double pert17 = +0.017 * Math.sin(Math.toRadians(2 * Mm + F));

            double pert18 = -0.58 * Math.cos(Math.toRadians(Mm - 2 * D));
            double pert19 = -0.46 * Math.cos(Math.toRadians(2 * D));

            double moon_longitude = put_in_360(longitude + pert1 + pert2 + pert3 + pert4 + pert5 + pert6 + pert7 + pert8 + pert9 + pert10 + pert11 + pert12);
            //double moon_latitude = latitude + pert13 + pert14 + pert15 + pert16 + pert17;

            return moon_longitude;

        }

    double put_in_360(double x){
            double result = x - Math.round(x/360)*360;
            while(result < 0)
                result = result + 360;

            return result;
        }

}

