package com.example.MyLearningApp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.prefs.Preferences;

/**
 * Created by Raja.Chirala on 14/03/2016.
 */

public class SettingsActivity extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener,
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            com.google.android.gms.location.LocationListener{

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    protected Context context;
  //  SharedPreferences prefs;

    public SettingsActivity (Context context){
        this.context = context.getApplicationContext();
    }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add 'general' preferences, defined in the XML file
            addPreferencesFromResource(R.xml.preferences);
          //  prefs = PreferenceManager.getDefaultSharedPreferences(context);
          //  PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

            // Build Google API client
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }


        }

        /**
         * Attaches a listener so the summary is always updated with the preference value.
         * Also fires the listener once, to initialize the summary (so it shows up before the value
         * is changed.)
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPref = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            String location = sharedPref.getString("pref_location", "Mayapura");
            if(preference.getKey()== "pref_location") {
                preference.setSummary(location);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list (since they have separate labels/values).
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[prefIndex]);
                }
            }
            else if (preference instanceof SwitchPreference){
                // For other preferences, set the summary to the value's simple string representation.
                ((SwitchPreference) preference).setChecked((boolean) value);

                if((boolean)value){
                    mGoogleApiClient.connect();
                    Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
                }
                else{
                    if(mGoogleApiClient.isConnected()){
                        mGoogleApiClient.disconnect();
                        Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
                    }
                }
            }
            return true;
        }

    @Override
    public void onStart(){
        super.onStart();
      //  mGoogleApiClient.connect();
      //  Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
        getView().setBackgroundColor(Color.WHITE);
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference("pref_getlocation"));
        bindPreferenceSummaryToValue(findPreference("pref_location"));
    }

    @Override
    public void onStop(){
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        Log.e("Connected?", String.valueOf(mGoogleApiClient.isConnected()));
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d("MYTAG", "GOOGLE API CONNECTED!");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(60*1000);
            mLocationRequest.setFastestInterval(5*1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else{
           // SharedPreferences prefs =  getActivity().getSharedPreferences("preferences.xml", Context.MODE_PRIVATE);
            Preference p = findPreference("pref_location");
            p.setSummary(mLastLocation.toString());
            SharedPreferences prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pref_location", mLastLocation.toString());
            editor.commit();

        }
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mLastLocation != null) {
            //SharedPreferences prefs =  getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            SharedPreferences prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("pref_location", mLastLocation.toString());
            editor.commit();
            Preference p = findPreference("pref_location");
            p.setSummary(mLastLocation.toString());

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //  Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
        //          + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int arg0) {
      //  mGoogleApiClient.connect();
    }

}
