package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.AppCompatEditText;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonArray;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.BaseInstaladaAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.KeyPairBoolData;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.MovableFloatingActionButton;
import proyecto.app.clientesabc.clases.MultiSpinnerListener;
import proyecto.app.clientesabc.clases.MultiSpinnerSearch;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoAPI;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoServidor;
import proyecto.app.clientesabc.clases.ValidacionAnomaliaServidor;
import proyecto.app.clientesabc.clases.Validaciones;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class MonitorEquipoFrioActivity extends AppCompatActivity implements LocacionGPSActivity.LocationListenerCallback{
    DataBaseHelper db;
    public static SQLiteDatabase mDb;
    private RecyclerView recyclerView;
    private static BaseInstaladaAdapter mAdapter;
    LocacionGPSActivity locationServices;
    double latitude = 0.0;
    double longitude = 0.0;
    private SearchView searchView;
    String codigo_cliente;
    String nombre_cliente;
    String canal_cliente;
    String correo_cliente;
    ArrayList<HashMap<String, String>> formList;
    Toolbar toolbar;
    private AppCompatTextView pais;
    private AppCompatTextView estado;
    private AppCompatTextView prioridad;
    private AppCompatTextView gec;
    private AppCompatTextView tipo_canal;
    private AppCompatTextView venta_actual;
    private AppCompatTextView puertas_sugeridas;
    private AppCompatTextView puertas_instaladas;
    private AppCompatTextView puertas_objetivo;
    private AppCompatTextView puertas_por_instalar;
    private AppCompatTextView venta_necesaria;
    private TextView etiqueta_venta_necesaria;
    private AppCompatEditText txt_num_puertas;
    private BottomNavigationView bottomNavigation;

    private HalfGauge halfGauge;

    public void calcularVenta() {
        Double venta_comprometida = 0.0;
        venta_comprometida = (Double.parseDouble(txt_num_puertas.getText().toString().replaceAll("[A-Z]",""))*Double.parseDouble(formList.get(0).get("cajas_monitor_ef").replaceAll("[A-Z]",""))) - Double.parseDouble(venta_actual.getText().toString().replaceAll("[A-Z]",""));
        venta_necesaria.setText(String.format(Locale.US, "%.2f", venta_comprometida)+" "+getResources().getString(R.string.unidad_caja_monitor)+"");
        if(venta_comprometida >= 0){
            etiqueta_venta_necesaria.setText("Faltante");
        }else{
            etiqueta_venta_necesaria.setText("Sobrante");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.detalle);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            codigo_cliente = b.getString("codigo_cliente");
            nombre_cliente = b.getString("nombre_cliente");
            canal_cliente = b.getString("canal_cliente");
            correo_cliente = b.getString("correo_cliente");
        }
        db = new DataBaseHelper(this);

        db = new DataBaseHelper(this);
        mDb = db.getWritableDatabase();

        formList = db.getDatosVistaMonitorEquipoFrioDB(codigo_cliente);
        setContentView(R.layout.activity_monitor_equipo_frio);

        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(codigo_cliente +" - "+nombre_cliente);
        toolbar.setSubtitle("Monitor Equipo Frio");
        toolbar.setTitleTextAppearance(this,R.style.Toolbar_TitleText);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorTextView,null));
        toolbar.setBackground(d);
        if (Build.VERSION.SDK_INT >= 28) {
            toolbar.setOutlineAmbientShadowColor(getResources().getColor(R.color.aprobados,null));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        /*Llenar los campos del layout con la lista de datos sacado ed la vista con el dbHelper*/
        pais = findViewById(R.id.pais);
        estado = findViewById(R.id.estado);
        prioridad = findViewById(R.id.prioridad);
        gec = findViewById(R.id.gec);
        tipo_canal = findViewById(R.id.tipo_canal);
        venta_actual = findViewById(R.id.venta_actual);
        puertas_sugeridas = findViewById(R.id.puertas_sugeridas);
        puertas_instaladas = findViewById(R.id.puertas_instaladas);
        puertas_objetivo = findViewById(R.id.puertas_objetivo);
        puertas_por_instalar = findViewById(R.id.puertas_por_instalar);
        txt_num_puertas = findViewById(R.id.txt_num_puertas);
        venta_necesaria = findViewById(R.id.venta_necesaria);
        etiqueta_venta_necesaria = findViewById(R.id.etiqueta_venta_necesaria);
        halfGauge = findViewById(R.id.halfGauge);

        pais.setText(formList.get(0).get("pais").toString());
        estado.setText(formList.get(0).get("estado").toString());
        prioridad.setText(formList.get(0).get("prioridad").toString() +" - "+formList.get(0).get("desc_prioridad").toString());
        gec.setText(formList.get(0).get("desc_gec").toString() +" ("+formList.get(0).get("cajas_monitor_ef").toString()+" "+getResources().getString(R.string.unidad_caja_monitor)+")");
        tipo_canal.setText(formList.get(0).get("desc_tipo_canal").toString());
        venta_actual.setText(formList.get(0).get("venta_actual").toString()+" "+getResources().getString(R.string.unidad_caja_monitor)+"");
        txt_num_puertas.setText(formList.get(0).get("puertas_objetivo").toString());

        txt_num_puertas.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    calcularVenta();
                }
            }
        });
        calcularVenta();
        puertas_sugeridas.setText(formList.get(0).get("puertas_sugeridas").toString());
        puertas_instaladas.setText(formList.get(0).get("puertas_instaladas").toString());
        puertas_objetivo.setText(formList.get(0).get("puertas_objetivo").toString());
        puertas_por_instalar.setText(formList.get(0).get("puertas_por_instalar").toString());

        Range range = new Range();
        range.setColor(Color.parseColor("#00b20b"));
        range.setFrom(0.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#ce0000"));
        range2.setFrom(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));

        if(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()) > Double.parseDouble(formList.get(0).get("puertas_objetivo").toString())){
            range.setColor(Color.parseColor("#cbfc28"));
            range2.setColor(Color.parseColor("#00b20b"));
            range.setTo(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()));
            range2.setFrom(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()));
            range2.setTo(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));
            halfGauge.setMaxValue(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                halfGauge.setTooltipText("Ventas permiten ligar mas equipos que el objetivo.");
            }
        }

        if(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()) > Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString())){
            range.setTo(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));
            range2.setFrom(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));
            range2.setTo(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()));
            halfGauge.setMaxValue(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()));
            range.setColor(Color.parseColor("#00b20b"));
            range2.setColor(Color.parseColor("#fc361c"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                halfGauge.setTooltipText("Cliente no tiene las ventas suficientes para llegar a su objetivo.");
            }
        }
        if(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()) == Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString())){
            range.setTo(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));
            range2.setFrom(Double.parseDouble(formList.get(0).get("puertas_sugeridas").toString()));
            range2.setTo(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()));
            halfGauge.setMaxValue(Double.parseDouble(formList.get(0).get("puertas_objetivo").toString()));
            range.setColor(Color.parseColor("#00b20b"));
            range2.setColor(Color.parseColor("#fc361c"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                halfGauge.setTooltipText("Cliente tiene la misma cantidad de sugerencia que su objetivo.");
            }
        }
        //add color ranges to gauge
        halfGauge.addRange(range);
        halfGauge.addRange(range2);

        //set min max and current value
        halfGauge.setMinValue(0.0);
        halfGauge.setValue(Double.parseDouble(formList.get(0).get("puertas_instaladas").toString()));

        /*Configuracion y Acciones del menu de abajo de la pantalla*/
        bottomNavigation = findViewById(R.id.bottom_navigation_monitor_equipo_frio);
        bottomNavigation.getMenu().getItem(0).setTitle(formList.get(0).get("solicitud").charAt(0) + formList.get(0).get("solicitud").substring(1).toLowerCase());
        if (Integer.parseInt(formList.get(0).get("puertas_instaladas")) == 0)
            bottomNavigation.getMenu().getItem(0).setTitle(bottomNavigation.getMenu().getItem(0).getTitle().toString().replace("/ cambio",""));
        //Setear Eventos de Elementos del bottom navigation
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_solicitud:
                        showDialogFormulariosModificacion(codigo_cliente,Integer.parseInt(formList.get(0).get("puertas_por_instalar")),Integer.parseInt(formList.get(0).get("puertas_instaladas")));
                        return true;
                    case R.id.action_base_instalada:
                        Bundle bc = new Bundle();
                        bc.putString("codigo_cliente", codigo_cliente);
                        bc.putString("nombre_cliente", nombre_cliente);
                        bc.putString("canal_cliente", canal_cliente);
                        bc.putString("correo_cliente", correo_cliente);
                        intent = new Intent(getApplicationContext(),BaseInstaladaActivity.class);
                        intent.putExtras(bc); //Pase el parametro el Intent
                        startActivity(intent);
                        return true;
                    case R.id.action_disponible:
                        bc = new Bundle();
                        bc.putString("codigo_cliente", codigo_cliente);
                        bc.putString("nombre_cliente", nombre_cliente);
                        bc.putString("canal_cliente", canal_cliente);
                        bc.putString("correo_cliente", correo_cliente);
                        intent = new Intent(getApplicationContext(),EquipoDisponibleActivity.class);
                        intent.putExtras(bc); //Pase el parametro el Intent
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });


        locationServices = new LocacionGPSActivity(MonitorEquipoFrioActivity.this, MonitorEquipoFrioActivity.this);
        //locationServices.startLocationUpdates();
    }

    @Override
    protected  void onResume(){
        super.onResume();
        /*if(estado != null && tipform != null)
            formList = db.getSolicitudes(estado,tipform);
        else if(estado != null)
            formList = db.getSolicitudes(estado,null);
        else if(tipform != null)
            formList = db.getSolicitudes(null,tipform);
        else
            formList = db.getSolicitudes();*/
        formList = db.getDatosVistaMonitorEquipoFrioDB(codigo_cliente);

        toolbar.setTitle(codigo_cliente +" - "+nombre_cliente);
        toolbar.setSubtitle("Monitor Equipo Frio");
        toolbar.setSubtitleTextColor(Color.DKGRAY);



    }

    private void showDialogFilters(View view) {
        final Dialog dialog =new Dialog(view.getContext());
        dialog.setContentView(R.layout.filtros_solicitudes_dialog_layout);
        dialog.show();

        final MultiSpinnerSearch estadoSpinner = (MultiSpinnerSearch)dialog.findViewById(R.id.estadoSpinner);
        final MultiSpinnerSearch tipoSolicitudSpinner = (MultiSpinnerSearch)dialog.findViewById(R.id.tipoSolicitudSpinner);
        Button btnFiltro = (Button) dialog.findViewById(R.id.saveBtn);
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filtroEstado = "";
                String filtroForm = "";
                dialog.hide();
            }
        });
        //Spinner 1
        final List<KeyPairBoolData> list = db.getEstadosCatalogoParaMultiSpinner();
        estadoSpinner.setItems(list,  new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                String multiFiltro = "";
                String coma = "";
                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        multiFiltro += coma+items.get(i).getName();
                        coma = ",";
                       //Toasty.info(getApplicationContext(), i + " : "+ items.get(i).getName()).show();
                    }
                }
                //mAdapter.getMultiFilter().filter(multiFiltro);

                //if(toolbar != null)
                    //toolbar.setTitle("Mis Solicitudes ("+mAdapter.getItemCount()+" de "+formList.size()+")");
            }
        });
        Drawable d1 = getResources().getDrawable(R.drawable.spinner_background, null);
        estadoSpinner.setBackground(d1);
        estadoSpinner.setColorSeparation(true);
        //Spinner 2
        final List<KeyPairBoolData> list2 = db.getTiposFormularioParaMultiSpinner();
        tipoSolicitudSpinner.setItems(list2,new MultiSpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                String multiFiltro = "";
                String coma = "";
                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        multiFiltro += coma+items.get(i).getName();
                        coma = ",";
                        //Toasty.info(getApplicationContext(), i + " : "+ items.get(i).getName()).show();
                    }
                }
                //mAdapter.getMultiFilter().filter(multiFiltro);

                //if(toolbar != null)
                    //toolbar.setTitle("Mis Solicitudes ("+mAdapter.getItemCount()+" de "+formList.size()+")");
            }
        });
        tipoSolicitudSpinner.setBackground(d1);
        tipoSolicitudSpinner.setColorSeparation(true);
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
            searchView.setQueryHint("Búsqueda");

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

    @Override
    public void onLocationUpdate(Location location) {
        // Handle location updates in your activity here
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public static class EliminarRegistroCenso implements Runnable {
        Context context;
        Activity activity;
        String id;
        public EliminarRegistroCenso(Context context, Activity activity, String id) {
            this.context = context;
            this.activity = activity;
            this.id = id;
        }

        @Override
        public void run() {
            ContentValues updateValues = new ContentValues();
            updateValues.put("activo", 0);

            long modifico = mDb.update("CensoEquipoFrio", updateValues, "num_placa = ?",new String[]{id});

            if(modifico > 0){
                Intent intent = activity.getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.finish();
                activity.overridePendingTransition(0, 0);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                Toasty.success(context,"Registro Eliminado!").show();
            }
        }
    }
    private static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }

    private void showDialogFormulariosModificacion(final String codigoCliente,  Integer puertas_por_instalar, Integer puertas_instaladas) {
        ArrayList<HashMap<String,String>> formulariosPermitidos = null;
        Intent intent;
        if(puertas_por_instalar == 0){
            Toasty.info(getBaseContext(),"El monitor no sugiere hacer ningun movimiento de equipo frio a este cliente.").show();
            return;
        }
            formulariosPermitidos = db.getOrdenesServicioPermitidasMonitor(puertas_por_instalar, puertas_instaladas);
            if(formulariosPermitidos == null || formulariosPermitidos.size() == 0){
                Toasty.info(getBaseContext(),"No se ha configurado ningun formulario de Equipo frio para HH.").show();
                return;
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
                    Intent intent;
                    if(forms[selectedPosition].toLowerCase().contains("eq.") || forms[selectedPosition].toLowerCase().contains("frio") || forms[selectedPosition].toLowerCase().contains("equipo")) {
                        if(forms[selectedPosition].toLowerCase().contains("instal")) {//Si es de instalacion no ocupa numero de maquina de equipo frio
                            dialog.dismiss();
                            Bundle b = new Bundle();
                            b.putString("tipoSolicitud", idforms[selectedPosition]); //id de solicitud
                            b.putString("codigoCliente", codigoCliente);
                            b.putString("monitor","1");
                            b.putString("numPuertas",puertas_por_instalar.toString());
                            intent = new Intent(getApplicationContext(), SolicitudAvisosEquipoFrioActivity.class);
                            intent.putExtras(b); //Pase el parametro el Intent
                            startActivity(intent);
                        }else{//Si NO es de instalacion se ocupa digitar o leer el equipo frio al que se le va a hacer el aviso.
                            //TODO
                            dialog.dismiss();
                            displayDialogSeleccionarEquipoFrio(getApplicationContext(),idforms[selectedPosition],codigoCliente);
                        }
                    }
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
                    //TODO realizaar la mate para saber realmente cuantas puertas puede instalar o desinstalar segun el equipo seleccionado ()
                    b.putString("monitor", "0");
                    b.putString("numPuertas", "0");
                    Intent intent = new Intent(getApplicationContext(), SolicitudAvisosEquipoFrioActivity.class);
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
        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

}
