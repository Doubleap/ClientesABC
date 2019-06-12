package proyecto.app.clientesabc.Actividades;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import proyecto.app.clientesabc.Adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.Adaptadores.MyAdapter;
import proyecto.app.clientesabc.R;


public class SolicitudesActivity extends AppCompatActivity {
    private SearchView searchView;
    private MyAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle);
        DataBaseHelper db = new DataBaseHelper(this);
        ArrayList<HashMap<String, String>> formList = db.getSolicitudes();
        RecyclerView rv = findViewById(R.id.user_list);

        mAdapter = new MyAdapter(formList,this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Buscar...");

        // listening to search query text change
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
        return true;
    }
}
