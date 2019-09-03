package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.TooltipCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
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

import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.BancoTableAdapter;
import proyecto.app.clientesabc.adaptadores.ContactoTableAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.ImpuestoTableAdapter;
import proyecto.app.clientesabc.adaptadores.InterlocutorTableAdapter;
import proyecto.app.clientesabc.adaptadores.VisitasTableAdapter;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Banco;
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

public class SolicitudActivity extends AppCompatActivity {

    final static int alturaFilaTableView = 65;
    static String tipoSolicitud ="";
    static String idSolicitud = "";
    @SuppressLint("StaticFieldLeak")
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    static ArrayList<String> listaCamposDinamicos = new ArrayList<>();
    static ArrayList<String> listaCamposObligatorios = new ArrayList<>();
    static ArrayList<String> listaCamposBloque = new ArrayList<>();
    static Map<String, View> mapeoCamposDinamicos = new HashMap<>();
    static  ArrayList<HashMap<String, String>> solicitudSeleccionada = new ArrayList<>();
    private static String GUID;
    private ProgressBar progressBar;

    Uri mPhotoUri;

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
    private static ArrayList<Contacto> contactosSolicitud;
    private static ArrayList<Impuesto> impuestosSolicitud;
    private static ArrayList<Banco> bancosSolicitud;
    private static ArrayList<Interlocutor> interlocutoresSolicitud;
    private static ArrayList<Visitas> visitasSolicitud;
    private static ArrayList<Adjuntos> adjuntosSolicitud;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            tipoSolicitud = b.getString("tipoSolicitud");
            idSolicitud = b.getString("idSolicitud");
            //accion = b.getString("accion");
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(10);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();

        if(idSolicitud != null){
            setTitle("Solicitud");
            solicitudSeleccionada = mDBHelper.getSolicitud(idSolicitud);
            tipoSolicitud = solicitudSeleccionada.get(0).get("TIPFORM");
            GUID = solicitudSeleccionada.get(0).get("id_solicitud");
            setTitle(GUID);
        }else{
            GUID = mDBHelper.getGuiId();
            solicitudSeleccionada.clear();
        }

        listaCamposDinamicos.clear();
        listaCamposBloque.clear();
        listaCamposObligatorios.clear();

        new MostrarFormulario(this).execute();

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
                            startActivityForResult(intent, 1);

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
                        //Al menos 1 dia de visita
                        if(((TextInputEditText)mapeoCamposDinamicos.get("ZPV_Lunes")).getText().toString().isEmpty())
                            if(((TextInputEditText)mapeoCamposDinamicos.get("ZPV_Martes")).getText().toString().isEmpty())
                                if(((TextInputEditText)mapeoCamposDinamicos.get("ZPV_Miercoles")).getText().toString().isEmpty())
                                    if(((TextInputEditText)mapeoCamposDinamicos.get("ZPV_Jueves")).getText().toString().isEmpty())
                                        if(((TextInputEditText)mapeoCamposDinamicos.get("ZPV_Viernes")).getText().toString().isEmpty())
                                            if(((TextInputEditText)mapeoCamposDinamicos.get("ZPV_Sabado")).getText().toString().isEmpty()) {
                                                numErrores++;
                                                mensajeError += "- El cliente debe tener al menos 1 día de visita!\n";
                                            }

                        if(numErrores == 0) {
                            String NextId = GUID;
                            ContentValues insertValues = new ContentValues();
                            for (int i = 0; i < listaCamposDinamicos.size(); i++) {
                                if(!listaCamposBloque.contains(listaCamposDinamicos.get(i).trim()) && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC")) {
                                    try {
                                        MaskedEditText tv = ((MaskedEditText) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                                        String valor = tv.getText().toString();
                                        /*if(valor.length() == 0)
                                            valor = valor+"1";*/
                                        if(!listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC"))
                                            insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor );
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
                                                    Toasty.error(getApplicationContext(), "Error Insertando Contacto de Solicitud", Toast.LENGTH_SHORT).show();
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
                                                    Toasty.error(getApplicationContext(), "Error Insertando Impuesto de Solicitud", Toast.LENGTH_SHORT).show();
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
                                                    Toasty.error(getApplicationContext(), "Error Insertando Interlocutor de Solicitud", Toast.LENGTH_SHORT).show();
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
                                                Toasty.error(getApplicationContext(), "Error Insertando Bancos de Solicitud", Toast.LENGTH_SHORT).show();
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
                                                    if (mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BZIRK", ""), visitasSolicitud.get(c).getVptyp())) {
                                                        //Tipo visita de Reparto
                                                        visitaValues.put("ruta", visitasSolicitud.get(c).getRuta());
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
                                                    } else {//Tipo Visita de Preventa
                                                        visitaValues.put("ruta", visitasSolicitud.get(c).getRuta());
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
                                                    }
                                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), null, visitaValues);
                                                    visitaValues.clear();
                                                }
                                            } catch (Exception e) {
                                                Toasty.error(getApplicationContext(), "Error Insertando Visitas de Solicitud", Toast.LENGTH_SHORT).show();
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
                                //TODO Se debe enviar el usuairo loqgueado al sistema (Nunca debe permitir un usuario default, este debe existir en KOF y el maestro de clientes WEB)
                                insertValues.put("[id_solicitud]", NextId);
                                insertValues.put("[tipform]", tipoSolicitud);
                                insertValues.put("[ususol]", PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("user",""));
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                                Date date = new Date();
                                //ContentValues initialValues = new ContentValues();
                                insertValues.put("[feccre]", dateFormat.format(date));

                                //mDBHelper.getWritableDatabase().insert("FormHvKof_solicitud", null, insertValues);
                                if(solicitudSeleccionada.size() > 0){
                                    insertValues.put("[estado]", "Modificado");
                                    long modifico = mDb.update("FormHvKof_solicitud",  insertValues,"id_solicitud = ?", new String[]{solicitudSeleccionada.get(0).get("id_solicitud")});
                                    Toasty.success(getApplicationContext(), "Registro modificado con éxito", Toast.LENGTH_SHORT).show();
                                }else {
                                    insertValues.put("[estado]", "Nuevo");
                                    long inserto = mDb.insertOrThrow("FormHvKof_solicitud", null, insertValues);
                                    //Una vez finalizado el proceso de guardado, se limpia la solicitud para una nueva.
                                    Intent sol = getIntent();
                                    sol.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    SolicitudActivity.this.finish();
                                    Bundle par = new Bundle();
                                    par.putString("tipo_solicitud",tipoSolicitud);
                                    SolicitudActivity.this.startActivity(sol);
                                    Toasty.success(getApplicationContext(), "Registro insertado con éxito", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Solicitud."+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            //Toasty.warning(getApplicationContext(), "Existen "+numErrores+" errores en los datos.", Toast.LENGTH_SHORT).show();
                            Toasty.warning(getApplicationContext(), "Revise los Siguientes campos: \n"+mensajeError, Toast.LENGTH_LONG).show();
                        }
                }
                return true;
            }
        });

        //View title = getWindow().findViewById(android.R.id.title);
        //View titleBar = (View) title.getParent();
        //titleBar.setBackground(gd);
        Drawable d=getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(d);
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
        tb_adjuntos.addDataClickListener(new AdjuntosClickListener());
        tb_adjuntos.addDataLongClickListener(new AdjuntosLongClickListener());
        contactosSolicitud = new ArrayList<>();
        impuestosSolicitud = new ArrayList<>();
        interlocutoresSolicitud = new ArrayList<>();
        bancosSolicitud = new ArrayList<>();
        visitasSolicitud = new ArrayList<>();
        //TODO esto se debe quitar y hacer solo cuando se realize la seleccion de modalidad de venta
        /*Visitas visitaSol = new Visitas();
        visitaSol.setVptyp("ZPV");
        visitaSol.setKvgr4("1DA");
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd",Locale.getDefault());
        String formattedDate = df.format(c);
        visitaSol.setF_ico(formattedDate);
        visitaSol.setF_fco("99991231");
        visitaSol.setFcalid("1");
        visitaSol.setRuta(VariablesGlobales.getRutaPreventa());
        visitasSolicitud.add(visitaSol);
        Visitas visitaSolR = new Visitas();
        visitaSolR.setVptyp("ZDD");
        visitaSolR.setKvgr4("1DA");
        visitaSolR.setF_ico(formattedDate);
        visitaSolR.setF_fco("99991231");
        visitaSolR.setFcalid("1");
        visitaSolR.setRuta("");
        visitasSolicitud.add(visitaSolR);*/
        adjuntosSolicitud = new ArrayList<>();
        //notificantesSolicitud = new ArrayList<Adjuntos>();
    }

    //Se dispara al escoger el documento que se quiere relacionar a la solicitud
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
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
                        //Agregar al tableView del UI
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, type, name, inputData);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(getBaseContext(), adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        Toasty.success(getBaseContext(),"Documento asociado correctamente.").show();
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
        }
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

            }
            if(nombre.equals("Facturación")|| nombre.equals("Facturacion")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"F");
            }
            if(nombre.equals("Ventas")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"V");
            }
            if(nombre.equals("Marketing")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"M");
            }
            if(nombre.equals("Adjuntos") || nombre.equals("Adicionales")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"Z");
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
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("adjuntos")) {
                    //Tipo ADJUNTOS
                    DesplegarBloque(mDBHelper,ll,campos.get(i));
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }
                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("grid")) {
                    //Tipo GRID o BLOQUE de Datos (Estos Datos requieren una tabla de la BD adicional a FORMHVKOF)
                    //Bloques Disponibles [Contactos, Impuestos, Funciones Interlocutor, visitas, bancos, notificantes]
                    DesplegarBloque(mDBHelper,ll,campos.get(i));
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }
                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("encuesta")) {
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
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        checkbox.setChecked(true);
                    }
                    if(solicitudSeleccionada.size() > 0){
                        checkbox.setChecked(true);
                    }
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
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        checkbox.setChecked(true);
                    }
                    if(solicitudSeleccionada.size() > 0){
                        checkbox.setChecked(true);
                    }
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

                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);
                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("checkbox")) {
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
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        checkbox.setChecked(true);
                    }
                    if(solicitudSeleccionada.size() > 0){
                        if(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim().length() > 0)
                            checkbox.setChecked(true);
                    }
                    ll.addView(checkbox);
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), checkbox);

                }else if (campos.get(i).get("tabla")!= null && campos.get(i).get("tabla").trim().length() > 0) {
                    //Tipo ComboBox/SelectBox/Spinner
                    TextView label = new TextView(getContext());
                    label.setText(campos.get(i).get("descr"));
                    label.setTextAppearance(R.style.AppTheme_TextFloatLabelAppearance);
                    LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lpl.setMargins(35, 5, 0, 0);
                    label.setPadding(0,0,0,0);
                    label.setLayoutParams(lpl);

                    Spinner combo = new Spinner(getContext(), Spinner.MODE_DROPDOWN);
                    combo.setTag(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        label.setVisibility(View.GONE);
                        combo.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        if(!campos.get(i).get("campo").trim().equals("W_CTE-LZONE"))
                            combo.setEnabled(false);
                    }

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, -10, 0, 25);
                    combo.setPadding(0,0,0,0);
                    combo.setLayoutParams(lp);
                    combo.setPopupBackgroundResource(R.drawable.menu_item);

                    ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim());

                    ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
                    int selectedIndex = 0;
                    String valorDefectoxRuta = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(campos.get(i).get("campo").trim().replace("-","_"),"");
                    for (int j = 0; j < opciones.size(); j++){
                        listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
                        if(solicitudSeleccionada.size() > 0){
                            //valor de la solicitud seleccionada
                            if(opciones.get(j).get("id").trim().equals(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()).trim())){
                                selectedIndex = j;
                            }
                        }else {
                            if (campos.get(i).get("dfaul").trim().length() > 0 && opciones.get(j).get("id").trim().equals(campos.get(i).get("dfaul").trim())) {
                                selectedIndex = j;
                            }
                        }
                        if(valorDefectoxRuta.trim().length() > 0 && opciones.get(j).get("id").trim().equals(valorDefectoxRuta.trim())){
                            selectedIndex = j;
                            combo.setEnabled(false);
                        }

                    }
                    // Creando el adaptador(opciones) para el comboBox deseado
                    ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.simple_spinner_item, listaopciones);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
                    combo.setBackground(d);
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
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-KVGR5")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                tb_visitas.getLayoutParams().height = 50;
                                final OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                                visitasSolicitud = mDBHelper.DeterminarPlanesdeVisita(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_VKORG",""), opcion.getId());
                                tb_visitas.setDataAdapter(new VisitasTableAdapter(view.getContext(), visitasSolicitud));
                                tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height+((alturaFilaTableView+10)*visitasSolicitud.size());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1").trim().contains("Provincia")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Provincias(parent);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1").trim().contains("Cantones")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Cantones(parent);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }

                    if(campos.get(i).get("llamado1").trim().contains("Distritos")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Distritos(parent);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1").trim().contains("DireccionCorta")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                DireccionCorta();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1").trim().contains("Canales(")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Canales(parent);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1").trim().contains("CanalesKof")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                CanalesKof(parent);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("llamado1").trim().contains("ImpuestoSegunUnidadNegocio")){
                        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ImpuestoSegunUnidadNegocio(parent);
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    //label.addView(combo);
                    ll.addView(label);
                    ll.addView(combo);

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
                                ReplicarValorSpinner(parent,nombreCampo+"1",position);
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
                                ReplicarValorSpinner(parent,nombreCampo,position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    if(campos.get(i).get("obl")!= null && campos.get(i).get("obl").trim().length() > 0){
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                    }
                } else {
                    //Tipo EditText normal textbox
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setWeightSum(10);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,10f));

                    TextInputLayout label = new TextInputLayout(Objects.requireNonNull(getContext()));
                    label.setHint(campos.get(i).get("descr"));
                    //label.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white,null)));

                    label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                    label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);

                    //final TextInputEditText et = new TextInputEditText(getContext());
                    final MaskedEditText et = new MaskedEditText(getContext(),null);

                    et.setTag(campos.get(i).get("descr"));
                    //et.setTextColor(getResources().getColor(R.color.colorTextView,null));
                    //et.setBackgroundColor(getResources().getColor(R.color.black,null));
                    //et.setHint(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        et.setVisibility(View.GONE);
                        label.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        et.setEnabled(false);
                        //et.setVisibility(View.GONE);
                    }
                    et.setMaxLines(1);

                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    // Atributos del Texto a crear
                    //TableLayout.LayoutParams lp =  new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT,0.5f);
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,5f);
                    lp.setMargins(0, 15, 0, 15);

                    et.setLayoutParams(lp);
                    et.setPadding(20, 5, 20, 5);
                    Drawable d = getResources().getDrawable(R.drawable.textbackground, null);
                    et.setBackground(d);

                    InputFilter[] editFilters = et.getFilters();
                    InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
                    System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
                    newFilters[editFilters.length] = new InputFilter.AllCaps();
                    et.setFilters(newFilters);
                    et.setAllCaps(true);

                    TableRow.LayoutParams textolp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 5f);
                    TableRow.LayoutParams btnlp = new TableRow.LayoutParams(75, 75);
                    if(campos.get(i).get("tooltip") != null){
                        textolp.setMargins(0,0,25,0);
                        label.setLayoutParams(textolp);
                        btnAyuda = new ImageView(getContext());
                        btnAyuda.setBackground(getResources().getDrawable(R.drawable.icon_ayuda,null));
                        btnlp.setMargins(0,55,75,0);
                        btnAyuda.setLayoutParams(btnlp);
                        btnAyuda.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                        btnAyuda.setForegroundGravity(GRAVITY_CENTER);
                        TooltipCompat.setTooltipText(btnAyuda, campos.get(i).get("tooltip"));

                    }
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        et.setText(campos.get(i).get("dfaul").trim());
                    }
                    //Le cae encima al valor default por el de la solicitud seleccionada
                    if(solicitudSeleccionada.size() > 0){
                        et.setText(solicitudSeleccionada.get(0).get(campos.get(i).get("campo").trim()));
                    }
                    //metodos configurados en tabla
                    if(campos.get(i).get("llamado1").trim().contains("ReplicarValor")){
                        String[] split = campos.get(i).get("llamado1").trim().split("'");
                        if(split.length < 3)
                            split = campos.get(i).get("llamado1").trim().split("`");
                        if(split.length < 3)
                            split = campos.get(i).get("llamado1").trim().split("\"");
                        final String campoAReplicar = split[1];
                        et.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    ReplicarValor(v,campoAReplicar);
                                }
                            }
                        });
                    }

                    label.addView(et);
                    fila.addView(label);
                    if(btnAyuda != null)
                        fila.addView(btnAyuda);
                    ll.addView(fila);


                    if(campos.get(i).get("campo").trim().equals("W_CTE-ZZCRMA_LAT") || campos.get(i).get("campo").trim().equals("W_CTE-ZZCRMA_LONG")){
                        et.setCompoundDrawablesWithIntrinsicBounds(null, null,getResources().getDrawable(R.drawable.icon_location,null), null);
                        et.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                final int DRAWABLE_LEFT = 0;
                                final int DRAWABLE_TOP = 1;
                                final int DRAWABLE_RIGHT = 2;
                                final int DRAWABLE_BOTTOM = 3;

                                if(event.getAction() == MotionEvent.ACTION_UP) {
                                    if(event.getRawX() >= (et.getRight() - et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                                        Toasty.info(getContext(),"Refrescando ubicacion..").show();
                                        LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                                        autoPineo.startLocationUpdates();
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-COMENTARIOS")){
                        et.setSingleLine(false);
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        et.setMinLines(5);
                        et.setMaxLines(6);
                        et.setVerticalScrollBarEnabled(true);
                        et.setMovementMethod(ScrollingMovementMethod.getInstance());
                        et.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        et.setGravity(INDICATOR_GRAVITY_TOP);
                    }
                    if(campos.get(i).get("campo").trim().equals("W_CTE-DATAB")){
                        Date c = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String fechaSistema = df.format(c);
                        et.setText(fechaSistema);
                    }

                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), et);
                    if(campos.get(i).get("obl")!= null && campos.get(i).get("obl").trim().length() > 0){
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                    }
                    if(campos.get(i).get("tabla_local").trim().length() > 0){
                        listaCamposBloque.add(campos.get(i).get("campo").trim());
                    }

                }

                seccionAnterior = campos.get(i).get("id_seccion").trim();

            }
            //Si estan los campos de Latitud y Longitud, activar el pineo automatico (W_CTE-ZZCRMA_LAT,W_CTE-ZZCRMA_LONG)
            if(listaCamposDinamicos.contains("W_CTE-ZZCRMA_LAT") && listaCamposDinamicos.contains("W_CTE-ZZCRMA_LONG") && solicitudSeleccionada.size() == 0){
                LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"), (MaskedEditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                autoPineo.startLocationUpdates();
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
            seccion_header.setTextSize(10);
            seccion_header.setTextAlignment(TEXT_ALIGNMENT_CENTER);

            Button btnAddBloque = new Button(getContext());
            RelativeLayout.LayoutParams tam_btn = new RelativeLayout.LayoutParams(60,60);

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
            CoordinatorLayout.LayoutParams rlp = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            rl.setLayoutParams(rlp);
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) rl.getLayoutParams();
            params.setBehavior(new AppBarLayout.ScrollingViewBehavior(getContext(), null));

            switch(campo.get("campo").trim()){
                case "W_CTE-CONTACTOS":
                    //bloque_contacto = tb_contactos;
                    seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogContacto(getContext(),null);
                        }
                    });
                    btnAddBloque.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogContacto(getContext(),null);
                        }
                    });

                    tb_contactos.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

                    tb_contactos.setLayoutParams(hlp);

                    if(solicitudSeleccionada.size() > 0){
                        contactosSolicitud = mDBHelper.getContactosDB(idSolicitud);
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

                    ArrayList<Impuesto> listaImpuestos = db.getImpuestosPais();
                    impuestosSolicitud.addAll(listaImpuestos);
                    if(solicitudSeleccionada.size() > 0){
                        impuestosSolicitud.clear();
                        impuestosSolicitud = mDBHelper.getImpuestosDB(idSolicitud);
                    }
                    //Adaptadores
                    if(impuestosSolicitud != null) {
                        tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height+(listaImpuestos.size()*alturaFilaTableView);
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
                    });
                    seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogInterlocutor(getContext(),null);
                        }
                    });*/
                    bloque_interlocutor.setColumnCount(3);
                    bloque_interlocutor.setHeaderBackgroundColor(getResources().getColor(R.color.colorHeaderTableView,null));
                    bloque_interlocutor.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_interlocutor.setLayoutParams(hlp);


                    ArrayList<Interlocutor> listaInterlocutores = db.getInterlocutoresPais();
                    interlocutoresSolicitud.addAll(listaInterlocutores);
                    if(solicitudSeleccionada.size() > 0){
                        interlocutoresSolicitud.clear();
                        interlocutoresSolicitud = mDBHelper.getInterlocutoresDB(idSolicitud);
                    }
                    //Adaptadores
                    if(interlocutoresSolicitud != null) {
                        if(tipoSolicitud.equals("1") || tipoSolicitud.equals("6")) {
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height + (interlocutoresSolicitud.size() * alturaFilaTableView);
                            tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(getContext(), interlocutoresSolicitud));
                        }
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
                    btnAddBloque.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogBancos(getContext(),null);
                        }
                    });
                    seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            displayDialogBancos(getContext(),null);
                        }
                    });
                    bloque_banco.setColumnCount(5);
                    bloque_banco.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_banco.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_banco.setLayoutParams(hlp);

                    if(solicitudSeleccionada.size() > 0){
                        bancosSolicitud = mDBHelper.getBancosDB(idSolicitud);
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

                    if(solicitudSeleccionada.size() > 0){
                        visitasSolicitud = mDBHelper.getVisitasDB(idSolicitud);
                    }
                    //Adaptadores
                    if(visitasSolicitud != null) {
                        VisitasTableAdapter stda = new VisitasTableAdapter(getContext(), visitasSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
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
                    final String[] diaLabel = {"Lunes","Martes","Miercoles","Jueves","Viernes","Sabado","Domingo"};
                    int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPV");
                    int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDD");
                    for(int x = 0; x < 6; x++){
                        TextInputLayout label = new TextInputLayout(getContext());
                        label.setHint("Secuencia dia "+diaLabel[x]);
                        label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                        label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);

                        final TextInputEditText et = new TextInputEditText(getContext());
                        mapeoCamposDinamicos.put("ZPV_"+diaLabel[x],et);
                        et.setMaxLines(1);
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,5f);
                        lp.setMargins(0, 15, 0, 15);
                        et.setLayoutParams(lp);
                        if(solicitudSeleccionada.size() > 0){
                            switch(x) {
                                case 0:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getLun_de()));
                                    break;
                                case 1:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getMar_de()));
                                    break;
                                case 2:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getMier_de()));
                                    break;
                                case 3:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getJue_de()));
                                    break;
                                case 4:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getVie_de()));
                                    break;
                                case 5:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getSab_de()));
                                    break;
                                case 6:
                                    et.setText(VariablesGlobales.HoraToSecuencia(visitasSolicitud.get(indicePreventa).getDom_de()));
                                    break;
                            }
                        }
                        //et.setPadding(20, 5, 20, 5);
                        Drawable d = getResources().getDrawable(R.drawable.textbackground, null);
                        et.setBackground(d);
                        final int finalX = x;
                        et.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                /*int indicePreventa = 0;
                                int indiceReparto = 0;
                                for(int i = 0 ; i < visitasSolicitud.size(); i++){
                                    if(visitasSolicitud.get(i).getVptyp().equals("ZPV")){
                                        indicePreventa = i;
                                    }
                                    if(visitasSolicitud.get(i).getVptyp().equals("ZDD")){
                                        indiceReparto = i;
                                    }
                                }*/
                                int indicePreventa = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZPV");
                                int indiceReparto = VariablesGlobales.getIndiceTipoVisita(visitasSolicitud,"ZDD");

                                final int finalIndicePreventa = indicePreventa;
                                final int finalIndiceReparto = indiceReparto;
                                if (!hasFocus) {
                                    int diaReparto = 0;
                                    if((finalX+1) > 5){
                                        diaReparto = ((finalX+1)-6);
                                    }else{
                                        diaReparto = (finalX+1);
                                    }
                                    Visitas visitaPreventa = visitasSolicitud.get(finalIndicePreventa);
                                    Visitas visitaReparto = visitasSolicitud.get(finalIndiceReparto);
                                    if(!((TextView)v).getText().toString().equals("") && Integer.valueOf(((TextView)v).getText().toString()) > 1440){
                                        switch(finalX) {
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
                                        switch(diaReparto) {
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
                                        ((TextView)v).setText(getResources().getString(R.string.max_secuencia));
                                        Toasty.warning(getContext(), R.string.error_max_secuencia).show();
                                    }

                                    //Si el valor es vacio, borrar si existe el dia
                                    if(((TextView)v).getText().toString().trim().equals("")){
                                        switch(finalX) {
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
                                        switch(diaReparto) {
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
                                    }else{
                                        String secuenciaSAP = VariablesGlobales.SecuenciaToHora(((TextView)v).getText().toString());
                                        /*int hours = Integer.valueOf(((TextView)v).getText().toString()) / 60;
                                        int minutes = Integer.valueOf(((TextView)v).getText().toString()) % 60;
                                        String h = String.format(Locale.getDefault(),"%02d", hours);
                                        String m = String.format(Locale.getDefault(),"%02d", minutes);
                                        String secuenciaSAP = h+m;*/
                                        switch(finalX) {
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
                                        switch(diaReparto) {
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
                            }
                        });
                        label.addView(et);
                        ll.addView(label);
                    }
                break;
                case "W_CTE-ADJUNTOS":
                    tb_adjuntos.setColumnCount(3);
                    tb_adjuntos.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);

                    tb_adjuntos.setLayoutParams(hlp);

                    //Adaptadores
                    if(adjuntosSolicitud != null) {
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(getContext(), adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*alturaFilaTableView);
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
                    ll.addView(rl);
                    break;
                case "W_CTE-NOTIFICANTES":
                    break;
            }
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

    //Pruebas para seccion de bloques
    public static void displayDialogContacto(Context context, final Contacto seleccionado) {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.contacto_dialog_layout);

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
                impuestosSolicitud.add(nuevoImpuesto);
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

        //Para campos de seleccion para grid impuestos campo clave de impuesto
        ArrayList<HashMap<String, String>> opciones = mDBHelper.getDatosCatalogo("cat_impstos",1,2, "taxkd=1");

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
                ArrayList<HashMap<String, String>> opcionesClasi = mDBHelper.getDatosCatalogo("cat_impstos",3,4,"tatyp='"+opcion.getId()+"'");

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
        ArrayList<HashMap<String, String>> opcionesClasi = mDBHelper.getDatosCatalogo("cat_impstos",3,4);

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
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
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
                bancosSolicitud.add(nuevoBanco);
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

                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                    //TODO Asignar los valores de los canales segun las respuestas obtenidas

                    Toasty.success(v.getContext(), "Encuesta Canales ejecutada correctamente!", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA");
                    ejecutada.setChecked(true);
                } catch (Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10,10,10,10);
        final ArrayList<HashMap<String, String>> preguntas = mDBHelper.getPreguntasGec();
        final ArrayList<HashMap<String, String>> respuestas = mDBHelper.getEncuestaGec(GUID);

        for (int j = 0; j < preguntas.size(); j++){
            TextInputEditText monto = new TextInputEditText(d.getContext());
            ArrayList<OpcionSpinner> misOpciones = new ArrayList<>();

            TextView label_pregunta = new TextView(d.getContext());
            label_pregunta.setText(String.format(d.getContext().getResources().getString(R.string.label_pregunta), preguntas.get(j).get("zid_quest"), preguntas.get(j).get("text")));
            //label_pregunta.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
            layout.addView(label_pregunta,params1);

            //TODO revisar si ya se ha realizado una encuesta para la solicitud, para poder mostrar las respuestas existentes.
            if(respuestas.size() > 0){
                monto.setText(respuestas.get(j).get("monto"));
                //monto.setText("2");
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
                        }

                        try {
                            mDb.insert(VariablesGlobales.getTablaEncuestaGecSolicitud(), null, encuestaValues);
                        } catch (Exception e) {
                            Toasty.error(v.getContext(), "Error Insertando Encuesta Canales (Registro #"+j+")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    String valor_gec = mDBHelper.getGecSegunEncuestaRealizada(suma_montos);
                    Spinner gecSpinner = (Spinner)mapeoCamposDinamicos.get("W_CTE-KLABC");
                    gecSpinner.setSelection(VariablesGlobales.getIndex(gecSpinner,valor_gec));

                    //TODO Asignar los valores de los canales segun las respuestas obtenidas
                    Toasty.success(v.getContext(), "Encuesta GEC ejecutada correctamente!", Toast.LENGTH_SHORT).show();
                    d.dismiss();
                    CheckBox ejecutada = (CheckBox)mapeoCamposDinamicos.get("W_CTE-ENCUESTA_GEC");
                    ejecutada.setChecked(true);
                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo salvar la encuesta gec. "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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

    public void mostrarAdjunto(Context context, Bitmap imagen) {
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.adjunto_layout);
        ImageView adjunto = d.findViewById(R.id.imagen);
        adjunto.setImageBitmap(imagen);
        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if(window != null) {
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
            String salida = seleccionado.getName1() + " " + seleccionado.getNamev()+" ha sido eliminado.";
            contactosSolicitud.remove(rowIndex);
            tb_contactos.setDataAdapter(new ContactoTableAdapter(getBaseContext(), contactosSolicitud));
            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
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
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
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
            String salida = seleccionado.getBankn() + " " + seleccionado.getBanks();
            bancosSolicitud.remove(rowIndex);
            tb_bancos.setDataAdapter(new BancoTableAdapter(getBaseContext(), bancosSolicitud));
            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
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
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
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
        final Dialog d = new Dialog(context);
        d.setContentView(R.layout.visita_dialog_layout);
        boolean reparto = mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_BZIRK",""), seleccionado.getVptyp());
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final Spinner kvgr4Spinner = d.findViewById(R.id.kvgr4Spinner);
        final TextInputEditText f_icoEditText = d.findViewById(R.id.f_icoEditTxt);
        final TextInputEditText f_fcoEditText = d.findViewById(R.id.f_fcoEditTxt);
        final TextInputEditText f_iniEditText = d.findViewById(R.id.f_iniEditTxt);
        final TextInputEditText f_finEditText = d.findViewById(R.id.f_finEditTxt);
        final Spinner fcalidSpinner = d.findViewById(R.id.fcalidSpinner);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        title.setText(String.format(context.getString(R.string.palabras_2),context.getString(R.string.label_vp),seleccionado.getVptyp()));

        kvgr4Spinner.setSelection(((ArrayAdapter<CharSequence>)kvgr4Spinner.getAdapter()).getPosition(seleccionado.getKvgr4()));
        f_icoEditText.setText(seleccionado.getF_ico());
        f_fcoEditText.setText(seleccionado.getF_fco());
        f_iniEditText.setText(seleccionado.getF_ini());
        f_finEditText.setText(seleccionado.getF_fin());

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
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionado.setKvgr4(kvgr4Spinner.getSelectedItem().toString().trim());
                seleccionado.setRuta(PreferenceManager.getDefaultSharedPreferences(SolicitudActivity.this).getString("W_CTE_RUTAHH",""));
                //TODO: setear ruta segun su tipo de visita
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
                RecalcularDiasDeReparto();

                tb_visitas.setDataAdapter(new VisitasTableAdapter(v.getContext(), visitasSolicitud));
                tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height+alturaFilaTableView;
                try {
                    d.dismiss();
                }catch(Exception e){
                    Toasty.error(v.getContext(), "No se pudo salvar la configuracion").show();
                }
            }
        });
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
            if(!esReparto){
                String rutaReparto = mDBHelper.RutaRepartoAsociada("PR", vp.getVptyp());
                for (int x = 0; x < visitasSolicitud.size(); x++) {
                    if (rutaReparto.equals(visitasSolicitud.get(x).getVptyp())) {
                        rep = visitasSolicitud.get(x);
                        break;
                    }
                }
                int diasParaReparto = Integer.valueOf(vp.getKvgr4().replace("DA",""));
                TextInputEditText vp_Lunes = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_Lunes"));
                TextInputEditText vp_Martes = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_Martes"));
                TextInputEditText vp_Miercoles = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_Miercoles"));
                TextInputEditText vp_Jueves = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_Jueves"));
                TextInputEditText vp_Viernes = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_Viernes"));
                TextInputEditText vp_Sabado = ((TextInputEditText) mapeoCamposDinamicos.get(vp.getVptyp()+"_Sabado"));

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

            }

        }
    }

    public static void asignarDiaReparto(int metodo, int diaPreventa, String secuencia, Visitas vp_reparto){
        if(secuencia != null){
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
            tb_visitas.setDataAdapter(new VisitasTableAdapter(getBaseContext(), visitasSolicitud));
            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height-alturaFilaTableView;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private class AdjuntosClickListener implements TableDataClickListener<Adjuntos> {
        @Override
        public void onDataClicked(int rowIndex, Adjuntos seleccionado) {
            //displayDialogVisitas(SolicitudActivity.this,seleccionado);
            byte[] image = seleccionado.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            mostrarAdjunto(SolicitudActivity.this, bitmap);
        }
    }
    private class AdjuntosLongClickListener implements TableDataLongClickListener<Adjuntos> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Adjuntos seleccionado) {
            String salida = seleccionado.getType() + " " + seleccionado.getName();
            adjuntosSolicitud.remove(rowIndex);
            tb_adjuntos.setDataAdapter(new AdjuntoTableAdapter(getBaseContext(), adjuntosSolicitud));
            tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height-(alturaFilaTableView-20);
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
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
        DireccionCorta();
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
        combo.setSelection(selectedIndex);
        DireccionCorta();
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
        combo.setSelection(selectedIndex);
        DireccionCorta();
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
        if(!d.getId().isEmpty())
            home.setText(d.getName().trim().split("-")[1]);

        StringBuilder dircorta = new StringBuilder();
        if (dir != null) {
            if (prov != null && !((OpcionSpinner)prov.getSelectedItem()).getId().equals("")) {
                if(!p.getId().isEmpty())
                    dircorta.append(p.getName().trim().split("-")[1]);
            }
            if (cant != null && !((OpcionSpinner)cant.getSelectedItem()).getId().equals("")) {
                if(!c.getId().isEmpty())
                    dircorta.append(c.getName().trim().split("-")[1]);
            }
            if (dist != null && !((OpcionSpinner)dist.getSelectedItem()).getId().equals("")) {
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
                    tb_impuestos.getDataAdapter().getData().get(indice).setTatyp("2");
                } else {
                    tb_impuestos.getDataAdapter().getData().get(indice).setTatyp("1");
                }
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
            return false;
        }
        Toasty.success(texto.getContext(),"Formato Regimen "+tipoCedula+" valido!").show();
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
            //LinearLayout ll = findViewById(R.id.LinearLayoutMain);
            publishProgress(10);
            return null;
            //return ll;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //LinearLayout ll = findViewById(R.id.LinearLayoutMain);
            //ll.addView(misTabs);
            //ll.addView(viewPager);
            progressBar.setVisibility(View.GONE);
        }

    }

}
