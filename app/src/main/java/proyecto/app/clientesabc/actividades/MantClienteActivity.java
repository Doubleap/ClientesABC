package proyecto.app.clientesabc.actividades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
/*import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;*/

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.MovableFloatingActionButton;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.TransmisionEncuestaServidor;
import proyecto.app.clientesabc.modelos.EncuestaCabecera;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.ResumenCliente;
import proyecto.app.clientesabc.modelos.ResumenEncuestaCliente;


public class MantClienteActivity extends AppCompatActivity {
    Intent intent;
    private SearchView searchView;
    private MyAdapter mAdapter;
    private DataBaseHelper db;
    //private AidcManager manager;
    //private BarcodeReader reader;
    private MovableFloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    List<EncuestaCabecera> encuestaCabeceras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);
        RecyclerView rv = findViewById(R.id.user_list);

        long t0 = System.currentTimeMillis();

        db = new DataBaseHelper(this);
        asegurarIndices();
        Log.d("PERF", "DB helper: " + (System.currentTimeMillis() - t0));

        long t1 = System.currentTimeMillis();

        ArrayList<HashMap<String, String>> clientList = db.getClientes();

        Log.d("PERF", "getClientes: " + (System.currentTimeMillis() - t1));

        long t2 = System.currentTimeMillis();

        HashMap<String, ResumenCliente> resumenMap = db.getResumenCensoEquipoFrioPorCliente();

        Log.d("PERF", "getResumenCenso: " + (System.currentTimeMillis() - t2));

        long t3 = System.currentTimeMillis();

        ArrayList<EncuestaCabecera> encuestaCabecerasTemp = db.getEncuestasCabecera("");

        Log.d("PERF", "getEncuestasCabecera: " + (System.currentTimeMillis() - t3));

        long t4 = System.currentTimeMillis();

        HashMap<String, ResumenEncuestaCliente> resumenEncuestaMap = db.getResumenEncuestasPorCliente(
                clientList,
                encuestaCabecerasTemp
        );

        Log.d("PERF", "getResumenEncuestas: " + (System.currentTimeMillis() - t4));

        long t5 = System.currentTimeMillis();
        final boolean usaMonitorEquipoFrio = db.UsaMonitorEquipoFrio();
        final boolean usaIndirectos = db.UsaIndirectos();

        mAdapter = new MyAdapter(clientList, resumenMap, encuestaCabecerasTemp, resumenEncuestaMap,usaIndirectos,usaMonitorEquipoFrio);//mAdapter = new MyAdapter(clientList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);

        Log.d("PERF", "setAdapter: " + (System.currentTimeMillis() - t5));

        Log.d("PERF", "TOTAL onCreate load: " + (System.currentTimeMillis() - t0));
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        fab1 = findViewById(R.id.filterBtn);
        fab2 = findViewById(R.id.addBtn);
        fab1.hide();fab2.hide();
        fab = findViewById(R.id.fabBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(db.UsaIndirectos()) {
                    mostrarDialogoSeleccionTipoCliente(MantClienteActivity.this, VariablesGlobales.getSociedad());
                }else {
                    view.setEnabled(false);
                    Bundle b = new Bundle();
                    //TODO seleccionar el tipo de solicitud por el UI
                    b.putString("tipoSolicitud", "1"); //id de solicitud
                    Intent intent = new Intent(view.getContext(),SolicitudActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    startActivity(intent);
                }
            }
        });

        /**/
        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Mis Clientes ("+clientList.size()+")");
        //toolbar.setSubtitle("");
        toolbar.setBackground(d);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white,null));
        if (Build.VERSION.SDK_INT >= 28) {
            toolbar.setOutlineAmbientShadowColor(getResources().getColor(R.color.aprobados,null));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                        intent = new Intent(getBaseContext(), TCPActivity.class);
                        /*PreferenceManager.getDefaultSharedPreferences(MantClienteActivity.this).getString("tipo_conexion","").equals("api")
                        if(!PreferenceManager.getDefaultSharedPreferences(MantClienteActivity.this).getString("tipo_conexion","").equals("api")) {
                            intent = new Intent(getBaseContext(), TCPActivity.class);
                        }else{
                            intent = new Intent(getBaseContext(), APIConfigActivity.class);
                        }*/
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
                    case R.id.enviar_encuestas_pendientes:
                        if(db.getValidacionEncuestaPendiente()){
                            WeakReference<Context> weakRef = new WeakReference<Context>(MantClienteActivity.this);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(MantClienteActivity.this);
                            TransmisionEncuestaServidor f = new TransmisionEncuestaServidor(weakRef,weakRefA,"",true);
                            if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("tipo_conexion", "").equals("wifi")) {
                                f.EnableWiFi();
                            } else {
                                f.DisableWiFi();
                            }
                            f.execute();
                        }else{
                            Toasty.info(getBaseContext(),"No hay encuestas pendientes.").show();
                        }

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

                return false;
            }
        });
        /**/

    }
    private void asegurarIndices() {
        try {
            db.getWritableDatabase().execSQL(
                    "CREATE INDEX IF NOT EXISTS idx_sapdbaseinstalada_kunnr ON SAPDBaseInstalada(kunnr)"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView rv = findViewById(R.id.user_list);
        ArrayList<HashMap<String, String>> clientList = db.getClientes();
        HashMap<String, ResumenCliente> resumenMap = db.getResumenCensoEquipoFrioPorCliente();
        ArrayList<EncuestaCabecera> encuestaCabecerasTemp = db.getEncuestasCabecera("");
        HashMap<String, ResumenEncuestaCliente> resumenEncuestaMap = db.getResumenEncuestasPorCliente(
                clientList,
                encuestaCabecerasTemp
        );
        final boolean usaMonitorEquipoFrio = db.UsaMonitorEquipoFrio();
        final boolean usaIndirectos = db.UsaIndirectos();
        mAdapter = new MyAdapter(clientList, resumenMap, encuestaCabecerasTemp, resumenEncuestaMap,usaIndirectos,usaMonitorEquipoFrio);
        //mAdapter = new MyAdapter(clientList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);

        FloatingActionButton fab = findViewById(R.id.fabBtn);
        if (fab != null) {
            fab.setEnabled(true);
        }
        actualizarEncuestaDialog();
        if (searchView != null && searchView.getQuery() != null) {
            mAdapter.getFilter().filter(searchView.getQuery());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null) {
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setQueryHint("Buscar...");

            // listener de buscar query text change
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    mAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // filter recycler view when text is changed
                    mAdapter.getFilter().filter(query);
                    return false;
                }
            });
            searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            TextView textView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            ImageView searchBtn = searchView.findViewById(androidx.appcompat.R.id.search_button);
            ImageView searchCloseBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            textView.setTextColor(getResources().getColor(R.color.white,null));
            searchBtn.setColorFilter(getResources().getColor(R.color.white,null));
            searchCloseBtn.setColorFilter(getResources().getColor(R.color.white,null));
        }
        return true;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  implements Filterable {
        private ArrayList<HashMap<String, String>> mDataset;
        private ArrayList<HashMap<String, String>> formListFiltered;
        private HashMap<String, ResumenCliente> resumenMap;
        private ArrayList<EncuestaCabecera> encuestasMap;
        private HashMap<String, ResumenEncuestaCliente> resumenEncuestaMap;
        private boolean usaIndirectos;
        private boolean usaMonitorEquipoFrio;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View listView;
            private MyViewHolder(View v) {
                super(v);
                listView = v;
            }
        }

        // Constructor de Adaptador HashMap
        private MyAdapter(Object myDataset, HashMap<String, ResumenCliente> resumenMap, ArrayList<EncuestaCabecera> encuestasMap, HashMap<String, ResumenEncuestaCliente> resumenEncuestaMap, boolean usaIndirectos, boolean usaMonitorEquipoFrio) {
            mDataset = (ArrayList<HashMap<String, String>>) myDataset;
            this.resumenMap = resumenMap;
            this.encuestasMap = encuestasMap;
            this.resumenEncuestaMap = resumenEncuestaMap;
            this.usaIndirectos = usaIndirectos;
            this.usaMonitorEquipoFrio = usaMonitorEquipoFrio;
            formListFiltered = mDataset;
        }

        // Crear nuevas Views
        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // New View creada
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mant_clientes_item, parent, false);
            return new MyViewHolder(v);
        }

        // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            long t0 = System.currentTimeMillis();
            ColorStateList colorStateListOk = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(getApplicationContext().getResources().getString(R.color.sinFormularios))
                    }
            );
            ColorStateList colorStateListAprobado = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(getApplicationContext().getResources().getString(R.color.aprobados))
                    }
            );
            ColorStateList colorStateListRechazado = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(getApplicationContext().getResources().getString(R.color.rechazado))
                    }
            );
            ColorStateList colorStateListHallazgo = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(getApplicationContext().getResources().getString(R.color.modificado))
                    }
            );
            ColorStateList colorStateListAlerta = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(getApplicationContext().getResources().getString(R.color.devuelto))
                    }
            );
            ColorStateList colorStateListDefault = new ColorStateList(
                    new int[][]{
                            new int[]{}
                    },
                    new int[]{
                            Color.parseColor(getApplicationContext().getResources().getString(R.color.black))
                    }
            );
            // - Obtener Elemento del data set en esta position
            // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset

            TextView codigo = holder.listView.findViewById(R.id.textViewHead);
            codigo.setText(formListFiltered.get(position).get("codigo") == null?"":formListFiltered.get(position).get("codigo").trim());
            TextView nombre = holder.listView.findViewById(R.id.textViewDesc);
            nombre.setText(formListFiltered.get(position).get("nombre") == null?"":formListFiltered.get(position).get("nombre").trim());
            ImageView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
            TextView idfiscal = holder.listView.findViewById(R.id.idfiscal);
            idfiscal.setText(formListFiltered.get(position).get("idfiscal") == null?"":formListFiltered.get(position).get("idfiscal").trim());
            TextView correo = holder.listView.findViewById(R.id.correo);
            correo.setText(formListFiltered.get(position).get("correo") == null?"":formListFiltered.get(position).get("correo").trim());
            TextView indirecto = holder.listView.findViewById(R.id.indirecto);
            final String tipo_canal = formListFiltered.get(position).get("tipo_canal") != null?formListFiltered.get(position).get("tipo_canal"):"";
            if(db.UsaIndirectos() && tipo_canal.equals("60")){
                indirecto.setVisibility(View.VISIBLE);
            }else{
                indirecto.setVisibility(View.INVISIBLE);
            }

            com.rey.material.widget.LinearLayout  credito_preaprobado_layout = (com.rey.material.widget.LinearLayout)holder.listView.findViewById(R.id.credito_preaprobado_layout);
            final String cupoPreaprobado = formListFiltered.get(position).get("cupo") == null?"":formListFiltered.get(position).get("cupo").trim();

            if(cupoPreaprobado != null && credito_preaprobado_layout != null){
                credito_preaprobado_layout.setVisibility(View.VISIBLE);
                ImageView  imagen_credito_preaprobado = (ImageView)holder.listView.findViewById(R.id.imagen_credito_preaprobado);
                if(imagen_credito_preaprobado != null){
                    imagen_credito_preaprobado.setTooltipText("Crédito Pre-Aprobado por "+cupoPreaprobado+".");
                }
            }else if (credito_preaprobado_layout != null){
                credito_preaprobado_layout.setVisibility(View.GONE);
            }


            final String codigoCliente = codigo.getText().toString().trim();
            final String nombreCliente = nombre.getText().toString().trim();
            final String canalCliente = formListFiltered.get(position).get("canal").trim();
            final String correoCliente = correo.getText().toString().trim();


            ResumenCliente resumen = resumenMap.get(codigoCliente);
            if (resumen == null) {
                resumen = new ResumenCliente();
            }

            int cantVerificados = resumen.cantVerificados;
            int cantHallazgos = resumen.cantHallazgos;
            int cantAnomalias = resumen.cantAnomalias;
            int cantAlertas = resumen.cantAlertas;
            /*int cantVerificados = db.CantidadVerificados(codigoCliente);
            int cantHallazgos = db.CantidadHallazgos(codigoCliente);
            int cantAnomalias = db.CantidadAnomalias(codigoCliente);
            int cantAlertas = db.CantidadAlertas(codigoCliente);*/

            if(formListFiltered.get(holder.getAdapterPosition()).get("correo") == null || formListFiltered.get(position).get("correo").equals("")){
                //correo.setVisibility(View.GONE);
            }else{
                correo.setVisibility(View.VISIBLE);
            }
            TextView razonSocial = holder.listView.findViewById(R.id.textRazonSocial);
            razonSocial.setText(formListFiltered.get(position).get("razonSocial") == null?"":formListFiltered.get(position).get("razonSocial").trim());
            if(formListFiltered.get(holder.getAdapterPosition()).get("razonSocial") == null){
                razonSocial.setVisibility(View.GONE);
            }else{
                razonSocial.setVisibility(View.VISIBLE);
            }
            com.rey.material.widget.LinearLayout  base_instalada_layout = (com.rey.material.widget.LinearLayout)holder.listView.findViewById(R.id.base_instalada_layout);
            ImageView imagen_base_instalada = (ImageView)holder.listView.findViewById(R.id.imagen_base_instalada);
            imagen_base_instalada.setBackgroundTintList(colorStateListDefault);
            TextView label_cantidad_base_instalada = (TextView)holder.listView.findViewById(R.id.label_cantidad_base_instalada);
            if(formListFiltered.get(holder.getAdapterPosition()).get("cant_base_instalada") == null || formListFiltered.get(holder.getAdapterPosition()).get("cant_base_instalada").equals("") || formListFiltered.get(holder.getAdapterPosition()).get("cant_base_instalada").equals("0")){
                base_instalada_layout.setVisibility(View.GONE);
            }else{
                base_instalada_layout.setVisibility(View.VISIBLE);
                label_cantidad_base_instalada.setText(formListFiltered.get(position).get("cant_base_instalada"));
                imagen_base_instalada.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bc = new Bundle();
                        bc.putString("codigo_cliente", codigoCliente);
                        bc.putString("nombre_cliente", nombreCliente);
                        bc.putString("canal_cliente", canalCliente);
                        bc.putString("correo_cliente", correoCliente);
                        intent = new Intent(getApplicationContext(),BaseInstaladaActivity.class);
                        intent.putExtras(bc); //Pase el parametro el Intent
                        startActivity(intent);
                    }
                });
                FloatingActionButton cantidad_alertas = holder.listView.findViewById(R.id.cantidad_alertas);

                if(Integer.parseInt(formListFiltered.get(position).get("cant_base_instalada")) == cantVerificados){
                    cantidad_alertas.setBackgroundTintList(colorStateListAprobado);
                    cantidad_alertas.setTooltipText("Todos los equipos han sido verificados");
                }
                if(Integer.parseInt(formListFiltered.get(position).get("cant_base_instalada")) > cantVerificados && cantVerificados == 0){
                    cantidad_alertas.setBackgroundTintList(colorStateListRechazado);
                    cantidad_alertas.setTooltipText("No se ha verificado ningun equipo");
                }
                if(Integer.parseInt(formListFiltered.get(position).get("cant_base_instalada")) > cantVerificados && cantVerificados > 0){
                    cantidad_alertas.setBackgroundTintList(colorStateListOk);
                    cantidad_alertas.setTooltipText("Falta al menos 1 equipo para verificar");
                }
                if(cantHallazgos > 0){
                    imagen_base_instalada.setBackgroundTintList(colorStateListHallazgo);
                    imagen_base_instalada.setTooltipText("Existe al menos 1 hallazgo en este cliente!");
                }
                if(cantAlertas > 0){
                    imagen_base_instalada.setBackgroundTintList(colorStateListAlerta);
                    imagen_base_instalada.setTooltipText("Existe al menos 1 alerta en este cliente!");
                }
                if(cantAnomalias > 0){
                    imagen_base_instalada.setBackgroundTintList(colorStateListRechazado);
                    imagen_base_instalada.setTooltipText("Existe al menos 1 anomalía en este cliente!");
                }
            }

            //ENCUESTA GEC
            long encuestaCreada=0;

            encuestaCabeceras = encuestasMap;

            ResumenEncuestaCliente resumenEncuesta = resumenEncuestaMap.get(codigoCliente);
            int pendientes = 0;
            boolean pendienteTransferir = false;
            if (resumenEncuesta != null) {
                pendientes = resumenEncuesta.pendientes;
                pendienteTransferir = resumenEncuesta.pendienteTransferir;
            }
            /*int pendientes =0;
            boolean pendienteTransferir = false;
            for(EncuestaCabecera encuestaCabecera : encuestaCabeceras){
                if(!db.getValidacionEncuestaClientePendiente(String.valueOf(encuestaCabecera.getId()),codigoCliente)){
                    pendientes++;
                }
                String GUIDactual = db.getGUIDEncuestaActualCliente(String.valueOf(encuestaCabecera.getId()),codigoCliente);
                if(GUIDactual.length()>0){
                    String estado= db.getEstadoRespuestaEnviada(GUIDactual);
                    if (estado!=null && estado.equals("pendiente")){
                        pendienteTransferir=true;
                    }
                }
            }*/
            com.rey.material.widget.LinearLayout  encuesta_gec_layout = (com.rey.material.widget.LinearLayout)holder.listView.findViewById(R.id.encuesta_gec_layout);
            ImageView imagen_encuesta_gec = (ImageView)holder.listView.findViewById(R.id.imagen_encuesta_gec);
            TextView label_cantidad_encuestas_pendientes= (TextView) holder.listView.findViewById(R.id.label_cantidad_encuestas_pendientes);
            if(pendientes>0){
                imagen_encuesta_gec.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black,null)));
                label_cantidad_encuestas_pendientes.setText(String.valueOf(pendientes));
                label_cantidad_encuestas_pendientes.setVisibility(View.VISIBLE);
                FloatingActionButton cantidad_encuestas_pendientes= (FloatingActionButton) holder.listView.findViewById(R.id.cantidad_encuestas_pendientes);
                cantidad_encuestas_pendientes.setVisibility(View.VISIBLE);
            }else{
                imagen_encuesta_gec.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.aprobados,null)));
                label_cantidad_encuestas_pendientes.setVisibility(View.GONE);
                FloatingActionButton cantidad_encuestas_pendientes= (FloatingActionButton) holder.listView.findViewById(R.id.cantidad_encuestas_pendientes);
                cantidad_encuestas_pendientes.setVisibility(View.GONE);
                if(encuestaCabeceras.size() == 0)
                    imagen_encuesta_gec.setVisibility(View.GONE);
            }
            if(pendienteTransferir){
                ImageView warningPendientes= holder.listView.findViewById(R.id.warningPendientes);
                warningPendientes.setVisibility(View.VISIBLE);
                imagen_encuesta_gec.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorBackground,null)));
            }else{
                ImageView warningPendientes= holder.listView.findViewById(R.id.warningPendientes);
                warningPendientes.setVisibility(View.INVISIBLE);
            }

            imagen_encuesta_gec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bc = new Bundle();
                    bc.putString("codigo_cliente", codigoCliente);
                    bc.putString("nombre_cliente", nombreCliente);
                    bc.putString("canal_cliente", canalCliente);
                    bc.putString("correo_cliente", correoCliente);
                    bc.putString("tipo_encuesta","GVC");
                    EncuestaCabeceraDialog listDialog = new EncuestaCabeceraDialog(getBaseContext(),MantClienteActivity.this, encuestaCabeceras);
                    // Remove previous instance if exists
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment prev = fm.findFragmentByTag("EncuestaCabeceraDialog");
                    if (prev != null) {
                        fm.beginTransaction().remove(prev).commit();
                    }
                    // Show the dialog
                    listDialog.setArguments(bc);
                    listDialog.show(fm,"EncuestaCabeceraDialog");
                }
            });
            //ENCUESTA GEC



            com.rey.material.widget.LinearLayout  puertas_por_instalar_layout = (com.rey.material.widget.LinearLayout)holder.listView.findViewById(R.id.puertas_por_instalar_layout);
            ImageView imagen_puertas_por_instalar = (ImageView)holder.listView.findViewById(R.id.imagen_puertas_por_instalar);
            TextView label_cantidad_puertas_por_instalar = (TextView)holder.listView.findViewById(R.id.label_cantidad_puertas_por_instalar);
            if((formListFiltered.get(holder.getAdapterPosition()).get("puertas_por_instalar") == null || formListFiltered.get(holder.getAdapterPosition()).get("puertas_por_instalar").equals(""))){
                puertas_por_instalar_layout.setVisibility(View.GONE);
            }else if(usaMonitorEquipoFrio){
                puertas_por_instalar_layout.setVisibility(View.VISIBLE);
                Integer puertas = formListFiltered.get(position).get("puertas_por_instalar") != null ? Integer.parseInt(formListFiltered.get(position).get("puertas_por_instalar").toString()) : 0;
                label_cantidad_puertas_por_instalar.setText(puertas.toString());
                FloatingActionButton cantidad_puertas_por_instalar =  (FloatingActionButton)holder.listView.findViewById(R.id.cantidad_puertas_por_instalar);


                if(puertas == 0){
                    cantidad_puertas_por_instalar.setBackgroundTintList(colorStateListOk);
                    cantidad_puertas_por_instalar.setTooltipText("Base instalada igual al sugerido");
                }else if(puertas > 0){
                    cantidad_puertas_por_instalar.setBackgroundTintList(colorStateListAprobado);
                    cantidad_puertas_por_instalar.setTooltipText("Oportunidad para instalar equipo en el cliente");
                }else if(puertas < 0){
                    cantidad_puertas_por_instalar.setBackgroundTintList(colorStateListRechazado);
                    cantidad_puertas_por_instalar.setTooltipText("Oportunidad para retirar equipo no productivo en el cliente");
                }

                imagen_puertas_por_instalar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toasty.info(v.getContext(),"Ingresar a ver datos del cliente de monitor de equipo frio").show();
                        Bundle bc = new Bundle();
                        bc.putString("codigo_cliente", codigoCliente);
                        bc.putString("nombre_cliente", nombreCliente);
                        bc.putString("canal_cliente", canalCliente);
                        bc.putString("correo_cliente", correoCliente);
                        intent = new Intent(getApplicationContext(),MonitorEquipoFrioActivity.class);
                        intent.putExtras(bc); //Pase el parametro el Intent
                        startActivity(intent);
                    }
                });
            }else{//no usa monitor
                label_cantidad_puertas_por_instalar.setVisibility(View.GONE);
                imagen_puertas_por_instalar.setVisibility(View.GONE);
            }

            /*TextView ubicacion = holder.listView.findViewById(R.id.ubicacion);
            ubicacion.setText(formListFiltered.get(position).get("ubicacion"));
            TextView direccion = holder.listView.findViewById(R.id.direccion);
            direccion.setText(formListFiltered.get(position).get("direccion"));*/
            LinearLayout color_gec = holder.listView.findViewById(R.id.color_gec);
            Drawable d = getResources().getDrawable(R.drawable.circulo_status_cliente, null);

            Drawable background = color_gec.getBackground();
            int color = R.color.sinFormularios;
            String klabc = formListFiltered.get(position).get("klabc").toString();
            final String latitud = formListFiltered.get(position).get("latitud").toString();
            final String longitud = formListFiltered.get(position).get("longitud").toString();



            switch(klabc) {
                case "00":
                    color = R.color.baja;break;
                case "50":
                    color = R.color.diamante;break;
                case "51":
                    color = R.color.oro;break;
                case "52":
                    color = R.color.plata;break;
                case "53":
                    color = R.color.bronce;break;
                case "54":
                    color = R.color.indirectos;break;
                case "55":
                    color = R.color.orovending;break;
                case "56":
                    color = R.color.platavending;break;
                case "57":
                    color = R.color.broncevending;break;
                case "58":
                    color = R.color.laton;break;
                case "59":
                    color = R.color.plataplus;break;
                case "99":
                    color = R.color.customizado;break;
                case "SA":
                    color = R.color.sinasignar;break;
            }
            color_gec.setBackground(ContextCompat.getDrawable(getBaseContext(), color));


            codigo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle cc = new Bundle();
                    cc.putString("tipoSolicitud", getResources().getString(R.string.ID_FORM_CONSULTA_CLIENTE)); //id de solicitud
                    cc.putString("codigoCliente", codigoCliente);
                    intent = new Intent(getApplicationContext(), ConsultaClienteTotalActivity.class);
                    intent.putExtras(cc); //Pase el parametro el Intent
                    startActivity(intent);
                }
            });
            textViewOptions.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onClick(final View view) {

                    Context wrapper = new ContextThemeWrapper(getApplication(), R.style.MyPopupMenu);

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(wrapper, holder.listView);
                    popup.setGravity(Gravity.END);

                    //inflating menu from xml resource
                    popup.inflate(R.menu.mant_clientes_item_menu);
                    //adding click listener
                    if(!db.ExistenFormulariosCredito()){
                        MenuItem menuItem = (MenuItem)popup.getMenu().getItem(3).setVisible(false);
                    }
                    if(!db.ExistenFormulariosEquipoFrio() || !db.AccesoEquipoFrioLibre()){
                        MenuItem menuItem = (MenuItem)popup.getMenu().getItem(4).setVisible(false);
                    }
                    if(!db.ExistenFormulariosRacks()){
                        MenuItem menuItem = (MenuItem)popup.getMenu().getItem(5).setVisible(false);
                    }
                    if(!db.ExistenIniciativas()){
                        MenuItem menuItem = (MenuItem)popup.getMenu().getItem(6).setVisible(false);
                    }
                    if(cupoPreaprobado != null){

                    }

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.detalle:
                                    /*Bundle b = new Bundle();
                                    b.putString("idCliente", codigoCliente); //id de solicitud
                                    Intent intent = new Intent(getBaseContext(),ConsultaClienteTotalActivity.class);
                                    intent.putExtras(b); //Pase el parametro el Intent
                                    startActivity(intent);*/
                                    Bundle cc = new Bundle();
                                    cc.putString("tipoSolicitud", getResources().getString(R.string.ID_FORM_CONSULTA_CLIENTE)); //id de solicitud
                                    cc.putString("codigoCliente", codigoCliente);
                                    intent = new Intent(getApplicationContext(), ConsultaClienteTotalActivity.class);
                                    intent.putExtras(cc); //Pase el parametro el Intent
                                    startActivity(intent);
                                    break;
                                case R.id.modificar:
                                    if(tipo_canal.equals("60"))
                                        showDialogFormulariosModificacion(codigoCliente,false, false, false, true);
                                    else
                                        showDialogFormulariosModificacion(codigoCliente,false, false, false, false);
                                    //Toasty.info(getBaseContext(),"Funcionalidad de Modificaciones NO disponible de momento.").show();
                                    break;
                                case R.id.cierre:
                                    Bundle bc = new Bundle();
                                    if(usaIndirectos && tipo_canal.equals("60")) {
                                        bc.putString("tipoSolicitud", "504"); //id de solicitud
                                    }else{
                                        bc.putString("tipoSolicitud", "5"); //id de solicitud
                                    }
                                    bc.putString("codigoCliente", codigoCliente);
                                    intent = new Intent(getApplicationContext(), SolicitudModificacionActivity.class);
                                    intent.putExtras(bc); //Pase el parametro el Intent
                                    startActivity(intent);
                                    //Toasty.info(getBaseContext(),"Funcionalidad de Cierre NO disponible de momento.").show();
                                    break;
                                case R.id.credito:
                                    if(tipo_canal.equals("60"))
                                        showDialogFormulariosModificacion(codigoCliente,true, false, false, true);
                                    else
                                        showDialogFormulariosModificacion(codigoCliente,true, false, false, false);
                                    //Toasty.info(getBaseContext(),"Funcionalidad de Credito NO disponible de momento.").show();
                                    break;
                                case R.id.equipofrio:
                                    //EQUIPO FRIO
                                    showDialogFormulariosModificacion(codigoCliente,false, true, false, false);
                                    //Toasty.info(getBaseContext(),"Funcionalidad de Avisos de equipo frio NO disponible de momento.").show();
                                    break;
                                case R.id.racks:
                                    //EQUIPO FRIO
                                    showDialogFormulariosRacks(codigoCliente);
                                    //Toasty.info(getBaseContext(),"Funcionalidad de Avisos de equipo frio NO disponible de momento.").show();
                                    break;
                                case R.id.iniciativas:
                                    //INICIATIVAS, estos formularios son locales y nunca van a SAP
                                    showDialogFormulariosModificacion(codigoCliente,false, false, true, false);
                                    break;
                                case R.id.comollegar:
                                    String uri = "";
                                    if(Float.parseFloat(latitud) > 30f || Float.parseFloat(latitud) < 0f) {
                                        uri = "geo:" + longitud + ","
                                                + latitud + "?q=" + longitud
                                                + "," + latitud;
                                    }else{
                                        uri = "geo:" + latitud + ","
                                                + longitud + "?q=" + latitud
                                                + "," + longitud;
                                    }
                                    try {
                                        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                                    }catch(ActivityNotFoundException e){
                                        Toasty.warning(getBaseContext(),"No se encontró una aplicacion GPS para abrir las coordenadas.").show();
                                    }
                                    break;
                                case R.id.baseinstalada:
                                    bc = new Bundle();
                                    bc.putString("codigo_cliente", codigoCliente);
                                    bc.putString("nombre_cliente", nombreCliente);
                                    bc.putString("canal_cliente", canalCliente);
                                    bc.putString("correo_cliente", correoCliente);
                                    intent = new Intent(getApplicationContext(),BaseInstaladaActivity.class);
                                    intent.putExtras(bc); //Pase el parametro el Intent
                                    startActivity(intent);
                                    break;
                            }
                            return false;
                        }
                    });

                    //MenuPopupHelper menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) popup.getMenu(), holder.listView);
                    //menuHelper.setForceShowIcon(true);
                    //menuHelper.show();
                    if (popup.getMenu() instanceof MenuBuilder) {
                        ((MenuBuilder) popup.getMenu()).setOptionalIconsVisible(true);
                    }
                    //displaying the popup
                    popup.show();

                }
            });

            /*View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView codigo = v.findViewById(R.id.codigo);
                    Toast.makeText(getApplicationContext(), "Cliente "+codigo.getText()+" seleccionado",Toast.LENGTH_SHORT).show();
                }
            };
            holder.listView.setOnClickListener(mOnClickListener);*/

            long elapsed = System.currentTimeMillis() - t0;

            if (elapsed > 20) {
                Log.d("PERF_BIND", "position " + position + " took " + elapsed + " ms");
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return formListFiltered.size();
        }

        //Para filtrar busquedas segun criterios
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        formListFiltered = mDataset;
                    } else {
                        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();
                        for (HashMap<String, String> row : mDataset) {
                            if (row.get("codigo").trim().contains(charString) || row.get("nombre").toUpperCase().trim().contains(charString.toUpperCase()) || row.get("direccion").trim().contains(charString)) {
                                filteredList.add(row);
                            }
                        }
                        formListFiltered = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = formListFiltered;
                    return filterResults;
                }
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    formListFiltered = (ArrayList<HashMap<String, String>>) filterResults.values;
                    // refresh the list with filtered data
                    notifyDataSetChanged();
                }
            };
        }
    }

    private void showDialogFormulariosModificacion(final String codigoCliente, boolean credito, boolean equipofrio, boolean iniciativas, boolean indirecto) {
        ArrayList<HashMap<String,String>> formulariosPermitidos = null;
        if(credito) {
            if(db.UsaIndirectos() && indirecto){
                formulariosPermitidos = db.getModificacionesCreditoIndirectosPermitidas();
                if(formulariosPermitidos == null || formulariosPermitidos.size() == 0){
                    Toasty.info(getBaseContext(),"No se ha configurado ningun formulario de crédito clientes Indirecto para HH.").show();
                    return;
                }
            }else {
                formulariosPermitidos = db.getModificacionesCreditoPermitidas();
                if(formulariosPermitidos == null || formulariosPermitidos.size() == 0){
                    Toasty.info(getBaseContext(),"No se ha configurado ningun formulario de crédito para HH.").show();
                    return;
                }
            }
        }else if(equipofrio) {
            formulariosPermitidos = db.getOrdenesServicioPermitidas();
            if(formulariosPermitidos == null || formulariosPermitidos.size() == 0){
                Toasty.info(getBaseContext(),"No se ha configurado ningun formulario de Equipo frio para HH.").show();
                return;
            }
        }else if(iniciativas) {
            formulariosPermitidos = db.getIniciativasLocalesPermitidas();
            if(formulariosPermitidos == null || formulariosPermitidos.size() == 0){
                Toasty.info(getBaseContext(),"No se ha configurado ningun formulario de Iniciativas para HH.").show();
                return;
            }
        }else {
            if(db.UsaIndirectos() && indirecto){
                formulariosPermitidos = db.getModificacionesIndirectosPermitidas();
                if (formulariosPermitidos == null || formulariosPermitidos.size() == 0) {
                    Toasty.info(getBaseContext(), "No se ha configurado ningun formulario de modificación Cliente Indirecto para HH.").show();
                    return;
                }
            }else {
                formulariosPermitidos = db.getModificacionesPermitidas();
                if (formulariosPermitidos == null || formulariosPermitidos.size() == 0) {
                    Toasty.info(getBaseContext(), "No se ha configurado ningun formulario de modificación para HH.").show();
                    return;
                }
            }
        }

        String[] idformsTemp = new String[formulariosPermitidos.size()];
        String[] formsTemp = new String[formulariosPermitidos.size()];
        for(int x=0; x < formulariosPermitidos.size(); x++){
            idformsTemp[x] = formulariosPermitidos.get(x).get("idform");
            formsTemp[x] = formulariosPermitidos.get(x).get("descripcion");
        }
        final String[] idforms = idformsTemp;
        final String[] forms = formsTemp;
        ContextThemeWrapper cw = new ContextThemeWrapper( this, R.style.AlertDialogTheme );
        final AlertDialog.Builder builder = new AlertDialog.Builder(cw);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.titlebar, null);
        builder.setCustomTitle(view);
        builder.setSingleChoiceItems(forms, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                //ListView lw = ((AlertDialog)dialog).getListView();
                //Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
            }

        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Solo para crearlo
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Sobreescribir handler de click de boton positivo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // user clicked OK, so save the mSelectedItems results somewhere
                // or return them to the component that opened the dialog
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if(selectedPosition < 0){
                    Toasty.warning(getBaseContext(),"Debe seleccionar el tipo de modificación!").show();
                }else {
                    if (forms[selectedPosition].toLowerCase().contains("credito") || forms[selectedPosition].toLowerCase().contains("crédito") || credito) {
                        dialog.dismiss();
                        Bundle b = new Bundle();
                        b.putString("tipoSolicitud", idforms[selectedPosition]); //id de solicitud
                        b.putString("codigoCliente", codigoCliente);
                        intent = new Intent(getApplicationContext(), SolicitudCreditoActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                    } else if(forms[selectedPosition].toLowerCase().contains("eq.") || forms[selectedPosition].toLowerCase().contains("frio") || forms[selectedPosition].toLowerCase().contains("equipo")) {
                        if(forms[selectedPosition].toLowerCase().contains("instal")) {//Si es de instalacion no ocupa numero de maquina de equipo frio
                            dialog.dismiss();
                            Bundle b = new Bundle();
                            b.putString("tipoSolicitud", idforms[selectedPosition]); //id de solicitud
                            b.putString("codigoCliente", codigoCliente);
                            b.putString("monitor", "0");
                            b.putString("numPuertas", "0");
                            intent = new Intent(getApplicationContext(), SolicitudAvisosEquipoFrioActivity.class);
                            intent.putExtras(b); //Pase el parametro el Intent
                            startActivity(intent);
                        }else{//Si NO es de instalacion se ocupa digitar o leer el equipo frio al que se le va a hacer el aviso.
                            //TODO
                            dialog.dismiss();
                            displayDialogSeleccionarEquipoFrio(getApplicationContext(),idforms[selectedPosition],codigoCliente);
                        }
                    }
                    else {
                        dialog.dismiss();
                        Bundle b = new Bundle();
                        b.putString("tipoSolicitud", idforms[selectedPosition]); //id de solicitud
                        b.putString("codigoCliente", codigoCliente);
                        intent = new Intent(getApplicationContext(), SolicitudModificacionActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void showDialogFormulariosRacks(final String codigoCliente) {
        ArrayList<HashMap<String,String>> formulariosPermitidos = null;

        formulariosPermitidos = db.getFormulariosRacksPermitidos();

        String[] idformsTemp = new String[formulariosPermitidos.size()];
        String[] formsTemp = new String[formulariosPermitidos.size()];
        for(int x=0; x < formulariosPermitidos.size(); x++){
            idformsTemp[x] = formulariosPermitidos.get(x).get("idform");
            formsTemp[x] = formulariosPermitidos.get(x).get("descripcion");
        }
        final String[] idforms = idformsTemp;
        final String[] forms = formsTemp;
        ContextThemeWrapper cw = new ContextThemeWrapper( this, R.style.AlertDialogTheme );
        final AlertDialog.Builder builder = new AlertDialog.Builder(cw);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.titlebar, null);
        builder.setCustomTitle(view);
        builder.setSingleChoiceItems(forms, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                //ListView lw = ((AlertDialog)dialog).getListView();
                //Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
            }

        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Solo para crearlo
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Sobreescribir handler de click de boton positivo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // user clicked OK, so save the mSelectedItems results somewhere
                // or return them to the component that opened the dialog
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if(selectedPosition < 0){
                    Toasty.warning(getBaseContext(),"Debe seleccionar el tipo de modificación!").show();
                }else {
                        dialog.dismiss();
                        Bundle b = new Bundle();
                        b.putString("tipoSolicitud", idforms[selectedPosition]); //id de solicitud
                        b.putString("codigoCliente", codigoCliente);
                        intent = new Intent(getApplicationContext(), SolicitudRacksActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);

                }
            }
        });
    }

    public void displayDialogSeleccionarEquipoFrio(Context context, final String tipoSolicitud, final String codigoCliente) {
        ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("sapDBaseInstalada",8,12,15, "kunnr='"+codigoCliente+"'");
        if(opciones.size() == 1){
            Toasty.warning(context, "El cliente no tiene equipo frio asignado!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog d=new Dialog(this);
        d.setContentView(R.layout.seleccionar_equipo_frio_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final SearchableSpinner equipoFrioSpinner = (SearchableSpinner) d.findViewById(R.id.equipoFrioSpinner);
        equipoFrioSpinner.setTitle("Seleccione un equipo");
        equipoFrioSpinner.setPositiveButton("Cerrar");
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(0, -10, 0, 25);
        equipoFrioSpinner.setPadding(0,0,0,0);
        equipoFrioSpinner.setLayoutParams(lp);
        equipoFrioSpinner.setPopupBackgroundResource(R.drawable.menu_item);
        Button saveBtn= d.findViewById(R.id.saveBtn);

        //SAVE, en este caso solo es aceptar, ir a a pintar el formulario correspondiente dependiendo del equipo frio seleccionado
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigoEquipoFrio = ((OpcionSpinner)equipoFrioSpinner.getSelectedItem()).getId();
                if(codigoEquipoFrio.isEmpty()){
                    Toasty.warning(v.getContext(), "Por favor seleccione un equipo frio!", Toast.LENGTH_SHORT).show();
                }
                try{
                    d.dismiss();
                    Bundle b = new Bundle();
                    b.putString("tipoSolicitud", tipoSolicitud);
                    b.putString("codigoCliente", codigoCliente);
                    b.putString("codigoEquipoFrio", codigoEquipoFrio);
                    b.putString("monitor", "0");
                    b.putString("numPuertas", "0");
                    intent = new Intent(getApplicationContext(), SolicitudAvisosEquipoFrioActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    startActivity(intent);
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo abrir la solicitud de aviso."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Para campos de seleccion de equipo frio del cliente para mantenimiento, cierre, retiro o cambio

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        for (int j = 0; j < opciones.size(); j++){
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = this.getResources().getDrawable(R.drawable.spinner_background, null);
        equipoFrioSpinner.setBackground(spinner_back);
        equipoFrioSpinner.setAdapter(dataAdapter);
        equipoFrioSpinner.setSelection(0);

        //Scanner
        /*AidcManager.create(this, new AidcManager.CreatedCallback() {
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                try {
                    reader = manager.createBarcodeReader();
                    reader.setProperty(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
                    reader.setProperty(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
                    BarcodeReader.BarcodeListener barcodeListener = new BarcodeReader.BarcodeListener() {
                        @Override
                        public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
                            // update UI to reflect the data
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //barcodeReadEvent.getAimId().substring(1,2).equals("L")
                                    if(true) {//Lectura a placa de equipo frio
                                        String lecturaEquipoFrio = barcodeReadEvent.getBarcodeData();
                                        try {
                                            reader.softwareTrigger(false);
                                            //Revisar si el codigo de equipo leido existe y es del cliente
                                            EquipoFrio equipo = db.getEquipoFrioDB(codigoCliente,lecturaEquipoFrio,false);
                                            if(equipo != null) {
                                                Toasty.info(getBaseContext(), "Código " + lecturaEquipoFrio + " leido!", Toast.LENGTH_SHORT).show();
                                                Bundle b = new Bundle();
                                                b.putString("tipoSolicitud", tipoSolicitud);
                                                b.putString("codigoCliente", codigoCliente);
                                                b.putString("codigoEquipoFrio", equipo.getEqunr().trim());
                                                b.putString("monitor", "0");
                                                b.putString("numPuertas", "0");
                                                intent = new Intent(getApplicationContext(), SolicitudAvisosEquipoFrioActivity.class);
                                                intent.putExtras(b); //Pase el parametro el Intent
                                                startActivity(intent);
                                            }else{
                                                Toasty.error(getBaseContext(), "Código " + lecturaEquipoFrio + " no existe para el cliente.", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (ScannerNotClaimedException e) {
                                            e.printStackTrace();
                                        } catch (ScannerUnavailableException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            });
                        }
                        @Override
                        public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {
                            //Toasty.warning(getBaseContext(), "no se leyó el código", Toast.LENGTH_SHORT).show();
                        }
                    };
                    reader.addBarcodeListener(barcodeListener);
                } catch (InvalidScannerNameException e) {
                    e.printStackTrace();
                } catch (UnsupportedPropertyException e) {
                    e.printStackTrace();
                }
            }
        });

        d.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if ((keyCode == KeyEvent.KEYCODE_UNKNOWN)) {
                        if(event.getAction() == KeyEvent.ACTION_DOWN){
                            if(reader != null && event.getRepeatCount() == 0) {
                                try {
                                    reader.claim();
                                    reader.aim(true);
                                    reader.light(true);
                                    reader.decode(true);
                                } catch (ScannerNotClaimedException e) {
                                    e.printStackTrace();
                                } catch (ScannerUnavailableException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(event.getAction() == KeyEvent.ACTION_UP){
                            if(reader != null) {
                                try {
                                    reader.aim(false);
                                    reader.light(false);
                                    reader.decode(false);
                                    reader.release();
                                } catch (ScannerNotClaimedException e) {
                                    e.printStackTrace();
                                } catch (ScannerUnavailableException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        return true;
                    }
                    return false;
            }
        });*/

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private void mostrarDialogoSeleccionTipoCliente(Context context, String pais) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.titlebar, null);
        TextView titulo = view.findViewById(R.id.title);
        titulo.setText("Nuevo Cliente");
        builder.setCustomTitle(view);

        String[] opciones = {"Mercado Abierto", "Indirecto"};

        builder.setSingleChoiceItems(opciones, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                //ListView lw = ((AlertDialog)dialog).getListView();
                //Object checkedItem = lw.getAdapter().getItem(lw.getCheckedItemPosition());
            }

        });

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Solo para crearlo
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        //Sobreescribir handler de click de boton positivo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // user clicked OK, so save the mSelectedItems results somewhere
                // or return them to the component that opened the dialog
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                if(selectedPosition < 0){
                    Toasty.warning(getBaseContext(),"Debe seleccionar el tipo de cliente!").show();
                }else {
                    dialog.dismiss();
                    if (selectedPosition == 0) {
                        abrirSolicitud("1");
                    }
                    else {
                        abrirSolicitud("501");
                    }
                }
            }
        });
    }
    private void abrirSolicitud(String tipoSolicitud) {
        Bundle b = new Bundle();
        b.putString("tipoSolicitud", tipoSolicitud);

        Intent intent = new Intent(getApplicationContext(), SolicitudActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }
    public void actualizarEncuestaDialog() {
        EncuestaCabeceraDialog fragment = new EncuestaCabeceraDialog();
        fragment = (EncuestaCabeceraDialog) getSupportFragmentManager().findFragmentByTag("EncuestaCabeceraDialog");

        // Remove previous instance if exists
        FragmentManager fm = getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("EncuestaCabeceraDialog");
        if (prev != null) {
            fm.beginTransaction().remove(prev).commit();
        }

        if(fragment != null){
            // ok, we got the fragment instance, but should we manipulate its view?
            //fragment.dismiss();
            for (EncuestaCabecera encuestaCabecera:  encuestaCabeceras) {
                if(encuestaCabecera.isGvc()){

                }
            }

            fragment.show(fm,"EncuestaCabeceraDialog");
        }
    }
}
