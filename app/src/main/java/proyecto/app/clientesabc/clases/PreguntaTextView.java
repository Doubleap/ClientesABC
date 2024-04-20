package proyecto.app.clientesabc.clases;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import proyecto.app.clientesabc.modelos.PreguntasEncuesta;

public class PreguntaTextView extends androidx.appcompat.widget.AppCompatTextView {

    PreguntasEncuesta preguntasEncuesta;

    public PreguntaTextView(@NonNull Context context) {
        super(context);
    }

    public PreguntaTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreguntaTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public PreguntasEncuesta getPreguntasEncuesta() {
        return preguntasEncuesta;
    }

    public void setPreguntasEncuesta(PreguntasEncuesta preguntasEncuesta) {
        this.preguntasEncuesta = preguntasEncuesta;
    }
}
