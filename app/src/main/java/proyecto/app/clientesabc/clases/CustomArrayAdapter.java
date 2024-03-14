package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.ArrayList;
import java.util.List;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.modelos.OpcionSpinner;

public class CustomArrayAdapter extends ArrayAdapter<OpcionSpinner> implements Filterable {
    ArrayList<OpcionSpinner> originalList;
    ArrayList<OpcionSpinner> formListFiltered;
    String filterString = "";
    // invoke the suitable constructor of the ArrayAdapter class
    public CustomArrayAdapter(@NonNull Context context, ArrayList<OpcionSpinner> arrayList) {
        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
        originalList = new ArrayList<>(arrayList);
        formListFiltered = new ArrayList<>(arrayList);
    }
    @Override
    public int getCount() {
        return formListFiltered.size();
    }

    @Override
    public OpcionSpinner getItem(int position) {
        return formListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        try {
            return Long.parseLong(((OpcionSpinner)formListFiltered.get(position)).getId());
        }catch(Exception e){
            return 0;
        }

    }
    @Override
    public boolean isEnabled(int position) {
        if(position >= formListFiltered.size())
            return true;
        else
            return ((OpcionSpinner)formListFiltered.get(position)).isEnabled();

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        if(position >= formListFiltered.size())
            return currentItemView;
        //View view = super.getView(position, convertView, parent);

        AppCompatTextView textview = (AppCompatTextView) currentItemView;
        textview.setText(((OpcionSpinner)formListFiltered.get(position)).getName());
        if((!((OpcionSpinner)formListFiltered.get(position)).isEnabled())){
            textview.setTextColor(Color.GRAY);
            textview.setBackgroundColor(Color.rgb(252, 220, 220));
        }else{
            textview.setTextColor(Color.BLACK);
            textview.setBackgroundColor(Color.WHITE);
        }

        return textview;
    }

    //Para filtrar busquedas segun criterios
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                formListFiltered = null;
                if(charSequence != null)
                    filterString = charSequence.toString();
                else
                    filterString = "";

                if (filterString.isEmpty()) {
                    formListFiltered = originalList;
                } else {
                    ArrayList<OpcionSpinner> filteredList = new ArrayList<>();
                    for (int x=0;x < originalList.size(); x++) {
                        if (((OpcionSpinner)originalList.get(x)).getName() != null && ((OpcionSpinner)originalList.get(x)).getName().trim().toLowerCase().replaceAll("á","a").replaceAll("é","e").replaceAll("í","i").replaceAll("ó","o").replaceAll("ú","u").contains(filterString.toLowerCase().replaceAll("á","a").replaceAll("é","e").replaceAll("í","i").replaceAll("ó","o").replaceAll("ú","u")))
                            filteredList.add(((OpcionSpinner)originalList.get(x)));
                    }
                    formListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.count = formListFiltered.size();
                filterResults.values = formListFiltered;

                return filterResults;
            }
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ArrayList<OpcionSpinner> miListaFiltrada = (ArrayList<OpcionSpinner>) filterResults.values;
                // refresh the list with filtered data
                notifyDataSetChanged();
                clear();
                for (int i = 0; i < miListaFiltrada.size(); i++)
                    add(miListaFiltrada.get(i));

                notifyDataSetInvalidated();
            }
        };
    }
}