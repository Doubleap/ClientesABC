package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Objects;

import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.R;

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
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "1:1", "9999:9999"
    };

    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = findViewById(R.id.user);

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

        Button mEmailSignInButton = findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(d);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            DataBaseHelper db = new DataBaseHelper(getBaseContext());
            boolean intentovalido = db.LoginUsuario(user,password);

            if(intentovalido){
                intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
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
        //TODO: Cualquier validacion al usuario
        DataBaseHelper db = new DataBaseHelper(getBaseContext());
        return db.validarUsuario(user);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Cualquier validacion al password
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

