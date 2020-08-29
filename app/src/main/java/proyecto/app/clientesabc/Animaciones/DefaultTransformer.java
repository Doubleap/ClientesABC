package proyecto.app.clientesabc.Animaciones;

import android.annotation.SuppressLint;
import android.view.View;

import proyecto.app.clientesabc.R;

public class DefaultTransformer extends BasePageTransformer {

    @Override
    public void transformPage(final View page, final int pageIndex, final float position) {
        page.setBackgroundColor(page.getContext().getResources().getColor(R.color.blue,null));
    }
}
