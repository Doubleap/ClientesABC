package proyecto.app.clientesabc.clases;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class Validaciones {
    public static boolean ValidarCoordenadaY(View v){
        TextView texto = (TextView)v;
        String coordenadaY;
        Pattern pattern;
        Matcher matcher;
        switch (PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS","")){
            case "F443":
                if(texto.getText().toString().replace("0","").replace(".","").isEmpty()){
                    texto.setError("El campo "+texto.getTag()+" no puede ser 0!");
                    return false;
                }
                coordenadaY = "^[-]?(([8-9]|[1][0-2])(\\.\\d{3,12}+)?)";
                pattern = Pattern.compile(coordenadaY);
                matcher = pattern.matcher(texto.getText().toString().trim());
                if (!matcher.matches()) {
                    texto.setError("Formato Coordenada Y "+texto.getText().toString().trim()+" invalido!");
                    return false;
                }
                break;
            case "F445":
            case "F446":
            case "F451":
            case "1657":
            case "1658":
                coordenadaY = "^(([1-9][0-9]?)(\\.\\d{3,12}+)?)";
                pattern = Pattern.compile(coordenadaY);
                matcher = pattern.matcher(texto.getText().toString().trim());
                if (!matcher.matches()) {
                    texto.setError("Formato Coordenada Y "+texto.getText().toString().trim()+" invalido!");
                    return false;
                }
                break;
        }
        return true;
    }
    public static boolean ValidarCoordenadaX(View v){
        TextView texto = (TextView)v;
        String coordenadaX;
        Pattern pattern;
        Matcher matcher;
        switch (PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("W_CTE_BUKRS","")){
            case "F443":
                if(texto.getText().toString().replace("0","").replace(".","").isEmpty()){
                    texto.setError("El campo "+texto.getTag()+" no puede ser 0!");
                    return false;
                }
                coordenadaX = "^[-](([8][2-6])(\\.\\d{3,12}+)?)";
                pattern = Pattern.compile(coordenadaX);
                matcher = pattern.matcher(texto.getText().toString().trim());
                if (!matcher.matches()) {
                    texto.setError("Formato Coordenada X "+ texto.getText().toString().trim()+" invalido!");
                    return false;
                }
                break;
            case "F445":
            case "F446":
            case "F451":
            case "1657":
            case "1658":
                coordenadaX = "^[-](([1-9][0-9])(\\.\\d{3,12}+)?)";
                pattern = Pattern.compile(coordenadaX);
                matcher = pattern.matcher(texto.getText().toString().trim());
                if (!matcher.matches()) {
                    texto.setError("Formato Coordenada X "+ texto.getText().toString().trim()+" invalido!");
                    return false;
                }
                break;
        }
        //Toasty.success(texto.getContext(),"Formato Coordenada X "+valor+" valido!").show();
        return true;
    }
    public final static boolean isValidEmail(View v) {
        TextView correo = (TextView)v;
        boolean valido = !TextUtils.isEmpty(correo.getText()) && android.util.Patterns.EMAIL_ADDRESS.matcher(correo.getText()).matches();
        if(valido)
            Toasty.success(correo.getContext(),"Formato de correo valido!").show();
        else
            Toasty.error(correo.getContext(),"Formato de correo Invalido!").show();
        return valido;
    }
}
