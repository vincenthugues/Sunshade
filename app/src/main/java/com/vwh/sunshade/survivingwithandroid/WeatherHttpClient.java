package com.vwh.sunshade.survivingwithandroid;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherHttpClient {
    private static String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=";
    //private static String LAT_LON_URL = "http://api.openweathermap.org/data/2.1/find/station?";
    private static String LAT_LON_URL = "http://api.openweathermap.org/data/2.5/weather?lat=35&lon=139";
    private static String IMG_URL = "http://openweathermap.org/img/w/";

    //public String getWeatherData(String location) {
    public String getWeatherData(Location location) {
        HttpURLConnection con = null ;
        InputStream is = null;

        try {
            //con = (HttpURLConnection) (new URL(BASE_URL + location)).openConnection();
            //String urlStr = LAT_LON_URL + "lat=" + (int)location.getLatitude() + "&lon=" + (int)location.getLongitude() + "&cnt=1";
            String urlStr = LAT_LON_URL + "lat=" + (int)location.getLatitude() + "&lon=" + (int)location.getLongitude();
            //Log.v(null, "urlStr: " + urlStr);
            con = (HttpURLConnection) (new URL(urlStr)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            //Log.v(null, "Buffer: " + buffer.toString());
            return buffer.toString();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;

    }

    public byte[] getImage(String code) {
        HttpURLConnection con = null ;
        InputStream is = null;
        try {
            con = (HttpURLConnection) ( new URL(IMG_URL + code)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            is = con.getInputStream();
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ( is.read(buffer) != -1)
                baos.write(buffer);

            return baos.toByteArray();
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }

        return null;
    }
}
