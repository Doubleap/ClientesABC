package proyecto.app.clientesabc.clases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import proyecto.app.clientesabc.adaptadores.DataBaseHelper;

public class Haversine {
    private static final double R = 6372.8; // Radio de la Tierra en kilómetros

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    public static String ClosestCoordinateRoute(double latitud, double longitud, DataBaseHelper db) {
        String reparto = "";
        double minDistance = Double.MAX_VALUE;
        HashMap<String, Object> closestCoordinate = null;
        ArrayList<HashMap<String, Object>> coordinates =  db.getListaCoordenadasHabilitador();
        for (HashMap<String, Object> coordinate : coordinates) {
            double distance = Haversine.distance(latitud, longitud, (Double)coordinate.get("latitud"), (Double)coordinate.get("longitud"));
            if (distance < minDistance) {
                minDistance = distance;
                closestCoordinate = coordinate;
            }
        }

        if (closestCoordinate != null) {
            //Se debe validar si esta en la PAVENT?
            if(db.rutaEnPavent(closestCoordinate.get("reparto").toString())) {
                reparto = closestCoordinate.get("reparto").toString();
            }
        }
        return reparto;
    }


}
