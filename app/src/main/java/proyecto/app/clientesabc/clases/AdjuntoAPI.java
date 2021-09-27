package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.Interfaces.InterfaceApi;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdjuntoAPI extends AsyncTask<Void,String,Bitmap> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private ImageView imagen;
    private TextView tv_nombre;
    private String nombre;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    private Bitmap adjunto;
    private byte[] adjuntoArray;
    AlertDialog dialog;
    public AdjuntoAPI(WeakReference<Context> c, WeakReference<Activity> a, ImageView imagen, TextView tv_nombre){
        this.context = c;
        this.activity = a;
        this.imagen = imagen;
        this.tv_nombre = tv_nombre;
    }
    @Override
    protected Bitmap doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        publishProgress("Estableciendo comunicación...");
        System.out.println("Estableciendo comunicación para enviar archivos...");
        String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
        if(mensaje.equals("")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String version = "";
            nombre = tv_nombre.getText().toString();
            version = dateFormat.format(BuildConfig.BuildDate).replace(":","COLON").replace("-","HYPHEN");

            InterfaceApi adjuntoService = ServiceGenerator.createService(context, activity,InterfaceApi.class, PreferenceManager.getDefaultSharedPreferences(context.get()).getString("TOKEN", ""));

            Call<ResponseBody> call = adjuntoService.Adjunto(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD",""), PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""), version, nombre);
            Response<ResponseBody> response;
            try {
                response = call.execute();
                if (!response.body().contentType().toString().equals("text/html")) {
                    publishProgress("Recibiendo datos...");

                    long fileSize = response.body().contentLength();
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(response.body().byteStream()));

                    byte[] r = new byte[(int) fileSize];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != fileSize) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (fileSize / 1024f)) * (offset / 1024f)) + "%");
                    }

                    if (nombre.toLowerCase().contains(".pdf")) {
                        adjuntoArray = r;
                    } else {
                        adjunto = BitmapFactory.decodeByteArray(r, 0, r.length);
                    }
                }else {
                    xceptionFlag = true;
                    messageFlag = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            xceptionFlag = true;
            messageFlag = mensaje;
        }
        publishProgress("Proceso Terminado...");

        Log.i("===end of start ====", "==");
        try{
            if(socket!=null && !socket.isClosed()){
                socket.close();
                Log.i("Socket cerrado", "==");
            }
        }
        catch (Exception e){
            xceptionFlag = true;
            messageFlag = e.getMessage();
            e.printStackTrace();
        }

        return adjunto;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        TextView v = (TextView) dialog.findViewById(R.id.mensaje_espera);
        v.setText(progress[0]);
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        builder.setCancelable(true); // Si quiere que el usuario espere por el proceso completo por obligacion poner en false
        builder.setView(R.layout.layout_loading_dialog);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                messageFlag = "Proceso cancelado por el usuario.";
                cancel(true);
            }
        });
        dialog = builder.create();
        if(!activity.get().isFinishing()) {
            dialog.show();
        }
    }
    @Override
    protected void onPostExecute(Bitmap adjunto) {
        super.onPostExecute(adjunto);
        if (!xceptionFlag){
            if(adjunto != null) {
                imagen.setImageBitmap(Bitmap.createScaledBitmap(adjunto, adjunto.getWidth(), adjunto.getHeight(), true));
            }else{
                File tempPDF;
                try {
                    File folder = new File(Environment.getExternalStorageDirectory(), "Download");
                    tempPDF = new File(folder, "Temp.pdf");
                    //tempPDF = File.createTempFile("temp", ".pdf", context.get().getExternalCacheDir());
                    //RandomAccessFile raf = new RandomAccessFile(tempPDF, "r");
                    tempPDF.deleteOnExit();

                    FileOutputStream fos = new FileOutputStream(tempPDF);
                    //FileOutputStream fos = context.get().openFileOutput(nombre, Context.MODE_WORLD_READABLE);
                    fos.write(adjuntoArray);
                    fos.close();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(tempPDF), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("Adjunto", Uri.fromFile(tempPDF));
                    activity.get().startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Toasty.success(context.get(),"No existe aplicacion para ver PDFs!",Toasty.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            tv_nombre.setText(nombre);
            //Toasty.success(context.get(),"Sincronizacion Exitosa!!",Toast.LENGTH_LONG).show();
        }
        else{
            Toasty.error(context.get(),"No se pudo obtener el adjunto: "+messageFlag).show();
        }
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        }
        if(dialog.isShowing()) {
            dialog.hide();
        }

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