package proyecto.app.clientesabc.clases;

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
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.VariablesGlobales;

public class SincronizacionServidor extends AsyncTask<Void,Void,Void> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;

    public SincronizacionServidor(WeakReference<Context> c, WeakReference<Activity> a){
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

            //File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
            File myFile = new File(context.get().getFilesDir().getPath(), "FAWM_ANDROID_2");

            //files.add(myFile);
            //files.add(myFile);

            System.out.println("Creando Streams de datos...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            //System.out.println(files.size());

            //Comando String que indicara que se queire realizar Sincronizacion/Transmision
            dos.writeUTF("Sincronizacion");
            dos.flush();
            //Enviar Ruta que se quiere sincronizar
            dos.writeUTF(VariablesGlobales.getRutaPreventa());
            dos.flush();

            //TODO Puedo recibir cualquier cosa de respuesta en el stream de la conexion del socket??
            long s = dis.readLong();
            byte[] respuesta = new byte[(int)s];

            dis.readFully(respuesta);
            //dis.read(respuesta,0,(int)s);

            File tranFileDir;
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null) {
                String externalStoragePath = externalStorage.getAbsolutePath();
                tranFileDir = new File(externalStoragePath + File.separator + context.get().getPackageName()+ File.separator+"Transmision");
                boolean ex = tranFileDir.mkdirs();
                File transferFile = new File(tranFileDir,"FAWM_ANDROID_2");
                OutputStream stream = new FileOutputStream(transferFile);
                stream.write(respuesta);
                stream.flush();
                stream.close();
            }
            //O cerrarlo aqui estara bien?
            dos.close();

            //Pasar de la sincronizacion a caerle encima a la base de datos actual
            DataBaseHelper mDBHelper = new DataBaseHelper(context.get());
            try {
                mDBHelper.updateDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            Toasty.success(context.get(),"Sincronizacion Exitosa!!",Toast.LENGTH_LONG).show();
        }
        else{
            Toasty.error(context.get(),"Sincronizacion Fallida."+messageFlag,Toast.LENGTH_LONG).show();
        }

    }
}