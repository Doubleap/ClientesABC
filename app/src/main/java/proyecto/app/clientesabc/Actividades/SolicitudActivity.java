package proyecto.app.clientesabc.Actividades;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.TooltipCompat;
import android.text.InputFilter;
import android.text.InputType;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.Adaptadores.BancoTableAdapter;
import proyecto.app.clientesabc.Adaptadores.ContactoTableAdapter;
import proyecto.app.clientesabc.Adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.Adaptadores.ImpuestoTableAdapter;
import proyecto.app.clientesabc.Adaptadores.InterlocutorTableAdapter;
import proyecto.app.clientesabc.Adaptadores.VisitasTableAdapter;
import proyecto.app.clientesabc.Modelos.Banco;
import proyecto.app.clientesabc.Modelos.Impuesto;
import proyecto.app.clientesabc.Modelos.Interlocutor;
import proyecto.app.clientesabc.Modelos.OpcionSpinner;
import proyecto.app.clientesabc.Modelos.Visitas;
import proyecto.app.clientesabc.Modelos.Contacto;

import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;

import static android.support.design.widget.TabLayout.*;

public class SolicitudActivity extends AppCompatActivity {

    static String tipoSolicitud ="";
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    static ArrayList<String> listaCamposDinamicos = new ArrayList<>();
    static ArrayList<String> listaCamposObligatorios = new ArrayList<>();
    static ArrayList<String> listaCamposBloque = new ArrayList<>();
    static Map<String, View> mapeoCamposDinamicos = new HashMap<String, View>();

    Uri mPhotoUri;

    static de.codecrafters.tableview.TableView<Contacto> tb_contactos;
    static de.codecrafters.tableview.TableView<Impuesto> tb_impuestos;
    static de.codecrafters.tableview.TableView<Interlocutor> tb_interlocutores;
    static de.codecrafters.tableview.TableView<Banco> tb_bancos;
    static de.codecrafters.tableview.TableView<Visitas> tb_visitas;
    private static ArrayList<Contacto> contactosSolicitud;
    private static ArrayList<Impuesto> impuestosSolicitud;
    private static ArrayList<Banco> bancosSolicitud;
    private static ArrayList<Interlocutor> interlocutoresSolicitud;
    private static ArrayList<Visitas> visitasSolicitud;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud);
        Bundle b = getIntent().getExtras();
        if(b != null)
            tipoSolicitud = b.getString("tipoSolicitud");
        //tipoSolicitud = "2";

        BottomNavigationView bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();
        listaCamposDinamicos.clear();
        listaCamposBloque.clear();
        listaCamposObligatorios.clear();
        //Traer primero las pestanas
        TabLayout misTabs = new TabLayout(this);
        misTabs.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        misTabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        final ViewPager viewPager = new ViewPager(this);
        viewPager.setId(1);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(5);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(misTabs));

        misTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        LinearLayout ll = findViewById(R.id.LinearLayoutMain);
        ll.addView(misTabs);
        ll.addView(viewPager);

        //Setear Eventos de Elementos
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
                            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                        }
                        return true;
                    case R.id.action_file:
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(intent, 1);

                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                        }
                        return true;
                    case R.id.action_save:
                        int numErrores = 0;
                        //Validacion de Datos Obligatorios Automatico
                        for(int i=0; i < listaCamposObligatorios.size(); i++) {
                            try{
                                EditText tv = ((EditText) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                String valor = tv.getText().toString().trim();
                                if(valor.isEmpty()){
                                    tv.setError("El campo "+tv.getHint()+" es obligatorio!");
                                    numErrores++;
                                }
                            }catch(Exception e){
                                Spinner combo = ((Spinner) mapeoCamposDinamicos.get(listaCamposObligatorios.get(i)));
                                if(combo.getSelectedItem() != null) {
                                    String valor = ((OpcionSpinner)combo.getAdapter().getItem((int) combo.getSelectedItemId())).getId();

                                    if (combo.getAdapter().getCount() == 0 || (combo.getAdapter().getCount() > 0 && valor.isEmpty() )) {
                                        ((TextView) combo.getChildAt(0)).setError("El campo es obligatorio!");
                                        //combo.setError("El campo "+combo.getHint()+" es obligatorio!");
                                        numErrores++;
                                    }
                                }else{
                                    TextView error = (TextView)combo.getSelectedView();
                                    error.setError("El campo es obligatorio!");
                                    numErrores++;
                                }
                            }
                        }
                        if(numErrores >= 0) {
                            int NextId = mDBHelper.getNextSolicitudId();
                            if(NextId == 0)
                                NextId = 1;
                            ContentValues insertValues = new ContentValues();
                            for (int i = 0; i < listaCamposDinamicos.size(); i++) {
                                if(!listaCamposBloque.contains(listaCamposDinamicos.get(i))) {
                                    try {
                                        EditText tv = ((EditText) mapeoCamposDinamicos.get(listaCamposDinamicos.get(i)));
                                        String valor = tv.getText().toString();
                                        if(valor.length() == 0)
                                            valor = valor+"1";
                                        if(!listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA") && !listaCamposDinamicos.get(i).equals("W_CTE-ENCUESTA_GEC"))
                                            insertValues.put("[" + listaCamposDinamicos.get(i) + "]", valor );
                                    } catch (Exception e) {
                                        insertValues.put("[" + listaCamposDinamicos.get(i) + "]", "2" );
                                    }
                                }else{//Revisar que tipo de bloque es para guardarlo en el lugar correcto.
                                    switch(listaCamposDinamicos.get(i)){
                                        case "W_CTE-CONTACTOS":
                                            ContentValues contactoValues = new ContentValues();
                                            for (int c = 0; c < contactosSolicitud.size(); c++) {
                                                contactoValues.put("id_solicitud", NextId);
                                                contactoValues.put("name1", contactosSolicitud.get(c).getName1());
                                                contactoValues.put("namev", contactosSolicitud.get(c).getNamev());
                                                contactoValues.put("telf1", contactosSolicitud.get(c).getTelf1());
                                                contactoValues.put("house_num1", contactosSolicitud.get(c).getHouse_num1());
                                                contactoValues.put("street", contactosSolicitud.get(c).getStreet());
                                                contactoValues.put("gbdat", contactosSolicitud.get(c).getGbdat());
                                                contactoValues.put("country", contactosSolicitud.get(c).getCountry());
                                                try {
                                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), null, contactoValues);
                                                } catch (Exception e) {
                                                    Toasty.error(getApplicationContext(), "Error Insertando Contacto de Solicitud", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            break;
                                        case "W_CTE-IMPUESTOS":
                                            ContentValues impuestoValues = new ContentValues();
                                            for (int c = 0; c < impuestosSolicitud.size(); c++) {
                                                impuestoValues.put("id_solicitud", NextId);
                                                impuestoValues.put("vtext", impuestosSolicitud.get(c).getVtext());
                                                impuestoValues.put("vtext2", impuestosSolicitud.get(c).getVtext2());
                                                impuestoValues.put("tatyp", impuestosSolicitud.get(c).getTatyp());
                                                impuestoValues.put("taxkd", impuestosSolicitud.get(c).getTaxkd());
                                                try {
                                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_HH(), null, impuestoValues);
                                                } catch (Exception e) {
                                                    Toasty.error(getApplicationContext(), "Error Insertando Impuesto de Solicitud", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            break;
                                        case "W_CTE-INTERLOCUTORES":
                                            ContentValues interlocutorValues = new ContentValues();
                                            for (int c = 0; c < interlocutoresSolicitud.size(); c++) {
                                                interlocutorValues.put("id_solicitud", NextId);
                                                interlocutorValues.put("name1", interlocutoresSolicitud.get(c).getName1());

                                                try {
                                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_HH(), null, interlocutorValues);
                                                } catch (Exception e) {
                                                    Toasty.error(getApplicationContext(), "Error Insertando Interlocutor de Solicitud", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            break;
                                        case "W_CTE-BANCOS":
                                            ContentValues bancoValues = new ContentValues();
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
                                                try {
                                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_BANCO_HH(), null, bancoValues);
                                                } catch (Exception e) {
                                                    Toasty.error(getApplicationContext(), "Error Insertando Bancos de Solicitud", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            break;
                                        case "W_CTE-VISITAS":
                                            ContentValues visitaValues = new ContentValues();
                                            for (int c = 0; c < visitasSolicitud.size(); c++) {
                                                visitaValues.put("id_solicitud", NextId);
                                                visitaValues.put("ruta", visitasSolicitud.get(c).getRuta());
                                                visitaValues.put("vptyp", visitasSolicitud.get(c).getVptyp());
                                                visitaValues.put("f_frec", visitasSolicitud.get(c).getF_frec());
                                                visitaValues.put("lun_de", visitasSolicitud.get(c).getLun_de());
                                                visitaValues.put("mar_de", visitasSolicitud.get(c).getMar_de());
                                                visitaValues.put("mier_de", visitasSolicitud.get(c).getMier_de());
                                                visitaValues.put("jue_de", visitasSolicitud.get(c).getJue_de());
                                                visitaValues.put("vie_d", visitasSolicitud.get(c).getVie_de());
                                                visitaValues.put("sab_de", visitasSolicitud.get(c).getSab_de());
                                                try {
                                                    mDb.insert(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), null, visitaValues);
                                                } catch (Exception e) {
                                                    Toasty.error(getApplicationContext(), "Error Insertando Visitas de Solicitud", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            break;
                                    }
                                }
                            }
                            try {
                                //Datos que siemrpe deben ir cuando se crea por primera vez.
                                insertValues.put("[estado]", "Nuevo");
                                insertValues.put("[idform]", NextId);
                                insertValues.put("[tipform]", tipoSolicitud);
                                insertValues.put("[ususol]", "TCRORBAAYMER");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                //ContentValues initialValues = new ContentValues();
                                insertValues.put("[feccre]", dateFormat.format(date));

                                //mDBHelper.getWritableDatabase().insert("FormHvKof_solicitud", null, insertValues);
                                long inserto = mDb.insert("FormHvKof_solicitud",null,insertValues);

                            } catch (Exception e) {
                                Toasty.error(getApplicationContext(), "Error Insertando Solicitud", Toast.LENGTH_SHORT).show();
                            }

                            Toasty.success(getApplicationContext(), "Registro insertado con éxito", Toast.LENGTH_SHORT).show();
                        }else{
                            Toasty.warning(getApplicationContext(), "Existen "+numErrores+" errores en los datos, por favor revise todos los campos.", Toast.LENGTH_SHORT).show();
                        }
                }
                return true;
            }
        });

        //View title = getWindow().findViewById(android.R.id.title);
        //View titleBar = (View) title.getParent();
        //titleBar.setBackground(gd);
        Drawable d=getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        getSupportActionBar().setBackgroundDrawable(d);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            //return;
        }

        //Manejo de Bloques
        tb_contactos = new de.codecrafters.tableview.TableView<Contacto>(this);
        tb_contactos.addDataClickListener(new ContactoClickListener());
        tb_contactos.addDataLongClickListener(new ContactoLongClickListener());
        tb_impuestos = new de.codecrafters.tableview.TableView<Impuesto>(this);
        tb_impuestos.addDataClickListener(new ImpuestoClickListener());
        tb_impuestos.addDataLongClickListener(new ImpuestoLongClickListener());
        tb_bancos = new de.codecrafters.tableview.TableView<Banco>(this);
        tb_bancos.addDataClickListener(new BancoClickListener());
        tb_bancos.addDataLongClickListener(new BancoLongClickListener());
        tb_interlocutores = new de.codecrafters.tableview.TableView<Interlocutor>(this);
        tb_interlocutores.addDataClickListener(new InterlocutorClickListener());
        tb_interlocutores.addDataLongClickListener(new InterlocutorLongClickListener());
        tb_visitas = new de.codecrafters.tableview.TableView<Visitas>(this);
        tb_visitas.addDataClickListener(new VisitasClickListener());
        tb_visitas.addDataLongClickListener(new VisitasLongClickListener());
        contactosSolicitud = new ArrayList<Contacto>();
        impuestosSolicitud = new ArrayList<Impuesto>();
        interlocutoresSolicitud = new ArrayList<Interlocutor>();
        bancosSolicitud = new ArrayList<Banco>();
        visitasSolicitud = new ArrayList<Visitas>();
    }

    //Menu de Opciones de Adjuntos para movil
    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        // OnClick del menu de adjuntos x opcion
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.item1:
                        intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(intent, 1);

                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                        }
                        return true;
                    case R.id.item2:
                        intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_APP_GALLERY);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        //intent.setType("image/*");
                        try {
                            startActivityForResult(intent, 1);

                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                        }
                        return true;
                    case R.id.item3:
                        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                new ContentValues());
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                        try {
                            startActivityForResult(intent, 1);

                        } catch (ActivityNotFoundException e) {
                            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                        }
                        return true;
                    default:
                        return false;
                }

            }
        });
        popup.inflate(R.menu.opciones_adjuntos);
        popup.show();


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
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String type = cR.getType(uri);
                        String name = getFileName(cR, uri);
                        byte[] inputData = getBytes(iStream);
                        mDBHelper.addAdjuntoSolicitud(type, name, inputData);
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
                        Toasty.error(getBaseContext(),"Error al documento el adjunto a la solicitud").show();
                        e.printStackTrace();
                    }

                }
                break;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> title = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            //DataBaseHelper db = new DataBaseHelper(getBaseContext());
            List<String> pestanas = mDBHelper.getPestanasFormulario(tipoSolicitud);
            for (int i=0; i<pestanas.size(); i++) {
                title.add(pestanas.get(i));
            }
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
            position = getArguments().getInt("pos");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.pagina_formulario, container, false);
            LinearLayout ll = view.findViewById(R.id.miPagina);
            String nombre = ((ViewPager) container).getAdapter().getPageTitle(position).toString().trim();

            if(nombre.equals("Datos Generales")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"D");
            }
            if(nombre.equals("Facturación")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"F");
            }
            if(nombre.equals("Ventas")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"V");
            }
            if(nombre.equals("Marketing")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"M");
            }
            if(nombre.equals("Adicionales")) {
                LlenarPestana(mDBHelper, ll, tipoSolicitud,"Z");

            }
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
        //LLenado Automatico de campos x pestana.
        public void LlenarPestana(DataBaseHelper db, View _ll, String tipoFormulario, String pestana) {
            //View view = inflater.inflate(R.layout.pagina_formulario, container, false);
            String seccionAnterior = "";
            LinearLayout ll = (LinearLayout)_ll;
            //DataBaseHelper db = new DataBaseHelper(getContext());
            ArrayList<HashMap<String, String>> campos = db.getCamposPestana(tipoFormulario, pestana);

            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            for (int i = 0; i < campos.size(); i++) {
                ImageView btnAyuda = null;
                //Creacion de seccion
                if(!seccionAnterior.equals(campos.get(i).get("id_seccion").trim()) && !campos.get(i).get("id_seccion").trim().equals("99")) {
                    CardView seccion_layout = new CardView(getContext());

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
                    CheckBox checkbox = new CheckBox(getContext());
                    checkbox.setText(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        checkbox.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        checkbox.setEnabled(false);
                    }
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        checkbox.setChecked(true);
                    }
                    ll.addView(checkbox);

                }else
                if (campos.get(i).get("tipo_input")!= null && campos.get(i).get("tipo_input").trim().toLowerCase().equals("grid")) {
                    //Tipo GRID o BLOQUE de Datos (Estos Datos requieren una tabla de la BD adicional a FORMHVKOF)
                    //Bloques Disponibles [Contactos, Impuestos, Funciones Interlocutor, visitas, bancos, notificantes]
                    DesplegarBloque(mDBHelper,ll,campos.get(i));
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
                    }
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        checkbox.setChecked(true);
                    }
                    ll.addView(checkbox);

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

                    if(campos.get(i).get("sup").trim().length() > 0){
                        label.setVisibility(View.GONE);
                        combo.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        //combo.setEnabled(false);
                    }

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, -10, 0, 25);
                    combo.setPadding(0,0,0,0);
                    combo.setLayoutParams(lp);
                    combo.setPopupBackgroundResource(R.drawable.menu_item);

                    ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("cat_"+campos.get(i).get("tabla").trim());

                    ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
                    int selectedIndex = 0;
                    for (int j = 0; j < opciones.size(); j++){
                        listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
                        if(campos.get(i).get("dfaul").trim().length() > 0 && opciones.get(j).get("id").trim().equals(campos.get(i).get("dfaul").trim())){
                            selectedIndex = j;
                        }
                        String valorDefectoxRuta = VariablesGlobales.getCampo(campos.get(i).get("campo").trim());
                        if(valorDefectoxRuta.trim().length() > 0 && opciones.get(j).get("id").trim().equals(valorDefectoxRuta.trim())){
                            selectedIndex = j;
                            combo.setEnabled(false);
                        }
                    }
                    // Creando el adaptador(opciones) para el comboBox deseado
                    ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<OpcionSpinner>(getContext(), R.layout.simple_spinner_item, listaopciones);
                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                    // attaching data adapter to spinner
                    Drawable d = getResources().getDrawable(R.drawable.spinner_background, null);
                    combo.setBackground(d);
                    combo.setAdapter(dataAdapter);
                    combo.setSelection(selectedIndex);

                    /*combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            OpcionSpinner opcion = (OpcionSpinner) parent.getSelectedItem();
                            //Toast.makeText(getContext(), "ID: " + opcion.getId() + ",  Descripcion : " + opcion.getName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });*/
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

                    //label.addView(combo);
                    ll.addView(label);
                    ll.addView(combo);
                    listaCamposDinamicos.add(campos.get(i).get("campo").trim());
                    mapeoCamposDinamicos.put(campos.get(i).get("campo").trim(), combo);
                    if(campos.get(i).get("obl")!= null && campos.get(i).get("obl").trim().length() > 0){
                        listaCamposObligatorios.add(campos.get(i).get("campo").trim());
                    }
                } else {
                    //Tipo EditText normal textbox
                    TableRow fila = new TableRow(getContext());
                    fila.setOrientation(TableRow.HORIZONTAL);
                    fila.setWeightSum(10);
                    fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,10f));

                    TextInputLayout label = new TextInputLayout(getContext());
                    label.setHint(campos.get(i).get("descr"));
                    //label.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white,null)));

                    label.setHintTextAppearance(R.style.TextAppearance_App_TextInputLayout);
                    label.setErrorTextAppearance(R.style.AppTheme_TextErrorAppearance);

                    //final TextInputEditText et = new TextInputEditText(getContext());
                    final MaskedEditText et = new MaskedEditText(getContext(),null);
                    //et.setTextColor(getResources().getColor(R.color.colorTextView,null));
                    //et.setBackgroundColor(getResources().getColor(R.color.black,null));
                    //et.setHint(campos.get(i).get("descr"));
                    if(campos.get(i).get("sup").trim().length() > 0){
                        et.setVisibility(View.GONE);
                        label.setVisibility(View.GONE);
                    }
                    if(campos.get(i).get("vis").trim().length() > 0){
                        et.setEnabled(false);
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
                        TooltipCompat.setTooltipText(btnAyuda, campos.get(i).get("tooltip").toString());

                    }
                    if(campos.get(i).get("dfaul").trim().length() > 0){
                        et.setText(campos.get(i).get("dfaul").trim());
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
                    /*if(campos.get(i).get("llamado1").trim().contains("ValidarCedula")){
                        et.setOnFocusChangeListener(new OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                if (!hasFocus) {
                                    ValidarCedula(v,"C1");
                                }
                            }
                        });
                    }*/

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
                                        LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(), (EditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"), (EditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                                        autoPineo.startLocationUpdates();
                                        return true;
                                    }
                                }
                                return false;
                            }
                        });
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
            if(listaCamposDinamicos.contains("W_CTE-ZZCRMA_LAT") && listaCamposDinamicos.contains("W_CTE-ZZCRMA_LONG")){
                LocacionGPSActivity autoPineo = new LocacionGPSActivity(getContext(), getActivity(), (EditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LAT"), (EditText)mapeoCamposDinamicos.get("W_CTE-ZZCRMA_LONG"));
                autoPineo.startLocationUpdates();
            }
        }

        public void DesplegarBloque(DataBaseHelper db, View _ll, HashMap<String, String> campo) {
            int height = 50;
            TextView empty_data = new TextView(getContext());
            empty_data.setText("Sin Datos");
            empty_data.setBackground(getResources().getDrawable(R.color.backColor,null));
            int colorEvenRows = getResources().getColor(R.color.white,null);
            int colorOddRows = getResources().getColor(R.color.backColor,null);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout ll = (LinearLayout)_ll;
            LinearLayout.LayoutParams hlp;
            String[] headers;
            SimpleTableHeaderAdapter sta;

            CardView seccion_layout = new CardView(getContext());

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

                    //Adaptadores
                    if(contactosSolicitud != null) {
                        ContactoTableAdapter stda = new ContactoTableAdapter(getContext(), contactosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        tb_contactos.setDataAdapter(stda);
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
                    bloque_impuesto = tb_impuestos;
                    seccion_header.setOnClickListener(new View.OnClickListener() {
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
                    });

                    bloque_impuesto.setColumnCount(3);
                    bloque_impuesto.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_impuesto.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_impuesto.setLayoutParams(hlp);

                    //Adaptadores
                    if(impuestosSolicitud != null) {
                        ImpuestoTableAdapter stda = new ImpuestoTableAdapter(getContext(), impuestosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        bloque_impuesto.setDataAdapter(stda);
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
                    btnAddBloque.setOnClickListener(new View.OnClickListener() {
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
                    });
                    bloque_interlocutor.setColumnCount(3);
                    bloque_interlocutor.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_interlocutor.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_interlocutor.setLayoutParams(hlp);

                    //Adaptadores
                    if(interlocutoresSolicitud != null) {
                        InterlocutorTableAdapter stda = new InterlocutorTableAdapter(getContext(), interlocutoresSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        bloque_interlocutor.setDataAdapter(stda);
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

                    //Adaptadores
                    if(bancosSolicitud != null) {
                        BancoTableAdapter stda = new BancoTableAdapter(getContext(), bancosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        bloque_banco.setDataAdapter(stda);
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
                    de.codecrafters.tableview.TableView<Visitas> bloque_visita;
                    bloque_visita = tb_visitas;
                    btnAddBloque.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //displayDialogVisitas(view);
                        }
                    });
                    seccion_header.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //displayDialogVisitas(view);
                        }
                    });
                    bloque_visita.setColumnCount(3);
                    bloque_visita.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                    bloque_visita.setHeaderElevation(2);
                    hlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, height);
                    bloque_visita.setLayoutParams(hlp);

                    //Textos de dias de visita
                    TextInputEditText txt_lunes = new TextInputEditText(getContext());
                    TextInputEditText txt_martes = new TextInputEditText(getContext());
                    TextInputEditText txt_miercoles = new TextInputEditText(getContext());
                    TextInputEditText txt_jueves = new TextInputEditText(getContext());
                    TextInputEditText txt_viernes = new TextInputEditText(getContext());
                    TextInputEditText txt_sabado = new TextInputEditText(getContext());
                    TextInputEditText txt_domingo = new TextInputEditText(getContext());

                    TextInputEditText txt_vptyp = new TextInputEditText(getContext());
                    TextInputEditText txt_kvgr4 = new TextInputEditText(getContext());
                    TextInputEditText txt_ruta = new TextInputEditText(getContext());



                    //Adaptadores
                if(visitasSolicitud != null) {
                    VisitasTableAdapter stda = new VisitasTableAdapter(getContext(), visitasSolicitud);
                    stda.setPaddings(10, 5, 10, 5);
                    stda.setTextSize(10);
                    stda.setGravity(GRAVITY_CENTER);
                    bloque_visita.setDataAdapter(stda);
                }

                headers = ((VisitasTableAdapter)bloque_visita.getDataAdapter()).getHeaders();
                sta = new SimpleTableHeaderAdapter(getContext(), headers);
                sta.setPaddings(10,5,10,5);
                sta.setTextSize(12);
                sta.setTextColor(getResources().getColor(R.color.white,null));
                sta.setTypeface(Typeface.BOLD);
                sta.setGravity(GRAVITY_CENTER);

                bloque_visita.setHeaderAdapter(sta);
                bloque_visita.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

                rl.addView(bloque_visita);
                ll.addView(rl);
                break;
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //Pruebas para seccion de bloques
    public static void displayDialogContacto(Context context, final Contacto seleccionado)
    {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.contacto_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final EditText name1EditText = d.findViewById(R.id.name1EditTxt);
        final EditText namevEditText = d.findViewById(R.id.namevEditTxt);
        final EditText telf1EditText = d.findViewById(R.id.telf1EditTxt);
        final Spinner funcionSpinner = d.findViewById(R.id.funcionSpinner);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText("Modificar");
            title.setText("Modificar Contacto");
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
                nuevoContacto.setCountry(VariablesGlobales.getLand1());
                try {
                    contactosSolicitud.add(nuevoContacto);
                    name1EditText.setText("");
                    namevEditText.setText("");
                    telf1EditText.setText("");
                    funcionSpinner.setSelection(0);
                    if(contactosSolicitud != null) {
                        tb_contactos.setDataAdapter(new ContactoTableAdapter(v.getContext(), contactosSolicitud));
                        if(seleccionado == null)
                            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height+100;
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
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<OpcionSpinner>(context, R.layout.simple_spinner_item, listaopciones);
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
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static void displayDialogImpuesto(Context context, final Impuesto seleccionado)
    {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.impuesto_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final EditText nameEditText= d.findViewById(R.id.nameEditTxt);
        final EditText propellantEditTxt= d.findViewById(R.id.propEditTxt);
        final EditText destEditTxt= d.findViewById(R.id.destEditTxt);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText("Modificar");
            title.setText("Modificar Impuesto");
        }
        //SAVE
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seleccionado != null)
                    impuestosSolicitud.remove(seleccionado);
                Impuesto nuevoImpuesto = new Impuesto();
                nuevoImpuesto.setVtext(nameEditText.getText().toString());
                nuevoImpuesto.setVtext2(propellantEditTxt.getText().toString());
                nuevoImpuesto.setTaxkd(destEditTxt.getText().toString());
                impuestosSolicitud.add(nuevoImpuesto);
                if (true/*mDBHelper.guardarContacto(nuevoContacto)*/) {
                    nameEditText.setText("");
                    propellantEditTxt.setText("");
                    destEditTxt.setText("");
                    if(impuestosSolicitud != null) {
                        tb_impuestos.setDataAdapter(new ImpuestoTableAdapter(v.getContext(), impuestosSolicitud));
                        if(seleccionado == null)
                            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height+100;
                        d.dismiss();
                    }
                } else {
                    Toasty.error(v.getContext(), "No se pudo salvar el impuesto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(seleccionado != null){
            nameEditText.setText(seleccionado.getVtext());
            nameEditText.setText(seleccionado.getVtext2());
            nameEditText.setText(seleccionado.getTatyp());
            //funcionSpinner.setSelection(selectedIndex);
        }

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static void displayDialogInterlocutor(Context context, final Interlocutor seleccionado)
    {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.interlocutor_dialog_layout);
        d.setTitle("+ Nuevo Interlocutor");

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final EditText nameEditText= d.findViewById(R.id.nameEditTxt);
        final EditText propellantEditTxt= d.findViewById(R.id.propEditTxt);
        final EditText destEditTxt= d.findViewById(R.id.destEditTxt);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText("Modificar");
            title.setText("Modificar Interlocutor");
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
                if (true/*mDBHelper.guardarContacto(nuevoContacto)*/) {
                    nameEditText.setText("");
                    propellantEditTxt.setText("");
                    destEditTxt.setText("");
                    if(interlocutoresSolicitud != null) {
                        tb_interlocutores.setDataAdapter(new InterlocutorTableAdapter(v.getContext(), interlocutoresSolicitud));
                        if(seleccionado == null)
                            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height+100;
                        d.dismiss();
                    }
                } else {
                    Toasty.error(v.getContext(), "No se pudo salvar el interlocutor", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public static void displayDialogBancos(Context context, final Banco seleccionado)
    {
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.banco_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final Spinner bancoSpinner = d.findViewById(R.id.bancoSpinner);
        final Spinner paisSpinner= d.findViewById(R.id.paisSpinner);
        final EditText cuentaEditTxt= d.findViewById(R.id.cuentaEditTxt);
        final EditText claveEditTxt= d.findViewById(R.id.claveEditTxt);
        final EditText titularEditTxt= d.findViewById(R.id.titularEditTxt);
        final EditText tipoEditTxt= d.findViewById(R.id.tipoEditTxt);
        final EditText montoMaximoEditTxt= d.findViewById(R.id.montoMaximoEditTxt);
        Button saveBtn= d.findViewById(R.id.saveBtn);
        if(seleccionado != null){
            saveBtn.setText("Modificar");
            title.setText("Modificar Cuenta Bancaria");
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
                if (true/*mDBHelper.guardarContacto(nuevoContacto)*/) {
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
                            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height+100;
                        d.dismiss();
                    }
                } else {
                    Toasty.error(v.getContext(), "No se pudo salvar el banco", Toast.LENGTH_SHORT).show();
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
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<OpcionSpinner>(context, R.layout.simple_spinner_item, listaopciones);
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
        ArrayAdapter<OpcionSpinner> dataAdapterP = new ArrayAdapter<OpcionSpinner>(context, R.layout.simple_spinner_item, listaopcionesP);
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
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
            String salida = seleccionado.getName1() + " " + seleccionado.getNamev();
            contactosSolicitud.remove(rowIndex);
            tb_contactos.setDataAdapter(new ContactoTableAdapter(getBaseContext(), contactosSolicitud));
            tb_contactos.getLayoutParams().height = tb_contactos.getLayoutParams().height-100;
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
            tb_impuestos.getLayoutParams().height = tb_impuestos.getLayoutParams().height-100;
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
            tb_bancos.getLayoutParams().height = tb_bancos.getLayoutParams().height-100;
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
            tb_interlocutores.getLayoutParams().height = tb_interlocutores.getLayoutParams().height-100;
            Toasty.info(getBaseContext(), salida, Toast.LENGTH_SHORT).show();
            return true;
        }
    }
    private class VisitasClickListener implements TableDataClickListener<Visitas> {
        @Override
        public void onDataClicked(int rowIndex, Visitas seleccionado) {
            //displayDialogVisitas(SolicitudActivity.this,seleccionado);
        }
    }
    private class VisitasLongClickListener implements TableDataLongClickListener<Visitas> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Visitas seleccionado) {
            String salida = seleccionado.getVptyp() + " " + seleccionado.getRuta();
            visitasSolicitud.remove(rowIndex);
            tb_visitas.setDataAdapter(new VisitasTableAdapter(getBaseContext(), visitasSolicitud));
            tb_visitas.getLayoutParams().height = tb_visitas.getLayoutParams().height-100;
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

    private static void ReplicarValor(View v, String campo){
        TextView desde = (TextView)v;
        TextView hasta = (TextView)mapeoCamposDinamicos.get(campo);
        hasta.setText(desde.getText());
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
}
