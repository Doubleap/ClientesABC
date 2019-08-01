package proyecto.app.clientesabc.Clases;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
            SQLiteDatabase mDataBase = SQLiteDatabase.openDatabase("/data/user/0/proyecto.app.clientesabc/databases/FAWM_ANDROID_SOLICITUDES", null, SQLiteDatabase.CREATE_IF_NECESSARY);

            System.out.println("Opened database successfully");
            String sqlDrop = "DROP TABLE IF EXISTS 'FormHvKof_solicitud';";
            String sqlAttach = "ATTACH DATABASE '/data/user/0/proyecto.app.clientesabc/databases/FAWM_ANDROID_2' AS fromDB";
            String sql =
                    "CREATE TABLE [FormHvKof_solicitud] (\n" +
                    "[IDFORM] INT,\n" +
                    "[TIPFORM] INT,\n" +
                    "[FECCRE] DATETIME,\n" +
                    "[USUSOL] VARCHAR(15),\n" +
                    "[ESTADO] VARCHAR(20),\n" +
                    "[W_CTE-AKONT] VARCHAR(10),\n" +
                    "[W_CTE-ALTKN] VARCHAR(10),\n" +
                    "[W_CTE-ANTLF] REAL,\n" +
                    "[W_CTE-BUKRS] VARCHAR(4),\n" +
                    "[W_CTE-BZIRK] VARCHAR(6),\n" +
                    "[W_CTE-CITY1] VARCHAR(40),\n" +
                    "[W_CTE-CITY2] VARCHAR(40),\n" +
                    "[W_CTE-CTLPC] VARCHAR(3),\n" +
                    "[W_CTE-DATAB] DATETIME,\n" +
                    "[W_CTE-DATBI] VARCHAR(8),\n" +
                    "[W_CTE-DBRTG] VARCHAR(5),\n" +
                    "[W_CTE-DMBTR1] REAL,\n" +
                    "[W_CTE-DMBTR2] REAL,\n" +
                    "[W_CTE-DMBTR3] REAL,\n" +
                    "[W_CTE-FAX_EXTENS] VARCHAR(10),\n" +
                    "[W_CTE-FAX_NUMBER] VARCHAR(30),\n" +
                    "[W_CTE-FDGRV] VARCHAR(10),\n" +
                    "[W_CTE-FLAG_FACT] VARCHAR(1),\n" +
                    "[W_CTE-FLAG_NTEN] VARCHAR(1),\n" +
                    "[W_CTE-HITYP] VARCHAR(1),\n" +
                    "[W_CTE-HKUNNR] VARCHAR(10),\n" +
                    "[W_CTE-HOME_CITY] VARCHAR(40),\n" +
                    "[W_CTE-HOUSE_NUM1] VARCHAR(10),\n" +
                    "[W_CTE-HOUSE_NUM2] VARCHAR(10),\n" +
                    "[W_CTE-INCO1] VARCHAR(3),\n" +
                    "[W_CTE-INCO2] VARCHAR(28),\n" +
                    "[W_CTE-KALKS] VARCHAR(1),\n" +
                    "[W_CTE-KATR3] VARCHAR(2),\n" +
                    "[W_CTE-KATR4] VARCHAR(2),\n" +
                    "[W_CTE-KATR5] VARCHAR(2),\n" +
                    "[W_CTE-KATR8] VARCHAR(3),\n" +
                    "[W_CTE-KDGRP] VARCHAR(2),\n" +
                    "[W_CTE-KKBER] VARCHAR(4),\n" +
                    "[W_CTE-KLABC] VARCHAR(2),\n" +
                    "[W_CTE-KLIMK] REAL,\n" +
                    "[W_CTE-KNKLI] VARCHAR(10),\n" +
                    "[W_CTE-KTGRD] VARCHAR(2),\n" +
                    "[W_CTE-KTOKD] VARCHAR(10),\n" +
                    "[W_CTE-KUKLA] VARCHAR(2),\n" +
                    "[W_CTE-KUNNR] REAL,\n" +
                    "[W_CTE-KVGR1] VARCHAR(3),\n" +
                    "[W_CTE-KVGR2] VARCHAR(3),\n" +
                    "[W_CTE-KVGR3] VARCHAR(3),\n" +
                    "[W_CTE-KVGR5] VARCHAR(3),\n" +
                    "[W_CTE-LAND1] VARCHAR(4),\n" +
                    "[W_CTE-LIFNR] VARCHAR(10),\n" +
                    "[W_CTE-LIMSUG] REAL,\n" +
                    "[W_CTE-LOCATION] VARCHAR(40),\n" +
                    "[W_CTE-LPRIO] VARCHAR(2),\n" +
                    "[W_CTE-LZONE] VARCHAR(10),\n" +
                    "[W_CTE-NAME_CO] VARCHAR(40),\n" +
                    "[W_CTE-NAME1] VARCHAR(35),\n" +
                    "[W_CTE-NAME2] VARCHAR(35),\n" +
                    "[W_CTE-NAME3] VARCHAR(35),\n" +
                    "[W_CTE-NAME4] VARCHAR(35),\n" +
                    "[W_CTE-PERNR] REAL,\n" +
                    "[W_CTE-PO_BOX] VARCHAR(10),\n" +
                    "[W_CTE-PO_BOX_LOC] VARCHAR(40),\n" +
                    "[W_CTE-PO_BOX_REG] VARCHAR(3),\n" +
                    "[W_CTE-POST_CODE2] VARCHAR(10),\n" +
                    "[W_CTE-PRFRE] VARCHAR(1),\n" +
                    "[W_CTE-PSON1] VARCHAR(35),\n" +
                    "[W_CTE-PSON2] VARCHAR(1),\n" +
                    "[W_CTE-PSON3] VARCHAR(3),\n" +
                    "[W_CTE-PSTLZ] VARCHAR(10),\n" +
                    "[W_CTE-PVKSM] VARCHAR(2),\n" +
                    "[W_CTE-REGION] VARCHAR(3),\n" +
                    "[W_CTE-ROOMNUMBER] VARCHAR(10),\n" +
                    "[W_CTE-SMTP_ADDR] VARCHAR(241),\n" +
                    "[W_CTE-STCD1] VARCHAR(35),\n" +
                    "[W_CTE-STCD3] VARCHAR(18),\n" +
                    "[W_CTE-STR_SUPPL1] VARCHAR(40),\n" +
                    "[W_CTE-STR_SUPPL2] VARCHAR(40),\n" +
                    "[W_CTE-STR_SUPPL3] VARCHAR(40),\n" +
                    "[W_CTE-STREET] VARCHAR(60),\n" +
                    "[W_CTE-TEL_EXTENS] VARCHAR(10),\n" +
                    "[W_CTE-TEL_NUMBER] VARCHAR(30),\n" +
                    "[W_CTE-TEL_NUMBER2] VARCHAR(30),\n" +
                    "[W_CTE-TELNUMBER2] VARCHAR(30),\n" +
                    "[W_CTE-TELNUMBER3] VARCHAR(30),\n" +
                    "[W_CTE-TOGRU] VARCHAR(4),\n" +
                    "[W_CTE-UPDAT] VARCHAR(8),\n" +
                    "[W_CTE-VKBUR] VARCHAR(4),\n" +
                    "[W_CTE-VKGRP] VARCHAR(3),\n" +
                    "[W_CTE-VKORG] VARCHAR(4),\n" +
                    "[W_CTE-VSBED] VARCHAR(2),\n" +
                    "[W_CTE-VWERK] VARCHAR(4),\n" +
                    "[W_CTE-WAERS] VARCHAR(5),\n" +
                    "[W_CTE-XZVER] VARCHAR(1),\n" +
                    "[W_CTE-ZGPOCANAL] VARCHAR(2),\n" +
                    "[W_CTE-ZTERM] VARCHAR(4),\n" +
                    "[W_CTE-ZTPOCANAL] VARCHAR(2),\n" +
                    "[W_CTE-ZSEGPRE] VARCHAR(10),\n" +
                    "[W_CTE-ZWELS] VARCHAR(10),\n" +
                    "[W_CTE-ZZAUART] VARCHAR(4),\n" +
                    "[W_CTE-ZZBLOQU] VARCHAR(2),\n" +
                    "[W_CTE-ZZCANAL] VARCHAR(4),\n" +
                    "[W_CTE-ZZCATFOCO] VARCHAR(2),\n" +
                    "[W_CTE-ZZCRMA_LAT] REAL,\n" +
                    "[W_CTE-ZZCRMA_LONG] REAL,\n" +
                    "[W_CTE-ZZENT1] VARCHAR(3),\n" +
                    "[W_CTE-ZZENT2] VARCHAR(3),\n" +
                    "[W_CTE-ZZENT3] VARCHAR(3),\n" +
                    "[W_CTE-ZZENT4] VARCHAR(3),\n" +
                    "[W_CTE-ZZENT5] VARCHAR(3),\n" +
                    "[W_CTE-ZZERDAT] VARCHAR(8),\n" +
                    "[W_CTE-ZZGERENTE] VARCHAR(40),\n" +
                    "[W_CTE-ZZINTCO] VARCHAR(2),\n" +
                    "[W_CTE-ZZINTTACT] VARCHAR(2),\n" +
                    "[W_CTE-ZZJEFATURA] VARCHAR(40),\n" +
                    "[W_CTE-ZZOCCONS] VARCHAR(2),\n" +
                    "[W_CTE-ZZREJA] VARCHAR(1),\n" +
                    "[W_CTE-ZZSEGCOM] VARCHAR(10),\n" +
                    "[W_CTE-ZZSEGDESC] VARCHAR(10),\n" +
                    "[W_CTE-ZZSEGEXH] VARCHAR(10),\n" +
                    "[W_CTE-ZZSEGPDE] VARCHAR(10),\n" +
                    "[W_CTE-ZZSEGPDV] VARCHAR(10),\n" +
                    "[W_CTE-ZZSEGPORT] VARCHAR(10),\n" +
                    "[W_CTE-ZZSEGPRE] VARCHAR(10),\n" +
                    "[W_CTE-ZZSHARE] VARCHAR(2),\n" +
                    "[W_CTE-ZZSTAT] VARCHAR(2),\n" +
                    "[W_CTE-ZZSUBUNNEG] VARCHAR(2),\n" +
                    "[W_CTE-ZZTIPSERV] VARCHAR(3),\n" +
                    "[W_CTE-ZZTFISI] VARCHAR(5),\n" +
                    "[W_CTE-ZZUNNEG] VARCHAR(2),\n" +
                    "[W_CTE-ZZZONACOST] VARCHAR(4),\n" +
                    "[W_CTE-COMENTARIOS] VARCHAR(255),\n" +
                    "[fuera_politica_plazo] INT,\n" +
                    "[fuera_politica_monto] INT,\n" +
                    "[W_CTE-NOTIFICANTES] VARCHAR(255),\n" +
                    "[FECFIN] DATETIME,\n" +
                    "[W_CTE-ZZKEYACC] VARCHAR(10),\n" +
                    "[W_CTE-ZIBASE] VARCHAR(10),\n" +
                    "[W_CTE-ZADICI] VARCHAR(3),\n" +
                    "[W_CTE-ZZUDATE] VARCHAR(8),\n" +
                    "[W_CTE-AUFSD] VARCHAR(2),\n" +
                    "[W_CTE-LIFSD] VARCHAR(2),\n" +
                    "[W_CTE-FAKSD] VARCHAR(2),\n" +
                    "[W_CTE-CASSD] VARCHAR(2),\n" +
                    "[W_CTE-LOEVM] VARCHAR(1),\n" +
                    "[W_CTE-TELF2] VARCHAR(16),\n" +
                    "[W_CTE-VTWEG] VARCHAR(2),\n" +
                    "[W_CTE-SPART] VARCHAR(2),\n" +
                    "[W_CTE-PERFK] VARCHAR(2),\n" +
                    "[W_CTE-KVGR4] VARCHAR(3),\n" +
                    "[W_CTE-KONDA] VARCHAR(2)\n" +
                    ")";
            mDataBase.execSQL(sqlDrop);
            mDataBase.execSQL(sql);
            mDataBase.execSQL(sqlAttach);
            String copiarTablaSql = "INSERT INTO main.FormHvKof_solicitud  SELECT * FROM fromDB.FormHvKof_solicitud";
            mDataBase.execSQL(copiarTablaSql);

            //File myFile = new File("/data/user/0/proyecto.app.clientesabc/databases/", "FAWM_ANDROID_SOLICITUDES");
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