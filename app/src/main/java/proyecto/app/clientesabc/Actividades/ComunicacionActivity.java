package proyecto.app.clientesabc.Actividades;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import es.dmoral.toasty.Toasty;
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
        new doitAsync().execute();
    }

    class doitAsync extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected void onPostExecute(Integer result) {
            if (result == -1) {
                Toasty.error(ComunicacionActivity.this,"Termino con error en postexecute").show();
                //System.exit(0);
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String FILE_TO_SEND = "c:/temp/ErroresCargaMasiva_13022018.txt";  // you may change this
            //String iptext = m_ipaddressTextbox.getText().toString().split(":")[0];
            String iptext = "192.168.0.13";
            InetAddress ip = null;
            int port = 0;
            try
            {
                ip = InetAddress.getByName(iptext);
                port = 3345;
                //port = Integer.parseInt(m_ipaddressTextbox.getText().toString().split(":")[1]);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                return -1;
            }

            FileInputStream fis = null;
            BufferedInputStream bis = null;
            OutputStream os = null;
            Socket sock = null;
            try
            {
                System.out.println("Estableciendo comunicación...");
                //Toasty.info(ComunicacionActivity.this,"Estableciendo Comunicacion...").show();
                m_socket = new Socket(iptext, port);
                // Enviar archivo en socket
                File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
                byte [] mybytearray  = new byte [(int)myFile.length()];
                fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                bis.read(mybytearray,0,mybytearray.length);
                os = m_socket.getOutputStream();
                System.out.println("Enviando " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
                //Toasty.info(ComunicacionActivity.this,"Enviando " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)").show();
                os.write(mybytearray,0,mybytearray.length);
                os.flush();
                //Toasty.success(ComunicacionActivity.this,"Envio Finalizado!").show();
                m_socket.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return -1;
            }
            return 0;
        }
    }
    public void ConnectToServer()
    {
        String FILE_TO_SEND = "c:/temp/ErroresCargaMasiva_13022018.txt";  // you may change this
        //String iptext = m_ipaddressTextbox.getText().toString().split(":")[0];
        String iptext = "192.168.0.13";
        InetAddress ip = null;
        int port = 0;
        try
        {
            ip = InetAddress.getByName(iptext);
            port = 3345;
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
            Toasty.info(ComunicacionActivity.this,"Estableciendo Comunicacion...").show();
            m_socket = new Socket(iptext, port);
            // Enviar archivo en socket
            File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
            byte [] mybytearray  = new byte [(int)myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            os = m_socket.getOutputStream();
            System.out.println("Enviando " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
            Toasty.info(ComunicacionActivity.this,"Enviando " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)").show();
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            Toasty.success(ComunicacionActivity.this,"Envio Finalizado!").show();
            m_socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
