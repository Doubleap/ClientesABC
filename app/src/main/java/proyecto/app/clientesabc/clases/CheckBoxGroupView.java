package proyecto.app.clientesabc.clases;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

import proyecto.app.clientesabc.modelos.OpcionCheckBox;

public class CheckBoxGroupView extends GridLayout {

    List<OpcionCheckBox> checkboxes = new ArrayList<OpcionCheckBox>();

    public CheckBoxGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void put(OpcionCheckBox checkBox) {
        checkboxes.add( checkBox);
        invalidate();
        requestLayout();
    }

    public void remove(Integer id) {
        // TODO: Remove items from ArrayList
    }

    public List<?> getCheckboxesChecked(){

        List<OpcionCheckBox> checkeds = new ArrayList<>();
        for (OpcionCheckBox c : checkboxes){
            if(c.isChecked())
                checkeds.add(c);
        }

        return checkeds;
    }

    public List<String> getCheckedIds(){

        List<String> checkeds = new ArrayList<>();
        for (OpcionCheckBox c : checkboxes){
            if(c.isChecked())
                checkeds.add(String.valueOf(c.getId()));
        }
        return checkeds;
    }

    public List<String> getCheckedIdsTexto(){

        List<String> checkeds = new ArrayList<String>();
        for (OpcionCheckBox c : checkboxes){
            if(c.isChecked())
                checkeds.add(c.getIdTexto());
        }
        return checkeds;
    }

    public List<String> getCheckedText(){

        List<String> checkeds = new ArrayList<String>();
        for (OpcionCheckBox c : checkboxes){
            if(c.isChecked())
                checkeds.add(c.getText().toString());
        }
        return checkeds;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        for(OpcionCheckBox c: checkboxes) {
            addView(c);
        }

        invalidate();
        requestLayout();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}