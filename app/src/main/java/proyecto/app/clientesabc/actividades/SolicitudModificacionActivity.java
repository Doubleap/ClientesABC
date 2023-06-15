package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.Animaciones.CubeTransformer;
import proyecto.app.clientesabc.Animaciones.FlipPageTransformer;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.BancoTableAdapter;
import proyecto.app.clientesabc.adaptadores.ComentarioTableAdapter;
import proyecto.app.clientesabc.adaptadores.ContactoTableAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.ImpuestoTableAdapter;
import proyecto.app.clientesabc.adaptadores.InterlocutorTableAdapter;
import proyecto.app.clientesabc.adaptadores.VisitasTableAdapter;
import proyecto.app.clientesabc.clases.ConsultaClienteAPI;
import proyecto.app.clientesabc.clases.ConsultaClienteServidor;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.MultiOnItemSelectedListener;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.Validaciones;
import proyecto.app.clientesabc.clases.ValidarFlujoClienteAPI;
import proyecto.app.clientesabc.clases.ValidarFlujoClienteServidor;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Banco;
import proyecto.app.clientesabc.modelos.Comentario;
import proyecto.app.clientesabc.modelos.Contacto;
import proyecto.app.clientesabc.modelos.EditTextDatePicker;
import proyecto.app.clientesabc.modelos.Horarios;
import proyecto.app.clientesabc.modelos.Impuesto;
import proyecto.app.clientesabc.modelos.Interlocutor;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.Visitas;

import static android.view.View.INVISIBLE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.core.content.ContextCompat.startActivity;
import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;
import static com.google.android.material.tabs.TabLayout.GRAVITY_FILL;
import static com.google.android.material.tabs.TabLayout.INDICATOR_GRAVITY_TOP;

public class SolicitudModificacionActivity extends AppCompatActivity {

    static ActionBar actionBar;
    final static int alturaFilaTableView = 100;
    static String tipoSolicitud ="";
    static String idSolicitud = "";
    static String codigoCliente = "";
    static String idForm = "";
    static int minAdjuntos = 0;
    @SuppressLint("StaticFieldLeak")
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    static ArrayList<String> listaCamposDinamicos = new ArrayList<>();
    static ArrayList<String> listaCamposDinamicosEnca = new ArrayList<>();
    static ArrayList<String> listaCamposObligatorios = new ArrayList<>();
    static ArrayList<String> listaCamposBloque = new ArrayList<>();
    static Map<String, View> mapeoCamposDinamicos = new HashMap<>();
    static Map<String, View> mapeoCamposDinamicosOld = new HashMap<>();
    static Map<String, View> mapeoCamposDinamicosEnca = new HashMap<>();
    static Map<String, View> mapeoVisitas = new HashMap<>();
    static  ArrayList<HashMap<String, String>> configExcepciones = new ArrayList<>();
    static  ArrayList<HashMap<String, String>> solicitudSeleccionada = new ArrayList<>();
    static  ArrayList<HashMap<String, String>> solicitudSeleccionadaOld = new ArrayList<>();
    private static String GUID;
    private ProgressBar progressBar;
    public static boolean firma;
    static boolean modificable;
    static boolean correoValidado;
    static boolean cedulaValidada;
    static boolean idFiscalValidado;
    static BottomNavigationView bottomNavigation;

    static Uri mPhotoUri;

    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Contacto> tb_contactos;
    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Impuesto> tb_impuestos;
    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Interlocutor> tb_interlocutores;
    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Banco> tb_bancos;
    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Visitas> tb_visitas;
    @SuppressLint("StaticFieldLeak")
    private static de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos;
    @SuppressLint("StaticFieldLeak")
    private static TableView<Comentario> tb_comentarios;
    private static ArrayList<Contacto> contactosSolicitud;
    private static ArrayList<Impuesto> impuestosSolicitud;
    private static ArrayList<Banco> bancosSolicitud;
    private static ArrayList<Interlocutor> interlocutoresSolicitud;
    private static ArrayList<Visitas> visitasSolicitud;
    private static ArrayList<Adjuntos> adjuntosSolicitud;
    //Bloques de datos con valores sin modificar o viejos
    private static ArrayList<Contacto> contactosSolicitud_old;
    private static ArrayList<Impuesto> impuestosSolicitud_old;
    private static ArrayList<Banco> bancosSolicitud_old;
    private static ArrayList<Interlocutor> interlocutoresSolicitud_old;
    private static ArrayList<Visitas> visitasSolicitud_old;
    private static ArrayList<Adjuntos> adjuntosSolicitud_old;
    private static ArrayList<Comentario> comentarios;
    private static ArrayList<Horarios> horariosSolicitud;
    //Datos traidos de SAP
    private static JsonArray cliente;
    private static JsonArray notaEntrega;
    private static JsonArray factura;
    private static JsonArray telefonos;
    private static JsonArray faxes;
    private static JsonArray contactos;
    private static JsonArray interlocutores;
    private static JsonArray impuestos;
    private static JsonArray bancos;
    private static JsonArray visitas;
    private static JsonArray horarios;
    public static ManejadorAdjuntos manejadorAdjuntos;
    private static String rutaRepartoNuevaOriginal;
    private static String centroSuministroNuevoOriginal;
    private static LinearLayout ll_visitas;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        firma = false;
        modificable = true;
        correoValidado = true;
        cedulaValidada = true;
        idFiscalValidado = true;
        FrameLayout f = findViewById(R.id.background);
        //f.getBackground().setAlpha(80);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            tipoSolicitud = b.getString("tipoSolicitud");
            idSolicitud = b.getString("idSolicitud");
            codigoCliente = b.getString("codigoCliente");
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.botella_coca_header_der,null));

        cliente = null;
        if(idSolicitud != null){
            setTitle("Solicitud");
            solicitudSeleccionada.clear();
            solicitudSeleccionadaOld.clear();
            mapeoCamposDinamicos.clear();
            mapeoCamposDinamicosEnca.clear();
            mapeoVisitas.clear();
            mapeoCamposDinamicosOld.clear();
            solicitudSeleccionada = mDBHelper.getSolicitud(idSolicitud);
            solicitudSeleccionadaOld = mDBHelper.getSolicitudOld(idSolicitud);
            tipoSolicitud = solicitudSeleccionada.get(0).get("TIPFORM");
            GUID = solicitudSeleccionada.get(0).get("id_solicitud").trim();
            idForm = solicitudSeleccionada.get(0).get("IDFORM");
            setTitle(GUID);
            String descripcion = mDBHelper.getDescripcionSolicitud(tipoSolicitud);
            getSupportActionBar().setSubtitle(descripcion +" - "+ solicitudSeleccionada.get(0).get("ESTADO").trim());
        }else{
            GUID = mDBHelper.getGuiId();
            idForm = "";
            solicitudSeleccionada.clear();
            solicitudSeleccionadaOld.clear();
            mapeoCamposDinamicos.clear();
            mapeoCamposDinamicosEnca.clear();
            mapeoVisitas.clear();
            mapeoCamposDinamicosOld.clear();
            setTitle(codigoCliente+"-");
            String descripcion = mDBHelper.getDescripcionSolicitud(tipoSolicitud);
            getSupportActionBar().setSubtitle(descripcion);
        }
        minAdjuntos = mDBHelper.CantidadAdjuntosMinima(tipoSolicitud);
        if(solicitudSeleccionada.size() > 0) {
            firma = true;
            correoValidado = true;
            cedulaValidada = true;
            idFiscalValidado = true;
            if(solicitudSeleccionada.get(0).get("ESTADO").equals("Pendiente")
                    ||solicitudSeleccionada.get(0).get("ESTADO").equals("Rechazado")
                    ||solicitudSeleccionada.get(0).get("ESTADO").equals("Aprobado")){
                modificable = false;
            }
            if(solicitudSeleccionadaOld.size() == 0){
                Toasty.error(getBaseContext(),"No se encontraron los datos de encabezado.").show();
            }
        }else{
            correoValidado = true;
            cedulaValidada = true;
            idFiscalValidado = true;
            if(!tipoSolicitud.equals("1") && !tipoSolicitud.equals("6") && !tipoSolicitud.equals("26") && !tipoSolicitud.equals("46") ){
                firma = true;
            }
        }

        configExcepciones.clear();
        listaCamposDinamicos.clear();
        listaCamposDinamicosEnca.clear();
        listaCamposBloque.clear();
        listaCamposObligatorios.clear();

        configExcepciones = mDBHelper.getConfigExcepciones(tipoSolicitud);

        //Setear Eventos de Elementos del bottom navigation
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_camara:
                        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new ContentValues());
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        try {
                            startActivityForResult(intent, 1);

                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", getResources().getString(R.string.no_activity));
                        }
                        return true;
                    case R.id.action_file:
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            //startActivityForResult(intent, 200);
                            startActivityForResult(Intent.createChooser(intent, "Seleccione un archivo para adjuntar!"),200);
                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", getResources().getString(R.string.no_activity));
                        }
                        return true;
                    case R.id.action_save:
                        int numErrores = 0;
                        String mensajeError="";
                        if(getCurrentFocus() != null)
                            getCurrentFocus().clearFocus();
                        //Validacion de Datos Obligatorios Automatico
                        for(int i=0; i < listaCamposObligatorios.size(); i++) {
                            try{
                                MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                String valor = tv.getText().toString().trim();
                                if(listaCamposObligatorios.get(i).contains("W_CTE-SMTP_ADDR")){
                                    if(tv.isFocused())
                                        tv.clearFocus();
                                }
                                if(valor.isEmpty()){
                                    tv.setError("El campo "+tv.getTag()+" es obligatorio!");
                                    numErrores++;
                                    mensajeError += "- "+tv.getTag()+"\n";
                                }
                                if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LAT") || listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LONG")){
                                    if(valor.equals("0")){
                                        tv.setError("El campo "+tv.getTag()+" no puede ser 0!");
                                        numErrores++;
                                        mensajeError += "- "+tv.getTag()+"\n";
                                    }
                                    if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LAT") && !Validaciones.ValidarCoordenadaY(tv)){
                                        numErrores++;
                                        mensajeError += "- Formato Coordenada Y invalido\n";
                                    }
                                    if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LONG") && !Validaciones.ValidarCoordenadaX(tv)){
                                        numErrores++;
                                        mensajeError += "- Formato Coordenada X invalido\n";
                                    }
                                }
                            }catch(Exception e){
                                Spinner combo = ((Spinner) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                if(combo != null) {
                                    if (combo.getSelectedItem() != null) {
                                        String valor = ((OpcionSpinner) combo.getAdapter().getItem((int) combo.getSelectedItemId())).getId();

                                        if (combo.getAdapter().getCount() == 0 || (combo.getAdapter().getCount() > 0 && valor.isEmpty())) {
                                            ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
                                            //combo.setError("El campo "+combo.getHint()+" es obligatorio!");
                                            numErrores++;
                                            mensajeError += "- " + combo.getTag() + "\n";
                                        }
                                    } else {
                                        TextView error = (TextView) combo.getSelectedView();
                                        error.setError("El campo es obligatorio!");
                                        numErrores++;
                                        mensajeError += "- " + combo.getTag() + "\n";
                                    }
                                }
                            }
                        }
                        //Validacion Formato Coordenadas en caso de tener algun valor pero que NO son obligatorios
                        if(mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT") != null){
                            MaskedEditText texto = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"));
                            if(!texto.getText().toString().replace("0","").replace(".","").isEmpty()){
                                if(!listaCamposObligatorios.contains("W_CTE-ZZCRMA_LAT") && !Validaciones.ValidarCoordenadaY(texto)){
                                    numErrores++;
                                    mensajeError += "- Formato Coordenada Y invalido\n";
                                }
                            }
                        }
                        if(mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG") != null){
                            MaskedEditText texto = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                            if(!texto.getText().toString().replace("0","").replace(".","").isEmpty()){
                                if(!listaCamposObligatorios.contains("W_CTE-ZZCRMA_LONG") && !Validaciones.ValidarCoordenadaX(texto)){
                                    numErrores++;
                                    mensajeError += "- Formato Coordenada X invalido\n";
                                }
                            }
                        }
                        //Validar si el tipo de pago es por transferencia, debe ingresar al menos 1 cuenta bancaria.
                        Spinner comboTipoPago = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR2"));
                        String tipoPago = "";
                        if(comboTipoPago != null && comboTipoPago.getSelectedItem() != null) {
                            tipoPago = ((OpcionSpinner) comboTipoPago.getAdapter().getItem((int) comboTipoPago.getSelectedItemId())).getId();
                            if(tipoPago.equals("T") && bancosSolicitud.size() == 0)
                                mensajeError += "- Tipo de Pago por Transferencia. Debe ingresar al menos 1 cuenta bancaria.\n";
                        }
                        MaskedEditText correo = (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-SMTP_ADDR");
                        if(mapeoCamposDinamicos.get("W_CTE-SMTP_ADDR") != null ){
                            if(correo.isFocused())
                                correo.clearFocus();
                        }
                        //Validacion de bloques obligatorios
                        //Validacion de encuestas ejecutadas
                        CheckBox encuesta = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA");
                        if(encuesta!= null && !encuesta.isChecked()){
                            numErrores++;
                            mensajeError += "- Debe ejecutar la encuesta GEC!\n";
                        }
                        CheckBox encuesta_gec = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                        if(encuesta_gec != null && !encuesta_gec.isChecked()){
                            numErrores++;
                            mensajeError += "- Debe ejecutar la encuesta de Canales!\n";
                        }
                        //Validar el campo de ruta de reparto del grid de visitas
                        Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                        String modalidad = "";
                        String tipoVisita = PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_TIPORUTA","ZPV").toString();//"ZPV";
                        if(comboModalidad != null) {
                            modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();
                        /*if(comboModalidad != null && comboModalidad.getSelectedItem() != null) {
                            modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();
                            if(modalidad.equals("GV"))
                                tipoVisita = "ZAT";
                            if(modalidad.equals("TA"))
                                tipoVisita = "ZTV";
                        }*/
                            int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                            if (!modalidad.equals("GV") && indiceReparto == -1 && visitasSolicitud.size() > 0) {
                                numErrores++;
                                mensajeError += "- No existe tipo visita ZDD de reparto!\n";
                            }

                            if (!modalidad.equals("GV") && indiceReparto != -1 && visitasSolicitud.size() > 0 && visitasSolicitud.get(indiceReparto).getRuta().trim().length() < 6) {
                                numErrores++;
                                mensajeError += "- Falta ruta de reparto(ZDD) en planes de visita!\n";
                            }

                            int indiceMixta = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZRM");
                            int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZPV");
                            if (modalidad.equals("PR") && indiceMixta != -1 && indicePreventa != -1 && visitasSolicitud.size() > 0 && visitasSolicitud.get(indicePreventa).getRuta().trim().length() < 6) {
                                numErrores++;
                                mensajeError += "- Falta asignar ruta de Preventa(ZPV) en PLANES DE VISITA!\n";
                            }

                            int indiceDummy = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDY");
                            if (indiceDummy != -1 && visitasSolicitud.size() > 0 && visitasSolicitud.get(indiceDummy).getRuta().trim().length() < 6) {
                                numErrores++;
                                mensajeError += "- Falta asignar ruta Dummy(ZDY) en PLANES DE VISITA!\n";
                            }
                        }/*else{
                            numErrores++;
                            mensajeError += "- Modalidad de Venta!\n";
                        }*/
                        if(visitasSolicitud.size() > 0) {
                            //Al menos 1 dia de visita
                            if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_L")) != null) {
                                if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_L")).getText().toString().isEmpty())
                                    if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_K")).getText().toString().isEmpty())
                                        if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_M")).getText().toString().isEmpty())
                                            if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_J")).getText().toString().isEmpty())
                                                if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_V")).getText().toString().isEmpty())
                                                    if (((TextInputEditText) mapeoCamposDinamicos.get(tipoVisita+"_S")).getText().toString().isEmpty()) {
                                                        numErrores++;
                                                        mensajeError += "- El cliente debe tener al menos 1 día de visita!\n";
                                                    }
                            }
                            int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZJV");
                            if(indiceEspecializada != -1){
                                //Al menos 1 dia de visita
                                if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_L")) != null) {
                                    if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_L")).getText().toString().isEmpty())
                                        if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_K")).getText().toString().isEmpty())
                                            if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_M")).getText().toString().isEmpty())
                                                if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_J")).getText().toString().isEmpty())
                                                    if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_V")).getText().toString().isEmpty())
                                                        if (((TextInputEditText) mapeoCamposDinamicos.get("ZJV_S")).getText().toString().isEmpty()) {
                                                            numErrores++;
                                                            mensajeError += "- El cliente debe tener al menos 1 día de visita especializada!\n";
                                                        }
                                }
                            }
                        }
                        //Validacion de cedula
                        if(!cedulaValidada){
                            numErrores++;
                            mensajeError += "- Formato de cédula Inválido!\n";
                        }
                        //Validacion de politica de privacidad firmada por el cliente.
                        if(!firma && tipoSolicitud.equals("26")){
                            numErrores++;
                            mensajeError += "- El cliente nuevo debe firmar las políticas de privacidad!\n";
                        }
                        //Validacion de politica de privacidad firmada por el cliente.
                        if(!firma && tipoSolicitud.equals("46")){
                            numErrores++;
                            mensajeError += "- El cliente debe firmar el acta de entrega de la tarjeta.\n";
                        }
                        //Validacion de correo
                        if(mapeoCamposDinamicos.get("W_CTE-SMTP_ADDR") != null && !correoValidado && (listaCamposObligatorios.contains("W_CTE-SMTP_ADDR") || ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-SMTP_ADDR")).getText().toString().trim().length() > 0 )){
                            numErrores++;
                            mensajeError += "- Formato de correo Inválido!\n";
                        }
                        if(minAdjuntos > adjuntosSolicitud.size()){
                            numErrores++;
                            mensajeError += "- Debe adjuntar al menos "+minAdjuntos+" documentos a la solicitud!\n";
                        }

                        //Validacion de siguiente aprobador seleccionado
                        Spinner combo = ((Spinner) mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR"));
                        if(combo.getSelectedItem() != null) {
                            String valor = ((OpcionSpinner)combo.getAdapter().getItem((int) combo.getSelectedItemId())).getId();

                            if (combo.getAdapter().getCount() == 0 || (combo.getAdapter().getCount() > 0 && valor.isEmpty() )) {
                                ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
                                numErrores++;
                                mensajeError += "- Siguiente Aprobador\n";
                            }
                        }else{
                            TextView error = (TextView)combo.getSelectedView();
                            error.setError("El campo es obligatorio!");
                            numErrores++;
                            mensajeError += "- Siguiente Aprobador\n";
                        }
                        if(horariosSolicitud.size() > 0) {
                            String errorHorarios = ValidarHorarios(horariosSolicitud);
                            if (errorHorarios != "") {
                                mensajeError += errorHorarios;
                            }
                        }

                        if(numErrores == 0) {
                            DialogHandler appdialog = new DialogHandler();
                            appdialog.Confirm(SolicitudModificacionActivity.this, "Confirmación Modificacion", "Esta seguro que desea guardar la solicitud de Modificacion?", "No", "Si", new GuardarFormulario(getBaseContext()));
                        }else{
                            Toasty.warning(getApplicationContext(), "Revise los Siguientes campos: \n"+mensajeError, Toast.LENGTH_LONG).show();
                        }
                }
                return true;
            }
        });
        ll_visitas = new LinearLayout(getBaseContext());
        LinearLayout.LayoutParams hhlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        ll_visitas.setLayoutParams(hhlp);
        ll_visitas.setPadding(5, 10, 5, 10);
        ll_visitas.setOrientation(LinearLayout.VERTICAL);
        new MostrarFormulario(this).execute();

        if(solicitudSeleccionada.size() == 0 ) {
            WeakReference<Context> weakRefs1 = new WeakReference<Context>(this);
            WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(this);

            if (VariablesGlobales.UsarAPI()) {
                ValidarFlujoClienteAPI v = new ValidarFlujoClienteAPI(weakRefs1, weakRefAs1, codigoCliente, tipoSolicitud, "0");
                v.execute();
                ConsultaClienteAPI c = new ConsultaClienteAPI(weakRefs1, weakRefAs1, codigoCliente);
                c.execute();
            } else {
                ValidarFlujoClienteServidor v = new ValidarFlujoClienteServidor(weakRefs1, weakRefAs1, codigoCliente, tipoSolicitud, "0");
                v.execute();
                ConsultaClienteServidor c = new ConsultaClienteServidor(weakRefs1, weakRefAs1, codigoCliente);
                c.execute();
            }
        }
        //cliente = c.execute().get();

        if(!modificable) {
            //bottomNavigation.setVisibility(INVISIBLE);
            LinearLayout ll = findViewById(R.id.LinearLayoutMain);
            DrawerLayout.LayoutParams h = new DrawerLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT);

            h.setMargins(0,0,0,0);
            ll.setLayoutParams(h);
            bottomNavigation.setVisibility(View.GONE);
            bottomNavigation.animate().translationY(150);
        }

        //View title = getWindow().findViewById(android.R.id.title);
        //View titleBar = (View) title.getParent();
        //titleBar.setBackground(gd);
        Drawable d=getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        //Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(d);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            //return;
        }

        //Manejo de Bloques
        tb_contactos = new de.codecrafters.tableview.TableView<>(this);
        tb_contactos.addDataClickListener(new ContactoClickListener());
        tb_contactos.addDataLongClickListener(new ContactoLongClickListener());
        tb_impuestos = new de.codecrafters.tableview.TableView<>(this);
        tb_impuestos.addDataClickListener(new ImpuestoClickListener());
        //tb_impuestos.addDataLongClickListener(new ImpuestoLongClickListener());
        tb_bancos = new de.codecrafters.tableview.TableView<>(this);
        tb_bancos.addDataClickListener(new BancoClickListener());
        tb_bancos.addDataLongClickListener(new BancoLongClickListener());
        tb_interlocutores = new de.codecrafters.tableview.TableView<>(this);
        tb_interlocutores.addDataClickListener(new InterlocutorClickListener());
        //tb_interlocutores.addDataLongClickListener(new InterlocutorLongClickListener());
        tb_visitas = new de.codecrafters.tableview.TableView<>(this);
        tb_visitas.addDataClickListener(new VisitasClickListener());
        //tb_visitas.addDataLongClickListener(new VisitasLongClickListener());

        tb_adjuntos = new de.codecrafters.tableview.TableView<>(this);
        tb_comentarios = new de.codecrafters.tableview.TableView<>(this);
        //tb_adjuntos.addDataClickListener(new AdjuntosClickListener());
        //tb_adjuntos.addDataLongClickListener(new AdjuntosLongClickListener());
        contactosSolicitud = new ArrayList<>();
        impuestosSolicitud = new ArrayList<>();
        interlocutoresSolicitud = new ArrayList<>();
        bancosSolicitud = new ArrayList<>();
        visitasSolicitud = new ArrayList<>();
        horariosSolicitud = new ArrayList<>();
        adjuntosSolicitud = new ArrayList<>();
        comentarios = new ArrayList<>();
        //notificantesSolicitud = new ArrayList<Adjuntos>();

        contactosSolicitud_old = new ArrayList<>();
        impuestosSolicitud_old = new ArrayList<>();
        interlocutoresSolicitud_old = new ArrayList<>();
        bancosSolicitud_old = new ArrayList<>();
        visitasSolicitud_old = new ArrayList<>();
        adjuntosSolicitud_old = new ArrayList<>();
        manejadorAdjuntos = new ManejadorAdjuntos();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon_info_title);
        builder.setTitle("Confirmación");
        builder.setCancelable(false);
        builder.setMessage("Esta seguro que quiere salir de la solicitud?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> title = new ArrayList<>();
        private Context context;
        private ViewPagerAdapter(FragmentManager manager, Context c) {
            super(manager);
            List<String> pestanas = mDBHelper.getPestanasFormulario(tipoSolicitud);
            title.addAll(pestanas);
            context = c;
        }

        @Override
        public Fragment getItem(int position) {
            return TabFragment.getInstance(position);
        }

        @Override
        public int getCount() {
            return title.size();
        }

        @Override
        public String getPageTitle(int position) {
            return title.get(position);
        }
    }

    public static class TabFragment extends Fragment {

        int position;
        private String name;
        private TextView textView;

        public static Fragment getInstance(int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("pos", position);
            TabFragment tabFragment = new TabFragment();
            tabFragment.setArguments(bundle);
            return tabFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                position = getArguments().getInt("pos");
            }
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.pagina_formulario, container, false);
            ViewPager viewPager = (ViewPager) container;
            LinearLayout ll = view.findViewById(R.id.miPagina);
            String nombre = Objects.requireNonNull(Objects.requireNonNull(((ViewPager) container).getAdapter()).getPageTitle(position)).toString().trim();

            if(nombre.equals("Datos Generales") || nombre.equals("Informacion General")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"D", idSolicitud);

            }else
            if(nombre.equals("Facturación")|| nombre.equals("Facturacion")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"F", idSolicitud);
            }else
            if(nombre.equals("Ventas")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"V", idSolicitud);
            }else
            if(nombre.equals("Marketing")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"M", idSolicitud);
            }else
            if(nombre.equals("Creditos") || nombre.equals("Créditos")  || nombre.equals("Crédito")  || nombre.equals("Credito")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"C", idSolicitud);
            }else
            if(nombre.equals("Adjuntos") || nombre.equals("Adicionales")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"Z", idSolicitud);
            }else
            if(nombre.toLowerCase().contains("equipo") || nombre.toLowerCase().contains("frio")|| nombre.toLowerCase().contains("eq.")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"E", idSolicitud);
            }
            try {
                viewPager.setPageTransformer(true, (ViewPager.PageTransformer) new CubeTransformer());
            }catch(Exception e){

            }
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
        //LLenado Automatico de campos x pestana.
        @SuppressLint("ClickableViewAccessibility")
        public void LlenarPestana(DataBaseHelper db, View _ll, String tipoFormulario, String pestana, String idSolicitud) {
            //View view = inflater.inflate(R.layout.pagina_formulario, container, false);
            String seccionAnterior = "";
            LinearLayout ll = (LinearLayout)_ll;
            //DataBaseHelper db = new DataBaseHelper(getContext());
            final ArrayList<HashMap<String, String>> campos = db.getCamposPestana(tipoFormulario, pestana, idSolicitud);

            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < campos.size(); i++) {
                final int finalI = i;
                ImageView btnAyuda = null;
                //Creacion de seccion
                if(!seccionAnterior.equals(campos.get(i).get("id_seccion").trim()) && !campos.get(i).get("id_seccion").trim().equals("99")) {
                    CardView seccion_layout = new CardView(Objects.requireNonNull(getContext()));

                    TextView seccion_header = new TextView(getContext());
                    seccion_header.setAllCaps(true);
                    seccion_header.setText(campos.get(i).get("seccion").trim());
                    seccion_header.setLayoutParams(tlp);
                    seccion_header.setPadding(10, 0, 0, 0);
                    seccion_header.setTextColor(getResources().getColor(R.color.white, null));
                    seccion_header.setTextSize(10);

                    //LinearLayout seccion_layout = new LinearLayout(getContext());
                    LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    hlp.setMargins(0, 25, 0, 15);
                    seccion_layout.setLayoutParams(hlp);
                    seccion_layout.setBackground(getResources().getDrawable(R.color.colorPrimary, null));
                    seccion_layout.setPadding(5, 5, 5, 5);
                    seccion_layout.addView(seccion_header);

                    ll.addView(seccion_layout);
                }
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").replace(" ","").trim().toLowerCase().equals("adjuntos")) {
                    //Tipo ADJUNTOS
                    DesplegarBloque(mDBHelper,ll,campos.get(i));
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }
                }else
                if (campos.get(i).get("tipo_input")!= null && (campos.get(i).get("tipo_input").replace(" ","").trim().toLowerCase().equals("grid") || campos.get(i).get("tipo_input").trim().toLowerCase().equals("horarios"))) {
                    //Tipo GRID o BLOQUE de Datos (Estos Datos requieren una tabla de la BD adicional a FORMHVKOF)
                    //Bloques Disponibles [Contactos, Impuestos, Funciones Interlocutor, visitas, bancos, notificantes]
                    DesplegarBloque(mDBHelper,ll,campos.get(i));
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }
                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").replace(" ","").trim().toLowerCase().equals("encuesta")) {
                    //Encuesta Canales, se genera un checkbox que indicara si se ha realizado la encuesta de canales completa
                    //Tipo CHECKBOX
                    CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        checkbox.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        checkbox.setEnabled(false);
                        //checkbox.setVisibility(View.GONE);
                    }

                    if(solicitudSeleccionada.size() > 0){
                        checkbox.setChecked(true);
                        checkbox.getButtonDrawable().jumpToCurrentState();
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_survey,null), null);

                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDialogEncuestaCanales(getContext());
                            if(((CheckBox) v).isChecked())
                                ((CheckBox) v).setChecked(false);
                            else
                                ((CheckBox) v).setChecked(true);
                            ((CheckBox) v).getButtonDrawable().jumpToCurrentState();
                        }
                    });

                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked}, // unchecked
                                    new int[]{android.R.attr.state_checked} , // checked
                            },
                            new int[]{
                                    Color.parseColor("#110000"),
                                    Color.parseColor("#00aa00"),
                            }
                    );

                    CompoundButtonCompat.setButtonTintList(checkbox,colorStateList);
                    if (!campos.get(i).get("modificacion").trim().equals("1")) {
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    }
                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("encuesta_gec")) {
                    //Encuesta gec, se genera un checkbox que indicara si se ha realizado la encuesta de canales completa
                    //Tipo CHECKBOX
                    final CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        checkbox.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        checkbox.setEnabled(false);
                        //checkbox.setVisibility(View.GONE);
                    }

                    if(solicitudSeleccionada.size() > 0){
                        checkbox.setChecked(true);
                        checkbox.getButtonDrawable().jumpToCurrentState();
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_survey,null), null);
                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDialogEncuestaGec(getContext());
                            if(((CheckBox) v).isChecked())
                                ((CheckBox) v).setChecked(false);
                            else
                                ((CheckBox) v).setChecked(true);
                            ((CheckBox) v).getButtonDrawable().jumpToCurrentState();
                        }
                    });

                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked}, // unchecked
                                    new int[]{android.R.attr.state_checked} , // checked
                            },
                            new int[]{
                                    Color.parseColor("#110000"),
                                    Color.parseColor("#00aa00"),
                            }
                    );

                    CompoundButtonCompat.setButtonTintList(checkbox,colorStateList);

                    if (!campos.get(i).get("modificacion").trim().equals("1")) {
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    }
                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("checkbox")) {
                    //Tipo CHECKBOX
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    TableRow.LayoutParams lp_old = new TableRow.LayoutParams(150, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    CheckBox checkbox_old = new CheckBox(getContext());
                    checkbox_old.setLayoutParams(lp_old);
                    if((campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("11") || campos.get(i).get("modificacion").trim().equals("10")) && campos.get(i).get("sup").trim().length() == 0){
                        checkbox_old.setEnabled(false);
                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(), checkbox_old);
                    }

                    CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setLayoutParams(lp);
                    checkbox.setText(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        checkbox.setVisibility(View.GONE);
                        checkbox_old.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        checkbox.setEnabled(false);
                        checkbox_old.setEnabled(false);
                        //checkbox.setVisibility(View.GONE);
                    }
                    fila.addView(checkbox_old);
                    fila.addView(checkbox);
                    ll.addView(fila);
                    if (!campos.get(i).get("modificacion").trim().equals("1")) {
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    }
                    //Excepciones de visualizacion y configuracion de campos dados por la tabla ConfigCampos
                    int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                    if(excepcion >= 0) {
                        HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                        Validaciones.ejecutarExcepcion(getContext(),checkbox,null,configExcepcion,listaCamposObligatorios,campos.get(i));
                        int excepcionxAgencia = 0;
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if(((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if (excepcionxAgencia >= 0) {
                            HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                            Validaciones.ejecutarExcepcion(getContext(),checkbox,null,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i));
                        }
                    }
                    if(solicitudSeleccionada.size() > 0){
                        if(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()) != null && solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim().length() > 0)
                            checkbox.setChecked(true);
                        if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()) != null && solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()).trim().length() > 0)
                            checkbox.setChecked(true);
                        if(!modificable){
                            checkbox.setEnabled(false);
                        }
                        checkbox.getButtonDrawable().jumpToCurrentState();
                    }
                }else if (campos.get(i).get("tabla")!= null && campos.get(i).get("tabla").replace(" ","").trim().length() > 0) {
                    //Tipo ComboBox/SelectBox/Spinner
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setWeightSum(10);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,10f));

                    final TextView label = new TextView(getContext());
                    label.setText(campos.get(i).get("descr"));
                    label.setTextAppearance(R.style.AppTheme_TextFloatLabelAppearance);
                    TableRow.LayoutParams lpl = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    if(campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("11") || campos.get(i).get("modificacion").trim().equals("10")){
                        lpl.setMargins(105, 0, 0, -5);
                    }else{
                        lpl.setMargins(35, 0, 0, -5);
                    }

                    label.setPadding(0,0,0,0);
                    label.setLayoutParams(lpl);

                    final SearchableSpinner combo = new SearchableSpinner(getContext(), null);
                    combo.setTitle("Buscar");
                    combo.setPositiveButton("Cerrar");
                    combo.setTag(campos.get(i).get("descr"));
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);

                    lp.setMargins(0, 0, 0, 25);
                    combo.setPadding(0,0,0,0);
                    combo.setLayoutParams(lp);
                    combo.setPopupBackgroundResource(R.drawable.menu_item);
                    if(campos.get(i).get("sup").trim().length() > 0){
                        label.setVisibility(View.GONE);
                        combo.setVisibility(View.GONE);
                    }
                    Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
                    combo.setBackground(d);
                    if(campos.get(i).get("vis").trim().length() > 0){
                        //if(!campos.get(i).get("campo").trim().equals("W_CTE-LZONE")) {
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        //}
                    }

                    //Si es zona de transporte debemos filtrar adicionales VWERK y KLABC si existen y corresponde al pais

                    String filtroxPais = "";
                    if(campos.get(i).get("campo").trim().equals("W_CTE-LZONE")){
                        Spinner centro_suministro = (Spinner)mapeoCamposDinamicos.get("W_CTE-VWERK");
                        String valor_centro_suministro = "";
                        if(centro_suministro != null) {
                            valor_centro_suministro = ((OpcionSpinner) centro_suministro.getSelectedItem()).getId().trim();
                            filtroxPais += "vwerks='"+valor_centro_suministro+"'";
                        }

                        switch(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                            case "1661":
                            case "Z001":
                                Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                if(gec != null)
                                    filtroxPais += " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                if(bzirk_sel != null)
                                    filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                break;
                        }
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-IN_PLAN_INICIATIVA") && solicitudSeleccionada.size() == 0){
                        filtroxPais += "activo='True'";
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-IN_PLAN_INICIATIVA") && solicitudSeleccionada.size() > 0){
                        if(solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia") || solicitudSeleccionada.get(0).get("ESTADO").equals("Modificado") || solicitudSeleccionada.get(0).get("ESTADO").equals("Nuevo"))
                        filtroxPais += "activo='True'";
                    }
                    ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim(), filtroxPais);

                    //Si son catalogos de equipo frio debo indicar el indice de las columnas de ID y Descripcion estan en otros indice de columnas
                    if(campos.get(i).get("tabla").trim().toLowerCase().equals("ef_causas") || campos.get(i).get("tabla").trim().toLowerCase().equals("ef_prioridades")){
                        opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim(),3,4,null);
                    }
                    if(campos.get(i).get("tabla").trim().toLowerCase().equals("ef_clases")){
                        opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim(),1,2,null);
                    }
                    if(campos.get(i).get("tabla").trim().toLowerCase().equals("sapdmateriales_pde")){
                        opciones = db.getDatosCatalogo(campos.get(i).get("tabla").trim(),1,4,null);
                    }
                    ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
                    int selectedIndex = 0;
                    int selectedIndexOld = 0;
                    String valorDefectoxRuta = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(campos.get(i).get("campo").trim().replace("-","_"),"");
                    for (int j = 0; j < opciones.size(); j++){
                        listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
                        if(solicitudSeleccionada.size() > 0){
                            //valor de la solicitud seleccionada
                            if(opciones.get(j).get("id").trim().equals(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim())){
                                selectedIndex = j;
                            }
                            if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim())!= null && opciones.get(j).get("id").trim().equals(solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()).trim())){
                                selectedIndexOld = j;
                            }
                        }
                        if(valorDefectoxRuta.trim().length() > 0 && opciones.get(j).get("id").trim().equals(valorDefectoxRuta.trim())){
                            //selectedIndex = j;
                            /*if(!campos.get(i).get("campo").trim().equals("W_CTE-VWERK")) {
                                if((PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("1661")
                                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("Z001"))
                                        && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","").toString().equals("ZRM")){
                                    combo.setEnabled(true);
                                    combo.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                                }else{
                                    combo.setEnabled(false);
                                    combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                }
                            }*/
                        }

                    }
                    // Creando el adaptador(opciones) para el comboBox deseado
                    ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, listaopciones);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    combo.setAdapter(dataAdapter);
                    combo.setSelection(selectedIndex);
                    if(campos.get(i).get("modificacion").trim().equals("1") && solicitudSeleccionada.size() != 0){
                        combo.setSelection(selectedIndexOld);
                    }

                    //Campo de regimen fiscal, se debe cambiar el formato de cedula segun el tipo de cedula
                    if(campos.get(i).get("campo").trim().equals("W_CTE-KATR3") || campos.get(i).get("campo").trim().equals("W_CTE-STCDT")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                MaskedEditText cedula = null;
                                cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD1");
                                if(cedula == null)
                                    cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-IN_CEDULA_RECIBE");
                                MaskedEditText finalCedula = cedula;
                                final MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                if(cedula != null){
                                    //cedula.setFilters(new InputFilter[]{new RegexInputFilter("[A-Z-a-z]")});
                                    if (opcion.getId().equals("C1") && PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
                                        cedula.setMask("0#-####-####-00");
                                    }
                                    if (opcion.getId().equals("C2") && PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
                                        cedula.setMask("#-###-######");
                                    }
                                    if (opcion.getId().equals("C3") && PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
                                        cedula.setMask("##-####-####-##");
                                    }
                                    if (opcion.getId().startsWith("N")) {
                                        cedula.setMask("AAAAAAAAAAAAAA");
                                    }
                                    if (opcion.getId().equals("P1")) {

                                        cedula.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void afterTextChanged(Editable s) { }
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().equals("")){
                                                    //cedula.setMask("");
                                                }
                                                if(s.toString().equals("N")){
                                                    finalCedula.setMask("N-####-######");
                                                }
                                                if(s.toString().equals("NA")){
                                                    finalCedula.setMask("NA-##-####-#####");
                                                }
                                                if(s.toString().equals("P")){
                                                    finalCedula.setMask("PE-####-#####");
                                                }
                                            }
                                        });
                                    }
                                    if (opcion.getId().equals("P2")) {
                                        cedula.setMask("****************");
                                    }
                                    if (opcion.getId().equals("P3")) {
                                        cedula.setMask("E-####-######");
                                    }
                                    if (opcion.getId().contains("C")
                                            && (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F446")
                                            || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("1657")
                                            || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("1658"))) {
                                        if(idfiscal != null) {
                                            idfiscal.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void afterTextChanged(Editable s) {
                                                }

                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                }

                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    String cantidad = "";
                                                    if (s.toString().length() < 13) {
                                                        for (int x = 0; x <= s.toString().length(); x++) {
                                                            cantidad += "#";
                                                        }
                                                        idfiscal.setMask(cantidad);
                                                        if (s.toString().length() <= 2) {
                                                            idfiscal.setMask("AA");
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                        cedula.setMask("AA");
                                        cedula.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void afterTextChanged(Editable s) { }
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().length() <= 2){
                                                    finalCedula.setMask("**");
                                                }
                                                if(s.toString().length() > 2 && s.toString().length() < 9 && !s.toString().contains("-")){
                                                    String cantidad="";
                                                    for(int x = 0; x < s.toString().length(); x++){
                                                        cantidad += "#";
                                                    }
                                                    finalCedula.setMask(cantidad+"-A");
                                                }
                                            }
                                        });
                                    }

                                    //Uruguay
                                    if (opcion.getId().equals("25")) {
                                        cedula.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void afterTextChanged(Editable s) { }
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().length() <= 8) {
                                                    String cantidad="";
                                                    for(int x = 0; x < s.toString().length(); x++){
                                                        cantidad += "#";
                                                    }
                                                    Spinner  tipoIdentificacion = (Spinner) mapeoCamposDinamicos.get("W_CTE-STCDT");
                                                    if(tipoIdentificacion != null) {
                                                        final OpcionSpinner opcionActual = (OpcionSpinner) tipoIdentificacion.getSelectedItem();
                                                        if (opcionActual.getId().equals("25"))
                                                            finalCedula.setMask(cantidad);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (opcion.getId().equals("42")) {
                                        cedula.setMask("AAAAAAAAAAAAAAAA");
                                    }
                                    if (opcion.getId().equals("88")) {
                                        cedula.setMask("###########A");
                                    }

                                    cedula.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            if (!hasFocus) {
                                                ValidarCedula(v,opcion.getId());
                                                if(VariablesGlobales.ComentariosAutomaticos() && campos.get(finalI).get("comentario_auto").equals("1")){
                                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get("W_CTE-STCD1"),mapeoCamposDinamicosOld.get("W_CTE-STCD1"),"CEDULA");
                                                }
                                            }
                                        }
                                    });
                                    if(idfiscal != null) {
                                        idfiscal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (!hasFocus) {
                                                    ValidarIDFiscal(getContext());
                                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get("W_CTE-STCD3"),mapeoCamposDinamicosOld.get("W_CTE-STCD3"),"ID FISCAL");
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    if(solicitudSeleccionada.size() > 0){
                        if(!modificable){
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled,null));
                        }
                    }

                    if(campos.get(i).get("campo").trim().equals("W_CTE-BZIRK")){

                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();
                                ActualizarAprobadores();

                                //Si tiene diferenciacion por agencia para algun campo llamarlo aqui
                                    final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                    String campo = "";
                                    if (campos.get(finalI).get("llamado1").trim().contains("ExcepcionValorDefaultxAgencia")) {
                                        String[] metodos = campos.get(finalI).get("llamado1").trim().split(";");
                                        for(int z = 0; z < metodos.length; z++){
                                            if(metodos[z].contains("ExcepcionValorDefaultxAgencia")) {
                                                campo = metodos[z].replace("ExcepcionValorDefaultxAgencia(","").replace(")","").split(",")[0];
                                                ArrayList<HashMap<String, String>> excepcion = mDBHelper.ExcepcionValorDefaultxAgencia(opcion.getId(),tipoFormulario,campo);
                                                if(excepcion.size() > 0){
                                                    ((Spinner)mapeoCamposDinamicos.get(campo)).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get(campo)),excepcion.get(0).get("DFAUL").trim()));
                                                }
                                            }
                                        }
                                    }
                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-VWERK")){
                        if(solicitudSeleccionada.size()  > 0){
                            centroSuministroNuevoOriginal = solicitudSeleccionada.get(0).get("W_CTE-VWERK").trim();
                        }
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Spinner zona_transporte = (Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE");
                                String valor_centro_suministro = "";
                                if(solicitudSeleccionada.size() == 0)
                                    valor_centro_suministro = ((OpcionSpinner)combo.getSelectedItem()).getId().trim();
                                else
                                    valor_centro_suministro = ((OpcionSpinner)combo.getSelectedItem()).getId().trim();

                                if(!valor_centro_suministro.equals("")) {
                                    String filtroxPais = "";
                                    switch(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                                        case "1661":
                                        case "Z001":
                                            Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                            if(gec != null)
                                                filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                            Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                            if(bzirk_sel != null)
                                                filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                            break;
                                        default:
                                            filtroxPais = "";
                                    }
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);
                                    // Creando el adaptador(opciones) para el comboBox deseado
                                    ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                    // Drop down layout style - list view with radio button
                                    dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                    if (zona_transporte != null) {
                                        String valor_zt = "";
                                        if(((OpcionSpinner)zona_transporte.getSelectedItem()) != null) {
                                            valor_zt = ((OpcionSpinner) zona_transporte.getSelectedItem()).getId().trim();
                                        }
                                        zona_transporte.setAdapter(dataAdapterRuta);
                                        int selectedIndex = 0;
                                        for (int j = 0; j < rutas_reparto.size(); j++) {
                                            if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-LZONE") != null && solicitudSeleccionada.get(0).get("W_CTE-LZONE").trim().equals(rutas_reparto.get(j).getId())) {
                                                zona_transporte.setSelection(j);
                                                break;
                                            }
                                        }
                                        if (solicitudSeleccionada.size() == 0) {
                                            if(!valor_zt.equals("")) {
                                                //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP.
                                                if(VariablesGlobales.getIndex(zona_transporte, valor_zt) == -1 ){
                                                    ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) zona_transporte.getAdapter());
                                                    OpcionSpinner opcionSAP = new OpcionSpinner(valor_zt,valor_zt +" - "+ valor_zt);
                                                    dataAdapter.add(opcionSAP);
                                                    dataAdapter.notifyDataSetChanged();
                                                }
                                                zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte, valor_zt));
                                            }

                                            Spinner cs_old = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-VWERK");
                                            if(cs_old != null && !valor_centro_suministro.equals(((OpcionSpinner)cs_old.getSelectedItem()).getId().trim())) {
                                                int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                                if(indiceReparto != -1)
                                                    visitasSolicitud.get(indiceReparto).setRuta("");
                                                tb_visitas.setDataAdapter(new VisitasTableAdapter(getContext(), visitasSolicitud));
                                            }else if(cs_old != null && valor_centro_suministro.equals(((OpcionSpinner)cs_old.getSelectedItem()).getId().trim())){
                                                Spinner zona_transporte_old = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-LZONE");
                                                int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                                if(indiceReparto != -1) {
                                                    if(((OpcionSpinner)zona_transporte_old.getSelectedItem()) != null) {
                                                        visitasSolicitud.get(indiceReparto).setRuta(((OpcionSpinner) zona_transporte_old.getSelectedItem()).getId().trim());
                                                    }else{
                                                        Toasty.warning(getContext(),"No se encontró la zona de transporte, se debe validar la replica en SAP");
                                                    }
                                                }
                                                tb_visitas.setDataAdapter(new VisitasTableAdapter(getContext(), visitasSolicitud));
                                                if(((OpcionSpinner)zona_transporte_old.getSelectedItem()) != null) {
                                                    zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte, ((OpcionSpinner) zona_transporte_old.getSelectedItem()).getId().trim()));
                                                }else{
                                                    Toasty.warning(getContext(),"No se encontró la zona de transporte, se debe validar la replica en SAP");
                                                }
                                            }
                                        }else{
                                            Spinner cs = (Spinner)mapeoCamposDinamicos.get("W_CTE-VWERK");
                                            if(cs != null && !centroSuministroNuevoOriginal.equals(((OpcionSpinner)cs.getSelectedItem()).getId().trim())) {
                                                int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                                if(indiceReparto != -1)
                                                    visitasSolicitud.get(indiceReparto).setRuta("");
                                                tb_visitas.setDataAdapter(new VisitasTableAdapter(getContext(), visitasSolicitud));
                                            }else if(centroSuministroNuevoOriginal.equals(((OpcionSpinner)cs.getSelectedItem()).getId().trim())){
                                                Spinner zona_transporte_old = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-LZONE");
                                                int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                                if(indiceReparto != -1)
                                                    visitasSolicitud.get(indiceReparto).setRuta(rutaRepartoNuevaOriginal);
                                                tb_visitas.setDataAdapter(new VisitasTableAdapter(getContext(), visitasSolicitud));

                                                if(rutaRepartoNuevaOriginal != null) {
                                                    int indiceSel = VariablesGlobales.getIndex(zona_transporte_old, rutaRepartoNuevaOriginal);
                                                    if(indiceSel == -1){
                                                        //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP o del formulario con incidencia
                                                        if(VariablesGlobales.getIndex(zona_transporte_old, rutaRepartoNuevaOriginal) == -1 ){
                                                            ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) zona_transporte_old.getAdapter());
                                                            OpcionSpinner opcionSAP = new OpcionSpinner(rutaRepartoNuevaOriginal,rutaRepartoNuevaOriginal +" - "+ rutaRepartoNuevaOriginal);
                                                            dataAdapter.add(opcionSAP);
                                                            dataAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                    zona_transporte_old.setSelection(VariablesGlobales.getIndex(zona_transporte_old, rutaRepartoNuevaOriginal));

                                                    indiceSel = VariablesGlobales.getIndex(zona_transporte, rutaRepartoNuevaOriginal);
                                                    if(indiceSel == -1){
                                                        //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP o del formulario con incidencia
                                                        if(VariablesGlobales.getIndex(zona_transporte, rutaRepartoNuevaOriginal) == -1 ){
                                                            ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) zona_transporte.getAdapter());
                                                            OpcionSpinner opcionSAP = new OpcionSpinner(rutaRepartoNuevaOriginal,rutaRepartoNuevaOriginal +" - "+ rutaRepartoNuevaOriginal);
                                                            dataAdapter.add(opcionSAP);
                                                            dataAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                    zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte, rutaRepartoNuevaOriginal));
                                                }
                                            }
                                        }


                                        //Campos zona de transporte se comporta diferente para Autoventa, debe ser la misma ruta de preventa y no puede ser seleccionable.
                                        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH", "").substring(2, 3).equals("A")) {
                                            zona_transporte.setEnabled(false);
                                            zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                            zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH", "").trim()));
                                        }
                                    }
                                }
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-KVGR5")){
                        //TODO aqui se debe cambiar si se quiere trabajar con diferentes tipos de 'PR'
                        if(solicitudSeleccionada.size() == 0){
                            /*combo.setSelection(VariablesGlobales.getIndex(combo, "PR"));
                            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")){
                                combo.setSelection(VariablesGlobales.getIndex(combo, "GV"));
                            }
                            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZTV")){
                                combo.setSelection(VariablesGlobales.getIndex(combo, "TA"));
                            }
                            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZJV")){
                                combo.setSelection(VariablesGlobales.getIndex(combo, "PE"));
                            }*/
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }else{
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }
                        int finalI1 = i;
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                new ResetearVisitas(getContext(), getActivity());
                                DesplegarBloque(db,ll,campos.get(getIndexOFkey("W_CTE-VISITAS",  campos)));
                                llenarDiasDeVisita(getContext());
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1") != null) {
                        if (campos.get(i).get("llamado1").trim().contains("Provincia")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Provincias(parent);
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("Cantones")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Cantones(parent);
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        if (campos.get(i).get("llamado1").trim().contains("Distritos")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Distritos(parent);
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("DireccionCorta")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    DireccionCorta(getContext());
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("Canales(")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Canales(parent);
                                    if (position == 0)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("CanalesKof")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    CanalesKof(parent);
                                    if (position == 0)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("ValoresSegunCanal(")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                        ArrayList<HashMap<String, String>> canales = mDBHelper.getValoresSegunCanal(opcion.getId());
                                        if (canales.size() > 0) {
                                            Spinner grupo_canal = (Spinner) mapeoCamposDinamicos.get("W_CTE-ZGPOCANAL");
                                            Spinner tipo_canal = (Spinner) mapeoCamposDinamicos.get("W_CTE-ZTPOCANAL");
                                            Spinner gec = (Spinner) mapeoCamposDinamicos.get("W_CTE-KLABC");
                                            if (grupo_canal != null) {
                                                grupo_canal.setSelection(VariablesGlobales.getIndex(grupo_canal, canales.get(0).get("zgpocanal")));
                                            }
                                            if (tipo_canal != null) {
                                                tipo_canal.setSelection(VariablesGlobales.getIndex(tipo_canal, canales.get(0).get("ztpocanal")));
                                            }
                                            if (gec != null) {
                                                //gec.setSelection(VariablesGlobales.getIndex(gec, canales.get(0).get("gec")));
                                            }
                                        }
                                        if (position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                        if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                            Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                        }
                                    }catch (Exception e){}
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("ImpuestoSegunUnidadNegocio")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ImpuestoSegunUnidadNegocio(parent);
                                    if (position == 0)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("AsignarTipoImpuesto")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    AsignarTipoImpuesto(parent);

                                    if(campos.get(finalI).get("campo").trim().equals("W_CTE-KATR3") || campos.get(finalI).get("campo").trim().equals("W_CTE-STCDT")){
                                        MaskedEditText cedula = null;
                                        cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD1");
                                        if(cedula == null)
                                            cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-IN_CEDULA_RECIBE");
                                        MaskedEditText finalCedula = cedula;
                                        final MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
                                        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                        if(cedula != null){
                                            //cedula.setFilters(new InputFilter[]{new RegexInputFilter("[A-Z-a-z]")});
                                            if (opcion.getId().equals("C1") && PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
                                                cedula.setMask("0#-####-####-00");
                                            }
                                            if (opcion.getId().equals("C2") && PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
                                                cedula.setMask("#-###-######");
                                            }
                                            if (opcion.getId().equals("C3") && PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
                                                cedula.setMask("##-####-####-##");
                                            }
                                            if (opcion.getId().startsWith("N")) {
                                                cedula.setMask("****************");
                                            }
                                            if (opcion.getId().equals("P1")) {

                                                cedula.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void afterTextChanged(Editable s) { }
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                        if(s.toString().equals("")){
                                                            //cedula.setMask("");
                                                        }
                                                        if(s.toString().equals("N")){
                                                            finalCedula.setMask("N-####-######");
                                                        }
                                                        if(s.toString().equals("NA")){
                                                            finalCedula.setMask("NA-##-####-#####");
                                                        }
                                                        if(s.toString().equals("P")){
                                                            finalCedula.setMask("PE-####-#####");
                                                        }
                                                    }
                                                });
                                            }
                                            if (opcion.getId().equals("P2")) {
                                                cedula.setMask("****************");
                                            }
                                            if (opcion.getId().equals("P3")) {
                                                cedula.setMask("E-####-######");
                                            }
                                            if (opcion.getId().contains("C")
                                                    && (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F446")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("1657")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("1658"))) {
                                                if(idfiscal != null) {
                                                    idfiscal.addTextChangedListener(new TextWatcher() {
                                                        @Override
                                                        public void afterTextChanged(Editable s) {
                                                        }

                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                            String cantidad = "";
                                                            if (s.toString().length() < 13) {
                                                                for (int x = 0; x <= s.toString().length(); x++) {
                                                                    cantidad += "#";
                                                                }
                                                                idfiscal.setMask(cantidad);
                                                                if (s.toString().length() <= 2) {
                                                                    idfiscal.setMask("AA");
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                                cedula.setMask("AA");
                                                cedula.addTextChangedListener(new TextWatcher() {
                                                    @Override
                                                    public void afterTextChanged(Editable s) { }
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                        if(s.toString().length() <= 2){
                                                            finalCedula.setMask("**");
                                                        }
                                                        if(s.toString().length() > 2 && s.toString().length() < 9 && !s.toString().contains("-")){
                                                            String cantidad="";
                                                            for(int x = 0; x < s.toString().length(); x++){
                                                                cantidad += "#";
                                                            }
                                                            finalCedula.setMask(cantidad+"-A");
                                                        }
                                                    }
                                                });
                                            }

                                            //Uruguay
                                            if (opcion.getId().equals("25")) {
                                                TextWatcher textWatcher_UY = new TextWatcher() {
                                                    @Override
                                                    public void afterTextChanged(Editable s) { }
                                                    @Override
                                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                    @Override
                                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                        if(s.toString().length() <= 8) {
                                                            String cantidad="";
                                                            for(int x = 0; x < s.toString().length(); x++){
                                                                cantidad += "#";
                                                            }
                                                            Spinner  tipoIdentificacion = (Spinner) mapeoCamposDinamicos.get("W_CTE-STCDT");
                                                            if(tipoIdentificacion != null) {
                                                                final OpcionSpinner opcionActual = (OpcionSpinner) tipoIdentificacion.getSelectedItem();
                                                                if (opcionActual.getId().equals("25"))
                                                                    finalCedula.setMask(cantidad);
                                                            }
                                                        }
                                                    }
                                                };
                                                cedula.addTextChangedListener(textWatcher_UY);
                                            }
                                            if (opcion.getId().equals("42")) {
                                                cedula.setMask("AAAAAAAAAAAAAAAA");
                                            }
                                            if (opcion.getId().equals("88")) {
                                                cedula.setMask("###########A");
                                            }

                                            cedula.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                @Override
                                                public void onFocusChange(View v, boolean hasFocus) {
                                                    if (!hasFocus) {
                                                        ValidarCedula(v,opcion.getId());
                                                        if(VariablesGlobales.ComentariosAutomaticos() && campos.get(getIndexOFkey("W_CTE-STCD1",  campos)).get("comentario_auto").trim().equals("1")){
                                                            Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get("W_CTE-STCD1"),mapeoCamposDinamicosOld.get("W_CTE-STCD1"),"CEDULA");
                                                        }
                                                    }
                                                }
                                            });
                                            if(idfiscal != null) {
                                                idfiscal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                                    @Override
                                                    public void onFocusChange(View v, boolean hasFocus) {
                                                        if (!hasFocus) {
                                                            ValidarIDFiscal(getContext());
                                                            if(VariablesGlobales.ComentariosAutomaticos() && campos.get(getIndexOFkey("W_CTE-STCD3",  campos)).get("comentario_auto").trim().equals("1")){
                                                                Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get("W_CTE-STCD3"),mapeoCamposDinamicosOld.get("W_CTE-STCD3"),"ID FISCAL");
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                        if(position == 0)
                                            ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    }

                                    if (position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                    if(VariablesGlobales.ComentariosAutomaticos() && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    }
                    if (campos.get(i).get("llamado1").trim().contains("ReplicarValor")) {
                        String[] split = campos.get(i).get("llamado1").trim().split("'");
                        if (split.length < 3)
                            split = campos.get(i).get("llamado1").trim().split("`");
                        if (split.length < 3)
                            split = campos.get(i).get("llamado1").trim().split("\"");
                        final String campoAReplicar = split[1];
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ReplicarValorSpinner(parent, campoAReplicar, ((OpcionSpinner) parent.getSelectedItem()).getId().trim());
                                if (position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                if(campos.get(finalI).get("campo").trim().equals("W_CTE-BZIRK")){
                                    String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                    PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();
                                    ActualizarAprobadores();
                                }

                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    //Campos de encabezado deben salir todos como deshabilitados en valor viejo
                    if(campos.get(i).get("modificacion").trim().equals("1") && campos.get(i).get("sup").trim().length() == 0){
                        combo.setEnabled(false);
                        combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_old,null));
                        listaCamposDinamicosEnca.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicosEnca.put(campos.get(i).get("campo").trim(),combo);
                    }

                    final Spinner combo_old = new Spinner(getContext(), Spinner.MODE_DROPDOWN);
                    combo_old.setVisibility(View.GONE);
                    combo_old.setEnabled(false);
                    if((campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("11") || campos.get(i).get("modificacion").trim().equals("10")) && campos.get(i).get("sup").trim().length() == 0){
                        Button btnAyudai=null;
                        TableRow.LayoutParams textolp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        TableRow.LayoutParams btnlp2 = new TableRow.LayoutParams(75, 75,1f);
                        textolp2.setMargins(0,0,65,0);
                        combo.setLayoutParams(textolp2);
                        combo_old.setLayoutParams(textolp2);
                        //label.setLayoutParams(textolp2);
                        //label_old.setLayoutParams(textolp2);
                        btnAyudai = new Button(getContext());
                        btnAyudai.setBackground(getResources().getDrawable(R.drawable.icon_ver_viejo,null));
                        btnlp2.setMargins(0,0,5,0);
                        btnAyudai.setLayoutParams(btnlp2);
                        btnAyudai.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        btnAyudai.setForegroundGravity(GRAVITY_CENTER);

                        btnAyudai.setOnTouchListener(new View.OnTouchListener()
                        {
                            @Override
                            public boolean onTouch(View v, MotionEvent event)
                            {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    combo.setVisibility(View.GONE);
                                    combo_old.setVisibility(View.VISIBLE);
                                    label.setText(label.getText() + " Actual");
                                    return true;
                                }else if (event.getAction() == MotionEvent.ACTION_UP){
                                    combo.setVisibility(View.VISIBLE);
                                    combo_old.setVisibility(View.GONE);
                                    label.setText(label.getText().toString().replace(" Actual",""));
                                    return false;
                                }else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                                    combo.setVisibility(View.VISIBLE);
                                    combo_old.setVisibility(View.GONE);
                                    label.setText(label.getText().toString().replace(" Actual",""));
                                    return false;
                                }
                                // TODO Auto-generated method stub
                                return false;
                            }
                        });

                        combo_old.setTag(campos.get(i).get("descr"));
                        TableRow.LayoutParams lp_old = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        lp_old.setMargins(0, 0, 65, 0);
                        combo_old.setPadding(0,0,0,0);
                        combo_old.setLayoutParams(lp_old);
                        combo_old.setBackground(getResources().getDrawable(R.drawable.spinner_background_old, null));

                        combo_old.setAdapter(dataAdapter);
                        combo_old.setSelection(selectedIndexOld);

                        if(btnAyudai != null)
                            fila.addView(btnAyudai);

                        if(campos.get(i).get("llamado1").trim().contains("Provincia")){
                            combo_old.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ProvinciasOld(parent);
                                    if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if(campos.get(i).get("llamado1").trim().contains("Cantones")){
                            combo_old.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    CantonesOld(parent);
                                    if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        if(campos.get(i).get("llamado1").trim().contains("Distritos")){
                            combo_old.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    DistritosOld(parent);
                                    if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(),combo_old);
                    }

                    if(combo_old != null && (campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("11") || campos.get(i).get("modificacion").trim().equals("10"))) {
                        ll.addView(label);
                        fila.addView(combo);
                        fila.addView(combo_old);
                    }else{
                        ll.addView(label);
                        ll.addView(combo);
                    }
                    if(btnAyuda != null)
                        fila.addView(btnAyuda);
                    ll.addView(fila);


                    if(!listaCamposDinamicos.contains(campos.get(i).get("campo").trim())) {
                        if (!campos.get(i).get("modificacion").trim().equals("1")) {
                            listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                            mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), combo);
                        }
                    }else{
                        //listaCamposDinamicos.add(campos.get(i).get("campo").trim()+"1");
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim()+"1", combo);
                        //Replicar valores de campos duplicados en configuracion
                        Spinner original = (Spinner) mapeoCamposDinamicos.get(campos.get(i).get("campo").trim());
                        Spinner duplicado = (Spinner) mapeoCamposDinamicos.get(campos.get(i).get("campo").trim()+"1");
                        final String nombreCampo = campos.get(i).get("campo").trim();
                        final int indice = i;
                        original.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(campos.get(indice).get("llamado1").contains("Provincia"))
                                    Provincias(parent);
                                if(campos.get(indice).get("llamado1").contains("Cantones"))
                                    Cantones(parent);
                                if(campos.get(indice).get("llamado1").contains("Distritos"))
                                    Distritos(parent);

                                if(nombreCampo.equals("W_CTE-VWERK")){
                                    Spinner zona_transporte = (Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE");
                                    String valor_centro_suministro = "";
                                    if(solicitudSeleccionada.size() == 0)
                                        valor_centro_suministro = ((OpcionSpinner)combo.getSelectedItem()).getId().trim();
                                    else
                                        valor_centro_suministro = solicitudSeleccionada.get(0).get("W_CTE-VWERK");
                                    String valor_zt = ((OpcionSpinner)zona_transporte.getSelectedItem()).getId().trim();
                                    String filtroxPais = "";
                                    switch(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                                        case "1661":
                                        case "Z001":
                                            Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                            if(gec != null)
                                                filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                            Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                            if(bzirk_sel != null)
                                                filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                            break;
                                        default:
                                            filtroxPais = "";
                                    }
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);// Creando el adaptador(opciones) para el comboBox deseado
                                    ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                    // Drop down layout style - list view with radio button
                                    dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                    if(zona_transporte != null) {
                                        zona_transporte.setAdapter(dataAdapterRuta);
                                        int selectedIndex = 0;
                                        for (int j = 0; j < rutas_reparto.size(); j++) {
                                            if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-LZONE") != null && solicitudSeleccionada.get(0).get("W_CTE-LZONE").trim().equals(rutas_reparto.get(j).getId())) {
                                                zona_transporte.setSelection(j);
                                                break;
                                            }
                                        }
                                        if(solicitudSeleccionada.size() == 0 && !valor_zt.equals("")){
                                            //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP.
                                            if(VariablesGlobales.getIndex(zona_transporte, valor_zt) == -1 ){
                                                ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) zona_transporte.getAdapter());
                                                OpcionSpinner opcionSAP = new OpcionSpinner(valor_zt,valor_zt +" - "+ valor_zt);
                                                dataAdapter.add(opcionSAP);
                                                dataAdapter.notifyDataSetChanged();
                                            }
                                            zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte,valor_zt));
                                        }
                                        //Campos zona de transporte se comporta diferente para Autoventa, debe ser la misma ruta de preventa y no puede ser seleccionable.
                                        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")) {
                                            zona_transporte.setEnabled(false);
                                            zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                            zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte,PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH","").trim()));

                                            String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG",""), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH","").trim());
                                            if(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")) != null)
                                                ((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")), condicionExpedicion));

                                        }
                                    }
                                }

                                if(nombreCampo.equals("W_CTE-BZIRK")){
                                    String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                    PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();
                                    ActualizarAprobadores();
                                }
                                ReplicarValorSpinner(parent,nombreCampo+"1",((OpcionSpinner) parent.getSelectedItem()).getId().trim());
                                if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        duplicado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(campos.get(indice).get("llamado1").contains("Provincia"))
                                    Provincias(parent);
                                if(campos.get(indice).get("llamado1").contains("Cantones"))
                                    Cantones(parent);
                                if(campos.get(indice).get("llamado1").contains("Distritos"))
                                    Distritos(parent);

                                if(nombreCampo.equals("W_CTE-VWERK")){
                                    Spinner zona_transporte = (Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE");
                                    String valor_centro_suministro = "";
                                    if(solicitudSeleccionada.size() == 0)
                                        valor_centro_suministro = ((OpcionSpinner)combo.getSelectedItem()).getId().trim();
                                    else
                                        valor_centro_suministro = solicitudSeleccionada.get(0).get("W_CTE-VWERK");
                                    String filtroxPais = "";
                                    switch(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                                        case "1661":
                                        case "Z001":
                                            Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                            if(gec != null)
                                                filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                            Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                            if(bzirk_sel != null)
                                                filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                            break;
                                        default:
                                            filtroxPais = "";
                                    }
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);// Creando el adaptador(opciones) para el comboBox deseado
                                    ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                    // Drop down layout style - list view with radio button
                                    dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                    if(zona_transporte != null) {
                                        zona_transporte.setAdapter(dataAdapterRuta);
                                        int selectedIndex = 0;
                                        for (int j = 0; j < rutas_reparto.size(); j++) {
                                            if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-LZONE") != null && solicitudSeleccionada.get(0).get("W_CTE-LZONE").trim().equals(rutas_reparto.get(j).getId().trim())) {
                                                zona_transporte.setSelection(j);
                                                break;
                                            }
                                        }
                                        //Campos zona de transporte se comporta diferente para Autoventa, debe ser la misma ruta de preventa y no puede ser seleccionable.
                                        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")) {
                                            zona_transporte.setEnabled(false);
                                            zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                            zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte,PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH","").trim()));

                                            String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG",""), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH","").trim());
                                            if(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")) != null)
                                                ((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")), condicionExpedicion));

                                        }
                                    }
                                }
                                if(nombreCampo.equals("W_CTE-BZIRK")){
                                    String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                    PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();
                                    ActualizarAprobadores();
                                }
                                ReplicarValorSpinner(parent,nombreCampo,((OpcionSpinner) parent.getSelectedItem()).getId().trim());
                                if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");

                                if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("obl")!= null && campos.get(i).get("obl").trim().length() > 0){
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                        OpcionSpinner op = new OpcionSpinner("","");
                        if(combo.getOnItemSelectedListener() == null){
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    final TextView opcion = (TextView) parent.getSelectedView();
                                    if(position == 0 && opcion != null)
                                        opcion.setError("El campo es obligatorio!");
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Toasty.info(getContext(),"Nothing Selected").show();
                                }
                            });
                        }
                    }

                    //Excepciones de visualizacion y configuracion de campos dados por la tabla ConfigCampos
                    int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                    if(excepcion >= 0) {
                        HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                        Validaciones.ejecutarExcepcion(getContext(),combo,label,configExcepcion,listaCamposObligatorios,campos.get(i));

                        int excepcionxAgencia = 0;
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if(((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if (excepcionxAgencia >= 0) {
                            HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                            Validaciones.ejecutarExcepcion(getContext(),combo,label,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i));
                        }
                    }

                    if(VariablesGlobales.ComentariosAutomaticos() && !campos.get(finalI).get("campo").trim().equals("W_CTE-COMENTARIOS")  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                        if(combo.getOnItemSelectedListener() == null){
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            });
                        }
                    }

                } else {
                    //Tipo EditText normal textbox
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setWeightSum(10);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,10f));

                    final TextInputLayout label = new TextInputLayout(Objects.requireNonNull(getContext()));
                    label.setHint(campos.get(i).get("descr"));
                    label.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorTextView,null)));
                    label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                    label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);

                    final MaskedEditText et = new MaskedEditText(getContext(),null);
                    InputFilter[] editFilters = et.getFilters();
                    InputFilter[] newFilters = null;
                    if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").trim().equals("F451")){
                        newFilters = new InputFilter[editFilters.length + 1];
                        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                        newFilters[editFilters.length] = Validaciones.getEditTextFilter();
                        et.setFilters(newFilters);
                    }
                    et.setTag(campos.get(i).get("descr"));
                    //et.setTextColor(getResources().getColor(R.color.colorTextView,null));
                    //et.setBackgroundColor(getResources().getColor(R.color.black,null));
                    //et.setHint(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        et.setVisibility(View.GONE);
                        label.setVisibility(View.GONE);
                    }
                    // Atributos del Texto a crear
                    //TableLayout.LayoutParams lp =  new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,0.5f);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
                    if(campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("11") || campos.get(i).get("modificacion").trim().equals("10"))
                        lp.setMargins(0, 15, 75, 15);
                    else
                        lp.setMargins(0, 15, 0, 15);
                    et.setLayoutParams(lp);
                    et.setPadding(15, 5, 0, 5);
                    Drawable d = getResources().getDrawable(R.drawable.textbackground, null);
                    et.setBackground(d);
                    if(campos.get(i).get("vis").trim().length() > 0){
                        et.setEnabled(false);
                        et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled,null));
                        //et.setVisibility(View.GONE);
                    }
                    et.setMaxLines(1);

                    if(campos.get(i).get("datatype") != null && campos.get(i).get("datatype").contains("char")) {
                        if(campos.get(i).get("campo").trim().equals("W_CTE-STCD3")){
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editFilters = et.getFilters();
                            newFilters = new InputFilter[editFilters.length + 1];
                            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                            newFilters[editFilters.length] = new InputFilter.LengthFilter( 18 );
                            et.setFilters(newFilters);
                            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("F446") || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD","").equals("1657") || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD","").equals("1658")){
                                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (!hasFocus) {
                                            ValidarIDFiscal(getContext());
                                            if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                                Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                            }
                                        }
                                    }
                                });
                            }
                        }else if(campos.get(i).get("campo").trim().equals("W_CTE-PSTLZ") && (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661") || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD","").equals("Z001"))){
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editFilters = et.getFilters();
                            newFilters = new InputFilter[editFilters.length + 1];
                            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                            newFilters[editFilters.length] = new InputFilter.LengthFilter( 5 );
                            et.setFilters(newFilters);
                        }else if(campos.get(i).get("campo").trim().equals("W_CTE-IN_MONTO_TARJETA")){
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editFilters = et.getFilters();
                            newFilters = new InputFilter[editFilters.length + 1];
                            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                            newFilters[editFilters.length] = new InputFilter.LengthFilter( 11 );
                            et.setFilters(newFilters);
                        }else{
                            et.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        if (Integer.valueOf(campos.get(i).get("maxlength")) > 0) {
                            editFilters = et.getFilters();
                            newFilters = new InputFilter[editFilters.length + 1];
                            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                            newFilters[editFilters.length] = new InputFilter.LengthFilter(Integer.valueOf(campos.get(i).get("maxlength")));
                            et.setFilters(newFilters);
                            if(Integer.valueOf(campos.get(i).get("maxlength")) >= 20){
                                // IMPORTANT, do this before any of the code following it
                                et.setSingleLine(true);
                                et.setHorizontallyScrolling(false);
                                et.setMaxLines(5);

                            }
                        }
                    }else if(campos.get(i).get("datatype") != null && campos.get(i).get("datatype").equals("decimal")) {
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editFilters = et.getFilters();
                        newFilters = new InputFilter[editFilters.length + 1];
                        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                        newFilters[editFilters.length] = new InputFilter.LengthFilter( Integer.valueOf(campos.get(i).get("numeric_precision")) );
                        et.setFilters(newFilters);
                    }


                    editFilters = et.getFilters();
                    newFilters = new InputFilter[editFilters.length + 1];
                    System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                    newFilters[editFilters.length] = new InputFilter.AllCaps();
                    et.setFilters(newFilters);
                    et.setAllCaps(true);

                    TableRow.LayoutParams textolp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                    TableRow.LayoutParams btnlp = new TableRow.LayoutParams(75, 75);
                    if(campos.get(i).get("tooltip") != null && campos.get(i).get("tooltip") != ""){
                        textolp.setMargins(0,0,25,0);
                        label.setLayoutParams(textolp);
                        btnAyuda = new ImageView(getContext());
                        btnAyuda.setBackground(getResources().getDrawable(R.drawable.icon_ayuda,null));
                        btnlp.setMargins(0,35,75,0);
                        btnAyuda.setLayoutParams(btnlp);
                        btnAyuda.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        btnAyuda.setForegroundGravity(GRAVITY_CENTER);
                        //TooltipCompat.setTooltipText(btnAyuda, campos.get(i).get("tooltip"));
                        ToolTipsManager mToolTipsManager = new ToolTipsManager();
                        ToolTip.Builder builder = new ToolTip.Builder(getContext(), et, (RelativeLayout)_ll.getParent() ,  campos.get(i).get("tooltip").toString(), ToolTip.POSITION_ABOVE);
                        builder.setAlign(ToolTip.ALIGN_LEFT);
                        //builder.setBackgroundColor(getResources().getColor(R.color.gray,null));
                        builder.setGravity(ToolTip.GRAVITY_LEFT);
                        builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                        btnAyuda.setOnLongClickListener((View.OnLongClickListener) view -> {
                            mToolTipsManager.show(builder.build());
                            return true;
                        });
                    }
                    if(campos.get(i).get("tipo_input") != null && campos.get(i).get("tipo_input").toLowerCase().contains("scan") && modificable){
                        textolp.setMargins(0,0,25,0);
                        label.setLayoutParams(textolp);
                        btnAyuda = new ImageView(getContext());
                        btnAyuda.setBackground(getResources().getDrawable(R.drawable.icon_scan,null));
                        btnlp.setMargins(0,35,75,0);
                        btnAyuda.setLayoutParams(btnlp);
                        btnAyuda.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        btnAyuda.setForegroundGravity(GRAVITY_CENTER);
                        //TooltipCompat.setTooltipText(btnAyuda, campos.get(i).get("tooltip"));
                        ToolTipsManager mToolTipsManager = new ToolTipsManager();
                        ToolTip.Builder builder = new ToolTip.Builder(getContext(), et, (RelativeLayout)_ll.getParent() ,  "Presione para escanear el codigo de barras usando la cámara.", ToolTip.POSITION_ABOVE);
                        builder.setAlign(ToolTip.ALIGN_LEFT);
                        //builder.setBackgroundColor(getResources().getColor(R.color.gray,null));
                        builder.setGravity(ToolTip.GRAVITY_LEFT);
                        builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                        int finalI2 = i;
                        btnAyuda.setOnClickListener((View.OnClickListener) view -> {
                            Intent intent = new Intent(getContext(), EscanearActivity.class);
                            Bundle bc = new Bundle();
                            bc.putString("campoEscaneo", campos.get(finalI2).get("campo")); //id de solicitud
                            bc.putInt("requestCode", VariablesGlobales.ESCANEO_TARJETA);
                            intent.putExtras(bc); //Pase el parametro el Intent
                            startActivityForResult(intent,VariablesGlobales.ESCANEO_TARJETA);
                        });
                        btnAyuda.setOnLongClickListener((View.OnLongClickListener) view -> {
                            mToolTipsManager.show(builder.build());
                            return true;
                        });
                    }
                    //Le cae encima al valor default por el de la solicitud seleccionada
                    if(solicitudSeleccionada.size() > 0){
                        if(campos.get(i).get("modificacion").trim().equals("1")) {
                            if(solicitudSeleccionadaOld.size() > 0) {
                                et.setText(solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()).trim());
                            }
                        }else{
                            et.setText(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim());
                        }
                        if (!modificable) {
                            et.setEnabled(false);
                            et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                        }
                    }
                    //metodos configurados en tabla
                    if(campos.get(i).get("llamado1").trim().contains("ReplicarValor")){
                        String[] split = campos.get(i).get("llamado1").trim().split("'");
                        if(split.length < 3)
                            split = campos.get(i).get("llamado1").trim().split("`");
                        if(split.length < 3)
                            split = campos.get(i).get("llamado1").trim().split("\"");
                        final String campoAReplicar = split[1];
                        if(!campos.get(i).get("campo").trim().equals("W_CTE-NAME1") && !campos.get(i).get("campo").trim().equals("W_CTE-NAME2") && !campos.get(i).get("campo").trim().equals("W_CTE-HOUSE_NUM1")) {
                            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        ReplicarValor(v, campoAReplicar);
                                        if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                            Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                        }
                                    }
                                }
                            });
                        }
                    }
                    //Campos de encabezado deben salir todos como deshabilitados en valor viejo
                    if(campos.get(i).get("modificacion").trim().equals("1") && campos.get(i).get("sup").trim().length() == 0){
                        et.setEnabled(false);
                        et.setBackground(getResources().getDrawable(R.drawable.textbackground_old,null));
                        listaCamposDinamicosEnca.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicosEnca.put(campos.get(i).get("campo").trim(),et);
                    }

                    final TextInputLayout label_old = new TextInputLayout(Objects.requireNonNull(getContext()));
                    label_old.setVisibility(View.GONE);
                    label_old.setHint(campos.get(i).get("descr")+" Actual");
                    label_old.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                    label_old.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);
                    final MaskedEditText et_old = new MaskedEditText(getContext(),null);
                    et_old.setVisibility(View.GONE);
                    et_old.setEnabled(false);

                    //Crear campo para valor viejo exclusivo.
                    if((campos.get(i).get("modificacion").trim().equals("2")  || campos.get(i).get("modificacion").trim().equals("11") || campos.get(i).get("modificacion").trim().equals("10")) && campos.get(i).get("sup").trim().length() == 0){
                        //textbox de valor viejo
                        TableRow.LayoutParams lp_old = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
                        lp_old.setMargins(0, 15, 0, 15);
                        et_old.setLayoutParams(lp_old);
                        et_old.setPadding(15, 5, 15, 5);
                        et_old.setBackground(getResources().getDrawable(R.drawable.textbackground_old,null));
                        //if(cliente != null && cliente.get(campos.get(i).get("campo")) != null)
                            //et_old.setText(cliente.get(campos.get(i).get("campo").trim()).getAsString());

                        Button btnAyudai=null;
                        TableRow.LayoutParams textolp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        TableRow.LayoutParams btnlp2 = new TableRow.LayoutParams(75, 75,1f);
                            textolp2.setMargins(0,0,5,0);
                            //label.setLayoutParams(textolp2);
                            //label_old.setLayoutParams(textolp2);
                            btnAyudai = new Button(getContext());
                            btnAyudai.setBackground(getResources().getDrawable(R.drawable.icon_ver_viejo,null));
                            btnlp2.setMargins(0,0,5,0);
                            btnAyudai.setLayoutParams(btnlp2);
                            btnAyudai.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            btnAyudai.setForegroundGravity(GRAVITY_CENTER);

                        btnAyudai.setOnTouchListener(new View.OnTouchListener()
                            {
                                @Override
                                public boolean onTouch(View v, MotionEvent event)
                                {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        label.setVisibility(View.GONE);
                                        et.setVisibility(View.GONE);
                                        label_old.setVisibility(View.VISIBLE);
                                        et_old.setVisibility(View.VISIBLE);
                                        return true;
                                    }else if (event.getAction() == MotionEvent.ACTION_UP){
                                        label.setVisibility(View.VISIBLE);
                                        et.setVisibility(View.VISIBLE);
                                        label_old.setVisibility(View.GONE);
                                        et_old.setVisibility(View.GONE);
                                        return true;
                                    }else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                                        label.setVisibility(View.VISIBLE);
                                        et.setVisibility(View.VISIBLE);
                                        label_old.setVisibility(View.GONE);
                                        et_old.setVisibility(View.GONE);
                                        return true;
                                    }

                                    // TODO Auto-generated method stub
                                    return false;
                                }
                            });

                        if(btnAyudai != null)
                            fila.addView(btnAyudai);

                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(),et_old);
                        //Le cae encima al valor default por el de la solicitud seleccionada del valor viejo
                        if(solicitudSeleccionadaOld.size() > 0){
                            et_old.setText(solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()).trim());
                            if(!modificable){
                                et.setEnabled(false);
                                et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled,null));
                            }

                        }
                    }

                    label.addView(et);
                    fila.addView(label);
                    if(et_old != null) {
                        label_old.addView(et_old);
                        fila.addView(label_old);
                    }
                    if(btnAyuda != null)
                        fila.addView(btnAyuda);
                    ll.addView(fila);


                    if(campos.get(i).get("campo").trim().equals("W_CTE-ZZCRMA_LAT") || campos.get(i).get("campo").trim().equals("W_CTE-ZZCRMA_LONG")){
                        et.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.icon_location,null), null, null,null);
                        et.setCompoundDrawablePadding(16);
                        et.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);

                        et.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                final int DRAWABLE_LEFT = 0;
                                final int DRAWABLE_TOP = 1;
                                final int DRAWABLE_RIGHT = 2;
                                final int DRAWABLE_BOTTOM = 3;

                                if(event.getAction() == MotionEvent.ACTION_UP) {
                                    if(event.getRawX() <= ((et.getLeft()+75) + et.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())*2) {
                                        Toasty.info(getContext(),"Refrescando ubicacion..").show();
                                        LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                                        autoPineo.startLocationUpdates();
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });
                        et.setText(et.getText().toString().replace(",","."));
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-COMENTARIOS")){
                        et.setSingleLine(false);
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        et.setMinLines(1);
                        et.setMaxLines(5);
                        et.setVerticalScrollBarEnabled(true);
                        et.setMovementMethod(ScrollingMovementMethod.getInstance());
                        et.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        et.setGravity(INDICATOR_GRAVITY_TOP);
                        if(solicitudSeleccionada.size() > 0 && (!solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Nuevo") && !solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Incompleto"))) {
                            et.setText("");

                            RelativeLayout rl = new RelativeLayout(getContext());
                            CoordinatorLayout.LayoutParams rlp = new CoordinatorLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            rl.setLayoutParams(rlp);
                            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) rl.getLayoutParams();
                            params.setBehavior(new AppBarLayout.ScrollingViewBehavior(getContext(), null));

                            tb_comentarios.setColumnCount(4);
                            tb_comentarios.setHeaderBackgroundColor(getResources().getColor(R.color.colorHeaderTableView,null));
                            tb_comentarios.setHeaderElevation(2);
                            LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            tb_comentarios.setLayoutParams(hlp);

                            if(solicitudSeleccionada.size() > 0){
                                comentarios.clear();
                                comentarios = mDBHelper.getComentariosDB(idForm);
                            }
                            //Adaptadores
                            if(comentarios != null) {
                                tb_comentarios.getLayoutParams().height = tb_comentarios.getLayoutParams().height + (comentarios.size() * alturaFilaTableView*2);
                                tb_comentarios.setDataAdapter(new ComentarioTableAdapter(getContext(), comentarios));
                            }
                            String[] headers = ((ComentarioTableAdapter) tb_comentarios.getDataAdapter()).getHeaders();
                            SimpleTableHeaderAdapter sta = new SimpleTableHeaderAdapter(getContext(), headers);
                            sta.setPaddings(5,15,5,15);
                            sta.setTextSize(12);
                            sta.setTextColor(getResources().getColor(R.color.white,null));
                            sta.setTypeface(Typeface.BOLD);
                            sta.setGravity(GRAVITY_CENTER);

                            tb_comentarios.setHeaderAdapter(sta);
                            tb_comentarios.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(getResources().getColor(R.color.white,null), getResources().getColor(R.color.backColor,null)));

                            //Necesario para el nested scrolling del tableview
                            final List<View> tocables = tb_comentarios.getFocusables(View.FOCUS_FORWARD);
                            for(int x=0; x < tocables.size(); x++) {
                                final int finalX = x;
                                tocables.get(x).setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                        v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                                        return false;
                                    }
                                });
                            }

                            rl.addView(tb_comentarios);
                            ll.addView(rl);
                        }
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-DATAB")){
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String fechaSistema = df.format(c);
                        et.setText(fechaSistema);
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-SMTP_ADDR")) {
                        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    correoValidado = isValidEmail(v);
                                    if(VariablesGlobales.ComentariosAutomaticos()  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }
                            }
                        });
                    }
                    if (!campos.get(i).get("modificacion").trim().equals("1")) {
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), et);
                    }
                    if(campos.get(i).get("obl")!= null && campos.get(i).get("obl").trim().length() > 0 && !campos.get(i).get("modificacion").equals("1") ){
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                        //if(cliente != null && cliente.get(campos.get(i).get("campo")) != null && cliente.get(campos.get(i).get("campo").trim()).getAsString().length() == 0) {
                            //et.setError("El campo es obligatorio!");
                        //}
                    }
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }

                    //Excepciones de visualizacion y configuracionde campos dados por la tabla ConfigCampos
                    int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                    if(excepcion >= 0) {
                        HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                        Validaciones.ejecutarExcepcion(getContext(),et,label,configExcepcion,listaCamposObligatorios,campos.get(i));
                        int excepcionxAgencia = -1;
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")) != null && !((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId().equals(""))
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if(((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")) != null && !((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId().equals(""))
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if (excepcionxAgencia >= 0) {
                            HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                            Validaciones.ejecutarExcepcion(getContext(),et,label,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i));
                        }
                    }
                    //if(cliente != null && cliente.get(campos.get(i).get("campo")) != null)
                        //et.setText(cliente.get(campos.get(i).get("campo").trim()).getAsString());

                    if(VariablesGlobales.ComentariosAutomaticos() && !campos.get(finalI).get("campo").trim().equals("W_CTE-COMENTARIOS")  && campos.get(finalI).get("comentario_auto").trim().equals("1")){
                        if(et.getOnFocusChangeListener() == null) {
                            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        Validaciones.ComentariosAutomaticos(getContext(),((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS")),mapeoCamposDinamicos.get(campos.get(finalI).get("campo").trim()),mapeoCamposDinamicosOld.get(campos.get(finalI).get("campo").trim()),campos.get(finalI).get("descr").trim());
                                    }
                                }
                            });
                        }
                    }
                }

                seccionAnterior = campos.get(i).get("id_seccion").trim();

            }
            //Si estan los campos de Latitud y Longitud, activar el pineo automatico (W_CTE-ZZCRMA_LAT,W_CTE-ZZCRMA_LONG)
            //Descomentar si se quiere PINEO Automatico de coordenadas al entrar a una solicitud de Inclusion
            /*if(listaCamposDinamicos.contains("W_CTE-ZZCRMA_LAT") && listaCamposDinamicos.contains("W_CTE-ZZCRMA_LONG") && solicitudSeleccionada.size() == 0){
                LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                autoPineo.startLocationUpdates();
            }*/

            //Si es la pestana de adicionales ("Z") le agregamos el combo de seleccion de Aprobador de la siguiente etapa en el flujo.
            if(pestana.equals("Z")){
                TextView label = new TextView(getContext());
                label.setText("Enviar al Aprobador");
                label.setTextAppearance(R.style.AppTheme_TextFloatLabelAppearance);
                LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lpl.setMargins(35, 5, 0, 0);
                label.setPadding(0,0,0,0);
                label.setLayoutParams(lpl);

                final Spinner combo = new Spinner(getContext(), Spinner.MODE_DROPDOWN);
                combo.setTag("Aprobador");

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, -10, 0, 25);
                combo.setPadding(0,0,0,0);
                combo.setLayoutParams(lp);
                combo.setPopupBackgroundResource(R.drawable.menu_item);

                String id_flujo = db.getIdFlujoDeTipoSolicitud(tipoSolicitud);

                ArrayList<OpcionSpinner> opciones = db.getDatosCatalogoParaSpinner("aprobadores"," fxp.id_Flujo = "+id_flujo);

                // Creando el adaptador(opciones) para el comboBox deseado
                ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, opciones);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                // attaching data adapter to spinner
                Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
                combo.setBackground(d);
                combo.setAdapter(dataAdapter);
                if(solicitudSeleccionada.size() == 0) {
                    if(dataAdapter.getCount() > 1) {
                        combo.setSelection(0);
                    }else{
                        combo.setSelection(0);
                    }
                }else{
                    combo.setSelection(VariablesGlobales.getIndex(combo,solicitudSeleccionada.get(0).get("SIGUIENTE_APROBADOR").trim()));
                    if(!solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Nuevo") && !solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Incidencia") && !solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Modificado")){
                        combo.setEnabled(false);
                    }
                }
                mapeoCamposDinamicos.put("SIGUIENTE_APROBADOR",combo);
                ll.addView(label);
                ll.addView(combo);

                if(tipoSolicitud.equals("26")) {
                    //Check Box para la aceptacion de las politicas de privacidad
                    final CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText("Aceptar Politicas de Privacidad");
                    if (solicitudSeleccionada.size() > 0) {
                        checkbox.setChecked(true);
                        checkbox.setEnabled(false);
                        if (!modificable) {
                            checkbox.setEnabled(false);
                        }
                        checkbox.getButtonDrawable().jumpToCurrentState();
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_privacy,null), null);
                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Aceptacion(v);
                            if(((CheckBox) v).isChecked())
                                ((CheckBox) v).setChecked(false);
                            else
                                ((CheckBox) v).setChecked(true);
                            ((CheckBox) v).getButtonDrawable().jumpToCurrentState();
                        }
                    });

                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked}, // unchecked
                                    new int[]{android.R.attr.state_checked} , // checked
                            },
                            new int[]{
                                    Color.parseColor("#110000"),
                                    Color.parseColor("#00aa00"),
                            }
                    );

                    CompoundButtonCompat.setButtonTintList(checkbox,colorStateList);
                    mapeoCamposDinamicos.put("politica",checkbox);
                }
                if(tipoSolicitud.equals("46")) {
                    //Check Box para la aceptacion de las politicas de privacidad
                    final CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText("Acta de Entrega de Tarjeta de Regalo");
                    if (solicitudSeleccionada.size() > 0) {
                        checkbox.setChecked(true);
                        checkbox.setEnabled(false);
                        if (!modificable) {
                            checkbox.setEnabled(false);
                        }
                        checkbox.getButtonDrawable().jumpToCurrentState();
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_privacy,null), null);
                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AceptacionTarjeta(v);
                            if(((CheckBox) v).isChecked())
                                ((CheckBox) v).setChecked(false);
                            else
                                ((CheckBox) v).setChecked(true);
                            ((CheckBox) v).getButtonDrawable().jumpToCurrentState();
                        }
                    });

                    ColorStateList colorStateList = new ColorStateList(
                            new int[][]{
                                    new int[]{-android.R.attr.state_checked}, // unchecked
                                    new int[]{android.R.attr.state_checked} , // checked
                            },
                            new int[]{
                                    Color.parseColor("#110000"),
                                    Color.parseColor("#00aa00"),
                            }
                    );

                    CompoundButtonCompat.setButtonTintList(checkbox,colorStateList);
                    mapeoCamposDinamicos.put("entregaTarjeta",checkbox);
                }
            }
        }

        private void ActualizarAprobadores() {
            String id_flujo = mDBHelper.getIdFlujoDeTipoSolicitud(tipoSolicitud);

            ArrayList<OpcionSpinner> opciones = mDBHelper.getDatosCatalogoParaSpinner("aprobadores"," fxp.id_flujo = "+id_flujo+"");
            // Creando el adaptador(opciones) para el comboBox deseado
            ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, opciones);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            // attaching data adapter to spinner
            Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
            Spinner aprobadores = (Spinner)mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR");
            //aprobadores.setBackground(d);
            if(aprobadores != null) {
                aprobadores.setAdapter(dataAdapter);
                if(solicitudSeleccionada.size() > 0){
                    aprobadores.setSelection(VariablesGlobales.getIndex(aprobadores,solicitudSeleccionada.get(0).get("SIGUIENTE_APROBADOR").toString().trim()));
                }
            }
        }

        private void Aceptacion(View v) {
            Intent intent;
            switch (PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS","")){
                case "F443":
                    intent = new Intent(getContext(),FirmaActivity.class);
                    getActivity().startActivityForResult(intent,100);
                    break;
                case "F445":
                    intent = new Intent(getContext(),FirmaActivity.class);
                    getActivity().startActivityForResult(intent,100);
                    break;
                case "F451":
                    intent = new Intent(getContext(),FirmaActivity.class);
                    getActivity().startActivityForResult(intent,100);
                    break;
                default:
                    intent = new Intent(getContext(),FirmaActivity.class);
                    getActivity().startActivityForResult(intent,100);
            }
        }
        private void AceptacionTarjeta(View v) {
            String name12 = ((MaskedEditText)mapeoCamposDinamicosEnca.get("W_CTE-NAME1")) != null?((MaskedEditText)mapeoCamposDinamicosEnca.get("W_CTE-NAME1")).getText().toString():"";
            String name34 = ((MaskedEditText)mapeoCamposDinamicosEnca.get("W_CTE-NAME3")) != null?((MaskedEditText)mapeoCamposDinamicosEnca.get("W_CTE-NAME3")).getText().toString():"";
            String numeroTarjeta = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_NUM_TARJETA")) != null?((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_NUM_TARJETA")).getText().toString():"";
            String montoTarjeta = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_MONTO_TARJETA")) != null?((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_MONTO_TARJETA")).getText().toString():"";
            String cedulaRecibe = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_CEDULA_RECIBE")) != null?((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_CEDULA_RECIBE")).getText().toString():"";
            String nombreRecibe = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_NOMBRE_RECIBE")) != null?((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-IN_NOMBRE_RECIBE")).getText().toString():"";
            String telefonoRecibe = "";
            String conceptoEntrega = "";
            String periodoValidez ="";
            if(((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER")) != null)
                telefonoRecibe = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER")).getText().toString().trim();
            if(((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER2")) != null && ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER2")).getText().toString().trim() != "")
                telefonoRecibe += " / "+((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER2")).getText().toString();
            if(((Spinner)mapeoCamposDinamicos.get("W_CTE-IN_CONCEPTO_ENTREGA")) != null && ((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-IN_CONCEPTO_ENTREGA")).getSelectedItem()).getId() != "")
                conceptoEntrega = ((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-IN_CONCEPTO_ENTREGA")).getSelectedItem()).getName().split(" - ")[1];
            if(((Spinner)mapeoCamposDinamicos.get("W_CTE-IN_PERIODO_VALIDEZ")) != null && ((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-IN_PERIODO_VALIDEZ")).getSelectedItem()).getId() != "")
                periodoValidez = ((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-IN_PERIODO_VALIDEZ")).getSelectedItem()).getName().split(" - ")[1];
            String mensajeError = "";
            //Validar datos llenos para el llenado automatico del documento a firmar
            if(numeroTarjeta.length() == 0){
                mensajeError += "- Número de Tarjeta\n";
            }
            if(montoTarjeta.length() == 0){
                mensajeError += "- Monto de Tarjeta\n";
            }
            if(cedulaRecibe.length() == 0){
                mensajeError += "- Cédula persona que recibe\n";
            }
            if(nombreRecibe.length() == 0){
                mensajeError += "- Nombre persona que recibe\n";
            }
            if(conceptoEntrega.length() == 0){
                mensajeError += "- Concepto de entrega\n";
            }
            if(periodoValidez.length() == 0){
                mensajeError += "- Periodo de validez\n";
            }
            if(telefonoRecibe.length() == 0){
                mensajeError += "- Teléfono\n";
            }
            if(mensajeError != ""){
                mensajeError = "Llene los siguientes campos obligatorios para la firma:\n\n"+mensajeError;
                Toasty.error(getContext(),mensajeError).show();
                return;
            }
            Intent intent = new Intent(getContext(),FirmaTarjetasActivity.class);
            intent.putExtra("numeroTarjeta",numeroTarjeta);
            intent.putExtra("montoTarjeta",montoTarjeta);
            intent.putExtra("codigoCliente",codigoCliente);
            intent.putExtra("name12",name12);
            intent.putExtra("name34",name34);
            intent.putExtra("cedulaRecibe",cedulaRecibe);
            intent.putExtra("nombreRecibe",nombreRecibe);
            intent.putExtra("telefonoRecibe",telefonoRecibe);
            intent.putExtra("conceptoEntrega",conceptoEntrega);
            intent.putExtra("periodoValidez",periodoValidez);
            intent.putExtra("indicadorFirma","TarjetaRegalo");

            getActivity().startActivityForResult(intent,100);
        }

        public void DesplegarBloque(DataBaseHelper db, View _ll, HashMap<String, String> campo) {
            int height = alturaFilaTableView;
            TextView empty_data = new TextView(getContext());
            empty_data.setText(R.string.texto_sin_datos);
            empty_data.setBackground(getResources().getDrawable(R.color.backColor,null));
            int colorEvenRows = getResources().getColor(R.color.white,null);
            int colorOddRows = getResources().getColor(R.color.backColor,null);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout ll = (LinearLayout)_ll;
            LinearLayout.LayoutParams hlp;
            String[] headers;
            SimpleTableHeaderAdapter sta;

            CardView seccion_layout = new CardView(Objects.requireNonNull(getContext()));

            TextView seccion_header = new TextView(getContext());
            seccion_header.setAllCaps(true);
            seccion_header.setText(campo.get("descr").trim());
            seccion_header.setLayoutParams(tlp);
            seccion_header.setPadding(10, 0, 0, 0);
            seccion_header.setTextColor(getResources().getColor(R.color.white, null));
            seccion_header.setTextSize(10);
            seccion_header.setTextAlignment(TEXT_ALIGNMENT_CENTER);

            Button btnAddBloque = new Button(getContext());
            LinearLayout.LayoutParams tam_btn = new LinearLayout.LayoutParams(60,60);

            btnAddBloque.setLayoutParams(tam_btn);
            btnAddBloque.setBackground(getResources().getDrawable(R.drawable.icon_solicitud,null));

            seccion_layout.addView(btnAddBloque);

            //LinearLayout seccion_layout = new LinearLayout(getContext());
            LinearLayout.LayoutParams hhlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            hhlp.setMargins(0, 25, 0, 0);

            seccion_layout.setLayoutParams(hhlp);
            seccion_layout.setBackground(getResources().getDrawable(R.color.colorPrimary, null));
            seccion_layout.setPadding(5, 5, 5, 5);
            seccion_layout.addView(seccion_header);
            if(!campo.get("campo").trim().equals("W_CTE-HORARIOS") && !campo.get("campo").trim().equals("W_CTE-VISITAS"))
                ll.addView(seccion_layout);
            if(campo.get("campo").trim().equals("W_CTE-VISITAS")) {
                ll_visitas.removeAllViews();
                ll_visitas.addView(seccion_layout);
            }
            RelativeLayout rl = new RelativeLayout(getContext());
            CoordinatorLayout.LayoutParams rlp = new CoordinatorLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rl.setLayoutParams(rlp);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) rl.getLayoutParams();
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior(getContext(), null));

            switch(campo.get("campo").trim()){
                case "W_CTE-CONTACTOS":
                    //bloque_contacto = tb_contactos;
                    if(modificable) {
                        seccion_header.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                displayDialogContacto(getContext(), null);
                            }
                        });
                        btnAddBloque.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                displayDialogContacto(getContext(), null);
                            }
                        });
                    }
                    tb_contactos.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

                    tb_contactos.setLayoutParams(hlp);

                    if(solicitudSeleccionada.size() > 0){
                        contactosSolicitud.clear();
                        contactosSolicitud_old.clear();
                        contactosSolicitud = mDBHelper.getContactosDB(idSolicitud);
                        contactosSolicitud_old = mDBHelper.getContactosOldDB(idSolicitud);
                    }
                    //Adaptadores
                    if(contactosSolicitud != null) {
                        ContactoTableAdapter stda = new ContactoTableAdapter(getContext(), contactosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_contactos.setDataAdapter(stda);
                        tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height+(contactosSolicitud.size()*(alturaFilaTableView));
                    }

                    headers = ((ContactoTableAdapter)tb_contactos.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10,5,10,5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white,null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    tb_contactos.setHeaderAdapter(sta);
                    tb_contactos.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    rl.addView(tb_contactos);
                    ll.addView(rl);
                    break;
                case "W_CTE-IMPUESTOS":
                    de.codecrafters.tableview.TableView<Impuesto> bloque_impuesto;
                    tb_impuestos.removeDataClickListener(null);
                    tb_impuestos.removeDataLongClickListener(null);
                    bloque_impuesto = tb_impuestos;
                    btnAddBloque.setVisibility(INVISIBLE);
                    /*seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogImpuesto(getContext(),null);
                        }
                    });
                    btnAddBloque.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogImpuesto(getContext(),null);
                        }
                    });*/
                    bloque_impuesto.setColumnCount(4);
                    bloque_impuesto.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_impuesto.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_impuesto.setLayoutParams(hlp);

                    if(solicitudSeleccionada.size() > 0){
                        impuestosSolicitud.clear();
                        impuestosSolicitud_old.clear();
                        impuestosSolicitud = mDBHelper.getImpuestosDB(idSolicitud);
                        impuestosSolicitud_old = mDBHelper.getImpuestosOldDB(idSolicitud);
                    }
                    //Adaptadores
                    if(impuestosSolicitud != null) {
                        tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(getContext(), impuestosSolicitud));
                        tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height+(impuestosSolicitud.size()*(alturaFilaTableView));
                    }
                    headers = ((ImpuestoTableAdapter)bloque_impuesto.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10,5,10,5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white,null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    bloque_impuesto.setHeaderAdapter(sta);
                    bloque_impuesto.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    rl.addView(bloque_impuesto);
                    ll.addView(rl);
                    break;
                case "W_CTE-INTERLOCUTORES":
                    de.codecrafters.tableview.TableView<Interlocutor> bloque_interlocutor;
                    bloque_interlocutor = tb_interlocutores;
                    btnAddBloque.setVisibility(INVISIBLE);
                    /*btnAddBloque.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogInterlocutor(getContext(),null);
                        }
                    });*/
                    seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //displayDialogInterlocutor(getContext(),null);
                        }
                    });
                    bloque_interlocutor.setColumnCount(3);
                    bloque_interlocutor.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_interlocutor.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_interlocutor.setLayoutParams(hlp);


                    //ArrayList<Interlocutor> listaInterlocutores = db.getInterlocutoresPais();
                    //interlocutoresSolicitud.addAll(listaInterlocutores);
                    if(solicitudSeleccionada.size() > 0){
                        interlocutoresSolicitud.clear();
                        interlocutoresSolicitud_old.clear();
                        interlocutoresSolicitud = mDBHelper.getInterlocutoresDB(idSolicitud);
                        interlocutoresSolicitud_old = mDBHelper.getInterlocutoresOldDB(idSolicitud);
                    }
                    //Adaptadores
                    if(interlocutoresSolicitud != null) {
                        //if(tipoSolicitud.equals("1") || tipoSolicitud.equals("6")) {
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height + (interlocutoresSolicitud.size() * alturaFilaTableView);
                            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(getContext(), interlocutoresSolicitud));
                        //}
                    }
                    headers = ((InterlocutorTableAdapter)bloque_interlocutor.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10,5,10,5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white,null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    bloque_interlocutor.setHeaderAdapter(sta);
                    bloque_interlocutor.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    rl.addView(bloque_interlocutor);
                    ll.addView(rl);
                    break;
                case "W_CTE-BANCOS":
                    de.codecrafters.tableview.TableView<Banco> bloque_banco;
                    bloque_banco = tb_bancos;
                    if(modificable) {
                        btnAddBloque.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                displayDialogBancos(getContext(), null);
                            }
                        });
                        seccion_header.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                displayDialogBancos(getContext(), null);
                            }
                        });
                    }
                    bloque_banco.setColumnCount(5);
                    bloque_banco.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_banco.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_banco.setLayoutParams(hlp);

                    if(solicitudSeleccionada.size() > 0){
                        bancosSolicitud.clear();
                        bancosSolicitud_old.clear();
                        bancosSolicitud = mDBHelper.getBancosDB(idSolicitud);
                        bancosSolicitud_old = mDBHelper.getBancosOldDB(idSolicitud);
                    }
                    //Adaptadores
                    if(bancosSolicitud != null) {
                        BancoTableAdapter stda = new BancoTableAdapter(getContext(), bancosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        bloque_banco.setDataAdapter(stda);
                        tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height + (bancosSolicitud.size() * alturaFilaTableView);
                    }
                    headers = ((BancoTableAdapter)bloque_banco.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10,5,10,5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white,null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    bloque_banco.setHeaderAdapter(sta);
                    bloque_banco.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    rl.addView(bloque_banco);
                    ll.addView(rl);
                    break;
                case "W_CTE-VISITAS":
                    tb_visitas.setColumnCount(4);
                    tb_visitas.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    tb_visitas.setHeaderElevation(1);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    tb_visitas.setLayoutParams(hlp);
                    btnAddBloque.setVisibility(INVISIBLE);

                    Button btnRefreshBloque = new Button(getContext());
                    LinearLayout.LayoutParams tam_btnv = new LinearLayout.LayoutParams(30,30);

                    btnRefreshBloque.setLayoutParams(tam_btnv);
                    btnRefreshBloque.setBackground(getResources().getDrawable(R.drawable.icon_refresh,null));

                    seccion_layout.addView(btnRefreshBloque);
                    if(modificable) {
                        btnRefreshBloque.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogHandler appdialog = new DialogHandler();
                                appdialog.Confirm(getActivity(), "Refrescar", "Esta seguro que desea resetear los tipos de visita del cliente? Debe volver a configurar el VP.", "NO", "SI", new ResetearVisitas(getContext(), getActivity()));
                                //return false;
                            }
                        });
                    }
                    if(solicitudSeleccionada.size() > 0){
                        visitasSolicitud.clear();
                        visitasSolicitud_old.clear();
                        visitasSolicitud = mDBHelper.getVisitasDB(idSolicitud);
                        visitasSolicitud_old = mDBHelper.getVisitasOldDB(idSolicitud);
                        int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                        if(indiceReparto != -1)
                            rutaRepartoNuevaOriginal = visitasSolicitud.get(indiceReparto).getRuta();
                    }
                    //Adaptadores
                    if(visitasSolicitud != null) {
                        VisitasTableAdapter stda = new VisitasTableAdapter(getContext(), visitasSolicitud);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height+(visitasSolicitud.size()*alturaFilaTableView);
                        tb_visitas.setDataAdapter(stda);
                        tb_visitas.getDataAdapter().notifyDataSetChanged();
                    }
                    headers = ((VisitasTableAdapter)tb_visitas.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10,5,10,5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white,null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    tb_visitas.setHeaderAdapter(sta);
                    tb_visitas.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    //if(tb_visitas.getParent() != null)
                        //((ViewGroup)tb_visitas.getParent()).removeView(tb_visitas);
                    ///rl.addView(tb_visitas);
                    ll_visitas.addView(tb_visitas);
                    //Textos de dias de visita
                    //Desplegar textos para las secuencias de los dias de visita de la preventa.
                    //TODO Generar segun la Modalidad de venta seleccionada para usuarios tipo jefe de ventas y no preventas
                    final String[] diaLabel = {"L","K","M","J","V","S","D"};
                    final String[] diaLabelVisible = {"L","M","R","J","V","S","D"};
                    Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                    String modalidad = "";
                    String tipoVisita = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV");//"ZPV";
                    if(comboModalidad != null)
                    modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();

                    int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPV");
                    int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZJV");
                    int indiceKafe = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZKV");
                    int indiceTeleventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZTV");
                    int indiceAutoventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZAT");
                    int indiceMixta = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZRM");
                    //int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDD");
                    int totalvp_preventa = visitasSolicitud.size();
                    /*if(indicePreventa != -1 && !tipoVisita.equals("ZAT")){
                        totalvp_preventa++;
                    }
                    if(indiceEspecializada != -1){
                        totalvp_preventa++;
                    }
                    if(indiceTeleventa != -1){
                        totalvp_preventa++;
                    }
                    if(indiceAutoventa != -1){
                        totalvp_preventa++;
                    }
                    if(indiceMixta != -1){
                        totalvp_preventa++;
                    }*/
                    if(visitasSolicitud.size() == 0){
                        totalvp_preventa = 7;
                    }
                    String tipoVisitaActual = tipoVisita;
                    for (int i = 0; i < totalvp_preventa; i++) {
                        if(visitasSolicitud.size() == 0) {
                            if (i == 0) {
                                tipoVisitaActual = "ZPV";
                            }
                            if (i == 1) {
                                tipoVisitaActual = "ZJV";
                            }
                            if (i == 2) {
                                tipoVisitaActual = "ZTV";
                            }
                            if (i == 3) {
                                tipoVisitaActual = "ZRM";
                            }
                            if (i == 4) {
                                tipoVisitaActual = "ZAT";
                            }
                            if (i == 5) {
                                tipoVisitaActual = "ZKV";
                            }
                            if (i == 6) {
                                tipoVisitaActual = "ZDY";
                            }
                        }else{
                            tipoVisitaActual = visitasSolicitud.get(i).getVptyp().trim();
                            if(tipoVisitaActual.equals("ZDD")){
                                continue;
                            }
                        }
                        if(mDBHelper.ExisteTipoVisita(tipoVisitaActual) && mDBHelper.ExisteEnVisitPlanActual(modalidad, tipoVisitaActual)) {
                            CardView seccion_visitas = new CardView(Objects.requireNonNull(getContext()));
                            mapeoVisitas.put(tipoVisitaActual, seccion_visitas);

                            TextView header_visitas = new TextView(getContext());
                            header_visitas.setAllCaps(true);
                            header_visitas.setText("Dias de Visita Preventa " + tipoVisitaActual);
                            header_visitas.setLayoutParams(tlp);
                            header_visitas.setPadding(10, 0, 0, 0);
                            header_visitas.setTextColor(getResources().getColor(R.color.white, null));
                            header_visitas.setTextSize(10);

                            LinearLayout.LayoutParams hlpv = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            hlpv.setMargins(0, 25, 0, 15);
                            seccion_visitas.setLayoutParams(hlpv);
                            seccion_visitas.setBackground(getResources().getDrawable(R.color.colorPrimary, null));
                            seccion_visitas.setPadding(5, 5, 5, 5);

                            seccion_visitas.addView(header_visitas);
                            ll_visitas.addView(seccion_visitas);

                            TableLayout v_ll = new TableLayout(getContext());
                            LinearLayout.LayoutParams hlpll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            v_ll.setOrientation(LinearLayout.HORIZONTAL);
                            v_ll.setLayoutParams(hlpll);
                            TableRow tr = new TableRow(getContext());
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 6f));
                            tr.setPadding(0, 0, 0, 0);

                            TextInputEditText et_anterior = null;
                            for (int x = 0; x <= 6; x++) {
                                TextInputLayout label = new TextInputLayout(getContext());
                                label.setHint("" + diaLabelVisible[x]);
                                label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                                label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);
                                label.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorTextView, null)));
                                label.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                label.setPadding(0, 0, 0, 0);

                                final TextInputEditText et = new TextInputEditText(getContext());
                                mapeoCamposDinamicos.put(tipoVisitaActual + "_" + diaLabel[x], et);
                                et.setTag(tipoVisitaActual + "_" + diaLabel[x]);
                                et.setMaxLines(1);
                                et.setTextSize(16);
                                et.setId(Integer.parseInt("1000"+i+x));

                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, 10, 10, 10);
                                et.setPadding(1, 0, 1, 0);

                                et.setLayoutParams(lp);
                                if (!modificable || (!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "").equals(tipoVisitaActual)) || (!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "").equals(tipoVisitaActual) && mDBHelper.ExisteTipoVisita("ZRM"))) {
                                    et.setEnabled(false);
                                    et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                                }

                                if (solicitudSeleccionada.size() > 0) {
                                    int indiceActual = -1;
                                    if (visitasSolicitud.size() == 0) {
                                        if (i == 0)
                                            indiceActual = indicePreventa;
                                        if (i == 1)
                                            indiceActual = indiceEspecializada;
                                        if (i == 2)
                                            indiceActual = indiceTeleventa;
                                        if (i == 3)
                                            indiceActual = indiceKafe;
                                        if (i == 4)
                                            indiceActual = indiceMixta;
                                    } else {
                                        indiceActual = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, tipoVisitaActual);
                                    }
                                    switch (x) {
                                        case 0:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getLun_a()));
                                            break;
                                        case 1:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getMar_a()));
                                            break;
                                        case 2:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getMier_a()));
                                            break;
                                        case 3:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getJue_a()));
                                            break;
                                        case 4:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getVie_a()));
                                            break;
                                        case 5:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getSab_a()));
                                            break;
                                        case 6:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indiceActual).getDom_a()));
                                            break;
                                    }
                                    //Esconder el domingo si el tipo NO ES ZDY
                                    if (x == 6 && (!tipoVisitaActual.equals("ZDY"))) {
                                        et.setVisibility(View.GONE);
                                        label.setVisibility(View.GONE);
                                    }
                                    if(x == 6 && et.getText().length() > 0 ){
                                        et.setVisibility(View.VISIBLE);
                                        label.setVisibility(View.VISIBLE);
                                    }
                                }else {

                                    //Esconder el domingo si el tipo NO ES ZDY
                                    if (x == 6 && (!tipoVisitaActual.equals("ZDY"))) {
                                        et.setVisibility(View.GONE);
                                        label.setVisibility(View.GONE);
                                    }
                                }

                                //et.setPadding(20, 5, 20, 5);
                                Drawable d = getResources().getDrawable(R.drawable.textbackground_min_padding, null);
                                et.setBackground(d);
                                final int finalX = x;

                                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                                        String modalidad = "";
                                        String tipoVisita = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString();//"ZPV";
                                        modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();

                                        int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZPV");
                                        int indiceTeleventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZTV");
                                        int indiceAutoventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZAT");
                                        int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                        int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZJV");
                                        int indiceMixta = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZRM");

                                        final int finalIndicePreventa = indicePreventa;
                                        final int finalIndiceTeleventa = indiceTeleventa;
                                        final int finalIndiceAutoventa = indiceAutoventa;
                                        final int finalIndiceReparto = indiceReparto;
                                        final int finalIndiceEspecializada = indiceEspecializada;
                                        final int finalIndiceMixta = indiceMixta;

                                        if (!hasFocus) {
                                            int diaReparto = 0;
                                            int diasParaReparto = 1;
                                            Visitas visitaPreventa = null;
                                            if(finalIndicePreventa != -1 && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "").equals("ZPV") ) {
                                                visitaPreventa = visitasSolicitud.get(finalIndicePreventa);
                                                if (visitaPreventa.getKvgr4() != null)
                                                    diasParaReparto = Integer.valueOf(visitaPreventa.getKvgr4().replace("DA", ""));
                                                if ((finalX + diasParaReparto) > 5) {
                                                    diaReparto = ((finalX + diasParaReparto) - 6);
                                                } else {
                                                    diaReparto = (finalX + diasParaReparto);
                                                }
                                            }else if(finalIndiceTeleventa != -1 && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "").equals("ZTV")) {
                                                visitaPreventa = visitasSolicitud.get(finalIndiceTeleventa);
                                                if (visitaPreventa.getKvgr4() != null)
                                                    diasParaReparto = Integer.valueOf(visitaPreventa.getKvgr4().replace("DA", ""));
                                                if ((finalX + diasParaReparto) > 5) {
                                                    diaReparto = ((finalX + diasParaReparto) - 6);
                                                } else {
                                                    diaReparto = (finalX + diasParaReparto);
                                                }
                                            }else if(finalIndiceEspecializada != -1 && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "").equals("ZJV")) {
                                                visitaPreventa = visitasSolicitud.get(finalIndiceEspecializada);
                                                if (visitaPreventa.getKvgr4() != null)
                                                    diasParaReparto = Integer.valueOf(visitaPreventa.getKvgr4().replace("DA", ""));
                                                if ((finalX + diasParaReparto) > 5) {
                                                    diaReparto = ((finalX + diasParaReparto) - 6);
                                                } else {
                                                    diaReparto = (finalX + diasParaReparto);
                                                }
                                            }else if(finalIndiceAutoventa != -1) {
                                                visitaPreventa = visitasSolicitud.get(finalIndiceAutoventa);
                                                if (visitaPreventa.getKvgr4() != null)
                                                    diasParaReparto = Integer.valueOf(visitaPreventa.getKvgr4().replace("DA", ""));
                                                if ((finalX + diasParaReparto) > 5) {
                                                    diaReparto = ((finalX + diasParaReparto) - 6);
                                                } else {
                                                    diaReparto = (finalX + diasParaReparto);
                                                }
                                            }

                                            Visitas visitaMixta = null;
                                            if (finalIndiceMixta != -1) {
                                                visitaMixta = visitasSolicitud.get(finalIndiceMixta);
                                                visitaPreventa = visitasSolicitud.get(finalIndicePreventa);
                                                if (visitaMixta.getKvgr4() != null)
                                                    diasParaReparto = Integer.valueOf(visitaMixta.getKvgr4().replace("DA", ""));
                                                if ((finalX + diasParaReparto) > 5) {
                                                    diaReparto = ((finalX + diasParaReparto) - 6);
                                                } else {
                                                    diaReparto = (finalX + diasParaReparto);
                                                }
                                            }
                                            Visitas visitaReparto = null;
                                            if (!modalidad.equals("GV")) {
                                                visitaReparto = visitasSolicitud.get(finalIndiceReparto);
                                            }
                                            if (!((TextView) v).getText().toString().equals("") && Integer.valueOf(((TextView) v).getText().toString()) > 1339) {
                                                visitaPreventa.setValorDiaSegunIndice(finalX, getResources().getString(R.string.max_secuencia));
                                                if(finalIndiceMixta != -1) {
                                                    visitaMixta.setValorDiaSegunIndice(finalX, getResources().getString(R.string.max_secuencia));
                                                }

                                                if (!modalidad.equals("GV")) {
                                                    visitaReparto.setValorDiaSegunIndice(diaReparto, getResources().getString(R.string.max_secuencia));
                                                }
                                                ((TextView) v).setText(getResources().getString(R.string.max_secuencia));
                                                if (finalIndiceMixta != -1) {
                                                    copiarDia(et, finalIndiceMixta,finalIndicePreventa);
                                                }
                                                Toasty.warning(getContext(), R.string.error_max_secuencia).show();
                                            }

                                            //Si el valor es vacio, borrar si existe el dia
                                            if (((TextView) v).getText().toString().trim().replace("0", "").equals("")
                                                    && !VariablesGlobales.AceptarVisitaCero()) {
                                                if (((TextView) v).getText().toString().trim().length() > 0
                                                        && !VariablesGlobales.AceptarVisitaCero()) {
                                                    ((TextView) v).setText("");
                                                    Toasty.warning(getContext(), "La secuencia debe ser mayor a 0").show();
                                                }

                                                visitaPreventa.setValorDiaSegunIndice(finalX, "");
                                                if(finalIndiceMixta != -1) {
                                                    visitaMixta.setValorDiaSegunIndice(finalX, "");
                                                }

                                                if (!modalidad.equals("GV")) {
                                                    visitaReparto.setValorDiaSegunIndice(diaReparto, "");
                                                }
                                                if (finalIndiceMixta != -1) {
                                                    copiarDia(et, finalIndiceMixta,finalIndicePreventa);
                                                }
                                            } else {
                                                String secuenciaSAP = VariablesGlobales.SecuenciaToHora(((TextView) v).getText().toString());

                                                visitaPreventa.setValorDiaSegunIndice(finalX, secuenciaSAP);
                                                if(finalIndiceMixta != -1) {
                                                    visitaMixta.setValorDiaSegunIndice(finalX, secuenciaSAP);
                                                }

                                                if (!modalidad.equals("GV")) {
                                                    visitaReparto.setValorDiaSegunIndice(diaReparto, secuenciaSAP);
                                                }
                                                if (finalIndiceMixta != -1) {
                                                    copiarDia(et, finalIndiceMixta,finalIndicePreventa);
                                                }
                                            }

                                            if (!modalidad.equals("GV")) {
                                                RecalcularDiasDeReparto(getContext());
                                            }

                                            if(VariablesGlobales.ComentariosAutomaticos()){
                                                MaskedEditText comentariosAuto = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS"));
                                                if((finalIndicePreventa != -1 && visitasSolicitud.get(finalIndicePreventa).DiferenciaDeDiasDeVisita(visitasSolicitud_old.get(finalIndicePreventa)))
                                                || (finalIndiceTeleventa != -1 && visitasSolicitud.get(finalIndiceTeleventa).DiferenciaDeDiasDeVisita(visitasSolicitud_old.get(finalIndiceTeleventa)))
                                                || (finalIndiceEspecializada != -1 && visitasSolicitud.get(finalIndiceEspecializada).DiferenciaDeDiasDeVisita(visitasSolicitud_old.get(finalIndiceEspecializada)))
                                                || (finalIndiceMixta != -1 && visitasSolicitud.get(finalIndiceMixta).DiferenciaDeDiasDeVisita(visitasSolicitud_old.get(finalIndiceMixta)))
                                                || (finalIndiceAutoventa != -1 && visitasSolicitud.get(finalIndiceAutoventa).DiferenciaDeDiasDeVisita(visitasSolicitud_old.get(finalIndiceAutoventa)))
                                                || (finalIndiceReparto != -1 && visitasSolicitud.get(finalIndiceReparto).DiferenciaDeDiasDeVisita(visitasSolicitud_old.get(finalIndiceReparto)))){

                                                    if(!comentariosAuto.getText().toString().contains("CAMBIO DIAS DE VISITA")) {
                                                        if(comentariosAuto.getText().toString().trim().length() > 0)
                                                            comentariosAuto.append("\nCAMBIO DIAS DE VISITA");
                                                        else
                                                            comentariosAuto.append("CAMBIO DIAS DE VISITA");
                                                    }
                                                }else{
                                                    if(comentariosAuto.getText().toString().trim().contains("\nCAMBIO DIAS DE VISITA")){
                                                        comentariosAuto.setText(comentariosAuto.getText().toString().replace("\nCAMBIO DIAS DE VISITA",""));
                                                    }else{
                                                        comentariosAuto.setText(comentariosAuto.getText().toString().replace("CAMBIO DIAS DE VISITA",""));
                                                    }
                                                }
                                            }
                                        }




                                    }
                                    private void copiarDia(TextInputEditText et, int finalIndPreventa, int finalIndMixta) {
                                        if (et.getTag().toString().contains("ZRM")) {
                                            TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get(et.getTag().toString().replace("ZRM", "ZPV"));
                                            if(copiar != null)
                                                copiar.setText(et.getText());
                                            if(finalIndPreventa > 0 && finalIndMixta > 0 && visitasSolicitud.size() > 0) {
                                                String valor = visitasSolicitud.get(finalIndMixta).getValorDiaSegunIndice(finalX);
                                                visitasSolicitud.get(finalIndPreventa).setValorDiaSegunIndice(finalX, valor);
                                            }
                                        }
                                        if (et.getTag().toString().contains("ZPV") && mDBHelper.ExisteTipoVisita("ZRM")) {
                                            TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get(et.getTag().toString().replace("ZPV", "ZRM"));
                                            if(copiar != null)
                                                copiar.setText(et.getText());
                                            if(finalIndPreventa > 0 && finalIndMixta > 0 && visitasSolicitud.size() > 0) {
                                                String valor = visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(finalX);
                                                visitasSolicitud.get(finalIndMixta).setValorDiaSegunIndice(finalX, valor);
                                            }
                                        }
                                    }

                                });

                                if(et_anterior != null){
                                    et_anterior.setNextFocusForwardId(et.getId());
                                    et_anterior.setNextFocusRightId(et.getId());
                                    et_anterior.setNextFocusLeftId(et.getId());
                                    et_anterior.setNextFocusUpId(et.getId());
                                    et_anterior.setNextFocusDownId(et.getId());
                                }
                                if(x == 6){
                                    et_anterior.setImeOptions(EditorInfo.IME_ACTION_DONE);
                                    et_anterior.setNextFocusForwardId(0);
                                    et_anterior.setNextFocusRightId(0);
                                    et_anterior.setNextFocusLeftId(0);
                                    et_anterior.setNextFocusUpId(0);
                                    et_anterior.setNextFocusDownId(0);
                                }
                                et_anterior = et;

                                tr.addView(label);
                                label.addView(et);
                            }

                            v_ll.addView(tr);
                            if(v_ll.getParent() != null)
                                ((ViewGroup)v_ll.getParent()).removeView(v_ll);
                            ll_visitas.addView(v_ll);
                            if(ll_visitas.getParent() == null)
                                ll.addView(ll_visitas);
                        }
                    }
                break;
                case "W_CTE-ADJUNTOS":
                    tb_adjuntos.setColumnCount(3);
                    tb_adjuntos.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

                    tb_adjuntos.setLayoutParams(hlp);

                    if(solicitudSeleccionada.size() > 0){
                        if((idForm == null || idForm.equals("")) || solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Incidencia")|| solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Modificado"))
                            adjuntosSolicitud = mDBHelper.getAdjuntosDB(idSolicitud);
                        else
                            adjuntosSolicitud = mDBHelper.getAdjuntosServidor(idForm);
                    }
                    if(modificable) {
                        btnAddBloque.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mPhotoUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                                try {
                                    getActivity().startActivityForResult(intent, 1);
                                } catch (ActivityNotFoundException e) {
                                    Log.e("tag", getResources().getString(R.string.no_activity));
                                }
                            }
                        });
                    }
                    //Adaptadores
                    if(adjuntosSolicitud != null) {
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(getContext(), adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*alturaFilaTableView);
                        tb_adjuntos.setDataAdapter(stda);
                    }
                    headers = ((AdjuntoTableAdapter)tb_adjuntos.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10,5,10,5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white,null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    tb_adjuntos.setHeaderAdapter(sta);
                    tb_adjuntos.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    if(tb_adjuntos.getParent() != null)
                        rl.addView(tb_adjuntos);

                    //Horizontal View de adjuntos
                    HorizontalScrollView hsv = new HorizontalScrollView(getContext());
                    ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsv, getContext(), getActivity(),adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

                    rl.addView(hsv);
                    ll.addView(rl);
                    mapeoCamposDinamicos.put("GaleriaAdjuntos", hsv);
                    break;
                case "W_CTE-HORARIOS":
                    final String[] dias = {"Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo"};
                    final String[] dias_prefijo = {"mo", "di", "mi", "do", "fr", "sa", "so"};
                    final String[] dias_sufijo = {"ab1", "bi1", "ab2", "bi2"};
                    if(solicitudSeleccionada.size() > 0){
                        horariosSolicitud = mDBHelper.getHorariosDB(GUID);
                        if(horariosSolicitud.size() == 0){
                            horariosSolicitud.add(new Horarios(GUID,"00:00:00"));
                        }
                    }
                    CardView seccion_horarios = new CardView(Objects.requireNonNull(getContext()));

                    TextView header_horarios = new TextView(getContext());
                    header_horarios.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    header_horarios.setAllCaps(true);
                    header_horarios.setText("Horarios para Reparto");
                    header_horarios.setLayoutParams(tlp);
                    header_horarios.setPadding(10, 5, 0, 5);
                    header_horarios.setTextColor(getResources().getColor(R.color.white, null));
                    header_horarios.setTextSize(14);

                    LinearLayout.LayoutParams hlph = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    hlph.setMargins(0, 25, 0, 5);
                    seccion_horarios.setLayoutParams(hlph);
                    seccion_horarios.setBackground(getResources().getDrawable(R.color.colorPrimary, null));
                    seccion_horarios.setPadding(5, 5, 5, 5);

                    seccion_horarios.addView(header_horarios);
                    ll.addView(seccion_horarios);

                    TableLayout h_tl = new TableLayout(getContext());
                    TableLayout.LayoutParams h_tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,1.0f);
                    h_tl.setOrientation(LinearLayout.HORIZONTAL);
                    h_tl.setLayoutParams(h_tlp);

                    TableLayout h_tl2 = new TableLayout(getContext());
                    TableLayout.LayoutParams h_tlp2 = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                    h_tl2.setOrientation(LinearLayout.HORIZONTAL);
                    h_tl2.setLayoutParams(h_tlp2);

                    TableRow tr_header = new TableRow(getContext());

                    tr_header.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                    tr_header.setBackgroundColor(getResources().getColor(R.color.red,null));

                    TextView label_empty = new TextView(getContext());
                    label_empty.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    label_empty.setTextColor(getResources().getColor(R.color.white, null));
                    label_empty.setText(" Día ");
                    label_empty.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.1f));
                    label_empty.setPadding(0, 0, 0, 0);
                    tr_header.addView(label_empty);

                    TextView label_manana = new TextView(getContext());
                    label_manana.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    label_manana.setTextColor(getResources().getColor(R.color.white, null));
                    //label_manana.setBackgroundColor(getResources().getColor(R.color.blue,null));
                    label_manana.setText("Por la mañana");
                    label_manana.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.45f));
                    label_manana.setPadding(0, 0, 0, 0);
                    tr_header.addView(label_manana);

                    TextView label_tarde = new TextView(getContext());
                    label_tarde.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                    label_tarde.setTextColor(getResources().getColor(R.color.white, null));
                    //label_tarde.setBackgroundColor(getResources().getColor(R.color.blue,null));
                    label_tarde.setText("Por la tarde");
                    label_tarde.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.45f));
                    label_tarde.setPadding(0, 0, 0, 0);
                    tr_header.addView(label_tarde);
                    h_tl2.addView(tr_header);
                    ll.addView(h_tl2);
                    for (int y = 0; y < dias.length; y++) {
                        TableRow tr_dia = new TableRow(getContext());
                        tr_dia.setLayoutParams(new TableRow.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                        TextView label = new TextView(getContext());
                        label.setTextAlignment(TEXT_ALIGNMENT_CENTER );
                        label.setTextColor(getResources().getColor(R.color.black, null));
                        label.setText("" + dias[y]+": ");
                        TableRow.LayoutParams tlp_l = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                        tlp_l.setMargins(0, 5, 0, 0);
                        label.setLayoutParams(tlp_l);
                        tr_dia.addView(label);

                        for (int z = 0; z < dias_sufijo.length; z++) {

                            final MaskedEditText et = new MaskedEditText(getContext(),null);
                            mapeoCamposDinamicos.put(dias_prefijo[y] + dias_sufijo[z], et);
                            et.setMaxLines(1);
                            et.setTextSize(16);
                            et.setHint("00:00:00");
                            et.setMask("##:##:00");
                            et.setTag(dias_prefijo[y] + dias_sufijo[z]);

                            et.setInputType(InputType.TYPE_CLASS_TEXT);
                            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,0.225f);
                            lp.setMargins(0, 10, 10, 10);
                            //et.setPadding(100, 0, 0, 0);
                            et.setLayoutParams(lp);
                            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (hasFocus) {
                                        // TODO Auto-generated method stub
                                        Calendar mcurrentTime = Calendar.getInstance();
                                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                        int minute = 0;
                                        TimePickerDialog mTimePicker;
                                        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                                if(et.getTag().toString().contains("ab1") || et.getTag().toString().contains("bi1")){
                                                    if(selectedHour > 18){
                                                        Toasty.warning(getContext(),"Rangos de horas en la mañana invalido! Solo horas entre 0 y 18 son permitidas.").show();
                                                        return;
                                                    }
                                                }
                                                if(et.getTag().toString().contains("ab2") || et.getTag().toString().contains("bi2")){
                                                    if(selectedHour < 6){
                                                        if(selectedHour > 0 && selectedMinute > 0) {
                                                            Toasty.warning(getContext(), "Rangos de horas de la tarde invalido! Solo horas entre 06 y 23 son permitidas").show();
                                                            return;
                                                        }
                                                    }
                                                }
                                                String mivalor = "00".substring(String.valueOf(selectedHour).length()) + String.valueOf(selectedHour) + ":" + "00".substring(String.valueOf(selectedMinute).length()) + String.valueOf(selectedMinute) + ":00";
                                                et.setText(mivalor);
                                                if(horariosSolicitud.size() == 0){
                                                    horariosSolicitud.add(new Horarios(GUID,"00:00:00"));
                                                }
                                                horariosSolicitud.get(0).actualizarCampo(et.getTag().toString(),mivalor);
                                            }
                                        }, hour, minute, true);//Yes 24 hour time
                                        mTimePicker.setTitle("Seleccione la hora");
                                        mTimePicker.show();
                                    }
                                }
                            });
                            et.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(v.isFocused()) {
                                        v.clearFocus();
                                        v.requestFocus();
                                    }
                                }
                            });

                            if (!modificable) {
                                et.setEnabled(false);
                                et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                            }
                            Drawable d = getResources().getDrawable(R.drawable.textbackground_min_padding, null);
                            et.setBackground(d);

                            if(horariosSolicitud.size() > 0){
                                et.setText(horariosSolicitud.get(0).getPorNombre(dias_prefijo[y] + dias_sufijo[z]));
                            }

                            tr_dia.addView(et);
                        }
                        //Icono para copiar a otros dias
                        TableRow.LayoutParams btnlp = new TableRow.LayoutParams(25, 25);
                        ImageView btnCopiar = new ImageView(getContext());
                        btnCopiar.setBackground(getResources().getDrawable(R.drawable.icon_copy, null));
                        btnlp.setMargins(0, 25, 5, 0);
                        btnCopiar.setLayoutParams(btnlp);
                        btnCopiar.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        btnCopiar.setForegroundGravity(GRAVITY_CENTER);

                        int finalY = y;
                        btnCopiar.setOnClickListener(view -> {
                            horariosSolicitud.get(0).copiarHorario(finalY);
                            for (int a = 0; a < dias_prefijo.length; a++) {
                                for (int b = 0; b < dias_sufijo.length; b++) {
                                    ((MaskedEditText)mapeoCamposDinamicos.get(dias_prefijo[a]+dias_sufijo[b])).setText(horariosSolicitud.get(0).getPorNombre(dias_prefijo[a]+dias_sufijo[b]));
                                }
                            }

                            //return;
                        });
                        tr_dia.addView(btnCopiar);
                        h_tl.addView(tr_dia);
                    }
                    ll.addView(h_tl);
                    break;
                case "W_CTE-NOTIFICANTES":
                    break;
            }
        }

    }

    //Pruebas para seccion de bloques
    public static void displayDialogMessage(Context context, String mensaje) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.message_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextView message = d.findViewById(R.id.message);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        Button cancelBtn= d.findViewById(R.id.cancelBtn);
        title.setText("Mensaje de sistema");
        message.setText(mensaje);
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }
    //Pruebas para seccion de bloques
    public static void displayDialogContacto(Context context, final Contacto seleccionado) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.contacto_dialog_layout);
        //ArrayList<HashMap<String, String>> columnMeta =  mDBHelper.getMetaData(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH());
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextInputEditText name1EditText = d.findViewById(R.id.name1EditTxt);
        final TextInputEditText namevEditText = d.findViewById(R.id.namevEditTxt);
        final TextInputEditText telf1EditText = d.findViewById(R.id.telf1EditTxt);
        final Spinner funcionSpinner = d.findViewById(R.id.funcionSpinner);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText(R.string.texto_modificar);
            title.setText(String.format(context.getResources().getString(R.string.palabras_2), context.getResources().getString(R.string.texto_modificar), context.getResources().getString(R.string.texto_contacto)));
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seleccionado != null)
                    contactosSolicitud.remove(seleccionado);
                Contacto nuevoContacto = new Contacto();
                nuevoContacto.setName1(name1EditText.getText().toString());
                nuevoContacto.setNamev(namevEditText.getText().toString());
                nuevoContacto.setTelf1(telf1EditText.getText().toString());
                nuevoContacto.setPafkt(((OpcionSpinner)funcionSpinner.getSelectedItem()).getId());
                nuevoContacto.setCountry(PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_LAND1",""));

                if(!nuevoContacto.validarObligatorios()){
                    Toasty.warning(v.getContext(), "Todos los campos son obligatorios!").show();
                    return;
                }
                try {
                    contactosSolicitud.add(nuevoContacto);
                    name1EditText.setText("");
                    namevEditText.setText("");
                    telf1EditText.setText("");
                    funcionSpinner.setSelection(0);
                    if(contactosSolicitud != null) {
                        tb_contactos.setDataAdapter(new ContactoTableAdapter(v.getContext(), contactosSolicitud));
                        if(seleccionado == null)
                            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height+alturaFilaTableView;
                        d.dismiss();
                    }
                }catch(Exception e){
                    Toasty.error(v.getContext(), "No se pudo salvar el contacto").show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }

        //Para campos de seleccion para grid bancos
        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_tpfkt");

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < opciones.size(); j++){
            if(seleccionado != null && opciones.get(j).get("id").equals(seleccionado.getPafkt())){
                selectedIndex = j;
            }
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
        funcionSpinner.setBackground(spinner_back);
        funcionSpinner.setAdapter(dataAdapter);

        if(seleccionado != null){
            name1EditText.setText(seleccionado.getName1());
            namevEditText.setText(seleccionado.getNamev());
            telf1EditText.setText(seleccionado.getTelf1());
            funcionSpinner.setSelection(selectedIndex);
        }

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public void displayDialogImpuesto(Context context, final Impuesto seleccionado) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.impuesto_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final Spinner claveSpinner= d.findViewById(R.id.claveSpinner);
        claveSpinner.setEnabled(false);
        final Spinner clasiSpinner= d.findViewById(R.id.clasiSpinner);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText(R.string.texto_modificar);
            title.setText(String.format(context.getResources().getString(R.string.palabras_2), context.getResources().getString(R.string.texto_modificar), context.getResources().getString(R.string.texto_impuesto)));
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seleccionado != null)
                    impuestosSolicitud.remove(seleccionado);
                Impuesto nuevoImpuesto = new Impuesto();
                nuevoImpuesto.setTatyp(((OpcionSpinner)claveSpinner.getSelectedItem()).getId());
                nuevoImpuesto.setVtext(((OpcionSpinner)claveSpinner.getSelectedItem()).getName());
                nuevoImpuesto.setTaxkd(((OpcionSpinner)clasiSpinner.getSelectedItem()).getId());
                nuevoImpuesto.setVtext2(((OpcionSpinner)clasiSpinner.getSelectedItem()).getName());
                if(!nuevoImpuesto.validarObligatorios()){
                    Toasty.warning(v.getContext(), "Todos los campos son obligatorios!").show();
                    return;
                }
                try{
                    impuestosSolicitud.add(nuevoImpuesto);
                    claveSpinner.setSelection(0);
                    clasiSpinner.setSelection(0);
                    if(impuestosSolicitud != null) {
                        tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(v.getContext(), impuestosSolicitud));
                        if(seleccionado == null)
                            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height+alturaFilaTableView;
                        d.dismiss();
                    }
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar el impuesto. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }

        //Para campos de seleccion para grid impuestos campo clave de impuesto
        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_impstos",1,2,null, "tatyp='"+seleccionado.getTatyp()+"' AND taxkd='"+seleccionado.getTaxkd()+"'");

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndexClave = 0;
        for (int j = 0; j < opciones.size(); j++){
            if(seleccionado != null && opciones.get(j).get("id").equals(seleccionado.getTatyp())){
                selectedIndexClave = j;
            }
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
        claveSpinner.setBackground(spinner_back);
        claveSpinner.setAdapter(dataAdapter);

        claveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                //Para campos de seleccion para grid bancos campo clasificacion fiscal
                ArrayList<HashMap<String, String>> opcionesClasi = mDBHelper.getDatosCatalogo("cat_impstos",3,4,null,"tatyp='"+opcion.getId()+"'");

                ArrayList<OpcionSpinner> listaopcionesClasi = new ArrayList<>();
                int selectedIndexClasi = 0;
                for (int j = 0; j < opcionesClasi.size(); j++){
                    if(seleccionado != null && opcionesClasi.get(j).get("id").equals(seleccionado.getTaxkd())){
                        selectedIndexClasi = j;
                    }
                    listaopcionesClasi.add(new OpcionSpinner(opcionesClasi.get(j).get("id"), opcionesClasi.get(j).get("descripcion")));
                }
                // Creando el adaptador(opcionesClasi) para el comboBox deseado
                ArrayAdapter<OpcionSpinner> dataAdapterClasi = new ArrayAdapter<>(view.getContext(), R.layout.simple_spinner_item, listaopcionesClasi);
                // Drop down layout style - list view with radio button
                dataAdapterClasi.setDropDownViewResource(R.layout.spinner_item);
                // attaching data adapter to spinner
                Drawable spinner_back_clasi = view.getContext().getResources().getDrawable(R.drawable.spinner_underlined, null);
                clasiSpinner.setBackground(spinner_back_clasi);
                clasiSpinner.setAdapter(dataAdapterClasi);
                clasiSpinner.setSelection(selectedIndexClasi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Para campos de seleccion para grid bancos campo clasificacion fiscal
        ArrayList<HashMap<String, String>> opcionesClasi = mDBHelper.getDatosCatalogo("cat_impstos",3,4,null,"tatyp='"+seleccionado.getTatyp()+"'");

        ArrayList<OpcionSpinner> listaopcionesClasi = new ArrayList<>();
        int selectedIndexClasi = 0;
        for (int j = 0; j < opcionesClasi.size(); j++){
            if(seleccionado != null && opcionesClasi.get(j).get("id").equals(seleccionado.getTaxkd())){
                selectedIndexClasi = j;
            }
            listaopcionesClasi.add(new OpcionSpinner(opcionesClasi.get(j).get("id"), opcionesClasi.get(j).get("descripcion")));
        }
        // Creando el adaptador(opcionesClasi) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapterClasi = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopcionesClasi);
        // Drop down layout style - list view with radio button
        dataAdapterClasi.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back_clasi = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
        clasiSpinner.setBackground(spinner_back_clasi);
        clasiSpinner.setAdapter(dataAdapterClasi);


        if(seleccionado != null){
            claveSpinner.setSelection(selectedIndexClave);
            clasiSpinner.setSelection(selectedIndexClasi);
        }
        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public void displayDialogInterlocutor(Context context, final Interlocutor seleccionado) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.interlocutor_dialog_layout);
        d.setTitle("+ Nuevo Interlocutor");

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextInputEditText nameEditText= d.findViewById(R.id.nameEditTxt);
        final TextInputEditText propellantEditTxt= d.findViewById(R.id.propEditTxt);
        final TextInputEditText destEditTxt= d.findViewById(R.id.destEditTxt);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText(R.string.texto_modificar);
            title.setText(String.format(context.getResources().getString(R.string.palabras_2), context.getResources().getString(R.string.texto_modificar), context.getResources().getString(R.string.texto_interlocutor)));
            if(seleccionado.getParvw().trim().equals("AG")){
                Toasty.warning(context,"La funcion de interlocutor 'Solicitante' NO puede ser modificada!").show();
                return;
            }
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seleccionado != null)
                    interlocutoresSolicitud.remove(seleccionado);
                Interlocutor nuevoInterlocutor = new Interlocutor();
                nuevoInterlocutor.setParvw(nameEditText.getText().toString());
                nuevoInterlocutor.setVtext(propellantEditTxt.getText().toString());
                nuevoInterlocutor.setKunn2(destEditTxt.getText().toString());
                interlocutoresSolicitud.add(nuevoInterlocutor);
                try{
                    nameEditText.setText("");
                    propellantEditTxt.setText("");
                    destEditTxt.setText("");
                    if(interlocutoresSolicitud != null) {
                        tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(v.getContext(), interlocutoresSolicitud));
                        if(seleccionado == null)
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height+alturaFilaTableView;
                        d.dismiss();
                    }
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar el interlocutor. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }

        if(seleccionado != null){
            nameEditText.setText(seleccionado.getParvw());
            nameEditText.setEnabled(false);
            propellantEditTxt.setText(seleccionado.getVtext());
            propellantEditTxt.setEnabled(false);
            destEditTxt.setText(seleccionado.getKunn2());
        }

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public static void displayDialogBancos(Context context, final Banco seleccionado) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.banco_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final Spinner bancoSpinner = d.findViewById(R.id.bancoSpinner);
        final Spinner paisSpinner= d.findViewById(R.id.paisSpinner);
        final TextInputEditText cuentaEditTxt= d.findViewById(R.id.cuentaEditTxt);
        final TextInputEditText claveEditTxt= d.findViewById(R.id.claveEditTxt);
        final TextInputEditText titularEditTxt= d.findViewById(R.id.titularEditTxt);
        final TextInputEditText tipoEditTxt= d.findViewById(R.id.tipoEditTxt);
        final TextInputEditText montoMaximoEditTxt= d.findViewById(R.id.montoMaximoEditTxt);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText(R.string.texto_modificar);
            title.setText(String.format(context.getString(R.string.palabras_2),context.getString(R.string.texto_modificar),context.getString(R.string.texto_cuenta_bancaria)));
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seleccionado != null)
                    bancosSolicitud.remove(seleccionado);
                Banco nuevoBanco = new Banco();
                nuevoBanco.setBankl(((OpcionSpinner)bancoSpinner.getSelectedItem()).getId());
                nuevoBanco.setBanks(((OpcionSpinner)paisSpinner.getSelectedItem()).getId());
                nuevoBanco.setBankn(cuentaEditTxt.getText().toString());
                nuevoBanco.setBkont(claveEditTxt.getText().toString());
                nuevoBanco.setKoinh(titularEditTxt.getText().toString());
                nuevoBanco.setBvtyp(tipoEditTxt.getText().toString());
                nuevoBanco.setBkref(montoMaximoEditTxt.getText().toString());
                if(!nuevoBanco.validarObligatorios()){
                    Toasty.warning(v.getContext(), "Todos los campos son obligatorios!").show();
                    return;
                }
                try {
                    bancosSolicitud.add(nuevoBanco);
                    bancoSpinner.setSelection(0);
                    paisSpinner.setSelection(0);
                    cuentaEditTxt.setText("");
                    claveEditTxt.setText("");
                    titularEditTxt.setText("");
                    tipoEditTxt.setText("");
                    montoMaximoEditTxt.setText("");
                    if(bancosSolicitud != null) {
                        tb_bancos.setDataAdapter(new BancoTableAdapter(v.getContext(), bancosSolicitud));
                        if(seleccionado == null)
                            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height+alturaFilaTableView;
                        d.dismiss();
                    }
                } catch (Exception e ) {
                    Toasty.error(v.getContext(), "No se pudo salvar el banco. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }
        //Para campos de seleccion para grid bancos
        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_zesdvt_00566");

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < opciones.size(); j++){
            if(seleccionado != null && opciones.get(j).get("id").equals(seleccionado.getBankl())){
                selectedIndex = j;
            }
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
        bancoSpinner.setBackground(spinner_back);
        bancoSpinner.setAdapter(dataAdapter);

        ArrayList<HashMap<String, String>> opcionesP = mDBHelper.getDatosCatalogo("cat_t005");

        ArrayList<OpcionSpinner> listaopcionesP = new ArrayList<>();
        int selectedIndexP = 0;
        for (int j = 0; j < opcionesP.size(); j++){
            if(seleccionado != null && opcionesP.get(j).get("id").equals(seleccionado.getBanks())){
                selectedIndexP = j;
            }
            listaopcionesP.add(new OpcionSpinner(opcionesP.get(j).get("id"), opcionesP.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapterP = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopcionesP);
        dataAdapterP.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        paisSpinner.setBackground(spinner_back);
        paisSpinner.setAdapter(dataAdapterP);

        if(seleccionado != null){
            bancoSpinner.setSelection(selectedIndex);
            paisSpinner.setSelection(selectedIndexP);
            cuentaEditTxt.setText(seleccionado.getBankn());
            claveEditTxt.setText(seleccionado.getBkont());
            titularEditTxt.setText(seleccionado.getKoinh());
            tipoEditTxt.setText(seleccionado.getBvtyp());
            montoMaximoEditTxt.setText(seleccionado.getBkref());
        }

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }
    @SuppressWarnings("unchecked")
    public static void displayDialogEncuestaCanales(final Context context) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //Toasty.info(context,"Dialogo Dismissed!").show();
            }
        });
        d.setContentView(R.layout.encuesta_canales_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final Spinner grupoIsscomSpinner = d.findViewById(R.id.grupoIsscomSpinner);

        grupoIsscomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                LinearLayout layout = d.findViewById(R.id.layoutDinamico);
                layout.removeAllViews();

                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params1.setMargins(10,10,10,10);
                ArrayList<HashMap<String, String>> preguntas = mDBHelper.getPreguntasSegunGrupo(opcion.getId());
                ArrayList<HashMap<String, String>> respuestas = mDBHelper.getRespuestasEncuesta(GUID);

                for (int j = 0; j < preguntas.size(); j++){
                    int selected = 0;
                    Spinner pregunta = new Spinner(d.getContext());
                    ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                    TextView label_pregunta = new TextView(d.getContext());
                    label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                    //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                    layout.addView(label_pregunta,params1);
                    ArrayList<HashMap<String, String>> opcionesxpregunta = mDBHelper.getOpcionesPreguntaGrupo(opcion.getId(),preguntas.get(j).get("zid_quest"));
                    for (int i = 0; i < opcionesxpregunta.size(); i++) {
                        OpcionSpinner op = new OpcionSpinner(opcionesxpregunta.get(i).get("zid_resp"), opcionesxpregunta.get(i).get("zid_resp")+" - "+opcionesxpregunta.get(i).get("text"));
                        if (respuestas.size() > 0 && respuestas.get(0) != null && opcionesxpregunta.get(i).get("zid_resp").equals(respuestas.get(0).get("col" + (j + 1)))
                                && (respuestas.get(0).get("id_grupo")).equals(String.valueOf(position))) {
                            op.setSelected(i);
                            selected = i;
                        }
                        misOpciones.add(op);
                    }

                    ArrayAdapter<OpcionSpinner> dataAdapterP = new ArrayAdapter<>(context, R.layout.simple_spinner_item, misOpciones);
                    dataAdapterP.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
                    pregunta.setBackground(spinner_back);
                    pregunta.setAdapter(dataAdapterP);

                    pregunta.setId(j+1);
                    pregunta.setSelection(selected);
                    layout.addView(pregunta,params1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toasty.warning(context,"Seleccion el Grupo Isscom para generar la encuesta.").show();
            }
        });

        Button saveBtn= d.findViewById(R.id.saveBtn);
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                try {
                    //Si existe una encuesta realizada se borrara y se guardara la nueva generada (Solo 1 encuesta x cliente permitida)
                    int del = mDb.delete(VariablesGlobales.getTablaEncuestaSolicitud(), "id_solicitud = ?", new String[]{GUID});
                    //Guardar la encuesta generada en la Base de datos
                    ContentValues encuestaValues = new ContentValues();

                    encuestaValues.put("id_solicitud", GUID);
                    Spinner s = d.findViewById(R.id.grupoIsscomSpinner);
                    String valorg = ((OpcionSpinner)s.getSelectedItem()).getId();
                    encuestaValues.put("id_Grupo", valorg);
                    s = d.findViewById(1);
                    String valor1 = s != null ? ((OpcionSpinner)s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col1", valor1 );
                    s = d.findViewById(2);
                    String valor2 = s != null ? ((OpcionSpinner)s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col2", valor2 );
                    s = d.findViewById(3);
                    String valor3 = s != null ? ((OpcionSpinner)s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col3", valor3 );
                    s = d.findViewById(4);
                    String valor4 = s != null ? ((OpcionSpinner)s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col4", valor4 );
                    s = d.findViewById(5);
                    String valor5 = s != null ? ((OpcionSpinner)s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col5", valor5 );
                    s = d.findViewById(6);
                    String valor6 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col6", valor6 );

                    HashMap<String, String> valor_canales = mDBHelper.getValoresSegunEncuestaRealizada(valorg, valor1, valor2, valor3, valor4, valor5, valor6);

                    //Asignar valores de canales segun respuesta obtenida
                    Spinner zzent3Spinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZENT3");
                    zzent3Spinner.setSelection(VariablesGlobales.getIndex(zzent3Spinner,valor_canales.get("W_CTE-ZZENT3").trim()));

                    Spinner zzent4Spinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZENT4");
                    zzent4Spinner.setSelection(VariablesGlobales.getIndex(zzent4Spinner,valor_canales.get("W_CTE-ZZENT4").trim()));

                    Spinner zzcanalSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZCANAL");
                    zzcanalSpinner.setSelection(VariablesGlobales.getIndex(zzcanalSpinner,valor_canales.get("W_CTE-ZZCANAL").trim()));

                    Spinner ztpocanalSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZTPOCANAL");
                    ztpocanalSpinner.setSelection(VariablesGlobales.getIndex(ztpocanalSpinner,valor_canales.get("W_CTE-ZTPOCANAL").trim()));

                    Spinner zgpocanalSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZGPOCANAL");
                    zgpocanalSpinner.setSelection(VariablesGlobales.getIndex(zgpocanalSpinner,valor_canales.get("W_CTE-ZGPOCANAL").trim()));

                    Spinner pson3Spinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-PSON3");
                    pson3Spinner.setSelection(VariablesGlobales.getIndex(pson3Spinner,valor_canales.get("W_CTE-PSON3").trim()));

                    try {
                        mDb.insert(VariablesGlobales.getTablaEncuestaSolicitud(), null, encuestaValues);
                    } catch (Exception e) {
                        Toasty.error(v.getContext(), "Error Insertando Encuesta Canales", Toast.LENGTH_SHORT).show();
                    }

                    try {
                        //Calcular Nivel Socioeconomico Planchado Segun el valor de su canal Pais
                        if (PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_VKORG", "").equals("0443")) {
                            String NSEPCalculado = mDBHelper.AlgoritmoNSEP(valor_canales.get("W_CTE-ZZENT4").trim());
                            Spinner nsepSpinner = (Spinner) mapeoCamposDinamicos.get("W_CTE-KATR4");
                            if (NSEPCalculado.trim().length() > 0)
                                nsepSpinner.setSelection(VariablesGlobales.getIndex(nsepSpinner, NSEPCalculado.trim()));
                        }
                    }catch (Exception e) {
                        Toasty.error(v.getContext(), "No se pudo asignar el Nivel SocioEconomico Planchado. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Toasty.success(v.getContext(), "Encuesta Canales ejecutada correctamente!", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA");
                    ejecutada.setChecked(true);
                } catch (Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_grupo_isscom");

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < opciones.size(); j++){
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
        grupoIsscomSpinner.setBackground(spinner_back);
        grupoIsscomSpinner.setAdapter(dataAdapter);
        ArrayList<HashMap<String, String>> respuestas = mDBHelper.getRespuestasEncuesta(GUID);
        if(respuestas.size() > 0){
            grupoIsscomSpinner.setSelection(VariablesGlobales.getIndex(grupoIsscomSpinner,respuestas.get(0).get("id_grupo").trim()));
        }

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public static void displayDialogEncuestaGec(final Context context) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //Toasty.info(context,"Dialogo Dismissed!").show();
            }
        });
        d.setContentView(R.layout.encuesta_gec_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        LinearLayout layout = d.findViewById(R.id.layoutDinamico);
        layout.removeAllViews();

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10,10,10,10);
        final ArrayList<HashMap<String, String>> preguntas = mDBHelper.getPreguntasGec();
        final ArrayList<HashMap<String, String>> respuestas = mDBHelper.getEncuestaGec(GUID);

        for (int j = 0; j < preguntas.size(); j++){
            TextInputEditText monto = new TextInputEditText(d.getContext());
            monto.setInputType(InputType.TYPE_CLASS_NUMBER);
            ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

            TextView label_pregunta = new TextView(d.getContext());
            label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
            //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
            layout.addView(label_pregunta,params1);

            //revisar si ya se ha realizado una encuesta para la solicitud, para poder mostrar las respuestas existentes.
            if(respuestas.size() > 0){
                monto.setText(respuestas.get(j).get("monto"));
            }
            monto.setId(j+1);
            layout.addView(monto,params1);
        }

        Button saveBtn= d.findViewById(R.id.saveBtn);
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                try{
                    //Si existe una encuesta realizada se borrara y se guardara la nueva generada (Solo 1 encuesta x cliente permitida)
                    //Guardar la encuesta generada en la Base de datos
                    int del = mDb.delete(VariablesGlobales.getTablaEncuestaGecSolicitud(), "id_solicitud = ?", new String[]{GUID});
                    ContentValues encuestaValues = new ContentValues();
                    Integer suma_montos = 0;
                    for (int j = 0; j < preguntas.size(); j++){
                        TextInputEditText monto = d.findViewById(j+1);
                        encuestaValues.clear();
                        //encuestaValues.put("id_encuesta_gec", GUID);
                        encuestaValues.put("id_solicitud", GUID);
                        encuestaValues.put("zid_grupo", (j+1));
                        encuestaValues.put("zid_quest", (j+1));
                        encuestaValues.put("monto", monto.getText().toString());
                        if(!monto.getText().toString().trim().equals("")){
                            suma_montos += Integer.valueOf(monto.getText().toString());
                        }else{
                            Toasty.error(v.getContext(), "Por favor llene todos los campos de la encuesta.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            mDb.insert(VariablesGlobales.getTablaEncuestaGecSolicitud(), null, encuestaValues);
                        } catch (Exception e) {
                            Toasty.error(v.getContext(), "Error Insertando Encuesta Canales (Registro #"+j+")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    String valor_gec = mDBHelper.getGecSegunEncuestaRealizada(suma_montos);
                    //Asignar los valores de los canales segun las respuestas obtenidas
                    Spinner gecSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                    gecSpinner.setSelection(VariablesGlobales.getIndex(gecSpinner,valor_gec));

                    Toasty.success(v.getContext(), "Encuesta GEC ejecutada correctamente!", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                    ejecutada.setChecked(true);
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta gec. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
        }

        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_grupo_isscom");

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < opciones.size(); j++){
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private class ContactoClickListener implements TableDataClickListener<Contacto> {
        @Override
        public void onDataClicked(int rowIndex, Contacto seleccionado) {
            displayDialogContacto(SolicitudModificacionActivity.this,seleccionado);
        }
    }
    //Listeners de Bloques de datos
    private class ContactoLongClickListener implements TableDataLongClickListener<Contacto> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Contacto seleccionado) {
            DialogHandler appdialog = new DialogHandler();

            appdialog.Confirm(SolicitudModificacionActivity.this, "Confirmación Borrado", "Esta seguro que quiere eliminar el contacto "+seleccionado.getName1() + " " +seleccionado.getNamev()+"?",
                    "Cancelar", "Eliminar", new EliminarContacto(getBaseContext(), rowIndex));
            return true;
        }
    }
    public static class EliminarContacto implements Runnable {
        private Context context;
        private int rowIndex;
        public EliminarContacto(Context context, int rowIndex) {
            this.context = context;
            this.rowIndex = rowIndex;
        }
        public void run() {
            Contacto seleccionado = contactosSolicitud.get(rowIndex);
            String salida = seleccionado.getName1() + " " + seleccionado.getNamev()+" ha sido eliminado.";
            contactosSolicitud.remove(rowIndex);
            tb_contactos.setDataAdapter(new ContactoTableAdapter(context, contactosSolicitud));
            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(context, salida, Toast.LENGTH_SHORT).show();
        }
    }
    private class ImpuestoClickListener implements TableDataClickListener<Impuesto> {
        @Override
        public void onDataClicked(int rowIndex, Impuesto seleccionado) {
            displayDialogImpuesto(SolicitudModificacionActivity.this,seleccionado);
        }
    }
    private class ImpuestoLongClickListener implements TableDataLongClickListener<Impuesto> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Impuesto seleccionado) {
            String salida = seleccionado.getVtext() + " " + seleccionado.getVtext2();
            impuestosSolicitud.remove(rowIndex);
            tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(getBaseContext(), impuestosSolicitud));
            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private class BancoClickListener implements TableDataClickListener<Banco> {
        @Override
        public void onDataClicked(int rowIndex, Banco seleccionado) {
            displayDialogBancos(SolicitudModificacionActivity.this,seleccionado);
        }
    }
    private class BancoLongClickListener implements TableDataLongClickListener<Banco> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Banco seleccionado) {
            DialogHandler appdialog = new DialogHandler();
            appdialog.Confirm(SolicitudModificacionActivity.this, "Confirmación Borrado", "Esta seguro que quiere eliminar el banco "+seleccionado.getBankn()+"?",
                    "Cancelar", "Eliminar", new EliminarBanco(getBaseContext(), rowIndex));
            return true;
        }
    }
    public static class EliminarBanco implements Runnable {
        private Context context;
        private int rowIndex;
        public EliminarBanco(Context context, int rowIndex) {
            this.context = context;
            this.rowIndex = rowIndex;
        }
        public void run() {
            Banco seleccionado = bancosSolicitud.get(rowIndex);
            String salida = seleccionado.getBankn() + " " + seleccionado.getBanks();
            bancosSolicitud.remove(rowIndex);
            tb_bancos.setDataAdapter(new BancoTableAdapter(context, bancosSolicitud));
            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(context, salida, Toast.LENGTH_SHORT).show();
        }
    }
    private class InterlocutorClickListener implements TableDataClickListener<Interlocutor> {
        @Override
        public void onDataClicked(int rowIndex, Interlocutor seleccionado) {
            displayDialogInterlocutor(SolicitudModificacionActivity.this,seleccionado);
        }
    }
    private class InterlocutorLongClickListener implements TableDataLongClickListener<Interlocutor> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Interlocutor seleccionado) {
            String salida = seleccionado.getName1() + " " + seleccionado.getKunn2();
            interlocutoresSolicitud.remove(rowIndex);
            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(getBaseContext(), interlocutoresSolicitud));
            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    private class VisitasClickListener implements TableDataClickListener<Visitas> {
        @Override
        public void onDataClicked(int rowIndex, Visitas seleccionado) {
            DetallesVisitPlan(SolicitudModificacionActivity.this, seleccionado);
        }
    }
    @SuppressWarnings("unchecked")
    private void DetallesVisitPlan(final Context context, final Visitas seleccionado) {
        final Dialog d = new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.visita_dialog_layout);
        final boolean reparto = mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_BZIRK",""), seleccionado.getVptyp());

        final boolean permite_modificar = PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_TIPORUTA","ZPV").toString().equals(seleccionado.getVptyp())
                || ((seleccionado.getVptyp().equals("ZPV") && PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZRM")))
                || (seleccionado.getVptyp().equals("ZDY") && PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZTV"));

        final TextView title = d.findViewById(R.id.title);
        final Spinner kvgr4Spinner = d.findViewById(R.id.kvgr4Spinner);
        final TextInputEditText f_icoEditText = d.findViewById(R.id.f_icoEditTxt);
        final TextInputEditText f_fcoEditText = d.findViewById(R.id.f_fcoEditTxt);
        final TextInputEditText f_iniEditText = d.findViewById(R.id.f_iniEditTxt);
        final TextInputEditText f_finEditText = d.findViewById(R.id.f_finEditTxt);
        final Spinner fcalidSpinner = d.findViewById(R.id.fcalidSpinner);
        final TextView ruta_reparto_label = d.findViewById(R.id.ruta_reparto_label);
        final Spinner ruta_reparto = d.findViewById(R.id.ruta_reparto);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        title.setText(String.format(context.getString(R.string.palabras_2),context.getString(R.string.label_vp),seleccionado.getVptyp()));

        ArrayAdapter<String> spinnerArrayAdapter;
        if(seleccionado.getVptyp().equals("ZAT"))
            spinnerArrayAdapter =new ArrayAdapter<String>(this,R.layout.spinner_item, getResources().getStringArray(R.array.OpcionesKvgr4Autoventa));
        else
            spinnerArrayAdapter =new ArrayAdapter<String>(this,R.layout.spinner_item, getResources().getStringArray(R.array.OpcionesKvgr4));
        if(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661")
                || PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("Z001")){
            spinnerArrayAdapter =new ArrayAdapter<String>(this,R.layout.spinner_item, getResources().getStringArray(R.array.OpcionesKvgr4Uruguay));
        }
        //spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        kvgr4Spinner.setAdapter(spinnerArrayAdapter);

        kvgr4Spinner.setSelection(((ArrayAdapter<CharSequence>)kvgr4Spinner.getAdapter()).getPosition(seleccionado.getKvgr4()));

        f_icoEditText.setText(seleccionado.getF_ico());
        f_fcoEditText.setText(seleccionado.getF_fco());
        f_iniEditText.setText(seleccionado.getF_ini());
        f_finEditText.setText(seleccionado.getF_fin());

        Spinner centro_suministro = (Spinner)mapeoCamposDinamicos.get("W_CTE-VWERK");
        String valor_centro_suministro = ((OpcionSpinner)centro_suministro.getSelectedItem()).getId().trim();
        String filtroxPais = "";
        switch(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
            case "1661":
            case "Z001":
                Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                if(gec != null)
                    filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                if(bzirk_sel != null)
                    filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                break;
            default:
                filtroxPais = "";
        }
        ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(Objects.requireNonNull(context), R.layout.simple_spinner_item, rutas_reparto);
        // Drop down layout style - list view with radio button
        dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
        ruta_reparto.setAdapter(dataAdapterRuta);

        ruta_reparto.setSelection(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()));

        if(!reparto){
            if((seleccionado.getVptyp().equals("ZPV") && !mDBHelper.ExisteRutaMixta()) || seleccionado.getVptyp().equals("ZAT")  || (PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_TIPORUTA","").equals("ZTV") && seleccionado.getVptyp().equals("ZTV"))  ) {
                ruta_reparto.setVisibility(View.GONE);
                ruta_reparto_label.setVisibility(View.GONE);
            }else{
                Spinner zona_ventas = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                String valor_zona_ventas = ((OpcionSpinner)zona_ventas.getSelectedItem()).getId().trim();
                ArrayList<OpcionSpinner> rutas_preventa = mDBHelper.getDatosCatalogoParaSpinner("EX_T_RUTAS_VP","(bzirk = '" + valor_zona_ventas + "' OR bzirk = '') AND vptyp = '" + seleccionado.getVptyp() + "' AND vkorg = '" + PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_VKORG","") + "'");
                // Creando el adaptador(opciones) para el comboBox deseado
                ArrayAdapter<OpcionSpinner> dataAdapterRutaP = new ArrayAdapter<>(Objects.requireNonNull(context), R.layout.simple_spinner_item, rutas_preventa);
                // Drop down layout style - list view with radio button
                dataAdapterRutaP.setDropDownViewResource(R.layout.spinner_item);
                ruta_reparto.setAdapter(dataAdapterRutaP);
                ruta_reparto.setSelection(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()));
            }
        }
        if(reparto) {
            int indiceSel = VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta());
            if(indiceSel == -1){
                //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP o del formulario con incidencia
                if(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()) == -1 ){
                    ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) ruta_reparto.getAdapter());
                    OpcionSpinner opcionSAP = new OpcionSpinner(seleccionado.getRuta(),seleccionado.getRuta() +" - "+ seleccionado.getRuta());
                    dataAdapter.add(opcionSAP);
                    dataAdapter.notifyDataSetChanged();
                }
            }
            ruta_reparto.setSelection(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()));
        }

        /*if(reparto){
            kvgr4Spinner.setVisibility(GONE);
            f_icoEditText.setVisibility(GONE);
            f_fcoEditText.setVisibility(GONE);
            f_iniEditText.setVisibility(GONE);
            f_finEditText.setVisibility(GONE);
        }*/

        EditTextDatePicker prueba = new EditTextDatePicker(context, f_icoEditText,"yyyymmdd");
        EditTextDatePicker prueba2 = new EditTextDatePicker(context, f_fcoEditText,"yyyymmdd");
        EditTextDatePicker prueba3 = new EditTextDatePicker(context, f_iniEditText,"yyyymmdd");
        EditTextDatePicker prueba4 = new EditTextDatePicker(context, f_finEditText,"yyyymmdd");

        //Catalogo quemado de frecuencia semanal
        int selectedIndex = 0;
        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        listaopciones.add(new OpcionSpinner("01","Cada semana"));
        listaopciones.add(new OpcionSpinner("02","Cada 2 semanas"));
        listaopciones.add(new OpcionSpinner("03","Cada 3 semanas"));
        listaopciones.add(new OpcionSpinner("04","Cada 4 semanas"));
        listaopciones.add(new OpcionSpinner("05","Cada 5 semanas"));
        listaopciones.add(new OpcionSpinner("06","Cada 6 semanas"));
        listaopciones.add(new OpcionSpinner("08","Cada 8 semanas"));
        listaopciones.add(new OpcionSpinner("10","Cada 10 semanas"));

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_background, null);
        fcalidSpinner.setBackground(spinner_back);
        fcalidSpinner.setAdapter(dataAdapter);
        fcalidSpinner.setSelection(selectedIndex);

        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //Setear ruta segun el tipo de visita ZPV, ZDD, etc
                MaskedEditText comentariosAuto = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS"));
                if( reparto ){
                    seleccionado.setRuta(((OpcionSpinner)ruta_reparto.getSelectedItem()).getId().toString().trim());
                    ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")),seleccionado.getRuta()));

                    String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_VKORG",""), seleccionado.getRuta());
                    if(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")) != null)
                        ((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")), condicionExpedicion));

                }else{
                    if((mDBHelper.ExisteTipoVisita("ZRM") || mDBHelper.ExisteTipoVisita("ZDY")) && ruta_reparto.getSelectedItem() != null){
                        seleccionado.setRuta(((OpcionSpinner)ruta_reparto.getSelectedItem()).getId().toString().trim());
                    }else {
                        seleccionado.setRuta(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_RUTAHH", ""));
                    }
                    if(seleccionado.getVptyp().equals("ZAT")){
                        ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")),seleccionado.getRuta()));
                        String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_VKORG",""), seleccionado.getRuta());
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")) != null)
                            ((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")), condicionExpedicion));

                    }
                }
                if(VariablesGlobales.ComentariosAutomaticos()) {
                    String tipoRuta = "";

                    switch(seleccionado.getVptyp()){
                        case "ZPV":
                            tipoRuta = " DE PREVENTA";
                            break;
                        case "ZTV":
                            tipoRuta = " DE TELEVENTA";
                            break;
                        case "ZJV":
                            tipoRuta = " ESPECIALIZADA";
                            break;
                        case "ZDD":
                            tipoRuta = " DE REPARTO";
                            break;
                        case "ZAT":
                            tipoRuta = " DE AUTOVENTA";
                            break;
                        case "ZRM":
                            tipoRuta = " MIXTA";
                            break;
                        case "ZDY":
                            tipoRuta = " DUMMY";
                            break;
                    }
                    if (seleccionado.getRuta().equals(visitasSolicitud_old.stream().filter(tipo -> seleccionado.getVptyp().equals(tipo.getVptyp())).findAny().get().getRuta())) {
                        if(comentariosAuto.getText().toString().contains("\nCAMBIO RUTA"+tipoRuta))
                            comentariosAuto.setText(comentariosAuto.getText().toString().replace("\nCAMBIO RUTA"+tipoRuta, ""));
                        else if(comentariosAuto.getText().toString().contains("CAMBIO RUTA"+tipoRuta))
                            comentariosAuto.setText(comentariosAuto.getText().toString().replace("CAMBIO RUTA"+tipoRuta+"\n", "").replace("CAMBIO RUTA"+tipoRuta+"", ""));
                    } else {
                        if (!comentariosAuto.getText().toString().contains("\nCAMBIO RUTA"+tipoRuta) && !comentariosAuto.getText().toString().contains("CAMBIO RUTA"+tipoRuta)) {
                            if(comentariosAuto.getText().toString().trim().length() > 0)
                                comentariosAuto.append("\nCAMBIO RUTA" + tipoRuta);
                            else{
                                comentariosAuto.append("CAMBIO RUTA" + tipoRuta);
                            }
                        }
                    }
                }

                //seleccionado.setRuta(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_RUTAHH",""));
                seleccionado.setKvgr4(kvgr4Spinner.getSelectedItem().toString().trim());
                seleccionado.setF_ini(f_iniEditText.getText().toString());
                seleccionado.setF_fin(f_finEditText.getText().toString());
                seleccionado.setF_ico(f_icoEditText.getText().toString());
                seleccionado.setF_fco(f_fcoEditText.getText().toString());
                seleccionado.setFcalid(((OpcionSpinner)fcalidSpinner.getSelectedItem()).getId());

                //Replicar los cambios en setF_ico, setF_fco
                for(int x=0; x < visitasSolicitud.size(); x++){
                    Visitas vp = visitasSolicitud.get(x);
                    vp.setF_ico(f_icoEditText.getText().toString());
                    vp.setF_fco(f_fcoEditText.getText().toString());
                    vp.setF_ini(f_iniEditText.getText().toString());
                    vp.setF_fin(f_finEditText.getText().toString());
                    vp.setKvgr4(seleccionado.getKvgr4());
                }

                //RECALCULAR DIAS DE VISITA
                RecalcularDiasDeReparto(context);

                tb_visitas.setDataAdapter(new VisitasTableAdapter(v.getContext(), visitasSolicitud));
                //tb_visitas.getDataAdapter().notifyDataSetChanged();
                try {
                    d.dismiss();
                }catch(Exception e){
                    Toasty.error(v.getContext(), "No se pudo salvar la configuracion").show();
                }
            }
        });

        if(!permite_modificar && !reparto){
            kvgr4Spinner.setEnabled(false);
            f_icoEditText.setEnabled(false);
            f_fcoEditText.setEnabled(false);
            f_iniEditText.setEnabled(false);
            f_finEditText.setEnabled(false);
            fcalidSpinner.setEnabled(false);
            if(!seleccionado.getVptyp().equals("ZDY")) {
                ruta_reparto.setEnabled(false);
                saveBtn.setVisibility(View.GONE);
            }
        }
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }
        d.show();
    }

    private static void RecalcularDiasDeReparto(Context context) {
        int numplanes = visitasSolicitud.size();
        //Iterrar sobre todos los planes de la modalida de venta seleccionada
        for (int y = 0 ; y < numplanes; y++) {
            Visitas vp = visitasSolicitud.get(y);
            if(vp.getKvgr4() != null && vp.getKvgr4().replace("DA","").trim().length() == 0 ){
                Toasty.warning(context,"Cliente no tiene metodo de venta asignado, por favor modificar el VP antes de continuar con la asignacion de los dias de visita.").show();
                return;
            }
            //Revisar si el VP es una ruta de reparto para ser borrada y recalculada
            if (mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BZIRK",""), vp.getVptyp())) {
                vp.setLun_de("");
                vp.setLun_a("");
                vp.setMar_de("");
                vp.setMar_a("");
                vp.setMier_de("");
                vp.setMier_a("");
                vp.setJue_de("");
                vp.setJue_a("");
                vp.setVie_de("");
                vp.setVie_a("");
                vp.setSab_de("");
                vp.setSab_a("");
            }
        }
        //Recalcular los dias de visita del(os) reparto segun las preventas existentes nuevas determinadas
        //Se recorren la cantidad de visit plans para obtener la data de cada uno especifico
        Visitas rep = null;
        Visitas rep_old = null;
        for (int y = 0 ; y < numplanes; y++) {
            Visitas vp = visitasSolicitud.get(y);

            //Si no es tipo de reparto debemos tomar en cuenta para calcular su reparto
            boolean esReparto = mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BZIRK",""), vp.getVptyp());
            if(!esReparto && !vp.getVptyp().equals("ZRM") && !vp.getVptyp().equals("ZDY")){
                Spinner metodo = ((Spinner)mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                String rutaReparto = mDBHelper.RutaRepartoAsociada(((OpcionSpinner)metodo.getSelectedItem()).getId(), vp.getVptyp());
                if(rutaReparto.equals("ZAT")){//Autoventa no ocupa recalcular su reparto
                    return;
                }
                for (int x = 0; x < visitasSolicitud.size(); x++) {
                    if (rutaReparto.equals(visitasSolicitud.get(x).getVptyp())) {
                        rep = visitasSolicitud.get(x);
                        break;
                    }
                }
                for (int x = 0; x < visitasSolicitud_old.size(); x++) {
                    if (rutaReparto.equals(visitasSolicitud_old.get(x).getVptyp())) {
                        rep_old = visitasSolicitud_old.get(x);
                        break;
                    }
                }
                int diasParaReparto = 1;
                if(vp.getKvgr4() != null)
                    diasParaReparto = Integer.valueOf(vp.getKvgr4().replace("DA",""));

                TextInputEditText vp_Lunes = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_L"));
                TextInputEditText vp_Martes = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_K"));
                TextInputEditText vp_Miercoles = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_M"));
                TextInputEditText vp_Jueves = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_J"));
                TextInputEditText vp_Viernes = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_V"));
                TextInputEditText vp_Sabado = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_S"));

                String l = vp_Lunes.getText().toString().isEmpty()? null : vp_Lunes.getText().toString();
                String m = vp_Martes.getText().toString().isEmpty()? null : vp_Martes.getText().toString();
                String k = vp_Miercoles.getText().toString().isEmpty()? null : vp_Miercoles.getText().toString();
                String j = vp_Jueves.getText().toString().isEmpty()? null : vp_Jueves.getText().toString();
                String v = vp_Viernes.getText().toString().isEmpty()? null : vp_Viernes.getText().toString();
                String s = vp_Sabado.getText().toString().isEmpty()? null : vp_Sabado.getText().toString();

                asignarDiaReparto(diasParaReparto, 1, l, rep, rep_old);
                asignarDiaReparto(diasParaReparto, 2, m, rep, rep_old);
                asignarDiaReparto(diasParaReparto, 3, k, rep, rep_old);
                asignarDiaReparto(diasParaReparto, 4, j, rep, rep_old);
                asignarDiaReparto(diasParaReparto, 5, v, rep, rep_old);
                asignarDiaReparto(diasParaReparto, 6, s, rep, rep_old);

            }

        }
    }

    public static void asignarDiaReparto(int metodo, int diaPreventa, String secuencia, Visitas vp_reparto, Visitas vp_reparto_old){
        if( (secuencia != null && secuencia.trim().length() > 0) && vp_reparto != null){
            int diaReparto;
            if ((diaPreventa+metodo) > 6) {
                diaReparto = ((diaPreventa+metodo) - 6);
            } else {
                diaReparto = (diaPreventa+metodo);
            }
            int hours = Integer.valueOf(secuencia) / 60; //since both are ints, you get an int
            int minutes = Integer.valueOf(secuencia) % 60;
            String h = String.format(Locale.getDefault(),"%02d", hours);
            String m = String.format(Locale.getDefault(),"%02d", minutes);
            String secuenciaSAP = h+m;
            switch(diaReparto){
                case 1:
                    if(vp_reparto_old == null || vp_reparto_old.getLun_a().trim().length() == 0) {
                        vp_reparto.setLun_a(secuenciaSAP);
                        vp_reparto.setLun_de(secuenciaSAP);
                    }else{
                        vp_reparto.setLun_a(vp_reparto_old.getLun_a());
                        vp_reparto.setLun_de(vp_reparto_old.getLun_a());
                    }
                    break;
                case 2:
                    if(vp_reparto_old == null || vp_reparto_old.getMar_a().trim().length() == 0) {
                        vp_reparto.setMar_a(secuenciaSAP);
                        vp_reparto.setMar_de(secuenciaSAP);
                    }else{
                        vp_reparto.setMar_a(vp_reparto_old.getMar_a());
                        vp_reparto.setMar_de(vp_reparto_old.getMar_a());
                    }
                    break;
                case 3:
                    if(vp_reparto_old == null || vp_reparto_old.getMier_a().trim().length() == 0) {
                        vp_reparto.setMier_a(secuenciaSAP);
                        vp_reparto.setMier_de(secuenciaSAP);
                    }
                    else{
                        vp_reparto.setMier_a(vp_reparto_old.getMier_a());
                        vp_reparto.setMier_de(vp_reparto_old.getMier_de());
                    }
                    break;
                case 4:
                    if(vp_reparto_old == null || vp_reparto_old.getJue_a().trim().length() == 0) {
                        vp_reparto.setJue_a(secuenciaSAP);
                        vp_reparto.setJue_de(secuenciaSAP);
                    }else{
                        vp_reparto.setJue_a(vp_reparto_old.getJue_a());
                        vp_reparto.setJue_de(vp_reparto_old.getJue_de());
                    }
                    break;
                case 5:
                    if(vp_reparto_old == null || vp_reparto_old.getVie_a().trim().length() == 0) {
                        vp_reparto.setVie_a(secuenciaSAP);
                        vp_reparto.setVie_de(secuenciaSAP);
                    }else{
                        vp_reparto.setVie_a(vp_reparto_old.getVie_a());
                        vp_reparto.setVie_de(vp_reparto_old.getVie_de());
                    }
                    break;
                case 6:
                    if(vp_reparto_old == null || vp_reparto_old.getSab_a().trim().length() == 0) {
                        vp_reparto.setSab_a(secuenciaSAP);
                        vp_reparto.setSab_de(secuenciaSAP);
                    }else{
                        vp_reparto.setSab_a(vp_reparto_old.getSab_a());
                        vp_reparto.setSab_de(vp_reparto_old.getSab_de());
                    }
                    break;
            }
        }
    }

    private class VisitasLongClickListener implements TableDataLongClickListener<Visitas> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Visitas seleccionado) {
            String salida = seleccionado.getVptyp() + " " + seleccionado.getRuta();
            visitasSolicitud.remove(rowIndex);
            tb_visitas.setDataAdapter(new VisitasTableAdapter(getBaseContext(), visitasSolicitud));
            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    public static class ResetearVisitas implements Runnable {
        private Context context;
        private Activity activity;
        public ResetearVisitas(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }
        public void run() {
            Spinner modalidad_preventa = (Spinner)mapeoCamposDinamicos.get("W_CTE-KVGR5");
            OpcionSpinner opcion = (OpcionSpinner) modalidad_preventa.getSelectedItem();
            visitasSolicitud = mDBHelper.DeterminarPlanesdeVisita(PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_VKORG", ""), opcion.getId());

            tb_visitas.setDataAdapter(new VisitasTableAdapter(context, visitasSolicitud));
            if (tb_visitas.getLayoutParams() != null) {
                tb_visitas.getLayoutParams().height = 50;
                tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView ) * visitasSolicitud.size());
            }
        }
    }

    public class GuardarFormulario implements Runnable {
        private Context context;
        public GuardarFormulario(Context context) {
            this.context = context;
        }
        public void run() {
            String NextId = GUID;
            ContentValues insertValues = new ContentValues();
            ContentValues insertValuesOld = new ContentValues();
            ArrayList<String> listaFinal = (ArrayList<String>)listaCamposDinamicos.clone();
            boolean prueba  = listaFinal.addAll(listaCamposDinamicosEnca);

            for (int i = 0; i < listaFinal.size(); i++) {
                if(!listaCamposBloque.contains(listaFinal.get(i).trim()) && !listaFinal.get(i).equals("W_CTE-ENCUESTA") && !listaFinal.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                    try {
                        MaskedEditText tv;
                        String valor;
                        if(!listaFinal.get(i).equals("W_CTE-ENCUESTA") && !listaFinal.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                            if(listaCamposDinamicos.contains(listaFinal.get(i).trim())) {
                                tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaFinal.get(i)));
                                valor = tv.getText().toString();
                                insertValues.put("[" + listaFinal.get(i) + "]", valor);
                                tv = ((MaskedEditText) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                                if (tv != null) {
                                    valor = tv.getText().toString();
                                    insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                }
                            }
                            if(listaCamposDinamicosEnca.contains(listaFinal.get(i).trim())) {
                                tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get(listaFinal.get(i)));
                                if (tv != null) {
                                    valor = tv.getText().toString();
                                    insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                }
                            }
                            if(listaFinal.get(i).equals("W_CTE-COMENTARIOS")) {
                                tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaFinal.get(i)));
                                valor = tv.getText().toString();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                                Date date = new Date();
                                if(comentarios.size() == 0)
                                    insertValues.put("[" + listaFinal.get(i) + "]", valor);
                                else
                                    insertValues.put("[" + listaFinal.get(i) + "]", comentarios.get(0).getComentario()+"("+dateFormat.format(date)+"): "+valor);
                            }
                        }

                    } catch (Exception e) {
                        try {
                            Spinner sp;
                            String valor;
                            if(listaCamposDinamicos.contains(listaFinal.get(i).trim())) {
                                sp = ((Spinner) mapeoCamposDinamicos.get(listaFinal.get(i)));
                                if (sp != null) {
                                    valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                    insertValues.put("[" + listaFinal.get(i) + "]", valor);
                                }

                                sp = ((Spinner) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                                if (sp != null) {
                                    valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                    insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                }
                            }
                            if(listaCamposDinamicosEnca.contains(listaFinal.get(i).trim())) {
                                sp = ((Spinner) mapeoCamposDinamicosEnca.get(listaFinal.get(i).trim()));
                                if (sp != null) {
                                    valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                    insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                    if(listaFinal.get(i).trim().equals("W_CTE-BZIRK") && !insertValues.containsKey("[W_CTE-BZIRK]")){
                                        insertValues.put("[W_CTE-BZIRK]", valor);
                                    }
                                }

                            }
                        } catch (Exception e2) {
                            try {
                                CheckBox check;
                                String valor;
                                if(listaCamposDinamicos.contains(listaFinal.get(i).trim())) {
                                    check = ((CheckBox) mapeoCamposDinamicos.get(listaFinal.get(i).trim()));
                                    valor = "";
                                    if (check.isChecked()) {
                                        valor = "X";
                                    }
                                    insertValues.put("[" + listaFinal.get(i) + "]", valor);

                                    check = ((CheckBox) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                                    if (check != null) {
                                        valor = "";
                                        if (check.isChecked()) {
                                            valor = "X";
                                        }
                                        insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                    }
                                }
                                if(listaCamposDinamicosEnca.contains(listaFinal.get(i).trim())) {
                                    check = ((CheckBox) mapeoCamposDinamicosEnca.get(listaFinal.get(i)));
                                    if (check != null) {
                                        valor = "";
                                        if (check.isChecked()) {
                                            valor = "X";
                                        }
                                        insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                    }
                                }
                            }catch(Exception e3){
                                Toasty.error(getBaseContext(),"No se pudo obtener el valor del campo "+listaFinal.get(i)).show();
                            }
                        }
                    }
                }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.
                    switch(listaFinal.get(i)){
                        case "W_CTE-CONTACTOS":
                            ContentValues contactoValues = new ContentValues();
                            ContentValues contactoValuesOld = new ContentValues();
                            if (solicitudSeleccionada.size() > 0) {
                                mDb.delete(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), "id_solicitud=?", new String[]{GUID});
                                mDb.delete(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_OLD_HH(), "id_solicitud=?", new String[]{GUID});
                            }
                            for (int c = 0; c < contactosSolicitud.size(); c++) {
                                contactoValues.put("id_solicitud", NextId);
                                contactoValues.put("name1", contactosSolicitud.get(c).getName1());
                                contactoValues.put("namev", contactosSolicitud.get(c).getNamev());
                                contactoValues.put("telf1", contactosSolicitud.get(c).getTelf1());
                                contactoValues.put("house_num1", contactosSolicitud.get(c).getHouse_num1());
                                contactoValues.put("street", contactosSolicitud.get(c).getStreet());
                                contactoValues.put("gbdat", contactosSolicitud.get(c).getGbdat());
                                contactoValues.put("country", contactosSolicitud.get(c).getCountry());
                                contactoValues.put("pafkt", contactosSolicitud.get(c).getPafkt());
                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), null, contactoValues);
                                    contactoValues.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Contacto de Solicitud Nuevo", Toast.LENGTH_SHORT).show();
                                }
                            }

                            for (int c = 0; c < contactosSolicitud_old.size(); c++) {
                                contactoValuesOld.put("id_solicitud", NextId);
                                contactoValuesOld.put("name1", contactosSolicitud_old.get(c).getName1());
                                contactoValuesOld.put("namev", contactosSolicitud_old.get(c).getNamev());
                                contactoValuesOld.put("telf1", contactosSolicitud_old.get(c).getTelf1());
                                contactoValuesOld.put("house_num1", contactosSolicitud_old.get(c).getHouse_num1());
                                contactoValuesOld.put("street", contactosSolicitud_old.get(c).getStreet());
                                contactoValuesOld.put("gbdat", contactosSolicitud_old.get(c).getGbdat());
                                contactoValuesOld.put("country", contactosSolicitud_old.get(c).getCountry());
                                contactoValuesOld.put("pafkt", contactosSolicitud_old.get(c).getPafkt());
                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_OLD_HH(), null, contactoValuesOld);
                                    contactoValuesOld.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Contacto de Solicitud Actual", Toast.LENGTH_SHORT).show();
                                }
                            }

                            break;
                        case "W_CTE-IMPUESTOS":
                            ContentValues impuestoValues = new ContentValues();
                            ContentValues impuestoValuesOld = new ContentValues();
                            int del;
                            if (solicitudSeleccionada.size() > 0) {
                                del = mDb.delete(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_HH(), "id_solicitud=?", new String[]{GUID});
                                del = mDb.delete(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_OLD_HH(), "id_solicitud=?", new String[]{GUID});
                            }
                            for (int c = 0; c < impuestosSolicitud.size(); c++) {
                                impuestoValues.put("id_solicitud", NextId);
                                impuestoValues.put("vtext", impuestosSolicitud.get(c).getVtext());
                                impuestoValues.put("vtext2", impuestosSolicitud.get(c).getVtext2());
                                impuestoValues.put("tatyp", impuestosSolicitud.get(c).getTatyp());
                                impuestoValues.put("taxkd", impuestosSolicitud.get(c).getTaxkd());
                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_HH(), null, impuestoValues);
                                    impuestoValues.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Impuesto de Solicitud Nuevo", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (solicitudSeleccionadaOld.size() > 0) {
                                del = mDb.delete(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_OLD_HH(), "id_solicitud=?", new String[]{GUID});
                            }
                            for (int c = 0; c < impuestosSolicitud_old.size(); c++) {
                                impuestoValuesOld.put("id_solicitud", NextId);
                                impuestoValuesOld.put("vtext", impuestosSolicitud_old.get(c).getVtext());
                                impuestoValuesOld.put("vtext2", impuestosSolicitud_old.get(c).getVtext2());
                                impuestoValuesOld.put("tatyp", impuestosSolicitud_old.get(c).getTatyp());
                                impuestoValuesOld.put("taxkd", impuestosSolicitud_old.get(c).getTaxkd());
                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_OLD_HH(), null, impuestoValuesOld);
                                    impuestoValuesOld.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Impuesto de Solicitud Nuevo", Toast.LENGTH_SHORT).show();
                                }
                            }

                            break;
                        case "W_CTE-INTERLOCUTORES":
                            ContentValues interlocutorValues = new ContentValues();
                            if (solicitudSeleccionada.size() > 0) {
                                mDb.delete(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_HH(), "id_solicitud=?", new String[]{GUID});
                            }
                            for (int c = 0; c < interlocutoresSolicitud.size(); c++) {
                                interlocutorValues.put("id_solicitud", NextId);
                                interlocutorValues.put("parvw", interlocutoresSolicitud.get(c).getParvw());
                                interlocutorValues.put("kunn2", interlocutoresSolicitud.get(c).getKunn2());
                                interlocutorValues.put("name1", interlocutoresSolicitud.get(c).getName1());
                                interlocutorValues.put("vtext", interlocutoresSolicitud.get(c).getVtext());

                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_HH(), null, interlocutorValues);
                                    interlocutorValues.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Interlocutor de Solicitud Nueva", Toast.LENGTH_SHORT).show();
                                }
                            }
                            //valroes OLD
                            ContentValues interlocutorValuesOld = new ContentValues();
                            if (solicitudSeleccionadaOld.size() > 0) {
                                mDb.delete(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_OLD_HH(), "id_solicitud=?", new String[]{GUID});
                            }
                            for (int c = 0; c < interlocutoresSolicitud_old.size(); c++) {
                                interlocutorValuesOld.put("id_solicitud", NextId);
                                interlocutorValuesOld.put("parvw", interlocutoresSolicitud_old.get(c).getParvw());
                                interlocutorValuesOld.put("kunn2", interlocutoresSolicitud_old.get(c).getKunn2());
                                interlocutorValuesOld.put("name1", interlocutoresSolicitud_old.get(c).getName1());
                                interlocutorValuesOld.put("vtext", interlocutoresSolicitud_old.get(c).getVtext());

                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_OLD_HH(), null, interlocutorValuesOld);
                                    interlocutorValuesOld.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Interlocutor de Solicitud Actual", Toast.LENGTH_SHORT).show();
                                }
                            }

                            break;
                        case "W_CTE-BANCOS":
                            ContentValues bancoValues = new ContentValues();
                            try {
                                if (solicitudSeleccionada.size() > 0) {
                                    mDb.delete(VariablesGlobales.getTABLA_BLOQUE_BANCO_HH(), "id_solicitud=?", new String[]{GUID});
                                }
                                for (int c = 0; c < bancosSolicitud.size(); c++) {
                                    bancoValues.put("id_solicitud", NextId);
                                    bancoValues.put("bankl", bancosSolicitud.get(c).getBankl());
                                    bancoValues.put("bankn", bancosSolicitud.get(c).getBankn());
                                    bancoValues.put("banks", bancosSolicitud.get(c).getBanks());
                                    bancoValues.put("bkont", bancosSolicitud.get(c).getBkont());
                                    bancoValues.put("bkref", bancosSolicitud.get(c).getBkref());
                                    bancoValues.put("bvtyp", bancosSolicitud.get(c).getBvtyp());
                                    bancoValues.put("koinh", bancosSolicitud.get(c).getKoinh());
                                    bancoValues.put("task", bancosSolicitud.get(c).getTask());
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_BANCO_HH(), null, bancoValues);
                                    bancoValues.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Bancos de Solicitud Nueva", Toast.LENGTH_SHORT).show();
                            }
                            //Valores OLD
                            ContentValues bancoValuesOld = new ContentValues();
                            try {
                                if (solicitudSeleccionadaOld.size() > 0) {
                                    mDb.delete(VariablesGlobales.getTABLA_BLOQUE_BANCO_OLD_HH(), "id_solicitud=?", new String[]{GUID});
                                }
                                for (int c = 0; c < bancosSolicitud_old.size(); c++) {
                                    bancoValuesOld.put("id_solicitud", NextId);
                                    bancoValuesOld.put("bankl", bancosSolicitud_old.get(c).getBankl());
                                    bancoValuesOld.put("bankn", bancosSolicitud_old.get(c).getBankn());
                                    bancoValuesOld.put("banks", bancosSolicitud_old.get(c).getBanks());
                                    bancoValuesOld.put("bkont", bancosSolicitud_old.get(c).getBkont());
                                    bancoValuesOld.put("bkref", bancosSolicitud_old.get(c).getBkref());
                                    bancoValuesOld.put("bvtyp", bancosSolicitud_old.get(c).getBvtyp());
                                    bancoValuesOld.put("koinh", bancosSolicitud_old.get(c).getKoinh());
                                    bancoValuesOld.put("task", bancosSolicitud_old.get(c).getTask());
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_BANCO_OLD_HH(), null, bancoValuesOld);
                                    bancoValuesOld.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Bancos de Solicitud Actual", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "W_CTE-VISITAS":
                            ContentValues visitaValues = new ContentValues();
                            try {
                                if (solicitudSeleccionada.size() > 0) {
                                    mDb.delete(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), "id_solicitud=?", new String[]{GUID});
                                }
                                for (int c = 0; c < visitasSolicitud.size(); c++) {
                                    visitaValues.put("id_solicitud", NextId);
                                    visitaValues.put("ruta", visitasSolicitud.get(c).getRuta());
                                        visitaValues.put("kvgr4", visitasSolicitud.get(c).getKvgr4());
                                        visitaValues.put("vptyp", visitasSolicitud.get(c).getVptyp());
                                        visitaValues.put("f_frec", visitasSolicitud.get(c).getF_frec());
                                        visitaValues.put("lun_de", visitasSolicitud.get(c).getLun_de());
                                        visitaValues.put("mar_de", visitasSolicitud.get(c).getMar_de());
                                        visitaValues.put("mier_de", visitasSolicitud.get(c).getMier_de());
                                        visitaValues.put("jue_de", visitasSolicitud.get(c).getJue_de());
                                        visitaValues.put("vie_de", visitasSolicitud.get(c).getVie_de());
                                        visitaValues.put("sab_de", visitasSolicitud.get(c).getSab_de());
                                        visitaValues.put("dom_de", visitasSolicitud.get(c).getDom_de());
                                        visitaValues.put("lun_a", visitasSolicitud.get(c).getLun_a());
                                        visitaValues.put("mar_a", visitasSolicitud.get(c).getMar_a());
                                        visitaValues.put("mier_a", visitasSolicitud.get(c).getMier_a());
                                        visitaValues.put("jue_a", visitasSolicitud.get(c).getJue_a());
                                        visitaValues.put("vie_a", visitasSolicitud.get(c).getVie_a());
                                        visitaValues.put("sab_a", visitasSolicitud.get(c).getSab_a());
                                        visitaValues.put("dom_a", visitasSolicitud.get(c).getDom_a());
                                        visitaValues.put("f_ico", visitasSolicitud.get(c).getF_ico());
                                        visitaValues.put("f_fco", visitasSolicitud.get(c).getF_fco());
                                        visitaValues.put("f_ini", visitasSolicitud.get(c).getF_ini());
                                        visitaValues.put("f_fin", visitasSolicitud.get(c).getF_fin());
                                        visitaValues.put("fcalid", visitasSolicitud.get(c).getFcalid());
                                    if(visitasSolicitud.get(c).getRuta().trim().length() > 0) {
                                        mDb.insert(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), null, visitaValues);
                                    }
                                    visitaValues.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Visitas de Solicitud Nueva", Toast.LENGTH_SHORT).show();
                            }
                            //OLD
                            ContentValues visitaValuesOld = new ContentValues();
                            try {
                                if (solicitudSeleccionada.size() > 0) {
                                    mDb.delete(VariablesGlobales.getTABLA_BLOQUE_VISITA_OLD_HH(), "id_solicitud=?", new String[]{GUID});
                                }
                                for (int c = 0; c < visitasSolicitud_old.size(); c++) {
                                    visitaValuesOld.put("id_solicitud", NextId);
                                    visitaValuesOld.put("ruta", visitasSolicitud_old.get(c).getRuta());
                                        visitaValuesOld.put("kvgr4", visitasSolicitud_old.get(c).getKvgr4());
                                        visitaValuesOld.put("vptyp", visitasSolicitud_old.get(c).getVptyp());
                                        visitaValuesOld.put("f_frec", visitasSolicitud_old.get(c).getF_frec());
                                        visitaValuesOld.put("lun_de", visitasSolicitud_old.get(c).getLun_de());
                                        visitaValuesOld.put("mar_de", visitasSolicitud_old.get(c).getMar_de());
                                        visitaValuesOld.put("mier_de", visitasSolicitud_old.get(c).getMier_de());
                                        visitaValuesOld.put("jue_de", visitasSolicitud_old.get(c).getJue_de());
                                        visitaValuesOld.put("vie_de", visitasSolicitud_old.get(c).getVie_de());
                                        visitaValuesOld.put("sab_de", visitasSolicitud_old.get(c).getSab_de());
                                        visitaValuesOld.put("dom_de", visitasSolicitud_old.get(c).getDom_de());
                                        visitaValuesOld.put("lun_a", visitasSolicitud_old.get(c).getLun_a());
                                        visitaValuesOld.put("mar_a", visitasSolicitud_old.get(c).getMar_a());
                                        visitaValuesOld.put("mier_a", visitasSolicitud_old.get(c).getMier_a());
                                        visitaValuesOld.put("jue_a", visitasSolicitud_old.get(c).getJue_a());
                                        visitaValuesOld.put("vie_a", visitasSolicitud_old.get(c).getVie_a());
                                        visitaValuesOld.put("sab_a", visitasSolicitud_old.get(c).getSab_a());
                                        visitaValuesOld.put("dom_a", visitasSolicitud_old.get(c).getDom_a());
                                        visitaValuesOld.put("f_ico", visitasSolicitud_old.get(c).getF_ico());
                                        visitaValuesOld.put("f_fco", visitasSolicitud_old.get(c).getF_fco());
                                        visitaValuesOld.put("f_ini", visitasSolicitud_old.get(c).getF_ini());
                                        visitaValuesOld.put("f_fin", visitasSolicitud_old.get(c).getF_fin());
                                        visitaValuesOld.put("fcalid", visitasSolicitud_old.get(c).getFcalid());

                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_VISITA_OLD_HH(), null, visitaValuesOld);
                                    visitaValuesOld.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Visitas de Solicitud Actual", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "W_CTE-ADJUNTOS":
                            ContentValues adjuntoValues = new ContentValues();
                            try {
                                if(solicitudSeleccionada.size() > 0){
                                    mDb.delete(VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD(),"id_solicitud=?",new String[]{GUID});
                                }
                                for (int c = 0; c < adjuntosSolicitud.size(); c++) {
                                    Adjuntos adjunto = adjuntosSolicitud.get(c);
                                    adjuntoValues.put("id_solicitud", NextId);
                                    adjuntoValues.put("tipo", adjunto.getType());
                                    adjuntoValues.put("nombre", adjunto.getName());
                                    adjuntoValues.put("imagen", adjunto.getImage());
                                    mDb.insert(VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD(), null, adjuntoValues);
                                    adjuntoValues.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Adjuntos de Solicitud. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case "W_CTE-HORARIOS":
                            ContentValues horariosValues = new ContentValues();
                            try {
                                if(solicitudSeleccionada.size() > 0){
                                    mDb.delete(VariablesGlobales.getTablaHorariosSolicitud(),"id_solicitud=?",new String[]{GUID});
                                }
                                for (int c = 0; c < horariosSolicitud.size(); c++) {
                                    Horarios horario = horariosSolicitud.get(c);
                                    horariosValues.put("id_solicitud", NextId);
                                    horariosValues.put("moab1", horario.getMoab1());
                                    horariosValues.put("mobi1", horario.getMobi1());
                                    horariosValues.put("diab1", horario.getDiab1());
                                    horariosValues.put("dibi1", horario.getDibi1());
                                    horariosValues.put("miab1", horario.getMiab1());
                                    horariosValues.put("mibi1", horario.getMibi1());
                                    horariosValues.put("doab1", horario.getDoab1());
                                    horariosValues.put("dobi1", horario.getDobi1());
                                    horariosValues.put("frab1", horario.getFrab1());
                                    horariosValues.put("frbi1", horario.getFrbi1());
                                    horariosValues.put("saab1", horario.getSaab1());
                                    horariosValues.put("sabi1", horario.getSabi1());
                                    horariosValues.put("soab1", horario.getSoab1());
                                    horariosValues.put("sobi1", horario.getSobi1());

                                    horariosValues.put("moab2", horario.getMoab2());
                                    horariosValues.put("mobi2", horario.getMobi2());
                                    horariosValues.put("diab2", horario.getDiab2());
                                    horariosValues.put("dibi2", horario.getDibi2());
                                    horariosValues.put("miab2", horario.getMiab2());
                                    horariosValues.put("mibi2", horario.getMibi2());
                                    horariosValues.put("doab2", horario.getDoab2());
                                    horariosValues.put("dobi2", horario.getDobi2());
                                    horariosValues.put("frab2", horario.getFrab2());
                                    horariosValues.put("frbi2", horario.getFrbi2());
                                    horariosValues.put("saab2", horario.getSaab2());
                                    horariosValues.put("sabi2", horario.getSabi2());
                                    horariosValues.put("soab2", horario.getSoab2());
                                    horariosValues.put("sobi2", horario.getSobi2());
                                    mDb.insert(VariablesGlobales.getTablaHorariosSolicitud(), null, horariosValues);
                                    horariosValues.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Adjuntos de Solicitud. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
                            }
                            break;
                    }
                }
            }
            try {
                //Datos que siemrpe deben ir cuando se crea por primera vez.
                insertValues.put("[W_CTE-KTOKD]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_KTOKD",""));
                Spinner sp = ((Spinner) mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR"));
                String id_aprobador = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                insertValues.put("[W_CTE-KUNNR]", codigoCliente);
                insertValues.put("[SIGUIENTE_APROBADOR]", id_aprobador);
                insertValues.put("[W_CTE-BUKRS]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_BUKRS",""));
                insertValues.put("[W_CTE-RUTAHH]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_RUTAHH",""));
                insertValues.put("[W_CTE-VKORG]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_VKORG",""));
                insertValues.put("[id_solicitud]", NextId);
                insertValues.put("[tipform]", tipoSolicitud);
                insertValues.put("[ususol]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("userMC",""));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                Date date = new Date();
                //ContentValues initialValues = new ContentValues();

                //mDBHelper.getWritableDatabase().insert("FormHvKof_solicitud", null, insertValues);
                if(solicitudSeleccionada.size() > 0){
                    if(solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia")) {
                        insertValues.put("[estado]", "Modificado");
                        insertValuesOld.put("[estado]", "Modificado");
                    }
                    long modifico = mDb.update("FormHvKof_solicitud", insertValues, "id_solicitud = ?", new String[]{solicitudSeleccionada.get(0).get("id_solicitud")});
                    if(solicitudSeleccionadaOld.size() > 0) {
                        long modifico2 = mDb.update("FormHvKof_old_solicitud", insertValuesOld, "id_solicitud = ?", new String[]{solicitudSeleccionadaOld.get(0).get("id_solicitud")});
                    }
                    Toasty.success(getApplicationContext(), "Registro modificado con éxito", Toast.LENGTH_SHORT).show();
                }else {
                    insertValues.put("[FECCRE]", dateFormat.format(date));
                    insertValues.put("[estado]", "Nuevo");
                    long inserto = mDb.insertOrThrow("FormHvKof_solicitud", null, insertValues);

                    insertValuesOld.put("[W_CTE-KUNNR]", codigoCliente);
                    insertValuesOld.put("[estado]", "Nuevo");
                    insertValuesOld.put("[W_CTE-BUKRS]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_BUKRS",""));
                    insertValuesOld.put("[W_CTE-RUTAHH]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_RUTAHH",""));
                    insertValuesOld.put("[W_CTE-VKORG]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_VKORG",""));
                    insertValuesOld.put("[id_solicitud]", NextId);
                    insertValuesOld.put("[tipform]", tipoSolicitud);
                    insertValuesOld.put("[ususol]", PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("userMC",""));
                    insertValuesOld.put("[FECCRE]", dateFormat.format(date));
                    long insertoOld = mDb.insertOrThrow("FormHvKof_old_solicitud", null, insertValuesOld);

                    Toasty.success(getApplicationContext(), "Solicitud de Modificación Creada", Toast.LENGTH_LONG).show();
                    //Una vez finalizado el proceso de guardado, se limpia la solicitud para una nueva.
                    Intent sol = getIntent();
                    sol.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    SolicitudModificacionActivity.this.finish();
                    //Bundle par = new Bundle();
                    //par.putString("tipo_solicitud",tipoSolicitud);
                    //SolicitudActivity.this.startActivity(sol);

                }
            } catch (Exception e) {
                Toasty.error(getApplicationContext(), "Error Insertando Solicitud."+e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    public static void SolicitudPermitida(Context context, Activity activity, ArrayList<JsonArray> mensajes) {
        String mensaje="";
        if(mensajes.size() > 0 && mensajes.get(0)  != null){
            mensaje = mensajes.get(0).getAsJsonArray().get(0).getAsJsonObject().get("mensaje").getAsString();
            if(!mensaje.isEmpty()) {
                Toasty.error(context.getApplicationContext(), mensaje,Toasty.LENGTH_LONG).show();
                //Deshabilitar el menu para que no pueda guardar el formulario pero si pueda ver la informacion del cliente.
                LinearLayout ll = activity.findViewById(R.id.LinearLayoutMain);
                DrawerLayout.LayoutParams h = new DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

                h.setMargins(0, 0, 0, 0);
                ll.setLayoutParams(h);
                bottomNavigation.setVisibility(View.GONE);
                bottomNavigation.animate().translationY(150);

                LinearLayout v = new LinearLayout(context.getApplicationContext());
                v.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(context.getApplicationContext());
                TextView subTitle = new TextView(context.getApplicationContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        LinearLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
                title.setLayoutParams(lp);
                title.setText(mensaje);
                title.setTextColor(Color.WHITE);
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                subTitle.setLayoutParams(lp);
                subTitle.setText("No se puede generar la solicitud de "+((AppCompatActivity)activity).getSupportActionBar().getSubtitle()+" en estos momentos.");
                subTitle.setTextColor(Color.WHITE);
                subTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                v.addView(title);
                v.addView(subTitle);

                ((AppCompatActivity)activity).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                ((AppCompatActivity)activity).getSupportActionBar().setCustomView(v);
            }
        }
    }
    public static void LlenarCampos(Context context, Activity activity, ArrayList<JsonArray> estructurasSAP){
        if(estructurasSAP.size() == 0){
            Toasty.error(context.getApplicationContext(),"No se pudo obtener la informacion del cliente!").show();
            activity.finish();
            return;
        }

        cliente = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Cliente");
        notaEntrega = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("NotaEntrega");
        factura = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Factura");
        telefonos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Telefonos");
        faxes = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Faxes");
        contactos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Contactos");
        interlocutores = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Interlocutores");
        impuestos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Impuestos");
        bancos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Bancos");
        visitas = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Visitas");
        horarios = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Horarios");

        Gson gson = new Gson();
        //Si es un cierre de URUGUAY se debe validar el cliente es una cuenta pagadora para que se realice por el formulario correcto ID = 45
        //Para saber si es cuenta pagadora voy a validar el interlocutor RG de SAP
        if(interlocutores.size() > 0 && (PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661") || PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD","").equals("Z001"))) {
            Interlocutor interlocutor = null;

            for (int x = 0; x < interlocutores.size(); x++) {
                interlocutor = gson.fromJson(interlocutores.get(x), Interlocutor.class);
                if (tipoSolicitud.equals("5") && interlocutor.getParvw().equals("RG") && !interlocutor.getKunn2().equals(cliente.get(0).getAsJsonObject().get("W_CTE-KUNNR").getAsString())) {
                    //Toasty.error(context.getApplicationContext(), "Cliente se debe dar de baja por el formulario de cierre CON cuenta pagadora!").show();
                    Bundle bc = new Bundle();
                    bc.putString("tipoSolicitud", "45"); //id de solicitud
                    bc.putString("codigoCliente", codigoCliente);
                    Intent intent = new Intent(context, SolicitudModificacionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    activity.finish();
                    intent.putExtras(bc); //Pase el parametro el Intent
                    activity.overridePendingTransition(0, 0);
                    ContextCompat.startActivity(context, intent, null);
                    activity.overridePendingTransition(0, 0);
                }
                if (tipoSolicitud.equals("45") && interlocutor.getParvw().equals("RG") && interlocutor.getKunn2().equals(cliente.get(0).getAsJsonObject().get("W_CTE-KUNNR").getAsString())) {
                    //Toasty.error(context.getApplicationContext(), "Cliente se debe dar de baja por el formulario de baja SIN cuenta pagadora!").show();
                    Bundle bc = new Bundle();
                    bc.putString("tipoSolicitud", "5"); //id de solicitud
                    bc.putString("codigoCliente", codigoCliente);
                    Intent intent = new Intent(context, SolicitudModificacionActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    activity.finish();
                    intent.putExtras(bc); //Pase el parametro el Intent
                    activity.overridePendingTransition(0, 0);
                    ContextCompat.startActivity(context, intent, null);
                    activity.overridePendingTransition(0, 0);
                }
            }
        }
        //Titulo deseado
        activity.setTitle(activity.getTitle()+cliente.get(0).getAsJsonObject().get("W_CTE-NAME1").getAsString());

        ArrayList<String> listaFinal = (ArrayList<String>)listaCamposDinamicos.clone();
        boolean prueba  = listaFinal.addAll(listaCamposDinamicosEnca);

        for (int i = 0; i < listaFinal.size(); i++) {
        //for (int i = 0; i < listaCamposDinamicos.size(); i++) {
            if(!listaCamposBloque.contains(listaFinal.get(i).trim()) && !listaFinal.get(i).equals("W_CTE-ENCUESTA") && !listaFinal.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                try {
                    MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaFinal.get(i)));
                    if(tv != null) {
                        tv.setText(cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString());
                        if(listaFinal.get(i).equals("W_CTE-AUFSD") && (tipoSolicitud.equals("5") || tipoSolicitud.equals("45"))){
                            tv.setText("YA");
                        }
                        if (listaFinal.get(i).equals("W_CTE-PO_BOX") && tv.getText().toString().trim().equals("")) {
                            tv.setText(".");
                        }
                        if (listaFinal.get(i).equals("W_CTE-DATBI")) {
                            if (cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().replace("-", "").equals("00000000"))
                                tv.setText("99991231");
                            else
                                tv.setText(cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().replace("-",""));
                        }
                        if (listaFinal.get(i).equals("W_CTE-DATAB"))
                        {
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String fechaSistema = df.format(c);
                            tv.setText(fechaSistema);
                        }
                        else if (listaFinal.get(i).equals("W_CTE-HITYP") && cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim().isEmpty())
                        {
                            tv.setText("B");
                        }

                        tv = ((MaskedEditText) mapeoCamposDinamicosOld.get(listaFinal.get(i).trim()));
                        if (tv != null) {
                            tv.setText(cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString());
                            if (listaFinal.get(i).equals("W_CTE-DATAB")) {
                                Date c = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String fechaSistema = df.format(c);
                                tv.setText(fechaSistema);
                            }
                            if (listaFinal.get(i).equals("W_CTE-DATBI")) {
                                tv.setText(cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString().replace("-",""));
                            }
                        }

                    }
                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get(listaFinal.get(i).trim()));
                    if(tv != null) {
                        tv.setText(cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString());
                    }
                    if(listaFinal.get(i).equals("W_CTE-SMTP_ADDR")){
                        correoValidado = isValidEmail(cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString());
                    }
                    if(listaFinal.get(i).equals("W_CTE-STCD1")){
                        cedulaValidada = ValidarCedula(cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString(),cliente.get(0).getAsJsonObject().get("W_CTE-KATR3").getAsString(),cliente.get(0).getAsJsonObject().get("W_CTE-BUKRS").getAsString());
                    }
                    if(listaFinal.get(i).equals("W_CTE-STCDT")){
                        cedulaValidada = ValidarCedula(cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString(),cliente.get(0).getAsJsonObject().get("W_CTE-STCDT").getAsString(),cliente.get(0).getAsJsonObject().get("W_CTE-BUKRS").getAsString());
                    }
                } catch (Exception e) {
                    try {
                        Spinner sp = ((Spinner) mapeoCamposDinamicos.get(listaFinal.get(i)));
                        if(sp != null) {
                            //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP.
                            if(VariablesGlobales.getIndex(sp, cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()) == -1 ){
                                ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) sp.getAdapter());
                                OpcionSpinner opcionSAP = new OpcionSpinner(cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim(),cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()+" - "+cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim());
                                dataAdapter.add(opcionSAP);
                                dataAdapter.notifyDataSetChanged();
                            }
                            sp.setSelection(VariablesGlobales.getIndex(sp, cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()));
                            if (listaFinal.get(i).trim().equals("W_CTE-VSBED") && PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")) {
                                String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_VKORG",""), PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_RUTAHH",""));
                                sp.setSelection(VariablesGlobales.getIndex(sp, condicionExpedicion));
                            }
                        }

                        sp = ((Spinner) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                        if(sp != null) {
                            //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP.
                            if(VariablesGlobales.getIndex(sp, cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()) == -1 ){
                                ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) sp.getAdapter());
                                OpcionSpinner opcionSAP = new OpcionSpinner(cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim(),cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()+" - "+cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim());
                                dataAdapter.add(opcionSAP);
                                dataAdapter.notifyDataSetChanged();
                            }
                            sp.setSelection(VariablesGlobales.getIndex(sp, cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()));
                        }

                        sp = ((Spinner) mapeoCamposDinamicosEnca.get(listaFinal.get(i)));
                        if(sp != null) {
                            //Si no existe la opcion, crear la opcion para garantizar AL MENOS no perder el valor que viene de SAP.
                            if(VariablesGlobales.getIndex(sp, cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()) == -1 ){
                                ArrayAdapter<OpcionSpinner> dataAdapter = ((ArrayAdapter<OpcionSpinner>) sp.getAdapter());
                                OpcionSpinner opcionSAP = new OpcionSpinner(cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim(),cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()+" - "+cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim());
                                dataAdapter.add(opcionSAP);
                                dataAdapter.notifyDataSetChanged();
                            }
                            sp.setSelection(VariablesGlobales.getIndex(sp, cliente.get(0).getAsJsonObject().get(listaFinal.get(i)).getAsString().trim()));
                        }
                    } catch (Exception e2) {
                        try {
                            CheckBox check = ((CheckBox) mapeoCamposDinamicos.get(listaFinal.get(i)));
                            CheckBox checkold = ((CheckBox) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                            String valor = "";
                            if (cliente.get(0).getAsJsonObject().get(listaFinal.get(i).trim()).getAsString().length() > 0) {
                                check.setChecked(true);
                                checkold.setChecked(true);
                                check.getButtonDrawable().jumpToCurrentState();
                                checkold.getButtonDrawable().jumpToCurrentState();
                            }

                        }catch(Exception e3){
                            //Toasty.error(context,"No se pudo obtener el valor del campo "+listaFinal.get(i)).show();
                        }
                    }
                }
            }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.

                switch(listaFinal.get(i)) {
                    case "W_CTE-CONTACTOS":
                        Contacto contacto = null;

                        for (int x = 0; x < contactos.size(); x++) {
                            contacto = gson.fromJson(contactos.get(x), Contacto.class);
                            contacto.setGbdat(contacto.getGbdat().replace("-", ""));
                            if (contacto.getGbdat().trim().replace("0", "").length() == 0)
                                contacto.setGbdat("");
                            contactosSolicitud.add(contacto);
                            try {
                                contactosSolicitud_old.add((Contacto) contacto.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (contactosSolicitud != null) {
                            tb_contactos.setDataAdapter(new ContactoTableAdapter(context, contactosSolicitud));
                            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height + (alturaFilaTableView * contactosSolicitud.size());
                        }
                        break;
                    case "W_CTE-IMPUESTOS":
                        Impuesto impuesto = null;
                        ArrayList<Impuesto> todos = mDBHelper.getImpuestosPais();
                        for (int x = 0; x < impuestos.size(); x++) {

                            impuesto = gson.fromJson(impuestos.get(x), Impuesto.class);
                            for (int y = 0; y < todos.size(); y++) {
                                if (todos.get(y).getTatyp().equals(impuestos.get(x).getAsJsonObject().get("W_CTE-TATYP").getAsString()) && todos.get(y).getTaxkd().equals(impuestos.get(x).getAsJsonObject().get("W_CTE-TAXKD").getAsString())) {
                                    impuesto.setVtext(todos.get(y).getVtext());
                                    impuesto.setVtext2(todos.get(y).getVtext2());
                                    break;
                                }
                            }
                            impuestosSolicitud.add(impuesto);
                            try {
                                impuestosSolicitud_old.add((Impuesto) impuesto.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (impuestosSolicitud != null) {
                            tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(context, impuestosSolicitud));
                            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height + (alturaFilaTableView * impuestosSolicitud.size());
                        }
                        break;
                    case "W_CTE-INTERLOCUTORES":
                        Interlocutor interlocutor = null;

                        for (int x = 0; x < interlocutores.size(); x++) {
                            interlocutor = gson.fromJson(interlocutores.get(x), Interlocutor.class);
                            interlocutoresSolicitud.add(interlocutor);
                            try {
                                interlocutoresSolicitud_old.add((Interlocutor) interlocutor.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (interlocutoresSolicitud != null) {
                            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(context, interlocutoresSolicitud));
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height + (alturaFilaTableView * interlocutoresSolicitud.size());
                        }
                        break;
                    case "W_CTE-BANCOS":
                        Banco banco = null;

                        for (int x = 0; x < bancos.size(); x++) {
                            banco = gson.fromJson(bancos.get(x), Banco.class);
                            bancosSolicitud.add(banco);
                            try {
                                bancosSolicitud_old.add((Banco) banco.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (bancosSolicitud != null) {
                            tb_bancos.setDataAdapter(new BancoTableAdapter(context, bancosSolicitud));
                            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height + (alturaFilaTableView * bancosSolicitud.size());
                        }
                        break;
                    case "W_CTE-VISITAS":
                        llenarDiasDeVisita(context);
                        break;
                    case "W_CTE-HORARIOS":
                        Horarios horario = null;
                        for (int x = 0; x < horarios.size(); x++) {
                            horario = gson.fromJson(horarios.get(x), Horarios.class);
                            horariosSolicitud.add(horario);
                            /*try {
                                horariosSolicitud_old.add((Interlocutor) horario.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }*/
                        }
                        if(horariosSolicitud.size() == 0){
                            horariosSolicitud.add(new Horarios(GUID,"00:00:00"));
                        }
                        if (horariosSolicitud != null) {
                            final String[] dias_prefijo = {"mo", "di", "mi", "do", "fr", "sa", "so"};
                            final String[] dias_sufijo = {"ab1", "bi1", "ab2", "bi2"};
                            for (int y = 0; y < dias_prefijo.length; y++) {
                                for (int z = 0; z < dias_sufijo.length; z++) {
                                    ((MaskedEditText)mapeoCamposDinamicos.get(dias_prefijo[y] + dias_sufijo[z])).setText(horariosSolicitud.get(0).getPorNombre(dias_prefijo[y] + dias_sufijo[z]));
                                }
                            }
                        }
                        break;
                    case "W_CTE-ADJUNTOS":
                        break;
                }
            }
        }
        //Estrucutura de telefonos y faxes
        if (telefonos.size() > 0) {
            for (int i = 0; i < telefonos.size(); i++) {
                //Telefono principal
                if (telefonos.get(i).getAsJsonObject().get("W_CTE-HOME_FLAG").getAsString().equals("1")) {
                    MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-TEL_EXTENS"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_EXTENS").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TEL_NUMBER"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TEL_EXTENS"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_EXTENS").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TEL_NUMBER"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TEL_EXTENS"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_EXTENS").getAsString());
                }
                //Telefono Celular
                if (telefonos.get(i).getAsJsonObject().get("W_CTE-HOME_FLAG").getAsString().equals("3")) {
                    MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER2"));
                    if(tv == null){
                        tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-TELNUMBER2"));
                    }
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TEL_NUMBER2"));
                    if(tv == null){
                        tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TELNUMBER2"));
                    }
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TEL_NUMBER2"));
                    if(tv == null){
                        tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TELNUMBER2"));
                    }
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());
                    if(VariablesGlobales.getSociedad().equals("1661") || VariablesGlobales.getSociedad().equals("Z001")){
                        tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-TELF2"));
                        if (tv != null)
                            tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                        tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TELF2"));
                        if (tv != null)
                            tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                        tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TELF2"));
                        if (tv != null)
                            tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());
                    }
                }
                //Telefono adicional
                if (telefonos.get(i).getAsJsonObject().get("W_CTE-HOME_FLAG").getAsString().equals(" ") || telefonos.get(i).getAsJsonObject().get("W_CTE-HOME_FLAG").getAsString().equals("")) {
                    MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-TEL_NUMBER3"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TEL_NUMBER3"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TEL_NUMBER3"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                }
            }
        }
        if (faxes.size() > 0) {
            MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-FAX_NUMBER"));
            if(tv != null)
                tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_NUMBER").getAsString());
            tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-FAX_EXTENS"));
            if(tv != null)
                tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_EXTENS").getAsString());
            tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-FAX_NUMBER"));
            if(tv != null)
                tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_NUMBER").getAsString());
            tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-FAX_EXTENS"));
            if(tv != null)
                tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_EXTENS").getAsString());

        }
    }

    private static void llenarDiasDeVisita(Context context) {
        if(idSolicitud == null){
            visitasSolicitud.clear();
            visitasSolicitud_old.clear();
        }
        Gson gson = new Gson();
        Visitas visita = null;
        int indiceReparto = -1;
        int indicePreventa = -1;
        int indiceEspecializada = -1;
        int indiceTeleventa = -1;
        int indiceAutoventa = -1;
        int indiceMixta = -1;
        int indiceDummy = -1;
        Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
        String modalidad = "";
        String tipoVisita = PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_TIPORUTA", "ZPV").toString();//"ZPV";
        if(comboModalidad != null)
        modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();

        if(visitas != null && idSolicitud == null) {

            for (int x = 0; x < visitas.size(); x++) {
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZDD")) {
                    indiceReparto = x;
                }
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZPV")) {
                    indicePreventa = x;
                }
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZJV")) {
                    indiceEspecializada = x;
                }
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZTV")) {
                    indiceTeleventa = x;
                }
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZRM")) {
                    indiceMixta = x;
                }
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZAT")) {
                    indiceAutoventa = x;
                }
                if (visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZDY")) {
                    indiceDummy = x;
                }
                visita = gson.fromJson(visitas.get(x), Visitas.class);

                visita.setF_ico(visita.getF_ico().replace("-", ""));
                visita.setF_fco(visita.getF_fco().replace("-", ""));
                visita.setF_ini(visita.getF_ini().replace("-", ""));
                visita.setF_fin(visita.getF_fin().replace("-", ""));
                if (visita.getF_ico().trim().replace("0", "").length() == 0)
                    visita.setF_ico("");
                if (visita.getF_fco().trim().replace("0", "").length() == 0)
                    visita.setF_fco("");
                if (visita.getF_ini().trim().replace("0", "").length() == 0)
                    visita.setF_ini("");
                if (visita.getF_fin().trim().replace("0", "").length() == 0)
                    visita.setF_fin("");

                visitasSolicitud.add(visita);
                try {
                    visitasSolicitud_old.add((Visitas) visita.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (visitasSolicitud != null) {
            tb_visitas.setDataAdapter(new VisitasTableAdapter(context, visitasSolicitud));
            tb_visitas.getLayoutParams().height = 50;
            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + (alturaFilaTableView * visitasSolicitud.size());
        }

        //Campos de las secuencias de la visita de preventa
        if (visitasSolicitud.size() > 0 && indicePreventa >= 0) {
            //Al menos 1 dia de visita
            TextInputEditText diaL = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_L"));
            TextInputEditText diaK = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_K"));
            TextInputEditText diaM = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_M"));
            TextInputEditText diaJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_J"));
            TextInputEditText diaV = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_V"));
            TextInputEditText diaS = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_S"));
            TextInputEditText diaD = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_D"));
            if (diaL != null) {
                diaL.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getLun_a()));
            }
            if (diaK != null) {
                diaK.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getMar_a()));
            }
            if (diaM != null) {
                diaM.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getMier_a()));
            }
            if (diaJ != null) {
                diaJ.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getJue_a()));
            }
            if (diaV != null) {
                diaV.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getVie_a()));
            }
            if (diaS != null) {
                diaS.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getSab_a()));
            }
            if (diaD != null) {
                String domingo = VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indicePreventa).getDom_a());
                diaD.setText(domingo);
                if(domingo.length() > 0){
                    diaD.setVisibility(View.VISIBLE);
                    ((TextInputLayout)diaD.getParent().getParent()).setVisibility(View.VISIBLE);
                }
            }
        }

        if (visitasSolicitud.size() > 0 && indiceEspecializada >= 0) {
            //En caso de que tenga especializada
            TextInputEditText diaEL = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_L"));
            TextInputEditText diaEK = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_K"));
            TextInputEditText diaEM = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_M"));
            TextInputEditText diaEJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_J"));
            TextInputEditText diaEV = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_V"));
            TextInputEditText diaES = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_S"));
            TextInputEditText diaED = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_D"));
            if (diaEL != null) {
                diaEL.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getLun_a()));
            }
            if (diaEK != null) {
                diaEK.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getMar_a()));
            }
            if (diaEM != null) {
                diaEM.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getMier_a()));
            }
            if (diaEJ != null) {
                diaEJ.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getJue_a()));
            }
            if (diaEV != null) {
                diaEV.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getVie_a()));
            }
            if (diaES != null) {
                diaES.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getSab_a()));
            }
            if (diaED != null) {
                String domingo = VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceEspecializada).getDom_a());
                diaED.setText(domingo);
                if(domingo.length() > 0){
                    diaED.setVisibility(View.VISIBLE);
                    ((TextInputLayout)diaED.getParent().getParent()).setVisibility(View.VISIBLE);
                }
            }
        }
        if (visitasSolicitud.size() > 0 && indiceTeleventa >= 0) {
            //En caso de que tenga especializada
            TextInputEditText diaTL = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_L"));
            TextInputEditText diaTK = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_K"));
            TextInputEditText diaTM = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_M"));
            TextInputEditText diaTJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_J"));
            TextInputEditText diaTV = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_V"));
            TextInputEditText diaTS = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_S"));
            TextInputEditText diaTD = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_D"));
            if (diaTL != null) {
                diaTL.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getLun_a()));
            }
            if (diaTK != null) {
                diaTK.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getMar_a()));
            }
            if (diaTM != null) {
                diaTM.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getMier_a()));
            }
            if (diaTJ != null) {
                diaTJ.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getJue_a()));
            }
            if (diaTV != null) {
                diaTV.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getVie_a()));
            }
            if (diaTS != null) {
                diaTS.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getSab_a()));
            }
            if (diaTD != null) {
                String domingo = VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceTeleventa).getDom_a());
                diaTD.setText(domingo);
                if(domingo.length() > 0){
                    diaTD.setVisibility(View.VISIBLE);
                    ((TextInputLayout)diaTD.getParent().getParent()).setVisibility(View.VISIBLE);
                }
            }
        }
        if (visitasSolicitud.size() > 0 && indiceAutoventa >= 0) {
            //En caso de que tenga especializada
            TextInputEditText diaAL = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_L"));
            TextInputEditText diaAK = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_K"));
            TextInputEditText diaAM = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_M"));
            TextInputEditText diaAJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_J"));
            TextInputEditText diaAV = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_V"));
            TextInputEditText diaAS = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_S"));
            TextInputEditText diaAD = ((TextInputEditText) mapeoCamposDinamicos.get("ZAT_D"));
            if (diaAL != null) {
                diaAL.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getLun_a()));
            }
            if (diaAK != null) {
                diaAK.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getMar_a()));
            }
            if (diaAM != null) {
                diaAM.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getMier_a()));
            }
            if (diaAJ != null) {
                diaAJ.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getJue_a()));
            }
            if (diaAV != null) {
                diaAV.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getVie_a()));
            }
            if (diaAS != null) {
                diaAS.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getSab_a()));
            }
            if (diaAD != null) {
                String domingo = VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceAutoventa).getDom_a());
                diaAD.setText(domingo);
                if(domingo.length() > 0){
                    diaAD.setVisibility(View.VISIBLE);
                    ((TextInputLayout)diaAD.getParent().getParent()).setVisibility(View.VISIBLE);
                }
            }
        }
        if (visitasSolicitud.size() > 0 && indiceMixta >= 0) {
            //En caso de que tenga especializada
            TextInputEditText diaTL = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_L"));
            TextInputEditText diaTK = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_K"));
            TextInputEditText diaTM = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_M"));
            TextInputEditText diaTJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_J"));
            TextInputEditText diaTV = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_V"));
            TextInputEditText diaTS = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_S"));
            TextInputEditText diaTD = ((TextInputEditText) mapeoCamposDinamicos.get("ZRM_D"));
            if (diaTL != null) {
                diaTL.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getLun_a()));
            }
            if (diaTK != null) {
                diaTK.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getMar_a()));
            }
            if (diaTM != null) {
                diaTM.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getMier_a()));
            }
            if (diaTJ != null) {
                diaTJ.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getJue_a()));
            }
            if (diaTV != null) {
                diaTV.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getVie_a()));
            }
            if (diaTS != null) {
                diaTS.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getSab_a()));
            }
            if (diaTD != null) {
                String domingo = VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceMixta).getDom_a());
                diaTD.setText(domingo);
                if(domingo.length() > 0){
                    diaTD.setVisibility(View.VISIBLE);
                    ((TextInputLayout)diaTD.getParent().getParent()).setVisibility(View.VISIBLE);
                }
            }
        }
        if (visitasSolicitud.size() > 0 && indiceDummy >= 0) {
            //En caso de que tenga especializada
            TextInputEditText diaDL = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_L"));
            TextInputEditText diaDK = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_K"));
            TextInputEditText diaDM = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_M"));
            TextInputEditText diaDJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_J"));
            TextInputEditText diaDV = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_V"));
            TextInputEditText diaDS = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_S"));
            TextInputEditText diaDD = ((TextInputEditText) mapeoCamposDinamicos.get("ZDY_D"));
            if (diaDL != null) {
                diaDL.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getLun_a()));
            }
            if (diaDK != null) {
                diaDK.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getMar_a()));
            }
            if (diaDM != null) {
                diaDM.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getMier_a()));
            }
            if (diaDJ != null) {
                diaDJ.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getJue_a()));
            }
            if (diaDV != null) {
                diaDV.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getVie_a()));
            }
            if (diaDS != null) {
                diaDS.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getSab_a()));
            }
            if (diaDD != null) {
                diaDD.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud_old.get(indiceDummy).getDom_a()));
            }
        }
    }

    private static void Provincias(AdapterView<?> parent){
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();

        ArrayList<HashMap<String, String>> provincias = mDBHelper.Provincias(opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < provincias.size(); j++){
            listaopciones.add(new OpcionSpinner(provincias.get(j).get("id"), provincias.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-REGION").trim().equals(provincias.get(j).get("id"))){
                selectedIndex = j;
            }
        }

        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-REGION");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        if(solicitudSeleccionada.size() > 0)
            combo.setSelection(selectedIndex);
        if(combo.getCount() > 1 && cliente != null)
            combo.setSelection(VariablesGlobales.getIndex(combo,cliente.get(0).getAsJsonObject().get("W_CTE-REGION").getAsString().trim()));
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
        DireccionCorta(parent.getContext());
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }

    }
    private static void Cantones(AdapterView<?> parent){
        Spinner pais = (Spinner)mapeoCamposDinamicos.get("W_CTE-LAND1");
        final OpcionSpinner opcionpais = (OpcionSpinner) pais.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> cantones = mDBHelper.Cantones(opcionpais.getId(),opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < cantones.size(); j++){
            listaopciones.add(new OpcionSpinner(cantones.get(j).get("id"), cantones.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-CITY1").trim().equals(cantones.get(j).get("id"))){
                selectedIndex = j;
            }
        }
        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-CITY1");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        if(combo != null) {
            combo.setBackground(d);
            combo.setAdapter(dataAdapter);
            if (solicitudSeleccionada.size() > 0)
                combo.setSelection(selectedIndex);
            if (combo.getCount() > 1 && cliente != null)
                combo.setSelection(VariablesGlobales.getIndex(combo, cliente.get(0).getAsJsonObject().get("W_CTE-CITY1").getAsString().trim()));
            if (selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
                ((TextView) combo.getSelectedView()).setError("El campo es obligatorio!");
            DireccionCorta(parent.getContext());
            if (!modificable) {
                combo.setEnabled(false);
                combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
        }
    }

    private static void Distritos(AdapterView<?> parent){
        Spinner provincia = (Spinner)mapeoCamposDinamicos.get("W_CTE-REGION");
        final OpcionSpinner opcionprovincia = (OpcionSpinner) provincia.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> distritos = mDBHelper.Distritos(opcionprovincia.getId(),opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < distritos.size(); j++){
            listaopciones.add(new OpcionSpinner(distritos.get(j).get("id"), distritos.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-STR_SUPPL3").trim().equals(distritos.get(j).get("id"))){
                selectedIndex = j;
            }
        }
        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-STR_SUPPL3");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        if(solicitudSeleccionada.size() > 0)
            combo.setSelection(selectedIndex);
        if(combo.getCount() > 1 && cliente != null)
            combo.setSelection(VariablesGlobales.getIndex(combo,cliente.get(0).getAsJsonObject().get("W_CTE-STR_SUPPL3").getAsString().trim()));
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getSelectedView()).setError("El campo es obligatorio!");
        DireccionCorta(parent.getContext());
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }
    }

    private static void ProvinciasOld(AdapterView<?> parent){
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();

        ArrayList<HashMap<String, String>> provincias = mDBHelper.Provincias(opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < provincias.size(); j++){
            listaopciones.add(new OpcionSpinner(provincias.get(j).get("id"), provincias.get(j).get("descripcion")));
            if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get("W_CTE-REGION").trim().equals(provincias.get(j).get("id"))){
                selectedIndex = j;
            }
        }

        Spinner combo = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-REGION");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        if(solicitudSeleccionadaOld.size() > 0)
            combo.setSelection(selectedIndex);
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
        DireccionCorta(parent.getContext());
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }

    }
    private static void CantonesOld(AdapterView<?> parent){
        Spinner pais = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-LAND1");
        final OpcionSpinner opcionpais = (OpcionSpinner) pais.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> cantones = mDBHelper.Cantones(opcionpais.getId(),opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < cantones.size(); j++){
            listaopciones.add(new OpcionSpinner(cantones.get(j).get("id"), cantones.get(j).get("descripcion")));
            if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get("W_CTE-CITY1").trim().equals(cantones.get(j).get("id"))){
                selectedIndex = j;
            }
        }
        Spinner combo = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-CITY1");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        if(combo != null) {
            combo.setBackground(d);
            combo.setAdapter(dataAdapter);
            if (solicitudSeleccionada.size() > 0)
                combo.setSelection(selectedIndex);
            if (combo.getCount() > 1 && cliente != null)
                combo.setSelection(VariablesGlobales.getIndex(combo, cliente.get(0).getAsJsonObject().get("W_CTE-CITY1").getAsString().trim()));
            if (selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
                ((TextView) combo.getSelectedView()).setError("El campo es obligatorio!");
            DireccionCorta(parent.getContext());
            if (!modificable) {
                combo.setEnabled(false);
                combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
        }
    }

    private static void DistritosOld(AdapterView<?> parent){
        Spinner provincia = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-REGION");
        final OpcionSpinner opcionprovincia = (OpcionSpinner) provincia.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> distritos = mDBHelper.Distritos(opcionprovincia.getId(),opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < distritos.size(); j++){
            listaopciones.add(new OpcionSpinner(distritos.get(j).get("id"), distritos.get(j).get("descripcion")));
            if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get("W_CTE-STR_SUPPL3").trim().equals(distritos.get(j).get("id"))){
                selectedIndex = j;
            }
        }
        Spinner combo = (Spinner)mapeoCamposDinamicosOld.get("W_CTE-STR_SUPPL3");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        if(solicitudSeleccionadaOld.size() > 0)
            combo.setSelection(selectedIndex);
        if(combo.getCount() > 1 && cliente != null)
            combo.setSelection(VariablesGlobales.getIndex(combo,cliente.get(0).getAsJsonObject().get("W_CTE-STR_SUPPL3").getAsString().trim()));
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getSelectedView()).setError("El campo es obligatorio!");
        DireccionCorta(parent.getContext());
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }
    }

    private static void  DireccionCorta(Context context) {
        String sociedad = PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BUKRS","");
        switch(sociedad){
            case "F443":
            case "F445":
            case "F451":
                MaskedEditText home = (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-HOME_CITY");

                MaskedEditText dir = (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-STREET");
                MaskedEditText dirF = (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-LOCATION");
                Spinner prov = (Spinner)mapeoCamposDinamicos.get("W_CTE-REGION");
                Spinner cant = (Spinner)mapeoCamposDinamicos.get("W_CTE-CITY1");
                Spinner dist = (Spinner)mapeoCamposDinamicos.get("W_CTE-STR_SUPPL3");
                OpcionSpinner p = (OpcionSpinner)prov.getSelectedItem();
                OpcionSpinner c = (OpcionSpinner)cant.getSelectedItem();
                OpcionSpinner d = (OpcionSpinner)dist.getSelectedItem();
                if(d != null && !d.getId().isEmpty())
                    home.setText(d.getName().trim().split("-")[1]);

                StringBuilder dircorta = new StringBuilder();
                if (dir != null) {
                    if (prov != null && p != null && !p.getId().equals("")) {
                        if(!p.getId().isEmpty())
                            dircorta.append(p.getName().trim().split("- ")[1]);
                    }
                    if (cant != null && c != null && !c.getId().equals("")) {
                        if(!c.getId().isEmpty())
                            dircorta.append(c.getName().trim().split("-")[1]);
                    }
                    if (dist != null && d != null &&  !d.getId().equals("")) {
                        if(!d.getId().isEmpty())
                            dircorta.append(d.getName().trim().split("-")[1]);
                    }
                    dir.setText(dircorta.toString().toUpperCase(Locale.getDefault()));
                    dirF.setText(dircorta.toString().toUpperCase(Locale.getDefault()));
                }
                break;
            case "F446":
            case "1657":
            case "1658":
                break;
        }
    }

    private static void Canales(AdapterView<?> parent){
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> canales = mDBHelper.Canales(opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < canales.size(); j++){
            listaopciones.add(new OpcionSpinner(canales.get(j).get("id"), canales.get(j).get("descripcion")));
        }
        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZGPOCANAL");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
    }
    private static void CanalesKof(AdapterView<?> parent){
        Spinner grupo_canal = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZTPOCANAL");
        final OpcionSpinner opciongrupocanal = (OpcionSpinner) grupo_canal.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> distritos = mDBHelper.CanalesKOF(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_VKORG",""),opciongrupocanal.getId(),opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < distritos.size(); j++){
            listaopciones.add(new OpcionSpinner(distritos.get(j).get("id"), distritos.get(j).get("descripcion")));
        }
        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZCANAL");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
    }
    private static void  ImpuestoSegunUnidadNegocio(AdapterView<?> parent) {
        if (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F443")) {
            int indice=-1;
            for (int x = 0; x < tb_impuestos.getDataAdapter().getCount(); x++) {
                if (tb_impuestos.getDataAdapter().getData().get(x).getTatyp().equals("MWCR")) {
                    indice = x;
                    break;
                }
            }
            final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
            if(indice != -1) {
                if (opcion.getId().equals("MA")) {
                    tb_impuestos.getDataAdapter().getData().get(indice).setTaxkd("2");
                } else {
                    tb_impuestos.getDataAdapter().getData().get(indice).setTaxkd("1");
                }
                tb_impuestos.getDataAdapter().notifyDataSetChanged();
            }
        }
    }
    private static void  AsignarTipoImpuesto(AdapterView<?> parent) {
        String pais = PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","");
        if (pais.equals("1661") || pais.equals("Z001")) {
            final OpcionSpinner tipo_nif = (OpcionSpinner) parent.getSelectedItem();
            Spinner fityp;
            switch (tipo_nif.getId()) {
                case "25":
                case "42":
                    fityp = (Spinner)mapeoCamposDinamicos.get("W_CTE-FITYP");
                    fityp.setSelection(VariablesGlobales.getIndex(fityp,"05"));
                    break;
                case "88":
                    fityp = (Spinner)mapeoCamposDinamicos.get("W_CTE-FITYP");
                    CheckBox zona_franca = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ZONA_FRANCA");
                    if (zona_franca != null && zona_franca.isChecked()) {
                        fityp.setSelection(VariablesGlobales.getIndex(fityp,"04"));
                    } else {
                        fityp.setSelection(VariablesGlobales.getIndex(fityp,"01"));
                    }
                    break;
                default:
                    fityp = (Spinner)mapeoCamposDinamicos.get("W_CTE-FITYP");
                    if(fityp != null)
                    fityp.setSelection(VariablesGlobales.getIndex(fityp,"05"));
            }
        }
    }

    private static void ReplicarValor(View v, String campo){
        TextView desde = (TextView)v;
        TextView hasta = (TextView)mapeoCamposDinamicos.get(campo);
        hasta.setText(desde.getText());
    }
    private static void ReplicarValorSpinner(View v, String campo,int selection){
        Spinner desde = (Spinner)v;
        Spinner hasta = (Spinner)mapeoCamposDinamicos.get(campo);
        hasta.setSelection(selection);
    }
    private static void ReplicarValorSpinner(View v, String campo,String valorSeleccioando){
        Spinner desde = (Spinner)v;
        Spinner hasta = (Spinner)mapeoCamposDinamicos.get(campo);
        if(hasta != null)
            hasta.setSelection(VariablesGlobales.getIndex(hasta,valorSeleccioando));
    }
    private static boolean ValidarCedula(View v, String tipoCedula){
        TextView texto = (TextView)v;
        String cedula = "";
        Pattern pattern;
        Matcher matcher;
        switch(PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS","")) {
            case "F443"://Costa Rica
                switch (tipoCedula) {
                    case "C1":
                        if (texto.getText().toString().trim().length() == 12) {
                            texto.setText(texto.getText() + "-00");
                        }
                        cedula = "[0][1-9]-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-00";
                        break;
                    case "C2":
                        cedula = "((3-[0-9]{3,3}-[0-9]{6,6})|(4-000-[0-9]{6,6}))";
                        break;
                    case "C3":
                        cedula = "([1-9][0-9])-[0-9]{4,4}-[0-9]{4,4}-[0-9]{2,2}";
                        break;
                }
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto.getText());
                if (!matcher.matches()) {
                    texto.setError("Formato Regimen "+tipoCedula+" invalido!");
                    cedulaValidada = false;
                    return true;
                }
                cedulaValidada = true;
                MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
                if(idfiscal != null) {
                    String cedulaDigitada = texto.getText().toString().trim();
                    if (texto.getText().toString().trim().endsWith("-00") && tipoCedula.equals("C1"))
                        idfiscal.setText(cedulaDigitada.substring(0, cedulaDigitada.length() - 3).replaceFirst("^0+(?!$)", "").replace("-", ""));
                    else
                        idfiscal.setText(cedulaDigitada.replaceFirst("^0+(?!$)", "").replace("-", ""));
                    idfiscal.setError(null);
                    idfiscal.clearFocus();
                }
                break;
            case "F445"://Nicaragua
                if (texto.getText().toString().trim().length() < 3) {
                    cedulaValidada = false;
                    texto.setError("Formato Regimen "+tipoCedula+" invalido!");
                    return true;
                }
                if (texto.getText().toString().trim().length() < 14) {
                    String padded = "00000000000000".substring(texto.getText().toString().trim().length()) + texto.getText().toString().trim();
                    texto.setText(padded);
                }
                cedula = "[0-9A-Z-]{14,14}";
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto.getText());
                if (!matcher.matches()) {
                    cedulaValidada = false;
                    texto.setError("Formato Regimen "+tipoCedula+" invalido!");
                    return true;
                }
                cedulaValidada = true;
                break;

            case "F451"://Panama
                switch (tipoCedula) {
                    case "P1":
                        if(texto.getText().toString().trim().startsWith("NA-")){
                            cedula = "NA-([0][1-9]|[1][0-2])-[0-9]{4,4}-[0-9]{5,5}";
                        }
                        if(texto.getText().toString().trim().startsWith("PE-")){
                            cedula = "PE-[0-9]{4,4}-[0-9]{5,5}";
                        }
                        if(texto.getText().toString().trim().startsWith("N-")){
                            cedula = "N-[0-9]{4,4}-[0-9]{6,6}";
                        }
                        break;
                    case "P2":
                        cedula = "[1-9a-zA-Z][0-9a-zA-Z\\-]{3,15}";
                        break;
                    case "P3":
                        cedula = "E-[0-9]{4,4}-[0-9]{6,6}";
                        break;
                }
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto.getText());
                if (!matcher.matches()) {
                    cedulaValidada = false;
                    Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                    texto.setError("Formato Regimen "+tipoCedula+" invalido!");
                    return true;
                }
                cedulaValidada = true;
                break;
            case "F446"://GT Embocem
            case "1657"://Volcanes
            case "1658"://Abasa
                /*Validaciones Adicionales para GT*/
                String regexp_idfiscal = "[1-9][0-9]{1,8}-[0-9A-Z]";
                String regexp_cf = "CF";
                Pattern patternFi = Pattern.compile(regexp_idfiscal);
                Pattern patternCF = Pattern.compile(regexp_cf);
                Matcher matcherFi = patternFi.matcher(texto.getText());
                Matcher matcherCF = patternCF.matcher(texto.getText());

                if (!matcherFi.matches() && !matcherCF.matches()) {
                    cedulaValidada = false;
                    texto.setError("NIT valor/formato inválido!");
                    return true;
                }
                /*Despues del formato se realiza validacion MOD 11*/
                if(texto.getText().toString().replace("-","").replace("0","").length() == 0){
                    cedulaValidada = false;
                    texto.setError("NIT no puede tener solo ceros!");
                    return true;
                }
                String[] nit = texto.getText().toString().split("-");
                Integer cantDigitos = nit[0].length();

                StringBuilder digitos = new StringBuilder();
                digitos.append(nit[0]);
                digitos = digitos.reverse();
                int temp = 0;
                for (int x = 2; x <= (cantDigitos + 1); x++) {
                    temp += x * Character.getNumericValue(digitos.charAt((x - 2)));
                }
                int resultado = temp % 11;
                int tempVerificador = 11 - resultado;
                if(tempVerificador == 11)
                    tempVerificador = resultado;
                String digitoVerificador = String.valueOf(tempVerificador);
                if (digitoVerificador.equals("10")) {
                    digitoVerificador = "K";
                }
                if (nit.length > 1 && !digitoVerificador.equals(nit[1].trim())) {
                    cedulaValidada = false;
                    texto.setError("NIT inválido por digito verificador!");
                    return true;
                }
                cedulaValidada = true;
                break;
            case "1661":
            case "Z001":
                String[] nit_uy = new String[2];
                Integer cantDigitos_uy;
                String ci;
                int digVerificador = -1;
                int[] factores = null;
                int suma=0;
                int factor = 10;
                int resto = 0;
                int checkdigit = 0;
                switch (tipoCedula) {
                    case "25":
                        nit_uy = texto.getText().toString().split("-");
                        if (texto.getText().toString().replace("-", "").replace("0", "").length() == 0) {
                            cedulaValidada = false;
                            texto.setError("CI no puede tener solo ceros!");
                            return true;
                        }
                        if (!TextUtils.isDigitsOnly(texto.getText())) {
                            cedulaValidada = false;
                            texto.setError("CI no puede tener ningún caracter, solo puede contener números");
                            return true;
                        }
                        cantDigitos_uy = nit_uy[0].length();
                        ci = nit_uy[0].trim();

                        digVerificador = -1;
                        if (nit_uy.length == 1) {
                            cantDigitos_uy--;
                            digVerificador = Integer.parseInt(nit_uy[0].trim().substring(cantDigitos_uy) + "");
                            ci = nit_uy[0].substring(0, cantDigitos_uy);
                        } else {
                            digVerificador = Integer.parseInt(nit_uy[1].trim() + "");
                        }

                        if (ci.length() != 6 && ci.length() != 7 && ci.length() != 11) {
                            cedulaValidada = false;
                        } else {
                            try {
                                Integer.parseInt(ci);
                            } catch (NumberFormatException e) {
                                cedulaValidada = false;
                            }
                        }
                        factores = null;
                        if (ci.length() == 6) { // CI viejas
                            factores = new int[]{9, 8, 7, 6, 3, 4};
                        } else if (ci.length() == 7) {
                            factores = new int[]{2, 9, 8, 7, 6, 3, 4};
                        } else if (ci.length() == 11) {
                            factores = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4};
                        }

                        if (factores == null) {
                            cedulaValidada = false;
                            Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }

                        suma = 0;
                        for (int i = 0; i < (cantDigitos_uy); i++) {
                            int digito = Integer.parseInt(ci.charAt(i) + "");
                            suma += digito * factores[i];
                        }

                        factor = 10;
                        if (ci.length() == 11) {
                            factor = 11;
                        }
                        resto = suma % factor;
                        checkdigit = factor - resto;

                        if (checkdigit == factor) {
                            cedulaValidada = (digVerificador == 0);
                        }else if (checkdigit == 10) {
                            cedulaValidada = true;//String.valueOf(digVerificador) == "K";
                        }else {
                            cedulaValidada = (checkdigit == digVerificador);
                        }
                        if (!cedulaValidada) {
                            Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }
                        break;
                    case "88":
                        if(texto.getText().toString().length() == 0){
                            cedulaValidada = false;
                            texto.setError("RUT no puede estar vacio!");
                            return true;
                        }
                        nit_uy[0] = texto.getText().toString().substring(0,texto.getText().toString().length()-1);
                        if (texto.getText().toString().replace("-", "").replace("0", "").length() == 0) {
                            cedulaValidada = false;
                            texto.setError("RUT no puede tener solo ceros!");
                            return true;
                        }
                        if (!TextUtils.isDigitsOnly(texto.getText())) {
                            cedulaValidada = false;
                            texto.setError("RUT no puede tener ningún caracter, solo puede contener números");
                            return true;
                        }
                        cantDigitos_uy = nit_uy[0].length();//Debe ser 11
                        ci = nit_uy[0].trim();

                        try {
                            digVerificador = Integer.parseInt(texto.getText().toString().substring(texto.getText().toString().length() - 1, texto.getText().toString().length()) + "");
                        }catch(Exception e){

                        }
                        if (ci.length() != 11) {
                            cedulaValidada = false;
                        } else {
                            try {
                                Integer.parseInt(ci);
                            } catch (NumberFormatException e) {
                                cedulaValidada = false;
                            }
                        }

                        if (ci.length() == 11) { // RUT
                            factores = new int[]{4,3,2,9,8,7,6,5,4,3,2};
                        }

                        if (factores == null) {
                            cedulaValidada = false;
                            Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }

                        suma = 0;
                        for (int i = 0; i < (cantDigitos_uy); i++) {
                            int digito = Integer.parseInt(ci.charAt(i) + "");
                            suma += digito * factores[i];
                        }

                        factor = 10;
                        if (ci.length() == 11) {
                            factor = 11;
                        }
                        resto = suma % factor;
                        checkdigit = factor - resto;

                        if (checkdigit == factor) {
                            cedulaValidada = (digVerificador == 0);
                        }else if (checkdigit == 10) {
                            cedulaValidada = true;//String.valueOf(digVerificador) == "K";
                        } else {
                            cedulaValidada = (checkdigit == digVerificador);
                        }
                        if (!cedulaValidada) {
                            Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }
                        break;
                    case "42":
                        cedulaValidada = true;
                        break;
                }
        }

        Toasty.success(texto.getContext(),"Formato Regimen "+tipoCedula+" valido!",Toasty.LENGTH_SHORT).show();
        return true;
    }
    private static boolean ValidarIDFiscal(Context context){
        //SearchableSpinner regimen = (SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-KATR3");
        MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
        if(idfiscal != null) {
            if (idfiscal.getText().toString().trim().length() == 0) {
                idFiscalValidado = false;
                return true;
            }
            if (PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad()).equals("F446") || PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD", "").equals("1657") || PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD", "").equals("1658")) {
                String regexp_dpi = "[0-9]{12,14}";
                String regexp_idfiscal = "[1-9][0-9]{1,8}-[0-9A-Z]";
                String regexp_cf = "CF";
                Pattern pattern = Pattern.compile(regexp_idfiscal);
                Pattern patternCF = Pattern.compile(regexp_cf);
                Matcher matcher = pattern.matcher(idfiscal.getText());
                Matcher matcherCF = patternCF.matcher(idfiscal.getText());

                pattern = Pattern.compile(regexp_dpi);
                matcher = pattern.matcher(idfiscal.getText().toString().trim());
                if (!matcher.matches()) {
                    if (!matcher.matches() && !matcherCF.matches()) {
                        idfiscal.setError("DPI/CUI/NIT formato inválido!");
                        idFiscalValidado = false;
                        return true;
                    }
                    /*Despues del formato se realiza validacion MOD 11*/
                    if (idfiscal.getText().toString().replace("-", "").replace("0", "").length() == 0) {
                        idfiscal.setError("ID Fiscal no puede tener solo ceros!");
                        cedulaValidada = false;
                        return true;
                    }
                    String[] nit = idfiscal.getText().toString().split("-");
                    Integer cantDigitos = nit[0].length();

                    StringBuilder digitos = new StringBuilder();
                    digitos.append(nit[0]);
                    digitos = digitos.reverse();
                    int temp = 0;
                    for (int x = 2; x <= (cantDigitos + 1); x++) {
                        temp += x * Character.getNumericValue(digitos.charAt((x - 2)));
                    }
                    int resultado = temp % 11;
                    int tempVerificador = 11 - resultado;
                    if (tempVerificador == 11)
                        tempVerificador = resultado;
                    String digitoVerificador = String.valueOf(tempVerificador);
                    if (digitoVerificador.equals("10")) {
                        digitoVerificador = "K";
                    }
                    if (nit.length > 1 && !digitoVerificador.equals(nit[1].trim())) {
                        idfiscal.setError("ID fiscal inválido por digito verificador!");
                        idFiscalValidado = false;
                        return true;
                    }
                }
            }
        }
        idFiscalValidado = true;
        return true;
    }
    private static boolean ValidarCedula(String v, String tipoCedula, String bukrs){
        String texto = v;
        String cedula = "";
        Pattern pattern;
        Matcher matcher;
        MaskedEditText et = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-STCD1"));

        switch(bukrs) {
            case "F443"://Costa Rica
                switch (tipoCedula) {
                    case "C1":
                        if (texto.trim().length() == 12) {
                            if(et != null)
                                et.setText(et.getText() + "-00");
                        }
                        cedula = "[0][1-9]-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-00";
                        break;
                    case "C2":
                        cedula = "((3-[0-9]{3,3}-[0-9]{6,6})|(4-000-[0-9]{6,6}))";
                        break;
                    case "C3":
                        cedula = "([1-9][0-9])-[0-9]{4,4}-[0-9]{4,4}-[0-9]{2,2}";
                        break;
                }
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto);
                cedulaValidada = true;
                break;
            case "F445"://Nicaragua
                if (texto.trim().length() < 3) {
                    cedulaValidada = false;
                    return true;
                }
                if (texto.trim().length() < 14) {
                    String padded = "00000000000000".substring(texto.trim().length()) + texto.trim();
                    if(et != null)
                        et.setText(padded);
                    texto = padded;
                }
                cedula = "[0-9A-Z-]{14,14}";
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto);
                if (!matcher.matches()) {
                    cedulaValidada = false;
                    return true;
                }
                cedulaValidada = true;
                break;

            case "F451"://Panama
                switch (tipoCedula) {
                    case "P1":
                        if(texto.trim().startsWith("NA-")){
                            cedula = "NA-([0][1-9]|[1][0-2])-[0-9]{4,4}-[0-9]{5,5}";
                        }
                        if(texto.trim().startsWith("PE-")){
                            cedula = "PE-[0-9]{4,4}-[0-9]{5,5}";
                        }
                        if(texto.trim().startsWith("N-")){
                            cedula = "N-[0-9]{4,4}-[0-9]{6,6}";
                        }
                        break;
                    case "P2":
                        cedula = "[1-9a-zA-Z][0-9a-zA-Z\\-]{3,15}";
                        break;
                    case "P3":
                        cedula = "E-[0-9]{4,4}-[0-9]{6,6}";
                        break;
                }
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto);
                if (!matcher.matches()) {
                    cedulaValidada = false;
                    return true;
                }
                break;
            case "F446"://GT Embocem
            case "1657"://Volcanes
            case "1658"://Abasa
                /*Validaciones Adicionales para GT*/
                String regexp_idfiscal = "[1-9][0-9]{1,8}-[0-9A-Z]";
                String regexp_cf = "CF";
                Pattern patternFi = Pattern.compile(regexp_idfiscal);
                Pattern patternCF = Pattern.compile(regexp_cf);
                Matcher matcherFi = patternFi.matcher(texto);
                Matcher matcherCF = patternCF.matcher(texto);

                if (!matcherFi.matches() && !matcherCF.matches()) {
                    cedulaValidada = false;
                    return true;
                }
                /*Despues del formato se realiza validacion MOD 11*/
                String[] nit = texto.split("-");
                Integer cantDigitos = nit[0].length();

                StringBuilder digitos = new StringBuilder();
                digitos.append(nit[0]);
                digitos = digitos.reverse();
                int temp = 0;
                for (int x = 2; x <= (cantDigitos + 1); x++) {
                    temp += x * Character.getNumericValue(digitos.charAt((x - 2)));
                }
                int resultado = temp % 11;
                int tempVerificador = 11 - resultado;
                if(tempVerificador == 11)
                    tempVerificador = resultado;
                String digitoVerificador = String.valueOf(tempVerificador);
                if (digitoVerificador.equals("10")) {
                    digitoVerificador = "K";
                }
                if (nit.length > 1 && !digitoVerificador.equals(nit[1].trim())) {
                    cedulaValidada = false;
                    return true;
                }
                cedulaValidada = true;
                break;
            case "1661":
            case "Z001":
                String[] nit_uy = new String[2];
                Integer cantDigitos_uy;
                String ci;
                int digVerificador = -1;
                int[] factores = null;
                int suma=0;
                int factor = 10;
                int resto = 0;
                int checkdigit = 0;
                switch (tipoCedula) {
                    case "25":
                        nit_uy = texto.split("-");
                        if (texto.replace("-", "").replace("0", "").length() == 0) {
                            et.setError("CI no puede tener solo ceros!");
                            cedulaValidada = false;
                            return true;
                        }
                        cantDigitos_uy = nit_uy[0].length();
                        ci = nit_uy[0].trim();

                        digVerificador = -1;
                        if (nit_uy.length == 1) {
                            cantDigitos_uy--;
                            digVerificador = Integer.parseInt(nit_uy[0].trim().substring(cantDigitos_uy) + "");
                            ci = nit_uy[0].substring(0, cantDigitos_uy);
                        } else {
                            digVerificador = Integer.parseInt(nit_uy[1].trim() + "");
                        }

                        if (ci.length() != 6 && ci.length() != 7 && ci.length() != 11) {
                            cedulaValidada = false;
                        } else {
                            try {
                                Integer.parseInt(ci);
                            } catch (NumberFormatException e) {
                                cedulaValidada = false;
                            }
                        }

                        if (ci.length() == 6) { // CI viejas
                            factores = new int[]{9, 8, 7, 6, 3, 4};
                        } else if (ci.length() == 7) {
                            factores = new int[]{2, 9, 8, 7, 6, 3, 4};
                        } else if (ci.length() == 11) {
                            factores = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4};
                        }

                        if (factores == null) {
                            cedulaValidada = false;
                            Toasty.warning(et.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }

                        suma = 0;
                        for (int i = 0; i < (cantDigitos_uy); i++) {
                            int digito = Integer.parseInt(ci.charAt(i) + "");
                            suma += digito * factores[i];
                        }

                        factor = 10;
                        if (ci.length() == 11) {
                            factor = 11;
                        }
                        resto = suma % factor;
                        checkdigit = factor - resto;

                        if (checkdigit == factor) {
                            cedulaValidada = (digVerificador == 0);
                        } else {
                            cedulaValidada = (checkdigit == digVerificador);
                        }
                        if (!cedulaValidada) {
                            Toasty.warning(et.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }
                        break;
                    case "88":
                        if(texto.toString().length() == 0){
                            et.setError("RUT no puede estar vacio!");
                            cedulaValidada = false;
                            return true;
                        }
                        nit_uy[0] = texto.toString().substring(0,texto.toString().length()-1);
                        if (texto.toString().replace("-", "").replace("0", "").length() == 0) {
                            et.setError("RUT no puede tener solo ceros!");
                            cedulaValidada = false;
                            return true;
                        }
                        cantDigitos_uy = nit_uy[0].length();//Debe ser 11
                        ci = nit_uy[0].trim();

                        try {
                            digVerificador = Integer.parseInt(texto.toString().substring(texto.toString().length() - 1, texto.toString().length()) + "");
                        }catch(Exception e){

                        }
                        if (ci.length() != 11) {
                            cedulaValidada = false;
                        } else {
                            try {
                                Integer.parseInt(ci);
                            } catch (NumberFormatException e) {
                                cedulaValidada = false;
                            }
                        }

                        if (ci.length() == 11) { // RUT
                            factores = new int[]{4,3,2,9,8,7,6,5,4,3,2};
                        }

                        if (factores == null) {
                            cedulaValidada = false;
                            Toasty.warning(et.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }

                        suma = 0;
                        for (int i = 0; i < (cantDigitos_uy); i++) {
                            int digito = Integer.parseInt(ci.charAt(i) + "");
                            suma += digito * factores[i];
                        }

                        factor = 10;
                        if (ci.length() == 11) {
                            factor = 11;
                        }
                        resto = suma % factor;
                        checkdigit = factor - resto;

                        if (checkdigit == factor) {
                            cedulaValidada = (digVerificador == 0);
                        }else if (checkdigit == 10) {
                            cedulaValidada = true;//String.valueOf(digVerificador) == "K";
                        } else {
                            cedulaValidada = (checkdigit == digVerificador);
                        }
                        if (!cedulaValidada) {
                            Toasty.warning(et.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }
                        break;
                    case "42":
                        cedulaValidada = true;
                        break;
                }
        }
        return true;
    }

    //Devuelve string vacio si estan los horarios correcctos, de lo contrario devuelve string de error.
    private String ValidarHorarios(ArrayList<Horarios> horariosSolicitud) {
        String mensajeHorarios = "";
        try {
            int moab1 = Integer.parseInt(horariosSolicitud.get(0).getMoab1().replaceAll(":", ""));
            int mobi1 = Integer.parseInt(horariosSolicitud.get(0).getMobi1().replaceAll(":", ""));
            int diab1 = Integer.parseInt(horariosSolicitud.get(0).getDiab1().replaceAll(":", ""));
            int dibi1 = Integer.parseInt(horariosSolicitud.get(0).getDibi1().replaceAll(":", ""));
            int miab1 = Integer.parseInt(horariosSolicitud.get(0).getMiab1().replaceAll(":", ""));
            int mibi1 = Integer.parseInt(horariosSolicitud.get(0).getMibi1().replaceAll(":", ""));
            int doab1 = Integer.parseInt(horariosSolicitud.get(0).getDoab1().replaceAll(":", ""));
            int dobi1 = Integer.parseInt(horariosSolicitud.get(0).getDobi1().replaceAll(":", ""));
            int frab1 = Integer.parseInt(horariosSolicitud.get(0).getFrab1().replaceAll(":", ""));
            int frbi1 = Integer.parseInt(horariosSolicitud.get(0).getFrbi1().replaceAll(":", ""));
            int saab1 = Integer.parseInt(horariosSolicitud.get(0).getSaab1().replaceAll(":", ""));
            int sabi1 = Integer.parseInt(horariosSolicitud.get(0).getSabi1().replaceAll(":", ""));
            int soab1 = Integer.parseInt(horariosSolicitud.get(0).getSoab1().replaceAll(":", ""));
            int sobi1 = Integer.parseInt(horariosSolicitud.get(0).getSobi1().replaceAll(":", ""));

            int moab2 = Integer.parseInt(horariosSolicitud.get(0).getMoab2().replaceAll(":", ""));
            int mobi2 = Integer.parseInt(horariosSolicitud.get(0).getMobi2().replaceAll(":", ""));
            int diab2 = Integer.parseInt(horariosSolicitud.get(0).getDiab2().replaceAll(":", ""));
            int dibi2 = Integer.parseInt(horariosSolicitud.get(0).getDibi2().replaceAll(":", ""));
            int miab2 = Integer.parseInt(horariosSolicitud.get(0).getMiab2().replaceAll(":", ""));
            int mibi2 = Integer.parseInt(horariosSolicitud.get(0).getMibi2().replaceAll(":", ""));
            int doab2 = Integer.parseInt(horariosSolicitud.get(0).getDoab2().replaceAll(":", ""));
            int dobi2 = Integer.parseInt(horariosSolicitud.get(0).getDobi2().replaceAll(":", ""));
            int frab2 = Integer.parseInt(horariosSolicitud.get(0).getFrab2().replaceAll(":", ""));
            int frbi2 = Integer.parseInt(horariosSolicitud.get(0).getFrbi2().replaceAll(":", ""));
            int saab2 = Integer.parseInt(horariosSolicitud.get(0).getSaab2().replaceAll(":", ""));
            int sabi2 = Integer.parseInt(horariosSolicitud.get(0).getSabi2().replaceAll(":", ""));
            int soab2 = Integer.parseInt(horariosSolicitud.get(0).getSoab2().replaceAll(":", ""));
            int sobi2 = Integer.parseInt(horariosSolicitud.get(0).getSobi2().replaceAll(":", ""));

            if (moab1 > mobi1 && mobi1 != 0) {
                mensajeHorarios += "En el dia Lunes en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (diab1 > dibi1 && dibi1 != 0) {
                mensajeHorarios += "En el dia Martes en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (miab1 > mibi1 && mibi1 != 0) {
                mensajeHorarios += "En el dia Miercoles en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (doab1 > dobi1 && dobi1 != 0) {
                mensajeHorarios += "En el dia Jueves en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (frab1 > frbi1 && frbi1 != 0) {
                mensajeHorarios += "En el dia Viernes en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (saab1 > sabi1 && sabi1 != 0) {
                mensajeHorarios += "En el dia Sabado en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (soab1 > sobi1 && sobi1 != 0) {
                mensajeHorarios += "En el dia Domingo en la mañana, la hora inicial no puede ser mayor a la hora final!\n";
            }

            if (moab2 > mobi2 && moab2 != 0) {
                mensajeHorarios += "En el dia Lunes en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (diab2 > dibi2 && diab2 != 0) {
                mensajeHorarios += "En el dia Martes en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (miab2 > mibi2 && miab2 != 0) {
                mensajeHorarios += "En el dia Miercoles en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (doab2 > dobi2 && doab2 != 0) {
                mensajeHorarios += "En el dia Jueves en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (frab2 > frbi2 && frab2 != 0) {
                mensajeHorarios += "En el dia Viernes en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (saab2 > sabi2 && saab2 != 0) {
                mensajeHorarios += "En el dia Sabado en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }
            if (soab2 > sobi2 && soab2 != 0) {
                mensajeHorarios += "En el dia Domingo en la tarde, la hora inicial no puede ser mayor a la hora final!\n";
            }

            if (moab1 != 0 && mobi2 != 0 && mobi1 == 0 && moab2 == 0 && moab1 > mobi2) {
                mensajeHorarios += "En el dia Lunes, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }
            if (diab1 != 0 && dibi2 != 0 && dibi1 == 0 && diab2 == 0 && diab1 > dibi2) {
                mensajeHorarios += "En el dia Martes, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }
            if (miab1 != 0 && mibi2 != 0 && mibi1 == 0 && miab2 == 0 && miab1 > mibi2) {
                mensajeHorarios += "En el dia Miercoles, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }
            if (doab1 != 0 && dobi2 != 0 && dobi1 == 0 && doab2 == 0 && doab1 > dobi2) {
                mensajeHorarios += "En el dia Jueves, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }
            if (frab1 != 0 && frbi2 != 0 && frbi1 == 0 && frab2 == 0 && frab1 > frbi2) {
                mensajeHorarios += "En el dia Viernes, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }
            if (saab1 != 0 && sabi2 != 0 && sabi1 == 0 && saab2 == 0 && saab1 > sabi2) {
                mensajeHorarios += "En el dia Sabado, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }
            if (soab1 != 0 && sobi2 != 0 && sobi1 == 0 && soab2 == 0 && soab1 > sobi2) {
                mensajeHorarios += "En el dia Domingo, la hora inicial de la mañana no puede ser mayor a la hora final de la tarde!\n";
            }

            if(moab1 != 0 && mobi2 != 0 && mobi1 == 0 && moab2 == 0 && moab1 > 1200){
                mensajeHorarios += "En el dia Lunes, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (diab1 != 0 && dibi2 != 0 && dibi1 == 0 && diab2 == 0 && diab1 > 1200) {
                mensajeHorarios += "En el dia Martes, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (miab1 != 0 && mibi2 != 0 && mibi1 == 0 && miab2 == 0 && miab1 > 1200) {
                mensajeHorarios += "En el dia Miercoles, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (doab1 != 0 && dobi2 != 0 && dobi1 == 0 && doab2 == 0 && doab1 > 1200) {
                mensajeHorarios += "En el dia Jueves, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (frab1 != 0 && frbi2 != 0 && frbi1 == 0 && frab2 == 0 && frab1 > 1200) {
                mensajeHorarios += "En el dia Viernes, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (saab1 != 0 && sabi2 != 0 && sabi1 == 0 && saab2 == 0 && saab1 > 1200) {
                mensajeHorarios += "En el dia Sabado, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (soab1 != 0 && sobi2 != 0 && sobi1 == 0 && soab2 == 0 && soab1 > 1200) {
                mensajeHorarios += "En el dia Domingo, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }

            if(moab1 != 0 && mobi2 != 0 && mobi1 == 0 && moab2 == 0 && mobi2 < 1200){
                mensajeHorarios += "En el dia Lunes, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (diab1 != 0 && dibi2 != 0 && dibi1 == 0 && diab2 == 0 && dibi2 < 1200) {
                mensajeHorarios += "En el dia Martes, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (miab1 != 0 && mibi2 != 0 && mibi1 == 0 && miab2 == 0 && mibi2 < 1200) {
                mensajeHorarios += "En el dia Miercoles, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (doab1 != 0 && dobi2 != 0 && dobi1 == 0 && doab2 == 0 && dobi2 < 1200) {
                mensajeHorarios += "En el dia Jueves, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (frab1 != 0 && frbi2 != 0 && frbi1 == 0 && frab2 == 0 && frbi2 < 1200) {
                mensajeHorarios += "En el dia Viernes, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (saab1 != 0 && sabi2 != 0 && sabi1 == 0 && saab2 == 0 && sabi2 < 1200) {
                mensajeHorarios += "En el dia Sabado, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (soab1 != 0 && sobi2 != 0 && sobi1 == 0 && soab2 == 0 && sobi2 < 1200) {
                mensajeHorarios += "En el dia Domingo, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }

        } catch (Exception exc) {
            mensajeHorarios += "Revise los horarios digitados, valor inválido.";
        }
        if (mensajeHorarios != "") {
            return mensajeHorarios;
        }
        return "";
    }

    private static boolean ValidarCliente(View v){
        TextView texto = (TextView)v;
        String coordenadaY = "^[-]?(([8-9]|[1][0-2])(\\.\\d{3,12}+)?)";
        Pattern pattern = Pattern.compile(coordenadaY);
        Matcher matcher = pattern.matcher(texto.getText().toString().trim());
        if (!matcher.matches()) {
            texto.setError("Formato Coordenada Y "+texto.getText().toString().trim()+" invalido!");
            return false;
        }
        //Toasty.success(texto.getContext(),"Formato Coordenada Y "+valor+" valido!").show();
        return true;
    }

    /*CORRER EN NUEVO THREAD Para poder mostrar avance o loading image*/
    private class MostrarFormulario extends AsyncTask<String, Integer, Void> {

        private WeakReference<Context> contextRef;
        final ViewPager viewPager;
        final TabLayout misTabs;
        final ViewPagerAdapter adapter;
        public MostrarFormulario(Context context) {
            contextRef = new WeakReference<>(context);
            viewPager = new ViewPager(context);
            misTabs = new TabLayout(new ContextThemeWrapper(context, R.style.MyTabs),null,0);
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), context);
        }
        @SuppressLint("ResourceType")
        @Override
        protected Void doInBackground(String... params) {
            LinearLayout ll = findViewById(R.id.LinearLayoutMain);
            ll.addView(misTabs);
            ll.addView(viewPager);
            publishProgress(0);
            Context context = contextRef.get();
            //Traer primero las pestanas
            publishProgress(2);
            misTabs.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            misTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
            misTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
            misTabs.setTabGravity(GRAVITY_FILL);
            misTabs.setTabTextColors(getResources().getColor(R.color.white,null), getResources().getColor(R.color.black,null));

            //final ViewPager viewPager = new ViewPager(context);
            publishProgress(4);
            viewPager.setId(1);
            //ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            publishProgress(6);
            viewPager.setOffscreenPageLimit(5);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(misTabs));
            publishProgress(8);
            misTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            misTabs.setupWithViewPager(viewPager);
            publishProgress(9);
            allotEachTabWithEqualWidth();
            publishProgress(10);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
        }
        private void allotEachTabWithEqualWidth() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewGroup slidingTabStrip = (ViewGroup) misTabs.getChildAt(0);
                    int tamxpestana = (slidingTabStrip.getWidth())/misTabs.getTabCount();
                    for (int i = 0; i < misTabs.getTabCount(); i++) {
                        View tab = slidingTabStrip.getChildAt(i);
                        tab.setMinimumWidth(tamxpestana);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
                        tab.setLayoutParams(layoutParams);
                    }
                }
            });
        }
    }

    public final static boolean isValidEmail(View v) {
        TextView correo = (TextView)v;
        boolean valido = !TextUtils.isEmpty(correo.getText()) && android.util.Patterns.EMAIL_ADDRESS.matcher(correo.getText()).matches();
        if(!listaCamposObligatorios.contains("W_CTE-SMTP_ADDR") && correo.getText().toString().trim().length() == 0){
            valido = true;
        }
        if(valido)
            Toasty.success(correo.getContext(),"Formato de correo valido!").show();
        else
            Toasty.error(correo.getContext(),"Formato de correo Invalido!").show();
        return valido;
    }
    public final static boolean isValidEmail(String v) {
        String correo = (String)v;
        boolean valido = !TextUtils.isEmpty(correo) && android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches();
        return valido;
    }
    public static int getIndexConfigCampo(String campo) {
        for (int i = 0; i < configExcepciones.size(); i++) {
            HashMap<String, String> map = configExcepciones.get(i);
            if (map.containsValue(campo)) { // Or map.getOrDefault("songTitle", "").equals(songName);
                return i;
            }
        }
        return -1; // Not found.
    }
    public static int getIndexConfigCampo(String campo, String agencia) {
        for (int i = 0; i < configExcepciones.size(); i++) {
            HashMap<String, String> map = configExcepciones.get(i);
            if (map.containsValue(campo) && map.containsValue(agencia)) { // Or map.getOrDefault("songTitle", "").equals(songName);
                return i;
            }
        }
        return -1; // Not found.
    }
    public static int getIndexOFkey(String key, ArrayList<HashMap<String,String>> listMap) {

        int i = 0;
        for (i=0; i<listMap.size(); i++)
        {
            if(listMap.get(i).get("campo").equalsIgnoreCase(key))
            {
                return i;
            }
        }

        return -1;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String campoEscaneo ="";
        Bundle b = null;
        if(data != null)
            b = data.getExtras();
        if(b != null) {
            campoEscaneo = b.getString("campoEscaneo");
            if(b.getInt("requestCode") != 0)
                requestCode = b.getInt("requestCode");
        }
        if(requestCode == VariablesGlobales.ESCANEO_TARJETA){
            if(b != null) {
                campoEscaneo = b.getString("campoEscaneo");
                ((MaskedEditText)mapeoCamposDinamicos.get(campoEscaneo)).setText(b.getString("codigo"));
            }
        }else {
            WeakReference<Activity> weakRefA = new WeakReference<Activity>(SolicitudModificacionActivity.this);
            try {
                ManejadorAdjuntos.ActivityResult(requestCode, resultCode, data, getApplicationContext(), weakRefA.get(), mPhotoUri, mDBHelper, adjuntosSolicitud, modificable, firma, GUID, tb_adjuntos, mapeoCamposDinamicos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
