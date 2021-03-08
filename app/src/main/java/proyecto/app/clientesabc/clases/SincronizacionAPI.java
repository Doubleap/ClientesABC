package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import static androidx.core.content.ContextCompat.startActivity;

public class SincronizacionAPI extends AsyncTask<Void,String,Void> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    AlertDialog dialog;
    public String TAG = "APITAG";
    public SincronizacionAPI(WeakReference<Context> c, WeakReference<Activity> a){
        this.context = c;
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        publishProgress("Estableciendo comunicación...");
        System.out.println("Estableciendo comunicación para enviar archivos...");
        String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
        if(mensaje.equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String version = "";
            version = dateFormat.format(BuildConfig.BuildDate).replace(":","COLON").replace("-","HYPHEN");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://kofcrofcdesa02:90/MaestroClientes/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            retrofit.create(InterfaceApi.class);

            InterfaceApi sincronizacionService = retrofit.create(InterfaceApi.class);

            Call<ResponseBody> call = sincronizacionService.Sincronizacion(VariablesGlobales.getSociedad(), PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""), version);
            Response<ResponseBody> response;
            try {
                response = call.execute();
                if (!response.body().contentType().toString().equals("text/html")) {
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    File tranFileDir;
                    File externalStorage = Environment.getExternalStorageDirectory();
                    String externalStoragePath = externalStorage.getAbsolutePath();
                    tranFileDir = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision");
                    boolean ex = tranFileDir.mkdirs();

                    publishProgress("Descomprimiendo datos...");
                    //UNZIP informacion recibida
                    boolean unzip = FileHelper.unzip(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision/FAWM_ANDROID_2", externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision");
                    File file = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision/" + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", "") + ".db");


                    // File (or directory) with new name
                    File file2 = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision/FAWM_ANDROID_2");
                    if (file2.exists()) {
                        file2.delete();
                    }
                    //Rename file (or directory)
                    boolean success = file.renameTo(file2);

                    if (!success) {
                        xceptionFlag = true;
                        messageFlag = "No se pudo renombrar el archivo.";
                    }
                    if (unzip) {
                        publishProgress("Reemplazando Base de datos...");
                        DataBaseHelper mDBHelper = new DataBaseHelper(context.get());
                        try {
                            mDBHelper.updateDataBase();
                            if (PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", "").trim().equals(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("ultimaRutaSincronizada", "").trim())) {
                                publishProgress("Recuperando informacion...");
                                SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(mDBHelper.DB_PATH + "FAWM_ANDROID_2", null, SQLiteDatabase.OPEN_READWRITE);
                                //Copiar nuevamente los formularios que tenga nuevos
                                String sqlAttach = "ATTACH DATABASE '" + externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "FAWM_ANDROID_2_BACKUP' AS fromDB";
                                mDataBase.execSQL(sqlAttach);

                                //Validar que tenga tablas la base attachada
                                String sqlCountTables = "SELECT * FROM fromDB.sqlite_master WHERE type='table' AND name != 'android_metadata' AND name != 'sqlite_sequence'";
                                Cursor cursor = mDataBase.rawQuery(sqlCountTables,null);
                                if(cursor.getCount() > 0) {
                                    //Borrar Incidencias que fueron modificadas pero no han sido transmitidas, para no duplicar solicitudes con estados diferentes
                                    String sqlInsert = "DELETE FROM FormHVKOF_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM encuesta_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM encuesta_gec_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_contacto_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_bancos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_impuestos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_visitas_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_interlocutor_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM adjuntos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    //Borrar Incidencias de Tablas _old que fueron modificadas pero no han sido transmitidas, para no duplicar solicitudes con estados diferentes
                                    sqlInsert = "DELETE FROM FormHVKOF_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_contacto_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_bancos_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_impuestos_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_visitas_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    sqlInsert = "DELETE FROM grid_interlocutor_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Modificado'))";
                                    mDataBase.execSQL(sqlInsert);
                                    try {
                                        //Insertar registros del BACK UP realizado antes de sincornizar de la HH para no perder nuevos , modificados o incompletos
                                        sqlInsert = "INSERT INTO FormHVKOF_solicitud SELECT * FROM fromDB.FormHVKOF_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO encuesta_solicitud SELECT * FROM fromDB.encuesta_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO encuesta_gec_solicitud SELECT * FROM fromDB.encuesta_gec_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_contacto_solicitud SELECT * FROM fromDB.grid_contacto_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_bancos_solicitud SELECT * FROM fromDB.grid_bancos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_impuestos_solicitud SELECT * FROM fromDB.grid_impuestos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_visitas_solicitud SELECT * FROM fromDB.grid_visitas_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_interlocutor_solicitud SELECT * FROM fromDB.grid_interlocutor_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO adjuntos_solicitud SELECT * FROM fromDB.adjuntos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        //Insertar registros de Tablas _old del BACK UP realizado antes de sincornizar de la HH para no perder nuevos , modificados o incompletos
                                        sqlInsert = "INSERT INTO FormHVKOF_old_solicitud SELECT * FROM fromDB.FormHVKOF_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_contacto_old_solicitud SELECT * FROM fromDB.grid_contacto_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_bancos_old_solicitud SELECT * FROM fromDB.grid_bancos_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_impuestos_old_solicitud SELECT * FROM fromDB.grid_impuestos_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_visitas_old_solicitud SELECT * FROM fromDB.grid_visitas_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                        sqlInsert = "INSERT INTO grid_interlocutor_old_solicitud SELECT * FROM fromDB.grid_interlocutor_old_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Modificado','Incompleto'))";
                                        mDataBase.execSQL(sqlInsert);
                                    }catch (SQLiteException e) {
                                        xceptionFlag = true;
                                        messageFlag = "Bases de datos incompatibles. Intente de nuevo." + e.getMessage();
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (SQLiteException e) {
                            xceptionFlag = true;
                            messageFlag = "Error con Sqlite al actualizar la Base de Datos." + e.getMessage();
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }else {
                    xceptionFlag = true;
                    messageFlag = response.body().string();
                }
            } catch (IOException e) {
                xceptionFlag = true;
                messageFlag = e.getMessage();
                e.printStackTrace();
            }
        }else{
            xceptionFlag = true;
            messageFlag = mensaje;
        }
        publishProgress("Proceso Terminado...");

        Log.i("===end of start ====", "==");

        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        TextView v = (TextView) dialog.findViewById(R.id.mensaje_espera);
        v.setText(progress[0]);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setCancelable(false); // Si quiere que el usuario espere por el proceso completo por obligacion poner en false
        builder.setView(R.layout.layout_loading_dialog);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                messageFlag = "Proceso cancelado por el usuario.";
                cancel(true);
            }
        });
        dialog = builder.create();
        if(!activity.get().isFinishing()) {
            dialog.show();
        }
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (!xceptionFlag){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("ultimaSincronizacion", dateFormat.format(date)).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("ultimaRutaSincronizada", PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH","")).apply();
            GenerarNotificaciones();
            Toasty.success(context.get(),"Sincronizacion Exitosa!!",Toast.LENGTH_LONG).show();
        }
        else{
            Toasty.error(context.get(),"Sincronizacion Fallida. "+messageFlag,Toast.LENGTH_LONG).show();
        }
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }
        if(dialog.isShowing()) {
            dialog.hide();
        }
        Intent intent = activity.get().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.get().finish();
        activity.get().overridePendingTransition(0, 0);
        startActivity(context.get(), intent, null);
        activity.get().overridePendingTransition(0, 0);
    }

    private void GenerarNotificaciones() {
        DataBaseHelper db = new DataBaseHelper(context.get());
        ArrayList<HashMap<String, String>> clientList = db.getNotificaciones();
        Notificacion notificacion = new Notificacion(context.get());
        for(int x = 0; x < clientList.size(); x++){
            switch(clientList.get(x).get("estado")){
                case "Aprobado":
                    notificacion.crearNotificacion(Integer.parseInt(clientList.get(x).get("id").toString()),clientList.get(x).get("titulo").trim(), clientList.get(x).get("mensaje").trim(), R.drawable.logo_mc, R.drawable.icon_add_client, R.color.aprobados);
                    break;
                case "Rechazado":
                    notificacion.crearNotificacion(Integer.parseInt(clientList.get(x).get("id").toString()),clientList.get(x).get("titulo").trim(), clientList.get(x).get("mensaje").trim(), R.drawable.logo_mc, R.drawable.icon_close, R.color.rechazado);
                    break;
                case "Incidencia":
                    notificacion.crearNotificacion(Integer.parseInt(clientList.get(x).get("id").toString()),clientList.get(x).get("titulo").trim(), clientList.get(x).get("mensaje").trim(), R.drawable.logo_mc, R.drawable.icon_info_title, R.color.devuelto);
                    break;
                case "Actualizacion":
                    notificacion.crearNotificacion(Integer.parseInt(clientList.get(x).get("id").toString()),clientList.get(x).get("titulo").trim(), clientList.get(x).get("mensaje").trim(), R.drawable.logo_mc, R.drawable.icon_update, R.color.pendientes);
                    break;
                default:
                    notificacion.crearNotificacion(Integer.parseInt(clientList.get(x).get("id").toString()),clientList.get(x).get("titulo").trim(), clientList.get(x).get("mensaje").trim(), R.drawable.logo_mc, R.drawable.icon_about, R.color.nuevo);
            }
        }
    }

    public void EnableWiFi(){
        WifiManager wifimanager = (WifiManager) context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(true);
    }

    public void DisableWiFi(){
        WifiManager wifimanager = (WifiManager) context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(false);
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null) {
                String externalStoragePath = externalStorage.getAbsolutePath();
                File tranFileDir = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Transmision");
                boolean ex = tranFileDir.mkdirs();
                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    long fileSize = body.contentLength();
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(body.byteStream()));

                    byte[] r = new byte[(int) fileSize];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != fileSize) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "%");
                    }

                    File transferFile = new File(tranFileDir, "FAWM_ANDROID_2");
                    //outputStream = new FileOutputStream(transferFile);
                    OutputStream stream = new FileOutputStream(transferFile);
                    stream.write(r);
                    stream.flush();
                    stream.close();

                    return true;
                } catch (IOException e) {
                    return false;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}