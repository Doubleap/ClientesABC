package proyecto.app.clientesabc.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class SpinnerAdapter extends ArrayAdapter<OpcionSpinner> {
    private Context context;
    private ArrayList<OpcionSpinner> items;

    public SpinnerAdapter(Context context, ArrayList<OpcionSpinner> items) {
        super(context, android.R.layout.simple_spinner_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(items.get(position).getName());

        return convertView;
    }
}
