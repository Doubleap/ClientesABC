package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import proyecto.app.clientesabc.VariablesGlobales;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    //Para Pruebas locales
    //public static final String API_BASE_URL = "http://10.0.2.2:51123/";
    //Para Pruebas en ambiente calidad
    public static final String API_BASE_URL = VariablesGlobales.getUrlApi();
    private static WeakReference<Context> c;
    private static WeakReference<Activity> a;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(120, TimeUnit.SECONDS);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S>
    S createService(WeakReference<Context> context, WeakReference<Activity> activity,Class<S> serviceClass) {
        return createService(context,  activity, serviceClass, null);
    }

    public static <S> S createService(WeakReference<Context> context, WeakReference<Activity> activity,Class<S> serviceClass, final String authToken) {
        c = context;
        a = activity;
        Retrofit retrofit=null;
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(c, a, authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
            }
            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(c,a);
            httpClient.authenticator(tokenAuthenticator);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }else{
            /*for(int x = 0;x < httpClient.interceptors().size(); x++){
                httpClient.interceptors().remove(x);
            }*/

            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator(c,a);
            httpClient.authenticator(tokenAuthenticator);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
