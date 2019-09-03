package proyecto.app.clientesabc.modelos;

public class Adjuntos {
    private String id_formulario;
    private String id_solicitud;
    private String type;
    private String name;
    private byte[]  image;

    public Adjuntos(){
    }
    public Adjuntos(String sol, String tip, String nam, byte[] img){
        id_solicitud = sol;
        type = tip;
        name = nam;
        image = img;
    }

    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getId_formulario();
                break;
            case 1:
                valorCelda = getId_solicitud();
                break;
            case 2:
                valorCelda = getType();
                break;
            case 3:
                valorCelda = getName();
                break;
            case 4:
                valorCelda = getImage().toString();
                break;
        }
        return valorCelda;
    }

    public String getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(String id_formulario) {
        this.id_formulario = id_formulario;
    }

    private String getId_solicitud() {
        return id_solicitud;
    }

    public void setId_solicitud(String id_solicitud) {
        this.id_solicitud = id_solicitud;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
