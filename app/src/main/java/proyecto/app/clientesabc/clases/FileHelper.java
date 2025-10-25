package proyecto.app.clientesabc.clases;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.VariablesGlobales;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;

public class FileHelper {
    private static final int BUFFER_SIZE = 8192 ;//2048;
    private static String TAG= FileHelper.class.getName();
    private static String parentPath ="";


    public static boolean zip( String sourcePath, String destinationPath, String destinationFileName, Boolean includeParentFolder)  {
        boolean p = new File(destinationPath ).mkdirs();
        if(p){
            Log.d(TAG, "Direccion " + destinationPath + " creado.");
        }
        FileOutputStream fileOutputStream ;
        ZipOutputStream zipOutputStream =  null;
        try{
            if (!destinationPath.endsWith("/")) destinationPath+="/";
            String destination = destinationPath + destinationFileName;
            File file = new File(destination);
            if (!file.exists()) {
                final boolean newFile = file.createNewFile();
                if(newFile){
                    Log.d(TAG, "Archivo " + destination + " creado.");
                }
            }

            fileOutputStream = new FileOutputStream(file);
            zipOutputStream =  new ZipOutputStream(new BufferedOutputStream(fileOutputStream));

            if (includeParentFolder)
                parentPath=new File(sourcePath).getParent() + "/";
            else
                parentPath=sourcePath;

            zipFile(zipOutputStream, sourcePath, destinationFileName.replace(".zip",""));

        }
        catch (IOException ioe){
            Log.d(TAG,ioe.getMessage());
            return false;
        }finally {
            if(zipOutputStream!=null)
                try {
                    zipOutputStream.close();
                } catch(IOException e) {
                    Log.d(TAG,e.getMessage());
                }
        }

        return true;

    }

    private static void

    zipFile(ZipOutputStream zipOutputStream, String sourcePath, String fileName ) throws  IOException{

        java.io.File files = new java.io.File(sourcePath);
        java.io.File[] fileList = files.listFiles();

        String entryPath;
        BufferedInputStream input;
        for (java.io.File file : fileList) {
            if (file.isDirectory()) {
                zipFile(zipOutputStream, file.getPath(), file.getName());
            } else {
                if(fileName != null && fileName.equals(file.getName())) {
                    byte data[] = new byte[BUFFER_SIZE];
                    FileInputStream fileInputStream = new FileInputStream(file.getPath());
                    input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                    entryPath = file.getAbsolutePath().replace(parentPath, "");

                    ZipEntry entry = new ZipEntry(entryPath);
                    zipOutputStream.putNextEntry(entry);

                    int count;
                    while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
                        zipOutputStream.write(data, 0, count);
                    }
                    input.close();
                }else if(fileName == null){
                    byte data[] = new byte[BUFFER_SIZE];
                    FileInputStream fileInputStream = new FileInputStream(file.getPath());
                    input = new BufferedInputStream(fileInputStream, BUFFER_SIZE);
                    entryPath = file.getAbsolutePath().replace(parentPath, "");

                    ZipEntry entry = new ZipEntry(entryPath);
                    zipOutputStream.putNextEntry(entry);

                    int count;
                    while ((count = input.read(data, 0, BUFFER_SIZE)) != -1) {
                        zipOutputStream.write(data, 0, count);
                    }
                    input.close();
                }
            }
        }



    }

    public static Boolean unzip(String sourceFile, String destinationFolder)  {
        ZipInputStream zis = null;

        try {
            zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceFile)));
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((ze = zis.getNextEntry()) != null) {
                String fileName = ze.getName();
                fileName = fileName.substring(fileName.indexOf("/") + 1);
                File file = new File(destinationFolder, fileName);
                File dir = ze.isDirectory() ? file : file.getParentFile();

                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Invalid path: " + dir.getAbsolutePath());
                if (ze.isDirectory()) continue;
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                }

            }
        } catch (IOException  ioe){
            Log.d(TAG,ioe.getMessage());
            return false;
        }  finally {
            if(zis!=null)
                try {
                    zis.close();
                } catch(IOException e) {
                    Log.d(TAG,e.getMessage());
                }
        }
        return true;
    }

    public static  void saveToFile( String destinationPath, String data, String fileName){
        try {
            boolean p = new File(destinationPath).mkdirs();
            if(p) {
                Log.d(TAG, "Direccion " + destinationPath + " creado.");
            }
                File file = new File(destinationPath + fileName);
                boolean creado;
                if (!file.exists()) {
                    creado = file.createNewFile();
                    if (creado) {
                        Log.d(TAG, "Archivo " + fileName + " creado.");
                    }
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());


        } catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    public static File saveBitmapToFile(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 1;
            // factor of downsizing the image
            int calidadImagen = 100;


            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 4;
            if(file.length() < 200000)
                o2.inSampleSize = 2;
            if(file.length() > 200000)
                o2.inSampleSize = 4;
            if(file.length() > 450000)
                o2.inSampleSize = 4;

            if(file.getName().contains("PoliticaPrivacidad") || file.getName().contains("Aceptacion")){
                o2.inSampleSize = 2;
                calidadImagen = 50;
            }

            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            if(selectedBitmap != null) {
                // here i override the original image file
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);

                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, calidadImagen, outputStream);
            }
            return file;
        } catch (Exception e) {
            return file;
        }
    }

    public static File saveBitmapToFile(File file, Context context){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 1;
            // factor of downsizing the image
            DataBaseHelper DBH = new DataBaseHelper(context);
            int calidadConfigurada = DBH.CalidadDeAdjuntos();
            int calidadImagen = 75;
            if(calidadConfigurada != -1 && calidadConfigurada > 0 && calidadConfigurada <= 100)
                calidadImagen = calidadConfigurada;


            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 4;
            if(file.length() < 200000)
                o2.inSampleSize = 2;
            if(file.length() > 200000)
                o2.inSampleSize = 4;
            if(file.length() > 450000)
                o2.inSampleSize = 4;

            if(file.getName().contains("PoliticaPrivacidad") || file.getName().contains("Aceptacion")){
                o2.inSampleSize = 2;
                calidadImagen = 50;
            }

            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            if(selectedBitmap != null) {
                // here i override the original image file
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);

                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, calidadImagen, outputStream);
            }
            return file;
        } catch (Exception e) {
            return file;
        }
    }
    public static File saveBitmapToFileNoReduction(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 1;
            // factor of downsizing the image
            int calidadImagen = 75;

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 4;
            if(file.length() < 200000)
                o2.inSampleSize = 2;
            if(file.length() > 200000)
                o2.inSampleSize = 4;
            if(file.length() > 450000)
                o2.inSampleSize = 4;

            if(file.getName().contains("PoliticaPrivacidad") || file.getName().contains("Aceptacion")){
                o2.inSampleSize = 2;
                calidadImagen = 45;
            }

            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            if(selectedBitmap != null) {
                // here i override the original image file
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);

                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, calidadImagen, outputStream);
            }
            return file;
        } catch (Exception e) {
            return file;
        }
    }
    public static void copyToPublicDownload(Context context, String path, String fileName) {
        File sourceFile = new File(path, fileName);

        if (!sourceFile.exists()) {
            showToast(context, "Archivo no encontrado");
            return;
        }

        OutputStream outStream = null;
        InputStream inStream = null;

        try {
            // Create MediaStore entry for Downloads
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName.replace(".apk","_"+VariablesGlobales.getSociedad()+".apk"));
            values.put(MediaStore.Downloads.MIME_TYPE, getMimeType(fileName));
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri externalUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            Uri newFileUri = context.getContentResolver().insert(externalUri, values);

            if (newFileUri == null) {
                showToast(context, "No se pudo crear el archivo en Descargas");
                return;
            }

            inStream = new FileInputStream(sourceFile);
            outStream = context.getContentResolver().openOutputStream(newFileUri);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            outStream.flush();
            showToast(context, "Archivo copiado a Descargas con éxito");

        } catch (Exception e) {
            e.printStackTrace();
            Toasty.warning(context, "Error: " + e.getMessage(), Toasty.LENGTH_SHORT).show();
        } finally {
            try {
                if (inStream != null) inStream.close();
                if (outStream != null) outStream.close();
            } catch (IOException ignored) {}
        }
    }

    private static String getMimeType(String fileName) {
        if (fileName.endsWith(".pdf")) return "application/pdf";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".txt")) return "text/plain";
        return "application/octet-stream";
    }
    private static void showToast(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toasty.warning(context, message, Toasty.LENGTH_SHORT).show()
        );
    }
}