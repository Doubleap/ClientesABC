package proyecto.app.clientesabc.clases;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;

public class TesseractOCR {

    private final TessBaseAPI mTess;

    public TesseractOCR(Context context, String language) {
        mTess = new TessBaseAPI();
        boolean fileExistFlag = false;

        AssetManager assetManager = context.getAssets();

        String dstPathDir = "/tesseract/tessdata/";

        String srcFile = "eng.traineddata";
        InputStream inFile = null;

        dstPathDir = context.getFilesDir() + dstPathDir;
        String dstInitPathDir = context.getFilesDir() + "/tesseract";
        String dstPathFile = dstPathDir + srcFile;
        FileOutputStream outFile = null;

        try {
            inFile = assetManager.open(srcFile);

            File f = new File(dstPathDir);

            if (!f.exists()) {
                if (!f.mkdirs()) {
                    Toasty.error(context, srcFile + " no pudo ser creado.", Toasty.LENGTH_SHORT).show();
                }
                outFile = new FileOutputStream(new File(dstPathFile));
            } else {
                fileExistFlag = true;
            }

        } catch (Exception ex) {
            Toasty.error(context, ex.getMessage(), Toasty.LENGTH_SHORT).show();
        } finally {

            if (fileExistFlag) {
                try {
                    if (inFile != null) inFile.close();
                    mTess.init(dstInitPathDir, language);
                    return;

                } catch (Exception ex) {
                    Toasty.error(context, ex.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            }

            if (inFile != null && outFile != null) {
                try {
                    //copy file
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inFile.read(buf)) != -1) {
                        outFile.write(buf, 0, len);
                    }
                    inFile.close();
                    outFile.close();
                    mTess.init(dstInitPathDir, language);
                } catch (Exception ex) {
                    Toasty.error(context, ex.getMessage(), Toasty.LENGTH_SHORT).show();
                }
            } else {
                Toasty.error(context, srcFile + " no pudo ser leido.", Toasty.LENGTH_SHORT).show();
            }
        }
    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
        return mTess.getUTF8Text();
    }
    public String getOCRResult(File file) {
        mTess.setImage(file);
        mTess.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890");
        return mTess.getUTF8Text();
    }
    public void setPageSegMode(int segMode) {
        mTess.setPageSegMode(segMode);
    }

    public void onDestroy() {
        if (mTess != null) mTess.clear();
    }
}