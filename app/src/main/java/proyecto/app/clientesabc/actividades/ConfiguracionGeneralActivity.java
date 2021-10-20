package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.ConexionTableAdapter;
import proyecto.app.clientesabc.clases.ConfiguracionPaisAPI;
import proyecto.app.clientesabc.clases.ConfiguracionPaisServidor;
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
        /*listapaises.add(opCR);listapaises.add(opNI);listapaises.add(opPA);
        listapaises.add(opEM);listapaises.add(opVO);listapaises.add(opAB);listapaises.add(opEM);*/
        listapaises.add(opUY);listapaises.add(opDI);

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, listapaises);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        pais_spinner = findViewById(R.id.paisConfigSpinner);
        pais_spinner.setAdapter(dataAdapter);

        pais_spinner.setSelection(VariablesGlobales.getIndex(pais_spinner,PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_SOCIEDAD","")));

        sociedad_text = (EditText)findViewById(R.id.txtSociedad);
        sociedad_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_SOCIEDAD",""));
        orgventas_text = (EditText)findViewById(R.id.txtOrgVentas);
        orgventas_text.setText(PreferenceManager.getDefaultSharedPreferences(ConfiguracionGeneralActivity.this).getString("CONFIG_ORGVENTAS",VariablesGlobales.getOrgvta()));
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
                if(VariablesGlobales.UsarAPI()) {
                    ConfiguracionPaisAPI v = new ConfiguracionPaisAPI(weakRefs1, weakRefAs1, opcion.getId());
                    v.execute();
                }else{
                    ConfiguracionPaisServidor v = new ConfiguracionPaisServidor(weakRefs1, weakRefAs1, opcion.getId());
                    v.execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void ActualizarConfiguracionPais(Context context, Activity activity, ArrayList<JsonArray> mensajes) throws IOException {
        JsonObject mensaje=null;
        if(mensajes.size() > 0 && mensajes.get(0)  != null && mensajes.get(0).size() > 0) {
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
                String grupo_cuentas = "";
                switch(mensaje.get("id_bukrs").getAsString()){
                    case "F443":
                        grupo_cuentas = "RCMA";
                        break;
                    case "F445":
                        grupo_cuentas = "RCMA";
                        break;
                    case "F446":
                        grupo_cuentas = "GCMA";
                        break;
                    case "1657":
                        grupo_cuentas = "GCMC";
                        break;
                    case "1658":
                        grupo_cuentas = "GCMB";
                        break;
                    case "1661":
                        grupo_cuentas = "UYDE";
                        break;
                    case "Z001":
                        grupo_cuentas = "UYDD";
                        break;
                    default:
                        grupo_cuentas = "NO ESPECIFICADO PARA EL PAIS";
                        break;
                }
                grupocuentas_text.setText(grupo_cuentas);

                //Cambiar los valores de la configuracion general TODO
                InputStream is = context.getAssets().open("configuracion.xml");

                File tranFileDir=null;
                File externalStorage = Environment.getExternalStorageDirectory();
                String externalStoragePath=null;
                if (externalStorage != null) {
                    externalStoragePath = externalStorage.getAbsolutePath();
                    tranFileDir = new File(externalStoragePath + File.separator + context.getPackageName(),"configuracion.xml");
                    //boolean ex = tranFileDir.mkdirs();
                    //File transferFile = new File(tranFileDir, "configuracion.xml");
                    OutputStream stream = null;
                    try {
                        stream = new FileOutputStream(tranFileDir);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        stream.write(buffer, 0, read);
                    }

                }

                //Modificar xml segun los datos traidos e la Base de Datos
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringComments(true);
                documentBuilderFactory.setIgnoringElementContentWhitespace(true);
                DocumentBuilder documentBuilder = null;
                try {
                    documentBuilder = documentBuilderFactory.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                Document document = null;
                try {
                    document = documentBuilder.parse(tranFileDir);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                Element docEle = document.getDocumentElement();
                NodeList nl = docEle.getChildNodes();
                NodeList conexiones = document.getElementsByTagName("configuracion").item(0).getChildNodes();
                //Seccion datos de conexion guardados o defectos
                for (int i = 0; i < conexiones.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) nl.item(i);
                        if (el.getNodeName().equals("sistema")) {
                            //Seccion datos de sistema
                            el.getElementsByTagName("sociedad").item(0).setTextContent(mensaje.get("id_bukrs").getAsString());
                            el.getElementsByTagName("orgvta").item(0).setTextContent(mensaje.get("vkorg").getAsString());
                            el.getElementsByTagName("land1").item(0).setTextContent(mensaje.get("land1").getAsString());
                            el.getElementsByTagName("cadenaRM").item(0).setTextContent(mensaje.get("hkunnr").getAsString());
                            el.getElementsByTagName("ktokd").item(0).setTextContent(grupo_cuentas);
                            //el.getElementsByTagName("urlApi").item(0).setTextContent(mensaje.get("id_bukrs").getAsString());
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("W_CTE_BUKRS", mensaje.get("id_bukrs").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("W_CTE_ORGVTA", mensaje.get("vkorg").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("W_CTE_LAND1", mensaje.get("land1").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("W_CTE_CADENARM", mensaje.get("hkunnr").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("W_CTE_KTOKD", grupo_cuentas).apply();
                            //PreferenceManager.getDefaultSharedPreferences(context).edit().putString("URL_API", urlapi).apply();

                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CONFIG_PAIS", mensaje.get("desc_bukrs").getAsString().toUpperCase()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CONFIG_SOCIEDAD", mensaje.get("id_bukrs").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CONFIG_ORGVENTAS", mensaje.get("vkorg").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CONFIG_LAND1", mensaje.get("land1").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CONFIG_CADENARM", mensaje.get("hkunnr").getAsString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CONFIG_GRUPOCUENTAS", grupo_cuentas).apply();
                        }
                        if (el.getNodeName().equals("login")) {
                            //Seccion datos de login
                            String usr = document.getElementsByTagName("user").item(0).getTextContent();
                            String pwd = document.getElementsByTagName("password").item(0).getTextContent();
                            String guardar_contrasena = document.getElementsByTagName("guardar_contrasena").item(0).getTextContent();
                            if(PreferenceManager.getDefaultSharedPreferences(context).getString("user","").isEmpty()){
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("user", usr).apply();
                            }
                            if(PreferenceManager.getDefaultSharedPreferences(context).getString("password","").isEmpty()){
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("password", pwd).apply();
                            }
                            if(PreferenceManager.getDefaultSharedPreferences(context).getString("guarda_contrasena","").isEmpty()){
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("guarda_contrasena", guardar_contrasena).apply();
                            }
                        }
                        /*if (el.getNodeName().equals("conexion")) {
                            String nombre = document.getElementsByTagName("nombre").item(i).getTextContent();
                            String tipo_conexion = document.getElementsByTagName("tipo_conexion").item(i).getTextContent();
                            String Ip = document.getElementsByTagName("Ip").item(i).getTextContent();
                            String Puerto = document.getElementsByTagName("Puerto").item(i).getTextContent();
                            boolean defecto = el.getAttribute("defecto").equals("true") ? true : false;
                            Conexion con = new Conexion();
                            con.setTipo(tipo_conexion);
                            con.setIp(Ip);
                            con.setPuerto(Puerto);
                            con.setDefecto(defecto);
                            if (!VariablesGlobales.UsarAPI()) {
                                TCPActivity.AgregarNuevaConexion(context, con);
                            }

                            if (el.getAttribute("defecto").equals("true")) {
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("tipo_conexion", tipo_conexion).apply();
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("Ip", Ip).apply();
                                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("Puerto", Puerto).apply();
                            }
                        }*/
                    }
                }

                //save to file
                DOMSource source = new DOMSource(document);
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = null;
                try {
                    transformer = transformerFactory.newTransformer();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                }
                StreamResult result = new StreamResult(new File(externalStoragePath + File.separator + context.getPackageName(),"configuracion.xml"));
                try {
                    transformer.transform(source, result);
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
        }else{
            EditText sociedad_text = activity.findViewById(R.id.txtSociedad);
            sociedad_text.setText("");
            EditText orgventas_text = activity.findViewById(R.id.txtOrgVentas);
            orgventas_text.setText("");
            EditText land1_text = activity.findViewById(R.id.txtLand1);
            land1_text.setText("");
            EditText cadenaRM_text = activity.findViewById(R.id.txtCadenaRM);
            cadenaRM_text.setText("");
            EditText bdregional_text = activity.findViewById(R.id.txtBDRegional);
            bdregional_text.setText("");
            EditText versionhh_text = activity.findViewById(R.id.txtVersionHH);
            versionhh_text.setText("");
            EditText diashistorial_text = activity.findViewById(R.id.txtDiasHistorial);
            diashistorial_text.setText("");
            EditText grupocuentas_text = activity.findViewById(R.id.txtGrupoCuentas);
            grupocuentas_text.setText("");
            Toasty.warning(context,"No se encontro la configuracion de la sociedad seleccionada en cat_bukrs!").show();
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
