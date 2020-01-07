package com.nutrilife.app

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nutrilife.app.Clases.VAR
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var sharedPref: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_hoy, R.id.navigation_progreso, R.id.navigation_menu))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setItemIconTintList(null)

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
