package proyecto.app.clientesabc.Actividades;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import proyecto.app.clientesabc.R;

public class ComunicacionActivity extends AppCompatActivity {

    private EditText m_ipaddressTextbox = null;
    private Socket m_socket = null;

    private SensorManager m_sensorManger = null;
    private Sensor m_sensor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comunicacion);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        m_ipaddressTextbox = (EditText)findViewById(R.id.txtservidor);
        ConnectToServer();
    }
    public void ConnectToServer()
    {
        String FILE_TO_SEND = "c:/temp/source.pdf";  // you may change this
        //String iptext = m_ipaddressTextbox.getText().toString().split(":")[0];
        String iptext = "192.168.0.10";
        InetAddress ip = null;
        int port = 0;
        try
        {
            ip = InetAddress.getByName(iptext);
            port = 17503;
            //port = Integer.parseInt(m_ipaddressTextbox.getText().toString().split(":")[1]);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        Socket sock = null;
        try
        {
            System.out.println("Estableciendo comunicación...");
            m_socket = new Socket(iptext, port);
            // Enviar archivo en socket
            File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
            byte [] mybytearray  = new byte [(int)myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            os = m_socket.getOutputStream();
            System.out.println("Enviando " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            System.out.println("Finalizado.");
            m_socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
