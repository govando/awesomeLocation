package com.citiaps.locationforegroundservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Utils {

    /** *
     *  Las KEY son usadas para almacenar datos (persistentes) en SharedPreferences
     *  SharedPreferences permite almacenar datos usando <key,value>
     */
    final static String KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested";
    final static String KEY_LOCATION_UPDATES_RESULT = "location-update-result";
    final static String KEY_CHECK_USERID = "user_id";

    /** userID único por Dispositivo **/
    private static String userID = "null";
    private static String TAG = "Utils";
    final static String CHANNEL_ID = "channel_01"; //variable para notificaciones
    private static Random generator = new Random();

    static void sendLocations(List<Location> locations) {
        JSONObject stepLoc=null;
        if (locations.isEmpty()) {
            // TODO avisar con un 'Toast' (mensaje flotante)  que no existe localización
        }
        //La respuesta al LocationRequest pueden ser varias localizaciones
        for (Location location : locations) {
            stepLoc = generateJSONstep(userID, Double.toString(location.getLatitude()),
                    Double.toString(location.getLongitude()),
                    Long.toString(location.getTime()), Float.toString(location.getAccuracy()),
                    Double.toString(location.getAltitude()), Float.toString(location.getSpeed()));
            /** -- Envio directo al web service --**/
            /** TODO if(online)
             *  TODO REVISAR EN http://hmkcode.com/android-send-json-data-to-server/
             */
            //new SendDeviceDetails.execute("http://localhost:8123/addLoc", stepLoc.toString());
            // TODO if(offline) Almacenar en sharedpreference carepalo
        }


        /*
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply();
        */
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    static void sendNotification(Context context, String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.putExtra("from_notification", true);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle("Location update")
                .setContentText(notificationDetails)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);

            // Channel ID
            builder.setChannelId(CHANNEL_ID);
        }

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }


    static void setRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
                .apply();
    }

    static boolean getRequestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false);
    }


    /**
     * Returns the title for reporting about a list of {@link Location} objects.
     *
     * @param context The {@link Context}.
     */
    static String getLocationResultTitle(Context context, List<Location> locations) {
        String numLocationsReported = context.getResources().getQuantityString(
                R.plurals.num_locations_reported, locations.size(), locations.size());
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(new Date());
    }

    /**
     * Returns te text for reporting about a list of  {@link Location} objects.
     *
     * @param locations List of {@link Location}s.
     */
    private static String getLocationResultText(Context context, List<Location> locations) {
        if (locations.isEmpty()) {
            return context.getString(R.string.unknown_location);
        }
        StringBuilder sb = new StringBuilder();
        for (Location location : locations) {
            sb.append("(");
            sb.append(location.getLatitude());
            sb.append(", ");
            sb.append(location.getLongitude());
            sb.append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    static void setLocationUpdatesResult(Context context, List<Location> locations) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations))
                .apply();
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

    /** JSON Utils**/
    static JSONObject generateJSONstep(String userID, String lat, String lon, String timestamp,
                                       String accuracy, String altitude, String speed) {
        JSONObject stepJSON = new JSONObject();
        try {
            // TODO
            stepJSON.put("index", "1");
            stepJSON.put("userID", userID);
            stepJSON.put("lat", lat);
            stepJSON.put("lon", lon);
            stepJSON.put("timestamp", timestamp);
            stepJSON.put("accuracy", accuracy);
            stepJSON.put("altitude", altitude);
            stepJSON.put("speed", speed);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "---- Utils: No se pudo generar el JSON");
        }
        return stepJSON;
    }

    /** Creo un userID o lo cargo si ya existe  */
    static void checkUserID(Context context){
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
            Log.i("TAG","--- MainActiviti: Se genero el id:"+userID);
        }
        Log.i("TAG","--- MainActiviti: Se usará el id:"+userID);
    }

}
