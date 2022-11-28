package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String authToken;

    public AuthenticationInterceptor(WeakReference<Context> c, WeakReference<Activity> a,String token) {
        this.context = c;
        this.activity = a;
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Refresh your access_token using a synchronous api request
        /*WeakReference<Context> weakRefs1 = new WeakReference<Context>(context.get());
        WeakReference<Activity> weakRefAs1 = new WeakReference<Activity>(activity.get());
        String user = PreferenceManager.getDefaultSharedPreferences(context.get()).getString("username", "");
        RefrescarAutenticacionTokenAPI v = new RefrescarAutenticacionTokenAPI(weakRefs1, weakRefAs1, user);
        try {
            v.execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        Request.Builder builder = original.newBuilder().header("Authorization", "Bearer " + authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
