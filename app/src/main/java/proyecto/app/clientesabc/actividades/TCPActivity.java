package proyecto.app.clientesabc.actividades;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import proyecto.app.clientesabc.clases.SincronizacionServidor;
import proyecto.app.clientesabc.clases.TransmisionServidor;
import proyecto.app.clientesabc.R;

public class TCPActivity extends Activity
{
    private Button serverTransmitButton;
    private Button clientReceiveButton;
    private Button serverUDPButton;
    private Button clientUDPButton;
    private int PICKFILE_REQUEST_CODE = 100;
    private String filePath="";
    private String wholePath="";
    private Button changeName;
    private String m_Text = "";

    private int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_NETWORK_STATE},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }

        changeName = findViewById(R.id.change);


        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        // TCP
        serverTransmitButton = findViewById(R.id.button_TCP_server);
        serverTransmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Start Server Clicked", "yipee");
                //Realizar la transmision de lo que se necesita (Db o txt)
                WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                TransmisionServidor f = new TransmisionServidor(weakRef,weakRefA,filePath,wholePath);
                f.execute();
            }
        });


        clientReceiveButton = findViewById(R.id.button_TCP_client);
        clientReceiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Read Button Clicked", "yipee");
                //startService(new Intent(TCPActivity.this, NameService.class));
                WeakReference<Context> weakRef = new WeakReference<Context>(TCPActivity.this);
                WeakReference<Activity> weakRefA = new WeakReference<Activity>(TCPActivity.this);
                SincronizacionServidor s = new SincronizacionServidor(weakRef,weakRefA);
                s.execute();

            }
        });
    }

    private void showInputDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("name", m_Text).apply();
                Toast.makeText(TCPActivity.this,m_Text,Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

}
