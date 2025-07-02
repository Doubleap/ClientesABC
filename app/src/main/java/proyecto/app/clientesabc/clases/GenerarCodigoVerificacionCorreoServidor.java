package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;

public class GenerarCodigoVerificacionCorreoServidor extends AsyncTask<Void,String,String> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private String sociedad;
    private String cliente;
    private String correo;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    ArrayList<JsonObject> estructuras;
    AlertDialog dialog;
    ImageView boton;
    public GenerarCodigoVerificacionCorreoServidor(WeakReference<Context> c, WeakReference<Activity> a, String sociedad, String cliente, String correo, ImageView btn){
        this.context = c;
        this.activity = a;
        this.sociedad = sociedad;
        this.cliente = cliente;
        this.correo = correo;
        this.boton = btn;
    }

    @Override
    protected String doInBackground(Void... voids) {
        ArrayList<JsonArray> estructurasSAP = new ArrayList<>();
        String jsonrespuesta = "";
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        try {
            publishProgress("Estableciendo comunicación...");
            System.out.println("Estableciendo comunicación para enviar archivos...");
            String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
            if(mensaje.equals("")) {
                socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip", ""), Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto", "")));

                System.out.println("Creando Streams de datos...");
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                //Comando String que indicara que se quiere realizar una Sincronizacion
                publishProgress("Comunicacion establecida...");
                //Enviar Pais de procedencia
                dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("CONFIG_SOCIEDAD",VariablesGlobales.getSociedad()));
                dos.flush();
                //Version con la que quiere transmitir
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-6"));
                dos.writeUTF(dateFormat.format(BuildConfig.BuildDate));
                dos.flush();
                //Enviar Ruta que se quiere sincronizar
                dos.writeUTF(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH", ""));
                dos.flush();

                dos.writeUTF("GenerarCodigoVerificacionCorreo");
                dos.flush();

                //Enviar Codigo de cliente
                dos.writeUTF(String.format("%10s", String.valueOf(cliente)).replace(' ', '0'));
                dos.flush();
                //Enviar correo electronico
                dos.writeUTF(correo);
                dos.flush();

                dos.writeUTF("FIN");
                dos.flush();

                //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la consulta para modificacion
                long s = dis.readLong();
                if (s < 0) {
                    publishProgress("Error al generar código de verificación en el servidor...");
                    s = dis.readLong();
                    byte[] e = new byte[(int) s];
                    dis.readFully(e);
                    String error = new String(e);
                    xceptionFlag = true;
                    messageFlag = "Error: " + error;
                } else {
                /*ORDEN DE ESTRUCTURAS SAP RECIBIDAS
                        String jsonCliente = 0;
                        String jsonNotaEntrega = 1;
                        String jsonFactura = 2;
                        String jsonTelefonos = 3;
                        String jsonFaxes = 4;
                        String jsonContactos = 5;
                        String jsonInterlocutores = 6;
                        String jsonImpuestos = 7;
                        String jsonBancos = 8;
                        String jsonVisitas = 9;*/

                    //Toda la info de cliente
                    publishProgress("Iniciando descarga...");
                    byte[] r = new byte[(int) s];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != s) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (s / 1024f)) * (offset / 1024f)) + "% ("+String.format("%.2f", (offset/1000000.0))+" de "+String.format("%.2f", (s/1000000.0))+")");
                    }
                    dos.writeUTF("END");
                    dos.flush();
                    publishProgress("Procesando datos recibidos...");

                    jsonrespuesta = new String(r);
                    //Gson gson = new Gson();
                    ///estructurasSAP.add(gson.fromJson(jsoncliente, JsonArray.class));
                    //publishProgress("Procesando datos cliente..");

                }
            }else{
                xceptionFlag = true;
                messageFlag = mensaje;
            }
            publishProgress("Proceso Terminado...");
        } catch (IOException e) {
            xceptionFlag = true;
            messageFlag = e.getMessage();
            e.printStackTrace();
        }

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
        return jsonrespuesta;
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
                Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
                activity.get().finish();
            }
        });
        dialog = builder.create();
        if(!activity.get().isFinishing()) {
            dialog.show();
        }
    }
    @Override
    protected void onPostExecute(String mensajes) {
        super.onPostExecute(mensajes);
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
        if(xceptionFlag){
            //activity.get().finish();
            Toasty.error(context.get(),messageFlag,Toast.LENGTH_LONG).show();
        }else {
                try {
                    Toasty.success(context.get(), "Código enviado al correo "+correo+".", Toast.LENGTH_LONG).show();
                    boton.setBackgroundTintList(ColorStateList.valueOf(context.get().getResources().getColor(R.color.devuelto, null)));
                    boton.setOnClickListener((View.OnClickListener) view -> {
                        //Abrir dialogo para digitar el codigo recibido.
                        displayDialogVerificarCodigo(cliente, correo);
                    });
                } catch (Exception e) {
                    Toasty.error(context.get(), "Error al verificar el correo: " + e.getMessage()).show();
                }
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
    // Method to send an HTML email to a single recipient without attachment
    public void sendEmailSingleRecipient(String recipient, String subject, String htmlBody) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient}); // Single recipient
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

        // Adding HTML body
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlBody));
        emailIntent.setType("text/html");

        context.get().startActivity(emailIntent);
    }
    public void displayDialogVerificarCodigo(final String codigoCliente, final String correo) {
        final Dialog d=new Dialog(context.get());
        d.setContentView(R.layout.verificar_codigo_dialog_layout);
        //INITIALIZE VIEWS
        final TextView title = d.findViewById(R.id.title);
        final MaskedEditText codigo = (MaskedEditText) d.findViewById(R.id.codigo);

        Button saveBtn= d.findViewById(R.id.saveBtn);

        //SAVE, Se valida el valor no se vacio y se valida contra el codigo de verificacion generado con el sistema, y se realiza la validacion
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo_txt = codigo.getText().toString();
                if(codigo_txt.isEmpty()){
                    Toasty.warning(v.getContext(), "Debe digitar el código enviado por correo a la direccion "+correo, Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    //Realizar el llamada el servicio de la aplicacion para validar el codigo digitado
                    if (PreferenceManager.getDefaultSharedPreferences(context.get()).getString("tipo_conexion","").equals("api")) {
                        VerificarCodigoCorreoAPI verificador = new VerificarCodigoCorreoAPI(context, activity, sociedad, cliente, correo, codigo_txt, boton);
                        verificador.execute();
                    } else {
                        VerificarCodigoCorreoServidor verificador = new VerificarCodigoCorreoServidor(context, activity, sociedad, cliente, correo, codigo_txt, boton);
                        verificador.execute();
                    }

                } catch(Exception e) {
                    Toasty.error(v.getContext(), "No se pudo guardar la alerta al equipo!."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                d.dismiss();
            }
        });

        //SHOW DIALOG
        d.show();
        Window window = d.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

}