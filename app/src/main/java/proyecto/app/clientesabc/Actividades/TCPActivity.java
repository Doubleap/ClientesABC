package proyecto.app.clientesabc.Actividades;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import proyecto.app.clientesabc.Clases.NameService;
import proyecto.app.clientesabc.Clases.SincronizacionServidor;
import proyecto.app.clientesabc.Clases.TransmisionServidor;
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

        changeName = (Button)findViewById(R.id.change);


        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        // TCP
        serverTransmitButton = (Button) findViewById(R.id.button_TCP_server);
        serverTransmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Start Server Clicked", "yipee");
                //Realizar la transmision de lo que se necesita (Db o txt)
                TransmisionServidor f = new TransmisionServidor(TCPActivity.this,TCPActivity.this,filePath,wholePath);
                f.execute();
            }
        });


        clientReceiveButton = (Button) findViewById(R.id.button_TCP_client);
        clientReceiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Read Button Clicked", "yipee");
                startService(new Intent(TCPActivity.this, NameService.class));
                SincronizacionServidor s = new SincronizacionServidor(TCPActivity.this,TCPActivity.this);
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
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                PreferenceManager.getDefaultSharedPreferences(TCPActivity.this).edit().putString("name", m_Text).commit();
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
