package com.example.scaneia.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.example.scaneia.R;

public class DialogsUtils {
//    public static void mostrarToast(Context context, String mensagem, boolean sucesso) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        int layoutId = sucesso ? R.layout.dialog_sucesso : R.layout.dialog_erro;
//
//        View layout = inflater.inflate(layoutId, null);
//        TextView text = layout.findViewById(R.id.toast_text);
//        text.setText(mensagem);
//
//        Toast toast = new Toast(context);
//        toast.setView(layout);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.BOTTOM, 0, 150);
//        toast.show();
//    }

    public static void mostrarToast(Context context, String mensagem, boolean sucesso) {
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutId = sucesso ? R.layout.dialog_sucesso : R.layout.dialog_erro;
        View layout = inflater.inflate(layoutId, null);
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(mensagem);
        // animação de entrada
        Animation animEntrada = AnimationUtils.loadAnimation(context, R.anim.dialog_entrada);
        layout.startAnimation(animEntrada);
        Toast toast = new Toast(context);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 590);
        toast.show();
        // animação de saída (depois de 1.5 segundos)
        new android.os.Handler().postDelayed(() -> {
            Animation animSaida = AnimationUtils.loadAnimation(context, R.anim.dialog_saida);
            layout.startAnimation(animSaida);
        }, 1500);
    }

}
