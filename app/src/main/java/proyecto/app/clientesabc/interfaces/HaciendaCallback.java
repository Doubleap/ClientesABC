package proyecto.app.clientesabc.interfaces;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import proyecto.app.clientesabc.modelos.HaciendaResult;

public interface HaciendaCallback {
    void onRespuesta(JSONObject respuesta);
    void onError(String error);
}