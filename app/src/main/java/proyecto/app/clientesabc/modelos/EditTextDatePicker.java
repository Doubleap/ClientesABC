package proyecto.app.clientesabc.modelos;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class EditTextDatePicker  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private EditText _editText;
    private String _day;
    private String _month;
    private String _birthYear;
    private Context _context;
    private String _formato;

    public EditTextDatePicker(Context context, EditText editTextView)
    {
        Activity act = (Activity)context;
        this._editText = editTextView;
        this._editText.setOnClickListener(this);
        this._context = context;
    }
    public EditTextDatePicker(Context context, EditText editTextView, String formato)
    {
        Activity act = (Activity)context;
        this._editText = editTextView;
        this._editText.setOnClickListener(this);
        this._context = context;
        this._formato = formato;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        _birthYear = String.format(Locale.getDefault(),"%04d", year);
        _month = String.format(Locale.getDefault(),"%02d", monthOfYear+1);
        _day = String.format(Locale.getDefault(),"%02d", dayOfMonth);
        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Limpiar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        _editText.setText("");
                    }
                });
        dialog.show();
    }

    // updates the date in the birth date EditText
    private void updateDisplay() {
        switch (_formato){
            case "yyyymmdd":
                _editText.setText(new StringBuilder().append(_birthYear).append(_month).append(_day));
                break;
            case "dd/mm/yyyy":
                _editText.setText(new StringBuilder().append(_day).append("/").append(_month).append("/").append(_birthYear).append(" "));
                break;
            case "dd-mm-yyyy":
                _editText.setText(new StringBuilder().append(_day).append("-").append(_month).append("-").append(_birthYear).append(" "));
                break;
            default:
                _editText.setText(new StringBuilder().append(_day).append("/").append(_month).append("/").append(_birthYear).append(" "));
                break;
        }

    }
}