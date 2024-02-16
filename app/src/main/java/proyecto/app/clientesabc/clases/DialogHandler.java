package proyecto.app.clientesabc.clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import proyecto.app.clientesabc.R;
import proyecto.app.clientesabc.adaptadores.BaseInstaladaAdapter;

public class DialogHandler {
    public Runnable ans_true = null;
    public BaseInstaladaAdapter.DesactivarRegistroCenso mithread = null;
    // Dialog. --------------------------------------------------------------

    public boolean Confirm(Activity act, String Title, String ConfirmText,
                           String CancelBtn, String OkBtn, Runnable aProcedure) {
        ans_true = aProcedure;
        AlertDialog dialog = new AlertDialog.Builder(act).create();
        dialog.setTitle(Title);
        //LayoutInflater inflater = act.getLayoutInflater();
        //View view = inflater.inflate(R.layout.titlebar, null);
        //dialog.setCustomTitle(view);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        ans_true.run();
                        dialog.dismiss();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        dialog.dismiss();
                    }
                });
        dialog.setIcon(R.drawable.icon_info_title);
        dialog.setInverseBackgroundForced(true);
        dialog.show();
        return true;
    }

    public boolean Confirm(Activity act, String Title, String ConfirmText,
                           String CancelBtn, String OkBtn, BaseInstaladaAdapter.DesactivarRegistroCenso aProcedure) {
        mithread = aProcedure;
        AlertDialog dialog = new AlertDialog.Builder(act).create();
        dialog.setTitle(Title);
        //LayoutInflater inflater = act.getLayoutInflater();
        //View view = inflater.inflate(R.layout.titlebar, null);
        //dialog.setCustomTitle(view);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        mithread.execute();
                        dialog.dismiss();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        dialog.dismiss();
                    }
                });
        dialog.setIcon(R.drawable.icon_info_title);
        dialog.setInverseBackgroundForced(true);
        dialog.show();
        return true;
    }

}
