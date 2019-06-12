package proyecto.app.clientesabc.Modelos;

public class Interlocutor {
    private String id_interlocutor;
    private String id_formulario;
    private String id_solicitud;
    private String parvw;
    private String vtext;
    private String kunn2;
    private String name1;

    public String getId_interlocutor() {
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
