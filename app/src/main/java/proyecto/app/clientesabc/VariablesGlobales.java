package proyecto.app.clientesabc;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Locale;

import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.Visitas;

@SuppressLint("Registered")
public class VariablesGlobales extends Application {

    //private static String ipcon = "192.168.0.13";
    //private static int puertocon = 3345;
    private static String TABLA_BLOQUE_CONTACTO_HH = "grid_contacto_solicitud";
    private static String TABLA_BLOQUE_BANCO_HH = "grid_bancos_solicitud";
    private static String TABLA_BLOQUE_IMPUESTO_HH = "grid_impuestos_solicitud";
    private static String TABLA_BLOQUE_INTERLOCUTOR_HH = "grid_interlocutor_solicitud";
    private static String TABLA_BLOQUE_VISITA_HH = "grid_visitas_solicitud";
    private static String TABLA_ADJUNTOS_SOLICITUD = "adjuntos_solicitud";
    private static String TABLA_ENCUESTA_SOLICITUD = "encuesta_solicitud";
    private static String TABLA_ENCUESTA_GEC_SOLICITUD = "encuesta_gec_solicitud";

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
        int pos=-1;
        for (int i=0;i<visitasSolicitud.size();i++){
            if(visitasSolicitud.get(i).getVptyp().trim().equals(tipo)){
              pos = i;
            }
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

        if(hora != null && !hora.equals("0999") && hora.length() == 4) {
            String h = hora.substring(0, 2);
            String m = hora.substring(2, 4);
            secuencia = String.valueOf(Integer.valueOf(h) * 60 + Integer.valueOf(m));
        }

        return secuencia;
    }
}