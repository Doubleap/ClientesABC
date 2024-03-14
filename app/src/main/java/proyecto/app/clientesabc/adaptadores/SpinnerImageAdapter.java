package proyecto.app.clientesabc.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class SpinnerImageAdapter extends ArrayAdapter {
    private Context context;
    private List<OpcionSpinner> list;

    public SpinnerImageAdapter(Context context, List<OpcionSpinner> list){
        super(context,0);
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_with_image_item,parent,false);
        TextView name = rootView.findViewById(R.id.nombre);
        ImageView imagen = rootView.findViewById(R.id.imagen);

        name.setText(list.get(position).getName());
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parent.getContext(), "Abrir ventana con datos adicionales del modelo!").show();
            }
        });
        return rootView;
    }
}
