package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;

public class OSMPickerActivity extends AppCompatActivity {

    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    private static final int RESULT_OK = 1;
    private MapView mapView;
    private LatLong selectedPoint;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());

        setContentView(R.layout.activity_osm_picker);

        mapView = findViewById(R.id.mapView);

        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Presione en el mapa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        File mapFile = null;

        switch(PreferenceManager.getDefaultSharedPreferences(mapView.getContext()).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad())){
            case "F443":
                mapFile = new File(getExternalFilesDir(null), "extra/costa-rica.map");
                break;
            case "F445":
                mapFile = new File(getExternalFilesDir(null), "nicaragua.map");
                break;
            case "F446":
            case "1657":
            case "1658":
                mapFile = new File(getExternalFilesDir(null), "guatemala.map");
                break;
            case "1661":
            case "Z001":
                mapFile = new File(getExternalFilesDir(null), "uruguay.map");
                break;
            case "F428":
                mapFile = new File(getExternalFilesDir(null), "colombia.map");
                break;
        }

        if (!mapFile.exists()) {
            throw new RuntimeException("Archivo .map no encontrado: " + mapFile.getAbsolutePath());
        }

        TileCache tileCache = AndroidUtil.createTileCache(this, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                mapView.getModel().frameBufferModel.getOverdrawFactor());

        MapFile mf = new MapFile(mapFile);

        TileRendererLayer tileRendererLayer = new TileRendererLayer(
                tileCache,
                mf,
                mapView.getModel().mapViewPosition,
                AndroidGraphicFactory.INSTANCE
        );

        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        LatLong initialPoint = new LatLong(9.92502852, -84.08184543);
        selectedPoint = initialPoint;
        mapView.setCenter(selectedPoint); // Costa Rica
        mapView.setZoomLevel((byte) 10);
        // Also set initial marker if coords were passed
        if (getIntent().hasExtra(EXTRA_LATITUDE) && getIntent().hasExtra(EXTRA_LONGITUDE)) {
            double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, initialPoint.latitude);
            double lon = getIntent().getDoubleExtra(EXTRA_LONGITUDE, initialPoint.longitude);

            selectedPoint = new LatLong(lat, lon);

            // Center and zoom in
            mapView.setCenter(selectedPoint);
            mapView.setZoomLevel((byte) 17); // or 17 for closer view

            // Add marker
            if (marker != null) {
                mapView.getLayerManager().getLayers().remove(marker);
            }

            marker = new Marker(
                    selectedPoint,
                    AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.pin_point, null)),
                    0,
                    -markerOffset()
            );
            mapView.getLayerManager().getLayers().add(marker);
        }

        // ✅ Escucha eventos táctiles
        mapView.setOnTouchListener(new View.OnTouchListener() {
            float downX = 0;
            float downY = 0;
            long downTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        downTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        float upX = event.getX();
                        float upY = event.getY();
                        long upTime = System.currentTimeMillis();

                        float deltaX = Math.abs(upX - downX);
                        float deltaY = Math.abs(upY - downY);
                        long deltaTime = upTime - downTime;

                        // Thresholds: max 10px movement and max 200ms touch
                        if (deltaX < 10 && deltaY < 10 && deltaTime < 200) {
                            LatLong latLong = mapView.getMapViewProjection().fromPixels((int) upX, (int) upY);
                            selectedPoint = latLong;

                            // Remove old marker
                            if (marker != null) {
                                mapView.getLayerManager().getLayers().remove(marker);
                            }

                            // Add new marker
                            marker = new Marker(
                                    latLong,
                                    AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.pin_point, null)),
                                    0,
                                    -markerOffset()
                            );
                            mapView.getLayerManager().getLayers().add(marker);
                        }
                        break;
                }
                return false;
            }
        });

        Button btnSelect = findViewById(R.id.btn_select_location);
        btnSelect.setOnClickListener(view -> {
            if (selectedPoint != null) {
                double lat1 = selectedPoint.latitude;
                double lon1 = selectedPoint.longitude;

                String formattedLat = String.format(Locale.US, "%.12f", lat1);
                String formattedLon = String.format(Locale.US, "%.12f", lon1);

                Intent result = new Intent();
                result.putExtra("latitude", formattedLat);
                result.putExtra("longitude", formattedLon);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toasty.info(this, "Seleccione un punto en el mapa").show();
            }
        });


        /*super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Configuration.getInstance().setUserAgentValue(getPackageName()); // ADD THIS LINE
        setContentView(R.layout.activity_osm_picker);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seleccione un punto en el mapa");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back arrow
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        map = findViewById(R.id.osm_map);

        Configuration.getInstance().setOsmdroidBasePath(getCacheDir());
        Configuration.getInstance().setOsmdroidTileCache(getCacheDir());

        map.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);
        map.setMultiTouchControls(true);

        IMapController controller = map.getController();
        double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 9.950590);
        double lon = getIntent().getDoubleExtra(EXTRA_LONGITUDE, -84.098625);
        GeoPoint initialPoint = new GeoPoint(lat, lon);
        controller.setZoom(13.5);
        controller.setCenter(initialPoint);

        // Also set initial marker if coords were passed
        if (getIntent().hasExtra(EXTRA_LATITUDE) && getIntent().hasExtra(EXTRA_LONGITUDE)) {
            selectedPoint = initialPoint;
            controller.setZoom(17.0);
            controller.setCenter(selectedPoint);
            marker = new Marker(map);
            marker.setPosition(initialPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle("Punto seleccionado");
            map.getOverlays().add(marker);
        }

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
                double lat1 = selectedPoint.getLatitude();
                double lon1 = selectedPoint.getLongitude();

                DecimalFormat df_lat;
                if (Math.abs(lat1) < 10 && Math.abs(lat1) > -10) {
                    df_lat = new DecimalFormat("0.############");
                    df_lat.setMaximumFractionDigits(12);
                } else {
                    df_lat = new DecimalFormat("0.###########");
                    df_lat.setMaximumFractionDigits(11);
                }
                DecimalFormat df_lng;
                if (Math.abs(lon1) < 10 && Math.abs(lon1) > -10) {
                    df_lng = new DecimalFormat("0.############");
                    df_lng.setMaximumFractionDigits(12);
                } else {
                    df_lng = new DecimalFormat("0.###########");
                    df_lng.setMaximumFractionDigits(11);
                }

                String formattedLat = df_lat.format(lat1);
                String formattedLon = df_lng.format(lon1);

                Intent result = new Intent();
                result.putExtra("latitude", formattedLat);
                result.putExtra("longitude", formattedLon);
                setResult(RESULT_OK, result);
                finish();
            } else {
                Toasty.info(this,"Seleccione en el mapa").show();
            }
        });*/
    }

    private int markerOffset() {
        return AndroidGraphicFactory.convertToBitmap(getResources().getDrawable(R.drawable.pin_point, null)).getHeight() / 2;
    }

    private void copiarArchivoMapSiNoExiste() {
        File mapFile = new File(getExternalFilesDir(null), "extra/costa-rica.map");
        if (!mapFile.exists()) {
            try (InputStream is = getAssets().open("extra/costa-rica.map");
                 OutputStream os = new FileOutputStream(mapFile)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error copiando .map desde assets", e);
            }
        }
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
        //mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }
}