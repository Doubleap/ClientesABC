package proyecto.app.clientesabc.modelos;

import android.content.Context;

import androidx.annotation.NonNull;



public class OpcionCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {
    private String idTexto;

    private OpcionesRespuesta opcionRespuesta;

    public OpcionCheckBox(@NonNull Context context) {
        super(context);
    }

    public String getIdTexto() {
        return idTexto;
    }

    public void setIdTexto(String idTexto) {
        this.idTexto = idTexto;
    }

    public OpcionesRespuesta getOpcionRespuesta() {
        return opcionRespuesta;
    }

    public void setOpcionRespuesta(OpcionesRespuesta opcionRespuesta) {
        this.opcionRespuesta = opcionRespuesta;
    }
}
