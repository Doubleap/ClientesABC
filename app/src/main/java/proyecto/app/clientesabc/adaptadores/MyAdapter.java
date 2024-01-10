package proyecto.app.clientesabc.adaptadores;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.SolicitudActivity;
import proyecto.app.clientesabc.actividades.SolicitudAvisosEquipoFrioActivity;
import proyecto.app.clientesabc.actividades.SolicitudCreditoActivity;
import proyecto.app.clientesabc.actividades.SolicitudModificacionActivity;
import proyecto.app.clientesabc.clases.TransmisionAPI;
import proyecto.app.clientesabc.clases.TransmisionServidor;

import static androidx.core.content.ContextCompat.startActivity;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    private ArrayList<HashMap<String, String>> mDataset;
    private ArrayList<HashMap<String, String>> formListFiltered;
    private Context context;
    private Activity activity;
    DataBaseHelper db;
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
    public MyAdapter(ArrayList<HashMap<String, String>> myDataset,Context c, Activity a) {
        mDataset = myDataset;
        formListFiltered = mDataset;
        context = c;
        activity = a;
        db = new DataBaseHelper(context);
    }
    // Crear nuevas Views. Puedo crear diferentes layouts para diferentes adaptadores desde la misma clase
    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // New View creada
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.solicitudes_item, parent, false);
        return new MyViewHolder(v);
    }
    // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.MyViewHolder holder, final int position) {
        // - Obtener Elemento del data set en esta position
        // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
        TextView textViewHead = holder.listView.findViewById(R.id.textViewHead);
        textViewHead.setText(formListFiltered.get(position).get("codigo"));
        TextView codigo = holder.listView.findViewById(R.id.codigo);
        codigo.setText(formListFiltered.get(position).get("id_fiscal"));
        TextView nombre = holder.listView.findViewById(R.id.textViewDesc);
        nombre.setText(formListFiltered.get(position).get("nombre"));
        LinearLayout estado = (LinearLayout) holder.listView.findViewById(R.id.estado);
        TextView estado_text = (TextView) holder.listView.findViewById(R.id.estado_text);
        ImageView estado_circulo = holder.listView.findViewById(R.id.estado_circulo);
        ImageView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
        TextView tipo_solicitud = (TextView) holder.listView.findViewById(R.id.tipo_solicitud);
        TextView idform = (TextView) holder.listView.findViewById(R.id.idform);
        TextView fechas = (TextView) holder.listView.findViewById(R.id.fechas_text);

        Drawable background = estado.getBackground();
        Drawable background_circulo = estado_circulo.getBackground();
        int color = R.color.sinFormularios;

        if(formListFiltered.get(position).get("estado").trim().equals("Pendiente")){
            color = R.color.pendientes;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Incidencia")){
            color = R.color.devuelto;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Rechazado")){
            color = R.color.rechazado;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Aprobado")){
            color = R.color.aprobados;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Nuevo")){
            color = R.color.nuevo;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Transmitido")){
            color = R.color.transmitido;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Modificado")){
            color = R.color.modificado;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Cancelado")){
            color = R.color.black;
        }
        estado_text.setText(formListFiltered.get(position).get("estado").trim());
        estado_text.setTextColor(ContextCompat.getColor(context, color));
        estado.setBackground(ContextCompat.getDrawable(context, color));
        estado_circulo.getBackground().setTint(ContextCompat.getColor(context, color));
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

        codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModificarSolicitud(position);
            }
        });
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModificarSolicitud(position);
            }
        });
        textViewOptions.setOnClickListener(new View.OnClickListener() {
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
        });

            /*View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView codigo = v.findViewById(R.id.codigo);
                    Toast.makeText(getApplicationContext(), "Cliente "+codigo.getText()+" seleccionado",Toast.LENGTH_SHORT).show();
                }
            };
            holder.listView.setOnClickListener(mOnClickListener);*/
    }

    private void ModificarSolicitud(int position) {
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
        }else if(formListFiltered.get(position).get("ind_modelo").trim().equals("S")){
            intent = new Intent(context, SolicitudAvisosEquipoFrioActivity.class);
            intent.putExtras(b); //Pase el parametro el Intent
            context.startActivity(intent);
        }
    }

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
                    ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();
                    for (HashMap<String, String> row : mDataset) {
                        if (row.get("codigo") != null && row.get("codigo").trim().contains(charString)  )
                            filteredList.add(row);
                        else if (row.get("nombre") != null && row.get("nombre").toUpperCase().trim().contains(charString.toUpperCase()))
                            filteredList.add(row);
                        else if (row.get("numero") != null && row.get("numero").trim().contains(charString) ){
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
                formListFiltered = (ArrayList<HashMap<String, String>>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

    public Filter getMultiFilter() {
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
                        for (HashMap<String, String> row : mDataset) {
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
    }
}