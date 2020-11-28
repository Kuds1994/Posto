package com.kudu.posto.notificacoes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kudu.posto.R;
import com.kudu.posto.beans.Device;
import com.kudu.posto.beans.DeviceService;
import com.kudu.posto.beans.User;
import com.kudu.posto.beans.UserService;
import com.kudu.posto.conexao.ConectarBanco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Notificacoes extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {


        if(remoteMessage.getNotification() != null){
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d("token", "Refreshed token: " + s);

        enviarToken(s);
    }

    private void enviarToken(String token){

        Device device = new Device();
        device.setRegistration_id(token);
        device.setCloud_message_type("FCM");

        ConectarBanco conectarBanco = new ConectarBanco(this);
        DeviceService deviceService = conectarBanco.RetroConfig().create(DeviceService.class);
        Call<Device> user = deviceService.post(device);
        user.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if(response.code() == 200){
                    Log.d("token", "token salvo");
                }
            }
            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }


    private void sendNotification(String messageBody) {

        String channelId = "asdasdasadadasd.,";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_gasoline_pump)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentTitle("Titulo")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nome";
            String description = "asdadsasd";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

}
