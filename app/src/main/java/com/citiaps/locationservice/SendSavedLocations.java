package com.citiaps.locationservice;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

//@params que se pasan a la función, @valor que se pasa a función 'Progess', @ dato que se pasa al
// terminar la función 'DoInBackgorund'
class SendSavedLocations extends AsyncTask<String, Void, String> {
    private static Context context;


    SendSavedLocations(Context _context){
        context=_context;
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String locationsToSend = sp.getString(Utils.KEY_LOCATION_RESULT, null);
        if(!locationsToSend.equals(null)){
            String[] parts = locationsToSend.split(",");
            //

        }

        sp.edit()
                .putString(Utils.KEY_LOCATION_RESULT, "")
                .commit();

        ;
        return "";
    }

    @Override
    protected void onPostExecute(String str) {

    }


}

