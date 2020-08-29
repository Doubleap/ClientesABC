package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.ActualizacionServidor;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.modelos.Conexion;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    private ImageView bookIconImageView;
    private TextView bookITextView;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    private BroadcastReceiver myReceiver;
    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private CheckBox mCheckbox;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView femsa_logo;
    private TextView ruta_datos;
    private Intent intent;
    private TextView versionLogin;
    private Button SignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getTitle()+VariablesGlobales.getNombrePais());
        /*Notificacion notificacion = new Notificacion(getBaseContext());
        notificacion.crearNotificacion(0,"Actualizar Aplicacion", "Andres Aymerich codigo 9000214432", R.drawable.logo_mc, R.drawable.icon_add_client, R.color.aprobados);
        notificacion.crearNotificacion(1,"Solicitud con incidencia!", "Codigo de solicitud o nombre de cliente", R.drawable.logo_mc, R.drawable.icon_info_title, R.color.devuelto);
        */
        versionLogin = findViewById(R.id.versionPanel);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date buildDate = BuildConfig.BuildDate;
        versionLogin.setText("Versión: "+ BuildConfig.VERSION_NAME+" ("+dateFormat.format(buildDate)+")");

        femsa_logo = findViewById(R.id.femsa_logo);
        if(VariablesGlobales.getLand1().equals("GT"))
            femsa_logo.setImageDrawable(getResources().getDrawable(R.drawable.femsa_logo_gt,null));
        ruta_datos = findViewById(R.id.ruta_datos);
        // Set up the login form.
        mUserView = findViewById(R.id.user);
        mCheckbox = findViewById(R.id.guardar_contrasena);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mUserView.setText(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("user",""));
        mPasswordView.setText(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("password",""));
        mCheckbox.setChecked(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean("guardar_contrasena",false));

        femsa_logo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHandler appdialog = new DialogHandler();
                appdialog.Confirm(LoginActivity.this, "Confirmar Sincronización", "Esta seguro que desea sincronizar la información de la ruta?", "NO", "SI", new LoginActivity.SincronizarLogin(LoginActivity.this));
            }
        });

        File externalStorage = Environment.getExternalStorageDirectory();
        String externalStoragePath = externalStorage.getAbsolutePath();
        File file = new File(externalStoragePath + File.separator + getPackageName() + File.separator +"configuracion.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = documentBuilder.parse(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        if(document != null) {
            Element docEle = document.getDocumentElement();
            NodeList nl = docEle.getChildNodes();
            NodeList conexiones = document.getElementsByTagName("conexiones");
            //Seccion datos de conexion guardados o defectos
            for (int i = 0; i < conexiones.getLength(); i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) nl.item(i);
                    if (el.getNodeName().equals("sistema")) {
                        //Seccion datos de sistema
                        String sociedad = el.getElementsByTagName("sociedad").item(0).getTextContent();
                        String orgvta = el.getElementsByTagName("orgvta").item(0).getTextContent();
                        String land1 = el.getElementsByTagName("land1").item(0).getTextContent();
                        String cadenaRM = el.getElementsByTagName("cadenaRM").item(0).getTextContent();
                        String ktokd = el.getElementsByTagName("ktokd").item(0).getTextContent();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("W_CTE_BUKRS", sociedad).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("W_CTE_ORGVTA", orgvta).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("W_CTE_LAND1", land1).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("W_CTE_CADENARM", cadenaRM).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("W_CTE_KTOKD", ktokd).apply();
                    }
                    if (el.getNodeName().equals("login")) {
                        //Seccion datos de login
                        String usr = document.getElementsByTagName("user").item(0).getTextContent();
                        String pwd = document.getElementsByTagName("password").item(0).getTextContent();
                        String guardar_contrasena = document.getElementsByTagName("guardar_contrasena").item(0).getTextContent();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("user", usr).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("password", pwd).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("guarda_contrasena", guardar_contrasena).apply();
                    }
                    if (el.getNodeName().equals("conexion")) {
                        String nombre = document.getElementsByTagName("nombre").item(i).getTextContent();
                        String tipo_conexion = document.getElementsByTagName("tipo_conexion").item(i).getTextContent();
                        String Ip = document.getElementsByTagName("Ip").item(i).getTextContent();
                        String Puerto = document.getElementsByTagName("Puerto").item(i).getTextContent();
                        boolean defecto = el.getAttribute("defecto").equals("true")?true:false;
                        Conexion con = new Conexion();
                        con.setTipo(tipo_conexion);
                        con.setIp(Ip);
                        con.setPuerto(Puerto);
                        con.setDefecto(defecto);
                        TCPActivity.AgregarNuevaConexion(LoginActivity.this,con);

                        if(el.getAttribute("defecto").equals("true")) {
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("tipo_conexion", tipo_conexion).apply();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("Ip", Ip).apply();
                            PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("Puerto", Puerto).apply();
                        }
                    }
                }
            }
        }else{
            try {
                XmlPullParser xrp=null;
                AssetManager assetMgr = this.getAssets();
                InputStream xml = assetMgr.open("configuracion.xml");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                xrp = factory.newPullParser();
                xrp.setInput(xml, "UTF-8");
                String text = "";
                String nombre = "";
                String nombreCon = "";
                String tipo_conexion = "";
                String Ip = "";
                String Puerto = "";
                int cantCon = -1;
                boolean defecto = false;
                try {
                    int event = xrp.getEventType();
                    while (event != XmlPullParser.END_DOCUMENT) {
                        String name=xrp.getName();
                        switch (event){
                            case XmlPullParser.START_TAG:
                                //Conexiones
                                if(name.equals("conexion") && xrp.getAttributeValue(null,"defecto").equals("true")){
                                    cantCon++;
                                    defecto = true;
                                }else if(name.equals("conexion") && xrp.getAttributeValue(null,"defecto").equals("false")){
                                    cantCon++;
                                    defecto =  false;
                                }
                                break;
                            case XmlPullParser.TEXT:
                                text = xrp.getText();
                                break;
                            case XmlPullParser.END_TAG:
                                //Sistema
                                if(name.equals("sociedad")){
                                    VariablesGlobales.setSociedad(text);
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("W_CTE_BUKRS", text).apply();
                                }
                                if(name.equals("organizacion")){
                                    VariablesGlobales.setOrgvta(text);
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("W_CTE_ORGVTA", text).apply();
                                }
                                if(name.equals("land1")){
                                    VariablesGlobales.setLand1(text);
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("W_CTE_LAND1", text).apply();
                                }
                                if(name.equals("cadenaRM")){
                                    VariablesGlobales.setCadenaRM(text);
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("W_CTE_HKUNNR", text).apply();
                                }
                                if(name.equals("ktokd")){
                                    VariablesGlobales.setKtokd(text);
                                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("W_CTE_KTOKD", text).apply();
                                }

                                if(name.equals("nombre")){
                                    nombre = text;
                                }
                                if(name.equals("tipo_conexion")){
                                    tipo_conexion = text;
                                }
                                if(name.equals("Ip")){
                                    Ip = text;
                                }
                                if(name.equals("Puerto")){
                                    Puerto = text;
                                }
                                if(name.equals("conexion")){
                                    Conexion con = new Conexion();
                                    con.setNombre(nombreCon);
                                    con.setTipo(tipo_conexion);
                                    con.setIp(Ip);
                                    con.setPuerto(Puerto);
                                    con.setDefecto(defecto);
                                    TCPActivity.AgregarNuevaConexion(LoginActivity.this,con);
                                    if(defecto && PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("Ip","").equals("")) {
                                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("tipo_conexion", tipo_conexion).apply();
                                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("Ip", Ip).apply();
                                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("Puerto", Puerto).apply();
                                    }
                                }

                                break;
                        }
                        event = xrp.next();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }

        if(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","").equals("")){
            ruta_datos.setText("Sin Datos!");
        }else{
            ruta_datos.setText("Ruta Preventa "+PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","")+".");
        }

        SignInButton = findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setEnabled(false);
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        //mProgressView = findViewById(R.id.login_progress);

        /**/
        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.titulo_login) +" "+ VariablesGlobales.getNombrePais());
        toolbar.setBackground(d);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white,null));
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawer.closeDrawers();

                switch(menuItem.getItemId()) {
                    case R.id.acercade:
                        showDialogAcercade(LoginActivity.this);
                        /*Bundle bp = new Bundle();
                        //TODO seleccionar el tipo de solicitud por el UI
                        bp.putString("tipoSolicitud", "1"); //id de solicitud

                        intent = new Intent(LoginActivity.this, SolicitudActivity.class);
                        intent.putExtras(bp); //Pase el parametro el Intent
                        startActivity(intent);*/
                        break;
                    case R.id.comunicacion:
                        Bundle b = new Bundle();
                        //TODO seleccionar el tipo de solicitud por el UI
                        b.putBoolean("deshabilitarTransmision", true); //id de solicitud
                        intent = new Intent(LoginActivity.this, TCPActivity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                        break;
                    case R.id.borrar_datos:
                        DialogHandler appdialog = new DialogHandler();
                        appdialog.Confirm(LoginActivity.this, "Confirmar Eliminacion", "Este proceso eliminara todo formulario que no haya sido transmitido, desea continuar con el proceso de eliminacion de datos?", "No", "Si", new LoginActivity.BorrarBaseDatos(getBaseContext()));
                        break;
                    case R.id.actualizar_version:
                        //Realizar la transmision de lo que se necesita (Db o txt)
                        WeakReference<Context> weakRefs = new WeakReference<Context>(LoginActivity.this);
                        WeakReference<Activity> weakRefAs = new WeakReference<Activity>(LoginActivity.this);

                        ActualizacionServidor a = new ActualizacionServidor(weakRefs, weakRefAs);
                        if(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("tipo_conexion","").equals("wifi")){
                            a.EnableWiFi();
                        }else{
                            a.DisableWiFi();
                        }
                        a.execute();
                        break;
                    default:
                        Toasty.info(getBaseContext(),"Opcion no encontrada!").show();
                }
                return false;
            }
        });
        /*Si quiere reversar algun estado al realizar debugging de la aplicacion*/
        //DataBaseHelper db = new DataBaseHelper(getBaseContext());
        //db.RestaurarEstadosSolicitudesTransmitidas();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE}, 0);
            //return;
        }
        //TODO seleccionar el tipo de solicitud por el UI
        //Intent i = new Intent(LoginActivity.this, EscanearActivity.class);
        //startActivityForResult(i,2);
        createNotificationChannel();
    }

    @Override
    protected  void onStart(){
        super.onStart();
        if(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","").equals("")){
            ruta_datos.setText("Sin Datos!");
        }else{
            ruta_datos.setText("Ruta Preventa "+PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","")+".");
        }
        SignInButton.setEnabled(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //Create an Intent Filter
        IntentFilter intentFilter = new IntentFilter("hsm.RECVRBI");
        myReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String d = intent.getStringExtra("data");
                if(d!=null && d.length()>0){
                    ruta_datos.setText(d);
                }
            }
        };
        this.registerReceiver(myReceiver, intentFilter);
        SignInButton.setEnabled(true);
    }
    @Override
    protected void onPause(){
        super.onPause();
        this.unregisterReceiver(this.myReceiver);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ABClientes";
            String description = "ABClientes";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ABClientes", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showDialogAcercade(Context context) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.acercade_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextView version = d.findViewById(R.id.version);
        final TextView ult_sinc = d.findViewById(R.id.ult_sinc);
        final TextView ult_trans = d.findViewById(R.id.ult_trans);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date buildDate = BuildConfig.BuildDate;

        version.setText("Versión: "+ BuildConfig.VERSION_NAME+" ("+dateFormat.format(buildDate)+")");
        ult_sinc.setText("Última sincronización: "+PreferenceManager.getDefaultSharedPreferences(context).getString("ultimaSincronizacion","No hay"));
        ult_trans.setText("Última transmisión: "+PreferenceManager.getDefaultSharedPreferences(context).getString("ultimaTransmision","No hay"));
        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Intenta iniciar sesión con el formulario de inicio de sesión.
     * Si hay errores de formulario (campos faltantes, usuario inexistente, etc.), el
     * Se presentan errores y no se realiza un intento de inicio de sesión real.
     */
    private void attemptLogin() {
        // Resetear Errores.
        mUserView.setError(null);
        mPasswordView.setError(null);
        // Guardar los valores de los campos en el momento del intento de login
        String user = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Checkear por el password correcto, si existe
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        boolean datosMinimos = DataBaseHelper.checkDataBase(getBaseContext());
        if(!datosMinimos){
            Toasty.error(getBaseContext(),"No hay base de datos para trabajar, debe sincronizar el dispositivo antes de ingresar.").show();
            SignInButton.setEnabled(true);
            return;
        }
        /*if(ruta_datos.getText().equals("Sin Datos!")){
            Toasty.error(getBaseContext(),"No hay datos para trabajar, debe sincronizar el dispositivo antes de ingresar.").show();
            return;
        }*/
        // Check for a valid user.
        DataBaseHelper db = new DataBaseHelper(getBaseContext());
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUserHHValid(user,db)) {
            mUserView.setError(getString(R.string.error_invalid_email));
            focusView = mUserView;
            cancel = true;
        }else if (!isUserMCValid(user,db)) {
            mUserView.setError(getString(R.string.error_invalid_user_mc));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // Hubo un error; no hacer login y focus el primer elemento con error
            // form field with an error.
            SignInButton.setEnabled(true);
            focusView.requestFocus();
        } else {
            //Realizar el login validando la base de datos sincronizada

            boolean intentovalido = db.LoginUsuario(user,password);

            if(intentovalido){
                String id_usuarioMC = VariablesGlobales.UsuarioHH2UsuarioMC(LoginActivity.this, mUserView.getText().toString());
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("user", mUserView.getText().toString()).apply();
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("userMC", id_usuarioMC).apply();
                String userName = db.getUserName(id_usuarioMC);
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("userName", userName).apply();
                if(mCheckbox.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("password", mPasswordView.getText().toString()).apply();
                }else{
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("password", "").apply();
                }
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean("guardar_contrasena", mCheckbox.isChecked()).apply();

                //Guardar las preferencias por ruta que tiene asignado el usuario logueado en la base de datos sincronizada.
                boolean tienePropiedades = db.setPropiedadesDeUsuario();
                if(tienePropiedades) {
                    intent = new Intent(getBaseContext(), PanelActivity.class);
                    startActivity(intent);
                }else{
                    SignInButton.setEnabled(true);
                    Toasty.warning(LoginActivity.this,"La informacion existente no pertenece al usuario o faltan datos en EX_T_RUTAS_VP!").show();
                }
            }else{
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                focusView = mPasswordView;
                //cancel = true;
                focusView.requestFocus();
                SignInButton.setEnabled(true);
            }
            //mAuthTask = new UserLoginTask(user, password);
            //mAuthTask.execute((Void) null);
        }
    }

    private boolean isUserHHValid(String user, DataBaseHelper db) {
        //Validacion del usuario
        return db.validarUsuarioHH(user);
    }

    private boolean isUserMCValid(String user, DataBaseHelper db) {
        //Validacion del usuario
        return db.validarUsuarioMC(user);
    }

    private boolean isPasswordValid(String password) {
        //Validacion del password
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        /*MenuItem item = menu.add(Menu.NONE, 1, 1, "Texto");
        item.setIcon(R.drawable.icon_solicitud);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/
        getMenuInflater().inflate(R.menu.menu_min, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.solicitud:
                Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud

                intent = new Intent(this,SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);
                break;
            case R.id.comunicacion:
                intent = new Intent(this,TCPActivity.class);
                startActivity(intent);
                break;
            case R.id.clientes:
                intent = new Intent(this,MantClienteActivity.class);
                startActivity(intent);
                break;
            case R.id.solicitudes:
                intent = new Intent(this,SolicitudesActivity.class);
                startActivity(intent);
                break;
            case R.id.coordenadas:
                intent = new Intent(this,LocacionGPSActivity.class);
                startActivity(intent);
                break;
            case R.id.firma:
                intent = new Intent(this,FirmaActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public class SincronizarLogin implements Runnable {
        private Context context;
        public SincronizarLogin(Context baseContext) {
            context = baseContext;
        }
        public void run() {
            Bundle b = new Bundle();
            //TODO seleccionar el tipo de solicitud por el UI
            b.putBoolean("deshabilitarTransmision", true); //id de solicitud
            intent = new Intent(LoginActivity.this, TCPActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    public class BorrarBaseDatos implements Runnable {
        private Context context;
        public BorrarBaseDatos(Context baseContext) {
            context = baseContext;
        }
        public void run() {
            DataBaseHelper.deleteDatabaseFile(context);
            Toasty.info(getBaseContext(),"Se han borrado los datos!! Debe sincronizar antes de ingresar!").show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String codigo = data.getStringExtra("codigo");
                    String cedula = codigo.substring(0, 9).trim();
                    String nombre = codigo.substring(61, 91).trim();
                    String apellido1 = codigo.substring(9, 35).trim();
                    String apellido2 = codigo.substring(35, 61).trim();
                    Toasty.info(getBaseContext(), cedula + " " + nombre + " " + apellido1 + " " + apellido2 + ".").show();
                }
            }
        }
    }
}

