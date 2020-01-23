package proyecto.app.clientesabc.actividades;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.MyAdapter;
import proyecto.app.clientesabc.clases.MovableFloatingActionButton;


public class SolicitudesActivity extends AppCompatActivity {
    DataBaseHelper db;
    private SearchView searchView;
    private MyAdapter mAdapter;
    private MovableFloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    boolean isFABOpen = false;
    String estado;
    String tipform;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            estado = b.getString("estado");
            tipform = b.getString("tipform");
        }
        db = new DataBaseHelper(this);
        ArrayList<HashMap<String, String>> formList;
        if(estado != null && tipform != null)
            formList = db.getSolicitudes(estado,tipform);
        else if(estado != null)
            formList = db.getSolicitudes(estado,null);
        else if(tipform != null)
            formList = db.getSolicitudes(null,tipform);
        else
            formList = db.getSolicitudes();
        RecyclerView rv = findViewById(R.id.user_list);

        mAdapter = new MyAdapter(formList,this,SolicitudesActivity.this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        fab = findViewById(R.id.fabBtn);
        fab1 = findViewById(R.id.filterBtn);
        fab2 = findViewById(R.id.addBtn);
        fab1.hide();fab2.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFilters(view);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
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

        Drawable d = getResources().getDrawable(R.drawable.header_curved_cc5,null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Mis Solicitudes");
        /*if(estado != null && tipform != null)
            toolbar.setSubtitle("Filtro: "+estado+" / "+tipform);
        else if(estado != null)
            toolbar.setSubtitle("Filtro: "+estado);
        else if(tipform != null)
            toolbar.setSubtitle("Filtro: "+tipform);*/
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorTextView,null));
        toolbar.setBackground(d);


        if (Build.VERSION.SDK_INT >= 28) {
            toolbar.setOutlineAmbientShadowColor(getResources().getColor(R.color.aprobados,null));
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void showDialogFilters(View view) {
        final Dialog dialog =new Dialog(view.getContext());
        dialog.setContentView(R.layout.filtros_solicitudes_dialog_layout);
        dialog.show();

        final MultiSpinnerSearch estadoSpinner = (MultiSpinnerSearch)dialog.findViewById(R.id.estadoSpinner);
        final MultiSpinnerSearch tipoSolicitudSpinner = (MultiSpinnerSearch)dialog.findViewById(R.id.tipoSolicitudSpinner);
        Button btnFiltro = (Button) dialog.findViewById(R.id.saveBtn);
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filtroEstado = "";
                String filtroForm = "";

            }
        });
        //Spinner 1
        final List<KeyPairBoolData> list = db.getEstadosCatalogoParaMultiSpinner();
        estadoSpinner.setItems(list,-1,  new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                String multiFiltro = "";
                String coma = "";
                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        multiFiltro += coma+items.get(i).getId();
                        coma = ",";
                       //Toasty.info(getApplicationContext(), i + " : "+ items.get(i).getName()).show();
                    }
                }
                mAdapter.getMultiFilter().filter(multiFiltro);
            }
        });
        Drawable d1 = getResources().getDrawable(R.drawable.spinner_background, null);
        estadoSpinner.setBackground(d1);
        estadoSpinner.setColorSeparation(true);
        //Spinner 2
        final List<KeyPairBoolData> list2 = db.getTiposFormularioParaMultiSpinner();
        tipoSolicitudSpinner.setItems(list2,-1,  new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                String multiFiltro = "";
                String coma = "";
                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        multiFiltro += coma+items.get(i).getId();
                        coma = ",";
                        //Toasty.info(getApplicationContext(), i + " : "+ items.get(i).getName()).show();
                    }
                }
                mAdapter.getMultiFilter().filter(multiFiltro);
            }
        });
        tipoSolicitudSpinner.setBackground(d1);
        tipoSolicitudSpinner.setColorSeparation(true);
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.show();fab2.show();
        fab1.animate().translationY((float)-120.0);
        fab2.animate().translationY((float)-240.0);

        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }
    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab1.hide();fab2.hide();
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
            searchView.setQueryHint("Busqueda");

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
}
