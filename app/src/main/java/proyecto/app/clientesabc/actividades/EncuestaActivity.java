package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.EncuestaAdapter;
import proyecto.app.clientesabc.clases.CheckBoxGroupView;
import proyecto.app.clientesabc.clases.PreguntaTextView;
import proyecto.app.clientesabc.clases.TransmisionEncuestaServidor;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionCheckBox;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.PreguntasEncuesta;
import proyecto.app.clientesabc.modelos.RespuestaPregunta;

public class EncuestaActivity extends AppCompatActivity implements EncuestaAdapter.OnImagePickerClickListener {
    @Override
    public void onImagePickerClicked(int position) {
        showImageSourceDialog(position); // now you can call your activity method safely
    }
    private int selectedImagePosition = -1;
    static Uri mPhotoUri;
    DataBaseHelper db;
    public static SQLiteDatabase mDb;
    private static EncuestaAdapter mAdapter;
    String codigo_cliente;
    String nombre_cliente;
    String tipo_encuesta;
    String idEncuesta;
    String nombre_encuesta;
    String GecNuevo;
    String GecActual;
    boolean esGVC;
    List<PreguntasEncuesta> preguntas;
    List<RespuestaPregunta> respuestaPreguntas = new ArrayList<>();
    RecyclerView rv;
    boolean encuestaNueva = true;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Uri imageUri;
    private ImageView imageView;
    private Map<Integer, File> imageFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            codigo_cliente = b.getString("codigo_cliente");
            nombre_cliente = b.getString("nombre_cliente");
            idEncuesta = b.getString("idEncuesta");
            nombre_encuesta = b.getString("nombre_encuesta");
            GecNuevo = b.getString("GecNuevo");
            GecActual = b.getString("GecActual");
            esGVC = b.getBoolean("esGVC");
        }
        db = new DataBaseHelper(this);
        mDb = db.getWritableDatabase();
        preguntas = db.getPreguntasEncuesta(idEncuesta);
        respuestaPreguntas = db.getRespuestasEncuestaCliente(codigo_cliente,idEncuesta);

        setContentView(R.layout.encuesta_gec_layout);

        TextView GecNuevoTv = findViewById(R.id.texto_gec_nuevo2);
        TextView GecActualTv = findViewById(R.id.texto_gec_actual2);
        ImageView flecha = findViewById(R.id.arrowGec2);
        LinearLayout GecLayout = findViewById(R.id.GecLayout);
        if(!respuestaPreguntas.isEmpty()){
            encuestaNueva=false;
            GecLayout.setVisibility(View.VISIBLE);
            GecNuevoTv.setText(GecNuevo);
            GecActualTv.setText(GecActual);

        }
        rv = findViewById(R.id.recycler_view);
        imageFiles = new HashMap<>();

        mAdapter = new EncuestaAdapter(preguntas,this, EncuestaActivity.this,nombre_cliente,respuestaPreguntas,this,imageFiles);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        TextView cliente =  findViewById(R.id.cliente);
        TextView title =  findViewById(R.id.title);

        cliente.setText(codigo_cliente +" - "+nombre_cliente);

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarRespuestas(v.getContext())){
                    guardarRespuestas(v.getContext());
                };

            }
        });
    }
    @Override
    protected  void onResume(){
        super.onResume();

        /*preguntas = db.getPreguntasEncuesta(idEncuesta);
        RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new EncuestaAdapter(preguntas,this, EncuestaActivity.this,nombre_cliente,respuestaPreguntas, this,imageFiles);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));*/
    }

    private void showImageSourceDialog(int position) {
        selectedImagePosition = position;
        String[] options = {"Cámara", "Galeria"};

        new AlertDialog.Builder(this)
                .setTitle("Selecione")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        //Initialize on every usage
        mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new ContentValues());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            Log.e("tag", getResources().getString(R.string.no_activity));
        }
       /*
        //Initialize on every usage
        boolean mPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        } catch (ActivityNotFoundException e) {
            Log.e("tag", getResources().getString(R.string.no_activity));
        }
        */
    }

    private File createImageFile() {
        try {
            String fileName = "img_" + System.currentTimeMillis();
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(fileName, ".jpg", storageDir);
            //File finalImageFile = image;
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("latitude", 0);
            double lng = data.getDoubleExtra("longitude", 0);

            //editTextLat.setText(String.valueOf(lat));
            //editTextLng.setText(String.valueOf(lng));
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = null;

            File finalImageFile = null;
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                selectedImageUri = data.getData();
                finalImageFile = compressImage(selectedImageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                selectedImageUri = mPhotoUri;
                finalImageFile = compressImage(selectedImageUri);
            }

            if (finalImageFile != null) {
                imageFiles.put(selectedImagePosition, finalImageFile);
                mAdapter.notifyItemChanged(selectedImagePosition);
                //mAdapter.notifyDataSetChanged();
            }
        }
    }
    private File compressImage(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            File compressedFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "compressed_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(compressedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out); // adjust quality
            out.flush();
            out.close();
            return compressedFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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

    //falta para imagen
    private boolean validarRespuestas(Context context){
        boolean valido=true;
        String msj = "Por favor validar las preguntas: ";
        for (int i=0; i < mAdapter.getItemCount(); i++){
            LinearLayout borderContainer = null;
            RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(i);
            switch (viewHolder.getItemViewType()){
                case 1:
                    EncuestaAdapter.TextoHolder textoHolder = (EncuestaAdapter.TextoHolder) viewHolder;
                    borderContainer = textoHolder.listView.findViewById(R.id.border_container);
                    EditText editText = textoHolder.listView.findViewById(R.id.multiple_group);
                    TextView textView = textoHolder.listView.findViewById(R.id.pregunta);

                    if(editText.getText().toString().isEmpty()){
                        TextView numPregunta = textoHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                        borderContainer.setBackgroundResource(R.drawable.squared_orange_border);
                    }else{
                        borderContainer.setBackgroundResource(R.drawable.squared_textbackground);
                    }
                    break;
                case 2:
                    EncuestaAdapter.SeleccionHolder seleccionHolder = (EncuestaAdapter.SeleccionHolder) viewHolder;
                    borderContainer = seleccionHolder.listView.findViewById(R.id.border_container);
                    Spinner editTextSeleccion = seleccionHolder.listView.findViewById(R.id.multiple_group);

                   if(editTextSeleccion.getSelectedItem() == null){
                        TextView numPregunta = seleccionHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                       borderContainer.setBackgroundResource(R.drawable.squared_orange_border);
                   }else{
                       borderContainer.setBackgroundResource(R.drawable.squared_textbackground);
                   }
                    break;
                case 3:
                    EncuestaAdapter.MultipleHolder multipleHolder = (EncuestaAdapter.MultipleHolder) viewHolder;
                    borderContainer = multipleHolder.listView.findViewById(R.id.border_container);
                    CheckBoxGroupView checkBoxGroupView = multipleHolder.listView.findViewById(R.id.checkGroup);

                    if(checkBoxGroupView.getCheckboxesChecked().isEmpty()){
                        TextView numPregunta = multipleHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                        borderContainer.setBackgroundResource(R.drawable.squared_orange_border);
                    }else{
                        borderContainer.setBackgroundResource(R.drawable.squared_textbackground);
                    }
                    break;
                case 4:
                    EncuestaAdapter.NumericoHolder numericoHolder = (EncuestaAdapter.NumericoHolder) viewHolder;
                    borderContainer = numericoHolder.listView.findViewById(R.id.border_container);
                    EditText editTextNum = numericoHolder.listView.findViewById(R.id.multiple_group);

                    if(editTextNum.getText().toString().isEmpty()){
                        TextView numPregunta = numericoHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                        borderContainer.setBackgroundResource(R.drawable.squared_orange_border);
                    }else{
                        borderContainer.setBackgroundResource(R.drawable.squared_textbackground);
                    }
                    break;
                case 5:
                    EncuestaAdapter.ImageHolder imageHolder = (EncuestaAdapter.ImageHolder) viewHolder;
                    ImageView imageView = imageHolder.listView.findViewById(R.id.image_view);
                    borderContainer = imageHolder.listView.findViewById(R.id.border_container);

                    if(imageView.getTag() == "default"){
                        TextView numPregunta = imageHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                        borderContainer.setBackgroundResource(R.drawable.squared_orange_border);
                    }else{
                        borderContainer.setBackgroundResource(R.drawable.squared_textbackground);
                    }
                    break;
            }

        }

        if(!valido){
            msj=msj.substring(0,msj.length()-2);
            Toasty.error(context,msj).show();
        }

        return valido;
    }

//falta para imagen
    private void guardarRespuestas(Context context){
        UUID myGUID = java.util.UUID.randomUUID();
        if(!respuestaPreguntas.isEmpty()){
            myGUID = java.util.UUID.fromString(respuestaPreguntas.get(0).getGUID());
        }
        respuestaPreguntas.clear();

        for (int i=0; i<mAdapter.getItemCount(); i++){

            RespuestaPregunta respuestaPregunta = new RespuestaPregunta();
            respuestaPregunta.setGUID(myGUID.toString());
            respuestaPregunta.setIdEncuesta(idEncuesta);
            respuestaPregunta.setEncuesta(nombre_encuesta);
            respuestaPregunta.setFecha((new java.sql.Date(Calendar.getInstance().getTimeInMillis())).toString());
            respuestaPregunta.setNombreCliente(nombre_cliente);
            respuestaPregunta.setCodigoCliente(codigo_cliente);
            respuestaPregunta.setSociedad(PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BUKRS",""));

            RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(i);
            PreguntaTextView pregunta;
            EditText respuesta;
            switch (viewHolder.getItemViewType()){
                case 1:
                    EncuestaAdapter.TextoHolder textoHolder = (EncuestaAdapter.TextoHolder) viewHolder;

                    pregunta = textoHolder.listView.findViewById(R.id.pregunta);
                    respuesta = textoHolder.listView.findViewById(R.id.multiple_group);

                    respuestaPregunta.setIdPregunta(pregunta.getPreguntasEncuesta().getId());
                    respuestaPregunta.setTextoPregunta(pregunta.getText().toString());
                    respuestaPregunta.setRespuesta(respuesta.getText().toString());
                    respuestaPregunta.setIdTextoRespuesta(respuesta.getText().toString());
                    respuestaPregunta.setIdRespuesta("0");
                    respuestaPregunta.setIdTipoPregunta(String.valueOf(viewHolder.getItemViewType()));
                    respuestaPreguntas.add(respuestaPregunta);

                    break;
                case 2:
                    EncuestaAdapter.SeleccionHolder seleccionHolder = (EncuestaAdapter.SeleccionHolder) viewHolder;

                    pregunta = seleccionHolder.listView.findViewById(R.id.pregunta);
                    Spinner respuestaSpinner = seleccionHolder.listView.findViewById(R.id.multiple_group);

                    respuestaPregunta.setIdPregunta(pregunta.getPreguntasEncuesta().getId());
                    respuestaPregunta.setTextoPregunta(pregunta.getText().toString());
                    OpcionSpinner opcionSeleccionada = (OpcionSpinner)respuestaSpinner.getSelectedItem();
                    respuestaPregunta.setRespuesta(opcionSeleccionada.getName());
                    respuestaPregunta.setIdTipoPregunta(String.valueOf(viewHolder.getItemViewType()));
                    respuestaPregunta.setIdRespuesta(String.valueOf(opcionSeleccionada.getIdSql()));
                    respuestaPregunta.setIdTextoRespuesta(opcionSeleccionada.getId());
                    respuestaPreguntas.add(respuestaPregunta);
                    break;
                case 3:
                    EncuestaAdapter.MultipleHolder multipleHolder = (EncuestaAdapter.MultipleHolder) viewHolder;
                    CheckBoxGroupView checkBoxGroupView = multipleHolder.listView.findViewById(R.id.checkGroup);
                    pregunta = multipleHolder.listView.findViewById(R.id.pregunta);

                    List<OpcionCheckBox> opcionesSeleccionadas = (List<OpcionCheckBox>) checkBoxGroupView.getCheckboxesChecked();

                    for (OpcionCheckBox opcion:opcionesSeleccionadas) {

                        RespuestaPregunta respuestaPreguntaOpcion = new RespuestaPregunta();
                        respuestaPreguntaOpcion.setIdPregunta(pregunta.getPreguntasEncuesta().getId());
                        respuestaPreguntaOpcion.setTextoPregunta(pregunta.getText().toString());
                        respuestaPreguntaOpcion.setGUID(myGUID.toString());
                        respuestaPreguntaOpcion.setIdEncuesta(idEncuesta);
                        respuestaPreguntaOpcion.setEncuesta(nombre_encuesta);
                        respuestaPreguntaOpcion.setFecha((new java.sql.Date(Calendar.getInstance().getTimeInMillis())).toString());
                        respuestaPreguntaOpcion.setNombreCliente(nombre_cliente);
                        respuestaPreguntaOpcion.setCodigoCliente(codigo_cliente);
                        respuestaPreguntaOpcion.setSociedad(PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BUKRS",""));
                        
                        respuestaPreguntaOpcion.setRespuesta(opcion.getOpcionRespuesta().getTexto());
                        respuestaPreguntaOpcion.setIdTipoPregunta(String.valueOf(viewHolder.getItemViewType()));
                        respuestaPreguntaOpcion.setIdRespuesta(String.valueOf(opcion.getOpcionRespuesta().getId()));
                        respuestaPreguntaOpcion.setIdTextoRespuesta(opcion.getOpcionRespuesta().getIdTexto());
                        respuestaPreguntas.add(respuestaPreguntaOpcion);
                    }

                    if(checkBoxGroupView.getCheckboxesChecked().isEmpty()){
                        //TextView numPregunta = multipleHolder.listView.findViewById(R.id.orden_pregunta);
                    }
                    break;
                case 4:
                    EncuestaAdapter.NumericoHolder numericoHolder = (EncuestaAdapter.NumericoHolder) viewHolder;

                    pregunta = numericoHolder.listView.findViewById(R.id.pregunta);
                    respuesta = numericoHolder.listView.findViewById(R.id.multiple_group);

                    respuestaPregunta.setIdPregunta(pregunta.getPreguntasEncuesta().getId());
                    respuestaPregunta.setTextoPregunta(pregunta.getText().toString());
                    respuestaPregunta.setRespuesta(respuesta.getText().toString());
                    respuestaPregunta.setIdRespuesta("0");
                    respuestaPregunta.setIdTextoRespuesta(respuesta.getText().toString());
                    respuestaPregunta.setIdTipoPregunta(String.valueOf(viewHolder.getItemViewType()));
                    respuestaPreguntas.add(respuestaPregunta);
                    break;
                case 5:
                    EncuestaAdapter.ImageHolder imageHolder = (EncuestaAdapter.ImageHolder) viewHolder;

                    pregunta = imageHolder.listView.findViewById(R.id.pregunta);
                    respuesta = imageHolder.listView.findViewById(R.id.multiple_group);
                    ImageView imagen = imageHolder.listView.findViewById(R.id.image_view);


                    respuestaPregunta.setIdPregunta(pregunta.getPreguntasEncuesta().getId());
                    respuestaPregunta.setTextoPregunta(pregunta.getText().toString());
                    //respuestaPregunta.setRespuesta(respuesta.getText().toString());
                    //respuestaPregunta.setIdRespuesta("0");
                    //respuestaPregunta.setIdTextoRespuesta("");
                    respuestaPregunta.setIdTipoPregunta(String.valueOf(viewHolder.getItemViewType()));

                    File imageFile = imageFiles.get(i);
                    if (imageFile != null) {
                        respuestaPregunta.setImagenPath(imageFile.getAbsolutePath());
                    }
                    respuestaPreguntas.add(respuestaPregunta);
                    break;
            }
        }

        ContentValues respuestaValue = new ContentValues();
        if(!encuestaNueva){
            mDb.delete("respuesta_pregunta", "GUID= ? AND codigo_cliente= ?", new String[]{myGUID.toString(),codigo_cliente});
        }
        for (RespuestaPregunta respuestaPregunta : respuestaPreguntas){
            respuestaValue.put("GUID", respuestaPregunta.getGUID());
            respuestaValue.put("id_pregunta_encuesta", respuestaPregunta.getIdPregunta());
            respuestaValue.put("id_tipo_pregunta", respuestaPregunta.getIdTipoPregunta());
            respuestaValue.put("texto_pregunta", respuestaPregunta.getTextoPregunta());
            respuestaValue.put("respuesta", respuestaPregunta.getRespuesta());
            respuestaValue.put("id_respuesta", respuestaPregunta.getIdRespuesta());
            respuestaValue.put("id_texto_respuesta", respuestaPregunta.getIdTextoRespuesta());
            respuestaValue.put("fecha_ejecucion", respuestaPregunta.getFecha().toString());
            respuestaValue.put("id_encuesta", respuestaPregunta.getIdEncuesta());
            respuestaValue.put("texto_encuesta", respuestaPregunta.getEncuesta());
            respuestaValue.put("codigo_cliente", respuestaPregunta.getCodigoCliente());
            respuestaValue.put("nombre_cliente", respuestaPregunta.getNombreCliente());
            respuestaValue.put("bukrs", respuestaPregunta.getSociedad());
            respuestaValue.put("imagenPath", respuestaPregunta.getImagenPath());
            respuestaValue.put("imagenUrl", respuestaPregunta.getImagenUrl());
            try {
//                if(encuestaNueva){
//                    mDb.insert("respuesta_pregunta", null, respuestaValue);
//                }else{
//                    mDb.update("respuesta_pregunta", respuestaValue, "GUID= ? AND id_pregunta_encuesta= ? AND codigo_cliente= ?", new String[]{respuestaPregunta.getGUID(),String.valueOf(respuestaPregunta.getIdPregunta()),respuestaPregunta.getCodigoCliente()});
//                }
                mDb.insert("respuesta_pregunta", null, respuestaValue);

                respuestaValue.clear();
            } catch (Exception e) {
                Toasty.error(getApplicationContext(), "Error Insertando Respuesta Encuesta", Toasty.LENGTH_SHORT).show();
            }
        }
        WeakReference<Context> weakRef = new WeakReference<Context>(EncuestaActivity.this);
        WeakReference<Activity> weakRefA = new WeakReference<Activity>(EncuestaActivity.this);
        TransmisionEncuestaServidor f = new TransmisionEncuestaServidor(weakRef,weakRefA,myGUID.toString());
        if (PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("tipo_conexion", "").equals("wifi")) {
            f.EnableWiFi();
        } else {
            f.DisableWiFi();
        }
        f.execute();
    }

    public static void ActualizarImagenesEncuesta(File file, Map<Integer, File> imageFiles,int position ) {
        imageFiles.put(position,file);
    }
}
