package proyecto.app.clientesabc.Animaciones;

import android.view.View;

public class AccordionPageTransformer extends BasePageTransformer2 {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
        view.setPivotX(view.getWidth());
        view.setScaleX(1.0f + position);
    }

    @Override
    public void handleRightPage(View view, float position) {
        view.setPivotX(0);
        view.setScaleX(1.0f - position);
    }
}
