package com.nutrilife.app.Clases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nutrilife.app.LoginActivity
import com.nutrilife.app.R
import com.nutrilife.app.SplashActivity

class HorarioAlarmaReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.e("myerror", "horario fire")


        val idhorario =   intent.getIntExtra("idhorario", 1)
        val arrHorario = arrayOf("DESAYUNO", "COLACION1", "ALMUERZO", "COLACION2", "CENA")
        val horario = arrHorario[idhorario-1]

        Log.e("myerror", "horario: $horario")
        val nintent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 200 + idhorario, nintent, 0)

        val builder = NotificationCompat.Builder(context, context.getString(R.string.notificacion_comidaid))
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Recordatorio de $horario")
            .setContentText("No se olvide de comer saludable de acuerdo a lo recomendado en la aplicación")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
         with(NotificationManagerCompat.from(context)) {
             notify(150 + idhorario , builder.build())
         }


    }
}