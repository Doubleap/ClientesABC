package proyecto.app.clientesabc.actividades;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;


public class TipoSolicitudPanelActivity extends AppCompatActivity {
    Intent intent;
    private SearchView searchView;
    private TipoSolicitudAdapter mAdapter;
    private DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_solicitud_panel);
        db = new DataBaseHelper(this);
        ArrayList<HashMap<String, String>> clientList = db.getTipoSolicitudPanel();
        RecyclerView rv = findViewById(R.id.tipform_list);

        mAdapter = new TipoSolicitudAdapter(clientList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //intent = new Intent(getBaseContext(),PanelActivity.class);
                //startActivity(intent);
            }
        });

        /**/
        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Monitor Solicitudes");
        //toolbar.setSubtitle("");
        toolbar.setBackground(d);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.black,null));
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
                        if(!VariablesGlobales.UsarAPI()) {
                            intent = new Intent(getBaseContext(), TCPActivity.class);
                        }else{
                            intent = new Intent(getBaseContext(), APIConfigActivity.class);
                        }
                        startActivity(intent);
                        break;
                    case R.id.clientes:
                        intent = new Intent(getBaseContext(),TipoSolicitudPanelActivity.class);
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
            searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            TextView textView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            ImageView searchBtn = searchView.findViewById(androidx.appcompat.R.id.search_button);
            ImageView searchCloseBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
            textView.setTextColor(getResources().getColor(R.color.white,null));
            searchBtn.setColorFilter(getResources().getColor(R.color.white,null));
            searchCloseBtn.setColorFilter(getResources().getColor(R.color.white,null));
        }
        return true;
    }

    public class TipoSolicitudAdapter extends RecyclerView.Adapter<TipoSolicitudAdapter.MyViewHolder>  implements Filterable {
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
        private TipoSolicitudAdapter(ArrayList<HashMap<String, String>> myDataset) {
            mDataset = myDataset;
            formListFiltered = mDataset;
        }

        // Crear nuevas Views
        @NonNull
        @Override
        public TipoSolicitudAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // New View creada
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tipo_solicitud_panel_item, parent, false);
            return new MyViewHolder(v);
        }

        // Reemplazar el contenido del View. Para ListView se llama solo, pero para RecyclerView hay que llamar al setLayoutManager
        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            // - Obtener Elemento del data set en esta position
            // - Reemplazar aqui cualquier contenido dinamico dependiendo de algun valor de l dataset creado y o el contenido del dataset
            TextView codigo = holder.listView.findViewById(R.id.textViewHead);
            ImageView textViewOptions = holder.listView.findViewById(R.id.textViewOptions);
            codigo.setText(formListFiltered.get(position).get("descripcion") == null?"":formListFiltered.get(position).get("descripcion").trim());

            TextView num_nuevos = holder.listView.findViewById(R.id.txt_nuevos);
            TextView num_pendientes = holder.listView.findViewById(R.id.txt_pendientes);
            TextView num_incidencias = holder.listView.findViewById(R.id.txt_incidencias);
            TextView num_aprobados = holder.listView.findViewById(R.id.txt_aprobados);
            TextView num_rechazados = holder.listView.findViewById(R.id.txt_rechazados);
            TextView num_incompletos = holder.listView.findViewById(R.id.txt_incompletos);
            TextView num_modificados = holder.listView.findViewById(R.id.txt_modificados);
            TextView num_total = holder.listView.findViewById(R.id.txt_total);
            num_nuevos.setText(formListFiltered.get(position).get("nuevos").trim());
            num_pendientes.setText(formListFiltered.get(position).get("pendientes").trim());
            num_incidencias.setText(formListFiltered.get(position).get("incidencias").trim());
            num_aprobados.setText(formListFiltered.get(position).get("aprobados").trim());
            num_rechazados.setText(formListFiltered.get(position).get("rechazados").trim());
            num_incompletos.setText(formListFiltered.get(position).get("incompletos").trim());
            num_modificados.setText(formListFiltered.get(position).get("modificados").trim());
            num_total.setText(" ("+formListFiltered.get(position).get("total").trim()+")");

            num_total.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes(null, formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_nuevos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Nuevo", formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_pendientes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Pendiente", formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_incidencias.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Incidencia", formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_aprobados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Aprobado", formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_rechazados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Rechazado", formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_incompletos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Incompleto", formListFiltered.get(position).get("tipform").trim());
                }
            });
            num_modificados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VerSolicitudes("Modificado", formListFiltered.get(position).get("tipform").trim());
                }
            });

            textViewOptions.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("RestrictedApi")
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

                                    break;
                                case R.id.modificar:
                                    //showDialogFormulariosModificacion(codigoCliente,false, false);
                                    Toasty.info(getBaseContext(),"Funcionalidad de Modificaciones NO disponible de momento.").show();
                                    break;
                                case R.id.cierre:
                                    /*Bundle bc = new Bundle();
                                    bc.putString("tipoSolicitud", "5"); //id de solicitud
                                    bc.putString("codigoCliente", codigoCliente);
                                    intent = new Intent(getApplicationContext(),SolicitudModificacionActivity.class);
                                    intent.putExtras(bc); //Pase el parametro el Intent
                                    startActivity(intent);*/
                                    Toasty.info(getBaseContext(),"Funcionalidad de Cierre NO disponible de momento.").show();
                                    break;
                                case R.id.credito:
                                    //showDialogFormulariosModificacion(codigoCliente,true, false);
                                    Toasty.info(getBaseContext(),"Funcionalidad de Credito NO disponible de momento.").show();
                                    break;
                                case R.id.equipofrio:
                                    //showDialogFormulariosModificacion(codigoCliente,false, true);
                                    Toasty.info(getBaseContext(),"Funcionalidad de Avisos de equipo frio NO disponible de momento.").show();
                                    break;
                                case R.id.comollegar:

                                    break;

                            }
                            return false;
                        }
                    });

                    //MenuPopupHelper menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) popup.getMenu(), holder.listView);
                    //menuHelper.setForceShowIcon(true);
                    //menuHelper.show();
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
                            if (row.get("descripcion").trim().contains(charString)) {
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

    public void VerSolicitudes() {
        intent = new Intent(this, SolicitudesActivity.class);
        startActivity(intent);
    }
    public void VerSolicitudes(String estado, String tipform) {
        Bundle b = new Bundle();
        if(estado != null)
            b.putString("estado", estado.trim()); //estado solicitud
        if(tipform != null)
            b.putString("tipform", tipform.trim()); //tipo de formulario
        intent = new Intent(this, SolicitudesActivity.class);
        intent.putExtras(b); //Pase el parametro el Intent
        startActivity(intent);
    }

}
