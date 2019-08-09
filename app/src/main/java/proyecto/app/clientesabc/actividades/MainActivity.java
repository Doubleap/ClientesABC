package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;


public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener{
    EditText name, loc, desig;
    Button saveBtn;
    Intent intent;
    Spinner spinner;
    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    ArrayList<EditText> listaCamposDinamicos = new ArrayList<>();
    Map<String, EditText> mapeoCamposDinamicos = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.txtName);
        loc = findViewById(R.id.txtLocation);
        desig = findViewById(R.id.txtDesignation);
        saveBtn = findViewById(R.id.btnSave);
        spinner = findViewById(R.id.spinner);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);
        //File existente = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
        //File existente = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
        deleteFile("FAWM_ANDROID_2");

        mDBHelper = new DataBaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditText et = new EditText(this); // this refers to the activity or the context.
        // Atributos del Texto a crear
        //et.setText("Campo Dinamico");
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setAllCaps(true);

        LinearLayout ll = findViewById(R.id.LinearLayoutMain);

        mapeoCamposDinamicos.put("Campo1",et);
        listaCamposDinamicos.add(et);
        ll.addView(et);

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            Toasty.error(this,mSQLException.getMessage()).show();
        }

        spinner.setOnItemSelectedListener(this);
        // Loading spinner data from database
        loadSpinnerData2();

        //Setear Eventos de Elementos
        /*saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = name.getText().toString()+"\n";
                String location = loc.getText().toString();
                //String designation = desig.getText().toString();
                String designation = mapeoCamposDinamicos.get("Campo1").getText().toString();
                DbHandler dbHandler = new DbHandler(MainActivity.this);
                dbHandler.insertUserDetails(username,location,designation);
                intent = new Intent(MainActivity.this,DetallesActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Registro insertado con éxito",Toast.LENGTH_SHORT).show();
            }
        });*/

        //View title = getWindow().findViewById(android.R.id.title);
        //View titleBar = (View) title.getParent();
        //titleBar.setBackground(gd);
        Drawable d=getResources().getDrawable(R.drawable.botella_coca_header_der,null);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(d);

    }

    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData() {
        /*DbHandler db = new DbHandler(MainActivity.this);
        List<String> labels = db.getAllRutasPreventa();
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, labels);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);*/
    }
    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData2() {
        /*DataBaseHelper db = new DataBaseHelper(MainActivity.this);
        List<String> labels = db.getDatosCatalogo("t_i_users");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, labels);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);*/
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Ha seleccionado: " + label,
                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.item1:
                //your action
                break;
            case R.id.item2:
                //your action
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}