package proyecto.app.clientesabc.clases;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

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

    public final static void ejecutarExcepcion(Context context, View elemento,View label, HashMap<String, String> configExcepcion, ArrayList<String> listaCamposObligatorios, String campo){
        //Posibles tipo de elemento
        if(elemento instanceof CheckBox)
        {
            CheckBox checkbox = (CheckBox) elemento;
            if (configExcepcion.get("vis").equals("1") || configExcepcion.get("vis").equals("X")) {
                checkbox.setEnabled(false);
            }else if(configExcepcion.get("vis") != null){
                checkbox.setEnabled(true);
            }
            if (configExcepcion.get("sup").equals("1") || configExcepcion.get("sup").equals("X")) {
                checkbox.setVisibility(View.GONE);
            }else if(configExcepcion.get("sup") != null){
                checkbox.setVisibility(View.VISIBLE);
            }
            if (configExcepcion.get("obl").equals("1") || configExcepcion.get("obl").equals("X")) {
                listaCamposObligatorios.add(campo);
            } else if (configExcepcion.get("obl") != null && !configExcepcion.get("obl").equals("NULL")) {
                listaCamposObligatorios.remove(campo);
            }
            if (configExcepcion.get("dfaul").trim().length() > 0) {
                checkbox.setChecked(true);
            }else if (configExcepcion.get("dfaul") != null && !configExcepcion.get("dfaul").equals("NULL")){
                checkbox.setChecked(false);
            }
        }
        if(elemento instanceof SearchableSpinner)
        {
            SearchableSpinner combo = (SearchableSpinner) elemento;
            if (configExcepcion.get("vis").equals("1") || configExcepcion.get("vis").equals("X")) {
                combo.setEnabled(false);
                combo.setBackground(context.getResources().getDrawable(R.drawable.spinner_background_disabled, null));
            } else if (configExcepcion.get("vis") != null && !configExcepcion.get("vis").equals("NULL")) {
                combo.setEnabled(true);
                combo.setBackground(context.getResources().getDrawable(R.drawable.spinner_background, null));
            }
            if (configExcepcion.get("sup").equals("1") || configExcepcion.get("sup").equals("X")) {
                combo.setVisibility(View.GONE);
                label.setVisibility(View.GONE);
            } else if (configExcepcion.get("sup") != null && !configExcepcion.get("sup").equals("NULL")) {
                combo.setVisibility(View.VISIBLE);
                label.setVisibility(View.VISIBLE);
            }
            if (configExcepcion.get("obl").equals("1") || configExcepcion.get("obl").equals("X")) {
                listaCamposObligatorios.add(campo);
            } else if (configExcepcion.get("obl") != null && !configExcepcion.get("obl").equals("NULL")) {
                listaCamposObligatorios.remove(campo);
            }
            if (!configExcepcion.get("dfaul").isEmpty() && !configExcepcion.get("dfaul").equals("NULL")) {
                combo.setSelection(VariablesGlobales.getIndex(combo,configExcepcion.get("dfaul").trim()));
            }
        }
        if(elemento instanceof MaskedEditText)
        {
            MaskedEditText et = (MaskedEditText) elemento;
            if (configExcepcion.get("vis").equals("1") || configExcepcion.get("vis").equals("X")) {
                et.setEnabled(false);
                et.setBackground(context.getResources().getDrawable(R.drawable.textbackground_disabled, null));
            } else if (configExcepcion.get("vis") != null && configExcepcion.get("vis").trim() != "NULL") {
                et.setEnabled(true);
                et.setBackground(context.getResources().getDrawable(R.drawable.textbackground, null));
            }
            if (configExcepcion.get("sup").equals("1") || configExcepcion.get("sup").equals("X")) {
                et.setVisibility(View.GONE);
                label.setVisibility(View.GONE);
            } else if (configExcepcion.get("sup") != null && configExcepcion.get("sup").trim() != "NULL") {
                et.setVisibility(View.VISIBLE);
                label.setVisibility(View.VISIBLE);
            }
            if (configExcepcion.get("obl").equals("1") || configExcepcion.get("obl").equals("X")) {
                listaCamposObligatorios.add(campo);
            } else if (configExcepcion.get("obl") != null && !configExcepcion.get("obl").equals("NULL")) {
                listaCamposObligatorios.remove(campo);
            }
            if (!configExcepcion.get("dfaul").isEmpty() && !configExcepcion.get("dfaul").equals("NULL")) {
                et.setText(configExcepcion.get("dfaul").trim());
            }
        }



    }

    public static InputFilter getEditTextFilter() {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                //[¡”#$%&/(),:]
                Pattern ps = Pattern.compile("^[a-zA-Z 0-9.-]+$");
                Matcher ms = ps.matcher(String.valueOf(c));
                return ms.matches();
            }
        };
    }
}
