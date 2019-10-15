package proyecto.app.clientesabc.actividades;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;


public class MantClienteActivity extends AppCompatActivity {
    Intent intent;
    private SearchView searchView;
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);
        DataBaseHelper db = new DataBaseHelper(this);
        ArrayList<HashMap<String, String>> clientList = db.getClientes();
        RecyclerView rv = findViewById(R.id.user_list);

        mAdapter = new MyAdapter(clientList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud
                Intent intent = new Intent(view.getContext(),SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);
            }
        });

        /**/
        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Mis Clientes");
        //toolbar.setSubtitle("");
        toolbar.setBackground(d);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white,null));
        if (Build.VERSION.SDK_INT >= 28) {
            toolbar.setOutlineAmbientShadowColor(getResources().getColor(R.color.aprobados,null));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white,null));
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawer.closeDrawers();

                switch(menuItem.getItemId()) {
                    case R.id.solicitud:
                        Bundle b = new Bundle();
                        //TODO seleccionar el tipo de solicitud por el UI
                        b.putString("tipoSolicitud", "1"); //id de solicitud
                        intent = new Intent(getBaseContext(),SolicitudActivity.class);
                        intent.putExtras(b); //Pase el parametro el Intent
                        startActivity(intent);
                        break;
                    case R.id.comunicacion:
                        intent = new Intent(getBaseContext(),TCPActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.clientes:
                        intent = new Intent(getBaseContext(),MantClienteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.solicitudes:
                        intent = new Intent(getBaseContext(),SolicitudesActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.coordenadas:
                        intent = new Intent(getBaseContext(),LocacionGPSActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.firma:
                        intent = new Intent(getBaseContext(),FirmaActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.detalles:
                        intent = new Intent(getBaseContext(), PanelActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        Toasty.info(getBaseContext(),"Opcion no encontrada!").show();
                }

                /*Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud

                intent = new Intent(getBaseContext(),SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);*/
                return false;
            }
        });
        /**/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null) {
            if (searchManager != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setQueryHint("Buscar...");

            // listener de buscar query text change
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    mAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    // filter recycler view when text is changed
                    mAdapter.getFilter().filter(query);
                    return false;
                }
            });
            searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            TextView textView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            ImageView searchBtn = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
            ImageView searchCloseBtn = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            textView.setTextColor(getResources().getColor(R.color.white,null));
            searchBtn.setColorFilter(getResources().getColor(R.color.white,null));
            searchCloseBtn.setColorFilter(getResources().getColor(R.color.white,null));
        }
        return true;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>  implements Filterable {
        private ArrayList<HashMap<String, String>> mDataset;
        private ArrayList<HashMap<String, String>> formListFiltered;
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
            formListFiltered = mDataset;
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
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            // - Obtener Elemento del data set en esta position
            // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
            TextView codigo = holder.listView.findViewById(R.id.textViewHead);
            codigo.setText(formListFiltered.get(position).get("codigo") == null?"":formListFiltered.get(position).get("codigo").trim());
            TextView nombre = holder.listView.findViewById(R.id.textViewDesc);
            nombre.setText(formListFiltered.get(position).get("nombre") == null?"":formListFiltered.get(position).get("nombre").trim());
            TextView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
            TextView idfiscal = holder.listView.findViewById(R.id.idfiscal);
            idfiscal.setText(formListFiltered.get(position).get("idfiscal") == null?"":formListFiltered.get(position).get("idfiscal").trim());
            TextView correo = holder.listView.findViewById(R.id.correo);
            correo.setText(formListFiltered.get(position).get("correo") == null?"":formListFiltered.get(position).get("correo").trim());
            /*TextView ubicacion = holder.listView.findViewById(R.id.ubicacion);
            ubicacion.setText(formListFiltered.get(position).get("ubicacion"));
            TextView direccion = holder.listView.findViewById(R.id.direccion);
            direccion.setText(formListFiltered.get(position).get("direccion"));*/
            LinearLayout color_gec = holder.listView.findViewById(R.id.color_gec);
            Drawable d = getResources().getDrawable(R.drawable.circulo_status_cliente, null);

            Drawable background = color_gec.getBackground();
            int color = R.color.sinFormularios;
            String klabc = formListFiltered.get(position).get("klabc").toString();

            switch(klabc) {
                case "00":
                    color = R.color.baja;break;
                case "50":
                    color = R.color.diamante;break;
                case "51":
                    color = R.color.oro;break;
                case "52":
                    color = R.color.plata;break;
                case "53":
                    color = R.color.bronce;break;
                case "54":
                    color = R.color.indirectos;break;
                case "55":
                    color = R.color.orovending;break;
                case "56":
                    color = R.color.platavending;break;
                case "57":
                    color = R.color.broncevending;break;
                case "58":
                    color = R.color.laton;break;
                case "59":
                    color = R.color.plataplus;break;
                case "99":
                    color = R.color.customizado;break;
                case "SA":
                    color = R.color.sinasignar;break;
            }
            color_gec.setBackground(ContextCompat.getDrawable(getBaseContext(), color));


            codigo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putString("idCliente", ((TextView)v).getText().toString()); //id de solicitud
                    Intent intent = new Intent(v.getContext(),ClienteActivity.class);
                    intent.putExtras(b); //Pase el parametro el Intent
                    startActivity(intent);
                }
            });
            final String codigoCliente = codigo.getText().toString().trim();
            textViewOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    Context wrapper = new ContextThemeWrapper(getApplication(), R.style.MyPopupMenu);

                    //creating a popup menu
                    PopupMenu popup = new PopupMenu(wrapper, holder.listView);
                    popup.setGravity(Gravity.END);

                    //inflating menu from xml resource
                    popup.inflate(R.menu.mant_clientes_item_menu);
                    //adding click listener

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.detalle:
                                    Bundle b = new Bundle();
                                    b.putString("idCliente", codigoCliente); //id de solicitud
                                    Intent intent = new Intent(getBaseContext(),ClienteActivity.class);
                                    intent.putExtras(b); //Pase el parametro el Intent
                                    startActivity(intent);
                                    break;
                                case R.id.modificar:
                                    //handle menu2 click
                                    break;
                                case R.id.cierre:
                                    //handle menu3 click
                                    break;
                                case R.id.equipofrio:
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
                            if (row.get("codigo").trim().contains(charString) || row.get("nombre").toUpperCase().trim().contains(charString.toUpperCase()) || row.get("direccion").trim().contains(charString)) {
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

}
