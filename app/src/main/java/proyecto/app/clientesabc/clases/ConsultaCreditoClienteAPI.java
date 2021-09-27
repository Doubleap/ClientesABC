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

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.SolicitudCreditoActivity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaCreditoClienteAPI extends AsyncTask<Void,String,ArrayList<JsonArray>> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String codigoCliente;
    private String tipoCreditoSAP;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    ArrayList<JsonObject> estructuras;
    AlertDialog dialog;

    public ConsultaCreditoClienteAPI(WeakReference<Context> c, WeakReference<Activity> a, String codigoCliente, String tipoCreditoSAP) {
        this.context = c;
        this.activity = a;
        this.codigoCliente = codigoCliente;
        this.tipoCreditoSAP = tipoCreditoSAP;
    }

    @Override
    protected ArrayList<JsonArray> doInBackground(Void... voids) {
        ArrayList<JsonArray> estructurasSAP = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        publishProgress("Estableciendo comunicación...");
        System.out.println("Estableciendo comunicación para enviar archivos...");
        String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
        if (mensaje.equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String version = "";

            version = dateFormat.format(BuildConfig.BuildDate).replace(":", "COLON").replace("-", "HYPHEN");

            InterfaceApi apiService = ServiceGenerator.createService(context, activity,InterfaceApi.class, PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", ""));

            Call<ResponseBody> call = apiService.ConsultaCreditoCliente(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD",""),
                    PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""),
                    version,
                    String.format("%10s", String.valueOf(codigoCliente)).replace(' ', '0'),
                    PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_AREACREDITO", ""),
                    tipoCreditoSAP);
            Response<ResponseBody> response;

            try {
                response = call.execute();
                if (!response.body().contentType().toString().equals("text/html")) {
                    publishProgress("Recibiendo datos...");

                    long fileSize = response.body().contentLength();
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(response.body().byteStream()));

                    byte[] r = new byte[(int) fileSize];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != fileSize) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "%");
                    }
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
                        String jsonVisitas = 9;
                        String jsonCredito = 10;*/
                    String jsoncliente = new String(r);
                    try {
                        Gson gson = new Gson();
                        estructurasSAP.add(gson.fromJson(jsoncliente, JsonArray.class));
                        publishProgress("Procesando datos cliente..");
                    } catch (Exception e) {
                        xceptionFlag = true;
                        messageFlag = jsoncliente;
                    }

                } else {
                    xceptionFlag = true;
                    messageFlag = response.body().string();
                }
                publishProgress("Proceso Terminado...");
            } catch (IOException e) {
                xceptionFlag = true;
                messageFlag = e.getMessage();
                e.printStackTrace();
            }
        }else{
            xceptionFlag = true;
            messageFlag = mensaje;
        }
        Log.i("===end of start ====", "==");

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
        builder.setCancelable(true); // Si quiere que el usuario espere por el proceso completo por obligacion poner en false
        builder.setView(R.layout.layout_loading_dialog);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.get().finish();
                messageFlag = "Proceso cancelado por el usuario.";
                cancel(true);
                Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
            }
        });
        dialog = builder.create();
        if(!activity.get().isFinishing()) {
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
        if(dialog.isShowing()) {
            dialog.hide();
        }
        if(xceptionFlag){
            activity.get().finish();
            Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
        }
        SolicitudCreditoActivity.LlenarCampos(context.get(), activity.get(), estructuras);
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