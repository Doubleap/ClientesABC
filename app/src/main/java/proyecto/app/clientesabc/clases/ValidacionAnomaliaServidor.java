package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
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

public class ValidacionAnomaliaServidor extends AsyncTask<Void,String,ArrayList<JsonArray>> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private EquipoFrio equipoFrio;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    ArrayList<JsonObject> estructuras;
    AlertDialog dialog;
    private DataBaseHelper mDBHelper;
    public ValidacionAnomaliaServidor(WeakReference<Context> c, WeakReference<Activity> a, EquipoFrio equipoFrio){
        this.context = c;
        this.activity = a;
        this.equipoFrio = equipoFrio;
        mDBHelper = new DataBaseHelper(this.context.get());
    }

    @Override
    protected ArrayList<JsonArray> doInBackground(Void... voids) {
        ArrayList<JsonArray> estructurasSAP = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        try {
            publishProgress("Estableciendo comunicación...");
            System.out.println("Estableciendo comunicación para enviar archivos...");
            String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
            if(mensaje.equals("")) {
                socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip", ""), Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto", "")));

                System.out.println("Creando Streams de datos...");
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                //Comando String que indicara que se quiere realizar una Sincronizacion
                publishProgress("Comunicacion establecida...");
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

                dos.writeUTF("ValidacionAnomalia");
                dos.flush();

                dos.writeUTF(String.valueOf(equipoFrio.getNumPlaca()));
                dos.flush();

                dos.writeUTF("FIN");
                dos.flush();

                //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la consulta para modificacion
                long s = dis.readLong();
                if (s < 0) {
                    publishProgress("Error en Validacion Anomalia...");
                    s = dis.readLong();
                    byte[] e = new byte[(int) s];
                    dis.readFully(e);
                    String error = new String(e);
                    xceptionFlag = true;
                    messageFlag = "Error: " + error;
                } else {
                    //Toda la info de cliente
                    publishProgress("Iniciando descarga...");
                    byte[] r = new byte[(int) s];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != s) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (s / 1024f)) * (offset / 1024f)) + "%");
                    }
                    dos.writeUTF("END");
                    dos.flush();
                    publishProgress("Procesando datos recibidos...");

                    String jsoncliente = new String(r);

                    publishProgress("Procesando datos del equipo frio..");

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

        return estructurasSAP;
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
                Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
                activity.get().finish();

            }
        });
        dialog = builder.create();
        if(activity.get() != null && !activity.get().isFinishing()) {
            dialog.show();
        }
    }
    @Override
    protected void onPostExecute(ArrayList<JsonArray> estructuras) {
        super.onPostExecute(estructuras);
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("transmitido","1");
        long update = db.update("CensoEquipoFrio",values,"trim(kunnr_censo) = ? AND trim(num_placa) = ? AND fecha_lectura = ? AND transmitido = '0'",new String[]{equipoFrio.getKunnrCenso(),equipoFrio.getNumPlaca(),equipoFrio.getFechaLectura()});
        if(update <= 0){
            //Toasty.success(context.get(),"No se actualizo el estado de transmision de la lectura!",Toast.LENGTH_LONG).show();
        }
        if(dialog.isShowing()) {
            dialog.hide();
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