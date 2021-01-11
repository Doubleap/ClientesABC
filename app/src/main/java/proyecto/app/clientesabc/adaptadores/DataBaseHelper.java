package proyecto.app.clientesabc.adaptadores;

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
import android.preference.PreferenceManager;
import android.util.Log;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;

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
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Banco;
import proyecto.app.clientesabc.modelos.Comentario;
import proyecto.app.clientesabc.modelos.Contacto;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import proyecto.app.clientesabc.modelos.Impuesto;
import proyecto.app.clientesabc.modelos.Interlocutor;
import proyecto.app.clientesabc.modelos.OpcionSpinner;
import proyecto.app.clientesabc.modelos.Visitas;

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
        File externalStorage = Environment.getExternalStorageDirectory();
        String externalStoragePath;
        if (externalStorage != null) {
            externalStoragePath = externalStorage.getAbsolutePath();
            tranFileDir = new File(externalStoragePath + File.separator + mContext.getPackageName() + File.separator + "Transmision"+ File.separator +"FAWM_ANDROID_2");
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
        File externalStorage = Environment.getExternalStorageDirectory();
        String externalStoragePath;
        if (externalStorage != null) {
            externalStoragePath = externalStorage.getAbsolutePath();
            tranFileDir = new File(externalStoragePath + File.separator + mContext.getPackageName() + File.separator + "Transmision"+ File.separator +"FAWM_ANDROID_2");
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
            File externalStorage = Environment.getExternalStorageDirectory();
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

    public ArrayList<HashMap<String, String>> getClientes(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String query = "SELECT KUNNR as codigo, NAME1_E as nombre, NAME_CO as direccion, 'Estado' as estado, KLABC as klabc, STCD3 as stcd3, STREET as street, STR_SUPPL1 as str_suppl1, SMTP_ADDR as smtp_addr, ZZCRMA_LAT as latitud, ZZCRMA_LONG as longitud " +
                " FROM SAPDClientes";
        Cursor cursor = mDataBase.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("codigo",cursor.getString(0) != null ? cursor.getString(0) : "");
            user.put("nombre",cursor.getString(1) != null ? cursor.getString(1) : "");
            user.put("direccion",cursor.getString(2) != null ? cursor.getString(2) : "");
            user.put("estado",cursor.getString(3) != null ? cursor.getString(3) : "");
            user.put("klabc",cursor.getString(cursor.getColumnIndex("klabc")) != null ? cursor.getString(cursor.getColumnIndex("klabc")) : "");
            user.put("idfiscal",cursor.getString(cursor.getColumnIndex("stcd3")) != null ? cursor.getString(cursor.getColumnIndex("stcd3")) : "");
            user.put("ubicacion",cursor.getString(cursor.getColumnIndex("street")) != null ? cursor.getString(cursor.getColumnIndex("street")) : "");
            user.put("direccion",cursor.getString(cursor.getColumnIndex("str_suppl1")) != null ? cursor.getString(cursor.getColumnIndex("str_suppl1")) : "");
            user.put("correo",cursor.getString(cursor.getColumnIndex("smtp_addr")) != null ? cursor.getString(cursor.getColumnIndex("smtp_addr")) : "");
            user.put("latitud",cursor.getString(cursor.getColumnIndex("latitud")) != null ? cursor.getString(cursor.getColumnIndex("latitud")) : "");
            user.put("longitud",cursor.getString(cursor.getColumnIndex("longitud")) != null ? cursor.getString(cursor.getColumnIndex("longitud")) : "");
            clientList.add(user);
        }
        cursor.close();
        return  clientList;
    }
    public ArrayList<HashMap<String, String>> getValidaCreditos(String tipo, String clasicxc){
        if(tipo.equals("ABC")){
            tipo = "F";
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
            datos.add(datosCredito);
        }
        cursor.close();
        return  datos;
    }

    public ArrayList<HashMap<String, String>> getCliente(String id_cliente){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT * FROM SAPDClientes WHERE kunnr = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_cliente});
        String valorNoEncontrado = "Campo No encontrado";
        while (cursor.moveToNext()){
            HashMap<String,String> cliente = new HashMap<>();
            cliente.put("W_CTE-AKONT",valorNoEncontrado);
            cliente.put("W_CTE-ALTKN",cursor.getString(cursor.getColumnIndex("ALTKN")) );
            cliente.put("W_CTE-ANTLF",valorNoEncontrado);
            cliente.put("W_CTE-BUKRS",cursor.getString(cursor.getColumnIndex("BUKRS")) );
            cliente.put("W_CTE-BZIRK",cursor.getString(cursor.getColumnIndex("BZIRK")) );
            cliente.put("W_CTE-CITY1",cursor.getString(cursor.getColumnIndex("CITY1")) );
            cliente.put("W_CTE-CITY2",valorNoEncontrado);
            cliente.put("W_CTE-CTLPC",valorNoEncontrado);
            cliente.put("W_CTE-DATAB",valorNoEncontrado);
            cliente.put("W_CTE-DATBI",valorNoEncontrado);
            cliente.put("W_CTE-DBRTG",valorNoEncontrado);
            cliente.put("W_CTE-DMBTR1",valorNoEncontrado);
            cliente.put("W_CTE-DMBTR2",valorNoEncontrado);
            cliente.put("W_CTE-DMBTR3",valorNoEncontrado);
            cliente.put("W_CTE-FAX_EXTENS",valorNoEncontrado);
            cliente.put("W_CTE-FAX_NUMBER",cursor.getString(cursor.getColumnIndex("FAX_NUMBER")) );
            cliente.put("W_CTE-FDGRV",valorNoEncontrado);
            cliente.put("W_CTE-FLAG_FACT",valorNoEncontrado);
            cliente.put("W_CTE-FLAG_NTEN",valorNoEncontrado);
            cliente.put("W_CTE-HITYP",valorNoEncontrado);
            cliente.put("W_CTE-HKUNNR",cursor.getString(cursor.getColumnIndex("HKUNNR")) );
            cliente.put("W_CTE-HOME_CITY",cursor.getString(cursor.getColumnIndex("HOME_CITY")) );
            cliente.put("W_CTE-HOUSE_NUM1",cursor.getString(cursor.getColumnIndex("HOUSE_NUM1")) );
            cliente.put("W_CTE-HOUSE_NUM2",cursor.getString(cursor.getColumnIndex("HOUSE_NUM2")) );
            cliente.put("W_CTE-INCO1",valorNoEncontrado);
            cliente.put("W_CTE-INCO2",valorNoEncontrado);
            cliente.put("W_CTE-KALKS",cursor.getString(cursor.getColumnIndex("KALKS")) );
            cliente.put("W_CTE-KATR3",valorNoEncontrado);
            cliente.put("W_CTE-KATR4",cursor.getString(cursor.getColumnIndex("KATR4")) );
            cliente.put("W_CTE-KATR5",valorNoEncontrado);
            cliente.put("W_CTE-KATR8",cursor.getString(cursor.getColumnIndex("KATR8")) );
            cliente.put("W_CTE-KDGRP",cursor.getString(cursor.getColumnIndex("KDGRP")) );
            cliente.put("W_CTE-KKBER",valorNoEncontrado);
            cliente.put("W_CTE-KLABC",cursor.getString(cursor.getColumnIndex("KLABC")) );
            cliente.put("W_CTE-KLIMK",valorNoEncontrado);
            cliente.put("W_CTE-KNKLI",valorNoEncontrado);
            cliente.put("W_CTE-KTGRD",cursor.getString(cursor.getColumnIndex("KTGRD")) );
            cliente.put("W_CTE-KTOKD",cursor.getString(cursor.getColumnIndex("KTOKD")) );
            cliente.put("W_CTE-KUKLA",cursor.getString(cursor.getColumnIndex("KUKLA")) );
            cliente.put("W_CTE-KUNNR",cursor.getString(cursor.getColumnIndex("KUNNR")) );
            cliente.put("W_CTE-KVGR1",cursor.getString(cursor.getColumnIndex("KVGR1")) );
            cliente.put("W_CTE-KVGR2",cursor.getString(cursor.getColumnIndex("KVGR2")) );
            cliente.put("W_CTE-KVGR3",cursor.getString(cursor.getColumnIndex("KVGR3")) );
            cliente.put("W_CTE-KVGR5",cursor.getString(cursor.getColumnIndex("KVGR5")) );
            cliente.put("W_CTE-LAND1",cursor.getString(cursor.getColumnIndex("LAND")) );
            cliente.put("W_CTE-LIFNR",valorNoEncontrado);
            cliente.put("W_CTE-LIMSUG",valorNoEncontrado);
            cliente.put("W_CTE-LOCATION",cursor.getString(cursor.getColumnIndex("LOCATION")) );
            cliente.put("W_CTE-LPRIO",valorNoEncontrado);
            cliente.put("W_CTE-LZONE",cursor.getString(cursor.getColumnIndex("LZONE")) );
            cliente.put("W_CTE-NAME_CO",cursor.getString(cursor.getColumnIndex("NAME_CO")) );
            cliente.put("W_CTE-NAME1",cursor.getString(cursor.getColumnIndex("NAME1_E")) );
            cliente.put("W_CTE-NAME2",cursor.getString(cursor.getColumnIndex("NAME2")) );
            cliente.put("W_CTE-NAME3",cursor.getString(cursor.getColumnIndex("NAME3")) );
            cliente.put("W_CTE-NAME4",cursor.getString(cursor.getColumnIndex("NAME4")) );
            cliente.put("W_CTE-PERNR",valorNoEncontrado);
            cliente.put("W_CTE-PO_BOX",cursor.getString(cursor.getColumnIndex("PO_BOX")) );
            cliente.put("W_CTE-PO_BOX_LOC",cursor.getString(cursor.getColumnIndex("PO_BOX_LOC")) );
            cliente.put("W_CTE-PO_BOX_REG",cursor.getString(cursor.getColumnIndex("PO_BOX_REG")) );
            cliente.put("W_CTE-POST_CODE2",cursor.getString(cursor.getColumnIndex("POST_CODE2")) );
            cliente.put("W_CTE-PRFRE",cursor.getString(cursor.getColumnIndex("PRFRE")) );
            cliente.put("W_CTE-PSON1",valorNoEncontrado);
            cliente.put("W_CTE-PSON2",valorNoEncontrado);
            cliente.put("W_CTE-PSON3",valorNoEncontrado);
            cliente.put("W_CTE-PSTLZ",cursor.getString(cursor.getColumnIndex("PSTLZ")) );
            cliente.put("W_CTE-PVKSM",valorNoEncontrado);
            cliente.put("W_CTE-REGION",cursor.getString(cursor.getColumnIndex("REGION")) );
            cliente.put("W_CTE-ROOMNUMBER",cursor.getString(cursor.getColumnIndex("ROOMNUMBER")) );
            cliente.put("W_CTE-SMTP_ADDR",cursor.getString(cursor.getColumnIndex("SMTP_ADDR")) );
            cliente.put("W_CTE-STCD1",cursor.getString(cursor.getColumnIndex("STCD1")) );
            cliente.put("W_CTE-STCD3",cursor.getString(cursor.getColumnIndex("STCD3")) );
            cliente.put("W_CTE-STR_SUPPL1",cursor.getString(cursor.getColumnIndex("STR_SUPPL1")) );
            cliente.put("W_CTE-STR_SUPPL2",cursor.getString(cursor.getColumnIndex("STR_SUPPL2")) );
            cliente.put("W_CTE-STR_SUPPL3",cursor.getString(cursor.getColumnIndex("STR_SUPPL3")) );
            cliente.put("W_CTE-STREET",cursor.getString(cursor.getColumnIndex("STREET")) );
            cliente.put("W_CTE-TEL_EXTENS",cursor.getString(cursor.getColumnIndex("TEL_EXTENS")) );
            cliente.put("W_CTE-TEL_NUMBER",cursor.getString(cursor.getColumnIndex("TEL_NUMBER")) );
            cliente.put("W_CTE-TEL_NUMBER2",valorNoEncontrado);
            cliente.put("W_CTE-TELNUMBER2",valorNoEncontrado);
            cliente.put("W_CTE-TELNUMBER3",valorNoEncontrado);
            cliente.put("W_CTE-TOGRU",valorNoEncontrado);
            cliente.put("W_CTE-UPDAT",valorNoEncontrado);
            cliente.put("W_CTE-VKBUR",cursor.getString(cursor.getColumnIndex("VKBUR")) );
            cliente.put("W_CTE-VKGRP",cursor.getString(cursor.getColumnIndex("VKGRP")) );
            cliente.put("W_CTE-VKORG",cursor.getString(cursor.getColumnIndex("VKORG")) );
            cliente.put("W_CTE-VSBED",cursor.getString(cursor.getColumnIndex("VSBED")) );
            cliente.put("W_CTE-VWERK",cursor.getString(cursor.getColumnIndex("VWERK")) );
            cliente.put("W_CTE-WAERS",valorNoEncontrado);
            cliente.put("W_CTE-XZVER",valorNoEncontrado);
            cliente.put("W_CTE-ZGPOCANAL",cursor.getString(cursor.getColumnIndex("ZZGPOCANAL")) );
            cliente.put("W_CTE-ZTERM",cursor.getString(cursor.getColumnIndex("ZTERM")) );
            cliente.put("W_CTE-ZTPOCANAL",cursor.getString(cursor.getColumnIndex("ZZTPOCANAL")) );
            cliente.put("W_CTE-ZSEGPRE",cursor.getString(cursor.getColumnIndex("ZSEGPRE")) );
            cliente.put("W_CTE-ZWELS",cursor.getString(cursor.getColumnIndex("ZWELS")) );
            cliente.put("W_CTE-ZZAUART",cursor.getString(cursor.getColumnIndex("ZZAUART")) );
            cliente.put("W_CTE-ZZBLOQU",valorNoEncontrado);
            cliente.put("W_CTE-ZZCANAL",cursor.getString(cursor.getColumnIndex("ZCANAL")) );
            cliente.put("W_CTE-ZZCATFOCO",cursor.getString(cursor.getColumnIndex("ZZCATFOCO")) );
            cliente.put("W_CTE-ZZCRMA_LAT",cursor.getString(cursor.getColumnIndex("ZZCRMA_LAT")) );
            cliente.put("W_CTE-ZZCRMA_LONG",cursor.getString(cursor.getColumnIndex("ZZCRMA_LONG")) );
            cliente.put("W_CTE-ZZENT1",valorNoEncontrado);
            cliente.put("W_CTE-ZZENT2",cursor.getString(cursor.getColumnIndex("ZZENT2")) );
            cliente.put("W_CTE-ZZENT3",valorNoEncontrado);
            cliente.put("W_CTE-ZZENT4",valorNoEncontrado);
            cliente.put("W_CTE-ZZENT5",valorNoEncontrado);
            cliente.put("W_CTE-ZZERDAT",valorNoEncontrado);
            cliente.put("W_CTE-ZZGERENTE",valorNoEncontrado);
            cliente.put("W_CTE-ZZINTCO",valorNoEncontrado);
            cliente.put("W_CTE-ZZINTTACT",valorNoEncontrado);
            cliente.put("W_CTE-ZZJEFATURA",valorNoEncontrado);
            cliente.put("W_CTE-ZZOCCONS",cursor.getString(cursor.getColumnIndex("ZOCCONS")) );
            cliente.put("W_CTE-ZZREJA",cursor.getString(cursor.getColumnIndex("ZZREJA")) );
            cliente.put("W_CTE-ZZSEGCOM",cursor.getString(cursor.getColumnIndex("ZSEGCOM")) );
            cliente.put("W_CTE-ZZSEGDESC",cursor.getString(cursor.getColumnIndex("ZSEGDESC")) );
            cliente.put("W_CTE-ZZSEGEXH",cursor.getString(cursor.getColumnIndex("ZSEGEXH")) );
            cliente.put("W_CTE-ZZSEGPDE",cursor.getString(cursor.getColumnIndex("ZSEGPDE")) );
            cliente.put("W_CTE-ZZSEGPDV",cursor.getString(cursor.getColumnIndex("ZSEGPDV")) );
            cliente.put("W_CTE-ZZSEGPORT",cursor.getString(cursor.getColumnIndex("ZSEGPORT")) );
            cliente.put("W_CTE-ZZSEGPRE",valorNoEncontrado);
            cliente.put("W_CTE-ZZSHARE",valorNoEncontrado);
            cliente.put("W_CTE-ZZSTAT",valorNoEncontrado);
            cliente.put("W_CTE-ZZSUBUNNEG",valorNoEncontrado);
            cliente.put("W_CTE-ZZTIPSERV",valorNoEncontrado);
            cliente.put("W_CTE-ZZTFISI",valorNoEncontrado);
            cliente.put("W_CTE-ZZUNNEG",valorNoEncontrado);
            cliente.put("W_CTE-ZZZONACOST",cursor.getString(cursor.getColumnIndex("ZZZONACOST")) );
            cliente.put("W_CTE-ZZKEYACC",cursor.getString(cursor.getColumnIndex("ZKEYACC")) );
            cliente.put("W_CTE-ZIBASE",removeLeadingZeroes(cursor.getString(cursor.getColumnIndex("ZIBASE"))) );
            cliente.put("W_CTE-ZADICI",valorNoEncontrado);
            cliente.put("W_CTE-ZZUDATE",valorNoEncontrado);
            cliente.put("W_CTE-AUFSD",cursor.getString(cursor.getColumnIndex("AUFSD")) );
            cliente.put("W_CTE-LIFSD",valorNoEncontrado);
            cliente.put("W_CTE-FAKSD",valorNoEncontrado);
            cliente.put("W_CTE-CASSD",valorNoEncontrado);
            cliente.put("W_CTE-LOEVM",cursor.getString(cursor.getColumnIndex("LOEVM")) );
            cliente.put("W_CTE-TELF2",cursor.getString(cursor.getColumnIndex("TELF2")) );
            cliente.put("W_CTE-VTWEG",cursor.getString(cursor.getColumnIndex("VTWEG")) );
            cliente.put("W_CTE-SPART",cursor.getString(cursor.getColumnIndex("SPART")) );
            cliente.put("W_CTE-PERFK",valorNoEncontrado);
            cliente.put("W_CTE-KVGR4",valorNoEncontrado);
            cliente.put("W_CTE-KONDA",cursor.getString(cursor.getColumnIndex("KONDA")) );
            cliente.put("W_CTE-RUTAHH",PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH",""));
            //cliente.put("W_CTE-ZZESQUINA",cursor.getString(cursor.getColumnIndex("ZZESQUINA")) );

            formList.add(cliente);
        }
        cursor.close();
        return  formList;
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
            solicitud.put("W_CTE-KLIMK",cursor.getString(cursor.getColumnIndex("W_CTE-KLIMK")) != null ? cursor.getString(cursor.getColumnIndex("W_CTE-KLIMK")) : "" );
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

            formList.add(solicitud);
        }
        cursor.close();
        return  formList;
    }

    public ArrayList<HashMap<String, String>> getSolicitudes(){
        //SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> formList = new ArrayList<>();
        String query = "SELECT s.idform , s.id_solicitud, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, f.Descripcion, " +
                "CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo, s.feccre, s.fecfin " +
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
        String query = "SELECT s.idform, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, s.id_solicitud, f.Descripcion, CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo " +
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

        String query = "SELECT s.idform, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NULL THEN CASE WHEN fo.[W_CTE-NAME1] IS NULL THEN s.[W_CTE-NAME3] ELSE fo.[W_CTE-NAME1] END ELSE s.[W_CTE-NAME1] END as nombre, s.estado as estado, s.tipform, s.id_solicitud, f.Descripcion, CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, f.ind_credito, f.ind_modelo, s.feccre, s.fecfin " +
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

        String query = "SELECT s.idform, s.[W_CTE-KUNNR] as codigo, CASE WHEN s.[W_CTE-NAME1] IS NOT NULL THEN s.[W_CTE-NAME1] ELSE CASE WHEN fo.[W_CTE-NAME1] IS NOT NULL THEN fo.[W_CTE-NAME1] ELSE s.[W_CTE-NAME3] END END as nombre, s.estado as estado, s.tipform, s.id_solicitud, f.Descripcion, CASE WHEN s.[W_CTE-STCD1] IS NULL THEN fo.[W_CTE-STCD1] ELSE s.[W_CTE-STCD1] END as id_fiscal, s.feccre, s.fecfin " +
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
            user.put("feccre",cursor.getString(8));
            user.put("fecfin",cursor.getString(9));
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

    public ArrayList<HashMap<String, String>> getCamposPestana(String id_formulario, String pestana){
        ArrayList<HashMap<String, String>> clientList = new ArrayList<>();
        String BUKRS = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","");
        String KTOKD = PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD","");
        /*String query = "SELECT c.campo, c.nombre, c.tipo_input, c.id_seccion, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION FROM configuracion c" +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","")+"' and cc.ktokd = 'RCMA')" +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion)" +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip)" +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo))" +
                " WHERE id_formulario = "+id_formulario+" AND c.panta = '"+pestana+"'" +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO')"+
                " ORDER BY c.panta, s.orden_hh, c.orden_hh";*/
        String query = "SELECT * FROM (" +
                "SELECT DISTINCT c.bukrs, c.panta, s.orden_hh as orden_seccion, c.orden_hh, c.campo, c.nombre, c.tipo_input, c.id_seccion, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION, c.sufijo " +
                "FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"') " +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion) " +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                " WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' AND trim(c.bukrs) = '"+BUKRS+"' " +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                "UNION " +
                "SELECT DISTINCT c.bukrs, c.panta, s.orden_hh as orden_seccion, c.orden_hh, c.campo, c.nombre, c.tipo_input, c.id_seccion, c.modificacion as modificacion, s.desc_seccion as seccion, cc.descr as descr, cc.tabla as tabla, cc.dfaul as dfaul, cc.sup as sup, cc.obl as obl, cc.vis as vis, cc.opc as opc, c.tabla_local as tabla_local, c.evento1, c.llamado1 , t.desc_tooltip as tooltip, m.DATA_TYPE, m.CHARACTER_MAXIMUM_LENGTH, m.NUMERIC_PRECISION, c.sufijo " +
                "FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(cc.panta) = ( " +
                " Select trim(cc2.panta) FROM configCampos cc2 WHERE trim(c.campo) = trim(cc2.CAMPO) AND trim(cc2.bukrs) = '"+BUKRS+"' and trim(cc2.ktokd) = '"+KTOKD+"' AND trim(cc2.panta) != trim(c.panta) LIMIT 1 " +
                " ) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"' and trim(c.campo) NOT IN ( " +
                " SELECT DISTINCT trim(c.campo) " +
                " FROM configuracion c " +
                " LEFT JOIN configCampos cc ON (trim(c.campo) = trim(cc.CAMPO) AND trim(c.panta) = trim(cc.panta) AND cc.bukrs = '"+BUKRS+"' and cc.ktokd = '"+KTOKD+"')\n" +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion) " +
                " LEFT JOIN cat_tooltips t ON (t.id_bukrs = cc.bukrs AND t.id_tooltip = c.tooltip) " +
                " LEFT JOIN TABLES_META_DATA m ON (trim(m.COLUMN_NAME) = trim(c.campo)) " +
                " WHERE id_formulario = "+id_formulario+" AND trim(c.panta) = '"+pestana+"' AND trim(c.bukrs) = '"+BUKRS+"' " +
                " AND trim(cc.campo) NOT IN ('W_CTE-DUPLICADO','W_CTE-NOTIFICANTES') " +
                " )) " +
                " LEFT JOIN Seccion s ON (s.id_seccion = c.id_seccion) " +
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
            user.put("id_seccion",cursor.getString(cursor.getColumnIndex("id_seccion")) != null ? cursor.getString(cursor.getColumnIndex("id_seccion")): "");
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
                " where id_formulario = "+id_formulario+" " +
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
            selectQuery = "SELECT * " +
                    " FROM " + tabla +" a INNER JOIN" +
                    " EX_T_RUTAS_VP AS b ON (trim(a.zone1) = trim(b.zroute_rep) OR trim(a.zone1) = trim(b.zroute_pr))";
        }
        //Cadena = cat_zesdvt_00561, Keyaccount = cat_ztmdcmc_00038t
        if(tabla.equals("cat_zesdvt_00561") /*&& PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("F443")*/){
            filtros.append(" AND trim(hkunnr) = '"+VariablesGlobales.getCadenaRM()+"' AND zzkeyacc = 'CA002'");
        }
        if(tabla.equals("cat_ztmdcmc_00038t") /*&& PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","").trim().equals("F443")*/){
            filtros.append(" AND trim(zkeyacc) = 'CA002'");
        }
        //TODO si entran formales D y ABC al app se debe cambiar esta manera de filtrar
        if(tabla.equals("cat_knvv")){
            filtros.append(" AND (zterm like '%L%' OR zterm like '%00')");
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
            selectQuery = "SELECT * " +
                    " FROM " + tabla +" a INNER JOIN" +
                    " EX_T_RUTAS_VP AS b ON (trim(a.zone1) = trim(b.zroute_rep) OR trim(a.zone1) = trim(b.zroute_pr))";
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
                    lista.put("descripcion",cursor.getString(0).trim() + " - " + cursor.getString(1).trim());//1era y 2da columna del query
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

    public ArrayList<OpcionSpinner> getEstadosCatalogoParaSpinner(){
        ArrayList<HashMap<String, String>> listaCatalogo = new ArrayList<>();
        ArrayList<OpcionSpinner> listaopciones = new ArrayList<>();
        // Select All Query
        try {
            //SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = mDataBase.rawQuery("Select estado as id, estado as descripcion from formHvKof_solicitud group by estado", null);//selectQuery,selectedArguments
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String,String> lista = new HashMap<>();
                    lista.put("id",cursor.getString(0).trim());//1era columna del query
                    lista.put("descripcion",cursor.getString(1).trim());//1era y 2da columna del query
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
            HashMap<String,String> seleccione = new HashMap<>();
            seleccione.put("id","");
            seleccione.put("descripcion","Seleccione...");
            listaCatalogo.add(seleccione);
        }
        return listaopciones;
    }
    public List<KeyPairBoolData> getEstadosCatalogoParaMultiSpinner(){
        List<KeyPairBoolData> listaCatalogo = new ArrayList<KeyPairBoolData>();
        //List<String> listaopciones = new ArrayList<>();
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

            /*int selectedIndex = 0;
            for (int j = 0; j < listaCatalogo.size(); j++){
                listaopciones.add(new OpcionSpinner(listaCatalogo.get(j).get("id"), listaCatalogo.get(j).get("descripcion")) );
            }*/

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
        if(existeColumna(tabla,"banks")){
            filtros.append(" AND banks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"talnd")){
            filtros.append(" AND talnd = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")).append("'");
        }
        if(existeColumna(tabla,"werks")){
            filtros.append(" AND werks = '").append(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_VWERK","")).append("'");
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
            if(columnas.getString(1).trim().equals(columna.trim())){
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

    public boolean setPropiedadesDeUsuario(){
        boolean ret = false;
        String userName = VariablesGlobales.UsuarioMC2UsuarioHH(mContext, PreferenceManager.getDefaultSharedPreferences(mContext).getString("user","").trim().toUpperCase());
        String query = "SELECT RouteID FROM t_i_users WHERE upper(trim(UserName)) = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String [] {userName});
        String rutaAsignada = "";
        while (cursor.moveToNext()){
            rutaAsignada = cursor.getString(0);
        }
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_RUTAHH", rutaAsignada ).apply();


        query = "SELECT * FROM EX_T_RUTAS_VP WHERE zroute_pr = ?";
        cursor = mDataBase.rawQuery(query, new String [] {rutaAsignada});

        while (cursor.moveToNext()){
            ret = true;
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_KTOKD", VariablesGlobales.getKtokd() ).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VKORG", cursor.getString(cursor.getColumnIndex("vkorg")) ).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_BUKRS", vkorgToBukrs(cursor.getString(cursor.getColumnIndex("vkorg")) )).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_LAND1", vkorgToLand1(cursor.getString(cursor.getColumnIndex("vkorg")) )).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_KDGRP", cursor.getString(cursor.getColumnIndex("kdgrp")) ).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_KVGR3", cursor.getString(cursor.getColumnIndex("kvgr3")) ).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_BZIRK", cursor.getString(cursor.getColumnIndex("bzirk")) ).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VKBUR", cursor.getString(cursor.getColumnIndex("vkbur")) ).apply();
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VKGRP", cursor.getString(cursor.getColumnIndex("vkgrp")) ).apply();
            if(!cursor.getString(cursor.getColumnIndex("vwerks")).isEmpty()){
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VWERK", cursor.getString(cursor.getColumnIndex("vwerks")) ).apply();
            }
            ArrayList<HashMap<String, String>> valores = getValoresKOFSegunZonaVentas(cursor.getString(cursor.getColumnIndex("bzirk")) );
            if(valores.size() == 0){
                ret = false;
            }else {
                PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_VWERK", valores.get(0).get("VWERK")).apply();
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
            }
            PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("W_CTE_AREACREDITO", areactrlcred ).apply();
        }
        cursor.close();
        return ret;
    }

    //Formularios de modificacion permitidos para la HH
    public ArrayList<HashMap<String, String>> getModificacionesPermitidas(){
        ArrayList<HashMap<String, String>> flujoList = new ArrayList<>();
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and ind_modelo = 'M' and ind_credito = 0 order by orden";
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
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and ind_modelo = 'M' and ind_credito = 1 order by orden";
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
        String query = "SELECT * FROM flujo WHERE permitirHH = 1 and ind_modelo = 'E' order by orden";
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
        String query = "SELECT * FROM "+VariablesGlobales.getTABLA_ADJUNTOS_SOLICITUD()+" WHERE id_solicitud = ?";
        Cursor cursor = mDataBase.rawQuery(query,new String[]{id_solicitud});
        while (cursor.moveToNext()){
            Adjuntos adjunto = new Adjuntos();
            adjunto.setId_solicitud(cursor.getString(cursor.getColumnIndex("id_solicitud")) );
            adjunto.setType(cursor.getString(cursor.getColumnIndex("tipo")) );
            adjunto.setName(cursor.getString(cursor.getColumnIndex("nombre")) );
            adjunto.setImage(cursor.getBlob(cursor.getColumnIndex("imagen")) );

            adjuntosList.add(adjunto);
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
        String query = "SELECT * FROM cat_impstos WHERE taxkd = 1 AND talnd = '"+PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_LAND1","")+"'";
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
        String query = "select vpore as vptyp, '' as descripcion, '"+metodo+"' as kvgr4, '' as ruta,'' as fec_frec, '" + fechaSistema + "' as f_ico, '99991231' as f_fco, '' as f_ini, '' as f_fin, '1' as fcalid FROM cat_ztsdvto_00185_x WHERE zopcional != 'X' and vkorg = '" + vkorg.trim() + "' and kvgr5 = '" + modalidad + "'";
        Cursor cursor = mDataBase.rawQuery(query,null);

        while (cursor.moveToNext()){
            Visitas visita = new Visitas();
            visita.setVptyp(cursor.getString(cursor.getColumnIndex("vptyp")) );
            if(EsTipodeReparto(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BZIRK",""), visita.getVptyp()))
                visita.setRuta(cursor.getString(cursor.getColumnIndex("ruta")) );
            else
                visita.setRuta(PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_RUTAHH",""));
            visita.setKvgr4(cursor.getString(cursor.getColumnIndex("kvgr4")) );
            visita.setF_ico(cursor.getString(cursor.getColumnIndex("f_ico")) );
            visita.setF_fco(cursor.getString(cursor.getColumnIndex("f_fco")) );
            visita.setF_ini(cursor.getString(cursor.getColumnIndex("f_ini")) );
            visita.setF_fin(cursor.getString(cursor.getColumnIndex("f_fin")) );
            visita.setFcalid(cursor.getString(cursor.getColumnIndex("fcalid")) );

            visitasList.add(visita);
        }
        cursor.close();
        return  visitasList;
    }

    public boolean EsTipodeReparto(String agencia, String tiporuta)
    {
        String query = "select vwerks, zroute_rep FROM EX_T_RUTAS_VP WHERE bzirk = ? AND vptyp = ?";
        Cursor cursor = mDataBase.rawQuery(query, new String[]{agencia, tiporuta});
        boolean retorno = false;
        if (cursor.moveToNext()){
            retorno = !cursor.getString(cursor.getColumnIndex("vwerks")).isEmpty() && !cursor.getString(cursor.getColumnIndex("vwerks")).trim().equals("") && !cursor.getString(cursor.getColumnIndex("zroute_rep")).trim().equals("");
        }
        cursor.close();
        //Caso exclusivo para tipos de visita autoventa, donde la preventa y el reparto son lo mismo.
        if(tiporuta.contains("ZAT")){
            retorno = false;
        }
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
                porpais = " AND SUBSTR(CITY1,1,1) = 'C' OR CITY1 = 'PCCI'";
                break;
            case "NI":
                porpais = " AND SUBSTR(CITY1,1,1) = 'N'";
                break;
            case "GT":
                porpais = " AND SUBSTR(CITY1,1,1)  NOT IN ('C','P','N','G')";
                break;
            case "PA":
                porpais = " AND SUBSTR(CITY1,1,1) = 'P'";
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
        micursor.close();
        return  preguntasList;
    }

    public ArrayList<HashMap<String, String>> getRespuestasEncuesta(String id_solicitud){
        ArrayList<HashMap<String, String>> preguntasList = new ArrayList<>();
        //String sql_encuesta = "select p.zid_quest, p.text as quest_text,r.zid_resp, r.text as resp_text from cat_preguntas_isscom p inner join cat_respuestas_isscom r ON (p.zid_grupo = r.zid_grupo AND p.zid_quest = r.zid_quest) where trim(p.zid_grupo) = '" + grupo_isscom + "' and bukrs = '" + VariablesGlobales.getSociedad() + "'";
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
        String sql_encuesta = "select zid_quest,text,text2  from cat_preguntas_gec p where bukrs = '" + PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS","") + "'";
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

    public ArrayList<HashMap<String, String>> getConfigExcepciones(String tiposolicitud) {
        ArrayList<HashMap<String, String>> excepciones = new ArrayList<>();
        String sql_encuesta = "SELECT * FROM ConfigExcepciones WHERE bukrs = ? AND ktokd = ? AND tipform = ?";
        //String sql_encuesta2 = "select zid_quest, monto from encuesta_gec_solicitud p";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,new String[]{PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_BUKRS",""),
                PreferenceManager.getDefaultSharedPreferences(mContext).getString("W_CTE_KTOKD","RCMA"),
                tiposolicitud});
        //bukrs, ktokd, tipform, campo, VIS, OBL, OPC, SUP
        while (cursor.moveToNext()){
            HashMap<String,String> resp = new HashMap<>();

            resp.put("bukrs",cursor.getString(cursor.getColumnIndex("bukrs")) != null ? cursor.getString(cursor.getColumnIndex("bukrs")).trim() : "");
            resp.put("ktokd",cursor.getString(cursor.getColumnIndex("ktokd")) != null ? cursor.getString(cursor.getColumnIndex("ktokd")).trim() : "");
            resp.put("tipform",cursor.getString(cursor.getColumnIndex("tipform")) != null ? cursor.getString(cursor.getColumnIndex("tipform")) .trim(): "");
            resp.put("campo",cursor.getString(cursor.getColumnIndex("campo")) != null ? cursor.getString(cursor.getColumnIndex("campo")).trim() : "");
            resp.put("bzirk",cursor.getString(cursor.getColumnIndex("bzirk")) != null ? cursor.getString(cursor.getColumnIndex("bzirk")).trim() : "");
            resp.put("vis",cursor.getString(cursor.getColumnIndex("VIS")) != null ? cursor.getString(cursor.getColumnIndex("VIS")).trim() : "");
            resp.put("obl",cursor.getString(cursor.getColumnIndex("OBL")) != null ? cursor.getString(cursor.getColumnIndex("OBL")).trim() : "");
            resp.put("opc",cursor.getString(cursor.getColumnIndex("OPC")) != null ? cursor.getString(cursor.getColumnIndex("OPC")).trim() : "");
            resp.put("sup",cursor.getString(cursor.getColumnIndex("SUP")) != null ? cursor.getString(cursor.getColumnIndex("SUP")).trim() : "");
            resp.put("dfaul",cursor.getString(cursor.getColumnIndex("DFAUL")) != null ? cursor.getString(cursor.getColumnIndex("DFAUL")).trim() : "");
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
        String sql_encuesta = "select min_adjuntos as cantidad from flujo WHERE id_form = ?";
        Cursor cursor = mDataBase.rawQuery(sql_encuesta,new String[]{idform});
        while (cursor.moveToNext()){
            cantidad = cursor.getInt(0);
        }
        cursor.close();
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
        String query = "SELECT id, bukrs, ktokd, tipform, bzirk, rtrim(campo) as campo, VIS, OBL, OPC, SUP, rtrim(DFAUL) as DFAUL FROM ConfigExcepciones WHERE (bukrs = '" + sociedad + "') AND (ktokd = '" + ktokd + "') AND (campo = '" + campo + "') AND (bzirk = '" + agencia + "') AND tipform = '" + formulario + "'";

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
}
