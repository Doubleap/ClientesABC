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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.vicmikhailau.maskededittext.MaskedEditText;

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

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.Animaciones.CubeTransformer;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.ComentarioTableAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.ConsultaClienteAPI;
import proyecto.app.clientesabc.clases.ConsultaClienteServidor;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.Validaciones;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Comentario;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;
import static com.google.android.material.tabs.TabLayout.GRAVITY_FILL;
import static com.google.android.material.tabs.TabLayout.INDICATOR_GRAVITY_TOP;

public class SolicitudAvisosEquipoFrioActivity extends AppCompatActivity {

    final static int alturaFilaTableView = 75;
    static String tipoSolicitud ="";
    static String idSolicitud = "";
    static String codigoCliente = "";
    static String codigoEquipoFrio = "";
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
    public static boolean firma;
    static boolean modificable;
    static boolean correoValidado;
    static boolean cedulaValidada;
    static BottomNavigationView bottomNavigation;

    static Uri mPhotoUri;

    @SuppressLint("StaticFieldLeak")
    private static TableView<Adjuntos> tb_adjuntos;
    @SuppressLint("StaticFieldLeak")
    private static TableView<Comentario> tb_comentarios;
    private static ArrayList<Adjuntos> adjuntosSolicitud;
    //Bloques de datos con valores sin modificar o viejos
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
    public static ManejadorAdjuntos manejadorAdjuntos;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        firma = false;
        modificable = true;
        correoValidado = true;
        cedulaValidada = true;
        FrameLayout f = findViewById(R.id.background);
        //f.getBackground().setAlpha(40);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            tipoSolicitud = b.getString("tipoSolicitud");
            idSolicitud = b.getString("idSolicitud");
            codigoCliente = b.getString("codigoCliente");
            codigoEquipoFrio = b.getString("codigoEquipoFrio");
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.botella_coca_header_der,null));

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
        if(solicitudSeleccionada.size() > 0) {
            firma = true;
            correoValidado = true;
            cedulaValidada = true;
            if(solicitudSeleccionada.get(0).get("ESTADO").equals("Pendiente")
                    ||solicitudSeleccionada.get(0).get("ESTADO").equals("Rechazado")
                    ||solicitudSeleccionada.get(0).get("ESTADO").equals("Aprobado")){
                modificable = false;
            }
            if(solicitudSeleccionadaOld.size() == 0){
                Toasty.error(this,"No se encontraron los datos de encabezado.").show();
            }
        }else{
            correoValidado = true;
            cedulaValidada = true;
            if(!tipoSolicitud.equals(getResources().getString(R.string.ID_FORM_INSTALACION_EQ)) && !tipoSolicitud.equals(getResources().getString(R.string.ID_FORM_CAMBIO_EQ))){
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
                        if(getCurrentFocus() != null)
                            getCurrentFocus().clearFocus();
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
                        //Validacion de constancia firmada por el cliente.
                        if(!firma){
                            numErrores++;
                            mensajeError += "- El cliente debe firmar la constancia de solicitud de Instalacion!\n";
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
                            appdialog.Confirm(SolicitudAvisosEquipoFrioActivity.this, "Confirmación Solicitud Eq. Frio", "Esta seguro que desea guardar la solicitud de Equipo Frio?", "No", "Si", new GuardarFormulario(getBaseContext()));
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

            if (VariablesGlobales.UsarAPI()) {
                ConsultaClienteAPI c = new ConsultaClienteAPI(weakRefs1, weakRefAs1, codigoCliente);
                if(PreferenceManager.getDefaultSharedPreferences(this).getString("tipo_conexion","").equals("wifi")){
                    c.EnableWiFi();
                }
                c.execute();
            } else {
                ConsultaClienteServidor c = new ConsultaClienteServidor(weakRefs1, weakRefAs1, codigoCliente);
                if(PreferenceManager.getDefaultSharedPreferences(this).getString("tipo_conexion","").equals("wifi")){
                    c.EnableWiFi();
                }else{
                    c.DisableWiFi();
                }
                c.execute();
            }


        }

        if(!modificable) {
            LinearLayout ll = findViewById(R.id.LinearLayoutMain);
            DrawerLayout.LayoutParams h = new DrawerLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT);

            h.setMargins(0,0,0,0);
            ll.setLayoutParams(h);
            bottomNavigation.setVisibility(View.GONE);
            bottomNavigation.animate().translationY(150);
        }

        Drawable d=getResources().getDrawable(R.drawable.botella_coca_header_der,null);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            //return;
        }

        //Manejo de Bloques
        tb_adjuntos = new TableView<>(this);
        tb_comentarios = new TableView<>(this);

        adjuntosSolicitud = new ArrayList<>();
        comentarios = new ArrayList<>();
        //notificantesSolicitud = new ArrayList<Adjuntos>();
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


    //Se dispara al escoger el documento que se quiere relacionar a la solicitud
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeakReference<Activity> weakRefA = new WeakReference<Activity>(SolicitudAvisosEquipoFrioActivity.this);
        ManejadorAdjuntos.ActivityResult(requestCode, resultCode, data, getApplicationContext(),weakRefA.get(), mPhotoUri, mDBHelper,  adjuntosSolicitud,  modificable,  firma,  GUID, tb_adjuntos, mapeoCamposDinamicos);
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
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("checkbox")) {
                    //Tipo CHECKBOX
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    TableRow.LayoutParams lp_old = new TableRow.LayoutParams(150, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    CheckBox checkbox_old = new CheckBox(getContext());
                    checkbox_old.setLayoutParams(lp_old);
                    if((campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("10")) && campos.get(i).get("sup").trim().length() == 0){
                        checkbox_old.setEnabled(false);
                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(), checkbox_old);
                    }

                    CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setLayoutParams(lp);
                    checkbox.setText(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0 || campos.get(i).get("modificacion").trim().equals("3") || campos.get(i).get("modificacion").trim().equals("4") || campos.get(i).get("modificacion").trim().equals("5")){
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
                        Validaciones.ejecutarExcepcion(getContext(),checkbox,null,configExcepcion, listaCamposObligatorios, campos.get(i).get("campo").trim());
                        int excepcionxAgencia = 0;
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if(((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if (excepcionxAgencia >= 0) {
                            HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                            Validaciones.ejecutarExcepcion(getContext(),checkbox,null,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i).get("campo").trim());
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
                    if(campos.get(i).get("sup").trim().length() > 0 || campos.get(i).get("modificacion").trim().equals("3") || campos.get(i).get("modificacion").trim().equals("4") || campos.get(i).get("modificacion").trim().equals("5")){
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
                        if(campos.get(i).get("campo").trim().toLowerCase().equals("w_cte-im_cause_codegrp")){
                            opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim(),2,2,null,"CODEGRUPPE like 'CS-%' AND CODEGRUPPE IN ('CS-CAENF','CS-CAPMX','CS-CAMQH')");
                        }
                        if(campos.get(i).get("campo").trim().toLowerCase().equals("w_cte-im_d_codegrp")){
                            opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim(),2,2,null,"CODEGRUPPE like 'CA-%' AND CODEGRUPPE IN ('CA-ENFRI','CA-MAQHI','CA-POSTM')");
                        }
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
                        listaopciones.add(new OpcionSpinner(opciones.get(j).get("id").toString(), opciones.get(j).get("descripcion")));
                        if(solicitudSeleccionada.size() > 0){
                            //valor de la solicitud seleccionada
                            if(opciones.get(j).get("id").trim().equals(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim())){
                                selectedIndex = j;
                            }
                            if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim())!= null && opciones.get(j).get("id").trim().equals(solicitudSeleccionadaOld.get(0).get(campos.get(i).get("campo").trim()).trim())){
                                selectedIndexOld = j;
                            }
                        } else {
                            if (campos.get(i).get("dfaul").trim().length() > 0 && opciones.get(j).get("id").trim().equals(campos.get(i).get("dfaul").trim())) {
                                selectedIndex = j;
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

                    if(campos.get(i).get("campo").trim().equals("W_CTE-IM_CAUSE_CODEGRP") && !campos.get(i).get("modificacion").trim().equals("1")){
                        final int finalI = i;
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String valorDefectoHijo = getValorDefectoCampo(campos,"W_CTE-IM_CAUSE_CODE");
                                OpcionSpinner ClaseAviso = ((OpcionSpinner) ((Spinner) mapeoCamposDinamicos.get("W_CTE-IM_NOTIF_TYPE")).getSelectedItem());
                                String clase_aviso="";
                                if(ClaseAviso != null)
                                    clase_aviso = ClaseAviso.getId();
                                CausasDeGrupo(parent,valorDefectoHijo,clase_aviso);
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    if(campos.get(i).get("campo").trim().equals("W_CTE-IM_D_CODEGRP") && !campos.get(i).get("modificacion").trim().equals("1")){
                        final int finalI1 = i;
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String valorDefectoHijo = getValorDefectoCampo(campos,"W_CTE-IM_D_CODE");
                                OpcionSpinner ClaseAviso = ((OpcionSpinner) ((Spinner) mapeoCamposDinamicos.get("W_CTE-IM_NOTIF_TYPE")).getSelectedItem());
                                String clase_aviso="";
                                if(ClaseAviso != null)
                                    clase_aviso = ClaseAviso.getId();
                                SintomasDeGrupo(parent,valorDefectoHijo,clase_aviso);
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                if(position == 0)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1") != null) {

                    }
                    //Campos de encabezado deben salir todos como deshabilitados en valor viejo
                    if(campos.get(i).get("modificacion").trim().equals("1") && campos.get(i).get("sup").trim().length() == 0){
                        combo.setEnabled(false);
                        combo.setBackground(getResources().getDrawable(R.drawable.spinner_background_old,null));
                        listaCamposDinamicosEnca.add(campos.get(i).get("campo").trim());
                        mapeoCamposDinamicosEnca.put(campos.get(i).get("campo").trim(),combo);
                        //Campos especificos de Equipo frio que no deben salir en la HH pero si llevar valor para crear el aviso correspondiente
                        if(campos.get(i).get("campo").trim().equals("W_CTE-IM_D_CODEGRP")
                            || campos.get(i).get("campo").trim().equals("W_CTE-IM_D_CODE")
                            || campos.get(i).get("campo").trim().equals("W_CTE-IM_CAUSE_CODEGRP")
                            || campos.get(i).get("campo").trim().equals("W_CTE-IM_CAUSE_CODE")){
                            label.setVisibility(View.GONE);
                            combo.setVisibility(View.GONE);
                        }
                    }

                    final Spinner combo_old = new Spinner(getContext(), Spinner.MODE_DROPDOWN);
                    combo_old.setVisibility(View.GONE);
                    combo_old.setEnabled(false);
                    if((campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("10")) && campos.get(i).get("sup").trim().length() == 0){
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

                        mapeoCamposDinamicosOld.put(campos.get(i).get("campo").trim(),combo_old);
                    }

                    if(combo_old != null && (campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("10"))) {
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
                    }else if(campos.get(i).get("campo").trim() != "1"){
                        //listaCamposDinamicos.add(campos.get(i).get("campo").trim()+"1");
                        mapeoCamposDinamicos.put(campos.get(i).get("campo").trim()+"1", combo);
                        //Replicar valores de campos duplicados en configuracion
                        Spinner original = (Spinner) mapeoCamposDinamicos.get(campos.get(i).get("campo").trim());
                        Spinner duplicado = (Spinner) mapeoCamposDinamicos.get(campos.get(i).get("campo").trim()+"1");
                        final String nombreCampo = campos.get(i).get("campo").trim();
                        final int indice = i;
                        /*original.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(nombreCampo.equals("W_CTE-VWERK")){
                                    Spinner zona_transporte = (Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE");
                                    String valor_centro_suministro = ((OpcionSpinner)parent.getSelectedItem()).getId().trim();
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'");
                                    // Creando el adaptador(opciones) para el comboBox deseado
                                    ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                    // Drop down layout style - list view with radio button
                                    dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                    zona_transporte.setAdapter(dataAdapterRuta);
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
                                if(nombreCampo.equals("W_CTE-VWERK")){
                                    Spinner zona_transporte = (Spinner)mapeoCamposDinamicos.get("W_CTE-LZONE");
                                    String valor_centro_suministro = ((OpcionSpinner)parent.getSelectedItem()).getId().trim();
                                    ArrayList<OpcionSpinner> rutas_reparto = mDBHelper.getDatosCatalogoParaSpinner("cat_tzont","vwerks='"+valor_centro_suministro+"'");
                                    // Creando el adaptador(opciones) para el comboBox deseado
                                    ArrayAdapter<OpcionSpinner> dataAdapterRuta = new ArrayAdapter<>(getContext(), R.layout.simple_spinner_item, rutas_reparto);
                                    // Drop down layout style - list view with radio button
                                    dataAdapterRuta.setDropDownViewResource(R.layout.spinner_item);
                                    zona_transporte.setAdapter(dataAdapterRuta);
                                }
                                ReplicarValorSpinner(parent,nombreCampo,position);
                                if(position == 0 && ((TextView) parent.getSelectedView()) != null)
                                    ((TextView) parent.getSelectedView()).setError("El campo es obligatorio!");
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });*/
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
                        Validaciones.ejecutarExcepcion(getContext(),combo,label,configExcepcion,listaCamposObligatorios,campos.get(i).get("campo").trim());

                        int excepcionxAgencia = -1;
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if(((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if (excepcionxAgencia >= 0) {
                            HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                            Validaciones.ejecutarExcepcion(getContext(),combo,label,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i).get("campo").trim());
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

                    et.setTag(campos.get(i).get("descr"));
                    //et.setTextColor(getResources().getColor(R.color.colorTextView,null));
                    //et.setBackgroundColor(getResources().getColor(R.color.black,null));
                    //et.setHint(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0 || campos.get(i).get("modificacion").trim().equals("3") || campos.get(i).get("modificacion").trim().equals("4") || campos.get(i).get("modificacion").trim().equals("5")){
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

                    if(campos.get(i).get("datatype").contains("char")) {
                        if(campos.get(i).get("campo").trim().equals("W_CTE-STCD3")){
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            et.setFilters(new InputFilter[] { new InputFilter.LengthFilter( 18 ) });
                        }else{
                            et.setInputType(InputType.TYPE_CLASS_TEXT);
                        }
                        if (Integer.valueOf(campos.get(i).get("maxlength")) > 0) {
                            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.valueOf(campos.get(i).get("maxlength")))});
                            if(Integer.valueOf(campos.get(i).get("maxlength")) >= 40){
                                et.setSingleLine(false);
                                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                                et.setMinLines(1);
                                et.setMaxLines(5);
                                et.setVerticalScrollBarEnabled(true);
                                et.setMovementMethod(ScrollingMovementMethod.getInstance());
                                et.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                                et.setGravity(INDICATOR_GRAVITY_TOP);
                            }
                        }
                    }else if(campos.get(i).get("datatype").equals("decimal")) {
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
                    if (campos.get(i).get("dfaul").trim().length() > 0) {
                        et.setText(campos.get(i).get("dfaul").trim());
                    }
                    //Le cae encima al valor default por el de la solicitud seleccionada
                    if(solicitudSeleccionada.size() > 0){
                        if(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()) != null)
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
                            et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                    if((campos.get(i).get("modificacion").trim().equals("2") || campos.get(i).get("modificacion").trim().equals("10")) && campos.get(i).get("sup").trim().length() == 0){
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
                                }
                            }
                        });
                    }
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), et);
                    if(campos.get(i).get("obl")!= null && campos.get(i).get("obl").trim().length() > 0 && !campos.get(i).get("modificacion").equals("1") ){
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                        if(et.getText().toString().trim().length() == 0) {
                            et.setError("El campo es obligatorio!");
                        }
                    }
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }

                    //Excepciones de visualizacion y configuracionde campos dados por la tabla ConfigCampos
                    int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                    if(excepcion >= 0) {
                        HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                        Validaciones.ejecutarExcepcion(getContext(),et,label,configExcepcion,listaCamposObligatorios,campos.get(i).get("campo").trim());
                        int excepcionxAgencia = 0;
                        if(((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicos.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if(((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")) != null)
                            excepcionxAgencia = getIndexConfigCampo(campos.get(i).get("campo").trim(),((OpcionSpinner)((Spinner)mapeoCamposDinamicosEnca.get("W_CTE-BZIRK")).getSelectedItem()).getId());
                        if (excepcionxAgencia >= 0) {
                            HashMap<String, String> configExcepcionxAgencia = configExcepciones.get(excepcionxAgencia);
                            Validaciones.ejecutarExcepcion(getContext(),et,label,configExcepcionxAgencia,listaCamposObligatorios,campos.get(i).get("campo").trim());
                        }
                    }
                    //if(cliente != null && cliente.get(campos.get(i).get("campo")) != null)
                        //et.setText(cliente.get(campos.get(i).get("campo").trim()).getAsString());
                }
                /*int excepcion = getIndexConfigCampo(campos.get(i).get("campo").trim());
                if(excepcion >= 0) {
                    HashMap<String, String> configExcepcion = configExcepciones.get(excepcion);
                    if (configExcepcion.get("opc").equals("1") || configExcepcion.get("opc").equals("X")) {
                        if (listaCamposObligatorios.contains(campos.get(i).get("campo").trim())) {
                        }
                        listaCamposObligatorios.remove(campos.get(i).get("campo").trim());
                    }
                    if (configExcepcion.get("obl").equals("1") || configExcepcion.get("obl").equals("X")) {
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                    } else if (configExcepcion.get("obl") != null) {
                        if (listaCamposObligatorios.contains(campos.get(i).get("campo").trim())) {
                        }
                        listaCamposObligatorios.remove(campos.get(i).get("campo").trim());
                    }
                }*/

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

                if(tipoSolicitud.equals(getResources().getString(R.string.ID_FORM_INSTALACION_EQ)) || tipoSolicitud.equals(getResources().getString(R.string.ID_FORM_CAMBIO_EQ))) {
                    //Check Box para la contancia de del cliente
                    final CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText("Constancia del cliente");
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
                    mapeoCamposDinamicos.put("constancia",checkbox);
                }
            }
        }

        private void Aceptacion(View v) {
            Intent intent;
            switch (PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS","")){
                case "F443":
                    intent = new Intent(getContext(),FirmaT4Activity.class);
                    getActivity().startActivityForResult(intent,100);
                    break;
                case "F445":
                    intent = new Intent(getContext(),FirmaT4Activity.class);
                    getActivity().startActivityForResult(intent,100);
                    break;
                case "F451":
                    intent = new Intent(getContext(),FirmaT4Activity.class);
                    getActivity().startActivityForResult(intent,100);
                    break;
                default:
                    intent = new Intent(getContext(),FirmaT4Activity.class);
                    getActivity().startActivityForResult(intent,100);
            }
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
                    ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsv, getContext(), getActivity(),adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

                    rl.addView(hsv);
                    ll.addView(rl);
                    mapeoCamposDinamicos.put("GaleriaAdjuntos", hsv);
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
                        MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaFinal.get(i)));
                        String valor = "";
                        if(tv != null) {
                            valor = tv.getText().toString();
                        }
                        if(!listaFinal.get(i).equals("W_CTE-ENCUESTA") && !listaFinal.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                            insertValues.put("[" + listaFinal.get(i) + "]", valor);
                            tv = ((MaskedEditText) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                            if(tv != null){
                                valor = tv.getText().toString();
                                insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                            }
                            tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get(listaFinal.get(i)));
                            if(tv != null){
                                valor = tv.getText().toString();
                                insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                            }
                            if(listaFinal.get(i).equals("W_CTE-COMENTARIOS")) {
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
                            Spinner sp = ((Spinner) mapeoCamposDinamicos.get(listaFinal.get(i)));
                            String valor="";
                            if(sp != null) {
                                valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                insertValues.put("[" + listaFinal.get(i) + "]", valor);
                            }
                            sp = ((Spinner) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                            if(sp != null) {
                                valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                            }
                            sp = ((Spinner) mapeoCamposDinamicosEnca.get(listaFinal.get(i)));
                            if(sp != null) {
                                valor = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                                insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                if(listaFinal.get(i).trim().equals("W_CTE-BZIRK") && !insertValues.containsKey("[W_CTE-BZIRK]")){
                                    insertValues.put("[W_CTE-BZIRK]", valor);
                                }
                            }
                        } catch (Exception e2) {
                            try {
                                CheckBox check = ((CheckBox) mapeoCamposDinamicos.get(listaFinal.get(i)));
                                String valor = "";
                                if (check.isChecked()) {
                                    valor = "X";
                                }
                                insertValues.put("[" + listaFinal.get(i) + "]", valor);

                                check = ((CheckBox) mapeoCamposDinamicosOld.get(listaFinal.get(i)));
                                if(check != null){
                                    valor = "";
                                    if (check.isChecked()) {
                                        valor = "X";
                                    }
                                    insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                }
                                check = ((CheckBox) mapeoCamposDinamicosEnca.get(listaFinal.get(i)));
                                if(check != null){
                                    valor = "";
                                    if (check.isChecked()) {
                                        valor = "X";
                                    }
                                    insertValuesOld.put("[" + listaFinal.get(i) + "]", valor);
                                }
                            }catch(Exception e3){
                                Toasty.error(getBaseContext(),"No se pudo obtener el valor del campo "+listaFinal.get(i)).show();
                            }
                        }
                    }
                }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.
                    switch(listaFinal.get(i)){
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
                insertValues.put("[W_CTE-KTOKD]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_KTOKD",""));
                Spinner sp = ((Spinner) mapeoCamposDinamicos.get("SIGUIENTE_APROBADOR"));
                String id_aprobador = ((OpcionSpinner) sp.getSelectedItem()).getId().trim();
                insertValues.put("[W_CTE-KUNNR]", codigoCliente);
                insertValues.put("[SIGUIENTE_APROBADOR]", id_aprobador);
                insertValues.put("[W_CTE-BUKRS]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_BUKRS",""));
                insertValues.put("[W_CTE-RUTAHH]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_RUTAHH",""));
                insertValues.put("[W_CTE-VKORG]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_VKORG",""));
                insertValues.put("[id_solicitud]", NextId);
                insertValues.put("[tipform]", tipoSolicitud);
                insertValues.put("[ususol]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("userMC",""));
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                Date date = new Date();

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
                    insertValuesOld.put("[W_CTE-BUKRS]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_BUKRS",""));
                    insertValuesOld.put("[W_CTE-RUTAHH]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_RUTAHH",""));
                    insertValuesOld.put("[W_CTE-VKORG]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("W_CTE_VKORG",""));
                    insertValuesOld.put("[id_solicitud]", NextId);
                    insertValuesOld.put("[tipform]", tipoSolicitud);
                    insertValuesOld.put("[ususol]", PreferenceManager.getDefaultSharedPreferences(SolicitudAvisosEquipoFrioActivity.this).getString("userMC",""));
                    insertValuesOld.put("[FECCRE]", dateFormat.format(date));
                    long insertoOld = mDb.insertOrThrow("FormHvKof_old_solicitud", null, insertValuesOld);
                    //Una vez finalizado el proceso de guardado, se limpia la solicitud para una nueva.
                    Intent sol = getIntent();
                    sol.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    SolicitudAvisosEquipoFrioActivity.this.finish();
                    //Bundle par = new Bundle();
                    //par.putString("tipo_solicitud",tipoSolicitud);
                    //SolicitudActivity.this.startActivity(sol);
                    Toasty.success(getApplicationContext(), "Solicitud de Equipo Frio Creada", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toasty.error(getApplicationContext(), "Error Insertando Solicitud."+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void LlenarCampos(Context context, Activity activity, ArrayList<JsonArray> estructurasSAP){
        if(estructurasSAP.size() == 0){
            Toasty.error(context.getApplicationContext(),"No se pudo obtener la informacion del cliente. Asegure la conexion e intente de nuevo.").show();
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

        if(codigoEquipoFrio != null) {
            EquipoFrio equipo = mDBHelper.getEquipoFrioDB(codigoCliente, codigoEquipoFrio, true);
            MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-IM_EQUIPMENT"));
            tv.setText(codigoEquipoFrio);
            MaskedEditText tvs = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-IM_SERIALNO"));
            tvs.setText(equipo.getSernr());
            try {
                SearchableSpinner tvm = ((SearchableSpinner) mapeoCamposDinamicos.get("W_CTE-IM_MATERIAL"));
                tvm.setSelection(VariablesGlobales.getIndex(tvm, equipo.getMatnr()));
            }catch(Exception e){
                MaskedEditText tvm = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-IM_MATERIAL"));
                tvm.setText(equipo.getMatnr());
            }
            MaskedEditText tvp = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-IM_PARTNER"));
            tvp.setText(codigoCliente);

            //Valores Enca si estan presentes
            MaskedEditText tve = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-IM_EQUIPMENT"));
            if (tve != null)
                tve.setText(codigoEquipoFrio);
            MaskedEditText tvse = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-IM_SERIALNO"));
            if (tvse != null)
                tvse.setText(equipo.getSernr());
            try{
                SearchableSpinner tvme = ((SearchableSpinner) mapeoCamposDinamicosEnca.get("W_CTE-IM_MATERIAL"));
                if (tvme != null)
                    tvme.setSelection(VariablesGlobales.getIndex(tvme, equipo.getMatnr()));
            }catch(Exception e){
                MaskedEditText tvm = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-IM_MATERIAL"));
                tvm.setText(equipo.getMatnr());
            }
            MaskedEditText tvpe = ((MaskedEditText) mapeoCamposDinamicosEnca.get("W_CTE-IM_PARTNER"));
            if (tvpe != null)
                tvpe.setText(codigoCliente);
            if (tipoSolicitud.equals(context.getResources().getString(R.string.ID_FORM_CAMBIO_EQ))) {
                String grupoCausa = "";
                String grupoSintoma = "";
                String codigoCausa = "";
                String codigoSintoma = "";
                if (equipo.getEqart().contains("ENF")) {
                    grupoCausa = "CS-CAENF";
                    grupoSintoma = "CA-ENFRI";
                    codigoCausa = "0580";
                    codigoSintoma = "0270";
                }else
                if (equipo.getEqart().contains("POS") || equipo.getEqart().contains("PMX") ) {
                    grupoCausa = "CS-CAPMX";
                    grupoSintoma = "CA-POSTM";
                    codigoCausa = "0640";
                    codigoSintoma = "0360";
                }else
                if (equipo.getEqart().contains("MAQ") || equipo.getEqart().contains("MQ")) {
                    grupoCausa = "CS-CAMQH";
                    grupoSintoma = "CA-MAQHI";
                    codigoCausa = "0080";
                    codigoSintoma = "0260";
                } else {
                    grupoCausa = "CS-CAENF";
                    grupoSintoma = "CA-ENFRI";
                    codigoCausa = "0580";
                    codigoSintoma = "0270";
                }

                SearchableSpinner dcodegrpe = ((SearchableSpinner) mapeoCamposDinamicosEnca.get("W_CTE-IM_D_CODEGRP"));
                if (dcodegrpe != null)
                    dcodegrpe.setSelection(VariablesGlobales.getIndex(dcodegrpe, grupoSintoma));
                SearchableSpinner causecodegrpe = ((SearchableSpinner) mapeoCamposDinamicosEnca.get("W_CTE-IM_CAUSE_CODEGRP"));
                if (causecodegrpe != null)
                    causecodegrpe.setSelection(VariablesGlobales.getIndex(causecodegrpe, grupoCausa));
                SearchableSpinner dcodee = ((SearchableSpinner) mapeoCamposDinamicosEnca.get("W_CTE-IM_D_CODE"));
                if (dcodee != null)
                    dcodee.setSelection(VariablesGlobales.getIndex(dcodee, codigoSintoma));
                SearchableSpinner causecodee = ((SearchableSpinner) mapeoCamposDinamicosEnca.get("W_CTE-IM_CAUSE_CODE"));
                if (causecodee != null)
                    causecodee.setSelection(VariablesGlobales.getIndex(causecodee, codigoCausa));
            }
        }
        //Titulo deseado
        activity.setTitle(activity.getTitle()+cliente.get(0).getAsJsonObject().get("W_CTE-NAME1").getAsString());

        for (int i = 0; i < listaCamposDinamicos.size(); i++) {
            if(!listaCamposBloque.contains(listaCamposDinamicos.get(i).trim()) && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                try {
                    MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                    if(tv!= null) {
                        if (cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)) != null) {
                            tv.setText(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString());
                            if (listaCamposDinamicos.get(i).equals("W_CTE-PO_BOX") && tv.getText().toString().trim().equals("")) {
                                tv.setText(".");
                            }

                            if (listaCamposDinamicos.get(i).equals("W_CTE-ZIBASE")) {
                                String valorSinCeros = removeLeadingZeroes(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString());
                                tv.setText(valorSinCeros);
                            }
                        }
                        if (listaCamposDinamicos.get(i).equals("W_CTE-IM_SHORT_TEXT")) {
                            tv.setText(((SolicitudAvisosEquipoFrioActivity) activity).getSupportActionBar().getSubtitle());
                        }
                        if (listaCamposDinamicos.get(i).equals("W_CTE-IM_PARTNER")) {
                            tv.setText(codigoCliente);
                            tv.setVisibility(View.GONE);
                            ((TextInputLayout) tv.getParent().getParent()).setVisibility(View.GONE);
                        }
                    }

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get(listaCamposDinamicos.get(i).trim()));
                    if(tv != null) {
                        tv.setText(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString());
                        if (listaCamposDinamicos.get(i).equals("W_CTE-ZIBASE")) {
                            String valorSinCeros = removeLeadingZeroes(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString());
                            tv.setText(valorSinCeros);
                        }
                    }
                    tv = ((MaskedEditText) mapeoCamposDinamicosEnca.get(listaCamposDinamicos.get(i).trim()));
                    if(tv != null) {
                        tv.setText(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i).trim()).getAsString());
                        if (listaCamposDinamicos.get(i).equals("W_CTE-ZIBASE")) {
                            String valorSinCeros = removeLeadingZeroes(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString());
                            tv.setText(valorSinCeros);
                        }
                    }

                } catch (Exception e) {
                    try {
                        Spinner sp = ((Spinner) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                        if(cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)) != null)
                            sp.setSelection(VariablesGlobales.getIndex(sp,cliente.get(0).getAsJsonObject().get(listaCamposDinamicos.get(i)).getAsString().trim()));

                        if (listaCamposDinamicos.get(i).equals("W_CTE-IM_NOTIF_TYPE")) {
                            if(((SolicitudAvisosEquipoFrioActivity)activity).getSupportActionBar().getSubtitle().toString().toLowerCase().contains("mant")) {
                                if(((SolicitudAvisosEquipoFrioActivity)activity).getSupportActionBar().getSubtitle().toString().toLowerCase().contains("corr"))
                                    sp.setSelection(VariablesGlobales.getIndex(sp, "T2"));
                                if(((SolicitudAvisosEquipoFrioActivity)activity).getSupportActionBar().getSubtitle().toString().toLowerCase().contains("prev"))
                                    sp.setSelection(VariablesGlobales.getIndex(sp, "T3"));
                            }
                            if(((SolicitudAvisosEquipoFrioActivity)activity).getSupportActionBar().getSubtitle().toString().toLowerCase().contains("instal")) {
                                sp.setSelection(VariablesGlobales.getIndex(sp, "T4"));
                            }
                            if(((SolicitudAvisosEquipoFrioActivity)activity).getSupportActionBar().getSubtitle().toString().toLowerCase().contains("cambio")) {
                                sp.setSelection(VariablesGlobales.getIndex(sp, "T9"));
                            }
                            if(((SolicitudAvisosEquipoFrioActivity)activity).getSupportActionBar().getSubtitle().toString().toLowerCase().contains("retiro")) {
                                sp.setSelection(VariablesGlobales.getIndex(sp, "T5"));
                            }
                            sp.setEnabled(false);
                        }

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
                if(tv != null) {
                    tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_NUMBER").getAsString());
                    tv = ((MaskedEditText) mapeoCamposDinamicos.get("W_CTE-FAX_EXTENS"));
                    tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_EXTENS").getAsString());

                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-FAX_NUMBER"));
                    tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_NUMBER").getAsString());
                    tv = ((MaskedEditText) mapeoCamposDinamicosOld.get("W_CTE-FAX_EXTENS"));
                    tv.setText(faxes.get(0).getAsJsonObject().get("W_CTE-FAX_EXTENS").getAsString());
                }
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

    private static void CausasDeGrupo(AdapterView<?> parent, String valorDefectoHijo, String clase_aviso){
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> causas = mDBHelper.CausasDeGrupo(opcion.getId(),clase_aviso);

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < causas.size(); j++){
            listaopciones.add(new OpcionSpinner(causas.get(j).get("id"), causas.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-IM_CAUSE_CODE").trim().equals(causas.get(j).get("id"))){
                selectedIndex = j;
            }else if(valorDefectoHijo.equals(causas.get(j).get("id"))){
                selectedIndex = j;
            }
        }

        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-IM_CAUSE_CODE");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        combo.setSelection(selectedIndex);
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
        }
    }
    private static void SintomasDeGrupo(AdapterView<?> parent, String valorDefectoHijo, String clase_aviso){
        final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
        ArrayList<HashMap<String, String>> sintomas = mDBHelper.SintomasDeGrupo(opcion.getId(),clase_aviso);

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        int selectedIndex = 0;
        for (int j = 0; j < sintomas.size(); j++){
            listaopciones.add(new OpcionSpinner(sintomas.get(j).get("id"), sintomas.get(j).get("descripcion")));
            if(solicitudSeleccionada.size() > 0 && solicitudSeleccionada.get(0).get("W_CTE-IM_D_CODE").trim().equals(sintomas.get(j).get("id"))){
                selectedIndex = j;
            }else if(valorDefectoHijo.equals(sintomas.get(j).get("id"))){
                selectedIndex = j;
            }
        }

        Spinner combo = (Spinner)mapeoCamposDinamicos.get("W_CTE-IM_D_CODE");
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(parent.getContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d = parent.getResources().getDrawable(R.drawable.spinner_background, null);
        combo.setBackground(d);
        combo.setAdapter(dataAdapter);
        combo.setSelection(selectedIndex);
        if(!modificable){
            combo.setEnabled(false);
            combo.setBackground(parent.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
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
            if (map.containsValue(campo) && map.containsValue(agencia)) {
                return i;
            }
        }
        return -1; // Not found.
    }
    public static String removeLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        return str;
    }
    public static String getValorDefectoCampo(ArrayList<HashMap<String, String>> campos,String campo){
        String valorDefectoHijo="";
        for(HashMap hm :campos){
            if(hm.get("campo").toString().trim().equals(campo))
            {
                valorDefectoHijo = hm.get("dfaul").toString().trim();
                break;
            }
        }
        return valorDefectoHijo;
    }
}
