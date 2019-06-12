package proyecto.app.clientesabc.Actividades;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import proyecto.app.clientesabc.Adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.R;


public class MantClienteActivity extends AppCompatActivity {
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
            public MyViewHolder(View v) {
                super(v);
                listView = v;
            }
        }

        // Constructor de Adaptador HashMap
        public MyAdapter(ArrayList<HashMap<String, String>> myDataset) {
            mDataset = myDataset;
        }

        // Crear nuevas Views
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // New View creada
            View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.mant_clientes_item, parent, false);

            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            // - Obtener Elemento del data set en esta position
            // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
            TextView codigo = holder.listView.findViewById(R.id.textViewHead);
            codigo.setText(mDataset.get(position).get("codigo"));
            TextView nombre = holder.listView.findViewById(R.id.textViewDesc);
            nombre.setText(mDataset.get(position).get("nombre"));
            TextView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
            ImageView estado = (ImageView) holder.listView.findViewById(R.id.estado);
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

            codigo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Cliente codigo clickeado:"+((TextView)v).getText(),Toast.LENGTH_SHORT).show();
                }
            });
            textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Context wrapper = new ContextThemeWrapper(getApplication(), R.style.MyPopupMenu);

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(wrapper, holder.listView);
                    popup.setGravity(Gravity.RIGHT);

                    //inflating menu from xml resource
                    popup.inflate(R.menu.mant_clientes_item_menu);
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
            return mDataset.size();
        }
    }

}
