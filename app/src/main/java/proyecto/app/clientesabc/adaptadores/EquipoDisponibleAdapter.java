package proyecto.app.clientesabc.adaptadores;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.clases.AdjuntoAPI;
import proyecto.app.clientesabc.clases.ImagenServidor;

public class EquipoDisponibleAdapter extends RecyclerView.Adapter<EquipoDisponibleAdapter.MyViewHolder> implements Filterable {
    private ArrayList<HashMap<String, String>> mDataset;
    private ArrayList<HashMap<String, String>> formListFiltered;
    private Context context;
    private Activity activity;
    private DataBaseHelper db;
    private static SQLiteDatabase mDb;
    private double latitude = 0.0;
    private double longitude = 0.0;
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
    public EquipoDisponibleAdapter(ArrayList<HashMap<String, String>> myDataset, Context c, Activity a) {
        mDataset = myDataset;
        formListFiltered = mDataset;
        context = c;
        activity = a;
        db = new DataBaseHelper(context);
        mDb = db.getWritableDatabase();
    }
    // Crear nuevas Views. Puedo crear diferentes layouts para diferentes adaptadores desde la misma clase
    @NonNull
    @Override
    public EquipoDisponibleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // New View creada
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipo_disponible_item, parent, false);
        return new MyViewHolder(v);
    }
    // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
    @Override
    public void onBindViewHolder(@NonNull final EquipoDisponibleAdapter.MyViewHolder holder, final int position) {
        // - Obtener Elemento del data set en esta position
        // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
        TextView material = holder.listView.findViewById(R.id.material);
        TextView modelo = holder.listView.findViewById(R.id.modelo);
        TextView disponibilidad = holder.listView.findViewById(R.id.disponibilidad);
        TextView centro_suministro = holder.listView.findViewById(R.id.centro_suministro);
        TextView num_puertas = holder.listView.findViewById(R.id.num_puertas);
        TextView estado_text = (TextView) holder.listView.findViewById(R.id.estado_text);
        LinearLayout estado = (LinearLayout) holder.listView.findViewById(R.id.color_estado);
        TextView emplazamiento = (TextView) holder.listView.findViewById(R.id.emplazamiento);

        ImageView ver_detalle = holder.listView.findViewById(R.id.ver_detalle);

        CardView card_view = (CardView) holder.listView.findViewById(R.id.encuesta);

        material.setText("");
        modelo.setText("");
        disponibilidad.setText("");
        centro_suministro.setText("");
        num_puertas.setText("");
        estado_text.setText("");
        emplazamiento.setText("");

        if(formListFiltered.get(position).get("material") != null) {
            material.setText(formListFiltered.get(position).get("material").replaceFirst("^0+", ""));
            modelo.setText(formListFiltered.get(position).get("modelo"));
        }else {
            material.setText(formListFiltered.get(position).get("modelo").replaceFirst("^0+", ""));
            modelo.setText("");
        }

        disponibilidad.setText("Stock: "+formListFiltered.get(position).get("stock") +" - Reservados: "+formListFiltered.get(position).get("reservado")+" - Disponible: "+(Integer.parseInt(formListFiltered.get(position).get("stock").toString()) - Integer.parseInt(formListFiltered.get(position).get("reservado").toString())));
        centro_suministro.setText(formListFiltered.get(position).get("centro_suministro") +" - "+formListFiltered.get(position).get("desc_centro_suministro"));
        num_puertas.setText(formListFiltered.get(position).get("num_puertas") +" Puerta(s)");
        estado_text.setText("Estado: "+formListFiltered.get(position).get("estado"));
        emplazamiento.setText("Emplazamiento: "+formListFiltered.get(position).get("emplazamiento"));

        Drawable background = estado.getBackground();
        //Drawable background_circulo = estado_circulo.getBackground();
        int color = R.color.sinFormularios;

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

        int color2 = R.color.aprobados;

        ColorStateList colorStateList2 = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}, // checked
                        new int[]{android.R.attr.state_enabled}, // enabled
                        new int[]{-android.R.attr.state_enabled}, // enabled
                },
                new int[]{
                        Color.parseColor(context.getResources().getString(color2)),
                        Color.parseColor(context.getResources().getString(color2)),
                        Color.parseColor(context.getResources().getString(color2)),
                        Color.parseColor(context.getResources().getString(color2)),
                }
        );

        //estado_text.setTextColor(ContextCompat.getColor(context, color));
        if( (Integer.parseInt(formListFiltered.get(position).get("stock").toString()) - Integer.parseInt(formListFiltered.get(position).get("reservado").toString())) > 0)
            estado.setBackground(ContextCompat.getDrawable(context, color2));
        else
            estado.setBackground(ContextCompat.getDrawable(context, color));

        modelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ModificarSolicitud(position);
            }
        });

        ver_detalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String material = formListFiltered.get(position).get("material") == null?formListFiltered.get(position).get("modelo"):formListFiltered.get(position).get("material");
                String nombreImagen = db.getNombreImagenModelo(material,formListFiltered.get(position).get("modelo"));
                mostrarImagenServidor(v.getContext(), activity, nombreImagen);
            }
        });
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
                        if (row.get("modelo") != null && row.get("modelo").trim().toLowerCase().contains(charString.toLowerCase())  )
                            filteredList.add(row);
                        else if (row.get("estado") != null && row.get("estado").trim().toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(row);
                        else if (row.get("centro_suministro") != null && row.get("centro_suministro").trim().toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(row);
                        else if (row.get("emplazamiento") != null && row.get("emplazamiento").trim().toLowerCase().contains(charString.toLowerCase()))
                            filteredList.add(row);
                        else if (row.get("num_puertas") != null && row.get("num_puertas").trim().toLowerCase().concat(" puerta").contains(charString.toLowerCase()) ){
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
                            toolbar.setTitle("Equipo Frio Disponible x Modelo ("+formListFiltered.size()+" de "+mDataset.size()+")");
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
    public static void mostrarImagenServidor(Context context, Activity activity, String nombreImagen) {
        final Dialog d = new Dialog(context, R.style.MyAlertDialogThemeAttachment);
        d.setContentView(R.layout.adjunto_layout_zoom);
        ImageView adjunto_img = d.findViewById(R.id.imagen);
        TextView adjunto_txt = d.findViewById(R.id.nombre);
        adjunto_txt.setText(nombreImagen);
        //adjunto_txt.setText(adjunto.getName());
        //if(!adjunto.getName().toLowerCase().contains(".pdf")) {
            //SHOW DIALOG
            d.show();
            Window window = d.getWindow();
            if (window != null) {
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        //}

        WeakReference<Context> weakRefs = new WeakReference<Context>(context);
        WeakReference<Activity> weakRefAs = new WeakReference<Activity>(activity);
        //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("api")) {
            AdjuntoAPI s = new AdjuntoAPI(weakRefs, weakRefAs, adjunto_img, adjunto_txt, d);
            if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
                s.EnableWiFi();
            }
            s.execute();
        } else {
            ImagenServidor s = new ImagenServidor(weakRefs, weakRefAs, adjunto_img, adjunto_txt);
            if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
                s.EnableWiFi();
            }else{
                s.DisableWiFi();
            }
            s.execute();
        }

    }
}