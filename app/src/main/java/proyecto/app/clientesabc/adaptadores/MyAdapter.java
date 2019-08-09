package proyecto.app.clientesabc.adaptadores;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import proyecto.app.clientesabc.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {
    private ArrayList<HashMap<String, String>> mDataset;
    private ArrayList<HashMap<String, String>> formListFiltered;
    private Context context;
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
    public MyAdapter(ArrayList<HashMap<String, String>> myDataset,Context c) {
        mDataset = myDataset;
        formListFiltered = mDataset;
        context = c;
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
    public void onBindViewHolder(@NonNull final MyAdapter.MyViewHolder holder, int position) {
        // - Obtener Elemento del data set en esta position
        // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
        TextView codigo = holder.listView.findViewById(R.id.textViewHead);
        codigo.setText(formListFiltered.get(position).get("codigo"));
        TextView nombre = holder.listView.findViewById(R.id.textViewDesc);
        nombre.setText(formListFiltered.get(position).get("nombre"));
        TextView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
        ImageView estado = holder.listView.findViewById(R.id.estado);
        Drawable d = context.getResources().getDrawable(R.drawable.circulo_status_cliente, null);

        Drawable background = estado.getBackground();
        int color = R.color.sinFormularios;
        if(formListFiltered.get(position).get("estado").trim().equals("Pendiente")){
            color = R.color.pendientes;
        }
        if(formListFiltered.get(position).get("estado").trim().equals("Devuelto")){
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
        if (background instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, color));
        } else if (background instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(context, color));
        } else if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(context, color));
        }

        codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Pantalla de visualizacion de datos del cliente, la informacion debe ser mapeada desde la tabla de clientes de HH (Dependiente de los datos que existan ahi para mostrar)
                Toast.makeText(context, "Cliente codigo clickeado:"+((TextView)v).getText(),Toast.LENGTH_SHORT).show();
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
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                //handle menu1 click
                                break;
                            case R.id.menu2:
                                //handle menu2 click
                                break;
                            case R.id.menu3:
                                //handle menu3 click
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
                        if (row.get("codigo").trim().contains(charString) || row.get("nombre").toUpperCase().trim().contains(charString.toUpperCase()) || row.get("numero").trim().contains(charString)) {
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