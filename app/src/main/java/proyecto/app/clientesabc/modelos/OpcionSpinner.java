package proyecto.app.clientesabc.modelos;

public class OpcionSpinner {
    private String id;
    private String name;
    private String rel1;
    private int selected;

    public OpcionSpinner(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public OpcionSpinner(String id, String name, String rel1) {
        this.id = id;
        this.name = name;
        this.rel1 = rel1;
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
}
