package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
/*import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;*/
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;

import proyecto.app.clientesabc.animaciones.CubeTransformer;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.BancoTableAdapter;
import proyecto.app.clientesabc.adaptadores.ComentarioTableAdapter;
import proyecto.app.clientesabc.adaptadores.ContactoTableAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.ImpuestoTableAdapter;
import proyecto.app.clientesabc.adaptadores.InterlocutorTableAdapter;
import proyecto.app.clientesabc.adaptadores.SpinnerAdapter;
import proyecto.app.clientesabc.adaptadores.VisitasTableAdapter;
import proyecto.app.clientesabc.clases.DevolverPreSolicitudAPI;
import proyecto.app.clientesabc.clases.DevolverPreSolicitudServidor;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.GenerarCodigoVerificacionAPI;
import proyecto.app.clientesabc.clases.GenerarCodigoVerificacionCorreoAPI;
import proyecto.app.clientesabc.clases.GenerarCodigoVerificacionCorreoServidor;
import proyecto.app.clientesabc.clases.GenerarCodigoVerificacionServidor;
import proyecto.app.clientesabc.clases.Haversine;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.RechazarPreSolicitudAPI;
import proyecto.app.clientesabc.clases.RechazarPreSolicitudServidor;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.Validaciones;
import proyecto.app.clientesabc.clases.ValidarIdConInspektor;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Banco;
import proyecto.app.clientesabc.modelos.Comentario;
import proyecto.app.clientesabc.modelos.Contacto;
import proyecto.app.clientesabc.modelos.Horarios;
import proyecto.app.clientesabc.modelos.Impuesto;
import proyecto.app.clientesabc.modelos.Interlocutor;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.Visitas;

import static android.view.View.INVISIBLE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;
import static com.google.android.material.tabs.TabLayout.GRAVITY_START;
import static com.google.android.material.tabs.TabLayout.INDICATOR_GRAVITY_TOP;

public class SolicitudActivity extends AppCompatActivity {

    static int alturaFilaTableView = 0;
    static String tipoSolicitud ="";
    static String idSolicitud = "";
    static String idPresolicitud = "";
    static String idForm = "";
    static int minAdjuntos = 0;
    @SuppressLint("StaticFieldLeak")
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    static ArrayList<String> listaCamposDinamicos = new ArrayList<>();
    static ArrayList<String> listaCamposObligatorios = new ArrayList<>();
    static ArrayList<String> listaCamposBloque = new ArrayList<>();
    static Map<String, View> mapeoCamposDinamicos = new HashMap<>();
    static Map<String, View> mapeoVisitas = new HashMap<>();
    static  ArrayList<HashMap<String, String>> configExcepciones = new ArrayList<>();
    static  ArrayList<HashMap<String, String>> solicitudSeleccionada = new ArrayList<>();
    private static String GUID;
    private ProgressBar progressBar;
    public static boolean firma;
    static boolean modificable;
    static boolean correoValidado;
    static boolean cedulaValidada;
    static boolean idFiscalValidado;
    static BottomNavigationView bottomNavigation;
    public static TextWatcher tw = null;
    static Uri mPhotoUri;
    static Spinner atCorreo = null;
    static Spinner prefijo_direccion = null;
    static ImageView verificarCorreo = null;
    static ImageView verificarCelular = null;
    static Drawable rightIconLocation = null;

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
    private static de.codecrafters.tableview.TableView<Comentario> tb_comentarios;
    //private static TableView tb_comentarios2;
    private static ArrayList<Contacto> contactosSolicitud;
    private static ArrayList<Impuesto> impuestosSolicitud;
    private static ArrayList<Banco> bancosSolicitud;
    private static ArrayList<Interlocutor> interlocutoresSolicitud;
    private static ArrayList<Visitas> visitasSolicitud;
    private static ArrayList<Adjuntos> adjuntosSolicitud;
    private static ArrayList<Horarios> horariosSolicitud;
    private static ArrayList<Comentario> comentarios;
    private static LinearLayout ll_visitas;
    //private AidcManager manager;
    //private BarcodeReader reader;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;
    static ManejadorAdjuntos manejadorAdjuntos;
    public static String[] visitas_permitidas = {"ZPV","ZJV","ZTV","ZRM","ZAT","ZKV","ZDY","ZGE","ZCM","ZCS","ZDI","ZEJ","ZES","ZIN","ZOP","ZPK","ZSP","ZWB","ZWE","ZWJ","ZWP","ZDM","ZMB","ZDP"};
    static boolean suppressRecreateAdapter = false;
    public static PendingIntent sentPI;
    public static PendingIntent deliveredPI;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_solicitud);
        firma = false;
        modificable = true;
        correoValidado = false;
        cedulaValidada = false;
        idFiscalValidado = false;

        alturaFilaTableView = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, getResources().getDisplayMetrics());

        FrameLayout f = findViewById(R.id.background);
        //f.getBackground().setAlpha(80);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            tipoSolicitud = b.getString("tipoSolicitud");
            idSolicitud = b.getString("idSolicitud");
            idPresolicitud = b.getString("idPresolicitud");
            //accion = b.getString("accion");
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);

        bottomNavigation = findViewById(R.id.bottom_navigation);


        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.botella_coca_header_der,null));

        if(idSolicitud != null){
            atCorreo = null;
            solicitudSeleccionada = mDBHelper.getSolicitud(idSolicitud);
            tipoSolicitud = solicitudSeleccionada.get(0).get("TIPFORM");
            GUID = solicitudSeleccionada.get(0).get("id_solicitud");
            idForm = solicitudSeleccionada.get(0).get("IDFORM");
            if(solicitudSeleccionada.get(0).get("TIPFORM").equals("70")){
                Menu menu = bottomNavigation.getMenu();
                menu.clear();
                menu.add(Menu.NONE, R.id.action_devolver, Menu.NONE, "Devolver").setIcon(R.drawable.ic_rotate_left_24);
                menu.add(Menu.NONE, R.id.action_rechazar, Menu.NONE, "Rechazar").setIcon(R.drawable.icon_reject);
                menu.add(Menu.NONE, R.id.action_aprobar, Menu.NONE, "Aprobar").setIcon(R.drawable.icon_approve);
                bottomNavigation.setSelectedItemId(menu.getItem(menu.size()-1).getItemId());
            }

            setTitle(GUID);
            String descripcion = mDBHelper.getDescripcionSolicitud(tipoSolicitud);
            getSupportActionBar().setSubtitle(descripcion +" - "+ solicitudSeleccionada.get(0).get("ESTADO").trim());
        }else{
            atCorreo = null;
            GUID = mDBHelper.getGuiId();
            idForm = "";
            solicitudSeleccionada.clear();
            mapeoCamposDinamicos.clear();
            //horariosSolicitud.clear();
            mapeoVisitas.clear();
            setTitle("Solicitud Nuevo Cliente");
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
               || solicitudSeleccionada.get(0).get("ESTADO").equals("Rechazado")
               || solicitudSeleccionada.get(0).get("ESTADO").equals("Aprobado")
               || solicitudSeleccionada.get(0).get("ESTADO").equals("Preventa")){
                modificable = false;
            }
        }else{
            if(!tipoSolicitud.equals("1") && !tipoSolicitud.equals("6")){
                firma = true;
            }
        }
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        sentPI = PendingIntent.getBroadcast(SolicitudActivity.this, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);
        deliveredPI = PendingIntent.getBroadcast(SolicitudActivity.this, 0, new Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SolicitudActivity.this.registerReceiver(sentReceiver, new IntentFilter(SENT), Context.RECEIVER_EXPORTED);
            SolicitudActivity.this.registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED), Context.RECEIVER_EXPORTED);
        } else {
            SolicitudActivity.this.registerReceiver(sentReceiver, new IntentFilter(SENT));
            SolicitudActivity.this.registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
        }
        suppressRecreateAdapter = false;
        verificarCorreo = null;
        verificarCelular = null;
        configExcepciones.clear();
        listaCamposDinamicos.clear();
        listaCamposBloque.clear();
        listaCamposObligatorios.clear();
        mapeoVisitas.clear();
        configExcepciones = mDBHelper.getConfigExcepciones(tipoSolicitud);

        //Setear Eventos de Elementos del bottom navigation
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_camara:
                        //Initialize on every usage
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
                        //intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        try {
                            //startActivityForResult(intent, 200);
                            startActivityForResult(Intent.createChooser(intent, "Seleccione un archivo para adjuntar!"),200);
                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", getResources().getString(R.string.no_activity));
                        }
                        return true;
                    case R.id.action_devolver:
                        MaskedEditText comentarios = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS"));
                        if(comentarios != null && comentarios.getText().toString().trim().isEmpty()){
                            displayDialogMessageInputDevolver(SolicitudActivity.this,"Motivo de devolución obligatorio:");
                            return true;
                        }

                        return true;
                    case R.id.action_rechazar:
                        comentarios = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-COMENTARIOS"));
                        if(comentarios != null && comentarios.getText().toString().trim().isEmpty()){
                            displayDialogMessageInput(SolicitudActivity.this,"Motivo de rechazo obligatorio:");
                            return true;
                        }

                        return true;
                    case R.id.action_aprobar:
                        finish();
                        Bundle b = new Bundle();
                        //TODO seleccionar el tipo de solicitud por el UI
                        b.putString("tipoSolicitud", "1"); //id de solicitud
                        b.putString("idPresolicitud", idSolicitud); //id de solicitud
                        intent = new Intent(SolicitudActivity.this,SolicitudActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                        return true;
                    case R.id.action_save:
                        int numErrores = 0;
                        String mensajeError="";
                        //View focusableView = getCurrentFocus();
                        //if(focusableView != null)
                            //focusableView.setFocusable(false);
                        //Validacion de Datos Obligatorios Automatico
                        if(getCurrentFocus() != null)
                            getCurrentFocus().clearFocus();
                        for(int i=0; i < listaCamposObligatorios.size(); i++) {
                            try{
                                MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                String valor = tv.getText().toString().trim();
                                if(listaCamposObligatorios.get(i).contains("W_CTE-SMTP_ADDR") ){
                                    if(tv.isFocused())
                                        tv.clearFocus();
                                }
                                if(valor.isEmpty() && !listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LAT") && !listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LONG")){
                                    tv.setError("El campo "+tv.getTag()+" es obligatorio!");
                                    numErrores++;
                                    mensajeError += "- "+tv.getTag()+"\n";
                                }
                                if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LAT") || listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LONG")){
                                    if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LAT")){
                                        if(tv.getText().toString().replace("0","").replace(".","").isEmpty()){
                                            numErrores++;
                                            mensajeError += "- El campo Coordenada Y no puede ser 0 cuando es obligatorio.\n";
                                        }else
                                        if(!Validaciones.ValidarCoordenadaY(tv)) {
                                            numErrores++;
                                            mensajeError += "- Formato Coordenada Y invalido\n";
                                        }else{
                                            Drawable leftIcon = getResources().getDrawable(R.drawable.icon_location, null);
                                            tv.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, rightIconLocation, null);
                                        }
                                    }
                                    if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LONG")){
                                        if(tv.getText().toString().replace("0","").replace(".","").isEmpty()){
                                            numErrores++;
                                            mensajeError += "- El campo Coordenada X no puede ser 0 cuando es obligatorio.\n";
                                        }else
                                        if(!Validaciones.ValidarCoordenadaX(tv)) {
                                            numErrores++;
                                            mensajeError += "- Formato Coordenada X invalido\n";
                                        }else{
                                            Drawable leftIcon = getResources().getDrawable(R.drawable.icon_location, null);
                                            tv.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, rightIconLocation, null);
                                        }
                                    }
                                }
                            }catch(Exception e){
                                Spinner combo = ((Spinner) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                if(combo.getSelectedItem() != null) {
                                    String valor = ((OpcionSpinner)combo.getAdapter().getItem((int) combo.getSelectedItemId())).getId();

                                    if (combo.getAdapter().getCount() == 0 || (combo.getAdapter().getCount() > 0 && valor.isEmpty() )) {
                                        setErrorWithTooltipOnTouch(combo, getResources().getString(R.string.error_field_required));
                                        numErrores++;
                                        mensajeError += "- "+combo.getTag()+"\n";
                                    }
                                }else{
                                    setErrorWithTooltipOnTouch(combo, getResources().getString(R.string.error_field_required));
                                    numErrores++;
                                    mensajeError += "- "+combo.getTag()+"\n";
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
                                }else{
                                    Drawable leftIcon = getResources().getDrawable(R.drawable.icon_location, null);
                                    texto.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, rightIconLocation, null);
                                }
                            }
                        }
                        if(mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG") != null){
                            MaskedEditText texto = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                            if(!texto.getText().toString().replace("0","").replace(".","").isEmpty()){
                                if(!listaCamposObligatorios.contains("W_CTE-ZZCRMA_LONG") && !Validaciones.ValidarCoordenadaX(texto)){
                                    numErrores++;
                                    mensajeError += "- Formato Coordenada X invalido\n";
                                }else{
                                    Drawable leftIcon = getResources().getDrawable(R.drawable.icon_location, null);
                                    texto.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, rightIconLocation, null);
                                }
                            }
                        }
                        if(mapeoCamposDinamicos.get("W_CTE-PSTLZ") != null && (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661") || PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("CONFIG_SOCIEDAD","").equals("Z001"))){
                            MaskedEditText texto = ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-PSTLZ"));
                            if(texto.getText().toString().length() != 5){
                                if(!listaCamposObligatorios.contains("W_CTE-PSTLZ")){
                                    numErrores++;
                                    mensajeError += "- Codigo Postal debe ser de 5 digitos!\n";
                                }
                            }
                        }
                        if(mapeoCamposDinamicos.get("W_CTE-STREET") != null && prefijo_direccion != null && (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("F428") )) {
                            if (listaCamposObligatorios.contains("W_CTE-STREET")) {
                                if(prefijo_direccion.getSelectedItem() == null || ((OpcionSpinner)prefijo_direccion.getSelectedItem()).getId().equals("") || ((OpcionSpinner)prefijo_direccion.getSelectedItem()).getId().equals("0")) {
                                    numErrores++;
                                    mensajeError += "- Debe seleccionar la nomenclatura dian para la direccion!\n";
                                }
                            }
                        }
                        //Validar si el tipo de pago es por transferencia, debe ingresar al menos 1 cuenta bancaria.
                        Spinner comboTipoPago = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR2"));
                        String tipoPago = "";
                        if(comboTipoPago != null && comboTipoPago.getSelectedItem() != null) {
                            tipoPago = ((OpcionSpinner) comboTipoPago.getAdapter().getItem((int) comboTipoPago.getSelectedItemId())).getId();
                            if(tipoPago.equals("T") && bancosSolicitud.size() == 0) {
                                mensajeError += "- Tipo de Pago por Transferencia. Debe ingresar al menos 1 cuenta bancaria.\n";
                                numErrores++;
                            }
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
                            mensajeError += "- Debe ejecutar la encuesta de Canales!\n";
                        }
                        CheckBox encuesta_gec = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                        if(encuesta_gec != null && !encuesta_gec.isChecked() && encuesta_gec.isShown()){
                            numErrores++;
                            mensajeError += "- Debe ejecutar la encuesta GEC!\n";
                        }
                        CheckBox encuesta_consumo = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_CONSUMO");
                        if(encuesta_consumo != null && !encuesta_consumo.isChecked() && encuesta_consumo.isShown()){
                            numErrores++;
                            mensajeError += "- Debe ejecutar la encuesta OCASION DE CONSUMO!\n";
                        }
                        //Validar el campo de ruta de reparto del grid de visitas
                        //int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPR");
                        Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                        String modalidad = "";
                        String tipoVisita = PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_TIPORUTA","ZPV").toString();//"ZPV";
                        if(comboModalidad != null) {
                            modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();

                            //REVISAR PARA COLOMBIA
                            //SACAR REALMENTE CUAL SERIA EL REPARTO SEGUN LA MODALIDAD SELECCIONADA
                            String tipoReparto = mDBHelper.RutaRepartoAsociada(modalidad, tipoVisita);
                            int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                            if (!modalidad.equals("GV") && indiceReparto == -1 && visitasSolicitud.size() > 0 && !tipoReparto.equals(tipoVisita)) {
                                numErrores++;
                                mensajeError += "- No existe tipo visita ZDD de reparto!\n";
                            }

                            if (!modalidad.equals("GV") && indiceReparto != -1 && visitasSolicitud.size() > 0 && visitasSolicitud.get(indiceReparto).getRuta().trim().length() < 6 && !tipoReparto.equals(tipoVisita)) {
                                numErrores++;
                                mensajeError += "- Falta ruta de reparto(ZDD) en PLANES DE VISITA!\n";
                            }

                            int indiceDummy = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDY");
                            if (indiceDummy != -1 && visitasSolicitud.size() > 0 && visitasSolicitud.get(indiceDummy).getRuta().trim().length() < 6) {
                                numErrores++;
                                mensajeError += "- Falta asignar ruta Dummy(ZDY) en PLANES DE VISITA!\n";
                            }

                            int indiceMixta = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZRM");
                            int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZPV");
                            if (modalidad.equals("PR") && indiceMixta != -1 && indicePreventa != -1 && visitasSolicitud.size() > 0 && visitasSolicitud.get(indicePreventa).getRuta().trim().length() < 6) {
                                numErrores++;
                                mensajeError += "- Falta asignar ruta de Preventa(ZPV) en PLANES DE VISITA!\n";
                            }
                        }else{
                            numErrores++;
                            mensajeError += "- Modalidad de Venta!\n";
                        }
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

                        //Validacion de politica de privacidad firmada por el cliente.
                        if(!firma){
                            numErrores++;
                            mensajeError += "- El cliente debe firmar las políticas de privacidad!\n";
                        }
                        //Validacion de correo
                        if(mapeoCamposDinamicos.get("W_CTE-SMTP_ADDR") != null && !correoValidado && (listaCamposObligatorios.contains("W_CTE-SMTP_ADDR") || ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-SMTP_ADDR")).getText().toString().trim().length() > 0 )){
                            numErrores++;
                            mensajeError += "- Formato de correo Inválido!\n";
                        }
                        //Validacion de cedula de identidad
                        if(!cedulaValidada){
                            numErrores++;
                            mensajeError += "- Formato de cédula Inválida!\n";
                        }
                        //Validacion de cedula de ID Fiscal para GT mas que todo
                        ValidarIDFiscal(getBaseContext());
                        if(!idFiscalValidado){
                            numErrores++;
                            String label = "ID fiscal";
                            if(PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("W_CTE_BUKRS","").equals("F446")
                                    || PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("W_CTE_BUKRS","").equals("1657")
                                    || PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("W_CTE_BUKRS","").equals("1658")){
                                MaskedEditText comboIdFiscal = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3"));
                                if(comboIdFiscal != null){
                                    label = comboIdFiscal.getTag().toString();
                                }
                            }
                            mensajeError += "- "+label+" Inválido!\n";
                        }
                        if(minAdjuntos > adjuntosSolicitud.size()){
                            numErrores++;
                            mensajeError += "- Debe adjuntar al menos "+minAdjuntos+" documentos a la solicitud!\n";
                        }
                        if(horariosSolicitud.size() > 0) {
                            String errorHorarios = ValidarHorarios(horariosSolicitud);
                            if (errorHorarios != "") {
                                numErrores++;
                                mensajeError += errorHorarios;
                            }
                        }

                        //Validacion de siguiente aprobador seleccionado
                        SearchableSpinner combo = ((SearchableSpinner) mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR"));
                        if(combo.getSelectedItem() != null) {
                            String valor = ((OpcionSpinner)combo.getAdapter().getItem((int) combo.getSelectedItemId())).getId();
                            if (combo.getAdapter().getCount() == 0 || (combo.getAdapter().getCount() > 0 && valor.isEmpty() )) {
                                setErrorWithTooltipOnTouch(combo, getResources().getString(R.string.error_field_required));
                                numErrores++;
                                mensajeError += "- Siguiente Aprobador\n";
                            }
                        }else{
                            setErrorWithTooltipOnTouch(combo, getResources().getString(R.string.error_field_required));;
                            numErrores++;
                            mensajeError += "- Siguiente Aprobador\n";
                        }
                        //Validaciones Colombia de verificacion de Celular y Correo Electronico
                        if(verificarCelular != null && verificarCelular.getBackgroundTintList().getColorForState(new int[] { android.R.attr.state_enabled},0) != getResources().getColor(R.color.aprobados,null) && (verificarCelular.getTag() == null || (verificarCelular.getTag() != null && !verificarCelular.getTag().toString().equals("Opcional")))){
                            numErrores++;
                            mensajeError += "- Falta verificar el número de celular\n";
                        }
                        if(verificarCorreo != null && verificarCorreo.getBackgroundTintList().getColorForState(new int[] { android.R.attr.state_enabled},0) != getResources().getColor(R.color.aprobados,null) && (verificarCorreo.getTag() == null || (verificarCorreo.getTag() != null && !verificarCorreo.getTag().toString().equals("Opcional")))){
                            numErrores++;
                            mensajeError += "- Falta verificar el correo electrónico\n";
                        }

                        //Validacion de datos obligatorios en los bloques (Contactos para Colombia)
                        for(int i=0; i < listaCamposBloque.size(); i++) {
                            String campoBloque = listaCamposBloque.get(i);
                            boolean bloque_obl = mDBHelper.EsBloqueObligatorio(campoBloque);
                            if(bloque_obl){
                                switch(campoBloque){
                                    case "W_CTE-CONTACTOS":
                                        if(contactosSolicitud.size() == 0) {
                                            numErrores++;
                                            mensajeError += "- Falta agregar al menos 1 contacto adicional!\n";
                                        }
                                        break;
                                    case "W_CTE-BANCOS":
                                        if(bancosSolicitud.size() == 0) {
                                            numErrores++;
                                            mensajeError += "- Falta agregar al menos 1 banco!\n";
                                        }
                                        break;
                                    case "W_CTE-VISITAS":
                                        if(visitasSolicitud.size() == 0) {
                                            numErrores++;
                                            mensajeError += "- Falta agregar al menos 1 tipo de visita!\n";
                                        }
                                        break;
                                    case "W_CTE-IMPUESTOS":
                                        if(visitasSolicitud.size() == 0) {
                                            numErrores++;
                                            mensajeError += "- Falta agregar al menos 1 Impuesto!\n";
                                        }
                                        break;
                                    case "W_CTE-INTERLOCUTOR":
                                        if(visitasSolicitud.size() == 0) {
                                            numErrores++;
                                            mensajeError += "- Falta agregar al menos 1 Interlocutor!\n";
                                        }
                                        break;
                                }
                            }
                        }

                        if(numErrores == 0) {
                            DialogHandler appdialog = new DialogHandler();
                            appdialog.Confirm(SolicitudActivity.this, "Confirmación Solicitud", "Esta seguro que desea guardar la solicitud?", "No", "Si", new GuardarFormulario(getBaseContext()));

                        }else{
                            Toasty.warning(SolicitudActivity.this, "Revise los Siguientes campos: \n"+mensajeError, Toasty.LENGTH_LONG).show();
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

        if(modificable) {
            // create the AidcManager providing a Context and an
            // CreatedCallback implementation.
            /*AidcManager.create(getBaseContext(), new AidcManager.CreatedCallback() {
                @Override
                public void onCreated(AidcManager aidcManager) {
                    manager = aidcManager;
                    final String sociedad = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("W_CTE_BUKRS","");
                    try {
                        reader = manager.createBarcodeReader();
                        if(sociedad.equals("F443")) {
                            //reader.setProperty(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
                            reader.setProperty(BarcodeReader.PROPERTY_OCR_MODE, BarcodeReader.POSTAL_OCR_MODE_NORMAL);
                            reader.setProperty(BarcodeReader.PROPERTY_OCR_ACTIVE_TEMPLATE, 2);
                        }else
                        if(sociedad.equals("F445")) {
                            reader.setProperty(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);
                        }else
                        if(sociedad.equals("F446")) {
                            reader.setProperty(BarcodeReader.PROPERTY_OCR_MODE, BarcodeReader.POSTAL_OCR_MODE_NORMAL);
                            reader.setProperty(BarcodeReader.PROPERTY_OCR_ACTIVE_TEMPLATE, 2);
                        }
                        BarcodeReader.BarcodeListener barcodeListener = new BarcodeReader.BarcodeListener() {
                            @Override
                            public void onBarcodeEvent(final BarcodeReadEvent barcodeReadEvent) {
                                // update UI to reflect the data
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if ((barcodeReadEvent.getAimId().substring(1, 2).equals("L") && (sociedad.equals("F443") || sociedad.equals("F445") || sociedad.equals("F451"))) //Cedula Fisica Costa Rica
                                                || (barcodeReadEvent.getAimId().substring(1, 2).equals("o") && (sociedad.equals("F443") || sociedad.equals("F446") || sociedad.equals("1657") || sociedad.equals("1658")))) {//DPI guatemala
                                            String lecturaCedula = barcodeReadEvent.getBarcodeData();
                                            try {
                                                reader.softwareTrigger(false);
                                            } catch (ScannerNotClaimedException e) {
                                                e.printStackTrace();
                                            } catch (ScannerUnavailableException e) {
                                                e.printStackTrace();
                                            }
                                            Spinner spinner_tipo = (Spinner) mapeoCamposDinamicos.get("W_CTE-KATR3");
                                            MaskedEditText editText_cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD1");
                                            MaskedEditText editText_name3 = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-NAME3");
                                            MaskedEditText editText_name4 = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-NAME4");

                                            if (spinner_tipo != null) {
                                                spinner_tipo.setSelection(VariablesGlobales.getIndex(spinner_tipo, "C1"));
                                            }
                                            String datosCedula = "";
                                            String codigo = "";
                                            String cedula = "";
                                            String nombre = "";
                                            String apellido1 = "";
                                            String apellido2 = "";
                                            switch (sociedad) {
                                                case "F443":
                                                case "F445":
                                                case "F451":
                                                    codigo = datosCedula;
                                                    codigo = lecturaCedula;
                                                    cedula = codigo.substring(5, 18).trim();
                                                    String[] nombreCompleto1 = codigo.substring(60, 90).split("<<");
                                                    nombre = nombreCompleto1[0].replace("<", " ");
                                                    apellido1 = nombreCompleto1[1].replace("<", " ");
                                                    if (editText_cedula != null) {
                                                        editText_cedula.setMask("0#-####-####-00");
                                                        editText_cedula.setText(cedula);
                                                        ValidarCedula(editText_cedula, ((OpcionSpinner) spinner_tipo.getSelectedItem()).getId());
                                                    }
                                                    break;
                                                case "F446":
                                                case "1657":
                                                case "1658":
                                                    editText_cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
                                                    codigo = lecturaCedula;
                                                    cedula = codigo.substring(5, 18).trim();
                                                    String[] nombreCompleto = codigo.substring(60, 90).split("<<");
                                                    nombre = nombreCompleto[0].replace("<", " ");
                                                    apellido1 = nombreCompleto[1].replace("<", " ");
                                                    if (editText_cedula != null) {
                                                        editText_cedula.setText(cedula);
                                                        ValidarCedula(editText_cedula, ((OpcionSpinner) spinner_tipo.getSelectedItem()).getId());
                                                    }
                                                    break;
                                            }

                                            if (editText_name3 != null) {
                                                String nombre_completo = nombre + " " + apellido1 + " " + apellido2;
                                                if (nombre_completo.length() <= 35)
                                                    editText_name3.setText(nombre_completo);
                                                else {
                                                    editText_name3.setText(nombre_completo.substring(0, 35));
                                                    if (editText_name4 != null) {
                                                        editText_name3.setText(nombre_completo.substring(35, 70));
                                                    }
                                                }
                                            }
                                        } else if (barcodeReadEvent.getAimId().substring(1, 2).equals("A") && sociedad.equals("F443")) {//Cedula Extranjera Costa rica
                                            Spinner spinner_tipo = (Spinner) mapeoCamposDinamicos.get("W_CTE-KATR3");
                                            if (spinner_tipo != null) {
                                                spinner_tipo.setSelection(VariablesGlobales.getIndex(spinner_tipo, "C3"));
                                            }
                                            Toasty.warning(getBaseContext(), "ID y nombre no presentes en la lectura!", Toasty.LENGTH_SHORT).show();
                                        } else {
                                            Toasty.warning(getBaseContext(), "Codigo leido no reconocido!", Toasty.LENGTH_SHORT).show();
                                        }
                                    }

                                    private String decodificarLecturaPDF417(String lecturaCedula) {
                                        byte[] raw = new byte[0];
                                        try {
                                            raw = lecturaCedula.getBytes("ISO-8859-1");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                        //Intento de decodificar el valor de la cedula en PDF417 con encriptacion XOR cypher
                                        String d = "";
                                        int j = 0;
                                        for (int i = 0; i < raw.length; i++) {
                                            if (j == 17) {
                                                j = 0;
                                            }
                                            char c = (char) (keysArray[j] ^ ((char) (raw[i])));
                                            if ((c + "").matches("^[a-zA-Z0-9]*$")) {
                                                d += c;
                                            } else {
                                                d += c;
                                            }
                                            j++;
                                        }
                                        return d;
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
            });*/
        }else if(!tipoSolicitud.equals("70")){
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
        //tb_impuestos.addDataClickListener(new ImpuestoClickListener());
        //tb_impuestos.addDataLongClickListener(new ImpuestoLongClickListener());
        tb_bancos = new de.codecrafters.tableview.TableView<>(this);
        tb_bancos.addDataClickListener(new BancoClickListener());
        tb_bancos.addDataLongClickListener(new BancoLongClickListener());
        tb_interlocutores = new de.codecrafters.tableview.TableView<>(this);
        //tb_interlocutores.addDataClickListener(new InterlocutorClickListener());
        //tb_interlocutores.addDataLongClickListener(new InterlocutorLongClickListener());
        tb_visitas = new de.codecrafters.tableview.TableView<>(this);
        tb_visitas.addDataClickListener(new VisitasClickListener());
        //tb_visitas.addDataLongClickListener(new VisitasLongClickListener());

        tb_adjuntos = new de.codecrafters.tableview.TableView<>(this);
        tb_comentarios = new de.codecrafters.tableview.TableView<>(this);

        //tb_comentarios2 = new TableView(this);
        //tb_adjuntos.addDataClickListener(new AdjuntosClickListener());
        //tb_adjuntos.addDataLongClickListener(new AdjuntosLongClickListener());
        contactosSolicitud = new ArrayList<>();
        impuestosSolicitud = new ArrayList<>();
        interlocutoresSolicitud = new ArrayList<>();
        bancosSolicitud = new ArrayList<>();
        visitasSolicitud = new ArrayList<>();
        adjuntosSolicitud = new ArrayList<>();
        horariosSolicitud = new ArrayList<>();
        comentarios = new ArrayList<>();
        //notificantesSolicitud = new ArrayList<Adjuntos>();

        cropImage = this.registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                manejadorAdjuntos.setUri(result.getUriContent());
                manejadorAdjuntos.AgregarAdjunto(result.getUriContent());
            }
        });
        manejadorAdjuntos = new ManejadorAdjuntos(mPhotoUri, mDBHelper, adjuntosSolicitud, modificable, firma, GUID, tb_adjuntos, mapeoCamposDinamicos,  getApplicationContext(),SolicitudActivity.this,cropImage);
    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        SolicitudActivity.this.unregisterReceiver(sentReceiver);
        SolicitudActivity.this.unregisterReceiver(deliveredReceiver);
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
    //Se dispara al escoger el documento que se quiere relacionar a la solicitud
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeakReference<Activity> weakRefA = new WeakReference<Activity>(SolicitudActivity.this);
        if (requestCode == VariablesGlobales.REQUEST_CODE_MAP && resultCode == 1 && data != null) {
            String lat = data.getStringExtra("latitude");
            String lng = data.getStringExtra("longitude");
            ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT")).setText(lat);
            ((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG")).setText(lng);
        }else {
            try {
                manejadorAdjuntos.setUri(mPhotoUri);
                manejadorAdjuntos.ActivityResult(requestCode, resultCode, data);
                //ManejadorAdjuntos.ActivityResult(requestCode, resultCode, data, getApplicationContext(),weakRefA.get(), mPhotoUri, mDBHelper,  adjuntosSolicitud,  modificable,  firma,  GUID, tb_adjuntos, mapeoCamposDinamicos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> title = new ArrayList<>();
        private FragmentManager fragmentManager;
        private Context context;
        private boolean isInitialized = false;
        private ViewPagerAdapter(FragmentManager manager,Context c) {
            super(manager);
            List<String> pestanas = mDBHelper.getPestanasFormulario(tipoSolicitud);
            title.addAll(pestanas);
            fragmentManager = manager;
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
            String nombre = "";
            try {
                //ms.requestDisallowInterceptTouchEvent(true);
                //ms.getParent().requestDisallowInterceptTouchEvent(true);
                LinearLayout ll = view.findViewById(R.id.miPagina);
                FrameLayout fl = view.findViewById(R.id.miFrame);

                nombre = Objects.requireNonNull(Objects.requireNonNull(viewPager.getAdapter()).getPageTitle(position)).toString().trim();

                if (nombre.equals("Datos Generales") || nombre.equals("Informacion General")) {
                    LlenarPestana(mDBHelper, ll, tipoSolicitud, "D", idSolicitud);
                }
                if (nombre.equals("Facturación") || nombre.equals("Facturacion")) {
                    LlenarPestana(mDBHelper, ll, tipoSolicitud, "F", idSolicitud);
                }
                if (nombre.equals("Ventas")) {
                    LlenarPestana(mDBHelper, ll, tipoSolicitud, "V", idSolicitud);
                }
                if (nombre.equals("Marketing")) {
                    LlenarPestana(mDBHelper, ll, tipoSolicitud, "M", idSolicitud);
                }
                if (nombre.equals("Creditos") || nombre.equals("Créditos") || nombre.equals("Crédito") || nombre.equals("Credito")) {
                    LlenarPestana(mDBHelper, ll, tipoSolicitud, "C", idSolicitud);
                }
                if (nombre.equals("Adjuntos") || nombre.equals("Adicionales")) {
                    LlenarPestana(mDBHelper, ll, tipoSolicitud, "Z", idSolicitud);
                }
                viewPager.setPageTransformer(true, (ViewPager.PageTransformer) new CubeTransformer());


            }catch(Exception e){
                Toasty.error(getContext(),"Los campos de la pestaña "+nombre+" estan INCOMPLETOS por error:"+e.getMessage()).show();
            }
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View viewa, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(viewa, savedInstanceState);
            //Si viene de Presolicitud, llenar los campos que tiene la presolcitud a la nueva solicitud
            if (idPresolicitud != null) {
                ArrayList<HashMap<String, String>> preSolicitudSeleccionada = new ArrayList<>();
                preSolicitudSeleccionada = mDBHelper.getSolicitud(idPresolicitud);
                if (preSolicitudSeleccionada.size() > 0) {
                    for (HashMap<String, String> map : preSolicitudSeleccionada) {
                        for (String key : map.keySet()) {
                            String value = map.get(key);
                            if(mapeoCamposDinamicos.containsKey(key)) {
                                View view = mapeoCamposDinamicos.get(key);

                                if (view != null) {
                                    if (view instanceof MaskedEditText) {
                                        if (value != null && value.length() > 0)
                                            ((MaskedEditText) view).setText(value);
                                    } else if (view instanceof TextView) {
                                        if (value != null && value.length() > 0)
                                            ((TextView) view).setText(value);
                                    } else if (view instanceof Spinner) {
                                        if (value != null && value.length() > 0 && VariablesGlobales.getIndex(((Spinner) view), value) >= 0)
                                            ((Spinner) view).setSelection(VariablesGlobales.getIndex(((Spinner) view), value));
                                    } else if (view instanceof CheckBox) {
                                        if (value != null && value.equals("X") || value.equals("1"))
                                            ((CheckBox) view).setChecked(true);
                                    }
                                } else {
                                    System.out.println("Key: " + key + " -> View not found!");
                                }
                            }
                        }
                    }
                    if(position == 0) {
                        adjuntosSolicitud = mDBHelper.getAdjuntosDB(idPresolicitud);
                        manejadorAdjuntos.setAdjuntosSolicitud(adjuntosSolicitud);
                    }
                }
            }
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
                    if (!seccionAnterior.equals(campos.get(i).get("id_seccion").trim()) && !campos.get(i).get("id_seccion").trim().equals("99")) {
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
                    if (campos.get(i).get("tipo_input") != null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("adjuntos")) {
                        //Tipo ADJUNTOS
                        DesplegarBloque(mDBHelper, ll, campos.get(i));
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        if (campos.get(i).get("tabla_local").trim().length() > 0) {
                            listaCamposBloque.add(campos.get(i).get("campo").trim());
                        }
                    } else if (campos.get(i).get("tipo_input") != null && (campos.get(i).get("tipo_input").trim().toLowerCase().equals("grid") || campos.get(i).get("tipo_input").trim().toLowerCase().equals("horarios"))) {
                        //Tipo GRID o BLOQUE de Datos (Estos Datos requieren una tabla de la BD adicional a FORMHVKOF)
                        //Bloques Disponibles [Contactos, Impuestos, Funciones Interlocutor, visitas, bancos, notificantes]
                        DesplegarBloque(mDBHelper, ll, campos.get(i));
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        if (campos.get(i).get("tabla_local").trim().length() > 0) {
                            if(campos.get(i).get("tipo_input").trim().toLowerCase().equals("horarios"))
                                listaCamposBloque.add("W_CTE-HORARIOS");
                            else
                                listaCamposBloque.add(campos.get(i).get("campo").trim());
                        }
                    } else if (campos.get(i).get("tipo_input") != null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("encuesta")) {
                        //Encuesta Canales, se genera un checkbox que indicara si se ha realizado la encuesta de canales completa
                        //Tipo CHECKBOX
                        CheckBox checkbox = new CheckBox(getContext());
                        checkbox.setText(campos.get(i).get("descr"));
                        if (campos.get(i).get("sup").trim().length() > 0) {
                            checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("vis").trim().length() > 0) {
                            checkbox.setEnabled(false);
                            //checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("dfaul").replace(" ","").trim().length() > 0) {//Sustituye un valor basura que NO es espacio xA0 en UTF8
                            checkbox.setChecked(true);
                        }
                        if (solicitudSeleccionada.size() > 0) {
                            checkbox.setChecked(true);
                        }
                        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        checkbox.setLayoutParams(clp);
                        checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_survey, null), null);

                        ll.addView(checkbox);
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String sociedad = PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS","");
                                switch(sociedad){
                                    case "F443":
                                    case "F445":
                                    case "F451":
                                    case "F446":
                                    case "1657":
                                    case "1658":
                                    case "1661":
                                    case "Z001":
                                        displayDialogEncuestaCanales(getContext());
                                        break;
                                    case "F428":
                                        displayDialogEncuestaCanalesColombia(getContext());
                                        break;
                                }

                                if (((CheckBox) v).isChecked())
                                    ((CheckBox) v).setChecked(false);
                                else
                                    ((CheckBox) v).setChecked(true);
                            }
                        });

                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_checked}, // unchecked
                                        new int[]{android.R.attr.state_checked}, // checked
                                },
                                new int[]{
                                        Color.parseColor("#110000"),
                                        Color.parseColor("#00aa00"),
                                }
                        );

                        CompoundButtonCompat.setButtonTintList(checkbox, colorStateList);

                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    } else if (campos.get(i).get("tipo_input") != null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("encuesta_gec")) {
                        //Encuesta gec, se genera un checkbox que indicara si se ha realizado la encuesta de canales completa
                        //Tipo CHECKBOX
                        final CheckBox checkbox = new CheckBox(getContext());
                        checkbox.setText(campos.get(i).get("descr"));
                        if (campos.get(i).get("sup").trim().length() > 0) {
                            checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("vis").trim().length() > 0) {
                            checkbox.setEnabled(false);
                            //checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("dfaul").replace(" ","").trim().length() > 0) {
                            checkbox.setChecked(true);
                        }
                        if (solicitudSeleccionada.size() > 0) {
                            checkbox.setChecked(true);
                        }
                        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        checkbox.setLayoutParams(clp);
                        checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_survey, null), null);
                        ll.addView(checkbox);
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(VariablesGlobales.getSociedad().equals("F428"))
                                    displayDialogEncuestaGecColombia(getContext());
                                else
                                    displayDialogEncuestaGec(getContext());
                                if (((CheckBox) v).isChecked())
                                    ((CheckBox) v).setChecked(false);
                                else
                                    ((CheckBox) v).setChecked(true);
                            }
                        });

                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_checked}, // unchecked
                                        new int[]{android.R.attr.state_checked}, // checked
                                },
                                new int[]{
                                        Color.parseColor("#110000"),
                                        Color.parseColor("#00aa00"),
                                }
                        );

                        CompoundButtonCompat.setButtonTintList(checkbox, colorStateList);

                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    } else if (campos.get(i).get("tipo_input") != null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("encuesta_consumo")) {
                        //Encuesta gec, se genera un checkbox que indicara si se ha realizado la encuesta de canales completa
                        //Tipo CHECKBOX
                        final CheckBox checkbox = new CheckBox(getContext());
                        checkbox.setText(campos.get(i).get("descr"));
                        if (campos.get(i).get("sup").trim().length() > 0) {
                            checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("vis").trim().length() > 0) {
                            checkbox.setEnabled(false);
                            //checkbox.setVisibility(View.GONE);
                        }
                        /*if (campos.get(i).get("dfaul").replace(" ","").trim().length() > 0) {
                            checkbox.setChecked(true);
                        }*/
                        if (solicitudSeleccionada.size() > 0) {
                            checkbox.setChecked(true);
                        }
                        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        checkbox.setLayoutParams(clp);
                        checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_survey, null), null);
                        ll.addView(checkbox);
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    displayDialogEncuestaOcasionConsumo(getContext());
                                if (((CheckBox) v).isChecked())
                                    ((CheckBox) v).setChecked(false);
                                else
                                    ((CheckBox) v).setChecked(true);
                            }
                        });

                        ColorStateList colorStateList = new ColorStateList(
                                new int[][]{
                                        new int[]{-android.R.attr.state_checked}, // unchecked
                                        new int[]{android.R.attr.state_checked}, // checked
                                },
                                new int[]{
                                        Color.parseColor("#110000"),
                                        Color.parseColor("#00aa00"),
                                }
                        );

                        CompoundButtonCompat.setButtonTintList(checkbox, colorStateList);

                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    } else if (campos.get(i).get("tipo_input") != null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("checkbox")) {
                        //Tipo CHECKBOX
                        CheckBox checkbox = new CheckBox(getContext());
                        checkbox.setText(campos.get(i).get("descr"));
                        if (campos.get(i).get("sup").trim().length() > 0) {
                            checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("vis").trim().length() > 0) {
                            checkbox.setEnabled(false);
                            //checkbox.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("dfaul").trim().length() > 0) {
                            checkbox.setChecked(true);
                        }

                        ll.addView(checkbox);
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                        //Excepciones de visualizacion y configuracionde campos dados por la tabla ConfigCampos
                        int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                        if (excepcion >= 0) {
                            HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                            Validaciones.ejecutarExcepcion(getContext(),checkbox,null,configExcepcion,listaCamposObligatorios, campos.get(i));

                            int excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((SearchableSpinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                            if (excepcionxAgencia >= 0) {
                                HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                                Validaciones.ejecutarExcepcion(getContext(),checkbox,null,configExcepcionxAgencia,listaCamposObligatorios, campos.get(i));
                            }
                        }
                        if (solicitudSeleccionada.size() > 0) {
                            System.out.print(campos.get(i).get("campo"));
                            if (solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()) != null && solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim().length() > 0)
                                checkbox.setChecked(true);
                            if (!modificable) {
                                checkbox.setEnabled(false);
                            }
                        }
                    } else if (campos.get(i).get("tabla") != null && campos.get(i).get("tabla").replace(" ","").trim().length() > 0) {
                        //Tipo ComboBox/SelectBox/Spinner
                        TextView label = new TextView(getContext());
                        label.setText(campos.get(i).get("descr"));
                        label.setTextAppearance(R.style.AppTheme_TextFloatLabelAppearance);
                        LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lpl.setMargins(35, 5, 0, 0);
                        label.setPadding(0, 0, 0, 0);
                        label.setLayoutParams(lpl);

                        final SearchableSpinner combo = new SearchableSpinner(getContext(), "TAG_"+campos.get(i).get("campo"));
                        combo.setTag(campos.get(i).get("descr"));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(0, -10, 0, 25);
                        combo.setPadding(0, 0, 0, 0);
                        combo.setLayoutParams(lp);
                        combo.setPopupBackgroundResource(R.drawable.menu_item);
                        if (campos.get(i).get("sup").trim().length() > 0) {
                            label.setVisibility(View.GONE);
                            combo.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-RUTAHH")) {
                            label.setVisibility(View.GONE);
                            combo.setVisibility(View.GONE);
                        }
                        Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
                        combo.setBackground(d);
                        if (campos.get(i).get("vis").trim().length() > 0) {
                                combo.setEnabled(false);
                                combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }

                        ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("cat_" + campos.get(i).get("tabla").trim());
                        if (opciones.size() == 0) {
                            opciones = db.getDatosCatalogo(campos.get(i).get("tabla").trim());
                        }

                        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
                        int selectedIndex = 0;
                        String valorDefectoxRuta = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(campos.get(i).get("campo").trim().replace("-", "_"), "");
                        for (int j = 0; j < opciones.size(); j++) {
                            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
                            if (solicitudSeleccionada.size() > 0) {
                                //valor de la solicitud seleccionada
                                if (opciones.get(j).get("id").trim().equals(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim())) {
                                    selectedIndex = j;
                                }
                            } else {
                                if (campos.get(i).get("dfaul").trim().length() > 0 && opciones.get(j).get("id").trim().equals(campos.get(i).get("dfaul").trim())) {
                                    selectedIndex = j;
                                }
                            }
                            if (valorDefectoxRuta.trim().length() > 0 && opciones.get(j).get("id").trim().equals(valorDefectoxRuta.trim())) {
                                selectedIndex = j;
                                if (!campos.get(i).get("campo").trim().equals("W_CTE-VWERK")) {
                                    if((PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("1661")
                                    || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("Z001"))
                                    && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","").toString().equals("ZRM")){
                                        combo.setEnabled(true);
                                        combo.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                                    }else{
                                        combo.setEnabled(false);
                                        combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                    }
                                }
                            }
                        }
                        // Creando el adaptador(opciones) para el comboBox deseado
                        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, listaopciones);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                        // attaching data adapter to spinner
                        combo.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
                        combo.setSelection(selectedIndex);

                        //Campo de regimen fiscal, se debe cambiar el formato de cedula segun el tipo de cedula
                        if (campos.get(i).get("campo").trim().equals("W_CTE-KATR3") || campos.get(i).get("campo").trim().equals("W_CTE-STCDT")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    final MaskedEditText cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD1");
                                    final MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
                                    final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                    if (cedula != null) {
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
                                            tw = new TextWatcher() {
                                                @Override
                                                public void afterTextChanged(Editable s) { }
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if(s.toString().equals("")){
                                                        cedula.setMask("");
                                                    }
                                                    if(s.toString().equals("N")){
                                                        cedula.setMask("N-####-######");
                                                    }
                                                    if(s.toString().equals("NA")){
                                                        cedula.setMask("NA-##-####-#####");
                                                    }
                                                    if(s.toString().equals("P")){
                                                        cedula.setMask("PE-####-#####");
                                                    }
                                                }
                                            };
                                            cedula.addTextChangedListener(tw);
                                        }else{
                                            if(tw != null){
                                                cedula.removeTextChangedListener(tw);
                                                tw = null;
                                            }
                                        }
                                        if (opcion.getId().equals("P2")) {
                                            cedula.setMask("****************");
                                        }
                                        if (opcion.getId().equals("P3")) {
                                            cedula.setMask("E-####-######");
                                        }
                                        if (opcion.getId().equals("G2")) {//CUI
                                            idfiscal.setMask("#############");
                                            listaCamposObligatorios.remove("W_CTE-STCD1");
                                            cedula.setError(null);
                                            listaCamposObligatorios.add("W_CTE-STCD3");
                                            idfiscal.setError("El campo CUI es obligatorio!");
                                        }
                                        if (opcion.getId().equals("G3")) {//Pasaporte
                                            idfiscal.setMask("************");
                                            listaCamposObligatorios.remove("W_CTE-STCD1");
                                            cedula.setError(null);
                                            listaCamposObligatorios.add("W_CTE-STCD3");
                                            idfiscal.setError("El campo PASAPORTE es obligatorio!");
                                        }
                                        if (opcion.getId().equals("G4")) {//NIT
                                            listaCamposObligatorios.remove("W_CTE-STCD3");
                                            idfiscal.setError(null);
                                            listaCamposObligatorios.add("W_CTE-STCD1");
                                            cedula.setError("El campo NIT es obligatorio!");
                                        }
                                        //mascaras para pais de Guatemala
                                        if (opcion.getId().contains("G") || opcion.getId().contains("C")
                                                && (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F446")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("1657")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("1658"))) {

                                            cedula.setMask("AA");
                                            cedula.addTextChangedListener(new TextWatcher() {
                                                @Override
                                                public void afterTextChanged(Editable s) { }
                                                @Override
                                                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                                @Override
                                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                    if(s.toString().length() <= 2){
                                                        cedula.setMask("**");
                                                    }
                                                    if(s.toString().length() > 2 && s.toString().length() < 10 && !s.toString().contains("-")){
                                                        String cantidad="";
                                                        for(int x = 0; x < s.toString().length(); x++){
                                                            cantidad += "#";
                                                        }
                                                        cedula.setMask(cantidad+"-A");
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
                                                                cedula.setMask(cantidad);
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

                                        //Colombia - seteaar el check de persona fisica en caso de tipo de identificacion (13, 22, 41 y 42)
                                        if (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS","").equals("F428")) {
                                            if((opcion.getId().equals("13") || opcion.getId().equals("22") || opcion.getId().equals("41") || opcion.getId().equals("42"))){
                                                CheckBox persona_fisica = (CheckBox)mapeoCamposDinamicos.get("W_CTE-STKZN");
                                                if(persona_fisica != null) {
                                                    persona_fisica.setChecked(true);
                                                }
                                            }else{
                                                CheckBox persona_fisica = (CheckBox)mapeoCamposDinamicos.get("W_CTE-STKZN");
                                                if(persona_fisica != null) {
                                                    persona_fisica.setChecked(false);
                                                }
                                            }
                                        }

                                        cedula.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                            @Override
                                            public void onFocusChange(View v, boolean hasFocus) {
                                                if (!hasFocus) {
                                                    ValidarCedula(v, opcion.getId());
                                                    TextView texto = (TextView) v;
                                                    if(!texto.getText().toString().trim().equals("")){
                                                        if(db.ConfiguracionxSociedad("verificar_cedula_api").toString().equals("1")){
                                                            WeakReference<Context> weakRefs1 = new WeakReference<Context>(getContext());
                                                            WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(getActivity());
                                                            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("tipo_conexion","").equals("api")) {
                                                                ValidarIdConInspektor inspektor = new ValidarIdConInspektor(weakRefs1, weakRefAs1, PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS",""), texto.getText().toString().trim(),texto);
                                                                inspektor.execute();
                                                            } else {
                                                                ValidarIdConInspektor inspektor = new ValidarIdConInspektor(weakRefs1, weakRefAs1, PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_BUKRS",""), texto.getText().toString().trim(),texto);
                                                                inspektor.execute();
                                                            }
                                                        }
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
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        if (solicitudSeleccionada.size() > 0) {
                            if (!modificable) {
                                combo.setEnabled(false);
                                combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                            }
                        }

                        if (campos.get(i).get("campo").trim().equals("W_CTE-BZIRK")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ArrayList<HashMap<String, String>> valores = mDBHelper.getValoresKOFSegunZonaVentas(((OpcionSpinner) combo.getSelectedItem()).getId());
                                    if(mapeoCamposDinamicos.get("W_CTE-VWERK") != null) {
                                        suppressRecreateAdapter=false;
                                        ((Spinner) mapeoCamposDinamicos.get("W_CTE-VWERK")).setSelection(VariablesGlobales.getIndex(((Spinner) mapeoCamposDinamicos.get("W_CTE-VWERK")), valores.get(0).get("VWERK")));
                                    }
                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
                                    String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                    PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();

                                    ActualizarAprobadores();

                                    //Si tiene diferenciacion por agencia para algun campo llamarlo aqui
                                    /*final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
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
                                    }*/
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-VKGRP")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ActualizarAprobadores();

                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-VWERK")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Spinner zona_transporte = (Spinner) mapeoCamposDinamicos.get("W_CTE-LZONE");
                                    String valor_centro_suministro = ((OpcionSpinner) combo.getSelectedItem()).getId().trim();
                                    String filtroxPais = "";
                                    switch(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                                        case "1661":
                                        case "Z001":
                                            Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                            if(gec != null)
                                                filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                            Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                            if(bzirk_sel != null)
                                                filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                            break;
                                        case "F428":
                                            filtroxPais += " route = '"+PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_RUTAHH","")+"'";
                                            break;
                                        default:
                                            filtroxPais = "";
                                    }
                                    ArrayList<OpcionSpinner> rutas_reparto = null;
                                    if(VariablesGlobales.getSociedad().equals("F428")){
                                        rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("SAPDCAT_Ruta_Relacion",""+filtroxPais);
                                    }else{
                                        rutas_reparto  = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);
                                    }

                                    // Creando el adaptador(opciones) para el comboBox deseado
                                    ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(parent.getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                    // Drop down layout style - list view with radio button
                                    dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                    if (zona_transporte != null) {
                                        zona_transporte.setAdapter(dataAdapterRuta);
                                        int selectedIndex = 0;
                                        for (int j = 0; j < rutas_reparto.size(); j++) {
                                            if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-LZONE") != null && solicitudSeleccionada.get(0).get("W_CTE-LZONE").trim().equals(rutas_reparto.get(j).getId())) {
                                                zona_transporte.setSelection(j);
                                                break;
                                            }
                                        }
                                        //Campos zona de transporte se comporta diferente para Autoventa, debe ser la misma ruta de preventa y no puede ser seleccionable.
                                        if (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")
                                        || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAH")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAN")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAI")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDI")) {
                                            zona_transporte.setEnabled(false);
                                            zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                            zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte,PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_RUTAHH","").trim()));
                                        }else if(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZCM")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDM")
                                                || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDP")){
                                            zona_transporte.setEnabled(true);
                                            zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                                        }
                                    }
                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-KVGR5")) {
                            //TODO aqui se debe cambiar si se quiere trabajar con diferentes tipos de 'PR'
                            if (solicitudSeleccionada.size() == 0) {
                                String mv = db.AsignarModalidadSegunAgenciayTipoVisita(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BZIRK",""),PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA",""));
                                if(mv != ""){
                                    combo.setSelection(VariablesGlobales.getIndex(combo, mv));
                                }else {
                                    combo.setSelection(VariablesGlobales.getIndex(combo, "PR"));
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").equals("ZAT")) {
                                        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS", "").equals("F428"))
                                            combo.setSelection(VariablesGlobales.getIndex(combo, "C27"));
                                        else
                                            combo.setSelection(VariablesGlobales.getIndex(combo, "GV"));
                                    }

                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString().equals("ZTV")) {
                                        combo.setSelection(VariablesGlobales.getIndex(combo, "TA"));
                                    }
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString().equals("ZJV")) {
                                        combo.setSelection(VariablesGlobales.getIndex(combo, "PE"));
                                    }
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString().equals("ZGE")) {
                                        combo.setSelection(VariablesGlobales.getIndex(combo, "C03"));
                                    }
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString().equals("ZCM")) {
                                        combo.setSelection(VariablesGlobales.getIndex(combo, "C32"));
                                    }
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString().equals("ZCS")) {
                                        combo.setSelection(VariablesGlobales.getIndex(combo, "C02"));
                                    }
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").equals("ZES")) {
                                        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS", "").equals("F428"))
                                            combo.setSelection(VariablesGlobales.getIndex(combo, "C11"));
                                    }
                                }
                                if (combo.getSelectedItemPosition() == Spinner.INVALID_POSITION || combo.getSelectedItemPosition() == 0) {
                                    // Nada seleccionado o está en la posición inicial (por ejemplo, "Seleccione una opción")
                                    combo.setEnabled(true);
                                    combo.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                                }else{
                                    combo.setEnabled(false);
                                    combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                }
                            }
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                    if (solicitudSeleccionada.size() == 0) {
                                        visitasSolicitud = mDBHelper.DeterminarPlanesdeVisita(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG", ""), opcion.getId());
                                        int dummy = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDY");
                                        if(dummy != -1) {
                                            visitasSolicitud.get(dummy).setDom_a("0001");
                                            visitasSolicitud.get(dummy).setDom_de("0001");
                                        }

                                        tb_visitas.setDataAdapter(new VisitasTableAdapter(getContext(), getActivity(), visitasSolicitud,modificable));
                                        if (tb_visitas.getLayoutParams() != null) {
                                            tb_visitas.getLayoutParams().height = 50;
                                            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView) * visitasSolicitud.size());
                                        }
                                        //new ResetearVisitas(getContext(), getActivity());
                                        DesplegarBloque(db,ll,campos.get(getIndexOFkey("W_CTE-VISITAS",  campos)));
                                    }else if(!solicitudSeleccionada.get(0).get("W_CTE-KVGR5").toString().equals(opcion.getId())){
                                        Spinner modalidad_preventa = (Spinner)mapeoCamposDinamicos.get("W_CTE-KVGR5");
                                        visitasSolicitud = mDBHelper.DeterminarPlanesdeVisita(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG", ""), opcion.getId());

                                        tb_visitas.setDataAdapter(new VisitasTableAdapter(getContext(), getActivity(), visitasSolicitud,modificable));
                                        if (tb_visitas.getLayoutParams() != null) {
                                            tb_visitas.getLayoutParams().height = 50;
                                            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView ) * visitasSolicitud.size());
                                        }

                                        DesplegarBloque(db,ll,campos.get(getIndexOFkey("W_CTE-VISITAS",  campos)));
                                    }else{
                                        DesplegarBloque(db,ll,campos.get(getIndexOFkey("W_CTE-VISITAS",  campos)));
                                    }
                                    if (position == 0 && listaCamposObligatorios.contains("W_CTE-KVGR5"))
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-VSBED") && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")) {
                            String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG",""), PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_RUTAHH",""));
                            combo.setSelection(VariablesGlobales.getIndex(combo, condicionExpedicion));
                        }
                        if (campos.get(i).get("llamado1").trim().contains("Provincia")) {
                                combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        Provincias(parent);
                                        if (parent.getSelectedView() != null && position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                        if (parent.getSelectedView() != null && position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                        if (parent.getSelectedView() != null && position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }
                        if (campos.get(i).get("llamado1").trim().contains("Municipios")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Municipios(parent);
                                    if (parent.getSelectedView() != null && position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("Barrios")) {
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(getActivity());
                                    BarriosAsync(parent, weakRefA);
                                    if (parent.getSelectedView() != null && position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                        if (parent.getSelectedView() != null && position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                        if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                        if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                    final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                    ArrayList<HashMap<String, String>> canales = mDBHelper.getValoresSegunCanal(opcion.getId());
                                    if(canales.size() > 0){
                                        Spinner grupo_canal = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZGPOCANAL");
                                        Spinner tipo_canal = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZTPOCANAL");
                                        Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                        if(grupo_canal != null){
                                            grupo_canal.setSelection(VariablesGlobales.getIndex(grupo_canal,canales.get(0).get("zgpocanal")));
                                        }
                                        if(tipo_canal != null){
                                            tipo_canal.setSelection(VariablesGlobales.getIndex(tipo_canal,canales.get(0).get("ztpocanal")));
                                        }
                                        if(gec != null){
                                            gec.setSelection(VariablesGlobales.getIndex(gec,canales.get(0).get("gec")));
                                        }
                                    }
                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch((SearchableSpinner)parent, parent.getResources().getString(R.string.error_field_required));
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
                                        if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                            setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));
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
                                    final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                    MaskedEditText editText_cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD1");
                                    editText_cedula.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            if (!hasFocus) {
                                                ValidarCedula(v, opcion.getId());
                                            }
                                        }
                                    });
                                    //Uruguay
                                    TextWatcher miTextWatcher = null;
                                    if (opcion.getId().equals("25")) {
                                        miTextWatcher = new TextWatcher() {
                                            @Override
                                            public void afterTextChanged(Editable s) { }
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if(s.toString().length() <= 8) {
                                                    String cantidad="";
                                                    for(int x = 0; x <= s.toString().length(); x++){
                                                        cantidad += "#";
                                                    }
                                                    OpcionSpinner opcionTipoNif = (OpcionSpinner) ((Spinner) (mapeoCamposDinamicos.get("W_CTE-STCDT"))).getSelectedItem();
                                                    if(opcionTipoNif.getId().equals("25"))
                                                        editText_cedula.setMask(cantidad);
                                                }
                                            }
                                        };
                                        editText_cedula.addTextChangedListener(miTextWatcher);
                                    }
                                    if (opcion.getId().equals("42")) {
                                        editText_cedula.setMask("AAAAAAAAAAAAAAAA");
                                    }
                                    if (opcion.getId().equals("88")) {
                                        editText_cedula.setMask("###########A");
                                    }

                                    AsignarTipoImpuesto(parent);
                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));

                                    ValidarCedula(((View) mapeoCamposDinamicos.get("W_CTE-STCD1")),opcion.getId());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("llamado1").trim().contains("ReplicarValor")) {
                            String[] split = campos.get(i).get("llamado1").trim().split("'");
                            if (split.length < 3)
                                split = campos.get(i).get("llamado1").trim().split("`");
                            if (split.length < 3)
                                split = campos.get(i).get("llamado1").trim().split("\"");
                            final String campoAReplicar = split[1];
                            int finalI1 = i;
                            combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ReplicarValorSpinner(parent, campoAReplicar, ((OpcionSpinner) parent.getSelectedItem()).getId().trim());
                                    if (parent.getSelectedView() != null && position == 0 && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));

                                    if(campos.get(finalI1).get("campo").trim().equals("W_CTE-BZIRK")){
                                        String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                        PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();

                                        ActualizarAprobadores();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        }

                        TableRow.LayoutParams textolp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                        int tamIcono = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                        TableRow.LayoutParams btnlp = new TableRow.LayoutParams(tamIcono, tamIcono);

                        TableRow filaLabel = null;
                        if (campos.get(i).get("campo").trim().equals("W_CTE-LZONE") && VariablesGlobales.getSociedad().equals("F428") && modificable) {
                            TableRow.LayoutParams textolp_h = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                            int tamIcono_h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                            TableRow.LayoutParams btnlp_h = new TableRow.LayoutParams(tamIcono_h, tamIcono_h);
                            filaLabel = new TableRow(getContext());
                            filaLabel.setOrientation(TableRow.HORIZONTAL);
                            filaLabel.setWeightSum(10);
                            filaLabel.setLayoutParams(new TableRow.LayoutParams(WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 10f));

                            btnAyuda = new ImageView(getContext());
                            int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                            textolp_h.setMargins(marginLeft, 0, 0, 0);
                            btnlp_h.setMargins(marginLeft, 0, 0, 0);
                            label.setLayoutParams(textolp_h);
                            btnAyuda.setBackground(getResources().getDrawable(R.drawable.icon_habilitador, null));

                            btnlp_h.gravity = Gravity.LEFT;
                            btnAyuda.setLayoutParams(btnlp_h);
                            btnAyuda.setMaxHeight(50);
                            btnAyuda.setForegroundGravity(GRAVITY_START);

                            //LLamar al hailitador para recualcular ruta de reparto en LZONE
                            btnAyuda.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getActivity().runOnUiThread(new SolicitudActivity.CalcularRepartoConHabilitador(getContext(), getActivity()));
                                }
                            });
                            ToolTipsManager mToolTipsManager = new ToolTipsManager();
                            final ToolTip.Builder builder = new ToolTip.Builder(getContext(), btnAyuda, (RelativeLayout)_ll.getParent() ,  "Calcula Ruta de Reparto LZONE según habilitador", ToolTip.POSITION_ABOVE);
                            builder.setAlign(ToolTip.ALIGN_LEFT);

                            builder.setGravity(ToolTip.GRAVITY_LEFT);
                            builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                            btnAyuda.setOnLongClickListener(view -> {
                                mToolTipsManager.show(builder.build());
                                return true;
                            });
                        }


                        if(filaLabel != null) {
                            filaLabel.addView(label);
                            ll.addView(filaLabel);
                        }else
                            ll.addView(label);
                        if (btnAyuda != null)
                            filaLabel.addView(btnAyuda);
                        ll.addView(combo);

                        if (!listaCamposDinamicos.contains(campos.get(i).get("campo").trim())) {
                            listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                            mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), combo);
                        } else {
                            //listaCamposDinamicos.add(campos.get(i).get("campo").trim()+"1");
                            mapeoCamposDinamicos.put(campos.get(i).get("campo").trim() + "1", combo);
                            //Replicar valores de campos duplicados en configuracion
                            Spinner original = (Spinner) mapeoCamposDinamicos.get(campos.get(i).get("campo").trim());
                            Spinner duplicado = (Spinner) mapeoCamposDinamicos.get(campos.get(i).get("campo").trim() + "1");
                            final String nombreCampo = campos.get(i).get("campo").trim();
                            final int indice = i;
                            original.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Provincia"))
                                        Provincias(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Cantones"))
                                        Cantones(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Municipios"))
                                        Municipios(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Distritos"))
                                        Distritos(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Barrios")){
                                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(getActivity());
                                        BarriosAsync(parent, weakRefA);
                                    }


                                    if (nombreCampo.equals("W_CTE-VWERK") && !suppressRecreateAdapter) {
                                        Spinner zona_transporte = (Spinner) mapeoCamposDinamicos.get("W_CTE-LZONE");
                                        String valor_centro_suministro = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                        String filtroxPais = "";
                                        switch(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                                            case "1661":
                                            case "Z001":
                                                Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                                if(gec != null)
                                                    filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                                Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                                if(bzirk_sel != null)
                                                    filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                                break;
                                            case "F428":
                                                filtroxPais += " route = '"+PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_RUTAHH","")+"'";
                                                break;
                                            default:
                                                filtroxPais = "";
                                        }

                                        ArrayList<OpcionSpinner> rutas_reparto = null;
                                        if(VariablesGlobales.getSociedad().equals("F428")){
                                            rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("SAPDCAT_Ruta_Relacion",""+filtroxPais);
                                        }else{
                                            rutas_reparto  = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);
                                        }
                                        ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                        // Drop down layout style - list view with radio button
                                        dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                        if (zona_transporte != null) {
                                            zona_transporte.setAdapter(dataAdapterRuta);
                                            int selectedIndex = 0;
                                            for (int j = 0; j < rutas_reparto.size(); j++) {
                                                if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-LZONE") != null && solicitudSeleccionada.get(0).get("W_CTE-LZONE").trim().equals(rutas_reparto.get(j).getId())) {
                                                    zona_transporte.setSelection(j);
                                                    break;
                                                }
                                            }
                                            //Campos zona de transporte se comporta diferente para Autoventa, debe ser la misma ruta de preventa y no puede ser seleccionable.
                                            if (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAH")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAN")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAI")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDI")) {
                                                zona_transporte.setEnabled(false);
                                                zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                                zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte,PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_RUTAHH","").trim()));
                                            }else if(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZCM")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDM")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDP")){
                                                zona_transporte.setEnabled(true);
                                                zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                                            }
                                        }
                                    }

                                    if(nombreCampo.equals("W_CTE-BZIRK")){
                                        String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                        PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();

                                        ActualizarAprobadores();
                                    }

                                    ReplicarValorSpinner(parent, nombreCampo + "1", ((OpcionSpinner) parent.getSelectedItem()).getId().trim());
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));

                                    if (suppressRecreateAdapter && duplicado == null) {
                                        suppressRecreateAdapter = false; // Reset after programmatic call
                                        return;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            duplicado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Provincia"))
                                        Provincias(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Cantones"))
                                        Cantones(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Municipios"))
                                        Municipios(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Distritos"))
                                        Distritos(parent);
                                    if (campos.get(indice).get("llamado1")!= null && campos.get(indice).get("llamado1").contains("Barrios")) {
                                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(getActivity());
                                        BarriosAsync(parent, weakRefA);
                                    }
                                    if (nombreCampo.equals("W_CTE-VWERK") && !suppressRecreateAdapter) {
                                        Spinner zona_transporte = (Spinner) mapeoCamposDinamicos.get("W_CTE-LZONE");
                                        String valor_centro_suministro = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                        String filtroxPais = "";
                                        switch(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
                                            case "1661":
                                            case "Z001":
                                                Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                                                if(gec != null)
                                                    filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                                                Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                                                if(bzirk_sel != null)
                                                    filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                                                break;
                                            case "F428":
                                                filtroxPais += " route = '"+PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_RUTAHH","")+"'";
                                                break;
                                            default:
                                                filtroxPais = "";
                                        }

                                        ArrayList<OpcionSpinner> rutas_reparto = null;
                                        if(VariablesGlobales.getSociedad().equals("F428")){
                                            rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("SAPDCAT_Ruta_Relacion",filtroxPais);
                                        }else{
                                            rutas_reparto =  mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);
                                        }
                                        ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                        // Drop down layout style - list view with radio button
                                        dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                        if (zona_transporte != null) {
                                            zona_transporte.setAdapter(dataAdapterRuta);
                                            int selectedIndex = 0;
                                            for (int j = 0; j < rutas_reparto.size(); j++) {
                                                if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-LZONE") != null && solicitudSeleccionada.get(0).get("W_CTE-LZONE").trim().equals(rutas_reparto.get(j).getId())) {
                                                    zona_transporte.setSelection(j);
                                                    break;
                                                }
                                            }
                                            //Campos zona de transporte se comporta diferente para Autoventa, debe ser la misma ruta de preventa y no puede ser seleccionable.
                                            if (PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAT")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAH")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAN")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZAI")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDI")) {
                                                zona_transporte.setEnabled(false);
                                                zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                                                zona_transporte.setSelection(VariablesGlobales.getIndex(zona_transporte,PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_RUTAHH","").trim()));
                                            }else if(PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZCM")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDM")
                                                    || PreferenceManager.getDefaultSharedPreferences(parent.getContext()).getString("W_CTE_TIPORUTA","ZPV").toString().equals("ZDP")){
                                                zona_transporte.setEnabled(true);
                                                zona_transporte.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                                            }
                                        }
                                    }

                                    if(nombreCampo.equals("W_CTE-BZIRK")){
                                        String zona_ventas = ((OpcionSpinner) parent.getSelectedItem()).getId().trim();
                                        PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext())).edit().putString("W_CTE_BZIRK",zona_ventas).apply();

                                        ActualizarAprobadores();
                                    }

                                    ReplicarValorSpinner(parent, nombreCampo, ((OpcionSpinner) parent.getSelectedItem()).getId().trim());
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null && campos.get(finalI).get("obl") != null && campos.get(finalI).get("obl").trim().length() > 0)
                                        setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));

                                    if (suppressRecreateAdapter) {
                                        suppressRecreateAdapter = false; // Reset after programmatic call
                                        return;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        if (campos.get(i).get("obl") != null && campos.get(i).get("obl").trim().length() > 0) {
                            listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                            OpcionSpinner op = new OpcionSpinner("", "");
                            if (combo.getOnItemSelectedListener() == null) {
                                combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        final TextView opcion = (TextView) parent.getSelectedView();
                                        if (position == 0 && opcion != null)
                                            setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        Toasty.info(getContext(), "Nothing Selected").show();
                                    }
                                });
                            }
                        }
                        //Excepciones de visualizacion y configuracionde campos dados por la tabla ConfigCampos
                        int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                        if (excepcion >= 0) {
                            HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                            Validaciones.ejecutarExcepcion(getContext(),combo,label,configExcepcion,listaCamposObligatorios, campos.get(i));

                            int excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                            if (excepcionxAgencia >= 0) {
                                HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                                Validaciones.ejecutarExcepcion(getContext(),combo,label,configExcepcionxAgencia,listaCamposObligatorios, campos.get(i));
                            }
                        }
                    } else {
                        //Tipo EditText normal textbox
                        TableRow fila = new TableRow(getContext());
                        fila.setOrientation(TableRow.HORIZONTAL);
                        fila.setWeightSum(10);
                        fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 10f));

                        final TextInputLayout label = new TextInputLayout(Objects.requireNonNull(getContext()));
                        label.setHint(campos.get(i).get("descr"));
                        label.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorTextView,null)));
                        label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                        label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);

                        final MaskedEditText et = new MaskedEditText(getContext(), null);
                        InputFilter[] editFilters = et.getFilters();
                        InputFilter[] newFilters = null;
                        //if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").trim().equals("F451")){
                            newFilters = new InputFilter[editFilters.length + 1];
                            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);

                        newFilters[editFilters.length] =  new Validaciones.TagAwareInputFilter(et);
                        et.setFilters(newFilters);

                            //newFilters[editFilters.length] = Validaciones.getEditTextFilter();
                            //et.setFilters(newFilters);
                        //}

                        et.setTag(campos.get(i).get("descr"));
                        //et.setTextColor(getResources().getColor(R.color.colorTextView,null));
                        //et.setBackgroundColor(getResources().getColor(R.color.black,null));
                        //et.setHint(campos.get(i).get("descr"));
                        if (campos.get(i).get("sup").trim().length() > 0) {
                            et.setVisibility(View.GONE);
                            label.setVisibility(View.GONE);
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-RUTAHH")) {
                            et.setVisibility(View.GONE);
                            label.setVisibility(View.GONE);
                        }
                        // Atributos del Texto a crear
                        //TableLayout.LayoutParams lp =  new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,0.5f);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                        lp.setMargins(0, 15, 0, 15);

                        et.setLayoutParams(lp);
                        et.setPadding(20, 5, 20, 5);
                        Drawable d = getResources().getDrawable(R.drawable.textbackground, null);
                        et.setBackground(d);
                        if (campos.get(i).get("vis").trim().length() > 0) {
                            et.setEnabled(false);
                            et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                            //et.setVisibility(View.GONE);
                        }
                        et.setMaxLines(1);

                        if (campos.get(i).get("datatype") != null && campos.get(i).get("datatype").contains("char")) {
                            if (campos.get(i).get("campo").trim().equals("W_CTE-STCD3")) {
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editFilters = et.getFilters();
                                newFilters = new InputFilter[editFilters.length + 1];
                                System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                                newFilters[editFilters.length] = new InputFilter.LengthFilter(18);
                                et.setFilters(newFilters);
                                if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("F446") || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD","").equals("1657") || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD","").equals("1658")){
                                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                                }
                                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (!hasFocus) {
                                            ValidarIDFiscal(getContext());
                                        }
                                    }
                                });
                            }else if(campos.get(i).get("campo").trim().equals("W_CTE-PSTLZ") && (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661") || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD","").equals("Z001"))){
                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                editFilters = et.getFilters();
                                newFilters = new InputFilter[editFilters.length + 1];
                                System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                                newFilters[editFilters.length] = new InputFilter.LengthFilter( 5 );
                                et.setFilters(newFilters);
                            } else {

                                // IMPORTANT, do this before any of the code following it
                                et.setSingleLine(true);
                                et.setHorizontallyScrolling(false);
                                et.setMaxLines(5);
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
                        } else if (campos.get(i).get("datatype") != null && campos.get(i).get("datatype").equals("decimal")) {
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editFilters = et.getFilters();
                            newFilters = new InputFilter[editFilters.length + 1];
                            System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                            newFilters[editFilters.length] = new InputFilter.LengthFilter(Integer.valueOf(campos.get(i).get("numeric_precision")));
                            et.setFilters(newFilters);
                        }


                        editFilters = et.getFilters();
                        newFilters = new InputFilter[editFilters.length + 1];
                        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                        newFilters[editFilters.length] = new InputFilter.AllCaps();
                        et.setFilters(newFilters);
                        et.setAllCaps(true);

                        TableRow.LayoutParams textolp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                        int tamIcono = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 31, getResources().getDisplayMetrics());
                        TableRow.LayoutParams btnlp = new TableRow.LayoutParams(tamIcono, tamIcono);
                        if (campos.get(i).get("tooltip") != null && campos.get(i).get("tooltip") != "") {
                            btnAyuda = new ImageView(getContext());
                            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                            textolp.setMargins(0, 0, 20, 0);
                            btnlp.setMargins(0, marginTop, tamIcono, 0);
                            label.setLayoutParams(textolp);
                            btnAyuda.setBackground(getResources().getDrawable(R.drawable.icon_ayuda, null));

                            btnAyuda.setLayoutParams(btnlp);
                            btnAyuda.setMaxHeight(75);
                            btnAyuda.setForegroundGravity(GRAVITY_CENTER);
                            btnlp.gravity = Gravity.CENTER;

                            //TooltipCompat.setTooltipText(btnAyuda, campos.get(i).get("tooltip"));
                            ToolTipsManager mToolTipsManager = new ToolTipsManager();
                            final ToolTip.Builder builder = new ToolTip.Builder(getContext(), et, (RelativeLayout)_ll.getParent() ,  campos.get(i).get("tooltip").toString(), ToolTip.POSITION_ABOVE);
                            builder.setAlign(ToolTip.ALIGN_LEFT);

                            builder.setGravity(ToolTip.GRAVITY_LEFT);
                            builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                            btnAyuda.setOnLongClickListener(view -> {
                                mToolTipsManager.show(builder.build());
                                return true;
                            });

                        }
                        if (campos.get(i).get("dfaul").trim().length() > 0) {
                            et.setText(campos.get(i).get("dfaul").trim());
                        }
                        //Le cae encima al valor default por el de la solicitud seleccionada
                        if (solicitudSeleccionada.size() > 0) {
                            et.setText(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim());
                            if (!modificable) {
                                et.setEnabled(false);
                                et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                            }
                        }
                        //metodos configurados en tabla
                        if (campos.get(i).get("llamado1").trim().contains("ReplicarValor")) {
                            String[] split = campos.get(i).get("llamado1").trim().split("'");
                            if (split.length < 3)
                                split = campos.get(i).get("llamado1").trim().split("`");
                            if (split.length < 3)
                                split = campos.get(i).get("llamado1").trim().split("\"");
                            final String campoAReplicar = split[1];
                            if (!campos.get(i).get("campo").trim().equals("W_CTE-NAME1") && !campos.get(i).get("campo").trim().equals("W_CTE-NAME2") && !campos.get(i).get("campo").trim().equals("W_CTE-HOUSE_NUM1")) {
                                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (!hasFocus) {
                                            ReplicarValor(v, campoAReplicar);
                                        }
                                    }
                                });
                                final boolean[] isUpdating = {false};
                                et.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        if (!isUpdating[0]) {
                                            isUpdating[0] = true;
                                            // Replicate value in targetEditText
                                            int cursorPosition = et.getSelectionStart(); // Save cursor position
                                            ReplicarValor(et, campoAReplicar);
                                            et.setSelection(Math.min(cursorPosition, et.length()));

                                            isUpdating[0] = false;
                                        }

                                    }
                                });

                            }
                        }
                        //Crear campo para valor viejo exclusivo. ??
                        if (campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("10")) {
                            Button btnAyudai = null;
                            TableRow.LayoutParams textolp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                            TableRow.LayoutParams btnlp2 = new TableRow.LayoutParams(75, 75);
                            textolp2.setMargins(0, 0, 5, 0);
                            label.setLayoutParams(textolp2);
                            btnAyudai = new Button(getContext());
                            btnAyudai.setBackground(getResources().getDrawable(R.drawable.icon_ver_viejo, null));
                            btnlp2.setMargins(0, 35, 5, 0);
                            btnAyudai.setLayoutParams(btnlp2);
                            btnAyudai.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            btnAyudai.setForegroundGravity(GRAVITY_CENTER);
                            btnAyudai.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        label.setVisibility(View.GONE);
                                        et.setVisibility(View.GONE);
                                        return true;
                                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                        label.setVisibility(View.VISIBLE);
                                        et.setVisibility(View.VISIBLE);
                                        return true;
                                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                                        label.setVisibility(View.VISIBLE);
                                        et.setVisibility(View.VISIBLE);
                                        return true;
                                    }

                                    // TODO Auto-generated method stub
                                    return false;
                                }
                            });
                            if (btnAyudai != null)
                                fila.addView(btnAyudai);
                        }

                        if(campos.get(i).get("nombre") != null && campos.get(i).get("nombre").toLowerCase().contains("verificarcelular") && modificable){
                            verificarCelular = new ImageView(getContext());
                            if(campos.get(i).get("nombre").toLowerCase().contains("opc"))
                                verificarCelular.setTag("Opcional");
                            label.setLayoutParams(textolp);
                            verificarCelular.setBackground(getResources().getDrawable(R.drawable.verifiy_phone,null));
                            if(solicitudSeleccionada.size() > 0 && (solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia") || solicitudSeleccionada.get(0).get("ESTADO").equals("Incompleto") || solicitudSeleccionada.get(0).get("ESTADO").equals("Modificado")))
                                verificarCelular.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.aprobados,null)));
                            else
                                verificarCelular.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red,null)));
                            // btnlp.setMargins(0,35,5,0);
                            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                            int marginEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                            btnlp.setMargins(0,marginTop,marginEnd,0);
                            verificarCelular.setLayoutParams(btnlp);
                            verificarCelular.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            verificarCelular.setForegroundGravity(GRAVITY_CENTER);
                            //TooltipCompat.setTooltipText(verificarCelular, campos.get(i).get("tooltip"));
                            ToolTipsManager mToolTipsManager = new ToolTipsManager();
                            ToolTip.Builder builder = new ToolTip.Builder(getContext(), et, (RelativeLayout)_ll.getParent() ,  "Presione para verificar el número de celular", ToolTip.POSITION_ABOVE);
                            builder.setAlign(ToolTip.ALIGN_LEFT);
                            //builder.setBackgroundColor(getResources().getColor(R.color.gray,null));
                            builder.setGravity(ToolTip.GRAVITY_LEFT);
                            builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                            int finalI2 = i;
                            ImageView finalBtnAyuda = verificarCelular;
                            verificarCelular.setOnClickListener((View.OnClickListener) view -> {
                                String bukrs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad());
                                if(isValidPhoneNumber(et.getText().toString(),bukrs,getContext())){
                                    WeakReference<Context> weakRefs1 = new WeakReference<Context>(getContext());
                                    WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(getActivity());
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("tipo_conexion","").equals("api")) {
                                        GenerarCodigoVerificacionAPI v = new GenerarCodigoVerificacionAPI(weakRefs1, weakRefAs1, bukrs, "0", et.getText().toString(), finalBtnAyuda,sentPI,deliveredPI);
                                        v.execute();
                                    } else {
                                        GenerarCodigoVerificacionServidor v = new GenerarCodigoVerificacionServidor(weakRefs1, weakRefAs1, bukrs, "0", et.getText().toString(), finalBtnAyuda,sentPI,deliveredPI);
                                        v.execute();
                                    }
                                }else{
                                    Toasty.warning(getContext(),"El numero '"+et.getText().toString()+"' no es válido.").show();
                                }
                            });
                            verificarCelular.setOnLongClickListener((View.OnLongClickListener) view -> {
                                mToolTipsManager.show(builder.build());
                                return true;
                            });
                            ImageView finalBtnAyuda1 = verificarCelular;
                            et.addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {
                                    //Restuarar icono de varificacion de numero celular
                                    finalBtnAyuda1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red, null)));
                                    finalBtnAyuda1.setOnClickListener((View.OnClickListener) view -> {
                                        String bukrs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad());
                                        if(isValidPhoneNumber(et.getText().toString(),bukrs,getContext())){
                                            WeakReference<Context> weakRefs1 = new WeakReference<Context>(getContext());
                                            WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(getActivity());
                                            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("tipo_conexion","").equals("api")) {
                                                GenerarCodigoVerificacionAPI v = new GenerarCodigoVerificacionAPI(weakRefs1, weakRefAs1, bukrs, "0", et.getText().toString(), finalBtnAyuda,sentPI,deliveredPI);
                                                v.execute();
                                            } else {
                                                GenerarCodigoVerificacionServidor v = new GenerarCodigoVerificacionServidor(weakRefs1, weakRefAs1, bukrs, "0", et.getText().toString(), finalBtnAyuda,sentPI,deliveredPI);
                                                v.execute();
                                            }
                                        }else{
                                            Toasty.warning(getContext(),"El numero '"+et.getText().toString()+"' no es válido.").show();
                                        }
                                    });
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                            });
                        }

                        TableRow.LayoutParams lp_at = null;
                        if (campos.get(i).get("campo").trim().equals("W_CTE-SMTP_ADDR") && VariablesGlobales.getSociedad().equals("F428")) {
                            //Para COLOMBIA se debe agregar un select para seleccinar el despues del arroba
                            atCorreo = new SearchableSpinner(getContext());
                            int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125, getResources().getDisplayMetrics());
                            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, getResources().getDisplayMetrics());
                            ArrayList<OpcionSpinner> opciones = db.getDatosCatalogoParaSpinner("cat_dominios");
                            TableRow.LayoutParams textolp_at = new TableRow.LayoutParams(MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,5.0f);
                            textolp_at.setMargins(0, 0, marginLeft, 0);
                            et.setLayoutParams(textolp_at);

                            lp_at = new TableRow.LayoutParams(marginLeft, WRAP_CONTENT,5.0f);
                            lp_at.setMargins(-marginLeft, marginTop, 0, 0);

                            atCorreo.setLayoutParams(lp_at);
                            // Creando el adaptador(opciones) para el comboBox deseado
                            ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item_mini, opciones);
                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                            // attaching data adapter to spinner
                            Drawable dat = getResources().getDrawable(R.drawable.spinner_background_cont, null);
                            atCorreo.setBackground(dat);
                            atCorreo.setAdapter(dataAdapter);
                            if(solicitudSeleccionada.size() >0) {
                                String correo_original = solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim();
                                String[] partes_correo = correo_original.split("@");
                                if (atCorreo != null) {
                                    //Armar el valor del correo segun la escogencia de dominio
                                    int index_at = partes_correo.length > 1 ? VariablesGlobales.getIndex(atCorreo,"@" + partes_correo[1]):-1;
                                    if (index_at != -1) {//Significa que si existe
                                        et.setText(partes_correo[0]);
                                        atCorreo.setSelection(index_at);
                                    } else {
                                        atCorreo.setSelection(VariablesGlobales.getIndex(atCorreo, "Otros"));
                                    }
                                }
                            }
                            atCorreo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                    String correoOriginal = et.getText().toString();

                                    if (correoOriginal.contains("@") && opcion != null && opcion.getId().contains("@")) {
                                        String parteAntesDelArroba = correoOriginal.substring(0, correoOriginal.indexOf("@"));
                                        et.setText(parteAntesDelArroba); // Quita el dominio
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    // No hacer nada
                                }
                            });
                        }
                        if(campos.get(i).get("nombre") != null && campos.get(i).get("nombre").toLowerCase().contains("verificarcorreo") && modificable) {
                            verificarCorreo = new ImageView(getContext());
                            if(campos.get(i).get("nombre").toLowerCase().contains("opc"))
                                verificarCorreo.setTag("Opcional");
                            int marginRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
                            int marginEnd = 0;
                            if(atCorreo != null){
                                int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, getResources().getDisplayMetrics());
                                marginEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                                TableRow.LayoutParams textolp_at = new TableRow.LayoutParams(MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,7.0f);
                                int marginFill = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                                textolp_at.setMargins(0, 0, marginRight, 0);
                                et.setLayoutParams(textolp_at);

                                lp_at = new TableRow.LayoutParams(marginLeft-marginEnd+marginFill, WRAP_CONTENT,3.0f);
                                int top_at = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, getResources().getDisplayMetrics());
                                lp_at.setMargins(-marginLeft, -top_at, marginRight+marginFill, 0);

                                btnlp.setMargins(-marginLeft-5, marginTop, 25, 0);
                                atCorreo.setLayoutParams(lp_at);
                                // Wait until the layout is drawn to get the height of the MaskedEditText
                                et.post(() -> {
                                    // Get the height of the MaskedEditText
                                    int height = et.getHeight();

                                    // Set the height of the Spinner programmatically
                                    atCorreo.getLayoutParams().height = height;
                                    atCorreo.requestLayout(); // Ensure the change takes effect
                                });
                            }else{
                                int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                                TableRow.LayoutParams textolp_at = new TableRow.LayoutParams(MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,9.0f);
                                marginEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
                                textolp_at.setMargins(0, 0, marginRight, 0);
                                btnlp.setMargins(0,marginTop,marginEnd,0);
                            }

                            label.setLayoutParams(textolp);
                            verificarCorreo.setBackground(getResources().getDrawable(R.drawable.verify_mail, null));

                            if(solicitudSeleccionada.size() > 0 && (solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia") || solicitudSeleccionada.get(0).get("ESTADO").equals("Incompleto") || solicitudSeleccionada.get(0).get("ESTADO").equals("Modificado")))
                                verificarCorreo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.aprobados,null)));
                            else
                                verificarCorreo.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red,null)));
                            // btnlp.setMargins(0,35,5,0);
                            verificarCorreo.setLayoutParams(btnlp);
                            verificarCorreo.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            verificarCorreo.setForegroundGravity(GRAVITY_CENTER);
                            //TooltipCompat.setTooltipText(verificarCorreo, campos.get(i).get("tooltip"));
                            ToolTipsManager mToolTipsManager = new ToolTipsManager();
                            ToolTip.Builder builder = new ToolTip.Builder(getContext(), et, (RelativeLayout) _ll.getParent(), "Presione para verificar el número de celular", ToolTip.POSITION_ABOVE);
                            builder.setAlign(ToolTip.ALIGN_LEFT);
                            //builder.setBackgroundColor(getResources().getColor(R.color.gray,null));
                            builder.setGravity(ToolTip.GRAVITY_LEFT);
                            builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                            int finalI2 = i;
                            ImageView finalBtnAyuda = verificarCorreo;
                            Spinner finalAtCorreo1 = atCorreo;
                            verificarCorreo.setOnClickListener((View.OnClickListener) view -> {
                                String bukrs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad());
                                String correo_armado = et.getText().toString();
                                if(finalAtCorreo1 != null){
                                    //Armar el valor del correo segun la escogencia de dominio
                                    if(!((OpcionSpinner) finalAtCorreo1.getSelectedItem()).getId().toString().equals("Otros")){
                                        correo_armado = ((TextView)et).getText().toString()+((OpcionSpinner)finalAtCorreo1.getSelectedItem()).getId().toString();
                                    }
                                }
                                if (Validaciones.isValidEmail(correo_armado)) {
                                    WeakReference<Context> weakRefs1 = new WeakReference<Context>(getContext());
                                    WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(getActivity());
                                    if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("tipo_conexion", "").equals("api")) {
                                        GenerarCodigoVerificacionCorreoAPI v = new GenerarCodigoVerificacionCorreoAPI(weakRefs1, weakRefAs1, bukrs, "0", correo_armado, finalBtnAyuda);
                                        v.execute();
                                    } else {
                                        GenerarCodigoVerificacionCorreoServidor v = new GenerarCodigoVerificacionCorreoServidor(weakRefs1, weakRefAs1, bukrs, "0", correo_armado, finalBtnAyuda);
                                        v.execute();
                                    }
                                } else {
                                    Toasty.warning(getContext(), "El correo '" + correo_armado + "' no es válido.").show();
                                }
                            });
                            verificarCorreo.setOnLongClickListener((View.OnLongClickListener) view -> {
                                mToolTipsManager.show(builder.build());
                                return true;
                            });
                            ImageView finalBtnAyuda1 = verificarCorreo;
                            et.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    //Restuarar icono de verificacion de correo
                                    finalBtnAyuda1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.rechazado, null)));
                                    finalBtnAyuda1.setOnClickListener((View.OnClickListener) view -> {
                                        String bukrs = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad());
                                        String correo_armado = et.getText().toString();
                                        if(finalAtCorreo1 != null){
                                            //Armar el valor del correo segun la escogencia de dominio
                                            if(!((OpcionSpinner) finalAtCorreo1.getSelectedItem()).getId().toString().equals("Otros")){
                                                correo_armado = ((TextView)et).getText().toString()+((OpcionSpinner)finalAtCorreo1.getSelectedItem()).getId().toString();
                                            }
                                        }
                                        if (Validaciones.isValidEmail(correo_armado)) {
                                            WeakReference<Context> weakRefs1 = new WeakReference<Context>(getContext());
                                            WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(getActivity());
                                            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getString("tipo_conexion", "").equals("api")) {
                                                GenerarCodigoVerificacionCorreoAPI v = new GenerarCodigoVerificacionCorreoAPI(weakRefs1, weakRefAs1, bukrs, "0", correo_armado, finalBtnAyuda);
                                                v.execute();
                                            } else {
                                                GenerarCodigoVerificacionCorreoServidor v = new GenerarCodigoVerificacionCorreoServidor(weakRefs1, weakRefAs1, bukrs, "0", correo_armado, finalBtnAyuda);
                                                v.execute();
                                            }
                                        } else {
                                            Toasty.warning(getContext(), "El correo '" + correo_armado + "' no es válido.").show();
                                        }
                                    });
                                    //Si pone el caracter arroba, automaticamente pone la opcion de OTROS
                                    if (s.toString().contains("@")) {
                                        // Programmatically select an option on the Spinner
                                        atCorreo.setSelection(VariablesGlobales.getIndex(atCorreo,"Otros")); // Change index based on the desired option
                                    }else{
                                        if(((OpcionSpinner) atCorreo.getSelectedItem()).getId().toString().equals("Otros"))
                                        {
                                            atCorreo.setSelection(VariablesGlobales.getIndex(atCorreo,""));
                                        }
                                    }
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }

                            });
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-STREET") && VariablesGlobales.getSociedad().equals("F428")) {
                            prefijo_direccion = new SearchableSpinner(getContext());
                            //Button btnAyudai = null;
                            //TableRow.LayoutParams textolp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                            //TableRow.LayoutParams btnlp2 = new TableRow.LayoutParams(75, 75);
                            int marginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 125, getResources().getDisplayMetrics());
                            int marginEnd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics());

                            int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13.0f, getResources().getDisplayMetrics());
                            TableRow.LayoutParams textolp2 = new TableRow.LayoutParams(MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,5.0f);
                            textolp2.setMargins(0, 0, -marginLeft, 0);
                            et.setLayoutParams(textolp2);

                            TableRow.LayoutParams btnlp2 = new TableRow.LayoutParams(marginLeft-marginEnd, WRAP_CONTENT,5.0f);
                            textolp2.setMargins(0, 0, 0, 0);
                            label.setLayoutParams(textolp2);
                            btnlp2.setMargins(0, marginTop, 0, 0);
                            prefijo_direccion.setLayoutParams(btnlp2);
                            prefijo_direccion.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            prefijo_direccion.setForegroundGravity(GRAVITY_CENTER);

                            ArrayList<OpcionSpinner> opciones = db.getDatosCatalogoParaSpinner("cat_direcciones_dian");

                            prefijo_direccion.setLayoutParams(btnlp2);
                            // Creando el adaptador(opciones) para el comboBox deseado
                            ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, opciones);
                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                            // attaching data adapter to spinner
                            Drawable dat = getResources().getDrawable(R.drawable.spinner_background_pre, null);
                            prefijo_direccion.setBackground(dat);
                            prefijo_direccion.setAdapter(dataAdapter);

                            if (prefijo_direccion != null) {
                                fila.addView(prefijo_direccion);
                                TableRow.LayoutParams filalp = new TableRow.LayoutParams(MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,10.0f);
                                filalp.setMargins(0,-marginTop,0,0);
                                fila.setLayoutParams(filalp);
                            }
                            if(solicitudSeleccionada.size() > 0) {
                                String direccion_original = solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim();
                                String[] partes_direccion = direccion_original.split(" ",2);
                                if (prefijo_direccion != null) {
                                    //Armar el valor de la direcccion DIAN COLOMBIA
                                    int index_at = partes_direccion.length > 1 ? VariablesGlobales.getIndex(prefijo_direccion,partes_direccion[0]):-1;
                                    if (index_at != -1) {//Significa que si existe
                                        et.setText(partes_direccion[1]);
                                        prefijo_direccion.setSelection(index_at);
                                    }
                                }
                            }
                            et.post(() -> {
                                // Get the height of the MaskedEditText
                                int height = et.getHeight();

                                // Set the height of the Spinner programmatically
                                prefijo_direccion.getLayoutParams().height = height;
                                prefijo_direccion.requestLayout(); // Ensure the change takes effect
                            });
                        }



                        label.addView(et);
                        fila.addView(label);
                        if(atCorreo != null && atCorreo.getParent() == null)
                            fila.addView(atCorreo);
                        if (btnAyuda != null)
                            fila.addView(btnAyuda);
                        if (verificarCelular != null && verificarCelular.getParent() == null)
                            fila.addView(verificarCelular);
                        if (verificarCorreo != null && verificarCorreo.getParent() == null)
                            fila.addView(verificarCorreo);
                        ll.addView(fila);


                        if (campos.get(i).get("campo").trim().equals("W_CTE-ZZCRMA_LAT") || campos.get(i).get("campo").trim().equals("W_CTE-ZZCRMA_LONG")) {
                            Drawable leftIcon = getResources().getDrawable(R.drawable.icon_location, null);
                            Drawable rightIcon = null;

                            if (campos.get(i).get("nombre").trim().equals("mapa")) {
                                rightIcon = getResources().getDrawable(R.drawable.map, null);
                                rightIconLocation = rightIcon;
                            }

                            et.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, rightIcon, null);

                            et.setCompoundDrawablePadding(16);
                            et.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                            et.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    final int DRAWABLE_LEFT = 0;
                                    final int DRAWABLE_TOP = 1;
                                    final int DRAWABLE_RIGHT = 2;
                                    final int DRAWABLE_BOTTOM = 3;

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        float touchX = event.getX();

                                        Drawable leftDrawable = et.getCompoundDrawables()[DRAWABLE_LEFT];
                                        Drawable rightDrawable = et.getCompoundDrawables()[DRAWABLE_RIGHT];

                                        if (leftDrawable != null) {
                                            int leftWidth = leftDrawable.getBounds().width();
                                            if (touchX <= et.getPaddingLeft() + leftWidth) {
                                                // LEFT icon clicked
                                                Toasty.info(getContext(), "Refrescando ubicación...").show();
                                                LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(),
                                                        (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"),
                                                        (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG")
                                                );
                                                autoPineo.startLocationUpdates();
                                                return true;
                                            }
                                        }

                                        if (rightDrawable != null) {
                                            Drawable expectedDrawable = ContextCompat.getDrawable(getContext(), R.drawable.map); // el ícono que esperás

                                            if (expectedDrawable != null && rightDrawable.getConstantState() != null && rightDrawable.getConstantState().equals(expectedDrawable.getConstantState())) {
                                                int rightWidth = rightDrawable.getBounds().width();
                                                if (touchX >= (et.getWidth() - et.getPaddingRight() - rightWidth)) {
                                                    // RIGHT icon clicked
                                                    Intent intent = new Intent(getContext(), OSMPickerActivity.class);
                                                    MaskedEditText met_lat = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT");
                                                    MaskedEditText met_long = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG");
                                                    if(Validaciones.ValidarCoordenadaX(met_long) && Validaciones.ValidarCoordenadaY(met_lat)){
                                                        intent.putExtra(OSMPickerActivity.EXTRA_LATITUDE, Double.parseDouble(met_lat.getText().toString()));
                                                        intent.putExtra(OSMPickerActivity.EXTRA_LONGITUDE, Double.parseDouble(met_long.getText().toString()));
                                                    }
                                                    getActivity().startActivityForResult(intent, VariablesGlobales.REQUEST_CODE_MAP);
                                                    return true;
                                                }
                                            } else {
                                                // ✅ User tapped the error icon, let the popup show
                                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                    et.setError(null); // remove error
                                                    et.setCompoundDrawablesWithIntrinsicBounds(
                                                            leftIcon, null, rightIconLocation, null
                                                    );
                                                }, 3000); // Delay allows popup to show first

                                                return false; // block custom icon click this time
                                            }
                                        }
                                    }

                                    return false;
                                }
                            });
                            et.setText(et.getText().toString().replace(",", "."));
                        }

                        if (campos.get(i).get("campo").trim().equals("W_CTE-COMENTARIOS")) {
                            et.setSingleLine(false);
                            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                            et.setMinLines(1);
                            et.setMaxLines(5);
                            et.setVerticalScrollBarEnabled(true);
                            et.setMovementMethod(ScrollingMovementMethod.getInstance());
                            et.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                            et.setGravity(INDICATOR_GRAVITY_TOP);


                            if (solicitudSeleccionada.size() > 0 && (!solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Nuevo") && !solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Incompleto"))) {
                                et.setText("");
                                RelativeLayout rl = new RelativeLayout(getContext());
                                rl.setVerticalScrollBarEnabled(true);
                                rl.startNestedScroll(1);

                                CoordinatorLayout.LayoutParams rlp = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                rlp.setBehavior(new AppBarLayout.ScrollingViewBehavior(getContext(), null));

                                rl.setLayoutParams(rlp);
                                rl.requestLayout();

                                tb_comentarios.setColumnCount(4);
                                tb_comentarios.setHeaderBackgroundColor(getResources().getColor(R.color.colorHeaderTableView, null));
                                tb_comentarios.setHeaderElevation(2);
                                LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                tb_comentarios.setLayoutParams(hlp);

                                if (solicitudSeleccionada.size() > 0) {
                                    comentarios.clear();
                                    comentarios = mDBHelper.getComentariosDB(idForm);
                                }
                                //Adaptadores
                                if (comentarios != null) {
                                    tb_comentarios.getLayoutParams().height = tb_comentarios.getLayoutParams().height + (comentarios.size() * alturaFilaTableView * 2);
                                    tb_comentarios.setDataAdapter(new ComentarioTableAdapter(getContext(), comentarios));
                                }
                                String[] headers = ((ComentarioTableAdapter) tb_comentarios.getDataAdapter()).getHeaders();
                                SimpleTableHeaderAdapter sta = new SimpleTableHeaderAdapter(getContext(), headers);
                                sta.setPaddings(5, 15, 5, 15);
                                sta.setTextSize(12);
                                sta.setTextColor(getResources().getColor(R.color.white, null));
                                sta.setTypeface(Typeface.BOLD);
                                sta.setGravity(GRAVITY_CENTER);

                                tb_comentarios.setHeaderAdapter(sta);
                                tb_comentarios.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(getResources().getColor(R.color.white, null), getResources().getColor(R.color.backColor, null)));

                                //Necesario para el nested scrolling del tableview
                                final List<View> tocables = tb_comentarios.getFocusables(View.FOCUS_FORWARD);
                                for (int x = 0; x < tocables.size(); x++) {
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
                        if (campos.get(i).get("campo").trim().equals("W_CTE-DATAB")) {
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String fechaSistema = df.format(c);
                            et.setText(fechaSistema);
                        }
                        if (campos.get(i).get("campo").trim().equals("W_CTE-SMTP_ADDR")) {
                            Spinner finalAtCorreo = atCorreo;
                            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        if(finalAtCorreo == null){
                                            correoValidado = Validaciones.isValidEmail(v);
                                        }else{
                                            //Armar el valor del correo segun la escogencia de dominio
                                            if(((OpcionSpinner)finalAtCorreo.getSelectedItem()).getId().toString().equals("Otros"))
                                                correoValidado = Validaciones.isValidEmail(v);
                                            else if(!((OpcionSpinner)finalAtCorreo.getSelectedItem()).getId().toString().equals("")){
                                                String correo_armado = ((TextView)v).getText().toString()+((OpcionSpinner)finalAtCorreo.getSelectedItem()).getId().toString();
                                                correoValidado = Validaciones.isValidEmail(correo_armado,true,getContext());
                                            }

                                        }
                                    }
                                }
                            });
                        }
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), et);
                        if (campos.get(i).get("obl") != null && campos.get(i).get("obl").trim().length() > 0) {
                            listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                            if (campos.get(i).get("dfaul").trim().length() == 0) {
                                et.setError(getResources().getString(R.string.error_field_required));
                            }
                        }
                        if (campos.get(i).get("tabla_local") != null && campos.get(i).get("tabla_local").trim().length() > 0) {
                            listaCamposBloque.add(campos.get(i).get("campo").trim());
                        }

                        //Excepciones de visualizacion y configuracionde campos dados por la tabla ConfigCampos
                        int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                        if (excepcion >= 0 && !campos.get(i).get("campo").trim().equals("W_CTE-RUTAHH")) {
                            HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                            Validaciones.ejecutarExcepcion(getContext(),et,label,configExcepcion,listaCamposObligatorios,campos.get(i));

                            int excepcionxAgencia = -1;
                            if(mapeoCamposDinamicos.get("W_CTE-BZIRK") != null)
                                excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                            if (excepcionxAgencia >= 0) {
                                HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                                Validaciones.ejecutarExcepcion(getContext(),et,label,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i));
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
                if(!db.getModeloSolicitud(tipoSolicitud).equals("P")) {
                    TextView label = new TextView(getContext());
                    label.setText("Enviar al Aprobador");
                    label.setTextAppearance(R.style.AppTheme_TextFloatLabelAppearance);
                    LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpl.setMargins(35, 5, 0, 0);
                    label.setPadding(0, 0, 0, 0);
                    label.setLayoutParams(lpl);

                    final SearchableSpinner combo = new SearchableSpinner(getContext());
                    combo.setTag("Aprobador");

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, -10, 0, 25);
                    combo.setPadding(0, 0, 0, 0);
                    combo.setLayoutParams(lp);
                    combo.setPopupBackgroundResource(R.drawable.menu_item);

                    String id_flujo = db.getIdFlujoDeTipoSolicitud(tipoSolicitud);

                    ArrayList<OpcionSpinner> opciones = db.getDatosCatalogoParaSpinner("aprobadores", " fxp.id_flujo = " + id_flujo);

                    // Creando el adaptador(opciones) para el comboBox deseado
                    ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, opciones);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
                    combo.setBackground(d);
                    combo.setAdapter(dataAdapter);
                    if (solicitudSeleccionada.size() == 0) {
                        if (dataAdapter.getCount() > 1) {
                            combo.setSelection(0);
                        } else {
                            combo.setSelection(0);
                        }
                        setErrorWithTooltipOnTouch(combo, getResources().getString(R.string.error_field_required));
                    } else {
                        combo.setSelection(VariablesGlobales.getIndex(combo, solicitudSeleccionada.get(0).get("SIGUIENTE_APROBADOR").toString().trim()));
                        if (!solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Nuevo") && !solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Incidencia") && !solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Modificado")) {
                            combo.setEnabled(false);
                        }
                    }
                    mapeoCamposDinamicos.put("SIGUIENTE_APROBADOR", combo);
                    ll.addView(label);
                    ll.addView(combo);
                }

                if(tipoSolicitud.equals("1") || tipoSolicitud.equals("6")) {
                    //Check Box para la aceptacion de las politicas de privacidad
                    final CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText("Aceptar Politicas de Privacidad");
                    if (solicitudSeleccionada.size() > 0) {
                        checkbox.setChecked(true);
                        checkbox.setEnabled(false);
                        if (!modificable) {
                            checkbox.setEnabled(false);
                        }
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
            }
        }

        public final static boolean isValidPhoneNumber(String v, String bukrs, Context context) {
            boolean retorno = false;
            String codigoPais = "";
            Pattern PHONE = Pattern.compile(                      // sdd = space, dot, or dash
                    "(\\+[0-9]+[\\- \\.]*)?"        // +<digits><sdd>*
                            + "(\\([0-9]+\\)[\\- \\.]*)?"   // (<digits>)<sdd>*
                            + "([0-9][0-9\\- \\.]+[0-9])");
            switch(bukrs){
                case "F443":
                    codigoPais = "+506";
                case "F445":
                    codigoPais = "+505";
                case "F446":
                    codigoPais = "+52";
                case "F451":
                    codigoPais = "+507";
                case "1657":
                    codigoPais = "+52";
                case "1658":
                    codigoPais = "+52";
                case "1661":
                    codigoPais = "+598";
                case "Z001":
                    codigoPais = "+598";
                    break;
                case "F428":
                    codigoPais = "+57";
                    break;
                case "ARG":
                    codigoPais = "+54";
                    break;
                default:
                    codigoPais = "+506";
                    return true;

            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("CODIGO_PAIS",codigoPais).apply();
            String numero = (String)v;
            boolean valido = !TextUtils.isEmpty(numero) && PHONE.matcher(codigoPais+numero).matches();
            return valido;
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
            SearchableSpinner aprobadores = (SearchableSpinner) mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR");
            //aprobadores.setBackground(d);
            if(aprobadores != null) {
                aprobadores.setAdapter(dataAdapter);
                if(solicitudSeleccionada.size() > 0){
                    aprobadores.setSelection(VariablesGlobales.getIndex(aprobadores,solicitudSeleccionada.get(0).get("SIGUIENTE_APROBADOR").toString().trim()));
                }
                if (aprobadores.getSelectedItemPosition() == 0)
                    setErrorWithTooltipOnTouch(aprobadores, getResources().getString(R.string.error_field_required));
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

        public void DesplegarBloque(DataBaseHelper db, View _ll, HashMap<String, String> campo) {
            int height = 50;
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
            seccion_header.setTextSize(14);
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

            switch(campo.get("campo").trim()) {
                case "W_CTE-CONTACTOS":
                    //bloque_contacto = tb_contactos;
                    if (modificable) {
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
                    tb_contactos.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

                    tb_contactos.setLayoutParams(hlp);

                    if (solicitudSeleccionada.size() > 0) {
                        contactosSolicitud = mDBHelper.getContactosDB(idSolicitud);
                    }
                    //Adaptadores
                    if (contactosSolicitud != null) {
                        ContactoTableAdapter stda = new ContactoTableAdapter(getContext(), contactosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_contactos.setDataAdapter(stda);
                        tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height + (contactosSolicitud.size() * (alturaFilaTableView));
                    }

                    headers = ((ContactoTableAdapter) tb_contactos.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10, 5, 10, 5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white, null));
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
                    //if(bloque_impuesto.getParent() != null) {
                    bloque_impuesto.setColumnCount(4);
                    bloque_impuesto.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    bloque_impuesto.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_impuesto.setLayoutParams(hlp);

                    ArrayList<Impuesto> listaImpuestos = db.getImpuestosPais();
                    impuestosSolicitud.addAll(listaImpuestos);
                    if (solicitudSeleccionada.size() > 0) {
                        impuestosSolicitud.clear();
                        impuestosSolicitud = mDBHelper.getImpuestosDB(idSolicitud);
                    }
                    //Adaptadores
                    if (impuestosSolicitud != null) {
                        tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(getContext(), impuestosSolicitud));
                        tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height + (impuestosSolicitud.size() * (alturaFilaTableView));
                    }
                    headers = ((ImpuestoTableAdapter) bloque_impuesto.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10, 5, 10, 5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white, null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    bloque_impuesto.setHeaderAdapter(sta);
                    bloque_impuesto.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    rl.addView(bloque_impuesto);
                    ll.addView(rl);
                    //}
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
                    });
                    seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogInterlocutor(getContext(),null);
                        }
                    });*/
                    bloque_interlocutor.setColumnCount(3);
                    bloque_interlocutor.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    bloque_interlocutor.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_interlocutor.setLayoutParams(hlp);


                    ArrayList<Interlocutor> listaInterlocutores = db.getInterlocutoresPais();
                    interlocutoresSolicitud.addAll(listaInterlocutores);
                    if (solicitudSeleccionada.size() > 0) {
                        interlocutoresSolicitud.clear();
                        interlocutoresSolicitud = mDBHelper.getInterlocutoresDB(idSolicitud);
                    }
                    //Adaptadores
                    if (interlocutoresSolicitud != null) {
                        if (tipoSolicitud.equals("1") || tipoSolicitud.equals("6")) {
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height + (interlocutoresSolicitud.size() * alturaFilaTableView);
                            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(getContext(), interlocutoresSolicitud));
                        }
                    }
                    headers = ((InterlocutorTableAdapter) bloque_interlocutor.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10, 5, 10, 5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white, null));
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
                    if (modificable) {
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
                    bloque_banco.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    bloque_banco.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_banco.setLayoutParams(hlp);

                    if (solicitudSeleccionada.size() > 0) {
                        bancosSolicitud = mDBHelper.getBancosDB(idSolicitud);
                    }else{
                        if(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("1661")
                        || PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("Z001")) {
                            //var datosBancosUY = [{ "bankl": "'001'", "banks": "'UY'", "bankn": "001", "koinh": "IGUAL A RAZÓN SOCIAL", "bkref": "CHEQUE AL DIA", "bkont": "CJ" }];
                            Banco bancoDefaultUY = new Banco("0", "0", GUID, "001", "UY", "001", "CJ", "IGUAL A RAZON SOCIAL", "", "CHEQUE AL DIA", "");
                            bancosSolicitud.add(bancoDefaultUY);
                        }
                    }


                    //Adaptadores
                    if (bancosSolicitud != null) {
                        BancoTableAdapter stda = new BancoTableAdapter(getContext(), bancosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        bloque_banco.setDataAdapter(stda);
                        tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height + (bancosSolicitud.size() * alturaFilaTableView);
                    }
                    headers = ((BancoTableAdapter) bloque_banco.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10, 5, 10, 5);
                    sta.setTextSize(12);
                    sta.setTextColor(getResources().getColor(R.color.white, null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    bloque_banco.setHeaderAdapter(sta);
                    bloque_banco.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    rl.addView(bloque_banco);
                    ll.addView(rl);
                    break;
                case "W_CTE-VISITAS":
                    tb_visitas.setColumnCount(4);
                    tb_visitas.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    tb_visitas.setHeaderElevation(1);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    tb_visitas.setLayoutParams(hlp);
                    btnAddBloque.setVisibility(INVISIBLE);

                    Button btnRefreshBloque = new Button(getContext());
                    LinearLayout.LayoutParams tam_btnv = new LinearLayout.LayoutParams(30, 30);

                    btnRefreshBloque.setLayoutParams(tam_btnv);
                    btnRefreshBloque.setBackground(getResources().getDrawable(R.drawable.icon_refresh, null));

                    seccion_layout.addView(btnRefreshBloque);
                    if (modificable) {
                        btnRefreshBloque.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                DialogHandler appdialog = new DialogHandler();
                                appdialog.Confirm(getActivity(), "Refrescar", "Está seguro que desea reinicializar los tipos de visita del cliente? Debe volver a configurar todo el VP.", "NO", "SI", new SolicitudActivity.ResetearVisitas(getContext(), getActivity()));
                                //return false;
                            }
                        });
                    }

                    if (solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-KVGR5").toString().equals( ((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-KVGR5")).getSelectedItem()).getId() )) {
                        visitasSolicitud = mDBHelper.getVisitasDB(idSolicitud);
                    }
                    //Adaptadores
                    if (visitasSolicitud != null) {
                        VisitasTableAdapter stda = new VisitasTableAdapter(getContext(), getActivity(), visitasSolicitud,modificable);
                        stda.setPaddings(10, 15, 10, 15);
                        stda.setTextSize(16);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + (visitasSolicitud.size() * alturaFilaTableView);
                        tb_visitas.setDataAdapter(stda);
                        tb_visitas.getDataAdapter().notifyDataSetChanged();
                    }
                    headers = ((VisitasTableAdapter) tb_visitas.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10, 5, 10, 5);
                    sta.setTextSize(14);
                    sta.setTextColor(getResources().getColor(R.color.white, null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    tb_visitas.setHeaderAdapter(sta);
                    tb_visitas.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
                    //rl.addView(tb_visitas);
                    ll_visitas.addView(tb_visitas);
                    //Textos de dias de visita
                    //Desplegar textos para las secuencias de los dias de visita de la preventa.
                    //TODO Generar segun la Modalidad de venta seleccionada para usuarios tipo jefe de ventas y no preventas
                    final String[] diaLabel = {"L", "K", "M", "J", "V", "S", "D"};
                    final String[] diaLabelVisible = {"L", "M", "R", "J", "V", "S", "D"};
                    Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                    String modalidad = "";
                    String tipoVisita = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "ZPV").toString();//"ZPV";
                    modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();

                    int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPV");
                    int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZJV");
                    int indiceKafe = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZKV");
                    int indiceTeleventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZTV");
                    int indiceAutoventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZAT");
                    int indiceMixta = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZRM");
                    int indiceDummy = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDY");
                    //COLOMBIA
                    int indiceZGE = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZGE");
                    int indiceZCM = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZCM");
                    int indiceZCS = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZCS");
                    int indiceZDI = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDI");
                    int indiceZEJ = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZEJ");
                    int indiceZES = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZES");
                    int indiceZIN = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZIN");
                    int indiceZOP = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZOP");
                    int indiceZPK = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPK");
                    int indiceZSP = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZSP");
                    int indiceZWB = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZWB");
                    int indiceZWE = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZWE");
                    int indiceZWJ = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZWJ");
                    int indiceZWP = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZWP");
                    //int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDD");
                    int totalvp_preventa = visitasSolicitud.size();

                    if(visitasSolicitud.size() == 0){
                        totalvp_preventa = visitas_permitidas.length;
                    }
                    String tipoVisitaActual = tipoVisita;

                    for (int i = 0; i < totalvp_preventa; i++) {
                        if (visitasSolicitud.size() == 0) {
                            tipoVisitaActual = visitas_permitidas[i];
                        } else {
                            tipoVisitaActual = visitasSolicitud.get(i).getVptyp().trim();
                            if (tipoVisitaActual.equals("ZDD") || tipoVisitaActual.equals("ZDA")) {
                                continue;
                            }
                        }
                        TableLayout v_ll = new TableLayout(getContext());
                        TableRow tr = new TableRow(getContext());
                        if (mDBHelper.ExisteEnVisitPlanActual(modalidad, tipoVisitaActual)) {
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


                            LinearLayout.LayoutParams hlpll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            v_ll.setOrientation(LinearLayout.HORIZONTAL);
                            v_ll.setLayoutParams(hlpll);

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
                                et.setId(Integer.parseInt("1000" + i + x));

                                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0, 10, 10, 10);
                                et.setPadding(1, 0, 1, 0);

                                et.setLayoutParams(lp);
                                if (solicitudSeleccionada.size() > 0 && visitasSolicitud.size() > 0) {
                                    switch (x) {
                                        case 0:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getLun_de()));
                                            break;
                                        case 1:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getMar_de()));
                                            break;
                                        case 2:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getMier_de()));
                                            break;
                                        case 3:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getJue_de()));
                                            break;
                                        case 4:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getVie_de()));
                                            break;
                                        case 5:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getSab_de()));
                                            break;
                                        case 6:
                                            et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(i).getDom_de()));
                                            break;
                                    }
                                } else if (tipoVisitaActual.equals("ZDY") && x == 6) {//Tipo ZDY debe llevar SOLAMENTE dia Domingo por obligacion
                                    et.setText("1");
                                }
                                //Esconder el domingo si el tipo NO ES ZDY, ZWE o ZWB
                                if ((!tipoVisitaActual.equals("ZDY") && !tipoVisitaActual.contains("ZW")) && x == 6) {
                                    et.setVisibility(View.GONE);
                                    label.setVisibility(View.GONE);
                                }

                                if (!modificable || tipoVisitaActual.equals("ZDY")) {
                                    et.setEnabled(false);
                                    et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                                }
                                //et.setPadding(20, 5, 20, 5);
                                Drawable d = getResources().getDrawable(R.drawable.textbackground_min_padding, null);
                                et.setBackground(d);
                                final int finalX = x;
                                String finalModalidad = modalidad;
                                final String[] originalValue = {""};
                                et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if (hasFocus) {
                                            // Save the original value when the EditText gains focus
                                            originalValue[0] = et.getText().toString();
                                        } else {
                                            Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                                            String modalidad = "";
                                            modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();

                                            int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZPV");
                                            int indiceTeleventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZTV");
                                            int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZJV");
                                            int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                            int indiceAutoventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZAT");
                                            int indiceMixta = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZRM");
                                            //int indiceDummy = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDY");
                                            //COLOMBIA
                                            int indiceZGE = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZGE");
                                            int indiceZCM = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZCM");
                                            int indiceZCS = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZCS");
                                            int indiceZDI = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDI");
                                            int indiceZEJ = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZEJ");
                                            int indiceZES = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZES");
                                            int indiceZIN = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZIN");
                                            int indiceZOP = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZOP");
                                            int indiceZPK = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZPK");
                                            int indiceZSP = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZSP");
                                            int indiceZWB = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZWB");
                                            int indiceZWE = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZWE");
                                            int indiceZWJ = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZWJ");
                                            int indiceZWP = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZWP");

                                            final int finalIndicePreventa = indicePreventa;
                                            final int finalIndiceEspecializada = indiceEspecializada;
                                            final int finalIndiceTeleventa = indiceTeleventa;
                                            final int finalIndiceReparto = indiceReparto;
                                            final int finalIndiceMixta = indiceMixta;
                                            final int finalIndiceAutoventa = indiceAutoventa;
                                            final int finalIndiceZGE = indiceZGE;
                                            final int finalIndiceZCM = indiceZCM;
                                            final int finalIndiceZCS = indiceZCS;
                                            final int finalIndiceZDI = indiceZDI;
                                            final int finalIndiceZEJ = indiceZEJ;
                                            final int finalIndiceZES = indiceZES;
                                            final int finalIndiceZIN = indiceZIN;
                                            final int finalIndiceZOP = indiceZOP;
                                            final int finalIndiceZPK = indiceZPK;
                                            final int finalIndiceZSP = indiceZSP;
                                            final int finalIndiceZWB = indiceZWB;
                                            final int finalIndiceZWE = indiceZWE;
                                            final int finalIndiceZWJ = indiceZWJ;
                                            final int finalIndiceZWP = indiceZWP;
                                            //final int finalIndiceDummy = indiceDummy;
                                            if (!hasFocus) {
                                                int diaReparto = 0;
                                                int diasParaReparto = 1;
                                                Visitas visitaPreventa = null;

                                                for (int x = 0; x < visitas_permitidas.length; x++) {
                                                    int indice = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, visitas_permitidas[x]);
                                                    if (indice != -1 && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA", "").equals(visitas_permitidas[x])) {
                                                        visitaPreventa = visitasSolicitud.get(indice);
                                                        if (visitaPreventa.getKvgr4() != null)
                                                            diasParaReparto = Integer.valueOf(visitaPreventa.getKvgr4().replace("DA", ""));
                                                        if ((finalX + diasParaReparto) > 5) {
                                                            diaReparto = ((finalX + diasParaReparto) - 6);
                                                        } else {
                                                            diaReparto = (finalX + diasParaReparto);
                                                        }
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
                                                    if (finalIndiceReparto != -1)
                                                        visitaReparto = visitasSolicitud.get(finalIndiceReparto);
                                                }
                                                if (!((TextView) v).getText().toString().equals("") && Integer.valueOf(((TextView) v).getText().toString()) > 1339) {
                                                    switch (finalX) {
                                                        case 0:
                                                            visitaPreventa.setLun_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setLun_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setLun_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setLun_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                        case 1:
                                                            visitaPreventa.setMar_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setMar_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setMar_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setMar_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                        case 2:
                                                            visitaPreventa.setMier_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setMier_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setMier_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setMier_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                        case 3:
                                                            visitaPreventa.setJue_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setJue_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setJue_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setJue_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                        case 4:
                                                            visitaPreventa.setVie_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setVie_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setVie_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setVie_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                        case 5:
                                                            visitaPreventa.setSab_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setSab_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setSab_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setSab_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                        case 6:
                                                            visitaPreventa.setDom_a(getResources().getString(R.string.max_secuencia));
                                                            visitaPreventa.setDom_de(getResources().getString(R.string.max_secuencia));
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setDom_a(getResources().getString(R.string.max_secuencia));
                                                                visitaMixta.setDom_de(getResources().getString(R.string.max_secuencia));
                                                            }
                                                            break;
                                                    }
                                                    if (!modalidad.equals("GV") && visitaReparto != null) {
                                                        switch (diaReparto) {
                                                            case 0:
                                                                visitaReparto.setLun_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setLun_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                            case 1:
                                                                visitaReparto.setMar_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setMar_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                            case 2:
                                                                visitaReparto.setMier_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setMier_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                            case 3:
                                                                visitaReparto.setJue_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setJue_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                            case 4:
                                                                visitaReparto.setVie_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setVie_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                            case 5:
                                                                visitaReparto.setSab_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setSab_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                            case 6:
                                                                visitaReparto.setDom_a(getResources().getString(R.string.max_secuencia));
                                                                visitaReparto.setDom_de(getResources().getString(R.string.max_secuencia));
                                                                break;
                                                        }
                                                    }
                                                    ((TextView) v).setText(getResources().getString(R.string.max_secuencia));
                                                    if (finalIndiceMixta != -1) {
                                                        copiarDia(et, finalIndiceMixta, finalIndicePreventa);
                                                    }
                                                    if ((finalIndiceZWB != -1 || finalIndiceZWE != -1)) {
                                                        determinarDiasDigitales(et, finalIndiceZWB, finalIndiceZWE);
                                                    }
                                                    Toasty.warning(v.getContext(), R.string.error_max_secuencia).show();
                                                }

                                                //Si el valor es vacio, borrar si existe el dia
                                                if (((TextView) v).getText().toString().trim().replace("0", "").equals("")
                                                        && !VariablesGlobales.AceptarVisitaCero()) {
                                                    if (((TextView) v).getText().toString().trim().length() > 0
                                                            && !VariablesGlobales.AceptarVisitaCero()) {
                                                        ((TextView) v).setText("");
                                                        Toasty.warning(v.getContext(), "La Secuencia debe ser mayor a 0").show();
                                                    }
                                                    switch (finalX) {
                                                        case 0:
                                                            visitaPreventa.setLun_a("");
                                                            visitaPreventa.setLun_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setLun_a("");
                                                                visitaMixta.setLun_de("");
                                                            }
                                                            break;
                                                        case 1:
                                                            visitaPreventa.setMar_a("");
                                                            visitaPreventa.setMar_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setMar_a("");
                                                                visitaMixta.setMar_de("");
                                                            }
                                                            break;
                                                        case 2:
                                                            visitaPreventa.setMier_a("");
                                                            visitaPreventa.setMier_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setMier_a("");
                                                                visitaMixta.setMier_de("");
                                                            }
                                                            break;
                                                        case 3:
                                                            visitaPreventa.setJue_a("");
                                                            visitaPreventa.setJue_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setJue_a("");
                                                                visitaMixta.setJue_de("");
                                                            }
                                                            break;
                                                        case 4:
                                                            visitaPreventa.setVie_a("");
                                                            visitaPreventa.setVie_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setVie_a("");
                                                                visitaMixta.setVie_de("");
                                                            }
                                                            break;
                                                        case 5:
                                                            visitaPreventa.setSab_a("");
                                                            visitaPreventa.setSab_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setSab_a("");
                                                                visitaMixta.setSab_de("");
                                                            }
                                                            break;
                                                        case 6:
                                                            visitaPreventa.setDom_a("");
                                                            visitaPreventa.setDom_de("");
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setDom_a("");
                                                                visitaMixta.setDom_de("");
                                                            }
                                                            break;
                                                    }
                                                    if (!modalidad.equals("GV") && visitaReparto != null) {
                                                        switch (diaReparto) {
                                                            case 0:
                                                                visitaReparto.setLun_a("");
                                                                visitaReparto.setLun_de("");
                                                                break;
                                                            case 1:
                                                                visitaReparto.setMar_a("");
                                                                visitaReparto.setMar_de("");
                                                                break;
                                                            case 2:
                                                                visitaReparto.setMier_a("");
                                                                visitaReparto.setMier_de("");
                                                                break;
                                                            case 3:
                                                                visitaReparto.setJue_a("");
                                                                visitaReparto.setJue_de("");
                                                                break;
                                                            case 4:
                                                                visitaReparto.setVie_a("");
                                                                visitaReparto.setVie_de("");
                                                                break;
                                                            case 5:
                                                                visitaReparto.setSab_a("");
                                                                visitaReparto.setSab_de("");
                                                                break;
                                                            case 6:
                                                                visitaReparto.setDom_a("");
                                                                visitaReparto.setDom_de("");
                                                                break;
                                                        }
                                                    }
                                                    if (finalIndiceMixta != -1) {
                                                        copiarDia(et, finalIndiceMixta, finalIndicePreventa);
                                                    }
                                                    if ((finalIndiceZWB != -1 || finalIndiceZWE != -1)) {
                                                        determinarDiasDigitales(et, finalIndiceZWB, finalIndiceZWE);
                                                    }
                                                } else {
                                                    String secuenciaSAP = VariablesGlobales.SecuenciaToHora(((TextView) v).getText().toString());
                                                    switch (finalX) {
                                                        case 0:
                                                            visitaPreventa.setLun_a(secuenciaSAP);
                                                            visitaPreventa.setLun_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setLun_a(secuenciaSAP);
                                                                visitaMixta.setLun_de(secuenciaSAP);
                                                            }
                                                            break;
                                                        case 1:
                                                            visitaPreventa.setMar_a(secuenciaSAP);
                                                            visitaPreventa.setMar_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setMar_a(secuenciaSAP);
                                                                visitaMixta.setMar_de(secuenciaSAP);
                                                            }
                                                            break;
                                                        case 2:
                                                            visitaPreventa.setMier_a(secuenciaSAP);
                                                            visitaPreventa.setMier_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setMier_a(secuenciaSAP);
                                                                visitaMixta.setMier_de(secuenciaSAP);
                                                            }
                                                            break;
                                                        case 3:
                                                            visitaPreventa.setJue_a(secuenciaSAP);
                                                            visitaPreventa.setJue_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setJue_a(secuenciaSAP);
                                                                visitaMixta.setJue_de(secuenciaSAP);
                                                            }
                                                            break;
                                                        case 4:
                                                            visitaPreventa.setVie_a(secuenciaSAP);
                                                            visitaPreventa.setVie_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setVie_a(secuenciaSAP);
                                                                visitaMixta.setVie_de(secuenciaSAP);
                                                            }
                                                            break;
                                                        case 5:
                                                            visitaPreventa.setSab_a(secuenciaSAP);
                                                            visitaPreventa.setSab_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setSab_a(secuenciaSAP);
                                                                visitaMixta.setSab_de(secuenciaSAP);
                                                            }
                                                            break;
                                                        case 6:
                                                            visitaPreventa.setDom_a(secuenciaSAP);
                                                            visitaPreventa.setDom_de(secuenciaSAP);
                                                            if (finalIndiceMixta != -1) {
                                                                visitaMixta.setDom_a(secuenciaSAP);
                                                                visitaMixta.setDom_de(secuenciaSAP);
                                                            }
                                                            break;
                                                    }
                                                    if (!modalidad.equals("GV") && visitaReparto != null) {
                                                        switch (diaReparto) {
                                                            case 0:
                                                                visitaReparto.setLun_a(secuenciaSAP);
                                                                visitaReparto.setLun_de(secuenciaSAP);
                                                                break;
                                                            case 1:
                                                                visitaReparto.setMar_a(secuenciaSAP);
                                                                visitaReparto.setMar_de(secuenciaSAP);
                                                                break;
                                                            case 2:
                                                                visitaReparto.setMier_a(secuenciaSAP);
                                                                visitaReparto.setMier_de(secuenciaSAP);
                                                                break;
                                                            case 3:
                                                                visitaReparto.setJue_a(secuenciaSAP);
                                                                visitaReparto.setJue_de(secuenciaSAP);
                                                                break;
                                                            case 4:
                                                                visitaReparto.setVie_a(secuenciaSAP);
                                                                visitaReparto.setVie_de(secuenciaSAP);
                                                                break;
                                                            case 5:
                                                                visitaReparto.setSab_a(secuenciaSAP);
                                                                visitaReparto.setSab_de(secuenciaSAP);
                                                                break;
                                                            case 6:
                                                                visitaReparto.setDom_a(secuenciaSAP);
                                                                visitaReparto.setDom_de(secuenciaSAP);
                                                                break;
                                                        }
                                                    }
                                                    if (finalIndiceMixta != -1) {
                                                        copiarDia(et, finalIndiceMixta, finalIndicePreventa);
                                                    }
                                                    if ((finalIndiceZWB != -1 || finalIndiceZWE != -1)) {
                                                        determinarDiasDigitales(et, finalIndiceZWB, finalIndiceZWE);
                                                    }
                                                }

                                            }
                                        }
                                    }

                                    private void copiarDia(TextInputEditText et, int finalIndPreventa, int finalIndMixta) {
                                        if (et.getTag().toString().contains("ZRM")) {
                                            TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get(et.getTag().toString().replace("ZRM", "ZPV"));
                                            if (copiar != null)
                                                copiar.setText(et.getText());
                                            if (finalIndPreventa > 0 && finalIndMixta > 0 && visitasSolicitud.size() > 0) {
                                                String valor = visitasSolicitud.get(finalIndMixta).getValorDiaSegunIndice(finalX);
                                                visitasSolicitud.get(finalIndPreventa).setValorDiaSegunIndice(finalX, valor);
                                            }
                                        }
                                        if (et.getTag().toString().contains("ZPV") && mDBHelper.ExisteTipoVisita("ZRM")) {
                                            TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get(et.getTag().toString().replace("ZPV", "ZRM"));
                                            if (copiar != null)
                                                copiar.setText(et.getText());
                                            if (finalIndPreventa > 0 && finalIndMixta > 0 && visitasSolicitud.size() > 0) {
                                                String valor = visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(finalX);
                                                visitasSolicitud.get(finalIndMixta).setValorDiaSegunIndice(finalX, valor);
                                            }
                                        }
                                    }
                                    private void determinarDiasDigitales(TextInputEditText et, int finalIndZWB, int finalIndZWE) {
                                        int finalIndPreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA",""));
                                        if(et.getTag().toString().contains(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","")) )
                                            if (mDBHelper.ExisteEnVisitPlanActual(finalModalidad,"ZWB")) {
                                                if(!et.getText().toString().equals("")) {
                                                    int frecuencia = Integer.parseInt(et.getText().toString());
                                                    if (frecuencia == 1) {
                                                        TextInputEditText copiar = null;
                                                        if (finalX == 0)//Preventa Lunes -> Digitales Miércoles
                                                            copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_M");
                                                        if (finalX == 1)//Preventa Martes -> Digitales Jueves
                                                            copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_J");
                                                        if (finalX == 2)//Preventa Miércoles -> Digitales Viernes
                                                            copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_V");
                                                        if (finalX == 3)//Preventa Jueves -> Digitales Sábado
                                                            copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_S");
                                                        if (copiar != null)
                                                            copiar.setText("1");
                                                        if (finalIndPreventa > 0 && finalIndZWB > 0 && visitasSolicitud.size() > 0) {
                                                            visitasSolicitud.get(finalIndZWB).setValorDiaSegunIndice(finalX + 2, "0001");
                                                        }
                                                    }
                                                    if (frecuencia > 1) {//Mayor que 1, poner dia domingo en visita digital
                                                        TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_D");
                                                        if (copiar != null)
                                                            copiar.setText("1");
                                                        if (finalIndPreventa > 0 && finalIndZWB > 0 && visitasSolicitud.size() > 0) {
                                                            visitasSolicitud.get(finalIndZWB).setValorDiaSegunIndice(6, "0001");
                                                        }
                                                    }
                                                }else if(originalValue[0].equals("1")){
                                                    TextInputEditText copiar = null;
                                                    if (finalX == 0)//Preventa Lunes -> Digitales Miércoles
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_M");
                                                    if (finalX == 1)//Preventa Martes -> Digitales Jueves
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_J");
                                                    if (finalX == 2)//Preventa Miércoles -> Digitales Viernes
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_V");
                                                    if (finalX == 3)//Preventa Jueves -> Digitales Sábado
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_S");
                                                    if (copiar != null)
                                                        copiar.setText("");
                                                    if (finalIndPreventa > 0 && finalIndZWB > 0 && visitasSolicitud.size() > 0) {
                                                        visitasSolicitud.get(finalIndZWB).setValorDiaSegunIndice(finalX + 2, "");
                                                    }

                                                }else if(!originalValue[0].equals("")){//Caso cuando es vacio, y le valor original es diferente 1
                                                    //Si viene vacio, revisar si tiene algun otro dia en 2 o mas para desactivar el domingo?
                                                    boolean activado = false;
                                                    for(int i = 0;i <= 5;i++){
                                                        if(finalIndPreventa > 0) {
                                                            if (visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(i) != null && !visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(i).equals("") && !visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(i).equals("0001") && finalX != i) {
                                                                activado = true;
                                                            }
                                                        }
                                                    }
                                                    if(!activado) {
                                                        TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWB_D");
                                                        if (copiar != null)
                                                            copiar.setText("");
                                                        if (finalIndPreventa > 0 && finalIndZWB > 0 && visitasSolicitud.size() > 0) {
                                                            visitasSolicitud.get(finalIndZWB).setValorDiaSegunIndice(6, "");
                                                        }
                                                    }
                                                }
                                            }
                                        if (mDBHelper.ExisteEnVisitPlanActual(finalModalidad,"ZWE")) {
                                            if(!et.getText().toString().equals("")) {
                                                int frecuencia = Integer.parseInt(et.getText().toString());
                                                if (frecuencia == 1) {
                                                    TextInputEditText copiar = null;
                                                    if (finalX == 0)//Preventa Lunes -> Digitales Miércoles
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_M");
                                                    if (finalX == 1)//Preventa Martes -> Digitales Jueves
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_J");
                                                    if (finalX == 2)//Preventa Miércoles -> Digitales Viernes
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_V");
                                                    if (finalX == 3)//Preventa Jueves -> Digitales Sábado
                                                        copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_S");
                                                    if (copiar != null)
                                                        copiar.setText("1");
                                                    if (finalIndPreventa > 0 && finalIndZWE > 0 && visitasSolicitud.size() > 0) {
                                                        visitasSolicitud.get(finalIndZWE).setValorDiaSegunIndice(finalX + 2, "0001");
                                                    }
                                                }
                                                if (frecuencia > 1) {//Mayor que 1, poner dia domingo en visita digital
                                                    TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_D");
                                                    if (copiar != null)
                                                        copiar.setText("1");
                                                    if (finalIndPreventa > 0 && finalIndZWE > 0 && visitasSolicitud.size() > 0) {
                                                        visitasSolicitud.get(finalIndZWE).setValorDiaSegunIndice(6, "0001");
                                                    }
                                                }
                                            }else if(originalValue[0].equals("1")){
                                                TextInputEditText copiar = null;
                                                if (finalX == 0)//Preventa Lunes -> Digitales Miércoles
                                                    copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_M");
                                                if (finalX == 1)//Preventa Martes -> Digitales Jueves
                                                    copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_J");
                                                if (finalX == 2)//Preventa Miércoles -> Digitales Viernes
                                                    copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_V");
                                                if (finalX == 3)//Preventa Jueves -> Digitales Sábado
                                                    copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_S");
                                                if (copiar != null)
                                                    copiar.setText("");
                                                if (finalIndPreventa > 0 && finalIndZWB > 0 && visitasSolicitud.size() > 0) {
                                                    visitasSolicitud.get(finalIndZWB).setValorDiaSegunIndice(finalX + 2, "");
                                                }

                                            }else if(!originalValue[0].equals("")){//Caso cuando es vacio, y el valor original es diferente 1
                                                //Si viene vacio, revisar si tiene algun otro dia en 2 o mas para desactivar el domingo?
                                                boolean activado = false;
                                                for(int i = 0;i <= 5;i++){
                                                    if(finalIndPreventa > 0) {
                                                        if (visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(i) != null && !visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(i).equals("") && !visitasSolicitud.get(finalIndPreventa).getValorDiaSegunIndice(i).equals("0001") && finalX != i) {
                                                            activado = true;
                                                        }
                                                    }
                                                }
                                                if(!activado) {
                                                    TextInputEditText copiar = (TextInputEditText) mapeoCamposDinamicos.get("ZWE_D");
                                                    if (copiar != null)
                                                        copiar.setText("");
                                                    if (finalIndPreventa > 0 && finalIndZWB > 0 && visitasSolicitud.size() > 0) {
                                                        visitasSolicitud.get(finalIndZWB).setValorDiaSegunIndice(6, "");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                                if (et_anterior != null) {
                                    et_anterior.setNextFocusForwardId(et.getId());
                                    et_anterior.setNextFocusRightId(et.getId());
                                    et_anterior.setNextFocusLeftId(et.getId());
                                    et_anterior.setNextFocusUpId(et.getId());
                                    et_anterior.setNextFocusDownId(et.getId());
                                }
                                if (x == 6) {
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

                            /*v_ll.addView(tr);
                            if (v_ll.getParent() != null)
                                ((ViewGroup) v_ll.getParent()).removeView(v_ll);
                            ll_visitas.addView(v_ll);
                            if (ll_visitas.getParent() == null)
                                ll.addView(ll_visitas);*/
                        }
                        v_ll.addView(tr);
                        if (v_ll.getParent() != null)
                            ((ViewGroup) v_ll.getParent()).removeView(v_ll);
                        ll_visitas.addView(v_ll);
                        if (ll_visitas.getParent() == null)
                            ll.addView(ll_visitas);
                    }
                    break;
                case "W_CTE-ADJUNTOS":
                    tb_adjuntos.setColumnCount(3);
                    tb_adjuntos.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

                    tb_adjuntos.setLayoutParams(hlp);

                    if (solicitudSeleccionada.size() > 0) {
                        if ((idForm == null || idForm.equals("")) || solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Incidencia") || solicitudSeleccionada.get(0).get("ESTADO").trim().equals("Modificado")) {
                            adjuntosSolicitud = mDBHelper.getAdjuntosDB(idSolicitud);
                            manejadorAdjuntos.setAdjuntosSolicitud(adjuntosSolicitud);
                        }
                        else {
                            adjuntosSolicitud = mDBHelper.getAdjuntosServidor(idForm);
                            manejadorAdjuntos.setAdjuntosSolicitud(adjuntosSolicitud);
                        }
                    }
                    if (modificable) {
                        btnAddBloque.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                mPhotoUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        new ContentValues());
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
                    if (adjuntosSolicitud != null) {
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(getContext(), adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*alturaFilaTableView);
                        tb_adjuntos.setDataAdapter(stda);
                    }
                    headers = ((AdjuntoTableAdapter) tb_adjuntos.getDataAdapter()).getHeaders();
                    sta = new SimpleTableHeaderAdapter(getContext(), headers);
                    sta.setPaddings(10, 5, 10, 5);
                    sta.setTextSize(16);
                    sta.setTextColor(getResources().getColor(R.color.white, null));
                    sta.setTypeface(Typeface.BOLD);
                    sta.setGravity(GRAVITY_CENTER);

                    tb_adjuntos.setHeaderAdapter(sta);
                    tb_adjuntos.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                    if (tb_adjuntos.getParent() != null)
                        rl.addView(tb_adjuntos);

                    //Horizontal View de adjuntos
                    HorizontalScrollView hsv = new HorizontalScrollView(getContext());
                    ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsv, getContext(), getActivity(), adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

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
                    }else{
                        horariosSolicitud.add(new Horarios(GUID,"00:00:00"));
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
                                                    Toasty.warning(getContext(),"Rangos de horas en la mañana invalido! Solo horas entre 0 y 12 son permitidas.").show();
                                                    return;
                                                }
                                            }
                                            if(et.getTag().toString().contains("ab2") || et.getTag().toString().contains("bi2")){
                                                if(selectedHour < 6){
                                                    if(selectedHour > 0 && selectedMinute > 0) {
                                                        Toasty.warning(getContext(), "Rangos de horas de la tarde invalido! Solo horas entre 12 y 23 son permitidas").show();
                                                        return;
                                                    }
                                                }
                                            }
                                            String mivalor = "00".substring(String.valueOf(selectedHour).length()) + String.valueOf(selectedHour) + ":" + "00".substring(String.valueOf(selectedMinute).length()) + String.valueOf(selectedMinute) + ":00";
                                            et.setText(mivalor);
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
                        if(solicitudSeleccionada.size() > 0){
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
                if(solicitudSeleccionada.size() == 0){
                    //horariosSolicitud.add(new Horarios(GUID,"00:00:00"));
                }
                    ll.addView(h_tl);
                break;
                case "W_CTE-COMENTARIOS":
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

    public static void displayDialogMessageInput(Context context, String mensaje) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.message_input_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextView message = d.findViewById(R.id.message);
        final TextView input = d.findViewById(R.id.input);
        input.setSingleLine(false);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(2);
        input.setMaxLines(10);
        input.setVerticalScrollBarEnabled(true);
        input.setMovementMethod(ScrollingMovementMethod.getInstance());
        input.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        input.setGravity(INDICATOR_GRAVITY_TOP);

        input.setBackground(context.getResources().getDrawable(R.drawable.textbackground_min_padding, null));

        InputFilter[] editFilters = input.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        input.setFilters(newFilters);
        input.setAllCaps(true);

        Button saveBtn= d.findViewById(R.id.saveBtn);
        Button cancelBtn= d.findViewById(R.id.cancelBtn);
        title.setText("Comentario");
        message.setText(mensaje);

        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setError(null);
                if(!input.getText().toString().trim().isEmpty() && input.getText().toString().trim().length() > 4) {
                    String bukrs = PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad());
                    WeakReference<Context> weakRefs1 = new WeakReference<Context>(context);
                    WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>((Activity) context);
                    if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion", "").equals("api")) {
                        RechazarPreSolicitudAPI r = new RechazarPreSolicitudAPI(weakRefs1, weakRefAs1, bukrs, idForm, "Rechazado", input.getText().toString().trim());
                        r.execute();
                    } else {
                        RechazarPreSolicitudServidor r = new RechazarPreSolicitudServidor(weakRefs1, weakRefAs1, bukrs, idForm, "Rechazado", input.getText().toString().trim());
                        r.execute();
                    }
                    d.dismiss();
                }else{
                    input.setError("Debe digitar un comentario obligatorio de rechazo!");
                }
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
    public static void displayDialogMessageInputDevolver(Context context, String mensaje) {
        final Dialog d=new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.message_input_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextView message = d.findViewById(R.id.message);
        final TextView input = d.findViewById(R.id.input);
        input.setSingleLine(false);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setMinLines(2);
        input.setMaxLines(10);
        input.setVerticalScrollBarEnabled(true);
        input.setMovementMethod(ScrollingMovementMethod.getInstance());
        input.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        input.setGravity(INDICATOR_GRAVITY_TOP);

        input.setBackground(context.getResources().getDrawable(R.drawable.textbackground_min_padding, null));

        InputFilter[] editFilters = input.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        input.setFilters(newFilters);
        input.setAllCaps(true);

        Button saveBtn= d.findViewById(R.id.saveBtn);
        Button cancelBtn= d.findViewById(R.id.cancelBtn);
        title.setText("Comentario");
        message.setText(mensaje);

        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setError(null);
                if(!input.getText().toString().trim().isEmpty() && input.getText().toString().trim().length() > 4) {
                    String bukrs = PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad());
                    WeakReference<Context> weakRefs1 = new WeakReference<Context>(context);
                    WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>((Activity) context);
                    if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion", "").equals("api")) {
                        DevolverPreSolicitudAPI r = new DevolverPreSolicitudAPI(weakRefs1, weakRefAs1, bukrs, idForm, "Pendiente", input.getText().toString().trim());
                        r.execute();
                    } else {
                        DevolverPreSolicitudServidor r = new DevolverPreSolicitudServidor(weakRefs1, weakRefAs1, bukrs, idForm, "Pendiente", input.getText().toString().trim());
                        r.execute();
                    }
                    d.dismiss();
                }else{
                    input.setError("Debe digitar un comentario obligatorio de rechazo!");
                }
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
                    Toasty.error(v.getContext(), "No se pudo salvar el impuesto. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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
        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_impstos",1,2,null, "taxkd=1");

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Para campos de seleccion para grid bancos campo clasificacion fiscal
        ArrayList<HashMap<String, String>> opcionesClasi = mDBHelper.getDatosCatalogo("cat_impstos",3,4,null);

        ArrayList<OpcionSpinner> listaopcionesClasi = new ArrayList<>();
        int selectedIndexClasi = 0;
        for (int j = 0; j < opcionesClasi.size(); j++){
            if(seleccionado != null && opciones.get(j).get("id").equals(seleccionado.getTatyp())){
                selectedIndexClave = j;
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
            clasiSpinner.setSelection(selectedIndexClave);
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
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Interlocutor nuevoInterlocutor = new Interlocutor();
                nuevoInterlocutor.setName1(nameEditText.getText().toString());
                nuevoInterlocutor.setKunn2(propellantEditTxt.getText().toString());
                nuevoInterlocutor.setVtext(destEditTxt.getText().toString());
                try{
                    interlocutoresSolicitud.add(nuevoInterlocutor);
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
                    Toasty.error(v.getContext(), "No se pudo salvar el interlocutor. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
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
                    Toasty.error(v.getContext(), "No se pudo salvar el banco. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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
                Toasty.warning(context,"Seleccione el Grupo Isscom para generar la encuesta.").show();
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
                        Toasty.error(v.getContext(), "Error Insertando Encuesta Canales", Toasty.LENGTH_SHORT).show();
                    }

                    //Calcular Nivel Socioeconomico Planchado Segun el valor de su canal Pais
                    if (PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_VKORG","").equals("0443")) {
                        String NSEPCalculado = mDBHelper.AlgoritmoNSEP(valor_canales.get("W_CTE-ZZENT4").trim());
                        Spinner nsepSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KATR4");
                        if(NSEPCalculado.trim().length() > 0)
                            nsepSpinner.setSelection(VariablesGlobales.getIndex(nsepSpinner,NSEPCalculado.trim()));
                    }

                    Toasty.success(v.getContext(), "Encuesta Canales ejecutada correctamente!", Toasty.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA");
                    ejecutada.setChecked(true);
                } catch (Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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

    public static void displayDialogEncuestaCanalesColombia(final Context context) {
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

                ArrayList<HashMap<String, String>> respuestas = mDBHelper.getRespuestasEncuesta(GUID);

                ArrayList<HashMap<String, String>> subgrupo = mDBHelper.getSubgrupoSegunGrupo(opcion.getId());
                if(subgrupo.size() > 0){//Si existe subgrupo, se debe pintar primero el subgrupo para saber que preguntas se pintaran al seleccionar el subgrupo
                    Spinner spinner_subgrupo = new Spinner(d.getContext());
                    spinner_subgrupo.setId(1000);
                    int selected = 0;
                    ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();
                    misOpciones.add(new OpcionSpinner("","Seleccione..."));
                    TextView label_pregunta = new TextView(d.getContext());
                    label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), "Grupo Isscom", "Sub Grupo Isscom"));
                    //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                    layout.addView(label_pregunta,params1);
                    for (int i = 0; i < subgrupo.size(); i++){
                        OpcionSpinner op = new OpcionSpinner(subgrupo.get(i).get("zid_subgrupo"), subgrupo.get(i).get("zid_subgrupo")+" - "+subgrupo.get(i).get("text"));
                        if (respuestas.size() > 0 && respuestas.get(0) != null && subgrupo.get(i).get("zid_subgrupo").equals(respuestas.get(0).get("col" + (i + 1)))
                                && (respuestas.get(0).get("zid_subgrupo")).equals(String.valueOf(position))) {
                            op.setSelected(i);
                            selected = i;
                        }
                        misOpciones.add(op);
                    }
                    ArrayAdapter<OpcionSpinner> dataAdapterP = new ArrayAdapter<>(context, R.layout.simple_spinner_item, misOpciones);
                    dataAdapterP.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
                    spinner_subgrupo.setBackground(spinner_back);
                    spinner_subgrupo.setAdapter(dataAdapterP);
                    layout.addView(spinner_subgrupo,params1);

                    spinner_subgrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final OpcionSpinner subgrupo = (OpcionSpinner) parent.getSelectedItem();
                            ArrayList<HashMap<String, String>> preguntas = mDBHelper.getPreguntasSegunSubGrupo(opcion.getId(),subgrupo.getId());

                            for (int j = 0; j < preguntas.size(); j++){
                                int selected = 0;
                                Spinner pregunta = new Spinner(d.getContext());
                                ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                                TextView label_pregunta = new TextView(d.getContext());
                                label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                                //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                                layout.addView(label_pregunta,params1);
                                ArrayList<HashMap<String, String>> opcionesxpregunta = mDBHelper.getOpcionesPreguntaSubGrupo(opcion.getId(),subgrupo.getId(),preguntas.get(j).get("zid_quest"));
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

                        }
                    });
                }else {//No tiene subgrupo, mostrar las preguntas normalmente

                    ArrayList<HashMap<String, String>> preguntas = mDBHelper.getPreguntasSegunGrupo(opcion.getId());

                    for (int j = 0; j < preguntas.size(); j++) {
                        int selected = 0;
                        Spinner pregunta = new Spinner(d.getContext());
                        ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                        TextView label_pregunta = new TextView(d.getContext());
                        label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                        //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                        layout.addView(label_pregunta, params1);
                        ArrayList<HashMap<String, String>> opcionesxpregunta = mDBHelper.getOpcionesPreguntaGrupo(opcion.getId(), preguntas.get(j).get("zid_quest"));
                        for (int i = 0; i < opcionesxpregunta.size(); i++) {
                            OpcionSpinner op = new OpcionSpinner(opcionesxpregunta.get(i).get("zid_resp"), opcionesxpregunta.get(i).get("zid_resp") + " - " + opcionesxpregunta.get(i).get("text"));
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

                        pregunta.setId(j + 1);
                        pregunta.setSelection(selected);
                        layout.addView(pregunta, params1);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toasty.warning(context,"Seleccione el Grupo Isscom para generar la encuesta.").show();
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
                    String valorg = ((OpcionSpinner) s.getSelectedItem()).getId();
                    encuestaValues.put("id_Grupo", valorg);
                    Spinner subs = d.findViewById(1000);
                    String valorsubg = "";
                    if(subs != null) {
                        valorsubg = ((OpcionSpinner) subs.getSelectedItem()).getId();
                        encuestaValues.put("id_Subgrupo", valorsubg);
                    }

                    s = d.findViewById(1);
                    String valor1 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col1", valor1);
                    s = d.findViewById(2);
                    String valor2 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col2", valor2);
                    s = d.findViewById(3);
                    String valor3 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col3", valor3);
                    s = d.findViewById(4);
                    String valor4 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col4", valor4);
                    s = d.findViewById(5);
                    String valor5 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col5", valor5);
                    s = d.findViewById(6);
                    String valor6 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col6", valor6);
                    s = d.findViewById(7);
                    String valor7 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col7", valor7);
                    s = d.findViewById(8);
                    String valor8 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col8", valor8);
                    s = d.findViewById(9);
                    String valor9 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col9", valor9);
                    s = d.findViewById(10);
                    String valor10 = s != null ? ((OpcionSpinner) s.getSelectedItem()).getId() : null;
                    encuestaValues.put("col10", valor10);
                    HashMap<String, String> valor_canales = mDBHelper.getValoresSegunEncuestaRealizadaColombia(valorg, valorsubg, valor1, valor2, valor3, valor4, valor5, valor6, valor7, valor8, valor9, valor10);

                    if(valor_canales.size() == 0) {
                        Toasty.error(v.getContext(), "No se pudo obtener los valores del resultado de esta encuesta!", Toasty.LENGTH_SHORT).show();
                        return;
                    }


                    //Asignar valores de canales segun respuesta obtenida
                    //Spinner zzent3Spinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZENT3");
                    //zzent3Spinner.setSelection(VariablesGlobales.getIndex(zzent3Spinner,valor_canales.get("W_CTE-ZZENT3").trim()));

                    //Spinner zzent4Spinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZENT4");
                    //zzent4Spinner.setSelection(VariablesGlobales.getIndex(zzent4Spinner,valor_canales.get("W_CTE-ZZENT4").trim()));

                    Spinner zzcanalSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZCANAL");
                    if(zzcanalSpinner != null)
                        zzcanalSpinner.setSelection(VariablesGlobales.getIndex(zzcanalSpinner,valor_canales.get("W_CTE-ZZCANAL").trim()));

                    Spinner ztpocanalSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZTPOCANAL");
                    if(ztpocanalSpinner != null)
                        ztpocanalSpinner.setSelection(VariablesGlobales.getIndex(ztpocanalSpinner,valor_canales.get("W_CTE-ZTPOCANAL").trim()));

                    Spinner zgpocanalSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZGPOCANAL");
                    if(zgpocanalSpinner != null)
                        zgpocanalSpinner.setSelection(VariablesGlobales.getIndex(zgpocanalSpinner,valor_canales.get("W_CTE-ZGPOCANAL").trim()));

                    //Spinner pson3Spinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-PSON3");
                    //pson3Spinner.setSelection(VariablesGlobales.getIndex(pson3Spinner,valor_canales.get("W_CTE-PSON3").trim()));

                    Spinner unnegSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZUNNEG");
                    if(unnegSpinner != null)
                        unnegSpinner.setSelection(VariablesGlobales.getIndex(unnegSpinner,valor_canales.get("W_CTE-ZZUNNEG").trim()));

                    Spinner subunnegSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZSUBUNNEG");
                    if(subunnegSpinner != null)
                        subunnegSpinner.setSelection(VariablesGlobales.getIndex(subunnegSpinner,valor_canales.get("W_CTE-ZZSUBUNNEG").trim()));

                    //Validar
                    Spinner occonsSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZOCCONS");
                    if(occonsSpinner != null) {
                        String valor = valor_canales.get("W_CTE-ZZOCCONS");
                        if (valor != null) {
                            occonsSpinner.setSelection(VariablesGlobales.getIndex(occonsSpinner, valor.trim()));
                            CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_CONSUMO");
                            ejecutada.setChecked(true);
                            ejecutada.setEnabled(false);
                        }else{
                            //Abrir encuesta ocasion consumo o activar/habilitar check de encuesta ocasion de consumo
                            CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_CONSUMO");
                            ejecutada.setChecked(false);
                            ejecutada.setEnabled(true);
                            //Y mensaje que debe hacerlo
                            Toasty.warning(d.getContext(), "Debe ejecutar la encuesta de Ocasion de Consumo", Toasty.LENGTH_SHORT).show();
                        }

                    }

                    Spinner gecSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                    if(gecSpinner != null) {
                        String valor = valor_canales.get("W_CTE-KLABC");
                        if( valor != null) {
                            if(valor.equals("52") || valor.equals("53")) {
                                gecSpinner.setSelection(VariablesGlobales.getIndex(gecSpinner, valor.trim()));
                                CheckBox ejecutada = (CheckBox) mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                                ejecutada.setChecked(true);
                                ejecutada.setEnabled(false);
                            }else{
                                CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                                ejecutada.setChecked(false);
                                ejecutada.setEnabled(true);
                                //Y mensaje que debe hacerlo
                                Toasty.warning(d.getContext(), "El valor GEC '"+valor+"' no es permitido para la creación del cliente.", Toasty.LENGTH_SHORT).show();
                            }
                        }else{
                            //activar/habilitar check de encuesta GEC
                            CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                            ejecutada.setChecked(false);
                            ejecutada.setEnabled(true);
                            //Y mensaje que debe hacerlo
                            Toasty.warning(d.getContext(), "Debe ejecutar la encuesta de GEC", Toasty.LENGTH_SHORT).show();
                        }
                    }
                    try {
                        mDb.insert(VariablesGlobales.getTablaEncuestaSolicitud(), null, encuestaValues);
                    } catch (Exception e) {
                        Toasty.error(d.getContext(), "Error Insertando Encuesta Canales", Toasty.LENGTH_SHORT).show();
                    }

                    //Calcular Nivel Socioeconomico Planchado Segun el valor de su canal Pais
                    if (PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_VKORG","").equals("0443")) {
                        String NSEPCalculado = mDBHelper.AlgoritmoNSEP(valor_canales.get("W_CTE-ZZENT4").trim());
                        Spinner nsepSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KATR4");
                        if(NSEPCalculado.trim().length() > 0)
                            nsepSpinner.setSelection(VariablesGlobales.getIndex(nsepSpinner,NSEPCalculado.trim()));
                    }

                    Toasty.success(d.getContext(), "Encuesta Canales ejecutada correctamente!", Toasty.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA");
                    ejecutada.setChecked(true);
                } catch (Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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
                            Toasty.error(v.getContext(), "Por favor llene todos los campos de la encuesta.", Toasty.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            mDb.insert(VariablesGlobales.getTablaEncuestaGecSolicitud(), null, encuestaValues);
                        } catch (Exception e) {
                            Toasty.error(v.getContext(), "Error Insertando Encuesta Canales (Registro #"+j+")", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    String valor_gec = mDBHelper.getGecSegunEncuestaRealizada(suma_montos);
                    //Asignar los valores de los canales segun las respuestas obtenidas
                    Spinner gecSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                    gecSpinner.setSelection(VariablesGlobales.getIndex(gecSpinner,valor_gec));

                    Toasty.success(v.getContext(), "Encuesta GEC ejecutada correctamente!", Toasty.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                    ejecutada.setChecked(true);
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta gec. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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

    public static void displayDialogEncuestaGecColombia(final Context context) {
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
        //final ArrayList<HashMap<String, String>> opciones_x_pregunta = mDBHelper.getRespuestasGec();

        for (int j = 0; j < preguntas.size(); j++){
            final ArrayList<HashMap<String, String>> opcionesxpregunta = mDBHelper.getOpcionesXPreguntaGec(preguntas.get(j).get("zid_quest"));
            if(opcionesxpregunta.size() == 0) {
                TextInputEditText monto = new TextInputEditText(d.getContext());
                monto.setInputType(InputType.TYPE_CLASS_NUMBER);
                ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                TextView label_pregunta = new TextView(d.getContext());
                label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                layout.addView(label_pregunta, params1);

                //revisar si ya se ha realizado una encuesta para la solicitud, para poder mostrar las respuestas existentes.
                if (respuestas.size() > 0) {
                    if (monto != null)
                        monto.setText(respuestas.get(j).get("monto"));
                }
                monto.setId(j + 1);
                layout.addView(monto, params1);
            }else{
                int selected = 0;
                Spinner pregunta = new Spinner(d.getContext());
                ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                TextView label_pregunta = new TextView(d.getContext());
                label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                layout.addView(label_pregunta, params1);
                for (int i = 0; i < opcionesxpregunta.size(); i++) {
                    OpcionSpinner op = new OpcionSpinner(opcionesxpregunta.get(i).get("zid_resp"), opcionesxpregunta.get(i).get("zid_resp") + " - " + opcionesxpregunta.get(i).get("text"));
                    if (respuestas.size() > 0 && respuestas.get(0) != null && opcionesxpregunta.get(i).get("zid_resp").equals(respuestas.get(j).get("monto"))) {
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

                pregunta.setId(j + 1);
                pregunta.setSelection(selected);
                layout.addView(pregunta, params1);
            }
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
                    String[] idValores = new String[preguntas.size()];
                    for (int j = 0; j < preguntas.size(); j++){
                            Spinner monto = d.findViewById(j + 1);
                            encuestaValues.clear();
                            //encuestaValues.put("id_encuesta_gec", GUID);
                            encuestaValues.put("id_solicitud", GUID);
                            encuestaValues.put("zid_grupo", (j+1));
                            encuestaValues.put("zid_quest", (j+1));
                            encuestaValues.put("monto", ((OpcionSpinner)monto.getSelectedItem()).getId());
                            idValores[j] = ((OpcionSpinner)monto.getSelectedItem()).getId();
                            if(((OpcionSpinner)monto.getSelectedItem()).getId().trim().equals("")){
                                Toasty.error(v.getContext(), "Por favor llene todos los campos de la encuesta.", Toasty.LENGTH_SHORT).show();
                                return;
                            }
                        try {
                            mDb.insert(VariablesGlobales.getTablaEncuestaGecSolicitud(), null, encuestaValues);
                        } catch (Exception e) {
                            Toasty.error(v.getContext(), "Error Insertando Encuesta Canales (Registro #"+j+")", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    String valor_gec = mDBHelper.getGecSegunEncuestaRealizadaColombia(idValores);
                    //Asignar los valores de los canales segun las respuestas obtenidas
                    Spinner gecSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                    gecSpinner.setSelection(VariablesGlobales.getIndex(gecSpinner,valor_gec));

                    Toasty.success(v.getContext(), "Encuesta GEC ejecutada correctamente!", Toasty.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                    ejecutada.setChecked(true);
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta gec. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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

    public static void displayDialogEncuestaOcasionConsumo(final Context context) {
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
        title.setText("Encuesta Ocasión de Consumo");
        LinearLayout layout = d.findViewById(R.id.layoutDinamico);
        layout.removeAllViews();

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10,10,10,10);
        final ArrayList<HashMap<String, String>> preguntas = mDBHelper.getPreguntasOcasionConsumo();
        final ArrayList<HashMap<String, String>> respuestas = mDBHelper.getEncuestaOcasionConsumo(GUID);

        for (int j = 0; j < preguntas.size(); j++){
            final ArrayList<HashMap<String, String>> opcionesxpregunta = mDBHelper.getOpcionesXPreguntaOcasionConsumo(preguntas.get(j).get("zid_quest"));
            if(opcionesxpregunta.size() == 0) {
                TextInputEditText monto = new TextInputEditText(d.getContext());
                monto.setInputType(InputType.TYPE_CLASS_NUMBER);
                ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                TextView label_pregunta = new TextView(d.getContext());
                label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                layout.addView(label_pregunta, params1);

                //revisar si ya se ha realizado una encuesta para la solicitud, para poder mostrar las respuestas existentes.
                if (respuestas.size() > 0) {
                    if (monto != null)
                        monto.setText(respuestas.get(j).get("monto"));
                }
                monto.setId(j + 1);
                layout.addView(monto, params1);
            }else{
                int selected = 0;
                Spinner pregunta = new Spinner(d.getContext());
                ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

                TextView label_pregunta = new TextView(d.getContext());
                label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
                //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
                layout.addView(label_pregunta, params1);
                for (int i = 0; i < opcionesxpregunta.size(); i++) {
                    OpcionSpinner op = new OpcionSpinner(opcionesxpregunta.get(i).get("zid_resp"), opcionesxpregunta.get(i).get("zid_resp") + " - " + opcionesxpregunta.get(i).get("text"));
                    if (respuestas.size() > 0 && respuestas.get(0) != null && opcionesxpregunta.get(i).get("zid_resp").equals(respuestas.get(j).get("respuesta_obtenida"))) {
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

                pregunta.setId(j + 1);
                pregunta.setSelection(selected);
                layout.addView(pregunta, params1);
            }
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
                    int del = mDb.delete(VariablesGlobales.getTablaEncuestaOcasionConsumoSolicitud(), "id_solicitud = ?", new String[]{GUID});
                    ContentValues encuestaValues = new ContentValues();
                    String[] idValores = new String[preguntas.size()];
                    for (int j = 0; j < preguntas.size(); j++){
                        Spinner respuesta = d.findViewById(j + 1);
                        encuestaValues.clear();

                        encuestaValues.put("id_solicitud", GUID);
                        encuestaValues.put("zid_grupo", (j+1));
                        encuestaValues.put("zid_quest", (j+1));
                        encuestaValues.put("respuesta_obtenida", ((OpcionSpinner)respuesta.getSelectedItem()).getId());
                        idValores[j] = ((OpcionSpinner)respuesta.getSelectedItem()).getId();
                        if(((OpcionSpinner)respuesta.getSelectedItem()).getId().trim().equals("")){
                            Toasty.error(v.getContext(), "Por favor llene todos los campos de la encuesta de ocasion de consumo.", Toasty.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            mDb.insert(VariablesGlobales.getTablaEncuestaOcasionConsumoSolicitud(), null, encuestaValues);
                        } catch (Exception e) {
                            Toasty.error(v.getContext(), "Error Insertando Encuesta Ocasion Consumo (Registro #"+j+")", Toasty.LENGTH_SHORT).show();
                        }
                    }

                    String valor_occons = mDBHelper.getOcasionConsumoSegunEncuestaRealizada(idValores);
                    //Asignar los valores de los canales segun las respuestas obtenidas
                    Spinner occonsSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-ZZOCCONS");
                    occonsSpinner.setSelection(VariablesGlobales.getIndex(occonsSpinner,valor_occons));

                    Toasty.success(v.getContext(), "Encuesta OCASION DE CONSUMO ejecutada correctamente!", Toasty.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_CONSUMO");
                    ejecutada.setChecked(true);
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta de ocasion de consumo. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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
            displayDialogContacto(SolicitudActivity.this,seleccionado);
        }
    }
    //Listeners de Bloques de datos
    private class ContactoLongClickListener implements TableDataLongClickListener<Contacto> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Contacto seleccionado) {
            DialogHandler appdialog = new DialogHandler();

            appdialog.Confirm(SolicitudActivity.this, "Confirmación Borrado", "Esta seguro que quiere eliminar el contacto "+seleccionado.getName1() + " " +seleccionado.getNamev()+"?",
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
            Toasty.info(context, salida, Toasty.LENGTH_SHORT).show();
        }
    }
    private class ImpuestoClickListener implements TableDataClickListener<Impuesto> {
        @Override
        public void onDataClicked(int rowIndex, Impuesto seleccionado) {
            displayDialogImpuesto(SolicitudActivity.this,seleccionado);
        }
    }
    private class ImpuestoLongClickListener implements TableDataLongClickListener<Impuesto> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Impuesto seleccionado) {
            String salida = seleccionado.getVtext() + " " + seleccionado.getVtext2();
            impuestosSolicitud.remove(rowIndex);
            tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(getBaseContext(), impuestosSolicitud));
            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toasty.LENGTH_SHORT).show();
            return true;
        }
    }

    private class BancoClickListener implements TableDataClickListener<Banco> {
        @Override
        public void onDataClicked(int rowIndex, Banco seleccionado) {
            displayDialogBancos(SolicitudActivity.this,seleccionado);
        }
    }
    private class BancoLongClickListener implements TableDataLongClickListener<Banco> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Banco seleccionado) {
            DialogHandler appdialog = new DialogHandler();
            appdialog.Confirm(SolicitudActivity.this, "Confirmación Borrado", "Esta seguro que quiere eliminar el banco "+seleccionado.getBankn()+"?",
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
            Toasty.info(context, salida, Toasty.LENGTH_SHORT).show();
        }
    }
    private class InterlocutorClickListener implements TableDataClickListener<Interlocutor> {
        @Override
        public void onDataClicked(int rowIndex, Interlocutor seleccionado) {
            displayDialogInterlocutor(SolicitudActivity.this,seleccionado);
        }
    }
    private class InterlocutorLongClickListener implements TableDataLongClickListener<Interlocutor> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Interlocutor seleccionado) {
            String salida = seleccionado.getName1() + " " + seleccionado.getKunn2();
            interlocutoresSolicitud.remove(rowIndex);
            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(getBaseContext(), interlocutoresSolicitud));
            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toasty.LENGTH_SHORT).show();
            return true;
        }
    }
    private class VisitasClickListener implements TableDataClickListener<Visitas> {
        @Override
        public void onDataClicked(int rowIndex, Visitas seleccionado) {
            DetallesVisitPlan(SolicitudActivity.this, seleccionado);
        }
    }
    @SuppressWarnings("unchecked")
    private void DetallesVisitPlan(final Context context, final Visitas seleccionado) {
        final Dialog d = new Dialog(context, R.style.MyAlertDialogTheme);
        d.setContentView(R.layout.visita_dialog_layout);
        final boolean reparto = mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BZIRK",""), seleccionado.getVptyp());
        //INITIALIZE VIEWS
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

        if(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661")
        || PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("Z001")){
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
        switch(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad())){
            case "1661":
            case "Z001":
                Spinner gec = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                if(gec != null)
                    filtroxPais = " AND kvgr3 = '"+((OpcionSpinner)gec.getSelectedItem()).getId().trim()+"'";
                Spinner bzirk_sel = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                if(bzirk_sel != null)
                    filtroxPais += " AND bzirk = '"+((OpcionSpinner)bzirk_sel.getSelectedItem()).getId().trim()+"'";
                break;
            case "F428":
                filtroxPais += " route = '"+PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_RUTAHH","")+"'";
                break;
            default:
                filtroxPais = "";
        }

        ArrayList<OpcionSpinner> rutas_reparto = null;
        if(VariablesGlobales.getSociedad().equals("F428")){
            rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("SAPDCAT_Ruta_Relacion",filtroxPais);
        }else{
            rutas_reparto =  mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'"+filtroxPais);
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(Objects.requireNonNull(context), R.layout.simple_spinner_item, rutas_reparto);
        // Drop down layout style - list view with radio button
        dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
        ruta_reparto.setAdapter(dataAdapterRuta);
        ruta_reparto.setSelection(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()));

        //Validar si existe ruta mixta para determinar si puede modificar o no la ruta de su VP
        if(!reparto){
            if((seleccionado.getVptyp().equals("ZPV") && !mDBHelper.ExisteRutaMixta() && PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_TIPORUTA","").equals(seleccionado.getVptyp()))
                    || seleccionado.getVptyp().equals("ZAT")
                    || (PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_TIPORUTA","").equals(seleccionado.getVptyp()))
            || (PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BUKRS","").equals("F428") && (seleccionado.getVptyp().equals("ZWE") || seleccionado.getVptyp().equals("ZWB"))) ) {
                ruta_reparto.setVisibility(View.GONE);
                ruta_reparto_label.setVisibility(View.GONE);
            }else{
                Spinner zona_ventas = (Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK");
                String valor_zona_ventas = ((OpcionSpinner)zona_ventas.getSelectedItem()).getId().trim();
                ArrayList<OpcionSpinner> rutas_preventa = mDBHelper.getDatosCatalogoParaSpinner("EX_T_RUTAS_VP","(bzirk = '" + valor_zona_ventas + "' OR bzirk = '') AND vptyp = '" + seleccionado.getVptyp() + "' AND vkorg = '" + PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_VKORG","") + "'");
                // Creando el adaptador(opciones) para el comboBox deseado
                ArrayAdapter<OpcionSpinner> dataAdapterRutaP = new ArrayAdapter<>(Objects.requireNonNull(context), R.layout.simple_spinner_item, rutas_preventa);
                // Drop down layout style - list view with radio button
                dataAdapterRutaP.setDropDownViewResource(R.layout.spinner_item);
                ruta_reparto.setAdapter(dataAdapterRutaP);
                ruta_reparto.setSelection(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()));
            }
        }
        /*if(reparto){
            kvgr4Spinner.setVisibility(GONE);
            f_icoEditText.setVisibility(GONE);
            f_fcoEditText.setVisibility(GONE);
            f_iniEditText.setVisibility(GONE);
            f_finEditText.setVisibility(GONE);
        }*/

        //Se quito para la inclusion para evitar que pusieran exclusiones por error, si quieren excluirlo ddeben realizar un formulario de modificacion
       // EditTextDatePicker prueba = new EditTextDatePicker(context, f_icoEditText,"yyyymmdd");
        //EditTextDatePicker prueba2 = new EditTextDatePicker(context, f_fcoEditText,"yyyymmdd");
        //EditTextDatePicker prueba3 = new EditTextDatePicker(context, f_iniEditText,"yyyymmdd");
        //EditTextDatePicker prueba4 = new EditTextDatePicker(context, f_finEditText,"yyyymmdd");

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
            @Override
            public void onClick(View v) {
                //Setear ruta segun el tipo de visita ZPV, ZDD, etc
                if( reparto ){
                    seleccionado.setRuta(((OpcionSpinner)ruta_reparto.getSelectedItem()).getId().toString().trim());
                    ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")),seleccionado.getRuta()));

                    Spinner vwerk = ((Spinner)mapeoCamposDinamicos.get("W_CTE-VWERK"));
                    String repartoSeleccionado = ((OpcionSpinner)ruta_reparto.getSelectedItem()).getId().trim();
                    if(vwerk != null) {
                        int ind_zona_transporte = VariablesGlobales.getIndex(vwerk, mDBHelper.centroSuministroSegunRutaReparto(repartoSeleccionado));
                        suppressRecreateAdapter=true;
                        vwerk.setSelection(ind_zona_transporte);
                    }

                    String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_VKORG",""), seleccionado.getRuta());
                    if(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")) != null)
                        ((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")), condicionExpedicion));
                }else{
                    if( mDBHelper.ExisteTipoVisita("ZRM") || mDBHelper.ExisteTipoVisita("ZDY") || !PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_TIPORUTA", "").equals(seleccionado.getVptyp())){
                        if(((OpcionSpinner)ruta_reparto.getSelectedItem()) != null)
                            seleccionado.setRuta(((OpcionSpinner)ruta_reparto.getSelectedItem()).getId().toString().trim());
                    }else{
                        seleccionado.setRuta(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_RUTAHH", ""));
                    }
                    if(seleccionado.getVptyp().equals("ZAT") || seleccionado.getVptyp().equals("ZAI") || seleccionado.getVptyp().equals("ZAH") || seleccionado.getVptyp().equals("ZAN") || seleccionado.getVptyp().equals("ZDI")){
                        ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")),seleccionado.getRuta()));
                        String condicionExpedicion = mDBHelper.CondicionExpedicionSegunRutaReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_VKORG",""), seleccionado.getRuta());
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")) != null)
                            ((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-VSBED")), condicionExpedicion));
                    }else if(seleccionado.getVptyp().equals("ZCM") || seleccionado.getVptyp().equals("ZDM") || seleccionado.getVptyp().equals("ZDP")){
                        //((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")),seleccionado.getRuta()));
                        ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setEnabled(true);
                        ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                    }
                }
                //seleccionado.setRuta(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_RUTAHH",""));
                seleccionado.setKvgr4(kvgr4Spinner.getSelectedItem().toString().trim());
                seleccionado.setF_ini(f_iniEditText.getText().toString());
                seleccionado.setF_fin(f_finEditText.getText().toString());
                seleccionado.setF_ico(f_icoEditText.getText().toString());
                seleccionado.setF_fco(f_fcoEditText.getText().toString());
                seleccionado.setFcalid(((OpcionSpinner)fcalidSpinner.getSelectedItem()).getId());

                //Replicar los cambios en setF_ico, setF_fco, setF_ini, setF_fin, setKvgr4
                for(int x=0; x < visitasSolicitud.size(); x++){
                    Visitas vp = visitasSolicitud.get(x);
                    vp.setF_ico(f_icoEditText.getText().toString());
                    vp.setF_fco(f_fcoEditText.getText().toString());
                    vp.setF_ini(f_iniEditText.getText().toString());
                    vp.setF_fin(f_finEditText.getText().toString());
                    vp.setKvgr4(seleccionado.getKvgr4());
                }

                //RECALCULAR DIAS DE VISITA
                RecalcularDiasDeReparto();

                tb_visitas.setDataAdapter(new VisitasTableAdapter(v.getContext(),SolicitudActivity.this, visitasSolicitud,modificable));
                try {
                    d.dismiss();
                }catch(Exception e){
                    Toasty.error(v.getContext(), "No se pudo salvar la configuracion").show();
                }
            }
        });
        if(!modificable){
            saveBtn.setEnabled(false);
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.boton_transparente,null));
        }else{
            saveBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }
        d.show();
    }

    private void RecalcularDiasDeReparto() {
        int numplanes = visitasSolicitud.size();
        //Iterrar sobre todos los planes de la modalida de venta seleccionada
        for (int y = 0 ; y < numplanes; y++) {
            Visitas vp = visitasSolicitud.get(y);
            //Revisar si el VP es una ruta de reparto para ser borrada y recalculada
            if (mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BZIRK",""), vp.getVptyp())) {
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
        for (int y = 0 ; y < numplanes; y++) {
            Visitas vp = visitasSolicitud.get(y);

            //Si no es tipo de reparto debemos tomar en cuenta para calcular su reparto
            boolean esReparto = mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BZIRK",""), vp.getVptyp());
            //TODO cambiar el valor "PR" por el valor dinamico del comboBox de Modalidad de venta
            if(!esReparto && !vp.getVptyp().equals("ZRM") && !vp.getVptyp().equals("ZDY")){
                String rutaReparto = "";
                if(rutaReparto.equals("ZAT")){//Autoventa no ocupa recalcular su reparto
                    return;
                }
                else if(vp.getVptyp().equals("ZTV"))
                    rutaReparto = mDBHelper.RutaRepartoAsociada("TA", vp.getVptyp());
                else
                    rutaReparto = mDBHelper.RutaRepartoAsociada("PR", vp.getVptyp());

                if(rutaReparto.isEmpty()){
                    Spinner comboModalidad = ((Spinner) mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                    String modalidad = "";
                    if(comboModalidad != null) {
                        modalidad = ((OpcionSpinner) comboModalidad.getAdapter().getItem((int) comboModalidad.getSelectedItemId())).getId();
                        rutaReparto = mDBHelper.RutaRepartoAsociada(modalidad, vp.getVptyp());
                    }
                }
                for (int x = 0; x < visitasSolicitud.size(); x++) {
                    if (rutaReparto.equals(visitasSolicitud.get(x).getVptyp())) {
                        rep = visitasSolicitud.get(x);
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

                asignarDiaReparto(diasParaReparto, 1, l, rep);
                asignarDiaReparto(diasParaReparto, 2, m, rep);
                asignarDiaReparto(diasParaReparto, 3, k, rep);
                asignarDiaReparto(diasParaReparto, 4, j, rep);
                asignarDiaReparto(diasParaReparto, 5, v, rep);
                asignarDiaReparto(diasParaReparto, 6, s, rep);

            }else if(vp.getVptyp().equals("ZRM")){//Si la ruta es MIXTA

            }
        }
    }

    public static void asignarDiaReparto(int metodo, int diaPreventa, String secuencia, Visitas vp_reparto){
        if(secuencia != null && secuencia.trim().length() > 0 && vp_reparto != null){
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
                    vp_reparto.setLun_a(secuenciaSAP);
                    vp_reparto.setLun_de(secuenciaSAP);
                    break;
                case 2:
                    vp_reparto.setMar_a(secuenciaSAP);
                    vp_reparto.setMar_de(secuenciaSAP);
                    break;
                case 3:
                    vp_reparto.setMier_a(secuenciaSAP);
                    vp_reparto.setMier_de(secuenciaSAP);
                    break;
                case 4:
                    vp_reparto.setJue_a(secuenciaSAP);
                    vp_reparto.setJue_de(secuenciaSAP);
                    break;
                case 5:
                    vp_reparto.setVie_a(secuenciaSAP);
                    vp_reparto.setVie_de(secuenciaSAP);
                    break;
                case 6:
                    vp_reparto.setSab_a(secuenciaSAP);
                    vp_reparto.setSab_de(secuenciaSAP);
                    break;
            }
        }
    }

    private class VisitasLongClickListener implements TableDataLongClickListener<Visitas> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Visitas seleccionado) {
            String salida = seleccionado.getVptyp() + " " + seleccionado.getRuta();
            visitasSolicitud.remove(rowIndex);
            tb_visitas.setDataAdapter(new VisitasTableAdapter(getBaseContext(), getParent(), visitasSolicitud,modificable));
            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toasty.LENGTH_SHORT).show();
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

            tb_visitas.setDataAdapter(new VisitasTableAdapter(context,activity, visitasSolicitud,modificable));
            if (tb_visitas.getLayoutParams() != null) {
                tb_visitas.getLayoutParams().height = 50;
                tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView ) * visitasSolicitud.size());
            }
        }
    }
    public static class CalcularRepartoConHabilitador implements Runnable {
        private Context context;
        private Activity activity;
        public CalcularRepartoConHabilitador(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
        }
        public void run() {
            //Si es colombia y las coordenadas existen llamar al habilitador
            if(PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BUKRS", "").equals("F428")//CAMBIAR COLOMBIA
                    && Validaciones.ValidarCoordenadaX(mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG")) && Validaciones.ValidarCoordenadaY(mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"))){
                double latitud = Double.parseDouble(((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT")).getText().toString());
                double longitud = Double.parseDouble(((MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG")).getText().toString());
                String reparto = Haversine.ClosestCoordinateRoute(latitud, longitud, mDBHelper);
                if(!reparto.equals("")) {//Si encontró alguna ruta de reparto, asignar a los VP
                    String vptyp = "";
                    int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                    if (indiceReparto != -1) {
                        visitasSolicitud.get(indiceReparto).setRuta(reparto);
                        vptyp = visitasSolicitud.get(indiceReparto).getVptyp();
                    }
                    int indiceRepartoAutoventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDA");
                    if (indiceRepartoAutoventa != -1) {
                        visitasSolicitud.get(indiceRepartoAutoventa).setRuta(reparto);
                        vptyp = visitasSolicitud.get(indiceRepartoAutoventa).getVptyp();
                    }
                    //Deberia llenar el LZONE si encuentra algo aca? creo que si

                    Spinner lzone = ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE"));
                    if(lzone != null) {
                        lzone.setSelection(VariablesGlobales.getIndex(((Spinner) mapeoCamposDinamicos.get("W_CTE-LZONE")), reparto));
                    }

                    Spinner vwerk = ((Spinner)mapeoCamposDinamicos.get("W_CTE-VWERK"));
                    if(vwerk != null) {
                        suppressRecreateAdapter=true;
                        vwerk.setSelection(VariablesGlobales.getIndex(vwerk, mDBHelper.centroSuministroSegunRutaReparto(reparto)));
                    }

                    tb_visitas.setDataAdapter(new VisitasTableAdapter(context,activity, visitasSolicitud,modificable));
                    if (tb_visitas.getLayoutParams() != null) {
                        tb_visitas.getLayoutParams().height = 50;
                        tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView ) * visitasSolicitud.size());
                    }
                    Toasty.success(context,"Ruta de Reparto calculada automaticamente según aproximidad de coordenadas!").show();
                }else{
                    tb_visitas.setDataAdapter(new VisitasTableAdapter(context,activity, visitasSolicitud,modificable));
                    if (tb_visitas.getLayoutParams() != null) {
                        tb_visitas.getLayoutParams().height = 50;
                        tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView ) * visitasSolicitud.size());
                    }
                    Toasty.warning(context,"Ruta de Reparto calculada automaticamente No encontrada en Pavent!").show();
                }

            }else{
                Toasty.warning(context,"Faltan las coordenadas geográficas del cliente o son incorrectas!").show();
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
            for (int i = 0; i < listaCamposDinamicos.size(); i++) {
                if(!listaCamposBloque.contains(listaCamposDinamicos.get(i).trim()) && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_CONSUMO")) {
                    try {
                        MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                        String valor = tv.getText().toString();

                        if(listaCamposDinamicos.get(i).equals("W_CTE-SMTP_ADDR")){
                            if(atCorreo != null){
                                //Armar el valor del correo segun la escogencia de dominio
                                if(!((OpcionSpinner) atCorreo.getSelectedItem()).getId().toString().equals("Otros")){
                                    valor = tv.getText().toString()+((OpcionSpinner)atCorreo.getSelectedItem()).getId().toString();
                                }
                            }
                        }
                        if(listaCamposDinamicos.get(i).equals("W_CTE-STREET") && VariablesGlobales.getSociedad().equals("F428")){
                            if(prefijo_direccion != null){
                                //Armar el valor de la direccion dian colombia
                                if(!((OpcionSpinner) prefijo_direccion.getSelectedItem()).getId().equals("0") && !((OpcionSpinner) prefijo_direccion.getSelectedItem()).getId().equals("")){
                                    valor = ((OpcionSpinner)prefijo_direccion.getSelectedItem()).getId().toString()+" "+tv.getText().toString();
                                }
                            }
                        }

                        if(!listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC")  && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_CONSUMO") && !listaCamposDinamicos.get(i).equals("W_CTE-COMENTARIOS"))
                            insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor );

                        if(listaCamposDinamicos.get(i).equals("W_CTE-COMENTARIOS")) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                            Date date = new Date();
                            if(comentarios.size() == 0)
                                insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            else {
                                if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia")) {
                                    insertValues.put("[" + listaCamposDinamicos.get(i) + "]", comentarios.get(0).getComentario() + "INCIDENCIA (" + dateFormat.format(date) + "): " + valor);
                                }else {
                                    insertValues.put("[" + listaCamposDinamicos.get(i) + "]", comentarios.get(0).getComentario() + "C(" + dateFormat.format(date) + "): " + valor);
                                }
                            }
                        }
                    } catch (Exception e) {
                        try {
                            Spinner sp = ((Spinner) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                            String valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                            insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                        } catch (Exception e2) {
                            try {
                                CheckBox check = ((CheckBox) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                                String valor = "";
                                if (check.isChecked()) {
                                    valor = "X";
                                }
                                insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            }catch(Exception e3){
                                Toasty.error(getBaseContext(),"No se pudo obtener el valor del campo "+listaCamposDinamicos.get(i)).show();
                            }
                        }
                    }
                }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.
                    switch(listaCamposDinamicos.get(i)){
                        case "W_CTE-CONTACTOS":
                            ContentValues contactoValues = new ContentValues();
                            if (solicitudSeleccionada.size() > 0) {
                                mDb.delete(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), "id_solicitud=?", new String[]{GUID});
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
                                    Toasty.error(getApplicationContext(), "Error Insertando Contacto de Solicitud", Toasty.LENGTH_SHORT).show();
                                }
                            }

                            break;
                        case "W_CTE-IMPUESTOS":
                            ContentValues impuestoValues = new ContentValues();
                            int del;
                            if (solicitudSeleccionada.size() > 0) {
                                del = mDb.delete(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_HH(), "id_solicitud=?", new String[]{GUID});
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
                                    Toasty.error(getApplicationContext(), "Error Insertando Impuesto de Solicitud", Toasty.LENGTH_SHORT).show();
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

                                try {
                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_HH(), null, interlocutorValues);
                                    interlocutorValues.clear();
                                } catch (Exception e) {
                                    Toasty.error(getApplicationContext(), "Error Insertando Interlocutor de Solicitud", Toasty.LENGTH_SHORT).show();
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
                                Toasty.error(getApplicationContext(), "Error Insertando Bancos de Solicitud", Toasty.LENGTH_SHORT).show();
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
                                    if(visitasSolicitud.get(c).getRuta().trim().length() > 0){
                                        mDb.insert(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), null, visitaValues);
                                    }
                                    visitaValues.clear();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Visitas de Solicitud", Toasty.LENGTH_SHORT).show();
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
                                Toasty.error(getApplicationContext(), "Error Insertando Adjuntos de Solicitud. "+e.getMessage(), Toasty.LENGTH_SHORT).show();
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
                    }
                }
            }
            try {
                //Datos que siemrpe deben ir cuando se crea por primera vez.
                insertValues.put("[W_CTE-KTOKD]", PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_KTOKD",""));
                Spinner sp = ((Spinner) mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR"));
                String id_aprobador = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                insertValues.put("[SIGUIENTE_APROBADOR]", id_aprobador);
                insertValues.put("[W_CTE-BUKRS]", PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BUKRS",""));
                insertValues.put("[W_CTE-RUTAHH]", PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_RUTAHH",""));
                insertValues.put("[W_CTE-VKORG]", PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_VKORG",""));
                insertValues.put("[id_solicitud]", NextId);
                insertValues.put("[tipform]", tipoSolicitud);
                insertValues.put("[ususol]", PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("userMC",""));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                Date date = new Date();
                //ContentValues initialValues = new ContentValues();

                //mDBHelper.getWritableDatabase().insert("FormHvKof_solicitud", null, insertValues);
                if(solicitudSeleccionada.size() > 0){
                    if(solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia")) {
                        insertValues.put("[estado]", "Modificado");
                    }
                    long modifico = mDb.update("FormHvKof_solicitud", insertValues, "id_solicitud = ?", new String[]{solicitudSeleccionada.get(0).get("id_solicitud")});
                    Toasty.success(getApplicationContext(), "Registro modificado con éxito", Toasty.LENGTH_SHORT).show();
                }else {
                    insertValues.put("[feccre]", dateFormat.format(date));
                    insertValues.put("[estado]", "Nuevo");
                    //En caso que venga de una presolicitud, debemos actualizar la presolicitud y el campo
                    if(idPresolicitud != null){
                        ArrayList<HashMap<String, String>> preSolicitudSeleccionada = new ArrayList<>();
                        preSolicitudSeleccionada = mDBHelper.getSolicitud(idPresolicitud);
                        insertValues.put("[ID_PREFORMULARIO]", preSolicitudSeleccionada.get(0).get("IDFORM"));
                    }
                    long inserto = mDb.insertOrThrow("FormHvKof_solicitud", null, insertValues);

                    //En caso que venga de una presolicitud, debemos actualizar la presolicitud y el campo
                    if(idPresolicitud != null){
                        ContentValues updateValues = new ContentValues();
                        updateValues.put("[ESTADO]", "Aprobado");
                        long modifico = mDb.update("FormHvKof_solicitud", updateValues, "id_solicitud = ?", new String[]{idPresolicitud});
                    }

                    //Una vez finalizado el proceso de guardado, se limpia la solicitud para una nueva.
                    Intent sol = getIntent();
                    sol.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    SolicitudActivity.this.finish();
                    //Bundle par = new Bundle();
                    //par.putString("tipo_solicitud",tipoSolicitud);
                    //SolicitudActivity.this.startActivity(sol);
                    Toasty.success(getApplicationContext(), "Registro insertado con éxito", Toasty.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toasty.error(getApplicationContext(), "Error Insertando Solicitud."+e.getMessage(), Toasty.LENGTH_LONG).show();
            }

        }
    }

    private static void Provincias(AdapterView<?> parent){
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();

        ArrayList<HashMap<String, String>> provincias = mDBHelper.Provincias(opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        ArrayList<HashMap<String, String>> preSolicitudSeleccionada = null;
        if(idPresolicitud != null){
            preSolicitudSeleccionada = mDBHelper.getSolicitud(idPresolicitud);
        }
        for (int j = 0; j < provincias.size(); j++){
            listaopciones.add(new OpcionSpinner(provincias.get(j).get("id"), provincias.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-REGION").trim().equals(provincias.get(j).get("id"))){
                selectedIndex = j;
            }
            if(idPresolicitud != null && preSolicitudSeleccionada != null && preSolicitudSeleccionada.get(0).get("W_CTE-REGION").trim().equals(provincias.get(j).get("id"))) {
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
        if(combo != null) {
            combo.setBackground(d);
            combo.setAdapter(dataAdapter);
            dataAdapter.notifyDataSetChanged();
            combo.setSelection(selectedIndex);

            if (selectedIndex == 0 && listaCamposObligatorios.contains("W_CTE-REGION")){
                setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));
            }
            DireccionCorta(parent.getContext());
            if (!modificable) {
                combo.setEnabled(false);
                combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
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
        SearchableSpinner combo = (SearchableSpinner)mapeoCamposDinamicos.get("W_CTE-CITY1");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        combo.setSelection(selectedIndex);

        if(selectedIndex == 0 && listaCamposObligatorios.contains("W_CTE-CITY1")){
            setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));
        }
        DireccionCorta(parent.getContext());
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }
    }
    private static void Municipios(AdapterView<?> parent) {
        ArrayList<HashMap<String, String>> municipios = new ArrayList<HashMap<String, String>>();
        Spinner pais = (Spinner) mapeoCamposDinamicos.get("W_CTE-LAND1");
        OpcionSpinner opcionpais = null;
        if(pais != null)
            opcionpais = (OpcionSpinner) pais.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        if(opcionpais != null && opcion != null)
            municipios = mDBHelper.Municipios(opcionpais.getId(), opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        int selectedIndex2 = 0;
        for (int j = 0; j < municipios.size(); j++) {
            listaopciones.add(new OpcionSpinner(municipios.get(j).get("id"), municipios.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-ORT01") != null) {
                if (solicitudSeleccionada.get(0).get("W_CTE-ORT01").trim().equals(municipios.get(j).get("id"))) {
                    selectedIndex = j;
                }
            }
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-CITY1") != null) {
                if (solicitudSeleccionada.get(0).get("W_CTE-CITY1").trim().equals(municipios.get(j).get("id"))) {
                    selectedIndex2 = j;
                }
            }
        }
        SearchableSpinner combo = (SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-ORT01");

        if(combo != null) {
            // Creando el adaptador(opciones) para el comboBox deseado
            ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            // attaching data adapter to spinner
            Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
            combo.setBackground(d);
            combo.setAdapter(dataAdapter);
            dataAdapter.notifyDataSetChanged();
            combo.setSelection(selectedIndex);

            if (selectedIndex == 0 && (listaCamposObligatorios.contains("W_CTE-ORT01") || listaCamposObligatorios.contains("W_CTE-CITY1"))){
                setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));
            }
            //DireccionCorta(parent.getContext());
            if (!modificable) {
                combo.setEnabled(false);
                combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
        }

        //en caso que exista el campo para colombia replicar opciones el de facturacion
        SearchableSpinner munfac = (SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-CITY1");
        if (munfac != null) {
            // Creando el adaptador(opciones) para el comboBox deseado
            ArrayAdapter<OpcionSpinner> dataAdapter2 = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
            // Drop down layout style - list view with radio button
            dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
            // attaching data adapter to spinner
            Drawable d2 = parent.getResources().getDrawable(R.drawable.spinner_background, null);
            munfac.setBackground(d2);
            munfac.setAdapter(dataAdapter2);
            dataAdapter2.notifyDataSetChanged();
            munfac.setSelection(selectedIndex2);

            if (selectedIndex == 0 && (listaCamposObligatorios.contains("W_CTE-CITY1") || listaCamposObligatorios.contains("W_CTE-CITY11"))) {
                setErrorWithTooltipOnTouch(munfac, parent.getContext().getResources().getString(R.string.error_field_required));
                /*SearchableSpinner finalMunfac = (SearchableSpinner) munfac;
                munfac.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View view = finalMunfac.getSelectedView();
                        if (view instanceof TextView) {
                            setErrorWithTooltipOnTouch(finalMunfac, parent.getContext().getResources().getString(R.string.error_field_required));
                        }
                        finalMunfac.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });*/
            }
            //DireccionCorta(parent.getContext());
            if (!modificable) {
                munfac.setEnabled(false);
                munfac.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
        }
        munfac = (SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-CITY11");
        if (munfac != null) {
            // Creando el adaptador(opciones) para el comboBox deseado
            ArrayAdapter<OpcionSpinner> dataAdapter2 = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
            // Drop down layout style - list view with radio button
            dataAdapter2.setDropDownViewResource(R.layout.spinner_item);
            // attaching data adapter to spinner
            Drawable d2 = parent.getResources().getDrawable(R.drawable.spinner_background, null);
            munfac.setBackground(d2);
            munfac.setAdapter(dataAdapter2);
            dataAdapter2.notifyDataSetChanged();
            munfac.setSelection(selectedIndex2);

            if (selectedIndex == 0 && (listaCamposObligatorios.contains("W_CTE-CITY1") || listaCamposObligatorios.contains("W_CTE-CITY11"))) {
                setErrorWithTooltipOnTouch(munfac, parent.getResources().getString(R.string.error_field_required));
            }
            //DireccionCorta(parent.getContext());
            if (!modificable) {
                munfac.setEnabled(false);
                munfac.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
        }

    }
    public static void setErrorWithTooltipOnTouch(Spinner spinner, String errorMessage) {
        if(spinner == null)
            return;
        SearchableSpinner finalMunfac = (SearchableSpinner) spinner;
        spinner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View view = finalMunfac.getSelectedView();
                if (view instanceof TextView) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        textView.setError(errorMessage); // This adds the error icon
                        textView.setFocusableInTouchMode(true);
                        textView.setFocusable(true);
                        // ✅ Replace OnClickListener with OnTouchListener for better control
                        textView.setOnTouchListener((v, event) -> {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                Drawable[] drawables = textView.getCompoundDrawables();
                                Drawable rightDrawable = drawables[2]; // Right icon (error icon)

                                if (rightDrawable != null) {
                                    int drawableWidth = rightDrawable.getBounds().width();
                                    int touchX = (int) event.getX();
                                    int iconStart = textView.getWidth() - textView.getPaddingRight() - drawableWidth;

                                    if (touchX >= iconStart) {
                                        // Touched on the error icon → show tooltip
                                        textView.post(() -> {
                                            textView.requestFocus();
                                            textView.performLongClick();
                                        });
                                        return true; // Don't propagate to avoid opening spinner
                                    }
                                }
                                // If not clicking on icon, simulate the touch to open spinner options
                                long now = SystemClock.uptimeMillis();
                                MotionEvent downEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, 0, 0, 0);
                                MotionEvent upEvent = MotionEvent.obtain(now, now, MotionEvent.ACTION_UP, 0, 0, 0);
                                ((SearchableSpinner)spinner).onTouch(spinner, downEvent);
                                ((SearchableSpinner)spinner).onTouch(spinner, upEvent);
                            }
                            return true;
                        });

                    }
                }
                finalMunfac.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
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
        dataAdapter.notifyDataSetChanged();
        combo.setSelection(selectedIndex);

        if(selectedIndex == 0 && listaCamposObligatorios.contains("W_CTE-STR_SUPPL3")){
            setErrorWithTooltipOnTouch(combo, parent.getResources().getString(R.string.error_field_required));
        }

        DireccionCorta(parent.getContext());
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }
    }

    private static void BarriosAsync(AdapterView<?> parent,WeakReference<Activity> activityRef) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Activity activity = activityRef.get();
            SearchableSpinner combo = (SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-STR_SUPPL3");
            if (combo != null) {
                activity.runOnUiThread(() -> {
                    ArrayList<OpcionSpinner> spinnerItems = new ArrayList<>();
                    spinnerItems.add(new OpcionSpinner("-1", "Cargando Opciones...")); // Use -1 as a placeholder ID

                    SpinnerAdapter adapter = new SpinnerAdapter(Objects.requireNonNull(parent.getContext()), spinnerItems);

                    combo.setAdapter(adapter);
                    combo.setLoading(true);
                    adapter.notifyDataSetChanged();
                });
            }
            // SQLite Query on background thread
            ArrayList<HashMap<String, String>> barrios = new ArrayList<HashMap<String, String>>();
            Spinner provincia = (Spinner)mapeoCamposDinamicos.get("W_CTE-REGION");
            OpcionSpinner opcionprovincia = null;
            if(provincia != null)
                opcionprovincia = (OpcionSpinner) provincia.getSelectedItem();
            final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
            if(opcionprovincia != null && opcion != null)
                barrios = mDBHelper.Barrios(opcionprovincia.getId(),opcion.getId());

            ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
            int selectedIndex = 0;
            for (int j = 0; j < barrios.size(); j++){
                listaopciones.add(new OpcionSpinner(barrios.get(j).get("id"), barrios.get(j).get("descripcion")));
                if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-STR_SUPPL3").trim().equals(barrios.get(j).get("id"))){
                    selectedIndex = j;
                }
            }

            int finalSelectedIndex = selectedIndex;
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    // Update UI after fetching data
                    //Spinner combo = (Spinner) mapeoCamposDinamicos.get("W_CTE-STR_SUPPL3");
                    if (combo != null) {
                        // Creando el adaptador(opciones) para el comboBox deseado
                        //ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
                        SpinnerAdapter dataAdapter = new SpinnerAdapter(Objects.requireNonNull(parent.getContext()), listaopciones);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                        // attaching data adapter to spinner
                        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
                        combo.setBackground(d);
                        combo.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
                        combo.setSelection(finalSelectedIndex);

                        if (finalSelectedIndex == 0  && listaCamposObligatorios.contains("W_CTE-STR_SUPPL3")) {
                            Spinner finalCombo = combo;
                            combo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    View view = finalCombo.getSelectedView();
                                    if (view instanceof TextView) {
                                        ((TextView) view).setError("El campo es obligatorio!");
                                    }
                                    finalCombo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            });
                        }

                        DireccionCorta(parent.getContext());
                        if (!modificable) {
                            combo.setEnabled(false);
                            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }
                        combo.setLoading(false);
                        //combo.setOnTouchListener(touchListener);
                    }
                });
            }
        });
    }

    private static void Barrios(AdapterView<?> parent){
        ArrayList<HashMap<String, String>> barrios = new ArrayList<HashMap<String, String>>();
        Spinner provincia = (Spinner)mapeoCamposDinamicos.get("W_CTE-REGION");
        OpcionSpinner opcionprovincia = null;
        if(provincia != null)
            opcionprovincia = (OpcionSpinner) provincia.getSelectedItem();
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        if(opcionprovincia != null && opcion != null)
            barrios = mDBHelper.Barrios(opcionprovincia.getId(),opcion.getId());

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < barrios.size(); j++){
            listaopciones.add(new OpcionSpinner(barrios.get(j).get("id"), barrios.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-STR_SUPPL3").trim().equals(barrios.get(j).get("id"))){
                selectedIndex = j;
            }
        }
        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-STR_SUPPL3");
        if(combo != null) {
            // Creando el adaptador(opciones) para el comboBox deseado
            ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            // attaching data adapter to spinner
            Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
            combo.setBackground(d);
            combo.setAdapter(dataAdapter);
            TextView view = null;
            view = ((TextView) combo.getAdapter().getView(0,null,null));
            combo.setSelection(selectedIndex);

            if (selectedIndex == 0 && view != null && listaCamposObligatorios.contains("W_CTE-STR_SUPPL3")){
                Spinner finalCombo = combo;
                combo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View view = finalCombo.getSelectedView();
                        if (view instanceof TextView) {
                            ((TextView) view).setError("El campo es obligatorio!");
                        }
                        finalCombo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
            DireccionCorta(parent.getContext());
            if (!modificable) {
                combo.setEnabled(false);
                combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            }
        }
    }

    private static void  DireccionCorta(Context context) {
        String sociedad = PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BUKRS","");
        switch(sociedad){
            case "F445":
                break;
            case "F443":
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
                if(!d.getId().isEmpty())
                    home.setText(d.getName().trim().split("-")[1]);

                StringBuilder dircorta = new StringBuilder();
                if (dir != null) {
                    if (prov != null && !((OpcionSpinner)prov.getSelectedItem()).getId().equals("")) {
                        if(!p.getId().isEmpty())
                            dircorta.append(p.getName().trim().split("- ")[1]);
                    }
                    if (cant != null && !((OpcionSpinner)cant.getSelectedItem()).getId().equals("")) {
                        if(!c.getId().isEmpty())
                            dircorta.append(c.getName().trim().split("-")[1]);
                    }
                    if (dist != null && !((OpcionSpinner)dist.getSelectedItem()).getId().equals("")) {
                        if(!d.getId().isEmpty())
                            dircorta.append(d.getName().trim().split("-")[1]);
                    }
                    if(dir != null)
                        dir.setText(dircorta.toString().toUpperCase(Locale.getDefault()));
                    if(dirF != null)
                        dirF.setText(dircorta.toString().toUpperCase(Locale.getDefault()));
                }
                break;
            case "F446":
            case "1657":
            case "1658":
                break;
            case "1661":
            case "Z001":
                break;
            case "F428":
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
                    if(fityp != null)
                        fityp.setSelection(VariablesGlobales.getIndex(fityp,"05"));
                    break;
                case "88":
                    fityp = (Spinner)mapeoCamposDinamicos.get("W_CTE-FITYP");
                    CheckBox zona_franca = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ZONA_FRANCA");
                    if (zona_franca != null && zona_franca.isChecked() && fityp != null) {
                        fityp.setSelection(VariablesGlobales.getIndex(fityp,"04"));
                    } else if(fityp != null){
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
        if(hasta != null) {
            if(campo.equals("W_CTE-SORT1")) {
                if(desde.getText().length() > 10)
                    hasta.setText(desde.getText().toString().substring(0, 10));
                else
                    hasta.setText(desde.getText().toString());
                if(desde.getText().toString().length() > 0)
                    hasta.setError(null);
                else
                    hasta.setError(v.getContext().getResources().getString(R.string.error_field_required));
            }else
                hasta.setText(desde.getText());
        }
    }
    private static void ReplicarValorSpinner(View v, String campo,int selection){
        Spinner desde = (Spinner)v;
        Spinner hasta = (Spinner)mapeoCamposDinamicos.get(campo);
        if(hasta != null)
            hasta.setSelection(selection);
    }
    private static void ReplicarValorSpinner(View v, String campo,String valorSeleccioando){
        Spinner desde = (Spinner)v;
        Spinner hasta = (Spinner)mapeoCamposDinamicos.get(campo);
        if(hasta != null)
            hasta.setSelection(VariablesGlobales.getIndex(hasta,valorSeleccioando));
    }
    private static boolean ValidarCedula(View v, String tipoCedula) {
        TextView texto = (TextView) v;
        String cedula = "";
        Pattern pattern;
        Matcher matcher;
        switch (PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS", "")) {
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
                    cedulaValidada = false;
                    Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                    texto.setError("Formato Regimen " + tipoCedula + " invalido!");
                    return true;
                }
                cedulaValidada = true;
                MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
                if (idfiscal != null) {
                    String cedulaDigitada = texto.getText().toString().trim();
                    if (texto.getText().toString().trim().endsWith("-00") && tipoCedula.equals("C1"))
                        idfiscal.setText(cedulaDigitada.substring(0, cedulaDigitada.length() - 3).replaceFirst("^0+(?!$)", "").replace("-", ""));
                    else
                        idfiscal.setText(cedulaDigitada.replaceFirst("^0+(?!$)", "").replace("-", ""));
                    idfiscal.setError(null);
                    idfiscal.clearFocus();
                    Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                }
                break;
            case "F445"://Nicaragua
                if (texto.getText().toString().trim().length() < 3) {
                    cedulaValidada = false;
                    texto.setError("Formato Regimen " + tipoCedula + " invalido!");
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
                    Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                    cedulaValidada = false;
                    texto.setError("Formato Regimen " + tipoCedula + " invalido!");
                    return true;
                }
                cedulaValidada = true;
                Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                break;

            case "F451"://Panama
                switch (tipoCedula) {
                    case "P1":
                        if (texto.getText().toString().trim().startsWith("NA-")) {
                            cedula = "NA-([0][1-9]|[1][0-2])-[0-9]{4,4}-[0-9]{5,5}";
                        }
                        if (texto.getText().toString().trim().startsWith("PE-")) {
                            cedula = "PE-[0-9]{4,4}-[0-9]{5,5}";
                        }
                        if (texto.getText().toString().trim().startsWith("N-")) {
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
                    texto.setError("Formato Regimen " + tipoCedula + " invalido!");
                    return true;
                }
                cedulaValidada = true;
                Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                break;
            case "F446"://GT Embocem
            case "1657"://Volcanes
            case "1658"://Abasa
                if (texto.getText().toString().trim().length() == 0) {
                    if (!listaCamposObligatorios.contains("W_CTE-STCD1")) {
                        cedulaValidada = true;
                        texto.setError(null);
                    } else {
                        cedulaValidada = false;
                        texto.setError("El campo NIT es obligatorio!");
                    }
                    return true;
                }
                /*Validaciones Adicionales para GT*/
                String regexp_idfiscal = "[1-9][0-9]{1,9}-[0-9A-Z]";

                Pattern patternFi = Pattern.compile(regexp_idfiscal);
                //Pattern patternCF = Pattern.compile(regexp_cf);
                Matcher matcherFi = patternFi.matcher(texto.getText());
                //Matcher matcherCF = patternCF.matcher(texto.getText());

                if (!matcherFi.matches()) {
                    cedulaValidada = false;
                    texto.setError("NIT valor/formato inválido!");
                    return true;
                }
                /*Despues del formato se realiza validacion MOD 11*/
                String[] nit = texto.getText().toString().split("-");
                if (texto.getText().toString().replace("-", "").replace("0", "").length() == 0) {
                    cedulaValidada = false;
                    texto.setError("NIT no puede tener solo ceros!");
                    return true;
                }
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
                    cedulaValidada = false;
                    texto.setError("NIT inválido por digito verificador!");
                    return true;
                }
                cedulaValidada = true;
                texto.setError(null);
                Toasty.success(texto.getContext(), "Formato de NIT valido!", Toasty.LENGTH_SHORT).show();
                break;
            case "1661":
            case "Z001":
                String[] nit_uy = new String[2];
                Integer cantDigitos_uy;
                String ci;
                int digVerificador = -1;
                int[] factores = null;
                int suma = 0;
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
                        } else {
                            cedulaValidada = (checkdigit == digVerificador);
                        }
                        if (!cedulaValidada) {
                            Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }
                        Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                        break;
                    case "88":
                        if (texto.getText().toString().length() == 0) {
                            cedulaValidada = false;
                            texto.setError("RUT no puede estar vacio!");
                            return true;
                        }
                        if (!TextUtils.isDigitsOnly(texto.getText())) {
                            cedulaValidada = false;
                            texto.setError("RUT no puede tener ningún caracter, solo puede contener números");
                            return true;
                        }
                        nit_uy[0] = texto.getText().toString().substring(0, texto.getText().toString().length() - 1);
                        if (texto.getText().toString().replace("-", "").replace("0", "").length() == 0) {
                            cedulaValidada = false;
                            texto.setError("RUT no puede tener solo ceros!");
                            return true;
                        }
                        cantDigitos_uy = nit_uy[0].length();//Debe ser 11
                        ci = nit_uy[0].trim();

                        try {
                            digVerificador = Integer.parseInt(texto.getText().toString().substring(texto.getText().toString().length() - 1, texto.getText().toString().length()) + "");
                        } catch (Exception e) {

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
                            factores = new int[]{4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
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
                        } else if (checkdigit == 10) {
                            cedulaValidada = true;//String.valueOf(digVerificador) == "K";
                        } else {
                            cedulaValidada = (checkdigit == digVerificador);
                        }
                        if (!cedulaValidada) {
                            Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                            return true;
                        }
                        cedulaValidada = true;
                        Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                        break;
                    case "42":
                        cedulaValidada = true;
                        Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                        break;
                }
            case "F428"://Colombia
                if (texto.getText().toString().trim().length() < 4) {
                    cedulaValidada = false;
                    texto.setError("Formato Regimen " + tipoCedula + " invalido!");
                    return true;
                }
                /*if (texto.getText().toString().trim().length() < 14) {
                    String padded = "00000000000000".substring(texto.getText().toString().trim().length()) + texto.getText().toString().trim();
                    texto.setText(padded);
                }*/
                cedula = "[0-9A-Z]{4,18}";
                pattern = Pattern.compile(cedula);
                matcher = pattern.matcher(texto.getText());
                if (!matcher.matches()) {
                    Toasty.warning(texto.getContext(), "Formato Regimen " + tipoCedula + " invalido!", Toasty.LENGTH_SHORT).show();
                    cedulaValidada = false;
                    texto.setError("Formato Regimen " + tipoCedula + " invalido!");
                    return true;
                }
                cedulaValidada = true;
                Toasty.success(texto.getContext(), "Formato Regimen " + tipoCedula + " valido!", Toasty.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
    private static boolean ValidarIDFiscal(Context context,boolean... mensajes) {
        SearchableSpinner regimen = (SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-KATR3");
        MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
        if (idfiscal != null) {
            switch (PreferenceManager.getDefaultSharedPreferences(context).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad())) {
                case "F443":
                    if (idfiscal.getText().toString().trim().length() == 0) {
                        if (!listaCamposObligatorios.contains("W_CTE-STCD3")) {
                            idFiscalValidado = true;
                        } else {
                            idFiscalValidado = false;
                        }
                        return true;
                    }
                    Toasty.success(context,"Formato Regimen "+((OpcionSpinner)regimen.getSelectedItem()).getId()+" valido!",Toasty.LENGTH_SHORT).show();
                    break;
                case "F445":
                case "F451":
                    if (idfiscal.getText().toString().trim().length() == 0) {
                        if (!listaCamposObligatorios.contains("W_CTE-STCD3")) {
                            idFiscalValidado = true;
                        } else {
                            idFiscalValidado = false;
                        }
                        return true;
                    }
                    Toasty.success(context,"Formato Regimen "+((OpcionSpinner)regimen.getSelectedItem()).getId()+" valido!",Toasty.LENGTH_SHORT).show();
                    break;
                case "F446":
                case "1658":
                case "1657":
                    if(idfiscal.getText().toString().trim().length() == 0){
                        if (!listaCamposObligatorios.contains("W_CTE-STCD3")) {
                            idFiscalValidado = true;
                        } else {
                            idFiscalValidado = false;
                        }
                        return true;
                    }

                    String regexp_cui = "^[0-9]{4}\\s?[0-9]{5}\\s?[0-9]{4}$";
                    String regexp_pasaporte = "[0-9A-Z]{9,12}";
                    //String regexp_cf = "CF";
                    Pattern patternPasaporte = Pattern.compile(regexp_pasaporte);
                    Pattern patternCUI = Pattern.compile(regexp_cui);
                    Matcher matcherPasaporte = patternPasaporte.matcher(idfiscal.getText());
                    Matcher matcherCUI = patternCUI.matcher(idfiscal.getText());

                    if (!matcherPasaporte.matches() && !matcherCUI.matches()) {
                        idfiscal.setError("CUI/Pasaporte formato inválido!");
                        idFiscalValidado = false;
                        return true;
                    }
                    /*Despues del formato se realiza validacion MOD 11*/
                    if(((OpcionSpinner)regimen.getSelectedItem()).getId().equals("G2")){
                        //cedulaValidada = true; //No ocuparia validacion por que no es obligatorio??
                        // Se reemplazan los espacios en blanco en la cadena (si tiene)
                        String Cui = idfiscal.getText().toString().replace(" ", "");
                        // Extraemos el numero del DPI
                        String no = Cui.substring(0, 8);
                        // Extraemos el numero de Departamento
                        int depto = Integer.parseInt(Cui.substring(9, 11));
                        // Extraemos el numero de Municipio
                        int muni = Integer.parseInt(Cui.substring(11, 13));
                        // Se extra el numero validador
                        int ver = Integer.parseInt(Cui.substring(8, 9));
                        // Array con la cantidad de municipios que contiene cada departamento.
                        int munisPorDepto[] = {
                                /* 01 - Guatemala tiene:      */ 17 /* municipios. */,
                                /* 02 - El Progreso tiene:    */  8 /* municipios. */,
                                /* 03 - Sacatepéquez tiene:   */ 16 /* municipios. */,
                                /* 04 - Chimaltenango tiene:  */ 16 /* municipios. */,
                                /* 05 - Escuintla tiene:      */ 14 /* municipios. */,
                                /* 06 - Santa Rosa tiene:     */ 14 /* municipios. */,
                                /* 07 - Sololá tiene:         */ 19 /* municipios. */,
                                /* 08 - Totonicapán tiene:    */  8 /* municipios. */,
                                /* 09 - Quetzaltenango tiene: */ 24 /* municipios. */,
                                /* 10 - Suchitepéquez tiene:  */ 21 /* municipios. */,
                                /* 11 - Retalhuleu tiene:     */  9 /* municipios. */,
                                /* 12 - San Marcos tiene:     */ 30 /* municipios. */,
                                /* 13 - Huehuetenango tiene:  */ 33 /* municipios. */,
                                /* 14 - Quiché tiene:         */ 21 /* municipios. */,
                                /* 15 - Baja Verapaz tiene:   */  9 /* municipios. */,
                                /* 16 - Alta Verapaz tiene:   */ 17 /* municipios. */,
                                /* 17 - Petén tiene:          */ 14 /* municipios. */,
                                /* 18 - Izabal tiene:         */  5 /* municipios. */,
                                /* 19 - Zacapa tiene:         */ 11 /* municipios. */,
                                /* 20 - Chiquimula tiene:     */ 11 /* municipios. */,
                                /* 21 - Jalapa tiene:         */  7 /* municipios. */,
                                /* 22 - Jutiapa tiene:        */ 17 /* municipios. */
                        };
                        //Verificamos que no se haya ingresado 0 en la posicion de depto o municipio
                        if ((muni == 0 || depto == 0) || (muni == 0 && depto == 0)) {
                            idFiscalValidado = false;
                            if(depto == 0)
                                idfiscal.setError("CUI no válido departamento invalido "+depto+"!");
                            if(muni == 0)
                                idfiscal.setError("CUI no válido municipio invalido "+muni+"!");
                            return true;
                        } else {
                            //Si el numero de depto ingresado en la cadena es mayor 22 es cui invalido
                            System.out.println("munixdepto: " + munisPorDepto.length);
                            if (depto > munisPorDepto.length) {
                                idFiscalValidado = false;
                                idfiscal.setError("CUI no válido Departamento "+depto+" fuera de rango!");
                                return true;
                            } else {
                                //si depto es menor o igual a 22
                                System.out.println("Municipios maximos: " + munisPorDepto[depto - 1]);
                                //se valida que el municipio ingresado en la cadena este dentro del rango del depto
                                if (muni > munisPorDepto[depto - 1]) {
                                    idFiscalValidado = false;
                                    idfiscal.setError("CUI no válido municipio "+muni+" fuera de rango!");
                                    return true;
                                } else {
                                    // si es valido
                                    int total = 0;
                                    //Se realiza la siguiente Ooperación
                                    for (int i = 0; i < no.length(); i++) {
                                        System.out.println("-" + no.substring(i, i + 1));
                                        total += (Integer.parseInt(no.substring(i, i + 1))) * (i + 2);
                                    }
                                    // al total de la anterior operación se le saca el mod 11
                                    int modulo = total % 11;
                                    System.out.println("cui con modulo" + modulo);
                                    // Si el mod es igual al numero verificador el cui es valido , sino es invalido
                                    if (modulo != ver) {
                                        idFiscalValidado = false;
                                        idfiscal.setError("CUI no válido por digito verificador!");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    if(((OpcionSpinner)regimen.getSelectedItem()).getId().equals("G3")) {
                    }
                    Toasty.success(context,"Formato Regimen CUI/Pasaporte válido!",Toasty.LENGTH_SHORT).show();
                    break;

            }
        }
        idFiscalValidado = true;
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
                mensajeHorarios += "En el dia Lunes, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }
            if (diab1 != 0 && dibi2 != 0 && dibi1 == 0 && diab2 == 0 && diab1 > dibi2) {
                mensajeHorarios += "En el dia Martes, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }
            if (miab1 != 0 && mibi2 != 0 && mibi1 == 0 && miab2 == 0 && miab1 > mibi2) {
                mensajeHorarios += "En el dia Miercoles, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }
            if (doab1 != 0 && dobi2 != 0 && dobi1 == 0 && doab2 == 0 && doab1 > dobi2) {
                mensajeHorarios += "En el dia Jueves, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }
            if (frab1 != 0 && frbi2 != 0 && frbi1 == 0 && frab2 == 0 && frab1 > frbi2) {
                mensajeHorarios += "En el dia Viernes, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }
            if (saab1 != 0 && sabi2 != 0 && sabi1 == 0 && saab2 == 0 && saab1 > sabi2) {
                mensajeHorarios += "En el dia Sabado, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }
            if (soab1 != 0 && sobi2 != 0 && sobi1 == 0 && soab2 == 0 && soab1 > sobi2) {
                mensajeHorarios += "En el dia Domingo, la hora inicial de la mañana no puede ser mayor a la hora inicial de la tarde!\n";
            }

            if(moab1 != 0 && mobi2 != 0 && mobi1 == 0 && moab2 == 0 && moab1 > 120000){
                mensajeHorarios += "En el dia Lunes, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (diab1 != 0 && dibi2 != 0 && dibi1 == 0 && diab2 == 0 && diab1 > 120000) {
                mensajeHorarios += "En el dia Martes, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (miab1 != 0 && mibi2 != 0 && mibi1 == 0 && miab2 == 0 && miab1 > 120000) {
                mensajeHorarios += "En el dia Miercoles, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (doab1 != 0 && dobi2 != 0 && dobi1 == 0 && doab2 == 0 && doab1 > 120000) {
                mensajeHorarios += "En el dia Jueves, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (frab1 != 0 && frbi2 != 0 && frbi1 == 0 && frab2 == 0 && frab1 > 120000) {
                mensajeHorarios += "En el dia Viernes, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (saab1 != 0 && sabi2 != 0 && sabi1 == 0 && saab2 == 0 && saab1 > 120000) {
                mensajeHorarios += "En el dia Sabado, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }
            if (soab1 != 0 && sobi2 != 0 && sobi1 == 0 && soab2 == 0 && soab1 > 120000) {
                mensajeHorarios += "En el dia Domingo, la hora inicial de la mañana no puede ser mayor a las 12:00 MD!\n";
            }

            if(moab1 != 0 && mobi2 != 0 && mobi1 == 0 && moab2 == 0 && mobi2 < 120000){
                mensajeHorarios += "En el dia Lunes, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (diab1 != 0 && dibi2 != 0 && dibi1 == 0 && diab2 == 0 && dibi2 < 120000) {
                mensajeHorarios += "En el dia Martes, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (miab1 != 0 && mibi2 != 0 && mibi1 == 0 && miab2 == 0 && mibi2 < 120000) {
                mensajeHorarios += "En el dia Miercoles, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (doab1 != 0 && dobi2 != 0 && dobi1 == 0 && doab2 == 0 && dobi2 < 120000) {
                mensajeHorarios += "En el dia Jueves, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (frab1 != 0 && frbi2 != 0 && frbi1 == 0 && frab2 == 0 && frbi2 < 120000) {
                mensajeHorarios += "En el dia Viernes, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (saab1 != 0 && sabi2 != 0 && sabi1 == 0 && saab2 == 0 && sabi2 < 120000) {
                mensajeHorarios += "En el dia Sabado, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }
            if (soab1 != 0 && sobi2 != 0 && sobi1 == 0 && soab2 == 0 && sobi2 < 120000) {
                mensajeHorarios += "En el dia Domingo, la hora final de la tarde no puede ser menor a las 12:00 MD!\n";
            }

        } catch (Exception exc) {
            mensajeHorarios += "Revise los horarios digitados, no pueden ser calculados!";
        }
        if (mensajeHorarios != "") {
            return mensajeHorarios;
        }
        return "";
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
            adapter = new ViewPagerAdapter(getSupportFragmentManager(),context);
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            misTabs.setLayoutParams(lp);
            misTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
            misTabs.setTabGravity(TabLayout.GRAVITY_FILL);
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
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_UNKNOWN:
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
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_UNKNOWN:
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
                break;
        }
        return super.onKeyUp(keyCode, event);
    }*/
    private final BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toasty.success(context, "SMS enviado!", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toasty.error(context, "Error genérico! Revisar SIM Card o la señal.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toasty.warning(context, "Sin servicio de red! Revisar Modo Avion Deshabilitado.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toasty.error(context, "PDU nulo! Formato de mensaje inválido.", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toasty.error(context, "Radio apagada! Datos Moviles o SIM Deshabilitada", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toasty.error(context, "Error desconocido: " + getResultCode(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final BroadcastReceiver deliveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toasty.success(context, "SMS entregado", Toast.LENGTH_SHORT).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toasty.error(context, "No se pudo entregar el SMS!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private static byte[] keysArray = new byte[]{
            (byte)0x27,            (byte)0x30,            (byte)0x04,            (byte)0xA0,
            (byte)0x00,            (byte)0x0F,            (byte)0x93,            (byte)0x12,
            (byte)0xA0,            (byte)0xD1,            (byte)0x33,            (byte)0xE0,
            (byte)0x03,            (byte)0xD0,            (byte)0x00,            (byte)0xDf,
            (byte)0x00
    };
}
