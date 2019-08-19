package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Objects;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class PanelActivity extends AppCompatActivity {
    LinearLayout principal;
    GridLayout gridLayout;
    private Intent intent;
    private static DataBaseHelper mDBHelper;
    private static SQLiteDatabase mDb;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        principal = (LinearLayout)findViewById(R.id.principal);
        gridLayout = (GridLayout)findViewById(R.id.mainGrid);
        mDBHelper = new DataBaseHelper(this);
        mDb = mDBHelper.getWritableDatabase();

        spinner = new Spinner(this, Spinner.MODE_DIALOG);
        ArrayList<OpcionSpinner> listaopciones = mDBHelper.getDatosCatalogoParaSpinner("cat_T171T");

        // Creando el adaptador(opciones) para el comboBox deseado
        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<OpcionSpinner>(Objects.requireNonNull(getBaseContext()), R.layout.simple_spinner_item, listaopciones);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        Drawable d1 = getResources().getDrawable(R.drawable.spinner_background, null);
        spinner.setBackground(d1);
        spinner.setAdapter(dataAdapter);
        spinner.setVisibility(View.INVISIBLE);
        principal.addView(spinner);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_panel);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.action_camara:

                        return true;
                    case R.id.action_file:

                        return true;
                    case R.id.action_save:

                }
                return true;
            }
        });

        setSingleEvent(gridLayout);
        Drawable d = getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(d);
    }
    // we are setting onClickListener for each element
    private void setSingleEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch(finalI) {
                        case 0:
                            intent = new Intent(getBaseContext(),MantClienteActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            spinner.performClick();
                            break;
                        case 2:
                            intent = new Intent(getBaseContext(),MantClienteActivity.class);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(getBaseContext(),SolicitudesActivity.class);
                            startActivity(intent);
                            break;
                        case 4:
                            intent = new Intent(getBaseContext(),TCPActivity.class);
                            startActivity(intent);
                            break;
                        case 5:
                            intent = new Intent(getBaseContext(),FirmaActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
            });
        }
    }

    private void showDialogSolicitudes() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        /*MenuItem item = menu.add(Menu.NONE, 1, 1, "Texto");
        item.setIcon(R.drawable.icon_solicitud);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);*/
        getMenuInflater().inflate(R.menu.menu_min, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.solicitud:
                Bundle b = new Bundle();
                //TODO seleccionar el tipo de solicitud por el UI
                b.putString("tipoSolicitud", "1"); //id de solicitud

                intent = new Intent(this,SolicitudActivity.class);
                intent.putExtras(b); //Pase el parametro el Intent
                startActivity(intent);
                break;
            case R.id.comunicacion:
                intent = new Intent(this,TCPActivity.class);
                startActivity(intent);
                break;
            case R.id.clientes:
                intent = new Intent(this,MantClienteActivity.class);
                startActivity(intent);
                break;
            case R.id.solicitudes:
                intent = new Intent(this,SolicitudesActivity.class);
                startActivity(intent);
                break;
            case R.id.coordenadas:
                intent = new Intent(this,LocacionGPSActivity.class);
                startActivity(intent);
                break;
            case R.id.firma:
                intent = new Intent(this,FirmaActivity.class);
                startActivity(intent);
                break;
            case R.id.detalles:
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
