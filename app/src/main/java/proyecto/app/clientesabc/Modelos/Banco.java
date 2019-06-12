package proyecto.app.clientesabc.Modelos;

public class Banco {
    private String id_bancos;
    private String id_formulario;
    private String id_solicitud;
    private String bankl;
    private String banks;
    private String bankn;
    private String bkont;
    private String koinh;
    private String bvtyp;
    private String bkref;
    private String task;

    public String getId_bancos() {
        return id_bancos;
    }

    public void setId_bancos(String id_bancos) {
        this.id_bancos = id_bancos;
    }

    public String getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(String id_formulario) {
        this.id_formulario = id_formulario;
    }

    public String getId_solicitud() {
        return id_solicitud;
    }

    public void setId_solicitud(String id_solicitud) {
        this.id_solicitud = id_solicitud;
    }

    public String getBankl() {
        return bankl;
    }

    public void setBankl(String bankl) {
        this.bankl = bankl;
    }

    public String getBanks() {
        return banks;
    }

    public void setBanks(String banks) {
        this.banks = banks;
    }

    public String getBankn() {
        return bankn;
    }

    public void setBankn(String bankn) {
        this.bankn = bankn;
    }

    public String getBkont() {
        return bkont;
    }

    public void setBkont(String bkont) {
        this.bkont = bkont;
    }

    public String getKoinh() {
        return koinh;
    }

    public void setKoinh(String koinh) {
        this.koinh = koinh;
    }

    public String getBvtyp() {
        return bvtyp;
    }

    public void setBvtyp(String bvtyp) {
        this.bvtyp = bvtyp;
    }

    public String getBkref() {
        return bkref;
    }

    public void setBkref(String bkref) {
        this.bkref = bkref;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getId_bancos();
                break;
            case 1:
                valorCelda = getId_formulario();
                break;
            case 2:
                valorCelda = getId_solicitud();
                break;
            case 3:
                valorCelda = getBankl();
                break;
            case 4:
                valorCelda = getBanks();
                break;
            case 5:
                valorCelda = getBankn();
                break;
            case 6:
                valorCelda = getBkont();
                break;
            case 7:
                valorCelda = getKoinh();
                break;
            case 8:
                valorCelda = getBvtyp();
                break;
            case 9:
                valorCelda = getBkref();
                break;
            case 10:
                valorCelda = getTask();
                break;
        }
        return valorCelda;
    }

}
