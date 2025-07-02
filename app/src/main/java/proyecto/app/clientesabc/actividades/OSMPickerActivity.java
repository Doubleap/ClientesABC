package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.text.DecimalFormat;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;

public class OSMPickerActivity extends AppCompatActivity {

    private static final int RESULT_OK = 1;
    private MapView map;
    private GeoPoint selectedPoint;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.activity_osm_picker);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seleccione un punto en el mapa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        map = findViewById(R.id.osm_map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController controller = map.getController();
        GeoPoint defaultPoint = new GeoPoint(9.950590, -84.098625); // Costa Rica
        controller.setZoom(13.5);
        controller.setCenter(defaultPoint);

        // Show current GPS location
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        locationOverlay.enableMyLocation();
        map.getOverlays().add(locationOverlay);

        // Handle map taps
        MapEventsOverlay eventsOverlay = new MapEventsOverlay(this, new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                selectedPoint = p;
                if (marker != null) map.getOverlays().remove(marker);
                marker = new Marker(map);
                marker.setPosition(p);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle("Punto seleccionado");
                map.getOverlays().add(marker);

                map.invalidate();
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        });
        map.getOverlays().add(eventsOverlay);

        Button btnSelect = findViewById(R.id.btn_select_location);
        btnSelect.setOnClickListener(view -> {
            if (selectedPoint != null) {
                double lat = selectedPoint.getLatitude();
                double lon = selectedPoint.getLongitude();

                DecimalFormat df_lat;
                if (Math.abs(lat) < 10 && Math.abs(lat) > -10) {
                    df_lat = new DecimalFormat("0.############");
                    df_lat.setMaximumFractionDigits(12);
                } else {
                    df_lat = new DecimalFormat("0.###########");
                    df_lat.setMaximumFractionDigits(11);
                }
                DecimalFormat df_lng;
                if (Math.abs(lon) < 10 && Math.abs(lon) > -10) {
                    df_lng = new DecimalFormat("0.############");
                    df_lng.setMaximumFractionDigits(12);
                } else {
                    df_lng = new DecimalFormat("0.###########");
                    df_lng.setMaximumFractionDigits(11);
                }

                String formattedLat = df_lat.format(lat);
                String formattedLon = df_lng.format(lon);

                Intent result = new Intent();
                result.putExtra("latitude", formattedLat);
                result.putExtra("longitude", formattedLon);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toasty.info(this,"Seleccione en el mapa").show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Back arrow clicked
            OSMPickerActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}