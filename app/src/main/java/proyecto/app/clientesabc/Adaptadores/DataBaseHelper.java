package proyecto.app.clientesabc.Adaptadores;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import proyecto.app.clientesabc.Modelos.Contacto;
import proyecto.app.clientesabc.Modelos.Impuesto;
import proyecto.app.clientesabc.Modelos.Interlocutor;
import proyecto.app.clientesabc.Modelos.Visitas;
import proyecto.app.clientesabc.VariablesGlobales;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "FAWM_ANDROID_2";
    private static String DB_PATH = "";
    private static String BK_PATH = "";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
            BK_PATH = context.getApplicationInfo().dataDir + "/backUp/";
        }
        else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            BK_PATH = "/data/data/" + context.getPackageName() + "/backUp/";
        }
        this.mContext = context;

        mDataBase = this.getWritableDatabase();
        /*try {
            backUpDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //File existente = new File(DB_PATH, DB_NAME);
        //existente.delete();
        //copyDataBase();
}

    public void updateDataBase() throws IOException {
        if (/*mNeedUpdate*/true) {
            backUpDataBase();
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    private void backUpDataBase() throws IOException {
        File bkFileDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null)
            {
                String externalStoragePath = externalStorage.getAbsolutePath();
                bkFileDir = new File(externalStoragePath + File.separator + getActivity(mContext).getPackageName()); //$NON-NLS-1$
                boolean ex = bkFileDir.mkdirs();
                File bkFile = new File(bkFileDir,DB_NAME+"_BACKUP");
                File dbFile = new File(DB_PATH + DB_NAME);
                FileInputStream fileInputStream = new FileInputStream(dbFile);

                //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
                FileOutputStream mOutput = new FileOutputStream(bkFile);
                byte[] mBuffer = new byte[1024];
                int mLength;
                while ((mLength = fileInputStream.read(mBuffer)) > 0)
                    mOutput.write(mBuffer, 0, mLength);
                mOutput.flush();
                mOutput.close();
                fileInputStream.close();
            }
        }
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }

    public Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }
        return null;
    }

    // Get rutas Details
    public ArrayList<HashMap<String, String>> getRutasPreventa(){
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT zroute_pr as id, zroute_pr as descripcion" +
                " FROM EX_T_RUTAS_VP" +
                " WHERE  (zroute_pr IS NOT NULL) AND (vkorg = '"+VariablesGlobales.getOrgVta()+"') AND (trim(zroute_pr) <> '')";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id",cursor.getString(0));
            user.put("descripcion",cursor.getString(1));
            userList.add(user);
        }
        return  userList;
    }

    public ArrayList<HashMap<String, String>> getClientes(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME_CO as direccion, 'Estado' as estado " +
                " FROM SAPDClientes";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("codigo",cursor.getString(0));
            user.put("nombre",cursor.getString(1));
            user.put("direccion",cursor.getString(2));
            user.put("estado",cursor.getString(3));
            clientList.add(user);
        }
        return  clientList;
    }

    public ArrayList<HashMap<String, String>> getSolicitudes(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT idform as numero, [W_CTE-KUNNR] as codigo, [W_CTE-NAME1] as nombre, estado as estado, tipform " +
                " FROM FormHVKOF_solicitud";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("numero",cursor.getString(0));
            user.put("codigo",String.valueOf(cursor.getInt(1)));
            user.put("nombre",cursor.getString(2));
            user.put("estado",cursor.getString(3));
            user.put("tipform",cursor.getString(4));
            formList.add(user);
        }
        return  formList;
    }
    public int getNextSolicitudId(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT \"ROWID\" from FormHvkof_solicitud order by \"ROWID\" DESC limit 1";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            return cursor.getInt(0)+1;
        }
        return 1;
    }

    public ArrayList<HashMap<String, String>> getCamposPestana(String id_formulario, String pestana){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String query = "SELECT c.campo, c.nombre, c.tipo_input, c.id_seccion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip FROM configuracion c" +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+VariablesGlobales.getSociedad()+"' and cc.ktokd = 'RCMA')" +
                " INNER JOIN Seccion s ON (s.id_seccion = c.id_seccion)" +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip)" +
                " WHERE id_formulario = "+id_formulario+" AND c.panta = '"+pestana+"'" +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO')"+
                " ORDER BY c.panta,c.id_seccion, c.orden";
        Cursor cursor = mDataBase.rawQuery(query,null);

        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("campo",cursor.getString(cursor.getColumnIndex("campo")));
            user.put("nombre",cursor.getString(cursor.getColumnIndex("nombre")));
            user.put("tipo_input",cursor.getString(cursor.getColumnIndex("tipo_input")));
            user.put("id_seccion",cursor.getString(cursor.getColumnIndex("id_seccion")));
            user.put("seccion",cursor.getString(cursor.getColumnIndex("seccion")));
            user.put("descr",cursor.getString(cursor.getColumnIndex("descr")));
            user.put("tabla",cursor.getString(cursor.getColumnIndex("tabla")));
            user.put("dfaul",cursor.getString(cursor.getColumnIndex("dfaul")));
            user.put("sup",cursor.getString(cursor.getColumnIndex("sup")));
            user.put("obl",cursor.getString(cursor.getColumnIndex("obl")));
            user.put("vis",cursor.getString(cursor.getColumnIndex("vis")));
            user.put("opc",cursor.getString(cursor.getColumnIndex("opc")));
            user.put("tabla_local",cursor.getString(cursor.getColumnIndex("tabla_local")));
            user.put("evento1",cursor.getString(cursor.getColumnIndex("evento1")));
            user.put("llamado1",cursor.getString(cursor.getColumnIndex("llamado1")));
            user.put("tooltip",cursor.getString(cursor.getColumnIndex("tooltip")));
            clientList.add(user);
        }
        return  clientList;
    }

    public List<String> getPestanasFormulario(String id_formulario){
        List<String> list = new ArrayList<String>();
        // Select All Query
        String selectQuery = "select DISTINCT p.orden, c.panta, p.desc_panta from configuracion c " +
                " join Pantalla p ON (p.id_panta = c.panta) " +
                " where id_formulario = "+id_formulario+" " +
                " order by p.orden, c.panta, p.desc_panta";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(2));//3era columna del query
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            //mDataBase.close();
            // returning lables
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * @param tabla : nombre de la tabla de base de datos del catálogo
     * @param filtroAdicional
     * @return
     */
    public ArrayList<HashMap<String, String>> getDatosCatalogo(String tabla, String... filtroAdicional){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * " +
                " FROM " + tabla +" WHERE 1=1";

        VariablesGlobales vg = new VariablesGlobales();
        StringBuilder filtros = new StringBuilder();

        //Crear Filtros manuales desde los parametros
        for(String filtro : filtroAdicional){
            filtros.append(" AND "+filtro);
        }

        //Crear Filtros Automaticos segun el pais

        //Si existe BUKRS en la tabla del catalago vamos a filtros por Sociedad
        if(existeColumna(tabla,"bukrs")){
            filtros.append(" AND bukrs = '"+vg.getSociedad()+"'");
        }
        if(existeColumna(tabla,"land1")){
            filtros.append(" AND land1 = '"+vg.getLand1()+"'");
        }
        if(existeColumna(tabla,"vkorg")){
            filtros.append(" AND vkorg = '"+vg.getOrgVta()+"'");
        }
        if(existeColumna(tabla,"banks")){
            filtros.append(" AND banks = '"+vg.getLand1()+"'");
        }
        if(existeColumna(tabla,"talnd")){
            filtros.append(" AND talnd = '"+vg.getLand1()+"'");
        }
        //TODO filtros segun la agencia o/y ruta de la HH o de jefe de ventas para las rutas de su agencia

        try {
            //SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = mDataBase.rawQuery(selectQuery + filtros, null);//selectQuery,selectedArguments
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String,String> lista = new HashMap<>();
                    lista.put("id",cursor.getString(0).trim());//1era columna del query
                    lista.put("descripcion",cursor.getString(0).trim() + " - " + cursor.getString(1).trim());//1era y 2da columna del query
                    listaCatalogo.add(lista);
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            //db.close();
            // returning lables
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
        }
        return listaCatalogo;
    }
    /**
     *
     * @param tabla : nombre de la tabla de base de datos del catálogo
     * @param filtroAdicional
     * @return
     */
    public ArrayList<HashMap<String, String>> getDatosCatalogo(String tabla, int columnaId, int columnaDesc, String... filtroAdicional){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * " +
                " FROM " + tabla +" WHERE 1=1";

        VariablesGlobales vg = new VariablesGlobales();
        StringBuilder filtros = new StringBuilder();

        //Crear Filtros manuales desde los parametros
        for(String filtro : filtroAdicional){
            filtros.append(" AND "+filtro);
        }

        //Crear Filtros Automaticos segun el pais

        //Si existe BUKRS en la tabla del catalago vamos a filtros por Sociedad
        if(existeColumna(tabla,"bukrs")){
            filtros.append(" AND bukrs = '"+vg.getSociedad()+"'");
        }
        if(existeColumna(tabla,"land1")){
            filtros.append(" AND land1 = '"+vg.getLand1()+"'");
        }
        if(existeColumna(tabla,"vkorg")){
            filtros.append(" AND vkorg = '"+vg.getOrgVta()+"'");
        }
        if(existeColumna(tabla,"banks")){
            filtros.append(" AND banks = '"+vg.getLand1()+"'");
        }
        if(existeColumna(tabla,"talnd")){
            filtros.append(" AND talnd = '"+vg.getLand1()+"'");
        }

        try {
            Cursor cursor = mDataBase.rawQuery(selectQuery + filtros, null);//selectQuery,selectedArguments
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {

                do {
                    HashMap<String,String> lista = new HashMap<>();
                    lista.put("id",cursor.getString(columnaId));//1era columna del query
                    lista.put("descripcion",cursor.getString(columnaId) + " - " + cursor.getString(columnaDesc));
                    listaCatalogo.add(lista);
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
            //db.close();
            // returning lables
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
        }
        return listaCatalogo;
    }


    public boolean existeColumna(String tabla, String columna){
        String sql = "PRAGMA table_info('"+tabla+"');";
        Cursor columnas = mDataBase.rawQuery(sql, null);
        //columnas.moveToFirst();
        while (columnas.moveToNext()) {
            if(columnas.getString(1).trim().equals(columna.trim())){
                columnas.close();
                return true;
            }
        }
        columnas.close();
        return false;
    }

    //LOGIN DE USUAIRO EN SISTEMA
    public boolean validarUsuario(String usuario){
        String selectQuery = "SELECT count(*) as existe FROM t_i_users WHERE UserName = '" + usuario.trim() +"' ";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);//selectQuery,selectedArguments
            cursor.moveToFirst();
            int cantidad = cursor.getInt(0);
            if(cantidad <= 0){
                return false;
            }
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
        }
        return true;
    }
    public boolean LoginUsuario(String usuario,String contrasena){
        String selectQuery = "SELECT count(*) as existe FROM t_i_users WHERE UserName = '" + usuario.trim() +"' AND Password = '" + contrasena.trim()+"'";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);//selectQuery,selectedArguments
            cursor.moveToFirst();
            int cantidad = cursor.getInt(0);
            if(cantidad <= 0){
                return false;
            }
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
        }
        return true;
    }

    //Informacion de BLOQUES DE DATOS

    //CONTACTOS
    public ArrayList<Contacto> getContactosDB(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contacto> contactList = new ArrayList<>();
        String query = "SELECT * FROM cat_t171t";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            Contacto contacto = new Contacto();
            contacto.setId_solicitud(cursor.getString(0));
            contacto.setId_formulario(cursor.getString(1));
            contacto.setName1(cursor.getString(2));
            /*contacto.setNamev(cursor.getString(3));
            contacto.setTelf1(cursor.getString(4));
            contacto.setPafkt(cursor.getString(5));
            contacto.setGbdat(cursor.getString(6));
            contacto.setStreet(cursor.getString(7));
            contacto.setHouse_num1(cursor.getString(8));
            contacto.setCountry(cursor.getString(9));*/
            contactList.add(contacto);
        }
        return  contactList;
    }
    public  String[][] getContactos()
    {
        ArrayList<Contacto> contactos = getContactosDB();
        String[][] losContactos;
        Contacto c;
        losContactos = new String[contactos.size()][10];
        for (int i=0;i<contactos.size();i++) {
            c=contactos.get(i);
            losContactos[i][0]=c.getId_solicitud();
            losContactos[i][1]=c.getId_formulario();
            losContactos[i][2]=c.getName1();
        }
        return losContactos;
    }

    public boolean guardarContacto(Contacto contacto){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("name1", contacto.getName1());
            cv.put("namev", contacto.getNamev());
            cv.put("country", contacto.getCountry());
            long result = db.insert(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), null, cv);
            if (result > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void EliminarContacto(int contactoid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH() , "id_contacto = ?",new String[]{String.valueOf(contactoid)});
        db.close();
    }
    // Update User Details
    public int ModificarContacto(Contacto contacto){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVals = new ContentValues();
        cVals.put("name1", contacto.getName1());
        cVals.put("namev", contacto.getNamev());
        int count = db.update(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), cVals, "id_contacto = ?",new String[]{String.valueOf(contacto.getId_solicitud())});
        return  count;
    }

    //IMPUESTOS DEFUALT X PAIS
    public ArrayList<Impuesto> getImpuestosPais(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Impuesto> impuestoList = new ArrayList<>();
        String query = "SELECT * FROM cat_impstos WHERE taxkd = 1 AND talnd = '"+VariablesGlobales.getLand1()+"'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            Impuesto impuesto = new Impuesto();
            impuesto.setTatyp(cursor.getString(1));
            impuesto.setVtext(cursor.getString(2));
            impuesto.setTaxkd(cursor.getString(3));
            impuesto.setVtext2(cursor.getString(4));
            impuestoList.add(impuesto);
        }
        return  impuestoList;
    }

    //INTERLOCUTORES DEFAULT X GRUPO DE CUENTAS
    public ArrayList<Interlocutor> getInterlocutoresPais(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Interlocutor> interlocutorList = new ArrayList<>();
        String grupoCuentasDefault = "RCMA";
        //Buscar el grupo de ceuntas adecuado por pais
        switch(VariablesGlobales.getLand1()){
            case "CR":
                grupoCuentasDefault = "RCMA";
                break;
            case "NI":
                grupoCuentasDefault = "NCMA";
                break;
            case "PA":
                grupoCuentasDefault = "PCMA";
                break;
            case "GT":
                grupoCuentasDefault = "GCMA";
                break;
        }
        String query = "SELECT * FROM cat_funcint WHERE ktokd = '"+grupoCuentasDefault+"'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            Interlocutor interlocutor = new Interlocutor();
            interlocutor.setParvw(cursor.getString(0));
            interlocutor.setVtext(cursor.getString(1));
            interlocutor.setKunn2("");
            interlocutor.setName1("");
            interlocutorList.add(interlocutor);
        }
        return  interlocutorList;
    }

    //Funciones de Ayuda para interfaz
    public ArrayList<Visitas> DeterminarPlanesdeVisita(String vkorg, String modalidad)
    {
        ArrayList<Visitas> visitasList = new ArrayList<>();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String fechaSistema = df.format(c);
        String query = "select vpore as vptyp, '' as descripcion, '1DA' as kvgr4, '' as ruta,'' as fec_frec, '" + fechaSistema + "' as f_ico, '99991231' as f_fco, '' as f_ini, '' as f_fin, '1' as fcalid FROM cat_ztsdvto_00185_x WHERE zopcional != 'X' and vkorg = '" + vkorg.trim() + "' and kvgr5 = '" + modalidad + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);

        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("vptyp")));
            visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")));
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("kvgr4")));
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("f_ico")));
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("f_fco")));
            visita.setF_ini(cursor.getString(cursor.getColumnIndex("f_ini")));
            visita.setF_fin(cursor.getString(cursor.getColumnIndex("f_fin")));
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("fcalid")));

            visitasList.add(visita);
        }
        return  visitasList;
    }

    public boolean EsTipodeReparto(String agencia, String tiporuta)
    {
        String query = "select vwerks FROM EX_T_RUTAS_VP WHERE bzirk = @agencia AND vptyp = @tiporuta";
        Cursor cursor = mDataBase.rawQuery("select vwerks FROM EX_T_RUTAS_VP WHERE bzirk = ? AND vptyp = ?", new String[]{agencia, tiporuta});
        while (cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("vwerks")).isEmpty() || cursor.getString(cursor.getColumnIndex("vwerks")).toString().trim() == ""){
                return false;
            }else{
                return true;
            }
        }
        return false;
    }
    public String RutaRepartoAsociada(String kvgr5, String vpore) {//vkorg,ktokd,name1,street,house_num1,suppl1,suppl3,city1,land1
        Cursor cursor = mDataBase.rawQuery("select vpent FROM cat_ztsdvto_00185_x WHERE kvgr5 = ? AND vkorg = ? AND vpore = ?", new String[]{kvgr5, VariablesGlobales.getOrgVta(), vpore});
        while (cursor.moveToNext()){
            return cursor.getString(cursor.getColumnIndex("vpent"));
        }
        return "";
    }

    //Adjuntos x solicitud
    public void addAdjuntoSolicitud( String tipo, String nombre, byte[] imagen) throws SQLiteException {
        ContentValues cv = new  ContentValues();
        cv.put("id_solicitud",   getNextSolicitudId());
        cv.put("tipo",   tipo);
        cv.put("nombre",   nombre);
        cv.put("imagen",   imagen);
        mDataBase.insert( VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD(), null, cv );
    }

    //ENCUESTA CANALES
    public ArrayList<HashMap<String, String>> getPreguntasSegunGrupo(String grupo_isscom){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        //String sql_encuesta = "select p.zid_quest, p.text as quest_text,r.zid_resp, r.text as resp_text from cat_preguntas_isscom p inner join cat_respuestas_isscom r ON (p.zid_grupo = r.zid_grupo AND p.zid_quest = r.zid_quest) where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + VariablesGlobales.getSociedad() + "'";
        String sql_encuesta = "select zid_quest,text  from cat_preguntas_isscom p where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + VariablesGlobales.getSociedad() + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_quest",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            preguntasList.add(user);
        }
        return  preguntasList;
    }
    public ArrayList<HashMap<String, String>> getOpcionesPreguntaGrupo(String grupo_isscom, String pregunta){
        ArrayList<HashMap<String, String>> respuestasList = new ArrayList<>();
        String sql_encuesta = "select zid_resp,text from cat_respuestas_isscom p where trim(zid_grupo) = '"+grupo_isscom.trim()+"' and trim(zid_quest) = '"+pregunta.trim()+"'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_resp",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            respuestasList.add(user);
        }
        return  respuestasList;
    }

    public HashMap<String,String> getValoresSegunEncuestaRealizada(String... valores) {
        HashMap<String,String> registro_canales = new HashMap<>();
        String sql_encuesta = "select zid_result from cat_ztsdvto_00186 p where trim(zid_grupo) = '"+valores[0].trim()+"'";
        for(int x = 1; x < valores.length;x++){
            if(valores[x] != null)
                sql_encuesta += " AND zid_quest"+x+" = '"+valores[x].trim()+"'";
        }
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        String idValores = "";
        cursor.moveToNext();
        idValores = cursor.getString(0).trim();

        sql_encuesta = "select zzent3,zzent4,zzcanal,ztpocanal,zgpocanal,pson3  from cat_ztsdvto_00187 p where vkorg = '"+VariablesGlobales.getOrgVta()+"' AND trim(zid_result) = '"+idValores.trim()+"'";
        cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            registro_canales.put("W_CTE-ZZENT3",cursor.getString(0).trim());
            registro_canales.put("W_CTE-ZZENT4",cursor.getString(1).trim());
            registro_canales.put("W_CTE-ZZCANAL",cursor.getString(2).trim());
            registro_canales.put("W_CTE-ZTPOCANAL",cursor.getString(3).trim());
            registro_canales.put("W_CTE-ZGPOCANAL",cursor.getString(4).trim());
            registro_canales.put("W_CTE-PSON3",cursor.getString(5).trim());
        }
        return registro_canales;
    }

    public ArrayList<HashMap<String, String>> getRespuestasEncuesta(){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        //String sql_encuesta = "select p.zid_quest, p.text as quest_text,r.zid_resp, r.text as resp_text from cat_preguntas_isscom p inner join cat_respuestas_isscom r ON (p.zid_grupo = r.zid_grupo AND p.zid_quest = r.zid_quest) where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + VariablesGlobales.getSociedad() + "'";
        String sql_encuesta = "select id_Grupo as id_grupo,col1,col2,col3,col4,col5,col6,col7,col8,col9,col10 from encuesta_solicitud where idform = '" + getNextSolicitudId() + "'";
        Cursor micursor = mDataBase.rawQuery(sql_encuesta,null);
        while (micursor.moveToNext()){
            HashMap<String, String> user = new HashMap<>();
            user.put("id_grupo", micursor.getString(0).trim());
            user.put("col1", micursor.getString(1) == null ? "" : micursor.getString(1).trim());
            user.put("col2", micursor.getString(2) == null ? "" : micursor.getString(2).trim());
            user.put("col3", micursor.getString(3) == null ? "" : micursor.getString(3).trim());
            user.put("col4", micursor.getString(4) == null ? "" : micursor.getString(4).trim());
            user.put("col5", micursor.getString(5) == null ? "" : micursor.getString(5).trim());
            user.put("col6", micursor.getString(6) == null ? "" : micursor.getString(6).trim());
            user.put("col7", micursor.getString(7) == null ? "" : micursor.getString(7).trim());
            user.put("col8", micursor.getString(8) == null ? "" : micursor.getString(8).trim());
            user.put("col9", micursor.getString(9) == null ? "" : micursor.getString(9).trim());
            user.put("col10", micursor.getString(10) == null ? "" : micursor.getString(10).trim());
            preguntasList.add(user);
        }
        return  preguntasList;
    }

    //ENCUESTA GEC
    public ArrayList<HashMap<String, String>> getPreguntasGec(){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        //String sql_encuesta = "select p.zid_quest, p.text as quest_text,r.zid_resp, r.text as resp_text from cat_preguntas_isscom p inner join cat_respuestas_isscom r ON (p.zid_grupo = r.zid_grupo AND p.zid_quest = r.zid_quest) where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + VariablesGlobales.getSociedad() + "'";
        String sql_encuesta = "select zid_quest,text,text2  from cat_preguntas_gec p where bukrs = '" + VariablesGlobales.getSociedad() + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_quest",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            user.put("text2",cursor.getString(2).trim());
            preguntasList.add(user);
        }
        return  preguntasList;
    }

    public String getGecSegunEncuestaRealizada(Integer monto_total) {
        String gec = "";

        String sql_encuesta = "select klabc  from cat_rangos_gec p where bukrs = '"+VariablesGlobales.getSociedad()+"' AND min <=  "+monto_total+" AND max >="+monto_total+"";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            gec = cursor.getString(0).trim();
        }
        return gec;
    }

    public ArrayList<HashMap<String, String>> getEncuestaGec(int nextSolicitudId) {
        ArrayList<HashMap<String, String>> respuestasEncuestaGec = new ArrayList<>();
        //String sql_encuesta = "select p.zid_quest, p.text as quest_text,r.zid_resp, r.text as resp_text from cat_preguntas_isscom p inner join cat_respuestas_isscom r ON (p.zid_grupo = r.zid_grupo AND p.zid_quest = r.zid_quest) where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + VariablesGlobales.getSociedad() + "'";
        String sql_encuesta = "select zid_quest, monto from encuesta_gec_solicitud p where idform = '" + nextSolicitudId + "'";
        //String sql_encuesta2 = "select zid_quest, monto from encuesta_gec_solicitud p";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        //Cursor cursor2 = mDataBase.rawQuery(sql_encuesta2,null);
        while (cursor.moveToNext()){
            HashMap<String,String> resp = new HashMap<>();
            resp.put("zid_quest",cursor.getString(0).trim());
            resp.put("monto",cursor.getString(1).trim());
            respuestasEncuestaGec.add(resp);
        }
        return  respuestasEncuestaGec;
    }
}
