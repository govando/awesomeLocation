package com.citiaps.locationservice;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

//@params que se pasan a la función, @valor que se pasa a función 'Progess', @ dato que se pasa al
// terminar la función 'DoInBackgorund'
class SendSavedLocations extends AsyncTask<String, Void, String> {
    private static Context context;
    private static String TAG="SendSavedLocs";

    SendSavedLocations(Context _context){
        context=_context;
    }

    @Override
    protected String doInBackground(String... urls) {
        String toSend[], paquetToSend="",toSave[];
        boolean flagEmptyLocs=true;

        while(flagEmptyLocs==true){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String locationsToSend = sp.getString(Utils.KEY_LOCATION_RESULT, null);
            Log.i(TAG,"----> Se enviaran las localizaciones: "+locationsToSend);
            if(!locationsToSend.equals(null)) {
                Log.i(TAG, "---> Preparando datos...");
                String[] parts = locationsToSend.split("#");
                if (parts.length >= 150) { //Envío un paquete de 150 locs
                    toSend = Arrays.copyOfRange(parts, 0, 150);
                    toSave = Arrays.copyOfRange(parts, 151, parts.length);
                } else {
                    toSend = Arrays.copyOfRange(parts, 0, parts.length);
                    toSave = new String[0];
                }
                //preparo y envio los datos
                StringBuilder builder = new StringBuilder();
                for (String s : toSend) {
                    builder.append(s + "#");
                }
                builder.setLength(builder.length() - 1); //remuevo el último gato #
                paquetToSend = builder.toString();
                Log.i(TAG, "--->Tamaño: " + toSend.length + " original:" + parts.length + " El paquete que se enviara es:" + paquetToSend);
                try {
                    String response = HttpPost(urls[0], paquetToSend);
                    if (response.equals("OK")) {
                        builder = new StringBuilder();
                        for (String s : toSave) {
                            builder.append(s + "#");
                        }
                        builder.setLength(builder.length() - 1); //remuevo el último gato #
                        String LocsRestantes = builder.toString();
                        //modifico las localizaciones locales
                        Utils.editAllLocation(LocsRestantes);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "----> Error al llamar HttpPost para BulkInsert");
                }

                /*sp.edit()
                        .putString(Utils.KEY_LOCATION_RESULT, "")
                        .commit();
                    */
            }
        }
        return "";
    }

    @Override
    protected void onPostExecute(String str) {

    }

    private String HttpPost(String myUrl, String locs) throws IOException, JSONException {

        URL url = new URL(myUrl);
        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setConnectTimeout(5000);
        // 3. add String content to POST request body
        setPostRequestContent(conn, locs);
        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       String locs) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(locs);
        Log.i(MainActivity.class.toString(), locs);
        writer.flush();
        writer.close();
        os.close();
    }


}

