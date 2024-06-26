package proyecto.app.clientesabc.adaptadores;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.io.Resources;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.actividades.EncuestaActivity;
import proyecto.app.clientesabc.actividades.LocacionGPSActivity;
import proyecto.app.clientesabc.clases.CheckBoxGroupView;
import proyecto.app.clientesabc.clases.PreguntaTextView;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoAPI;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoServidor;
import proyecto.app.clientesabc.modelos.EncuestaCabecera;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionCheckBox;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.PreguntasEncuesta;
import proyecto.app.clientesabc.modelos.RespuestaPregunta;

public class EncuestaCabeceraAdapter extends RecyclerView.Adapter<EncuestaCabeceraAdapter.MyViewHolder>  {
    private List<EncuestaCabecera>  encuestaCabeceras;
    private Context context;
    private Activity activity;
    private DataBaseHelper db;
    private static SQLiteDatabase mDb;
    String codigo_cliente;
    String nombre_cliente;
    String tipo_encuesta;
    String GecActual;
    String GecNuevo;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listView;
        TextView nombre;
        TextView descripcion;
        TextView GecActual;
        TextView GecNuevo;
        CardView encuestaCard;
        ImageView estadoIcon;
        ImageView flecha;
        LinearLayout gecLayout;
        private MyViewHolder(View v) {
            super(v);
            listView = v;
            nombre = v.findViewById(R.id.nombre_encuesta);
            descripcion = v.findViewById(R.id.descripcion_encuesta);
            encuestaCard = v.findViewById(R.id.encuesta);
            estadoIcon = v.findViewById(R.id.estadoIcon);
            GecActual = v.findViewById(R.id.texto_gec_actual);
            GecNuevo = v.findViewById(R.id.texto_gec_nuevo);
            flecha = v.findViewById(R.id.arrowGec);
            gecLayout = v.findViewById(R.id.gecLayout);
        }
    }
    // Constructor de Adaptador HashMap
    public EncuestaCabeceraAdapter(List<EncuestaCabecera>  dbencuestaCabeceras, Context c,String codigo_cliente,String nombre_cliente,String tipo_encuesta) {
        encuestaCabeceras = dbencuestaCabeceras;
        context = c;
        this.nombre_cliente = nombre_cliente;
        this.codigo_cliente = codigo_cliente;
        this.tipo_encuesta = tipo_encuesta;
        db = new DataBaseHelper(context);
        mDb = db.getWritableDatabase();

    }
    // Crear nuevas Views. Puedo crear diferentes layouts para diferentes adaptadores desde la misma clase
    @NonNull
    @Override
    public EncuestaCabeceraAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // New View creada
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.encuesta_cabecera_item, parent, false);
        return new MyViewHolder(v);
    }

    // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
    @Override
    public void onBindViewHolder(@NonNull final EncuestaCabeceraAdapter.MyViewHolder holder, final int position) {
        // - Obtener Elemento del data set en esta position
        // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset

       EncuestaCabecera encuestaCabecera = encuestaCabeceras.get(position);

        boolean ejecutada = db.getValidacionEncuestaClientePendiente(String.valueOf(encuestaCabecera.getId()),codigo_cliente);
        String GUIDactual = db.getGUIDEncuestaActualCliente(String.valueOf(encuestaCabecera.getId()),codigo_cliente);
        String estado= db.getEstadoRespuestaEnviada(GUIDactual);
        Integer montoTotal= db.getMontoTotalEncuestaGVC(GUIDactual);
        String gecNuevo= db.getGecDescripcionSegunEncuestaRealizada(montoTotal);
        String gecActual= db.getGecDescripcionActual(codigo_cliente);
        holder.descripcion.setText(encuestaCabecera.getDescripcion());
        holder.nombre.setText(encuestaCabecera.getNombre());
        if(ejecutada){
            if(estado.equals("pendiente")){
                holder.estadoIcon.setBackgroundResource(R.drawable.ic_baseline_warning_24);
                holder.estadoIcon.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.devuelto,null)));
            }else{
                holder.estadoIcon.setBackgroundResource(R.drawable.ic_check_white_24dp);
                holder.estadoIcon.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.aprobados,null)));

            }
            if(encuestaCabecera.isGvc()){
                holder.gecLayout.setVisibility(View.VISIBLE);
                holder.GecNuevo.setText(gecNuevo);
                holder.GecActual.setText(gecActual);
            }
        }else{
            holder.GecActual.setVisibility(View.INVISIBLE);
            holder.GecNuevo.setVisibility(View.INVISIBLE);
            holder.flecha.setVisibility(View.INVISIBLE);
            holder.estadoIcon.setBackgroundResource(R.drawable.icon_close);
            holder.estadoIcon.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.rechazado,null)));
        }

        holder.encuestaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bc = new Bundle();
                    bc.putString("codigo_cliente", codigo_cliente);
                    bc.putString("nombre_cliente", nombre_cliente);
                    bc.putString("canal_cliente", "");
                    bc.putString("correo_cliente", "");
                    bc.putString("tipo_encuesta","GVC");
                    bc.putString("idEncuesta",String.valueOf(encuestaCabecera.getId()));
                    bc.putString("nombre_encuesta",encuestaCabecera.getNombre());
                    bc.putString("GecNuevo",gecNuevo);
                    bc.putString("GecActual",gecActual);
                    bc.putBoolean("esGVC",encuestaCabecera.isGvc());
                    Intent intent;
                    intent = new Intent(context, EncuestaActivity.class);
                    intent.putExtras(bc); //Pase el parametro el Inten
                    v.getContext().startActivity(intent);
            }
        });






    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return encuestaCabeceras.size();
    }



    public void displayDialogGenerarAlerta(final String codigoCliente, final String numPlaca, Double latitude, Double longitude) {
        ArrayList<HashMap<String, String>> opciones = db.getDatosCatalogo("cat_loc_motivo_no_scan_ef", "genera_formulario='0'");
        if(opciones.size() == 1){
            Toasty.warning(context, "NO existen motivos de alerta!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog d=new Dialog(context);
        d.setContentView(R.layout.generar_alerta_dialog_layout);

        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final SearchableSpinner motivoSpinner = (SearchableSpinner) d.findViewById(R.id.motivoSpinner);
        final MaskedEditText comentario = (MaskedEditText) d.findViewById(R.id.comentario);
        motivoSpinner.setTitle("Generar Alerta placa #"+numPlaca);
        motivoSpinner.setPositiveButton("Cerrar");
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(0, -10, 0, 25);
        motivoSpinner.setPadding(0,0,0,0);
        motivoSpinner.setLayoutParams(lp);
        motivoSpinner.setPopupBackgroundResource(R.drawable.menu_item);
        Button saveBtn= d.findViewById(R.id.saveBtn);

        //SAVE, en este caso solo es aceptar, ir a a pintar el formulario correspondiente dependiendo del equipo frio seleccionado
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String motivo = ((OpcionSpinner)motivoSpinner.getSelectedItem()).getId();
                String comentario_txt = comentario.getText().toString();
                if(motivo.isEmpty()){
                    Toasty.warning(v.getContext(), "Por favor seleccione un motivo para la alerta!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(comentario_txt.isEmpty()){
                    Toasty.warning(v.getContext(), "Por favor realice un comentario explicativo de la alerta!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                   //Guardar la alerta del equipo que no es posible escanear en el momento
                    EquipoFrio eq = db.getEquipoFrioDB(codigoCliente, numPlaca, false);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = new Date();
                    ContentValues insertValues = new ContentValues();
                    insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BUKRS",""));
                    insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BZIRK",""));
                    insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_RUTAHH",""));
                    insertValues.put("estado","Alerta");
                    insertValues.put("kunnr_censo",codigoCliente);
                    insertValues.put("num_placa",numPlaca);
                    insertValues.put("coordenada_x", latitude);
                    insertValues.put("coordenada_y", longitude);
                    insertValues.put("activo", "1");
                    insertValues.put("transmitido", "0");
                    insertValues.put("fecha_lectura", dateFormat.format(date));
                    insertValues.put("num_activo", eq.getSernr());
                    insertValues.put("num_equipo", eq.getEqunr());
                    insertValues.put("modelo_equipo", eq.getMatnr());
                    insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(context).getString("userMC",""));
                    insertValues.put("comentario", comentario.getText().toString());
                    insertValues.put("id_motivo_alerta", ((OpcionSpinner) motivoSpinner.getSelectedItem()).getId());
                    insertValues.put("fuente", "Alerta");

                    //Justo antes de guardar, SI NO TIENE LAS COORDENADAS, preguntar si realmente quiere realizar el regsitro del censo SIN coordendas

                    if(latitude.equals(0) && longitude.equals(0)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(R.drawable.icon_info_title);
                        builder.setTitle("Confirmación");
                        builder.setCancelable(false);
                        builder.setMessage("No se han capturado las coordenadas. Desea continuar de todas maneras?");
                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if user pressed "yes", continue with execution
                                long inserto = mDb.insertOrThrow("CensoEquipoFrio", null, insertValues);

                                if(inserto == -1){
                                    Toasty.info(context, "No se pudo guardar la alerta de equipo frio!").show();
                                }else{
                                    //Intentar 1 vez el envio automatico de la lectura.
                                    WeakReference<Context> weakRef = new WeakReference<Context>(context);
                                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(activity);
                                    EquipoFrio ef = db.getEquipoFrioDatosCenso(numPlaca);

                                    if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
                                        TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, ef);
                                        f.execute();
                                    } else {
                                        TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                        if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion", "").equals("wifi")) {
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
                            Toasty.info(context, "No se pudo guardar la alerta de equipo frio!").show();
                        }else{
                            //Intentar 1 vez el envio automatico de la lectura.
                            WeakReference<Context> weakRef = new WeakReference<Context>(context);
                            WeakReference<Activity> weakRefA = new WeakReference<Activity>(activity);
                            EquipoFrio ef = db.getEquipoFrioDatosCenso(numPlaca);
                            if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
                                TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, ef);
                                f.execute();
                            } else {
                                TransmisionLecturaCensoServidor f = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                                if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion", "").equals("wifi")) {
                                    f.EnableWiFi();
                                } else {
                                    f.DisableWiFi();
                                }
                                f.execute();
                            }

                        }
                    }


                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo guardar la alerta al equipo!."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                d.dismiss();
            }
        });

        //Para campos de seleccion

        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        for (int j = 0; j < opciones.size(); j++){
            listaopciones.add(new OpcionSpinner(opciones.get(j).get("id"), opciones.get(j).get("descripcion")));
        }
        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable spinner_back = context.getDrawable(R.drawable.spinner_background);
        motivoSpinner.setBackground(spinner_back);
        motivoSpinner.setAdapter(dataAdapter);
        motivoSpinner.setSelection(0);

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }


    @Override
    public int getItemViewType(int position) {
        //GET TIPO PREGUNTA TODO
        return encuestaCabeceras.get(position).getId();
    }
}