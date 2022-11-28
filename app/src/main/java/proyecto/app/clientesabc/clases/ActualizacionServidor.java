package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.BuildConfig;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.actividades.TCPActivity;
import proyecto.app.clientesabc.modelos.Conexion;

public class ActualizacionServidor extends AsyncTask<Void,String,Void> {
    private WeakReference<Context> context;
    private WeakReference<Activity> activity;
    private boolean xceptionFlag = false;
    private String messageFlag = "";
    private ServerSocket ss;
    private Socket socket;
    AlertDialog dialog;
    int UNINSTALL_REQUEST_CODE = 1;
    public ActualizacionServidor(WeakReference<Context> c, WeakReference<Activity> a){
        this.context = c;
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Solo enviamos los datos necesarios para que la sincronizacion sepa que traer
        try {
            publishProgress("Estableciendo comunicación...");
            System.out.println("Estableciendo comunicación para enviar archivos...");
            String mensaje = VariablesGlobales.validarConexionDePreferencia(context.get());
            if (mensaje.equals("")) {
                socket = new Socket(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Ip", "").trim(), Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("Puerto", "").trim()));

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

                dos.writeUTF("Actualizacion");
                dos.flush();

                dos.writeUTF("FIN");
                dos.flush();

                //Recibiendo respuesta del servidor para saber como proceder, error o continuar con la sincronizacion
                long s = dis.readLong();
                if (s < 0) {
                    publishProgress("No se pudo descargar...");
                    s = dis.readLong();
                    byte[] e = new byte[(int) s];
                    dis.readFully(e);
                    String error = new String(e);
                    xceptionFlag = true;
                    messageFlag = "Error: " + error;
                } else {
                    byte[] r = new byte[(int) s];
                    int offset = 0;
                    int bytesRead;
                    while ((bytesRead = dis.read(r, offset, r.length - offset)) > -1 && offset != s) {
                        offset += bytesRead;
                        publishProgress("Descargando..." + String.format("%.02f", (100f / (s / 1024f)) * (offset / 1024f)) + "%");
                    }
                    dos.writeUTF("END");
                    dos.flush();
                    publishProgress("Procesando datos recibidos...");
                    File tranFileDir;
                    File externalStorage = context.get().getExternalFilesDir(null);
                    if (externalStorage != null) {
                        String externalStoragePath = externalStorage.getAbsolutePath();
                        tranFileDir = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "");
                        boolean ex = tranFileDir.mkdirs();
                        File transferFile = new File(tranFileDir, "ClientesABC");
                        OutputStream stream = new FileOutputStream(transferFile);
                        stream.write(r);
                        stream.flush();
                        stream.close();

                        dos.close();
                        //UNZIP informacion recibida
                        boolean unzip = FileHelper.unzip(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "ClientesABC", externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "");
                        final File file = new File(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "ClientesABC.apk");

                        if (unzip && file != null) {
                            Date lastModDate = new Date(file.lastModified());
                            //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date buildDate = proyecto.app.clientesabc.BuildConfig.BuildDate;
                            if (buildDate.after(lastModDate)) {
                                activity.get().runOnUiThread(new Runnable() {
                                    public void run() {
                                        DialogHandler appdialog = new DialogHandler();
                                        appdialog.Confirm(activity.get(), "Version Antigua", "Al aceptar esta instalacion estara devolviendose a una version ANTERIOR a la actual. Desea continuar con la instalacion?", "No", "Si", new ActualizacionServidor.ActualizarVersion(context.get(), file));

                                    }
                                });
                            } else if (buildDate.before(lastModDate)) {
                                publishProgress("Iniciando Instalacion...");
                                //Intentar REInstalar la aplicacion con el permiso del usuario.
                                try {
                                    activity.get().runOnUiThread(new Runnable() {
                                        public void run() {
                                            DialogHandler appdialog = new DialogHandler();
                                            appdialog.Confirm(activity.get(), "Nueva versión", "Existe una actualización de la aplicacion! Desea instalar la nueva actualizacion de la aplicacion?", "No", "Si", new ActualizacionServidor.ActualizarVersion(context.get(), file));
                                        }
                                    });

                                } catch (Exception e) {
                                    xceptionFlag = true;
                                    messageFlag = "Error Actualizando la aplicacion." + e.getMessage();
                                    e.printStackTrace();
                                }
                            } else if (buildDate.equals(lastModDate)) {
                                xceptionFlag = true;
                                messageFlag = "No se encontraron nuevas versiones de la aplicacion.";
                            } else {
                                xceptionFlag = true;
                                messageFlag = "Problemas al desempaquetar la informacion.";
                            }
                        }
                    }
                }
            } else {
                xceptionFlag = true;
                messageFlag = mensaje;
            }
            publishProgress("Proceso Terminado...");
        } catch (IOException e) {
            xceptionFlag = true;
            messageFlag = e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
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

        return null;
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
        builder.setCancelable(false); // Si quiere que el usuario espere por el proceso completo por obligacion poner en false
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
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
            Toasty.error(context.get(),"No se pudo actualizar: "+messageFlag,Toast.LENGTH_LONG).show();
        }else{
            File externalStorage = context.get().getExternalFilesDir(null);
            String externalStoragePath = externalStorage.getAbsolutePath();
            CrearArchivoConfiguracion(externalStoragePath + File.separator + context.get().getPackageName() + File.separator + "");
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

    public class ActualizarVersion implements Runnable {
        File file;
        public ActualizarVersion(Context context, File file) {
            this.file = file;
        }
        public void run() {
            try {
                Intent intent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //Uri packageURI = Uri.parse("package:proyecto.app.clientesabc");
                    //Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                    //context.get().startActivity(uninstallIntent);
                    /*Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    uninstallIntent.setData(Uri.parse("package:proyecto.app.clientesabc"));
                    uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    activity.get().startActivityForResult(uninstallIntent, UNINSTALL_REQUEST_CODE);*/
                    Uri apkUri = FileProvider.getUriForFile(context.get(), BuildConfig.APPLICATION_ID + ".providers.FileProvider", file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    /*
                    Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    uninstallIntent.setData(Uri.parse("package:proyecto.app.clientesabc"));
                    uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                    activity.get().startActivityForResult(uninstallIntent, UNINSTALL_REQUEST_CODE);
*/
                    Uri apkUri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.get().startActivity(intent);
            }catch (Exception e){
                Toasty.error(context.get(),"Actualización Falló!!",Toast.LENGTH_LONG).show();
            }
        }

    }

    private void CrearArchivoConfiguracion(String path) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        // root elements
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("configuracion");
        doc.appendChild(rootElement);
        // set attribute to staff element
        /*Attr usuario = doc.createAttribute("usuario");
        usuario.setValue(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("user",""));
        rootElement.setAttributeNode(usuario);*/

        //Sistema
        Element sistema = doc.createElement("sistema");
        rootElement.appendChild(sistema);

        Element nombrePais = doc.createElement("nombrePais");
        nombrePais.appendChild(doc.createTextNode(VariablesGlobales.getNombrePais()));
        sistema.appendChild(nombrePais);

        Element bukrs = doc.createElement("sociedad");
        bukrs.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_BUKRS","")));
        sistema.appendChild(bukrs);

        Element orgvta = doc.createElement("orgvta");
        orgvta.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_ORGVTA","")));
        sistema.appendChild(orgvta);

        Element land1 = doc.createElement("land1");
        land1.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_LAND1","")));
        sistema.appendChild(land1);

        Element cadenaRM = doc.createElement("cadenaRM");
        cadenaRM.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_CADENARM", VariablesGlobales.getCadenaRM())));
        sistema.appendChild(cadenaRM);

        Element ktokd = doc.createElement("ktokd");
        ktokd.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_KTOKD","")));
        sistema.appendChild(ktokd);

        //Login
        Element login = doc.createElement("login");
        rootElement.appendChild(login);

        Element user = doc.createElement("user");
        user.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("user","")));
        login.appendChild(user);

        Element password = doc.createElement("password");
        password.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("password","")));
        login.appendChild(password);

        Element guardar_contrasena = doc.createElement("guardar_contrasena");
        guardar_contrasena.appendChild(doc.createTextNode(String.valueOf(PreferenceManager.getDefaultSharedPreferences(context.get()).getBoolean("guardar_contrasena", false))));
        login.appendChild(guardar_contrasena);

        Element ruta = doc.createElement("ruta");
        ruta.appendChild(doc.createTextNode(PreferenceManager.getDefaultSharedPreferences(context.get()).getString("W_CTE_RUTAHH","")));
        login.appendChild(ruta);

        //Conexiones
        ArrayList<Conexion> misConexiones = TCPActivity.getConexionesFromSharedPreferences(context.get());

        for(int x=0; x < misConexiones.size(); x++){
            Element conexion = doc.createElement("conexion");
            rootElement.appendChild(conexion);
            conexion.setAttribute("defecto", String.valueOf(misConexiones.get(x).isDefecto()) );

            Element nombre = doc.createElement("nombre");
            String nombreConexion = misConexiones.get(x).getNombre() == null ? "Conexion "+x : misConexiones.get(x).getNombre();
            nombre.appendChild(doc.createTextNode(nombreConexion));
            conexion.appendChild(nombre);

            Element tipo_conexion = doc.createElement("tipo_conexion");
            tipo_conexion.appendChild(doc.createTextNode(misConexiones.get(x).getTipo()));
            conexion.appendChild(tipo_conexion);

            Element Ip = doc.createElement("Ip");
            Ip.appendChild(doc.createTextNode(misConexiones.get(x).getIp()));
            conexion.appendChild(Ip);

            Element Puerto = doc.createElement("Puerto");
            Puerto.appendChild(doc.createTextNode(misConexiones.get(x).getPuerto()));
            conexion.appendChild(Puerto);
        }

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path+"configuracion.xml"));

        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }


}