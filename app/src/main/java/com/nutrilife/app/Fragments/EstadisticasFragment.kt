package com.nutrilife.app.Fragments

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nutrilife.app.Clases.VAR
import com.nutrilife.app.R
import es.dmoral.toasty.Toasty
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import lecho.lib.hellocharts.util.ChartUtils
import lecho.lib.hellocharts.view.PieChartView
import java.util.*

class EstadisticasFragment : Fragment() {

    var sharedPref: SharedPreferences? = null
    var swipeRefreshLayout:SwipeRefreshLayout? = null
    var txtMensaje:TextView? = null
    var pieChart:PieChartView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas, container, false)
        sharedPref = activity?.getSharedPreferences(
            VAR.PREF_NAME,
            VAR.PRIVATE_MODE
        )


        val toolbar:Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }


        pieChart = view.findViewById(R.id.chart)
        txtMensaje = view.findViewById(R.id.mensaje)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            buscarEstadisticas()
        }
        buscarEstadisticas()

        return view
    }

    fun buscarEstadisticas(){


        if(!swipeRefreshLayout!!.isRefreshing) swipeRefreshLayout?.isRefreshing = true
        val request : JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, VAR.url("persona_estadistica"), null,
            Response.Listener { response ->

                val success = response.getBoolean("success")
                val message = response.getString("message")

                if(success){
                    val estadistica = response.getJSONObject("estadistica")
                    val total = estadistica.getInt("total")
                    val terminado = estadistica.getInt("terminado")
                    var totalStr = if (total == 1)  " 1 día" else total.toString() + " días"
                    var terminadoStr = if (terminado == 1)  " 1 día" else terminado.toString() + " días"

                    val texto = "Llevas "+ totalStr + " con nosotros, y has cumplido tu dieta "+
                            terminadoStr+ "."

                    val values :  LinkedList<SliceValue> =  LinkedList()
                    values.add(SliceValue(total.toFloat(), ChartUtils.COLOR_ORANGE))
                    values.add(SliceValue(terminado.toFloat(), ChartUtils.COLOR_GREEN))
                    val pieChartData = PieChartData(values)
                    pieChartData.setHasLabels(true)
                    pieChart?.isValueSelectionEnabled  =true
                    pieChart?.onValueTouchListener =ValueTouchListener(activity!!)
                    pieChart?.pieChartData = pieChartData
                    txtMensaje?.text = texto

                }else{
                    txtMensaje?.text = message
                    pieChart?.visibility = View.GONE
                }
                swipeRefreshLayout?.isRefreshing = false
            },
            Response.ErrorListener{
                try {
                    txtMensaje?.text = ""
                    pieChart?.visibility = View.GONE
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


    class ValueTouchListener(act: Activity) : PieChartOnValueSelectListener
    {
        var activity = act

        override fun onValueSelected(index: Int, p1: SliceValue?) {
            if(index == 0){
                Toasty.warning(activity, "Total de dias: "+ p1?.value?.toInt().toString(), Toast.LENGTH_SHORT, true).show()
            }else{
                Toasty.success(activity, "Dias Cumplidos: "+ p1?.value?.toInt().toString(), Toast.LENGTH_SHORT, true).show()
            }
        }

        override fun onValueDeselected() {
        }
    }


}