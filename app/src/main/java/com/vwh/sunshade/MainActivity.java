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
import java.util.List;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationProvider.LocationCallback  {
    private final static String TAG = "MainActivity";
    private LocationProvider mLocationProvider;
    private Location mLastLocation;

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

        // Locality
        TextView sunriseDisplay = (TextView) findViewById(R.id.sunriseDisplayTextView);
        sunriseDisplay.setText(sunrise);

        // Locality
        TextView sunsetDisplay = (TextView) findViewById(R.id.sunsetDisplayTextView);
        sunsetDisplay.setText(sunset);

        // Make info visible
        LinearLayout infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        infoLayout.setVisibility(View.VISIBLE);
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
