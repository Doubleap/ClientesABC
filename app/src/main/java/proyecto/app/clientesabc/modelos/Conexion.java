package proyecto.app.clientesabc.modelos;

public class Conexion {
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
        }
        return valorCelda;
    }

    public boolean isDefecto() {
        return defecto;
    }

    public void setDefecto(boolean defecto) {
        this.defecto = defecto;
    }
}
