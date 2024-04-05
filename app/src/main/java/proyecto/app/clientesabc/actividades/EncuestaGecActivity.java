package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.EncuestaGecAdapter;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.PreguntasEncuesta;

public class EncuestaGecActivity extends AppCompatActivity{
    DataBaseHelper db;
    public static SQLiteDatabase mDb;
    private static EncuestaGecAdapter mAdapter;
    String codigo_cliente;
    String nombre_cliente;
    String canal_cliente;
    String correo_cliente;
    String tipo_encuesta;
    List<PreguntasEncuesta> preguntas;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            codigo_cliente = b.getString("codigo_cliente");
            nombre_cliente = b.getString("nombre_cliente");
            canal_cliente = b.getString("canal_cliente");
            correo_cliente = b.getString("correo_cliente");
            tipo_encuesta = b.getString("tipo_encuesta");
        }
        db = new DataBaseHelper(this);

        db = new DataBaseHelper(this);
        mDb = db.getWritableDatabase();
        preguntas = db.getPreguntasEncuesta();


        setContentView(R.layout.encuesta_gec_layout);
        //setContentView(R.layout.activity_base_instalada);
        rv = findViewById(R.id.recycler_view);

        mAdapter = new EncuestaGecAdapter(preguntas,this, EncuestaGecActivity.this,canal_cliente,correo_cliente,nombre_cliente);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));





        TextView cliente =  findViewById(R.id.cliente);
        TextView title =  findViewById(R.id.title);
        cliente.setText(codigo_cliente +" - "+nombre_cliente);
        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarRespuestas(v.getContext());
            }
        });






    }
    @Override
    protected  void onResume(){
        super.onResume();

        preguntas = db.getPreguntasEncuesta();
        RecyclerView rv = findViewById(R.id.recycler_view);

        mAdapter = new EncuestaGecAdapter(preguntas,this, EncuestaGecActivity.this,canal_cliente,correo_cliente,nombre_cliente);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(mAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this.getBaseContext(), DividerItemDecoration.VERTICAL));

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        Bundle b = null;
        if (data != null)
            b = data.getExtras();
        if (b != null) {
            //campoEscaneo = b.getString("campoEscaneo");
            if (b.getInt("requestCode") != 0)
                requestCode = b.getInt("requestCode");
        }

        if (requestCode == VariablesGlobales.ESCANEO_EQUIPO_FRIO) {
            if (resultCode == RESULT_OK) {
                if (b != null) {
                    //Se verifica el codigo leida y se pueden dar las siguientes situaciones:
                    //1. El codigo del equipo frio si existe en el cliente, simplemente se marca como censado
                    //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                    //3. El codigo del equipo leida esta en otro cliente
                    //4. Hay un equipo que no puede ser censado pero si esta en la lista del cliente(NO tiene placa, NO esta en sitio, no existe), Se debe poder indicar que el equipo no pudo ser censado y ver que estado ponerle

                    //Caso 1. El codigo del equipo frio si exsite en el cliente, simplemente se marca como censado con un nuevo regsitro en CensoEquipoFrio
                    if (db.ExisteEquipoFrioEnCliente(codigo_cliente, b.getString("codigo"))) {
                        EquipoFrio eq = db.getEquipoFrioDB(codigo_cliente, b.getString("codigo"), false);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Verificado");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",eq.getSerge());
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("num_activo", eq.getSernr());
                        insertValues.put("num_equipo", eq.getEqunr());
                        insertValues.put("modelo_equipo", eq.getMatnr());
                        insertValues.put("correo", correo_cliente);
                        insertValues.put("canal", canal_cliente);
                        insertValues.put("fuente", "Escaner");
                        insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("userMC",""));


                    }else
                    //2. El codigo del equipo no existe en sistema, se debe agregar a la lista de censados como HALLAZGO o anomalía
                    if (!db.ExisteEquipoFrio(b.getString("codigo"))) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Hallazgo");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",b.getString("codigo").trim());
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("correo", correo_cliente);
                        insertValues.put("canal", canal_cliente);
                        insertValues.put("comentario","Número de placa no aparece en ningun cliente instalado.");
                        insertValues.put("fuente", "Escaner");
                    }//3. El codigo del equipo leida esta en otro cliente
                    else if (db.ExisteEquipoFrio(b.getString("codigo"))) {
                        EquipoFrio eq = db.getEquipoFrioDatosCenso(b.getString("codigo"));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        ContentValues insertValues = new ContentValues();
                        insertValues.put("bukrs", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_BUKRS",""));
                        insertValues.put("bzirk", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_BZIRK",""));
                        insertValues.put("ruta", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("W_CTE_RUTAHH",""));
                        insertValues.put("estado","Hallazgo");
                        insertValues.put("kunnr_censo",codigo_cliente);
                        insertValues.put("nombre_cliente", nombre_cliente);
                        insertValues.put("num_placa",eq.getSerge());
                        insertValues.put("activo", "1");
                        insertValues.put("transmitido", "0");
                        insertValues.put("fecha_lectura", dateFormat.format(date));
                        insertValues.put("num_activo", eq.getSernr());
                        insertValues.put("num_equipo", eq.getEqunr());
                        insertValues.put("modelo_equipo", eq.getMatnr());
                        insertValues.put("correo", correo_cliente);
                        insertValues.put("canal", canal_cliente);
                        insertValues.put("creado_por", PreferenceManager.getDefaultSharedPreferences(EncuestaGecActivity.this).getString("userMC",""));
                        insertValues.put("comentario","Pertenece a otro cliente "+eq.getKunnr()+"!");
                        insertValues.put("fuente","Escaner");
                    }
                }
            }
        }

    }

    private static Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }

    private boolean validarRespuestas(Context context){
        boolean valido=true;
        for (int i=0; i<mAdapter.getItemCount(); i++){


        RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(i);

            switch (viewHolder.getItemViewType()){
                case 1:
                    EncuestaGecAdapter.TextoHolder textoHolder = (EncuestaGecAdapter.TextoHolder) viewHolder;

                    EditText editText = textoHolder.listView.findViewById(R.id.spinner);

                    if(editText.getText().toString().isEmpty()){
                        Toasty.error(context,"TEST CAMPO").show();
                        valido=false;
                    }
                    break;
                case 2:
                    EncuestaGecAdapter.SeleccionHolder seleccionHolder = (EncuestaGecAdapter.SeleccionHolder) viewHolder;

                    EditText editTextSeleccion = seleccionHolder.listView.findViewById(R.id.spinner);

                    if(editTextSeleccion.getText().toString().isEmpty()){
                        Toasty.error(context,"TEST CAMPO").show();
                        valido=false;
                    }
                    break;
            }

        }
        return valido;
    }
}
