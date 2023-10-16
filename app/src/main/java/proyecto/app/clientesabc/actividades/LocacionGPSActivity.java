package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.text.DecimalFormat;

import es.dmoral.toasty.Toasty;

public class LocacionGPSActivity {

    private static final String TAG = "LocacionGPSActivity";
    private MaskedEditText mLatitudeTextView;
    private MaskedEditText mLongitudeTextView;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1000; /* 1 sec */

    private LocationManager locationManager;
    private Context context;
    private Activity activity;

    private LocationListener locationListener;
    private LocationListenerCallback callback;
    LocacionGPSActivity() {

    }

    public LocacionGPSActivity(Context context, LocationListenerCallback callback) {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        this.callback = callback;

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (callback != null) {
                    callback.onLocationUpdate(location);
                }
            }
            // Other LocationListener methods...
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FASTEST_INTERVAL, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, FASTEST_INTERVAL, 0, locationListener);
        } catch (java.lang.SecurityException ex) {
            Toasty.error(context, "Fallo en pedir la ubicacion").show();
        } catch (IllegalArgumentException ex) {
            Toasty.error(context, "Proveedor de ubicacion no existe").show();
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    LocacionGPSActivity(Context c, Activity a, MaskedEditText lat, MaskedEditText longi){
        context = c;
        activity = a;
        mLatitudeTextView = lat;
        mLongitudeTextView = longi;
    }

    protected void startLocationUpdates() {
        checkLocation();
        // Listener para escuchar cada vez que la locacion cambia
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String latitud = String.valueOf(Double.parseDouble(new DecimalFormat("##.############").format(location.getLatitude())));
                String longitud = String.valueOf(Double.parseDouble(new DecimalFormat("##.############").format(location.getLongitude())));
                String msg = "Coordenadas Actualizadas: " + latitud + "," + longitud;
                mLatitudeTextView.setText(latitud);
                mLongitudeTextView.setText(longitud);

                Toasty.success(context, msg, Toasty.LENGTH_SHORT).show();
                // You can now create a LatLng Object for use with maps
                //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if(!mLatitudeTextView.getText().equals("0") && !mLongitudeTextView.getText().equals("0")) {
                    locationManager.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Toasty.info(context, "Status "+provider + " : "+status, Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
                //Toasty.info(context, "Habilitado " + provider, Toasty.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                //Toast.makeText(getBaseContext(), "Deshabilitado "+provider, Toast.LENGTH_SHORT).show();
            }
        };
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FASTEST_INTERVAL, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FASTEST_INTERVAL, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, FASTEST_INTERVAL, 0, locationListener);
        } catch (java.lang.SecurityException ex) {
            Toasty.error(context, "Fallo en pedir la ubicacion").show();
        } catch (IllegalArgumentException ex) {
            Toasty.error(context, "Proveedor de ubicacion no existe").show();
        }

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
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER));
    }

    public interface LocationListenerCallback {
        void onLocationUpdate(Location location);
    }
}