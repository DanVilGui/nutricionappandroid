package com.nutrilife.app

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nutrilife.app.Clases.HorarioAlarmaReceiver
import com.nutrilife.app.Clases.Receiver
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.Fragments.AdvertenciaDietaDialog
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notificacion_comida)
            val descriptionText = getString(R.string.notificacion_comida_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notificacion_comidaid), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val alarmMgr = this.getSystemService(ALARM_SERVICE) as AlarmManager

        val receiver = ComponentName(this, Receiver::class.java)
        val broadcast = Receiver()
        broadcast.registrar(this)
        /*
        var alarmIntent = Intent(this, HorarioAlarmaReceiver::class.java)
        alarmIntent.putExtra("idhorario", 2+1)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            101,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 20)
        calendar.set(Calendar.MINUTE, 10)
        calendar.set(Calendar.SECOND, 0)

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

         */

//        broadcast.registrar(this)
        /*
        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

         */

        /*
        try {
            val info = packageManager.getPackageInfo(
                "com.nutrilife.app",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.e("KeyHash:", Base64.getEncoder().encodeToString(md.digest()))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }

         */

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        sharedPref = getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        mostrarAdvertenciaDieta()
        verificarFinDieta()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_hoy, R.id.navigation_progreso, R.id.navigation_menu))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setItemIconTintList(null)

    }
    fun mostrarAdvertenciaDieta(){
        val mostrar = sharedPref?.getString(VAR.PREF_ADVERTENCIA, "")
        if(mostrar != ""){
            val fragReferenciaDialog = AdvertenciaDietaDialog()
            fragReferenciaDialog.show(supportFragmentManager, "dialogfrag")
        }
    }
    fun verificarFinDieta(){

        val datosPersona = sharedPref?.getString(VAR.PREF_DATA_USUARIO, "")
        if(datosPersona!=""){
            val datos = JSONObject(datosPersona)
            if(!datos.isNull("ultimadieta")){
                try {
                    val ultimadieta = datos.getString("ultimadieta")
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val fechaActual = dateFormat.parse(DatePickerActivityFragment.formatFecha())
                    Log.e("myerror",fechaActual.toString())
                    val fechaDieta = dateFormat.parse(ultimadieta)
                    Log.e("myerror",fechaDieta.toString())

                    if (fechaDieta.before(fechaActual)){
                        val intent = Intent(this, ActualizarMedidasActivity::class.java)
                        intent.putExtra("control", true)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                }catch (ex:Exception){

                }
            }
        }
    }

    class DatePickerActivityFragment : DialogFragment() {

        var listener: DatePickerDialog.OnDateSetListener? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val arr = fecha.split("-")
            val year = arr[0].toInt()
            val month = arr[1].toInt()-1
            val day = arr[2].toInt()
            val now = System.currentTimeMillis() - 1000
            val datePickerDialog  = DatePickerDialog(activity!!, listener, year, month, day)
            datePickerDialog.datePicker.maxDate = now+(1000*60*60*24*7)
            return datePickerDialog

        }

        companion object {

            fun formatFecha():String{
                val c = Calendar.getInstance()
                val anio = c.get(Calendar.YEAR)
                val mes = c.get(Calendar.MONTH)
                val dia = c.get(Calendar.DAY_OF_MONTH)
                return  anio.toString() +"-"+ (mes+1).toString().padStart(2,'0') +"-" + dia.toString().padStart(2, '0')
            }

            fun formatHora():String{
                val c = Calendar.getInstance()
                val hora = c.get(Calendar.HOUR_OF_DAY)
                val minuto = c.get(Calendar.MINUTE)
                return  hora.toString().padStart(2,'0')+":"+minuto.toString().padStart(2,'0')
            }

            fun fechaHoy(){
                fecha = formatFecha()
            }
            var fecha:String = ""

            fun newInstance(listener: DatePickerDialog.OnDateSetListener): DatePickerActivityFragment {
                val fragment = DatePickerActivityFragment()
                fragment.listener = listener
                return fragment
            }
        }

    }

    override fun onDestroy() {
        Handler().removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
