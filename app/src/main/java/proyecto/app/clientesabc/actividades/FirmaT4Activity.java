package proyecto.app.clientesabc.actividades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.clases.FileHelper;

public class FirmaT4Activity extends AppCompatActivity {

    private Button btnClear, btnSave, btnZoomIn, btnZoomOut;
    private File file;
    private LinearLayout completo;
    private ScrollView scroll;
    private FrameLayout documento;
    private LinearLayout canvasLL;
    private View view;
    private signature mSignature;
    private Bitmap bitmap;

    // Creating Separate Directory for saving Generated Images
    String DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Signature/";
    String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String StoredPath = DIRECTORY + "ConstanciaSolicitudT4_"+pic_name + ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmat4);

        completo = findViewById(R.id.completo);
        documento = findViewById(R.id.documento);
        canvasLL = findViewById(R.id.canvasLL);
        scroll = findViewById(R.id.scroll);
        mSignature = new signature(getApplicationContext(), null);

        //mSignature.setBackgroundColor(Color.WHITE);

        Drawable d = getResources().getDrawable(R.drawable.squared_textbackground,null);
        documento.setBackground(d);
        // Dynamically generating Layout through java code
        canvasLL.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mSignature.getParent().requestDisallowInterceptTouchEvent(true);
        canvasLL.getParent().requestDisallowInterceptTouchEvent(true);
        canvasLL.requestDisallowInterceptTouchEvent(true);

        btnClear = findViewById(R.id.btnclear);
        btnSave = findViewById(R.id.btnsave);
        btnZoomIn = findViewById(R.id.btnzoomin);
        btnZoomOut = findViewById(R.id.btnzoomout);

        view = completo;

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignature.clear();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setDrawingCacheEnabled(true);
                mSignature.save(view,StoredPath);
            }
        });

        btnZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSignature.clear();
                if(mSignature.getScaleX() <= 2f) {
                    mSignature.setScaleX(mSignature.getScaleX()+0.1f);
                    mSignature.setScaleY(mSignature.getScaleY()+0.1f);
                }else{
                    Toasty.info(getBaseContext(),"Escala máxima").show();
                }
            }
        });

        btnZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSignature.clear();
                if(mSignature.getScaleX() > 0.3f) {
                    mSignature.setScaleX(mSignature.getScaleX() - 0.1f);
                    mSignature.setScaleY(mSignature.getScaleY() - 0.1f);
                }else{
                    Toasty.info(getBaseContext(),"Escala mínima").show();
                }
            }
        });

        // Method to create Directory, if the Directory doesn't exists
        file = new File(DIRECTORY);
        if (!file.exists()) {
            boolean creado = file.mkdir();
        }
    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 3f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v, String StoredPath) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());

            PathMeasure mp = new PathMeasure(path,false);
            if(path.isEmpty()){
                Toasty.warning(getBaseContext(),"Las politicas deben ser firmadas por el cliente!").show();
                return;
            }
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
            }

            Canvas canvas = new Canvas(bitmap);

            try {
                // Output the file
                FileOutputStream mFileOutStream = new FileOutputStream(StoredPath);
                v.draw(canvas);

                // Convert the output file to Image such as .png
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();

                //Una vez guardado en el archivo Signature, se procede a ligar la imagen al formulario activo
                Toasty.success(getBaseContext(),"Documento asociado correctamente.").show();
                Intent resultIntent = new Intent();
                File file = new File(StoredPath);

                file = FileHelper.saveBitmapToFile(file);

                MimeTypeMap mime = MimeTypeMap.getSingleton();
                int index = file.getName().lastIndexOf('.')+1;
                String ext = file.getName().substring(index).toLowerCase();
                String type = mime.getMimeTypeFromExtension(ext);
                resultIntent.setDataAndType(Uri.fromFile(file), type);
                resultIntent.putExtra("ImageName", file.getName());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }

        }

        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean performClick() {
            return super.performClick();
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mSignature.getParent().requestDisallowInterceptTouchEvent(true);
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Evento Touch Ignorado: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }


        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
