package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.R;


public class DetallesActivity extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);
        DataBaseHelper db = new DataBaseHelper(this);
        ArrayList<HashMap<String, String>> clientList = db.getClientes();
        RecyclerView rv = findViewById(R.id.user_list);

        MyAdapter mAdapter = new MyAdapter(clientList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Un SnackBar", Snackbar.LENGTH_LONG)
                        .setAction("Accion", null).show();
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<HashMap<String, String>> mDataset;
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
        private MyAdapter(ArrayList<HashMap<String, String>> myDataset) {
            mDataset = myDataset;
        }

        // Crear nuevas Views
        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // New View creada
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mant_clientes_item, parent, false);
            return new MyViewHolder(v);
        }

        // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // - Obtener Elemento del data set en esta position
            // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
            TextView codigo = holder.listView.findViewById(R.id.codigo);
            codigo.setText(mDataset.get(position).get("codigo"));
            TextView nombre = holder.listView.findViewById(R.id.nombre);
            nombre.setText(mDataset.get(position).get("nombre"));
            TextView direccion = holder.listView.findViewById(R.id.direccion);
            direccion.setText(mDataset.get(position).get("direccion"));
            ImageView estado = holder.listView.findViewById(R.id.estado);
            Drawable d = getResources().getDrawable(R.drawable.circulo_status_cliente, null);

            Drawable background = estado.getBackground();
            if (background instanceof ShapeDrawable) {
                ShapeDrawable shapeDrawable = (ShapeDrawable) background;
                shapeDrawable.getPaint().setColor(ContextCompat.getColor(getBaseContext(), R.color.black));
            } else if (background instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) background;
                gradientDrawable.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorAccent));
            } else if (background instanceof ColorDrawable) {
                ColorDrawable colorDrawable = (ColorDrawable) background;
                colorDrawable.setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
            }

            View.OnClickListener mOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView codigo = v.findViewById(R.id.codigo);
                    Toast.makeText(getApplicationContext(), "Cliente "+codigo.getText()+" seleccionado",Toast.LENGTH_SHORT).show();
                }
            };
            holder.listView.setOnClickListener(mOnClickListener);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

}


