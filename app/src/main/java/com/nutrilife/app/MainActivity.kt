package com.nutrilife.app

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nutrilife.app.Clases.VAR
import java.util.*


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

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
