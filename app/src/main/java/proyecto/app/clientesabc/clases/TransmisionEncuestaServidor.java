package proyecto.app.clientesabc.clases;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.BaseInstaladaActivity;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.modelos.EquipoFrio;

public class TransmisionEncuestaServidor extends AsyncTask<Void,String,Void> {

    private String solicitudes_procesadas;
    private WeakReference<ListView> listView;
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String GUID;
    private boolean xceptionFlag = false;
    private String errorFlag = "Transmision NO pudo realizarse.";
    private Socket socket;
    private String hostName,canonicalHostname;
    private String givenName;
    private DataBaseHelper mDBHelper;
    private boolean pendientes = false;
    AlertDialog dialog;

    public TransmisionEncuestaServidor(WeakReference<Context> context, WeakReference<Activity> act, String GUID){
        this.context = context;
        this.activity = act;
        this.GUID = GUID;
        mDBHelper = new DataBaseHelper(this.context.get());
    }

    public TransmisionEncuestaServidor(WeakReference<Context> context, WeakReference<Activity> act, String GUID, boolean pendientes){
        this.context = context;
        this.activity = act;
        this.GUID = GUID;
        this.pendientes = pendientes;
        mDBHelper = new DataBaseHelper(this.context.get());
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        TextView v = (TextView) dialog.findViewById(R.id.mensaje_espera);
        v.setText(progress[0]);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        System.out.println("ArrayList de Archivos");
        ArrayList<File> files = new ArrayList<>();
        errorFlag = "Transmision NO pudo realizarse.";

        try {
                publishProgress("Estableciendo comunicación...");
                String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
                if(mensaje.equals("")) {
                    socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip", "").trim(), Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto", "").trim()));
                    SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(context.get().getDatabasePath("TRANSMISION_ENCUESTA_").getPath() + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""), null, SQLiteDatabase.CREATE_IF_NECESSARY);

                    //Crear una base de datos solo para los datos que deben ser transmitidos
                    publishProgress("Extrayendo datos...");
                    String sqlDrop = "DROP TABLE IF EXISTS 'respuesta_pregunta';";
                    String sqlAttach = "ATTACH DATABASE '"+context.get().getDatabasePath("FAWM_ANDROID_2").getPath()+"' AS fromDB";
                    String[] droptables = sqlDrop.split(";");
                    for (String query : droptables) {
                        mDataBase.execSQL(query);
                    }
                    mDataBase.execSQL(sqlAttach);

                    //Tabla de respuesta_pregunta
                    String sqlCreate = "";
                    if(pendientes){
                        sqlCreate = "CREATE TABLE respuesta_pregunta AS SELECT * FROM fromDB.respuesta_pregunta WHERE estado = 'pendiente'";
                    }else{
                        sqlCreate = "CREATE TABLE respuesta_pregunta AS SELECT * FROM fromDB.respuesta_pregunta WHERE GUID = '"+GUID+"'";
                    }

                    mDataBase.execSQL(sqlCreate);

                    publishProgress("Empaquetando datos...");
                    String pathFileToZip = context.get().getApplicationInfo().dataDir + "/databases/";
                    FileHelper.zip(pathFileToZip, context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_ENCUESTA_" + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", "") + ".zip", false);
                    File myFile = new File(context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_ENCUESTA_" + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", "") + ".zip");

                    files.add(myFile);

                    System.out.println("Creando Streams de datos...");
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    System.out.println(files.size());

                    publishProgress("Enviando datos...");
                    //Enviar Pais de procedencia
                    dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()));
                    dos.flush();
                    //Version con la que quiere transmitir
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));
                    dos.writeUTF(dateFormat.format(BuildConfig.BuildDate));
                    dos.flush();
                    //Enviar Ruta que se quiere sincronizar
                    dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""));
                    dos.flush();
                    //Comando String que indicara que se queire realizar Sincronizacion/Transmision
                    dos.writeUTF("TransmisionEncuesta");
                    dos.flush();


                    //cantidad de archivos a enviar al servidor
                    dos.writeInt(files.size());
                    //dos.flush();

                    //Escribir tamanos de los archivos
                    for (int i = 0; i < files.size(); i++) {
                        int file_size = Integer.parseInt(String.valueOf(files.get(i).length()));
                        dos.writeLong(file_size);
                        dos.flush();
                    }
                    //escribir nombres de archivos
                    for (int i = 0; i < files.size(); i++) {
                        dos.writeUTF(files.get(i).getName());
                        dos.flush();
                    }

                    //buffer para la escritura del archivo
                    int n;
                    byte[] buf = new byte[1024];
                    //outer loop, executes one for each file
                    for (int i = 0; i < files.size(); i++) {
                        System.out.println(files.get(i).getName());
                        //crear nueva fileinputstream para cada archivo
                        FileInputStream fis = new FileInputStream(files.get(i));
                        //escribir el archivo al dos
                        while ((n = fis.read(buf)) != -1) {
                            dos.write(buf, 0, n);
                            dos.flush();
                        }
                        //deberia cerrar el dataoutputstream aqui y hacer una nueva cada vez???
                        System.out.println("Termino Iteracion : " + i + ".");
                    }

                    dos.writeUTF("FIN");
                    dos.flush();
                    //dialog.setCancelable(false);
                    publishProgress("Esperando respuesta...");
                    //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la sincronizacion
                    long s = dis.readLong();
                    if (s < 0) {
                        publishProgress("Error Transmision...");
                        s = dis.readLong();
                        byte[] e = new byte[(int) s];
                        dis.readFully(e);
                        String error = new String(e);
                        xceptionFlag = true;
                        errorFlag = "Transmisión Fallida. Error: " + error;
                    } else {
                        publishProgress("Respuesta recibida...");
                        byte[] r = new byte[(int) s];
                        int offset = 0;
                        int bytesRead;
                        while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != s) {
                            offset += bytesRead;
                            publishProgress("Descargando..." + String.format("%.02f", (100f / (s / 1024f)) * (offset / 1024f)) + "%");
                        }
                        dos.writeUTF("END");
                        dos.flush();
                        publishProgress("Actualizando respuestas...");
                        //solicitudes_procesadas = new String(r, Charset.defaultCharset());
                    }

                    //O cerrarlo aqui estara bien?
                    dos.close();
                }else{
                    xceptionFlag = true;
                    errorFlag = mensaje;
                }
            publishProgress("Transmision encuesta Finalizada...");
        } catch (IOException e) {
            xceptionFlag = true;
            errorFlag = e.getMessage();
            e.printStackTrace();
        }

        Log.i("===end of start ====", "==");
        try{
            if(socket!=null && !socket.isClosed()){
                socket.close();
            }
        }
        catch (Exception e){
            xceptionFlag = true;
            errorFlag = e.getMessage();
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setCancelable(false);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                errorFlag = "Proceso cancelado por el usuario.";
                cancel(false);
            }
        });
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
        if(!activity.get().isFinishing()) {
            dialog.show();
        }

    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(xceptionFlag){
            Toasty.error(context.get(),errorFlag,Toast.LENGTH_LONG).show();
            Toasty.warning(context.get(),"Encuesta no se envio!",Toast.LENGTH_LONG).show();
            //Adicionalmente se debe actualizar el estado de las solicitudes enviadas para que no se dupliquen.
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("estado","pendiente");
            long update = db.update("respuesta_pregunta",values,"GUID = ?",new String[]{GUID});
            if(update <= 0){
                Toasty.success(context.get(),"No se actualizo el estado de la encuesta!",Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toasty.success(context.get(),"Encuesta exitosa!",Toast.LENGTH_LONG).show();
            //Adicionalmente se debe actualizar el estado de las solicitudes enviadas para que no se dupliquen.
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("estado","enviado");
            long update = 0;
            if(pendientes){
                update = db.update("respuesta_pregunta",values,"estado = ?",new String[]{"pendiente"});
            }else{
                update = db.update("respuesta_pregunta",values,"GUID = ?",new String[]{GUID});
            }

            if(update <= 0){
                Toasty.success(context.get(),"No se actualizo el estado de la encuesta!",Toast.LENGTH_LONG).show();
            }

        }
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }
        if(dialog.isShowing())
            dialog.hide();
        //activity.get().recreate();
        if(!pendientes){
            if(!activity.get().isFinishing()) {
                Intent intent = activity.get().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.get().finish();
            }
        }else{
            activity.get().recreate();
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
}