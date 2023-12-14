package proyecto.app.clientesabc.actividades;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.startActivity;
import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;

import static proyecto.app.clientesabc.R.drawable.textbackground;

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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.vicmikhailau.maskededittext.MaskedEditText;

import org.intellij.lang.annotations.Language;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.BaseInstaladaAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.ConsultaClienteAPI;
import proyecto.app.clientesabc.clases.ConsultaClienteServidor;
import proyecto.app.clientesabc.clases.ConsultaEquipoFrioServidor;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.FileHelper;
import proyecto.app.clientesabc.clases.KeyPairBoolData;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.MovableFloatingActionButton;
import proyecto.app.clientesabc.clases.MultiSpinnerListener;
import proyecto.app.clientesabc.clases.MultiSpinnerSearch;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.TesseractOCR;
import proyecto.app.clientesabc.clases.TransmisionAPI;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoServidor;
import proyecto.app.clientesabc.clases.TransmisionServidor;
import proyecto.app.clientesabc.clases.ValidacionAnomaliaServidor;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

import androidx.core.content.ContextCompat;

public class BaseInstaladaActivity extends AppCompatActivity implements LocacionGPSActivity.LocationListenerCallback{
    DataBaseHelper db;
    public static SQLiteDatabase mDb;
    private RecyclerView recyclerView;
    private static BaseInstaladaAdapter mAdapter;
    LocacionGPSActivity locationServices;
    double latitude = 0.0;
    double longitude = 0.0;
    private SearchView searchView;
    //private MyAdapter mAdapter;
    private MovableFloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    boolean isFABOpen = false;
    String codigo_cliente;
    String nombre_cliente;
    String canal_cliente;
    String correo_cliente;
    ArrayList<EquipoFrio> formList;
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

        db = new DataBaseHelper(this);
        mDb = db.getWritableDatabase();
        /*if(estado != null && tipform != null)
            formList = db.getSolicitudes(estado,tipform);
        else if(estado != null)
            formList = db.getSolicitudes(estado,null);
        else if(tipform != null)
            formList = db.getSolicitudes(null,tipform);
        else
            formList = db.getSolicitudes();*/
        formList = db.getCensoEquiposFriosDB(codigo_cliente);

        setContentView(R.layout.activity_base_instalada);
        RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new BaseInstaladaAdapter(formList,this,BaseInstaladaActivity.this,canal_cliente,correo_cliente,nombre_cliente);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        fab = findViewById(R.id.scanBtn);
        fab2 = findViewById(R.id.camaraBtn);
        fab3 = findViewById(R.id.manualBtn);

        boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        if (!isFlashAvailable) {
            showNoFlashError();
        }
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        toggleButton = findViewById(R.id.onOffFlashlight);
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFlashLight(activar_foco);
            }
        });

        //fab1.hide();
        fab3.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EscanearActivity.class);
                Bundle bc = new Bundle();
                bc.putString("campoEscaneo", "censo_equipo_frio");
                bc.putInt("requestCode", VariablesGlobales.ESCANEO_EQUIPO_FRIO);
                bc.putBoolean("flash", activar_foco);
                intent.putExtras(bc); //Pase el parametro el Intent
                startActivityForResult(intent,VariablesGlobales.ESCANEO_EQUIPO_FRIO);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryintent = new Intent(Intent.ACTION_GET_CONTENT, null);
                galleryintent.setType("image/*");
                mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryintent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");

                Intent[] intentArray = { cameraIntent };
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, VariablesGlobales.ESCANEO_OCR);
                /*mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new ContentValues());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                try {
                    startActivityForResult(intent, VariablesGlobales.ESCANEO_OCR);
                } catch (ActivityNotFoundException e) {
                    Log.e("tag", getResources().getString(R.string.no_activity));
                }*/

            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog d=new Dialog(view.getContext());
                d.setContentView(R.layout.digitar_equipo_frio_dialog_layout);

                //INITIALIZE VIEWS
                final TextView title = d.findViewById(R.id.title);
                final MaskedEditText equipoFrio = (MaskedEditText) d.findViewById(R.id.equipoFrio);
                //equipoFrio.setTitle("Digite la placa del equipo");
                //equipoFrio.setPositiveButton("Cerrar");
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                lp.setMargins(10, 25, 10, 25);
                equipoFrio.setPadding(10,10,10,10);
                equipoFrio.setLayoutParams(lp);
                Drawable back = getResources().getDrawable(R.drawable.textbackground, null);
                equipoFrio.setBackground(back);
                Button saveBtn= d.findViewById(R.id.saveBtn);

                //SAVE, en este caso solo es aceptar, ir a a pintar el formulario correspondiente dependiendo del equipo frio seleccionado
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String codigoEquipoFrio = equipoFrio.getText().toString();
                        if(codigoEquipoFrio.isEmpty()){
                            Toasty.warning(v.getContext(), "Por favor digite un placa de equipo frio!", Toast.LENGTH_SHORT).show();
                        }
                        try{
                            d.dismiss();
                            Bundle b = new Bundle();
                            b.putString("tipoSolicitud", "200");
                            b.putString("codigoCliente", codigo_cliente);
                            b.putString("codigoEquipoFrio", codigoEquipoFrio);
                            Intent intent = new Intent(getApplicationContext(), SolicitudAvisosEquipoFrioActivity.class);
                            intent.putExtras(b); //Pase el parametro el Intent
                            startActivity(intent);
                        } catch(Exception e) {
                            Toasty.error(v.getContext(), "No se pudo abrir la solicitud de alerta."+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //SHOW DIALOG
                d.show();
                Window window = d.getWindow();
                if (window != null) {
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });


        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(codigo_cliente +" - "+nombre_cliente);
        toolbar.setSubtitle("Base Instalada ("+mAdapter.getItemCount()+")");
        toolbar.setTitleTextAppearance(this,R.style.Toolbar_TitleText);
        /*if(estado != null && tipform != null)
            toolbar.setSubtitle("Filtro: "+estado+" / "+tipform);
        else if(estado != null)
            toolbar.setSubtitle("Filtro: "+estado);
        else if(tipform != null)
            toolbar.setSubtitle("Filtro: "+tipform);*/
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorTextView,null));
        toolbar.setBackground(d);


        if (Build.VERSION.SDK_INT >= 28) {
            toolbar.setOutlineAmbientShadowColor(getResources().getColor(R.color.aprobados,null));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        locationServices = new LocacionGPSActivity(BaseInstaladaActivity.this, BaseInstaladaActivity.this);
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
        formList = db.getCensoEquiposFriosDB(codigo_cliente);
        RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new BaseInstaladaAdapter(formList,this, BaseInstaladaActivity.this,canal_cliente,correo_cliente,nombre_cliente);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));
        toolbar.setTitle(codigo_cliente +" - "+nombre_cliente);
        toolbar.setSubtitle("Base Instalada ("+mAdapter.getItemCount()+")");
        toolbar.setSubtitleTextColor(Color.DKGRAY);
    }

    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    public void switchFlashLight(boolean status) {
        if(status){
            toggleButton.setImageIcon(Icon.createWithResource(getApplicationContext(),R.drawable.icon_flashlight_off));
        }else{
            toggleButton.setImageIcon(Icon.createWithResource(getApplicationContext(),R.drawable.icon_flashlight_on));
        }
        activar_foco = !activar_foco;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        Bundle b = null;
        if (data != null)
            b = data.getExtras();
        if (b != null) {
            //campoEscaneo = b.getString("campoEscaneo");
            if (b.getInt("requestCode") != 0)
                requestCode = b.getInt("requestCode");
        }

        if (requestCode == VariablesGlobales.ESCANEO_EQUIPO_FRIO) {
            if (resultCode == RESULT_OK) {
                if (b != null) {
                    //Se verifica el codigo leida y se pueden dar las siguientes situaciones:
                    //1. El codigo del equipo frio si existe en el cliente, simplemente se marca como censado
                    //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                    //3. El codigo del equipo leida esta en otro cliente
                    //4. Hay un equipo que no puede ser censado pero si esta en la lista del cliente(NO tiene placa, NO esta en sitio, no existe), Se debe poder indicar que el equipo no pudo ser censado y ver que estado ponerle

                    //Caso 1. El codigo del equipo frio si exsite en el cliente, simplemente se marca como censado con un nuevo regsitro en CensoEquipoFrio
                    if (db.ExisteEquipoFrioEnCliente(codigo_cliente, b.getString("codigo"))) {
                        EquipoFrio eq = db.getEquipoFrioDB(codigo_cliente, b.getString("codigo"), false);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Verificado");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",eq.getSerge());
                        insertValues.put("coordenada_x", latitude);
                        insertValues.put("coordenada_y", longitude);
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("num_activo", eq.getSernr());
                        insertValues.put("num_equipo", eq.getEqunr());
                        insertValues.put("modelo_equipo", eq.getMatnr());
                        insertValues.put("correo", correo_cliente);
                        insertValues.put("canal", canal_cliente);
                        insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("userMC",""));

                        if(latitude == 0 && longitude == 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(BaseInstaladaActivity.this);
                            builder.setIcon(R.drawable.icon_info_title);
                            builder.setTitle("Confirmación");
                            builder.setCancelable(false);
                            builder.setMessage("No se han capturado las coordenadas geograficas. Desea continuar de todas maneras?");
                            Bundle finalB = b;
                            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", continue with execution
                                    long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                    if(inserto == -1){
                                        Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                    }else{
                                        //Intentar 1 vez el envio automatico de la lectura.
                                        WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                        if (VariablesGlobales.UsarAPI()) {
                                            /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                            if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                EnableWiFi();
                                            }
                                            f.execute();*/
                                        } else {
                                            EquipoFrio ef = db.getEquipoFrioDatosCenso(finalB.getString("codigo"));
                                            TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                            if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                f.EnableWiFi();
                                            } else {
                                                f.DisableWiFi();
                                            }
                                            f.execute();
                                            //Validacion de Anomalia Pendiente del equipo Verificado Local y Del servidor
                                            long update = db.ValidacionAnomalia(finalB.getString("codigo"));
                                            //if(update > 0) {
                                            ValidacionAnomaliaServidor a = new ValidacionAnomaliaServidor(weakRef, weakRefA, ef);
                                            if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                a.EnableWiFi();
                                            } else {
                                                a.DisableWiFi();
                                            }
                                            a.execute();
                                            //}
                                        }
                                    }
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
                        }else{
                            long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                            if(inserto == -1){
                                Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                            }else{
                                //Intentar 1 vez el envio automatico de la lectura.
                                WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                if (VariablesGlobales.UsarAPI()) {
                                /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }
                                f.execute();*/
                                } else {
                                    EquipoFrio ef = db.getEquipoFrioDatosCenso(b.getString("codigo"));
                                    TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                    if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                        f.EnableWiFi();
                                    } else {
                                        f.DisableWiFi();
                                    }
                                    f.execute();
                                    //Validacion de Anomalia Pendiente del equipo Verificado Local y Del servidor
                                    long update = db.ValidacionAnomalia(b.getString("codigo"));
                                    //if(update > 0) {
                                    ValidacionAnomaliaServidor a = new ValidacionAnomaliaServidor(weakRef, weakRefA, ef);
                                    if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                        a.EnableWiFi();
                                    } else {
                                        a.DisableWiFi();
                                    }
                                    a.execute();
                                    //}
                                }
                            }
                        }

                    }else
                    //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                    if (!db.ExisteEquipoFrio(b.getString("codigo"))) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Hallazgo");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",b.getString("codigo").trim());
                        insertValues.put("coordenada_x", latitude);
                        insertValues.put("coordenada_y", longitude);
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("correo", correo_cliente);
                        insertValues.put("canal", canal_cliente);
                        insertValues.put("comentario","Número de placa no aparece en ningun cliente instalado.");

                        if(latitude == 0 && longitude == 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(BaseInstaladaActivity.this);
                            builder.setIcon(R.drawable.icon_info_title);
                            builder.setTitle("Confirmación");
                            builder.setCancelable(false);
                            builder.setMessage("No se han capturado las coordenadas geograficas. Desea continuar de todas maneras?");
                            Bundle finalB = b;
                            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", continue with execution
                                    long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                    if(inserto == -1){
                                        Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                    }else{
                                        //Intentar 1 vez el envio automatico de la lectura.
                                        WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                        if (VariablesGlobales.UsarAPI()) {
                                            /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                            if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                EnableWiFi();
                                            }
                                            f.execute();*/
                                        } else {
                                            EquipoFrio ef = new EquipoFrio();
                                            ef.setKunnrCenso(codigo_cliente);
                                            ef.setEstado("Hallazgo");
                                            ef.setNumPlaca(finalB.getString("codigo").trim());
                                            ef.setSerge(finalB.getString("codigo").trim());
                                            ef.setActivo("1");
                                            ef.setTransmitido("0");
                                            ef.setFechaLectura(dateFormat.format(date));
                                            ef.setComentario("Número de placa no aparece en ningun cliente instalado.");

                                            TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                            if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                f.EnableWiFi();
                                            } else {
                                                f.DisableWiFi();
                                            }
                                            f.execute();
                                        }
                                    }
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
                        }else{
                            long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                            if(inserto == -1){
                                Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                            }else{
                                //Intentar 1 vez el envio automatico de la lectura.
                                WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                if (VariablesGlobales.UsarAPI()) {
                                            /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                            if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                EnableWiFi();
                                            }
                                            f.execute();*/
                                } else {
                                    EquipoFrio ef = new EquipoFrio();
                                    ef.setKunnrCenso(codigo_cliente);
                                    ef.setEstado("Hallazgo");
                                    ef.setNumPlaca(b.getString("codigo").trim());
                                    ef.setSerge(b.getString("codigo").trim());
                                    ef.setActivo("1");
                                    ef.setTransmitido("0");
                                    ef.setFechaLectura(dateFormat.format(date));
                                    ef.setComentario("Número de placa no aparece en ningun cliente instalado.");

                                    TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                    if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                        f.EnableWiFi();
                                    } else {
                                        f.DisableWiFi();
                                    }
                                    f.execute();
                                }
                            }
                        }
                    }//3. El codigo del equipo leida esta en otro cliente
                    else if (db.ExisteEquipoFrio(b.getString("codigo"))) {
                        EquipoFrio eq = db.getEquipoFrioDatosCenso(b.getString("codigo"));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Hallazgo");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",eq.getSerge());
                        insertValues.put("coordenada_x", latitude);
                        insertValues.put("coordenada_y", longitude);
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("num_activo", eq.getSernr());
                        insertValues.put("num_equipo", eq.getEqunr());
                        insertValues.put("modelo_equipo", eq.getMatnr());
                        insertValues.put("correo", correo_cliente);
                        insertValues.put("canal", canal_cliente);
                        insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("userMC",""));
                        insertValues.put("comentario","Pertenece a otro cliente "+eq.getKunnr()+"!");

                        if(latitude == 0 && longitude == 0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(BaseInstaladaActivity.this);
                            builder.setIcon(R.drawable.icon_info_title);
                            builder.setTitle("Confirmación");
                            builder.setCancelable(false);
                            builder.setMessage("No se han capturado las coordenadas geograficas. Desea continuar de todas maneras?");
                            Bundle finalB = b;
                            builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //if user pressed "yes", continue with execution
                                    long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                    if(inserto == -1){
                                        Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                    }else{
                                        //Intentar 1 vez el envio automatico de la lectura.
                                        WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                        if (VariablesGlobales.UsarAPI()) {
                                /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }
                                f.execute();*/
                                        } else {
                                            EquipoFrio ef = db.getEquipoFrioDatosCenso(finalB.getString("codigo"));
                                            TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                            if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                f.EnableWiFi();
                                            } else {
                                                f.DisableWiFi();
                                            }
                                            f.execute();
                                        }
                                    }
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
                        }else{
                            long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                            if(inserto == -1){
                                Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                            }else{
                                //Intentar 1 vez el envio automatico de la lectura.
                                WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                if (VariablesGlobales.UsarAPI()) {
                                /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                    EnableWiFi();
                                }
                                f.execute();*/
                                } else {
                                    EquipoFrio ef = db.getEquipoFrioDatosCenso(b.getString("codigo"));
                                    TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                    if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                        f.EnableWiFi();
                                    } else {
                                        f.DisableWiFi();
                                    }
                                    f.execute();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (requestCode == VariablesGlobales.ESCANEO_ARCHIVO) {
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
                try {
                    ContentResolver cR = getContentResolver();
                    String type = cR.getType(uri);
                    String name = ManejadorAdjuntos.getFileName(cR, uri);
                    byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                    File file = null;
                    try {
                        file = new File(getExternalFilesDir(null).getAbsolutePath());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file + "//" + name);
                        fos.write(inputData);
                        fos.close();
                    } catch (Exception e) {
                        Log.e("thumbnail", e.getMessage());
                    }
                    File file2 = new File(getExternalFilesDir(null).getAbsolutePath() + "//" + name);
                    String filePath = file2.getPath();
                    image = BitmapFactory.decodeFile(filePath);

                    InputImage inputImage = InputImage.fromBitmap(image, 0);

                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                    Task<Text> result =
                            recognizer.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text visionText) {
                                            Toasty.info(getApplicationContext(), "RECONOCIO LOS CARACTERES : " + visionText.getText()).show();
                                        }
                                    })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toasty.error(getApplicationContext(), "Fallo al procesar la imagen capturada!").show();
                                                    e.printStackTrace();
                                                }
                                            });


                } catch (IOException e) {
                    Toasty.error(this, "Error al asociar el documento a la solicitud").show();
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == VariablesGlobales.ESCANEO_OCR) {
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
                try {
                    ContentResolver cR = getContentResolver();
                    String type = cR.getType(uri);
                    String name = ManejadorAdjuntos.getFileName(cR, uri);
                    byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                    File file = null;
                    try {
                        file = new File(getExternalFilesDir(null).getAbsolutePath());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file + "//" + name);
                        fos.write(inputData);
                        fos.close();
                    } catch (Exception e) {
                        Log.e("thumbnail", e.getMessage());
                    }
                    File file2 = new File(getExternalFilesDir(null).getAbsolutePath() + "//" + name);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(file2.getAbsolutePath(), options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;
                    if (data != null && data.getData() != null) {
                        String filePath = file2.getPath();
                        image = BitmapFactory.decodeFile(filePath);

                        InputImage inputImage = InputImage.fromBitmap(image, 0);
                        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                        Task<Text> result = recognizer.process(inputImage)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        Toasty.info(getApplicationContext(), "RECONOCIO LOS CARACTERES : " + visionText.getText()).show();

                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toasty.error(getApplicationContext(), "Fallo al procesar la imagen capturada!").show();
                                                e.printStackTrace();
                                            }
                                        });
                    } else {
                        /*CROP*/
                        try {
                            Intent intent = CropImage.activity(mPhotoUri).getIntent(this);
                            startActivityForResult(intent, 210);
                        }
                        // respond to users whose devices do not support the crop action
                        catch (ActivityNotFoundException anfe) {
                            Toast toast = Toast.makeText(this, "Este dispositivo no soporta la acción de recortar!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                    /*END CROP*/
                } catch (IOException e) {
                    Toasty.error(this, "Error al asociar el documento a la solicitud").show();
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == 210) {
            Uri uri = null;
            Uri uriCopia = null;
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    uri = result.getUri();
                    uriCopia = result.getOriginalUri();
                    /*Copiar el archivo a un Uri que si puedo utilizar*/
                    InputStream is = null;
                    try {
                        is = getContentResolver().openInputStream(uri);
                        OutputStream os = null;
                        os = getContentResolver().openOutputStream(uriCopia);

                        byte[] bt = new byte[4096];
                        int read = 0;
                        while (true) {
                            if (!((read = is.read(bt)) != -1)) break;
                            os.write(bt, 0, read);
                        }
                        os.flush();
                        os.close();
                        is.close();
                    }catch (Exception e){
                        Toasty.error(this, e.getMessage()).show();
                    }
                    uri = uriCopia;
                }

                if (uri == null) {
                    uri = data.getData();
                }
                InputStream iStream = null;
                try {
                    iStream = getContentResolver().openInputStream(uri);
                    //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toasty.error(this, "Imagen seleccionada ya no existe en el dispositivo!").show();
                    return;
                }
                try {
                    ContentResolver cR = getContentResolver();
                    String type = cR.getType(uri);
                    String name = ManejadorAdjuntos.getFileName(cR, uri);
                    byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                    File file = null;
                    try {
                        file = new File(getExternalFilesDir(null).getAbsolutePath());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file + "//" + name);
                        fos.write(inputData);
                        fos.close();
                    } catch (Exception e) {
                        Log.e("thumbnail", e.getMessage());
                    }
                    File file2 = new File(getExternalFilesDir(null).getAbsolutePath() + "//" + name);
                    String filePath = file2.getPath();
                    image = BitmapFactory.decodeFile(filePath);
                    //image = ColorToGrayscale(image);
                    //image = GrayscaleToBin(image,75);

                    InputImage inputImage = InputImage.fromBitmap(image, 0);

                    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

                    Task<Text> result =
                            recognizer.process(inputImage)
                                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                                        @Override
                                        public void onSuccess(Text visionText) {
                                            Toasty.info(getApplicationContext(), "RECONOCIO LOS CARACTERES : " + visionText.getText()).show();
                                            //Se verifica el codigo leida y se pueden dar las siguientes situaciones:
                                            //1. El codigo del equipo frio si existe en el cliente, simplemente se marca como censado
                                            //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                                            //3. El codigo del equipo leida esta en otro cliente
                                            //4. Hay un equipo que no puede ser censado pero si esta en la lista del cliente(NO tiene placa, NO esta en sitio, no existe), Se debe poder indicar que el equipo no pudo ser censado y ver que estado ponerle

                                            //Caso 1. El codigo del equipo frio si exsite en el cliente, simplemente se marca como censado con un nuevo regsitro en CensoEquipoFrio
                                            if (db.ExisteEquipoFrioEnCliente(codigo_cliente, visionText.getText())) {
                                                EquipoFrio eq = db.getEquipoFrioDB(codigo_cliente, visionText.getText(), false);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                Date date = new Date();
                                                ContentValues insertValues = new ContentValues();
                                                insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BUKRS", ""));
                                                insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BZIRK", ""));
                                                insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_RUTAHH", ""));
                                                insertValues.put("estado", "Verificado");
                                                insertValues.put("kunnr_censo", codigo_cliente);
                                                insertValues.put("nombre_cliente", nombre_cliente);
                                                insertValues.put("num_placa", eq.getSerge());
                                                insertValues.put("coordenada_x", latitude);
                                                insertValues.put("coordenada_y", longitude);
                                                insertValues.put("activo", "1");
                                                insertValues.put("transmitido", "0");
                                                insertValues.put("fecha_lectura", dateFormat.format(date));
                                                insertValues.put("num_activo", eq.getSernr());
                                                insertValues.put("num_equipo", eq.getEqunr());
                                                insertValues.put("modelo_equipo", eq.getMatnr());
                                                insertValues.put("correo", correo_cliente);
                                                insertValues.put("canal", canal_cliente);
                                                insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("userMC", ""));

                                                if (latitude == 0 && longitude == 0) {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseInstaladaActivity.this);
                                                    builder.setIcon(R.drawable.icon_info_title);
                                                    builder.setTitle("Confirmación");
                                                    builder.setCancelable(false);
                                                    builder.setMessage("No se han capturado las coordenadas geograficas. Desea continuar de todas maneras?");

                                                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //if user pressed "yes", continue with execution
                                                            long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                                            if (inserto == -1) {
                                                                Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                                            } else {
                                                                //Intentar 1 vez el envio automatico de la lectura.
                                                                WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                                                WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                                                if (VariablesGlobales.UsarAPI()) {
                                                                    /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                                                    if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                                        EnableWiFi();
                                                                    }
                                                                    f.execute();*/
                                                                } else {
                                                                    EquipoFrio ef = db.getEquipoFrioDatosCenso(visionText.getText());
                                                                    TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                                                    if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                        f.EnableWiFi();
                                                                    } else {
                                                                        f.DisableWiFi();
                                                                    }
                                                                    f.execute();
                                                                    //Validacion de Anomalia Pendiente del equipo Verificado Local y Del servidor
                                                                    long update = db.ValidacionAnomalia(visionText.getText());
                                                                    //if(update > 0) {
                                                                    ValidacionAnomaliaServidor a = new ValidacionAnomaliaServidor(weakRef, weakRefA, ef);
                                                                    if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                        a.EnableWiFi();
                                                                    } else {
                                                                        a.DisableWiFi();
                                                                    }
                                                                    a.execute();
                                                                    //}
                                                                }
                                                            }
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
                                                } else {
                                                    long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                                    if (inserto == -1) {
                                                        Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                                    } else {
                                                        //Intentar 1 vez el envio automatico de la lectura.
                                                        WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                                        WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                                        if (VariablesGlobales.UsarAPI()) {
                                                                    /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                                                    if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                                        EnableWiFi();
                                                                    }
                                                                    f.execute();*/
                                                        } else {
                                                            EquipoFrio ef = db.getEquipoFrioDatosCenso(visionText.getText());
                                                            TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                                            if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                f.EnableWiFi();
                                                            } else {
                                                                f.DisableWiFi();
                                                            }
                                                            f.execute();
                                                            //Validacion de Anomalia Pendiente del equipo Verificado Local y Del servidor
                                                            long update = db.ValidacionAnomalia(visionText.getText());
                                                            //if(update > 0) {
                                                            ValidacionAnomaliaServidor a = new ValidacionAnomaliaServidor(weakRef, weakRefA, ef);
                                                            if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                a.EnableWiFi();
                                                            } else {
                                                                a.DisableWiFi();
                                                            }
                                                            a.execute();
                                                            //}
                                                        }
                                                    }
                                                }
                                            } else
                                                //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                                                if (!db.ExisteEquipoFrio(visionText.getText())) {
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                    Date date = new Date();
                                                    ContentValues insertValues = new ContentValues();
                                                    insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BUKRS", ""));
                                                    insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BZIRK", ""));
                                                    insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_RUTAHH", ""));
                                                    insertValues.put("estado", "Hallazgo");
                                                    insertValues.put("kunnr_censo", codigo_cliente);
                                                    insertValues.put("nombre_cliente", nombre_cliente);
                                                    insertValues.put("num_placa", visionText.getText());
                                                    insertValues.put("coordenada_x", latitude);
                                                    insertValues.put("coordenada_y", longitude);
                                                    insertValues.put("activo", "1");
                                                    insertValues.put("transmitido", "0");
                                                    insertValues.put("fecha_lectura", dateFormat.format(date));
                                                    insertValues.put("correo", correo_cliente);
                                                    insertValues.put("canal", canal_cliente);
                                                    insertValues.put("comentario", "Número de placa no aparece en ningun cliente instalado.");

                                                    if (latitude == 0 && longitude == 0) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(BaseInstaladaActivity.this);
                                                        builder.setIcon(R.drawable.icon_info_title);
                                                        builder.setTitle("Confirmación");
                                                        builder.setCancelable(false);
                                                        builder.setMessage("No se han capturado las coordenadas geograficas. Desea continuar de todas maneras?");

                                                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //if user pressed "yes", continue with execution
                                                                long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                                                if (inserto == -1) {
                                                                    Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                                                } else {
                                                                    //Intentar 1 vez el envio automatico de la lectura.
                                                                    WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                                                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                                                    if (VariablesGlobales.UsarAPI()) {
                                                            /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                                            if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                                EnableWiFi();
                                                            }
                                                            f.execute();*/
                                                                    } else {
                                                                        EquipoFrio ef = new EquipoFrio();
                                                                        ef.setKunnrCenso(codigo_cliente);
                                                                        ef.setEstado("Hallazgo");
                                                                        ef.setNumPlaca(visionText.getText());
                                                                        ef.setSerge(visionText.getText());
                                                                        ef.setActivo("1");
                                                                        ef.setTransmitido("0");
                                                                        ef.setFechaLectura(dateFormat.format(date));
                                                                        ef.setComentario("Número de placa no aparece en ningun cliente instalado.");

                                                                        TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                                                        if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                            f.EnableWiFi();
                                                                        } else {
                                                                            f.DisableWiFi();
                                                                        }
                                                                        f.execute();
                                                                    }
                                                                }
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
                                                    } else {
                                                        long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                                        if (inserto == -1) {
                                                            Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                                        } else {
                                                            //Intentar 1 vez el envio automatico de la lectura.
                                                            WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                                            if (VariablesGlobales.UsarAPI()) {
                                                            /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                                            if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                                EnableWiFi();
                                                            }
                                                            f.execute();*/
                                                            } else {
                                                                EquipoFrio ef = new EquipoFrio();
                                                                ef.setKunnrCenso(codigo_cliente);
                                                                ef.setEstado("Hallazgo");
                                                                ef.setNumPlaca(visionText.getText());
                                                                ef.setSerge(visionText.getText());
                                                                ef.setActivo("1");
                                                                ef.setTransmitido("0");
                                                                ef.setFechaLectura(dateFormat.format(date));
                                                                ef.setComentario("Número de placa no aparece en ningun cliente instalado.");

                                                                TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                                                if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                    f.EnableWiFi();
                                                                } else {
                                                                    f.DisableWiFi();
                                                                }
                                                                f.execute();
                                                            }
                                                        }
                                                    }
                                                }//3. El codigo del equipo leida esta en otro cliente
                                                else if (db.ExisteEquipoFrio(visionText.getText())) {
                                                    EquipoFrio eq = db.getEquipoFrioDatosCenso(visionText.getText());
                                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                                    Date date = new Date();
                                                    ContentValues insertValues = new ContentValues();
                                                    insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BUKRS", ""));
                                                    insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_BZIRK", ""));
                                                    insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("W_CTE_RUTAHH", ""));
                                                    insertValues.put("estado", "Hallazgo");
                                                    insertValues.put("kunnr_censo", codigo_cliente);
                                                    insertValues.put("nombre_cliente", nombre_cliente);
                                                    insertValues.put("num_placa", eq.getSerge());
                                                    insertValues.put("coordenada_x", latitude);
                                                    insertValues.put("coordenada_y", longitude);
                                                    insertValues.put("activo", "1");
                                                    insertValues.put("transmitido", "0");
                                                    insertValues.put("fecha_lectura", dateFormat.format(date));
                                                    insertValues.put("num_activo", eq.getSernr());
                                                    insertValues.put("num_equipo", eq.getEqunr());
                                                    insertValues.put("modelo_equipo", eq.getMatnr());
                                                    insertValues.put("correo", correo_cliente);
                                                    insertValues.put("canal", canal_cliente);
                                                    insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("userMC", ""));
                                                    insertValues.put("comentario", "Pertenece a otro cliente " + eq.getKunnr() + "!");


                                                    if (latitude == 0 && longitude == 0) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(BaseInstaladaActivity.this);
                                                        builder.setIcon(R.drawable.icon_info_title);
                                                        builder.setTitle("Confirmación");
                                                        builder.setCancelable(false);
                                                        builder.setMessage("No se han capturado las coordenadas geograficas. Desea continuar de todas maneras?");

                                                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                //if user pressed "yes", continue with execution
                                                                long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                                                if (inserto == -1) {
                                                                    Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                                                } else {
                                                                    //Intentar 1 vez el envio automatico de la lectura.
                                                                    WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                                                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                                                    if (VariablesGlobales.UsarAPI()) {
                                                                        /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                                                        if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                                            EnableWiFi();
                                                                        }
                                                                        f.execute();*/
                                                                    } else {
                                                                        EquipoFrio ef = db.getEquipoFrioDatosCenso(visionText.getText());
                                                                        TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                                                        if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                            f.EnableWiFi();
                                                                        } else {
                                                                            f.DisableWiFi();
                                                                        }
                                                                        f.execute();
                                                                    }
                                                                }
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
                                                    } else {
                                                        long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                                        if (inserto == -1) {
                                                            Toasty.info(getApplicationContext(), "No se pudo guardar la lectura de equipo frio ejecutada!").show();
                                                        } else {
                                                            //Intentar 1 vez el envio automatico de la lectura.
                                                            WeakReference<Context> weakRef = new WeakReference<Context>(BaseInstaladaActivity.this);
                                                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(BaseInstaladaActivity.this);

                                                            if (VariablesGlobales.UsarAPI()) {
                                                                        /*TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, filePath, wholePath,"");
                                                                        if(((OpcionSpinner) tipo_conexion.getSelectedItem()).getId().equals("wifi")){
                                                                            EnableWiFi();
                                                                        }
                                                                        f.execute();*/
                                                            } else {
                                                                EquipoFrio ef = db.getEquipoFrioDatosCenso(visionText.getText());
                                                                TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                                                if (PreferenceManager.getDefaultSharedPreferences(BaseInstaladaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
                                                                    f.EnableWiFi();
                                                                } else {
                                                                    f.DisableWiFi();
                                                                }
                                                                f.execute();
                                                            }
                                                        }
                                                    }
                                                }
                                        }
                                    })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toasty.error(getApplicationContext(), "Fallo al procesar la imagen capturada!").show();
                                                    e.printStackTrace();
                                                }
                                            });
                } catch (IOException e) {
                    Toasty.error(this, "Error al procesar la imagen capturada!").show();
                    e.printStackTrace();
                }
            }
        }
    }

    public static void EquipoFrioEncontradoenSAP(Context context, Activity activity, ArrayList<JsonArray> estructurasSAP) {
        if (estructurasSAP.size() == 0) {
            Toasty.error(context.getApplicationContext(), "No se pudo obtener la informacion del equipo frio!").show();
            activity.finish();
            return;
        }
        JsonArray equipoFrio;
        equipoFrio = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("EquipoFrio");
    }
    public static void EquipoFrioNOEncontradoenSAP(Context context, Activity activity, ArrayList<JsonArray> estructurasSAP) {
        if (estructurasSAP.size() == 0) {
            Toasty.error(context.getApplicationContext(), "No se pudo obtener la informacion del equipo frio!").show();
            activity.finish();
            return;
        }
        JsonArray equipoFrio;
        equipoFrio = estructurasSAP.get(0).getAsJsonArray().get(0).getAsJsonObject().getAsJsonArray("EquipoFrio");
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
}
