package proyecto.app.clientesabc.Modelos;

import android.widget.Spinner;

public class OpcionSpinner {
    private String id;
    private String name;
    private int selected;

    public OpcionSpinner(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public OpcionSpinner(String id, String name, int selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    //to display object as a string in spinner
    @Override
    public String toString() {
        return name;
    }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof OpcionSpinner){
            OpcionSpinner c = (OpcionSpinner )obj;
            if(c.getName().equals(name) && c.getId()==id ) return true;
        }
        return false;
    }
    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
