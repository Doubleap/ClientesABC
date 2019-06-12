package proyecto.app.clientesabc.Actividades;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

public class LocacionGPSActivity {

    private static final String TAG = "LocacionGPSActivity";
    private EditText mLatitudeTextView;
    private EditText mLongitudeTextView;
    private Location mLocation;
    private LocationManager mLocationManager;

    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;
    private Context context;
    private Activity activity;

    LocacionGPSActivity() {

    }

    LocacionGPSActivity(Context c, Activity a, EditText lat, EditText longi){
        context = c;
        activity = a;
        mLatitudeTextView = lat;
        mLongitudeTextView = longi;
    }

    protected void startLocationUpdates() {
        // Listener para escuchar cada vez que la locacion cambia
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String msg = "Actualizando ubicacion: " +
                        Double.toString(location.getLatitude()) + "," +
                        Double.toString(location.getLongitude());
                mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
                mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));

                //Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                // You can now create a LatLng Object for use with maps
                //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Toast.makeText(getBaseContext(), "Status "+provider + " : "+status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                //Toast.makeText(getBaseContext(), "Habilitado "+provider, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                //Toast.makeText(getBaseContext(), "Deshabilitado "+provider, Toast.LENGTH_SHORT).show();
            }
        };
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLatitudeTextView.setText("0");
        mLongitudeTextView.setText("0");
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Habilitar ubicacion")
                .setMessage("Su configuracion de ubicacion esta 'Apagada'.\nPor favor habilitar para " +
                        " poder ubicar las coordenadas del cliente.")
                .setPositiveButton("Configuracion de ubicacion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
    }

}