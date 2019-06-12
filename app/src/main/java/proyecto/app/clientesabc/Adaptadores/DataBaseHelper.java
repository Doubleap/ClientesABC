package proyecto.app.clientesabc.Adaptadores;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import proyecto.app.clientesabc.Modelos.Visitas;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.Modelos.Contacto;

import static java.sql.Types.ROWID;

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
                " WHERE  (zroute_pr IS NOT NULL) AND (vkorg = '0443') AND (trim(zroute_pr) <> '')";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("id",cursor.getString(0));
            user.put("desccripcion",cursor.getString(1));
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
        return 0;
    }

    public ArrayList<HashMap<String, String>> getCamposPestana(String id_formulario, String pestana){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String query = "SELECT c.campo, c.nombre, c.tipo_input, c.id_seccion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip FROM configuracion c" +
                " INNER JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = 'F443' and cc.ktokd = 'RCMA')" +
                " INNER JOIN Seccion s ON (s.id_seccion = c.id_seccion)" +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip)" +
                " WHERE id_formulario = "+id_formulario+" AND c.panta = '"+pestana+"'" +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO')"+
                " ORDER BY c.panta,c.id_seccion";
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
                    lista.put("id",cursor.getString(0));//1era columna del query
                    lista.put("descripcion",cursor.getString(0) + " - " + cursor.getString(1));//1era y 2da columna del query
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

   /*public  String[][] convertirArrayContactosParaAdaptador(ArrayList<Contacto> contactos)
    {
        String[][] losContactos = null;
        if(contactos != null) {
            Contacto c;
            losContactos = new String[contactos.size()][10];
            for (int i = 0; i < contactos.size(); i++) {
                c = contactos.get(i);
                losContactos[i][0] = c.getId();
                losContactos[i][1] = c.getId_formulario();
                losContactos[i][2] = c.getName1();
            }
        }
        return losContactos;
    }*/

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


    //Funciones de Ayuda apra interfaz
    public ArrayList<Visitas> DeterminarPlanesdeVisita(String vkorg, String modalidad)
    {
        ArrayList<Visitas> visitasList = new ArrayList<>();
        String fechaSistema = String.format(new Date().toString(), "yyyyMMdd");
        String query = "select vpore as vptyp, '' as descripcion, '''1DA''' as kvgr4, '' as ruta,'' as fec_frec, '" + fechaSistema + "' as f_ico, '99991231' as f_fco, '' as f_ini, '' as f_fin, 1 as obligatorio, '''1''' as fcalid FROM cat_ztsdvto_00185_x WHERE zopcional != 'X' and vkorg = '" + vkorg.trim() + "' and kvgr5 = '" + modalidad + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);

        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")));
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("vptyp")));
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("kvgr4")));
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("f_ico")));
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("f_fco")));
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("Fcalid")));

            visitasList.add(visita);
        }
        return  visitasList;
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
}
