package proyecto.app.clientesabc.clases;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.modelos.EquipoFrio;
import retrofit2.Call;
import retrofit2.Response;

public class TransmisionLecturaCensoAPI extends AsyncTask<Void,String,Void> {

    private String solicitudes_procesadas;
    private WeakReference<ListView> listView;
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private EquipoFrio equipoFrio;
    private String id_solicitud;
    private boolean xceptionFlag = false;
    private String errorFlag = "Transmision NO pudo realizarse.";
    private DataBaseHelper mDBHelper;
    AlertDialog dialog;

    public TransmisionLecturaCensoAPI(WeakReference<Context> context, WeakReference<Activity> act, EquipoFrio equipoFrio){
        this.context = context;
        this.activity = act;
        this.equipoFrio = equipoFrio;
        mDBHelper = new DataBaseHelper(this.context.get());
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        TextView v = (TextView) dialog.findViewById(R.id.mensaje_espera);
        v.setText(progress[0]);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        System.out.println("ArrayList de Archivos");
        ArrayList<File> files = new ArrayList<>();
        errorFlag = "Transmision NO pudo realizarse.";

        publishProgress("Estableciendo comunicación...");
        String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
        if (mensaje.equals("")) {
            SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase(context.get().getDatabasePath("TRANSMISION_").getPath() + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""), null, SQLiteDatabase.CREATE_IF_NECESSARY);

            //Crear una base de datos solo para los datos que deben ser transmitidos
            publishProgress("Extrayendo datos...");
            String sqlDrop = "DROP TABLE IF EXISTS 'CensoEquipoFrio';";
            String sqlAttach = "ATTACH DATABASE '" + context.get().getDatabasePath("FAWM_ANDROID_2").getPath() + "' AS fromDB";
            String[] droptables = sqlDrop.split(";");
            for (String query : droptables) {
                mDataBase.execSQL(query);
            }
            mDataBase.execSQL(sqlAttach);

            //Tabla de CensoEquipoFrio nueva a partir de del año 2024
            String sqlCreate = "";
            if(equipoFrio.getSerge() == null)
                sqlCreate = "CREATE TABLE CensoEquipoFrio AS SELECT * FROM fromDB.CensoEquipoFrio WHERE kunnr_censo = '"+equipoFrio.getKunnrCenso()+"' AND num_placa = '"+equipoFrio.getSerge()+"' AND fecha_lectura = '"+equipoFrio.getFechaLectura()+"' AND transmitido = '0'";
            else
                sqlCreate = "CREATE TABLE CensoEquipoFrio AS SELECT * FROM fromDB.CensoEquipoFrio WHERE kunnr_censo = '"+equipoFrio.getKunnrCenso()+"' AND num_placa = '"+equipoFrio.getNumPlaca()+"' AND fecha_lectura = '"+equipoFrio.getFechaLectura()+"' AND transmitido = '0'";
            mDataBase.execSQL(sqlCreate);

            publishProgress("Empaquetando datos...");
            String pathFileToZip = context.get().getApplicationInfo().dataDir + "/databases/";
            FileHelper.zip(pathFileToZip, context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_" + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", "") + ".zip", false);
            File myFile = new File(context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_" + PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", "") + ".zip");
            //File myFile = new File(context.get().getApplicationInfo().dataDir + "/databases/", "TRANSMISION_"+ PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH",""));

            //File externalStorage = Environment.getExternalStorageDirectory();
            //String externalStoragePath = externalStorage.getAbsolutePath();
            //File myFile = new File(externalStoragePath + File.separator + context.get().getPackageName()+ File.separator+"Transmision", "PRUEBA_COPIA");

            files.add(myFile);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));
            String version = "";

            version = dateFormat.format(BuildConfig.BuildDate).replace(":", "COLON").replace("-", "HYPHEN");

            InterfaceApi apiService = ServiceGenerator.createService(context, activity, InterfaceApi.class, PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", ""));

            //RequestBody fbody = RequestBody.create(MediaType.parse("application/octet-stream"), myFile);

            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse("application/zip"),
                            myFile
                    );

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", myFile.getName(), requestFile);

            // add another part within the multipart request
            String descriptionString = "hello, this is description speaking";
            RequestBody description = RequestBody.create(MultipartBody.FORM, descriptionString);

            // finally, execute the request
            Call<ResponseBody> call = apiService.TransmisionLecturaCenso(description, body, PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD", VariablesGlobales.getSociedad()), PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""), version);

            Response<ResponseBody> response;

            try {
                response = call.execute();
                if (response.body() != null && !response.body().contentType().toString().equals("text/html")) {
                    publishProgress("Respuesta recibida...");
                    long fileSize = response.body().contentLength();
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(response.body().byteStream()));

                    byte[] r = new byte[(int) fileSize];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != fileSize) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "%");
                    }
                    publishProgress("Actualizando censo...");
                    //solicitudes_procesadas = new String(r, Charset.defaultCharset());
                } else {
                    xceptionFlag = true;
                    if (response.body() != null)
                        errorFlag = response.body().string();
                    else if (response.errorBody() != null)
                        errorFlag = response.errorBody().string();
                }
            } catch (Exception e) {
                xceptionFlag = true;
                errorFlag = e.getMessage();
                e.printStackTrace();
            }
            publishProgress("Esperando respuesta censo...");
            //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la sincronizacion

        } else {
            xceptionFlag = true;
            errorFlag = mensaje;
        }

        publishProgress("Transmision Censo Finalizada...");
        Log.i("===end of start ====", "==");

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setCancelable(false);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                errorFlag = "Proceso cancelado por el usuario.";
                cancel(false);
            }
        });
        builder.setView(R.layout.layout_loading_dialog);
        dialog = builder.create();
        if(!activity.get().isFinishing()) {
            dialog.show();
        }

    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(xceptionFlag){
            Toasty.error(context.get(),errorFlag,Toast.LENGTH_LONG).show();
        }
        else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            PreferenceManager.getDefaultSharedPreferences(context.get()).edit().putString("ultimaTransmision", dateFormat.format(date)).apply();
            Toasty.success(context.get(),"Transmision Finalizada Correctamente!",Toast.LENGTH_LONG).show();
            //Adicionalmente se debe actualizar el estado de las solicitudes enviadas para que no se dupliquen.
            mDBHelper.ActualizarEstadosSolicitudesTransmitidas(solicitudes_procesadas);
        }
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }
        if(dialog.isShowing())
            dialog.hide();
        //activity.get().recreate();
        Intent intent = activity.get().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.get().finish();
        activity.get().overridePendingTransition(0, 0);
        startActivity(context.get(), intent, null);
        activity.get().overridePendingTransition(0, 0);
    }

    public void EnableWiFi(){
        WifiManager wifimanager = (WifiManager) context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(true);
    }

    public void DisableWiFi(){
        WifiManager wifimanager = (WifiManager) context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifimanager.setWifiEnabled(false);
    }
}