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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
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
import proyecto.app.clientesabc.adaptadores.ConexionTableAdapter;
import proyecto.app.clientesabc.clases.PruebaConexionServidor;
import proyecto.app.clientesabc.clases.SincronizacionServidor;
import proyecto.app.clientesabc.clases.TransmisionServidor;
import proyecto.app.clientesabc.modelos.Conexion;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

import static android.support.design.widget.TabLayout.GRAVITY_CENTER;

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
    private AppCompatButton addConexion;
    private String m_Text = "";

    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Conexion> tv_conexiones;
    private static ArrayList<Conexion> list_conexiones;

    private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    /** Se llama cuando la actividad es creada por primera vez */
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

        addConexion = findViewById(R.id.add_conexion);
        ip_text = findViewById(R.id.txtservidor);
        ip_text.setText(PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).getString("Ip",""));
        puerto_text = (EditText)findViewById(R.id.txtPuerto);
        puerto_text.setText(PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).getString("Puerto",""));
        ruta_text = findViewById(R.id.txtRuta);
        ruta_text.setText(PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).getString("W_CTE_RUTAHH",""));
        tv_conexiones = findViewById(R.id.tv_conexiones);
        tv_conexiones.setColumnCount(3);
        tv_conexiones.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
        tv_conexiones.setHeaderElevation(2);
        int height = 75;
        list_conexiones = getConexionesFromSharedPreferences();

        if(list_conexiones != null) {
            //ip_text.setText(list_conexiones.get(0).getIp());

            ConexionTableAdapter stda = new ConexionTableAdapter(this, list_conexiones);
            stda.setPaddings(5, 20, 5, 20);
            stda.setGravity(GRAVITY_CENTER);
            tv_conexiones.setDataAdapter(stda);
            //tv_conexiones.getLayoutParams().height = tv_conexiones.getLayoutParams().height + (list_conexiones.size() * (75));

            String[] headers = ((ConexionTableAdapter)tv_conexiones.getDataAdapter()).getHeaders();
            SimpleTableHeaderAdapter sta = new SimpleTableHeaderAdapter(this, headers);
            sta.setPaddings(10,5,10,5);
            sta.setTextSize(12);
            sta.setTextColor(getResources().getColor(R.color.white,null));
            sta.setTypeface(Typeface.BOLD);
            sta.setGravity(GRAVITY_CENTER);

            tv_conexiones.setHeaderAdapter(sta);
            tv_conexiones.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(getResources().getColor(R.color.white,null), getResources().getColor(R.color.backColor,null)));
            tv_conexiones.addDataClickListener(new TCPActivity.ConexionClickListener());
            tv_conexiones.addDataLongClickListener(new TCPActivity.ConexionLongClickListener());
        }
        addConexion.setOnClickListener(new View.OnClickListener() {
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
                    PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("Ip",ip_text.getText().toString()).apply();
                    PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("Puerto",puerto_text.getText().toString()).apply();
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
                        if(validarConexion()) {
                            //startService(new Intent(TCPActivity.this, NameService.class));
                            WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("Ip",ip_text.getText().toString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("Puerto",puerto_text.getText().toString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("W_CTE_RUTAHH",ruta_text.getText().toString()).apply();
                            SincronizacionServidor s = new SincronizacionServidor(weakRef, weakRefA);
                            s.execute();
                        }
                        return true;
                    case R.id.action_transmitir:
                        if(validarConexion()) {
                            //Realizar la transmision de lo que se necesita (Db o txt)
                            WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("Ip",ip_text.getText().toString()).apply();
                            PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("Puerto",puerto_text.getText().toString()).apply();
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
        if(ip_text.getText().toString().trim().isEmpty()){
            Toasty.warning(getBaseContext(),"Por favor digite una direccion IP válida.");
            retorno = false;
        }
        if(puerto_text.getText().toString().trim().isEmpty()){
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
        final Dialog d=new Dialog(this);
        d.setContentView(R.layout.new_conexion_layout);

        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
// Set up the input
        final Spinner tipo = d.findViewById(R.id.tipoSpinner);
        final EditText ip = d.findViewById(R.id.ipEditTxt);
        final EditText puerto = d.findViewById(R.id.puertoTxt);
        final Button saveBtn = d.findViewById(R.id.saveBtn);
        //final EditText ruta = findViewById(R.id.rutaTxt);

        ArrayList<OpcionSpinner> listatipos = new ArrayList<>();
        OpcionSpinner opWifi = new OpcionSpinner("wifi","WiFi");
        OpcionSpinner opGPRS = new OpcionSpinner("gprs","GPRS");
        //OpcionSpinner opLocal = new OpcionSpinner("local","Local");
        listatipos.add(opWifi);
        listatipos.add(opGPRS);
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, listatipos);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = this.getResources().getDrawable(R.drawable.spinner_underlined, null);
        tipo.setBackground(spinner_back);
        tipo.setAdapter(dataAdapter);

// Set up the buttons
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ip.getText().toString().trim().isEmpty() && !puerto.getText().toString().trim().isEmpty() && !tipo.getSelectedItem().toString().trim().isEmpty()) {
                    Conexion nuevaConexion = new Conexion();
                    nuevaConexion.setIp(ip.getText().toString());
                    nuevaConexion.setPuerto(puerto.getText().toString());
                    nuevaConexion.setTipo(tipo.getSelectedItem().toString());
                    AgregarNuevaConexion(nuevaConexion);
                    tv_conexiones.setDataAdapter(new ConexionTableAdapter(v.getContext(), list_conexiones));
                    //tv_conexiones.getLayoutParams().height = tv_conexiones.getLayoutParams().height + (75);
                    d.dismiss();
                    Toasty.info(TCPActivity.this, "Se han guardado las preferencias de conexion", Toast.LENGTH_SHORT).show();
                }else{
                    Toasty.warning(TCPActivity.this, "Todos los campos son obligatorios para la conexión.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        d.show();
    }

    //Conexiones de preferencia
    private ArrayList<Conexion> getConexionesFromSharedPreferences(){
        ArrayList<Conexion> productFromShared = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TCPActivity.this);
        String jsonPreferences = sharedPref.getString("Conexiones", "");

        Type type = new TypeToken<ArrayList<Conexion>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }

    private void setConexionesFromSharedPreferences(Conexion curConexion){
        Gson gson = new Gson();
        String jsonCurProduct = gson.toJson(curConexion);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TCPActivity.this);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("Conexiones", jsonCurProduct);
        editor.commit();
    }

    private void AgregarNuevaConexion(Conexion conexion){
        list_conexiones.add(conexion);
        //tv_conexiones.getLayoutParams().height = tv_conexiones.getLayoutParams().height+(75);
        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TCPActivity.this);

        String jsonSaved = sharedPref.getString("Conexiones", "");
        String jsonNewproductToAdd = gson.toJson(conexion);

        JSONArray jsonArrayProduct= new JSONArray();
        try {
            if(jsonSaved.length()!=0){
                jsonArrayProduct = new JSONArray(jsonSaved);
            }
            jsonArrayProduct.put(new JSONObject(jsonNewproductToAdd));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //SAVE NEW ARRAY
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Conexiones", jsonArrayProduct.toString());
        editor.commit();
    }

    private class ConexionClickListener implements TableDataClickListener<Conexion> {
        @Override
        public void onDataClicked(int rowIndex, Conexion seleccionado) {
            String salida = seleccionado.getIp() + ":" + seleccionado.getPuerto()+" ha sido seleccionado.";
            ip_text.setText(seleccionado.getIp());
            puerto_text.setText(seleccionado.getPuerto());
            Toasty.success(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
        }
    }
    //Borrar el registro en el longClick
    private class ConexionLongClickListener implements TableDataLongClickListener<Conexion> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Conexion seleccionado) {
            Gson gson = new Gson();
            String salida = seleccionado.getIp() + ":" + seleccionado.getPuerto()+" ha sido eliminado.";
            list_conexiones = getConexionesFromSharedPreferences();
            list_conexiones.remove(rowIndex);
            tv_conexiones.setDataAdapter(new ConexionTableAdapter(getBaseContext(), list_conexiones));


            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TCPActivity.this);
            String jsonSaved = sharedPref.getString("Conexiones", "");

            JSONArray jsonArrayProduct= new JSONArray();
            try {
                if(jsonSaved.length()!=0){
                    jsonArrayProduct = new JSONArray(jsonSaved);
                }
                jsonArrayProduct.remove(rowIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //SAVE NEW ARRAY
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("Conexiones", jsonArrayProduct.toString());
            editor.commit();
            //tv_conexiones.getLayoutParams().height = tv_conexiones.getLayoutParams().height-(75);
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
