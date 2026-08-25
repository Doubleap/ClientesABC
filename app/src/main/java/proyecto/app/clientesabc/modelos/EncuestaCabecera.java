package proyecto.app.clientesabc.modelos;

import org.chalup.microorm.annotations.Column;

import java.util.ArrayList;
import java.util.List;

public class EncuestaCabecera {


    @Column("id_encuesta")
    private int id;
    @Column("nombre")
    private String nombre;
    @Column("descripcion")
    private String descripcion;
    @Column("id_bukrs")
    private String bukrs;
    @Column("fecha_inicio")
    private String fecha_inicio;
    @Column("fecha_fin")
    private String fecha_fin;
    @Column("gvc")
    private Boolean gvc;


    private String valorGVC;
    private List<String> rutas=new ArrayList<String>();
    private List<String> bzirks=new ArrayList<String>();

    public EncuestaCabecera() {
    }

    public EncuestaCabecera(int id, String nombre, String descripcion, String bukrs, String fecha_inicio, String fecha_fin,boolean gvc) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.bukrs = bukrs;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.gvc = gvc;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getBukrs() {
        return bukrs;
    }

    public void setBukrs(String bukrs) {
        this.bukrs = bukrs;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public List<String> getRutas() {
        return rutas;
    }

    public void setRutas(List<String> rutas) {
        this.rutas = rutas;
    }

    public List<String> getBzirks() {
        return bzirks;
    }

    public void setBzirks(List<String> bzirks) {
        this.bzirks = bzirks;
    }

    public boolean isGvc() {
        return gvc;
    }

    public void setGvc(boolean gvc) {
        this.gvc = gvc;
    }

    public String getValorGVC() {
        return valorGVC;
    }

    public void setValorGVC(String valorGVC) {
        this.valorGVC = valorGVC;
    }
}
