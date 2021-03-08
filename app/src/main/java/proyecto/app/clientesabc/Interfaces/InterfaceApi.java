package proyecto.app.clientesabc.Interfaces;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
public interface InterfaceApi {

    @GET("api/Adjunto/{bukrs}/{ruta}/{version}/{nombre}")
    Call<ResponseBody> Adjunto(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("version") String nombre);

    @GET("api/Actualizacion/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> Actualizacion(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @GET("api/ConsultaCliente/{bukrs}/{ruta}/{version}/{codigo}")
    Call<ResponseBody> ConsultaCliente(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo);

    @GET("api/ConsultaClienteTotal/{bukrs}/{ruta}/{version}/{codigo}")
    Call<ResponseBody> ConsultaClienteTotal(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo);

    @GET("api/ConsultaCreditoCliente/{bukrs}/{ruta}/{version}/{codigo}/{areacred}/{tipocred}")
    Call<ResponseBody> ConsultaCreditoCliente(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo, @Path("areacred") String areacred, @Path("tipocred") String tipocred);

    @GET("api/Sincronizacion/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> Sincronizacion(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @GET("api/ValidarFlujoCliente/{bukrs}/{ruta}/{version}/{codigo}/{tipoformulario}/{numequipo}")
    Call<ResponseBody> ValidarFlujoCliente(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo, @Path("tipoformulario") String tipoformulario, @Path("numequipo") String numequipo);

    @GET("api/PruebaConexion/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> PruebaConexion(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @Multipart
    @POST("api/Transmision/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> Transmision(@Part("description") RequestBody description, @Part MultipartBody.Part file, @Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);
}
