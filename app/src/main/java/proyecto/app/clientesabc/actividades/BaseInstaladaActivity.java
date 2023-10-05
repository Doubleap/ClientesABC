package proyecto.app.clientesabc.actividades;

import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.BaseInstaladaAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.clases.FileHelper;
import proyecto.app.clientesabc.clases.KeyPairBoolData;
import proyecto.app.clientesabc.clases.ManejadorAdjuntos;
import proyecto.app.clientesabc.clases.MovableFloatingActionButton;
import proyecto.app.clientesabc.clases.MultiSpinnerListener;
import proyecto.app.clientesabc.clases.MultiSpinnerSearch;
import proyecto.app.clientesabc.clases.TesseractOCR;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.EquipoFrio;


public class BaseInstaladaActivity extends AppCompatActivity {
    DataBaseHelper db;

    private RecyclerView recyclerView;
    private BaseInstaladaAdapter mAdapter;

    private SearchView searchView;
    //private MyAdapter mAdapter;
    private MovableFloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    boolean isFABOpen = false;
    String codigo_cliente;
    String nombre_cliente;
    ArrayList<EquipoFrio> formList;
    ArrayList<HashMap<String, String>> filteredFormList;
    Toolbar toolbar;
    static Uri mPhotoUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.detalle);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            codigo_cliente = b.getString("codigo_cliente");
            nombre_cliente = b.getString("nombre_cliente");
        }
        db = new DataBaseHelper(this);
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

        mAdapter = new BaseInstaladaAdapter(formList,this,BaseInstaladaActivity.this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        fab = findViewById(R.id.scanBtn);
        fab2 = findViewById(R.id.camaraBtn);
        //fab3 = findViewById(R.id.addBtn);
        //fab1.hide();fab2.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EscanearActivity.class);
                Bundle bc = new Bundle();
                bc.putString("campoEscaneo", "censo_equipo_frio");
                bc.putInt("requestCode", VariablesGlobales.ESCANEO_EQUIPO_FRIO);
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
        /*fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFilters(view);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud

                Intent intent = new Intent(view.getContext(),SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);
            }
        });*/
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
        formList = db.getEquiposFriosDB(codigo_cliente);
        RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new BaseInstaladaAdapter(formList,this, BaseInstaladaActivity.this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));
        toolbar.setTitle(codigo_cliente +" - "+nombre_cliente);
        toolbar.setSubtitle("Base Instalada ("+mAdapter.getItemCount()+")");
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

    private void showFABMenu(){
        isFABOpen=true;
        fab1.show();fab2.show();
        fab1.animate().translationY((float)-120.0);
        fab2.animate().translationY((float)-240.0);

        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }
    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab1.hide();fab2.hide();
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
                    //1. El codigo del equipo frio si exsite en el cliente, simplemente se marca como censado
                    //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como DESCUBRIMIENTO o anomalía
                    //3. Hay un equipo que no puede ser censado pero si esta en la lista del cliente(NO tiene placa, NO esta en sitio, no existe), Se debe poder indicar que el equipo no pudo ser censado y ver que estado ponerle
                    //4. El codigo del equipo leida esta en otro cliente
                    //campoEscaneo = b.getString("campoEscaneo");
                    //Toasty.info(getBaseContext(),b.getString("codigo")).show();
                    //Validaciones de equipo Leido
                    if (db.ExisteEquipoFrioEnCliente(codigo_cliente, b.getString("codigo"))) {

                    }

                    //Toasty.info(getApplicationContext(), "Codigo Leido : " + b.getString("codigo")).show();
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

    public static Bitmap ColorToGrayscale(Bitmap bm) {
        Bitmap grayScale = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.RGB_565);

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);

        Paint p = new Paint();
        p.setColorFilter(new ColorMatrixColorFilter(cm));

        new Canvas(grayScale).drawBitmap(bm, 0, 0, p);

        return grayScale;
    }

    public static Bitmap GrayscaleToBin(Bitmap bm, int threshold) {
        Bitmap bin = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

        ColorMatrix cm = new ColorMatrix(new float[] {
                85.f, 85.f, 85.f, 0.f, -255.f * threshold,
                85.f, 85.f, 85.f, 0.f, -255.f * threshold,
                85.f, 85.f, 85.f, 0.f, -255.f * threshold,
                0f, 0f, 0f, 1f, 0f
        });

        Paint p = new Paint();
        p.setColorFilter(new ColorMatrixColorFilter(cm));

        new Canvas(bin).drawBitmap(bm, 0, 0, p);

        return bin;
    }
}
