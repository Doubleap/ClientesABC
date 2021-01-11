package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.actividades.SolicitudActivity;
import proyecto.app.clientesabc.actividades.SolicitudAvisosEquipoFrioActivity;
import proyecto.app.clientesabc.actividades.SolicitudCreditoActivity;
import proyecto.app.clientesabc.actividades.SolicitudModificacionActivity;
import proyecto.app.clientesabc.adaptadores.AdjuntoTableAdapter;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;
import proyecto.app.clientesabc.modelos.Adjuntos;
import proyecto.app.clientesabc.modelos.Solicitud;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;

public class ManejadorAdjuntos  extends AppCompatActivity {

    private Uri mPhotoUri;
    private DataBaseHelper mDBHelper;
    private ArrayList<Adjuntos> adjuntosSolicitud;
    private boolean modificable;
    private boolean firma;
    private String GUID;
    private de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos;
    private Map<String, View> mapeoCamposDinamicos;

    public ManejadorAdjuntos(){};
    public ManejadorAdjuntos(Uri mPhotoUri, DataBaseHelper mDBHelper, ArrayList<Adjuntos> adjuntosSolicitud, boolean modificable, boolean firma, String GUID, de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos, Map<String, View> mapeoCamposDinamicos){
        this.mPhotoUri = mPhotoUri;
        this.mDBHelper = mDBHelper;
        this.adjuntosSolicitud = adjuntosSolicitud;
        this.modificable = modificable;
        this.firma = firma;
        this.GUID = GUID;
        this.tb_adjuntos = tb_adjuntos;
        this.mapeoCamposDinamicos = mapeoCamposDinamicos;
    }
    public static void MostrarGaleriaAdjuntosHorizontal(HorizontalScrollView hsv, final Context context, final Activity activity, ArrayList<Adjuntos> adjuntosSolicitud, boolean modificable, boolean firma, de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos, Map<String, View> mapeoCamposDinamicos) {
        hsv.removeAllViews();
        hsv.setBackground(context.getResources().getDrawable(R.drawable.squared_textbackground,null));
        hsv.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout myGallery = new LinearLayout(context);
        myGallery.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        myGallery.setPadding(10,10,10,10);
        for(int x=0; x < adjuntosSolicitud.size(); x++){
            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lp.setMargins(0,0,5,0);
            layout.setLayoutParams(lp);
            layout.setGravity(Gravity.CENTER);

            LinearLayout layout2 = new LinearLayout(context);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lp.setMargins(5,0,0,0);
            layout2.setLayoutParams(lp2);
            layout2.setOrientation(LinearLayout.VERTICAL);

            final ImageView adjunto_image = new ImageView(context);
            adjunto_image.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
            adjunto_image.setScaleType(ImageView.ScaleType.FIT_XY);
            adjunto_image.setPadding(5,5,5,5);

            final String nombre_adjunto = adjuntosSolicitud.get(x).getName();
            TextView nombre = new TextView(context);
            nombre.setTextSize(8);
            nombre.setSingleLine(false);
            nombre.setLayoutParams( new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            nombre.setMinLines(1);
            nombre.setMaxLines(3);
            nombre.setTag(nombre_adjunto);
            nombre.setText(nombre_adjunto);

            if(adjuntosSolicitud.get(x).getImage() != null) {
                byte[] image = adjuntosSolicitud.get(x).getImage();
                //_bitmap.compress(Bitmap.CompressFormat.PNG, 50, image);
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inSampleSize = 1;
                if (image.length > 200000)
                    o.inSampleSize = 2;
                if (image.length > 400000)
                    o.inSampleSize = 4;
                if (image.length > 500000)
                    o.inSampleSize = 8;
                Bitmap imagen = BitmapFactory.decodeByteArray(image, 0, image.length, o);
                //Si no es una imagen, poner in icono del tipo de documento adjunto
                if(imagen == null){
                    if(nombre_adjunto.endsWith(".xls") || nombre_adjunto.endsWith(".xlsx") || nombre_adjunto.endsWith(".odx")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_excel));
                        adjuntosSolicitud.get(x).setType("application/excel");
                    }else
                    if(nombre_adjunto.endsWith(".doc") || nombre_adjunto.endsWith(".docx") || nombre_adjunto.endsWith(".odt")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_word));
                        adjuntosSolicitud.get(x).setType("application/msword");
                    }else
                    if(nombre_adjunto.endsWith(".ppt") || nombre_adjunto.endsWith(".pptx")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_ppt));
                        adjuntosSolicitud.get(x).setType("application/powerpoint");
                    }else
                    if(nombre_adjunto.endsWith(".pdf")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_pdf));
                        adjuntosSolicitud.get(x).setType("application/pdf");
                    }else
                    if(nombre_adjunto.endsWith(".msg") || nombre_adjunto.endsWith(".msgx")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_msg));
                        adjuntosSolicitud.get(x).setType("application/vnd.ms-outlook");
                    }else
                    if(nombre_adjunto.endsWith(".eml") || nombre_adjunto.endsWith(".emlx")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_msg));
                        adjuntosSolicitud.get(x).setType("application/vnd.ms-outlook");
                    }else
                    if(nombre_adjunto.endsWith(".html") || nombre_adjunto.endsWith(".cshtml") || nombre_adjunto.endsWith(".asp") || nombre_adjunto.endsWith(".aspx")){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_html));
                        adjuntosSolicitud.get(x).setType("text/html");
                    }else
                    if(nombre_adjunto.endsWith(".xml") ){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_xml));
                        adjuntosSolicitud.get(x).setType("application/xml");
                    }else
                    if(nombre_adjunto.endsWith(".zip") || nombre_adjunto.endsWith(".rar") ){
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_zip));
                        adjuntosSolicitud.get(x).setType("application/zip");
                    }else{
                        adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_text));
                        adjuntosSolicitud.get(x).setType("text/plain");
                    }
                }else{
                    adjunto_image.setImageBitmap(imagen);
                    adjunto_image.setBackground(context.getResources().getDrawable(R.drawable.border, null));
                }

                final int finalX = x;
                adjunto_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarAdjunto(v.getContext(), adjuntosSolicitud.get(finalX));
                        //mostrarAdjuntoServidor(v.getContext(), activity, adjuntosSolicitud.get(finalX));
                    }
                });
                if (modificable) {
                    adjunto_image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DialogHandler appdialog = new DialogHandler();
                            appdialog.Confirm((Activity) context, "Confirmación Borrado", "Esta seguro que quiere eliminar el archivo adjunto #" + finalX + "?", "Cancelar", "Eliminar", new ManejadorAdjuntos.EliminarAdjunto(context, activity, finalX,  adjuntosSolicitud, mapeoCamposDinamicos, tb_adjuntos, modificable, firma));
                            return false;
                        }
                    });
                }
            }else{
                if(nombre_adjunto.endsWith(".jpg") || nombre_adjunto.endsWith(".jpeg")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_jpeg));
                    adjuntosSolicitud.get(x).setType("image/jpeg");
                }else
                if(nombre_adjunto.endsWith(".png")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_png));
                    adjuntosSolicitud.get(x).setType("image/png");
                }else
                if(nombre_adjunto.endsWith(".bmp") || nombre_adjunto.endsWith(".bmp") || nombre_adjunto.endsWith(".bmp")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_bmp));
                    adjuntosSolicitud.get(x).setType("application/excel");
                }else
                if(nombre_adjunto.endsWith(".gif") || nombre_adjunto.endsWith(".gif") || nombre_adjunto.endsWith(".gif")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_gif));
                    adjuntosSolicitud.get(x).setType("application/excel");
                }else
                if(nombre_adjunto.endsWith(".xls") || nombre_adjunto.endsWith(".xlsx") || nombre_adjunto.endsWith(".odx")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_excel));
                    adjuntosSolicitud.get(x).setType("application/excel");
                }else
                if(nombre_adjunto.endsWith(".doc") || nombre_adjunto.endsWith(".docx") || nombre_adjunto.endsWith(".odt")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_word));
                    adjuntosSolicitud.get(x).setType("application/msword");
                }else
                if(nombre_adjunto.endsWith(".ppt") || nombre_adjunto.endsWith(".pptx")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_ppt));
                    adjuntosSolicitud.get(x).setType("application/powerpoint");
                }else
                if(nombre_adjunto.endsWith(".pdf")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_pdf));
                    adjuntosSolicitud.get(x).setType("application/pdf");
                }else
                if(nombre_adjunto.endsWith(".msg") || nombre_adjunto.endsWith(".msgx")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_msg));
                    adjuntosSolicitud.get(x).setType("application/vnd.ms-outlook");
                }else
                if(nombre_adjunto.endsWith(".eml") || nombre_adjunto.endsWith(".emlx")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_eml));
                    adjuntosSolicitud.get(x).setType("application/vnd.ms-outlook");
                }else
                if(nombre_adjunto.endsWith(".html") || nombre_adjunto.endsWith(".cshtml") || nombre_adjunto.endsWith(".asp") || nombre_adjunto.endsWith(".aspx")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_html));
                    adjuntosSolicitud.get(x).setType("text/html");
                }else
                if(nombre_adjunto.endsWith(".xml") ){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_xml));
                    adjuntosSolicitud.get(x).setType("application/xml");
                }else
                if(nombre_adjunto.endsWith(".xps") || nombre_adjunto.endsWith(".oxps")){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_pdf));
                    adjuntosSolicitud.get(x).setType("application/oxps");
                }else
                if(nombre_adjunto.endsWith(".zip") || nombre_adjunto.endsWith(".rar") ){
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_zip));
                    adjuntosSolicitud.get(x).setType("application/zip");
                }else{
                    adjunto_image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.type_icon_text));
                    adjuntosSolicitud.get(x).setType("text/plain");
                }
                final int finalX = x;
                adjunto_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mostrarAdjuntoServidor(v.getContext(), activity, adjuntosSolicitud.get(finalX));
                    }
                });
            }
            layout.addView(layout2);
            layout2.addView(adjunto_image);
            layout2.addView(nombre);
            myGallery.addView(layout);
        }
        hsv.addView(myGallery);
    }

    public static void mostrarAdjunto(Context context, Adjuntos adjunto) {
        final Dialog d = new Dialog(context, R.style.MyAlertDialogThemeAttachment);
        d.setContentView(R.layout.adjunto_layout_zoom);
        ImageView adjunto_img = d.findViewById(R.id.imagen);
        TextView adjunto_txt = d.findViewById(R.id.nombre);
        final String nombre_adjunto = adjunto.getName();
        byte[] image = adjunto.getImage();
        Bitmap imagen = BitmapFactory.decodeByteArray(image, 0, image.length);
        if(imagen != null) {
            adjunto_img.setImageBitmap(imagen);
            adjunto_txt.setText(adjunto.getName());
            //SHOW DIALOG
            d.show();
            Window window = d.getWindow();
            if(window != null) {
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }else{//Si no es una imagen debo abrirlo con algun app
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ nombre_adjunto);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(adjunto.getImage());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file),adjunto.getType());
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "Ver archivo con: ");
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
    }

    public static void mostrarAdjuntoServidor(Context context, Activity activity, Adjuntos adjunto) {
        final Dialog d = new Dialog(context, R.style.MyAlertDialogThemeAttachment);
        d.setContentView(R.layout.adjunto_layout_zoom);
        ImageView adjunto_img = d.findViewById(R.id.imagen);
        TextView adjunto_txt = d.findViewById(R.id.nombre);
        adjunto_txt.setText(adjunto.getName());
        if(!adjunto.getName().toLowerCase().contains(".pdf")) {
            //SHOW DIALOG
            d.show();
            Window window = d.getWindow();
            if (window != null) {
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        }
        //Realizar la transmision de lo que se necesita (Db o txt)
        WeakReference<Context> weakRefs = new WeakReference<Context>(context);
        WeakReference<Activity> weakRefAs = new WeakReference<Activity>(activity);
        //PreferenceManager.getDefaultSharedPreferences(PanelActivity.this).getString("W_CTE_RUTAHH","");
        AdjuntoServidor s = new AdjuntoServidor(weakRefs, weakRefAs, adjunto_img, adjunto_txt);
        if(PreferenceManager.getDefaultSharedPreferences(context).getString("tipo_conexion","").equals("wifi")){
            s.EnableWiFi();
        }else{
            s.DisableWiFi();
        }
        s.execute();

    }

    public static class EliminarAdjunto implements Runnable {
        private Context context;
        private Activity activity;
        private int rowIndex;
        private ArrayList<Adjuntos> adjuntosSolicitud;
        private Map<String, View> mapeoCamposDinamicos;
        private de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos;
        private boolean modificable;
        private boolean firma;
        public EliminarAdjunto(Context context, Activity activity, int rowIndex, ArrayList<Adjuntos> adjuntosSolicitud, Map<String, View> mapeoCamposDinamicos, de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos, boolean modificable, boolean firma) {
            this.context = context;
            this.activity = activity;
            this.rowIndex = rowIndex;
            this.adjuntosSolicitud = adjuntosSolicitud;
            this.mapeoCamposDinamicos = mapeoCamposDinamicos;
            this.tb_adjuntos = tb_adjuntos;
            this.modificable = modificable;
            this.firma = firma;
        }
        public void run() {
            if(adjuntosSolicitud.get(rowIndex).getName().contains("PoliticaPrivacidad")) {
                CheckBox politica = (CheckBox) mapeoCamposDinamicos.get("politica");
                if(politica != null) {
                    politica.setChecked(false);
                    politica.setEnabled(true);

                    if (activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if (activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if (activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if (activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            if(adjuntosSolicitud.get(rowIndex).getName().contains("AceptacionCredito")) {
                CheckBox aceptacion_credito = (CheckBox) mapeoCamposDinamicos.get("aceptacion_credito");
                if(aceptacion_credito != null) {
                    aceptacion_credito.setChecked(false);
                    aceptacion_credito.setEnabled(true);

                    if (activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if (activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if (activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if (activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            if(adjuntosSolicitud.get(rowIndex).getName().contains("AceptacionContrato")) {
                CheckBox aceptacion_contrato = (CheckBox) mapeoCamposDinamicos.get("aceptacion_contrato");
                if(aceptacion_contrato != null) {
                    aceptacion_contrato.setChecked(false);
                    aceptacion_contrato.setEnabled(true);

                    if (activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if (activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if (activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if (activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            if(adjuntosSolicitud.get(rowIndex).getName().contains("AceptacionPagare")) {
                CheckBox aceptacion_credito = (CheckBox) mapeoCamposDinamicos.get("aceptacion_credito");
                if(aceptacion_credito != null) {
                    aceptacion_credito.setChecked(false);
                    aceptacion_credito.setEnabled(true);

                    if (activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if (activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if (activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if (activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            if(adjuntosSolicitud.get(rowIndex).getName().contains("AceptacionLetra")) {
                CheckBox aceptacion_letra = (CheckBox) mapeoCamposDinamicos.get("aceptacion_letra");
                if(aceptacion_letra != null) {
                    aceptacion_letra.setChecked(false);
                    aceptacion_letra.setEnabled(true);

                    if (activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if (activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if (activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if (activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            if(adjuntosSolicitud.get(rowIndex).getName().contains("AceptacionApc")) {
                CheckBox aceptacion_apc = (CheckBox) mapeoCamposDinamicos.get("aceptacion_apc");
                if (aceptacion_apc != null) {
                    aceptacion_apc.setChecked(false);
                    aceptacion_apc.setEnabled(true);

                    if (activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if (activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if (activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if (activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            if(adjuntosSolicitud.get(rowIndex).getName().contains("Constancia")) {
                CheckBox constancia = (CheckBox) mapeoCamposDinamicos.get("constancia");
                if(constancia != null) {
                    constancia.setChecked(false);
                    constancia.setEnabled(true);
                    if(activity instanceof SolicitudActivity)
                        ((SolicitudActivity) activity).firma = false;
                    if(activity instanceof SolicitudModificacionActivity)
                        ((SolicitudModificacionActivity) activity).firma = false;
                    if(activity instanceof SolicitudCreditoActivity)
                        ((SolicitudCreditoActivity) activity).firma = false;
                    if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                        ((SolicitudAvisosEquipoFrioActivity) activity).firma = false;
                }
            }
            adjuntosSolicitud.remove(rowIndex);
            tb_adjuntos.setDataAdapter(new AdjuntoTableAdapter(context, adjuntosSolicitud));

            ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal((HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos"),context, activity,adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

            Toasty.info(context, "Se ha eliminado el adjunto!", Toast.LENGTH_SHORT).show();
        }
    }
    //Se dispara al escoger el documento que se quiere relacionar a la solicitud
    public static void ActivityResult(int requestCode, int resultCode, Intent data, Context context, Activity activity, Uri mPhotoUri, DataBaseHelper mDBHelper, ArrayList<Adjuntos> adjuntosSolicitud, boolean modificable, boolean firma, String GUID, de.codecrafters.tableview.TableView<Adjuntos> tb_adjuntos, Map<String, View> mapeoCamposDinamicos){
        switch (requestCode) {
            //Captura de imagen por medio de la camara del dispositivo, aqui se realiza el crop
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = context.getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        ContentResolver cR = context.getContentResolver();
                        String type = cR.getType(uri);
                        String name = ManejadorAdjuntos.getFileName(cR, uri);
                        byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                        File file = null;
                        try {
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream fos = new FileOutputStream(file + "//" + name);
                            fos.write(inputData);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("thumbnail", e.getMessage());
                        }
                        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + name);


                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;

                        BitmapFactory.decodeFile(file2.getAbsolutePath(), options);
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;
                        /*CROP*/
                        try {
                            // call the standard crop action intent (the user device may not
                            // support it)
                            Intent cropIntent = new Intent("com.android.camera.action.CROP");
                            // indicate image type and Uri
                            cropIntent.setDataAndType(mPhotoUri, "image/*");
                            // set crop properties
                            cropIntent.putExtra("crop", true);
                            // indicate aspect of desired crop
                            cropIntent.putExtra("aspectX", 1);
                            cropIntent.putExtra("aspectY", 1.33);
                            // indicate output X and Y
                            //cropIntent.putExtra("outputX", imageWidth);
                            //cropIntent.putExtra("outputY", imageHeight);
                            // retrieve data on return
                            cropIntent.putExtra("return-data", true);
                            cropIntent.putExtra("return-eliminar", true);
                            // start the activity - we handle returning in onActivityResult

                            activity.startActivityForResult(cropIntent, 210);
                        }
                        // respond to users whose devices do not support the crop action
                        catch (ActivityNotFoundException anfe) {
                            Toast toast = Toast.makeText(context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        /*END CROP*/
                    } catch (IOException e) {
                        Toasty.error(context, "Error al asociar el documento a la solicitud").show();
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = context.getContentResolver().openInputStream(uri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        ContentResolver cR = context.getContentResolver();
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String type = cR.getType(uri);
                        String name = ManejadorAdjuntos.getFileName(cR, uri);
                        byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                        mDBHelper = new DataBaseHelper(context);
                        mDBHelper.addAdjuntoSolicitud(type, name, inputData);
                        mDBHelper.close();
                        Toasty.success(context, "Documento asociado correctamente.").show();
                    } catch (IOException e) {
                        Toasty.error(context, "Error al adjuntar el documento a la solicitud").show();
                        e.printStackTrace();
                    }

                }
                break;
            case 100://resultado firma de aceptaciones
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = context.getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //Agregar al tableView del UI
                    try {
                        byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, data.getType(), data.getExtras().getString("ImageName"), inputData);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(context, adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        HorizontalScrollView hsvn = (HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos");
                        ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsvn, hsvn.getContext(), activity,adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CheckBox politica = (CheckBox) mapeoCamposDinamicos.get("politica");
                    if(politica != null) {
                        politica.setChecked(true);
                        if(activity instanceof SolicitudActivity)
                            ((SolicitudActivity) activity).firma = true;
                        if(activity instanceof SolicitudModificacionActivity)
                            ((SolicitudModificacionActivity) activity).firma = true;
                        if(activity instanceof SolicitudCreditoActivity)
                            ((SolicitudCreditoActivity) activity).firma = true;
                        if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                            ((SolicitudAvisosEquipoFrioActivity) activity).firma = true;
                        politica.setEnabled(false);
                    }

                    CheckBox aceptacion_credito = (CheckBox) mapeoCamposDinamicos.get("aceptacion_credito");
                    if(aceptacion_credito != null) {
                        aceptacion_credito.setChecked(true);
                        //TODO firma depende de 2 o mas firmas como hacer para validar
                        if(activity instanceof SolicitudActivity)
                            ((SolicitudActivity) activity).firma = true;
                        if(activity instanceof SolicitudModificacionActivity)
                            ((SolicitudModificacionActivity) activity).firma = true;
                        if(activity instanceof SolicitudCreditoActivity)
                            ((SolicitudCreditoActivity) activity).firma = true;
                        if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                            ((SolicitudAvisosEquipoFrioActivity) activity).firma = true;
                        aceptacion_credito.setEnabled(false);
                    }
                    CheckBox constancia = (CheckBox) mapeoCamposDinamicos.get("constancia");
                    if(constancia != null) {
                        constancia.setChecked(true);
                        if(activity instanceof SolicitudActivity)
                            ((SolicitudActivity) activity).firma = true;
                        if(activity instanceof SolicitudModificacionActivity)
                            ((SolicitudModificacionActivity) activity).firma = true;
                        if(activity instanceof SolicitudCreditoActivity)
                            ((SolicitudCreditoActivity) activity).firma = true;
                        if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                            ((SolicitudAvisosEquipoFrioActivity) activity).firma = true;
                        constancia.setEnabled(false);
                    }
                    CheckBox aceptacion_letra = (CheckBox) mapeoCamposDinamicos.get("aceptacion_letra");
                    if(aceptacion_letra != null) {
                        aceptacion_letra.setChecked(true);
                        //TODO firma depende de 2 o mas firmas como hacer para validar
                        if(activity instanceof SolicitudActivity)
                            ((SolicitudActivity) activity).firma = true;
                        if(activity instanceof SolicitudModificacionActivity)
                            ((SolicitudModificacionActivity) activity).firma = true;
                        if(activity instanceof SolicitudCreditoActivity)
                            ((SolicitudCreditoActivity) activity).firma = true;
                        if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                            ((SolicitudAvisosEquipoFrioActivity) activity).firma = true;
                        aceptacion_letra.setEnabled(false);
                    }

                    Toasty.success(context, "Documento asociado correctamente.").show();
                }
                break;
            case 110://Aceptacion de firma numero 2, de contrato
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = context.getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //Agregar al tableView del UI
                    try {
                        byte[] inputData = getBytes(iStream);
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, data.getType(), data.getExtras().getString("ImageName"), inputData);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(context, adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        HorizontalScrollView hsvn = (HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos");
                        ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsvn, hsvn.getContext(), activity, adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CheckBox aceptacion_contrato = (CheckBox) mapeoCamposDinamicos.get("aceptacion_contrato");
                    if(aceptacion_contrato != null) {
                        aceptacion_contrato.setChecked(true);
                        if(activity instanceof SolicitudActivity)
                            ((SolicitudActivity) activity).firma = true;
                        if(activity instanceof SolicitudModificacionActivity)
                            ((SolicitudModificacionActivity) activity).firma = true;
                        if(activity instanceof SolicitudCreditoActivity)
                            ((SolicitudCreditoActivity) activity).firma = true;
                        if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                            ((SolicitudAvisosEquipoFrioActivity) activity).firma = true;
                        aceptacion_contrato.setEnabled(false);
                    }

                    CheckBox aceptacion_apc = (CheckBox) mapeoCamposDinamicos.get("aceptacion_apc");
                    if(aceptacion_apc != null) {
                        aceptacion_apc.setChecked(true);
                        //TODO firma depende de 2 o mas firmas como hacer para validar
                        if(activity instanceof SolicitudActivity)
                            ((SolicitudActivity) activity).firma = true;
                        if(activity instanceof SolicitudModificacionActivity)
                            ((SolicitudModificacionActivity) activity).firma = true;
                        if(activity instanceof SolicitudCreditoActivity)
                            ((SolicitudCreditoActivity) activity).firma = true;
                        if(activity instanceof SolicitudAvisosEquipoFrioActivity)
                            ((SolicitudAvisosEquipoFrioActivity) activity).firma = true;
                        aceptacion_apc.setEnabled(false);
                    }
                    Toasty.success(context, "Documento asociado correctamente.").show();
                }
                break;
            case 200://Resultado de archivo seleccionado, no borra archivo
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = context.getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toasty.error(context, "Imagen seleccionada ya no existe en el dispositivo!").show();
                        return;
                    }
                    try {
                        ContentResolver cR = context.getContentResolver();
                        String type = cR.getType(uri);
                        String name = ManejadorAdjuntos.getFileName(cR, uri);
                        byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                        File file = null;
                        try {
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream fos = new FileOutputStream(file + "//" + name);
                            fos.write(inputData);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("thumbnail", e.getMessage());
                        }
                        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + name);
                        file2 = FileHelper.saveBitmapToFile(file2);

                        byte[] bytesArray = new byte[(int) file2.length()];

                        FileInputStream fis = new FileInputStream(file2);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                        //Agregar al tableView del UI
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, type, name, bytesArray);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(context, adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        HorizontalScrollView hsvn = (HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos");
                        ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsvn, hsvn.getContext(), activity, adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

                        Toasty.success(context, "Documento asociado correctamente.").show();
                    } catch (IOException e) {
                        Toasty.error(context, "Error al asociar el documento a la solicitud").show();
                        e.printStackTrace();
                    }
                }
                break;
            case 210://Resultado de crop de camara, si borra archivo
                if (resultCode == RESULT_OK) {
                    Uri uri = null;
                    if (data != null)
                        uri = data.getData();
                    if (uri == null) {
                        uri = mPhotoUri;
                    }
                    InputStream iStream = null;
                    try {
                        iStream = context.getContentResolver().openInputStream(uri);
                        //Bitmap yourSelectedImage = BitmapFactory.decodeStream(iStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toasty.error(context, "Imagen seleccionada ya no existe en el dispositivo!").show();
                        return;
                    }
                    try {
                        ContentResolver cR = context.getContentResolver();
                        String type = cR.getType(uri);
                        String name = ManejadorAdjuntos.getFileName(cR, uri);
                        byte[] inputData = ManejadorAdjuntos.getBytes(iStream);
                        File file = null;
                        try {
                            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream fos = new FileOutputStream(file + "//" + name);
                            fos.write(inputData);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("thumbnail", e.getMessage());
                        }
                        File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//" + name);
                        file2 = FileHelper.saveBitmapToFile(file2);

                        byte[] bytesArray = new byte[(int) file2.length()];

                        FileInputStream fis = new FileInputStream(file2);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                        file2.delete();
                        //Agregar al tableView del UI
                        Adjuntos nuevoAdjunto = new Adjuntos(GUID, type, name, bytesArray);

                        adjuntosSolicitud.add(nuevoAdjunto);
                        AdjuntoTableAdapter stda = new AdjuntoTableAdapter(context, adjuntosSolicitud);
                        stda.setPaddings(10, 5, 10, 5);
                        stda.setTextSize(10);
                        stda.setGravity(GRAVITY_CENTER);
                        //tb_adjuntos.getLayoutParams().height = tb_adjuntos.getLayoutParams().height+(adjuntosSolicitud.size()*(alturaFilaTableView-20));
                        tb_adjuntos.setDataAdapter(stda);

                        HorizontalScrollView hsvn = (HorizontalScrollView) mapeoCamposDinamicos.get("GaleriaAdjuntos");
                        ManejadorAdjuntos.MostrarGaleriaAdjuntosHorizontal(hsvn, hsvn.getContext(), activity, adjuntosSolicitud, modificable, firma, tb_adjuntos, mapeoCamposDinamicos);

                        //Intento de borrar el archivo que se guarda automatico en Pictures
                        File file3 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//Pictures//" + name);
                        file3.delete();

                        Toasty.success(context, "Documento asociado correctamente.").show();
                    } catch (IOException e) {
                        Toasty.error(context, "Error al asociar el documento a la solicitud").show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String getFileName(ContentResolver resolver, Uri uri) {
        String name = "";
        try {
            Cursor returnCursor = resolver.query(uri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            name = returnCursor.getString(nameIndex);
            returnCursor.close();
        }catch (Exception e){
            name = new File(uri.getPath()).getName();
        }
        return name;
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
}
