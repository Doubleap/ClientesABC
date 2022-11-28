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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.ConfiguracionGeneralActivity;
import proyecto.app.clientesabc.actividades.LoginActivity;
import retrofit2.Call;
import retrofit2.Response;

public class ObtenerAutenticacionTokenAPI extends AsyncTask<Void,String,ArrayList<JsonArray>> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String sociedad;
    private String username;
    private String password;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    AlertDialog dialog;
    public ObtenerAutenticacionTokenAPI(WeakReference<Context> c, WeakReference<Activity> a, String sociedad, String username, String password){
        this.context = c;
        this.activity = a;
        this.sociedad = sociedad;
        this.username = username;
        this.password = password;
    }

    @Override
    protected ArrayList<JsonArray> doInBackground(Void... voids) {
        ArrayList<JsonArray> respuesta = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        publishProgress("Estableciendo comunicación...");
        System.out.println("Estableciendo comunicación para enviar archivos...");
        String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
        if(mensaje.equals("")) {
            //String bukrs = String.format("%4s", String.valueOf(sociedad)).replace(' ', '0');
            Map<String, String> fields = new HashMap<>();
            fields.put("username", username);
            fields.put("password", password);
            fields.put("grant_type", "password");

            InterfaceApi configuracionService = ServiceGenerator.createService(context, activity,InterfaceApi.class, null);

            Call<ResponseBody> call = configuracionService.Token(fields);
            Response<ResponseBody> response = null;
            try {
                response = call.execute();
                String error = "";
                if(response.errorBody() != null)
                    error = response.errorBody().string();
                if (response != null && response.body() != null && !response.body().contentType().toString().equals("text/html")) {
                    InputStream is = new BufferedInputStream(response.body().byteStream());
                    publishProgress("Recibiendo datos...");

                    long fileSize = response.body().contentLength();
                    if(fileSize == -1){
                        fileSize = Long.parseLong(response.raw().networkResponse().header("Content-Length"));
                    }
                    DataInputStream dis = new DataInputStream(is);

                    byte[] temp = new byte[750];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(temp, offset, temp.length - offset)) > -1 && offset != fileSize) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "%");
                    }
                    byte[] r = Arrays.copyOfRange(temp, 0, offset);
                    //dis.readFully(r);
                    String respuestajson = new String(r);
                    respuestajson = "["+respuestajson+"]";
                    try {
                        Gson gson = new Gson();
                        respuesta.add(gson.fromJson(respuestajson, JsonArray.class));
                        publishProgress("Procesando datos cliente..");
                    }catch(Exception e){
                        xceptionFlag = true;
                        messageFlag = e.getMessage();
                    }
                }else {
                    messageFlag = response.raw().message()+". "/*+ response.raw().request().url()*/;
                    if(error.length() < 100  )
                        messageFlag += error;
                    xceptionFlag = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                messageFlag += e.getMessage();
                xceptionFlag = true;
            }
        }else{
            xceptionFlag = true;
            messageFlag = mensaje;
        }
        publishProgress("Proceso Terminado...");

        Log.i("===end of start ====", "==");

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
            //activity.get().finish();
            Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
        }
        //Actualizar el token interno para uso de la aplicacion
        if(mensajes.size() > 0) {
            /*FORMATO
            {
            "access_token": "XXXXX",
            "token_type": "bearer",
            "expires_in": 1199,
            "refresh_token": "YYYYY",
            "userName": "usergmail.com",
            "roles": "Preventa",
            ".issued": "Mon, 21 Jun 2021 18:40:11 GMT",
            ".expires": "Mon, 21 Jun 2021 19:00:11 GMT"
            }
            */
            JsonObject propiedadesToken = mensajes.get(0).getAsJsonArray().get(0).getAsJsonObject();
            String token = propiedadesToken.get("access_token").getAsString();
            String refreshToken = propiedadesToken.get("refresh_token").getAsString();
            String expiresIn = propiedadesToken.get("expires_in").getAsString();
            String expiresDate = propiedadesToken.get(".expires").getAsString();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("TOKEN", token).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("REFRESH_TOKEN", refreshToken).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("EXPIRES_IN", expiresIn).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("EXPIRES_DATE", expiresDate).apply();
        }else{
            //PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("TOKEN", "").apply();
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