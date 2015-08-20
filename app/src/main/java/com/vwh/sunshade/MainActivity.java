package com.vwh.sunshade;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onCustomLocationEdittextClick(View view) {
        /*
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_pirates:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_ninjas:
                if (checked)
                    // Ninjas rule
                    break;
        }
        */

        Log.i(null, "Test");

        EditText customLocationEditText = (EditText) view;
        customLocationEditText.setFocusable(true);
        customLocationEditText.setText("");
        customLocationEditText.setActivated(true);

        RadioButton customLocationRadioButton = (RadioButton) findViewById(R.id.custom_location_radiobutton);
        customLocationRadioButton.setChecked(true);
    }

    public void onGetInfoButtonClick(View view) {
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
                Log.i(null, address.getLocality());
            }
        }

        LinearLayout sunriseLayout = (LinearLayout) findViewById(R.id.sunriseLayout);
        sunriseLayout.setVisibility(View.VISIBLE);

        LinearLayout sunsetLayout = (LinearLayout) findViewById(R.id.sunsetLayout);
        sunsetLayout.setVisibility(View.VISIBLE);
    }

    public void onCurrentLocationRadioButtonCLick(View view) {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        customLocationEditText.setEnabled(false);
        customLocationEditText.setVisibility(View.INVISIBLE);
    }

    public void onCustomLocationRadioButtonCLick(View view) {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        customLocationEditText.setEnabled(true);
        customLocationEditText.setVisibility(View.VISIBLE);
    }
}
