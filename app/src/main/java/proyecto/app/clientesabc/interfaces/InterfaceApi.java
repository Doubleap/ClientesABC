package proyecto.app.clientesabc.interfaces;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
public interface InterfaceApi {

    String ftp = "/ftp";//vacio para api sin servidor FTP o '/ftp' para uso de servidor de FTP por app service azure (Usar siempre con FTP ya que se agrego parametro en API para usar FTP servidor)
    @Streaming
    @FormUrlEncoded
    @POST("Token")
    Call<ResponseBody> Token(@FieldMap Map<String, String> fields);

    @Streaming
    @FormUrlEncoded
    @POST("RefreshToken")
    Call<ResponseBody> RefreshToken(@FieldMap Map<String, String> fields);

    /*@GET("api/token")
    Call<ResponseBody> token();*/

    @Streaming
    @GET("api"+ftp+"/Adjunto/{bukrs}/{ruta}/{version}/{nombre}")
    Call<ResponseBody> Adjunto(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Query("nombre") String nombre);

    @Streaming
    @GET("api"+ftp+"/Actualizacion/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> Actualizacion(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @Streaming
    @GET("api"+ftp+"/ConsultaCliente/{bukrs}/{ruta}/{version}/{codigo}")
    Call<ResponseBody> ConsultaCliente(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo);

    @Streaming
    @GET("api"+ftp+"/ConsultaClienteTotal/{bukrs}/{ruta}/{cliente}/{version}/{codigo}")
    Call<ResponseBody> ConsultaClienteTotal(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("cliente") String cliente, @Path("version") String version, @Path("codigo") String codigo);

    @Streaming
    @GET("api"+ftp+"/ConsultaCreditoCliente/{bukrs}/{ruta}/{version}/{codigo}/{areacred}/{tipocred}")
    Call<ResponseBody> ConsultaCreditoCliente(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo, @Path("areacred") String areacred, @Path("tipocred") String tipocred);

    @Streaming
    @GET("api"+ftp+"/Sincronizacion/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> Sincronizacion(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @Streaming
    @GET("api"+ftp+"/ValidarFlujoCliente/{bukrs}/{ruta}/{version}/{codigo}/{tipoformulario}/{numequipo}")
    Call<ResponseBody> ValidarFlujoCliente(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("codigo") String codigo, @Path("tipoformulario") String tipoformulario, @Path("numequipo") String numequipo);

    @Streaming
    @GET("api"+ftp+"/PruebaConexion/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> PruebaConexion(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @Multipart
    @POST("api"+ftp+"/Transmision/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> Transmision(@Part("description") RequestBody description, @Part MultipartBody.Part file, @Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @Multipart
    @POST("api"+ftp+"/TransmisionLecturaCenso/{bukrs}/{ruta}/{version}")
    Call<ResponseBody> TransmisionLecturaCenso(@Part("description") RequestBody description, @Part MultipartBody.Part file, @Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version);

    @Streaming
    @GET("api"+ftp+"/ConfiguracionPais/{bukrs}")
    Call<ResponseBody> ConfiguracionPais(@Path("bukrs") String bukrs);

    @Streaming
    @GET("api"+ftp+"/GenerarCodigoVerificacion/{bukrs}/{kunnr}/{celular}")
    Call<ResponseBody> GenerarCodigoVerificacion(@Path("bukrs") String bukrs, @Path("kunnr") String kunnr, @Path("celular") String celular);

    @Streaming
    @GET("api"+ftp+"/GenerarCodigoVerificacionCorreo/{bukrs}/{kunnr}/{correo}")
    Call<ResponseBody> GenerarCodigoVerificacionCorreo(@Path("bukrs") String bukrs, @Path("kunnr") String kunnr, @Path("correo") String correo);

    @Streaming
    @GET("api"+ftp+"/VerificarCodigo/{bukrs}/{kunnr}/{celular}/{codigo_recibido}")
    Call<ResponseBody> VerificarCodigo(@Path("bukrs") String bukrs,@Path("kunnr") String kunnr,@Path("celular") String celular,  @Path("codigo_recibido") String codigo_recibido);

    @Streaming
    @GET("api"+ftp+"/VerificarCodigoCorreo/{bukrs}/{kunnr}/{correo}/{codigo_recibido}")
    Call<ResponseBody> VerificarCodigoCorreo(@Path("bukrs") String bukrs,@Path("kunnr") String kunnr,@Path("correo") String correo,  @Path("codigo_recibido") String codigo_recibido);

    @Streaming
    @GET("api"+ftp+"/RechazarPreSolicitud/{bukrs}/{ruta}/{idform}/{estado}/{comentario}")
    Call<ResponseBody> RechazarPreSolicitud(@Path("bukrs") String bukrs,@Path("ruta") String ruta,@Path("idform") String idform,@Path("estado") String estado,@Path("comentario") String comentario);

    @Streaming
    @GET("api"+ftp+"/DevolverPreSolicitud/{bukrs}/{ruta}/{idform}/{estado}/{comentario}")
    Call<ResponseBody> DevolverPreSolicitud(@Path("bukrs") String bukrs,@Path("ruta") String ruta,@Path("idform") String idform,@Path("estado") String estado,@Path("comentario") String comentario);

    @Streaming
    @POST("api"+ftp+"/ConsultarHacienda/{bukrs}/{cedula}")
    Call<ResponseBody> ConsultarHacienda(@Path("bukrs") String bukrs, @Path("cedula") String cedula);

    @Streaming
    @GET("api"+ftp+"/TraerEquipoDisponible/{bukrs}/{ruta}/{version}/{centro}/{tipoformulario}/{numpuertas}")
    Call<ResponseBody> TraerEquipoDisponible(@Path("bukrs") String bukrs, @Path("ruta") String ruta, @Path("version") String version, @Path("centro") String centro, @Path("tipoformulario") String tipoformulario, @Path("numpuertas") String numpuertas);
}
