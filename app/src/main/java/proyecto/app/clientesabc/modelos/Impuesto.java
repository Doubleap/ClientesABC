package proyecto.app.clientesabc.modelos;

import com.google.gson.annotations.SerializedName;

public class Impuesto implements Cloneable{
    private String id_impuestos;
    private String id_solicitud;
    private String id_formulario;
    @SerializedName("W_CTE-TATYP")
    private String tatyp;
    @SerializedName("W_CTE-VTEXT")
    private String vtext;
    @SerializedName("W_CTE-TAXKD")
    private String taxkd;
    @SerializedName("W_CTE-VTEXT2")
    private String vtext2;

    public Impuesto(String id_impuestos, String id_solicitud, String id_formulario, String tatyp, String vtext, String taxkd, String vtext2) {
        this.id_impuestos = id_impuestos;
        this.id_solicitud = id_solicitud;
        this.id_formulario = id_formulario;
        this.tatyp = tatyp;
        this.vtext = vtext;
        this.taxkd = taxkd;
        this.vtext2 = vtext2;
    }

    public Impuesto() {
    }

    private String getId_impuestos() {
        return id_impuestos;
    }

    public void setId_impuestos(String id_impuestos) {
        this.id_impuestos = id_impuestos;
    }

    public String getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(String id_formulario) {
        this.id_formulario = id_formulario;
    }

    public String getTatyp() {
        return tatyp;
    }

    public void setTatyp(String tatyp) {
        this.tatyp = tatyp;
    }

    public String getVtext() {
        return vtext;
    }

    public void setVtext(String vtext) {
        this.vtext = vtext;
    }

    public String getTaxkd() {
        return taxkd;
    }

    public void setTaxkd(String taxkd) {
        this.taxkd = taxkd;
    }

    public String getVtext2() {
        return vtext2;
    }

    public void setVtext2(String vtext2) {
        this.vtext2 = vtext2;
    }

    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getId_impuestos();
                break;
            case 1:
                valorCelda = getId_formulario();
                break;
            case 2:
                valorCelda = getTatyp();
                break;
            case 3:
                valorCelda = getVtext();
                break;
            case 4:
                valorCelda = getTaxkd();
                break;
            case 5:
                valorCelda = getVtext2();
                break;
        }
        return valorCelda;
    }

    public String getId_solicitud() {
        return id_solicitud;
    }

    public void setId_solicitud(String id_solicitud) {
        this.id_solicitud = id_solicitud;
    }

    public boolean validarObligatorios() {
        return (!this.getTatyp().trim().equals("") && !this.getTaxkd().trim().equals(""));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
