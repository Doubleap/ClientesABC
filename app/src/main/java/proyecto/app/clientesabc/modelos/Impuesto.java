package proyecto.app.clientesabc.modelos;

public class Impuesto {
    private String id_impuestos;
    private String id_formulario;
    private String tatyp;
    private String vtext;
    private String taxkd;
    private String vtext2;

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
}
