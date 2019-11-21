package proyecto.app.clientesabc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.HashMap;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.actividades.SolicitudActivity;
import proyecto.app.clientesabc.actividades.SolicitudModificacionActivity;
import proyecto.app.clientesabc.clases.TransmisionServidor;

import static android.support.v4.content.ContextCompat.startActivity;

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

        holder.listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Bundle b = new Bundle();
                Intent intent;
                b.putString("idSolicitud", formListFiltered.get(position).get("id_solicitud").trim()); //id de solicitud
                if(formListFiltered.get(position).get("tipform").trim().equals("1") || formListFiltered.get(position).get("tipform").trim().equals("6")) {
                    intent = new Intent(context, SolicitudActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    context.startActivity(intent);
                }else{
                    intent = new Intent(context, SolicitudModificacionActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    context.startActivity(intent);
                }*/
            }
        });
        codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent;
                b.putString("idSolicitud", formListFiltered.get(position).get("id_solicitud").trim()); //id de solicitud
                if(formListFiltered.get(position).get("tipform").trim().equals("1") || formListFiltered.get(position).get("tipform").trim().equals("6")) {
                    intent = new Intent(context, SolicitudActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    context.startActivity(intent);
                }else{
                    intent = new Intent(context, SolicitudModificacionActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    context.startActivity(intent);
                }
            }
        });
        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                Intent intent;
                b.putString("idSolicitud", formListFiltered.get(position).get("id_solicitud").trim()); //id de solicitud
                if(formListFiltered.get(position).get("tipform").trim().equals("1") || formListFiltered.get(position).get("tipform").trim().equals("6")) {
                    intent = new Intent(context, SolicitudActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    context.startActivity(intent);
                }else{
                    intent = new Intent(context, SolicitudModificacionActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    context.startActivity(intent);
                }
            }
        });
        textViewOptions.setOnClickListener(new View.OnClickListener() {
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
                        Bundle b = new Bundle();
                        Intent intent = null;
                        switch (item.getItemId()) {
                            case R.id.detalle:
                                //TODO enviar el tipo de formulario tambien a la solicitud
                                //b.putString("tipoSolicitud", "1"); //id de solicitud
                                b.putString("idSolicitud", formListFiltered.get(position).get("id_solicitud").trim()); //id de solicitud
                                if(formListFiltered.get(position).get("tipform").trim().equals("1") || formListFiltered.get(position).get("tipform").trim().equals("6")) {
                                    intent = new Intent(context, SolicitudActivity.class);
                                    intent.putExtras(b); //Pase el parametro el Intent
                                    context.startActivity(intent);
                                }else{
                                    intent = new Intent(context, SolicitudModificacionActivity.class);
                                    intent.putExtras(b); //Pase el parametro el Intent
                                    context.startActivity(intent);
                                }
                                break;
                            case R.id.modificar:
                                b.putString("idSolicitud", formListFiltered.get(position).get("id_solicitud").trim()); //id de solicitud
                                if(formListFiltered.get(position).get("tipform").trim().equals("1") || formListFiltered.get(position).get("tipform").trim().equals("6")) {
                                    intent = new Intent(context, SolicitudActivity.class);
                                    intent.putExtras(b); //Pase el parametro el Intent
                                    context.startActivity(intent);
                                }else{
                                    intent = new Intent(context, SolicitudModificacionActivity.class);
                                    intent.putExtras(b); //Pase el parametro el Intent
                                    context.startActivity(intent);
                                }
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
                                TransmisionServidor f = new TransmisionServidor(weakRef, weakRefA, "", "", formListFiltered.get(position).get("id_solicitud").trim());
                                f.execute();
                                break;
                        }
                        return false;
                    }
                });
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