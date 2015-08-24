package com.vwh.sunshade;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.widget.Toast;

import com.vwh.sunshade.luckycatlabs.SunriseSunsetCalculator;

public class MainActivity extends AppCompatActivity implements LocationProvider.LocationCallback  {
    private final static String TAG = "MainActivity";
    private LocationProvider mLocationProvider;
    private Location mLastLocation;

    private String mOfficialSunrise = "", mOfficialSunset = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationProvider = new LocationProvider(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    protected void displayInfo(double latitude, double longitude, String locality, String sunrise, String sunset) {
        // Latitude
        TextView latitudeDisplay = (TextView) findViewById(R.id.latitudeDisplayTextView);
        latitudeDisplay.setText(String.valueOf(latitude));
        // Longitude
        TextView longitudeDisplay = (TextView) findViewById(R.id.longitudeDisplayTextView);
        longitudeDisplay.setText(String.valueOf(longitude));

        // Locality
        TextView localityDisplay = (TextView) findViewById(R.id.localityDisplayTextView);
        localityDisplay.setText(locality);

        updateSunriseSunset();
        sunrise = mOfficialSunrise;
        sunset = mOfficialSunset;
        // Sunrise
        TextView sunriseDisplay = (TextView) findViewById(R.id.sunriseDisplayTextView);
        sunriseDisplay.setText(sunrise);
        // Sunset
        TextView sunsetDisplay = (TextView) findViewById(R.id.sunsetDisplayTextView);
        sunsetDisplay.setText(sunset);

        // Make info visible
        LinearLayout infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        infoLayout.setVisibility(View.VISIBLE);
    }

    protected void updateSunriseSunset() {
        // Get data
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
        Date date = new Date(); // Current date

        // Process data
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
                new com.vwh.sunshade.luckycatlabs.Location(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                TimeZone.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        mOfficialSunrise = calculator.getOfficialSunriseForDate(calendar);
        mOfficialSunset = calculator.getOfficialSunsetForDate(calendar);

        /*astronomicalSunrise = calculator.getAstronomicalSunriseForDate(calendar);
        astronomicalSunset = calculator.getAstronomicalSunsetForDate(calendar);

        civilSunrise = calculator.getCivilSunriseForDate(calendar);
        civilSunset = calculator.getCivilSunsetForDate(calendar);

        nauticalSunrise = calculator.getNauticalSunriseForDate(calendar);
        nauticalSunset = calculator.getNauticalSunsetForDate(calendar);*/
    }

    protected void getCurrentLocationInfo() {
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);

        try {
            Location lastLocation = mLastLocation;

            if (lastLocation != null) {
                addressList = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation.getLongitude(), 1);
            } else {
                Log.i(null, "Location empty");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        if (addressList != null && !addressList.isEmpty()) {
            Address address = addressList.get(0);
            displayInfo(address.getLatitude(), address.getLongitude(), address.getLocality(), "", "");
        }
    }

    protected void getCustomLocationInfo() {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        String location = customLocationEditText.getText().toString();
        List<Address> addressList = null;

        if (location != null && !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch(IOException e) {
                e.printStackTrace();
            }

            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                //LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                displayInfo(address.getLatitude(), address.getLongitude(), address.getLocality(), "", "");
            }
        }
    }

    public void onGetInfoButtonClick(View view) {
        // Is the button now checked?
        RadioGroup locationRadioGroup = (RadioGroup) findViewById(R.id.location_radiogroup);
        int checkedRadioButtonId = locationRadioGroup.getCheckedRadioButtonId();

        // Check which radio button was checked
        switch(checkedRadioButtonId) {
            case R.id.current_location_radiobutton:
                getCurrentLocationInfo();
                break;
            case R.id.custom_location_radiobutton:
                getCustomLocationInfo();
                break;
        }
    }

    public void onCurrentLocationRadioButtonCLick(View view) {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        customLocationEditText.setEnabled(false);
        customLocationEditText.setVisibility(View.INVISIBLE);
    }

    public void onCustomLocationRadioButtonCLick(View view) {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        customLocationEditText.setEnabled(true);
        customLocationEditText.setText("");
        customLocationEditText.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        mLastLocation = location;
    }
}
