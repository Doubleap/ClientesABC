package proyecto.app.clientesabc.adaptadores;

import static android.view.View.GONE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.UnsupportedPropertyException;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.BaseInstaladaActivity;
import proyecto.app.clientesabc.actividades.LocacionGPSActivity;
import proyecto.app.clientesabc.actividades.SolicitudActivity;
import proyecto.app.clientesabc.actividades.SolicitudAvisosEquipoFrioActivity;
import proyecto.app.clientesabc.clases.DialogHandler;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.clases.SwipeableRecyclerViewTouchListener;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoAPI;
import proyecto.app.clientesabc.clases.TransmisionLecturaCensoServidor;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class BaseInstaladaAdapter extends RecyclerView.Adapter<BaseInstaladaAdapter.MyViewHolder> implements Filterable , LocacionGPSActivity.LocationListenerCallback {
    private ArrayList<EquipoFrio> mDataset;
    private ArrayList<EquipoFrio> formListFiltered;
    private Context context;
    private Activity activity;
    private DataBaseHelper db;
    private static SQLiteDatabase mDb;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private String correo_cliente;
    private String canal_cliente;
    private String nombre_cliente;
    //LocacionGPSActivity locationServices;
    boolean coordenadasCapturadas = false;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View listView;
        private MyViewHolder(View v) {
            super(v);
            listView = v;
        }
    }
    // Constructor de Adaptador HashMap
    public BaseInstaladaAdapter(ArrayList<EquipoFrio> myDataset, Context c, Activity a, String canal_cliente, String correo_cliente, String nombre_cliente) {
        mDataset = myDataset;
        formListFiltered = mDataset;
        context = c;
        activity = a;
        db = new DataBaseHelper(context);
        mDb = db.getWritableDatabase();
        this.canal_cliente = canal_cliente;
        this.correo_cliente = correo_cliente;
        this.nombre_cliente = nombre_cliente;
        //locationServices = new LocacionGPSActivity(context, BaseInstaladaAdapter.this);
    }
    // Crear nuevas Views. Puedo crear diferentes layouts para diferentes adaptadores desde la misma clase
    @NonNull
    @Override
    public BaseInstaladaAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // New View creada
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.base_instalada_item, parent, false);
        return new MyViewHolder(v);
    }
    // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
    @Override
    public void onBindViewHolder(@NonNull final BaseInstaladaAdapter.MyViewHolder holder, final int position) {
        // - Obtener Elemento del data set en esta position
        // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
        TextView textViewHead = holder.listView.findViewById(R.id.base_instalada);
        TextView placa = holder.listView.findViewById(R.id.num_placa);
        TextView codigo = holder.listView.findViewById(R.id.num_serie);
        TextView nombre = holder.listView.findViewById(R.id.num_equipo);
        TextView modelo = holder.listView.findViewById(R.id.modelo);
        TextView ultima_fecha = (TextView) holder.listView.findViewById(R.id.text_ultima_fecha);
        CheckBox censado = (CheckBox) holder.listView.findViewById(R.id.check_box);
        LinearLayout estado = (LinearLayout) holder.listView.findViewById(R.id.color_estado);
        TextView estado_text = (TextView) holder.listView.findViewById(R.id.estado_escaneo);
        TextView comentario = (TextView) holder.listView.findViewById(R.id.comentario);
        ImageView anomalia = holder.listView.findViewById(R.id.anomalia);
        ImageView alerta = holder.listView.findViewById(R.id.alerta);
        ImageView eliminar = holder.listView.findViewById(R.id.eliminar);
        FloatingActionButton cantidad_alertas = (FloatingActionButton)holder.listView.findViewById(R.id.cantidad_alertas);
        TextView label_cantidad_alertas = (TextView)holder.listView.findViewById(R.id.label_cantidad_alertas);
        LinearLayout menu_bottom = (LinearLayout) holder.listView.findViewById(R.id.menu_bottom);
        ImageView transmitido = holder.listView.findViewById(R.id.transmitido);
        ImageView ver_anomalia = holder.listView.findViewById(R.id.ver_anomalia);
        /*ImageView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
        TextView tipo_solicitud = (TextView) holder.listView.findViewById(R.id.tipo_solicitud);
        TextView idform = (TextView) holder.listView.findViewById(R.id.idform);
        TextView fechas = (TextView) holder.listView.findViewById(R.id.fechas_text);*/
        int num_alertas = 0;
        CardView card_view = (CardView) holder.listView.findViewById(R.id.card_view);

        textViewHead.setText("");
        placa.setText("");
        codigo.setText("");
        nombre.setText("");
        modelo.setText("");
        ultima_fecha.setText("");
        estado_text.setText("");
        comentario.setText("");
        Integer maximoAlertas = db.MaximoAlertas(PreferenceManager.getDefaultSharedPreferences(context).getString("W_CTE_BUKRS",""));
        textViewHead.setText(formListFiltered.get(position).getIbase());
        placa.setText(formListFiltered.get(position).getSerge());
        codigo.setText(formListFiltered.get(position).getSernr());
        nombre.setText(formListFiltered.get(position).getEqunr());
        modelo.setText(formListFiltered.get(position).getEqktx());
        if(formListFiltered.get(position).getFechaLectura() != null)
            ultima_fecha.setText(formListFiltered.get(position).getFechaLectura().trim());
        comentario.setText(formListFiltered.get(position).getComentario());

        Drawable background = estado.getBackground();
        //Drawable background_circulo = estado_circulo.getBackground();
        int color = R.color.sinFormularios;

        if(formListFiltered.get(position).getTransmitido() != null && formListFiltered.get(position).getTransmitido().equals("0")){
            transmitido.setVisibility(View.VISIBLE);
            transmitido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Enviar la anomalia de una vez a la base de datos de SQL Server
                    WeakReference<Context> weakRef = new WeakReference<Context>(context);
                    WeakReference<Activity> weakRefA = new WeakReference<Activity>(activity);
                    EquipoFrio ef = db.getEquipoFrioDatosCenso(formListFiltered.get(position).getNumPlaca());
                    //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
                    if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
                        if(ef != null) {
                            TransmisionLecturaCensoAPI f = new TransmisionLecturaCensoAPI(weakRef, weakRefA, ef);
                            f.execute();
                        }
                    } else {
                        if(ef != null) {
                            TransmisionLecturaCensoServidor l = new TransmisionLecturaCensoServidor(weakRef, weakRefA, ef);
                            if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion", "").equals("wifi")) {
                                l.EnableWiFi();
                            } else {
                                l.DisableWiFi();
                            }
                            l.execute();
                        }
                    }
                }
            });
        }else{
            transmitido.setVisibility(View.GONE);
        }
        if(formListFiltered.get(position).getEstado() != null) {
            censado.setEnabled(false);
            if (formListFiltered.get(position).getEstado().trim().equals("Verificado")) {
                color = R.color.aprobados;
                censado.setChecked(true);
                eliminar.setVisibility(GONE);
                alerta.setVisibility(GONE);
                cantidad_alertas.setVisibility(GONE);
                anomalia.setVisibility(GONE);
                censado.setVisibility(View.VISIBLE);
                menu_bottom.setVisibility(View.GONE);

            }
            if (formListFiltered.get(position).getEstado().trim().equals("Anomalia") || formListFiltered.get(position).getEstado().trim().equals("Anomalía")) {
                color = R.color.rechazado;
                placa.setText(formListFiltered.get(position).getSerge());
                alerta.setVisibility(GONE);
                anomalia.setVisibility(GONE);
                cantidad_alertas.setVisibility(GONE);
                label_cantidad_alertas.setVisibility(GONE);
                eliminar.setVisibility(GONE);
                //menu_bottom.setVisibility(View.GONE);
                ver_anomalia.setVisibility(View.VISIBLE);
                ver_anomalia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putString("tipoSolicitud", "200");
                        b.putString("idSolicitud", formListFiltered.get(position).getIdSolicitud());
                        b.putString("codigoCliente", formListFiltered.get(position).getKunnr());
                        b.putString("codigoEquipoFrio", formListFiltered.get(position).getSerge());
                        Intent intent = new Intent(context, SolicitudAvisosEquipoFrioActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        context.startActivity(intent);
                    }
                });
            }

            if (formListFiltered.get(position).getEstado().trim().equals("Alerta")) {
                color = R.color.devuelto;
                placa.setText(formListFiltered.get(position).getSerge());
                eliminar.setVisibility(View.GONE);
                //alerta.setVisibility(GONE);
                //cantidad_alertas.setVisibility(GONE);
                num_alertas = db.CatidadAlertasPeriodo(formListFiltered.get(position).getSerge());
                label_cantidad_alertas.setText(String.valueOf(num_alertas));
            }
            if (formListFiltered.get(position).getEstado().trim().equals("Pendiente") || formListFiltered.get(position).getEstado().trim().equals("No escaneado")) {
                color = R.color.pendientes;
                alerta.setVisibility(View.VISIBLE);
                cantidad_alertas.setVisibility(View.VISIBLE);
                anomalia.setVisibility(View.VISIBLE);
                eliminar.setVisibility(GONE);
            }
            if (formListFiltered.get(position).getEstado().trim().equals("Hallazgo")) {
                color = R.color.modificado;
                placa.setText(formListFiltered.get(position).getNumPlaca());
                eliminar.setVisibility(View.VISIBLE);
                alerta.setVisibility(GONE);
                cantidad_alertas.setVisibility(GONE);
                anomalia.setVisibility(GONE);
            }
            estado_text.setText(formListFiltered.get(position).getEstado().trim());
        }else{
            color = R.color.pendientes;
            estado_text.setText("Pendiente");
            alerta.setVisibility(View.VISIBLE);
            cantidad_alertas.setVisibility(View.VISIBLE);
            anomalia.setVisibility(View.VISIBLE);
            eliminar.setVisibility(GONE);
        }
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}, // checked
                        new int[]{android.R.attr.state_enabled}, // enabled
                        new int[]{-android.R.attr.state_enabled}, // enabled
                },
                new int[]{
                        Color.parseColor(context.getResources().getString(color)),
                        Color.parseColor(context.getResources().getString(color)),
                        Color.parseColor(context.getResources().getString(color)),
                        Color.parseColor(context.getResources().getString(color)),
                }
        );
        CompoundButtonCompat.setButtonTintList(censado, colorStateList);
        estado_text.setTextColor(ContextCompat.getColor(context, color));
        estado.setBackground(ContextCompat.getDrawable(context, color));

        if(comentario.getText().toString().trim().equals("")){
            comentario.setVisibility(View.GONE);
        }else{
            comentario.setVisibility(View.VISIBLE);
        }


        //Funcionalidades con clicks
        if(estado_text.getText().equals("Hallazgo")) {
            card_view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogHandler appdialog = new DialogHandler();
                    appdialog.Confirm(activity, "Confirmar Eliminación", "Esta seguro que desea eliminar el registro?", "No", "Si", new BaseInstaladaAdapter.EliminarRegistroCenso(context,activity,formListFiltered.get(position).getNumPlaca()));
                    return false;
                }
            });
            eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogHandler appdialog = new DialogHandler();
                    appdialog.Confirm(activity, "Confirmar Eliminación", "Esta seguro que desea eliminar el registro?", "No", "Si", new BaseInstaladaAdapter.EliminarRegistroCenso(context,activity,formListFiltered.get(position).getNumPlaca()));
                }
            });
        }
        codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ModificarSolicitud(position);
            }
        });
        if(num_alertas < maximoAlertas) {
            alerta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//Genera un registro en tabla CensoEquipoFrio con comentario obligatoria y alerta
                    displayDialogGenerarAlerta(formListFiltered.get(position).getKunnr(), formListFiltered.get(position).getSerge(), latitude, longitude);
                }
            });
        }else{
            alerta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//Genera un registro en tabla CensoEquipoFrio con comentario obligatoria y alerta
                    Toasty.error(context,"Ha llegado al máximo de alertas permitido por equipo!\n\nPor favor generar la anomalía correspondiente para el seguimiento apropiado del equipo",Toasty.LENGTH_LONG).show();
                }
            });
        }
        cantidad_alertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Genera un registro en tabla CensoEquipoFrio con comentario obligatoria y alerta
                displayDialogGenerarAlerta(formListFiltered.get(position).getKunnr(), formListFiltered.get(position).getSerge(),  latitude, longitude);
            }
        });
        label_cantidad_alertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Genera un registro en tabla CensoEquipoFrio con comentario obligatoria y alerta
                displayDialogGenerarAlerta(formListFiltered.get(position).getKunnr(), formListFiltered.get(position).getSerge(),  latitude, longitude);
            }
        });
        anomalia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(context, "Abriendo Anomalía...", Toast.LENGTH_SHORT).show();
                Bundle b = new Bundle();
                b.putString("tipoSolicitud", "200");
                b.putString("codigoCliente", formListFiltered.get(position).getKunnr());
                b.putString("codigoEquipoFrio", formListFiltered.get(position).getSerge());
                Intent intent = new Intent(context, SolicitudAvisosEquipoFrioActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                context.startActivity(intent);
            }
        });
        /*
        tipo_solicitud.setText(formListFiltered.get(position).get("tipo_solicitud").trim());
        tipo_solicitud.setTextColor(color);
        if(formListFiltered.get(position).get("idform") != null)
            idform.setText("("+formListFiltered.get(position).get("idform").trim()+")");
        else
            idform.setText("(0)");

        Date date = null;
        Date dateEnd = null;
        try {
            if(formListFiltered.get(position).get("feccre") != null)
                date = new SimpleDateFormat("M/d/yyyy h:mm:ss aa").parse(formListFiltered.get(position).get("feccre").trim());
        } catch (ParseException e) {
            if(formListFiltered.get(position).get("feccre") != null) {
                try {
                    date = new SimpleDateFormat("dd/MM/yyyy h:mm:ss aa").parse(formListFiltered.get(position).get("feccre").trim().replace("p.m.","PM").replace("a.m.","AM"));
                } catch (ParseException ex) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(formListFiltered.get(position).get("feccre").trim().replace("p.m.","PM").replace("a.m.","AM"));
                    } catch (ParseException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }
        try {
            if(formListFiltered.get(position).get("fecfin") != null)
                dateEnd = new SimpleDateFormat("M/d/yyyy h:mm:ss aa").parse(formListFiltered.get(position).get("fecfin").trim());
        } catch (ParseException e) {
            if(formListFiltered.get(position).get("fecfin") != null) {
                try {
                    dateEnd = new SimpleDateFormat("dd/MM/yyyy h:mm:ss aa").parse(formListFiltered.get(position).get("fecfin").trim().replace("p.m.","PM").replace("a.m.","AM"));
                } catch (ParseException ex) {
                    try {
                        dateEnd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(formListFiltered.get(position).get("fecfin").trim().replace("p.m.","PM").replace("a.m.","AM"));
                    } catch (ParseException exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }
        String formattedDate = " - ";
        String formattedDateEnd = " - ";
        if(date != null) {
            formattedDate = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss aa").format(date).toUpperCase();
        }else{
            formattedDate = formListFiltered.get(position).get("feccre");
        }
        if(dateEnd != null){
            formattedDateEnd = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss aa").format(dateEnd).toUpperCase();
        }else{
            formattedDateEnd = formListFiltered.get(position).get("fecfin");
        }

        fechas.setText("Inicio: "+formattedDate+"\nFin: "+formattedDateEnd);
*/

        /*nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ModificarSolicitud(position);
            }
        });*/
        /*textViewOptions.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {

                Context wrapper = new ContextThemeWrapper(context, R.style.MyPopupMenu);

                //creating a popup menu
                PopupMenu popup = new PopupMenu(wrapper, holder.listView);
                popup.setGravity(Gravity.END);

                //inflating menu from xml resource
                popup.inflate(R.menu.solicitudes_item_menu);
                Menu popupMenu = popup.getMenu();
                if(formListFiltered.get(position).get("estado").trim().equals("Nuevo")){
                    MenuItem item = popupMenu.findItem(R.id.reactivar);
                    item.setVisible(false);
                }
                if(formListFiltered.get(position).get("estado").trim().equals("Aprobado")
                        || formListFiltered.get(position).get("estado").trim().equals("Rechazado")
                        || formListFiltered.get(position).get("estado").trim().equals("Pendiente")){
                    MenuItem item = popupMenu.findItem(R.id.modificar);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.incompleto);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.eliminar);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.transmitir);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.reactivar);
                    item.setVisible(false);
                }
                if(formListFiltered.get(position).get("estado").trim().equals("Incidencia")){
                    MenuItem item = popupMenu.findItem(R.id.incompleto);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.eliminar);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.transmitir);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.reactivar);
                    item.setVisible(false);
                }
                if(formListFiltered.get(position).get("estado").trim().equals("Modificado")){
                    MenuItem item = popupMenu.findItem(R.id.incompleto);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.eliminar);
                    item.setVisible(false);
                }
                if(formListFiltered.get(position).get("estado").trim().equals("Incompleto")){
                    MenuItem item = popupMenu.findItem(R.id.incompleto);
                    item.setVisible(false);
                    item = popupMenu.findItem(R.id.transmitir);
                    item.setVisible(false);
                }

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = null;
                        switch (item.getItemId()) {
                            case R.id.detalle:
                                ModificarSolicitud(position);
                                break;
                            case R.id.modificar:
                                ModificarSolicitud(position);
                                break;
                            case R.id.incompleto:
                                db.CambiarEstadoSolicitud(formListFiltered.get(position).get("id_solicitud").trim(),"Incompleto");
                                intent = activity.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                activity.finish();
                                activity.overridePendingTransition(0, 0);
                                startActivity(context, intent, null);
                                activity.overridePendingTransition(0, 0);
                                break;
                            case R.id.reactivar:
                                if(formListFiltered.get(position).get("estado").trim().equals("Modificado")){
                                    db.CambiarEstadoSolicitud(formListFiltered.get(position).get("id_solicitud").trim(),"Incidencia");
                                }else{
                                    db.CambiarEstadoSolicitud(formListFiltered.get(position).get("id_solicitud").trim(),"Nuevo");
                                }
                                intent = activity.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                activity.finish();
                                activity.overridePendingTransition(0, 0);
                                startActivity(context, intent, null);
                                activity.overridePendingTransition(0, 0);
                                break;
                            case R.id.eliminar:
                                db.EliminarSolicitud(formListFiltered.get(position).get("id_solicitud").trim());
                                intent = activity.getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                activity.finish();
                                activity.overridePendingTransition(0, 0);
                                startActivity(context, intent, null);
                                activity.overridePendingTransition(0, 0);
                                break;
                            case R.id.transmitir:
                                WeakReference<Context> weakRef = new WeakReference<Context>(context);
                                WeakReference<Activity> weakRefA = new WeakReference<Activity>((Activity)context);
                                //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
                                if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
                                    TransmisionAPI f = new TransmisionAPI(weakRef, weakRefA, "", "",formListFiltered.get(position).get("id_solicitud").trim());
                                    f.execute();
                                } else {
                                    TransmisionServidor f = new TransmisionServidor(weakRef, weakRefA, "", "",formListFiltered.get(position).get("id_solicitud").trim());
                                    f.execute();
                                }
                                break;
                        }
                        return false;
                    }
                });
                if (popup.getMenu() instanceof MenuBuilder) {
                    ((MenuBuilder) popup.getMenu()).setOptionalIconsVisible(true);
                }
                //displaying the popup
                popup.show();

            }
        });*/

            /*View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView codigo = v.findViewById(R.id.codigo);
                    Toast.makeText(getApplicationContext(), "Cliente "+codigo.getText()+" seleccionado",Toast.LENGTH_SHORT).show();
                }
            };
            holder.listView.setOnClickListener(mOnClickListener);*/
    }
    @Override
    public void onLocationUpdate(Location location) {
        // Handle location updates in your activity here
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        /*if(!coordenadasCapturadas) {
            Toasty.success(context, "Coordenadas Capturadas!").show();
            coordenadasCapturadas = true;
        }*/

    }
    /*private void ModificarSolicitud(int position) {
        Bundle b = new Bundle();
        Intent intent = null;
        b.putString("idSolicitud", formListFiltered.get(position).get("id_solicitud").trim()); //id de solicitud
        b.putString("codigoCliente", formListFiltered.get(position).get("codigo").trim());

        if(formListFiltered.get(position).get("tipform").trim().equals("1") || formListFiltered.get(position).get("tipform").trim().equals("6") || formListFiltered.get(position).get("ind_modelo").trim().equals("I")) {
            intent = new Intent(context, SolicitudActivity.class);
            intent.putExtras(b); //Pase el parametro el Intent
            context.startActivity(intent);
        }else if(formListFiltered.get(position).get("ind_modelo").trim().equals("M") || formListFiltered.get(position).get("ind_modelo").trim().equals("B") || formListFiltered.get(position).get("ind_modelo").trim().equals("L")){
            if(formListFiltered.get(position).get("ind_credito").trim().equals("0")) {
                intent = new Intent(context, SolicitudModificacionActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                context.startActivity(intent);
            }else if(formListFiltered.get(position).get("ind_credito").trim().equals("1")) {
                intent = new Intent(context, SolicitudCreditoActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                context.startActivity(intent);
            }
        }else if(formListFiltered.get(position).get("ind_modelo").trim().equals("E")){
            intent = new Intent(context, SolicitudAvisosEquipoFrioActivity.class);
            intent.putExtras(b); //Pase el parametro el Intent
            context.startActivity(intent);
        }
    }*/

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return formListFiltered.size();
    }

    //Para filtrar busquedas segun criterios
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    formListFiltered = mDataset;
                } else {
                    ArrayList<EquipoFrio> filteredList = new ArrayList<>();
                    for (EquipoFrio row : mDataset) {
                        if (row.getSernr() != null && row.getSernr().trim().contains(charString)  )
                            filteredList.add(row);
                        else if (row.getEqunr() != null && row.getEqunr().trim().contains(charString))
                            filteredList.add(row);
                        else if (row.getIbase() != null && row.getIbase().trim().contains(charString) ){
                            filteredList.add(row);
                        }
                    }
                    formListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = formListFiltered;

                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
                        if(toolbar != null)
                            toolbar.setTitle("Mis Solicitudes ("+formListFiltered.size()+" de "+mDataset.size()+")");
                    }
                });
                return filterResults;
            }
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                formListFiltered = (ArrayList<EquipoFrio>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
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
                    insertValues.put("nombre_cliente", nombre_cliente);
                    insertValues.put("num_placa",numPlaca);
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
                    insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(context).getString("userMC",""));
                    insertValues.put("comentario", comentario.getText().toString());
                    insertValues.put("id_motivo_alerta", ((OpcionSpinner) motivoSpinner.getSelectedItem()).getId());

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
    /*public Filter getMultiFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String[] charString = charSequence.toString().split(",");
                FilterResults filterResults = new FilterResults();
                ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();
                for(int x=0; x < charString.length; x++) {
                    if (charString[x].isEmpty()) {
                        formListFiltered = mDataset;
                    } else {
                        for (EquipoFrio row : mDataset) {
                            if (row.get("estado") != null && row.get("estado").trim().contains(charString[x]))
                                filteredList.add(row);
                            else if (row.get("tipo_solicitud") != null && row.get("tipo_solicitud").trim().contains(charString[x])) {
                                filteredList.add(row);
                            }
                        }
                        formListFiltered = filteredList;
                    }

                    filterResults.values = formListFiltered;
                }
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
                        if(toolbar != null)
                            toolbar.setTitle("Mis Solicitudes ("+formListFiltered.size()+" de "+mDataset.size()+")");
                    }
                });
                return filterResults;
            }
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                formListFiltered = (ArrayList<HashMap<String, String>>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }*/
}