package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import proyecto.app.clientesabc.clases.CustomZXingScannerView;

public class EscanearActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView escanerZXing;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        //escanerZXing = new ZXingScannerView(this);
        escanerZXing = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomZXingScannerView(context);
            }

        };    // Programmatically initialize the scanner view
        List<BarcodeFormat> formato = new ArrayList<BarcodeFormat>();
        formato.add(BarcodeFormat.PDF_417);
        escanerZXing.setFormats(formato);
        escanerZXing.setMinimumWidth(5000);
        escanerZXing.setMinimumHeight(500);
        // Hacer que el contenido de la actividad sea el escaner
        setContentView(escanerZXing);
    }

    @Override
    public void onResume() {
        super.onResume();
        // El "manejador" del resultado es esta misma clase, por eso implementamos ZXingScannerView.ResultHandler
        escanerZXing.setResultHandler(this);
        escanerZXing.startCamera(); // Comenzar la cámara en onResume
    }

    @Override
    public void onPause() {
        super.onPause();
        escanerZXing.stopCamera(); // Pausar en onPause
    }

    // Estamos sobrescribiendo un método de la interfaz ZXingScannerView.ResultHandler
    @Override
    public void handleResult(Result resultado) {

        // Si quieres que se siga escaneando después de haber leído el código, descomenta lo siguiente:
        // Si la descomentas no recomiendo que llames a finish
//        escanerZXing.resumeCameraPreview(this);
        // Obener código/texto leído
        String codigo = resultado.getText();
        byte[] raw = new byte[0];
        try {
            raw = codigo.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //Intento de decodificar el valor de la cedula en PDF417 con encriptacion XOR cypher
        String d= "";
        int j = 0;
        for (int i = 0; i < raw.length; i++) {
            if (j == 17) {
                j = 0;
            }
            char c = (char) (keysArray[j] ^ ((char) (raw[i])));
            if((c+"").matches("^[a-zA-Z0-9]*$")){
                d += c;
            }else{
                d += c;
            }
            j ++;
        }

        // Preparar un Intent para regresar datos a la actividad que nos llamó
        Intent intentRegreso = new Intent();
        intentRegreso.putExtra("codigo", d);
        setResult(Activity.RESULT_OK, intentRegreso);
        // Cerrar la actividad. Ahora mira onActivityResult de
        finish();
    }
    private static byte[] keysArray = new byte[]{
            (byte)0x27,
            (byte)0x30,
            (byte)0x04,
            (byte)0xA0,
            (byte)0x00,
            (byte)0x0F,
            (byte)0x93,
            (byte)0x12,
            (byte)0xA0,
            (byte)0xD1,
            (byte)0x33,
            (byte)0xE0,
            (byte)0x03,
            (byte)0xD0,
            (byte)0x00,
            (byte)0xDf,
            (byte)0x00
    };
}