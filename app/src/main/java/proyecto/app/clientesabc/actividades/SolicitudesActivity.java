package proyecto.app.clientesabc.actividades;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.MyAdapter;


public class SolicitudesActivity extends AppCompatActivity {
    DataBaseHelper db;
    private SearchView searchView;
    private MyAdapter mAdapter;
    private FloatingActionButton fab;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    boolean isFABOpen = false;
    String estado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);
        Bundle b = getIntent().getExtras();
        if(b != null)
            estado = b.getString("estado");
        db = new DataBaseHelper(this);
        ArrayList<HashMap<String, String>> formList;
        if(estado != null)
            formList = db.getSolicitudes(estado);
        else
            formList = db.getSolicitudes();
        RecyclerView rv = findViewById(R.id.user_list);

        mAdapter = new MyAdapter(formList,this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

        fab = findViewById(R.id.fabBtn);
        fab1 = findViewById(R.id.filterBtn);
        fab2 = findViewById(R.id.addBtn);
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
                Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud

                Intent intent = new Intent(view.getContext(),SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFilters(view);
            }
        });
    }

    private void showDialogFilters(View view) {
        final Dialog dialog =new Dialog(view.getContext());
        dialog.setContentView(R.layout.filtros_solicitudes_dialog_layout);
        dialog.show();

        MultiSpinnerSearch estadoSpinner = (MultiSpinnerSearch)dialog.findViewById(R.id.estadoSpinner);
        MultiSpinnerSearch tipoSolicitudSpinner = (MultiSpinnerSearch)dialog.findViewById(R.id.tipoSolicitudSpinner);
        //Spinner 1
        final List<KeyPairBoolData> list = db.getEstadosCatalogoParaMultiSpinner();
        estadoSpinner.setItems(list,-1,  new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        Toasty.info(getApplicationContext(), i + " : "+ items.get(i).getName());
                    }
                }
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
                for(int i=0; i<items.size(); i++) {
                    if(items.get(i).isSelected()) {
                        Toasty.info(getApplicationContext(), i + " : "+ items.get(i).getName());
                    }
                }
            }
        });
        tipoSolicitudSpinner.setBackground(d1);
        tipoSolicitudSpinner.setColorSeparation(true);
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab1.animate().translationY((float)-120.0);
        fab2.animate().translationY((float)-240.0);
        //fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }
    private void closeFABMenu(){
        isFABOpen=false;
        fab.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        //fab3.animate().translationY(0);
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
        }
        return true;
    }
}
