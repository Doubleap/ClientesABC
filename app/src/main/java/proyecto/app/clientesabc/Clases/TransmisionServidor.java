package proyecto.app.clientesabc.Clases;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;

public class TransmisionServidor extends AsyncTask<Void,Void,Void> {

    private ArrayList<String> a;
    private ListView listView;
    private Context context;
    private Activity activity;
    private String destinationAddress="192.168.0.13";
    private String filePath;
    private String wholePath;
    private boolean xceptionFlag = false;
    private Socket socket;
    private String hostName,canonicalHostname;
    private String givenName;

    public TransmisionServidor(Context context, Activity act, String path, String fullPath){
        this.context = context;
        this.activity = act;
        this.filePath = path;
        this.wholePath = fullPath;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        //Seteando listView para desplegar IPs disponibles
        listView = (ListView)activity.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, a );

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //obtener la IP seleccionada del UI
                destinationAddress = (String)adapterView.getItemAtPosition(position);
            }
        });
    }

    @Override
    protected Void doInBackground(Void... voids) {

        System.out.println("ArrayList de Archivos");
        ArrayList<File> files = new ArrayList<>();
        System.out.println("A punto de crearse.");

        //Add files
        //files.add(new File("mnt/sdcard/Download/loquesea.mp3"));
        // filePath = filePath.replace("%20"," ");
        ///files.add(new File(wholePath));

        //System.out.println("file created..");
        try {
            System.out.println("Estableciendo comunicación para enviar archivos...");
            socket = new Socket(VariablesGlobales.getIpcon(),VariablesGlobales.getPuertocon());
            //socket.setReuseAddress(true);

            // Enviar archivo en socket
            File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_2");
            files.add(myFile);
            //files.add(myFile);

            System.out.println("Creando Streams de datos...");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            System.out.println(files.size());

            //Comando String que indicara que se queire realizar Sincronizacion/Transmision
            dos.writeUTF("Transmision");
            //cantidad de archivos a enviar al servidor
            dos.writeInt(files.size());
            //dos.flush();

            //Escribir tamanos de los archivos
            for(int i = 0;i< files.size();i++){
                int file_size = Integer.parseInt(String.valueOf(files.get(i).length()));
                dos.writeLong(file_size);
                dos.flush();
            }
            //escribir nombres de archivos
            for(int i = 0 ; i < files.size();i++){
                dos.writeUTF(files.get(i).getName());
                dos.flush();
            }

            //buffer para la escritura del archivo
            int n = 0;
            byte[]buf = new byte[1024];
            //outer loop, executes one for each file
            for(int i =0; i < files.size(); i++){
                System.out.println(files.get(i).getName());
                //crear nueva fileinputstream para cada archivo
                FileInputStream fis = new FileInputStream(files.get(i));
                //escribir el archivo al dos
                while((n = fis.read(buf)) != -1){
                    dos.write(buf,0,n);
                    dos.flush();
                }
                //deberia cerrar el dataoutputstream aqui y hacer una nueva cada vez???
                System.out.println("Termino Iteracion : "+i+".");
            }
            //TODO Puedo recibir cualquier cosa de respuesta en el stream de la conexion del socket??
            //int r = dis.read();
            int s = dis.readInt();
            byte[] respuesta = new byte[s];

            dis.read(respuesta,0,s);

            File tranFileDir = null;
            File externalStorage = Environment.getExternalStorageDirectory();
            if (externalStorage != null) {
                String externalStoragePath = externalStorage.getAbsolutePath();
                tranFileDir = new File(externalStoragePath + File.separator + context.getPackageName()+ File.separator+"Transmision");
                boolean ex = tranFileDir.mkdirs();
                File transferFile = new File(tranFileDir,"add1.PNG");
                FileOutputStream stream = new FileOutputStream(transferFile);
                stream.write(respuesta);

                stream.flush();
                stream.close();
            }
            //O cerrarlo aqui estara bien?
            dos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            xceptionFlag = true;
            e.printStackTrace();
        }

        Log.i("===end of start ====", "==");
        try{
            if(socket!=null && !socket.isClosed()){
                socket.close();
            }
        }
        catch (Exception e){
            xceptionFlag = true;
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(xceptionFlag){
            Toasty.error(context,"Transmision NO pudo realizarse",Toast.LENGTH_LONG).show();
        }
        else{
            Toasty.success(context,"Transmision Finalizada Correctamente!!",Toast.LENGTH_LONG).show();
        }
        //TODO si es posible recibir del server los archivos validar para cuando se quiera hcer una sincronizacion en vez de una transmision
    }

    public ArrayList<String> getClientList() {

        final ArrayList<String> arr = new ArrayList<>(25);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = null;
                boolean isFirstLine = true;

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;

                    while ((line = br.readLine()) != null) {
                        if (isFirstLine) {
                            isFirstLine = false;
                            continue;
                        }

                        String[] splitted = line.split(" +");

                        if (splitted != null && splitted.length >= 4) {

                            String ipAddress = splitted[0];
                            String macAddress = splitted[3];

                            boolean isReachable = InetAddress.getByName(
                                    splitted[0]).isReachable(500);
                            // this is network call so we cant do that on UI thread, so take background thread.
                            if (isReachable) {
                                Log.d("Device Information", ipAddress + " : "
                                        + macAddress);

                                //added afterwards for receiving names of available clients..
                                //but by adding this names to array list, the ip addresses is lost. so do something.
                                try {
                                    Socket socket = new Socket();
                                    //receive from port 5006 and timeout is 5s.
                                    socket.connect(new InetSocketAddress(ipAddress, 5006), 5000);
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    givenName = reader.readLine();
                                    reader.close();
                                    socket.close();
                                    Log.i("TAG", givenName);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                //Assigning values to final array or array list is perfectly fine.

                                arr.add(ipAddress);
                                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                                hostName = inetAddress.getHostName();
                                canonicalHostname = inetAddress.getCanonicalHostName();

                                //  Toast.makeText(context,hostName+canonicalHostname,Toast.LENGTH_LONG).show();

                            }

                        }

                    }

                } catch (Exception e) {
                    xceptionFlag = true;
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        xceptionFlag = true;
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        //Wait util thread is completed. And then return array.
        //Otherwise it'll return null array or array list or what ever.
        try{
            thread.join();
        }
        catch (Exception e){
            xceptionFlag = true;
            e.printStackTrace();
        }
        return arr;

    }
}