package proyecto.app.clientesabc.modelos;

import com.google.gson.annotations.SerializedName;

public class EquipoFrio implements Cloneable{
    @SerializedName("VKORG")
    private String vkorg;
    @SerializedName("KDGRP")
    private String kdgrp;
    @SerializedName("BZIRK")
    private String bzirk;
    @SerializedName("KUNNR")
    private String kunnr;
    @SerializedName("IBASE")
    private String ibase;
    @SerializedName("INSTANCE")
    private String instance;
    @SerializedName("OBJECTTYP")
    private String objecttyp;
    @SerializedName("OBJNR")
    private String objnr;
    @SerializedName("EQUNR")
    private String equnr;
    @SerializedName("MATNR")
    private String matnr;
    @SerializedName("EQART")
    private String eqart;
    @SerializedName("HERST")
    private String herst;
    @SerializedName("EQKTX")
    private String eqktx;
    @SerializedName("SPRAS")
    private String spras;
    @SerializedName("MATKL")
    private String matkl;
    @SerializedName("SERGE")
    private String serge;
    @SerializedName("SERNR")
    private String sernr;
    @SerializedName("ESTADO")
    private String estado;

    public EquipoFrio() { }
    public EquipoFrio(String vkorg, String kdgrp, String bzirk, String kunnr, String ibase, String instance, String objecttyp, String objnr, String equnr, String matnr, String eqart, String herst, String eqktx, String spras, String matkl, String serge, String sernr) {
        this.vkorg = vkorg;
        this.kdgrp = kdgrp;
        this.bzirk = bzirk;
        this.kunnr = kunnr;
        this.ibase = ibase;
        this.instance = instance;
        this.objecttyp = objecttyp;
        this.objnr = objnr;
        this.equnr = equnr;
        this.matnr = matnr;
        this.eqart = eqart;
        this.herst = herst;
        this.eqktx = eqktx;
        this.spras = spras;
        this.matkl = matkl;
        this.serge = serge;
        this.sernr = sernr;
        this.estado = "Pendiente";
    }



    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getEqunr();
                break;
            case 1:
                valorCelda = getMatnr();
                break;
            case 2:
                valorCelda = getEqktx();
                break;
            case 3:
                valorCelda = getSernr();
                break;
            case 4:
                valorCelda = getKunnr();
                break;
        }
        return valorCelda;
    }

    public boolean validarObligatorios() {
        return (!this.getKunnr().trim().equals(""));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getVkorg() {
        return vkorg;
    }

    public void setVkorg(String vkorg) {
        this.vkorg = vkorg;
    }

    public String getKdgrp() {
        return kdgrp;
    }

    public void setKdgrp(String kdgrp) {
        this.kdgrp = kdgrp;
    }

    public String getBzirk() {
        return bzirk;
    }

    public void setBzirk(String bzirk) {
        this.bzirk = bzirk;
    }

    public String getKunnr() {
        return kunnr;
    }

    public void setKunnr(String kunnr) {
        this.kunnr = kunnr;
    }

    public String getIbase() {
        return ibase;
    }

    public void setIbase(String ibase) {
        this.ibase = ibase;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getObjecttyp() {
        return objecttyp;
    }

    public void setObjecttyp(String objecttyp) {
        this.objecttyp = objecttyp;
    }

    public String getObjnr() {
        return objnr;
    }

    public void setObjnr(String objnr) {
        this.objnr = objnr;
    }

    public String getEqunr() {
        return equnr;
    }

    public void setEqunr(String equnr) {
        this.equnr = equnr;
    }

    public String getMatnr() {
        return matnr;
    }

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public String getEqart() {
        return eqart;
    }

    public void setEqart(String eqart) {
        this.eqart = eqart;
    }

    public String getHerst() {
        return herst;
    }

    public void setHerst(String herst) {
        this.herst = herst;
    }

    public String getEqktx() {
        return eqktx;
    }

    public void setEqktx(String eqktx) {
        this.eqktx = eqktx;
    }

    public String getSpras() {
        return spras;
    }

    public void setSpras(String spras) {
        this.spras = spras;
    }

    public String getMatkl() {
        return matkl;
    }

    public void setMatkl(String matkl) {
        this.matkl = matkl;
    }

    public String getSerge() {
        return serge;
    }

    public void setSerge(String serge) {
        this.serge = serge;
    }

    public String getSernr() {
        return sernr;
    }

    public void setSernr(String sernr) {
        this.sernr = sernr;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
