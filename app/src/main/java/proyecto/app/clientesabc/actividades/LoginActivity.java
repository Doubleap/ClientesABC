package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;

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
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                    case R.id.solicitud:
                        Bundle b = new Bundle();
                        //TODO seleccionar el tipo de solicitud por el UI
                        b.putString("tipoSolicitud", "1"); //id de solicitud
                        intent = new Intent(getBaseContext(),SolicitudActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                        break;
                    case R.id.comunicacion:
                        intent = new Intent(getBaseContext(),TCPActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.clientes:
                        intent = new Intent(getBaseContext(),MantClienteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.solicitudes:
                        intent = new Intent(getBaseContext(),SolicitudesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.coordenadas:
                        intent = new Intent(getBaseContext(),LocacionGPSActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.firma:
                        intent = new Intent(getBaseContext(),FirmaActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.detalles:
                        intent = new Intent(getBaseContext(), PanelActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        Toasty.info(getBaseContext(),"Opcion no encontrada!").show();
                }

                /*Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud

                intent = new Intent(getBaseContext(),SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);*/
                return false;
            }
        });
        /**/

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

        // Check for a valid user.
        if (TextUtils.isEmpty(user)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        } else if (!isUserValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_email));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // Hubo un error; no hacer login y focus el primer elemento con error
            // form field with an error.
            focusView.requestFocus();
        } else {
            //Realizar el login validando la base de datos sincronizada
            DataBaseHelper db = new DataBaseHelper(getBaseContext());
            boolean datosMinimos = db.checkDataBase();
            boolean intentovalido = db.LoginUsuario(user,password);

            if(!datosMinimos){
                Toasty.error(getBaseContext(),"Debe sincronizar el dispositivo antes de ingresar.").show();
                return;
                //intent = new Intent(getBaseContext(),TCPActivity.class);
                //startActivity(intent);
            }
            if(intentovalido){

                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString("user", mUserView.getText().toString()).apply();
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
                    Toasty.warning(LoginActivity.this,"Debe Sincronizar los datos primero. La informacion existente no pertenece al usuario!").show();
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

    private boolean isUserValid(String user) {
        //Validacion del usuario
        DataBaseHelper db = new DataBaseHelper(getBaseContext());
        return db.validarUsuario(user);
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

}

