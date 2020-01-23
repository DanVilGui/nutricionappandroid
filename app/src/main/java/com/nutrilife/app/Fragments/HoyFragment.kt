package com.nutrilife.app.Fragments

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Adapters.DietaBlockListAdapter
import com.nutrilife.app.Clases.*
import com.nutrilife.app.MainActivity
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_hoy.*
import org.json.JSONObject
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

class HoyFragment : Fragment() {

    var contenedorDietas:LinearLayout? = null
    var contenedorTerminar:LinearLayout? = null
    var sharedPref: SharedPreferences? = null
    var adaptador :DietaBlockListAdapter ? = null
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    var listaDietas :LinkedList<ClsDietaBlock> = LinkedList()
    var txtFechaFormat:TextView?=null
    var recyclerView:RecyclerView ? = null
    var btnTerminar:Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hoy, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )
        sharedPref?.edit {
            putString(VAR.FECHA_HOY, fechaAndroid())
        }


        val mainActivity = activity as MainActivity
        mainActivity.verificarFinDieta()
        MainActivity.DatePickerActivityFragment.fechaHoy()
        recyclerView = view.findViewById(R.id.recyclerView)
       txtFechaFormat = view.findViewById(R.id.fecha)
        contenedorDietas = view.findViewById(R.id.contenedorDietas)
        contenedorTerminar = view.findViewById(R.id.contenedorTerminar)

        val recyclerView:RecyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
         buscarDatos()
        }
       buscarDatos()


        adaptador = DietaBlockListAdapter(activity!!,listaDietas)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adaptador
        }


        val btnCalendario: ImageView = view.findViewById(R.id.btnCalendario)
        btnCalendario.setOnClickListener {
            val newFragment = MainActivity.DatePickerActivityFragment.newInstance(DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val dia = day.toString().padStart(2, '0')
                val mes = (month + 1).toString().padStart(2, '0')
                val selectedDate = dia + " / " + mes + " / " + year
                val f = year.toString() + "-" + mes + "-" + dia
                MainActivity.DatePickerActivityFragment.fecha = f
                buscarDatos()
            })
            newFragment.show(fragmentManager!!, "datePicker")
        }

        btnTerminar = view.findViewById(R.id.btnTerminar)
        btnTerminar?.setOnClickListener {
            terminarDia()
        }
        actualizarDiaActual()
        return view
    }

    fun buscarDatos(){

        mensajedescanso?.visibility = View.GONE
        val parser = SimpleDateFormat("yyyy-MM-dd")
        val fecha = parser.parse(MainActivity.DatePickerActivityFragment.fecha)

        val dateFormatSymbols = DateFormatSymbols(Locale.getDefault())
        dateFormatSymbols.setWeekdays(
            arrayOf("", "Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sábado"))
        dateFormatSymbols.setMonths(arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Setiembre", "Octubre", "Noviembre", "Diciembre"))
        val formatter = SimpleDateFormat("EEEE, dd 'de' MMMM", dateFormatSymbols)
        txtFechaFormat?.text = formatter.format(fecha)
        val parameters = JSONObject()
        parameters.put("fecha", MainActivity.DatePickerActivityFragment.fecha)
        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        mostrarTerminarDia(false)

        if(( fechaPrefsHoy() == fechaSeleccionada() )&& getDietaHoy() != "" ){
            Log.e("myerror", "carga desde prefs")
            val json = JSONObject(getDietaHoy())
            procesarResponseDieta(json)
        }else{
            Log.e("myerror", "carga desde webservice")

            recyclerView?.visibility = View.GONE
            val request : JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, VAR.url("persona_dieta_fecha"), parameters,
                Response.Listener { response ->

                    if(( fechaAndroid() == fechaSeleccionada() ) && getDietaHoy() == "" ){
                        sharedPref?.edit {
                            putString(VAR.FECHA_HOY, fechaAndroid())
                            putString(VAR.DIETA_HOY, response.toString())
                        }
                    }

                    procesarResponseDieta(response)

                },
                Response.ErrorListener{
                    try {
                        swipeRefreshLayout?.isRefreshing = true
                        Toasty.error(activity!!, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
                        Log.e("myerror",  (it.message))
                        val nr = it.networkResponse
                        val r = String(nr.data)
                    }catch (ex:Exception){
                        Log.e("myerror", ex.message.toString())
                    }
                }) {
                override fun getHeaders(): Map<String, String> {
                    var params: MutableMap<String, String> = HashMap()
                    params["TOKEN"] =  sharedPref?.getString("token", "")!!
                    return params
                }
            }


            val requestQueue = Volley.newRequestQueue(activity!!)
            requestQueue.add(request)
        }
    }

    fun procesarResponseDieta(response:JSONObject){
        val success = response.getBoolean("success")
        val message = response.getString("message")
        if(success){
            val info = response.getJSONObject("info")
            val asignado = info.getInt("asignado")
            val terminado = info.getInt("terminado")
            if(asignado == 1) {
                val dietas = response.getJSONArray("dieta")
                val horarios = response.getJSONArray("horarios")
                val listaDietaHorario = LinkedList<ClsDietaHorario>()
                for (j in 0 until dietas.length()) {
                    val dieta = dietas.getJSONObject(j)
                    listaDietaHorario.add(
                        ClsDietaHorario(
                            dieta.getString("producto"),
                            dieta.getInt("idhorario"), dieta.getDouble("cantidad"),
                            dieta.getString("medida"), dieta.getString("fecha")
                        )
                    )
                }

                listaDietas.clear()
                val posPintado = pintarHorario()
                Log.e("myerror", "pintado: "+ posPintado.toString())
                for (i in 0 until horarios.length()) {
                    val horarioStr = horarios.getJSONObject(i)
                    val idhorario = horarioStr.getInt("id")
                    val filtro = listaDietaHorario.filter { it.idhorario == idhorario }
                    val horario = ClsHorario(idhorario, horarioStr.getString("nombre"))
                    val block = ClsDietaBlock(horario, filtro)
                    if( posPintado == idhorario){
                        block.pintar  = true
                    }
                    listaDietas.add(block)
                }

                mostrarTerminarDia( terminado == 0 && seTerminoDia() )
                adaptador?.notifyDataSetChanged()
                recyclerView?.visibility = View.VISIBLE
            }else{
                mensajedescanso?.visibility = View.VISIBLE
                mensajedescanso?.text = "Hoy es día libre, puedes comer a tu gusto. Mañana continuamos."
            }
        }else{
            Toasty.error(activity!!, message, Toast.LENGTH_SHORT, true).show()
        }
        swipeRefreshLayout?.isRefreshing = false
    }

    fun fechaPrefsHoy():String{
        val s=  sharedPref?.getString(VAR.FECHA_HOY,"")
        if(s==null) return ""
        else return s
    }

    fun fechaSeleccionada():String{
        return MainActivity.DatePickerActivityFragment.fecha
    }

    fun fechaAndroid():String{
      return  MainActivity.DatePickerActivityFragment.formatFecha()
    }
    fun getDietaHoy():String{
        val s=  sharedPref?.getString(VAR.DIETA_HOY,"")
        if(s==null) return ""
        else return s
    }

    fun actualizarDiaActual(){
        val handler = Handler()
        handler.postDelayed({
            var actualizar = true
            if ( fechaPrefsHoy() == fechaSeleccionada() ) {
                if(recyclerView?.visibility == View.VISIBLE) {
                    try {
                        buscarDatos()
                    }catch (ex:java.lang.Exception){
                        actualizar = false
                    }
                }
            }
            if (actualizar){
                actualizarDiaActual()
            }
        }, 1000*60*1)
    }

    fun terminarDia(){
        swipeRefreshLayout?.isRefreshing = true
        mostrarTerminarDia(false)
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_terminar_dia"), null,
            Response.Listener { response ->
                val success = response.getBoolean("success")
                val message = response.getString("message")
                if(success){
                    Toasty.success(activity!!, message, Toast.LENGTH_SHORT, true).show()
                }else{
                    Toasty.error(activity!!, message, Toast.LENGTH_SHORT, true).show()
                }
                swipeRefreshLayout?.isRefreshing = false
            },
            Response.ErrorListener{
                try {
                    Toasty.error(activity!!, "Error de conexión.", Toast.LENGTH_SHORT, true).show()
                    Log.e("myerror",  (it.message))
                    val nr = it.networkResponse
                    val r = String(nr.data)
                }catch (ex:Exception){
                    Log.e("myerror", ex.message.toString())
                }
            }) {
            override fun getHeaders(): Map<String, String> {
                var params: MutableMap<String, String> = HashMap()
                params["TOKEN"] =  sharedPref?.getString("token", "")!!
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(activity!!)
        requestQueue.add(request) 
    }


    fun pintarHorario():Int{
        val horariosRango = ClsHorario.horariosRango
        Log.e("myerror", MainActivity.DatePickerActivityFragment.fecha)
        Log.e("myerror", MainActivity.DatePickerActivityFragment.formatFecha())

        if(MainActivity.DatePickerActivityFragment.formatFecha() == MainActivity.DatePickerActivityFragment.fecha){
            val hora = MainActivity.DatePickerActivityFragment.formatHora()
            val minsActual = ClsHorario.indicadorHora(hora)
            Log.e("myerro", "hora "+ minsActual.toString())


            for(i in horariosRango.size-1 downTo 0) {
                val horaMin =  ClsHorario.indicadorHora(horariosRango[i])

                Log.e("myerro", "horahorario "+ horaMin.toString())
                if(minsActual >horaMin   ){
                    return i+1
                }

            }
        }
        return -1
    }

    fun mostrarTerminarDia(estado:Boolean){
        var margins = (contenedorDietas?.layoutParams as ConstraintLayout.LayoutParams)
        if(estado){
            contenedorTerminar?.visibility = View.VISIBLE
            margins.apply {
                bottomMargin = 170
            }
        }else{
            contenedorTerminar?.visibility = View.GONE
            margins.apply {
                bottomMargin = 0
            }
        }
        contenedorDietas?.layoutParams = margins
        contenedorDietas?.requestLayout()
    }
    fun seTerminoDia():Boolean{
        val horariosRango = ClsHorario.horariosRango
        if(MainActivity.DatePickerActivityFragment.formatFecha() == MainActivity.DatePickerActivityFragment.fecha){
            val hora = MainActivity.DatePickerActivityFragment.formatHora()
            val minsActual = ClsHorario.indicadorHora(hora)
            val minsCena = ClsHorario.indicadorHora(horariosRango[horariosRango.size-1])
            if(minsActual >= minsCena){
                return true
            }
        }
        return false
    }


}