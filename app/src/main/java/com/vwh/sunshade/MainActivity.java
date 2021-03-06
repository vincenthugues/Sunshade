package com.vwh.sunshade;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.vwh.sunshade.survivingwithandroid.JSONWeatherParser;
import com.vwh.sunshade.survivingwithandroid.Weather;
import com.vwh.sunshade.survivingwithandroid.WeatherHttpClient;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements LocationProvider.LocationCallback  {
    private final static String TAG = "MainActivity";

    final Calendar mCalendar = Calendar.getInstance();
    private LocationProvider mLocationProvider;
    private Location mLastLocation;
    private String mOfficialSunrise = "", mOfficialSunset = "";
    private int mYear, mMonth, mDay;

    private TextView mTVDisplayDate;
    private Button mBtnChangeDate;

    static final int DATE_DIALOG_ID = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationProvider = new LocationProvider(this, this);

        setCurrentDateOnView();
        addDateListener();
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

    protected void displayInfo(Address address) {
        double latitude = address.getLatitude();
        double longitude = address.getLongitude();

        String locality = address.getLocality();
        // Sometimes the locality is null, in those cases the address has 2 lines
        // so the city is line 0 (instead of: [0] street, [1] city, [2] country code)
        if (locality == null || locality.isEmpty())
            locality = address.getAddressLine(0);

        updateSunriseSunset();
        String sunrise = mOfficialSunrise;
        String sunset = mOfficialSunset;

        LinearLayout infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        infoLayout.setVisibility(View.INVISIBLE);

        // Weather
        TextView weatherDisplay = (TextView) findViewById(R.id.weatherDisplayTextView);
        // Display a temporary message until we get the weather data
        weatherDisplay.setText("Fetching data...");
        JSONWeatherTask task = new JSONWeatherTask();
        //task.execute(locality + "," + address.getCountryCode());
        com.vwh.sunshade.survivingwithandroid.Location location = new com.vwh.sunshade.survivingwithandroid.Location();
        location.setLatitude((float) latitude);
        location.setLongitude((float) longitude);
        task.execute(location);

        // Latitude
        TextView latitudeDisplay = (TextView) findViewById(R.id.latitudeDisplayTextView);
        latitudeDisplay.setText(String.valueOf(latitude));
        // Longitude
        TextView longitudeDisplay = (TextView) findViewById(R.id.longitudeDisplayTextView);
        longitudeDisplay.setText(String.valueOf(longitude));

        // Locality
        TextView localityDisplay = (TextView) findViewById(R.id.localityDisplayTextView);
        localityDisplay.setText(locality);

        // Sunrise
        TextView sunriseDisplay = (TextView) findViewById(R.id.sunriseDisplayTextView);
        sunriseDisplay.setText(sunrise);

        // Sunset
        TextView sunsetDisplay = (TextView) findViewById(R.id.sunsetDisplayTextView);
        sunsetDisplay.setText(sunset);

        // Make info visible
        infoLayout.setVisibility(View.VISIBLE);
    }

    private class JSONWeatherTask extends AsyncTask<com.vwh.sunshade.survivingwithandroid.Location, Void, Weather> {

        @Override
        protected Weather doInBackground(com.vwh.sunshade.survivingwithandroid.Location... params) {
            Weather weather = new Weather();
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Retrieve the icon
                //weather.iconData = ((new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            /*
            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }
            */

            /*
            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "�C");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
            windDeg.setText("" + weather.wind.getDeg() + "�");
            */

            TextView weatherDisplay = (TextView) findViewById(R.id.weatherDisplayTextView);
            weatherDisplay.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
        }
    }

    protected void updateSunriseSunset() {
        // Get data
        //SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy");
        //Date date = new Date(); // Current date

        // Process data
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
                new com.vwh.sunshade.luckycatlabs.Location(mLastLocation.getLatitude(), mLastLocation.getLongitude()),
                TimeZone.getDefault());

        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(date);

        //Log.v(TAG, mCalendar.toString());

        mOfficialSunrise = calculator.getOfficialSunriseForDate(mCalendar);
        mOfficialSunset = calculator.getOfficialSunsetForDate(mCalendar);

        /*astronomicalSunrise = calculator.getAstronomicalSunriseForDate(mCalendar);
        astronomicalSunset = calculator.getAstronomicalSunsetForDate(mCalendar);

        civilSunrise = calculator.getCivilSunriseForDate(mCalendar);
        civilSunset = calculator.getCivilSunsetForDate(mCalendar);

        nauticalSunrise = calculator.getNauticalSunriseForDate(mCalendar);
        nauticalSunset = calculator.getNauticalSunsetForDate(mCalendar);*/
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

        if (addressList != null && !addressList.isEmpty())
            displayInfo(addressList.get(0));
    }

    protected void getCustomLocationInfo() {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        String location = customLocationEditText.getText().toString();
        List<Address> addressList = null;

        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch(IOException e) {
                e.printStackTrace();
            }

            if (addressList != null && !addressList.isEmpty())
                displayInfo(addressList.get(0));
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

    public void onCurrentLocationRadioButtonClick(View view) {
        EditText customLocationEditText = (EditText) findViewById(R.id.custom_location_edittext);
        customLocationEditText.setEnabled(false);
        customLocationEditText.setVisibility(View.GONE);
    }

    public void onCustomLocationRadioButtonClick(View view) {
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

    // Display current date
    public void setCurrentDateOnView() {
        mTVDisplayDate = (TextView) findViewById(R.id.tvDate);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        // Set current date (month is 0 based)
        mTVDisplayDate.setText((mMonth + 1) + "-" + mDay + "-" + mYear + " ");
    }

    public void addDateListener() {
        mBtnChangeDate = (Button) findViewById(R.id.btnChangeDate);

        mBtnChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }

        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // Set date picker as current date
                return new DatePickerDialog(this, datePickerListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            mYear = selectedYear;
            mMonth = selectedMonth;
            mDay = selectedDay;

            // set selected date into textview
            mTVDisplayDate.setText((mMonth + 1) + "-" + mDay + "-" + mYear + " ");
        }
    };
}
