package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.InvalidScannerNameException;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

public class IntermecScannerActivity implements BarcodeReader.BarcodeListener ,
        BarcodeReader.TriggerListener {
    private AidcManager manager;
    private BarcodeReader reader;
    private boolean triggerState = false;
    private Context context;
    private Activity activity;
    private String barcodeData;
    private TextView label;

    public IntermecScannerActivity(final Context context, Activity activity, TextView label){
        this.context = context;
        this.activity = activity;
        this.label = label;
        // create the AidcManager providing a Context and an
        // CreatedCallback implementation.
        AidcManager.create(context, new AidcManager.CreatedCallback() {

            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                // use the manager to create a BarcodeReader with a session
                // associated with the internal imager.
                try {
                    reader = manager.createBarcodeReader();
                } catch (InvalidScannerNameException e) {
                    e.printStackTrace();
                }

                try {
                    // apply settings
                    reader.setProperty(BarcodeReader.PROPERTY_CODE_39_ENABLED, false);
                    reader.setProperty(BarcodeReader.PROPERTY_PDF_417_ENABLED, true);

                    // set the trigger mode to client control
                    reader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL);
                    reader.claim();
                    reader.aim(true);
                    reader.decode(true);
                    reader.light(true);
                } catch (UnsupportedPropertyException e) {
                    Toast.makeText(context, "Failed to apply properties",
                            Toast.LENGTH_SHORT).show();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                }

                // register bar code event listener
                reader.addBarcodeListener(IntermecScannerActivity.this);

                // register trigger state change listener
                reader.addTriggerListener(IntermecScannerActivity.this);
            }
        });
    }

    @Override
    public void onBarcodeEvent(final BarcodeReadEvent event) {
                this.barcodeData = event.getBarcodeData();
                //label.setText(this.barcodeData);
                label.setText("FIN");
                String timestamp = event.getTimestamp();
                try {
                    reader.softwareTrigger(false);
                } catch (ScannerNotClaimedException e) {
                    e.printStackTrace();
                } catch (ScannerUnavailableException e) {
                    e.printStackTrace();
                }
                // update UI to reflect the data


        // reset the trigger state
        triggerState = false;
    }

    @Override
    public void onFailureEvent(final BarcodeFailureEvent event) {
        Toast.makeText(context, "Barcode read failed", Toast.LENGTH_SHORT).show();
    }

    // these events can be used to implement custom trigger modes if the automatic
    // behavior provided by the scanner service is insufficient for your application.
    // the following code demonstrates a "toggle" mode implementation, where the state
    // of the scanner changes each time the scan trigger is pressed.
    @Override
    public void onTriggerEvent(TriggerStateChangeEvent event) {
        try {
            // only handle trigger presses
            if (event.getState()) {
                // turn on/off aimer, illumination and decoding
                reader.aim(!triggerState);
                reader.light(!triggerState);
                reader.decode(!triggerState);

                triggerState = !triggerState;
            }
        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
            Toast.makeText(context, "Scanner is not claimed", Toast.LENGTH_SHORT).show();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
            Toast.makeText(context, "Scanner unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public String getBarcodeData() {
        return barcodeData;
    }

    public void setBarcodeData(String barcodeData) {
        this.barcodeData = barcodeData;
    }
}
