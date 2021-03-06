package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;

public class ActualizacionServidor extends AsyncTask<Void,String,Void> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    AlertDialog dialog;
    public ActualizacionServidor(WeakReference<Context> c, WeakReference<Activity> a){
        this.context = c;
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        try {
            publishProgress("Estableciendo comunicación...");
            System.out.println("Estableciendo comunicación para enviar archivos...");
            String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
            if(mensaje.equals("")) {
            socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip","").trim(),Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto","").trim()));

            System.out.println("Creando Streams de datos...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            //Comando String que indicara que se quiere realizar una Sincronizacion
            publishProgress("Comunicacion establecida...");
            //Enviar Pais de procedencia
            /*dos.writeUTF(VariablesGlobales.getSociedad());
            dos.flush();
            //Version con la que quiere transmitir
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dos.writeUTF(dateFormat.format(BuildConfig.BuildDate));
            dos.flush();
            dos.writeUTF(VariablesGlobales.getSociedad());
            dos.flush();
            //Enviar Ruta que se quiere sincronizar
            dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""));
            dos.flush();
            */

            dos.writeUTF("Actualizacion");
            dos.flush();

            dos.writeUTF("FIN");
            dos.flush();

            //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la sincronizacion
            long s = dis.readLong();
            if(s < 0){
                publishProgress("No se pudo descargar...");
                s = dis.readLong();
                byte[] e = new byte[(int) s];
                dis.readFully(e);
                String error = new String(e);
                xceptionFlag = true;
                messageFlag = "Error: "+error;
            }else {
                byte[] r = new byte[(int) s];
                int offset = 0;
                int bytesRead;
                while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != s) {
                    offset += bytesRead;
                    publishProgress("Descargando..."+String.format("%.02f",(100f/(s/1024f))*(offset/1024f))+"%");
                }
                publishProgress("Procesando datos recibidos...");
                File tranFileDir;
                File externalStorage = Environment.getExternalStorageDirectory();
                if (externalStorage != null) {
                    String externalStoragePath = externalStorage.getAbsolutePath();
                    tranFileDir = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Actualizacion");
                    boolean ex = tranFileDir.mkdirs();
                    File transferFile = new File(tranFileDir, "ClientesABC");
                    OutputStream stream = new FileOutputStream(transferFile);
                    stream.write(r);
                    stream.flush();
                    stream.close();

                    dos.close();
                    //UNZIP informacion recibida
                    boolean unzip = FileHelper.unzip(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Actualizacion/ClientesABC",externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Actualizacion");
                    final File file = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "Actualizacion/ClientesABC.apk");

                    Date lastModDate = new Date(file.lastModified());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date buildDate = proyecto.app.clientesabc.BuildConfig.BuildDate;

                    if(unzip) {
                        if (buildDate.after(lastModDate)) {
                            activity.get().runOnUiThread(new Runnable() {
                                public void run() {
                                    DialogHandler appdialog = new DialogHandler();
                                    appdialog.Confirm(activity.get(), "Version Antigua", "Al aceptar esta instalacion estara devolviendose a una version ANTERIOR a la actual. Desea continuar con la instalacion?", "No", "Si", new ActualizacionServidor.ActualizarVersion(context.get(),file));

                                }
                            });
                        }else
                        if (buildDate.before(lastModDate)) {
                            publishProgress("Iniciando Instalacion...");
                            //Intentar REInstalar la aplicacion con el permiso del usuario.
                            try {
                                activity.get().runOnUiThread(new Runnable() {
                                    public void run() {
                                        DialogHandler appdialog = new DialogHandler();
                                        appdialog.Confirm(activity.get(), "Nueva versión", "Existe una actualización de la aplicacion! Desea instalar la nueva actualizacion de la aplicacion?", "No", "Si", new ActualizacionServidor.ActualizarVersion(context.get(),file));
                                    }
                                });

                            } catch (Exception e) {
                                xceptionFlag = true;
                                messageFlag = "Error Actualizando la aplicacion." + e.getMessage();
                                e.printStackTrace();
                            }
                        }else if(buildDate.equals(lastModDate)){
                            xceptionFlag = true;
                            messageFlag = "No se encontraron nuevas versiones de la aplicacion.";
                        }
                        else {
                            xceptionFlag = true;
                            messageFlag = "Problemas al desempaquetar la informacion.";
                        }
                    }
                }
            }
        }else{
            xceptionFlag = true;
            messageFlag = mensaje;
        }
            publishProgress("Proceso Terminado...");
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
        dialog.show();
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if(dialog.isShowing()) {
            dialog.hide();
        }
        if(xceptionFlag){
            Toasty.error(context.get(),"No se pudo actualizar: "+messageFlag,Toast.LENGTH_LONG).show();
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

    public class ActualizarVersion implements Runnable {
        File file;
        public ActualizarVersion(Context context, File file) {
            this.file = file;
        }
        public void run() {
            try {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //Uri packageURI = Uri.parse("package:proyecto.app.clientesabc");
                    //Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                    //context.get().startActivity(uninstallIntent);
                    Uri apkUri = FileProvider.getUriForFile(context.get(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    //Uri packageURI = Uri.parse("package:proyecto.app.clientesabc");
                    //Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                    //context.get().startActivity(uninstallIntent);
                    Uri apkUri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.get().startActivity(intent);
            }catch (Exception e){
                Toasty.error(context.get(),"Actualización Falló!!",Toast.LENGTH_LONG).show();
            }
        }
    }
}