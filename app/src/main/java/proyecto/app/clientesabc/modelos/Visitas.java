package proyecto.app.clientesabc.modelos;

public class Visitas {
    private String id_formulario;
    private String id_solicitud;
    private String vptyp;
    private String kvgr4;
    private String ruta;
    private String lun_de;
    private String lun_a;
    private String mar_de;
    private String mar_a;
    private String mier_de;
    private String mier_a;
    private String jue_de;
    private String jue_a;
    private String vie_de;
    private String vie_a;
    private String sab_de;
    private String sab_a;
    private String dom_de;
    private String dom_a;
    private String f_ini;
    private String f_fin;
    private String f_frec;
    private String f_ico;
    private String f_fco;
    private String fcalid;

    public String getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(String id_formulario) {
        this.id_formulario = id_formulario;
    }

    private String getId_solicitud() {
        return id_solicitud;
    }

    public void setId_solicitud(String id_solicitud) {
        this.id_solicitud = id_solicitud;
    }

    public String getVptyp() {
        return vptyp;
    }

    public void setVptyp(String vptyp) {
        this.vptyp = vptyp;
    }

    public String getKvgr4() {
        return kvgr4;
    }

    public void setKvgr4(String kvgr4) {
        this.kvgr4 = kvgr4;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getLun_de() {
        return lun_de;
    }

    public void setLun_de(String lun_de) {
        this.lun_de = lun_de;
    }

    public String getLun_a() {
        return lun_a;
    }

    public void setLun_a(String lun_a) {
        this.lun_a = lun_a;
    }

    public String getMar_de() {
        return mar_de;
    }

    public void setMar_de(String mar_de) {
        this.mar_de = mar_de;
    }

    public String getMar_a() {
        return mar_a;
    }

    public void setMar_a(String mar_a) {
        this.mar_a = mar_a;
    }

    public String getMier_de() {
        return mier_de;
    }

    public void setMier_de(String mier_de) {
        this.mier_de = mier_de;
    }

    public String getMier_a() {
        return mier_a;
    }

    public void setMier_a(String mier_a) {
        this.mier_a = mier_a;
    }

    public String getJue_de() {
        return jue_de;
    }

    public void setJue_de(String jue_de) {
        this.jue_de = jue_de;
    }

    public String getJue_a() {
        return jue_a;
    }

    public void setJue_a(String jue_a) {
        this.jue_a = jue_a;
    }

    public String getVie_de() {
        return vie_de;
    }

    public void setVie_de(String vie_de) {
        this.vie_de = vie_de;
    }

    public String getVie_a() {
        return vie_a;
    }

    public void setVie_a(String vie_a) {
        this.vie_a = vie_a;
    }

    public String getSab_de() {
        return sab_de;
    }

    public void setSab_de(String sab_de) {
        this.sab_de = sab_de;
    }

    public String getSab_a() {
        return sab_a;
    }

    public void setSab_a(String sab_a) {
        this.sab_a = sab_a;
    }

    public String getDom_de() {
        return dom_de;
    }

    public void setDom_de(String dom_de) {
        this.dom_de = dom_de;
    }

    public String getDom_a() {
        return dom_a;
    }

    public void setDom_a(String dom_a) {
        this.dom_a = dom_a;
    }

    public String getF_ini() {
        return f_ini;
    }

    public void setF_ini(String f_ini) {
        this.f_ini = f_ini;
    }

    public String getF_fin() {
        return f_fin;
    }

    public void setF_fin(String f_fin) {
        this.f_fin = f_fin;
    }

    public String getF_frec() {
        return f_frec;
    }

    public void setF_frec(String f_frec) {
        this.f_frec = f_frec;
    }

    public String getF_ico() {
        return f_ico;
    }

    public void setF_ico(String f_ico) {
        this.f_ico = f_ico;
    }

    public String getF_fco() {
        return f_fco;
    }

    public void setF_fco(String f_fco) {
        this.f_fco = f_fco;
    }

    public String getFcalid() {
        return fcalid;
    }

    public void setFcalid(String fcalid) {
        this.fcalid = fcalid;
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
                valorCelda = getVptyp();
                break;
            case 3:
                valorCelda = getKvgr4();
                break;
            case 4:
                valorCelda = getRuta();
                break;
            case 5:
                valorCelda = getFcalid();
                break;
        }
        return valorCelda;
    }
}
