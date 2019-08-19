package proyecto.app.clientesabc.actividades;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;

public class panelActivity extends Activity {
    GridLayout gridLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);
        gridLayout=(GridLayout)findViewById(R.id.mainGrid);
        setSingleEvent(gridLayout);
    }
    // we are setting onClickListener for each element
    private void setSingleEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toasty.info(panelActivity.this, "Opcion " + finalI,
                            Toasty.LENGTH_SHORT).show();
                }
            });
        }
    }
}
