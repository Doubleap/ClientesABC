package proyecto.app.clientesabc.modelos;

public class Conexion {
    private String nombre;
    private String sociedad;
    private String ip;
    private String puerto;
    private String tipo;
    private String ruta;
    private boolean defecto;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPuerto() {
        return puerto;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }


    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getIp();
                break;
            case 1:
                valorCelda = getPuerto();
                break;
            case 2:
                valorCelda = getTipo();
                break;
            case 3:
                valorCelda = getRuta();
                break;
            case 4:
                valorCelda = getNombre();
                break;
        }
        return valorCelda;
    }

    public boolean isDefecto() {
        return defecto;
    }

    public void setDefecto(boolean defecto) {
        this.defecto = defecto;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    @Override
    public boolean equals(Object conexion){
        if (conexion == null) return false;
        if (conexion == this) return true;
        if (!(conexion instanceof Conexion))return false;
        Conexion otherConexion = (Conexion)conexion;
        if(this.getTipo().equals(otherConexion.getTipo())  && this.getNombre().equals(otherConexion.getNombre())
                && this.getIp().equals(otherConexion.getIp()) && this.getPuerto().equals(otherConexion.getPuerto())){
            return true;
        }
        return false;
    }
}
