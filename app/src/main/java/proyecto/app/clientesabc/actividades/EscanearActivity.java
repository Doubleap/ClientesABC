package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import proyecto.app.clientesabc.clases.CustomZXingScannerView;

public class EscanearActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView escanerZXing;
    private String campoEscaneo;
    private int requestCode;
    private boolean flash;
    private CameraManager mCameraManager;
    private String mCameraId;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            campoEscaneo = b.getString("campoEscaneo");
            requestCode = b.getInt("requestCode");
            flash = b.getBoolean("flash");
        }
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        //escanerZXing = new ZXingScannerView(this);
        escanerZXing = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomZXingScannerView(context);
            }

        };    // Programmatically initialize the scanner view
        List<BarcodeFormat> formato = new ArrayList<BarcodeFormat>();
        formato.add(BarcodeFormat.CODE_128);
        formato.add(BarcodeFormat.EAN_13);
        escanerZXing.setFormats(formato);
        escanerZXing.setMinimumWidth(5000);
        escanerZXing.setMinimumHeight(500);
        escanerZXing.setFlash(flash);
        ToggleButton toggleButton = new ToggleButton(this);
        toggleButton.setTextOff("Encender Luz");
        toggleButton.setTextOn("Apagar Luz");
        toggleButton.setChecked(flash);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                escanerZXing.setFlash(isChecked);
            }
        });
        //escanerZXing.addView(toggleButton);

        // Hacer que el contenido de la actividad sea el escaner
        addContentView(toggleButton,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        addContentView(escanerZXing,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
        //setContentView(toggleButton);
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
        // Obtener código/texto leído
        String codigo = resultado.getText();
        Toasty.warning(this, "Codigo leido: "+codigo, Toasty.LENGTH_SHORT).show();
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

        intentRegreso.putExtra("codigo", codigo);
        intentRegreso.putExtra("campoEscaneo", campoEscaneo);
        intentRegreso.putExtra("requestCode", requestCode);
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