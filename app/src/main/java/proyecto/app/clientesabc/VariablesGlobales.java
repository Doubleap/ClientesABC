package proyecto.app.clientesabc;

import android.app.Application;
import android.widget.Spinner;

import proyecto.app.clientesabc.Modelos.OpcionSpinner;

public class VariablesGlobales extends Application {
    private static String land1 = "CR";
    private static String sociedad = "F443";
    private static String orgvta = "0443";
    private static String rutapreventa = "CRP901";
    private static String ipcon = "192.168.0.13";
    //private static String ipcon = "10.124.4.137";
    private static int puertocon = 3345;

    //Condicionales según la ruta de Preventa
    private static String bzirk = "CR0001";
    private static String kdgrp = "R1";
    private static String kvgr3 = "CR1";
    private static String vkbur = "AJ01";
    private static String vkgrp = "AE2";

    private static String TABLA_BLOQUE_CONTACTO_HH = "grid_contacto_solicitud";
    private static String TABLA_BLOQUE_BANCO_HH = "grid_bancos_solicitud";
    private static String TABLA_BLOQUE_IMPUESTO_HH = "grid_impuestos_solicitud";
    private static String TABLA_BLOQUE_INTERLOCUTOR_HH = "grid_interlocutor_solicitud";
    private static String TABLA_BLOQUE_VISITA_HH = "grid_visitas_solicitud";
    private static String TABLA_ADJUNTOS_SOLICITUD = "adjuntos_solicitud";
    private static String TABLA_ENCUESTA_SOLICITUD = "encuesta_solicitud";
    private static String TABLA_ENCUESTA_GEC_SOLICITUD = "encuesta_gec_solicitud";

    public static String getLand1() {
        return land1;
    }
    public static String getSociedad() {
        return sociedad;
    }
    public static String getOrgVta() {
        return orgvta;
    }
    public static String getRutaPreventa() {
        return rutapreventa;
    }

    public static String getCampo(String campo){
        String valor = "";
        switch(campo){
            case "W_CTE-BZIRK":
                valor = bzirk;
                break;
            case "W_CTE-KDGRP":
                valor = kdgrp;
                break;
            case "W_CTE-KVGR3":
                valor = kvgr3;
                break;
            case "W_CTE-VKBUR":
                valor = vkbur;
                break;
            case "W_CTE-VKGRP":
                valor = vkgrp;
                break;
        }
        return valor;
    }

    public static String getBzirk() {
        return bzirk;
    }

    public static void setBzirk(String bzirk) {
        VariablesGlobales.bzirk = bzirk;
    }

    public static String getKdgrp() {
        return kdgrp;
    }

    public static void setKdgrp(String kdgrp) {
        VariablesGlobales.kdgrp = kdgrp;
    }

    public static String getKvgr3() {
        return kvgr3;
    }

    public static void setKvgr3(String kvgr3) {
        VariablesGlobales.kvgr3 = kvgr3;
    }

    public static String getVkbur() {
        return vkbur;
    }

    public static void setVkbur(String vkbur) {
        VariablesGlobales.vkbur = vkbur;
    }

    public static String getVkgrp() {
        return vkgrp;
    }

    public static void setVkgrp(String vkgrp) {
        VariablesGlobales.vkgrp = vkgrp;
    }

    public static String getTablaEncuestaSolicitud() {
        return TABLA_ENCUESTA_SOLICITUD;
    }

    public static String getTablaEncuestaGecSolicitud() {
        return TABLA_ENCUESTA_GEC_SOLICITUD;
    }

    public static String getIpcon() {
        return ipcon;
    }

    public static void setIpcon(String ipcon) {
        VariablesGlobales.ipcon = ipcon;
    }

    public static int getPuertocon() {
        return puertocon;
    }

    public static void setPuertocon(int puertocon) {
        VariablesGlobales.puertocon = puertocon;
    }

    public void setRutaPreventa(String nuevaRuta) {
        rutapreventa = nuevaRuta;
    }

    public static String getTABLA_BLOQUE_CONTACTO_HH() {
        return TABLA_BLOQUE_CONTACTO_HH;
    }

    public static String getTABLA_BLOQUE_BANCO_HH() {
        return TABLA_BLOQUE_BANCO_HH;
    }

    public static String getTABLA_BLOQUE_IMPUESTO_HH() {
        return TABLA_BLOQUE_IMPUESTO_HH;
    }

    public static String getTABLA_BLOQUE_INTERLOCUTOR_HH() {
        return TABLA_BLOQUE_INTERLOCUTOR_HH;
    }

    public static String getTABLA_BLOQUE_VISITA_HH() {
        return TABLA_BLOQUE_VISITA_HH;
    }
    public static String getTABLA_ADJUNTOS_SOLICITUD() {
        return TABLA_ADJUNTOS_SOLICITUD;
    }

    //Get IndexOf by value ID on some Spinner
    public static int getIndex(Spinner spinner, String valorId){
        int pos=-1;
        for (int i=0;i<spinner.getCount();i++){
            if (((OpcionSpinner)spinner.getItemAtPosition(i)).getId().equals(valorId)){
                pos = i;
                break;
            }
        }
        return pos;
    }
}