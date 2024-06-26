package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

public class EncuestaActivity extends AppCompatActivity{
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

        mAdapter = new EncuestaAdapter(preguntas,this, EncuestaActivity.this,nombre_cliente,respuestaPreguntas);
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

        preguntas = db.getPreguntasEncuesta(idEncuesta);
        RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new EncuestaAdapter(preguntas,this, EncuestaActivity.this,nombre_cliente,respuestaPreguntas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

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
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Verificado");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",eq.getSerge());
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("num_activo", eq.getSernr());
                        insertValues.put("num_equipo", eq.getEqunr());
                        insertValues.put("modelo_equipo", eq.getMatnr());
                        insertValues.put("fuente", "Escaner");
                        insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("userMC",""));


                    }else
                    //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                    if (!db.ExisteEquipoFrio(b.getString("codigo"))) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Hallazgo");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",b.getString("codigo").trim());
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("comentario","Número de placa no aparece en ningun cliente instalado.");
                        insertValues.put("fuente", "Escaner");
                    }//3. El codigo del equipo leida esta en otro cliente
                    else if (db.ExisteEquipoFrio(b.getString("codigo"))) {
                        EquipoFrio eq = db.getEquipoFrioDatosCenso(b.getString("codigo"));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Hallazgo");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",eq.getSerge());
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("num_activo", eq.getSernr());
                        insertValues.put("num_equipo", eq.getEqunr());
                        insertValues.put("modelo_equipo", eq.getMatnr());
                        insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(EncuestaActivity.this).getString("userMC",""));
                        insertValues.put("comentario","Pertenece a otro cliente "+eq.getKunnr()+"!");
                        insertValues.put("fuente","Escaner");
                    }
                }
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

    private boolean validarRespuestas(Context context){
        boolean valido=true;
        String msj = "Por favor validar las preguntas: ";
        for (int i=0; i<mAdapter.getItemCount(); i++){


        RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(i);

            switch (viewHolder.getItemViewType()){
                case 1:
                    EncuestaAdapter.TextoHolder textoHolder = (EncuestaAdapter.TextoHolder) viewHolder;

                    EditText editText = textoHolder.listView.findViewById(R.id.multiple_group);
                    TextView textView = textoHolder.listView.findViewById(R.id.pregunta);
                    if(editText.getText().toString().isEmpty()){
                        TextView numPregunta = textoHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                    }
                    break;
                case 2:
                    EncuestaAdapter.SeleccionHolder seleccionHolder = (EncuestaAdapter.SeleccionHolder) viewHolder;

//                   EditText editTextSeleccion = seleccionHolder.listView.findViewById(R.id.multiple_group);
//
//                   if(editTextSeleccion.getText().toString().isEmpty()){
//                        TextView numPregunta = seleccionHolder.listView.findViewById(R.id.orden_pregunta);
//                        msj+=numPregunta.getText()+", ";
//                        valido=false;
//                   }
                    break;
                case 3:
                    EncuestaAdapter.MultipleHolder multipleHolder = (EncuestaAdapter.MultipleHolder) viewHolder;

                    CheckBoxGroupView checkBoxGroupView = multipleHolder.listView.findViewById(R.id.checkGroup);

                    if(checkBoxGroupView.getCheckboxesChecked().isEmpty()){
                        TextView numPregunta = multipleHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
                    }
                    break;
                case 4:
                    EncuestaAdapter.NumericoHolder numericoHolder = (EncuestaAdapter.NumericoHolder) viewHolder;

                    EditText editTextNum = numericoHolder.listView.findViewById(R.id.multiple_group);

                    if(editTextNum.getText().toString().isEmpty()){
                        TextView numPregunta = numericoHolder.listView.findViewById(R.id.orden_pregunta);
                        msj+=numPregunta.getText()+", ";
                        valido=false;
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


    private void guardarRespuestas(Context context){
        UUID myGUID = java.util.UUID.randomUUID();
        if(!respuestaPreguntas.isEmpty()){
            myGUID = java.util.UUID.fromString(respuestaPreguntas.get(0).getGUID());
        }
        respuestaPreguntas.clear();

        for (int i=0; i<mAdapter.getItemCount(); i++){
//id



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
                        respuestaPreguntaOpcion.setIdPregunta(pregunta.getPreguntasEncuesta().getId());
                        respuestaPreguntaOpcion.setTextoPregunta(pregunta.getText().toString());
                        respuestaPreguntaOpcion.setGUID(myGUID.toString());
                        respuestaPreguntaOpcion.setEncuesta(tipo_encuesta);
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



}
