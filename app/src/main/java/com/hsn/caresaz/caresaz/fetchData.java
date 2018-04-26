package com.hsn.caresaz.caresaz;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HULYA on 21.04.2018.
 */

public class fetchData extends AsyncTask<Void,Void,Void> {
    String data ="";
    String enlem = "";
    String boylam = "";
    double enlemm;
    double boylamm;
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("http://178.62.233.127/result.php");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray JA = new JSONArray(data);
            for(int i =0 ;i <JA.length(); i++){
                JSONObject JO = (JSONObject) JA.get(i);
                enlem = (String) JO.get("enlem");
                boylam = (String) JO.get("boylam");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        enlemm = Double.parseDouble(enlem);
        boylamm = Double.parseDouble(boylam);
        LatLng koordinat = new LatLng(enlemm,boylamm);
        AnaSayfa.googleMap.addMarker(new MarkerOptions().position(koordinat).title("Konum"));
        AnaSayfa.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(koordinat, 15));
    }
}