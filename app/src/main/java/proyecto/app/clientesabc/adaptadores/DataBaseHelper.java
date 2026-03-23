package proyecto.app.clientesabc.adaptadores;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;

//import com.androidbuts.multispinnerfilter.KeyPairBoolData;

import org.chalup.microorm.MicroOrm;

import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.MantClienteActivity;
import proyecto.app.clientesabc.actividades.PanelActivity;
import proyecto.app.clientesabc.actividades.SolicitudActivity;
import proyecto.app.clientesabc.actividades.SolicitudModificacionActivity;
import proyecto.app.clientesabc.clases.KeyPairBoolData;
import proyecto.app.clientesabc.clases.SearchableSpinner;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Banco;
import proyecto.app.clientesabc.modelos.Comentario;
import proyecto.app.clientesabc.modelos.Contacto;
import proyecto.app.clientesabc.modelos.EncuestaCabecera;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.Horarios;
import proyecto.app.clientesabc.modelos.Impuesto;
import proyecto.app.clientesabc.modelos.Interlocutor;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.OpcionesRespuesta;
import proyecto.app.clientesabc.modelos.PreguntasEncuesta;
import proyecto.app.clientesabc.modelos.RespuestaPregunta;
import proyecto.app.clientesabc.modelos.Visitas;

@SuppressLint("Range")
public class DataBaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "FAWM_ANDROID_2";
    public static String DB_PATH = "";
    public static String BK_PATH = "";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        BK_PATH = context.getApplicationInfo().dataDir + "/backUp/";
        this.mContext = context;

        try {
            mDataBase = getWritableDatabase();
        }catch(Exception e){
            Toasty.error(context,"Hubo un error desconocido leyendo la DB. Por favor vuelva a intentar.").show();
        }
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
        backUpDataBase();
        File dbFile = new File(DB_PATH + DB_NAME);
        boolean bandera = false;
        if (dbFile.exists()) {
            this.close();
            bandera = dbFile.delete();
        }
        if(bandera)
            Log.d("MI TAG","Se ha borrado el archivo "+dbFile.getName());

        copyDataBase();
        //this.getReadableDatabase();
        //this.openDataBase();
        mNeedUpdate = false;
    }
    public void deleteDataBase() throws IOException {
        //backUpDataBase();
        File dbFile = new File(DB_PATH + DB_NAME);
        boolean bandera = false;
        if (dbFile.exists())
            bandera = dbFile.delete();
        if(bandera)
            Log.d("MI TAG","Se ha borrado el archivo "+dbFile.getName());
    }
    public static void deleteDatabaseFile(Context context) {
        File databases = new File(context.getApplicationInfo().dataDir + "/databases");
        File db = new File(databases, DB_NAME);
        if (db.delete())
            System.out.println("Database deleted");
        else
            System.out.println("Failed to delete database");

        File journal = new File(databases, DB_NAME + "-journal");
        if (journal.exists()) {
            if (journal.delete())
                System.out.println("Database journal deleted");
            else
                System.out.println("Failed to delete database journal");
        }
    }

    public void restoreDataBase() throws IOException {
        copyDataBaseFromBackUp();
    }
    public static boolean checkDataBase(Context context) {
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase(mContext)) {
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
        File tranFileDir = null;
        File externalStorage = mContext.getExternalFilesDir(null);
        String externalStoragePath;
        if (externalStorage != null) {
            externalStoragePath = externalStorage.getAbsolutePath();
            tranFileDir = new File(externalStoragePath + File.separator + "Transmision"+ File.separator +"FAWM_ANDROID_2");
        }
        InputStream mInput = null;
        if (tranFileDir != null) {
            mInput = new FileInputStream(tranFileDir);
        }
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        if (mInput != null) {
            while ((mLength = mInput.read(mBuffer)) > 0)
                mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        if (mInput != null) {
            mInput.close();
        }
    }

    private void copyDataBaseFromBackUp() throws IOException {
        File tranFileDir = null;
        File externalStorage = mContext.getExternalFilesDir(null);
        String externalStoragePath;
        if (externalStorage != null) {
            externalStoragePath = externalStorage.getAbsolutePath();
            tranFileDir = new File(externalStoragePath + File.separator + "Transmision"+ File.separator +"FAWM_ANDROID_2");
        }
        InputStream mInput = null;
        if (tranFileDir != null) {
            mInput = new FileInputStream(tranFileDir);
        }
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        if (mInput != null) {
            while ((mLength = mInput.read(mBuffer)) > 0)
                mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        if (mInput != null) {
            mInput.close();
        }
    }

    private void backUpDataBase() throws IOException {
        File bkFileDir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File externalStorage = mContext.getExternalFilesDir(null);
            if (externalStorage != null)
            {
                String externalStoragePath = externalStorage.getAbsolutePath();
                if(getActivity(mContext) != null)
                    bkFileDir = new File(externalStoragePath + File.separator + getActivity(mContext).getPackageName()); //$NON-NLS-1$
                else
                    bkFileDir = new File(externalStoragePath + File.separator + mContext.getPackageName());
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

    private Activity getActivity(Context context) {
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

    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getClientes(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String query = "";
        String columnas_monitor = "";
        try {
            boolean usaMonitor =  UsaMonitorEquipoFrio();
            if(usaMonitor){
                columnas_monitor = ",v.puertas_por_instalar";
            }
            switch (PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "")) {
                case "F443":
                    query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME2 as razonSocial, NAME_CO as direccion, 'Estado' as estado, KLABC as klabc, STCD3 as stcd3, STREET as street, STR_SUPPL1 as str_suppl1, SMTP_ADDR as smtp_addr, ZZCRMA_LAT as latitud, ZZCRMA_LONG as longitud, ZCANAL as canal, ZZTPOCANAL as tipo_canal, ZTERM as zterm " +
                            ", (SELECT count(*) FROM SAPDBaseInstalada WHERE kunnr = SAPDClientes.KUNNR) as cant_base_instalada" + columnas_monitor +
                            " FROM SAPDClientes ";
                            if(usaMonitor)
                                query += " LEFT JOIN VistaMonitorEquipoFrio v ON (v.codigo_cliente = SapDCLIENTES.KUNNR)";
                    break;
                case "F445":
                case "F451":
                    query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME2 as razonSocial, NAME_CO as direccion, 'Estado' as estado, KLABC as klabc, STCD1 as stcd3, STREET as street, STR_SUPPL1 as str_suppl1, SMTP_ADDR as smtp_addr, ZZCRMA_LAT as latitud, ZZCRMA_LONG as longitud, ZCANAL as canal, ZZTPOCANAL as tipo_canal " +
                            ", (SELECT count(*) FROM SAPDBaseInstalada WHERE kunnr = SAPDClientes.KUNNR) as cant_base_instalada" + columnas_monitor +
                            " FROM SAPDClientes ";
                        if(usaMonitor)
                            query += " LEFT JOIN VistaMonitorEquipoFrio v ON (v.codigo_cliente = SapDCLIENTES.KUNNR)";
                    break;
                case "1657":
                case "1658":
                case "F446":
                    query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME2 as razonSocial, NAME_CO as direccion, 'Estado' as estado, KLABC as klabc, STCD1 as stcd3, STREET as street, STR_SUPPL1 as str_suppl1, SMTP_ADDR as smtp_addr, ZZCRMA_LAT as latitud, ZZCRMA_LONG as longitud, ZCANAL as canal, ZZTPOCANAL as tipo_canal " +
                            ", (SELECT count(*) FROM SAPDBaseInstalada WHERE kunnr = SAPDClientes.KUNNR) as cant_base_instalada" + columnas_monitor +
                            " FROM SAPDClientes";
                    if(usaMonitor)
                        query += " LEFT JOIN VistaMonitorEquipoFrio v ON (v.codigo_cliente = SapDCLIENTES.KUNNR)";
                    break;
                case "1661":
                case "Z001":
                    query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME2 as razonSocial, STRAS as direccion, 'Estado' as estado, KLABC as klabc, stcd1 as stcd3, STREET as street, STR_SUPPL1 as str_suppl1, SMTP_ADDR as smtp_addr, ZZCRMA_LAT as latitud, ZZCRMA_LONG as longitud, ZCANAL as canal " +
                            ", (SELECT count(*) FROM SAPDBaseInstalada WHERE kunnr = SAPDClientes.KUNNR) as cant_base_instalada" + columnas_monitor +
                            " FROM SAPDClientes";
                    if(usaMonitor)
                        query += " LEFT JOIN VistaMonitorEquipoFrio v ON (v.codigo_cliente = SapDCLIENTES.KUNNR)";
                    break;
                case "F428":
                    query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME2 as razonSocial, NAME_CO as direccion, 'Estado' as estado, KLABC as klabc, STCD1 as stcd3, STREET as street, STR_SUPPL1 as str_suppl1, SMTP_ADDR as smtp_addr, ZZCRMA_LAT as latitud, ZZCRMA_LONG as longitud, ZCANAL as canal, ZZTPOCANAL as tipo_canal " +
                            ", 0 as cant_base_instalada" + columnas_monitor +
                            " FROM SAPDClientes ";
                    if(usaMonitor)
                        query += " LEFT JOIN VistaMonitorEquipoFrio v ON (v.codigo_cliente = SapDCLIENTES.KUNNR)";
                    break;
            }

            Cursor cursor = mDataBase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                HashMap<String, String> user = new HashMap<>();
                user.put("codigo", cursor.getString(0) != null ? cursor.getString(0) : "");
                user.put("nombre", cursor.getString(1) != null ? cursor.getString(1) : "");
                user.put("razonSocial", cursor.getString(2) != null ? cursor.getString(2) : "");
                user.put("direccion", cursor.getString(3) != null ? cursor.getString(3) : "");
                user.put("estado", cursor.getString(4) != null ? cursor.getString(4) : "");
                user.put("klabc", cursor.getString(cursor.getColumnIndex("klabc")) != null ? cursor.getString(cursor.getColumnIndex("klabc")) : "");
                user.put("idfiscal", cursor.getString(cursor.getColumnIndex("stcd3")) != null ? cursor.getString(cursor.getColumnIndex("stcd3")) : "");
                user.put("ubicacion", cursor.getString(cursor.getColumnIndex("street")) != null ? cursor.getString(cursor.getColumnIndex("street")) : "");
                user.put("direccion", cursor.getString(cursor.getColumnIndex("str_suppl1")) != null ? cursor.getString(cursor.getColumnIndex("str_suppl1")) : "");
                user.put("correo", cursor.getString(cursor.getColumnIndex("smtp_addr")) != null ? cursor.getString(cursor.getColumnIndex("smtp_addr")) : "");
                user.put("latitud", cursor.getString(cursor.getColumnIndex("latitud")) != null ? cursor.getString(cursor.getColumnIndex("latitud")) : "");
                user.put("longitud", cursor.getString(cursor.getColumnIndex("longitud")) != null ? cursor.getString(cursor.getColumnIndex("longitud")) : "");
                user.put("cant_base_instalada", cursor.getString(cursor.getColumnIndex("cant_base_instalada")) != null ? cursor.getString(cursor.getColumnIndex("cant_base_instalada")) : "");
                if (cursor.getColumnIndex("puertas_por_instalar") != -1) {
                    user.put("puertas_por_instalar", cursor.getString(cursor.getColumnIndex("puertas_por_instalar")) != null ? cursor.getString(cursor.getColumnIndex("puertas_por_instalar")) : "");
                }
                user.put("canal", cursor.getString(cursor.getColumnIndex("canal")) != null ? cursor.getString(cursor.getColumnIndex("canal")) : "");
                if (cursor.getColumnIndex("tipo_canal") != -1) {
                    user.put("tipo_canal", cursor.getString(cursor.getColumnIndex("tipo_canal")) != null ? cursor.getString(cursor.getColumnIndex("tipo_canal")) : "");
                }
                if (cursor.getColumnIndex("zterm") != -1) {
                    user.put("zterm", cursor.getString(cursor.getColumnIndex("zterm")) != null ? cursor.getString(cursor.getColumnIndex("zterm")) : "");
                }
                clientList.add(user);
            }
            cursor.close();
        }catch(Exception e){
            if(UsaMonitorEquipoFrio())
                Toasty.error(mContext,"Se encontraron problemas con módulo de Monitor de Equio Frio. Error: "+e.getMessage()).show();
        }

        return  clientList;
    }

    public ArrayList<HashMap<String, String>> getValidaCreditos(String tipo, String clasicxc){
        if(clasicxc.equals("ABC")){
            clasicxc = "F"; //Para la RFC de SAP solo existe formal o informal.
        }
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> datos = new ArrayList<>();
        String query = "Select * from ValidaCreditos where sociedad = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "' and tipform = '"+tipo+"C" + clasicxc + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            //[sociedad], [tipform], [cuentacont], [claseriesgo], [tipocobro], [clasedocven], [clasicxc], [condpago]
            HashMap<String,String> datosCredito = new HashMap<>();
            datosCredito.put("sociedad",cursor.getString(cursor.getColumnIndex("sociedad")) != null ? cursor.getString(cursor.getColumnIndex("sociedad")) : "");
            datosCredito.put("tipform",cursor.getString(cursor.getColumnIndex("tipform")) != null ? cursor.getString(cursor.getColumnIndex("tipform")) : "");
            datosCredito.put("cuentacont",cursor.getString(cursor.getColumnIndex("cuentacont")) != null ? cursor.getString(cursor.getColumnIndex("cuentacont")) : "");
            datosCredito.put("claseriesgo",cursor.getString(cursor.getColumnIndex("claseriesgo")) != null ? cursor.getString(cursor.getColumnIndex("claseriesgo")) : "");
            datosCredito.put("tipocobro",cursor.getString(cursor.getColumnIndex("tipocobro")) != null ? cursor.getString(cursor.getColumnIndex("tipocobro")) : "");
            datosCredito.put("clasedocven",cursor.getString(cursor.getColumnIndex("clasedocven")) != null ? cursor.getString(cursor.getColumnIndex("clasedocven")) : "");
            datosCredito.put("clasicxc",cursor.getString(cursor.getColumnIndex("clasicxc")) != null ? cursor.getString(cursor.getColumnIndex("clasicxc")) : "");
            datosCredito.put("condpago",cursor.getString(cursor.getColumnIndex("condpago")) != null ? cursor.getString(cursor.getColumnIndex("condpago")) : "");
            //Datos SAP4H
            if(cursor.getColumnIndex("check_rule") != -1)
            datosCredito.put("check_rule",cursor.getString(cursor.getColumnIndex("check_rule")) != null ? cursor.getString(cursor.getColumnIndex("check_rule")) : "");
            if(cursor.getColumnIndex("limit_rule") != -1)
            datosCredito.put("limit_rule",cursor.getString(cursor.getColumnIndex("limit_rule")) != null ? cursor.getString(cursor.getColumnIndex("limit_rule")) : "");
            if(cursor.getColumnIndex("credit_group") != -1)
            datosCredito.put("credit_group",cursor.getString(cursor.getColumnIndex("credit_group")) != null ? cursor.getString(cursor.getColumnIndex("credit_group")) : "");

            datos.add(datosCredito);
        }
        cursor.close();
        return  datos;
    }

    public ArrayList<Visitas> getVisitasCliente(String id_cliente){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Visitas> visitasList = new ArrayList<>();
        String query = "SELECT * FROM SAPDVPLAN WHERE KUNNR = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_cliente});
        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setId_solicitud("0");
            visita.setId_formulario("0");
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("VPTYP")) != null ? cursor.getString(cursor.getColumnIndex("VPTYP")) : "" );
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("ZZMETODO")) != null ? cursor.getString(cursor.getColumnIndex("ZZMETODO"))+"DA" : "");
            visita.setRuta(cursor.getString(cursor.getColumnIndex("ROUTE")) != null ? cursor.getString(cursor.getColumnIndex("ROUTE")) : "" );
            visita.setLun_de(cursor.getString(cursor.getColumnIndex("SECLUNES")) != null ? cursor.getString(cursor.getColumnIndex("SECLUNES")) : "" );
            visita.setMar_de(cursor.getString(cursor.getColumnIndex("SECMARTES")) != null ? cursor.getString(cursor.getColumnIndex("SECMARTES")) : "" );
            visita.setMier_de(cursor.getString(cursor.getColumnIndex("SECMIERCOLES")) != null ? cursor.getString(cursor.getColumnIndex("SECMIERCOLES")) : "" );
            visita.setJue_de(cursor.getString(cursor.getColumnIndex("SECJUEVES")) != null ? cursor.getString(cursor.getColumnIndex("SECJUEVES")) : "" );
            visita.setVie_de(cursor.getString(cursor.getColumnIndex("SECVIERNES")) != null ? cursor.getString(cursor.getColumnIndex("SECVIERNES")) : "" );
            visita.setSab_de(cursor.getString(cursor.getColumnIndex("SECSABADO")) != null ? cursor.getString(cursor.getColumnIndex("SECSABADO")) : "" );
            visita.setDom_de(cursor.getString(cursor.getColumnIndex("SECDOMINGO")) != null ? cursor.getString(cursor.getColumnIndex("SECDOMINGO")) : "" );
            visita.setLun_a(cursor.getString(cursor.getColumnIndex("SECLUNES")) != null ? cursor.getString(cursor.getColumnIndex("SECLUNES")) : "" );
            visita.setMar_a(cursor.getString(cursor.getColumnIndex("SECMARTES")) != null ? cursor.getString(cursor.getColumnIndex("SECMARTES")) : "" );
            visita.setMier_a(cursor.getString(cursor.getColumnIndex("SECMIERCOLES")) != null ? cursor.getString(cursor.getColumnIndex("SECMIERCOLES")) : "" );
            visita.setJue_a(cursor.getString(cursor.getColumnIndex("SECJUEVES")) != null ? cursor.getString(cursor.getColumnIndex("SECJUEVES")) : "" );
            visita.setVie_a(cursor.getString(cursor.getColumnIndex("SECVIERNES")) != null ? cursor.getString(cursor.getColumnIndex("SECVIERNES")) : "" );
            visita.setSab_a(cursor.getString(cursor.getColumnIndex("SECSABADO")) != null ? cursor.getString(cursor.getColumnIndex("SECSABADO")) : "" );
            visita.setDom_a(cursor.getString(cursor.getColumnIndex("SECDOMINGO")) != null ? cursor.getString(cursor.getColumnIndex("SECDOMINGO")) : "" );
            visita.setF_ini(cursor.getString(cursor.getColumnIndex("INVALIDO_DE")) != null ? cursor.getString(cursor.getColumnIndex("INVALIDO_DE")) : "" );
            visita.setF_fin(cursor.getString(cursor.getColumnIndex("INVALIDO_A")) != null ? cursor.getString(cursor.getColumnIndex("INVALIDO_A")) : "" );
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("DATFR")) != null ? cursor.getString(cursor.getColumnIndex("DATFR")) : "" );
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("DATTO")) != null ? cursor.getString(cursor.getColumnIndex("DATTO")) : "" );
            visita.setF_frec(cursor.getString(cursor.getColumnIndex("ZFREC")) != null ? cursor.getString(cursor.getColumnIndex("ZFREC")) : "" );
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("FCALID")) != null ? cursor.getString(cursor.getColumnIndex("FCALID")) : "" );

            visitasList.add(visita);
        }
        cursor.close();
        return  visitasList;
    }

    public ArrayList<HashMap<String, String>> getSolicitud(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT * FROM FormHVKOF_solicitud WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            HashMap<String,String> solicitud = new HashMap<>();
            solicitud.put("id_solicitud",cursor.getString(cursor.getColumnIndex("id_solicitud")) != null ? cursor.getString(cursor.getColumnIndex("id_solicitud")) : "" );
            solicitud.put("IDFORM",cursor.getString(cursor.getColumnIndex("IDFORM")) != null ? cursor.getString(cursor.getColumnIndex("IDFORM")) : "" );
            solicitud.put("TIPFORM",cursor.getString(cursor.getColumnIndex("TIPFORM")) != null ? cursor.getString(cursor.getColumnIndex("TIPFORM")) : "" );
            solicitud.put("FECCRE",cursor.getString(cursor.getColumnIndex("FECCRE")) != null ? cursor.getString(cursor.getColumnIndex("FECCRE")) : "" );
            solicitud.put("USUSOL",cursor.getString(cursor.getColumnIndex("USUSOL")) != null ? cursor.getString(cursor.getColumnIndex("USUSOL")) : "" );
            solicitud.put("ESTADO",cursor.getString(cursor.getColumnIndex("ESTADO")).trim() != null ? cursor.getString(cursor.getColumnIndex("ESTADO")).trim() : "");
            solicitud.put("W_CTE-AKONT",cursor.getString(cursor.getColumnIndex("W_CTE-AKONT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-AKONT")) : "" );
            solicitud.put("W_CTE-ALTKN",cursor.getString(cursor.getColumnIndex("W_CTE-ALTKN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ALTKN")) : "" );
            solicitud.put("W_CTE-ANTLF",cursor.getString(cursor.getColumnIndex("W_CTE-ANTLF")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ANTLF")) : "" );
            solicitud.put("W_CTE-BUKRS",cursor.getString(cursor.getColumnIndex("W_CTE-BUKRS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-BUKRS")) : "" );
            solicitud.put("W_CTE-BZIRK",cursor.getString(cursor.getColumnIndex("W_CTE-BZIRK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-BZIRK")) : "" );
            solicitud.put("W_CTE-CITY1",cursor.getString(cursor.getColumnIndex("W_CTE-CITY1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CITY1")) : "" );
            solicitud.put("W_CTE-CITY2",cursor.getString(cursor.getColumnIndex("W_CTE-CITY2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CITY2")) : "" );
            solicitud.put("W_CTE-CTLPC",cursor.getString(cursor.getColumnIndex("W_CTE-CTLPC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CTLPC")) : "" );
            solicitud.put("W_CTE-DATAB",cursor.getString(cursor.getColumnIndex("W_CTE-DATAB")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATAB")) : "" );
            solicitud.put("W_CTE-DATBI",cursor.getString(cursor.getColumnIndex("W_CTE-DATBI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATBI")) : "" );
            solicitud.put("W_CTE-DBRTG",cursor.getString(cursor.getColumnIndex("W_CTE-DBRTG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DBRTG")) : "" );
            solicitud.put("W_CTE-DMBTR1",String.format ("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-DMBTR1"))) );
            solicitud.put("W_CTE-DMBTR2",String.format ("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-DMBTR2"))) );
            solicitud.put("W_CTE-DMBTR3",String.format ("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-DMBTR3"))) );
            solicitud.put("W_CTE-FAX_EXTENS",cursor.getString(cursor.getColumnIndex("W_CTE-FAX_EXTENS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FAX_EXTENS")) : "" );
            solicitud.put("W_CTE-FAX_NUMBER",cursor.getString(cursor.getColumnIndex("W_CTE-FAX_NUMBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FAX_NUMBER")) : "" );
            solicitud.put("W_CTE-FDGRV",cursor.getString(cursor.getColumnIndex("W_CTE-FDGRV")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FDGRV")) : "" );
            solicitud.put("W_CTE-FLAG_FACT",cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_FACT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_FACT")) : "" );
            solicitud.put("W_CTE-FLAG_NTEN",cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_NTEN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_NTEN")) : "" );
            solicitud.put("W_CTE-HITYP",cursor.getString(cursor.getColumnIndex("W_CTE-HITYP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HITYP")) : "" );
            solicitud.put("W_CTE-HKUNNR",cursor.getString(cursor.getColumnIndex("W_CTE-HKUNNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HKUNNR")) : "" );
            solicitud.put("W_CTE-HOME_CITY",cursor.getString(cursor.getColumnIndex("W_CTE-HOME_CITY")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HOME_CITY")) : "" );
            solicitud.put("W_CTE-HOUSE_NUM1",cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM1")) : "" );
            solicitud.put("W_CTE-HOUSE_NUM2",cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM2")) : "" );
            solicitud.put("W_CTE-INCO1",cursor.getString(cursor.getColumnIndex("W_CTE-INCO1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-INCO1")) : "" );
            solicitud.put("W_CTE-INCO2",cursor.getString(cursor.getColumnIndex("W_CTE-INCO2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-INCO2")) : "" );
            solicitud.put("W_CTE-KALKS",cursor.getString(cursor.getColumnIndex("W_CTE-KALKS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KALKS")) : "" );
            solicitud.put("W_CTE-KATR3",cursor.getString(cursor.getColumnIndex("W_CTE-KATR3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR3")) : "" );
            solicitud.put("W_CTE-KATR4",cursor.getString(cursor.getColumnIndex("W_CTE-KATR4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR4")) : "" );
            solicitud.put("W_CTE-KATR5",cursor.getString(cursor.getColumnIndex("W_CTE-KATR5")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR5")) : "" );
            solicitud.put("W_CTE-KATR8",cursor.getString(cursor.getColumnIndex("W_CTE-KATR8")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR8")) : "" );
            solicitud.put("W_CTE-KDGRP",cursor.getString(cursor.getColumnIndex("W_CTE-KDGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KDGRP")) : "" );
            solicitud.put("W_CTE-KKBER",cursor.getString(cursor.getColumnIndex("W_CTE-KKBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KKBER")) : "" );
            solicitud.put("W_CTE-KLABC",cursor.getString(cursor.getColumnIndex("W_CTE-KLABC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KLABC")) : "" );
            solicitud.put("W_CTE-KLIMK",String.format ("%.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-KLIMK"))) );
            solicitud.put("W_CTE-KNKLI",cursor.getString(cursor.getColumnIndex("W_CTE-KNKLI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KNKLI")) : "" );
            solicitud.put("W_CTE-KTGRD",cursor.getString(cursor.getColumnIndex("W_CTE-KTGRD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KTGRD")) : "" );
            solicitud.put("W_CTE-KTOKD",cursor.getString(cursor.getColumnIndex("W_CTE-KTOKD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KTOKD")) : "" );
            solicitud.put("W_CTE-KUKLA",cursor.getString(cursor.getColumnIndex("W_CTE-KUKLA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KUKLA")) : "" );
            solicitud.put("W_CTE-KUNNR",cursor.getString(cursor.getColumnIndex("W_CTE-KUNNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KUNNR")) : "" );
            solicitud.put("W_CTE-KVGR1",cursor.getString(cursor.getColumnIndex("W_CTE-KVGR1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR1")) : "" );
            solicitud.put("W_CTE-KVGR2",cursor.getString(cursor.getColumnIndex("W_CTE-KVGR2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR2")) : "" );
            solicitud.put("W_CTE-KVGR3",cursor.getString(cursor.getColumnIndex("W_CTE-KVGR3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR3")) : "" );
            solicitud.put("W_CTE-KVGR5",cursor.getString(cursor.getColumnIndex("W_CTE-KVGR5")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR5")) : "" );
            solicitud.put("W_CTE-LAND1",cursor.getString(cursor.getColumnIndex("W_CTE-LAND1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LAND1")) : "" );
            solicitud.put("W_CTE-LIFNR",cursor.getString(cursor.getColumnIndex("W_CTE-LIFNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LIFNR")) : "" );
            solicitud.put("W_CTE-LIMSUG",String.format ("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-LIMSUG"))) );
            solicitud.put("W_CTE-LOCATION",cursor.getString(cursor.getColumnIndex("W_CTE-LOCATION")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LOCATION")) : "" );
            solicitud.put("W_CTE-LPRIO",cursor.getString(cursor.getColumnIndex("W_CTE-LPRIO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LPRIO")) : "" );
            solicitud.put("W_CTE-LZONE",cursor.getString(cursor.getColumnIndex("W_CTE-LZONE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LZONE")) : "" );
            solicitud.put("W_CTE-NAME_CO",cursor.getString(cursor.getColumnIndex("W_CTE-NAME_CO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME_CO")) : "" );
            solicitud.put("W_CTE-NAME1",cursor.getString(cursor.getColumnIndex("W_CTE-NAME1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME1")) : "" );
            solicitud.put("W_CTE-NAME2",cursor.getString(cursor.getColumnIndex("W_CTE-NAME2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME2")) : "" );
            solicitud.put("W_CTE-NAME3",cursor.getString(cursor.getColumnIndex("W_CTE-NAME3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME3")) : "" );
            solicitud.put("W_CTE-NAME4",cursor.getString(cursor.getColumnIndex("W_CTE-NAME4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME4")) : "" );
            solicitud.put("W_CTE-PERNR",cursor.getString(cursor.getColumnIndex("W_CTE-PERNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PERNR")) : "" );
            solicitud.put("W_CTE-PO_BOX",cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX")) : "" );
            solicitud.put("W_CTE-PO_BOX_LOC",cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_LOC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_LOC")) : "" );
            solicitud.put("W_CTE-PO_BOX_REG",cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_REG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_REG")) : "" );
            solicitud.put("W_CTE-POST_CODE2",cursor.getString(cursor.getColumnIndex("W_CTE-POST_CODE2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-POST_CODE2")) : "" );
            solicitud.put("W_CTE-PRFRE",cursor.getString(cursor.getColumnIndex("W_CTE-PRFRE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PRFRE")) : "" );
            solicitud.put("W_CTE-PSON1",cursor.getString(cursor.getColumnIndex("W_CTE-PSON1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSON1")) : "" );
            solicitud.put("W_CTE-PSON2",cursor.getString(cursor.getColumnIndex("W_CTE-PSON2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSON2")) : "" );
            solicitud.put("W_CTE-PSON3",cursor.getString(cursor.getColumnIndex("W_CTE-PSON3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSON3")) : "" );
            solicitud.put("W_CTE-PSTLZ",cursor.getString(cursor.getColumnIndex("W_CTE-PSTLZ")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSTLZ")) : "" );
            solicitud.put("W_CTE-PVKSM",cursor.getString(cursor.getColumnIndex("W_CTE-PVKSM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PVKSM")) : "" );
            solicitud.put("W_CTE-REGION",cursor.getString(cursor.getColumnIndex("W_CTE-REGION")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-REGION")) : "" );
            solicitud.put("W_CTE-ROOMNUMBER",cursor.getString(cursor.getColumnIndex("W_CTE-ROOMNUMBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ROOMNUMBER")) : "" );
            solicitud.put("W_CTE-SMTP_ADDR",cursor.getString(cursor.getColumnIndex("W_CTE-SMTP_ADDR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SMTP_ADDR")) : "" );
            solicitud.put("W_CTE-STCD1",cursor.getString(cursor.getColumnIndex("W_CTE-STCD1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STCD1")) : "" );
            solicitud.put("W_CTE-STCD3",cursor.getString(cursor.getColumnIndex("W_CTE-STCD3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STCD3")) : "" );
            solicitud.put("W_CTE-STR_SUPPL1",cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL1")) : "" );
            solicitud.put("W_CTE-STR_SUPPL2",cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL2")) : "" );
            solicitud.put("W_CTE-STR_SUPPL3",cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL3")) : "" );
            solicitud.put("W_CTE-STREET",cursor.getString(cursor.getColumnIndex("W_CTE-STREET")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STREET")) : "" );
            solicitud.put("W_CTE-TEL_EXTENS",cursor.getString(cursor.getColumnIndex("W_CTE-TEL_EXTENS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TEL_EXTENS")) : "" );
            solicitud.put("W_CTE-TEL_NUMBER",cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER")) : "" );
            solicitud.put("W_CTE-TEL_NUMBER2",cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER2")) : "" );
            solicitud.put("W_CTE-TELNUMBER2",cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER2")) : "" );
            solicitud.put("W_CTE-TELNUMBER3",cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER3")) : "" );
            solicitud.put("W_CTE-TOGRU",cursor.getString(cursor.getColumnIndex("W_CTE-TOGRU")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TOGRU")) : "" );
            solicitud.put("W_CTE-UPDAT",cursor.getString(cursor.getColumnIndex("W_CTE-UPDAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-UPDAT")) : "" );
            solicitud.put("W_CTE-VKBUR",cursor.getString(cursor.getColumnIndex("W_CTE-VKBUR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VKBUR")) : "" );
            solicitud.put("W_CTE-VKGRP",cursor.getString(cursor.getColumnIndex("W_CTE-VKGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VKGRP")) : "" );
            solicitud.put("W_CTE-VKORG",cursor.getString(cursor.getColumnIndex("W_CTE-VKORG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VKORG")) : "" );
            solicitud.put("W_CTE-VSBED",cursor.getString(cursor.getColumnIndex("W_CTE-VSBED")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VSBED")) : "" );
            solicitud.put("W_CTE-VWERK",cursor.getString(cursor.getColumnIndex("W_CTE-VWERK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VWERK")) : "" );
            solicitud.put("W_CTE-WAERS",cursor.getString(cursor.getColumnIndex("W_CTE-WAERS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-WAERS")) : "" );
            solicitud.put("W_CTE-XZVER",cursor.getString(cursor.getColumnIndex("W_CTE-XZVER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-XZVER")) : "" );
            solicitud.put("W_CTE-ZGPOCANAL",cursor.getString(cursor.getColumnIndex("W_CTE-ZGPOCANAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZGPOCANAL")) : "" );
            solicitud.put("W_CTE-ZTERM",cursor.getString(cursor.getColumnIndex("W_CTE-ZTERM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZTERM")) : "" );
            solicitud.put("W_CTE-ZTPOCANAL",cursor.getString(cursor.getColumnIndex("W_CTE-ZTPOCANAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZTPOCANAL")) : "" );
            solicitud.put("W_CTE-ZSEGPRE",cursor.getString(cursor.getColumnIndex("W_CTE-ZSEGPRE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZSEGPRE")) : "" );
            solicitud.put("W_CTE-ZWELS",cursor.getString(cursor.getColumnIndex("W_CTE-ZWELS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZWELS")) : "" );
            solicitud.put("W_CTE-ZZAUART",cursor.getString(cursor.getColumnIndex("W_CTE-ZZAUART")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZAUART")) : "" );
            solicitud.put("W_CTE-ZZBLOQU",cursor.getString(cursor.getColumnIndex("W_CTE-ZZBLOQU")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZBLOQU")) : "" );
            solicitud.put("W_CTE-ZZCANAL",cursor.getString(cursor.getColumnIndex("W_CTE-ZZCANAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCANAL")) : "" );
            solicitud.put("W_CTE-ZZCATFOCO",cursor.getString(cursor.getColumnIndex("W_CTE-ZZCATFOCO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCATFOCO")) : "" );
            solicitud.put("W_CTE-ZZCRMA_LAT",cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LAT")) : "" );
            solicitud.put("W_CTE-ZZCRMA_LONG",cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LONG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LONG")) : "" );
            solicitud.put("W_CTE-ZZENT1",cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT1")) : "" );
            solicitud.put("W_CTE-ZZENT2",cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT2")) : "" );
            solicitud.put("W_CTE-ZZENT3",cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT3")) : "" );
            solicitud.put("W_CTE-ZZENT4",cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT4")) : "" );
            solicitud.put("W_CTE-ZZENT5",cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT5")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT5")) : "" );
            solicitud.put("W_CTE-ZZERDAT",cursor.getString(cursor.getColumnIndex("W_CTE-ZZERDAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZERDAT")) : "" );
            solicitud.put("W_CTE-ZZGERENTE",cursor.getString(cursor.getColumnIndex("W_CTE-ZZGERENTE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZGERENTE")) : "" );
            solicitud.put("W_CTE-ZZINTCO",cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTCO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTCO")) : "" );
            solicitud.put("W_CTE-ZZINTTACT",cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTTACT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTTACT")) : "" );
            solicitud.put("W_CTE-ZZJEFATURA",cursor.getString(cursor.getColumnIndex("W_CTE-ZZJEFATURA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZJEFATURA")) : "" );
            solicitud.put("W_CTE-ZZOCCONS",cursor.getString(cursor.getColumnIndex("W_CTE-ZZOCCONS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZOCCONS")) : "" );
            solicitud.put("W_CTE-ZZREJA",cursor.getString(cursor.getColumnIndex("W_CTE-ZZREJA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZREJA")) : "" );
            solicitud.put("W_CTE-ZZSEGCOM",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGCOM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGCOM")) : "" );
            solicitud.put("W_CTE-ZZSEGDESC",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGDESC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGDESC")) : "" );
            solicitud.put("W_CTE-ZZSEGEXH",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGEXH")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGEXH")) : "" );
            solicitud.put("W_CTE-ZZSEGPDE",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDE")) : "" );
            solicitud.put("W_CTE-ZZSEGPDV",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDV")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDV")) : "" );
            solicitud.put("W_CTE-ZZSEGPORT",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPORT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPORT")) : "" );
            solicitud.put("W_CTE-ZZSEGPRE",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPRE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPRE")) : "" );
            solicitud.put("W_CTE-ZZSHARE",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSHARE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSHARE")) : "" );
            solicitud.put("W_CTE-ZZSTAT",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSTAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSTAT")) : "" );
            solicitud.put("W_CTE-ZZSUBUNNEG",cursor.getString(cursor.getColumnIndex("W_CTE-ZZSUBUNNEG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSUBUNNEG")) : "" );
            solicitud.put("W_CTE-ZZTIPSERV",cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPSERV")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPSERV")) : "" );
            solicitud.put("W_CTE-ZZTFISI",cursor.getString(cursor.getColumnIndex("W_CTE-ZZTFISI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZTFISI")) : "" );
            solicitud.put("W_CTE-ZZUNNEG",cursor.getString(cursor.getColumnIndex("W_CTE-ZZUNNEG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZUNNEG")) : "" );
            solicitud.put("W_CTE-ZZZONACOST",cursor.getString(cursor.getColumnIndex("W_CTE-ZZZONACOST")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZZONACOST")) : "" );
            solicitud.put("W_CTE-COMENTARIOS",cursor.getString(cursor.getColumnIndex("W_CTE-COMENTARIOS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-COMENTARIOS")) : "" );
            solicitud.put("fuera_politica_plazo",cursor.getString(cursor.getColumnIndex("fuera_politica_plazo")) != null ? cursor.getString(cursor.getColumnIndex("fuera_politica_plazo")) : "" );
            solicitud.put("fuera_politica_monto",cursor.getString(cursor.getColumnIndex("fuera_politica_monto")) != null ? cursor.getString(cursor.getColumnIndex("fuera_politica_monto")) : "" );
            solicitud.put("W_CTE-NOTIFICANTES",cursor.getString(cursor.getColumnIndex("W_CTE-NOTIFICANTES")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NOTIFICANTES")) : "" );
            solicitud.put("FECFIN",cursor.getString(cursor.getColumnIndex("FECFIN")) != null ? cursor.getString(cursor.getColumnIndex("FECFIN")) : "" );
            solicitud.put("W_CTE-ZZKEYACC",cursor.getString(cursor.getColumnIndex("W_CTE-ZZKEYACC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZKEYACC")) : "" );
            solicitud.put("W_CTE-ZIBASE",cursor.getString(cursor.getColumnIndex("W_CTE-ZIBASE")) != null ? removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("W_CTE-ZIBASE"))) : "" );
            solicitud.put("W_CTE-ZADICI",cursor.getString(cursor.getColumnIndex("W_CTE-ZADICI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZADICI")) : "" );
            solicitud.put("W_CTE-ZZUDATE",cursor.getString(cursor.getColumnIndex("W_CTE-ZZUDATE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZUDATE")) : "" );
            solicitud.put("W_CTE-AUFSD",cursor.getString(cursor.getColumnIndex("W_CTE-AUFSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-AUFSD")) : "" );
            solicitud.put("W_CTE-LIFSD",cursor.getString(cursor.getColumnIndex("W_CTE-LIFSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LIFSD")) : "" );
            solicitud.put("W_CTE-FAKSD",cursor.getString(cursor.getColumnIndex("W_CTE-FAKSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FAKSD")) : "" );
            solicitud.put("W_CTE-CASSD",cursor.getString(cursor.getColumnIndex("W_CTE-CASSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CASSD")) : "" );
            solicitud.put("W_CTE-LOEVM",cursor.getString(cursor.getColumnIndex("W_CTE-LOEVM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LOEVM")) : "" );
            solicitud.put("W_CTE-TELF2",cursor.getString(cursor.getColumnIndex("W_CTE-TELF2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TELF2")) : "" );
            solicitud.put("W_CTE-VTWEG",cursor.getString(cursor.getColumnIndex("W_CTE-VTWEG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VTWEG")) : "" );
            solicitud.put("W_CTE-SPART",cursor.getString(cursor.getColumnIndex("W_CTE-SPART")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SPART")) : "" );
            solicitud.put("W_CTE-PERFK",cursor.getString(cursor.getColumnIndex("W_CTE-PERFK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PERFK")) : "" );
            solicitud.put("W_CTE-KVGR4",cursor.getString(cursor.getColumnIndex("W_CTE-KVGR4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR4")) : "" );
            solicitud.put("W_CTE-KONDA",cursor.getString(cursor.getColumnIndex("W_CTE-KONDA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KONDA")) : "" );
            solicitud.put("W_CTE-RUTAHH",cursor.getString(cursor.getColumnIndex("W_CTE-RUTAHH")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-RUTAHH")) : "" );
            solicitud.put("W_CTE-ZZESQUINA",cursor.getString(cursor.getColumnIndex("W_CTE-ZZESQUINA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZESQUINA")) : "" );
            solicitud.put("W_CTE-ZZESTAC",cursor.getString(cursor.getColumnIndex("W_CTE-ZZESTAC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZESTAC")) : "" );
            solicitud.put("W_CTE-KATR2",cursor.getString(cursor.getColumnIndex("W_CTE-KATR2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR2")) : "" );
            solicitud.put("W_CTE-ZZTIPONEC",cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPONEC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPONEC")) : "" );
            solicitud.put("W_CTE-VBUND",cursor.getString(cursor.getColumnIndex("W_CTE-VBUND")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VBUND")) : "" );
            solicitud.put("SIGUIENTE_APROBADOR",cursor.getString(cursor.getColumnIndex("SIGUIENTE_APROBADOR")) != null ? cursor.getString(cursor.getColumnIndex("SIGUIENTE_APROBADOR")) : "" );
            try {
                solicitud.put("W_CTE-ORT01", cursor.getString(cursor.getColumnIndex("W_CTE-ORT01")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ORT01")) : "");
                solicitud.put("W_CTE-STCDT", cursor.getString(cursor.getColumnIndex("W_CTE-STCDT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STCDT")) : "");
                solicitud.put("W_CTE-ZONA_FRANCA", cursor.getString(cursor.getColumnIndex("W_CTE-ZONA_FRANCA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZONA_FRANCA")) : "");
                solicitud.put("W_CTE-FITYP", cursor.getString(cursor.getColumnIndex("W_CTE-FITYP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FITYP")) : "");
                solicitud.put("W_CTE-GUZTE", cursor.getString(cursor.getColumnIndex("W_CTE-GUZTE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-GUZTE")) : "");
                solicitud.put("W_CTE-ID_PREFORMULARIO", cursor.getString(cursor.getColumnIndex("W_CTE-ID_PREFORMULARIO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ID_PREFORMULARIO")) : "");
            }catch(Exception e){}
            //CAMPOS PARA AVISOS DE EQUIPO FRIO
            solicitud.put("W_CTE-IM_EQUIPMENT",cursor.getString(cursor.getColumnIndex("W_CTE-IM_EQUIPMENT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_EQUIPMENT")) : "" );
            solicitud.put("W_CTE-IM_PARTNER",cursor.getString(cursor.getColumnIndex("W_CTE-IM_PARTNER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_PARTNER")) : "" );
            solicitud.put("W_CTE-IM_NOTIF_TYPE",cursor.getString(cursor.getColumnIndex("W_CTE-IM_NOTIF_TYPE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_NOTIF_TYPE")) : "" );
            solicitud.put("W_CTE-IM_DESCRIPT",cursor.getString(cursor.getColumnIndex("W_CTE-IM_DESCRIPT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_DESCRIPT")) : "" );
            solicitud.put("W_CTE-IM_MATERIAL",cursor.getString(cursor.getColumnIndex("W_CTE-IM_MATERIAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_MATERIAL")) : "" );
            solicitud.put("W_CTE-IM_SERIALNO",cursor.getString(cursor.getColumnIndex("W_CTE-IM_SERIALNO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_SERIALNO")) : "" );
            solicitud.put("W_CTE-IM_CAUSE_CODE",cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODE")) : "" );
            solicitud.put("W_CTE-IM_CAUSE_CODEGRP",cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODEGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODEGRP")) : "" );
            solicitud.put("W_CTE-IM_D_CODE",cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODE")) : "" );
            solicitud.put("W_CTE-IM_D_CODEGRP",cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODEGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODEGRP")) : "" );
            solicitud.put("W_CTE-IM_PRIORITY",cursor.getString(cursor.getColumnIndex("W_CTE-IM_PRIORITY")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_PRIORITY")) : "" );
            solicitud.put("W_CTE-IM_SHORT_TEXT",cursor.getString(cursor.getColumnIndex("W_CTE-IM_SHORT_TEXT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_SHORT_TEXT")) : "" );
            solicitud.put("W_CTE-IM_TEXT_LINE",cursor.getString(cursor.getColumnIndex("W_CTE-IM_TEXT_LINE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_TEXT_LINE")) : "" );
            solicitud.put("W_CTE-IM_NUM_AVISO",cursor.getString(cursor.getColumnIndex("W_CTE-IM_NUM_AVISO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_NUM_AVISO")) : "" );

            //CAMPOS NEUVOS DE NI Y PA PARA REALIZAR CONTRATOS O POLITICAS CON ESTA INFORMACION DENTRO DEL TEXTO FIRMABLE
            solicitud.put("W_CTE-ESTADO_CIVIL",cursor.getString(cursor.getColumnIndex("W_CTE-ESTADO_CIVIL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ESTADO_CIVIL")) : "" );
            solicitud.put("W_CTE-ACTIVIDAD_ECONOMICA",cursor.getString(cursor.getColumnIndex("W_CTE-ACTIVIDAD_ECONOMICA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ACTIVIDAD_ECONOMICA")) : "" );
            solicitud.put("W_CTE-DURACION_CONTRATO",cursor.getString(cursor.getColumnIndex("W_CTE-DURACION_CONTRATO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DURACION_CONTRATO")) : "" );
            solicitud.put("W_CTE-TIPO_CREDITO",cursor.getString(cursor.getColumnIndex("W_CTE-TIPO_CREDITO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TIPO_CREDITO")) : "" );

            try {
                //CAMPOS NUEVOS PARA FORMULARIO ENTREGA DE TARJETAS
                solicitud.put("W_CTE-IN_NUM_TARJETA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_NUM_TARJETA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_NUM_TARJETA")) : "");
                solicitud.put("W_CTE-IN_NOMBRE_RECIBE", cursor.getString(cursor.getColumnIndex("W_CTE-IN_NOMBRE_RECIBE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_NOMBRE_RECIBE")) : "");
                solicitud.put("W_CTE-IN_MONTO_TARJETA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_MONTO_TARJETA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_MONTO_TARJETA")) : "");
                solicitud.put("W_CTE-IN_CEDULA_RECIBE", cursor.getString(cursor.getColumnIndex("W_CTE-IN_CEDULA_RECIBE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_CEDULA_RECIBE")) : "");
                solicitud.put("W_CTE-IN_CONCEPTO_ENTREGA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA")) : "");
                solicitud.put("W_CTE-IN_CONCEPTO_ENTREGA2", cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA2")) : "");
                solicitud.put("W_CTE-IN_PERIODO_VALIDEZ", cursor.getString(cursor.getColumnIndex("W_CTE-IN_PERIODO_VALIDEZ")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_PERIODO_VALIDEZ")) : "");
                solicitud.put("W_CTE-IN_PLAN_INICIATIVA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_PLAN_INICIATIVA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_PLAN_INICIATIVA")) : "");
            }catch(Exception e){}

            try {
                //CAMPOS NUEVOS PARA MODULO CENSO QUIPO FRIO
                solicitud.put("W_CTE-CE_MOTIVO_ALERTA", cursor.getString(cursor.getColumnIndex("W_CTE-CE_MOTIVO_ALERTA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_MOTIVO_ALERTA")) : "");
                solicitud.put("W_CTE-CE_LOCAL_ABIERTO", cursor.getString(cursor.getColumnIndex("W_CTE-CE_LOCAL_ABIERTO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_LOCAL_ABIERTO")) : "");
                solicitud.put("W_CTE-CE_CONTACTO", cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO")) : "");
                solicitud.put("W_CTE-CE_CONTACTO_ADICIONAL", cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO_ADICIONAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO_ADICIONAL")) : "");
            }catch(Exception e){}

            try {
                //CAMPOS NUEVOS PARA MODULO CENSO QUIPO FRIO
                solicitud.put("W_CTE-VENTA_COMPROMETIDA", cursor.getString(cursor.getColumnIndex("W_CTE-VENTA_COMPROMETIDA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VENTA_COMPROMETIDA")) : "");
                solicitud.put("W_CTE-NUM_PUERTAS_ACTUAL", cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_ACTUAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_ACTUAL")) : "");
                solicitud.put("W_CTE-NUM_PUERTAS_MODELO", cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_MODELO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_MODELO")) : "");
                solicitud.put("W_CTE-PUERTAS_SUGERIDAS", cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_SUGERIDAS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_SUGERIDAS")) : "");
                solicitud.put("W_CTE-PUERTAS_INSTALADAS", cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_INSTALADAS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_INSTALADAS")) : "");
                solicitud.put("W_CTE-PUERTAS_OBJETIVO", cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_OBJETIVO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_OBJETIVO")) : "");
            }catch(Exception e){}

            try {
                //CAMPOS NUEVOS PARA CREDITO SAP4HANA
                solicitud.put("W_CTE-CREDIT_SGMNT", cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_SGMNT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_SGMNT")) : "");
                solicitud.put("W_CTE-CREDIT_GROUP", cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_GROUP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_GROUP")) : "");
                solicitud.put("W_CTE-CHECK_RULE", cursor.getString(cursor.getColumnIndex("W_CTE-CHECK_RULE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CHECK_RULE")) : "");
                solicitud.put("W_CTE-LIMIT_RULE", cursor.getString(cursor.getColumnIndex("W_CTE-LIMIT_RULE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LIMIT_RULE")) : "");
                solicitud.put("W_CTE-XBLOCKED", cursor.getString(cursor.getColumnIndex("W_CTE-XBLOCKED")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-XBLOCKED")) : "");
            }catch(Exception e){}

            try {
                //CAMPOS NUEVOS PARA COLOMBIA
                solicitud.put("W_CTE-BEGRU", cursor.getString(cursor.getColumnIndex("W_CTE-BEGRU")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-BEGRU")) : "");
                solicitud.put("W_CTE-STRAS", cursor.getString(cursor.getColumnIndex("W_CTE-STRAS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STRAS")) : "");
                solicitud.put("W_CTE-SORT1", cursor.getString(cursor.getColumnIndex("W_CTE-SORT1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SORT1")) : "");
                solicitud.put("W_CTE-SORT2", cursor.getString(cursor.getColumnIndex("W_CTE-SORT2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SORT2")) : "");
                solicitud.put("W_CTE-STKZN", cursor.getString(cursor.getColumnIndex("W_CTE-STKZN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STKZN")) : "");
            }catch(Exception e){}

            try {//Para Actividad Economica CR
                solicitud.put("W_CTE-ZZCNAE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZCNAE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCNAE")) : "");
                solicitud.put("W_CTE-ZREGFISCAL", cursor.getString(cursor.getColumnIndex("W_CTE-ZREGFISCAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZREGFISCAL")) : "");
            }catch(Exception e){}

            try {//Campo especifico para colombia para saber si viene de un cliente anterior por cambio de razon social
                solicitud.put("CAMBIO_RAZON", cursor.getString(cursor.getColumnIndex("CAMBIO_RAZON")) != null ? cursor.getString(cursor.getColumnIndex("CAMBIO_RAZON")) : "");
            }catch(Exception e){}

            try {//Campos nuevos para manejo de formulario de consignacion
                solicitud.put("W_CTE-DATST", cursor.getString(cursor.getColumnIndex("W_CTE-DATST")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATST")) : "");
                solicitud.put("W_CTE-DATEN", cursor.getString(cursor.getColumnIndex("W_CTE-DATEN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATEN")) : "");
            }catch(Exception e){}

            formList.add(solicitud);
        }

        cursor.close();
        return  formList;
    }

    public ArrayList<HashMap<String, String>> getSolicitudOld(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT * FROM FormHVKOF_old_solicitud WHERE rtrim(id_solicitud) = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()) {
            HashMap<String, String> solicitud = new HashMap<>();
            solicitud.put("id_solicitud", cursor.getString(cursor.getColumnIndex("id_solicitud")) != null ? cursor.getString(cursor.getColumnIndex("id_solicitud")) : "");
            solicitud.put("IDFORM", cursor.getString(cursor.getColumnIndex("IDFORM")) != null ? cursor.getString(cursor.getColumnIndex("IDFORM")) : "");
            solicitud.put("TIPFORM", cursor.getString(cursor.getColumnIndex("TIPFORM")) != null ? cursor.getString(cursor.getColumnIndex("TIPFORM")) : "");
            solicitud.put("FECCRE", cursor.getString(cursor.getColumnIndex("FECCRE")) != null ? cursor.getString(cursor.getColumnIndex("FECCRE")) : "");
            solicitud.put("USUSOL", cursor.getString(cursor.getColumnIndex("USUSOL")) != null ? cursor.getString(cursor.getColumnIndex("USUSOL")) : "");
            solicitud.put("ESTADO", cursor.getString(cursor.getColumnIndex("ESTADO")).trim() != null ? cursor.getString(cursor.getColumnIndex("ESTADO")).trim() : "");
            solicitud.put("W_CTE-AKONT", cursor.getString(cursor.getColumnIndex("W_CTE-AKONT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-AKONT")) : "");
            solicitud.put("W_CTE-ALTKN", cursor.getString(cursor.getColumnIndex("W_CTE-ALTKN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ALTKN")) : "");
            solicitud.put("W_CTE-ANTLF", cursor.getString(cursor.getColumnIndex("W_CTE-ANTLF")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ANTLF")) : "");
            solicitud.put("W_CTE-BUKRS", cursor.getString(cursor.getColumnIndex("W_CTE-BUKRS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-BUKRS")) : "");
            solicitud.put("W_CTE-BZIRK", cursor.getString(cursor.getColumnIndex("W_CTE-BZIRK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-BZIRK")) : "");
            solicitud.put("W_CTE-CITY1", cursor.getString(cursor.getColumnIndex("W_CTE-CITY1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CITY1")) : "");
            solicitud.put("W_CTE-CITY2", cursor.getString(cursor.getColumnIndex("W_CTE-CITY2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CITY2")) : "");
            solicitud.put("W_CTE-CTLPC", cursor.getString(cursor.getColumnIndex("W_CTE-CTLPC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CTLPC")) : "");
            solicitud.put("W_CTE-DATAB", cursor.getString(cursor.getColumnIndex("W_CTE-DATAB")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATAB")) : "");
            solicitud.put("W_CTE-DATBI", cursor.getString(cursor.getColumnIndex("W_CTE-DATBI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATBI")) : "");
            solicitud.put("W_CTE-DBRTG", cursor.getString(cursor.getColumnIndex("W_CTE-DBRTG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DBRTG")) : "");
            solicitud.put("W_CTE-DMBTR1", String.format("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-DMBTR1"))));
            solicitud.put("W_CTE-DMBTR2", String.format("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-DMBTR2"))));
            solicitud.put("W_CTE-DMBTR3", String.format("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-DMBTR3"))));
            solicitud.put("W_CTE-FAX_EXTENS", cursor.getString(cursor.getColumnIndex("W_CTE-FAX_EXTENS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FAX_EXTENS")) : "");
            solicitud.put("W_CTE-FAX_NUMBER", cursor.getString(cursor.getColumnIndex("W_CTE-FAX_NUMBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FAX_NUMBER")) : "");
            solicitud.put("W_CTE-FDGRV", cursor.getString(cursor.getColumnIndex("W_CTE-FDGRV")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FDGRV")) : "");
            solicitud.put("W_CTE-FLAG_FACT", cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_FACT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_FACT")) : "");
            solicitud.put("W_CTE-FLAG_NTEN", cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_NTEN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FLAG_NTEN")) : "");
            solicitud.put("W_CTE-HITYP", cursor.getString(cursor.getColumnIndex("W_CTE-HITYP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HITYP")) : "");
            solicitud.put("W_CTE-HKUNNR", cursor.getString(cursor.getColumnIndex("W_CTE-HKUNNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HKUNNR")) : "");
            solicitud.put("W_CTE-HOME_CITY", cursor.getString(cursor.getColumnIndex("W_CTE-HOME_CITY")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HOME_CITY")) : "");
            solicitud.put("W_CTE-HOUSE_NUM1", cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM1")) : "");
            solicitud.put("W_CTE-HOUSE_NUM2", cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-HOUSE_NUM2")) : "");
            solicitud.put("W_CTE-INCO1", cursor.getString(cursor.getColumnIndex("W_CTE-INCO1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-INCO1")) : "");
            solicitud.put("W_CTE-INCO2", cursor.getString(cursor.getColumnIndex("W_CTE-INCO2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-INCO2")) : "");
            solicitud.put("W_CTE-KALKS", cursor.getString(cursor.getColumnIndex("W_CTE-KALKS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KALKS")) : "");
            solicitud.put("W_CTE-KATR3", cursor.getString(cursor.getColumnIndex("W_CTE-KATR3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR3")) : "");
            solicitud.put("W_CTE-KATR4", cursor.getString(cursor.getColumnIndex("W_CTE-KATR4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR4")) : "");
            solicitud.put("W_CTE-KATR5", cursor.getString(cursor.getColumnIndex("W_CTE-KATR5")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR5")) : "");
            solicitud.put("W_CTE-KATR8", cursor.getString(cursor.getColumnIndex("W_CTE-KATR8")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR8")) : "");
            solicitud.put("W_CTE-KDGRP", cursor.getString(cursor.getColumnIndex("W_CTE-KDGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KDGRP")) : "");
            solicitud.put("W_CTE-KKBER", cursor.getString(cursor.getColumnIndex("W_CTE-KKBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KKBER")) : "");
            solicitud.put("W_CTE-KLABC", cursor.getString(cursor.getColumnIndex("W_CTE-KLABC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KLABC")) : "");
            solicitud.put("W_CTE-KLIMK", cursor.getString(cursor.getColumnIndex("W_CTE-KLIMK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KLIMK")) : "");
            solicitud.put("W_CTE-KNKLI", cursor.getString(cursor.getColumnIndex("W_CTE-KNKLI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KNKLI")) : "");
            solicitud.put("W_CTE-KTGRD", cursor.getString(cursor.getColumnIndex("W_CTE-KTGRD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KTGRD")) : "");
            solicitud.put("W_CTE-KTOKD", cursor.getString(cursor.getColumnIndex("W_CTE-KTOKD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KTOKD")) : "");
            solicitud.put("W_CTE-KUKLA", cursor.getString(cursor.getColumnIndex("W_CTE-KUKLA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KUKLA")) : "");
            solicitud.put("W_CTE-KUNNR", cursor.getString(cursor.getColumnIndex("W_CTE-KUNNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KUNNR")) : "");
            solicitud.put("W_CTE-KVGR1", cursor.getString(cursor.getColumnIndex("W_CTE-KVGR1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR1")) : "");
            solicitud.put("W_CTE-KVGR2", cursor.getString(cursor.getColumnIndex("W_CTE-KVGR2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR2")) : "");
            solicitud.put("W_CTE-KVGR3", cursor.getString(cursor.getColumnIndex("W_CTE-KVGR3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR3")) : "");
            solicitud.put("W_CTE-KVGR5", cursor.getString(cursor.getColumnIndex("W_CTE-KVGR5")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR5")) : "");
            solicitud.put("W_CTE-LAND1", cursor.getString(cursor.getColumnIndex("W_CTE-LAND1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LAND1")) : "");
            solicitud.put("W_CTE-LIFNR", cursor.getString(cursor.getColumnIndex("W_CTE-LIFNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LIFNR")) : "");
            solicitud.put("W_CTE-LIMSUG", String.format("%,.2f", cursor.getDouble(cursor.getColumnIndex("W_CTE-LIMSUG"))));
            solicitud.put("W_CTE-LOCATION", cursor.getString(cursor.getColumnIndex("W_CTE-LOCATION")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LOCATION")) : "");
            solicitud.put("W_CTE-LPRIO", cursor.getString(cursor.getColumnIndex("W_CTE-LPRIO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LPRIO")) : "");
            solicitud.put("W_CTE-LZONE", cursor.getString(cursor.getColumnIndex("W_CTE-LZONE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LZONE")) : "");
            solicitud.put("W_CTE-NAME_CO", cursor.getString(cursor.getColumnIndex("W_CTE-NAME_CO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME_CO")) : "");
            solicitud.put("W_CTE-NAME1", cursor.getString(cursor.getColumnIndex("W_CTE-NAME1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME1")) : "");
            solicitud.put("W_CTE-NAME2", cursor.getString(cursor.getColumnIndex("W_CTE-NAME2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME2")) : "");
            solicitud.put("W_CTE-NAME3", cursor.getString(cursor.getColumnIndex("W_CTE-NAME3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME3")) : "");
            solicitud.put("W_CTE-NAME4", cursor.getString(cursor.getColumnIndex("W_CTE-NAME4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NAME4")) : "");
            solicitud.put("W_CTE-PERNR", cursor.getString(cursor.getColumnIndex("W_CTE-PERNR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PERNR")) : "");
            solicitud.put("W_CTE-PO_BOX", cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX")) : "");
            solicitud.put("W_CTE-PO_BOX_LOC", cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_LOC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_LOC")) : "");
            solicitud.put("W_CTE-PO_BOX_REG", cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_REG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PO_BOX_REG")) : "");
            solicitud.put("W_CTE-POST_CODE2", cursor.getString(cursor.getColumnIndex("W_CTE-POST_CODE2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-POST_CODE2")) : "");
            solicitud.put("W_CTE-PRFRE", cursor.getString(cursor.getColumnIndex("W_CTE-PRFRE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PRFRE")) : "");
            solicitud.put("W_CTE-PSON1", cursor.getString(cursor.getColumnIndex("W_CTE-PSON1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSON1")) : "");
            solicitud.put("W_CTE-PSON2", cursor.getString(cursor.getColumnIndex("W_CTE-PSON2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSON2")) : "");
            solicitud.put("W_CTE-PSON3", cursor.getString(cursor.getColumnIndex("W_CTE-PSON3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSON3")) : "");
            solicitud.put("W_CTE-PSTLZ", cursor.getString(cursor.getColumnIndex("W_CTE-PSTLZ")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PSTLZ")) : "");
            solicitud.put("W_CTE-PVKSM", cursor.getString(cursor.getColumnIndex("W_CTE-PVKSM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PVKSM")) : "");
            solicitud.put("W_CTE-REGION", cursor.getString(cursor.getColumnIndex("W_CTE-REGION")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-REGION")) : "");
            solicitud.put("W_CTE-ROOMNUMBER", cursor.getString(cursor.getColumnIndex("W_CTE-ROOMNUMBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ROOMNUMBER")) : "");
            solicitud.put("W_CTE-SMTP_ADDR", cursor.getString(cursor.getColumnIndex("W_CTE-SMTP_ADDR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SMTP_ADDR")) : "");
            solicitud.put("W_CTE-STCD1", cursor.getString(cursor.getColumnIndex("W_CTE-STCD1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STCD1")) : "");
            solicitud.put("W_CTE-STCD3", cursor.getString(cursor.getColumnIndex("W_CTE-STCD3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STCD3")) : "");
            solicitud.put("W_CTE-STR_SUPPL1", cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL1")) : "");
            solicitud.put("W_CTE-STR_SUPPL2", cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL2")) : "");
            solicitud.put("W_CTE-STR_SUPPL3", cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STR_SUPPL3")) : "");
            solicitud.put("W_CTE-STREET", cursor.getString(cursor.getColumnIndex("W_CTE-STREET")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STREET")) : "");
            solicitud.put("W_CTE-TEL_EXTENS", cursor.getString(cursor.getColumnIndex("W_CTE-TEL_EXTENS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TEL_EXTENS")) : "");
            solicitud.put("W_CTE-TEL_NUMBER", cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER")) : "");
            solicitud.put("W_CTE-TEL_NUMBER2", cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TEL_NUMBER2")) : "");
            solicitud.put("W_CTE-TELNUMBER2", cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER2")) : "");
            solicitud.put("W_CTE-TELNUMBER3", cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TELNUMBER3")) : "");
            solicitud.put("W_CTE-TOGRU", cursor.getString(cursor.getColumnIndex("W_CTE-TOGRU")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TOGRU")) : "");
            solicitud.put("W_CTE-UPDAT", cursor.getString(cursor.getColumnIndex("W_CTE-UPDAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-UPDAT")) : "");
            solicitud.put("W_CTE-VKBUR", cursor.getString(cursor.getColumnIndex("W_CTE-VKBUR")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VKBUR")) : "");
            solicitud.put("W_CTE-VKGRP", cursor.getString(cursor.getColumnIndex("W_CTE-VKGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VKGRP")) : "");
            solicitud.put("W_CTE-VKORG", cursor.getString(cursor.getColumnIndex("W_CTE-VKORG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VKORG")) : "");
            solicitud.put("W_CTE-VSBED", cursor.getString(cursor.getColumnIndex("W_CTE-VSBED")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VSBED")) : "");
            solicitud.put("W_CTE-VWERK", cursor.getString(cursor.getColumnIndex("W_CTE-VWERK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VWERK")) : "");
            solicitud.put("W_CTE-WAERS", cursor.getString(cursor.getColumnIndex("W_CTE-WAERS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-WAERS")) : "");
            solicitud.put("W_CTE-XZVER", cursor.getString(cursor.getColumnIndex("W_CTE-XZVER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-XZVER")) : "");
            solicitud.put("W_CTE-ZGPOCANAL", cursor.getString(cursor.getColumnIndex("W_CTE-ZGPOCANAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZGPOCANAL")) : "");
            solicitud.put("W_CTE-ZTERM", cursor.getString(cursor.getColumnIndex("W_CTE-ZTERM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZTERM")) : "");
            solicitud.put("W_CTE-ZTPOCANAL", cursor.getString(cursor.getColumnIndex("W_CTE-ZTPOCANAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZTPOCANAL")) : "");
            solicitud.put("W_CTE-ZSEGPRE", cursor.getString(cursor.getColumnIndex("W_CTE-ZSEGPRE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZSEGPRE")) : "");
            solicitud.put("W_CTE-ZWELS", cursor.getString(cursor.getColumnIndex("W_CTE-ZWELS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZWELS")) : "");
            solicitud.put("W_CTE-ZZAUART", cursor.getString(cursor.getColumnIndex("W_CTE-ZZAUART")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZAUART")) : "");
            solicitud.put("W_CTE-ZZBLOQU", cursor.getString(cursor.getColumnIndex("W_CTE-ZZBLOQU")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZBLOQU")) : "");
            solicitud.put("W_CTE-ZZCANAL", cursor.getString(cursor.getColumnIndex("W_CTE-ZZCANAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCANAL")) : "");
            solicitud.put("W_CTE-ZZCATFOCO", cursor.getString(cursor.getColumnIndex("W_CTE-ZZCATFOCO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCATFOCO")) : "");
            solicitud.put("W_CTE-ZZCRMA_LAT", cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LAT")) : "");
            solicitud.put("W_CTE-ZZCRMA_LONG", cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LONG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCRMA_LONG")) : "");
            solicitud.put("W_CTE-ZZENT1", cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT1")) : "");
            solicitud.put("W_CTE-ZZENT2", cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT2")) : "");
            solicitud.put("W_CTE-ZZENT3", cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT3")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT3")) : "");
            solicitud.put("W_CTE-ZZENT4", cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT4")) : "");
            solicitud.put("W_CTE-ZZENT5", cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT5")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZENT5")) : "");
            solicitud.put("W_CTE-ZZERDAT", cursor.getString(cursor.getColumnIndex("W_CTE-ZZERDAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZERDAT")) : "");
            solicitud.put("W_CTE-ZZGERENTE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZGERENTE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZGERENTE")) : "");
            solicitud.put("W_CTE-ZZINTCO", cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTCO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTCO")) : "");
            solicitud.put("W_CTE-ZZINTTACT", cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTTACT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZINTTACT")) : "");
            solicitud.put("W_CTE-ZZJEFATURA", cursor.getString(cursor.getColumnIndex("W_CTE-ZZJEFATURA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZJEFATURA")) : "");
            solicitud.put("W_CTE-ZZOCCONS", cursor.getString(cursor.getColumnIndex("W_CTE-ZZOCCONS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZOCCONS")) : "");
            solicitud.put("W_CTE-ZZREJA", cursor.getString(cursor.getColumnIndex("W_CTE-ZZREJA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZREJA")) : "");
            solicitud.put("W_CTE-ZZSEGCOM", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGCOM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGCOM")) : "");
            solicitud.put("W_CTE-ZZSEGDESC", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGDESC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGDESC")) : "");
            solicitud.put("W_CTE-ZZSEGEXH", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGEXH")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGEXH")) : "");
            solicitud.put("W_CTE-ZZSEGPDE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDE")) : "");
            solicitud.put("W_CTE-ZZSEGPDV", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDV")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPDV")) : "");
            solicitud.put("W_CTE-ZZSEGPORT", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPORT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPORT")) : "");
            solicitud.put("W_CTE-ZZSEGPRE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPRE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSEGPRE")) : "");
            solicitud.put("W_CTE-ZZSHARE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSHARE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSHARE")) : "");
            solicitud.put("W_CTE-ZZSTAT", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSTAT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSTAT")) : "");
            solicitud.put("W_CTE-ZZSUBUNNEG", cursor.getString(cursor.getColumnIndex("W_CTE-ZZSUBUNNEG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZSUBUNNEG")) : "");
            solicitud.put("W_CTE-ZZTIPSERV", cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPSERV")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPSERV")) : "");
            solicitud.put("W_CTE-ZZTFISI", cursor.getString(cursor.getColumnIndex("W_CTE-ZZTFISI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZTFISI")) : "");
            solicitud.put("W_CTE-ZZUNNEG", cursor.getString(cursor.getColumnIndex("W_CTE-ZZUNNEG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZUNNEG")) : "");
            solicitud.put("W_CTE-ZZZONACOST", cursor.getString(cursor.getColumnIndex("W_CTE-ZZZONACOST")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZZONACOST")) : "");
            solicitud.put("W_CTE-COMENTARIOS", cursor.getString(cursor.getColumnIndex("W_CTE-COMENTARIOS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-COMENTARIOS")) : "");
            solicitud.put("fuera_politica_plazo", cursor.getString(cursor.getColumnIndex("fuera_politica_plazo")) != null ? cursor.getString(cursor.getColumnIndex("fuera_politica_plazo")) : "");
            solicitud.put("fuera_politica_monto", cursor.getString(cursor.getColumnIndex("fuera_politica_plazo")) != null ? cursor.getString(cursor.getColumnIndex("fuera_politica_plazo")) : "");
            solicitud.put("W_CTE-NOTIFICANTES", cursor.getString(cursor.getColumnIndex("W_CTE-NOTIFICANTES")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NOTIFICANTES")) : "");
            solicitud.put("W_CTE-ZZKEYACC", cursor.getString(cursor.getColumnIndex("W_CTE-ZZKEYACC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZKEYACC")) : "");
            solicitud.put("W_CTE-ZIBASE", cursor.getString(cursor.getColumnIndex("W_CTE-ZIBASE")) != null ? removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("W_CTE-ZIBASE"))) : "");
            solicitud.put("W_CTE-ZADICI", cursor.getString(cursor.getColumnIndex("W_CTE-ZADICI")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZADICI")) : "");
            solicitud.put("W_CTE-ZZUDATE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZUDATE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZUDATE")) : "");
            solicitud.put("W_CTE-AUFSD", cursor.getString(cursor.getColumnIndex("W_CTE-AUFSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-AUFSD")) : "");
            solicitud.put("W_CTE-LIFSD", cursor.getString(cursor.getColumnIndex("W_CTE-LIFSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LIFSD")) : "");
            solicitud.put("W_CTE-FAKSD", cursor.getString(cursor.getColumnIndex("W_CTE-FAKSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FAKSD")) : "");
            solicitud.put("W_CTE-CASSD", cursor.getString(cursor.getColumnIndex("W_CTE-CASSD")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CASSD")) : "");
            solicitud.put("W_CTE-LOEVM", cursor.getString(cursor.getColumnIndex("W_CTE-LOEVM")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LOEVM")) : "");
            solicitud.put("W_CTE-TELF2", cursor.getString(cursor.getColumnIndex("W_CTE-TELF2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TELF2")) : "");
            solicitud.put("W_CTE-VTWEG", cursor.getString(cursor.getColumnIndex("W_CTE-VTWEG")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VTWEG")) : "");
            solicitud.put("W_CTE-SPART", cursor.getString(cursor.getColumnIndex("W_CTE-SPART")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SPART")) : "");
            solicitud.put("W_CTE-PERFK", cursor.getString(cursor.getColumnIndex("W_CTE-PERFK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PERFK")) : "");
            solicitud.put("W_CTE-KVGR4", cursor.getString(cursor.getColumnIndex("W_CTE-KVGR4")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KVGR4")) : "");
            solicitud.put("W_CTE-KONDA", cursor.getString(cursor.getColumnIndex("W_CTE-KONDA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KONDA")) : "");
            solicitud.put("W_CTE-RUTAHH", cursor.getString(cursor.getColumnIndex("W_CTE-RUTAHH")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-RUTAHH")) : "");
            solicitud.put("W_CTE-ZZESQUINA", cursor.getString(cursor.getColumnIndex("W_CTE-ZZESQUINA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZESQUINA")) : "");
            solicitud.put("W_CTE-ZZESTAC", cursor.getString(cursor.getColumnIndex("W_CTE-ZZESTAC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZESTAC")) : "");
            solicitud.put("W_CTE-KATR2", cursor.getString(cursor.getColumnIndex("W_CTE-KATR2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KATR2")) : "");
            solicitud.put("W_CTE-ZZTIPONEC", cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPONEC")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZTIPONEC")) : "");
            solicitud.put("W_CTE-VBUND", cursor.getString(cursor.getColumnIndex("W_CTE-VBUND")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VBUND")) : "");
            try {
                solicitud.put("W_CTE-ORT01", cursor.getString(cursor.getColumnIndex("W_CTE-ORT01")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ORT01")) : "");
                solicitud.put("W_CTE-STCDT", cursor.getString(cursor.getColumnIndex("W_CTE-STCDT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STCDT")) : "");
                solicitud.put("W_CTE-ZONA_FRANCA", cursor.getString(cursor.getColumnIndex("W_CTE-ZONA_FRANCA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZONA_FRANCA")) : "");
                solicitud.put("W_CTE-FITYP", cursor.getString(cursor.getColumnIndex("W_CTE-FITYP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-FITYP")) : "");
                solicitud.put("W_CTE-GUZTE", cursor.getString(cursor.getColumnIndex("W_CTE-GUZTE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-GUZTE")) : "");
            } catch (Exception e) {
            }
            //CAMPOS PARA AVISOS DE EQUIPO FRIO

            solicitud.put("W_CTE-IM_EQUIPMENT", cursor.getString(cursor.getColumnIndex("W_CTE-IM_EQUIPMENT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_EQUIPMENT")) : "");
            solicitud.put("W_CTE-IM_PARTNER", cursor.getString(cursor.getColumnIndex("W_CTE-IM_PARTNER")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_PARTNER")) : "");
            solicitud.put("W_CTE-IM_NOTIF_TYPE", cursor.getString(cursor.getColumnIndex("W_CTE-IM_NOTIF_TYPE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_NOTIF_TYPE")) : "");
            solicitud.put("W_CTE-IM_DESCRIPT", cursor.getString(cursor.getColumnIndex("W_CTE-IM_DESCRIPT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_DESCRIPT")) : "");
            solicitud.put("W_CTE-IM_MATERIAL", cursor.getString(cursor.getColumnIndex("W_CTE-IM_MATERIAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_MATERIAL")) : "");
            solicitud.put("W_CTE-IM_SERIALNO", cursor.getString(cursor.getColumnIndex("W_CTE-IM_SERIALNO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_SERIALNO")) : "");
            solicitud.put("W_CTE-IM_CAUSE_CODE", cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODE")) : "");
            solicitud.put("W_CTE-IM_CAUSE_CODEGRP", cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODEGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_CAUSE_CODEGRP")) : "");
            solicitud.put("W_CTE-IM_D_CODE", cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODE")) : "");
            solicitud.put("W_CTE-IM_D_CODEGRP", cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODEGRP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_D_CODEGRP")) : "");
            solicitud.put("W_CTE-IM_PRIORITY", cursor.getString(cursor.getColumnIndex("W_CTE-IM_PRIORITY")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_PRIORITY")) : "");
            solicitud.put("W_CTE-IM_SHORT_TEXT", cursor.getString(cursor.getColumnIndex("W_CTE-IM_SHORT_TEXT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_SHORT_TEXT")) : "");
            solicitud.put("W_CTE-IM_TEXT_LINE", cursor.getString(cursor.getColumnIndex("W_CTE-IM_TEXT_LINE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_TEXT_LINE")) : "");
            solicitud.put("W_CTE-IM_NUM_AVISO", cursor.getString(cursor.getColumnIndex("W_CTE-IM_NUM_AVISO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IM_NUM_AVISO")) : "");

            //CAMPOS NEUVOS DE NI Y PA PARA REALIZAR CONTRATOS O POLITICAS CON ESTA INFORMACION DENTRO DEL TEXTO FIRMABLE
            solicitud.put("W_CTE-ESTADO_CIVIL", cursor.getString(cursor.getColumnIndex("W_CTE-ESTADO_CIVIL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ESTADO_CIVIL")) : "");
            solicitud.put("W_CTE-ACTIVIDAD_ECONOMICA", cursor.getString(cursor.getColumnIndex("W_CTE-ACTIVIDAD_ECONOMICA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ACTIVIDAD_ECONOMICA")) : "");
            solicitud.put("W_CTE-DURACION_CONTRATO", cursor.getString(cursor.getColumnIndex("W_CTE-DURACION_CONTRATO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DURACION_CONTRATO")) : "");
            solicitud.put("W_CTE-TIPO_CREDITO", cursor.getString(cursor.getColumnIndex("W_CTE-TIPO_CREDITO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-TIPO_CREDITO")) : "");

            try {
                //CAMPOS NUEVOS PARA FORMULARIO ENTREGA DE TARJETAS
                solicitud.put("W_CTE-IN_NUM_TARJETA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_NUM_TARJETA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_NUM_TARJETA")) : "");
                solicitud.put("W_CTE-IN_NOMBRE_RECIBE", cursor.getString(cursor.getColumnIndex("W_CTE-IN_NOMBRE_RECIBE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_NOMBRE_RECIBE")) : "");
                solicitud.put("W_CTE-IN_MONTO_TARJETA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_MONTO_TARJETA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_MONTO_TARJETA")) : "");
                solicitud.put("W_CTE-IN_CEDULA_RECIBE", cursor.getString(cursor.getColumnIndex("W_CTE-IN_CEDULA_RECIBE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_CEDULA_RECIBE")) : "");
                solicitud.put("W_CTE-IN_CONCEPTO_ENTREGA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA")) : "");
                solicitud.put("W_CTE-IN_CONCEPTO_ENTREGA2", cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_CONCEPTO_ENTREGA2")) : "");
                solicitud.put("W_CTE-IN_PERIODO_VALIDEZ", cursor.getString(cursor.getColumnIndex("W_CTE-IN_PERIODO_VALIDEZ")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_PERIODO_VALIDEZ")) : "");
                solicitud.put("W_CTE-IN_PLAN_INICIATIVA", cursor.getString(cursor.getColumnIndex("W_CTE-IN_PLAN_INICIATIVA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-IN_PLAN_INICIATIVA")) : "");
            } catch (Exception e) {
            }

            try {
                //CAMPOS NUEVOS PARA MODULO CENSO QUIPO FRIO
                solicitud.put("W_CTE-CE_MOTIVO_ALERTA", cursor.getString(cursor.getColumnIndex("W_CTE-CE_MOTIVO_ALERTA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_MOTIVO_ALERTA")) : "");
                solicitud.put("W_CTE-CE_LOCAL_ABIERTO", cursor.getString(cursor.getColumnIndex("W_CTE-CE_LOCAL_ABIERTO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_LOCAL_ABIERTO")) : "");
                solicitud.put("W_CTE-CE_CONTACTO", cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO")) : "");
                solicitud.put("W_CTE-CE_CONTACTO_ADICIONAL", cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO_ADICIONAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CE_CONTACTO_ADICIONAL")) : "");
            } catch (Exception e) {
            }
            try {
                //CAMPOS NUEVOS PARA MODULO CENSO QUIPO FRIO
                solicitud.put("W_CTE-VENTA_COMPROMETIDA", cursor.getString(cursor.getColumnIndex("W_CTE-VENTA_COMPROMETIDA")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-VENTA_COMPROMETIDA")) : "");
                solicitud.put("W_CTE-NUM_PUERTAS_MODELO", cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_MODELO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_MODELO")) : "");
                solicitud.put("W_CTE-NUM_PUERTAS_ACTUAL", cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_ACTUAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-NUM_PUERTAS_ACTUAL")) : "");
                solicitud.put("W_CTE-PUERTAS_SUGERIDAS", cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_SUGERIDAS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_SUGERIDAS")) : "");
                solicitud.put("W_CTE-PUERTAS_INSTALADAS", cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_INSTALADAS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_INSTALADAS")) : "");
                solicitud.put("W_CTE-PUERTAS_OBJETIVO", cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_OBJETIVO")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-PUERTAS_OBJETIVO")) : "");
            }catch(Exception e){}

            try {
                //CAMPOS NUEVOS PARA CREDITO SAP4HANA
                solicitud.put("W_CTE-CREDIT_SGMNT", cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_SGMNT")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_SGMNT")) : "");
                solicitud.put("W_CTE-CREDIT_GROUP", cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_GROUP")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CREDIT_GROUP")) : "");
                solicitud.put("W_CTE-CHECK_RULE", cursor.getString(cursor.getColumnIndex("W_CTE-CHECK_RULE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-CHECK_RULE")) : "");
                solicitud.put("W_CTE-LIMIT_RULE", cursor.getString(cursor.getColumnIndex("W_CTE-LIMIT_RULE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-LIMIT_RULE")) : "");
                solicitud.put("W_CTE-XBLOCKED", cursor.getString(cursor.getColumnIndex("W_CTE-XBLOCKED")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-XBLOCKED")) : "");
            }catch(Exception e){}

            try {
                //CAMPOS NUEVOS PARA COLOMBIA
                solicitud.put("W_CTE-BEGRU", cursor.getString(cursor.getColumnIndex("W_CTE-BEGRU")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-BEGRU")) : "");
                solicitud.put("W_CTE-STRAS", cursor.getString(cursor.getColumnIndex("W_CTE-STRAS")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STRAS")) : "");
                solicitud.put("W_CTE-SORT1", cursor.getString(cursor.getColumnIndex("W_CTE-SORT1")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SORT1")) : "");
                solicitud.put("W_CTE-SORT2", cursor.getString(cursor.getColumnIndex("W_CTE-SORT2")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-SORT2")) : "");
                solicitud.put("W_CTE-STKZN", cursor.getString(cursor.getColumnIndex("W_CTE-STKZN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-STKZN")) : "");
            }catch(Exception e){}

            try {//Para Actividad Economica CR
                solicitud.put("W_CTE-ZZCNAE", cursor.getString(cursor.getColumnIndex("W_CTE-ZZCNAE")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZZCNAE")) : "");
                solicitud.put("W_CTE-ZREGFISCAL", cursor.getString(cursor.getColumnIndex("W_CTE-ZREGFISCAL")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-ZREGFISCAL")) : "");
            }catch(Exception e){}

            try {//Campo especifico para colombia para saber si viene de un cliente anterior por cambio de razon social
                solicitud.put("CAMBIO_RAZON", cursor.getString(cursor.getColumnIndex("CAMBIO_RAZON")) != null ? cursor.getString(cursor.getColumnIndex("CAMBIO_RAZON")) : "");
            }catch(Exception e){}

            try {//Campos nuevos para manejo de formulario de consignacion
                solicitud.put("W_CTE-DATST", cursor.getString(cursor.getColumnIndex("W_CTE-DATST")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATST")) : "");
                solicitud.put("W_CTE-DATEN", cursor.getString(cursor.getColumnIndex("W_CTE-DATEN")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-DATEN")) : "");
            }catch(Exception e){}

            formList.add(solicitud);
        }
        cursor.close();
        return  formList;
    }

    public ArrayList<HashMap<String, String>> getSolicitudes(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT s.idform , s.id_solicitud, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, f.Descripcion, " +
                "CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo, s.feccre, s.fecfin, s.id_preformulario " +
                " FROM FormHVKOF_solicitud s  " +
                " INNER JOIN flujo f ON (f.id_form = s.tipform ) "+
                " LEFT JOIN FormHVKOF_old_solicitud fo ON ( trim(fo.id_solicitud) = trim(s.id_solicitud) ) ";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("idform",String.valueOf(cursor.getInt(0)) );
            user.put("id_solicitud",cursor.getString(1));
            user.put("codigo",String.valueOf(cursor.getLong(2)) );
            user.put("nombre",cursor.getString(3));
            user.put("estado",cursor.getString(4));
            user.put("tipform",cursor.getString(5));
            user.put("tipo_solicitud",cursor.getString(6));
            user.put("id_fiscal",cursor.getString(7));
            user.put("ind_credito",cursor.getString(8));
            user.put("ind_modelo",cursor.getString(9));
            user.put("feccre",cursor.getString(10));
            user.put("fecfin",cursor.getString(11));
            user.put("id_preformulario",cursor.getString(12));
            formList.add(user);
        }
        cursor.close();
        return  formList;
    }

    public ArrayList<HashMap<String, String>> getSolicitudes(String... estados){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String params = "(";
        String coma = "";
        for (int x=0;x < estados.length; x++){
            params += coma+"?";
            coma = ",";
        }
        params += ")";
        String query = "SELECT s.idform, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, s.id_solicitud, f.Descripcion, CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo, s.feccre, s.fecfin, s.id_preformulario " +
                " FROM FormHVKOF_solicitud s" +
                " INNER JOIN flujo f ON (f.id_form = s.tipform ) " +
                " LEFT JOIN FormHVKOF_old_solicitud fo ON ( trim(fo.id_solicitud) = trim(s.id_solicitud) ) "+
                " WHERE trim(s.estado) IN "+params;
        Cursor cursor = mDataBase.rawQuery(query, estados);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("idform",cursor.getString(0));
            user.put("codigo",String.valueOf(cursor.getLong(1)) );
            user.put("nombre",cursor.getString(2));
            user.put("estado",cursor.getString(3));
            user.put("tipform",cursor.getString(4));
            user.put("id_solicitud",cursor.getString(5));
            user.put("tipo_solicitud",cursor.getString(6));
            user.put("id_fiscal",cursor.getString(7));
            user.put("ind_credito",cursor.getString(8));
            user.put("ind_modelo",cursor.getString(9));
            user.put("feccre",cursor.getString(10));
            user.put("fecfin",cursor.getString(11));
            user.put("id_preformulario",cursor.getString(12));
            formList.add(user);
        }
        cursor.close();
        return  formList;
    }

    public ArrayList<HashMap<String, String>> getSolicitudes(String estado, String tipform){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String where = " WHERE 1=1 ";
        ArrayList<String> parametros = new ArrayList<>();
        if(estado != null) {
            where += " AND trim(s.estado) = ? ";
            parametros.add(estado);
        }
        if(tipform != null) {
            where += " AND trim(s.tipform) = ? ";
            parametros.add(tipform);
        }

        String query = "SELECT s.idform, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, s.id_solicitud, f.Descripcion, CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo, s.feccre, s.fecfin, '' as id_preformulario " +
                " FROM FormHVKOF_solicitud s" +
                " INNER JOIN flujo f ON (f.id_form = s.tipform ) " +
                " LEFT JOIN FormHVKOF_old_solicitud fo ON ( trim(fo.id_solicitud) = trim(s.id_solicitud) ) "+
                where;
        String[] p = parametros.size() == 0? null: parametros.toArray(new String[0]);
        Cursor cursor = mDataBase.rawQuery(query,  p);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("idform",cursor.getString(0));
            user.put("codigo",String.valueOf(cursor.getLong(1)) );
            user.put("nombre",cursor.getString(2));
            user.put("estado",cursor.getString(3));
            user.put("tipform",cursor.getString(4));
            user.put("id_solicitud",cursor.getString(5));
            user.put("tipo_solicitud",cursor.getString(6));
            user.put("id_fiscal",cursor.getString(7));
            user.put("ind_credito",cursor.getString(8));
            user.put("ind_modelo",cursor.getString(9));
            user.put("feccre",cursor.getString(10));
            user.put("fecfin",cursor.getString(11));
            user.put("id_preformulario",cursor.getString(12));
            formList.add(user);
        }
        cursor.close();
        return  formList;
    }
    public ArrayList<HashMap<String, String>> getSolicitudes(ArrayList<String> estados, ArrayList<String> tipforms){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String where = " WHERE 1=1 ";
        ArrayList<String> parametros = new ArrayList<>();

        String params1 = "";
        String coma = "";
        for (int x=0;x < estados.size(); x++){
            params1 += coma+estados.get(x).trim();
            coma = ",";
        }
        if(estados != null && !params1.equals("")) {
            where += " AND trim(s.estado) IN (?) ";
            parametros.add(params1);
        }
        String params2 = "";
        coma = "";
        for (int x=0;x < tipforms.size(); x++){
            params2 += coma+tipforms.get(x).trim();
            coma = ",";
        }
        if(tipforms != null) {
            where += " AND trim(s.tipform) IN (?) ";
            parametros.add(params2);
        }

        String query = "SELECT s.idform, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, s.id_solicitud, f.Descripcion, CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo, s.feccre, s.fecfin, s.id_preformulario " +
                " FROM FormHVKOF_solicitud s" +
                " INNER JOIN flujo f ON (f.id_form = s.tipform ) " +
                " LEFT JOIN FormHVKOF_old_solicitud fo ON ( trim(fo.id_solicitud) = trim(s.id_solicitud) ) "+
                where;
        String[] p = parametros.size() == 0? null: parametros.toArray(new String[0]);
        Cursor cursor = mDataBase.rawQuery(query,  p);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("idform",cursor.getString(0));
            user.put("codigo",String.valueOf(cursor.getLong(1)) );
            user.put("nombre",cursor.getString(2));
            user.put("estado",cursor.getString(3));
            user.put("tipform",cursor.getString(4));
            user.put("id_solicitud",cursor.getString(5));
            user.put("tipo_solicitud",cursor.getString(6));
            user.put("id_fiscal",cursor.getString(7));
            user.put("ind_credito",cursor.getString(8));
            user.put("ind_modelo",cursor.getString(9));
            user.put("feccre",cursor.getString(10));
            user.put("fecfin",cursor.getString(11));
            user.put("id_preformulario",cursor.getString(12));
            formList.add(user);
        }
        cursor.close();
        return  formList;
    }

    public ArrayList<HashMap<String, String>> getTipoSolicitudPanel(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "select s.tipform as tipform, f.Descripcion, count(*) as total," +
                "count(case rtrim(estado) when 'Nuevo' then 1 else null end) as nuevos," +
                "count(case rtrim(estado) when 'Incidencia' then 1 else null end) as incidencias," +
                "count(case rtrim(estado) when 'Pendiente' then 1 else null end) as pendientes," +
                "count(case rtrim(estado) when 'Aprobado' then 1 else null end) as aprobados," +
                "count(case rtrim(estado) when 'Rechazado' then 1 else null end) as rechazados," +
                "count(case rtrim(estado) when 'Modificado' then 1 else null end) as modificados," +
                "count(case rtrim(estado) when 'Preventa' then 1 else null end) as preventa," +
                "count(case rtrim(estado) when 'Incompleto' then 1 else null end) as incompletos" +
                " from FormHvKof_solicitud s" +
                " inner join flujo f ON(f.id_form = s.tipform) " +
                " group by s.tipform , f.Descripcion";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> tipform = new HashMap<>();
            tipform.put("tipform",cursor.getString(cursor.getColumnIndex("tipform")) != null ? cursor.getString(cursor.getColumnIndex("tipform")) : "" );
            tipform.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")) != null ? cursor.getString(cursor.getColumnIndex("Descripcion")) : "" );
            tipform.put("total",cursor.getString(cursor.getColumnIndex("total")) != null ? cursor.getString(cursor.getColumnIndex("total")) : "" );
            tipform.put("nuevos",cursor.getString(cursor.getColumnIndex("nuevos")) != null ? cursor.getString(cursor.getColumnIndex("nuevos")) : "" );
            tipform.put("incidencias",cursor.getString(cursor.getColumnIndex("incidencias")) != null ? cursor.getString(cursor.getColumnIndex("incidencias")) : "" );
            tipform.put("pendientes",cursor.getString(cursor.getColumnIndex("pendientes")) != null ? cursor.getString(cursor.getColumnIndex("pendientes")) : "" );
            tipform.put("aprobados",cursor.getString(cursor.getColumnIndex("aprobados")) != null ? cursor.getString(cursor.getColumnIndex("aprobados")) : "" );
            tipform.put("rechazados",cursor.getString(cursor.getColumnIndex("rechazados")) != null ? cursor.getString(cursor.getColumnIndex("rechazados")) : "" );
            tipform.put("modificados",cursor.getString(cursor.getColumnIndex("modificados")) != null ? cursor.getString(cursor.getColumnIndex("modificados")) : "" );
            tipform.put("incompletos",cursor.getString(cursor.getColumnIndex("incompletos")) != null ? cursor.getString(cursor.getColumnIndex("incompletos")) : "" );
            tipform.put("preventa",cursor.getString(cursor.getColumnIndex("preventa")) != null ? cursor.getString(cursor.getColumnIndex("preventa")) : "" );
            formList.add(tipform);
        }
        cursor.close();
        return  formList;
    }


    public int getNextSolicitudId(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT \"ROWID\" from FormHvkof_solicitud order by \"ROWID\" DESC limit 1";
        Cursor cursor = mDataBase.rawQuery(query,null);
        int id = 1;
        if (cursor.moveToNext()){
            id = cursor.getInt(0)+1;
        }
        cursor.close();
        return id;
    }

    public String getGuiId(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT \"ROWID\" from FormHvkof_solicitud order by \"ROWID\" DESC limit 1";
        Cursor cursor = mDataBase.rawQuery(query,null);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateandTime = sdf.format(new Date());
        String id = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH","")+currentDateandTime;

        if (cursor.moveToNext()){
            id += String.format("%4s", String.valueOf(cursor.getInt(0)+1)).replace(' ', '0');
        }else{
            id += String.format("%4s", String.valueOf(1)).replace(' ', '0');
        }
        cursor.close();
        return id;
    }

    public ArrayList<HashMap<String, String>> getMetaData(String tabla){
        ArrayList<HashMap<String, String>> columnList = new ArrayList<>();
        String query = "SELECT m.COLUMN_NAME, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION FROM TABLES_META_DATA m WHERE m.TABLE_NAME = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{tabla});

        while (cursor.moveToNext()){
            HashMap<String,String> column = new HashMap<>();
            //metadatas
            column.put("column_name",cursor.getString(cursor.getColumnIndex("COLUMN_NAME")) != null ? cursor.getString(cursor.getColumnIndex("COLUMN_NAME")) : "" );
            column.put("datatype",cursor.getString(cursor.getColumnIndex("DATA_TYPE")) != null ? cursor.getString(cursor.getColumnIndex("DATA_TYPE")) : "" );
            column.put("numeric_precision",cursor.getString(cursor.getColumnIndex("NUMERIC_PRECISION")) != null ? cursor.getString(cursor.getColumnIndex("NUMERIC_PRECISION")) : "" );
            column.put("maxlength",cursor.getString(cursor.getColumnIndex("CHARACTER_MAXIMUM_LENGTH")) != null ? cursor.getString(cursor.getColumnIndex("CHARACTER_MAXIMUM_LENGTH")) : "" );
            columnList.add(column);
        }
        cursor.close();
        return  columnList;
    }

    public ArrayList<HashMap<String, String>> getCamposPestana(String id_formulario, String pestana ){
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String BUKRS = PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad());
        String KTOKD = PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_GRUPOCUENTAS","");
        /*String query = "SELECT c.campo, c.nombre, c.tipo_input, c.id_seccion, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION FROM configuracion c" +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"' and cc.ktokd = 'RCMA')" +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion)" +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip)" +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo))" +
                " WHERE id_formulario = "+id_formulario+" AND c.panta = '"+pestana+"'" +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO')"+
                " ORDER BY c.panta, s.orden_hh, c.orden_hh";*/
        String fechas = "";
        /*switch(mContext.getClass().getSimpleName()){
            case "SolicitudActivity":
        }*/
        String query = "SELECT * FROM (" +
                "SELECT DISTINCT c.bukrs, c.panta, s.orden_hh as orden_seccion, c.orden_hh, c.campo, c.nombre, c.tipo_input, c.id_seccion_hh, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION, c.sufijo, c.comentario_auto " +
                "FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"') " +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion_hh) " +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                " WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' AND trim(c.bukrs) = '"+BUKRS+"' " +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                "UNION " +
                "SELECT DISTINCT c.bukrs, c.panta, s.orden_hh as orden_seccion, c.orden_hh, c.campo, c.nombre, c.tipo_input, c.id_seccion_hh, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION, c.sufijo " +
                "FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(cc.panta) = ( " +
                " Select trim(cc2.panta) FROM configCampos cc2 WHERE trim(c.campo) = trim(cc2.CAMPO) AND trim(cc2.bukrs) = '"+BUKRS+"' and trim(cc2.ktokd) = '"+KTOKD+"' AND trim(cc2.panta) != trim(c.panta) LIMIT 1 " +
                " ) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"' and trim(c.campo) NOT IN ( " +
                " SELECT DISTINCT trim(c.campo) " +
                " FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"')\n" +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion_hh) " +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                " WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' AND trim(c.bukrs) = '"+BUKRS+"' " +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                " )) " +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion_hh) " +
                "                LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                "                LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                "                WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' " +
                "                AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                ") T " +
                " ORDER BY T.panta, T.orden_seccion, T.orden_hh";
        Cursor cursor = mDataBase.rawQuery(query,null);

        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("campo",cursor.getString(cursor.getColumnIndex("campo")) != null ? cursor.getString(cursor.getColumnIndex("campo")): "");
            user.put("nombre",cursor.getString(cursor.getColumnIndex("nombre")) != null ? cursor.getString(cursor.getColumnIndex("nombre")): "");
            user.put("tipo_input",cursor.getString(cursor.getColumnIndex("tipo_input")) != null ? cursor.getString(cursor.getColumnIndex("tipo_input")): "");
            user.put("id_seccion",cursor.getString(cursor.getColumnIndex("id_seccion_hh")) != null ? cursor.getString(cursor.getColumnIndex("id_seccion_hh")): "");
            user.put("seccion",cursor.getString(cursor.getColumnIndex("seccion")) != null ? cursor.getString(cursor.getColumnIndex("seccion")): "");
            user.put("descr",cursor.getString(cursor.getColumnIndex("descr")) != null ? cursor.getString(cursor.getColumnIndex("descr")): "");
            user.put("tabla",cursor.getString(cursor.getColumnIndex("tabla")) != null ? cursor.getString(cursor.getColumnIndex("tabla")): "");
            user.put("dfaul",cursor.getString(cursor.getColumnIndex("dfaul")) != null ? cursor.getString(cursor.getColumnIndex("dfaul")): "");
            user.put("sup",cursor.getString(cursor.getColumnIndex("sup")) != null ? cursor.getString(cursor.getColumnIndex("sup")): "");
            user.put("obl",cursor.getString(cursor.getColumnIndex("obl")) != null ? cursor.getString(cursor.getColumnIndex("obl")): "");
            user.put("vis",cursor.getString(cursor.getColumnIndex("vis")) != null ? cursor.getString(cursor.getColumnIndex("vis")): "");
            user.put("opc",cursor.getString(cursor.getColumnIndex("opc")) != null ? cursor.getString(cursor.getColumnIndex("opc")): "");
            user.put("tabla_local",cursor.getString(cursor.getColumnIndex("tabla_local")) != null ? cursor.getString(cursor.getColumnIndex("tabla_local")): "");
            user.put("evento1",cursor.getString(cursor.getColumnIndex("evento1")) != null ? cursor.getString(cursor.getColumnIndex("evento1")): "");
            user.put("llamado1",cursor.getString(cursor.getColumnIndex("llamado1")) != null ? cursor.getString(cursor.getColumnIndex("llamado1")): "");
            user.put("tooltip",cursor.getString(cursor.getColumnIndex("tooltip")) != null ? cursor.getString(cursor.getColumnIndex("tooltip")): "");
            user.put("modificacion",cursor.getString(cursor.getColumnIndex("modificacion")) != null ? cursor.getString(cursor.getColumnIndex("modificacion")): "");
            user.put("sufijo",cursor.getString(cursor.getColumnIndex("sufijo")) != null ? cursor.getString(cursor.getColumnIndex("sufijo")): "");
            user.put("comentario_auto",cursor.getString(cursor.getColumnIndex("comentario_auto")) != null ? cursor.getString(cursor.getColumnIndex("comentario_auto")): "");
            //metadatas
            user.put("datatype",cursor.getString(cursor.getColumnIndex("DATA_TYPE")) != null ? cursor.getString(cursor.getColumnIndex("DATA_TYPE")): "");
            user.put("numeric_precision",cursor.getString(cursor.getColumnIndex("NUMERIC_PRECISION")) != null ? cursor.getString(cursor.getColumnIndex("NUMERIC_PRECISION")): "");
            user.put("maxlength",cursor.getString(cursor.getColumnIndex("CHARACTER_MAXIMUM_LENGTH")) != null ? cursor.getString(cursor.getColumnIndex("CHARACTER_MAXIMUM_LENGTH")): "");
            clientList.add(user);
        }
        cursor.close();
        return  clientList;
    }
    public ArrayList<HashMap<String, String>> getCamposPestana(String id_formulario, String pestana, String idSolicitud ){
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String BUKRS = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS",VariablesGlobales.getSociedad());
        String KTOKD = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD",VariablesGlobales.getKtokd());
        /*String query = "SELECT c.campo, c.nombre, c.tipo_input, c.id_seccion, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION FROM configuracion c" +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"' and cc.ktokd = 'RCMA')" +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion)" +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip)" +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo))" +
                " WHERE id_formulario = "+id_formulario+" AND c.panta = '"+pestana+"'" +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO')"+
                " ORDER BY c.panta, s.orden_hh, c.orden_hh";*/
        String whereVigencia = "";
        if(idSolicitud != null){
            ArrayList<HashMap<String, String>> formList = new ArrayList<>();
            String query = "SELECT FECCRE from FormHvkof_solicitud where id_solicitud = ?";
            Cursor cursor = mDataBase.rawQuery(query,new String[]{idSolicitud});
            if (cursor.moveToNext()){
                whereVigencia = " AND c.fecini <= '"+cursor.getString(0)+"' AND c.fecfin >= '"+cursor.getString(0)+"'";
            }
            cursor.close();
        }else{
            whereVigencia = " AND c.fecini <= datetime('now') AND c.fecfin >= datetime('now')";
        }
        String query = "SELECT * FROM (" +
                "SELECT DISTINCT c.bukrs, c.panta, s.orden_hh as orden_seccion, c.orden_hh, c.campo, c.nombre, c.tipo_input, c.id_seccion_hh, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION, c.sufijo, c.comentario_auto " +
                "FROM configuracion c " +
                " LEFT OUTER JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"') " +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion_hh) " +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                " WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' AND trim(c.bukrs) = '"+BUKRS+"' " + whereVigencia +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                "UNION " +
                "SELECT DISTINCT c.bukrs, c.panta, s.orden_hh as orden_seccion, c.orden_hh, c.campo, c.nombre, c.tipo_input, c.id_seccion_hh, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION, c.sufijo, c.comentario_auto " +
                "FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(cc.panta) = ( " +
                " Select trim(cc2.panta) FROM configCampos cc2 WHERE trim(c.campo) = trim(cc2.CAMPO) AND trim(cc2.bukrs) = '"+BUKRS+"' and trim(cc2.ktokd) = '"+KTOKD+"' AND trim(cc2.panta) != trim(c.panta) LIMIT 1 " +
                " ) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"' and trim(c.campo) NOT IN ( " +
                " SELECT DISTINCT trim(c.campo) " +
                " FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"')\n" +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion_hh) " +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                " WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' AND trim(c.bukrs) = '"+BUKRS+"' " + whereVigencia +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                " )) " +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion_hh) " +
                "                LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                "                LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                "                WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' " +
                "                AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') "+ whereVigencia +
                ") T " +
                " ORDER BY T.panta, T.orden_seccion, T.orden_hh";
        Cursor cursor = mDataBase.rawQuery(query,null);

        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("campo",cursor.getString(cursor.getColumnIndex("campo")) != null ? cursor.getString(cursor.getColumnIndex("campo")): "");
            user.put("nombre",cursor.getString(cursor.getColumnIndex("nombre")) != null ? cursor.getString(cursor.getColumnIndex("nombre")): "");
            user.put("tipo_input",cursor.getString(cursor.getColumnIndex("tipo_input")) != null ? cursor.getString(cursor.getColumnIndex("tipo_input")): "");
            user.put("id_seccion",cursor.getString(cursor.getColumnIndex("id_seccion_hh")) != null ? cursor.getString(cursor.getColumnIndex("id_seccion_hh")): "");
            user.put("seccion",cursor.getString(cursor.getColumnIndex("seccion")) != null ? cursor.getString(cursor.getColumnIndex("seccion")): "");
            user.put("descr",cursor.getString(cursor.getColumnIndex("descr")) != null ? cursor.getString(cursor.getColumnIndex("descr")): "");
            user.put("tabla",cursor.getString(cursor.getColumnIndex("tabla")) != null ? cursor.getString(cursor.getColumnIndex("tabla")): "");
            user.put("dfaul",cursor.getString(cursor.getColumnIndex("dfaul")) != null ? cursor.getString(cursor.getColumnIndex("dfaul")): "");
            user.put("sup",cursor.getString(cursor.getColumnIndex("sup")) != null ? cursor.getString(cursor.getColumnIndex("sup")): "");
            user.put("obl",cursor.getString(cursor.getColumnIndex("obl")) != null ? cursor.getString(cursor.getColumnIndex("obl")): "");
            user.put("vis",cursor.getString(cursor.getColumnIndex("vis")) != null ? cursor.getString(cursor.getColumnIndex("vis")): "");
            user.put("opc",cursor.getString(cursor.getColumnIndex("opc")) != null ? cursor.getString(cursor.getColumnIndex("opc")): "");
            user.put("tabla_local",cursor.getString(cursor.getColumnIndex("tabla_local")) != null ? cursor.getString(cursor.getColumnIndex("tabla_local")): "");
            user.put("evento1",cursor.getString(cursor.getColumnIndex("evento1")) != null ? cursor.getString(cursor.getColumnIndex("evento1")): "");
            user.put("llamado1",cursor.getString(cursor.getColumnIndex("llamado1")) != null ? cursor.getString(cursor.getColumnIndex("llamado1")): "");
            user.put("tooltip",cursor.getString(cursor.getColumnIndex("tooltip")) != null ? cursor.getString(cursor.getColumnIndex("tooltip")): "");
            user.put("modificacion",cursor.getString(cursor.getColumnIndex("modificacion")) != null ? cursor.getString(cursor.getColumnIndex("modificacion")): "");
            user.put("sufijo",cursor.getString(cursor.getColumnIndex("sufijo")) != null ? cursor.getString(cursor.getColumnIndex("sufijo")): "");
            try {
                user.put("comentario_auto", cursor.getString(cursor.getColumnIndex("comentario_auto")) != null ? cursor.getString(cursor.getColumnIndex("comentario_auto")) : "");
            }catch(Exception e){}
            //metadatas
            user.put("datatype",cursor.getString(cursor.getColumnIndex("DATA_TYPE")) != null ? cursor.getString(cursor.getColumnIndex("DATA_TYPE")): "");
            user.put("numeric_precision",cursor.getString(cursor.getColumnIndex("NUMERIC_PRECISION")) != null ? cursor.getString(cursor.getColumnIndex("NUMERIC_PRECISION")): "");
            user.put("maxlength",cursor.getString(cursor.getColumnIndex("CHARACTER_MAXIMUM_LENGTH")) != null ? cursor.getString(cursor.getColumnIndex("CHARACTER_MAXIMUM_LENGTH")): "");
            clientList.add(user);
        }
        cursor.close();
        return  clientList;
    }

    public List<String> getPestanasFormulario(String id_formulario){
        List<String> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "select DISTINCT p.orden, c.panta, p.desc_panta from configuracion c " +
                " join Pantalla p ON (p.id_panta = c.panta) " +
                " where id_formulario = "+id_formulario+" AND c.fecini <= datetime('now') AND c.fecfin >= datetime('now')" +
                " order by p.orden, c.panta, p.desc_panta";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(2).trim());//3era columna del query
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
     * @param filtroAdicional : where sql
     * @return listaCatalogo : listaCatalogo
     */
    public ArrayList<HashMap<String, String>> getDatosCatalogo(String tabla, String... filtroAdicional){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT * " +
                " FROM " + tabla +" WHERE 1=1";

        StringBuilder filtros = new StringBuilder();

        //Crear Filtros manuales desde los parametros

        for(String filtro : filtroAdicional){
            if(filtro.length() > 0)
                filtros.append(" AND ").append(filtro);
        }
        if(tabla.equals("cat_tzont")){
            selectQuery = "SELECT DISTINCT a.* " +
                    " FROM " + tabla +" a INNER JOIN" +
                    " EX_T_RUTAS_VP AS b ON (trim(a.zone1) = trim(b.zroute_rep) OR trim(a.zone1) = trim(b.zroute_pr)) WHERE trim(zone1) != '' ";
        }


        //Cadena = cat_zesdvt_00561, Keyaccount = cat_ztmdcmc_00038t
        if(tabla.equals("cat_zesdvt_00561") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("1661") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("Z001") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("F428")){
            String tipoSolicitud  = "";
            if (mContext instanceof SolicitudActivity) {
                SolicitudActivity activity = (SolicitudActivity) mContext;
                Bundle extras = activity.getIntent().getExtras();
                if (extras != null) {
                    tipoSolicitud = extras.getString("tipoSolicitud");
                }
                if(tipoSolicitud == null || tipoSolicitud .equals("")){
                    tipoSolicitud = SolicitudActivity.tipoSolicitud;
                }
            }
            if (mContext instanceof SolicitudModificacionActivity) {
                SolicitudModificacionActivity activity = (SolicitudModificacionActivity) mContext;
                Bundle extras = activity.getIntent().getExtras();
                if (extras != null) {
                    tipoSolicitud = extras.getString("tipoSolicitud");
                }
                if(tipoSolicitud == null || tipoSolicitud .equals("")){
                    tipoSolicitud = SolicitudModificacionActivity.tipoSolicitud;
                }
            }
            if(UsaIndirectos() && (esInclusionIndirecto(tipoSolicitud) || esDestinoIndirecto(tipoSolicitud))) {
                List<String> listaCadenasInd = getCadenasIndirectos();
                // Convert list → string for IN clause
                String inClause =  "'" + TextUtils.join("','", listaCadenasInd) + "'";

                filtros.append(" AND trim(hkunnr) IN (" + inClause + ")");

            }
            else {
                filtros.append(" AND trim(hkunnr) = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_CADENARM", "") + "' AND zzkeyacc = 'CA002'");
            }
        }
        if(tabla.equals("cat_ztmdcmc_00038t") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("1661") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("Z001") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("F428")){
            String tipoSolicitud  = "";
            if (mContext instanceof SolicitudActivity) {
                SolicitudActivity activity = (SolicitudActivity) mContext;
                Bundle extras = activity.getIntent().getExtras();
                if (extras != null) {
                    tipoSolicitud = extras.getString("tipoSolicitud");
                }
                if(tipoSolicitud == null || tipoSolicitud .equals("")){
                    tipoSolicitud = SolicitudActivity.tipoSolicitud;
                }
            }
            if (mContext instanceof SolicitudModificacionActivity) {
                SolicitudModificacionActivity activity = (SolicitudModificacionActivity) mContext;
                Bundle extras = activity.getIntent().getExtras();
                if (extras != null) {
                    tipoSolicitud = extras.getString("tipoSolicitud");
                }
                if(tipoSolicitud == null || tipoSolicitud .equals("")){
                    tipoSolicitud = SolicitudModificacionActivity.tipoSolicitud;
                }
            }
            if(UsaIndirectos() && (esInclusionIndirecto(tipoSolicitud) || esDestinoIndirecto(tipoSolicitud))) {
                List<String> listaCadenasInd = getCadenasIndirectos();
                // Convert list → string for IN clause
                filtros.append(" AND trim(zkeyacc) != 'CA002'");

            }else {
                filtros.append(" AND trim(zkeyacc) = 'CA002'");
            }
        }
        if(tabla.equals("cat_ztsdvto_00185")) {
            if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("F428")) {
                if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA","").equals("ZGE"))
                    filtros.append(" AND (id_kvgr5 IN(SELECT id FROM cat_ztsdvto_00185_x WHERE(vpore = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA", "") + "'))) AND (id_kvgr5 = 'C03' OR id_kvgr5 = 'D03')");
                else
                    filtros.append(" AND (id_kvgr5 IN(SELECT id FROM cat_ztsdvto_00185_x WHERE(vpore = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA", "") + "')))");
            }else
                filtros.append(" AND (id_kvgr5 IN(SELECT id FROM cat_ztsdvto_00185_x WHERE(vpore = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA","")+"')))");
        }
        if(tabla.contains("zesdvt_01044")){
            filtros.append("");
        }
        //TODO si entran formales D y ABC al app se debe cambiar esta manera de filtrar
        if(tabla.equals("cat_knvv")){
            if(!PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("1661") && !PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("Z001"))
                filtros.append(" AND (zterm like '%L%' OR zterm like '%00')");
            else
                filtros.append(" AND (zterm like '%UF%' OR zterm like '%00')");
        }
        //Crear Filtros Automaticos segun el pais

        //Si existe BUKRS en la tabla del catalago vamos a filtros por Sociedad
        if(existeColumna(tabla,"bukrs")){
            filtros.append(" AND trim(bukrs) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")).append("'");
        }
        if(existeColumna(tabla,"land1")){
            filtros.append(" AND trim(land1) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"vkorg")){
            filtros.append(" AND trim(vkorg) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","")).append("'");
        }
        if(existeColumna(tabla,"banks")){
            filtros.append(" AND trim(banks) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"talnd")){
            filtros.append(" AND trim(talnd) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"werks")){
            filtros.append(" AND werks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VWERK","")).append("'");
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
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
            //Toasty.error(mContext,"No se pudo extraer los datos de Catalogo "+tabla+". "+e.getMessage()).show();
        }
        return listaCatalogo;
    }
    /**
     *
     * @param tabla : nombre de la tabla de base de datos del catálogo
     * @param filtroAdicional : where sql
     * @return listaCatalogo : listaCatalogo
     */
    public ArrayList<OpcionSpinner> getDatosCatalogoParaSpinner(String tabla, String... filtroAdicional){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();
        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * " +
                " FROM " + tabla +" WHERE 1=1";

        StringBuilder filtros = new StringBuilder();

        //Crear Filtros manuales desde los parametros
        for(String filtro : filtroAdicional){
            filtros.append(" AND ").append(filtro);
        }
        if(tabla.equals("cat_tzont")){
            selectQuery = "SELECT DISTINCT a.* " +
                    " FROM " + tabla +" a INNER JOIN" +
                    " EX_T_RUTAS_VP AS b ON (trim(a.zone1) = trim(b.zroute_rep) OR trim(a.zone1) = trim(b.zroute_pr)) WHERE trim(zone1) != '' ";
        }
        if(tabla.equals("SAPDCAT_Ruta_Relacion")){
            selectQuery = "SELECT DISTINCT zroute as id, zroute as descripcion  FROM " + tabla +" WHERE 1=1 ";
        }
        if(tabla.equals("EX_T_RUTAS_VP")){
            selectQuery = "SELECT DISTINCT zroute_pr as id, zroute_pr as ruta " +
                    " FROM " + tabla +" WHERE 1=1 ";
        }
        if(tabla.equals("aprobadores")){
            selectQuery = "select DISTINCT id_usuario, nombre_usuario from flujoxpais as fxp" +
                    "        INNER JOIN etapa as e  ON fxp.id_Etapa = e.id_Etapa" +
                    "        INNER JOIN aprobadores as a ON (a.id_flujoxpais = fxp.id_flujoxpais)" +
                    "        LEFT JOIN mant_usuarios m ON (upper(trim(m.id_usuario)) = upper(trim(a.id_aprobador)))" +
                    "        where id_pais = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"' and fxp.orden = 1 and id_agencia = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK","")+"' and Estado = 1";
        }
        //Crear Filtros Automaticos segun el pais

        //Si existe BUKRS en la tabla del catalago vamos a filtros por Sociedad
        if(existeColumna(tabla,"bukrs")){
            filtros.append(" AND trim(bukrs) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")).append("'");
        }
        if(existeColumna(tabla,"land1")){
            filtros.append(" AND trim(land1) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"vkorg")){
            filtros.append(" AND trim(vkorg) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","")).append("'");
        }
        if(existeColumna(tabla,"banks")){
            filtros.append(" AND trim(banks) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"talnd")){
            filtros.append(" AND trim(talnd) = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"werks")){
            filtros.append(" AND werks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VWERK","")).append("'");
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
                    lista.put("id",cursor.getString(0).trim());//1era columna del query
                    if(tabla.equals("cat_dominios")){//Hacer variable para generico que solo salga la descripcion y no el codigo o id
                        lista.put("descripcion",cursor.getString(1).trim());//1era y 2da columna del query
                    }else{
                        lista.put("descripcion",cursor.getString(0).trim() + " - " + cursor.getString(1).trim());//1era y 2da columna del query
                    }

                    listaCatalogo.add(lista);
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();

            int selectedIndex = 0;
            for (int j = 0; j < listaCatalogo.size(); j++){
                listaopciones.add(new OpcionSpinner(listaCatalogo.get(j).get("id"), listaCatalogo.get(j).get("descripcion")) );
            }

            //db.close();
            // returning lables
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            listaopciones.add(new OpcionSpinner("","Seleccione*..."));
        }
        return listaopciones;
    }
    public List<KeyPairBoolData> getEstadosCatalogoParaMultiSpinner(){
        List<KeyPairBoolData> listaCatalogo = new ArrayList<KeyPairBoolData>();
        //List<String> listaopciones = new ArrayList<>();*
        // Select All Query
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery("Select estado as id, estado as descripcion from formHvKof_solicitud group by estado", null);//selectQuery,selectedArguments
            //listaCatalogo.put("Seleccione..",true);
            // looping through all rows and adding to list
            int indice = 0;
            if (cursor.moveToFirst()) {
                do {
                    KeyPairBoolData lista = new KeyPairBoolData();
                    lista.setId(indice);
                    lista.setName(cursor.getString(0).trim());
                    lista.setSelected(false);
                    listaCatalogo.add(lista);
                    indice++;
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();

            //int selectedIndex = 0;
            //for (int j = 0; j < listaCatalogo.size(); j++){
                //listaopciones.add(new OpcionSpinner(listaCatalogo.get(j).get("id"), listaCatalogo.get(j).get("descripcion")) );
            //}

            //db.close();
            // returning lables
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            //listaCatalogo.put("Seleccione...",true);
        }
        return listaCatalogo;
    }

    public List<KeyPairBoolData> getTiposFormularioParaMultiSpinner(){
        List<KeyPairBoolData> listaCatalogo = new ArrayList<KeyPairBoolData>();
        try {
            Cursor cursor = mDataBase.rawQuery("Select id_form as id, descripcion as descripcion from flujo", null);//selectQuery,selectedArguments
            // iterar sobre todas las filas recibidas
            if (cursor.moveToFirst()) {
                do {
                    KeyPairBoolData lista = new KeyPairBoolData();
                    lista.setId(cursor.getInt(0));
                    lista.setName(cursor.getString(1).trim());
                    lista.setSelected(false);
                    listaCatalogo.add(lista);
                } while (cursor.moveToNext());
            }
            // closing connection
            cursor.close();
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
        }
        return listaCatalogo;
    }

    public List<String> getCadenasIndirectos(){
        List<String> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "select hkunnr FROM cat_loc_cadenas_indirectos " +
                " where bukrs = '"+ PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'"+
                " order by hkunnr";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0).trim());//3era columna del query
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

    public void getDatosCatalogoAsync(String tabla, int columnaId, int columnaDesc, Integer columnaAdicional, WeakReference<Activity> activity, ArrayList<HashMap<String, String>> solicitudSeleccionada, ArrayList<HashMap<String, String>> solicitudSeleccionadaOld, HashMap<String, String> campo, JsonArray clienteJson, SearchableSpinner combo, String... filtroAdicional){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT * " +
                " FROM " + tabla +" WHERE 1=1";
        StringBuilder filtros = new StringBuilder();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //Crear Filtros manuales desde los parametros
                for(String filtro : filtroAdicional){
                    filtros.append(" AND ").append(filtro);
                }
                //Si existe BUKRS en la tabla del catalago vamos a filtros por Sociedad
                if(existeColumna(tabla,"bukrs")){
                    filtros.append(" AND bukrs = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")).append("'");
                }
                if(existeColumna(tabla,"land1")){
                    filtros.append(" AND land1 = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
                }
                if(existeColumna(tabla,"vkorg")){
                    filtros.append(" AND vkorg = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","")).append("'");
                }
                if(existeColumna(tabla,"bzirk")){
                    filtros.append(" AND bzirk = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK","")).append("'");
                }
                if(existeColumna(tabla,"banks")){
                    filtros.append(" AND banks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
                }
                if(existeColumna(tabla,"talnd")){
                    filtros.append(" AND talnd = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
                }
                if(existeColumna(tabla,"werks")){
                    filtros.append(" AND werks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VWERK","")).append("'");
                }
                if(existeColumna(tabla,"activo")){
                    filtros.append(" AND activo = 'True'");
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
                            HashMap<String, String> lista = new HashMap<>();
                            lista.put("id", cursor.getString(columnaId).trim());//1era columna del query
                            if(columnaAdicional == null) {
                                lista.put("descripcion", cursor.getString(columnaId).trim() + " - " + cursor.getString(columnaDesc).trim());
                            }else{
                                lista.put("descripcion", cursor.getString(columnaId).trim() + " - " + cursor.getString(columnaDesc).trim()+ " ("+cursor.getString(columnaAdicional).trim()+ ") ");
                            }
                            if(!listaCatalogo.contains(lista)) {
                                if (tabla.equals("sapdmateriales_pde")) {
                                    lista.put("id", cursor.getString(columnaId).trim().substring(10));
                                    lista.put("descripcion", cursor.getString(columnaId).trim().substring(10) + " - " + cursor.getString(columnaDesc));
                                }
                                listaCatalogo.add(lista);
                            }
                        } while (cursor.moveToNext());
                    }
                    // closing connection
                    cursor.close();



                }catch (Exception e){
                    e.getMessage();
                    e.printStackTrace();
                    HashMap<String,String> seleccione = new HashMap<>();
                    seleccione.put("id","");
                    seleccione.put("descripcion","Seleccione...");
                    listaCatalogo.add(seleccione);
                }

                // Update the UI with the data on the main thread
                activity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update the adapter on the main thread
                        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
                        int selectedIndex = 0;
                        int selectedIndexOld = 0;
                        String valorDefectoxRuta = PreferenceManager.getDefaultSharedPreferences(mContext).getString(campo.get("campo").trim().replace("-","_"),"");
                        for (int j = 0; j < listaCatalogo.size(); j++){
                            listaopciones.add(new OpcionSpinner(listaCatalogo.get(j).get("id"), listaCatalogo.get(j).get("descripcion")));
                            if(solicitudSeleccionada.size() > 0){
                                //valor de la solicitud seleccionada
                                if(listaCatalogo.get(j).get("id").trim().equals(solicitudSeleccionada.get(0).get(campo.get("campo").trim()).trim())){
                                    selectedIndex = j;
                                }
                                if(solicitudSeleccionadaOld.size() > 0 && solicitudSeleccionadaOld.get(0).get(campo.get("campo").trim())!= null && listaCatalogo.get(j).get("id").trim().equals(solicitudSeleccionadaOld.get(0).get(campo.get("campo").trim()).trim())){
                                    selectedIndexOld = j;
                                }
                            }
                        }
                        // Creando el adaptador(opciones) para el comboBox deseado
                        ArrayAdapter<OpcionSpinner> dataAdapter = new ArrayAdapter<OpcionSpinner>(mContext, R.layout.simple_spinner_item, listaopciones);
                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                        // attaching data adapter to spinner
                        combo.setAdapter(dataAdapter);
                        dataAdapter.notifyDataSetChanged();
                        if(solicitudSeleccionada.size() > 0) {
                            combo.setSelection(selectedIndex);
                        }
                        if(combo.getCount() > 1 && clienteJson != null) {
                            Log.w("Catalogos", campo.get("campo").trim()+" - "+VariablesGlobales.getIndex(combo, clienteJson.get(0).getAsJsonObject().get(campo.get("campo").trim()).getAsString().trim()));
                            combo.setSelection(VariablesGlobales.getIndex(combo, clienteJson.get(0).getAsJsonObject().get(campo.get("campo").trim()).getAsString().trim()));
                        }
                        if(campo.get("modificacion").trim().equals("1") && solicitudSeleccionada.size() != 0){
                            combo.setSelection(selectedIndexOld);
                        }

                    }
                });
            }
        });
    }
    /**
     *
     * @param tabla : nombre de la tabla de base de datos del catálogo
     * @param filtroAdicional : filtro deseado en formato de sql WHERE
     * @return listaCatalogo : Lista de datos del catalago de la tabla
     */
    public ArrayList<HashMap<String, String>> getDatosCatalogo(String tabla, int columnaId, int columnaDesc, Integer columnaAdicional, String... filtroAdicional){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT * " +
                " FROM " + tabla +" WHERE 1=1";
        StringBuilder filtros = new StringBuilder();

        //Crear Filtros manuales desde los parametros
        for(String filtro : filtroAdicional){
            filtros.append(" AND ").append(filtro);
        }
        //Si existe BUKRS en la tabla del catalago vamos a filtros por Sociedad
        if(existeColumna(tabla,"bukrs")){
            filtros.append(" AND bukrs = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")).append("'");
        }
        if(existeColumna(tabla,"land1")){
            filtros.append(" AND land1 = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"vkorg")){
            filtros.append(" AND vkorg = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","")).append("'");
        }
        if(existeColumna(tabla,"bzirk")){
            filtros.append(" AND bzirk = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK","")).append("'");
        }
        if(existeColumna(tabla,"banks")){
            filtros.append(" AND banks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"talnd")){
            filtros.append(" AND talnd = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"werks")){
            filtros.append(" AND werks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VWERK","")).append("'");
        }
        if(existeColumna(tabla,"activo")){
            filtros.append(" AND activo = 'True'");
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
                    HashMap<String, String> lista = new HashMap<>();
                    lista.put("id", cursor.getString(columnaId).trim());//1era columna del query
                    if(columnaAdicional == null) {
                        lista.put("descripcion", cursor.getString(columnaId).trim() + " - " + cursor.getString(columnaDesc).trim());
                    }else{
                        lista.put("descripcion", cursor.getString(columnaId).trim() + " - " + cursor.getString(columnaDesc).trim()+ " ("+cursor.getString(columnaAdicional).trim()+ ") ");
                    }
                    if(!listaCatalogo.contains(lista)) {
                        if (tabla.equals("sapdmateriales_pde")) {
                            lista.put("id", cursor.getString(columnaId).trim().substring(10));
                            lista.put("descripcion", cursor.getString(columnaId).trim().substring(10) + " - " + cursor.getString(columnaDesc));
                        }
                        listaCatalogo.add(lista);
                    }
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


    private boolean existeColumna(String tabla, String columna){
        String sql = "PRAGMA table_info('"+tabla+"');";
        Cursor columnas = mDataBase.rawQuery(sql, null);
        //columnas.moveToFirst();
        while (columnas.moveToNext()) {
            if(columnas.getString(1).trim().toLowerCase().equals(columna.trim().toLowerCase())){
                columnas.close();
                return true;
            }
        }
        columnas.close();
        return false;
    }

    //LOGIN DE USUAIRO EN SISTEMA
    public boolean validarUsuarioHH(String usuario){
        String user = usuario;
        try{
            Integer.parseInt(usuario);
        }
        catch (NumberFormatException ex){
            user = VariablesGlobales.UsuarioMC2UsuarioHH(mContext, usuario);
        }
        String selectQuery = "SELECT count(*) as existe FROM t_i_users WHERE upper(trim(UserName)) = '" + user.trim().toUpperCase() +"'";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);//selectQuery,selectedArguments
            cursor.moveToFirst();
            int cantidad = cursor.getInt(0);
            cursor.close();
            if(cantidad <= 0){
                return false;
            }
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean validarUsuarioMC(String usuario){
        String usuarioMC = VariablesGlobales.UsuarioHH2UsuarioMC(mContext, usuario);
        String selectQuery = "SELECT count(*) as existe FROM mant_usuarios WHERE upper(trim(id_Usuario)) = '" + usuarioMC.trim().toUpperCase() +"' ";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);//selectQuery,selectedArguments
            cursor.moveToFirst();
            int cantidad = cursor.getInt(0);
            cursor.close();
            if(cantidad <= 0){
                return false;
            }
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean LoginUsuario(String usuario,String contrasena){
        String user = usuario;
        try{
            Integer.parseInt(usuario);
        }
        catch (NumberFormatException ex){
            user = VariablesGlobales.UsuarioMC2UsuarioHH(mContext, usuario);
        }

        String selectQuery = "SELECT count(*) as existe FROM t_i_users WHERE upper(trim(UserName)) = '" + user.trim().toUpperCase() +"' AND Password = '" + contrasena.trim()+"'";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);//selectQuery,selectedArguments
            cursor.moveToFirst();
            int cantidad = cursor.getInt(0);
            cursor.close();
            if(cantidad <= 0){
                return false;
            }
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean validarRutaSincronizada(String vkorg, String rutaSincronizada){
        String selectQuery = "SELECT count(*) FROM EX_T_RUTAS_VP WHERE vkorg = '" + vkorg +"' AND (zroute_pr = '" + rutaSincronizada +"' or zroute_pr = '*')";
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery(selectQuery, null);//selectQuery,selectedArguments
            cursor.moveToFirst();
            int cantidad = cursor.getInt(0);
            cursor.close();
            if(cantidad <= 0){
                return false;
            }
        }catch (Exception e){
            e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean setPropiedadesDeUsuario(){
        boolean ret = false;
        String userName = VariablesGlobales.UsuarioMC2UsuarioHH(mContext, PreferenceManager.getDefaultSharedPreferences(mContext).getString("user","").trim().toUpperCase());
        String query = "SELECT RouteID FROM t_i_users WHERE upper(trim(UserName)) = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String [] {userName});
        String rutaAsignada = "";
        while (cursor.moveToNext()){
            rutaAsignada = cursor.getString(0);
        }
        if(!rutaAsignada.equals("*")) {
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_RUTAHH", rutaAsignada).apply();
        }

        query = "SELECT * FROM EX_T_RUTAS_VP WHERE zroute_pr = ?";
        cursor = mDataBase.rawQuery(query, new String [] {PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH","").trim()});

        while (cursor.moveToNext()) {
            ret = true;
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_KTOKD", PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_GRUPOCUENTAS", VariablesGlobales.getKtokd())).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VKORG", cursor.getString(cursor.getColumnIndex("vkorg"))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_BUKRS", vkorgToBukrs(cursor.getString(cursor.getColumnIndex("vkorg")))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_LAND1", vkorgToLand1(cursor.getString(cursor.getColumnIndex("vkorg")))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_KDGRP", cursor.getString(cursor.getColumnIndex("kdgrp"))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_KVGR3", cursor.getString(cursor.getColumnIndex("kvgr3"))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_BZIRK", cursor.getString(cursor.getColumnIndex("bzirk"))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VKBUR", cursor.getString(cursor.getColumnIndex("vkbur"))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VKGRP", cursor.getString(cursor.getColumnIndex("vkgrp"))).apply();

            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("CONFIG_SOCIEDAD", vkorgToBukrs(cursor.getString(cursor.getColumnIndex("vkorg")))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("CONFIG_ORGVENTAS", cursor.getString(cursor.getColumnIndex("vkorg"))).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("CONFIG_LAND1", vkorgToLand1(cursor.getString(cursor.getColumnIndex("vkorg")))).apply();

            if (!cursor.getString(cursor.getColumnIndex("vwerks")).trim().isEmpty()) {
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VWERK", cursor.getString(cursor.getColumnIndex("vwerks"))).apply();
            } else {
                ArrayList<HashMap<String, String>> valores = getValoresKOFSegunZonaVentas(cursor.getString(cursor.getColumnIndex("bzirk")));
                if (valores.size() == 0) {
                    ret = false;
                } else {
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VWERK", valores.get(0).get("VWERK")).apply();
                }
            }
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_TIPORUTA", cursor.getString(cursor.getColumnIndex("vptyp")) ).apply();
            String areactrlcred = "";
            if(cursor.getString(cursor.getColumnIndex("vptyp")).contains("ZPV") || cursor.getString(cursor.getColumnIndex("vptyp")).contains("ZAT") || cursor.getString(cursor.getColumnIndex("vptyp")).contains("ZTV")){
                areactrlcred = "C#RF";
            }
            if(cursor.getString(cursor.getColumnIndex("vptyp")).contains("ZJV")){
                areactrlcred = "C#JG";
            }
            if(cursor.getString(cursor.getColumnIndex("vptyp")).contains("ZPK")){
                areactrlcred = "C#KF";
            }
            switch (PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim()){
                case "F443":
                    areactrlcred = areactrlcred.replace("#","R");
                    break;
                case "F445":
                    areactrlcred = areactrlcred.replace("#","N");
                    break;
                case "1657"://Volcanes
                    areactrlcred = areactrlcred.replace("#","G").replace("RF","VR");
                    break;
                case "1658"://Abasa
                    areactrlcred = areactrlcred.replace("#","G").replace("RF","AR");
                    break;
                case "F446":
                    areactrlcred = areactrlcred.replace("#","G");
                    break;
                case "F451":
                    areactrlcred = areactrlcred.replace("#","P");
                    break;
                case "1661":
                    areactrlcred = "U661";
                    break;
                case "Z001":
                    areactrlcred = "Z661";
                    break;
            }
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_AREACREDITO", areactrlcred ).apply();
        }
        cursor.close();
        return ret;
    }

    //Formularios de modificacion permitidos para la HH
    public ArrayList<HashMap<String, String>> getModificacionesPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'M' and ind_credito = 0  and ind_tipo_cliente != 'IN' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }

    public ArrayList<HashMap<String, String>> getModificacionesIndirectosPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'M' and ind_credito = 0 and ind_tipo_cliente = 'IN' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    //Formularios de modificacion permitidos para la HH
    public ArrayList<HashMap<String, String>> getModificacionesCreditoPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'M' and ind_credito = 1 and ind_tipo_cliente != 'IN' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    public ArrayList<HashMap<String, String>> getModificacionesCreditoIndirectosPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'M' and ind_credito = 1 and ind_tipo_cliente = 'IN' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    public ArrayList<HashMap<String, String>> getModificacionesRacksPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'W' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    //Formularios de modificacion permitidos para la HH
    public ArrayList<HashMap<String, String>> getOrdenesServicioPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'E' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    public ArrayList<HashMap<String, String>> getFormulariosRacksPermitidos(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'W' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    public ArrayList<HashMap<String, String>> getOrdenesServicioPermitidasMonitor(Integer puertas_por_instalar, Integer puertas_instaladas){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and activo = 1 and ind_modelo = 'E'";

        if(puertas_por_instalar > 0) {
            if (puertas_instaladas == 0)
                query += " AND (descripcion like '%INSTALACION%') ";
            else
                query += " AND (descripcion like '%CAMBIO%' OR descripcion like '%INSTALACION%') ";
        }
        else
        if(puertas_por_instalar < 0)
            query += " AND (descripcion like '%CAMBIO%' OR descripcion like '%RETIRO%') ";

        query += " order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    //Formularios de modificacion permitidos para la HH
    public ArrayList<HashMap<String, String>> getIniciativasLocalesPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 /*UYand activo = 1*/ and ind_modelo = 'L' order by orden";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> flujo = new HashMap<>();
            flujo.put("idform",cursor.getString(cursor.getColumnIndex("id_form")).trim());
            flujo.put("descripcion",cursor.getString(cursor.getColumnIndex("Descripcion")).trim());
            flujoList.add(flujo);
        }
        cursor.close();
        return  flujoList;
    }
    //Informacion de BLOQUES DE DATOS

    //CONTACTOS
    public ArrayList<Contacto> getContactosDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contacto> contactList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Contacto contacto = new Contacto();
            contacto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            contacto.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            contacto.setName1(cursor.getString(cursor.getColumnIndex("name1")) );
            contacto.setNamev(cursor.getString(cursor.getColumnIndex("namev")) );
            contacto.setTelf1(cursor.getString(cursor.getColumnIndex("telf1")) );
            contacto.setPafkt(cursor.getString(cursor.getColumnIndex("pafkt")) );
            contactList.add(contacto);
        }
        cursor.close();
        return  contactList;
    }
    public ArrayList<Contacto> getContactosOldDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Contacto> contactList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_CONTACTO_OLD_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Contacto contacto = new Contacto();
            contacto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            contacto.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            contacto.setName1(cursor.getString(cursor.getColumnIndex("name1")) );
            contacto.setNamev(cursor.getString(cursor.getColumnIndex("namev")) );
            contacto.setTelf1(cursor.getString(cursor.getColumnIndex("telf1")) );
            contacto.setPafkt(cursor.getString(cursor.getColumnIndex("pafkt")) );
            contactList.add(contacto);
        }
        cursor.close();
        return  contactList;
    }
    public ArrayList<Banco> getBancosDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Banco> bancosList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_BANCO_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Banco banco = new Banco();
            banco.setId_bancos(cursor.getString(cursor.getColumnIndex("id_bancos")) );
            banco.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            banco.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            banco.setBankl(cursor.getString(cursor.getColumnIndex("bankl")) );
            banco.setBankn(cursor.getString(cursor.getColumnIndex("bankn")) );
            banco.setBanks(cursor.getString(cursor.getColumnIndex("banks")) );
            banco.setBkont(cursor.getString(cursor.getColumnIndex("bkont")) );
            banco.setKoinh(cursor.getString(cursor.getColumnIndex("koinh")) );
            banco.setBvtyp(cursor.getString(cursor.getColumnIndex("bvtyp")) );
            banco.setBkref(cursor.getString(cursor.getColumnIndex("bkref")) );
            banco.setTask(cursor.getString(cursor.getColumnIndex("task")) );
            bancosList.add(banco);
        }
        cursor.close();
        return  bancosList;
    }

    public ArrayList<Banco> getBancosOldDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Banco> bancosList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_BANCO_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Banco banco = new Banco();
            banco.setId_bancos(cursor.getString(cursor.getColumnIndex("id_bancos")) );
            banco.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            banco.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            banco.setBankl(cursor.getString(cursor.getColumnIndex("bankl")) );
            banco.setBankn(cursor.getString(cursor.getColumnIndex("bankn")) );
            banco.setBanks(cursor.getString(cursor.getColumnIndex("banks")) );
            banco.setBkont(cursor.getString(cursor.getColumnIndex("bkont")) );
            banco.setKoinh(cursor.getString(cursor.getColumnIndex("koinh")) );
            banco.setBvtyp(cursor.getString(cursor.getColumnIndex("bvtyp")) );
            banco.setBkref(cursor.getString(cursor.getColumnIndex("bkref")) );
            banco.setTask(cursor.getString(cursor.getColumnIndex("task")) );
            bancosList.add(banco);
        }
        cursor.close();
        return  bancosList;
    }

    public ArrayList<Adjuntos> getAdjuntosDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Adjuntos> adjuntosList = new ArrayList<>();
        String queryName = "SELECT adjunto FROM adjuntos WHERE idform = (Select idform from formhvkof_solicitud where id_solicitud = ?)";
        String query = "SELECT id_solicitud,tipo,nombre,length(imagen) as tamano FROM "+VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD()+" WHERE id_solicitud = ?";
        String queryImage = "SELECT imagen FROM "+VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD()+" WHERE id_solicitud = ? AND nombre = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Adjuntos adjunto = new Adjuntos();
            int tamImagen = cursor.getInt(cursor.getColumnIndex("tamano"));
            try {
                if(tamImagen < 1000000) {
                    adjunto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")));
                    adjunto.setType(cursor.getString(cursor.getColumnIndex("tipo")));
                    adjunto.setName(cursor.getString(cursor.getColumnIndex("nombre")));
                    Cursor cursorInner = mDataBase.rawQuery(queryImage,new String[]{id_solicitud,cursor.getString(cursor.getColumnIndex("nombre"))});
                    while (cursorInner.moveToNext()) {
                        adjunto.setImage(cursorInner.getBlob(cursorInner.getColumnIndex("imagen")));
                    }
                    adjuntosList.add(adjunto);
                }else{
                    adjunto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")));
                    adjunto.setType(cursor.getString(cursor.getColumnIndex("tipo")));
                    adjunto.setName(cursor.getString(cursor.getColumnIndex("nombre")));
                    int buffer = 1000000;
                    byte[] totalImagen = new byte[tamImagen];
                    int tamAcumulado = 0;
                    for (int x = 1; x < tamImagen;x = x + buffer) {
                        String queryImagePart = "SELECT substr(imagen, ?, ?) as parteImagen FROM " + VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD() + " WHERE id_solicitud = ? AND nombre = ?";
                        Cursor cursorInner = mDataBase.rawQuery(queryImagePart, new String[]{Integer.toString(x), Integer.toString(buffer) ,id_solicitud, cursor.getString(cursor.getColumnIndex("nombre"))});
                        while (cursorInner.moveToNext()) {
                            byte[] parteImagen = cursorInner.getBlob(cursorInner.getColumnIndex("parteImagen"));
                            System.arraycopy(parteImagen, 0, totalImagen, tamAcumulado, parteImagen.length);
                            tamAcumulado += parteImagen.length;
                        }
                    }
                    adjunto.setImage(totalImagen);
                    adjuntosList.add(adjunto);
                }
            }catch(Exception e){
                String error = e.getMessage();
            }
        }
        cursor.close();
        return  adjuntosList;
    }
    public ArrayList<Adjuntos> getAdjuntosServidor(String idform){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Adjuntos> adjuntosList = new ArrayList<>();
        String query = "SELECT * FROM adjuntos WHERE idform = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{idform});
        while (cursor.moveToNext()){
            Adjuntos adjunto = new Adjuntos();
            adjunto.setId_solicitud(cursor.getString(cursor.getColumnIndex("idform")) );
            adjunto.setType(null);
            adjunto.setName(cursor.getString(cursor.getColumnIndex("adjunto")) );
            adjunto.setImage(null);
            adjuntosList.add(adjunto);
        }
        cursor.close();
        return  adjuntosList;
    }

    public ArrayList<Interlocutor> getInterlocutoresDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Interlocutor> interlocutoresList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Interlocutor interlocutor = new Interlocutor();
            interlocutor.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            interlocutor.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            interlocutor.setKunn2(cursor.getString(cursor.getColumnIndex("kunn2")) );
            interlocutor.setName1(cursor.getString(cursor.getColumnIndex("name1")) );
            interlocutor.setParvw(cursor.getString(cursor.getColumnIndex("parvw")) );
            interlocutor.setVtext(cursor.getString(cursor.getColumnIndex("vtext")) );

            interlocutoresList.add(interlocutor);
        }
        cursor.close();
        return  interlocutoresList;
    }

    public ArrayList<Interlocutor> getInterlocutoresOldDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Interlocutor> interlocutoresList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_OLD_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Interlocutor interlocutor = new Interlocutor();
            interlocutor.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            interlocutor.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            interlocutor.setKunn2(cursor.getString(cursor.getColumnIndex("kunn2")) );
            interlocutor.setName1(cursor.getString(cursor.getColumnIndex("name1")) );
            interlocutor.setParvw(cursor.getString(cursor.getColumnIndex("parvw")) );
            interlocutor.setVtext(cursor.getString(cursor.getColumnIndex("vtext")) );

            interlocutoresList.add(interlocutor);
        }
        cursor.close();
        return  interlocutoresList;
    }

    public ArrayList<Impuesto> getImpuestosDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Impuesto> impuestoList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Impuesto impuesto = new Impuesto();
            impuesto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            impuesto.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            impuesto.setTatyp(cursor.getString(cursor.getColumnIndex("tatyp")) );
            impuesto.setVtext(cursor.getString(cursor.getColumnIndex("vtext")) );
            impuesto.setTaxkd(cursor.getString(cursor.getColumnIndex("taxkd")) );
            impuesto.setVtext2(cursor.getString(cursor.getColumnIndex("vtext2")) );;
            impuestoList.add(impuesto);
        }
        cursor.close();
        return  impuestoList;
    }

    public ArrayList<Impuesto> getImpuestosOldDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Impuesto> impuestoList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_OLD_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Impuesto impuesto = new Impuesto();
            impuesto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            impuesto.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            impuesto.setTatyp(cursor.getString(cursor.getColumnIndex("tatyp")) );
            impuesto.setVtext(cursor.getString(cursor.getColumnIndex("vtext")) );
            impuesto.setTaxkd(cursor.getString(cursor.getColumnIndex("taxkd")) );
            impuesto.setVtext2(cursor.getString(cursor.getColumnIndex("vtext2")) );;
            impuestoList.add(impuesto);
        }
        cursor.close();
        return  impuestoList;
    }

    public ArrayList<Visitas> getVisitasDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Visitas> visitasList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_VISITA_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            visita.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("vptyp")) );
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("kvgr4")) );
            visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")) );
            visita.setLun_de(cursor.getString(cursor.getColumnIndex("lun_de")) );
            visita.setMar_de(cursor.getString(cursor.getColumnIndex("mar_de")) );
            visita.setMier_de(cursor.getString(cursor.getColumnIndex("mier_de")) );
            visita.setJue_de(cursor.getString(cursor.getColumnIndex("jue_de")) );
            visita.setVie_de(cursor.getString(cursor.getColumnIndex("vie_de")) );
            visita.setSab_de(cursor.getString(cursor.getColumnIndex("sab_de")) );
            visita.setDom_de(cursor.getString(cursor.getColumnIndex("dom_de")) );
            visita.setLun_a(cursor.getString(cursor.getColumnIndex("lun_a")) );
            visita.setMar_a(cursor.getString(cursor.getColumnIndex("mar_a")) );
            visita.setMier_a(cursor.getString(cursor.getColumnIndex("mier_a")) );
            visita.setJue_a(cursor.getString(cursor.getColumnIndex("jue_a")) );
            visita.setVie_a(cursor.getString(cursor.getColumnIndex("vie_a")) );
            visita.setSab_a(cursor.getString(cursor.getColumnIndex("sab_a")) );
            visita.setDom_a(cursor.getString(cursor.getColumnIndex("dom_a")) );
            visita.setF_ini(cursor.getString(cursor.getColumnIndex("f_ini")) );
            visita.setF_fin(cursor.getString(cursor.getColumnIndex("f_fin")) );
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("f_ico")) );
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("f_fco")) );
            visita.setF_frec(cursor.getString(cursor.getColumnIndex("f_frec")) );
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("fcalid")) );

            visitasList.add(visita);
        }
        cursor.close();
        return  visitasList;
    }

    public ArrayList<Visitas> getVisitasOldDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Visitas> visitasList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_BLOQUE_VISITA_OLD_HH()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            visita.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("vptyp")) );
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("kvgr4")) );
            visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")) );
            visita.setLun_de(cursor.getString(cursor.getColumnIndex("lun_de")) );
            visita.setMar_de(cursor.getString(cursor.getColumnIndex("mar_de")) );
            visita.setMier_de(cursor.getString(cursor.getColumnIndex("mier_de")) );
            visita.setJue_de(cursor.getString(cursor.getColumnIndex("jue_de")) );
            visita.setVie_de(cursor.getString(cursor.getColumnIndex("vie_de")) );
            visita.setSab_de(cursor.getString(cursor.getColumnIndex("sab_de")) );
            visita.setDom_de(cursor.getString(cursor.getColumnIndex("dom_de")) );
            visita.setLun_a(cursor.getString(cursor.getColumnIndex("lun_a")) );
            visita.setMar_a(cursor.getString(cursor.getColumnIndex("mar_a")) );
            visita.setMier_a(cursor.getString(cursor.getColumnIndex("mier_a")) );
            visita.setJue_a(cursor.getString(cursor.getColumnIndex("jue_a")) );
            visita.setVie_a(cursor.getString(cursor.getColumnIndex("vie_a")) );
            visita.setSab_a(cursor.getString(cursor.getColumnIndex("sab_a")) );
            visita.setDom_a(cursor.getString(cursor.getColumnIndex("dom_a")) );
            visita.setF_ini(cursor.getString(cursor.getColumnIndex("f_ini")) );
            visita.setF_fin(cursor.getString(cursor.getColumnIndex("f_fin")) );
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("f_ico")) );
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("f_fco")) );
            visita.setF_frec(cursor.getString(cursor.getColumnIndex("f_frec")) );
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("fcalid")) );

            visitasList.add(visita);
        }
        cursor.close();
        return  visitasList;
    }

    public ArrayList<Horarios> getHorariosDB(String id_solicitud){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Horarios> horariosList = new ArrayList<>();
        String query = "SELECT * FROM "+VariablesGlobales.getTablaHorariosSolicitud()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Horarios horario = new Horarios();
            horario.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            horario.setId_formulario(cursor.getString(cursor.getColumnIndex("id_formulario")) );

            horario.setMoab1(cursor.getString(cursor.getColumnIndex("moab1")) );
            horario.setMobi1(cursor.getString(cursor.getColumnIndex("mobi1")) );
            horario.setDiab1(cursor.getString(cursor.getColumnIndex("diab1")) );
            horario.setDibi1(cursor.getString(cursor.getColumnIndex("dibi1")) );
            horario.setMiab1(cursor.getString(cursor.getColumnIndex("miab1")) );
            horario.setMibi1(cursor.getString(cursor.getColumnIndex("mibi1")) );
            horario.setDoab1(cursor.getString(cursor.getColumnIndex("doab1")) );
            horario.setDobi1(cursor.getString(cursor.getColumnIndex("dobi1")) );
            horario.setFrab1(cursor.getString(cursor.getColumnIndex("frab1")) );
            horario.setFrbi1(cursor.getString(cursor.getColumnIndex("frbi1")) );
            horario.setSaab1(cursor.getString(cursor.getColumnIndex("saab1")) );
            horario.setSabi1(cursor.getString(cursor.getColumnIndex("sabi1")) );
            horario.setSoab1(cursor.getString(cursor.getColumnIndex("soab1")) );
            horario.setSobi1(cursor.getString(cursor.getColumnIndex("sobi1")) );

            horario.setMoab2(cursor.getString(cursor.getColumnIndex("moab2")) );
            horario.setMobi2(cursor.getString(cursor.getColumnIndex("mobi2")) );
            horario.setDiab2(cursor.getString(cursor.getColumnIndex("diab2")) );
            horario.setDibi2(cursor.getString(cursor.getColumnIndex("dibi2")) );
            horario.setMiab2(cursor.getString(cursor.getColumnIndex("miab2")) );
            horario.setMibi2(cursor.getString(cursor.getColumnIndex("mibi2")) );
            horario.setDoab2(cursor.getString(cursor.getColumnIndex("doab2")) );
            horario.setDobi2(cursor.getString(cursor.getColumnIndex("dobi2")) );
            horario.setFrab2(cursor.getString(cursor.getColumnIndex("frab2")) );
            horario.setFrbi2(cursor.getString(cursor.getColumnIndex("frbi2")) );
            horario.setSaab2(cursor.getString(cursor.getColumnIndex("saab2")) );
            horario.setSabi2(cursor.getString(cursor.getColumnIndex("sabi2")) );
            horario.setSoab2(cursor.getString(cursor.getColumnIndex("soab2")) );
            horario.setSobi2(cursor.getString(cursor.getColumnIndex("sobi2")) );

            horariosList.add(horario);
        }
        cursor.close();
        return  horariosList;
    }

    public ArrayList<EquipoFrio> getEquiposFriosDB(String id_cliente){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<EquipoFrio> equiposFriosList = new ArrayList<>();
        String query = "SELECT * FROM sapDBaseInstalada WHERE KUNNR = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_cliente});

        while (cursor.moveToNext()){
            EquipoFrio ef = new EquipoFrio();
            ef.setKdgrp(cursor.getString(cursor.getColumnIndex("KDGRP")).trim());
            ef.setBzirk(cursor.getString(cursor.getColumnIndex("BZIRK")).trim());
            ef.setKunnr(cursor.getString(cursor.getColumnIndex("KUNNR")).trim());
            ef.setIbase(removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("IBASE")).trim()));
            ef.setInstance(cursor.getString(cursor.getColumnIndex("INSTANCE")).trim());
            ef.setObjecttyp(cursor.getString(cursor.getColumnIndex("OBJECTTYP")).trim());
            ef.setObjnr(cursor.getString(cursor.getColumnIndex("OBJNR")).trim());
            ef.setEqunr(cursor.getString(cursor.getColumnIndex("EQUNR")).trim());
            ef.setMatnr(cursor.getString(cursor.getColumnIndex("MATNR")).trim());
            ef.setEqart(cursor.getString(cursor.getColumnIndex("EQART")).trim());
            ef.setHerst(cursor.getString(cursor.getColumnIndex("HERST")).trim());
            ef.setEqktx(cursor.getString(cursor.getColumnIndex("EQKTX")).trim());
            ef.setSpras(cursor.getString(cursor.getColumnIndex("SPRAS")).trim());
            ef.setMatkl(cursor.getString(cursor.getColumnIndex("MATKL")).trim());
            ef.setSerge(cursor.getString(cursor.getColumnIndex("SERGE")).trim());
            ef.setSernr(cursor.getString(cursor.getColumnIndex("SERNR")).trim());

            equiposFriosList.add(ef);
        }
        cursor.close();
        return  equiposFriosList;
    }

    public EquipoFrio getEquipoFrioDB(String id_cliente, String id_equipo, boolean codigoSAP){
        //SQLiteDatabase db = this.getWritableDatabase();
        EquipoFrio ef = null;
        String query  = "";
        if(codigoSAP){
            query = "SELECT * FROM sapDBaseInstalada WHERE KUNNR = ? AND  trim(EQUNR) = ?";
        }else{
            query = "SELECT * FROM sapDBaseInstalada WHERE KUNNR = ? AND  trim(SERGE) = ?";
        }
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_cliente, id_equipo});

        if((cursor == null) || (cursor.getCount() == 0)){
            query = "SELECT * FROM sapDBaseInstalada WHERE KUNNR = ? AND  ltrim(trim(SERNR),'0') = ?";
            cursor = mDataBase.rawQuery(query,new String[]{id_cliente, id_equipo});
        }

        while (cursor.moveToNext()){
            ef = new EquipoFrio();
            ef.setKdgrp(cursor.getString(cursor.getColumnIndex("KDGRP")).trim());
            ef.setBzirk(cursor.getString(cursor.getColumnIndex("BZIRK")).trim());
            ef.setKunnr(cursor.getString(cursor.getColumnIndex("KUNNR")).trim());
            ef.setIbase(removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("IBASE")).trim()));
            ef.setInstance(cursor.getString(cursor.getColumnIndex("INSTANCE")).trim());
            ef.setObjecttyp(cursor.getString(cursor.getColumnIndex("OBJECTTYP")).trim());
            ef.setObjnr(cursor.getString(cursor.getColumnIndex("OBJNR")).trim());
            ef.setEqunr(cursor.getString(cursor.getColumnIndex("EQUNR")).trim());
            ef.setMatnr(cursor.getString(cursor.getColumnIndex("MATNR")).trim());
            ef.setEqart(cursor.getString(cursor.getColumnIndex("EQART")).trim());
            ef.setHerst(cursor.getString(cursor.getColumnIndex("HERST")).trim());
            ef.setEqktx(cursor.getString(cursor.getColumnIndex("EQKTX")).trim());
            ef.setSpras(cursor.getString(cursor.getColumnIndex("SPRAS")).trim());
            ef.setMatkl(cursor.getString(cursor.getColumnIndex("MATKL")).trim());
            ef.setSerge(cursor.getString(cursor.getColumnIndex("SERGE")).trim());
            ef.setSernr(cursor.getString(cursor.getColumnIndex("SERNR")).trim());
        }
        cursor.close();
        return  ef;
    }

    public HashMap<String, String> getEquipoFrioDatosMonitor(String num_equipo){
        HashMap<String, String> equipo = new HashMap<>();
        String query = "SELECT * FROM eq_inventario WHERE num_equipo = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{String.format("%1$18s", num_equipo).replace(' ', '0')});
        while (cursor.moveToNext()){
            equipo.put("sociedad",cursor.getString(cursor.getColumnIndex("sociedad"))!=null?cursor.getString(cursor.getColumnIndex("sociedad")).trim():"");
            equipo.put("num_equipo",cursor.getString(cursor.getColumnIndex("num_equipo"))!=null?cursor.getString(cursor.getColumnIndex("num_equipo")).trim():"");
            equipo.put("modelo",cursor.getString(cursor.getColumnIndex("modelo"))!=null?cursor.getString(cursor.getColumnIndex("modelo")).trim():"");
            equipo.put("material",cursor.getString(cursor.getColumnIndex("material"))!=null?cursor.getString(cursor.getColumnIndex("material")).trim():"");
            equipo.put("num_serie",cursor.getString(cursor.getColumnIndex("num_serie"))!=null?cursor.getString(cursor.getColumnIndex("num_serie")).trim():"");
            equipo.put("num_puertas",cursor.getString(cursor.getColumnIndex("num_puertas"))!=null?cursor.getString(cursor.getColumnIndex("num_puertas")).trim():"");
            equipo.put("estado",cursor.getString(cursor.getColumnIndex("estado"))!=null?cursor.getString(cursor.getColumnIndex("estado")).trim():"");
            equipo.put("centro_suministro",cursor.getString(cursor.getColumnIndex("centro_suministro"))!=null?cursor.getString(cursor.getColumnIndex("centro_suministro")).trim():"");
            equipo.put("codigo_cliente",cursor.getString(cursor.getColumnIndex("codigo_cliente"))!=null?cursor.getString(cursor.getColumnIndex("codigo_cliente")).trim():"");
            equipo.put("nombre_cliente",cursor.getString(cursor.getColumnIndex("nombre_cliente"))!=null?cursor.getString(cursor.getColumnIndex("nombre_cliente")).trim():"");
            equipo.put("emplazamiento",cursor.getString(cursor.getColumnIndex("emplazamiento"))!=null?cursor.getString(cursor.getColumnIndex("emplazamiento")).trim():"");
        }
        cursor.close();
        return  equipo;
    }

    public EquipoFrio getEquipoFrioDatosCenso(String num_placa){
        //SQLiteDatabase db = this.getWritableDatabase();
        EquipoFrio ef = new EquipoFrio();
        String query = "SELECT b.*, c.* FROM sapDBaseInstalada b " +
                "LEFT JOIN CensoEquipoFrio c ON (b.kunnr = c.kunnr_censo AND c.activo = 1 AND b.serge = c.num_placa) " +
                "LEFT OUTER JOIN CensoEquipoFrio p2 ON (b.serge = p2.num_placa AND " +
                "(c.fecha_lectura < p2.fecha_lectura OR (c.fecha_lectura = p2.fecha_lectura AND c.id < p2.id))) " +
                "WHERE b.SERGE = ?  AND p2.num_placa IS NULL";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{num_placa});

        if((cursor == null) || (cursor.getCount() == 0)){
            query = "SELECT b.*, c.* FROM sapDBaseInstalada b " +
                    "LEFT JOIN CensoEquipoFrio c ON (b.kunnr = c.kunnr_censo AND c.activo = 1 AND b.serge = c.num_placa) " +
                    "LEFT OUTER JOIN CensoEquipoFrio p2 ON (b.serge = p2.num_placa AND " +
                    "(c.fecha_lectura < p2.fecha_lectura OR (c.fecha_lectura = p2.fecha_lectura AND c.id < p2.id))) " +
                    "WHERE (b.SERNR = ? OR ltrim(trim(b.SERNR),'0')  = ? )  AND p2.num_placa IS NULL";
            cursor = mDataBase.rawQuery(query,new String[]{num_placa,num_placa});
        }
        if((cursor == null) || (cursor.getCount() == 0)){
            query = "SELECT b.*, c.* FROM sapDBaseInstalada b " +
                    "LEFT JOIN CensoEquipoFrio c ON (c.activo = 1 AND b.serge = c.num_placa) " +
                    "LEFT OUTER JOIN CensoEquipoFrio p2 ON (b.serge = p2.num_placa AND " +
                    "(c.fecha_lectura < p2.fecha_lectura OR (c.fecha_lectura = p2.fecha_lectura AND c.id < p2.id))) " +
                    "WHERE b.SERGE = ? ORDER BY c.fecha_lectura desc LIMIT 1";
            cursor = mDataBase.rawQuery(query,new String[]{num_placa});
        }

        while (cursor.moveToNext()){
            if(cursor.getString(cursor.getColumnIndex("KDGRP")) !=  null)
                ef.setKdgrp(cursor.getString(cursor.getColumnIndex("KDGRP")).trim());
            if(cursor.getString(cursor.getColumnIndex("BZIRK")) !=  null)
                ef.setBzirk(cursor.getString(cursor.getColumnIndex("BZIRK")).trim());
            if(cursor.getString(cursor.getColumnIndex("KUNNR")) !=  null)
                ef.setKunnr(cursor.getString(cursor.getColumnIndex("KUNNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("kunnr_censo")) !=  null)
                ef.setKunnrCenso(cursor.getString(cursor.getColumnIndex("kunnr_censo")).trim());
            if(cursor.getString(cursor.getColumnIndex("IBASE")) !=  null)
                ef.setIbase(removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("IBASE")).trim()));
            if(cursor.getString(cursor.getColumnIndex("INSTANCE")) !=  null)
                ef.setInstance(cursor.getString(cursor.getColumnIndex("INSTANCE")).trim());
            if(cursor.getString(cursor.getColumnIndex("OBJECTTYP")) !=  null)
                ef.setObjecttyp(cursor.getString(cursor.getColumnIndex("OBJECTTYP")).trim());
            if(cursor.getString(cursor.getColumnIndex("OBJNR")) !=  null)
                ef.setObjnr(cursor.getString(cursor.getColumnIndex("OBJNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("EQUNR")) !=  null)
                ef.setEqunr(cursor.getString(cursor.getColumnIndex("EQUNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("MATNR")) !=  null)
                ef.setMatnr(cursor.getString(cursor.getColumnIndex("MATNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("EQART")) !=  null)
                ef.setEqart(cursor.getString(cursor.getColumnIndex("EQART")).trim());
            if(cursor.getString(cursor.getColumnIndex("HERST")) !=  null)
                ef.setHerst(cursor.getString(cursor.getColumnIndex("HERST")).trim());
            if(cursor.getString(cursor.getColumnIndex("EQKTX")) !=  null)
                ef.setEqktx(cursor.getString(cursor.getColumnIndex("EQKTX")).trim());
            if(cursor.getString(cursor.getColumnIndex("SPRAS")) !=  null)
                ef.setSpras(cursor.getString(cursor.getColumnIndex("SPRAS")).trim());
            if(cursor.getString(cursor.getColumnIndex("MATKL")) !=  null)
                ef.setMatkl(cursor.getString(cursor.getColumnIndex("MATKL")).trim());
            if(cursor.getString(cursor.getColumnIndex("SERGE")) !=  null)
                ef.setSerge(cursor.getString(cursor.getColumnIndex("SERGE")).trim());
            if(cursor.getString(cursor.getColumnIndex("SERNR")) !=  null)
                ef.setSernr(cursor.getString(cursor.getColumnIndex("SERNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("estado")) !=  null)
                ef.setEstado(cursor.getString(cursor.getColumnIndex("estado")).trim());
            if(cursor.getString(cursor.getColumnIndex("fecha_lectura")) !=  null)
                ef.setFechaLectura(cursor.getString(cursor.getColumnIndex("fecha_lectura")).trim());
            if(cursor.getString(cursor.getColumnIndex("num_placa")) !=  null)
                ef.setNumPlaca(cursor.getString(cursor.getColumnIndex("num_placa")).trim());
            if(cursor.getString(cursor.getColumnIndex("activo")) !=  null)
                ef.setActivo(cursor.getString(cursor.getColumnIndex("activo")).trim());
            if(cursor.getString(cursor.getColumnIndex("comentario")) !=  null)
                ef.setComentario(cursor.getString(cursor.getColumnIndex("comentario")).trim());
            if(cursor.getString(cursor.getColumnIndex("transmitido")) !=  null)
                ef.setTransmitido(cursor.getString(cursor.getColumnIndex("transmitido")).trim());
            if(cursor.getString(cursor.getColumnIndex("id_solicitud")) !=  null)
                ef.setIdSolicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")).trim());
        }
        cursor.close();
        return  ef;
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
        return db.update(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), cVals, "id_contacto = ?",new String[]{String.valueOf(contacto.getId_solicitud())});
    }

    //IMPUESTOS DEFUALT X PAIS
    public ArrayList<Impuesto> getImpuestosPais(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Impuesto> impuestoList = new ArrayList<>();
        String query = query = "SELECT * FROM cat_impstos WHERE taxkd = 1 AND talnd = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")+"'";
        if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()).equals("1661") || PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_SOCIEDAD","").equals("Z001"))
            query = "SELECT * FROM cat_impstos WHERE taxkd = 2 AND talnd = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")+"'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            Impuesto impuesto = new Impuesto();
            impuesto.setTatyp(cursor.getString(1));
            impuesto.setVtext(cursor.getString(2));
            impuesto.setTaxkd(cursor.getString(3));
            impuesto.setVtext2(cursor.getString(4));
            impuestoList.add(impuesto);
        }
        cursor.close();
        return  impuestoList;
    }

    //INTERLOCUTORES DEFAULT X GRUPO DE CUENTAS
    public ArrayList<Interlocutor> getInterlocutoresPais(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Interlocutor> interlocutorList = new ArrayList<>();
        String grupoCuentasDefault = "RCMA";
        //Buscar el grupo de cuentas adecuado por pais
        switch(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")){
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
            case "UY":
                grupoCuentasDefault = "UYDE";
                break;
            case "CO":
                grupoCuentasDefault = "CODE";
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
        cursor.close();
        return  interlocutorList;
    }

    //Funciones de Ayuda para interfaz
    public ArrayList<Visitas> DeterminarPlanesdeVisita(String vkorg, String modalidad)
    {
        ArrayList<Visitas> visitasList = new ArrayList<>();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String fechaSistema = df.format(c);
        String metodo = "1DA";
        if(modalidad.equals("GV")){
            metodo = "0DA";
        }
        String query = "select vpore as vptyp, '' as descripcion, '"+metodo+"' as kvgr4, '' as ruta,'' as fec_frec, '" + fechaSistema + "' as f_ico, '99991231' as f_fco, '' as f_ini, '' as f_fin, '01' as fcalid FROM cat_ztsdvto_00185_x WHERE vkorg = '" + vkorg.trim() + "' and kvgr5 = '" + modalidad + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        int count = 0;
        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("vptyp")) );
            if(EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK",""), visita.getVptyp()))
                visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")) );
            else /*if(!visita.getVptyp().equals("ZRM"))*/ {
                //Validar que el tipo de visita ZPV no viene en ruta mixta
                String queryI = "select * FROM EX_T_RUTAS_VP WHERE vptyp = 'ZRM' and vkorg = '" + vkorg.trim() + "'";
                Cursor cursorI = mDataBase.rawQuery(queryI,null);
                count = cursorI.getCount();
                if(count > 0 && visita.getVptyp().equals("ZRM")){
                    cursorI.moveToFirst();
                    visita.setRuta(cursorI.getString(cursorI.getColumnIndex("zroute_pr")) );
                }else if( (count == 0 && !cursor.getString(cursor.getColumnIndex("vptyp")).equals("ZDY")) && (cursor.getString(cursor.getColumnIndex("vptyp")).equals(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA", "")))){
                    visita.setRuta(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH", ""));
                }else{
                    visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")) );
                }
                //Determinar ruta digitales si existen
                if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "").equals("F428") && (visita.getVptyp().equals("ZWE") || visita.getVptyp().equals("ZWB"))) {
                    queryI = "select * FROM loc_rutas_digitales_preventa WHERE ruta_venta = ?";
                    cursorI = mDataBase.rawQuery(queryI, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH", "")});
                    count = cursorI.getCount();
                    if (count > 0 && visita.getVptyp().equals("ZWE")) {
                        cursorI.moveToFirst();
                        visita.setRuta(cursorI.getString(cursorI.getColumnIndex("ruta_ZWE")));
                    }
                    if (count > 0 && visita.getVptyp().equals("ZWB")) {
                        cursorI.moveToFirst();
                        visita.setRuta(cursorI.getString(cursorI.getColumnIndex("ruta_ZWB")));
                    }
                }
            }
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("kvgr4")) );
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("f_ico")) );
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("f_fco")) );
            visita.setF_ini(cursor.getString(cursor.getColumnIndex("f_ini")) );
            visita.setF_fin(cursor.getString(cursor.getColumnIndex("f_fin")) );
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("fcalid")) );

            if( !(count == 0 && visita.getVptyp().equals("ZRM") && PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA", "").equals("ZPV")) ) {
                visitasList.add(visita);
            }
        }
        cursor.close();
        return  visitasList;
    }

    public boolean EsTipodeReparto(String agencia, String tiporuta)
    {
        String query = "select DISTINCT vwerks, zroute_rep FROM EX_T_RUTAS_VP WHERE bzirk = ? AND vptyp = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{agencia, tiporuta});
        boolean retorno = false;
        if (cursor.moveToNext()){
            retorno = !cursor.getString(cursor.getColumnIndex("vwerks")).isEmpty() && !cursor.getString(cursor.getColumnIndex("vwerks")).trim().equals("") && !cursor.getString(cursor.getColumnIndex("zroute_rep")).trim().equals("");
        }
        cursor.close();
        //Caso exclusivo para tipos de visita autoventa, donde la preventa y el reparto son lo mismo.
        if(tiporuta.contains("ZAT") || tiporuta.contains("ZAH") || tiporuta.contains("ZAI") || tiporuta.contains("ZAN") || tiporuta.contains("ZAP")
        || tiporuta.contains("ZDI") || tiporuta.contains("ZCM") || tiporuta.contains("ZDM") || tiporuta.contains("ZDP") || tiporuta.contains("ZMB") || tiporuta.contains("ZMY")){
            retorno = false;
        }
        return retorno;
    }
    public boolean esTipoAutoventa(String tiporuta)
    {
        boolean retorno = false;
        if(tiporuta.contains("ZAT") || tiporuta.contains("ZAH") || tiporuta.contains("ZAI") || tiporuta.contains("ZAN") || tiporuta.contains("ZAP")
                || tiporuta.contains("ZDI") || tiporuta.contains("ZCM") || tiporuta.contains("ZDM") || tiporuta.contains("ZDP") || tiporuta.contains("ZMB") || tiporuta.contains("ZMY")){
            retorno = true;
        }
        return retorno;
    }
    public boolean ExisteRutaMixta()
    {
        String query = "select * FROM EX_T_RUTAS_VP WHERE vptyp = 'ZRM' and vkorg = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG", "").trim() + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        int count = cursor.getCount();
        boolean retorno = false;
        if (count > 0){
            retorno = true;
        }
        cursor.close();
        return retorno;
    }
    public boolean ExisteTipoVisita(String tipoVisita)
    {
        String query = "select * FROM EX_T_RUTAS_VP WHERE vptyp = '"+tipoVisita+"' and vkorg = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG", "").trim() + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        int count = cursor.getCount();
        boolean retorno = false;
        if (count > 0){
            retorno = true;
        }
        cursor.close();
        return retorno;
    }
    public boolean ExisteEnVisitPlanActual(String modalidad,String tipoVisita)
    {
        String query = "SELECT id FROM cat_ztsdvto_00185_x where vkorg = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG", "").trim()+"' and kvgr5 = '"+modalidad+"' and vpore = '"+tipoVisita+"'";
        Cursor cursor = mDataBase.rawQuery(query,null);
        int count = cursor.getCount();
        boolean retorno = false;
        if (count > 0){
            retorno = true;
        }
        cursor.close();
        return retorno;
    }
    public String RutaRepartoAsociada(String kvgr5, String vpore) {//vkorg,ktokd,name1,street,house_num1,suppl1,suppl3,city1,land1
        Cursor cursor = mDataBase.rawQuery("select vpent FROM cat_ztsdvto_00185_x WHERE kvgr5 = ? AND vkorg = ? AND vpore = ?", new String[]{kvgr5, PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG",""), vpore});
        String valor = "";
        if(cursor.moveToNext()) {
            valor = cursor.getString(cursor.getColumnIndex("vpent"));
        }
        cursor.close();
        return valor;
    }

    public ArrayList<HashMap<String, String>> RutasDigitalesAsociadas() {
        ArrayList<HashMap<String, String>> rutasDigitales = new ArrayList<>();
        Cursor cursor = mDataBase.rawQuery("select ruta_ZWE, ruta_ZWB FROM loc_rutas_digitales_preventa WHERE ruta_venta = ?", new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH","")});
        if(cursor.moveToNext()) {
            HashMap<String,String> lista = new HashMap<>();
            lista.put("ruta_ZWE", cursor.getString(cursor.getColumnIndex("ruta_ZWE")));
            lista.put("ruta_ZWB", cursor.getString(cursor.getColumnIndex("ruta_ZWB")));
            rutasDigitales.add(lista);
        }
        cursor.close();
        return rutasDigitales;
    }

    public String AsignarModalidadSegunAgenciayTipoVisita(String agencia,String tipo_visita) {
        String modalidadVenta = "";
        try {
            Cursor cursor = mDataBase.rawQuery("select case when ind_digital = 0 OR ind_digital IS NULL then mod_venta else mod_venta_digital END as modalidad_venta FROM loc_modvta_x_uo_tipvis WHERE agencia = ? AND tipo_visita = ?", new String[]{agencia, tipo_visita});
            if (cursor.moveToNext()) {
                modalidadVenta = cursor.getString(cursor.getColumnIndex("modalidad_venta"));
            }

            if (modalidadVenta == "") {
                cursor = mDataBase.rawQuery("select case when ind_digital = 0 OR ind_digital IS NULL then mod_venta else mod_venta_digital END as modalidad_venta FROM loc_modvta_x_uo_tipvis WHERE agencia = ? AND tipo_visita = ?", new String[]{"*", tipo_visita});
                if (cursor.moveToNext()) {
                    modalidadVenta = cursor.getString(cursor.getColumnIndex("modalidad_venta"));
                }
            }
            cursor.close();
        }catch(Exception ex){
            modalidadVenta = "";
        }
        return modalidadVenta;
    }

    public boolean EsBloqueObligatorio(String campo)
    {
        String query = "select OBL FROM ConfigCampos WHERE bukrs = ? AND ktokd = ? AND campo = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS",""),PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD","") , campo});
        boolean retorno = false;
        if (cursor.moveToNext()){
            retorno = !cursor.getString(cursor.getColumnIndex("OBL")).isEmpty() && !cursor.getString(cursor.getColumnIndex("OBL")).trim().equals("");
        }
        cursor.close();
        return retorno;
    }

    public ArrayList<HashMap<String, String>> Provincias(String pais)
    {
        ArrayList<HashMap<String, String>> provincias = getDatosCatalogo("cat_t005u", "land1 = '"+pais+"'");
        return provincias;
    }

    public ArrayList<HashMap<String, String>> Cantones(String pais, String provincia)
    {
        String porpais = "";
        switch (pais) {
            case "CR":
                //porpais = " AND SUBSTR(CITY1,1,1) = 'C' OR CITY1 = 'PCCI'";
                porpais = " AND LAND1 = 'CR'";
                break;
            case "NI":
                porpais = " AND (SUBSTR(CITY1,1,1) = 'N' OR (LAND1 = 'NI'))";
                break;
            case "GT":
                porpais = " AND (SUBSTR(CITY1,1,1) NOT IN ('C','P','N','G') OR (LAND1 = 'GT'))";
                break;
            case "PA":
                porpais = " AND (SUBSTR(CITY1,1,1) = 'P' OR (LAND1 = 'PA'))";
                break;
        }
        ArrayList<HashMap<String, String>> cantones = getDatosCatalogo("cat_ztsdvtc_00296", "regio = '"+provincia+"'"+porpais);
        return cantones;
    }

    public ArrayList<HashMap<String, String>> Distritos(String provincia, String canton)
    {
        String sinRelacion = "";
        if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("F446")
        ||PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("1657")
        ||PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("1658")){
            sinRelacion = " OR (region = '' AND city1 = '')";
        }
        ArrayList<HashMap<String, String>> distritos = getDatosCatalogo("cat_ztsdvtc_00297", "(region = '"+provincia+"' AND city1 = '"+canton+"')"+sinRelacion);
        return distritos;
    }

    public ArrayList<HashMap<String, String>> Municipios(String pais, String departamento)
    {
        String adicional = "";
        switch (pais) {
            case "CO":
                adicional = " AND (zdemunicipio <> '')";
                break;
        }
        ArrayList<HashMap<String, String>> municipios = getDatosCatalogo("cat_zesdvt_01044",3,3,null, "regio = '"+departamento+"'"+adicional);
        return municipios;
    }

    public ArrayList<HashMap<String, String>> Barrios(String departamento, String municipio)
    {
        String adicional = " AND (zdebarrio <> '')";
        ArrayList<HashMap<String, String>> barrios = getDatosCatalogo("cat_zesdvt_01044",4,4,null, "regio = '"+departamento+"' AND zdemunicipio = '"+municipio+"'"+adicional);
        return barrios;
    }

    //Campo W_CTE-ZGPOCANAL, con Etiqueta 'Canal'
    public ArrayList<HashMap<String, String>> Canales(String grupo_canal)
    {
        ArrayList<HashMap<String, String>> canales = getDatosCatalogo("cat_ztmdcmc_00036", "ztpocanal = '"+grupo_canal+"'");
        return canales;
    }
    //Campo W_CTE-ZZCANAL0, con Etiqueta 'Canal KOF'
    public ArrayList<HashMap<String, String>> CanalesKOF(String vkorg, String grupo_canal, String canal)
    {
        ArrayList<HashMap<String, String>> distritos = getDatosCatalogo("cat_ztmdcmc_00017", "vkorg = '"+vkorg+"' AND ztpocanal = '"+grupo_canal+"' AND zgpocanal = '"+canal+"'");
        return distritos;
    }


    //Adjuntos x solicitud
    public void addAdjuntoSolicitud( String tipo, String nombre, byte[] imagen) throws SQLiteException {
        ContentValues cv = new  ContentValues();
        cv.put("id_solicitud",   getNextSolicitudId());
        cv.put("tipo",   tipo);
        cv.put("nombre",   nombre);
        cv.put("imagen",   imagen);
        try {
            mDataBase.insertOrThrow(VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD(), null, cv);
        }
        catch(SQLiteException se){
            Toasty.error(mContext, "No se pudo insertar en tabla "+VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD()+". "+se.getMessage()).show();

        }
    }

    //ENCUESTA CANALES
    public ArrayList<HashMap<String, String>> getSubgrupoSegunGrupo(String grupo_isscom){
        ArrayList<HashMap<String, String>> subgruposList = new ArrayList<>();

        String sql_subgrupos = "select DISTINCT s.zid_subgrupo,s.text  from cat_preguntas_isscom p " +
                "INNER JOIN cat_subgrupo_isscom s ON (s.zid_subgrupo = p.zid_subgrupo) " +
                "where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_subgrupos,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_subgrupo",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            subgruposList.add(user);
        }
        cursor.close();
        return  subgruposList;
    }
    public ArrayList<HashMap<String, String>> getPreguntasSegunGrupo(String grupo_isscom){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();

        String sql_encuesta = "select DISTINCT zid_quest,text  from cat_preguntas_isscom p where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_quest",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            preguntasList.add(user);
        }
        cursor.close();
        return  preguntasList;
    }
    public ArrayList<HashMap<String, String>> getPreguntasSegunSubGrupo(String grupo_isscom, String subgrupo_isscom){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();

        String sql_encuesta = "select DISTINCT zid_quest,text  from cat_preguntas_isscom p where trim(p.zid_grupo) = '" + grupo_isscom + "' and trim(p.zid_subgrupo) = '" + subgrupo_isscom + "' and bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_quest",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            preguntasList.add(user);
        }
        cursor.close();
        return  preguntasList;
    }
    public ArrayList<HashMap<String, String>> getOpcionesPreguntaGrupo(String grupo_isscom, String pregunta){
        ArrayList<HashMap<String, String>> respuestasList = new ArrayList<>();
        String filtroPais = " AND spras = 'C'";
        if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("F446") || PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("1657") || PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("1658"))
            filtroPais = " AND spras = 'G'";
        String sql_encuesta = "select DISTINCT zid_resp,text from cat_respuestas_isscom p where trim(zid_grupo) = '"+grupo_isscom.trim()+"' and trim(zid_quest) = '"+pregunta.trim()+"'"+filtroPais;
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_resp",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            respuestasList.add(user);
        }
        cursor.close();
        return  respuestasList;
    }
    public ArrayList<HashMap<String, String>> getOpcionesPreguntaSubGrupo(String grupo_isscom,String subgrupo_isscom, String pregunta){
        ArrayList<HashMap<String, String>> respuestasList = new ArrayList<>();
        String filtroPais = " AND spras = 'C'";
        if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("F446") || PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("1657") || PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").equals("1658"))
            filtroPais = " AND spras = 'G'";
        String sql_encuesta = "select DISTINCT zid_resp,text from cat_respuestas_isscom p where trim(zid_grupo) = '"+grupo_isscom.trim()+"' AND trim(zid_subgrupo) = '"+subgrupo_isscom.trim()+"' and trim(zid_quest) = '"+pregunta.trim()+"'"+filtroPais;
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_resp",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            respuestasList.add(user);
        }
        cursor.close();
        return  respuestasList;
    }

    public HashMap<String,String> getValoresSegunEncuestaRealizada(String... valores) {
        HashMap<String,String> registro_canales = new HashMap<>();
        String tablaOrigen = "cat_ztsdvto_00186";
        if(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","").equals("0446")
        ||PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","").equals("0657")
        ||PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","").equals("0658")){
            tablaOrigen = "cat_ztcmvto_00005";
        }
        StringBuilder sql_encuesta = new StringBuilder("select zid_result from "+tablaOrigen+" p where trim(zid_grupo) = '" + valores[0].trim() + "'");
        for(int x = 1; x < valores.length;x++){
            if(valores[x] != null)
                sql_encuesta.append(" AND zid_quest").append(x).append(" = '").append(valores[x].trim()).append("'");
        }
        Cursor cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
        String idValores;
        cursor.moveToNext();
        idValores = cursor.getString(0).trim();

        sql_encuesta = new StringBuilder("select zzent3,zzent4,zzcanal,ztpocanal,zgpocanal,pson3  from cat_ztsdvto_00187 p where vkorg = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","") + "' AND trim(zid_result) = '" + idValores.trim() + "'");
        cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
        while (cursor.moveToNext()){
            registro_canales.put("W_CTE-ZZENT3",cursor.getString(0).trim());
            registro_canales.put("W_CTE-ZZENT4",cursor.getString(1).trim());
            registro_canales.put("W_CTE-ZZCANAL",cursor.getString(2).trim());
            registro_canales.put("W_CTE-ZTPOCANAL",cursor.getString(3).trim());
            registro_canales.put("W_CTE-ZGPOCANAL",cursor.getString(4).trim());
            registro_canales.put("W_CTE-PSON3",cursor.getString(5).trim());
        }
        cursor.close();
        return registro_canales;
    }
    public HashMap<String,String> getValoresSegunEncuestaRealizadaColombia(String... valores) {
        HashMap<String,String> registro_canales = new HashMap<>();
        String tablaOrigen = "cat_ztsdvto_00186";
        String flitroSubGrupo = "";
        if(valores[1] != null && valores[1] != "")
            flitroSubGrupo = " AND trim(zid_subgrupo) = '" + valores[1].trim() + "'";

        StringBuilder sql_encuesta = new StringBuilder("select zid_result from "+tablaOrigen+" p where trim(zid_grupo) = '" + valores[0].trim() + "'"+flitroSubGrupo);
        for(int x = 2; x < valores.length;x++){
            if(valores[x] != null)
                sql_encuesta.append(" AND zid_quest").append((x-1)).append(" = '").append(valores[x].trim()).append("'");
        }
        Cursor cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
        String idValores;
        cursor.moveToNext();
        idValores = cursor.getString(0).trim();

        sql_encuesta = new StringBuilder("select zzent3,zzent4,zzcanal,ztpocanal,zgpocanal,pson3,unneg,subunneg  from cat_ztsdvto_00187 p where vkorg = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","") + "' AND trim(zid_result) = '" + idValores.trim() + "'");
        cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
        while (cursor.moveToNext()){
            //registro_canales.put("W_CTE-ZZENT3",cursor.getString(0).trim());
            //registro_canales.put("W_CTE-ZZENT4",cursor.getString(1).trim());
            registro_canales.put("W_CTE-ZZCANAL",cursor.getString(cursor.getColumnIndex("zzcanal")).trim());
            registro_canales.put("W_CTE-ZTPOCANAL",cursor.getString(cursor.getColumnIndex("ztpocanal")).trim());
            registro_canales.put("W_CTE-ZGPOCANAL",cursor.getString(cursor.getColumnIndex("zgpocanal")).trim());
            //registro_canales.put("W_CTE-PSON3",cursor.getString(5).trim());
            registro_canales.put("W_CTE-ZZUNNEG",cursor.getString(cursor.getColumnIndex("unneg"))!=null?cursor.getString(cursor.getColumnIndex("unneg")).trim():"");
            registro_canales.put("W_CTE-ZZSUBUNNEG",cursor.getString(cursor.getColumnIndex("subunneg"))!=null?cursor.getString(cursor.getColumnIndex("subunneg")).trim():"");

            //Validar aqui? la ocasion de cosumo dependiendo del tipo de canal y el GEC dependiendo del canal
            // Si no se encuentran en las tablas respectivas se debe ejecutar una encuesta adicional para asignar.

            sql_encuesta = new StringBuilder("SELECT id_oc_consumo FROM cat_loc_oc_consumo_tpocanal p where bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "' AND trim(id_tpocanal) = '" + cursor.getString(cursor.getColumnIndex("ztpocanal")).trim() + "'");
            cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
            while (cursor.moveToNext()) {
                registro_canales.put("W_CTE-ZZOCCONS",cursor.getString(cursor.getColumnIndex("id_oc_consumo")).trim());
            }


            sql_encuesta = new StringBuilder("SELECT zgec FROM cat_zesdvt_00615 p where vkorg = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG","") + "' AND trim(zzcanal) = '" + registro_canales.get("W_CTE-ZZCANAL") + "'");
            cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
            while (cursor.moveToNext()) {
                registro_canales.put("W_CTE-KLABC",cursor.getString(cursor.getColumnIndex("zgec")).trim());
            }
        }
        cursor.close();
        return registro_canales;
    }

    public ArrayList<HashMap<String, String>> getValoresKOFSegunZonaVentas(String bzirk){
        String query = "select vwerks FROM EX_T_RUTAS_VP WHERE bzirk = ? AND vptyp = ?";
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        Cursor micursor = mDataBase.rawQuery(query,new String[]{bzirk,"ZDD"});
        while (micursor.moveToNext()){
            HashMap<String, String> user = new HashMap<>();
            user.put("VWERK", micursor.getString(0).trim());
            preguntasList.add(user);
            break;
        }
        if(preguntasList.size() == 0){
            Cursor micursorZat = mDataBase.rawQuery(query,new String[]{bzirk,"ZAT"});
            while (micursorZat.moveToNext()){
                HashMap<String, String> user = new HashMap<>();
                user.put("VWERK", micursorZat.getString(0).trim());
                preguntasList.add(user);
                break;
            }
        }
        if(preguntasList.size() == 0){//Con el VP de la  ruta sincronizada
            query = "select vwerks FROM EX_T_RUTAS_VP WHERE bzirk = ? AND vptyp = ?";
            Cursor micursorZat = mDataBase.rawQuery(query,new String[]{bzirk,PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_TIPORUTA", "")});
            while (micursorZat.moveToNext()){
                HashMap<String, String> user = new HashMap<>();
                user.put("VWERK", micursorZat.getString(0).trim());
                preguntasList.add(user);
                break;
            }
        }
        // lo mas general posible para no depender de un tipo de visita especifico para la informacion
        if(preguntasList.size() == 0){
            query = "select vwerks FROM EX_T_RUTAS_VP WHERE bzirk = ? AND vwerks IS NOT NULL AND vwerks != ''";
            Cursor micursorZat = mDataBase.rawQuery(query,new String[]{bzirk});
            while (micursorZat.moveToNext()){
                HashMap<String, String> user = new HashMap<>();
                user.put("VWERK", micursorZat.getString(0).trim());
                preguntasList.add(user);
                break;
            }
        }
        micursor.close();
        return  preguntasList;
    }

    public ArrayList<HashMap<String, String>> getValoresSegunCanal(String zcanal){
        String query = "select ztpocanal, zgpocanal, CASE WHEN b.gec IS NULL THEN '52' ELSE b.gec END as gec FROM cat_ztmdcmc_00017 a" +
                " LEFT OUTER JOIN loc_gec_x_canal b ON(a.zcanal = b.zcanal and a.vkorg = b.vkorg)" +
                " WHERE a.zcanal = ? AND a.vkorg = ?";
        ArrayList<HashMap<String, String>> valoresList = new ArrayList<>();
        Cursor micursor = mDataBase.rawQuery(query,new String[]{zcanal,PreferenceManager.getDefaultSharedPreferences(mContext).getString("CONFIG_ORGVENTAS",VariablesGlobales.getOrgvta())});
        while (micursor.moveToNext()){
            HashMap<String, String> user = new HashMap<>();
            user.put("ztpocanal", micursor.getString(0).trim());
            user.put("zgpocanal", micursor.getString(1).trim());
            user.put("gec", micursor.getString(2).trim());
            valoresList.add(user);
            break;
        }
        micursor.close();
        return  valoresList;
    }

    public ArrayList<HashMap<String, String>> getRespuestasEncuesta(String id_solicitud){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        //String sql_encuesta = "select p.zid_quest, p.text as quest_text,r.zid_resp, r.text as resp_text from cat_preguntas_isscom p inner join cat_respuestas_isscom r ON (p.zid_grupo = r.zid_grupo AND p.zid_quest = r.zid_quest) where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()) + "'";
        String sql_encuesta = "select id_Grupo as id_grupo,col1,col2,col3,col4,col5,col6,col7,col8,col9,col10 from encuesta_solicitud where id_solicitud = ?";
        Cursor micursor = mDataBase.rawQuery(sql_encuesta,new String[]{id_solicitud});
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
        micursor.close();
        return  preguntasList;
    }

    //ENCUESTA GEC
    public ArrayList<HashMap<String, String>> getPreguntasGec(){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        String sql_encuesta = "select zid_quest,text,text2,orden  from cat_preguntas_gec p where bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_quest",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            user.put("text2",cursor.getString(2).trim());
            user.put("orden",cursor.getString(3).trim());
            preguntasList.add(user);
        }
        cursor.close();
        return  preguntasList;
    }

    public List<PreguntasEncuesta> getPreguntasEncuesta(String id_encuesta){
        List<PreguntasEncuesta> preguntasList = new ArrayList<>();
        String sql_encuesta = "SELECT 'test' as test,p.id_preguntas_encuesta,p.id_encuesta,e.nombre as nombreEncuesta,e.descripcion as descripcionEncuesta,p.id_bukrs,b.desc_bukrs,p.id_tipo_pregunta,t.tipo,p.texto,p.tooltip,p.orden\n" +
                "  FROM preguntas_encuesta p\n" +
                "  JOIN cat_bukrs b on b.id_bukrs=p.id_bukrs\n" +
                "  JOIN cat_tipo_pregunta t on t.id_tipo_pregunta=p.id_tipo_pregunta\n" +
                "  JOIN encuesta_cabecera e ON e.id_encuesta=p.id_encuesta\n" +
                "  WHERE p.id_encuesta='"+id_encuesta+"' AND p.id_bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'" +
                "ORDER BY p.orden asc";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        MicroOrm uOrm = new MicroOrm();
        preguntasList = uOrm.listFromCursor(cursor, PreguntasEncuesta.class);
        cursor.close();

        for (PreguntasEncuesta preguntasEncuesta : preguntasList) {
            sql_encuesta="SELECT * from opciones_respuestas where id_preguntas_encuesta="+preguntasEncuesta.getId();
            Cursor cursor2 = mDataBase.rawQuery(sql_encuesta,null);
            List<OpcionesRespuesta> opcionesRespuestas= uOrm.listFromCursor(cursor2, OpcionesRespuesta.class);
            preguntasEncuesta.setOpciones(opcionesRespuestas);
            cursor2.close();
        }



        return  preguntasList;
    }
    public Integer getMontoTotalEncuestaGVC(String GUID) {
        Integer monto_total = 0;

        String sql_encuesta = "SELECT SUM(CAST(respuesta as decimal)) as monto_total FROM respuesta_pregunta where GUID='"+GUID+"'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            monto_total = cursor.getInt(0);
        }
        cursor.close();
        return monto_total;
    }

    public String getGecDescripcionSegunEncuestaRealizada(Integer monto_total) {
        String gec = "";

        String sql_encuesta = "select d.zdescripci  from cat_rangos_gec p join cat_ztmdcmc_00001t d on d.zklabc=p.klabc where bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"' AND min <=  "+monto_total+" AND max >="+monto_total+"";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            gec = cursor.getString(0).trim();
        }
        cursor.close();
        return gec;
    }

    public String getGecDescripcionActual(String codigo_cliente) {
        String gec = "";

        String sql_encuesta = "select distinct d.zdescripci from SAPDClientes c join cat_ztmdcmc_00001t d on d.zklabc=c.KLABC where c.KUNNR = '"+codigo_cliente+"'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            gec = cursor.getString(0).trim();
        }
        cursor.close();
        return gec;
    }


    public ArrayList<HashMap<String, String>> getOpcionesXPreguntaGec(String zid_quest){
        ArrayList<HashMap<String, String>> respuestasList = new ArrayList<>();
        String sql_encuesta = "select  zid_resp, text, bukrs  from cat_respuestas_gec p where zid_quest = '"+zid_quest+"' AND bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_resp",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            respuestasList.add(user);
        }
        cursor.close();
        return  respuestasList;
    }

    public String getGecSegunEncuestaRealizada(Integer monto_total) {
        String gec = "";

        String sql_encuesta = "select klabc  from cat_rangos_gec p where bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"' AND min <=  "+monto_total+" AND max >="+monto_total+"";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            gec = cursor.getString(0).trim();
        }
        cursor.close();
        return gec;
    }

    public String getGecSegunEncuestaRealizadaColombia(String... respuestas) {
        String gec = "";

        StringBuilder sql_encuesta = new StringBuilder("select klabc from cat_resultado_gec p where bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"'");

        for(int x = 0; x < respuestas.length;x++){
            if(respuestas[x] != null)
                sql_encuesta.append(" AND zid_quest").append((x+1)).append(" = '").append(respuestas[x].trim()).append("'");
        }
        Cursor cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
        while (cursor.moveToNext()){
            gec = cursor.getString(0).trim();
        }
        cursor.close();
        return gec;
    }

    public ArrayList<HashMap<String, String>> getEncuestaGec(String nextSolicitudId) {
        ArrayList<HashMap<String, String>> respuestasEncuestaGec = new ArrayList<>();
        String sql_encuesta = "select zid_quest, monto from encuesta_gec_solicitud p where id_solicitud = '" + nextSolicitudId + "'";
        //String sql_encuesta2 = "select zid_quest, monto from encuesta_gec_solicitud p";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        //Cursor cursor2 = mDataBase.rawQuery(sql_encuesta2,null);
        while (cursor.moveToNext()){
            HashMap<String,String> resp = new HashMap<>();
            resp.put("zid_quest",cursor.getString(0).trim());
            resp.put("monto",cursor.getString(1).trim());
            respuestasEncuestaGec.add(resp);
        }
        cursor.close();
        return  respuestasEncuestaGec;
    }

    //ENCUESTA OCASION CONSUMO COLOMBIA
    public ArrayList<HashMap<String, String>> getPreguntasOcasionConsumo(){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        String sql_encuesta = "select zid_quest,text,text2  from cat_preguntas_occons p where bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_quest",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            user.put("text2",cursor.getString(2).trim());
            preguntasList.add(user);
        }
        cursor.close();
        return  preguntasList;
    }
    public ArrayList<HashMap<String, String>> getOpcionesXPreguntaOcasionConsumo(String zid_quest){
        ArrayList<HashMap<String, String>> respuestasList = new ArrayList<>();
        String sql_encuesta = "select  zid_resp, text, bukrs  from cat_respuestas_occons p where zid_quest = '"+zid_quest+"' AND bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("zid_resp",cursor.getString(0).trim());
            user.put("text",cursor.getString(1).trim());
            respuestasList.add(user);
        }
        cursor.close();
        return  respuestasList;
    }
    public String getOcasionConsumoSegunEncuestaRealizada(String... respuestas) {
        String occons = "";

        StringBuilder sql_encuesta = new StringBuilder("select occons from cat_resultado_occons p where bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"'");

        for(int x = 0; x < respuestas.length;x++){
            if(respuestas[x] != null)
                sql_encuesta.append(" AND zid_quest").append((x+1)).append(" = '").append(respuestas[x].trim()).append("'");
        }
        Cursor cursor = mDataBase.rawQuery(sql_encuesta.toString(),null);
        while (cursor.moveToNext()){
            occons = cursor.getString(0).trim();
        }
        cursor.close();
        return occons;
    }
    public ArrayList<HashMap<String, String>> getEncuestaOcasionConsumo(String nextSolicitudId) {
        ArrayList<HashMap<String, String>> respuestasEncuestaOcasionConsumo = new ArrayList<>();
        String sql_encuesta = "select zid_quest, respuesta_obtenida from encuesta_occons_solicitud p where id_solicitud = '" + nextSolicitudId + "'";
        //String sql_encuesta2 = "select zid_quest, monto from encuesta_gec_solicitud p";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        //Cursor cursor2 = mDataBase.rawQuery(sql_encuesta2,null);
        while (cursor.moveToNext()){
            HashMap<String,String> resp = new HashMap<>();
            resp.put("zid_quest",cursor.getString(0).trim());
            resp.put("respuesta_obtenida",cursor.getString(1).trim());
            respuestasEncuestaOcasionConsumo.add(resp);
        }
        cursor.close();
        return  respuestasEncuestaOcasionConsumo;
    }

    public ArrayList<HashMap<String, String>> getConfigExcepciones(String tiposolicitud) {
        ArrayList<HashMap<String, String>> excepciones = new ArrayList<>();
        String sql_encuesta = "SELECT * FROM ConfigExcepciones WHERE (bukrs = ? OR bukrs = '*') AND (ktokd = ? OR ktokd = '*') AND (tipform = ? OR tipform = 0)";
        //String sql_encuesta2 = "select zid_quest, monto from encuesta_gec_solicitud p";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS",""),
                PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD","RCMA"),
                tiposolicitud});
        //bukrs, ktokd, tipform, campo, VIS, OBL, OPC, SUP
        while (cursor.moveToNext()){
            HashMap<String,String> resp = new HashMap<>();

            resp.put("bukrs",cursor.getString(cursor.getColumnIndex("bukrs")) != null ? cursor.getString(cursor.getColumnIndex("bukrs")).trim() : "NULL");
            resp.put("ktokd",cursor.getString(cursor.getColumnIndex("ktokd")) != null ? cursor.getString(cursor.getColumnIndex("ktokd")).trim() : "NULL");
            resp.put("tipform",cursor.getString(cursor.getColumnIndex("tipform")) != null ? cursor.getString(cursor.getColumnIndex("tipform")) .trim(): "NULL");
            resp.put("campo",cursor.getString(cursor.getColumnIndex("campo")) != null ? cursor.getString(cursor.getColumnIndex("campo")).trim() : "NULL");
            resp.put("bzirk",cursor.getString(cursor.getColumnIndex("bzirk")) != null ? cursor.getString(cursor.getColumnIndex("bzirk")).trim() : "NULL");
            resp.put("vis",cursor.getString(cursor.getColumnIndex("VIS")) != null ? cursor.getString(cursor.getColumnIndex("VIS")).trim() : "NULL");
            resp.put("obl",cursor.getString(cursor.getColumnIndex("OBL")) != null ? cursor.getString(cursor.getColumnIndex("OBL")).trim() : "NULL");
            resp.put("opc",cursor.getString(cursor.getColumnIndex("OPC")) != null ? cursor.getString(cursor.getColumnIndex("OPC")).trim() : "NULL");
            resp.put("sup",cursor.getString(cursor.getColumnIndex("SUP")) != null ? cursor.getString(cursor.getColumnIndex("SUP")).trim() : "NULL");
            resp.put("dfaul",cursor.getString(cursor.getColumnIndex("DFAUL")) != null ? cursor.getString(cursor.getColumnIndex("DFAUL")).trim() : "NULL");
            resp.put("tabla",cursor.getString(cursor.getColumnIndex("TABLA")) != null ? cursor.getString(cursor.getColumnIndex("TABLA")).trim() : "NULL");
            if(cursor.getColumnIndex("DESCR") != -1)
            resp.put("descr",cursor.getString(cursor.getColumnIndex("DESCR")) != null ? cursor.getString(cursor.getColumnIndex("DESCR")).trim() : "NULL");
            excepciones.add(resp);
        }
        cursor.close();
        return  excepciones;
    }

    public ArrayList<Comentario> getComentariosDB(String idform) {
        if(idform == null){
            idform = "0";
        }
        ArrayList<Comentario> comentarios = new ArrayList<>();
        String sql_encuesta = "select 0 as orden, 'Creacion' as etapa," +
                "ususol|| ' - ' || nom_sol as aprobador,feccre as fecha,comentario_sol as comentarios, estado as estado " +
                "FROM VistaFlujos WHERE idform = ?" +
                " UNION " +
                "select Orden as orden, cast(id_etapa as varchar) || ' - ' ||nom_etapa as etapa," +
                "siguienteAprobador|| ' - ' || nom_aprob  as aprobador,fechaIngreso as fecha,comentario_aprob as comentarios , estado as estado " +
                "FROM VistaFlujos " +
                "WHERE idform = ? ORDER by orden asc";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,new String[]{idform,idform});
        while (cursor.moveToNext()){
            Comentario comentario = new Comentario();
            comentario.setId_formulario(idform);
            comentario.setOrden(cursor.getString(cursor.getColumnIndex("orden")) );
            comentario.setEtapa(cursor.getString(cursor.getColumnIndex("etapa")) );
            comentario.setAprobador(cursor.getString(cursor.getColumnIndex("aprobador")) );
            comentario.setFecha(cursor.getString(cursor.getColumnIndex("fecha")) );
            comentario.setComentario(cursor.getString(cursor.getColumnIndex("comentarios")) );
            comentario.setEstado(cursor.getString(cursor.getColumnIndex("estado")) );
            comentarios.add(comentario);
        }
        cursor.close();
        return  comentarios;
    }

    // Metodos de Ayuda para la Transmision de Datos y evitar que se vayan duplicados o que no se vayan
    public void ActualizarEstadosSolicitudesTransmitidas(){
        //SQLiteDatabase db = this.getWritableDatabase();
        String sqlUpdate = "UPDATE FormHvKof_solicitud SET estado = 'Transmitido' WHERE id_solicitud IN (SELECT id_solicitud FROM FormHvKof_solicitud WHERE trim(estado) IN ('Nuevo','Modificado'));";
        mDataBase.execSQL(sqlUpdate);
        String sqlUpdateOld = "UPDATE FormHvKof_old_solicitud SET estado = 'Transmitido' WHERE id_solicitud IN (SELECT id_solicitud FROM FormHvKof_old_solicitud WHERE trim(estado) IN ('Nuevo','Modificado'));";
        mDataBase.execSQL(sqlUpdateOld);
    }
    public void ActualizarEstadosSolicitudesTransmitidas(String lista_id_solicitudes){
        //SQLiteDatabase db = this.getWritableDatabase();
        String sqlUpdate = "UPDATE FormHvKof_solicitud SET estado = 'Pendiente' WHERE id_solicitud IN ("+lista_id_solicitudes+");";
        mDataBase.execSQL(sqlUpdate);
        String sqlUpdateOld = "UPDATE FormHvKof_old_solicitud SET estado = 'Pendiente' WHERE id_solicitud IN ("+lista_id_solicitudes+");";
        mDataBase.execSQL(sqlUpdateOld);
    }
    public void ActualizarEstadosCensosTransmitidos(){
        //SQLiteDatabase db = this.getWritableDatabase();
        try {
            String sqlUpdate = "UPDATE CensoEquipoFrio SET transmitido = 1;";
            mDataBase.execSQL(sqlUpdate);
        }catch(Exception e){
            Log.w("UPDATE",e.getMessage());
        }
    }
    //Solo para debugging
    public void RestaurarEstadosSolicitudesTransmitidas(){
        //SQLiteDatabase db = this.getWritableDatabase();
        String sqlUpdate = "UPDATE FormHvKof_solicitud SET estado = 'Nuevo' WHERE id_solicitud IN (SELECT id_solicitud FROM FormHvKof_solicitud WHERE trim(estado) IN ('Modificado'));";
        mDataBase.execSQL(sqlUpdate);
        String sqlUpdateOld = "UPDATE FormHvKof_old_solicitud SET estado = 'Nuevo' WHERE id_solicitud IN (SELECT id_solicitud FROM FormHvKof_old_solicitud WHERE trim(estado) IN ('Modificado'));";
        mDataBase.execSQL(sqlUpdateOld);
    }
    public int CantidadAdjuntosMinima(String idform) {
        int cantidad = 0;
        Cursor cursor = null;
        String sql_encuesta = "";
        try {
            sql_encuesta = "select min_adjuntos_hh as cantidad from flujo WHERE id_form = ?";
            cursor = mDataBase.rawQuery(sql_encuesta,new String[]{idform});
            while (cursor.moveToNext()){
                cantidad = cursor.getInt(0);
            }
        }catch(Exception e){
            sql_encuesta = "select min_adjuntos as cantidad from flujo WHERE id_form = ?";
            cursor = mDataBase.rawQuery(sql_encuesta,new String[]{idform});
            while (cursor.moveToNext()){
                cantidad = cursor.getInt(0);
            }
        }finally{
            if(cursor != null)
                cursor.close();
        }
        return cantidad;
    }
    public int CantidadSolicitudesTransmision() {
        int cantidad = 0;

        String sql_encuesta = "select count(*) as cantidad  from FormHvKof_solicitud where trim(estado) IN ('Nuevo','Modificado')";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        return cantidad;
    }
    public int CantidadCensosTransmision() {
        int cantidad = 0;
        try {
            String sql_censo = "select count(*) as cantidad  from CensoEquipoFrio where transmitido = 0 or transmitido IS NULL or transmitido = null";
            Cursor cursor = mDataBase.rawQuery(sql_censo, null);
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
           //Toasty.warning(mContext,"Información de Censo de Equipo frio No disponible").show();
        }
        return cantidad;
    }
    public int CantidadSolicitudesTotal() {
        int cantidad = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from FormHvKof_solicitud";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }
    public int CantidadSolicitudes(String estado) {
        int cantidad = 0;
        try {

            String sql_encuesta = "select count(*) as cantidad  from FormHvKof_solicitud where trim(estado) = ?";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{estado});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }

    public void CambiarEstadoSolicitud(String lista_id_solicitudes,String estado){
        String sqlUpdate = "UPDATE FormHvKof_solicitud SET estado = '"+estado.trim()+"' WHERE id_solicitud IN ('"+lista_id_solicitudes+"');";
        mDataBase.execSQL(sqlUpdate);
        sqlUpdate = "UPDATE FormHvKof_old_solicitud SET estado = '"+estado.trim()+"' WHERE id_solicitud IN ('"+lista_id_solicitudes+"');";
        mDataBase.execSQL(sqlUpdate);
    }
    public void CambiarEstadoSolicitudPorIdform(String idform,String estado){
        String sqlUpdate = "UPDATE FormHvKof_solicitud SET estado = '"+estado.trim()+"' WHERE idform IN ('"+idform+"');";
        mDataBase.execSQL(sqlUpdate);
        sqlUpdate = "UPDATE FormHvKof_old_solicitud SET estado = '"+estado.trim()+"' WHERE idform IN ('"+idform+"');";
        mDataBase.execSQL(sqlUpdate);
    }
    public void EliminarSolicitud(String id_solicitud){
        try {
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_BANCO_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_VISITA_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTablaEncuestaGecSolicitud(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTablaEncuestaSolicitud(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete("FormHvKof_solicitud", "id_solicitud=?", new String[]{id_solicitud});
            //tablas Old si eixsten
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_CONTACTO_OLD_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_BANCO_OLD_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_IMPUESTO_OLD_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_INTERLOCUTOR_OLD_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete(VariablesGlobales.getTABLA_BLOQUE_VISITA_OLD_HH(), "id_solicitud=?", new String[]{id_solicitud});
            mDataBase.delete("FormHvKof_old_solicitud", "id_solicitud=?", new String[]{id_solicitud});
        }catch(Exception e){
            Toasty.warning(mContext,"Error al eliminar solicitud. "+e.getMessage()).show();
        }

    }

    public Integer ConfiguracionxSociedad(String parametro) {
        Integer param = 0;
        try {
            String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
            String query = "SELECT " + parametro + " FROM cat_bukrs WHERE id_bukrs = ?";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{sociedad});
            if (cursor.moveToNext()) {
                param = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            //Toasty.warning(mContext, "Error al traer configuraciones de sociedad" + e.getMessage()).show();
        }
        return param;
    }

    public Integer MaximoAlertas(String bukrs){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT num_max_alertas FROM cat_bukrs WHERE id_bukrs = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{bukrs});
        Integer max_alertas = 0;
        if (cursor.moveToNext()){
            max_alertas = cursor.getInt(0);
        }
        cursor.close();
        return max_alertas;
    }
    public String vkorgToLand1(String vkorg){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT land1 FROM cat_bukrs WHERE vkorg = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{vkorg});
        String land1 = "";
        if (cursor.moveToNext()){
            land1 = cursor.getString(0);
        }
        cursor.close();
        return land1;
    }
    public String vkorgToBukrs(String vkorg){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT id_bukrs FROM cat_bukrs WHERE vkorg = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{vkorg});
        String bukrs = "";
        if (cursor.moveToNext()){
            bukrs = cursor.getString(0);
        }
        cursor.close();
        return bukrs;
    }

    public String getUserName(String usuarioMC) {
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT nombre_Usuario as userName FROM mant_usuarios WHERE upper(trim(id_Usuario)) = upper(?)";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{usuarioMC});
        String userName = "";
        if (cursor.moveToNext()){
            userName = cursor.getString(0);
        }
        cursor.close();
        return userName;
    }

    public String getDescripcionSolicitud(String tipoSolicitud) {
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT Descripcion FROM flujo WHERE id_form = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{tipoSolicitud});
        String descripcion = "";
        if (cursor.moveToNext()){
            descripcion = cursor.getString(0);
        }
        cursor.close();
        return descripcion;
    }

    public String getModeloSolicitud(String tipoSolicitud) {
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT ind_modelo FROM flujo WHERE id_form = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{tipoSolicitud});
        String modelo = "";
        if (cursor.moveToNext()){
            modelo = cursor.getString(0);
        }
        cursor.close();
        return modelo;
    }

    public String AlgoritmoNSEP(String zzent4){
        String valorNSEPAlgoritmo = "";
        //Revisar que tipo de canal es ON o OFF
        String query = "select tip_zzent4 FROM cat_ztsdvtc_00288 WHERE zzent4 = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{zzent4});
        String OnOff = "";
        if (cursor.moveToNext()){
            OnOff = cursor.getString(0);
        }
        cursor.close();
        if (OnOff != null)
        {
            if (OnOff.trim().equals("ON"))
            {
                //Cayo en canal ON, se trae el NSEP segun el canal pais asignado
                query = "select katr4 FROM loc_algoritmo_on WHERE trim(zzent4) = ?";
                cursor = mDataBase.rawQuery(query, new String[]{zzent4});
                if (cursor.moveToNext()){
                    valorNSEPAlgoritmo = cursor.getString(0);
                }
                return valorNSEPAlgoritmo;
            }
            else if (OnOff.trim().equals("OFF")) {
                query = "select katr4 FROM loc_algoritmo_off WHERE trim(katr4_ant) = '' AND trim(route_pr) = ?";
                cursor = mDataBase.rawQuery(query, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH","")});
                if (cursor.moveToNext()){
                    valorNSEPAlgoritmo = cursor.getString(0);
                }else {
                    //Por ultimo SIEMPRE, se busca el piso del algoritmo(SIN RUTA Y SIN ANTERIOR) Valor por defecto para canales OFF, le cae encima a cualquier otro algoritmo, se debe quitar si se quiere utilizar el algoritmo de cliente anterior
                    query = "select katr4 FROM loc_algoritmo_off WHERE trim(katr4_ant) = '' AND trim(route_pr) = '' AND trim(vkorg) = ?";
                    cursor = mDataBase.rawQuery(query, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VKORG", "")});
                    if (cursor.moveToNext()) {
                        valorNSEPAlgoritmo = cursor.getString(0);
                    }
                }
            }
        }
        return valorNSEPAlgoritmo;
    }


    public ArrayList<HashMap<String,String>> CausasDeGrupo(String grupo, String clase_aviso) {
        String masFiltros = "";
        switch(clase_aviso){
            case "T1":
                break;
            case "T2":
            case "T3":
            case "T5":
                masFiltros = " AND KURZTEXT NOT LIKE '%INSTAL%'";
                break;
            case "T4":
                masFiltros = " AND KURZTEXT LIKE '%INSTAL%'";
                break;
            case "T9":
                break;
        }
        ArrayList<HashMap<String, String>> causas = getDatosCatalogo("cat_ef_causas", 3,4,null, "CODEGRUPPE = '"+grupo+"'"+masFiltros);
        return causas;
    }
    public ArrayList<HashMap<String,String>> SintomasDeGrupo(String grupo, String clase_aviso) {
        String masFiltros = "";
        switch(clase_aviso){
            case "T1":
                break;
            case "T2":
            case "T3":
            case "T5":
                masFiltros = " AND KURZTEXT NOT LIKE '%INSTAL%'";
                break;
            case "T4":
                masFiltros = " AND KURZTEXT LIKE '%INSTAL%'";
                break;
            case "T9":
                break;
            default:
        }
        ArrayList<HashMap<String, String>> sintomas = getDatosCatalogo("cat_ef_causas", 3,4,null, "CODEGRUPPE = '"+grupo+"'"+masFiltros);
        return sintomas;
    }

    public String removeLeadingZeroes(String str) {
        String strPattern = "^0+(?!$)";
        str = str.replaceAll(strPattern, "");
        return str;
    }

    public ArrayList<HashMap<String, String>> getNotificaciones(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String query = "SELECT * FROM notificaciones";
        try {
            Cursor cursor = mDataBase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                HashMap<String, String> user = new HashMap<>();
                user.put("id", cursor.getString(cursor.getColumnIndex("id")) != null ? cursor.getString(cursor.getColumnIndex("id")) : "");
                user.put("titulo", cursor.getString(cursor.getColumnIndex("titulo")) != null ? cursor.getString(cursor.getColumnIndex("titulo")) : "");
                user.put("mensaje", cursor.getString(cursor.getColumnIndex("mensaje")) != null ? cursor.getString(cursor.getColumnIndex("mensaje")) : "");
                user.put("bukrs", cursor.getString(cursor.getColumnIndex("bukrs")) != null ? cursor.getString(cursor.getColumnIndex("bukrs")) : "");
                user.put("bzirk", cursor.getString(cursor.getColumnIndex("bzirk")) != null ? cursor.getString(cursor.getColumnIndex("bzirk")) : "");
                user.put("estado", cursor.getString(cursor.getColumnIndex("estado")) != null ? cursor.getString(cursor.getColumnIndex("estado")) : "");
                user.put("version", cursor.getString(cursor.getColumnIndex("version")) != null ? cursor.getString(cursor.getColumnIndex("version")) : "");
                clientList.add(user);
            }
            cursor.close();
        }catch(Exception e){

        }
        return  clientList;
    }

    public String getIdFlujoDeTipoSolicitud(String tipoSolicitud) {
        String id_flujo = "";
        //Revisar que tipo de canal es ON o OFF
        String query = "select id_flujo FROM flujo WHERE id_form = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{tipoSolicitud});
        if (cursor.moveToNext()){
            id_flujo = cursor.getString(0);
        }
        cursor.close();
        return id_flujo;
    }
    public String ClaseRiesgoSegunCondicionPago(String bukrs, String condpago)
    {
        String clase_riesgo = "";
        //Trae la Clase de riesgo segun la condicion de pago seleccionada por sociedad
        String query = "SELECT claseriesgo from cat_rel_condpago_claseriesgo where bukrs = ? AND condpago = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{bukrs, condpago});
        if (cursor.moveToNext()){
            clase_riesgo = cursor.getString(0);
        }
        cursor.close();
        return clase_riesgo;
    }
    public String CondicionExpedicionSegunRutaReparto(String vkorg ,String zroute_rep)
    {
        //Revisar si existe relacion en la tabla loc_ruta_expedicion para esta ruta
        String cond_expedicion = "";
        String res = "";
        //Trae la Clase de riesgo segun la condicion de pago seleccionada por sociedad
        String query = "select vsbed FROM loc_ruta_expedicion WHERE vkorg = ? AND zroute_rep = ?";
        Cursor cursor = null;
        try {
            cursor = mDataBase.rawQuery(query, new String[]{vkorg, zroute_rep});

            if (cursor.moveToNext()) {
                cond_expedicion = cursor.getString(0);
            }
            cursor.close();
        }catch(Exception e){
            Log.d("SELECT FAIL", e.getMessage());
        }
        if (cond_expedicion != null && !cond_expedicion.equals("") )
        {
            res = cond_expedicion;
        }
        else {
            query = "SELECT [DFAUL] FROM ConfigCampos WHERE CAMPO = 'W_CTE-VSBED' AND KTOKD = ? AND BUKRS = ?";
            cursor = mDataBase.rawQuery(query, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD", ""), PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "")});
            if (cursor.moveToNext()){
                cond_expedicion = cursor.getString(0);
            }
            cursor.close();

            if (cond_expedicion != null && !cond_expedicion.equals("") )
            {
                res = cond_expedicion;
            }
        }
        return res;
    }
    public String getTipoCambio() {//vkorg,ktokd,name1,street,house_num1,suppl1,suppl3,city1,land1
        String valor = "";
        try {
            Cursor cursor = mDataBase.rawQuery("select tipo_cambio FROM tipoCambio WHERE pkPais = ?", new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "")});
            if (cursor.moveToNext()) {
                valor = cursor.getString(cursor.getColumnIndex("tipo_cambio"));
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener el tipo de cambio a dolares.").show();
            return "0.00";
        }
        return valor;
    }

    public ArrayList<HashMap<String, String>> ExcepcionValorDefaultxAgencia(String agencia, String formulario, String campo)
    {
        ArrayList<HashMap<String, String>> excepcionCampoList = new ArrayList<>();
        String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
        String ktokd = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD", "");
        String query = "SELECT id, bukrs, ktokd, tipform, bzirk, rtrim(campo) as campo, VIS, OBL, OPC, SUP, rtrim(DFAUL) as DFAUL,TABLA, DESCR FROM ConfigExcepciones WHERE (bukrs = '" + sociedad + "') AND (ktokd = '" + ktokd + "') AND (campo = '" + campo + "') AND (bzirk = '" + agencia + "') AND tipform = '" + formulario + "'";

        try {
            Cursor cursor = mDataBase.rawQuery(query, null);
            while (cursor.moveToNext()) {
                HashMap<String, String> excepcionCampo = new HashMap<>();
                excepcionCampo.put("id", cursor.getString(cursor.getColumnIndex("id")) != null ? cursor.getString(cursor.getColumnIndex("id")) : "");
                excepcionCampo.put("bukrs", cursor.getString(cursor.getColumnIndex("bukrs")) != null ? cursor.getString(cursor.getColumnIndex("bukrs")) : "");
                excepcionCampo.put("ktokd", cursor.getString(cursor.getColumnIndex("ktokd")) != null ? cursor.getString(cursor.getColumnIndex("ktokd")) : "");
                excepcionCampo.put("tipform", cursor.getString(cursor.getColumnIndex("tipform")) != null ? cursor.getString(cursor.getColumnIndex("tipform")) : "");
                excepcionCampo.put("bzirk", cursor.getString(cursor.getColumnIndex("bzirk")) != null ? cursor.getString(cursor.getColumnIndex("bzirk")) : "");
                excepcionCampo.put("campo", cursor.getString(cursor.getColumnIndex("campo")) != null ? cursor.getString(cursor.getColumnIndex("campo")) : "");
                excepcionCampo.put("VIS", cursor.getString(cursor.getColumnIndex("VIS")) != null ? cursor.getString(cursor.getColumnIndex("VIS")) : "");
                excepcionCampo.put("OBL", cursor.getString(cursor.getColumnIndex("OBL")) != null ? cursor.getString(cursor.getColumnIndex("OBL")) : "");
                excepcionCampo.put("OPC", cursor.getString(cursor.getColumnIndex("OPC")) != null ? cursor.getString(cursor.getColumnIndex("OPC")) : "");
                excepcionCampo.put("SUP", cursor.getString(cursor.getColumnIndex("SUP")) != null ? cursor.getString(cursor.getColumnIndex("SUP")) : "");
                excepcionCampo.put("DFAUL", cursor.getString(cursor.getColumnIndex("DFAUL")) != null ? cursor.getString(cursor.getColumnIndex("DFAUL")) : "");
                excepcionCampo.put("TABLA", cursor.getString(cursor.getColumnIndex("TABLA")) != null ? cursor.getString(cursor.getColumnIndex("TABLA")) : "");
                excepcionCampo.put("DESCR", cursor.getString(cursor.getColumnIndex("DESCR")) != null ? cursor.getString(cursor.getColumnIndex("DESCR")) : "");
                excepcionCampoList.add(excepcionCampo);
            }
            cursor.close();
        }catch(Exception e){

        }
        if(excepcionCampoList.size() == 0){
                query = "SELECT bukrs, ktokd, rtrim(campo) as campo, MAX(VIS) as VIS, MAX(OBL) as OBL, MAX(OPC) as OPC, MAX(SUP) as SUP, MAX(rtrim(DFAUL)) as DFAUL FROM ConfigCampos WHERE (bukrs = '" + sociedad + "') AND (ktokd = '" + ktokd + "') AND (campo = '" + campo + "') group BY bukrs, ktokd, rtrim(campo)";
            try {
                Cursor cursor = mDataBase.rawQuery(query, null);
                while (cursor.moveToNext()) {
                    HashMap<String, String> excepcionCampo = new HashMap<>();
                    excepcionCampo.put("id", cursor.getString(cursor.getColumnIndex("id")) != null ? cursor.getString(cursor.getColumnIndex("id")) : "");
                    excepcionCampo.put("bukrs", cursor.getString(cursor.getColumnIndex("bukrs")) != null ? cursor.getString(cursor.getColumnIndex("bukrs")) : "");
                    excepcionCampo.put("ktokd", cursor.getString(cursor.getColumnIndex("ktokd")) != null ? cursor.getString(cursor.getColumnIndex("ktokd")) : "");
                    excepcionCampo.put("tipform", cursor.getString(cursor.getColumnIndex("tipform")) != null ? cursor.getString(cursor.getColumnIndex("tipform")) : "");
                    excepcionCampo.put("bzirk", cursor.getString(cursor.getColumnIndex("bzirk")) != null ? cursor.getString(cursor.getColumnIndex("bzirk")) : "");
                    excepcionCampo.put("campo", cursor.getString(cursor.getColumnIndex("campo")) != null ? cursor.getString(cursor.getColumnIndex("campo")) : "");
                    excepcionCampo.put("VIS", cursor.getString(cursor.getColumnIndex("VIS")) != null ? cursor.getString(cursor.getColumnIndex("VIS")) : "");
                    excepcionCampo.put("OBL", cursor.getString(cursor.getColumnIndex("OBL")) != null ? cursor.getString(cursor.getColumnIndex("OBL")) : "");
                    excepcionCampo.put("OPC", cursor.getString(cursor.getColumnIndex("OPC")) != null ? cursor.getString(cursor.getColumnIndex("OPC")) : "");
                    excepcionCampo.put("SUP", cursor.getString(cursor.getColumnIndex("SUP")) != null ? cursor.getString(cursor.getColumnIndex("SUP")) : "");
                    excepcionCampo.put("DFAUL", cursor.getString(cursor.getColumnIndex("DFAUL")) != null ? cursor.getString(cursor.getColumnIndex("DFAUL")) : "");
                    excepcionCampoList.add(excepcionCampo);
                }
                cursor.close();
            }catch(Exception e){

            }

        }
        return  excepcionCampoList;
    }

    public boolean ExistenIniciativas() {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM flujo WHERE ind_modelo = ? and activo = 1", new String[]{"L"});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener formularios de Iniciativa").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }
    public boolean ExistenFormulariosCredito() {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM flujo WHERE ind_credito = ? and activo = 1", new String[]{"1"});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener formularios de Credito").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }
    public boolean ExistenFormulariosEquipoFrio() {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM flujo WHERE ind_modelo = ? and activo = 1", new String[]{"E"});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener formularios de Equipo Frio").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }
    public boolean ExistenPresolicitudes() {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM flujo WHERE ind_modelo = ? and activo = 1", new String[]{"P"});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener formularios de Equipo Frio").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }
    public boolean ExistenFormulariosRacks() {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM flujo WHERE ind_modelo = ? and activo = 1", new String[]{"W"});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener formularios de Racks").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }
    public ArrayList<EquipoFrio> getCensoEquiposFriosDB(String id_cliente){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<EquipoFrio> equiposFriosList = new ArrayList<>();
        String query = "SELECT DISTINCT b.*, c.*,(SELECT ROUND(julianday('now') - julianday(c2.fecha_lectura)) FROM CensoEquipoFrio c2 WHERE c2.num_equipo = b.EQUNR ORDER BY c2.fecha_lectura DESC LIMIT 1) AS ultima_lectura FROM sapDBaseInstalada b " +
                "LEFT JOIN CensoEquipoFrio c ON (b.kunnr = ? AND c.activo = 1 AND b.serge = c.num_placa) " +
                "and c.fecha_lectura = (SELECT MAX(c2.fecha_lectura) FROM CensoEquipoFrio c2 WHERE c2.kunnr_censo = ? AND c2.activo = 1 AND c2.num_placa = c.num_placa)" +
                "WHERE b.KUNNR = ?" +
                " UNION "+
                "SELECT DISTINCT b.*, c.*, (SELECT ROUND(julianday('now') - julianday(c2.fecha_lectura)) FROM CensoEquipoFrio c2 WHERE c2.num_equipo = b.EQUNR ORDER BY c2.fecha_lectura DESC LIMIT 1) AS ultima_lectura FROM CensoEquipoFrio c " +
                "LEFT JOIN sapDBaseInstalada AS b ON (b.serge IS NULL AND c.kunnr_censo = ? AND c.activo = 1) " +
                "WHERE c.kunnr_censo = ? AND c.activo = 1 AND c.num_placa NOT IN (SELECT serge FROM sapDBaseInstalada s WHERE s.kunnr = ?) " +
                "and c.fecha_lectura = (SELECT MAX(c2.fecha_lectura) FROM CensoEquipoFrio c2 WHERE c2.kunnr_censo = ? AND c2.activo = 1 AND c2.num_placa = c.num_placa) ";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_cliente,id_cliente,id_cliente,id_cliente,id_cliente,id_cliente,id_cliente});

        while (cursor.moveToNext()){
            EquipoFrio ef = new EquipoFrio();
            if(cursor.getString(cursor.getColumnIndex("id")) !=  null)
                ef.setId(cursor.getString(cursor.getColumnIndex("id")).trim());
            if(cursor.getString(cursor.getColumnIndex("KDGRP")) !=  null)
                ef.setKdgrp(cursor.getString(cursor.getColumnIndex("KDGRP")).trim());
            if(cursor.getString(cursor.getColumnIndex("BZIRK")) !=  null)
                ef.setBzirk(cursor.getString(cursor.getColumnIndex("BZIRK")).trim());
            if(cursor.getString(cursor.getColumnIndex("KUNNR")) !=  null)
                ef.setKunnr(cursor.getString(cursor.getColumnIndex("KUNNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("kunnr_censo")) !=  null)
                ef.setKunnrCenso(cursor.getString(cursor.getColumnIndex("kunnr_censo")).trim());
            if(cursor.getString(cursor.getColumnIndex("IBASE")) !=  null)
                ef.setIbase(removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("IBASE")).trim()));
            if(cursor.getString(cursor.getColumnIndex("INSTANCE")) !=  null)
                ef.setInstance(cursor.getString(cursor.getColumnIndex("INSTANCE")).trim());
            if(cursor.getString(cursor.getColumnIndex("OBJECTTYP")) !=  null)
                ef.setObjecttyp(cursor.getString(cursor.getColumnIndex("OBJECTTYP")).trim());
            if(cursor.getString(cursor.getColumnIndex("OBJNR")) !=  null)
                ef.setObjnr(cursor.getString(cursor.getColumnIndex("OBJNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("EQUNR")) !=  null)
                ef.setEqunr(cursor.getString(cursor.getColumnIndex("EQUNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("MATNR")) !=  null)
                ef.setMatnr(cursor.getString(cursor.getColumnIndex("MATNR")).trim());
            if(cursor.getString(cursor.getColumnIndex("EQART")) !=  null)
                ef.setEqart(cursor.getString(cursor.getColumnIndex("EQART")).trim());
            if(cursor.getString(cursor.getColumnIndex("HERST")) !=  null)
                ef.setHerst(cursor.getString(cursor.getColumnIndex("HERST")).trim());
            if(cursor.getString(cursor.getColumnIndex("EQKTX")) !=  null)
                ef.setEqktx(cursor.getString(cursor.getColumnIndex("EQKTX")).trim());
            if(cursor.getString(cursor.getColumnIndex("SPRAS")) !=  null)
                ef.setSpras(cursor.getString(cursor.getColumnIndex("SPRAS")).trim());
            if(cursor.getString(cursor.getColumnIndex("MATKL")) !=  null)
                ef.setMatkl(cursor.getString(cursor.getColumnIndex("MATKL")).trim());
            if(cursor.getString(cursor.getColumnIndex("SERGE")) !=  null)
                ef.setSerge(cursor.getString(cursor.getColumnIndex("SERGE")).trim());
            if(cursor.getColumnIndex("SERNR") > -1) {
                if (cursor.getString(cursor.getColumnIndex("SERNR")) != null)
                    ef.setSernr(cursor.getString(cursor.getColumnIndex("SERNR")).trim());
            }
            if(cursor.getString(cursor.getColumnIndex("estado")) !=  null)
                ef.setEstado(cursor.getString(cursor.getColumnIndex("estado")).trim());
            if(cursor.getString(cursor.getColumnIndex("fecha_lectura")) !=  null)
                ef.setFechaLectura(cursor.getString(cursor.getColumnIndex("fecha_lectura")).trim());
            if(cursor.getString(cursor.getColumnIndex("num_placa")) !=  null)
                ef.setNumPlaca(cursor.getString(cursor.getColumnIndex("num_placa")).trim());
            if(cursor.getString(cursor.getColumnIndex("activo")) !=  null)
                ef.setActivo(cursor.getString(cursor.getColumnIndex("activo")).trim());
            if(cursor.getString(cursor.getColumnIndex("comentario")) !=  null)
                ef.setComentario(cursor.getString(cursor.getColumnIndex("comentario")).trim());
            if(cursor.getString(cursor.getColumnIndex("transmitido")) !=  null)
                ef.setTransmitido(cursor.getString(cursor.getColumnIndex("transmitido")).trim());
            if(cursor.getString(cursor.getColumnIndex("id_solicitud")) !=  null)
                ef.setIdSolicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")).trim());
            if(cursor.getString(cursor.getColumnIndex("ultima_lectura")) !=  null)
                ef.setUltima_lectura(cursor.getString(cursor.getColumnIndex("ultima_lectura")).trim());
            equiposFriosList.add(ef);
        }
        cursor.close();
        return  equiposFriosList;
    }
    public boolean ExisteEquipoFrio(String num_equipo) {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM SapDBaseInstalada WHERE rtrim(SERGE) = ?", new String[]{num_equipo});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            if((cursor == null) || valor == 0){
                cursor = mDataBase.rawQuery("select count(*) as cantidad FROM SapDBaseInstalada WHERE (ltrim(SERNR, '0') = ? OR SERNR = ?)", new String[]{num_equipo});
                if (cursor.moveToNext()) {
                    valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
                }
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener existencia de equipo frio").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }
    public boolean ExisteEquipoFrioEnCliente(String cliente, String num_equipo) {
        int valor = 0;
        try {
            Cursor cursor = mDataBase.rawQuery("select count(*) as cantidad FROM SapDBaseInstalada WHERE KUNNR = ? AND rtrim(SERGE) = ?", new String[]{cliente,num_equipo});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
            }
            //Si no encuentra con la lectura de la placa que es lo normal, encontrar con la lectura de la serie de IMBERA del equipo frio
            if(valor == 0){
                cursor = mDataBase.rawQuery("select count(*) as cantidad FROM SapDBaseInstalada WHERE KUNNR = ? AND (ltrim(SERNR, '0') = ? OR SERNR = ?)", new String[]{cliente,num_equipo,num_equipo});
                if (cursor.moveToNext()) {
                    valor = cursor.getInt(cursor.getColumnIndex("cantidad"));
                }
            }
            cursor.close();
        }catch (Exception e){
            Toasty.error(mContext,"Error al obtener existencia de equipo frio").show();
            return false;
        }
        if(valor > 0)
            return true;
        else
            return false;
    }

    public int CatidadAlertasPeriodo(String placa) {
        int cantidad = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from CensoEquipoFrio where trim(estado) = ? AND num_placa = ? and activo = 1";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{"Alerta", placa});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }

    public long ValidacionAnomalia(String codigo) {
        int updates = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from CensoEquipoFrio where trim(estado) = ? AND num_placa = ? and activo = 1";
            ContentValues values = new  ContentValues();
            values.put("estado",   "Rechazado");
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            values.put("[W_CTE-COMENTARIOS]",   "Equipo Verificado con éxito el "+currentDateandTime);
            values.put("[FECFIN]",   currentDateandTime);
            updates = mDataBase.update("FormHvkof",values,"tipform = 200 AND [W_CTE-IM_EQUIPMENT] = ? AND estado = 'Pendiente'", new String[]{codigo});
        }catch (SQLiteException e){

        }
        return updates;
    }

    public String GetIdSolicitudDeAnomalia(String num_placa)
    {
        String id_solicitud = "";
        //Trae la el id_solicitud de la anomalia para poder visualizarla en la parte de base instalada y censos.
        String query = "SELECT id_solicitud FROM FormHVKOF_solicitud where [W_CTE-IM_EQUIPMENT] = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{num_placa});
        if (cursor.moveToNext()){
            id_solicitud = cursor.getString(0);
        }
        cursor.close();
        return id_solicitud;
    }
    public String getNombreImagenModelo(String codigo_material, String modelo)
    {
        String nombreImagen = "";
        //Trae ell nombre de la imagen, segun el material en SAP
        String query = "SELECT nombreImagen FROM cat_loc_modelo_ef where codigoSAP = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{codigo_material});
        if (cursor.moveToNext()){
            nombreImagen = cursor.getString(0);
        }
        if(nombreImagen.equals("")){
            query = "SELECT nombreImagen FROM cat_loc_modelo_ef where codigo = ?";
            cursor = mDataBase.rawQuery(query, new String[]{modelo});
            if (cursor.moveToNext()){
                nombreImagen = cursor.getString(0);
            }
        }
        cursor.close();
        return nombreImagen;
    }
    public boolean AccesoEquipoFrioLibre() {
        int valor = 1;
        try {
            Cursor cursor = mDataBase.rawQuery("select opcion_ef_libre FROM ConfigReestriccionRuta WHERE ruta = ? OR (ruta = '*' AND bukrs = ?)", new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH",""),PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")});
            if (cursor.moveToNext()) {
                valor = cursor.getInt(cursor.getColumnIndex("opcion_ef_libre"));
            }
            cursor.close();
        }catch (Exception e){
            return true;
        }
        if(valor > 0)
            return true;
        else {
            return false;
        }
    }
    public String getGrupoCredito() {
        String grupoCredito = "";
        String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
        String kkber = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_AREACREDITO","");
        String sql_encuesta = "select credit_sgmnt from cat_sap4h_area_cred p where bukrs = '" + sociedad + "' AND kkber = '" + kkber + "'";

        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        //Cursor cursor2 = mDataBase.rawQuery(sql_encuesta2,null);
        while (cursor.moveToNext()){
            grupoCredito = cursor.getString(0).trim();
        }
        cursor.close();
        return  grupoCredito;
    }

    public boolean UsaMonitorEquipoFrio()
    {
        boolean retorno = false;
        try {
            String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
            String query = "select usa_monitor_ef FROM cat_bukrs WHERE id_bukrs = ? ";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{sociedad});
            if (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex("usa_monitor_ef")).trim().equals("1") ? true : false;
            }
            cursor.close();
        }catch(Exception e){
            //Toasty.error(mContext,"Error determinando Uso de Monitor Equipo Frio: "+e.getMessage()).show();
        }
        return retorno;
    }
    public boolean UsaIndirectos()
    {
        boolean retorno = false;
        try {
            String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
            String query = "select usa_indirectos FROM cat_bukrs WHERE id_bukrs = ? ";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{sociedad});
            if (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex("usa_indirectos")).trim().equals("1") ? true : false;
            }
            cursor.close();
        }catch(Exception e){
            //Toasty.error(mContext,"Error determinando Uso de Monitor Equipo Frio: "+e.getMessage()).show();
        }
        return retorno;
    }
    public boolean esInclusionIndirecto(String id_form)
    {
        boolean retorno = false;
        try {
            String query = "select ind_tipo_cliente FROM flujo WHERE id_form = ? AND ind_modelo = 'I'";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{id_form});
            if (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex("ind_tipo_cliente")).trim().equals("IN") ? true : false;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.error(mContext,"No se pudo obtener campo Indirecto: "+e.getMessage()).show();
        }
        return retorno;
    }
    public boolean esModificacionIndirecto(String id_form)
    {
        boolean retorno = false;
        try {
            String query = "select ind_tipo_cliente FROM flujo WHERE id_form = ? AND ind_modelo = 'M'";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{id_form});
            if (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex("ind_tipo_cliente")).trim().equals("IN") ? true : false;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.error(mContext,"No se pudo obtener campo Indirecto: "+e.getMessage()).show();
        }
        return retorno;
    }
    public boolean esDestinoIndirecto(String id_form)
    {
        boolean retorno = false;
        try {
            String query = "select ind_tipo_cliente_n FROM flujo WHERE id_form = ? AND ind_modelo <> 'I'";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{id_form});
            if (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex("ind_tipo_cliente_n")).trim().equals("IN") ? true : false;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.error(mContext,"No se pudo obtener campo Indirecto: "+e.getMessage()).show();
        }
        return retorno;
    }
    public String getTipoClienteDestino(String id_form)
    {
        String retorno = "";
        try {
            String query = "select ind_tipo_cliente_n FROM flujo WHERE id_form = ?";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{id_form});
            if (cursor.moveToNext()) {
                retorno = cursor.getString(cursor.getColumnIndex("ind_tipo_cliente_n")).trim();
            }
            cursor.close();
        }catch(Exception e){
            Toasty.error(mContext,"No se pudo obtener campo ind_tipo_cliente_n: "+e.getMessage()).show();
        }
        return retorno;
    }
    public int CalidadDeAdjuntos()
    {
        int retorno = -1;
        try {
            String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
            String query = "select calidadAdjuntos FROM cat_bukrs WHERE id_bukrs = ? ";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{sociedad});
            if (cursor.moveToNext()) {
                retorno = cursor.getInt(cursor.getColumnIndex("calidadAdjuntos"));
            }
            cursor.close();
        }catch(Exception e){
            //Toasty.error(mContext,"Error determinando Uso de Monitor Equipo Frio: "+e.getMessage()).show();
        }
        return retorno;
    }

    public ArrayList<HashMap<String, String>> getDatosVistaMonitorEquipoFrioDB(String id_cliente){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> dataMonitor = new ArrayList<>();
        String query = "SELECT * FROM VistaMonitorEquipoFrio v WHERE v.codigo_cliente = ?";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{id_cliente});

            while (cursor.moveToNext()) {
                HashMap<String, String> solicitud = new HashMap<>();
                solicitud.put("estado", cursor.getString(cursor.getColumnIndex("estado")) != null ? cursor.getString(cursor.getColumnIndex("estado")) : "");
                solicitud.put("prioridad", cursor.getString(cursor.getColumnIndex("prioridad")) != null ? cursor.getString(cursor.getColumnIndex("prioridad")) : "");
                solicitud.put("desc_prioridad", cursor.getString(cursor.getColumnIndex("desc_prioridad")) != null ? cursor.getString(cursor.getColumnIndex("desc_prioridad")) : "");
                solicitud.put("ruta", cursor.getString(cursor.getColumnIndex("ruta")) != null ? cursor.getString(cursor.getColumnIndex("ruta")) : "");
                solicitud.put("codigo_cliente", cursor.getString(cursor.getColumnIndex("codigo_cliente")) != null ? cursor.getString(cursor.getColumnIndex("codigo_cliente")) : "");
                solicitud.put("nombre_cliente", cursor.getString(cursor.getColumnIndex("nombre_cliente")) != null ? cursor.getString(cursor.getColumnIndex("nombre_cliente")) : "");
                solicitud.put("gec", cursor.getString(cursor.getColumnIndex("gec")) != null ? cursor.getString(cursor.getColumnIndex("gec")) : "");
                solicitud.put("desc_gec", cursor.getString(cursor.getColumnIndex("desc_gec")) != null ? cursor.getString(cursor.getColumnIndex("desc_gec")) : "");
                solicitud.put("tipo_canal", cursor.getString(cursor.getColumnIndex("tipo_canal")) != null ? cursor.getString(cursor.getColumnIndex("tipo_canal")) : "");
                solicitud.put("desc_tipo_canal", cursor.getString(cursor.getColumnIndex("desc_tipo_canal")) != null ? cursor.getString(cursor.getColumnIndex("desc_tipo_canal")) : "");
                solicitud.put("venta_actual", cursor.getString(cursor.getColumnIndex("venta_actual")) != null ? cursor.getString(cursor.getColumnIndex("venta_actual")) : "");
                solicitud.put("venta_comprometida", cursor.getString(cursor.getColumnIndex("venta_comprometida")) != null ? cursor.getString(cursor.getColumnIndex("venta_comprometida")) : "");
                solicitud.put("venta_total", cursor.getString(cursor.getColumnIndex("venta_total")) != null ? cursor.getString(cursor.getColumnIndex("venta_total")) : "");
                solicitud.put("puertas_sugeridas", cursor.getString(cursor.getColumnIndex("puertas_sugeridas")) != null ? cursor.getString(cursor.getColumnIndex("puertas_sugeridas")) : "");
                solicitud.put("puertas_instaladas", cursor.getString(cursor.getColumnIndex("puertas_instaladas")) != null ? cursor.getString(cursor.getColumnIndex("puertas_instaladas")) : "");
                solicitud.put("puertas_proceso", cursor.getString(cursor.getColumnIndex("puertas_proceso")) != null ? cursor.getString(cursor.getColumnIndex("puertas_proceso")) : "");
                solicitud.put("puertas_por_instalar", cursor.getString(cursor.getColumnIndex("puertas_por_instalar")) != null ? cursor.getString(cursor.getColumnIndex("puertas_por_instalar")) : "");
                solicitud.put("puertas_objetivo", cursor.getString(cursor.getColumnIndex("puertas_objetivo")) != null ? cursor.getString(cursor.getColumnIndex("puertas_objetivo")) : "");
                solicitud.put("solicitud", cursor.getString(cursor.getColumnIndex("solicitud")) != null ? cursor.getString(cursor.getColumnIndex("solicitud")) : "");
                solicitud.put("cajas_monitor_ef", cursor.getString(cursor.getColumnIndex("cajas_monitor_ef")) != null ? cursor.getString(cursor.getColumnIndex("cajas_monitor_ef")) : "");
                solicitud.put("pais", cursor.getString(cursor.getColumnIndex("pais")) != null ? cursor.getString(cursor.getColumnIndex("pais")) : "");

                solicitud.put("bukrs", cursor.getString(cursor.getColumnIndex("bukrs")) != null ? cursor.getString(cursor.getColumnIndex("bukrs")) : "");
                solicitud.put("prioridad_volumen", cursor.getString(cursor.getColumnIndex("prioridad_volumen")) != null ? cursor.getString(cursor.getColumnIndex("prioridad_volumen")) : "");
                solicitud.put("prioridad_cliente", cursor.getString(cursor.getColumnIndex("prioridad_cliente")) != null ? cursor.getString(cursor.getColumnIndex("prioridad_cliente")) : "");
                solicitud.put("prioridad_por_objetivo", cursor.getString(cursor.getColumnIndex("prioridad_por_objetivo")) != null ? cursor.getString(cursor.getColumnIndex("prioridad_por_objetivo")) : "");
                solicitud.put("prioridad_gec", cursor.getString(cursor.getColumnIndex("prioridad_gec")) != null ? cursor.getString(cursor.getColumnIndex("prioridad_gec")) : "");
                solicitud.put("prioridad", cursor.getString(cursor.getColumnIndex("prioridad")) != null ? cursor.getString(cursor.getColumnIndex("prioridad")) : "");
                solicitud.put("desc_prioridad", cursor.getString(cursor.getColumnIndex("desc_prioridad")) != null ? cursor.getString(cursor.getColumnIndex("desc_prioridad")) : "");
                dataMonitor.add(solicitud);
            }
            cursor.close();
        }catch(Exception e){
            if(UsaMonitorEquipoFrio())
                Toasty.warning(mContext,"Error al obtener datos de la Vista de Monitor de Equipo Frio: "+e.getMessage()).show();
        }
        return  dataMonitor;
    }

    public ArrayList<String> CalcularPrioridad(ArrayList<HashMap<String, String>> formList) {
        int sumaPrioridades = Integer.parseInt(formList.get(0).get("prioridad_volumen"))+Integer.parseInt(formList.get(0).get("prioridad_cliente"))+Integer.parseInt(formList.get(0).get("prioridad_por_objetivo"))+Integer.parseInt(formList.get(0).get("prioridad_gec"));
        ArrayList<String> retorno = new ArrayList<>();
        try {
            String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
            String query = "select valor, valor2, valor3, p.PRIOKX as desc_prioridad FROM ConfigConstantes " +
                    " LEFT JOIN cat_ef_prioridades AS p ON (p.PRIOK = ConfigConstantes.valor3)" +
                    " WHERE bukrs = ? AND constante like '%rango_prioridad%'";
            Cursor cursor = mDataBase.rawQuery(query, new String[]{sociedad});
            while (cursor.moveToNext()){
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex("valor")).trim()) <= sumaPrioridades && Integer.parseInt(cursor.getString(cursor.getColumnIndex("valor2")).trim()) > sumaPrioridades) {
                    retorno.add(cursor.getString(cursor.getColumnIndex("valor3")).trim());
                    retorno.add(cursor.getString(cursor.getColumnIndex("desc_prioridad")).trim());
                }
            }
            cursor.close();
        }catch(Exception e){
            Toasty.error(mContext,"Error determinando la prioridad sugerida: "+e.getMessage()).show();
        }
        return retorno;
    }

    public ArrayList<HashMap<String, Object>> getListaCoordenadasHabilitador(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, Object>> dataCoordenadas = new ArrayList<>();
        String query = "SELECT * FROM cat_habilitador h WHERE (BZIRK = ? OR BZIRK IS NULL OR BZIRK = '') AND ruta_venta = ?";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK", ""),PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH", "")});

            while (cursor.moveToNext()) {
                HashMap<String, Object> solicitud = new HashMap<>();
                solicitud.put("latitud", cursor.getString(cursor.getColumnIndex("latitud")) != null ? cursor.getDouble(cursor.getColumnIndex("latitud")) : 0.00);
                solicitud.put("longitud", cursor.getString(cursor.getColumnIndex("longitud")) != null ? cursor.getDouble(cursor.getColumnIndex("longitud")) : 0.00);
                solicitud.put("reparto", cursor.getString(cursor.getColumnIndex("reparto")) != null ? cursor.getString(cursor.getColumnIndex("reparto")) : "");
                dataCoordenadas.add(solicitud);
            }
            cursor.close();
        }catch(Exception e){
                Toasty.warning(mContext,"Error al obtener datos de la lista de coordenadas del habilitador: "+e.getMessage()).show();
        }
        return  dataCoordenadas;
    }
    public boolean CorreoValidadoUltimamente(String correo){
        boolean existe = false;
        String query = "SELECT correo FROM VerificacionCodigos p WHERE correo = ? AND estado = ? and fecha_mod >= datetime('now', '-3 months')";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{correo,"Verificado"});
            while (cursor.moveToNext()) {
                existe = true;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.warning(mContext,"No se pudo validar el correo: "+e.getMessage()).show();
        }
        return  existe;
    }
    public boolean NumeroValidadoUltimamente(String celular){
        boolean existe = false;
        String query = "SELECT celular FROM VerificacionCodigos p WHERE celular = ? AND estado = ? and fecha_mod >= datetime('now', '-3 months')";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{celular,"Verificado"});

            while (cursor.moveToNext()) {
                existe = true;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.warning(mContext,"No se pudo validar el # celular: "+e.getMessage()).show();
        }
        return  existe;
    }
    public boolean rutaEnPavent(String reparto){
        boolean existe = false;
        String query = "SELECT * FROM SAPDCAT_Ruta_Relacion p WHERE route = ? AND zroute = ?";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH",""),reparto});

            while (cursor.moveToNext()) {
                existe = true;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.warning(mContext,"Error al obtener datos de PAVENT: "+e.getMessage()).show();
        }
        return  existe;
    }
    public String centroSuministroSegunRutaReparto(String reparto){
        String centro = "";
        String query = "SELECT * FROM EX_T_RUTAS_VP p WHERE zroute_rep = ?";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{reparto});

            while (cursor.moveToNext()) {
                centro = cursor.getString(cursor.getColumnIndex("vwerks"));
            }
            cursor.close();
        }catch(Exception e){
            Toasty.warning(mContext,"Error al obtener el centro de suministro, seleccione manualmente: "+e.getMessage()).show();
        }
        return  centro;
    }

    public int CantidadVerificados(String codigoCliente) {
        int cantidad = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from CensoEquipoFrio where kunnr_censo = ? AND  trim(estado) = ? and activo = 1 AND num_placa IN (SELECT SERGE FROM SapDBaseInstalada WHERE kunnr = ?)";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{codigoCliente, "Verificado",codigoCliente});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }
    public int CantidadHallazgos(String codigoCliente) {
        int cantidad = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from CensoEquipoFrio where kunnr_censo = ? AND  trim(estado) = ? and activo = 1";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{codigoCliente, "Hallazgo"});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }
    public int CantidadAnomalias(String codigoCliente) {
        int cantidad = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from CensoEquipoFrio where kunnr_censo = ? AND  trim(estado) = ? and activo = 1";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{codigoCliente, "Anomalia"});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }
    public int CantidadAlertas(String codigoCliente) {
        int cantidad = 0;
        try {
            String sql_encuesta = "select count(*) as cantidad  from CensoEquipoFrio where kunnr_censo = ? AND  trim(estado) = ? and activo = 1";
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, new String[]{codigoCliente, "Alerta"});
            while (cursor.moveToNext()) {
                cantidad = cursor.getInt(0);
            }
            cursor.close();
        }catch (SQLiteException e){

        }
        return cantidad;
    }

    public boolean censoEquipoFrioVigente(){
        boolean existe = false;
        String query = "SELECT * FROM cat_bukrs c WHERE c.fecha_inicio_censo <= datetime('now') AND c.fecha_fin_censo >= datetime('now')";
        try {
            Cursor cursor = mDataBase.rawQuery(query, new String[]{});

            while (cursor.moveToNext()) {
                existe = true;
            }
            cursor.close();
        }catch(Exception e){
            Toasty.warning(mContext,"Error al obtener datos de Censo EF: "+e.getMessage()).show();
        }
        return  existe;
    }

    public List<EncuestaCabecera> getEncuestasCabecera(String ruta) {
        List<EncuestaCabecera> encuestasCabeceras = new ArrayList<>();
        List<EncuestaCabecera> encuestasCabecerasFiltrado = new ArrayList<>();
        ruta = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH", "");
        String bzirk = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK", "");

        String sql_encuesta ="SELECT e.id_encuesta,nombre ,descripcion ,id_bukrs ,fecha_inicio,fecha_fin,fecha_creacion,fecha_modificacion, gvc FROM encuesta_cabecera e where fecha_inicio<=datetime('now') and fecha_fin>=datetime('now')";

        try {
            Cursor cursor = mDataBase.rawQuery(sql_encuesta, null);
            MicroOrm uOrm = new MicroOrm();
            if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {
                //cursor is empty
            } else {
                encuestasCabeceras = uOrm.listFromCursor(cursor, EncuestaCabecera.class);
            }
            cursor.close();
            for (EncuestaCabecera encuestaCabecera : encuestasCabeceras) {
                sql_encuesta = "SELECT * from encuesta_ruta where id_encuesta=" + encuestaCabecera.getId();
                Cursor cursorRuta = mDataBase.rawQuery(sql_encuesta, null);
                while (cursorRuta.moveToNext()) {
                    encuestaCabecera.getRutas().add(cursorRuta.getString(cursorRuta.getColumnIndex("ruta")));
                }
                cursorRuta.close();

                sql_encuesta = "SELECT * from encuesta_bzirk where id_encuesta=" + encuestaCabecera.getId();
                Cursor cursorBzirk = mDataBase.rawQuery(sql_encuesta, null);
                while (cursorBzirk.moveToNext()) {
                    encuestaCabecera.getBzirks().add(cursorBzirk.getString(cursorBzirk.getColumnIndex("bzirk")));
                }
                cursorBzirk.close();
            }

            for (EncuestaCabecera encuestaCabecera : encuestasCabeceras) {
                if (encuestaCabecera.getBzirks().contains(bzirk)) {
                    if (encuestaCabecera.getRutas().isEmpty()) {
                        encuestasCabecerasFiltrado.add(encuestaCabecera);
                    } else {
                        if (encuestaCabecera.getRutas().contains(ruta)) {
                            encuestasCabecerasFiltrado.add(encuestaCabecera);
                        }
                    }
                }
            }
        }catch(Exception e){

        }

        return  encuestasCabecerasFiltrado;
    }

    public String getGUIDEncuestaActualCliente(String id_encuesta,String codigo_cliente) {
        String GUID = "";
        long cantidad = 0;
        String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
        String kkber = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_AREACREDITO","");
        String sql_encuesta = "select distinct GUID from respuesta_pregunta r join encuesta_cabecera e on e.id_encuesta=r.id_encuesta where r.id_encuesta = '"+id_encuesta+"'        and r.codigo_cliente = '"+codigo_cliente+"'        and fecha_ejecucion between e.fecha_inicio and e.fecha_fin";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            GUID = cursor.getString(0);
        }
        cursor.close();
        return  GUID;
    }

    public String getEstadoRespuestaEnviada(String GUID) {
        String estado="";
        String sql_encuesta = "select distinct estado from respuesta_pregunta r where r.GUID = '"+GUID+"'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            estado = cursor.getString(0);
        }
        cursor.close();
        return  estado;
    }

    public boolean getValidacionEncuestaClientePendiente(String id_encuesta,String codigo_cliente) {
        boolean ejecutada = false;
        long cantidad = 0;
        String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
        String kkber = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_AREACREDITO","");
        String sql_encuesta = "select count(*) from respuesta_pregunta r join encuesta_cabecera e on e.id_encuesta=r.id_encuesta where r.id_encuesta = '"+id_encuesta+"'        and r.codigo_cliente = '"+codigo_cliente+"'        and fecha_ejecucion between e.fecha_inicio and e.fecha_fin and r.estado!='cerrado'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            cantidad = cursor.getLong(0);
        }
        cursor.close();
        if(cantidad>0){
            ejecutada=true;
        }
        return  ejecutada;
    }
    public boolean getValidacionEncuestaPendiente() {
        boolean pendiente = false;
        long cantidad = 0;
        String sociedad = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS", "");
        String kkber = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_AREACREDITO","");
        String sql_encuesta = "select count(*) from respuesta_pregunta p where estado = 'pendiente'";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        while (cursor.moveToNext()){
            cantidad = cursor.getLong(0);
        }
        cursor.close();
        if(cantidad>0){
            pendiente=true;
        }
        return  pendiente;
    }

    public List<RespuestaPregunta> getRespuestasEncuestaCliente(String codigo_cliente,String id_encuesta){
        List<RespuestaPregunta> respuestaPreguntas = new ArrayList<>();
        String sql_encuesta = "select * from respuesta_pregunta r join encuesta_cabecera e on e.id_encuesta=r.id_encuesta where r.id_encuesta = '"+id_encuesta+"'        and r.codigo_cliente = '"+codigo_cliente+"' and fecha_ejecucion between e.fecha_inicio and e.fecha_fin";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,null);
        MicroOrm uOrm = new MicroOrm();
        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            //cursor is empty
        }else{
            respuestaPreguntas = uOrm.listFromCursor(cursor, RespuestaPregunta.class);
        }

        cursor.close();

        return  respuestaPreguntas;
    }

    public List<String> getImagenPaths(String guid) {
        List<String> paths = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT imagenPath FROM respuesta_pregunta WHERE GUID = ? AND imagenPath IS NOT NULL", new String[]{guid});
        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(0);
                if (path != null && !path.isEmpty()) {
                    paths.add(path);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paths;
    }
}
