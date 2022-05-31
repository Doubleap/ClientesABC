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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import retrofit2.Call;
import retrofit2.Response;

public class RefrescarAutenticacionTokenAPISinc {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String username;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    AlertDialog dialog;
    public RefrescarAutenticacionTokenAPISinc(WeakReference<Context> c, WeakReference<Activity> a, String username){
        this.context = c;
        this.activity = a;
        this.username = username;
    }

    protected ArrayList<JsonArray> RefrescarToken(Void... voids) {
        ArrayList<JsonArray> respuesta = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        //publishProgress("Estableciendo comunicación...");
        System.out.println("Estableciendo comunicación para enviar archivos...");
        String mensaje = "";//VariablesGlobales.validarConexionDePreferencia(context.get());
        if(mensaje.equals("")) {
            //String bukrs = String.format("%4s", String.valueOf(sociedad)).replace(' ', '0');
            Map<String, String> fields = new HashMap<>();
            fields.put("username", username);
            fields.put("grant_type", "refresh_token");
            fields.put("refresh_token", PreferenceManager.getDefaultSharedPreferences(context.get()).getString("REFRESH_TOKEN", ""));

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
                    //publishProgress("Recibiendo datos...");

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
                        //publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "%");
                    }
                    byte[] r = Arrays.copyOfRange(temp, 0, offset);
                    //dis.readFully(r);
                    String respuestajson = new String(r);
                    respuestajson = "["+respuestajson+"]";
                    try {
                        Gson gson = new Gson();
                        respuesta.add(gson.fromJson(respuestajson, JsonArray.class));
                        //publishProgress("Procesando datos cliente..");
                    }catch(Exception e){
                        xceptionFlag = true;
                        messageFlag = e.getMessage();
                    }
                }else {
                    //messageFlag = response.raw().message()+". "/*+ response.raw().request().url()*/;
                    if(error.length() < 500  )
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
        //publishProgress("Proceso Terminado...");

        Log.i("===end of start ====", "==");

        if(respuesta.size() > 0) {
            JsonObject propiedadesToken = respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject();
            String token = propiedadesToken.get("access_token").getAsString();
            String refreshToken = propiedadesToken.get("refresh_token").getAsString();
            String expiresIn = propiedadesToken.get("expires_in").getAsString();
            String expiresDate = propiedadesToken.get(".expires").getAsString();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("TOKEN", token).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("REFRESH_TOKEN", refreshToken).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("EXPIRES_IN", expiresIn).apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("EXPIRES_DATE", expiresDate).apply();
        }else{
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("TOKEN", "").apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("REFRESH_TOKEN", "").apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("EXPIRES_IN", "").apply();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("EXPIRES_DATE", "").apply();
            JsonArray probando = new JsonArray(1);
            Gson gson = new Gson();
            //messageFlag = "["+messageFlag+"]";
            probando.add(gson.fromJson(messageFlag, JsonObject.class));
            respuesta.add(probando);
        }

        return respuesta;
    }

}