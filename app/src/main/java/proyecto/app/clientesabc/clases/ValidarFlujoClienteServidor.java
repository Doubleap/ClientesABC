package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import proyecto.app.clientesabc.actividades.ConsultaClienteTotalActivity;
import proyecto.app.clientesabc.actividades.SolicitudAvisosEquipoFrioActivity;
import proyecto.app.clientesabc.actividades.SolicitudCreditoActivity;
import proyecto.app.clientesabc.actividades.SolicitudModificacionActivity;

public class ValidarFlujoClienteServidor extends AsyncTask<Void,String,ArrayList<JsonArray>> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String codigoCliente;
    private String tipoFormulario;
    private String numEquipo;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    ArrayList<JsonObject> estructuras;
    AlertDialog dialog;
    public ValidarFlujoClienteServidor(WeakReference<Context> c, WeakReference<Activity> a, String codigoCliente, String tipoFormulario, String numEquipo){
        this.context = c;
        this.activity = a;
        this.codigoCliente = codigoCliente;
        this.tipoFormulario = tipoFormulario;
        this.numEquipo = numEquipo;
    }

    @Override
    protected ArrayList<JsonArray> doInBackground(Void... voids) {
        ArrayList<JsonArray> respuesta = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        try {
            publishProgress("Estableciendo comunicación...");
            System.out.println("Estableciendo comunicación para enviar archivos...");
            String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
            if(mensaje.equals("")) {
                socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip", "").trim(), Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto", "").trim()));

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

                dos.writeUTF("ValidarFlujo");
                dos.flush();

                dos.writeUTF(String.format("%10s", String.valueOf(codigoCliente)).replace(' ', '0'));
                dos.flush();

                dos.writeUTF(tipoFormulario);
                dos.flush();

                dos.writeUTF(numEquipo);
                dos.flush();

                dos.writeUTF("FIN");
                dos.flush();

                //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la consulta para modificacion
                long s = dis.readLong();
                if (s < 0) {
                    publishProgress("Error en Consulta Cliente...");
                    s = dis.readLong();
                    byte[] e = new byte[(int) s];
                    dis.readFully(e);
                    String error = new String(e);
                    xceptionFlag = true;
                    messageFlag = "" + error;
                } else {
                    publishProgress("Procesando datos recibidos...");
                    byte[] r = new byte[(int) s];
                    dis.readFully(r);
                    String respuestajson = new String(r,"UTF-8");
                    Gson gson = new Gson();
                    respuesta.add(gson.fromJson(respuestajson, JsonArray.class));
                    publishProgress("Procesando datos cliente..");
                }
                dos.writeUTF("END");
                dos.flush();
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

        return respuesta;
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
        builder.setCancelable(true); // Si quiere que el usuario espere por el proceso completo por obligacion poner en false
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
        if(!activity.get().isFinishing()) {
            dialog.show();
        }
    }
    @Override
    protected void onPostExecute(ArrayList<JsonArray> mensajes) {
        super.onPostExecute(mensajes);
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
        if(xceptionFlag){
            Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
            activity.get().finish();
        }
        if(context.get().getClass().getSimpleName().equals("SolicitudModificacionActivity"))
            SolicitudModificacionActivity.SolicitudPermitida(context.get(), activity.get(), mensajes);
        else if(context.get().getClass().getSimpleName().equals("SolicitudAvisosEquipoFrioActivity"))
            SolicitudAvisosEquipoFrioActivity.SolicitudPermitida(context.get(), activity.get(), mensajes);
        else if(context.get().getClass().getSimpleName().equals("SolicitudCreditoActivity"))
            SolicitudCreditoActivity.SolicitudPermitida(context.get(), activity.get(), mensajes);

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