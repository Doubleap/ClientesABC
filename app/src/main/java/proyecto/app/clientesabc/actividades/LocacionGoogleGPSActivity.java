package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.text.DecimalFormat;

import es.dmoral.toasty.Toasty;

public class LocacionGoogleGPSActivity {

    private static final String TAG = "LocacionGoogleGPSActivity";
    private MaskedEditText mLatitudeTextView;
    private MaskedEditText mLongitudeTextView;
    private static final long UPDATE_INTERVAL = 10 * 1000;  // 10 secs
    private static final long FASTEST_INTERVAL = 5000;      // 5 secs
    private AlertDialog mAlertDialog;
    private Context context;
    private Activity activity;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocacionGPSActivity.LocationListenerCallback callback;

    // Constructor for use with MaskedEditText views
    LocacionGoogleGPSActivity(Context c, Activity a, MaskedEditText lat, MaskedEditText longi) {
        context = c;
        activity = a;
        mLatitudeTextView = lat;
        mLongitudeTextView = longi;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mAlertDialog = new AlertDialog.Builder(this.context).create();
    }

    // Constructor for location updates
    public LocacionGoogleGPSActivity(Context context, LocacionGPSActivity.LocationListenerCallback callback) {
        this.context = context;
        this.callback = callback;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        mAlertDialog = new AlertDialog.Builder(this.context).create();
        checkLocation();

        // Handle location permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }

        // Request last known location and set up location updates
        requestLocationUpdates();
    }

    // Method to start location updates
    protected void startLocationUpdates() {
        checkLocation();
        // Set up location request with FusedLocationProviderClient
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        String latitud = String.valueOf(Double.parseDouble(new DecimalFormat("##.############").format(location.getLatitude())));
                        String longitud = String.valueOf(Double.parseDouble(new DecimalFormat("##.############").format(location.getLongitude())));
                        String msg = "Coordenadas Actualizadas: " + latitud + "," + longitud;
                        mLatitudeTextView.setText(latitud);
                        mLongitudeTextView.setText(longitud);
                        Toasty.success(context, msg, Toasty.LENGTH_SHORT).show();

                        if (!mLatitudeTextView.getText().equals("0") && !mLongitudeTextView.getText().equals("0")) {
                            fusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        // Request location updates
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void requestLocationUpdates() {
        // Ensure permissions are granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }

        // Request location updates from FusedLocationProviderClient
        startLocationUpdates();
    }

    // Check if location is enabled
    private boolean checkLocation() {
        if (!isLocationEnabled()) {
            showAlert();
        }
        return isLocationEnabled();
    }

    // Show alert to enable location services
    private void showAlert() {
        if (!mAlertDialog.isShowing()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("Su configuración de ubicación está 'Desactivada'.\nPor favor, actívela para poder obtener las coordenadas.")
                    .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            paramDialogInterface.dismiss();
                        }
                    });
            mAlertDialog = dialog.create();
            mAlertDialog.show();
        }
    }

    // Check if location services are enabled
    private boolean isLocationEnabled() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        return fusedLocationClient != null && (fusedLocationClient.getLastLocation() != null);
    }

    // Callback interface for location updates
    public interface LocationListenerCallback {
        void onLocationUpdate(Location location);
    }
}
