package proyecto.app.clientesabc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.actividades.EncuestaActivity;
import proyecto.app.clientesabc.actividades.LocacionGPSActivity;
import proyecto.app.clientesabc.clases.AdjuntoAPI;
import proyecto.app.clientesabc.clases.AdjuntoServidor;
import proyecto.app.clientesabc.clases.CheckBoxGroupView;
import proyecto.app.clientesabc.actividades.OSMPickerActivity;
import proyecto.app.clientesabc.clases.PreguntaTextView;
import proyecto.app.clientesabc.modelos.OpcionCheckBox;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.PreguntasEncuesta;
import proyecto.app.clientesabc.modelos.RespuestaPregunta;

public class EncuestaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements LocacionGPSActivity.LocationListenerCallback {
    public interface OnImagePickerClickListener {
        void onImagePickerClicked(int position); // or pass any info you want
    }
    private static final int REQUEST_CODE_MAP = 123;
    private OnImagePickerClickListener listener;
    private Map<Integer, File> imageFiles;
    public EncuestaAdapter(OnImagePickerClickListener listener) {
        this.listener = listener;
    }
    private List<PreguntasEncuesta>  preguntas;
    private List<RespuestaPregunta>  respuestas;
    private Context context;
    private Activity activity;
    private DataBaseHelper db;
    private static SQLiteDatabase mDb;
    private String nombre_cliente;
    //LocacionGPSActivity locationServices;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class SeleccionHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listView;
        private SeleccionHolder(View v) {
            super(v);
            listView = v;
        }
    }

    public class TextoHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listView;
        private TextoHolder(View v) {
            super(v);
            listView = v;
        }
    }
    public class MultipleHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listView;
        private MultipleHolder(View v) {
            super(v);
            listView = v;
        }
    }
    public class NumericoHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listView;
        private NumericoHolder(View v) {
            super(v);
            listView = v;
        }
    }
    public class ImageHolder extends RecyclerView.ViewHolder {
        public View listView;
        private ImageHolder(View v) {
            super(v);
            listView = v;
        }
    }
    // Constructor de Adaptador HashMap
    public EncuestaAdapter(List<PreguntasEncuesta> dbpreguntas, Context c, Activity a, String nombre_cliente, List<RespuestaPregunta>  respuestas, OnImagePickerClickListener listener, Map<Integer, File> imageFiles) {
        preguntas = dbpreguntas;
        context = c;
        activity = a;
        db = new DataBaseHelper(context);
        mDb = db.getWritableDatabase();
        this.nombre_cliente = nombre_cliente;
        this.respuestas = respuestas;
        this.imageFiles = imageFiles;
        //para el lllamado de los botones se utiliza una interface
        this.listener = listener;
    }
    // Crear nuevas Views. Puedo crear diferentes layouts para diferentes adaptadores desde la misma clase
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // New View creada
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        switch (viewType){
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_texto_item, parent, false);
                viewHolder = new TextoHolder(v);
                break;
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_seleccion_item, parent, false);
                viewHolder = new SeleccionHolder(v);
                break;
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_multiple_item, parent, false);
                viewHolder = new MultipleHolder(v);
                break;
            case 4:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_numerico_item, parent, false);
                viewHolder = new NumericoHolder(v);
                break;
            case 5:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_image_item, parent, false);
                viewHolder = new ImageHolder(v);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_texto_item, parent, false);
                viewHolder = new TextoHolder(v);
                //throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return viewHolder;
    }
    // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        // - Obtener Elemento del data set en esta position
        // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset

        PreguntaTextView pregunta;
        TextView orden;
        CardView cardView;
        int viewType = holder.getItemViewType();
        switch (viewType){
            //texto
            case 1:
                TextoHolder textoHolder = (TextoHolder) holder;
                pregunta = textoHolder.listView.findViewById(R.id.pregunta);
                orden = textoHolder.listView.findViewById(R.id.orden_pregunta);
                pregunta.setText(preguntas.get(position).getTexto() == null?"":  preguntas.get(position).getTexto().trim());
                pregunta.setPreguntasEncuesta(preguntas.get(position));
                orden.setText(String.valueOf(preguntas.get(position).getOrden()));
                if(!respuestas.isEmpty()){
                    for (RespuestaPregunta respuesta: respuestas) {
                        if(respuesta.getIdPregunta()==pregunta.getPreguntasEncuesta().getId()){
                            EditText editText = textoHolder.listView.findViewById(R.id.multiple_group);
                            editText.setText(respuesta.getRespuesta());
                        }
                    }
                }
                break;
            case 2:
                SeleccionHolder seleccionHolder = (SeleccionHolder) holder;
                pregunta = seleccionHolder.listView.findViewById(R.id.pregunta);
                orden = seleccionHolder.listView.findViewById(R.id.orden_pregunta);
                Spinner spinner = seleccionHolder.listView.findViewById(R.id.multiple_group);
                pregunta.setPreguntasEncuesta(preguntas.get(position));

                ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
                int selectedIndex = 0;
                for (int j = 0; j < preguntas.get(position).getOpciones().size(); j++){
                    listaopciones.add(new OpcionSpinner(preguntas.get(position).getOpciones().get(j).getId(),preguntas.get(position).getOpciones().get(j).getIdTexto(), preguntas.get(position).getOpciones().get(j).getTexto()));
                }


                // Creando el adaptador(opciones) para el comboBox deseado
                ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                // attaching data adapter to spinner
                Drawable spinner_back = context.getResources().getDrawable(R.drawable.spinner_underlined, null);
                spinner.setBackground(spinner_back);
                spinner.setAdapter(dataAdapter);
                if(!respuestas.isEmpty()){
                    for (RespuestaPregunta respuesta: respuestas) {
                        if(respuesta.getIdPregunta()==pregunta.getPreguntasEncuesta().getId()){
                            for (int i = 0; i < listaopciones.size(); i++) {
                                if(String.valueOf( listaopciones.get(i).getIdSql()).equals(respuesta.getIdRespuesta())){
                                    spinner.setSelection(i);
                                }
                            }

                        }

                    }

                }
                pregunta.setText(preguntas.get(position).getTexto() == null?"":  preguntas.get(position).getTexto().trim());
                pregunta.setPreguntasEncuesta(preguntas.get(position));
                orden.setText(String.valueOf(preguntas.get(position).getOrden()));
                break;
            case 3:
                MultipleHolder multipleHolder = (MultipleHolder) holder;
                pregunta = multipleHolder.listView.findViewById(R.id.pregunta);
                orden = multipleHolder.listView.findViewById(R.id.orden_pregunta);
                pregunta.setText(preguntas.get(position).getTexto() == null?"":  preguntas.get(position).getTexto().trim());
                pregunta.setPreguntasEncuesta(preguntas.get(position));
                orden.setText(String.valueOf(preguntas.get(position).getOrden()));


                CheckBoxGroupView checkBoxGroupView = multipleHolder.listView.findViewById(R.id.checkGroup);
                checkBoxGroupView.setColumnCount(2);
                for (int j = 0; j < preguntas.get(position).getOpciones().size(); j++){
                    OpcionCheckBox checkBox = new OpcionCheckBox(context);
                    checkBox.setText(preguntas.get(position).getOpciones().get(j).getTexto());
                    checkBox.setIdTexto(preguntas.get(position).getOpciones().get(j).getIdTexto());
                    checkBox.setOpcionRespuesta(preguntas.get(position).getOpciones().get(j));


                    checkBoxGroupView.put(checkBox);
                    if(!respuestas.isEmpty()){
                        for (RespuestaPregunta respuesta: respuestas) {
                            if(respuesta.getIdPregunta()==pregunta.getPreguntasEncuesta().getId()){
                                if(respuesta.getIdRespuesta().equals(String.valueOf(checkBox.getOpcionRespuesta().getId()))){
//                                    checkBoxGroupView.setCheckboxCheckedById(Integer.parseInt(respuesta.getIdRespuesta()));
                                    checkBox.setChecked(true);
                                }

                            }
                        }
                    }
                }
                break;
            case 4:
                NumericoHolder numericoHolder = (NumericoHolder) holder;
                pregunta = numericoHolder.listView.findViewById(R.id.pregunta);
                orden = numericoHolder.listView.findViewById(R.id.orden_pregunta);
                pregunta.setText(preguntas.get(position).getTexto() == null?"":  preguntas.get(position).getTexto().trim());
                pregunta.setPreguntasEncuesta(preguntas.get(position));
                orden.setText(String.valueOf(preguntas.get(position).getOrden()));
                if(!respuestas.isEmpty()){
                    for (RespuestaPregunta respuesta: respuestas) {
                        if(respuesta.getIdPregunta()==pregunta.getPreguntasEncuesta().getId()){
                            EditText editText = numericoHolder.listView.findViewById(R.id.multiple_group);
                            editText.setText(respuesta.getRespuesta());
                        }
                    }
                }
                break;
            case 5:
                ImageHolder imageHolder = (ImageHolder) holder;
                pregunta = imageHolder.listView.findViewById(R.id.pregunta);
                pregunta.setPreguntasEncuesta(preguntas.get(position));
                orden = imageHolder.listView.findViewById(R.id.orden_pregunta);
                ImageView imageView = imageHolder.listView.findViewById(R.id.image_view);
                imageView.setImageResource(R.drawable.icon_file);
                orden.setText(String.valueOf(preguntas.get(position).getOrden()));

                File imageFile = imageFiles.get(position);
                if (imageFile != null && imageFile.exists()) {
                    imageView.setImageURI(Uri.fromFile(imageFile));
                    imageView.setTag("foto");
                } else {
                    // fallback to RespuestaPregunta if available
                    RespuestaPregunta respuesta = findRespuestaByPreguntaId(preguntas.get(position).getId());
                    //Recuperar el archivo desde el servidor
                    if(respuesta != null && respuesta.getImagenUrl() != null ){
                        TextView textView = new TextView(context);
                        textView.setText(respuesta.getImagenUrl());
                        //Realizar la transmision de lo que se necesita (Db o txt)
                        WeakReference<Context> weakRefs = new WeakReference<Context>(context);
                        WeakReference<Activity> weakRefAs = new WeakReference<Activity>(activity);
                        //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
                        if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
                            AdjuntoAPI s = new AdjuntoAPI(weakRefs, weakRefAs, imageView, textView, null);
                            if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
                                s.EnableWiFi();
                            }
                            s.execute();
                        } else {
                            AdjuntoServidor s = new AdjuntoServidor(weakRefs, weakRefAs, imageView, textView,imageFiles,position);
                            if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
                                s.EnableWiFi();
                            }else{
                                s.DisableWiFi();
                            }
                            s.execute();
                        }

                    }else
                    if (respuesta != null && respuesta.getImagenPath() != null) {
                        File fileFromDb = new File(respuesta.getImagenPath());
                        if (fileFromDb.exists()) {
                            imageView.setImageURI(Uri.fromFile(fileFromDb));
                            EncuestaActivity.ActualizarImagenesEncuesta(fileFromDb,imageFiles,position);
                            imageView.setTag("foto");
                        } else {
                            imageView.setImageResource(R.drawable.icon_file);
                            imageView.setTag("default");
                        }
                    } else {
                        imageView.setImageResource(R.drawable.icon_file);
                        imageView.setTag("default");
                    }
                }

                 // Simple and safe
                imageView.setOnClickListener(v -> {
                    listener.onImagePickerClicked(position);
                    //Intent intent = new Intent(context,OSMPickerActivity.class);
                    //activity.startActivityForResult(intent, REQUEST_CODE_MAP);
                });

                break;
            default:
                TextoHolder textoHolderdefault = (TextoHolder) holder;
                pregunta = textoHolderdefault.listView.findViewById(R.id.pregunta);
                orden = textoHolderdefault.listView.findViewById(R.id.orden_pregunta);
                pregunta.setText(preguntas.get(position).getTexto() == null?"":  preguntas.get(position).getTexto().trim());
                orden.setText(String.valueOf(preguntas.get(position).getOrden()));
                break;

        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        // Handle location updates in your activity here

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return preguntas.size();
    }

    @Override
    public int getItemViewType(int position) {
        //GET TIPO PREGUNTA TODO
        return preguntas.get(position).getTipoPregunta();
    }

    private RespuestaPregunta findRespuestaByPreguntaId(int preguntaId) {
        for (RespuestaPregunta r : respuestas) {
            if (r.getIdPregunta() == preguntaId) {
                return r;
            }
        }
        return null;
    }
}