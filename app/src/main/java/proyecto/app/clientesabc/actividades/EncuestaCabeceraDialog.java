package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.adaptadores.EncuestaCabeceraAdapter;
import proyecto.app.clientesabc.modelos.EncuestaCabecera;

public class EncuestaCabeceraDialog extends DialogFragment {
    DataBaseHelper db;
    public static SQLiteDatabase mDb;
    private static EncuestaCabeceraAdapter mAdapter;
    List<EncuestaCabecera> encuestas;
    MantClienteActivity parentActivity;
    RecyclerView rv;
    String codigo_cliente;
    String nombre_cliente;
    String tipo_encuesta;
    String valor_gvc;

    public EncuestaCabeceraDialog() {


    }

    public EncuestaCabeceraDialog(@NonNull Context context,Activity activity,List<EncuestaCabecera> pencuestaCabecera) {
        this.encuestas=pencuestaCabecera;
        this.parentActivity = (MantClienteActivity)activity;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        View view
                = LayoutInflater.from(getContext())
                .inflate(R.layout.encuesta_cabecera_dialog_layout, null);
        Bundle args = getArguments();
        codigo_cliente = args.getString("codigo_cliente");
        nombre_cliente = args.getString("nombre_cliente");
        tipo_encuesta = args.getString("tipo_encuesta");

        Dialog myDialog = new Dialog(getContext());



        // Use the LayoutInflater to inflate the
        // dialog_list layout file into a View object

        TextView cliente_encuesta =  view.findViewById(R.id.cliente_encuesta);
        cliente_encuesta.setText(codigo_cliente +" - "+nombre_cliente);
        // Set the dialog's content view
        // to the newly created View object
        myDialog.setContentView(view);

        // Allow the dialog to be dismissed
        // by touching outside of it
        myDialog.setCanceledOnTouchOutside(true);

        // Allow the dialog to be canceled
        // by pressing the back button
        myDialog.setCancelable(true);

        // Set up the RecyclerView in the dialog
        setUpRecyclerView(view);

        return myDialog;
    }
    @Override
    public void onResume() {

        super.onResume();

        View view
                = LayoutInflater.from(getContext())
                .inflate(R.layout.encuesta_cabecera_dialog_layout, null);
        Bundle args = getArguments();
        codigo_cliente = args.getString("codigo_cliente");
        nombre_cliente = args.getString("nombre_cliente");
        tipo_encuesta = args.getString("tipo_encuesta");

        Dialog myDialog = new Dialog(getContext());

        // Use the LayoutInflater to inflate the
        // dialog_list layout file into a View object

        TextView cliente_encuesta =  view.findViewById(R.id.cliente_encuesta);
        cliente_encuesta.setText(codigo_cliente +" - "+nombre_cliente);
        // Set the dialog's content view
        // to the newly created View object
        myDialog.setContentView(view);

        // Allow the dialog to be dismissed
        // by touching outside of it
        myDialog.setCanceledOnTouchOutside(true);

        // Allow the dialog to be canceled
        // by pressing the back button
        myDialog.setCancelable(true);

        // Set up the RecyclerView in the dialog
        setUpRecyclerView(view);
    }

    // This method sets up the RecyclerView in the dialog
    private void setUpRecyclerView(View view)
    {
        // Find the RecyclerView in the layout file and set
        // its layout manager to a LinearLayoutManager
        RecyclerView recyclerView
                = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        // Create a new instance of the EmployeeAdapter
        // and set it as the RecyclerView's adapter
        mAdapter = new EncuestaCabeceraAdapter(encuestas,getContext(),codigo_cliente,nombre_cliente,tipo_encuesta);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }



}
