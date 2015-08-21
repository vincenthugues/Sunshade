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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(null, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(null, "Connection failed");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    */

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
            if (mLastLocation != null) {
                addressList = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
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
}
