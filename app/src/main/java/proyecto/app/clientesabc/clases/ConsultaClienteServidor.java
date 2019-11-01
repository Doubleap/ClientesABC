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
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.actividades.SolicitudModificacionActivity;

public class ConsultaClienteServidor extends AsyncTask<Void,String,ArrayList<JsonArray>> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String codigoCliente;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    ArrayList<JsonObject> estructuras;
    AlertDialog dialog;
    public ConsultaClienteServidor(WeakReference<Context> c, WeakReference<Activity> a, String codigoCliente){
        this.context = c;
        this.activity = a;
        this.codigoCliente = codigoCliente;
    }

    @Override
    protected ArrayList<JsonArray> doInBackground(Void... voids) {
        ArrayList<JsonArray> estructurasSAP = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        try {
            publishProgress("Estableciendo comunicación...");
            System.out.println("Estableciendo comunicación para enviar archivos...");
            socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip",""),Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto","")));

            System.out.println("Creando Streams de datos...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            //Comando String que indicara que se quiere realizar una Sincronizacion
            publishProgress("Comunicacion establecida...");
            dos.writeUTF("ConsultaCliente");
            dos.flush();

            dos.writeUTF(String.format("%10s", String.valueOf(codigoCliente)).replace(' ', '0'));
            dos.flush();

            dos.writeUTF("FIN");
            dos.flush();

            //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la consulta para modificacion
            long s = dis.readLong();
            if(s < 0){
                publishProgress("Error en Consulta Cliente...");
                s = dis.readLong();
                byte[] e = new byte[(int) s];
                dis.readFully(e);
                String error = new String(e);
                xceptionFlag = true;
                messageFlag = "Error: "+error;
            }else {
                publishProgress("Procesando datos recibidos...");
                /*ORDEN DE ESTRUCTURAS SAP RECIBIDAS
                        String jsonCliente = 0;
                        String jsonNotaEntrega = 1;
                        String jsonFactura = 2;
                        String jsonTelefonos = 3;
                        String jsonFaxes = 4;
                        String jsonContactos = 5;
                        String jsonInterlocutores = 6;
                        String jsonImpuestos = 7;
                        String jsonBancos = 8;
                        String jsonVisitas = 9;*/

                //Lectura InfoCliente
                byte[] r = new byte[(int) s];
                dis.readFully(r);
                String jsoncliente = new String(r);
                //jsoncliente = jsoncliente.substring(1,jsoncliente.length()-1);
                Gson gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsoncliente, JsonArray.class));
                publishProgress("Datos cliente..");

                //Lectura InfoNotaEntrega
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonNotaEntrega = new String(r);
                //jsonNotaEntrega = jsonNotaEntrega.substring(1,jsonNotaEntrega.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonNotaEntrega, JsonArray.class));
                publishProgress("Datos Nota Entrega..");

                //Lectura InfoFactura
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonFactura = new String(r);
                //jsonFactura = jsonFactura.substring(1,jsonFactura.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonFactura, JsonArray.class));
                publishProgress("Datos Factura..");

                //Lectura InfoTelefonos
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonTelefonos = new String(r);
                //jsonTelefonos = jsonTelefonos.substring(1,jsonTelefonos.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonTelefonos, JsonArray.class));
                publishProgress("Datos Telefonos..");

                //Lectura InfoFaxes
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonFaxes = new String(r);
                //jsonFaxes = jsonFaxes.substring(1,jsonFaxes.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonFaxes, JsonArray.class));
                publishProgress("Datos Faxes..");

                //Lectura InfoContactos
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonContactos = new String(r);
                //jsonContactos = jsonContactos.substring(1,jsonContactos.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonContactos, JsonArray.class));
                publishProgress("Datos Contactos..");

                //Lectura InfoInterlocutores
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonInterlocutores = new String(r);
                //jsonInterlocutores = jsonInterlocutores.substring(1,jsonInterlocutores.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonInterlocutores, JsonArray.class));
                publishProgress("Datos Interlocutores..");

                //Lectura InfoImpuestos
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonImpuestos = new String(r);
                //jsonImpuestos = jsonImpuestos.substring(1,jsonImpuestos.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonImpuestos, JsonArray.class));
                publishProgress("Datos Impuestos..");

                //Lectura InfoBancos
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonBancos = new String(r);
                //jsonBancos = jsonBancos.substring(1,jsonBancos.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonBancos, JsonArray.class));
                publishProgress("Datos Bancos..");

                //Lectura InfoVisitas
                s = dis.readLong();
                r = new byte[(int) s];
                dis.readFully(r);
                String jsonVisitas = new String(r);
                //jsonVisitas = jsonVisitas.substring(1,jsonVisitas.length()-1);
                gson = new Gson();
                estructurasSAP.add(gson.fromJson(jsonVisitas, JsonArray.class));
                publishProgress("Datos Visitas..");

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
            }
        });
        dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onPostExecute(ArrayList<JsonArray> estructuras) {
        super.onPostExecute(estructuras);
        dialog.dismiss();
        if(dialog.isShowing()) {
            dialog.hide();
        }
        if(xceptionFlag){
            Toasty.error(context.get(),"No se pudo consultar el cliente: "+messageFlag,Toast.LENGTH_LONG).show();
        }
        SolicitudModificacionActivity.LlenarCampos(context.get(), activity.get(), estructuras);
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