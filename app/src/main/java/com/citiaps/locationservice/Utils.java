package com.citiaps.locationservice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.citiaps.locationforegroundservice.BuildConfig;
import com.citiaps.locationforegroundservice.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Utils {

    /** *
     *  Las KEY son usadas para almacenar datos (persistentes) en SharedPreferences
     *  SharedPreferences permite almacenar datos usando <key,value>
     */
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    final static String KEY_CHECK_USERID = "user_id";
    final static String KEY_LOCATION_RESULT = "locations-waiting-for-connection";
    final static String KEY_SEND_LOCAL_LOCS = "send_saved_locations_to_server";
    final static String KEY_EXIST_LOCAL_DATA = "exist_local_data";
    public static final String url = "http://frontend.citiaps.usach.cl:80"; // "http://192.168.1.31" ;//"http://citiapsdevs.ddns.net";


    /** userID único por Dispositivo **/
    private static String userID = "null";
    private static String TAG = "Utils";
//    final static String CHANNEL_ID = "channel_01"; //variable para notificaciones
    private static Random generator = new Random();
    private static Context context;


    static void sendLocations(List<Location> locations) {
        JSONObject stepLoc=null;
        if (!locations.isEmpty()) {
            //La respuesta al LocationRequest pueden ser varias localizaciones
            for (Location location : locations) {
                stepLoc = generateJSONstep(userID, location.getLatitude(),
                        location.getLongitude(),
                        location.getTime(), location.getAccuracy(),
                        location.getAltitude(), location.getSpeed());
                HTTPAsyncTask asyncRequest = new HTTPAsyncTask(stepLoc,context);
                asyncRequest.execute(url+"/addloc/");
            }
        } else{
                // TODO avisar con un 'Toast' (mensaje flotante)  que no existe localización
        }
    }


    /** Retorna la/s  última/s localización/es */
    static String getLocationUpdatesResult(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LOCATION_UPDATES_RESULT, "");
    }

    /** Utils */
    static int randomGenerator() {
        return generator.nextInt(Integer.MAX_VALUE);
    }

    static public void setContext(Context _context) {
        context = _context;
    }

    /** Genero un JSON por cada localización recibida**/
    static JSONObject generateJSONstep(String userID, Double lat, Double lon, Long timestamp,
                                       Float accuracy, Double altitude, float speed) {
        JSONObject stepJSON = new JSONObject();
        int level = MainActivity.batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = MainActivity.batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level / (float)scale;
        try {
            stepJSON.put("userID", userID);
            stepJSON.put("lat", lat);
            stepJSON.put("lon", lon);
            stepJSON.put("timestamp", timestamp);
            stepJSON.put("accuracy", accuracy);
            stepJSON.put("altitude", altitude);
            stepJSON.put("speed", speed);
            stepJSON.put("batteryPct", batteryPct);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "---- Utils: No se pudo generar el JSON");
        }
        return stepJSON;
    }

    /** Creo un userID o lo cargo si ya existe  */
    static void checkUserID(){
        userID = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Utils.KEY_CHECK_USERID, "null");
        //crear un ID random*+timestamp *por defecto java.util.random usa nanoSystemTime
        if(userID.equals("null")){
            StringBuilder id = new StringBuilder();
            id.append(Utils.randomGenerator());
            id.append(System.currentTimeMillis());
            userID = id.toString();
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putString(Utils.KEY_CHECK_USERID, userID)
                    .apply();
            Log.i("TAG","--- MainActivity: Se genero el id:"+userID);
        }
        Log.i("TAG","--- MainActivity: Se usará el id:"+userID);
    }

    /** Almacena una localización de modo local **/
    static public void saveLocation(String response){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String locations = sp.getString(KEY_LOCATION_RESULT, "").concat(response+"#");

        //////////////----------
        //Las localizaciones guardadas no pueden superar los 1MB ~= 3000 localizaciones aprox
        String[] parts = locations.split("#");
        if(parts.length >=3000) {
            parts = Arrays.copyOfRange(parts, 1, 3000);
            StringBuilder builder = new StringBuilder();
            for (String s : parts) {
                builder.append(s + "#");
            }
            locations = builder.toString();
            //Utils.editAllLocation(locations);
        }
        ///////////////-----------
        sp.edit()
           .putString(KEY_LOCATION_RESULT, locations)
           .commit();
        Log.i(TAG,"----> Localizaciones guardadas: "+sp.getString(KEY_LOCATION_RESULT, locations));
    }
    /** Sobreescribe todas las localizaciones almacenadas**/
    static public void editAllLocation(String locations){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putString(KEY_LOCATION_RESULT, locations)
                .commit();
        Log.i(TAG,"----> Bulk de localizaciones guardadas despues de BulkInsert: "+sp.getString(KEY_LOCATION_RESULT, locations));
    }

    static public void change_SendLocalLocs(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isOn = sp.getBoolean(KEY_SEND_LOCAL_LOCS, false);
        isOn = !isOn;
        sp.edit()
                .putBoolean(KEY_SEND_LOCAL_LOCS, isOn)
                .commit();
    }

    static public boolean isRunning_SendLocalLocs(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(KEY_SEND_LOCAL_LOCS, false);
    }

    static public void checkSnackBar() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String localLocations = sp.getString(Utils.KEY_LOCATION_RESULT, null);
        if(localLocations ==null || localLocations =="") {
            Log.i(TAG,"----> localLocations ES NULL: -"+localLocations+"-");
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putBoolean(Utils.KEY_EXIST_LOCAL_DATA, false)
                    .apply();
        }
        Log.i(TAG,"----> localLocations: -"+localLocations+"-");

    }



////////////////////////////// NO SE USAN
//
//    /**
//     * Posts a notification in the notification bar when a transition is detected.
//     * If the user clicks the notification, control goes to the MainActivity.
//     */
//    static void sendNotification(Context context, String notificationDetails) {
//        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//
//        notificationIntent.putExtra("from_notification", true);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//
//        // Define the notification settings.
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                // In a real app, you may want to use a library like Volley
//                // to decode the Bitmap.
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
//                        R.mipmap.ic_launcher))
//                .setColor(Color.RED)
//                .setContentTitle("Location update")
//                .setContentText(notificationDetails)
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Android O requires a Notification Channel.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = context.getString(R.string.app_name);
//            // Create the channel for the notification
//            NotificationChannel mChannel =
//                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Set the Notification Channel for the Notification Manager.
//            mNotificationManager.createNotificationChannel(mChannel);
//
//            // Channel ID
//            builder.setChannelId(CHANNEL_ID);
//        }
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
//    }
//
//
//    static void setRequestingLocationUpdates(Context context, boolean value) {
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit()
//                .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
//                .apply();
//    }
//
//    static boolean getRequestingLocationUpdates(Context context) {
//        return PreferenceManager.getDefaultSharedPreferences(context)
//                .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false);
//    }
//
//
//    /**
//     * Returns the title for reporting about a list of {@link Location} objects.
//     *
//     * @param context The {@link Context}.
//     */
//    static String getLocationResultTitle(Context context, List<Location> locations) {
//        String numLocationsReported = context.getResources().getQuantityString(
//                R.plurals.num_locations_reported, locations.size(), locations.size());
//        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
//    }
//
//    /**
//     * Returns te text for reporting about a list of  {@link Location} objects.
//     *
//     * @param locations List of {@link Location}s.
//     */
//    private static String getLocationResultText(Context context, List<Location> locations) {
//        if (locations.isEmpty()) {
//            return context.getString(R.string.unknown_location);
//        }
//        StringBuilder sb = new StringBuilder();
//        for (Location location : locations) {
//            sb.append("(");
//            sb.append(location.getLatitude());
//            sb.append(", ");
//            sb.append(location.getLongitude());
//            sb.append(")");
//            sb.append("\n");
//        }
//        return sb.toString();
//    }
//
//    static void setLocationUpdatesResult(Context context, List<Location> locations) {
//        PreferenceManager.getDefaultSharedPreferences(context)
//                .edit()
//                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
//                        + "\n" + getLocationResultText(context, locations))
//                .apply();
//    }

}
