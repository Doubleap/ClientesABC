package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import retrofit2.Call;
import retrofit2.Response;

public class VerificarCodigoCorreoAPI extends AsyncTask<Void,String,ArrayList<JsonArray>> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String sociedad;
    private String cliente;
    private String correo;
    private String codigo;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    AlertDialog dialog;
    ImageView boton;
    public VerificarCodigoCorreoAPI(WeakReference<Context> c, WeakReference<Activity> a, String sociedad, String cliente, String correo, String codigo, ImageView btn){
        this.context = c;
        this.activity = a;
        this.sociedad = sociedad;
        this.cliente = cliente;
        this.correo = correo;
        this.codigo = codigo;
        this.boton = btn;
    }

    @Override
    protected ArrayList<JsonArray> doInBackground(Void... voids) {
        ArrayList<JsonArray> respuesta = new ArrayList<>();
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        publishProgress("Estableciendo comunicación...");
        System.out.println("Estableciendo comunicación para enviar archivos...");
        String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
        if(mensaje.equals("")) {
            String codigocliente = String.format("%10s", String.valueOf(this.cliente)).replace(' ', '0');
            //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la consulta para modificacion
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));
            String version = "";
            version = dateFormat.format(BuildConfig.BuildDate).replace(":","COLON").replace("-","HYPHEN");

            InterfaceApi apiService = ServiceGenerator.createService(context, activity,InterfaceApi.class, PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", ""));

            Call<ResponseBody> call = apiService.VerificarCodigoCorreo(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()), codigocliente, correo, codigo);
            Response<ResponseBody> response;
            try {
                response = call.execute();
                if (!response.body().contentType().toString().equals("text/html")) {
                    InputStream is = new BufferedInputStream(response.body().byteStream());
                    publishProgress("Recibiendo datos...");

                    long fileSize = response.body().contentLength();
                    DataInputStream dis = new DataInputStream(is);

                    byte[] temp = new byte[(int) 1000];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(temp, offset, temp.length - offset)) > -1 && offset != fileSize) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "% ("+String.format("%.2f", (offset/1000000.0))+" de "+String.format("%.2f", (fileSize/1000000.0))+")");
                    }
                    byte[] r = Arrays.copyOfRange(temp, 0, offset);
                    String respuestajson = new String(r);
                    try {
                        publishProgress("Validando verificación..");
                    }catch(Exception e){
                        xceptionFlag = true;
                        messageFlag = e.getMessage();
                    }
                }else {
                    messageFlag = response.body().string();
                    xceptionFlag = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        }else {
            try {
                boton.setBackgroundTintList(ColorStateList.valueOf(context.get().getResources().getColor(R.color.aprobados, null)));
                Toasty.success(context.get(), "Correo electrónico verificado!", Toast.LENGTH_LONG).show();
                boton.setOnClickListener(null);
            } catch (Exception e) {
                Toasty.error(context.get(), "Error al verificar el correo electrónico: " + e.getMessage()).show();
            }
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