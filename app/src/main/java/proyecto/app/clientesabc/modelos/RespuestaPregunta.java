package proyecto.app.clientesabc.modelos;

import org.chalup.microorm.annotations.Column;

import java.sql.Date;
import java.util.List;

public class RespuestaPregunta {

    @Column("id")
    private int id;

    @Column("GUID")
    private String GUID;

    @Column("bukrs")
    private String sociedad;

    @Column("texto_encuesta")
    private  String encuesta;

    @Column("id_encuesta")
    private  String idEncuesta;

    @Column(value = "fecha_ejecucion",treatNullAsDefault = true)
    private String fecha;

    @Column("codigo_cliente")
    private String codigoCliente;

    @Column("nombre_cliente")
    private String nombreCliente;

    @Column("id_pregunta_encuesta")
    private int idPregunta;

    @Column("id_tipo_pregunta")
    private String idTipoPregunta;

    @Column("texto_pregunta")
    private String textoPregunta;

    @Column("id_respuesta")
    private String idRespuesta;

    @Column("id_texto_respuesta")
    private String idTextoRespuesta;

    @Column("respuesta")
    private String respuesta;

    @Column("estado")
    private String estado;

    @Column("imagenPath")
    private String imagenPath;

    @Column("imagenUrl")
    private String imagenUrl;

    public RespuestaPregunta() {
    }

    public RespuestaPregunta(int id, String GUID, String sociedad, String encuesta, String fecha, String codigoCliente, String nombreCliente, int idPregunta, String idTipoPregunta, String textoPregunta, String idRespuesta, String idTextoRespuesta, String respuesta) {
        this.id = id;
        this.GUID = GUID;
        this.sociedad = sociedad;
        this.encuesta = encuesta;
        this.fecha = fecha;
        this.codigoCliente = codigoCliente;
        this.nombreCliente = nombreCliente;
        this.idPregunta = idPregunta;
        this.idTipoPregunta = idTipoPregunta;
        this.textoPregunta = textoPregunta;
        this.idRespuesta = idRespuesta;
        this.idTextoRespuesta = idTextoRespuesta;
        this.respuesta = respuesta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public String getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(String encuesta) {
        this.encuesta = encuesta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getIdTipoPregunta() {
        return idTipoPregunta;
    }

    public void setIdTipoPregunta(String idTipoPregunta) {
        this.idTipoPregunta = idTipoPregunta;
    }

    public String getTextoPregunta() {
        return textoPregunta;
    }

    public void setTextoPregunta(String textoPregunta) {
        this.textoPregunta = textoPregunta;
    }

    public String getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(String idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public String getIdTextoRespuesta() {
        return idTextoRespuesta;
    }

    public void setIdTextoRespuesta(String idTextoRespuesta) {
        this.idTextoRespuesta = idTextoRespuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(String idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    public String getImagenPath() {
        return imagenPath;
    }

    public void setImagenPath(String path) {
        this.imagenPath = path;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String url) {
        this.imagenUrl = url;
    }
}
