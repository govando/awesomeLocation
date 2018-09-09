package com.citiaps.locationservice;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.citiaps.locationforegroundservice.BuildConfig;
import com.citiaps.locationforegroundservice.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    /** Parametros del Request para la API de localización **/
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL = 5000; // Cada 5 segundos.
    private static final long FASTEST_UPDATE_INTERVAL = 5000; // Cada 3 segundos.
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL*2 ; // Cada 10 segundos.

    /** Acceso a la API de localización */
    private FusedLocationProviderClient mFusedLocationClient;

    // Acceso a elementos del Layout.
    private Button mRequestUpdatesButton;
    private Button mRemoveUpdatesButton;
    //private TextView mOnOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mRequestUpdatesButton =  findViewById(R.id.mRequestUpdatesButton);
        //mRemoveUpdatesButton =  findViewById(R.id.mRemoveUpdatesButton);
        //mOnOff = (TextView) findViewById(R.id.textView_onOff);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit()
                .putBoolean(Utils.KEY_SEND_LOCAL_LOCS, false)
                .apply();
        // Revisa si existen permisos y los solicita si está denegado
        if (!checkPermissions()) {
            requestPermissions();
            Log.i("MainActvity","----> Permiso concedido");
        }

        //Genera o carga un userID único
        Utils.setContext(this);
        Utils.checkUserID();
        //Acceso a la API que provee localización (Fused Location Provider)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Log.i("activityMain", " -----> Llamando createLocationRequest()");
        createLocationRequest();
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateButtonsState(Utils.getRequestingLocationUpdates(this));
        //mLocationUpdatesResultView.setText(Utils.getLocationUpdatesResult(this));
    }

    @Override
    protected void onStop() {
      //  PreferenceManager.getDefaultSharedPreferences(this)
      //          .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }


    /**  Utils  **/
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Pidiendo Permisos...");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /** Genera la estructura del Request **/
    private void createLocationRequest() {
        Log.i("activityMain", "Creando Location Request");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        if (s.equals(Utils.KEY_LOCATION_UPDATES_RESULT)) {
//            //mLocationUpdatesResultView.setText(Utils.getLocationUpdatesResult(this));
//        } else if (s.equals(Utils.KEY_LOCATION_UPDATES_REQUESTED)) {
//            updateButtonsState(Utils.getRequestingLocationUpdates(this));
//        }
//    }

//    /** Maneja los botones de Requests Handles the Request Updates button and requests start of location updates. */
//    public void requestLocationUpdates(View view) {
//        try {
//            Log.i(TAG, "Starting location updates");
//            //--- Controla que el botón 'Activar' este deshabilitado
//            Utils.setRequestingLocationUpdates(this, true);
//            /** ---  Comienza a trabajar la API --- */
//            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
//        } catch (SecurityException e) {
//            Utils.setRequestingLocationUpdates(this, false);
//            e.printStackTrace();
//        }
//    }

      private PendingIntent getPendingIntent() {
          Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
          intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
          return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      }

    /** Mantiene un solo botón disponible */
    private void updateButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestUpdatesButton.setEnabled(false);
            mRemoveUpdatesButton.setEnabled(true);
        } else {
            mRequestUpdatesButton.setEnabled(true);
            mRemoveUpdatesButton.setEnabled(false);
        }
    }



}
