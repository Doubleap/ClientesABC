package proyecto.app.clientesabc.modelos;

import com.rey.material.widget.TextView;

public class OpcionSpinner {
    private String id;
    private String name;
    private String rel1;
    private String rel2;
    private int selected;
    private TextView texto;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private boolean enabled;

    public OpcionSpinner(String id, String name) {
        this.id = id;
        this.name = name;
        this.enabled = true;
    }
    public OpcionSpinner(String id, String name, String rel1) {
        this.id = id;
        this.name = name;
        this.rel1 = rel1;
        this.enabled = true;
    }
    public OpcionSpinner(String id, String name, String rel1, boolean enabled) {
        this.id = id;
        this.name = name;
        this.rel1 = rel1;
        this.enabled = enabled;
    }
    public OpcionSpinner(String id, String name, String rel1, String rel2, boolean enabled) {
        this.id = id;
        this.name = name;
        this.rel1 = rel1;
        this.rel2 = rel2;
        this.enabled = enabled;
    }
    public OpcionSpinner(String id, String name, int selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
        this.enabled = true;
    }
    public OpcionSpinner(String id, String name, boolean enabled) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
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
            return c.getName().equals(name) && c.getId().equals(id);
        }
        return false;
    }
    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getRel1() {
        return rel1;
    }

    public void setRel1(String rel1) {
        this.rel1 = rel1;
    }

    public String getRel2() {
        return rel2;
    }

    public void setRel2(String rel2) {
        this.rel2 = rel2;
    }

    public TextView getTexto() {
        return texto;
    }

    public void setTexto(TextView texto) {
        this.texto = texto;
    }
}
