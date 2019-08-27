package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.clases.PruebaConexionServidor;
import proyecto.app.clientesabc.clases.SincronizacionServidor;
import proyecto.app.clientesabc.clases.TransmisionServidor;

public class TCPActivity extends AppCompatActivity
{
    private Button serverTransmitButton;
    private Button clientReceiveButton;
    private Button serverUDPButton;
    private Button clientUDPButton;
    private Button probarConexionButton;
    private EditText ip_text;
    private EditText puerto_text;
    private EditText ruta_text;

    private int PICKFILE_REQUEST_CODE = 100;
    private String filePath="";
    private String wholePath="";
    private FloatingActionButton changeName;
    private String m_Text = "";

    private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }

        changeName = findViewById(R.id.change);
        ip_text = findViewById(R.id.txtservidor);
        ip_text.setText(VariablesGlobales.getIpcon());
        puerto_text = (EditText)findViewById(R.id.txtPuerto);
        puerto_text.setText(String.valueOf(VariablesGlobales.getPuertocon()));
        ruta_text = findViewById(R.id.txtRuta);
        ruta_text.setText(PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).getString("W_CTE_RUTAHH",""));

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        probarConexionButton = findViewById(R.id.button_probar_conexion);
        probarConexionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(validarConexion()) {
                    //Realizar una prueba de conexion para validar los datos ingresados
                    WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                    VariablesGlobales.setIpcon(ip_text.getText().toString());
                    VariablesGlobales.setPuertocon(Integer.valueOf(puerto_text.getText().toString()));

                    PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();
                    PruebaConexionServidor f = new PruebaConexionServidor(weakRef, weakRefA);
                    f.execute();
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
                        Log.i("Read Button Clicked", "yipee");
                        if(validarConexion()) {
                            //startService(new Intent(TCPActivity.this, NameService.class));
                            WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                            VariablesGlobales.setIpcon(ip_text.getText().toString());
                            VariablesGlobales.setPuertocon(Integer.valueOf(puerto_text.getText().toString()));
                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();
                            SincronizacionServidor s = new SincronizacionServidor(weakRef, weakRefA);
                            s.execute();
                        }
                        return true;
                    case R.id.action_transmitir:
                        Log.i("Start Server Clicked", "yipee");
                        if(validarConexion()) {
                            //Realizar la transmision de lo que se necesita (Db o txt)
                            WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                            VariablesGlobales.setIpcon(ip_text.getText().toString());
                            VariablesGlobales.setPuertocon(Integer.valueOf(puerto_text.getText().toString()));

                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();
                            TransmisionServidor f = new TransmisionServidor(weakRef, weakRefA, filePath, wholePath);
                            f.execute();
                        }
                }
                return true;
            }
        });
    }

    private boolean validarConexion(){
        boolean retorno = true;
        if(VariablesGlobales.getIpcon().trim().isEmpty()){
            Toasty.warning(getBaseContext(),"Por favor digite una direccion IP válida.");
            retorno = false;
        }
        if(String.valueOf(VariablesGlobales.getPuertocon()).isEmpty()){
            Toasty.warning(getBaseContext(),"Por favor digite un puerto válido.");
            retorno = false;
        }
        if(ruta_text.getText().toString().trim().isEmpty()){
            Toasty.warning(getBaseContext(),"Por favor digite una ruta de venta válida.");
            retorno = false;
        }
        return retorno;
    }

    private void showInputDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Conexion");
// Set up the input
        final EditText ip = new EditText(this);
        final EditText puerto = new EditText(this);
        final EditText ruta = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        LinearLayout LL_view = new LinearLayout(this);
        ip.setInputType(InputType.TYPE_CLASS_TEXT);
        LL_view.addView(ip);
        puerto.setInputType(InputType.TYPE_CLASS_NUMBER);
        LL_view.addView(puerto);
        ruta.setInputType(InputType.TYPE_CLASS_TEXT);
        LL_view.addView(ruta);
        builder.setView(LL_view);
// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO Poder guardar conexiones de diferentes tipos y poder seleccionarlo antes de conectar
                PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("ip", ip.getText().toString()).apply();
                PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("puerto", puerto.getText().toString()).apply();
                PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("ruta", ruta.getText().toString()).apply();
                Toasty.info(TCPActivity.this,"Se han guardado las preferencias de conexion",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
}
