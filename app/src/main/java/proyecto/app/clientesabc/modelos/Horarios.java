package proyecto.app.clientesabc.modelos;

import com.google.gson.annotations.SerializedName;

public class Horarios implements Cloneable{

    private String id_formulario;
    private String id_solicitud;
    @SerializedName("MOAB1")
    private String moab1;
    @SerializedName("MOBI1")
    private String mobi1;
    @SerializedName("DIAB1")
    private String diab1;
    @SerializedName("DIBI1")
    private String dibi1;
    @SerializedName("MIAB1")
    private String miab1;
    @SerializedName("MIBI1")
    private String mibi1;
    @SerializedName("DOAB1")
    private String doab1;
    @SerializedName("DOBI1")
    private String dobi1;
    @SerializedName("FRAB1")
    private String frab1;
    @SerializedName("FRBI1")
    private String frbi1;
    @SerializedName("SAAB1")
    private String saab1;
    @SerializedName("SABI1")
    private String sabi1;
    @SerializedName("SOAB1")
    private String soab1;
    @SerializedName("SOBI1")
    private String sobi1;
    @SerializedName("MOAB2")
    private String moab2;
    @SerializedName("MOBI2")
    private String mobi2;
    @SerializedName("DIAB2")
    private String diab2;
    @SerializedName("DIBI2")
    private String dibi2;
    @SerializedName("MIAB2")
    private String miab2;
    @SerializedName("MIBI2")
    private String mibi2;
    @SerializedName("DOAB2")
    private String doab2;
    @SerializedName("DOBI2")
    private String dobi2;
    @SerializedName("FRAB2")
    private String frab2;
    @SerializedName("FRBI2")
    private String frbi2;
    @SerializedName("SAAB2")
    private String saab2;
    @SerializedName("SABI2")
    private String sabi2;
    @SerializedName("SOAB2")
    private String soab2;
    @SerializedName("SOBI2")
    private String sobi2;

    public Horarios(){}
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Horarios(String id_solicitud, String valorInicial){
        this.id_solicitud = id_solicitud;
        this.moab1 = valorInicial;
        this.mobi1 = valorInicial;
        this.diab1 = valorInicial;
        this.dibi1 = valorInicial;
        this.miab1 = valorInicial;
        this.mibi1 = valorInicial;
        this.doab1 = valorInicial;
        this.dobi1 = valorInicial;
        this.frab1 = valorInicial;
        this.frbi1 = valorInicial;
        this.saab1 = valorInicial;
        this.sabi1 = valorInicial;
        this.soab1 = valorInicial;
        this.sobi1 = valorInicial;
        this.moab2 = valorInicial;
        this.mobi2 = valorInicial;
        this.diab2 = valorInicial;
        this.dibi2 = valorInicial;
        this.miab2 = valorInicial;
        this.mibi2 = valorInicial;
        this.doab2 = valorInicial;
        this.dobi2 = valorInicial;
        this.frab2 = valorInicial;
        this.frbi2 = valorInicial;
        this.saab2 = valorInicial;
        this.sabi2 = valorInicial;
        this.soab2 = valorInicial;
        this.sobi2 = valorInicial;
    }

    public Horarios(String id_formulario, String id_solicitud, String moab1, String mobi1, String diab1, String dibi1, String miab1, String mibi1, String doab1, String dobi1, String frab1, String frbi1, String saab1, String sabi1, String soab1, String sobi1, String moab2, String mobi2, String diab2, String dibi2, String miab2, String mibi2, String doab2, String dobi2, String frab2, String frbi2, String saab2, String sabi2, String soab2, String sobi2) {
        this.id_formulario = id_formulario;
        this.id_solicitud = id_solicitud;
        this.moab1 = moab1;
        this.mobi1 = mobi1;
        this.diab1 = diab1;
        this.dibi1 = dibi1;
        this.miab1 = miab1;
        this.mibi1 = mibi1;
        this.doab1 = doab1;
        this.dobi1 = dobi1;
        this.frab1 = frab1;
        this.frbi1 = frbi1;
        this.saab1 = saab1;
        this.sabi1 = sabi1;
        this.soab1 = soab1;
        this.sobi1 = sobi1;
        this.moab2 = moab2;
        this.mobi2 = mobi2;
        this.diab2 = diab2;
        this.dibi2 = dibi2;
        this.miab2 = miab2;
        this.mibi2 = mibi2;
        this.doab2 = doab2;
        this.dobi2 = dobi2;
        this.frab2 = frab2;
        this.frbi2 = frbi2;
        this.saab2 = saab2;
        this.sabi2 = sabi2;
        this.soab2 = soab2;
        this.sobi2 = sobi2;
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

    public String getMoab1() {
        return moab1;
    }

    public void setMoab1(String moab1) {
        this.moab1 = moab1;
    }

    public String getMobi1() {
        return mobi1;
    }

    public void setMobi1(String mobi1) {
        this.mobi1 = mobi1;
    }

    public String getDiab1() {
        return diab1;
    }

    public void setDiab1(String diab1) {
        this.diab1 = diab1;
    }

    public String getDibi1() {
        return dibi1;
    }

    public void setDibi1(String dibi1) {
        this.dibi1 = dibi1;
    }

    public String getMiab1() {
        return miab1;
    }

    public void setMiab1(String miab1) {
        this.miab1 = miab1;
    }

    public String getMibi1() {
        return mibi1;
    }

    public void setMibi1(String mibi1) {
        this.mibi1 = mibi1;
    }

    public String getDoab1() {
        return doab1;
    }

    public void setDoab1(String doab1) {
        this.doab1 = doab1;
    }

    public String getDobi1() {
        return dobi1;
    }

    public void setDobi1(String dobi1) {
        this.dobi1 = dobi1;
    }

    public String getFrab1() {
        return frab1;
    }

    public void setFrab1(String frab1) {
        this.frab1 = frab1;
    }

    public String getFrbi1() {
        return frbi1;
    }

    public void setFrbi1(String frbi1) {
        this.frbi1 = frbi1;
    }

    public String getSaab1() {
        return saab1;
    }

    public void setSaab1(String saab1) {
        this.saab1 = saab1;
    }

    public String getSabi1() {
        return sabi1;
    }

    public void setSabi1(String sabi1) {
        this.sabi1 = sabi1;
    }

    public String getSoab1() {
        return soab1;
    }

    public void setSoab1(String soab1) {
        this.soab1 = soab1;
    }

    public String getSobi1() {
        return sobi1;
    }

    public void setSobi1(String sobi1) {
        this.sobi1 = sobi1;
    }

    public String getMoab2() {
        return moab2;
    }

    public void setMoab2(String moab2) {
        this.moab2 = moab2;
    }

    public String getMobi2() {
        return mobi2;
    }

    public void setMobi2(String mobi2) {
        this.mobi2 = mobi2;
    }

    public String getDiab2() {
        return diab2;
    }

    public void setDiab2(String diab2) {
        this.diab2 = diab2;
    }

    public String getDibi2() {
        return dibi2;
    }

    public void setDibi2(String dibi2) {
        this.dibi2 = dibi2;
    }

    public String getMiab2() {
        return miab2;
    }

    public void setMiab2(String miab2) {
        this.miab2 = miab2;
    }

    public String getMibi2() {
        return mibi2;
    }

    public void setMibi2(String mibi2) {
        this.mibi2 = mibi2;
    }

    public String getDoab2() {
        return doab2;
    }

    public void setDoab2(String doab2) {
        this.doab2 = doab2;
    }

    public String getDobi2() {
        return dobi2;
    }

    public void setDobi2(String dobi2) {
        this.dobi2 = dobi2;
    }

    public String getFrab2() {
        return frab2;
    }

    public void setFrab2(String frab2) {
        this.frab2 = frab2;
    }

    public String getFrbi2() {
        return frbi2;
    }

    public void setFrbi2(String frbi2) {
        this.frbi2 = frbi2;
    }

    public String getSaab2() {
        return saab2;
    }

    public void setSaab2(String saab2) {
        this.saab2 = saab2;
    }

    public String getSabi2() {
        return sabi2;
    }

    public void setSabi2(String sabi2) {
        this.sabi2 = sabi2;
    }

    public String getSoab2() {
        return soab2;
    }

    public void setSoab2(String soab2) {
        this.soab2 = soab2;
    }

    public String getSobi2() {
        return sobi2;
    }

    public void setSobi2(String sobi2) {
        this.sobi2 = sobi2;
    }

    public void actualizarCampo(String nombre, String valor){
        switch(nombre){
            case "moab1":
                setMoab1(valor);
                break;
            case "mobi1":
                setMobi1(valor);
                break;
            case "diab1":
                setDiab1(valor);
                break;
            case "dibi1":
                setDibi1(valor);
                break;
            case "miab1":
                setMiab1(valor);
                break;
            case "mibi1":
                setMibi1(valor);
                break;
            case "doab1":
                setDoab1(valor);
                break;
            case "dobi1":
                setDobi1(valor);
                break;
            case "frab1":
                setFrab1(valor);
                break;
            case "frbi1":
                setFrbi1(valor);
                break;
            case "saab1":
                setSaab1(valor);
                break;
            case "sabi1":
                setSabi1(valor);
                break;
            case "soab1":
                setSoab1(valor);
                break;
            case "sobi1":
                setSobi1(valor);
                break;

            case "moab2":
                setMoab2(valor);
                break;
            case "mobi2":
                setMobi2(valor);
                break;
            case "diab2":
                setDiab2(valor);
                break;
            case "dibi2":
                setDibi2(valor);
                break;
            case "miab2":
                setMiab2(valor);
                break;
            case "mibi2":
                setMibi2(valor);
                break;
            case "doab2":
                setDoab2(valor);
                break;
            case "dobi2":
                setDobi2(valor);
                break;
            case "frab2":
                setFrab2(valor);
                break;
            case "frbi2":
                setFrbi2(valor);
                break;
            case "saab2":
                setSaab2(valor);
                break;
            case "sabi2":
                setSabi2(valor);
                break;
            case "soab2":
                setSoab2(valor);
                break;
            case "sobi2":
                setSobi2(valor);
                break;
        }
    }

    public void copiarHorario(int dia){
        String man1= "00:00:00",man2= "00:00:00",tar1= "00:00:00",tar2 = "00:00:00";
        switch(dia){
            case 0:
                man1 = getMoab1();
                man2 = getMobi1();
                tar1 = getMoab2();
                tar2 = getMobi2();
                break;
            case 1:
                man1 = getDiab1();
                man2 = getDibi1();
                tar1 = getDiab2();
                tar2 = getDibi2();
                break;
            case 2:
                man1 = getMiab1();
                man2 = getMibi1();
                tar1 = getMiab2();
                tar2 = getMibi2();
                break;
            case 3:
                man1 = getDoab1();
                man2 = getDobi1();
                tar1 = getDoab2();
                tar2 = getDobi2();
                break;
            case 4:
                man1 = getFrab1();
                man2 = getFrbi1();
                tar1 = getFrab2();
                tar2 = getFrbi2();
                break;
            case 5:
                man1 = getSaab1();
                man2 = getSabi1();
                tar1 = getSaab2();
                tar2 = getSabi2();
                break;
            case 6:
                man1 = getSoab1();
                man2 = getSobi1();
                tar1 = getSoab2();
                tar2 = getSobi2();
                break;
        }
            this.moab1 = man1;
            this.mobi1 = man2;
            this.diab1 = man1;
            this.dibi1 = man2;
            this.miab1 = man1;
            this.mibi1 = man2;
            this.doab1 = man1;
            this.dobi1 = man2;
            this.frab1 = man1;
            this.frbi1 = man2;
            this.saab1 = man1;
            this.sabi1 = man2;
            this.soab1 = man1;
            this.sobi1 = man2;
            this.moab2 = tar1;
            this.mobi2 = tar2;
            this.diab2 = tar1;
            this.dibi2 = tar2;
            this.miab2 = tar1;
            this.mibi2 = tar2;
            this.doab2 = tar1;
            this.dobi2 = tar2;
            this.frab2 = tar1;
            this.frbi2 = tar2;
            this.saab2 = tar1;
            this.sabi2 = tar2;
            this.soab2 = tar1;
            this.sobi2 = tar2;
    }

    public String getPorNombre(String nombre) {
        String valor = "";
        switch(nombre){
            case "moab1":
                valor = getMoab1();
                break;
            case "mobi1":
                valor = getMobi1();
                break;
            case "diab1":
                valor = getDiab1();
                break;
            case "dibi1":
                valor = getDibi1();
                break;
            case "miab1":
                valor = getMiab1();
                break;
            case "mibi1":
                valor = getMibi1();
                break;
            case "doab1":
                valor = getDoab1();
                break;
            case "dobi1":
                valor = getDobi1();
                break;
            case "frab1":
                valor = getFrab1();
                break;
            case "frbi1":
                valor = getFrbi1();
                break;
            case "saab1":
                valor = getSaab1();
                break;
            case "sabi1":
                valor = getSabi1();
                break;
            case "soab1":
                valor = getSoab1();
                break;
            case "sobi1":
                valor = getSobi1();
                break;

            case "moab2":
                valor = getMoab2();
                break;
            case "mobi2":
                valor = getMobi2();
                break;
            case "diab2":
                valor = getDiab2();
                break;
            case "dibi2":
                valor = getDibi2();
                break;
            case "miab2":
                valor = getMiab2();
                break;
            case "mibi2":
                valor = getMibi2();
                break;
            case "doab2":
                valor = getDoab2();
                break;
            case "dobi2":
                valor = getDobi2();
                break;
            case "frab2":
                valor = getFrab2();
                break;
            case "frbi2":
                valor = getFrbi2();
                break;
            case "saab2":
                valor = getSaab2();
                break;
            case "sabi2":
                valor = getSabi2();
                break;
            case "soab2":
                valor = getSoab2();
                break;
            case "sobi2":
                valor = getSobi2();
                break;
        }
        return valor;
    }
}
