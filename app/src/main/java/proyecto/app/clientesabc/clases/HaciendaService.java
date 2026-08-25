package proyecto.app.clientesabc.clases;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.interfaces.HaciendaCallback;
import proyecto.app.clientesabc.modelos.HaciendaResult;

public class HaciendaService {

    private static final OkHttpClient client = new OkHttpClient();

    public static void consultar(String identificacion, HaciendaCallback callback) {

        String url = VariablesGlobales.getUrlApiHaciendaCR() + identificacion;

        Request request = new Request.Builder().url(url).get().build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("No se pudo consultar Hacienda");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                int code = response.code();
                String body = response.body() != null ? response.body().string() : "";

                Log.e("HACIENDA", "HTTP CODE: " + code);
                Log.e("HACIENDA", "BODY: " + body);

                if (!response.isSuccessful()) {
                    callback.onError("Error en consulta Hacienda");
                    return;
                }

                try {
                    //body = response.body().string();
                    JSONObject data = new JSONObject(body);

                    HaciendaResult result = new HaciendaResult();
                    result.nombre = data.optString("nombre", "");

                    JSONArray actividades = data.optJSONArray("actividades");

                    if (actividades != null) {
                        for (int i = 0; i < actividades.length(); i++) {
                            JSONObject act = actividades.getJSONObject(i);

                            if ("P".equals(act.optString("tipo"))) {
                                result.actividadCodigo = act.optString("codigo");
                                break;
                            }
                        }
                    }

                    callback.onRespuesta(data);

                } catch (Exception ex) {
                    callback.onError("Error procesando respuesta");
                }
            }
        });
    }
}