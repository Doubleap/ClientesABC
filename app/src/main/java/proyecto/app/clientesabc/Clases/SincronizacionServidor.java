package proyecto.app.clientesabc.Clases;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import proyecto.app.clientesabc.VariablesGlobales;

public class SincronizacionServidor extends AsyncTask<Void,Void,Void> {
    private Context context;
    private Activity activity;
    private boolean xceptionFlag = false;
    private ServerSocket ss;
    private Socket socket;

    public SincronizacionServidor(Context c, Activity a){
        this.context = c;
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer

        try {
            System.out.println("Estableciendo comunicación para enviar archivos...");
            socket = new Socket(VariablesGlobales.getIpcon(),VariablesGlobales.getPuertocon());
            //socket.setReuseAddress(true);

            // Enviar archivo en socket
            File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
            //files.add(myFile);
            //files.add(myFile);

            System.out.println("Creando Streams de datos...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            //System.out.println(files.size());

            //Comando String que indicara que se queire realizar Sincronizacion/Transmision
            dos.writeUTF("Sincronizacion");
            dos.flush();
            //TODO Puedo recibir cualquier cosa de respuesta en el stream de la conexion del socket??
            int s = dis.readInt();
            byte[] respuesta = new byte[s];

            dis.read(respuesta,0,s);

            File tranFileDir = null;
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null) {
                String externalStoragePath = externalStorage.getAbsolutePath();
                tranFileDir = new File(externalStoragePath + File.separator + context.getPackageName()+ File.separator+"Transmision");
                boolean ex = tranFileDir.mkdirs();
                File transferFile = new File(tranFileDir,"SINC_FAWM_ANDROID");
                FileOutputStream stream = new FileOutputStream(transferFile);
                stream.write(respuesta);

                stream.flush();
                stream.close();
            }
            //O cerrarlo aqui estara bien?
            dos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            xceptionFlag = true;
            e.printStackTrace();
        }

        Log.i("===end of start ====", "==");
        try{
            if(socket!=null && !socket.isClosed()){
                socket.close();
                Log.i("Socket cerrado", "==");
            }
        }
        catch (Exception e){
            xceptionFlag = true;
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!xceptionFlag){
            Toast.makeText(context,"Sincronizacion Exitosa!!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context,"Sincronizacion Fallida.",Toast.LENGTH_LONG).show();
        }

    }
}