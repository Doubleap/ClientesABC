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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

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
import proyecto.app.clientesabc.clases.ConfiguracionPaisAPI;
import proyecto.app.clientesabc.clases.PruebaConexionAPI;
import proyecto.app.clientesabc.clases.PruebaConexionServidor;
import proyecto.app.clientesabc.clases.SincronizacionServidor;
import proyecto.app.clientesabc.clases.TransmisionServidor;
import proyecto.app.clientesabc.modelos.Conexion;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;

public class ConfiguracionGeneralActivity extends AppCompatActivity
{
    private Button serverTransmitButton;
    private Button clientReceiveButton;
    private Button serverUDPButton;
    private Button clientUDPButton;
    private Button probarConexionButton;
    private Spinner pais_spinner;
    private EditText sociedad_text;
    private EditText orgventas_text;
    private EditText land1_text;
    private EditText cadenaRM_text;
    private EditText grupocuentas_text;
    private EditText bdregional_text;
    private EditText versionhh_text;
    private EditText diashistorial_text;

    @SuppressLint("StaticFieldLeak")

    private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    /** Se llama cuando la actividad es creada por primera vez */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_general);

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

        ArrayList<OpcionSpinner> listapaises = new ArrayList<>();
        OpcionSpinner opCR = new OpcionSpinner("F443","Costa Rica");
        OpcionSpinner opNI = new OpcionSpinner("F445","Nicaragua");
        OpcionSpinner opPA = new OpcionSpinner("F451","Panamá");
        OpcionSpinner opEM = new OpcionSpinner("F446","Guatemala Embocen");
        OpcionSpinner opVO = new OpcionSpinner("1657","Guatemala Volcanes");
        OpcionSpinner opAB = new OpcionSpinner("1658","Guatemala Abasa");
        OpcionSpinner opUY = new OpcionSpinner("1661","Uruguay");
        OpcionSpinner opDI = new OpcionSpinner("Z001","Uruguay Distribuidores");
        //OpcionSpinner opLocal = new OpcionSpinner("local","Local");
        listapaises.add(opCR);listapaises.add(opNI);listapaises.add(opPA);
        listapaises.add(opEM);listapaises.add(opVO);listapaises.add(opAB);
        listapaises.add(opUY);listapaises.add(opDI);
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, listapaises);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        pais_spinner = findViewById(R.id.paisConfigSpinner);
        pais_spinner.setAdapter(dataAdapter);

        pais_spinner.setSelection(VariablesGlobales.getIndex(pais_spinner,PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_PAIS","")));

        sociedad_text = (EditText)findViewById(R.id.txtSociedad);
        sociedad_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_SOCIEDAD",""));
        orgventas_text = (EditText)findViewById(R.id.txtOrgVentas);
        orgventas_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_ORGVENTAS",""));
        land1_text = (EditText)findViewById(R.id.txtLand1);
        land1_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_LAND1",""));
        cadenaRM_text = (EditText)findViewById(R.id.txtCadenaRM);
        cadenaRM_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_CADENARM",""));
        grupocuentas_text = (EditText)findViewById(R.id.txtGrupoCuentas);
        grupocuentas_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_GRUPOCUENTAS",""));
        bdregional_text = (EditText)findViewById(R.id.txtBDRegional);
        bdregional_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_BDREGIONAL",""));
        versionhh_text = (EditText)findViewById(R.id.txtVersionHH);
        versionhh_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_VERSIONHH",""));
        diashistorial_text = (EditText)findViewById(R.id.txtDiasHistorial);
        diashistorial_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_DIASHISTORIAL",""));
        /**/
        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Configuración GENERAL ADMIN");
        toolbar.setBackground(d);

        /*Cada vez que cambia el campo de pais se traeran los datos de la Base de Datos, NO puede ser modificado por el preventa solo de modo de visualizacion x pais.*/
        pais_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                WeakReference<Context> weakRefs1 = new WeakReference<Context>(parent.getContext());
                WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(ConfiguracionGeneralActivity.this);
                ConfiguracionPaisAPI v = new ConfiguracionPaisAPI(weakRefs1, weakRefAs1, opcion.getId());
                //PruebaConexionAPI v = new PruebaConexionAPI(weakRefs1,weakRefAs1);
                v.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void ActualizarConfiguracionPais(Context context, Activity activity, ArrayList<JsonArray> mensajes) {
        JsonObject mensaje=null;
        if(mensajes.size() > 0 && mensajes.get(0)  != null) {
            mensaje = mensajes.get(0).getAsJsonArray().get(0).getAsJsonObject();
            if (!mensaje.isJsonNull()) {
                EditText sociedad_text = activity.findViewById(R.id.txtSociedad);
                sociedad_text.setText(mensaje.get("id_bukrs").getAsString());
                EditText orgventas_text = activity.findViewById(R.id.txtOrgVentas);
                orgventas_text.setText(mensaje.get("vkorg").getAsString());
                EditText land1_text = activity.findViewById(R.id.txtLand1);
                land1_text.setText(mensaje.get("land1").getAsString());
                EditText cadenaRM_text = activity.findViewById(R.id.txtCadenaRM);
                cadenaRM_text.setText(mensaje.get("hkunnr").getAsString());
                EditText bdregional_text = activity.findViewById(R.id.txtBDRegional);
                bdregional_text.setText(mensaje.get("bd_regional_r40").getAsString());
                EditText versionhh_text = activity.findViewById(R.id.txtVersionHH);
                versionhh_text.setText(mensaje.get("versionHH").getAsString());
                EditText diashistorial_text = activity.findViewById(R.id.txtDiasHistorial);
                diashistorial_text.setText(mensaje.get("diasHistorialHH").getAsString() + " Días");

                EditText grupocuentas_text = activity.findViewById(R.id.txtGrupoCuentas);
                switch(mensaje.get("id_bukrs").getAsString()){
                    case "F443":
                        grupocuentas_text.setText("RCMA");
                        break;
                    case "F445":
                        grupocuentas_text.setText("NCMA");
                        break;
                    case "F446":
                        grupocuentas_text.setText("GCMA");
                        break;
                    case "1657":
                        grupocuentas_text.setText("GCMC");
                        break;
                    case "1658":
                        grupocuentas_text.setText("GCMB");
                        break;
                    case "1661":
                        grupocuentas_text.setText("UYDE");
                        break;
                    case "Z001":
                        grupocuentas_text.setText("UYDE");
                        break;
                    default:
                        grupocuentas_text.setText("NO ESPECIFICADO PARA EL PAIS");
                        break;
                }
                //orgventas_text.setText(mensaje.get("ktokd").getAsString());
            }
        }
    }

    //Conexiones de preferencia
    public static ArrayList<Conexion> getConexionesFromSharedPreferences(Context context){
        ArrayList<Conexion> productFromShared = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonPreferences = sharedPref.getString("Conexiones", "");

        Type type = new TypeToken<ArrayList<Conexion>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
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
