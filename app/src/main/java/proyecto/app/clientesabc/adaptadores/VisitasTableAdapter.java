package proyecto.app.clientesabc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.hardware.camera2.params.MultiResolutionStreamInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.util.ArrayList;

import de.codecrafters.tableview.TableDataAdapter;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.SolicitudActivity;
import proyecto.app.clientesabc.actividades.TCPActivity;
import proyecto.app.clientesabc.clases.Haversine;
import proyecto.app.clientesabc.clases.Validaciones;
import proyecto.app.clientesabc.modelos.Visitas;

public class VisitasTableAdapter extends TableDataAdapter<Visitas> {

    private static String[] headers = new String[]{"VP", "Metodo Venta", "Ruta", "Frec. Sem."};
    private static final String LOG_TAG = TableDataAdapter.class.getName();

    private int paddingLeft = 20;
    private int paddingTop = 25;
    private int paddingRight = 20;
    private int paddingBottom = 25;
    private int textSize = 12;
    private int typeface = Typeface.NORMAL;
    private int textColor = 0x99000000;
    private int gravity = Gravity.CENTER;
    ArrayList<Visitas> visitasArray;
    Context context;
    Activity activity;
    boolean modificable;


    public VisitasTableAdapter(Context context, ArrayList<Visitas> data) {
        super(context, data);
        visitasArray = data;
        this.context = context;
    }
    public VisitasTableAdapter(Context context, Activity activity, ArrayList<Visitas> data, boolean modificable) {
        super(context, data);
        visitasArray = data;
        this.context = context;
        this.activity = activity;
        this.modificable = modificable;
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
            Visitas visita = getRowData(rowIndex);
            final String textToShow = visita.getValueFromColumn(columnIndex+2);


            if(columnIndex+1 == 3){
                DataBaseHelper mDBHelper = new DataBaseHelper(context);
                SQLiteDatabase mDb = mDBHelper.getWritableDatabase();
                if(mDBHelper.EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BZIRK",""), visita.getVptyp())
                && PreferenceManager.getDefaultSharedPreferences(getContext()).getString("W_CTE_BUKRS","").equals("F428") && modificable) {
                    LinearLayout celda = new LinearLayout(getContext());
                    final ImageView accion_calcular = new ImageView(getContext());
                    accion_calcular.setImageDrawable(getResources().getDrawable(R.drawable.icon_habilitador, null));
                    accion_calcular.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(activity != null)
                                activity.runOnUiThread(new SolicitudActivity.CalcularRepartoConHabilitador(context, activity));
                        }
                    });

                    ToolTipsManager mToolTipsManager = new ToolTipsManager();
                    final ToolTip.Builder builder = new ToolTip.Builder(getContext(), accion_calcular, parentView ,  "Calcula Ruta de Reparto VP según habilitador", ToolTip.POSITION_ABOVE);
                    builder.setAlign(ToolTip.ALIGN_LEFT);

                    builder.setGravity(ToolTip.GRAVITY_LEFT);
                    builder.setTextAppearance(R.style.TooltipTextAppearance); // from `styles.xml`
                    accion_calcular.setOnLongClickListener(view -> {
                        mToolTipsManager.show(builder.build());
                        return true;
                    });


                    if(getContext().getClass().getName().contains("ConsultaClienteTotalActivity"))
                        textView.setTextColor(getResources().getColor(R.color.pendientes,null));
                    textView.setText(textToShow);
                    textView.setGravity(gravity);

                    celda.setGravity(gravity);
                    celda.addView(textView);
                    celda.addView(accion_calcular);

                    return celda;
                }
            }

            if(getContext().getClass().getName().contains("ConsultaClienteTotalActivity"))
                textView.setTextColor(getResources().getColor(R.color.pendientes,null));
            textView.setText(textToShow);
            textView.setGravity(gravity);
        } catch (final IndexOutOfBoundsException e) {
            Log.w(LOG_TAG, "No String given for row " + rowIndex + ", column " + columnIndex + ". "
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