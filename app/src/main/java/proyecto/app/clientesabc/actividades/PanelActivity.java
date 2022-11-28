package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.SincronizacionAPI;
import proyecto.app.clientesabc.clases.SincronizacionServidor;
import proyecto.app.clientesabc.clases.SingleClickListener;
import proyecto.app.clientesabc.clases.TransmisionAPI;
import proyecto.app.clientesabc.clases.TransmisionServidor;

public class PanelActivity extends AppCompatActivity {
    LinearLayout principal;
    GridLayout gridLayout;
    private Intent intent;
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    private TextView versionPanel;
    private TextView rutaPanel;
    private TextView userPanel;
    private TextView userName;
    Spinner spinner;
    private TextView num_nuevos;
    private TextView num_pendientes;
    private TextView num_incidencias;
    private TextView num_aprobados;
    private TextView num_rechazados;
    private TextView num_incompletos;
    private TextView num_modificados;
    private TextView num_total;
    private FloatingActionButton resumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel2);

        mDBHelper = new DataBaseHelper(this);
        //mDb = mDBHelper.getWritableDatabase();
        principal = (LinearLayout)findViewById(R.id.principal);
        gridLayout = (GridLayout)findViewById(R.id.mainGrid);
        versionPanel = findViewById(R.id.versionPanel);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date buildDate = BuildConfig.BuildDate;
        versionPanel.setText("Versión: "+ BuildConfig.VERSION_NAME+" ("+dateFormat.format(buildDate)+")");
        rutaPanel = findViewById(R.id.rutaPanel);
        rutaPanel.setText(PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","").trim().toUpperCase());
        userPanel = findViewById(R.id.userPanel);
        userPanel.setText(VariablesGlobales.UsuarioHH2UsuarioMC(PanelActivity.this, PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("user","")).trim().toUpperCase() );
        userName = findViewById(R.id.userName);
        userName.setText(PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("userName",""));
        num_nuevos = findViewById(R.id.num_nuevos);
        num_pendientes = findViewById(R.id.num_pendientes);
        num_incidencias = findViewById(R.id.num_incidencias);
        num_aprobados = findViewById(R.id.num_aprobados);
        num_rechazados = findViewById(R.id.num_rechazados);
        num_incompletos = findViewById(R.id.num_incompletos);
        num_modificados = findViewById(R.id.num_modificados);
        num_total = findViewById(R.id.num_total);
        resumen = findViewById(R.id.resumen);
        resumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getBaseContext(),TipoSolicitudPanelActivity.class);
                startActivity(intent);
            }
        });
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) principal.getLayoutParams();
        lp.setMargins(0,0,0,0);
        principal.setLayoutParams(lp);


        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_panel);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                item.setEnabled(false);
                switch (item.getItemId()) {
                    case R.id.action_solicitudes:
                        intent = new Intent(getBaseContext(), SolicitudesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_nuevo_cliente:
                        item.setEnabled(false);
                        Bundle b = new Bundle();
                        b.putString("tipoSolicitud", "1"); //id de solicitud
                        intent = new Intent(getApplicationContext(), SolicitudActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                        break;
                    case R.id.action_clientes:
                        intent = new Intent(getBaseContext(), MantClienteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.action_sincronizar:
                        item.setEnabled(false);
                        //Realizar la transmision de lo que se necesita (Db o txt)
                        WeakReference<Context> weakRefs = new WeakReference<Context>(PanelActivity.this);
                        WeakReference<Activity> weakRefAs = new WeakReference<Activity>(PanelActivity.this);
                        //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
                        if (VariablesGlobales.UsarAPI()) {
                            SincronizacionAPI s = new SincronizacionAPI(weakRefs, weakRefAs);
                            if (PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                s.EnableWiFi();
                            }
                            s.execute();
                        } else {
                            SincronizacionServidor s = new SincronizacionServidor(weakRefs, weakRefAs);
                            if (PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                s.EnableWiFi();
                            } else {
                                s.DisableWiFi();
                            }
                            s.execute();
                        }
                        break;
                    case R.id.action_transmitir:
                        item.setEnabled(false);
                        //if(validarConexion()) {
                        //Realizar la transmision de lo que se necesita (Db o txt)
                        WeakReference<Context> weakRef = new WeakReference<Context>(PanelActivity.this);
                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(PanelActivity.this);

                        if (VariablesGlobales.UsarAPI()) {
                            TransmisionAPI f = new TransmisionAPI(weakRef, weakRefA, "", "", "");
                            if (PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                f.EnableWiFi();
                            }
                            f.execute();
                        } else {
                            TransmisionServidor f = new TransmisionServidor(weakRef, weakRefA, "", "", "");
                            if (PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                f.EnableWiFi();
                            } else {
                                f.DisableWiFi();
                            }
                            f.execute();
                        }
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
        toolbar.setTitle(PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","")+" - "+PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("user",""));
        toolbar.setSubtitle(PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("userName","Dato No encontrado"));
        toolbar.setBackground(d);
        toolbar.setVisibility(View.GONE);
        toolbar.setLayoutParams(new AppBarLayout.LayoutParams(0,0));
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
                        if(!VariablesGlobales.UsarAPI()) {
                            intent = new Intent(getBaseContext(), TCPActivity.class);
                        }else{
                            intent = new Intent(getBaseContext(), APIConfigActivity.class);
                        }
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
                    case R.id.panel_alternativo:
                        intent = new Intent(getBaseContext(),TipoSolicitudPanelActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        Toasty.info(getBaseContext(),"Opcion no encontrada!").show();
                }
                return false;
            }
        });
    }

    @Override
    protected  void onResume(){
        super.onResume();
        num_nuevos.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Nuevo")));
        num_pendientes.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Pendiente")));
        num_incidencias.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Incidencia")));
        num_aprobados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Aprobado")));
        num_rechazados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Rechazado")));
        num_incompletos.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Incompleto")));
        num_modificados.setText(String.valueOf(mDBHelper.CantidadSolicitudes("Modificado")));
        num_total.setText(String.valueOf(mDBHelper.CantidadSolicitudesTotal()));
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_panel);
        for(int x = 0; x < bottomNavigation.getMenu().size(); x++){
            bottomNavigation.getMenu().getItem(x).setEnabled(true);
        }

    }
    // we are setting onClickListener for each element
    private void setSingleEvent(GridLayout gridLayout) {

        for(int i = 0; i<gridLayout.getChildCount();i++) {
            try {
                CardView cardView = (CardView) gridLayout.getChildAt(i);
                final int finalI = i;
                cardView.setOnClickListener(new SingleClickListener() {
                    @Override
                    public void performClick(View view) {
                        switch (finalI) {
                            case 0:
                                VerSolicitudes("Nuevo");
                                break;
                            case 1:
                                VerSolicitudes("Pendiente");
                                break;
                            case 2:
                                VerSolicitudes("Rechazado");
                                break;
                            case 3:
                                VerSolicitudes("Aprobado");
                                break;
                            case 4:
                                VerSolicitudes("Incidencia");
                                break;
                            case 5:
                                VerSolicitudes("Modificado");
                                break;
                            case 6:
                                VerSolicitudes("Incompleto");
                                break;
                            case 7:
                                VerSolicitudes();
                                break;
                        }
                    }
                });
            }catch(Exception e){}
        }
    }
    public void VerSolicitudes() {
        intent = new Intent(this, SolicitudesActivity.class);
        startActivity(intent);
    }
    public void VerSolicitudes(String estado) {
        Bundle b = new Bundle();
        b.putString("estado", estado.trim()); //id de solicitud
        intent = new Intent(this, SolicitudesActivity.class);
        intent.putExtras(b); //Pase el parametro el Intent
        startActivity(intent);
    }
}
