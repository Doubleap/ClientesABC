package proyecto.app.clientesabc.modelos;

public class Comentario {
    private String id_formulario;
    private String id_solicitud;
    private String orden;
    private String etapa;
    private String  aprobador;
    private String  fecha;
    private String  comentario;
    private String  estado;

    public Comentario(){
    }
    public Comentario(String id_formulario, String id_solicitud, String orden, String etapa, String aprobador, String fecha, String comentario, String estado){
        this.id_formulario = id_formulario;
        this.id_solicitud = id_solicitud;
        this.orden = orden;
        this.etapa = etapa;
        this.aprobador = aprobador;
        this.fecha = fecha;
        this.comentario = comentario;
        this.estado = estado;
    }

    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getId_formulario();
                break;
            case 1:
                valorCelda = getId_solicitud();
                break;
            case 2:
                valorCelda = getOrden();
                break;
            case 3:
                valorCelda = getEtapa();
                break;
            case 4:
                valorCelda = getAprobador();
                break;
            case 5:
                valorCelda = getFecha();
                break;
            case 6:
                valorCelda = getComentario();
                break;
            case 7:
                valorCelda = getEstado();
                break;
        }
        return valorCelda;
    }

    public String getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(String id_formulario) {
        this.id_formulario = id_formulario;
    }

    public String getId_solicitud() {
        return id_solicitud;
    }

    public void setId_solicitud(String id_solicitud) {
        this.id_solicitud = id_solicitud;
    }

    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getAprobador() {
        return aprobador;
    }

    public void setAprobador(String aprobador) {
        this.aprobador = aprobador;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
