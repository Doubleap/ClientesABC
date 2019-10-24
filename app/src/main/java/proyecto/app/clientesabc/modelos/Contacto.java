package proyecto.app.clientesabc.modelos;

public class Contacto {
   private String id;
   private String id_solicitud;
   private String id_formulario;
   private String name1;
   private String namev;
   private String telf1;
   private String pafkt;
   private String gbdat;
   private String street;
   private String house_num1;
   private String country;
   private String datatype;
   private String numeric_precision;
   private String maxlength;

    public String getId_solicitud() {
        return id_solicitud;
    }

    public void setId_solicitud(String id) {
        this.id_solicitud = id;
    }

    public String getId_formulario() {
        return id_formulario;
    }

    public void setId_formulario(String id_formulario) {
        this.id_formulario = id_formulario;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getNamev() {
        return namev;
    }

    public void setNamev(String namev) {
        this.namev = namev;
    }

    public String getTelf1() {
        return telf1;
    }

    public void setTelf1(String telf1) {
        this.telf1 = telf1;
    }

    public String getPafkt() {
        return pafkt;
    }

    public void setPafkt(String pafkt) {
        this.pafkt = pafkt;
    }

    public String getGbdat() {
        return gbdat;
    }

    public void setGbdat(String gbdat) {
        this.gbdat = gbdat;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse_num1() {
        return house_num1;
    }

    public void setHouse_num1(String house_num1) {
        this.house_num1 = house_num1;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getValueFromColumn(int columnIndex){
        String valorCelda = "";
        switch(columnIndex){
            case 0:
                valorCelda = getId_solicitud();
                break;
            case 1:
                valorCelda = getId_formulario();
                break;
            case 2:
                valorCelda = getName1();
                break;
            case 3:
                valorCelda = getNamev();
                break;
            case 4:
                valorCelda = getTelf1();
                break;
            case 5:
                valorCelda = getPafkt();
                break;
            case 6:
                valorCelda = getGbdat();
                break;
            case 7:
                valorCelda = getStreet();
                break;
            case 8:
                valorCelda = getHouse_num1();
                break;
            case 9:
                valorCelda = getCountry();
                break;
        }
        return valorCelda;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getNumeric_precision() {
        return numeric_precision;
    }

    public void setNumeric_precision(String numeric_precision) {
        this.numeric_precision = numeric_precision;
    }

    public String getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    public boolean validarObligatorios(){
        return (!this.getName1().trim().equals("") && !this.getNamev().trim().equals("") && !this.getTelf1().trim().equals("")  && !this.getPafkt().trim().equals("")  && !this.getCountry().trim().equals(""));
    }
}
