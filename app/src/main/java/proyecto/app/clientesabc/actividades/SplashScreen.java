package proyecto.app.clientesabc.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.VariablesGlobales;


public class SplashScreen extends AppCompatActivity {

    Thread splashTread;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeNoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        int[] ids = new int[]{R.drawable.splash,R.drawable.splash_gt, R.drawable.splash_volcanes, R.drawable.splash_abasa, R.drawable.splash_uruguay};
        //Random randomGenerator = new Random();
        //int r= randomGenerator.nextInt(ids.length);
        switch(VariablesGlobales.getSociedad()){
            case "F443":
            case "F445":
            case "F451":
                this.imageView.setImageDrawable(getResources().getDrawable(ids[0],null));
                break;
            case "F446":
                this.imageView.setImageDrawable(getResources().getDrawable(ids[1],null));
                break;
            case "1657":
                this.imageView.setImageDrawable(getResources().getDrawable(ids[2],null));
                break;
            case "1658":
                this.imageView.setImageDrawable(getResources().getDrawable(ids[3],null));
                break;
            case "1661":
            case "Z001":
                this.imageView.setImageDrawable(getResources().getDrawable(ids[4],null));
                break;
            default:
                break;
        }


        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(SplashScreen.this,
                            LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashScreen.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    SplashScreen.this.finish();
                }

            }
        };
        splashTread.start();
    }

}
