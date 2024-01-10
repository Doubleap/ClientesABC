package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vicmikhailau.maskededittext.MaskedEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;

import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.ConexionTableAdapter;
import proyecto.app.clientesabc.clases.PruebaConexionAPI;
import proyecto.app.clientesabc.clases.PruebaConexionServidor;
import proyecto.app.clientesabc.clases.SincronizacionAPI;
import proyecto.app.clientesabc.clases.SincronizacionServidor;
import proyecto.app.clientesabc.clases.TransmisionAPI;
import proyecto.app.clientesabc.clases.TransmisionServidor;
import proyecto.app.clientesabc.modelos.Conexion;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;

public class APIConfigActivity extends AppCompatActivity
{
    private Button serverTransmitButton;
    private Button clientReceiveButton;
    private Button serverUDPButton;
    private Button clientUDPButton;
    private Button probarConexionButton;
    private Spinner tipo_conexion;
    private EditText ip_text;
    private TextView elemento_invisible;
    private EditText puerto_text;
    private MaskedEditText ruta_text;

    private int PICKFILE_REQUEST_CODE = 100;
    private String filePath="";
    private String wholePath="";
    private AppCompatButton addConexion;
    private String m_Text = "";
    private int contadorEditar = 0;

    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Conexion> tv_conexiones;
    private static ArrayList<Conexion> list_conexiones;

    private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    /** Se llama cuando la actividad es creada por primera vez */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_config);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
        boolean deshabilitarTransmision = false;
        Bundle b = getIntent().getExtras();
        if(b != null) {
            deshabilitarTransmision = b.getBoolean("deshabilitarTransmision", false);
        }


        addConexion = findViewById(R.id.add_conexion);
        tipo_conexion = findViewById(R.id.tipo_conexion);

        ArrayList<OpcionSpinner> listatipos = new ArrayList<>();
        OpcionSpinner opAPI = new OpcionSpinner("api","REST API");
        //OpcionSpinner opWIFI = new OpcionSpinner("wifi","WiFi");
        //OpcionSpinner opGPRS = new OpcionSpinner("gprs","GPRS");
        //OpcionSpinner opLocal = new OpcionSpinner("local","Local");
        listatipos.add(opAPI);
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, listatipos);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        tipo_conexion.setAdapter(dataAdapter);

        tipo_conexion.setSelection(VariablesGlobales.getIndex(tipo_conexion,PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).getString("tipo_conexion","")));
        ip_text = findViewById(R.id.txtservidor);
        elemento_invisible = findViewById(R.id.elemento_invisible);
        ip_text.setText(PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).getString("url_api",VariablesGlobales.getUrlApi()));
        elemento_invisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contadorEditar == 9){
                    elemento_invisible.setVisibility(View.GONE);
                    ip_text.setEnabled(true);
                    Toasty.success(APIConfigActivity.this, "URL API ahora puede ser modificada!", Toast.LENGTH_SHORT).show();
                    /*ip_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                VariablesGlobales.setUrlApi(ip_text.getText().toString());
                                Toasty.success(APIConfigActivity.this, "URL API ha sido guardada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });*/
                }
                contadorEditar++;
            }
        });
        ruta_text = findViewById(R.id.txtRuta);
        InputFilter[] editFilters = ruta_text.getFilters();
        InputFilter[] newFilters = null;
        editFilters = ruta_text.getFilters();
        newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        ruta_text.setFilters(newFilters);
        ruta_text.setAllCaps(true);
        ruta_text.setText(PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).getString("W_CTE_RUTAHH",""));

        probarConexionButton = findViewById(R.id.button_probar_conexion);
        probarConexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(validarConexion()) {
                    //Realizar una prueba de conexion para validar los datos ingresados
                    WeakReference<Context> weakRef = new WeakReference<Context>(APIConfigActivity.this);
                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(APIConfigActivity.this);
                    ip_text.setText(ip_text.getText().toString().trim());
                    ruta_text.setText(ruta_text.getText().toString().trim());
                    PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("tipo_conexion",((OpcionSpinner)tipo_conexion.getSelectedItem()).getId()).apply();
                    PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("url_api",ip_text.getText().toString()).apply();
                    PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();
                    if (PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).getString("tipo_conexion","").equals("api")) {
                        PruebaConexionAPI f = new PruebaConexionAPI(weakRef, weakRefA);
                        if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                            EnableWiFi();
                        }
                        f.execute();
                    } else {
                        PruebaConexionServidor f = new PruebaConexionServidor(weakRef, weakRefA);
                        if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                            EnableWiFi();
                        }else{
                            DisableWiFi();
                        }
                        f.execute();
                    }
                }
            }
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_tcp);

        BottomNavigationMenuView navMenuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
        navMenuView.setPadding(0,0,1,0);
        //navMenuView.addItemDecoration(new DividerItemDecoration(TCPActivity.this,DividerItemDecoration.VERTICAL));
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_cancelar:
                        finish();
                        return true;
                    case R.id.action_sincronizar:
                        if(validarConexion()) {
                            //startService(new Intent(TCPActivity.this, NameService.class));
                            WeakReference<Context> weakRef = new WeakReference<Context>(APIConfigActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(APIConfigActivity.this);
                            PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("tipo_conexion",((OpcionSpinner)tipo_conexion.getSelectedItem()).getId()).apply();
                            PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("url_api",ip_text.getText().toString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();

                            if (PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).getString("tipo_conexion","").equals("api")) {
                                SincronizacionAPI s = new SincronizacionAPI(weakRef, weakRefA);
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }
                                s.execute();
                            } else {
                                SincronizacionServidor s = new SincronizacionServidor(weakRef, weakRefA);
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }else{
                                    DisableWiFi();
                                }
                                s.execute();
                            }
                        }
                        return true;
                    case R.id.action_transmitir:
                        if(validarConexion()) {
                            //Realizar la transmision de lo que se necesita (Db o txt)
                            WeakReference<Context> weakRef = new WeakReference<Context>(APIConfigActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(APIConfigActivity.this);
                            PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("tipo_conexion",((OpcionSpinner)tipo_conexion.getSelectedItem()).getId()).apply();
                            PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("url_api",ip_text.getText().toString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();

                            if (PreferenceManager.getDefaultSharedPreferences(APIConfigActivity.this).getString("tipo_conexion","").equals("api")) {
                                TransmisionAPI f = new TransmisionAPI(weakRef, weakRefA, filePath, wholePath,"");
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }
                                f.execute();
                            } else {
                                TransmisionServidor f = new TransmisionServidor(weakRef, weakRefA, filePath, wholePath,"");
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }else{
                                    DisableWiFi();
                                }
                                f.execute();
                            }
                        }
                }
                return true;
            }
        });

        bottomNavigation.getMenu().getItem(2).setEnabled(!deshabilitarTransmision);
        bottomNavigation.getMenu().getItem(2).setVisible(!deshabilitarTransmision);
        /**/
        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Configuración de Comunicación API REST");
        toolbar.setBackground(d);
    }

    private boolean validarConexion(){
        boolean retorno = true;
        if(ip_text.getText().toString().trim().isEmpty()){
            Toasty.warning(getBaseContext(),"Por favor digite una direccion url api válida.").show();
            retorno = false;
        }
        if(ruta_text.getText().toString().trim().isEmpty()){
            Toasty.warning(getBaseContext(),"Por favor digite una ruta de venta válida.").show();
            retorno = false;
        }
        return retorno;
    }

    public void EnableWiFi() {
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(true);
    }

    public void DisableWiFi() {
        WifiManager wifimanager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(false);
    }
}
