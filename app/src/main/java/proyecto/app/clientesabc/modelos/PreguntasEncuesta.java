package proyecto.app.clientesabc.modelos;


import org.chalup.microorm.annotations.Column;
import org.chalup.microorm.annotations.Embedded;

import java.util.List;

public class PreguntasEncuesta {


    @Column("id_preguntas_encuesta")
    private int id;
    @Column("nombreEncuesta")
    private String encuesta;
    @Column("desc_bukrs")
    private String sociedad;
    @Column("id_tipo_pregunta")
    private int tipoPregunta;
    @Column("texto")
    private String texto;
    @Column("tooltip")
    private String tooltip;
    @Column("orden")
    private int orden;


    private List<OpcionesRespuesta> opciones;

    public PreguntasEncuesta() {
    }

    public PreguntasEncuesta(int id, String encuesta, String sociedad, int tipoPregunta, String texto, String tooltip, int orden) {
        this.id = id;
        this.encuesta = encuesta;
        this.sociedad = sociedad;
        this.tipoPregunta = tipoPregunta;
        this.texto = texto;
        this.tooltip = tooltip;
        this.orden = orden;
    }

    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }

    public String getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(String encuesta) {
        this.encuesta = encuesta;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public int getTipoPregunta() {
        return tipoPregunta;
    }

    public void setTipoPregunta(int tipoPregunta) {
        this.tipoPregunta = tipoPregunta;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public List<OpcionesRespuesta> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<OpcionesRespuesta> opciones) {
        this.opciones = opciones;
    }
}
