package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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
            File myFile = new File(context.get().getDatabasePath("FAWM_ANDROID_2").getPath() );

            System.out.println("Creando Streams de datos...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            //Comando String que indicara que se quire realizar una Sincronizacion
            dos.writeUTF("Sincronizacion");
            dos.flush();
            //Enviar Ruta que se quiere sincronizar
            dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH",""));
            dos.flush();

            //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la sincronizacion
            long s = dis.readLong();
            if(s < 0){
                s = dis.readLong();
                byte[] e = new byte[124];
                dis.readFully(e);
                String error = new String(e);
                xceptionFlag = true;
                messageFlag = "Error: "+error;
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

                    dos.close();
                    //UNZIP informacion recibida
                    boolean unzip = FileHelper.unzip(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision/FAWM_ANDROID_2",externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision");
                    File file = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision/"+PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH","")+".db");
                    // File (or directory) with new name
                    File file2 = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision/FAWM_ANDROID_2");

                    if (file2.exists()) {
                        file2.delete();
                    }

                    // Rename file (or directory)
                    boolean success = file.renameTo(file2);

                    if (!success) {
                        xceptionFlag = true;
                        messageFlag = "No se pudo renombrar el archivo.";
                    }
                    if(unzip) {
                        //Pasar de la sincronizacion a caerle encima a la base de datos actual con la informacion recibida.
                        DataBaseHelper mDBHelper = new DataBaseHelper(context.get());
                        try {
                            mDBHelper.updateDataBase();
                            SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(mDBHelper.DB_PATH+"FAWM_ANDROID_2", null, SQLiteDatabase.OPEN_READWRITE);
                            //Copiar nuevamente los formularios que tenga nuevos
                            String sqlAttach = "ATTACH DATABASE '"+externalStoragePath + File.separator + context.get().getPackageName()+ File.separator+"FAWM_ANDROID_2_BACKUP' AS fromDB";
                            mDataBase.execSQL(sqlAttach);
                            String sqlInsert = "INSERT INTO FormHVKOF_solicitud SELECT * FROM fromDB.FormHVKOF_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);

                            sqlInsert = "INSERT INTO encuesta_solicitud SELECT * FROM fromDB.encuesta_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO encuesta_gec_solicitud SELECT * FROM fromDB.encuesta_gec_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO grid_contacto_solicitud SELECT * FROM fromDB.grid_contacto_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO grid_bancos_solicitud SELECT * FROM fromDB.grid_bancos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO grid_impuestos_solicitud SELECT * FROM fromDB.grid_impuestos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO grid_visitas_solicitud SELECT * FROM fromDB.grid_visitas_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO grid_interlocutor_solicitud SELECT * FROM fromDB.grid_interlocutor_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Corregido'))";
                            mDataBase.execSQL(sqlInsert);
                            sqlInsert = "INSERT INTO adjuntos_solicitud SELECT * FROM fromDB.adjuntos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo'))";
                            mDataBase.execSQL(sqlInsert);

                        } catch (IOException e) {
                            xceptionFlag = true;
                            messageFlag = "Error al actualizar la Base de Datos.";
                            e.printStackTrace();
                        }
                    }else{
                        xceptionFlag = true;
                        messageFlag = "Problemas al desempaquetar la informacion.";
                    }
                }
            }

        } catch (IOException e) {
            xceptionFlag = true;
            messageFlag = e.getMessage();
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
            messageFlag = e.getMessage();
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
            Toasty.error(context.get(),"Sincronizacion Fallida. "+messageFlag,Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
        if(dialog.isShowing()) {
            dialog.hide();
        }
    }
}