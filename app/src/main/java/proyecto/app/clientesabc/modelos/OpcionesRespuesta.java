package proyecto.app.clientesabc.modelos;

import org.chalup.microorm.annotations.Column;

public class OpcionesRespuesta {
    @Column("id_opciones_respuestas")
    private int id;
    @Column("id_preguntas_encuesta")
    private int idPregunta;
    @Column("texto")
    private String texto;
    @Column("id_texto")
    private String idTexto;

    public OpcionesRespuesta() {
    }

    public OpcionesRespuesta(int id, int idPregunta, String texto, String idTexto) {
        this.id = id;
        this.idPregunta = idPregunta;
        this.texto = texto;
        this.idTexto = idTexto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getIdTexto() {
        return idTexto;
    }

    public void setIdTexto(String idTexto) {
        this.idTexto = idTexto;
    }
}
