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
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
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
import proyecto.app.clientesabc.adaptadores.EquipoDisponibleAdapter;
import proyecto.app.clientesabc.clases.AdjuntoAPI;
import proyecto.app.clientesabc.clases.AdjuntoServidor;
import proyecto.app.clientesabc.clases.KeyPairBoolData;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.MovableFloatingActionButton;
import proyecto.app.clientesabc.clases.MultiSpinnerListener;
import proyecto.app.clientesabc.clases.MultiSpinnerSearch;
import proyecto.app.clientesabc.clases.TraerEquipoDisponibleServidor;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoAPI;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoServidor;
import proyecto.app.clientesabc.clases.ValidacionAnomaliaServidor;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class EquipoDisponibleActivity extends AppCompatActivity{
    DataBaseHelper db;
    public static SQLiteDatabase mDb;
    private RecyclerView recyclerView;
    private static EquipoDisponibleAdapter mAdapter;
    LocacionGPSActivity locationServices;
    double latitude = 0.0;
    double longitude = 0.0;
    private SearchView searchView;
    //private MyAdapter mAdapter;
    private MovableFloatingActionButton fab;
    boolean isFABOpen = false;
    String codigo_cliente;
    String nombre_cliente;
    String canal_cliente;
    String correo_cliente;
    static ArrayList<HashMap<String, String>> formList;
    ArrayList<HashMap<String, String>> filteredFormList;
    Toolbar toolbar;
    static Uri mPhotoUri;
    private CameraManager mCameraManager;
    private String mCameraId;
    private FloatingActionButton toggleButton;
    private boolean activar_foco;

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
        mDb = db.getWritableDatabase();

        setContentView(R.layout.activity_equipo_disponible);

        WeakReference<Context> weakRefs1 = new WeakReference<Context>(this);
        WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(EquipoDisponibleActivity.this);
        TraerEquipoDisponibleServidor v = new TraerEquipoDisponibleServidor(weakRefs1, weakRefAs1, PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("W_CTE_VWERK",""), "39","0");
        v.execute();

        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Equipo Frio Disponible x Modelo");
        toolbar.setTitleTextAppearance(this,R.style.Toolbar_TitleText);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorTextView,null));
        toolbar.setBackground(d);

        if (Build.VERSION.SDK_INT >= 28) {
            toolbar.setOutlineAmbientShadowColor(getResources().getColor(R.color.aprobados,null));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    public static void AsignarLista(Context context, Activity activity, ArrayList<JsonArray> mensajes) {
        String mensaje="";
        ArrayList<HashMap<String, String>> listaopciones = new ArrayList<>();

        //opcion.put("","Seleccione...");
        if(mensajes.size() > 0 && mensajes.get(0)  != null && mensajes.get(0).size() > 0){
            for(int x = 0; x < mensajes.get(0).getAsJsonArray().size() ; x++){
                JsonObject jsonOpcion = mensajes.get(0).getAsJsonArray().get(x).getAsJsonObject();
                HashMap<String, String> opcion = new HashMap<>();
                opcion.put("modelo",jsonOpcion.get("modelo").getAsString());
                opcion.put("stock",jsonOpcion.get("stock").getAsString());
                opcion.put("reservado",jsonOpcion.get("reservado").getAsString());
                opcion.put("centro_suministro",jsonOpcion.get("centro_suministro").getAsString());
                opcion.put("desc_centro_suministro",jsonOpcion.get("desc_centro_suministro").getAsString());
                opcion.put("estado",jsonOpcion.get("estado").getAsString());
                opcion.put("emplazamiento",jsonOpcion.get("emplazamiento").getAsString());
                opcion.put("num_puertas",jsonOpcion.get("num_puertas").getAsString());
                listaopciones.add(opcion);
            }

            formList = listaopciones;

            RecyclerView rv = activity.findViewById(R.id.recycler_view);

            mAdapter = new EquipoDisponibleAdapter(formList,context, activity);
            rv.setLayoutManager(new LinearLayoutManager(context));
            rv.setAdapter(mAdapter);
            rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        }
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
        //formList = db.getCensoEquiposFriosDB(codigo_cliente);
        /*RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new EquipoDisponibleAdapter(formList,this, EquipoDisponibleActivity.this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));
        toolbar.setTitle("Equipo Frio Disponible x Modelo");
        toolbar.setSubtitleTextColor(Color.DKGRAY);*/
    }
    public static void mostrarImagenServidor(Context context, Activity activity, Adjuntos adjunto) {
        final Dialog d = new Dialog(context, R.style.MyAlertDialogTheme);
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
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
            AdjuntoAPI s = new AdjuntoAPI(weakRefs, weakRefAs, adjunto_img, adjunto_txt, d);
            if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
                s.EnableWiFi();
            }
            s.execute();
        } else {
            AdjuntoServidor s = new AdjuntoServidor(weakRefs, weakRefAs, adjunto_img, adjunto_txt);
            if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
                s.EnableWiFi();
            }else{
                s.DisableWiFi();
            }
            s.execute();
        }

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
}
