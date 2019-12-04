package proyecto.app.clientesabc.modelos;

import com.google.gson.annotations.SerializedName;

public class Interlocutor implements Cloneable{
    private String id_interlocutor;
    private String id_formulario;
    private String id_solicitud;
    @SerializedName("W_CTE-PARVW")
    private String parvw;
    @SerializedName("W_CTE-VTEXT")
    private String vtext;
    @SerializedName("W_CTE-KUNN2")
    private String kunn2;
    @SerializedName("W_CTE-NAME1")
    private String name1;

    public Interlocutor(String id_interlocutor, String id_formulario, String id_solicitud, String parvw, String vtext, String kunn2, String name1) {
        this.id_interlocutor = id_interlocutor;
        this.id_formulario = id_formulario;
        this.id_solicitud = id_solicitud;
        this.parvw = parvw;
        this.vtext = vtext;
        this.kunn2 = kunn2;
        this.name1 = name1;
    }

    public Interlocutor() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private String getId_interlocutor() {
        return id_interlocutor;
    }

    public void setId_interlocutor(String id_interlocutor) {
        this.id_interlocutor = id_interlocutor;
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

    public String getParvw() {
        return parvw;
    }

    public void setParvw(String parvw) {
        this.parvw = parvw;
    }

    public String getVtext() {
        return vtext;
    }

    public void setVtext(String vtext) {
        this.vtext = vtext;
    }

    public String getKunn2() {
        return kunn2;
    }

    public void setKunn2(String kunn2) {
        this.kunn2 = kunn2;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getId_interlocutor();
                break;
            case 1:
                valorCelda = getId_formulario();
                break;
            case 2:
                valorCelda = getParvw();
                break;
            case 3:
                valorCelda = getVtext();
                break;
            case 4:
                valorCelda = getKunn2();
                break;
            case 5:
                valorCelda = getName1();
                break;
        }
        return valorCelda;
    }
}
