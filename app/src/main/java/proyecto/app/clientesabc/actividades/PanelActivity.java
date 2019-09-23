package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.TransmisionServidor;

public class PanelActivity extends AppCompatActivity {
    LinearLayout principal;
    GridLayout gridLayout;
    private Intent intent;
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    private TextView rutaPanel;
    private TextView userPanel;
    Spinner spinner;
    private TextView num_nuevos;
    private TextView num_pendientes;
    private TextView num_incidencias;
    private TextView num_aprobados;
    private TextView num_rechazados;
    private TextView num_transmitidos;
    private TextView num_cancelados;
    private TextView num_modificados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel2);

        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();
        //principal = (LinearLayout)findViewById(R.id.principal);
        gridLayout = (GridLayout)findViewById(R.id.mainGrid);
        rutaPanel = findViewById(R.id.rutaPanel);
        rutaPanel.setText(PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH",""));
        userPanel = findViewById(R.id.userPanel);
        userPanel.setText(PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("user",""));
        num_nuevos = findViewById(R.id.num_nuevos);
        num_pendientes = findViewById(R.id.num_pendientes);
        num_incidencias = findViewById(R.id.num_incidencias);
        num_aprobados = findViewById(R.id.num_aprobados);
        num_rechazados = findViewById(R.id.num_rechazados);
        num_transmitidos = findViewById(R.id.num_transmitidos);
        num_cancelados = findViewById(R.id.num_cancelados);
        num_modificados = findViewById(R.id.num_modificados);

        /*spinner = new Spinner(this, Spinner.MODE_DIALOG);
        ArrayList<OpcionSpinner> listaopciones = mDBHelper.getDatosCatalogoParaSpinner("cat_T171T");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<OpcionSpinner>(Objects.requireNonNull(getBaseContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d1 = getResources().getDrawable(R.drawable.spinner_background, null);
        spinner.setBackground(d1);
        spinner.setAdapter(dataAdapter);
        spinner.setVisibility(View.GONE);
        principal.addView(spinner);*/

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_panel);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_solicitudes:
                        intent = new Intent(getBaseContext(),SolicitudesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_nuevo_cliente:
                        Bundle b = new Bundle();
                        b.putString("tipoSolicitud", "1"); //id de solicitud
                        intent = new Intent(getApplicationContext(),SolicitudActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                        break;
                    case R.id.action_clientes:
                        intent = new Intent(getBaseContext(),MantClienteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_transmitir:
                        //if(validarConexion()) {
                            //Realizar la transmision de lo que se necesita (Db o txt)
                            WeakReference<Context> weakRef = new WeakReference<Context>(PanelActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(PanelActivity.this);
                            //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
                            TransmisionServidor f = new TransmisionServidor(weakRef, weakRefA, "", "");
                            f.execute();
                        //}
                        break;
                }
                return true;
            }
        });

        setSingleEvent(gridLayout);
        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        //Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(d);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Monitor de Solicitudes");
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
                        intent = new Intent(getBaseContext(),MainActivity.class);
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
    }

    @Override
    protected  void onStart(){
        super.onStart();
        num_nuevos.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Nuevo")));
        num_pendientes.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Pendiente")));
        num_incidencias.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Incidencia")));
        num_aprobados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Aprobado")));
        num_rechazados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Rechazado")));
        num_transmitidos.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Transmitido")));
        num_cancelados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Cancelado")));
        num_modificados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Modificado")));
    }
    // we are setting onClickListener for each element
    private void setSingleEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch(finalI) {
                        case 0:
                            VerSolicitudes("Nuevo");
                            break;
                        case 1:
                            VerSolicitudes("Pendiente");
                            break;
                        case 2:
                            VerSolicitudes("Incidencia");
                            break;
                        case 3:
                            VerSolicitudes("Aprobado");
                            break;
                        case 4:
                            VerSolicitudes("Rechazado");
                            break;
                        case 5:
                            VerSolicitudes("Transmitido");
                            break;
                        case 6:
                            VerSolicitudes("Cancelado");
                            break;
                        case 7:
                            VerSolicitudes("Modificado");
                            break;
                    }
                }
            });
        }
    }

    private void showDialogSolicitudes() {

    }

    public void VerSolicitudes(String estado) {
        Bundle b = new Bundle();
        b.putString("estado", estado.trim()); //id de solicitud
        intent = new Intent(this, SolicitudesActivity.class);
        intent.putExtras(b); //Pase el parametro el Intent
        startActivity(intent);
    }
}
