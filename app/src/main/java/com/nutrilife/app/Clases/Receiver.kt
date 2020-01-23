package com.nutrilife.app.Clases

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class Receiver : BroadcastReceiver() {
    private var alarmMgr: AlarmManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {

            registrar(context)
        }
    }

    fun registrar(context: Context){

        val horarios = ClsHorario.horariosRango
        val ahora: Calendar = Calendar.getInstance()
        val hours = ahora.get(Calendar.HOUR_OF_DAY)
        val mins = ahora.get(Calendar.MINUTE)
        ahora.set(Calendar.MINUTE, 0)
        ahora.set(Calendar.SECOND, 0)



        for (i in 0..horarios.size-1){

            val index = i
                alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                Log.e("myerror", "horario $index")
                var alarmIntent = Intent(context, HorarioAlarmaReceiver::class.java)
                alarmIntent.putExtra("idhorario", index+1)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    100 + index,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val str = horarios[i]
                val a = str.split(":")
                val hora = a[0].toInt()
                val min = a[1].toInt()
                val calendar: Calendar = Calendar.getInstance()
                //calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hora)
                calendar.set(Calendar.MINUTE, min)
                calendar.set(Calendar.SECOND, 0)

                if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
                    calendar.add(Calendar.DATE, 1)
                }else if(calendar.timeInMillis < ahora.timeInMillis){
                    calendar.add(Calendar.DATE, 1)
                }else if(calendar.get(Calendar.HOUR_OF_DAY) == hours &&
                          mins - calendar.get(Calendar.MINUTE) > 10 ){
                    calendar.add(Calendar.DATE, 1)
                }

                Log.e("myerror", "Registrado $index")
                Log.e("myerror", "timeinmillis :  ${calendar.timeInMillis}")

                alarmMgr?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }

    }
}