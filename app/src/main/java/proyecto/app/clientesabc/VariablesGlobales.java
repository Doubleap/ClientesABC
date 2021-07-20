package proyecto.app.clientesabc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.RegexInputFilter;
import proyecto.app.clientesabc.modelos.Visitas;

@SuppressLint("Registered")
public class VariablesGlobales extends Application {
    private static boolean usarAPI = true;
    public static boolean UsarAPI() {
        return usarAPI;
    }

    private static String urlApi = "http://kofcrofcdesa02:90/MaestroClientes/";
    private static String nombrePais = "Costa Rica";
    private static String sociedad = "F443";
    private static String orgvta = "0443";
    private static String land1 = "CR";
    private static String cadenaRM = "0000160000";
    private static String ktokd = "RCMA";

    /*
    private static String nombrePais = "Costa Rica";
    private static String sociedad = "F443";
    private static String orgvta = "0443";
    private static String land1 = "CR";
    private static String cadenaRM = "0000160000";
    private static String ktokd = "RCMA";

    private static String nombrePais = "Guatemala Embocen";
    private static String sociedad = "F446";
    private static String orgvta = "0446";
    private static String land1 = "GT";
    private static String cadenaRM = "0000170027";
    private static String ktokd = "GCMA";

    private static String nombrePais = "Guatemala Volcanes";
    private static String sociedad = "1657";
    private static String orgvta = "0657";
    private static String land1 = "GT";
    private static String cadenaRM = "0000210027";
    private static String ktokd = "GCMC";

    private static String nombrePais = "Guatemala Abasa";
    private static String sociedad = "1658";
    private static String orgvta = "0658";
    private static String land1 = "GT";
    private static String cadenaRM = "0000190027";
    private static String ktokd = "GCMB";

    private static String nombrePais = "Nicaragua";
    private static String sociedad = "F445";
    private static String orgvta = "0445";
    private static String land1 = "NI";
    private static String cadenaRM = "0000180000";
    private static String ktokd = "NCMA";

    private static String nombrePais = "Panamá";
    private static String sociedad = "F451";
    private static String orgvta = "0451";
    private static String land1 = "PA";
    private static String cadenaRM = "0000200000";
    private static String ktokd = "PCMA";

    private static String nombrePais = "Uruguay";
    private static String sociedad = "1661";
    private static String orgvta = "0661";
    private static String land1 = "UY";
    private static String cadenaRM = "0000240000";
    private static String ktokd = "UYDE";
    */

    private static String TABLA_BLOQUE_CONTACTO_HH = "grid_contacto_solicitud";
    private static String TABLA_BLOQUE_BANCO_HH = "grid_bancos_solicitud";
    private static String TABLA_BLOQUE_IMPUESTO_HH = "grid_impuestos_solicitud";
    private static String TABLA_BLOQUE_INTERLOCUTOR_HH = "grid_interlocutor_solicitud";
    private static String TABLA_BLOQUE_VISITA_HH = "grid_visitas_solicitud";
    private static String TABLA_ADJUNTOS_SOLICITUD = "adjuntos_solicitud";
    private static String TABLA_ENCUESTA_SOLICITUD = "encuesta_solicitud";
    private static String TABLA_ENCUESTA_GEC_SOLICITUD = "encuesta_gec_solicitud";

    public static String getTablaHorariosSolicitud() {
        return TABLA_HORARIOS_SOLICITUD;
    }

    public static void setTablaHorariosSolicitud(String tablaHorariosSolicitud) {
        TABLA_HORARIOS_SOLICITUD = tablaHorariosSolicitud;
    }

    public static String getTablaHorariosOldSolicitud() {
        return TABLA_HORARIOS_OLD_SOLICITUD;
    }

    public static void setTablaHorariosOldSolicitud(String tablaHorariosOldSolicitud) {
        TABLA_HORARIOS_OLD_SOLICITUD = tablaHorariosOldSolicitud;
    }

    private static String TABLA_HORARIOS_SOLICITUD = "horarios_solicitud";

    private static String TABLA_BLOQUE_CONTACTO_OLD_HH = "grid_contacto_old_solicitud";
    private static String TABLA_BLOQUE_BANCO_OLD_HH = "grid_bancos_old_solicitud";
    private static String TABLA_BLOQUE_IMPUESTO_OLD_HH = "grid_impuestos_old_solicitud";
    private static String TABLA_BLOQUE_INTERLOCUTOR_OLD_HH = "grid_interlocutor_old_solicitud";
    private static String TABLA_BLOQUE_VISITA_OLD_HH = "grid_visitas_old_solicitud";
    private static String TABLA_ENCUESTA_OLD_SOLICITUD = "encuesta_old_solicitud";
    private static String TABLA_ENCUESTA_OLD_GEC_SOLICITUD = "encuesta_gec_old_solicitud";
    private static String TABLA_HORARIOS_OLD_SOLICITUD = "horarios_old_solicitud";

    public static String getTablaEncuestaSolicitud() {
        return TABLA_ENCUESTA_SOLICITUD;
    }

    public static String getTablaEncuestaGecSolicitud() {
        return TABLA_ENCUESTA_GEC_SOLICITUD;
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

    public static int getIndiceTipoVisita(ArrayList<Visitas> visitasSolicitud, String tipo) {
        int pos = -1;
        try {
            for (int i = 0; i < visitasSolicitud.size(); i++) {
                if (visitasSolicitud.get(i).getVptyp().trim().equals(tipo)) {
                    pos = i;
                }
            }
        }catch(Exception e){

        }
        return pos;
    }

    public static String SecuenciaToHora(String secuencia) {
        int hours = Integer.valueOf(secuencia) / 60;
        int minutes = Integer.valueOf(secuencia) % 60;
        String h = String.format(Locale.getDefault(),"%02d", hours);
        String m = String.format(Locale.getDefault(),"%02d", minutes);
        String secuenciaSAP = h+m;
        return secuenciaSAP;
    }

    public static String HoraToSecuencia(String hora) {
        String secuencia = "";
        hora = String.format("%1$" + 4 + "s", hora).replace(' ', '0');

        if((hora != null && !hora.equals("null")) && !hora.equals("0999") && hora.length() == 4) {
            String h = hora.substring(0, 2);
            String m = hora.substring(2, 4);
            secuencia = String.valueOf(Integer.valueOf(h) * 60 + Integer.valueOf(m));
        }

        return secuencia.equals("0")?"":secuencia;
    }

    public static String validarConexionDePreferencia(Context context){
        String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
        String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
        Pattern IP_PATTERN = Pattern.compile(IP_REGEXP);
        String retorno = "";
        try {
            if(!IP_PATTERN.matcher(PreferenceManager.getDefaultSharedPreferences(context).getString("Ip", "").trim()).matches())
                return "La IP '"+PreferenceManager.getDefaultSharedPreferences(context).getString("Ip", "").trim()+"' es inválida. Revise los datos de comunicación.";
        }catch (Exception e){
            return "La IP '"+PreferenceManager.getDefaultSharedPreferences(context).getString("Ip", "").trim()+"' es inválida. Revise los datos de comunicación.";
        }
        try {
            Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("Puerto","").trim());
        }catch (Exception e){
            return "El puerto '"+PreferenceManager.getDefaultSharedPreferences(context).getString("Puerto","").trim()+"' es inválido. Revise los datos de comunicación.";
        }

        return retorno;
    }

    public static String getSociedad() {
        return sociedad;
    }

    public static void setSociedad(String sociedad) {
        VariablesGlobales.sociedad = sociedad;
    }

    public static String getOrgvta() {
        return orgvta;
    }

    public static void setOrgvta(String orgvta) {
        VariablesGlobales.orgvta = orgvta;
    }

    public static String getUrlApi() {
        return urlApi;
    }

    public static void setUrlApi(String urlApi) {
        VariablesGlobales.urlApi = urlApi;
    }

    public static String UsuarioHH2UsuarioMC(Context context, String usuarioHH) {
        String usuarioMC = "";
        String padded = "";
        try {
            Integer.parseInt(usuarioHH);
            padded = "00000000".substring(usuarioHH.length()) + usuarioHH;
            usuarioMC = getLand1()+padded;
        }catch (NumberFormatException ne){
            usuarioMC = usuarioHH;
        }
        return usuarioMC;
    }

    public static String UsuarioMC2UsuarioHH(Context context, String usuarioMC) {
        int numero = 0;
        String usuarioHH = usuarioMC;
        try {
            numero = Integer.parseInt(usuarioMC);
        }catch (NumberFormatException ne){
            try {
                if(usuarioMC.length() == 10) {
                    numero = Integer.parseInt(usuarioMC.substring(2, usuarioMC.length()));
                    usuarioHH = String.valueOf(numero);
                }else{
                    usuarioHH = usuarioMC;
                }
            }catch (NumberFormatException nex){
                usuarioHH = usuarioMC;
            }
        }
        return usuarioHH;
    }

    public static String getLand1() {
        return land1;
    }

    public static void setLand1(String land1) {
        VariablesGlobales.land1 = land1;
    }

    public static String getTABLA_BLOQUE_CONTACTO_OLD_HH() {
        return TABLA_BLOQUE_CONTACTO_OLD_HH;
    }

    public static void setTABLA_BLOQUE_CONTACTO_OLD_HH(String tablaBloqueContactoOldHh) {
        TABLA_BLOQUE_CONTACTO_OLD_HH = tablaBloqueContactoOldHh;
    }

    public static String getTABLA_BLOQUE_BANCO_OLD_HH() {
        return TABLA_BLOQUE_BANCO_OLD_HH;
    }

    public static void setTABLA_BLOQUE_BANCO_OLD_HH(String tablaBloqueBancoOldHh) {
        TABLA_BLOQUE_BANCO_OLD_HH = tablaBloqueBancoOldHh;
    }

    public static String getTABLA_BLOQUE_IMPUESTO_OLD_HH() {
        return TABLA_BLOQUE_IMPUESTO_OLD_HH;
    }

    public static void setTABLA_BLOQUE_IMPUESTO_OLD_HH(String tablaBloqueImpuestoOldHh) {
        TABLA_BLOQUE_IMPUESTO_OLD_HH = tablaBloqueImpuestoOldHh;
    }

    public static String getTABLA_BLOQUE_INTERLOCUTOR_OLD_HH() {
        return TABLA_BLOQUE_INTERLOCUTOR_OLD_HH;
    }

    public static void setTABLA_BLOQUE_INTERLOCUTOR_OLD_HH(String tablaBloqueInterlocutorOldHh) {
        TABLA_BLOQUE_INTERLOCUTOR_OLD_HH = tablaBloqueInterlocutorOldHh;
    }

    public static String getTABLA_BLOQUE_VISITA_OLD_HH() {
        return TABLA_BLOQUE_VISITA_OLD_HH;
    }

    public static void setTABLA_BLOQUE_VISITA_OLD_HH(String tablaBloqueVisitaOldHh) {
        TABLA_BLOQUE_VISITA_OLD_HH = tablaBloqueVisitaOldHh;
    }

    public static String getTABLA_ENCUESTA_OLD_SOLICITUD() {
        return TABLA_ENCUESTA_OLD_SOLICITUD;
    }

    public static void setTABLA_ENCUESTA_OLD_SOLICITUD(String tablaEncuestaOldSolicitud) {
        TABLA_ENCUESTA_OLD_SOLICITUD = tablaEncuestaOldSolicitud;
    }

    public static String getTABLA_ENCUESTA_OLD_GEC_SOLICITUD() {
        return TABLA_ENCUESTA_OLD_GEC_SOLICITUD;
    }

    public static void setTABLA_ENCUESTA_OLD_GEC_SOLICITUD(String tablaEncuestaOldGecSolicitud) {
        TABLA_ENCUESTA_OLD_GEC_SOLICITUD = tablaEncuestaOldGecSolicitud;
    }

    public static String getCadenaRM() {
        return cadenaRM;
    }

    public static void setCadenaRM(String cadenaRM) {
        VariablesGlobales.cadenaRM = cadenaRM;
    }

    public static String getKtokd() {
        return ktokd;
    }

    public static void setKtokd(String ktokd) {
        VariablesGlobales.ktokd = ktokd;
    }

    public static String getNombrePais() {
        return nombrePais;
    }

    public static void setNombrePais(String nombrePais) {
        VariablesGlobales.nombrePais = nombrePais;
    }

    public boolean getUsarAPI() {
        return usarAPI;
    }

    public void setUsarAPI(boolean usarAPI) {
        this.usarAPI = usarAPI;
    }
}