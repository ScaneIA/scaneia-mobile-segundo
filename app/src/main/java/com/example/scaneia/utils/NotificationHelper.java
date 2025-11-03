package com.example.scaneia.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.scaneia.NoticiaWebViewActivity;
import com.example.scaneia.R;
public class NotificationHelper {

    private static final String CANAL_ID = "notificacao_canal_id";
    private static final int NOTIFICATION_ID = 100;

    // Cria o canal de notificação (chame uma vez no onCreateView ou onCreate)
    public static void criarCanalNotificacao(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CANAL_ID,
                    "Notificações Importantes",
                    NotificationManager.IMPORTANCE_HIGH // IMPORTANCE_HIGH = heads-up
            );
            channel.setDescription("Canal para notificações com popup");
            channel.enableVibration(true);
            channel.setShowBadge(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Mostra a notificação
    public static void mostrarNotificacao(Context context, String titulo, String mensagem, String url) {

        // Verifica permissão (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        ((android.app.Activity) context),
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
                return;
            }
        }

        // Intent ao clicar na notificação (abre WebView)
        Intent intent = new Intent(context, NoticiaWebViewActivity.class);
        intent.putExtra(NoticiaWebViewActivity.EXTRA_LINK, url); // passa a URL
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_MAX) // máximo = heads-up
                .setDefaults(NotificationCompat.DEFAULT_ALL)   // som, vibração, luz
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

}