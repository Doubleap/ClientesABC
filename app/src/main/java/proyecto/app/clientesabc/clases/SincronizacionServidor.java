package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;

public class SincronizacionServidor extends AsyncTask<Void,Void,Void> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    AlertDialog dialog;
    public SincronizacionServidor(WeakReference<Context> c, WeakReference<Activity> a){
        this.context = c;
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer

        try {
            System.out.println("Estableciendo comunicación para enviar archivos...");
            socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip",""),Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto","")));
            //socket.setReuseAddress(true);

            // Enviar archivo en socket

            //File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
            File myFile = new File(context.get().getDatabasePath("FAWM_ANDROID_2").getPath() );

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
            dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH",""));
            dos.flush();

            //TODO Puedo recibir cualquier cosa de respuesta en el stream de la conexion del socket??
            long s = dis.readLong();
            if(s < 0){
                s = dis.readLong();
                byte[] e = new byte[124];
                dis.readFully(e);
                String error = new String(e);
                xceptionFlag = true;
                messageFlag = error;
            }else {
                byte[] r = new byte[(int) s];
                dis.readFully(r);

                File tranFileDir;
                File externalStorage = Environment.getExternalStorageDirectory();
                if (externalStorage != null) {
                    String externalStoragePath = externalStorage.getAbsolutePath();
                    tranFileDir = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision");
                    boolean ex = tranFileDir.mkdirs();
                    File transferFile = new File(tranFileDir, "FAWM_ANDROID_2");
                    OutputStream stream = new FileOutputStream(transferFile);
                    stream.write(r);
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
                    xceptionFlag = true;
                    messageFlag = "Error al actualizar la Base de Datos.";
                    e.printStackTrace();
                }
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
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setCancelable(true); // Si quiere que el usuario espere por el proceso completo por obligacion
        builder.setView(R.layout.layout_loading_dialog);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                messageFlag = "Proceso cancelado por el usuario.";
                cancel(true);
            }
        });
        dialog = builder.create();
        dialog.show();
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
        dialog.dismiss();
        if(dialog.isShowing()) {
            dialog.hide();
        }
    }
}