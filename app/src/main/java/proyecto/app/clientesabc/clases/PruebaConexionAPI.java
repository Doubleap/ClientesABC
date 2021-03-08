package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PruebaConexionAPI extends AsyncTask<Void,Void,Void> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    AlertDialog dialog;
    public PruebaConexionAPI(WeakReference<Context> c, WeakReference<Activity> a){
        this.context = c;
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        System.out.println("Estableciendo comunicación");
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

            Call<ResponseBody> call = sincronizacionService.PruebaConexion(VariablesGlobales.getSociedad(), PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""), version);
            Response<ResponseBody> response;

            try {
                response = call.execute();
                if (!response.body().contentType().toString().equals("text/html")) {
                    xceptionFlag = true;
                    messageFlag = response.body().string();
                }
            } catch (Exception e) {
                xceptionFlag = true;
                messageFlag = e.getMessage();
                e.printStackTrace();
            }


        }else{
            xceptionFlag = true;
            messageFlag = mensaje;
        }

        Log.i("===end of start ====", "==");

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setCancelable(false); // Si quiere que el usuario espere por el proceso completo por obligacion
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
            Toasty.success(context.get(),"Conexion Exitosa!!",Toast.LENGTH_LONG).show();
        }
        else{
            Toasty.error(context.get(),"No se pudo conectar a servidor."+messageFlag,Toast.LENGTH_LONG).show();
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
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
        dialog.dismiss();
        if(dialog.isShowing())
            dialog.hide();
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