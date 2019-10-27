package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;

import static android.support.v4.content.ContextCompat.startActivity;

public class TransmisionServidor extends AsyncTask<Void,String,Void> {

    private String solicitudes_procesadas;
    private WeakReference<ListView> listView;
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String destinationAddress="192.168.0.10";
    private String filePath;
    private String wholePath;
    private String id_solicitud;
    private boolean xceptionFlag = false;
    private String errorFlag = "Transmision NO pudo realizarse.";
    private Socket socket;
    private String hostName,canonicalHostname;
    private String givenName;
    private DataBaseHelper mDBHelper;
    AlertDialog dialog;

    public TransmisionServidor(WeakReference<Context> context, WeakReference<Activity> act, String path, String fullPath, String id_solicitud){
        this.context = context;
        this.activity = act;
        this.filePath = path;
        this.wholePath = fullPath;
        this.id_solicitud = id_solicitud;
        mDBHelper = new DataBaseHelper(context.get());
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
            //Validar si existen solicitudes para indicar que no ya todas las solicitudes sen transmitido con exito
            //mDBHelper.RestaurarEstadosSolicitudesTransmitidas();
            int cantidad = mDBHelper.CantidadSolicitudesTransmision();
            //int cantidad = 1;
            if(cantidad <= 0){
                xceptionFlag = true;
                errorFlag = "Hay "+cantidad+" solicitudes nuevas para transmitir.";
            }else {
                publishProgress("Estableciendo comunicación...");
                socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip",""),Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto","")));
                // Enviar archivo en socket
                //File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
                //files.add(myFile);

                SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase("/data/user/0/proyecto.app.clientesabc/databases/TRANSMISION_"+ PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH",""), null, SQLiteDatabase.CREATE_IF_NECESSARY);

                //Crear una base de datos solo para los datos que deben ser transmitidos
                publishProgress("Extrayendo datos...");
                String sqlDrop = "DROP TABLE IF EXISTS 'FormHvKof_solicitud';DROP TABLE IF EXISTS 'FormHvKof_old_solicitud';DROP TABLE IF EXISTS 'encuesta_solicitud';DROP TABLE IF EXISTS 'encuesta_gec_solicitud';DROP TABLE IF EXISTS 'grid_contacto_solicitud';DROP TABLE IF EXISTS 'grid_impuestos_solicitud';DROP TABLE IF EXISTS 'grid_bancos_solicitud';DROP TABLE IF EXISTS 'grid_visitas_solicitud';DROP TABLE IF EXISTS 'grid_interlocutor_solicitud';DROP TABLE IF EXISTS 'adjuntos_solicitud';";
                String sqlAttach = "ATTACH DATABASE '/data/user/0/proyecto.app.clientesabc/databases/FAWM_ANDROID_2' AS fromDB";
                String[] droptables = sqlDrop.split(";");
                for(String query : droptables){
                    mDataBase.execSQL(query);
                }
                mDataBase.execSQL(sqlAttach);

                String filtroUnicaSolicitud = "";
                if(id_solicitud.trim().length() > 0){
                    filtroUnicaSolicitud = " AND id_solicitud = '"+id_solicitud.trim()+"'";
                }
                //Comenzar a crear las tablas segun lo que existe actualmente en la base de datos
                String sqlCreate = "CREATE TABLE FormHvKof_solicitud AS SELECT * FROM fromDB.FormHvKof_solicitud WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud;
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE encuesta_solicitud AS SELECT * FROM fromDB.encuesta_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido') "+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE encuesta_gec_solicitud AS SELECT * FROM fromDB.encuesta_gec_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE grid_contacto_solicitud AS SELECT * FROM fromDB.grid_contacto_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE grid_bancos_solicitud AS SELECT * FROM fromDB.grid_bancos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE grid_impuestos_solicitud AS SELECT * FROM fromDB.grid_impuestos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE grid_visitas_solicitud AS SELECT * FROM fromDB.grid_visitas_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE grid_interlocutor_solicitud AS SELECT * FROM fromDB.grid_interlocutor_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo','Corregido')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                sqlCreate = "CREATE TABLE adjuntos_solicitud AS SELECT * FROM fromDB.adjuntos_solicitud WHERE id_solicitud IN (Select id_solicitud FROM fromDB.FormHvKof_solicitud  WHERE trim(estado) IN ('Nuevo')"+filtroUnicaSolicitud+")";
                mDataBase.execSQL(sqlCreate);
                publishProgress("Empaquetando datos...");
                String pathFileToZip = context.get().getApplicationInfo().dataDir + "/databases/";
                FileHelper.zip(pathFileToZip,context.get().getApplicationInfo().dataDir + "/databases/","TRANSMISION_"+ PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH","")+".zip",false);
                File myFile = new File(context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_"+ PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH","")+".zip");
                //File myFile = new File(context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_"+ PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH",""));

                //File externalStorage = Environment.getExternalStorageDirectory();
                //String externalStoragePath = externalStorage.getAbsolutePath();
                //File myFile = new File(externalStoragePath + File.separator + context.get().getPackageName()+ File.separator+"Transmision", "PRUEBA_COPIA");

                files.add(myFile);

                System.out.println("Creando Streams de datos...");
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                System.out.println(files.size());

                publishProgress("Enviando datos...");
                //Comando String que indicara que se queire realizar Sincronizacion/Transmision
                dos.writeUTF("Transmision");
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
                publishProgress("Esperando respuesta...");
                //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la sincronizacion
                long s = dis.readLong();
                if(s < 0){
                    publishProgress("Error Transmision...");
                    s = dis.readLong();
                    byte[] e = new byte[(int) s];
                    dis.readFully(e);
                    String error = new String(e);
                    xceptionFlag = true;
                    errorFlag = "Error: "+error;
                }else {
                    publishProgress("Procesando respuesta...");
                    byte[] r = new byte[(int) s];
                    dis.readFully(r);
                    solicitudes_procesadas = new String(r, Charset.defaultCharset());
                }

                //O cerrarlo aqui estara bien?
                dos.close();
            }
            publishProgress("Transmision Finalizada...");
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
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                errorFlag = "Proceso cancelado por el usuario.";
                cancel(true);
            }
        });
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
        dialog.show();

    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(xceptionFlag){
            Toasty.error(context.get(),errorFlag,Toast.LENGTH_LONG).show();
        }
        else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("ultimaTransmision", dateFormat.format(date)).apply();
            Toasty.success(context.get(),"Transmision Finalizada Correctamente!!",Toast.LENGTH_LONG).show();
            //Adicionalmente se debe actualizar el estado de las solicitudes enviadas para que no se dupliquen.
            mDBHelper.ActualizarEstadosSolicitudesTransmitidas(solicitudes_procesadas);
        }
        dialog.dismiss();
        if(dialog.isShowing())
            dialog.hide();
        //activity.get().recreate();
        Intent intent = activity.get().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.get().finish();
        activity.get().overridePendingTransition(0, 0);
        startActivity(context.get(), intent, null);
        activity.get().overridePendingTransition(0, 0);
    }

    public ArrayList<String> getClientList() {

        final ArrayList<String> arr = new ArrayList<>(25);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                boolean isFirstLine = true;

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        String[] splitted = line.split(" +");

                        if (splitted.length >= 4) {

                            String ipAddress = splitted[0];
                            String macAddress = splitted[3];

                            boolean isReachable = InetAddress.getByName(
                                    splitted[0]).isReachable(500);
                            // this is network call so we cant do that on UI thread, so take background thread.
                            if (isReachable) {
                                Log.d("Device Information", ipAddress + " : "
                                        + macAddress);

                                //added afterwards for receiving names of available clients..
                                //but by adding this names to array list, the ip addresses is lost. so do something.
                                try {
                                    Socket socket = new Socket();
                                    //receive from port 5006 and timeout is 5s.
                                    socket.connect(new InetSocketAddress(ipAddress, 5006), 5000);
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    givenName = reader.readLine();
                                    reader.close();
                                    socket.close();
                                    Log.i("TAG", givenName);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //Assigning values to final array or array list is perfectly fine.

                                arr.add(ipAddress);
                                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                                hostName = inetAddress.getHostName();
                                canonicalHostname = inetAddress.getCanonicalHostName();

                                //  Toast.makeText(context,hostName+canonicalHostname,Toast.LENGTH_LONG).show();

                            }

                        }

                    }

                } catch (Exception e) {
                    xceptionFlag = true;
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        xceptionFlag = true;
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        //Wait util thread is completed. And then return array.
        //Otherwise it'll return null array or array list or what ever.
        try{
            thread.join();
        }
        catch (Exception e){
            xceptionFlag = true;
            e.printStackTrace();
        }
        return arr;

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