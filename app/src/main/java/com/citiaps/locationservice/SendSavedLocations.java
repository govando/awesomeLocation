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
        String toSend[], toSave[], paquetToSend="";
        boolean flagEmptyLocs=true;

/**TODO : Ver por qué se cortan las localizaciones. Las estoy obteniendo como el pico
 * I/PostExecute: ----> Utils.isRunning_SendLocalLocs():false
 I/HTTPAsyncTask: ----> No estaba corriendo. Se pondrá a ejecutar el hilo
 I/HTTPAsyncTask: ----> Ahora esta corrriendo!:
 I/SendSavedLocs: ----> Se enviaran las localizaciones: {"userID":"3484476461536545228698","lat":-33.4484403,"lon":-70.6452268,"timestamp":1536545236245,"accuracy":22.941,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484422,"lon":-70.6452465,"timestamp":1536545246308,"accuracy":19.94,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484322,"lon":-70.6452413,"timestamp":1536550465326,"accuracy":27.795,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484327,"lon":-70.6452466,"timestamp":1536550475693,"accuracy":29.146,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484321,"lon":-70.645277,"timestamp":1536550485789,"accuracy":29.071,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484339,"lon":-70.6452696,"timestamp":1536550495788,"accuracy":28.791,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484381,"lon":-70.6452645,"timestamp":1536550510751,"accuracy":24.025,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484408,"lon":-70.6452665,"timestamp":1536550520771,"accuracy":28.286,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484408,"lon":-70.6452665,"timestamp":1536550525858,"accuracy":92.658,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.448441,"lon":-70.645225,"timestamp":1536550531859,"accuracy":27.565,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452263,"timestamp":1536550550136,"accuracy":23.424,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484401,"lon":-70.6452266,"timestamp":1536550560440,"accuracy":27.752,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484396,"lon":-70.6452211,"timestamp":1536550571471,"accuracy":27.789,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484396,"lon":-70.6452211,"timestamp":1536550691546,"accuracy":27.789,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484389,"lon":-70.6452081,"timestamp":1536550777220,"accuracy":22.647,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452189,"timestamp":1536550802019,"accuracy":23.068,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452189,"timestamp":1536550807088,"accuracy":91.567,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484346,"lon":-70.6452272,"timestamp":1536550813160,"accuracy":27.55,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484336,"lon":-70.6452261,"timestamp":1536550823631,"accuracy":27.614,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484332,"lon":-70.6452289,"timestamp":1536550834156,"accuracy":27.744,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484333,"lon":-70.6452291,"timestamp":1536550844390,"accuracy":27.736,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484382,"lon":-70.6452242,"timestamp":1536550854512,"accuracy":27.56,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484397,"lon":-70.6452155,"timestamp":1536550864553,"accuracy":27.563,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484376,"lon":-70.64523,"timestamp":1536550874582,"accuracy":27.796,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484376,"lon":-70.64523,"timestamp":1536550997090,"accuracy":27.796,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484386,"lon":-70.6452297,"timestamp":1536551003234,"accuracy":17.891,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484386,"lon":-70.6452297,"timestamp":1536551008358,"accuracy":88.199,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484409,"lon":-70.6452738,"timestamp":1536551016088,"accuracy":29.022,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484409,"lon":-70.64527
 ---> Preparando datos...
 I/SendSavedLocs: --->Tamaño: 52 original:52 El paquete que se enviara es:{"userID":"3484476461536545228698","lat":-33.4484403,"lon":-70.6452268,"timestamp":1536545236245,"accuracy":22.941,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484422,"lon":-70.6452465,"timestamp":1536545246308,"accuracy":19.94,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484322,"lon":-70.6452413,"timestamp":1536550465326,"accuracy":27.795,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484327,"lon":-70.6452466,"timestamp":1536550475693,"accuracy":29.146,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484321,"lon":-70.645277,"timestamp":1536550485789,"accuracy":29.071,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484339,"lon":-70.6452696,"timestamp":1536550495788,"accuracy":28.791,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484381,"lon":-70.6452645,"timestamp":1536550510751,"accuracy":24.025,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484408,"lon":-70.6452665,"timestamp":1536550520771,"accuracy":28.286,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484408,"lon":-70.6452665,"timestamp":1536550525858,"accuracy":92.658,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.448441,"lon":-70.645225,"timestamp":1536550531859,"accuracy":27.565,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452263,"timestamp":1536550550136,"accuracy":23.424,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484401,"lon":-70.6452266,"timestamp":1536550560440,"accuracy":27.752,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484396,"lon":-70.6452211,"timestamp":1536550571471,"accuracy":27.789,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484396,"lon":-70.6452211,"timestamp":1536550691546,"accuracy":27.789,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484389,"lon":-70.6452081,"timestamp":1536550777220,"accuracy":22.647,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452189,"timestamp":1536550802019,"accuracy":23.068,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452189,"timestamp":1536550807088,"accuracy":91.567,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484346,"lon":-70.6452272,"timestamp":1536550813160,"accuracy":27.55,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484336,"lon":-70.6452261,"timestamp":1536550823631,"accuracy":27.614,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484332,"lon":-70.6452289,"timestamp":1536550834156,"accuracy":27.744,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484333,"lon":-70.6452291,"timestamp":1536550844390,"accuracy":27.736,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484382,"lon":-70.6452242,"timestamp":1536550854512,"accuracy":27.56,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484397,"lon":-70.6452155,"timestamp":1536550864553,"accuracy":27.563,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484376,"lon":-70.64523,"timestamp":1536550874582,"accuracy":27.796,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484376,"lon":-70.64523,"timestamp":1536550997090,"accuracy":27.796,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484386,"lon":-70.6452297,"timestamp":1536551003234,"accuracy":17.891,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484386,"lon":-70.6452297,"timestamp":1536551008358,"accuracy":88.199,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484409,"lon":-70.6452738,"timestamp":1536551016088,"accuracy":29.022,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484
 I/class com.citiaps.locationservice.MainActivity: {"userID":"3484476461536545228698","lat":-33.4484403,"lon":-70.6452268,"timestamp":1536545236245,"accuracy":22.941,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484422,"lon":-70.6452465,"timestamp":1536545246308,"accuracy":19.94,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484322,"lon":-70.6452413,"timestamp":1536550465326,"accuracy":27.795,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484327,"lon":-70.6452466,"timestamp":1536550475693,"accuracy":29.146,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484321,"lon":-70.645277,"timestamp":1536550485789,"accuracy":29.071,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484339,"lon":-70.6452696,"timestamp":1536550495788,"accuracy":28.791,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484381,"lon":-70.6452645,"timestamp":1536550510751,"accuracy":24.025,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484408,"lon":-70.6452665,"timestamp":1536550520771,"accuracy":28.286,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484408,"lon":-70.6452665,"timestamp":1536550525858,"accuracy":92.658,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.448441,"lon":-70.645225,"timestamp":1536550531859,"accuracy":27.565,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452263,"timestamp":1536550550136,"accuracy":23.424,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484401,"lon":-70.6452266,"timestamp":1536550560440,"accuracy":27.752,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484396,"lon":-70.6452211,"timestamp":1536550571471,"accuracy":27.789,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484396,"lon":-70.6452211,"timestamp":1536550691546,"accuracy":27.789,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484389,"lon":-70.6452081,"timestamp":1536550777220,"accuracy":22.647,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452189,"timestamp":1536550802019,"accuracy":23.068,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484402,"lon":-70.6452189,"timestamp":1536550807088,"accuracy":91.567,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484346,"lon":-70.6452272,"timestamp":1536550813160,"accuracy":27.55,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484336,"lon":-70.6452261,"timestamp":1536550823631,"accuracy":27.614,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484332,"lon":-70.6452289,"timestamp":1536550834156,"accuracy":27.744,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484333,"lon":-70.6452291,"timestamp":1536550844390,"accuracy":27.736,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484382,"lon":-70.6452242,"timestamp":1536550854512,"accuracy":27.56,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484397,"lon":-70.6452155,"timestamp":1536550864553,"accuracy":27.563,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484376,"lon":-70.64523,"timestamp":1536550874582,"accuracy":27.796,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484376,"lon":-70.64523,"timestamp":1536550997090,"accuracy":27.796,"altitude":0,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484386,"lon":-70.6452297,"timestamp":1536551003234,"accuracy":17.891,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484386,"lon":-70.6452297,"timestamp":1536551008358,"accuracy":88.199,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484409,"lon":-70.6452738,"timestamp":1536551016088,"accuracy":29.022,"altitude":607.9000244140625,"speed":0}#{"userID":"3484476461536545228698","lat":-33.4484409,"lon":-70.6452738,"t
 I/Utils: ----> Bulk de localizaciones guardadas despues de BulkInsert:
 */
        while(flagEmptyLocs==true){
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String locationsToSend = sp.getString(Utils.KEY_LOCATION_RESULT, null);
            Log.i(TAG,"----> Se enviaran las localizaciones: "+locationsToSend);
            if(locationsToSend !=null ) {
                Log.i(TAG, "---> Preparando datos...");
                String[] parts = locationsToSend.split("#");
                if (parts.length >= 150) { //Envío un paquete de 150 locs
                    toSend = Arrays.copyOfRange(parts, 0, 150);
                    toSave = Arrays.copyOfRange(parts, 151, parts.length);
                } else {
                    toSend = Arrays.copyOfRange(parts, 0, parts.length);
                    toSave = new String[0];
                    flagEmptyLocs=false;
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
                    if (response.equals("OK")) {  //Actualizo las locs locales
                        builder = new StringBuilder();
                        for (String s : toSave) {
                            builder.append(s + "#");
                        }
                        String LocsRestantes = builder.toString();
                        Utils.editAllLocation(LocsRestantes);
                    }
                } catch (Exception e) {
                    Log.i(TAG, "----> Error al llamar HttpPost para BulkInsert");
                }
            }
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(Utils.KEY_SEND_LOCAL_LOCS, false)
                .commit();
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

