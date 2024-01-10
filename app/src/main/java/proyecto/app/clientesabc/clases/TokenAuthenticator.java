package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.content.Context;
import android.net.Proxy;
import android.os.AsyncTask;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import androidx.annotation.WorkerThread;

import com.google.gson.JsonArray;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import proyecto.app.clientesabc.actividades.LoginActivity;

public class TokenAuthenticator  implements Authenticator {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private int cantidadIntentos = 0;
    TokenAuthenticator(WeakReference<Context> c, WeakReference<Activity> a){
        this.context = c;
        this.activity = a;
    }
    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) throws IOException {
        String newAccessToken = "";//PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", "");
        String expires_date = "";//PreferenceManager.getDefaultSharedPreferences(context.get()).getString("EXPIRES_DATE", "");
        String user = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("user", "");
        String password = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("password", "");
        // Refresh your access_token using a synchronous api request
        //Si YA se ha vencido del tiempo para el token, podemos refrescar el token si es posible //Mon, 21 Jun 2021 21:42:12 GMT
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.getDefault());
        ArrayList<JsonArray> refrescado = null;
        try {
            if(!expires_date.equals("")) {
                Date expira = dateFormat.parse(expires_date);
                if (new Date().after(expira)) {
                    activity.get().runOnUiThread(new Runnable() {
                        public void run() {
                            Toasty.info(context.get(), "Renovando Token de Autenticación. Expiró: " + expira).show();
                        }
                    });
                    RefrescarAutenticacionTokenAPISinc r = new RefrescarAutenticacionTokenAPISinc(context, activity, user);
                    refrescado = r.RefrescarToken((Void[]) null);
                    if(refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") != null){
                        String errorServer = refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description").getAsString();
                        String finalErrorServer = errorServer;
                        activity.get().runOnUiThread(new Runnable() {
                            public void run() {
                                Toasty.info(context.get(), "API Error: "+ finalErrorServer).show();
                            }
                        });

                    }else if(refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error") != null && refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") == null) {
                        String errorServer = refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error").getAsString();
                        String finalErrorServer = errorServer;
                        activity.get().runOnUiThread(new Runnable() {
                            public void run() {
                                Toasty.info(context.get(), "API Error: "+ finalErrorServer).show();
                            }
                        });
                    }

                    newAccessToken = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", "");
                }
            }
            if (refrescado == null || refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error") != null || refrescado.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") != null) {
                ObtenerAutenticacionTokenAPISinc v = new ObtenerAutenticacionTokenAPISinc(context, activity, "sociedad", user, password);
                ArrayList<JsonArray> respuesta = v.ObtenerToken((Void[]) null);
                if(respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") != null && respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error") != null){
                    String errorServer = respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description").getAsString();
                    if(respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error") != null && respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") == null)
                        errorServer = respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error").getAsString();
                    String finalErrorServer = errorServer;
                    activity.get().runOnUiThread(new Runnable() {
                        public void run() {
                            Toasty.error(context.get(), "API Error: "+finalErrorServer).show();
                        }
                    });
                    //newAccessToken = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", "");
                    return null;
                }
                newAccessToken = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", "");

            }
        } catch (ParseException e) {
            e.printStackTrace();
            ObtenerAutenticacionTokenAPISinc v = new ObtenerAutenticacionTokenAPISinc(context, activity, "sociedad", user, password);
            ArrayList<JsonArray> respuesta = v.ObtenerToken((Void[]) null);
            if(respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") != null){
                String errorServer = respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description").getAsString();
                if(respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error") != null && respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error_description") == null)
                    errorServer = respuesta.get(0).getAsJsonArray().get(0).getAsJsonObject().get("error").getAsString();
                String finalErrorServer = errorServer;
                activity.get().runOnUiThread(new Runnable() {
                    public void run() {
                        Toasty.info(context.get(), "API Error: "+finalErrorServer).show();
                    }
                });
            }
            newAccessToken = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", "");
        }
        // Add new header to rejected request (Unauthorized) and retry it with new Token
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newAccessToken)
                .build();
    }
}
