package proyecto.app.clientesabc.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.codecrafters.tableview.TableDataAdapter;
import proyecto.app.clientesabc.modelos.Contacto;

public class ContactoTableAdapter extends TableDataAdapter<Contacto> {

    private static String[] headers = new String[]{"Nombre", "Apellido", "Telefono","Funcion"};
    private static final String LOG_TAG = TableDataAdapter.class.getName();

    private int paddingLeft = 20;
    private int paddingTop = 25;
    private int paddingRight = 20;
    private int paddingBottom = 25;
    private int textSize = 10;
    private int typeface = Typeface.NORMAL;
    private int textColor = 0x99000000;
    private int gravity = Gravity.START;

    public ContactoTableAdapter(Context context, ArrayList<Contacto> data) {
        super(context, data);
    }

    @Override
    public View getCellView(final int rowIndex, final int columnIndex, final ViewGroup parentView) {
        final TextView textView = new TextView(getContext());
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        textView.setTypeface(textView.getTypeface(), typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);

        try {
            //final String textToShow = getItem(rowIndex)[columnIndex];
            Contacto contacto = getRowData(rowIndex);
            final String textToShow = contacto.getValueFromColumn(columnIndex+2);
            textView.setText(textToShow);
            textView.setGravity(gravity);
        } catch (final IndexOutOfBoundsException e) {
            Log.w(LOG_TAG, "No Sting given for row " + rowIndex + ", column " + columnIndex + ". "
                    + "Caught exception: " + e.toString());
            // Show no text
        }

        return textView;
    }

    public String[] getHeaders(){
        return headers;
    }
    public void setHeaders(String[] h ){
        headers = h;
    }
    /**
     * Sets the gravity of the text inside the data cell.
     * @param gravity Sets the gravity of the text inside the data cell.
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * Sets the padding that will be used for all table cells.
     *
     * @param left   The padding on the left side.
     * @param top    The padding on the top side.
     * @param right  The padding on the right side.
     * @param bottom The padding on the bottom side.
     */
    public void setPaddings(final int left, final int top, final int right, final int bottom) {
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
    }

    /**
     * Sets the padding that will be used on the left side for all table cells.
     *
     * @param paddingLeft The padding on the left side.
     */
    public void setPaddingLeft(final int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * Sets the padding that will be used on the top side for all table cells.
     *
     * @param paddingTop The padding on the top side.
     */
    public void setPaddingTop(final int paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * Sets the padding that will be used on the right side for all table cells.
     *
     * @param paddingRight The padding on the right side.
     */
    public void setPaddingRight(final int paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * Sets the padding that will be used on the bottom side for all table cells.
     *
     * @param paddingBottom The padding on the bottom side.
     */
    public void setPaddingBottom(final int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * Sets the text size that will be used for all table cells.
     *
     * @param textSize The text size that shall be used.
     */
    public void setTextSize(final int textSize) {
        this.textSize = textSize;
    }

    /**
     * Sets the typeface that will be used for all table cells.
     *
     * @param typeface The type face that shall be used.
     */
    public void setTypeface(final int typeface) {
        this.typeface = typeface;
    }

    /**
     * Sets the text color that will be used for all table cells.
     *
     * @param textColor The text color that shall be used.
     */
    public void setTextColor(final int textColor) {
        this.textColor = textColor;
    }


}