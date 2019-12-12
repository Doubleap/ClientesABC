package proyecto.app.clientesabc.clases;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class Notificacion {
    Context context;
    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nombre";
            String description = "Descripcion";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ABClientes", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notificacion(Context context){
        this.context = context;
    }

    public void crearNotificacion(int id, String titulo, String mensaje, int iconoGrande, int iconoPeq, int colorPeq){
        //Prueba de notificacion para el APP
        Bitmap bitmapIcon = BitmapFactory.decodeResource(context.getResources(), iconoGrande);
        NotificationCompat.Builder notiCompat = new NotificationCompat.Builder(context, "ABClientes")
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setSmallIcon(iconoPeq)
                .setColor(context.getColor(colorPeq))
                .setLargeIcon(bitmapIcon)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = notiCompat.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notification);
    }
}
