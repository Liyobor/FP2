package com.example.fingerprint2;

import android.content.Context;
import android.widget.Toast;

public class ToastController {

    public static void showToast(Context ctx, String message){

        if(GlobalInformation.toast!=null){
            GlobalInformation.toast.cancel();
        }
        GlobalInformation.toast = Toast.makeText(ctx,message,Toast.LENGTH_LONG);
        GlobalInformation.toast.show();
    }
}
