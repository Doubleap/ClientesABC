package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.DialogHandler;

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

    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private CheckBox mCheckbox;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView femsa_logo;
    private TextView ruta_datos;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        femsa_logo = findViewById(R.id.femsa_logo);
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
        if(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","").equals("")){
            ruta_datos.setText("Sin Datos!");
        }else{
            ruta_datos.setText("Ruta Preventa "+PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","")+".");
        }


        Button mEmailSignInButton = findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        /**/
        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        toolbar.setTitle("Maestro Clientes Femsa CR");
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

    }

    @Override
    protected  void onStart(){
        super.onStart();
        if(PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","").equals("")){
            ruta_datos.setText("Sin Datos!");
        }else{
            ruta_datos.setText("Ruta Preventa "+PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString("W_CTE_RUTAHH","")+".");
        }
    }

    private void showDialogAcercade(Context context) {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.acercade_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextView version = d.findViewById(R.id.version);
        final TextView ult_sinc = d.findViewById(R.id.ult_sinc);
        final TextView ult_trans = d.findViewById(R.id.ult_trans);

        version.setText("Versión: "+VariablesGlobales.getVersion());
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
                    Toasty.warning(LoginActivity.this,"Debe Sincronizar los datos antes de continuar. La informacion existente no pertenece al usuario!").show();
                }
            }else{
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                focusView = mPasswordView;
                //cancel = true;
                focusView.requestFocus();
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
            case R.id.detalles:
                intent = new Intent(this,MainActivity.class);
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
}

