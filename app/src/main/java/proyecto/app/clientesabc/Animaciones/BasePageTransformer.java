package proyecto.app.clientesabc.Animaciones;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import proyecto.app.clientesabc.R;

public abstract class BasePageTransformer implements ViewPager.PageTransformer {

    public static boolean inRange(final float position) {
        return position <= 1.0 && position >= -1.0;
    }

    public static boolean isLeftPage(final float position) {
        return position < 0;
    }

    public static boolean isRightPage(final float position) {
        return position > 0;
    }

    @Override
    public void transformPage(final View page, final float position) {
        page.setBackgroundColor(page.getContext().getResources().getColor(R.color.rechazado,null));
        //final int pageIndex = (Integer) page.getTag();
        transformPage(page, (int)position, position);
    }

    protected abstract void transformPage(final View page, final int pageIndex, final float position);

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}


