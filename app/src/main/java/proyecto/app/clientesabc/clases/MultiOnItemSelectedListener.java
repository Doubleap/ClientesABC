package proyecto.app.clientesabc.clases;


import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import es.dmoral.toasty.Toasty;

public class MultiOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    AdapterView.OnItemSelectedListener miListener;
    AdapterView.OnItemSelectedListener oldListener;

    public MultiOnItemSelectedListener(AdapterView.OnItemSelectedListener oldListener){
        this.oldListener = oldListener;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toasty.info(view.getContext(), "Funcionalidad agregada despues de ejecutar el codigo original").show();
        parent.setOnItemSelectedListener(oldListener);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}