package com.citiaps.locationservice;

import android.content.Context;
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

class HTTPAsyncTask extends AsyncTask<String, Void, String> {
    private JSONObject stepLoc;
    private static Context context;
    private String TAG="HTTPAsyncTask";

    public HTTPAsyncTask(JSONObject stepLoc, Context context){
        this.stepLoc = stepLoc;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        String response;
        try {
            Log.i("doInBackground","----> doInBackground! Deberia llamar a HttpPost");
            response = HttpPost(urls[0]);
        } catch (JSONException e) {
            e.printStackTrace();
            return "Error!";
        }
        catch (IOException e) {
            Utils.saveLocation(stepLoc.toString());
            return "No se ha podido realizar la conexión. La localización se almacenará en disco";
        }
        return response;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        if(result.equals("OK")) {
            Log.i("PostExecute", "----> Response: OK " + result);
            Log.i("PostExecute", "----> Utils.isRunning_SendLocalLocs():" + Utils.isRunning_SendLocalLocs());
            //En este caso existe conexión a Internet. Genero un solo thread que envíe las Locs guardadas si es que existen
            if(Utils.isRunning_SendLocalLocs()==false){
                Log.i(TAG,"----> No estaba corriendo. Se pondrá a ejecutar el hilo");
                Utils.change_SendLocalLocs();
                Log.i(TAG,"----> Ahora esta corrriendo!:");
                SendSavedLocations sendSavedLocs = new SendSavedLocations(context);
                sendSavedLocs.execute("http://citiapsdevs.ddns.net:3000/addbulkloc");
            }
        } else{
            Log.i("PostExecute", "----> Response: MAL: Se almacena en celular "+result);
        }
    }

    private String HttpPost(String myUrl) throws IOException, JSONException {
        //Log.i("HttpPost2","El json a enviar es: "+stepLoc.toString());

        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setConnectTimeout(5000);
        // 2. build JSON object
        JSONObject jsonObject = stepLoc;

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(MainActivity.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

}

