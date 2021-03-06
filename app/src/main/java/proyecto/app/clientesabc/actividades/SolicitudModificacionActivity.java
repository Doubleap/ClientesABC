package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.TooltipCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;
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
import proyecto.app.clientesabc.clases.AdjuntoServidor;
import proyecto.app.clientesabc.clases.ConsultaClienteServidor;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.FileHelper;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Banco;
import proyecto.app.clientesabc.modelos.Comentario;
import proyecto.app.clientesabc.modelos.Contacto;
import proyecto.app.clientesabc.modelos.EditTextDatePicker;
import proyecto.app.clientesabc.modelos.Impuesto;
import proyecto.app.clientesabc.modelos.Interlocutor;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.Visitas;

import static android.support.design.widget.TabLayout.GRAVITY_CENTER;
import static android.support.design.widget.TabLayout.INDICATOR_GRAVITY_TOP;
import static android.support.design.widget.TabLayout.INVISIBLE;
import static android.support.design.widget.TabLayout.OnClickListener;
import static android.support.design.widget.TabLayout.OnFocusChangeListener;
import static android.support.design.widget.TabLayout.OnTouchListener;
import static android.support.design.widget.TabLayout.TEXT_ALIGNMENT_CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SolicitudModificacionActivity extends AppCompatActivity {

    final static int alturaFilaTableView = 75;
    static String tipoSolicitud ="";
    static String idSolicitud = "";
    static String codigoCliente = "";
    static String idForm = "";
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
    static boolean firma;
    static boolean modificable;
    static boolean correoValidado;
    static boolean cedulaValidada;
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

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        firma = false;
        modificable = true;
        correoValidado = true;
        cedulaValidada = true;

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

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.botella_coca_header_der,null));


        if(idSolicitud != null){
            setTitle("Solicitud");
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
        if(solicitudSeleccionada.size() > 0) {
            firma = true;
            correoValidado = true;
            cedulaValidada = true;
            if(solicitudSeleccionada.get(0).get("ESTADO").equals("Pendiente")
                    ||solicitudSeleccionada.get(0).get("ESTADO").equals("Rechazado")
                    ||solicitudSeleccionada.get(0).get("ESTADO").equals("Aprobado")){
                modificable = false;
            }
        }else{
            correoValidado = true;
            cedulaValidada = true;
            if(!tipoSolicitud.equals("1") && !tipoSolicitud.equals("6") && !tipoSolicitud.equals("26") ){
                firma = true;
            }
        }

        configExcepciones.clear();
        listaCamposDinamicos.clear();
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
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(intent, 200);

                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", getResources().getString(R.string.no_activity));
                        }
                        return true;
                    case R.id.action_save:
                        int numErrores = 0;
                        String mensajeError="";
                        //Validacion de Datos Obligatorios Automatico
                        for(int i=0; i < listaCamposObligatorios.size(); i++) {
                            try{
                                MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                String valor = tv.getText().toString().trim();

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
                                    if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LAT") && !ValidarCoordenadaY(tv)){
                                        numErrores++;
                                        mensajeError += "- Formato Coordenada Y invalido\n";
                                    }
                                    if(listaCamposObligatorios.get(i).trim().equals("W_CTE-ZZCRMA_LONG") && !ValidarCoordenadaX(tv)){
                                        numErrores++;
                                        mensajeError += "- Formato Coordenada X invalido\n";
                                    }
                                }
                            }catch(Exception e){
                                Spinner combo = ((Spinner) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                if(combo.getSelectedItem() != null) {
                                    String valor = ((OpcionSpinner)combo.getAdapter().getItem((int) combo.getSelectedItemId())).getId();

                                    if (combo.getAdapter().getCount() == 0 || (combo.getAdapter().getCount() > 0 && valor.isEmpty() )) {
                                        ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
                                        //combo.setError("El campo "+combo.getHint()+" es obligatorio!");
                                        numErrores++;
                                        mensajeError += "- "+combo.getTag()+"\n";
                                    }
                                }else{
                                    TextView error = (TextView)combo.getSelectedView();
                                    error.setError("El campo es obligatorio!");
                                    numErrores++;
                                    mensajeError += "- "+combo.getTag()+"\n";
                                }
                            }
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
                        //int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPR");
                        int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDD");
                        if(visitasSolicitud.size() > 0 && visitasSolicitud.get(indiceReparto).getRuta().trim().length() < 6){
                            numErrores++;
                            mensajeError += "- Falta ruta de reparto en planes de visita!\n";
                        }
                        if(visitasSolicitud.size() > 0) {
                            //Al menos 1 dia de visita
                            if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_L")) != null) {
                                if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_L")).getText().toString().isEmpty())
                                    if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_K")).getText().toString().isEmpty())
                                        if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_M")).getText().toString().isEmpty())
                                            if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_J")).getText().toString().isEmpty())
                                                if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_V")).getText().toString().isEmpty())
                                                    if (((TextInputEditText) mapeoCamposDinamicos.get("ZPV_S")).getText().toString().isEmpty()) {
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
                        if(!firma && tipoSolicitud.equals("26")){
                            numErrores++;
                            mensajeError += "- El cliente nuevo debe firmar las políticas de privacidad!\n";
                        }
                        //Validacion de correo
                        if(!correoValidado){
                            numErrores++;
                            mensajeError += "- Formato de correo Inválido!\n";
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
        new MostrarFormulario(this).execute();

        if(solicitudSeleccionada.size() == 0 ) {
            WeakReference<Context> weakRefs1 = new WeakReference<Context>(this);
            WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(this);
            ConsultaClienteServidor c = new ConsultaClienteServidor(weakRefs1, weakRefAs1, codigoCliente);
            c.execute();
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
        adjuntosSolicitud = new ArrayList<>();
        comentarios = new ArrayList<>();
        //notificantesSolicitud = new ArrayList<Adjuntos>();

        contactosSolicitud_old = new ArrayList<>();
        impuestosSolicitud_old = new ArrayList<>();
        interlocutoresSolicitud_old = new ArrayList<>();
        bancosSolicitud_old = new ArrayList<>();
        visitasSolicitud_old = new ArrayList<>();
        adjuntosSolicitud_old = new ArrayList<>();

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
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if(uri == null){
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        ContentResolver cR =  getContentResolver();
                        String type = cR.getType(uri);
                        String name = getFileName(cR, uri);
                        byte[] inputData = getBytes(iStream);
                        File file = null;
                        try {
                            file = new File( Environment.getExternalStorageDirectory().getAbsolutePath());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream fos = new FileOutputStream(file+"//"+name);
                            fos.write(inputData);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("thumbnail", e.getMessage());
                        }
                        File file2 = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"//"+name);


                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;

                        BitmapFactory.decodeFile(file2.getAbsolutePath(), options);
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;
                        /*CROP*/
                        try {
                            // call the standard crop action intent (the user device may not
                            // support it)
                            Intent cropIntent = new Intent("com.android.camera.action.CROP");
                            // indicate image type and Uri
                            cropIntent.setDataAndType(mPhotoUri, "image/*");
                            // set crop properties
                            cropIntent.putExtra("crop", true);
                            // indicate aspect of desired crop
                            cropIntent.putExtra("aspectX", 1);
                            cropIntent.putExtra("aspectY", 1.33);
                            // indicate output X and Y
                            //cropIntent.putExtra("outputX", imageWidth);
                            //cropIntent.putExtra("outputY", imageHeight);
                            // retrieve data on return
                            cropIntent.putExtra("return-data", true);
                            // start the activity - we handle returning in onActivityResult
                            startActivityForResult(cropIntent, 200);
                        }
                        // respond to users whose devices do not support the crop action
                        catch (ActivityNotFoundException anfe) {
                            Toast toast = Toast
                                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        /*END CROP*/
                    } catch (IOException e) {
                        Toasty.error(getBaseContext(),"Error al asociar el documento a la solicitud").show();
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if(uri == null){
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        ContentResolver cR =  getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String type = cR.getType(uri);
                        String name = getFileName(cR, uri);
                        byte[] inputData = getBytes(iStream);
                        mDBHelper.addAdjuntoSolicitud(type, name, inputData);
                        Toasty.success(getBaseContext(),"Documento asociado correctamente.").show();
                    } catch (IOException e) {
                        Toasty.error(getBaseContext(),"Error al adjuntar el documento a la solicitud").show();
                        e.printStackTrace();
                    }

                }
                break;
            case 100:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //Agregar al tableView del UI
                    try {
                        byte[] inputData = getBytes(iStream);
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, data.getType(), data.getExtras().getString("ImageName"), inputData);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(getBaseContext(), adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        HorizontalScrollView hsvn = (HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos");
                        MostrarGaleriaAdjuntosHorizontal(hsvn, hsvn.getContext(), SolicitudModificacionActivity.this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CheckBox politica = (CheckBox) mapeoCamposDinamicos.get("politica");
                    politica.setChecked(true);
                    firma = true;
                    politica.setEnabled(false);
                    Toasty.success(getBaseContext(), "Documento asociado correctamente.").show();
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if(uri == null){
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toasty.error(getBaseContext(), "Imagen seleccionada ya no existe en el dispositivo!").show();
                        return;
                    }
                    try {
                        ContentResolver cR =  getContentResolver();
                        String type = cR.getType(uri);
                        String name = getFileName(cR, uri);
                        byte[] inputData = getBytes(iStream);
                        File file = null;
                        try {
                            file = new File( Environment.getExternalStorageDirectory().getAbsolutePath());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream fos = new FileOutputStream(file+"//"+name);
                            fos.write(inputData);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("thumbnail", e.getMessage());
                        }
                        File file2 = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"//"+name);
                        file2 = FileHelper.saveBitmapToFile(file2);

                        byte[] bytesArray = new byte[(int) file2.length()];

                        FileInputStream fis = new FileInputStream(file2);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                        file2.delete();
                        //Agregar al tableView del UI
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, type, name, bytesArray);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(getBaseContext(), adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        HorizontalScrollView hsvn = (HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos");
                        MostrarGaleriaAdjuntosHorizontal(hsvn, hsvn.getContext(), SolicitudModificacionActivity.this);

                        //Intento de borrar el archivo que se guarda automatico en Pictures
                        File file3 = new File( Environment.getExternalStorageDirectory().getAbsolutePath()+"//Pictures//"+name);
                        file3.delete();

                        Toasty.success(getBaseContext(),"Documento asociado correctamente.").show();
                    } catch (IOException e) {
                        Toasty.error(getBaseContext(),"Error al asociar el documento a la solicitud").show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> title = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            List<String> pestanas = mDBHelper.getPestanasFormulario(tipoSolicitud);
            title.addAll(pestanas);
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
            LinearLayout ll = view.findViewById(R.id.miPagina);
            String nombre = Objects.requireNonNull(Objects.requireNonNull(((ViewPager) container).getAdapter()).getPageTitle(position)).toString().trim();

            if(nombre.equals("Datos Generales") || nombre.equals("Informacion General")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"D");

            }else
            if(nombre.equals("Facturación")|| nombre.equals("Facturacion")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"F");
            }else
            if(nombre.equals("Ventas")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"V");
            }else
            if(nombre.equals("Marketing")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"M");
            }else
            if(nombre.equals("Creditos") || nombre.equals("Créditos")  || nombre.equals("Crédito")  || nombre.equals("Credito")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"C");
            }else
            if(nombre.equals("Adjuntos") || nombre.equals("Adicionales")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"Z");
            }else
            if(nombre.toLowerCase().contains("equipo") || nombre.toLowerCase().contains("frio")|| nombre.toLowerCase().contains("eq.")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"E");
            }
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
        //LLenado Automatico de campos x pestana.
        @SuppressLint("ClickableViewAccessibility")
        public void LlenarPestana(DataBaseHelper db, View _ll, String tipoFormulario, String pestana) {
            //View view = inflater.inflate(R.layout.pagina_formulario, container, false);
            String seccionAnterior = "";
            LinearLayout ll = (LinearLayout)_ll;
            //DataBaseHelper db = new DataBaseHelper(getContext());
            final ArrayList<HashMap<String, String>> campos = db.getCamposPestana(tipoFormulario, pestana);

            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < campos.size(); i++) {
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
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").replace(" ","").trim().toLowerCase().equals("grid")) {
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
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_survey,null), null);

                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDialogEncuestaCanales(getContext());
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

                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
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
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_survey,null), null);
                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            displayDialogEncuestaGec(getContext());
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

                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
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
                    if(campos.get(i).get("modificacion").trim().equals("2") && campos.get(i).get("sup").trim().length() == 0){
                        checkbox_old.setEnabled(false);
                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(), checkbox_old);
                    }

                    CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setLayoutParams(lp);
                    checkbox.setText(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        checkbox.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        checkbox.setEnabled(false);
                        //checkbox.setVisibility(View.GONE);
                    }
                    fila.addView(checkbox_old);
                    fila.addView(checkbox);
                    ll.addView(fila);
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                    //Excepciones de visualizacion y configuracion de campos dados por la tabla ConfigCampos
                    int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                    if(excepcion >= 0) {
                        HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                        if (configExcepcion.get("vis").equals("1") || configExcepcion.get("vis").equals("X")) {
                            checkbox.setEnabled(false);
                        }else if(configExcepcion.get("vis") != null){
                            checkbox.setEnabled(true);
                        }
                        if (configExcepcion.get("sup").equals("1") || configExcepcion.get("sup").equals("X")) {
                            checkbox.setVisibility(View.GONE);
                        }else if(configExcepcion.get("sup") != null){
                            checkbox.setVisibility(View.VISIBLE);
                        }
                    }
                    if(solicitudSeleccionada.size() > 0){
                        if(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim().length() > 0)
                            checkbox.setChecked(true);
                        if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()).trim().length() > 0)
                            checkbox.setChecked(true);
                        if(!modificable){
                            checkbox.setEnabled(false);
                        }
                    }
                }else if (campos.get(i).get("tabla")!= null && campos.get(i).get("tabla").trim().length() > 0) {
                    //Tipo ComboBox/SelectBox/Spinner
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setWeightSum(10);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,10f));

                    final TextView label = new TextView(getContext());
                    label.setText(campos.get(i).get("descr"));
                    label.setTextAppearance(R.style.AppTheme_TextFloatLabelAppearance);
                    TableRow.LayoutParams lpl = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    lpl.setMargins(35, 5, 0, 0);
                    label.setPadding(0,0,0,0);
                    label.setLayoutParams(lpl);

                    final SearchableSpinner combo = new SearchableSpinner(getContext(), null);
                    combo.setTitle("Buscar");
                    combo.setPositiveButton("Cerrar");
                    combo.setTag(campos.get(i).get("descr"));
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    lp.setMargins(0, -10, 0, 25);
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
                        if(!campos.get(i).get("campo").trim().equals("W_CTE-LZONE")) {
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }
                    }

                    //Si son catalogos de equipo frio debo las columnas de ID y Descripcion estan en otros indice de columnas

                    ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim());
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
                            selectedIndex = j;
                            if(!campos.get(i).get("campo").trim().equals("W_CTE-VWERK")) {
                                combo.setEnabled(false);
                                combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                            }
                        }

                    }
                    // Creando el adaptador(opciones) para el comboBox deseado
                    ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, listaopciones);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    combo.setAdapter(dataAdapter);
                    combo.setSelection(selectedIndex);

                    //Campo de regimen fiscal, se debe cambiar el formato de cedula segun el tipo de cedula
                    if(campos.get(i).get("campo").trim().equals("W_CTE-KATR3")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                MaskedEditText cedula = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD1");
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                if(cedula != null){
                                    //cedula.setFilters(new InputFilter[]{new RegexInputFilter("[A-Z-a-z]")});
                                    if(opcion.getId().equals("C1")){
                                        cedula.setMask("0#-####-####-##");
                                    }
                                    if(opcion.getId().equals("C2")){
                                        cedula.setMask("#-###-######");
                                    }
                                    if(opcion.getId().equals("C3")){
                                        cedula.setMask("##-####-####-##");
                                    }
                                    cedula.setOnFocusChangeListener(new OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            if (!hasFocus) {
                                                ValidarCedula(v,opcion.getId());
                                            }
                                        }
                                    });
                                }
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
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
                                ArrayList<HashMap<String, String>> valores = mDBHelper.getValoresKOFSegunZonaVentas(((OpcionSpinner)combo.getSelectedItem()).getId());
                                if(valores.size() > 0) {
                                    ((Spinner) mapeoCamposDinamicos.get("W_CTE-VWERK")).setSelection(VariablesGlobales.getIndex(((Spinner) mapeoCamposDinamicos.get("W_CTE-VWERK")), valores.get(0).get("VWERK")));
                                    if (position == 0)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                                }else{
                                    Toasty.error(getContext(),"No se pudo obtener los datos de KOF segun zona de ventas").show();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-VWERK")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Spinner zona_transporte = (Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE");
                                String valor_centro_suministro = ((OpcionSpinner)combo.getSelectedItem()).getId().trim();
                                ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'");
                                // Creando el adaptador(opciones) para el comboBox deseado
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
                                }
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-KVGR5")){
                        //TODO aqui se debe cambiar si se quiere trabajar con diferentes tipos de 'PR'
                        if(solicitudSeleccionada.size() == 0) {
                            combo.setSelection(VariablesGlobales.getIndex(combo, "PR"));
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }else{
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                /*if (solicitudSeleccionada.size() == 0) {
                                    visitasSolicitud = mDBHelper.DeterminarPlanesdeVisita(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG", ""), opcion.getId());

                                    tb_visitas.setDataAdapter(new VisitasTableAdapter(view.getContext(), visitasSolicitud));
                                    if (tb_visitas.getLayoutParams() != null) {
                                        tb_visitas.getLayoutParams().height = 50;
                                        tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height + ((alturaFilaTableView ) * visitasSolicitud.size());
                                    }
                                }*/
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
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
                                    DireccionCorta();
                                    if (position == 0 && ((TextView) parent.getSelectedView()) != null)
                                        ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
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
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
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
                    if(campos.get(i).get("modificacion").trim().equals("2") && campos.get(i).get("sup").trim().length() == 0){
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

                        btnAyudai.setOnTouchListener(new OnTouchListener()
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
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }

                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(),combo_old);
                    }

                    if(combo_old != null && campos.get(i).get("modificacion").trim().equals("2")) {
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
                        listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), combo);
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
                                    String valor_centro_suministro = ((OpcionSpinner)parent.getSelectedItem()).getId().trim();
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'");
                                    // Creando el adaptador(opciones) para el comboBox deseado
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
                                    }
                                }
                                ReplicarValorSpinner(parent,nombreCampo+"1",position);
                                if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
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
                                    String valor_centro_suministro = ((OpcionSpinner)parent.getSelectedItem()).getId().trim();
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'");
                                    // Creando el adaptador(opciones) para el comboBox deseado
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
                                    }
                                }
                                ReplicarValorSpinner(parent,nombreCampo,position);
                                if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
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
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Toasty.info(getContext(),"Nothing Selected").show();
                                }
                            });
                        }
                    }
                    //Excepciones de visualizacion y configuracionde campos dados por la tabla ConfigCampos
                    int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                    if(excepcion >= 0) {
                        HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                        if (configExcepcion.get("vis").equals("1") || configExcepcion.get("vis").equals("X")) {
                            combo.setEnabled(false);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_disabled, null));
                        }else if(configExcepcion.get("vis") != null){
                            combo.setEnabled(true);
                            combo.setBackground(getResources().getDrawable(R.drawable.spinner_background, null));
                        }
                        if (configExcepcion.get("sup").equals("1") || configExcepcion.get("sup").equals("X")) {
                            combo.setVisibility(View.GONE);
                        }else if(configExcepcion.get("sup") != null){
                            combo.setVisibility(View.VISIBLE);
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
                    label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                    label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);

                    final MaskedEditText et = new MaskedEditText(getContext(),null);

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
                    lp.setMargins(0, 15, 0, 15);

                    et.setLayoutParams(lp);
                    et.setPadding(20, 5, 20, 5);
                    Drawable d = getResources().getDrawable(R.drawable.textbackground, null);
                    et.setBackground(d);
                    if(campos.get(i).get("vis").trim().length() > 0){
                        et.setEnabled(false);
                        et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled,null));
                        //et.setVisibility(View.GONE);
                    }
                    et.setMaxLines(1);

                    if(campos.get(i).get("datatype") != null && campos.get(i).get("datatype").equals("char")) {

                        et.setFilters(new InputFilter[] { new InputFilter.LengthFilter( Integer.valueOf(campos.get(i).get("maxlength")) ) });
                        if(campos.get(i).get("campo").trim().equals("W_CTE-STCD3")){
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            et.setFilters(new InputFilter[] { new InputFilter.LengthFilter( 18 ) });
                        }else{
                            et.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                    }else if(campos.get(i).get("datatype") != null && campos.get(i).get("datatype").equals("decimal")) {
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et.setFilters(new InputFilter[] { new InputFilter.LengthFilter( Integer.valueOf(campos.get(i).get("numeric_precision")) ) });
                    }


                    InputFilter[] editFilters = et.getFilters();
                    InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
                    System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                    newFilters[editFilters.length] = new InputFilter.AllCaps();
                    et.setFilters(newFilters);
                    et.setAllCaps(true);

                    TableRow.LayoutParams textolp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    TableRow.LayoutParams btnlp = new TableRow.LayoutParams(75, 75);
                    if(campos.get(i).get("tooltip") != null && campos.get(i).get("tooltip") != ""){
                        textolp.setMargins(0,0,25,0);
                        //label.setLayoutParams(textolp);
                        btnAyuda = new ImageView(getContext());
                        btnAyuda.setBackground(getResources().getDrawable(R.drawable.icon_ayuda,null));
                        btnlp.setMargins(0,35,75,0);
                        btnAyuda.setLayoutParams(btnlp);
                        btnAyuda.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        btnAyuda.setForegroundGravity(GRAVITY_CENTER);
                        TooltipCompat.setTooltipText(btnAyuda, campos.get(i).get("tooltip"));
                    }
                    //Le cae encima al valor default por el de la solicitud seleccionada
                    if(solicitudSeleccionada.size() > 0){
                        et.setText(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim());
                        if(!modificable){
                            et.setEnabled(false);
                            et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled,null));
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
                            et.setOnFocusChangeListener(new OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        ReplicarValor(v, campoAReplicar);
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
                    if(campos.get(i).get("modificacion").trim().equals("2") && campos.get(i).get("sup").trim().length() == 0){
                        //textbox de valor viejo
                        TableRow.LayoutParams lp_old = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
                        lp_old.setMargins(0, 15, 0, 15);
                        et_old.setLayoutParams(lp_old);
                        et_old.setPadding(20, 5, 20, 5);
                        et_old.setBackground(getResources().getDrawable(R.drawable.textbackground_old,null));
                        //if(cliente != null && cliente.get(campos.get(i).get("campo")) != null)
                            //et_old.setText(cliente.get(campos.get(i).get("campo").trim()).getAsString());

                        Button btnAyudai=null;
                        TableRow.LayoutParams textolp2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                        TableRow.LayoutParams btnlp2 = new TableRow.LayoutParams(75, 75);
                            textolp2.setMargins(0,0,5,0);
                            //label.setLayoutParams(textolp2);
                            //label_old.setLayoutParams(textolp2);
                            btnAyudai = new Button(getContext());
                            btnAyudai.setBackground(getResources().getDrawable(R.drawable.icon_ver_viejo,null));
                            btnlp2.setMargins(0,35,5,0);
                            btnAyudai.setLayoutParams(btnlp2);
                            btnAyudai.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            btnAyudai.setForegroundGravity(GRAVITY_CENTER);

                        btnAyudai.setOnTouchListener(new OnTouchListener()
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

                        et.setOnTouchListener(new OnTouchListener() {
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
                                tocables.get(x).setOnTouchListener(new OnTouchListener() {
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
                        et.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    correoValidado = isValidEmail(v);
                                }
                            }
                        });
                    }
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), et);
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
                        if (configExcepcion.get("vis").equals("1") || configExcepcion.get("vis").equals("X")) {
                            et.setEnabled(false);
                            et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                        }else if(configExcepcion.get("vis") != null){
                            et.setEnabled(true);
                            et.setBackground(getResources().getDrawable(R.drawable.textbackground, null));
                        }
                        if (configExcepcion.get("sup").equals("1") || configExcepcion.get("sup").equals("X")) {
                            et.setVisibility(View.GONE);
                        }else if(configExcepcion.get("sup") != null){
                            et.setVisibility(View.VISIBLE);
                        }
                    }
                    //if(cliente != null && cliente.get(campos.get(i).get("campo")) != null)
                        //et.setText(cliente.get(campos.get(i).get("campo").trim()).getAsString());
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

                ArrayList<OpcionSpinner> opciones = db.getDatosCatalogoParaSpinner("aprobadores"," fxp.id_Flujo = "+tipoSolicitud);

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
                    }
                    LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkbox.setLayoutParams(clp);
                    checkbox.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_privacy,null), null);
                    ll.addView(checkbox);
                    checkbox.setOnClickListener(new OnClickListener() {
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

        private void Aceptacion(View v) {
            Intent intent = new Intent(getContext(),FirmaActivity.class);
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

            ll.addView(seccion_layout);

            RelativeLayout rl = new RelativeLayout(getContext());
            CoordinatorLayout.LayoutParams rlp = new CoordinatorLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rl.setLayoutParams(rlp);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) rl.getLayoutParams();
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior(getContext(), null));

            switch(campo.get("campo").trim()){
                case "W_CTE-CONTACTOS":
                    //bloque_contacto = tb_contactos;
                    if(modificable) {
                        seccion_header.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                displayDialogContacto(getContext(), null);
                            }
                        });
                        btnAddBloque.setOnClickListener(new OnClickListener() {
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
                        btnAddBloque.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                displayDialogBancos(getContext(), null);
                            }
                        });
                        seccion_header.setOnClickListener(new OnClickListener() {
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
                    if(solicitudSeleccionada.size() > 0){
                        visitasSolicitud.clear();
                        visitasSolicitud_old.clear();
                        visitasSolicitud = mDBHelper.getVisitasDB(idSolicitud);
                        visitasSolicitud_old = mDBHelper.getVisitasOldDB(idSolicitud);
                    }else{

                    }
                    //Adaptadores
                    if(visitasSolicitud != null) {
                        VisitasTableAdapter stda = new VisitasTableAdapter(getContext(), visitasSolicitud);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height+(visitasSolicitud.size()*alturaFilaTableView);
                        tb_visitas.setDataAdapter(stda);
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
                    rl.addView(tb_visitas);
                    ll.addView(rl);
                    //Textos de dias de visita
                    //Desplegar textos para las secuencias de los dias de visita de la preventa.
                    //TODO Generar segun la Modalidad de venta seleccionada para usuarios tipo jefe de ventas y no preventas
                    final String[] diaLabel = {"L","K","M","J","V","S","D"};
                    int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPV");
                    int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZJV");
                    int indiceKafe = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZKV");
                    int indiceTeleventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZTV");
                    int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDD");
                    int totalvp_preventa = 1;
                    if(indicePreventa != -1){
                        totalvp_preventa++;
                    }
                    if(indiceEspecializada != -1){
                        totalvp_preventa++;
                    }
                    if(indiceTeleventa != -1){
                        totalvp_preventa++;
                    }
                    if(visitasSolicitud.size() == 0){
                        totalvp_preventa = 3;
                    }
                    String tipoVisitaActual = "ZPV";
                    for (int i = 0; i < totalvp_preventa; i++) {
                        if(visitasSolicitud.size() == 0) {
                            if (i == 0)
                                tipoVisitaActual = "ZPV";
                            if (i == 1)
                                tipoVisitaActual = "ZJV";
                            if (i == 2)
                                tipoVisitaActual = "ZTV";
                            if (i == 3)
                                tipoVisitaActual = "ZKV";
                        }else{
                            tipoVisitaActual = visitasSolicitud.get(i).getVptyp().trim();
                            if(tipoVisitaActual.equals("ZDD")){
                                continue;
                            }
                        }
                        CardView seccion_visitas = new CardView(Objects.requireNonNull(getContext()));
                        mapeoVisitas.put(tipoVisitaActual,seccion_visitas);

                        TextView header_visitas = new TextView(getContext());
                        header_visitas.setAllCaps(true);
                        header_visitas.setText("Dias de Visita Preventa "+tipoVisitaActual);
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
                        ll.addView(seccion_visitas);

                        TableLayout v_ll = new TableLayout(getContext());
                        LinearLayout.LayoutParams hlpll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        v_ll.setOrientation(LinearLayout.HORIZONTAL);
                        v_ll.setLayoutParams(hlpll);
                        TableRow tr = new TableRow(getContext());
                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 6f));
                        tr.setPadding(0, 0, 0, 0);

                        for (int x = 0; x < 6; x++) {

                            TextInputLayout label = new TextInputLayout(getContext());
                            label.setHint("" + diaLabel[x]);
                            label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                            label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);
                            label.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                            label.setPadding(0, 0, 0, 0);

                            final TextInputEditText et = new TextInputEditText(getContext());
                            mapeoCamposDinamicos.put(tipoVisitaActual + "_" + diaLabel[x], et);
                            et.setMaxLines(1);
                            et.setTextSize(16);

                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 10, 10, 10);
                            et.setPadding(1, 0, 1, 0);

                            et.setLayoutParams(lp);
                            if (!modificable || !PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_TIPORUTA","").equals(tipoVisitaActual)) {
                                et.setEnabled(false);
                                et.setBackground(getResources().getDrawable(R.drawable.textbackground_disabled, null));
                            }

                            if(solicitudSeleccionada.size() > 0){
                                int indiceActual = -1;
                                if(visitasSolicitud.size() == 0) {
                                    if(i==0)
                                        indiceActual = indicePreventa;
                                    if(i==1)
                                        indiceActual = indiceEspecializada;
                                    if(i==2)
                                        indiceActual = indiceTeleventa;
                                    if(i==3)
                                        indiceActual = indiceKafe;
                                }else{
                                    indiceActual = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,tipoVisitaActual);
                                }
                                switch (x) {
                                    case 0:
                                        et.setText(visitasSolicitud.get(indiceActual).getLun_a());
                                        break;
                                    case 1:
                                        et.setText(visitasSolicitud.get(indiceActual).getMar_a());
                                        break;
                                    case 2:
                                        et.setText(visitasSolicitud.get(indiceActual).getMier_a());
                                        break;
                                    case 3:
                                        et.setText(visitasSolicitud.get(indiceActual).getJue_a());
                                        break;
                                    case 4:
                                        et.setText(visitasSolicitud.get(indiceActual).getVie_a());
                                        break;
                                    case 5:
                                        et.setText(visitasSolicitud.get(indiceActual).getSab_a());
                                        break;
                                    case 6:
                                        et.setText(visitasSolicitud.get(indiceActual).getDom_a());
                                        break;
                                }
                            }

                            //et.setPadding(20, 5, 20, 5);
                            Drawable d = getResources().getDrawable(R.drawable.textbackground_min_padding, null);
                            et.setBackground(d);
                            final int finalX = x;
                            et.setTag(tipoVisitaActual);
                            et.setOnFocusChangeListener(new OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {

                                    int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, v.getTag().toString());
                                    int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZDD");
                                    //int indiceEspecializada = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud, "ZJV");

                                    final int finalIndicePreventa = indicePreventa;
                                    final int finalIndiceReparto = indiceReparto;

                                    if (!hasFocus) {
                                        int diaReparto = 0;
                                        if ((finalX + 1) > 5) {
                                            diaReparto = ((finalX + 1) - 6);
                                        } else {
                                            diaReparto = (finalX + 1);
                                        }
                                        Visitas visitaPreventa = visitasSolicitud.get(finalIndicePreventa);
                                        Visitas visitaReparto = visitasSolicitud.get(finalIndiceReparto);
                                        //Visitas visitaEspecializada = visitasSolicitud.get(finalIndiceEspecializada);
                                        if (!((TextView) v).getText().toString().equals("") && Integer.valueOf(((TextView) v).getText().toString()) > 1440) {
                                            switch (finalX) {
                                                case 0:
                                                    visitaPreventa.setLun_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setLun_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                                case 1:
                                                    visitaPreventa.setMar_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setMar_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                                case 2:
                                                    visitaPreventa.setMier_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setMier_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                                case 3:
                                                    visitaPreventa.setJue_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setJue_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                                case 4:
                                                    visitaPreventa.setVie_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setVie_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                                case 5:
                                                    visitaPreventa.setSab_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setSab_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                                case 6:
                                                    visitaPreventa.setDom_a(getResources().getString(R.string.max_secuencia));
                                                    visitaPreventa.setDom_de(getResources().getString(R.string.max_secuencia));
                                                    break;
                                            }
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
                                            ((TextView) v).setText(getResources().getString(R.string.max_secuencia));
                                            Toasty.warning(getContext(), R.string.error_max_secuencia).show();
                                        }

                                        //Si el valor es vacio, borrar si existe el dia
                                        if (((TextView) v).getText().toString().trim().equals("")) {
                                            switch (finalX) {
                                                case 0:
                                                    visitaPreventa.setLun_a("");
                                                    visitaPreventa.setLun_de("");
                                                    break;
                                                case 1:
                                                    visitaPreventa.setMar_a("");
                                                    visitaPreventa.setMar_de("");
                                                    break;
                                                case 2:
                                                    visitaPreventa.setMier_a("");
                                                    visitaPreventa.setMier_de("");
                                                    break;
                                                case 3:
                                                    visitaPreventa.setJue_a("");
                                                    visitaPreventa.setJue_de("");
                                                    break;
                                                case 4:
                                                    visitaPreventa.setVie_a("");
                                                    visitaPreventa.setVie_de("");
                                                    break;
                                                case 5:
                                                    visitaPreventa.setSab_a("");
                                                    visitaPreventa.setSab_de("");
                                                    break;
                                                case 6:
                                                    visitaPreventa.setDom_a("");
                                                    visitaPreventa.setDom_de("");
                                                    break;
                                            }
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
                                        } else {
                                            String secuenciaSAP = VariablesGlobales.SecuenciaToHora(((TextView) v).getText().toString());
                                            switch (finalX) {
                                                case 0:
                                                    visitaPreventa.setLun_a(secuenciaSAP);
                                                    visitaPreventa.setLun_de(secuenciaSAP);
                                                    break;
                                                case 1:
                                                    visitaPreventa.setMar_a(secuenciaSAP);
                                                    visitaPreventa.setMar_de(secuenciaSAP);
                                                    break;
                                                case 2:
                                                    visitaPreventa.setMier_a(secuenciaSAP);
                                                    visitaPreventa.setMier_de(secuenciaSAP);
                                                    break;
                                                case 3:
                                                    visitaPreventa.setJue_a(secuenciaSAP);
                                                    visitaPreventa.setJue_de(secuenciaSAP);
                                                    break;
                                                case 4:
                                                    visitaPreventa.setVie_a(secuenciaSAP);
                                                    visitaPreventa.setVie_de(secuenciaSAP);
                                                    break;
                                                case 5:
                                                    visitaPreventa.setSab_a(secuenciaSAP);
                                                    visitaPreventa.setSab_de(secuenciaSAP);
                                                    break;
                                                case 6:
                                                    visitaPreventa.setDom_a(secuenciaSAP);
                                                    visitaPreventa.setDom_de(secuenciaSAP);
                                                    break;
                                            }
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

                                    }
                                    RecalcularDiasDeReparto(getContext());
                                }
                            });
                            tr.addView(label);
                            label.addView(et);
                        }

                        v_ll.addView(tr);
                        ll.addView(v_ll);
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
                        btnAddBloque.setOnClickListener(new OnClickListener() {
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

                    rl.addView(tb_adjuntos);

                    //Horizontal View de adjuntos
                    HorizontalScrollView hsv = new HorizontalScrollView(getContext());
                    MostrarGaleriaAdjuntosHorizontal(hsv, getContext(), getActivity());

                    rl.addView(hsv);
                    ll.addView(rl);
                    mapeoCamposDinamicos.put("GaleriaAdjuntos", hsv);
                    break;
                case "W_CTE-NOTIFICANTES":
                    break;
            }
        }

    }

    public static void MostrarGaleriaAdjuntosHorizontal(HorizontalScrollView hsv, final Context context, final Activity activity) {
        hsv.removeAllViews();
        hsv.setBackground(context.getResources().getDrawable(R.drawable.squared_textbackground,null));
        hsv.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout myGallery = new LinearLayout(context);
        myGallery.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        myGallery.setPadding(10,10,10,10);
        for(int x=0; x < adjuntosSolicitud.size(); x++){
            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lp.setMargins(0,0,5,0);
            layout.setLayoutParams(lp);
            layout.setGravity(Gravity.CENTER);


            final ImageView adjunto_image = new ImageView(context);
            adjunto_image.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            adjunto_image.setScaleType(ImageView.ScaleType.FIT_XY);
            adjunto_image.setPadding(5,5,5,5);



            final String nombre_adjunto = adjuntosSolicitud.get(x).getName();
            //Bitmap _bitmap = null;

            if(adjuntosSolicitud.get(x).getImage() != null) {
                byte[] image = adjuntosSolicitud.get(x).getImage();
                //_bitmap.compress(Bitmap.CompressFormat.PNG, 50, image);
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inSampleSize = 1;
                if (image.length > 200000)
                    o.inSampleSize = 2;
                if (image.length > 400000)
                    o.inSampleSize = 4;
                if (image.length > 500000)
                    o.inSampleSize = 8;
                Bitmap imagen = BitmapFactory.decodeByteArray(image, 0, image.length, o);

                adjunto_image.setImageBitmap(imagen);
                adjunto_image.setBackground(context.getResources().getDrawable(R.drawable.border, null));

                final int finalX = x;
                adjunto_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarAdjunto(v.getContext(), adjuntosSolicitud.get(finalX));
                        //mostrarAdjuntoServidor(v.getContext(), activity, adjuntosSolicitud.get(finalX));
                    }
                });
                if (modificable) {
                    adjunto_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DialogHandler appdialog = new DialogHandler();
                            appdialog.Confirm((Activity) context, "Confirmación Borrado", "Esta seguro que quiere eliminar el archivo adjunto #" + finalX + "?", "Cancelar", "Eliminar", new EliminarAdjunto(context, activity, finalX));
                            return false;
                        }
                    });
                }
            }else{
                adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_about));
                final int finalX = x;
                adjunto_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarAdjuntoServidor(v.getContext(), activity, adjuntosSolicitud.get(finalX));
                    }
                });
            }
            layout.addView(adjunto_image);
            myGallery.addView(layout);
        }
        hsv.addView(myGallery);
    }

    //Pruebas para seccion de bloques
    public static void displayDialogMessage(Context context, String mensaje) {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.message_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final TextView message = d.findViewById(R.id.message);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        Button cancelBtn= d.findViewById(R.id.cancelBtn);
        title.setText("Mensaje de sistema");
        message.setText(mensaje);
        //SAVE
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {
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
        final Dialog d=new Dialog(context);
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
        saveBtn.setOnClickListener(new OnClickListener() {
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
        final Dialog d=new Dialog(context);
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
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seleccionado != null)
                    impuestosSolicitud.remove(seleccionado);
                Impuesto nuevoImpuesto = new Impuesto();
                nuevoImpuesto.setTatyp(((OpcionSpinner)claveSpinner.getSelectedItem()).getId());
                nuevoImpuesto.setVtext(((OpcionSpinner)claveSpinner.getSelectedItem()).getName());
                nuevoImpuesto.setTaxkd(((OpcionSpinner)clasiSpinner.getSelectedItem()).getId());
                nuevoImpuesto.setVtext2(((OpcionSpinner)clasiSpinner.getSelectedItem()).getName());
                impuestosSolicitud.add(nuevoImpuesto);

                if(!nuevoImpuesto.validarObligatorios()){
                    Toasty.warning(v.getContext(), "Todos los campos son obligatorios!").show();
                    return;
                }
                try{
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
        final Dialog d=new Dialog(context);
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
            }
        }
        //SAVE
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Interlocutor nuevoInterlocutor = new Interlocutor();
                nuevoInterlocutor.setName1(nameEditText.getText().toString());
                nuevoInterlocutor.setKunn2(propellantEditTxt.getText().toString());
                nuevoInterlocutor.setVtext(destEditTxt.getText().toString());
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
        final Dialog d=new Dialog(context);
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
        saveBtn.setOnClickListener(new OnClickListener() {
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
                bancosSolicitud.add(nuevoBanco);

                if(!nuevoBanco.validarObligatorios()){
                    Toasty.warning(v.getContext(), "Todos los campos son obligatorios!").show();
                    return;
                }

                try {
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
        final Dialog d=new Dialog(context);
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
        saveBtn.setOnClickListener(new OnClickListener() {
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
        final Dialog d=new Dialog(context);
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
        saveBtn.setOnClickListener(new OnClickListener() {
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

    public static void mostrarAdjunto(Context context, Adjuntos adjunto) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.adjunto_layout);
        ImageView adjunto_img = d.findViewById(R.id.imagen);
        TextView adjunto_txt = d.findViewById(R.id.nombre);
        final String nombre_adjunto = adjunto.getName();
        byte[] image = adjunto.getImage();
        Bitmap imagen = BitmapFactory.decodeByteArray(image, 0, image.length);
        adjunto_img.setImageBitmap(imagen);
        adjunto_txt.setText(adjunto.getName());
        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if(window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    public static void mostrarAdjuntoServidor(Context context, Activity activity, Adjuntos adjunto) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.adjunto_layout);
        ImageView adjunto_img = d.findViewById(R.id.imagen);
        TextView adjunto_txt = d.findViewById(R.id.nombre);
        adjunto_txt.setText(adjunto.getName());
        if(!adjunto.getName().toLowerCase().contains(".pdf")) {
            //SHOW DIALOG
            d.show();
            Window window = d.getWindow();
            if (window != null) {
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }
        //Realizar la transmision de lo que se necesita (Db o txt)
        WeakReference<Context> weakRefs = new WeakReference<Context>(context);
        WeakReference<Activity> weakRefAs = new WeakReference<Activity>(activity);
        //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
        AdjuntoServidor s = new AdjuntoServidor(weakRefs, weakRefAs, adjunto_img, adjunto_txt);
        if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
            s.EnableWiFi();
        }else{
            s.DisableWiFi();
        }
        s.execute();

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
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.visita_dialog_layout);
        final boolean reparto = mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_BZIRK",""), seleccionado.getVptyp());

        final boolean permite_modificar = PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_TIPORUTA","ZPV").toString().equals(seleccionado.getVptyp());

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

        kvgr4Spinner.setSelection(((ArrayAdapter<CharSequence>)kvgr4Spinner.getAdapter()).getPosition(seleccionado.getKvgr4()));
        f_icoEditText.setText(seleccionado.getF_ico());
        f_fcoEditText.setText(seleccionado.getF_fco());
        f_iniEditText.setText(seleccionado.getF_ini());
        f_finEditText.setText(seleccionado.getF_fin());

        Spinner centro_suministro = (Spinner)mapeoCamposDinamicos.get("W_CTE-VWERK");
        String valor_centro_suministro = ((OpcionSpinner)centro_suministro.getSelectedItem()).getId().trim();
        ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(Objects.requireNonNull(context), R.layout.simple_spinner_item, rutas_reparto);
        // Drop down layout style - list view with radio button
        dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
        ruta_reparto.setAdapter(dataAdapterRuta);
        ruta_reparto.setSelection(VariablesGlobales.getIndex(ruta_reparto, seleccionado.getRuta()));

        if(!reparto){
            ruta_reparto.setVisibility(View.GONE);
            ruta_reparto_label.setVisibility(View.GONE);
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
        listaopciones.add(new OpcionSpinner("1","Cada semana"));
        listaopciones.add(new OpcionSpinner("2","Cada 2 semanas"));
        listaopciones.add(new OpcionSpinner("3","Cada 3 semanas"));
        listaopciones.add(new OpcionSpinner("4","Cada 4 semanas"));
        listaopciones.add(new OpcionSpinner("5","Cada 5 semanas"));
        listaopciones.add(new OpcionSpinner("6","Cada 6 semanas"));
        listaopciones.add(new OpcionSpinner("8","Cada 8 semanas"));
        listaopciones.add(new OpcionSpinner("10","Cada 10 semanas"));

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_background, null);
        fcalidSpinner.setBackground(spinner_back);
        fcalidSpinner.setAdapter(dataAdapter);
        fcalidSpinner.setSelection(selectedIndex);

        //SAVE
        saveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setear ruta segun el tipo de visita ZPV, ZDD, etc
                if( reparto ){
                    seleccionado.setRuta(((OpcionSpinner)ruta_reparto.getSelectedItem()).getId().toString().trim());
                    ((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")).setSelection(VariablesGlobales.getIndex(((Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE")),seleccionado.getRuta()));
                }else{
                    seleccionado.setRuta(PreferenceManager.getDefaultSharedPreferences(SolicitudModificacionActivity.this).getString("W_CTE_RUTAHH",""));
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
                }

                //RECALCULAR DIAS DE VISITA
                RecalcularDiasDeReparto(context);

                tb_visitas.setDataAdapter(new VisitasTableAdapter(v.getContext(), visitasSolicitud));
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
            ruta_reparto.setEnabled(false);
            saveBtn.setVisibility(View.GONE);
        }

        d.show();
    }

    private static void RecalcularDiasDeReparto(Context context) {
        int numplanes = visitasSolicitud.size();
        //Iterrar sobre todos los planes de la modalida de venta seleccionada
        for (int y = 0 ; y < numplanes; y++) {
            Visitas vp = visitasSolicitud.get(y);
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

            if(!esReparto){
                Spinner metodo = ((Spinner)mapeoCamposDinamicos.get("W_CTE-KVGR5"));
                String rutaReparto = mDBHelper.RutaRepartoAsociada(((OpcionSpinner)metodo.getSelectedItem()).getId(), vp.getVptyp());
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
        if(secuencia != null && secuencia.trim().length() > 0){
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

    public static class EliminarAdjunto implements Runnable {
        private Context context;
        private Activity activity;
        private int rowIndex;
        public EliminarAdjunto(Context context, Activity activity, int rowIndex) {
            this.context = context;
            this.activity = activity;
            this.rowIndex = rowIndex;
        }
        public void run() {
            if(adjuntosSolicitud.get(rowIndex).getName().contains("PoliticaPrivacidad")) {
                CheckBox politica = (CheckBox) mapeoCamposDinamicos.get("politica");
                politica.setChecked(false);
                politica.setEnabled(true);
                firma = false;
            }
            adjuntosSolicitud.remove(rowIndex);
            tb_adjuntos.setDataAdapter(new AdjuntoTableAdapter(context, adjuntosSolicitud));
            MostrarGaleriaAdjuntosHorizontal((HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos"),context, activity);

            Toasty.info(context, "Se ha eliminado el adjunto!", Toast.LENGTH_SHORT).show();
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
            boolean cambioRealizado = false;
            for (int i = 0; i < listaCamposDinamicos.size(); i++) {
                if(!listaCamposBloque.contains(listaCamposDinamicos.get(i).trim()) && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                    try {
                        MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                        String valor = tv.getText().toString();

                        if(!listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                            insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            tv = ((MaskedEditText) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i)));
                            if(tv != null){
                                valor = tv.getText().toString();
                                insertValuesOld.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            }
                            tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get(listaCamposDinamicos.get(i)));
                            if(tv != null){
                                valor = tv.getText().toString();
                                insertValuesOld.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            }
                            if(listaCamposDinamicos.get(i).equals("W_CTE-COMENTARIOS")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                                Date date = new Date();
                                if(comentarios.size() == 0)
                                    insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                                else
                                    insertValues.put("[" + listaCamposDinamicos.get(i) + "]", comentarios.get(0).getComentario()+"("+dateFormat.format(date)+"): "+valor);
                            }
                        }

                    } catch (Exception e) {
                        try {
                            Spinner sp = ((Spinner) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                            String valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                            insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);

                            sp = ((Spinner) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i)));
                            if(sp != null) {
                                valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                insertValuesOld.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            }
                            sp = ((Spinner) mapeoCamposDinamicosEnca.get(listaCamposDinamicos.get(i)));
                            if(sp != null) {
                                valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                insertValuesOld.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                            }
                        } catch (Exception e2) {
                            try {
                                CheckBox check = ((CheckBox) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                                String valor = "";
                                if (check.isChecked()) {
                                    valor = "X";
                                }
                                insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor);

                                check = ((CheckBox) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i)));
                                if(check != null){
                                    valor = "";
                                    if (check.isChecked()) {
                                        valor = "X";
                                    }
                                    insertValuesOld.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                                }
                                check = ((CheckBox) mapeoCamposDinamicosEnca.get(listaCamposDinamicos.get(i)));
                                if(check != null){
                                    valor = "";
                                    if (check.isChecked()) {
                                        valor = "X";
                                    }
                                    insertValuesOld.put("[" + listaCamposDinamicos.get(i) + "]", valor);
                                }
                            }catch(Exception e3){
                                Toasty.error(getBaseContext(),"No se pudo obtener el valor del campo "+listaCamposDinamicos.get(i)).show();
                            }
                        }
                    }
                }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.
                    switch(listaCamposDinamicos.get(i)){
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
                                        visitaValues.put("lun_a", visitasSolicitud.get(c).getLun_a());
                                        visitaValues.put("mar_a", visitasSolicitud.get(c).getMar_a());
                                        visitaValues.put("mier_a", visitasSolicitud.get(c).getMier_a());
                                        visitaValues.put("jue_a", visitasSolicitud.get(c).getJue_a());
                                        visitaValues.put("vie_a", visitasSolicitud.get(c).getVie_a());
                                        visitaValues.put("sab_a", visitasSolicitud.get(c).getSab_a());
                                        visitaValues.put("f_ico", visitasSolicitud.get(c).getF_ico());
                                        visitaValues.put("f_fco", visitasSolicitud.get(c).getF_fco());
                                        visitaValues.put("f_ini", visitasSolicitud.get(c).getF_ini());
                                        visitaValues.put("f_fin", visitasSolicitud.get(c).getF_fin());
                                        visitaValues.put("fcalid", visitasSolicitud.get(c).getFcalid());

                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), null, visitaValues);
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
                                        visitaValuesOld.put("lun_a", visitasSolicitud_old.get(c).getLun_a());
                                        visitaValuesOld.put("mar_a", visitasSolicitud_old.get(c).getMar_a());
                                        visitaValuesOld.put("mier_a", visitasSolicitud_old.get(c).getMier_a());
                                        visitaValuesOld.put("jue_a", visitasSolicitud_old.get(c).getJue_a());
                                        visitaValuesOld.put("vie_a", visitasSolicitud_old.get(c).getVie_a());
                                        visitaValuesOld.put("sab_a", visitasSolicitud_old.get(c).getSab_a());
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
                    }
                }
            }
            try {
                //Datos que siemrpe deben ir cuando se crea por primera vez.
                //TODO seccionar par pais el grupo de cuentas, talvez sacar de una tabla
                if(tipoSolicitud.equals("1"))
                    insertValues.put("[W_CTE-KTOKD]", "RCMA");
                else{
                    insertValues.put("[W_CTE-KTOKD]", "RCMA");
                }
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
                insertValues.put("[FECCRE]", dateFormat.format(date));

                //mDBHelper.getWritableDatabase().insert("FormHvKof_solicitud", null, insertValues);
                if(solicitudSeleccionada.size() > 0){
                    if(solicitudSeleccionada.get(0).get("ESTADO").equals("Incidencia")) {
                        insertValues.put("[estado]", "Modificado");
                    }
                    long modifico = mDb.update("FormHvKof_solicitud", insertValues, "id_solicitud = ?", new String[]{solicitudSeleccionada.get(0).get("id_solicitud")});
                    Toasty.success(getApplicationContext(), "Registro modificado con éxito", Toast.LENGTH_SHORT).show();
                }else {
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
                    //Una vez finalizado el proceso de guardado, se limpia la solicitud para una nueva.
                    Intent sol = getIntent();
                    sol.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    SolicitudModificacionActivity.this.finish();
                    //Bundle par = new Bundle();
                    //par.putString("tipo_solicitud",tipoSolicitud);
                    //SolicitudActivity.this.startActivity(sol);
                    Toasty.success(getApplicationContext(), "Solicitud de Modificación Creada", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toasty.error(getApplicationContext(), "Error Insertando Solicitud."+e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    public static void LlenarCampos(Context context, Activity activity, ArrayList<JsonArray> estructurasSAP){
        if(estructurasSAP.size() == 0){
            Toasty.error(context.getApplicationContext(),"No se pudo obtener la informacion del cliente!");
            activity.finish();
            return;
        }

        cliente = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Cliente");
        notaEntrega = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("NotaEntrega");
        factura = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Factura");;
        telefonos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Telefonos");
        faxes = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Faxes");
        contactos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Contactos");
        interlocutores = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Interlocutores");
        impuestos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Impuestos");
        bancos = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Bancos");
        visitas = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("Visitas");

        //Titulo deseado
        activity.setTitle(activity.getTitle()+cliente.get(0).getAsJsonObject().get("W_CTE-NAME1").getAsString());

        for (int i = 0; i < listaCamposDinamicos.size(); i++) {
            if(!listaCamposBloque.contains(listaCamposDinamicos.get(i).trim()) && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                try {
                    MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                    tv.setText(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString());
                    if (listaCamposDinamicos.get(i).equals("W_CTE-PO_BOX") && tv.getText().toString().trim().equals("")) {
                        tv.setText(".");
                    }
                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i).trim()));
                    if(tv != null) {
                        tv.setText(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString());
                    }
                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get(listaCamposDinamicos.get(i).trim()));
                    if(tv != null) {
                        tv.setText(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString());

                    }
                    if(listaCamposDinamicos.get(i).equals("W_CTE-SMTP_ADDR")){
                        correoValidado = isValidEmail(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString());
                    }
                    if(listaCamposDinamicos.get(i).equals("W_CTE-STCD1")){
                        cedulaValidada = ValidarCedula(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString(),cliente.get(0).getAsJsonObject().get("W_CTE-KATR3").getAsString());
                    }

                } catch (Exception e) {
                    try {
                        Spinner sp = ((Spinner) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                        if(sp != null)
                            sp.setSelection(VariablesGlobales.getIndex(sp,cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString().trim()));

                        sp = ((Spinner) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i)));
                        if(sp != null)
                            sp.setSelection(VariablesGlobales.getIndex(sp,cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString().trim()));

                        sp = ((Spinner) mapeoCamposDinamicosEnca.get(listaCamposDinamicos.get(i)));
                        if(sp != null)
                            sp.setSelection(VariablesGlobales.getIndex(sp,cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString().trim()));
                    } catch (Exception e2) {
                        try {
                            CheckBox check = ((CheckBox) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                            CheckBox checkold = ((CheckBox) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i)));
                            String valor = "";
                            if (cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString().length() > 0) {
                                check.setChecked(true);
                                checkold.setChecked(true);
                            }

                        }catch(Exception e3){
                            //Toasty.error(context,"No se pudo obtener el valor del campo "+listaCamposDinamicos.get(i)).show();
                        }
                    }
                }
            }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.
                Gson gson = new Gson();
                switch(listaCamposDinamicos.get(i)){
                    case "W_CTE-CONTACTOS":
                        Contacto contacto=null;

                        for(int x=0; x < contactos.size(); x++){
                            contacto = gson.fromJson(contactos.get(x), Contacto.class);
                            contacto.setGbdat(contacto.getGbdat().replace("-",""));
                            if(contacto.getGbdat().trim().replace("0","").length()==0)
                                contacto.setGbdat("");
                            contactosSolicitud.add(contacto);
                            try {
                                contactosSolicitud_old.add((Contacto)contacto.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(contactosSolicitud != null) {
                            tb_contactos.setDataAdapter(new ContactoTableAdapter(context, contactosSolicitud));
                            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height+(alturaFilaTableView*contactosSolicitud.size());
                        }
                        break;
                    case "W_CTE-IMPUESTOS":
                        Impuesto impuesto=null;
                        ArrayList<Impuesto> todos = mDBHelper.getImpuestosPais();
                        for(int x=0; x < impuestos.size(); x++){

                            impuesto = gson.fromJson(impuestos.get(x), Impuesto.class);
                            for(int y=0; y < todos.size(); y++){
                                if(todos.get(y).getTatyp().equals(impuestos.get(x).getAsJsonObject().get("W_CTE-TATYP").getAsString()) && todos.get(y).getTaxkd().equals(impuestos.get(x).getAsJsonObject().get("W_CTE-TAXKD").getAsString())){
                                    impuesto.setVtext(todos.get(y).getVtext());
                                    impuesto.setVtext2(todos.get(y).getVtext2());
                                    break;
                                }
                            }
                            impuestosSolicitud.add(impuesto);
                            try {
                                impuestosSolicitud_old.add((Impuesto)impuesto.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(impuestosSolicitud != null) {
                            tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(context, impuestosSolicitud));
                            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height+(alturaFilaTableView*impuestosSolicitud.size());
                        }
                        break;
                    case "W_CTE-INTERLOCUTORES":
                        Interlocutor interlocutor=null;

                        for(int x=0; x < interlocutores.size(); x++){
                            interlocutor = gson.fromJson(interlocutores.get(x), Interlocutor.class);
                            interlocutoresSolicitud.add(interlocutor);
                            try {
                                interlocutoresSolicitud_old.add((Interlocutor) interlocutor.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(interlocutoresSolicitud != null) {
                            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(context, interlocutoresSolicitud));
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height+(alturaFilaTableView*interlocutoresSolicitud.size());
                        }
                        break;
                    case "W_CTE-BANCOS":
                        Banco banco = null;

                        for(int x=0; x < bancos.size(); x++){
                            banco = gson.fromJson(bancos.get(x), Banco.class);
                            bancosSolicitud.add(banco);
                            try {
                                bancosSolicitud_old.add((Banco)banco.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(bancosSolicitud != null) {
                            tb_bancos.setDataAdapter(new BancoTableAdapter(context, bancosSolicitud));
                            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height+(alturaFilaTableView*bancosSolicitud.size());
                        }
                        break;
                    case "W_CTE-VISITAS":
                        Visitas visita = null;
                        int indiceReparto = -1;
                        int indicePreventa = -1;
                        int indiceEspecializada = -1;
                        int indiceTeleventa = -1;

                        for(int x=0; x < visitas.size(); x++){
                            if(visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZDD")){
                                indiceReparto = x;
                            }
                            if(visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZPV")){
                                indicePreventa = x;
                            }
                            if(visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZJV")){
                                indiceEspecializada = x;
                            }
                            if(visitas.get(x).getAsJsonObject().get("W_CTE-VPTYP").getAsString().equals("ZTV")){
                                indiceTeleventa = x;
                            }
                            visita = gson.fromJson(visitas.get(x), Visitas.class);

                            visita.setF_ico(visita.getF_ico().replace("-",""));
                            visita.setF_fco(visita.getF_fco().replace("-",""));
                            visita.setF_ini(visita.getF_ini().replace("-",""));
                            visita.setF_fin(visita.getF_fin().replace("-",""));
                            if(visita.getF_ico().trim().replace("0","").length()==0)
                                visita.setF_ico("");
                            if(visita.getF_fco().trim().replace("0","").length()==0)
                                visita.setF_fco("");
                            if(visita.getF_ini().trim().replace("0","").length()==0)
                                visita.setF_ini("");
                            if(visita.getF_fin().trim().replace("0","").length()==0)
                                visita.setF_fin("");

                            visitasSolicitud.add(visita);
                            try {
                                visitasSolicitud_old.add((Visitas)visita.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(visitasSolicitud != null) {
                            tb_visitas.setDataAdapter(new VisitasTableAdapter(context, visitasSolicitud));
                            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height+(alturaFilaTableView*visitasSolicitud.size());
                        }

                        //Campos de las secuencias de la visita de preventa
                        if(visitasSolicitud.size() > 0 && indicePreventa >= 0) {
                            //Al menos 1 dia de visita
                            TextInputEditText diaL = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_L"));
                            TextInputEditText diaK = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_K"));
                            TextInputEditText diaM = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_M"));
                            TextInputEditText diaJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_J"));
                            TextInputEditText diaV = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_V"));
                            TextInputEditText diaS = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_S"));
                            if (diaL != null) {
                                diaL.setText(visitasSolicitud_old.get(indicePreventa).getLun_de());
                            }
                            if (diaK != null) {
                                diaK.setText(visitasSolicitud_old.get(indicePreventa).getMar_de());
                            }
                            if (diaM != null) {
                                diaM.setText(visitasSolicitud_old.get(indicePreventa).getMier_de());
                            }
                            if (diaJ != null) {
                                diaJ.setText(visitasSolicitud_old.get(indicePreventa).getJue_de());
                            }
                            if (diaV != null) {
                                diaV.setText(visitasSolicitud_old.get(indicePreventa).getVie_de());
                            }
                            if (diaS != null) {
                                diaS.setText(visitasSolicitud_old.get(indicePreventa).getSab_de());
                            }
                        }else{
                            CardView tituloV = ((CardView)mapeoVisitas.get("ZPV"));
                            TextInputEditText diaL = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_L"));
                            TextInputEditText diaK = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_K"));
                            TextInputEditText diaM = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_M"));
                            TextInputEditText diaJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_J"));
                            TextInputEditText diaV = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_V"));
                            TextInputEditText diaS = ((TextInputEditText) mapeoCamposDinamicos.get("ZPV_S"));

                            tituloV.setVisibility(View.GONE);
                            diaL.setVisibility(View.GONE);
                            diaK.setVisibility(View.GONE);
                            diaM.setVisibility(View.GONE);
                            diaJ.setVisibility(View.GONE);
                            diaV.setVisibility(View.GONE);
                            diaS.setVisibility(View.GONE);
                        }
                        if(visitasSolicitud.size() > 0 && indiceEspecializada >= 0) {
                            //En caso de que tenga especializada
                            TextInputEditText diaEL = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_L"));
                            TextInputEditText diaEK = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_K"));
                            TextInputEditText diaEM = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_M"));
                            TextInputEditText diaEJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_J"));
                            TextInputEditText diaEV = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_V"));
                            TextInputEditText diaES = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_S"));
                            if ( diaEL != null) {
                                diaEL.setText(visitasSolicitud_old.get(indiceEspecializada).getLun_de());
                            }
                            if ( diaEK != null) {
                                diaEK.setText(visitasSolicitud_old.get(indiceEspecializada).getMar_de());
                            }
                            if ( diaEM != null) {
                                diaEM.setText(visitasSolicitud_old.get(indiceEspecializada).getMier_de());
                            }
                            if ( diaEJ != null) {
                                diaEJ.setText(visitasSolicitud_old.get(indiceEspecializada).getJue_de());
                            }
                            if ( diaEV != null) {
                                diaEV.setText(visitasSolicitud_old.get(indiceEspecializada).getVie_de());
                            }
                            if ( diaES != null) {
                                diaES.setText(visitasSolicitud_old.get(indiceEspecializada).getSab_de());
                            }
                        }else{
                            CardView tituloV = ((CardView)mapeoVisitas.get("ZJV"));
                            TextInputEditText diaEL = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_L"));
                            TextInputEditText diaEK = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_K"));
                            TextInputEditText diaEM = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_M"));
                            TextInputEditText diaEJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_J"));
                            TextInputEditText diaEV = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_V"));
                            TextInputEditText diaES = ((TextInputEditText) mapeoCamposDinamicos.get("ZJV_S"));

                            tituloV.setVisibility(View.GONE);
                            diaEL.setVisibility(View.GONE);
                            diaEK.setVisibility(View.GONE);
                            diaEM.setVisibility(View.GONE);
                            diaEJ.setVisibility(View.GONE);
                            diaEV.setVisibility(View.GONE);
                            diaES.setVisibility(View.GONE);
                        }
                        if(visitasSolicitud.size() > 0 && indiceTeleventa >= 0) {
                            //En caso de que tenga especializada
                            TextInputEditText diaTL = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_L"));
                            TextInputEditText diaTK = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_K"));
                            TextInputEditText diaTM = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_M"));
                            TextInputEditText diaTJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_J"));
                            TextInputEditText diaTV = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_V"));
                            TextInputEditText diaTS = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_S"));
                            if ( diaTL != null) {
                                diaTL.setText(visitasSolicitud_old.get(indiceTeleventa).getLun_de());
                            }
                            if ( diaTK != null) {
                                diaTK.setText(visitasSolicitud_old.get(indiceTeleventa).getMar_de());
                            }
                            if ( diaTM != null) {
                                diaTM.setText(visitasSolicitud_old.get(indiceTeleventa).getMier_de());
                            }
                            if ( diaTJ != null) {
                                diaTJ.setText(visitasSolicitud_old.get(indiceTeleventa).getJue_de());
                            }
                            if ( diaTV != null) {
                                diaTV.setText(visitasSolicitud_old.get(indiceTeleventa).getVie_de());
                            }
                            if ( diaTS != null) {
                                diaTS.setText(visitasSolicitud_old.get(indiceTeleventa).getSab_de());
                            }
                        }else{
                            CardView tituloT = ((CardView)mapeoVisitas.get("ZTV"));
                            TextInputEditText diaTL = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_L"));
                            TextInputEditText diaTK = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_K"));
                            TextInputEditText diaTM = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_M"));
                            TextInputEditText diaTJ = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_J"));
                            TextInputEditText diaTV = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_V"));
                            TextInputEditText diaTS = ((TextInputEditText) mapeoCamposDinamicos.get("ZTV_S"));

                            tituloT.setVisibility(View.GONE);
                            diaTL.setVisibility(View.GONE);
                            diaTK.setVisibility(View.GONE);
                            diaTM.setVisibility(View.GONE);
                            diaTJ.setVisibility(View.GONE);
                            diaTV.setVisibility(View.GONE);
                            diaTS.setVisibility(View.GONE);
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
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-TEL_NUMBER2"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-TEL_NUMBER2"));
                    if (tv != null)
                        tv.setText(telefonos.get(0).getAsJsonObject().get("W_CTE-TEL_NUMBER").getAsString());

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

    private String getFileName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
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
        combo.setSelection(selectedIndex);
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
        DireccionCorta();
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
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        if(solicitudSeleccionada.size() > 0)
            combo.setSelection(selectedIndex);
        if(combo.getCount() > 1 && cliente != null)
            combo.setSelection(VariablesGlobales.getIndex(combo,cliente.get(0).getAsJsonObject().get("W_CTE-CITY1").getAsString().trim()));
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getSelectedView()).setError("El campo es obligatorio!");
        DireccionCorta();
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
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
        DireccionCorta();
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
        DireccionCorta();
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
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        if(solicitudSeleccionada.size() > 0)
            combo.setSelection(selectedIndex);
        if(combo.getCount() > 1 && cliente != null)
            combo.setSelection(VariablesGlobales.getIndex(combo,cliente.get(0).getAsJsonObject().get("W_CTE-CITY1").getAsString().trim()));
        if(selectedIndex == 0 && ((TextView) combo.getSelectedView()) != null)
            ((TextView) combo.getSelectedView()).setError("El campo es obligatorio!");
        DireccionCorta();
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
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
        DireccionCorta();
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }
    }

    private static void  DireccionCorta() {
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
    private static boolean ValidarCedula(View v, String tipoCedula){
        TextView texto = (TextView)v;
        String cedula = "";
        switch(tipoCedula){
            case "C1":
                cedula = "[0][1-9]-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-[0-9]{2}";
                break;
            case "C2":
                cedula = "((3-[0-9]{3,3}-[0-9]{6,6})|(4-000-[0-9]{6,6}))";
                break;
            case "C3":
                cedula = "([1-9][0-9])-[0-9]{4,4}-[0-9]{4,4}-[0-9]{2,2}";
                break;
        }
        Pattern pattern = Pattern.compile(cedula);
        Matcher matcher = pattern.matcher(texto.getText());
        if (!matcher.matches()) {
            texto.setError("Formato Regimen "+tipoCedula+" invalido!");
            cedulaValidada = false;
            return false;
        }
        cedulaValidada = true;
        MaskedEditText idfiscal = (MaskedEditText) mapeoCamposDinamicos.get("W_CTE-STCD3");
        String cedulaDigitada = texto.getText().toString().trim();
        if(texto.getText().toString().trim().endsWith("-00"))
            idfiscal.setText(cedulaDigitada.substring(0,cedulaDigitada.length()-3).replaceFirst("^0+(?!$)", "").replace("-",""));
        else
            idfiscal.setText(cedulaDigitada.replaceFirst("^0+(?!$)", "").replace("-",""));
        idfiscal.setError(null);
        idfiscal.clearFocus();
        Toasty.success(texto.getContext(),"Formato Regimen "+tipoCedula+" valido!").show();
        return true;
    }
    private static boolean ValidarCedula(String v, String tipoCedula){
        String texto = v;
        String cedula = "";
        switch(tipoCedula){
            case "C1":
                cedula = "[0][1-9]-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-[0-9]{2}";
                break;
            case "C2":
                cedula = "((3-[0-9]{3,3}-[0-9]{6,6})|(4-000-[0-9]{6,6}))";
                break;
            case "C3":
                cedula = "([1-9][0-9])-[0-9]{4,4}-[0-9]{4,4}-[0-9]{2,2}";
                break;
        }
        Pattern pattern = Pattern.compile(cedula);
        Matcher matcher = pattern.matcher(texto);
        if (!matcher.matches()) {
            cedulaValidada = false;
            return false;
        }
        cedulaValidada = true;
        return true;
    }
    private static boolean ValidarCoordenadaY(View v){
        TextView texto = (TextView)v;
        String coordenadaY = "^[-]?(([8-9]|[1][0-2])(\\.\\d{5,12}+)?)";
        Pattern pattern = Pattern.compile(coordenadaY);
        Matcher matcher = pattern.matcher(texto.getText().toString().trim());
        if (!matcher.matches()) {
            texto.setError("Formato Coordenada Y "+texto.getText().toString().trim()+" invalido!");
            return false;
        }
        //Toasty.success(texto.getContext(),"Formato Coordenada Y "+valor+" valido!").show();
        return true;
    }
    private static boolean ValidarCoordenadaX(View v){
        TextView texto = (TextView)v;
        String coordenadaX = "^[-](([8][2-6])(\\.\\d{5,12}+)?)";
        Pattern pattern = Pattern.compile(coordenadaX);
        Matcher matcher = pattern.matcher(texto.getText().toString().trim());
        if (!matcher.matches()) {
            texto.setError("Formato Coordenada X "+ texto.getText().toString().trim()+" invalido!");
            return false;
        }
        //Toasty.success(texto.getContext(),"Formato Coordenada X "+valor+" valido!").show();
        return true;
    }

    private static boolean ValidarCliente(View v){
        TextView texto = (TextView)v;
        String coordenadaY = "^[-]?(([8-9]|[1][0-2])(\\.\\d{5,12}+)?)";
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
            misTabs = new TabLayout(context);
            adapter = new ViewPagerAdapter(getSupportFragmentManager());
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
            misTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
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

    public final static boolean isValidEmail(View v) {
        TextView correo = (TextView)v;
        boolean valido = !TextUtils.isEmpty(correo.getText()) && android.util.Patterns.EMAIL_ADDRESS.matcher(correo.getText()).matches();
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
}
